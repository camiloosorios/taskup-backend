package com.uptask.api.Services.impl;


import com.uptask.api.DTOs.CreateUserDTO;
import com.uptask.api.DTOs.ResetPasswordDTO;
import com.uptask.api.DTOs.UserDTO;
import com.uptask.api.Repositories.UserRepository;
import com.uptask.api.Services.TokenService;
import com.uptask.api.Services.UserService;
import com.uptask.api.Services.helpers.EmailService;
import com.uptask.api.Services.helpers.JwtService;
import com.uptask.api.models.Token;
import com.uptask.api.models.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtService jwtService;


    @Override
    public User createUser(CreateUserDTO createUserDTO) {
        if (createUserDTO.getName() == null || createUserDTO.getName().isBlank()) {
            throw new RuntimeException("El nombre no puede ir vacio");
        }
        if (createUserDTO.getEmail() == null || createUserDTO.getEmail().isBlank()) {
            throw new RuntimeException("El E-mail no puede ir vacio");
        }
        if (!createUserDTO.getEmail().matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            throw new RuntimeException("E-mail no válido");
        }
        if (createUserDTO.getPassword() == null || createUserDTO.getPassword().trim().length() < 8) {
            throw new RuntimeException("El password es muy corto, minimo 8 caracteres");
        }
        if (createUserDTO.getPasswordConfirmation() == null ||!createUserDTO.getPasswordConfirmation().equals(createUserDTO.getPassword())) {
            throw new RuntimeException("Los password no son iguales");
        }
        User user = userRepository.findByEmail(createUserDTO.getEmail());
        if (user != null) {
            throw new RuntimeException("El usuario ya esta registrado");
        }
        try {
            String encryptedPassword = passwordEncoder.encode(createUserDTO.getPassword());
            User newUser = User.builder()
                    .name(createUserDTO.getName())
                    .email(createUserDTO.getEmail())
                    .password(encryptedPassword)
                    .confirmed(false)
                    .build();

            User finalUser = userRepository.save(newUser);
            CompletableFuture<String> tokenFuture = tokenService.create(newUser.getId());
            tokenFuture.thenAccept(token -> emailService.sendConfirmationEmail(finalUser.getEmail(),
                    "UpTask - Confirma tu cuenta",
                    finalUser.getName(),
                    token));

            return newUser;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al crear usuario");
        }
    }

    @Override
    public void sendConfirmationCode(String email) {
        User user = userRepository.findByEmail(email);
        validateUserNotExists(user);
        if (user.getConfirmed()) {
            throw new RuntimeException("El usuario ya esta confirmado");
        }
        try {
            sendConfirmationEmail(user);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo de confirmacion");
        }

    }

    @Override
    @Transactional
    public void confirmAcount(String id) {
        User user = userRepository.findById(id).orElse(null);
        validateUserNotExists(user);
        user.setConfirmed(true);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error al confirmar cuenta");
        }
    }

    @Override
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email);
        validateUserNotExists(user);
        try {
            CompletableFuture<String> tokenFuture = tokenService.create(user.getId());
            tokenFuture.thenAccept(token -> emailService.sendResetPasswordEmail(user.getEmail(),
                    "UpTask - Reestablece tu password", user.getName(), token));
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo de reestablecer password");
        }
    }

    @Override
    public void updatePassword(String token, ResetPasswordDTO resetPasswordDTO) {
        if (resetPasswordDTO.getPassword() == null || resetPasswordDTO.getPassword().trim().length() < 8) {
            throw new RuntimeException("El password es muy corto, mínimo 8 caractéres");
        }
        if (resetPasswordDTO.getPasswordConfirmation() == null ||!resetPasswordDTO.getPasswordConfirmation().equals(resetPasswordDTO.getPassword())) {
            throw new RuntimeException("Los password no son iguales");
        }

        Token tokenResult = tokenService.validate(token);
        User user = userRepository.findById(tokenResult.getUser()).orElse(null);
        validateUserNotExists(user);
        String newPassword = passwordEncoder.encode(resetPasswordDTO.getPassword());
        user.setPassword(newPassword);
        userRepository.save(user);
        tokenService.delete(tokenResult);
    }

    @Override
    public String login(String email, String password) {
        if (email == null || email.isBlank() ) {
            throw new RuntimeException("El E-mail no puede ir vacío");
        }
        if (password == null || password.isBlank()) {
            throw new RuntimeException("El password no puede ir vacío");
        }

        User user = userRepository.findByEmail(email);
        if (user == null || !user.getEmail().equals(email)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Password incorrecto");
        }
        if (!user.getConfirmed()) {
            sendConfirmationEmail(user);
            throw new RuntimeException("La cuenta no ha sido confirmada, hemos enviado un e-mail de confirmación");
        }

        return jwtService.generateToken(user.getId());
    }

    @Override
    public UserDTO getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getDetails();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("El usuario no esta registrado");
        }
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .confirmed(user.getConfirmed())
                .build();
    }

    @Override
    public UserDTO findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("El usuario no esta registrado");
        }
        return  UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public Optional<User> findUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public void updateProfile(String userId, CreateUserDTO createUserDTO) {
        if (createUserDTO.getName() == null || createUserDTO.getName().isBlank()) {
            throw new RuntimeException("El nombre no puede ir vacio");
        }
        if (createUserDTO.getEmail() == null || createUserDTO.getEmail().isBlank()) {
            throw new RuntimeException("El E-mail no puede ir vacio");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!user.getEmail().equals(createUserDTO.getEmail())) {
            User userExists = userRepository.findByEmail(createUserDTO.getEmail());
            if (userExists!= null) {
                throw new RuntimeException("El E-mail ya esta registrado");
            }
        }
        user.setName(createUserDTO.getName());
        user.setEmail(createUserDTO.getEmail());
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el usuario");
        }
    }

    @Override
    public void updateCurrentPassword(String userId, ResetPasswordDTO resetPasswordDTO) {
        if (resetPasswordDTO.getCurrentPassword() == null || resetPasswordDTO.getCurrentPassword().isBlank()) {
            throw new RuntimeException("El password actual no puede ir vacio");
        }
        if (resetPasswordDTO.getPassword() == null || resetPasswordDTO.getPassword().isBlank()) {
            throw new RuntimeException("El password es muy corto minimo 8 caracteres");
        }
        if (resetPasswordDTO.getPasswordConfirmation() == null ||!resetPasswordDTO.getPasswordConfirmation().equals(resetPasswordDTO.getPassword())) {
            throw new RuntimeException("Los password no son iguales");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!passwordEncoder.matches(resetPasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("El password actual es incorrecto");
        }
        String newPassword = passwordEncoder.encode(resetPasswordDTO.getPassword());
        user.setPassword(newPassword);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el password");
        }
    }

    @Override
    public void checkPassword(String userId, ResetPasswordDTO resetPasswordDTO) {
        if (resetPasswordDTO.getPassword() == null || resetPasswordDTO.getPassword().isBlank()) {
            throw new RuntimeException("El password es muy corto minimo 8 caracteres");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("El usuario no existe"));
        if (!passwordEncoder.matches(resetPasswordDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("El password es incorrecto");
        }
    }

    private void validateUserNotExists(User user) {
        if (user == null) {
            throw new RuntimeException("El usuario no esta registrado");
        }
    }

    private void sendConfirmationEmail(User user){
        CompletableFuture<String> tokenFuture = tokenService.create(user.getId());
        tokenFuture.thenAccept(token -> emailService.sendConfirmationEmail(user.getEmail(),
                "UpTask - Confirma tu cuenta",
                user.getName(),
                token));
    }

}
