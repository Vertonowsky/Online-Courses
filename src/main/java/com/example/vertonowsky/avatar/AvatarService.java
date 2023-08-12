package com.example.vertonowsky.avatar;

import com.example.vertonowsky.ErrorCode;
import com.example.vertonowsky.exception.UserNotFoundException;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserRepository;
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

    public Set<Avatar> list() {
        return avatarRepository.findAll();
    }

    public Avatar findByUser(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage()));
        return avatarRepository.findAvatarByUser(user).orElse(null);
    }

    public void change(String email, Integer avatarId) throws UserNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage()));

        Avatar avatar = avatarRepository.findById(avatarId).orElseThrow();

        user.setAvatar(avatar);
        userRepository.save(user);
    }

}
