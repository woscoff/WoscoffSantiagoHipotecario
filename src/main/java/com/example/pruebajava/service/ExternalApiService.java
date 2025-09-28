package com.example.pruebajava.service;

import com.example.pruebajava.exception.ExternalServiceException;
import com.example.pruebajava.exception.PostNotFoundException;
import com.example.pruebajava.exception.UserNotFoundException;
import com.example.pruebajava.model.Comment;
import com.example.pruebajava.model.Post;
import com.example.pruebajava.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
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
        
        try {
            ResponseEntity<Post[]> response = restTemplate.getForEntity(url, Post[].class);
            List<Post> posts = Arrays.asList(response.getBody());
            logger.info("Successfully retrieved {} posts", posts.size());
            return posts;
        } catch (HttpClientErrorException e) {
            logger.error("Client error fetching posts: {} - {}", e.getStatusCode(), e.getMessage());
            throw ExternalServiceException.serviceUnavailable("JSONPlaceholder Posts API");
        } catch (HttpServerErrorException e) {
            logger.error("Server error fetching posts: {} - {}", e.getStatusCode(), e.getMessage());
            throw ExternalServiceException.serviceUnavailable("JSONPlaceholder Posts API");
        } catch (ResourceAccessException e) {
            logger.error("Timeout or connection error fetching posts: {}", e.getMessage());
            throw ExternalServiceException.timeout("JSONPlaceholder Posts API", 5);
        } catch (Exception e) {
            logger.error("Unexpected error fetching posts: {}", e.getMessage(), e);
            throw ExternalServiceException.serviceUnavailable("JSONPlaceholder Posts API");
        }
    }

    public List<Comment> getCommentsForPost(Integer postId) {
        String url = String.format(baseUrl + "/posts/%d/comments", postId);
        logger.debug("Fetching comments for post {} from {}", postId, url);
        
        try {
            ResponseEntity<Comment[]> response = restTemplate.getForEntity(url, Comment[].class);
            List<Comment> comments = Arrays.asList(response.getBody());
            logger.debug("Successfully retrieved {} comments for post {}", comments.size(), postId);
            return comments;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("No comments found for post {} (404)", postId);
                throw new PostNotFoundException("Post not found with ID: " + postId);
            }
            logger.error("Client error fetching comments for post {}: {} - {}", postId, e.getStatusCode(), e.getMessage());
            throw ExternalServiceException.serviceUnavailable("JSONPlaceholder Comments API");
        } catch (HttpServerErrorException e) {
            logger.error("Server error fetching comments for post {}: {} - {}", postId, e.getStatusCode(), e.getMessage());
            throw ExternalServiceException.serviceUnavailable("JSONPlaceholder Comments API");
        } catch (ResourceAccessException e) {
            logger.error("Timeout fetching comments for post {}: {}", postId, e.getMessage());
            throw ExternalServiceException.timeout("JSONPlaceholder Comments API", 5);
        } catch (Exception e) {
            logger.error("Unexpected error fetching comments for post {}: {}", postId, e.getMessage(), e);
            throw ExternalServiceException.serviceUnavailable("JSONPlaceholder Comments API");
        }
    }

    public User getUser(Integer userId) {
        String url = String.format(baseUrl + "/users/%d", userId);
        logger.debug("Fetching user {} from {}", userId, url);
        
        try {
            ResponseEntity<User> response = restTemplate.getForEntity(url, User.class);
            User user = response.getBody();
            logger.debug("Successfully retrieved user {}: {}", userId, user != null ? user.getName() : "null");
            return user;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("User not found: {} (404)", userId);
                throw UserNotFoundException.forUserId(userId);
            }
            logger.error("Client error fetching user {}: {} - {}", userId, e.getStatusCode(), e.getMessage());
            throw ExternalServiceException.serviceUnavailable("JSONPlaceholder Users API");
        } catch (HttpServerErrorException e) {
            logger.error("Server error fetching user {}: {} - {}", userId, e.getStatusCode(), e.getMessage());
            throw ExternalServiceException.serviceUnavailable("JSONPlaceholder Users API");
        } catch (ResourceAccessException e) {
            logger.error("Timeout fetching user {}: {}", userId, e.getMessage());
            throw ExternalServiceException.timeout("JSONPlaceholder Users API", 5);
        } catch (Exception e) {
            logger.error("Unexpected error fetching user {}: {}", userId, e.getMessage(), e);
            throw ExternalServiceException.serviceUnavailable("JSONPlaceholder Users API");
        }
    }

    public void deletePost(Integer postId) {
        String url = String.format(baseUrl + "/posts/%d", postId);
        logger.info("Deleting post {} at {}", postId, url);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
            logger.info("Successfully deleted post {} - Status: {}", postId, response.getStatusCode());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Post not found for deletion: {} (404)", postId);
                throw new PostNotFoundException("Post not found with ID: " + postId);
            }
            logger.error("Client error deleting post {}: {} - {}", postId, e.getStatusCode(), e.getMessage());
            throw ExternalServiceException.serviceUnavailable("JSONPlaceholder Delete API");
        } catch (HttpServerErrorException e) {
            logger.error("Server error deleting post {}: {} - {}", postId, e.getStatusCode(), e.getMessage());
            throw ExternalServiceException.serviceUnavailable("JSONPlaceholder Delete API");
        } catch (ResourceAccessException e) {
            logger.error("Timeout deleting post {}: {}", postId, e.getMessage());
            throw ExternalServiceException.timeout("JSONPlaceholder Delete API", 5);
        } catch (Exception e) {
            logger.error("Unexpected error deleting post {}: {}", postId, e.getMessage(), e);
            throw ExternalServiceException.serviceUnavailable("JSONPlaceholder Delete API");
        }
    }
}
