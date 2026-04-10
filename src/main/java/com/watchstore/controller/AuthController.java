package com.watchstore.controller;

import com.watchstore.model.User;
import com.watchstore.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session, Model model) {
        var userOpt = userService.loginUser(email, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("loggedUser", user);
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole().name());
            if (user.getRole() == User.Role.ADMIN) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/shop";
        }
        model.addAttribute("error", "Invalid email or password!");
        model.addAttribute("user", new User());
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("error", "Email already registered!");
            return "auth/register";
        }
        userService.registerUser(user);
        return "redirect:/auth/login?registered=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login?logout=true";
    }
}
