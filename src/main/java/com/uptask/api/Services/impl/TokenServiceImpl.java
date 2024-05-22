package com.uptask.api.Services.impl;

import com.uptask.api.Repositories.TokenRepository;
import com.uptask.api.Services.TokenService;
import com.uptask.api.models.Token;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    @Transactional
    public String create(String user) {
        Token newToken = Token.builder()
                .user(user)
                .token(generateToken())
                .createdAt(LocalDateTime.now())
                .build();

        tokenRepository.save(newToken);
        return newToken.getToken();
    }

    @Override
    @Transactional
    public Token validate(String token) {
        Token tokenExists =  tokenRepository.findByToken(token);
        if (tokenExists == null) {
            throw new RuntimeException("Token no valido");
        }
        try {
            tokenRepository.delete(tokenExists);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar token");
        }
        return tokenExists;
    }

    private String generateToken() {
        return Long.toString(Math.round((Math.random() + 1) * 100000));
    }
}
