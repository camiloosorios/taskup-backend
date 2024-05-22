package com.uptask.api.Services.impl;


import com.uptask.api.DTOs.CreateUserDTO;
import com.uptask.api.Repositories.UserRepository;
import com.uptask.api.Services.TokenService;
import com.uptask.api.Services.UserService;
import com.uptask.api.Services.helpers.EmailService;
import com.uptask.api.models.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        validateUserExists(createUserDTO.getEmail());
        try {
            String encryptedPassword = passwordEncoder.encode(createUserDTO.getPassword());
            User user = User.builder()
                    .name(createUserDTO.getName())
                    .email(createUserDTO.getEmail())
                    .password(encryptedPassword)
                    .confirmed(false)
                    .build();

            user = userRepository.save(user);
            String token = tokenService.create(user.getId());
            emailService.sendEmail(user.getEmail(),
                    "UpTask - Confirma tu cuenta",
                    user.getName(),
                    token);

            return user;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al crear usuario");
        }
    }

    @Override
    @Transactional
    public void confirmAcount(String id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new RuntimeException("El usuario no existe");
        }
        user.setConfirmed(true);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error al confirmar cuenta");
        }
    }

    @Override
    public String login(String email, String password) {
        if (email == null || email.isBlank() ) {
            throw new RuntimeException("El E-mail no puede ir vacio");
        }
        if (password == null || password.isBlank()) {
            throw new RuntimeException("El password no puede ir vacio");
        }

        User user = userRepository.findByEmail(email);
        if (user == null || !user.getEmail().equals(email)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        if (!user.getConfirmed()) {
            String token = tokenService.create(user.getId());
            emailService.sendEmail(user.getEmail(),
                    "UpTask - Confirma tu cuenta",
                    user.getName(),
                    token);
            throw new RuntimeException("La cuenta no ha sido confirmada, hemos enviado un e-mail de confirmacion");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Password incorrecto");
        }

        return "Usuario Autenticado";
    }

    private void validateUserExists(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            throw new RuntimeException("El usuario ya esta registrado");
        }
    }

}
