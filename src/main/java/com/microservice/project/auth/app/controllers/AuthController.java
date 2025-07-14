package com.microservice.project.auth.app.controllers;

import com.microservice.project.auth.app.entities.Credential;
import com.microservice.project.auth.app.entities.LoginCredential;
import com.microservice.project.auth.app.entities.Token;
import com.microservice.project.auth.app.services.AuthService;
import com.microservice.project.auth.app.services.TokenService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final String defaultUserRole = "NA";

    @Autowired
    AuthService authService;

    @Autowired
    TokenService tokenService;

    @PostMapping("signup")
    public ResponseEntity<String> signUp(@RequestBody Credential credential) {
        if (authService.validateAndSignUp(credential)) {
            return ResponseEntity.ok("Signup Successful");
        }else {
            return ResponseEntity.badRequest()
                    .body("Username already exists");
        }
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody LoginCredential loginCredential) {
        Token token = authService.validateLogin(loginCredential);

        if (token != null) {
            return ResponseEntity.ok()
                    .header("AuthId", Long.toString(token.getAuthId()))
                    .header("AuthToken", token.getAuthToken())
                    .body("Login Successful");
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Username or Password is incorrect");
        }
    }

    @GetMapping("validate")
    public ResponseEntity<String> validateToken(@RequestHeader("AuthId") String authId,
                                                @RequestHeader("AuthToken") String authToken,
                                                @RequestHeader(value = "User", defaultValue = defaultUserRole) String user) {

        Token token = tokenService.findByAuthToken(authToken);
        String userType = null;
        if (!user.equals(defaultUserRole) && token != null) {
            userType = authService.getUserType(token.getUsername());
        }

        if (token!=null && tokenService.validateToken(token, Long.parseLong(authId), authToken, user, userType)) {
            return ResponseEntity.ok(token.getUsername());
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Token");
        }
    }

    @GetMapping("logout")
    public ResponseEntity<String> logout(@RequestHeader("AuthId") String authId, @RequestHeader("AuthToken") String authToken) {
        Token token = tokenService.findByAuthToken(authToken);
        if (token != null && tokenService.validateToken(token,Long.parseLong(authId), authToken, defaultUserRole, null)){
            tokenService.deleteToken(token);
            return ResponseEntity.ok("Logout Successful");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid Token");
    }
}
