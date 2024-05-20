package com.uptask.api.Services.impl;


import com.uptask.api.DTOs.CreateUserDTO;
import com.uptask.api.Repositories.UserRepository;
import com.uptask.api.Services.UserService;
import com.uptask.api.Services.utils.PasswordEncryptionService;
import com.uptask.api.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncryptionService passwordEncryptionService;

    @Override
    public void createUser(CreateUserDTO createUserDTO) {
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
        try {
            String encryptedPassword = passwordEncryptionService.encryptPassword(createUserDTO.getPassword());
            User user = User.builder()
                    .name(createUserDTO.getName())
                    .email(createUserDTO.getEmail())
                    .password(encryptedPassword)
                    .confirmed(false)
                    .build();
            userRepository.save(user);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al crear usuario");
        }
    }

}
