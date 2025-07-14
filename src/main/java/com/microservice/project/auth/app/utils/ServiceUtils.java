package com.microservice.project.auth.app.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ServiceUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServiceUtils.class);


    public String getHashString(String key) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(key.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public String getBase64String(String key) {
        return Base64.getEncoder().encodeToString(key.getBytes());
    }
}
