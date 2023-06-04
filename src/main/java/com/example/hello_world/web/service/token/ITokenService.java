package com.example.hello_world.web.service.token;

import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.model.token.Token;
import com.example.hello_world.persistence.repository.TokenRepository;

import java.util.List;

public interface ITokenService {


    private <T extends Token, S extends TokenRepository<T>> void disableOldTokens(User user, S tokenRepository) {
        List<T> tokens = tokenRepository.findAllByUser(user);
        if (tokens.size() > 0) {
            for(T t : tokens)
                t.setValid(false);
            //tokenRepository.saveAll(tokens);
        }
    }

}
