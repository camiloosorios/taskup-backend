package com.uptask.api.controllers;

import com.uptask.api.DTOs.CreateUserDTO;
import com.uptask.api.Services.TokenService;
import com.uptask.api.Services.UserService;
import com.uptask.api.Services.helpers.EmailService;
import com.uptask.api.models.Token;
import com.uptask.api.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestBody CreateUserDTO createAccount) {
        try {
            userService.createUser(createAccount);

            return ResponseEntity.ok().body("Cuenta Creada, revisa tu email para confirmarla");
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/confirm-account")
    public ResponseEntity<?> confirmAccount(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");
            System.out.println(token);
            Token tokenExists = tokenService.validate(token);
            if (tokenExists == null) {
                throw new RuntimeException("Token no valido");
            }
            userService.confirmAcount(tokenExists.getUser());

            return ResponseEntity.ok().body("Cuenta Confirmada correctamente");
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        try {
            String message = userService.login(email, password);

            return ResponseEntity.ok().body(message);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }


}
