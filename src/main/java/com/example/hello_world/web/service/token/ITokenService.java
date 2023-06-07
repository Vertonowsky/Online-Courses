package com.example.hello_world.web.service.token;

public interface ITokenService {


    /*private <T extends Token, S extends TokenRepository<T>> void disableOldTokens(User user, S tokenRepository) {
        List<T> tokens = tokenRepository.findAllByUser(user);
        if (tokens.size() > 0) {
            for(T t : tokens)
                t.setValid(false);
            //tokenRepository.saveAll(tokens);
        }
    }*/

}
