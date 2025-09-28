package com.example.pruebajava.controller;

import com.example.pruebajava.dto.MergedPost;
import com.example.pruebajava.model.Comment;
import com.example.pruebajava.model.Post;
import com.example.pruebajava.model.User;
import com.example.pruebajava.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostsControllerTest {
    @Mock
    PostService postService;

    @InjectMocks
    PostsController postsController;

    private MergedPost sampleMergedPost;

    @BeforeEach
    void setUp() {
        Post post = new Post();
        post.setId(1);
        post.setTitle("Sample Post");
        post.setBody("Sample body");

        User user = new User();
        user.setId(1);
        user.setName("Lionel Messi");
        user.setEmail("lionel.messi@example.com");

        Comment comment = new Comment();
        comment.setId(1);
        comment.setName("Comment Name");
        comment.setBody("Comment body");

        sampleMergedPost = new MergedPost();
        sampleMergedPost.setId(1);
        sampleMergedPost.setTitle("Sample Post");
        sampleMergedPost.setBody("Sample body");
        sampleMergedPost.setUser(user);
        sampleMergedPost.setComments(Arrays.asList(comment));
    }

    @Test
    void testGetPosts_Controller() {
        List<MergedPost> expectedPosts = Arrays.asList(sampleMergedPost);
        when(postService.getAllMergedPosts()).thenReturn(expectedPosts);

        ResponseEntity<List<MergedPost>> response = postsController.getPosts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Sample Post", response.getBody().get(0).getTitle());
        verify(postService, times(1)).getAllMergedPosts();
    }

    @Test
    void testDeletePost_Controller() {
        doNothing().when(postService).deletePost(1);

        ResponseEntity<Void> response = postsController.deletePost(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(postService, times(1)).deletePost(1);
    }
}