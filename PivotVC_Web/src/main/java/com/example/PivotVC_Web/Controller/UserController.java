package com.example.PivotVC_Web.Controller;

import com.example.PivotVC_Web.Entities.User;
import com.example.PivotVC_Web.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@CrossOrigin(origins="*")
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
        System.out.println("Input password = " + password);
        System.out.println("DB password = " + user.get().getPasswordHash());
        return user.get().getPasswordHash().equals(password);
    }
}
