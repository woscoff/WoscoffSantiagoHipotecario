package com.example.pruebajava.service;

import com.example.pruebajava.dto.MergedPost;
import com.example.pruebajava.model.Comment;
import com.example.pruebajava.model.Post;
import com.example.pruebajava.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    private final ExternalApiService externalApi;

    public PostService(ExternalApiService externalApi) {
        this.externalApi = externalApi;
    }

    @Cacheable("posts")
    public List<MergedPost> getAllMergedPosts() {
        logger.info("Building merged posts list");
        List<Post> posts = externalApi.getPosts();

        List<MergedPost> merged = posts.stream().map(p -> {
            MergedPost mp = new MergedPost();
            mp.setId(p.getId());
            mp.setUserId(p.getUserId());
            mp.setTitle(p.getTitle());
            mp.setBody(p.getBody());

            try {
                List<Comment> comments = externalApi.getCommentsForPost(p.getId());
                mp.setComments(comments);
            } catch (Exception e) {
                logger.warn("No se pudieron obtener comentarios para post {}", p.getId());
                mp.setComments(new ArrayList<>());
            }

            try {
                User user = externalApi.getUser(p.getUserId());
                mp.setUser(user);
            } catch (Exception e) {
                logger.warn("No se pudo obtener usuario {} para post {}", p.getUserId(), p.getId());
            }

            return mp;
        }).collect(Collectors.toList());

        return merged;
    }

    public boolean deletePost(Integer id) {
        try {
            externalApi.deletePost(id);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting post {}: {}", id, e.getMessage());
            return false;
        }
    }
}
