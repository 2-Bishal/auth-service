package com.microservice.project.auth.app.services;

import com.microservice.project.auth.app.entities.Credential;
import com.microservice.project.auth.app.entities.Customer;
import com.microservice.project.auth.app.entities.LoginCredential;
import com.microservice.project.auth.app.entities.Token;
import com.microservice.project.auth.app.repositories.CredentialRepository;
import com.microservice.project.auth.app.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    CustomerService customerService;

    private final ServiceUtils serviceUtils = new ServiceUtils();

    private final String salt = "ecomm-auth-1234";

    public boolean validateAndSignUp(Credential credential) {
        // check if username already exists
        if(credentialRepository.findByUsername(credential.getUsername()) == null){
            // Store hashed password
            String password = serviceUtils.getHashString(credential.getPassword()+salt);
            if(password == null){
                return false;
            }
            credential.setPassword(password);
            credentialRepository.save(credential);

            Customer customer = new Customer();
            customer.setUsername(credential.getUsername());
            customer.setName("");
            customer.setPhone("");
            customer.setAddress("");
            customer.setType(credential.getType());
            customerService.updateCustomer(customer);
            return true;
        }else{
            return false;
        }
    }

    public Token validateLogin(LoginCredential loginCredential) {
        // Find credentials by entered username
        Credential fetchedCredential = credentialRepository.findByUsername(loginCredential.getUsername());

        // Validate the hashed password
        if (fetchedCredential.getPassword().equals(serviceUtils.getHashString(loginCredential.getPassword()+salt))) {
            // Check if token already exists for the user
            Token token = tokenService.getTokenByUsername(loginCredential.getUsername());

            // If token does not exist, generate a new token
            if (token == null) {
                token = tokenService.generateToken(fetchedCredential.getUsername(), false);
            }

            // Save the token
            tokenService.saveToken(token);
            logger.info(token.toString());
            return token;
        }else {
            return null;
        }
    }

    public String getUserType(String username) {
        return credentialRepository.findByUsername(username).getType();
    }
}
