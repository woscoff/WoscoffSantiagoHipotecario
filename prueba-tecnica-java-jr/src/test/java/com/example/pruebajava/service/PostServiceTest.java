package com.example.pruebajava.service;

import com.example.pruebajava.dto.MergedPost;
import com.example.pruebajava.model.Comment;
import com.example.pruebajava.model.Post;
import com.example.pruebajava.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PostServiceTest {
    @Mock
    ExternalApiService externalApiService;

    @InjectMocks
    PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMergedPosts() {
        Post p = new Post();
        p.setId(1);
        p.setUserId(1);
        p.setTitle("title");
        p.setBody("body");

        User u = new User();
        u.setId(1);
        u.setName("John");

        Comment c = new Comment();
        c.setId(1);
        c.setPostId(1);
        c.setBody("comment");

        when(externalApiService.getPosts()).thenReturn(Arrays.asList(p));
        when(externalApiService.getCommentsForPost(1)).thenReturn(Arrays.asList(c));
        when(externalApiService.getUser(1)).thenReturn(u);

        List<MergedPost> merged = postService.getAllMergedPosts();
        assertNotNull(merged);
        assertEquals(1, merged.size());
        MergedPost mp = merged.get(0);
        assertEquals(1, mp.getId());
        assertEquals(1, mp.getUser().getId());
        assertEquals(1, mp.getComments().size());
    }
}
