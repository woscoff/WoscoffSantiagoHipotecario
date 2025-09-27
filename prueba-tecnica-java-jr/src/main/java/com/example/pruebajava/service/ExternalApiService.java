package com.example.pruebajava.service;

import com.example.pruebajava.model.Comment;
import com.example.pruebajava.model.Post;
import com.example.pruebajava.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ExternalApiService {
    private static final Logger logger = LoggerFactory.getLogger(ExternalApiService.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ExternalApiService(RestTemplate restTemplate, @Value("${external.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public List<Post> getPosts() {
        String url = baseUrl + "/posts";
        logger.info("Fetching posts from {}", url);
        ResponseEntity<Post[]> response = restTemplate.getForEntity(url, Post[].class);
        return Arrays.asList(response.getBody());
    }

    public List<Comment> getCommentsForPost(Integer postId) {
        String url = String.format(baseUrl + "/posts/%d/comments", postId);
        try {
            ResponseEntity<Comment[]> resp = restTemplate.getForEntity(url, Comment[].class);
            return Arrays.asList(resp.getBody());
        } catch (HttpClientErrorException e) {
            logger.error("Error fetching comments for post {}: {}", postId, e.getStatusCode());
            throw e;
        }
    }

    public User getUser(Integer userId) {
        String url = String.format(baseUrl + "/users/%d", userId);
        ResponseEntity<User> resp = restTemplate.getForEntity(url, User.class);
        return resp.getBody();
    }

    public ResponseEntity<String> deletePost(Integer postId) {
        String url = String.format(baseUrl + "/posts/%d", postId);
        logger.info("Deleting post at {}", url);
        return restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
    }
}
