package com.uptask.api.Services;

import com.uptask.api.DTOs.CreateUserDTO;
import com.uptask.api.models.User;

public interface UserService {

    User createUser(CreateUserDTO createUserDTO);

    void confirmAcount(String id);

    String login(String email, String password);
}
