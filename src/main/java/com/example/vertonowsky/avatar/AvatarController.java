package com.example.vertonowsky.avatar;

import com.example.vertonowsky.exception.UserNotFoundException;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserQueryType;
import com.example.vertonowsky.user.UserSerializer;
import com.example.vertonowsky.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static com.example.vertonowsky.user.UserSerializer.Task.AVATAR;

@RestController
@Controller
@RequestMapping("api/avatar")
public class AvatarController {

    @Value("${server.url}")
    private String serverUrl;
    private final AvatarService avatarService;
    private final UserService userService;

    public AvatarController(AvatarService avatarService, UserService userService) {
        this.avatarService = avatarService;
        this.userService = userService;
    }

    @GetMapping("list")
    public List<AvatarDto> list() {
        return avatarService.list().stream().map(AvatarSerializer::serialize).toList();
    }

    @GetMapping("view")
    public ModelAndView view(Model model) {
        model.addAttribute("serverUrl", serverUrl);
        model.addAttribute("avatars", avatarService.list().stream().map(AvatarSerializer::serialize).toList());
        return new ModelAndView("profil :: avatar_container");
    }

    @GetMapping("detail")
    public ModelAndView detail(@RequestParam AvatarDetailType type, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.get(auth, UserQueryType.BASE);
        if (user == null) return null;

        model.addAttribute("serverUrl", serverUrl);
        model.addAttribute("user", UserSerializer.serialize(user, AVATAR));

        if (type.equals(AvatarDetailType.PROFILE))
            return new ModelAndView("profil :: avatar_detail");

        else if (type.equals(AvatarDetailType.HEADER))
            return new ModelAndView("header :: avatar_detail");

        return null;
    }

    @PostMapping("change")
    public boolean change(@RequestParam(value = "id") Integer id) throws UserNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        avatarService.change(auth, userService.getEmail(auth), id);
        return true;
    }

}
