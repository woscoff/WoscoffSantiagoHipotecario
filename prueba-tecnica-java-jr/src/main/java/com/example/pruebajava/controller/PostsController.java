package com.example.pruebajava.controller;

import com.example.pruebajava.dto.MergedPost;
import com.example.pruebajava.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/posts")
@Validated
public class PostsController {
    private static final Logger logger = LoggerFactory.getLogger(PostsController.class);

    private final PostService postService;

    public PostsController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<MergedPost>> getPosts() {
        logger.info("GET /posts requested");
        List<MergedPost> merged = postService.getAllMergedPosts();
        return ResponseEntity.ok(merged);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable @Min(1) Integer id) {
        logger.info("DELETE /posts/{} requested", id);
        boolean ok = postService.deletePost(id);
        if (ok) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting post");
        }
    }
}
