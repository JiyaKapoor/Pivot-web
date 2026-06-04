package com.example.PivotVC_Web.Controller;

import com.example.PivotVC_Web.Entities.User;
import com.example.PivotVC_Web.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/repo")
public class UserController{
    @Autowired
    UserRepository userRepository;
    @PostMapping("/register-user")
    public void registerUser(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        User user = new User(username, email, password);
        userRepository.save(user);
    }
    @PostMapping("/login")
    public boolean login(@RequestParam String email,@RequestParam String password){
        Optional<User> user=userRepository.findByEmail(email);
        if(user.isEmpty())return false;
        return user.get().getPasswordHash().equals(password);
    }
}
