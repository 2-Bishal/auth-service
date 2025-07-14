package com.microservice.project.auth.app.services;


import com.microservice.project.auth.app.entities.Token;
import com.microservice.project.auth.app.repositories.TokenRepository;
import com.microservice.project.auth.app.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    private final Integer inactivityExpiryTime = 600; // Token expires after 10 minutes of inactivity (defined in seconds)
    private final Integer perDayExpiryTime = 86400; // Token expires after 24 hours (defined in seconds)
    private final String salt = "ecomm-token-6789";
    private final String defaultUserRole = "NA";

    @Autowired
    private TokenRepository tokenRepository;

    private final ServiceUtils serviceUtils = new ServiceUtils();


    public Token generateToken(String username, Boolean isSpecial) {
        // Generate a random authId
        long authId = Math.abs(new Random().nextLong());

        // Generate an auth token by hashing username and authId with salt
        String tokenString = salt + username + Long.toString(authId);
        String hashToken = serviceUtils.getHashString(tokenString);

        // If hashing fails, generate a base64 string to ensure a token
        if(hashToken == null) {
            hashToken = serviceUtils.getBase64String(tokenString);
        }

        // Create a new token object
        Token token = new Token();
        token.setUsername(username);
        token.setAuthToken(hashToken);
        token.setAuthId(authId);
        token.setIsSpecial(isSpecial);
        token.setCreatedAt(Instant.now());
        return token;
    }

    public boolean validateToken(Token token,Long authId, String authToken, String userType, String expectedUserType) {
        // If token is not found, fail the validation
        if (token != null) {

            if(!userType.equals(defaultUserRole)) {
                if(!userType.equals(expectedUserType)){
                    return false;
                }
            }
            // Generate auth token for validation by hashing username from fetched token object and provided authId with salt
            String tokenString = salt + token.getUsername() + Long.toString(authId);
            String hashString = serviceUtils.getHashString(tokenString);

            // If hashing fails, fail the validation which will make the user login again
            if(hashString == null) {
                return false;
            }

            // Validate the generated auth token against the provided auth token
            if (hashString.equals(token.getAuthToken())) {

                // Skip expiry check for special tokens
                if(token.getIsSpecial() == true) {
                    return true;
                }
                // If token value is valid then validate the token expiry based on inactivity and 24 hour expiry policy
                if(Instant.now().getEpochSecond() - token.getUpdatedAt().getEpochSecond() < inactivityExpiryTime
                        && Instant.now().getEpochSecond() - token.getCreatedAt().getEpochSecond() < perDayExpiryTime){
                    saveToken(token);
                    return true;
                } else{
                    // If token is valid but expired, delete the token
                    deleteToken(token);
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Token findByAuthToken(String authToken) {
        return tokenRepository.findByAuthToken(authToken);
    }

    public Token getTokenByUsername(String username) {
        return tokenRepository.findByUsername(username);
    }

    public void saveToken(Token token) {
        // Update the updatedAt field while saving
        token.setUpdatedAt(Instant.now());
        tokenRepository.save(token);
    }

    public void deleteToken(Token token) {
        tokenRepository.delete(token);
    }

    public String getUsernameByAuthToken(String authToken) {
        return tokenRepository.findByAuthToken(authToken).getUsername();
    }
}
