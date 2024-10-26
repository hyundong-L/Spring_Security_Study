package org.security_study.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/success")
    public String successPage() {
        return "loginSuccess";
    }

    @GetMapping("/fail")
    public String failPage() {
        return "loginFailed";
    }

    @GetMapping("/signup/fail")
    public String signupFailPage() {
        return "/signupFailed";
    }
}
