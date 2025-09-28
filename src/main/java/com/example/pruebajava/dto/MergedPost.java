package com.example.pruebajava.dto;

import com.example.pruebajava.model.Comment;
import com.example.pruebajava.model.User;

import java.util.List;

public class MergedPost {
    private Integer id;
    private Integer userId;
    private String title;
    private String body;
    private User user;
    private List<Comment> comments;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}
