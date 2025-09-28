package com.example.pruebajava.service;

import com.example.pruebajava.dto.MergedPost;
import com.example.pruebajava.exception.PostNotFoundException;
import com.example.pruebajava.exception.UserNotFoundException;
import com.example.pruebajava.model.Comment;
import com.example.pruebajava.model.Post;
import com.example.pruebajava.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private final ExternalApiService externalApi;

    public PostService(ExternalApiService externalApi) {
        this.externalApi = externalApi;
    }

    @Cacheable("posts")
    public List<MergedPost> getAllMergedPosts() {
        logger.info("Building merged posts list with concurrent processing");
        
        List<Post> posts = externalApi.getPosts();
        logger.debug("Retrieved {} posts from external API", posts.size());

        List<Integer> userIds = posts.stream()
            .map(Post::getUserId)
            .distinct()
            .collect(Collectors.toList());

        Map<Integer, User> usersMap = fetchUsersConcurrently(userIds);
        logger.debug("Retrieved {} unique users", usersMap.size());
        List<MergedPost> merged = posts.parallelStream().map(post -> {
            MergedPost mergedPost = new MergedPost();
            mergedPost.setId(post.getId());
            mergedPost.setUserId(post.getUserId());
            mergedPost.setTitle(post.getTitle());
            mergedPost.setBody(post.getBody());

            User user = usersMap.get(post.getUserId());
            if (user != null) {
                mergedPost.setUser(user);
            } else {
                logger.warn("User not found for post {} with userId {}", post.getId(), post.getUserId());
                throw new UserNotFoundException("User not found with ID: " + post.getUserId());
            }
            try {
                List<Comment> comments = externalApi.getCommentsForPost(post.getId());
                mergedPost.setComments(comments);
                logger.debug("Retrieved {} comments for post {}", comments.size(), post.getId());
            } catch (Exception e) {
                logger.warn("Could not retrieve comments for post {}: {}", post.getId(), e.getMessage());
                mergedPost.setComments(new ArrayList<>());
            }

            return mergedPost;
        }).collect(Collectors.toList());

        logger.info("Successfully built {} merged posts with concurrent processing", merged.size());
        return merged;
    }

    private Map<Integer, User> fetchUsersConcurrently(List<Integer> userIds) {
        List<CompletableFuture<User>> futures = userIds.stream()
            .map(userId -> CompletableFuture.supplyAsync(() -> {
                try {
                    return externalApi.getUser(userId);
                } catch (Exception e) {
                    logger.error("Error fetching user {}: {}", userId, e.getMessage());
                    throw new UserNotFoundException("User not found with ID: " + userId, e);
                }
            }, executorService))
            .collect(Collectors.toList());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );

        try {
            allOf.get();
            
            return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toMap(User::getId, user -> user));
        } catch (Exception e) {
            logger.error("Error in concurrent user fetching: {}", e.getMessage());
            if (e.getCause() instanceof UserNotFoundException) {
                throw (UserNotFoundException) e.getCause();
            }
            throw new RuntimeException("Failed to fetch users concurrently", e);
        }
    }

    public void deletePost(Integer id) {
        logger.info("Attempting to delete post with ID: {}", id);
        
        try {
            validatePostExists(id);
            
            externalApi.deletePost(id);
            logger.info("Successfully deleted post with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting post {}: {}", id, e.getMessage());
            throw e;
        }
    }

    private void validatePostExists(Integer postId) {
        try {
            List<Post> posts = externalApi.getPosts();
            boolean postExists = posts.stream()
                .anyMatch(post -> post.getId().equals(postId));
            
            if (!postExists) {
                throw new PostNotFoundException("Post not found with ID: " + postId);
            }
        } catch (Exception e) {
            if (e instanceof PostNotFoundException) {
                throw e;
            }
            logger.error("Error validating post existence for ID {}: {}", postId, e.getMessage());
            throw new RuntimeException("Could not validate post existence", e);
        }
    }
}
