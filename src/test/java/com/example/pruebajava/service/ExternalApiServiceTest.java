package com.example.pruebajava.service;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalApiServiceTest {
    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    ExternalApiService externalApiService;

    private Post samplePost;
    private User sampleUser;
    private Comment sampleComment;

    @BeforeEach
    void setUp() {
        samplePost = new Post();
        samplePost.setId(1);
        samplePost.setUserId(1);
        samplePost.setTitle("Sample Post");
        samplePost.setBody("Sample body");

        sampleUser = new User();
        sampleUser.setId(1);
        sampleUser.setName("John Doe");
        sampleUser.setEmail("john@example.com");

        sampleComment = new Comment();
        sampleComment.setId(1);
        sampleComment.setPostId(1);
        sampleComment.setName("Comment Name");
        sampleComment.setBody("Comment body");
    }

    @Test
    void testGetPosts_Success() {
        Post[] posts = {samplePost};
        ResponseEntity<Post[]> response = new ResponseEntity<>(posts, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Post[].class))).thenReturn(response);

        List<Post> result = externalApiService.getPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sample Post", result.get(0).getTitle());
    }

    @Test
    void testGetUser_NotFound() {
        HttpClientErrorException notFoundException = new HttpClientErrorException(HttpStatus.NOT_FOUND, "User not found");
        when(restTemplate.getForEntity(anyString(), eq(User.class))).thenThrow(notFoundException);

        assertThrows(UserNotFoundException.class, () -> {
            externalApiService.getUser(999);
        });
    }

    @Test
    void testDeletePost_NotFound() {
        HttpClientErrorException notFoundException = new HttpClientErrorException(HttpStatus.NOT_FOUND, "Post not found");
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenThrow(notFoundException);

        assertThrows(PostNotFoundException.class, () -> {
            externalApiService.deletePost(999);
        });
    }
}