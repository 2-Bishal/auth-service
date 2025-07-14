package com.microservice.project.auth.app.repositories;

import com.microservice.project.auth.app.entities.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepository extends MongoRepository<Token, String> {

    Token findByAuthToken(String authToken);
    Token findByUsername(String username);
}
