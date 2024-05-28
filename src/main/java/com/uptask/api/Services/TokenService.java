package com.uptask.api.Services;

import com.uptask.api.models.Token;

import java.util.concurrent.CompletableFuture;

public interface TokenService {

    CompletableFuture<String> create(String user);

    Token validate(String token);

    void delete(Token token);

}
