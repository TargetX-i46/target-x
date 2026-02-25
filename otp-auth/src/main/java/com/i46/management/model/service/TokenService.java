package com.i46.management.model.service;


import com.i46.management.model.entity.Token;
import com.i46.management.model.repository.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TokenService {
    private final TokenRepository tokenRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public Token save(Token token) {
        return tokenRepository.save(token);
    }

}
