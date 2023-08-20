package com.example.vertonowsky.avatar;

import com.example.vertonowsky.ErrorCode;
import com.example.vertonowsky.exception.UserNotFoundException;
import com.example.vertonowsky.security.model.CustomOidcUser;
import com.example.vertonowsky.security.model.CustomUserDetails;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AvatarService {

    private final AvatarRepository avatarRepository;
    private final UserRepository userRepository;

    public AvatarService(AvatarRepository avatarRepository, UserRepository userRepository) {
        this.avatarRepository = avatarRepository;
        this.userRepository = userRepository;
    }

    public Avatar get(Integer id) {
        return avatarRepository.findById(id).orElse(null);
    }

    public Set<Avatar> list() {
        return avatarRepository.findAll();
    }

    public void change(Authentication auth, String email, Integer avatarId) throws UserNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage()));
        Avatar avatar = avatarRepository.findById(avatarId).orElseThrow();

        user.setAvatar(avatar);
        userRepository.save(user);

        if (auth != null)
            updateAuthenticationUserAvatar(auth, AvatarSerializer.serialize(user.getAvatar()));
    }

    private void updateAuthenticationUserAvatar(Authentication authentication, AvatarDto avatar) {
        if (authentication.getPrincipal() instanceof CustomUserDetails)
            ((CustomUserDetails) authentication.getPrincipal()).setAvatar(avatar);
        if (authentication.getPrincipal() instanceof CustomOidcUser)
            ((CustomOidcUser)    authentication.getPrincipal()).setAvatar(avatar);
    }

}
