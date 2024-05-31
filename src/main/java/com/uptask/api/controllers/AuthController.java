package com.uptask.api.controllers;

import com.uptask.api.DTOs.CreateUserDTO;
import com.uptask.api.DTOs.LoginDTO;
import com.uptask.api.DTOs.ResetPasswordDTO;
import com.uptask.api.DTOs.UserDTO;
import com.uptask.api.Services.TokenService;
import com.uptask.api.Services.UserService;
import com.uptask.api.Services.helpers.EmailService;
import com.uptask.api.models.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

            return ResponseEntity.status(201).body("Cuenta Creada, revisa tu email para confirmarla");
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
            Token tokenExists = tokenService.validate(token);
            userService.confirmAcount(tokenExists.getUser());
            tokenService.delete(tokenExists);

            return ResponseEntity.ok().body("Cuenta Confirmada correctamente");
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO body) {
        String email = body.getEmail();
        String password = body.getPassword();
        try {
            String message = userService.login(email, password);

            return ResponseEntity.ok().body(message);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/request-code")
    public ResponseEntity<?> requestConfirmationCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        try {
            userService.sendConfirmationCode(email);

            return ResponseEntity.ok().body("Se envio un nuevo token a tu e-mail");
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        try {
            userService.resetPassword(email);

            return ResponseEntity.ok().body("Se envio un nuevo token a tu e-mail");
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        try {
            tokenService.validate(token);

            return ResponseEntity.ok().body("Token valido, Define tu nuevo password");
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/update-password/{token}")
    public ResponseEntity<?> updatePassword(@PathVariable String token, @RequestBody ResetPasswordDTO resetPasswordDTO) {
        try {
            userService.updatePassword(token, resetPasswordDTO);

            return ResponseEntity.ok().body("El password se modificó correctamente");
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user")
    public UserDTO getUser() {
        return userService.getUser();
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody CreateUserDTO createUserDTO) {
        String userId = getAuthenticatedUser();
        try {
            userService.updateProfile(userId, createUserDTO);

            return ResponseEntity.ok().body("Perfil actualizado");
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        String userId = getAuthenticatedUser();
        try {
            userService.updateCurrentPassword(userId, resetPasswordDTO);

            return ResponseEntity.ok().body("El password se modificó correctamente");
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    private String getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String) authentication.getDetails();
    }

}
