package com.example.pruebajava.service;

import com.example.pruebajava.dto.MergedPost;
import com.example.pruebajava.exception.PostNotFoundException;
import com.example.pruebajava.exception.UserNotFoundException;
import com.example.pruebajava.model.Comment;
import com.example.pruebajava.model.Post;
import com.example.pruebajava.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    ExternalApiService externalApiService;

    @InjectMocks
    PostService postService;

    private Post samplePost;
    private User sampleUser;
    private Comment sampleComment;

    @BeforeEach
    void setUp() {
        samplePost = new Post();
        samplePost.setId(1);
        samplePost.setUserId(1);
        samplePost.setTitle("Sample Post Title");
        samplePost.setBody("Sample post body content");

        sampleUser = new User();
        sampleUser.setId(1);
        sampleUser.setName("Lionel Messi");
        sampleUser.setUsername("lionelmessi");
        sampleUser.setEmail("lionel.messi@example.com");

        sampleComment = new Comment();
        sampleComment.setId(1);
        sampleComment.setPostId(1);
        sampleComment.setName("Comment Name");
        sampleComment.setEmail("comment@example.com");
        sampleComment.setBody("Comment body content");
    }

    @Test
    void testGetAllMergedPosts_Success() {
        List<Post> posts = Arrays.asList(samplePost);
        List<Comment> comments = Arrays.asList(sampleComment);

        when(externalApiService.getPosts()).thenReturn(posts);
        when(externalApiService.getUser(1)).thenReturn(sampleUser);
        when(externalApiService.getCommentsForPost(1)).thenReturn(comments);

        List<MergedPost> result = postService.getAllMergedPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sample Post Title", result.get(0).getTitle());
        assertEquals("Lionel Messi", result.get(0).getUser().getName());
        assertEquals(1, result.get(0).getComments().size());
    }

    @Test
    void testGetAllMergedPosts_UserNotFound() {
        List<Post> posts = Arrays.asList(samplePost);
        when(externalApiService.getPosts()).thenReturn(posts);
        when(externalApiService.getUser(1)).thenThrow(UserNotFoundException.forUserId(1));

        assertThrows(UserNotFoundException.class, () -> {
            postService.getAllMergedPosts();
        });
    }

    @Test
    void testDeletePost_Success() {
        when(externalApiService.getPosts()).thenReturn(Arrays.asList(samplePost));
        doNothing().when(externalApiService).deletePost(1);

        postService.deletePost(1);

        verify(externalApiService, times(1)).deletePost(1);
    }

    @Test
    void testDeletePost_PostNotFound() {
        when(externalApiService.getPosts()).thenReturn(Arrays.asList());

        assertThrows(PostNotFoundException.class, () -> {
            postService.deletePost(999);
        });
    }

    @Test
    void testDeletePost_ValidationError() {
        when(externalApiService.getPosts()).thenReturn(Arrays.asList());
        
        assertThrows(PostNotFoundException.class, () -> {
            postService.deletePost(0);
        });
    }


    @Test
    void testGetAllMergedPosts_MultiplePostsWithSameUser() {
        Post post1 = new Post();
        post1.setId(1);
        post1.setUserId(1);
        post1.setTitle("First Post");
        post1.setBody("First post body");

        Post post2 = new Post();
        post2.setId(2);
        post2.setUserId(1); 
        post2.setTitle("Second Post");
        post2.setBody("Second post body");

        List<Post> posts = Arrays.asList(post1, post2);
        List<Comment> comments1 = Arrays.asList(sampleComment);
        List<Comment> comments2 = Arrays.asList();

        when(externalApiService.getPosts()).thenReturn(posts);
        when(externalApiService.getUser(1)).thenReturn(sampleUser);
        when(externalApiService.getCommentsForPost(1)).thenReturn(comments1);
        when(externalApiService.getCommentsForPost(2)).thenReturn(comments2);

        List<MergedPost> result = postService.getAllMergedPosts();

        assertNotNull(result);
        assertEquals(2, result.size());
        
        assertEquals("First Post", result.get(0).getTitle());
        assertEquals("Lionel Messi", result.get(0).getUser().getName());
        assertEquals(1, result.get(0).getComments().size());
        
        assertEquals("Second Post", result.get(1).getTitle());
        assertEquals("Lionel Messi", result.get(1).getUser().getName());
        assertEquals(0, result.get(1).getComments().size());
        
        verify(externalApiService, times(1)).getUser(1);
    }
}