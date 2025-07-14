package com.microservice.project.auth.app.repositories;

import com.microservice.project.auth.app.entities.Credential;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CredentialRepository extends MongoRepository<Credential, String> {

    Credential findByUsername(String username);
}
