package com.uptask.api.Services;

import com.uptask.api.models.Token;

public interface TokenService {

    String create(String user);

    Token validate(String token);

}
