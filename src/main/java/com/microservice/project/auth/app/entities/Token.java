package com.microservice.project.auth.app.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "Tokens")
public class Token {

    @Id
    private String username;
    private String authToken;
    private Long authId;
    private Boolean isSpecial;
    private Instant createdAt;
    private Instant updatedAt;

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    public Long getAuthId() {
        return authId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Boolean getIsSpecial() { return isSpecial;}

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setIsSpecial(Boolean isSpecial) { this.isSpecial = isSpecial;}
}
