package com.uptask.api.controllers;

import com.uptask.api.DTOs.CreateUserDTO;
import com.uptask.api.Services.UserService;
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

    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestBody CreateUserDTO createAccount) {
        try {
            userService.createUser(createAccount);

            return ResponseEntity.ok().body("Cuenta Creada");
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

}
