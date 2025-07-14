package com.microservice.project.auth.app.services;

import com.microservice.project.auth.app.entities.Customer;
import com.microservice.project.auth.app.entities.Token;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private WebClient updateCustomer;

    @Autowired
    private TokenService tokenService;

    public void updateCustomer(Customer customer) {
        Token token = getOrCreateCustomerServiceToken();
        updateCustomer
                .post()
                .header("AuthId", Long.toString(token.getAuthId()))
                .header("AuthToken", token.getAuthToken())
                .bodyValue(customer)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    public Token getOrCreateCustomerServiceToken() {
        Token token = tokenService.getTokenByUsername("customer-service");
        if (token == null) {
            token = tokenService.generateToken("customer-service", true);
            tokenService.saveToken(token);
        }
        return token;
    }
}