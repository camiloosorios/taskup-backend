package com.uptask.api.Services;

import com.uptask.api.DTOs.CreateUserDTO;
import com.uptask.api.DTOs.ResetPasswordDTO;
import com.uptask.api.models.User;

public interface UserService {

    User createUser(CreateUserDTO createUserDTO);

    void confirmAcount(String id);

    void sendConfirmationCode(String email);

    void resetPassword(String email);

    void updatePassword(String token, ResetPasswordDTO resetPasswordDTO);

    String login(String email, String password);
}
