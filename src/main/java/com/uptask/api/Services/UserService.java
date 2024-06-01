package com.uptask.api.Services;

import com.uptask.api.DTOs.CreateUserDTO;
import com.uptask.api.DTOs.ResetPasswordDTO;
import com.uptask.api.DTOs.UserDTO;
import com.uptask.api.models.User;

import java.util.Optional;

public interface UserService {

    User createUser(CreateUserDTO createUserDTO);

    void confirmAcount(String id);

    void sendConfirmationCode(String email);

    void resetPassword(String email);

    void updatePassword(String token, ResetPasswordDTO resetPasswordDTO);

    String login(String email, String password);

    UserDTO getUser();

    UserDTO findUserByEmail(String email);

    Optional<User> findUserById(String id);

    void updateProfile(String userId, CreateUserDTO createUserDTO);

    void updateCurrentPassword(String userId, ResetPasswordDTO resetPasswordDTO);

    void checkPassword(String userId, ResetPasswordDTO resetPasswordDTO);
}
