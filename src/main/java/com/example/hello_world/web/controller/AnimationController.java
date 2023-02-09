package com.example.hello_world.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RestController
public class AnimationController {


    @RequestMapping(value = {"/animation"})
    public ModelAndView openAdminView(Model model) {
        ModelAndView mav = new ModelAndView("animation");
        return mav;
    }

}
