package com.example.pruebajava.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PostsIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetPosts_IntegrationTest() {
        ResponseEntity<String> response = restTemplate.getForEntity("/posts", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        String responseBody = response.getBody();
        assertTrue(responseBody.contains("\"id\""));
        assertTrue(responseBody.contains("\"title\""));
        assertTrue(responseBody.contains("\"user\""));
        assertTrue(responseBody.contains("\"comments\""));
    }

    @Test
    void testDeletePost_IntegrationTest() {
        ResponseEntity<Void> response = restTemplate.exchange("/posts/1", 
                org.springframework.http.HttpMethod.DELETE, null, Void.class);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeletePost_NotFound_IntegrationTest() {
        ResponseEntity<String> response = restTemplate.exchange("/posts/99999", 
                org.springframework.http.HttpMethod.DELETE, null, String.class);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("POST_NOT_FOUND"));
    }

    @Test
    void testValidation_InvalidPostId() {
        ResponseEntity<String> response = restTemplate.exchange("/posts/0", 
                org.springframework.http.HttpMethod.DELETE, null, String.class);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("CONSTRAINT_VIOLATION"));
    }

    @Test
    void testSwaggerDocumentation() {
        ResponseEntity<String> swaggerResponse = restTemplate.getForEntity("/swagger-ui.html", String.class);
        assertTrue(swaggerResponse.getStatusCode() == HttpStatus.OK || 
                  swaggerResponse.getStatusCode() == HttpStatus.FOUND,
                  "Expected 200 OK or 302 FOUND, but got: " + swaggerResponse.getStatusCode());

        ResponseEntity<String> apiDocsResponse = restTemplate.getForEntity("/v3/api-docs", String.class);
        assertEquals(HttpStatus.OK, apiDocsResponse.getStatusCode());
        assertNotNull(apiDocsResponse.getBody());
        assertTrue(apiDocsResponse.getBody().contains("\"openapi\""));
        assertTrue(apiDocsResponse.getBody().contains("\"info\""));
        assertTrue(apiDocsResponse.getBody().contains("\"paths\""));
    }
}