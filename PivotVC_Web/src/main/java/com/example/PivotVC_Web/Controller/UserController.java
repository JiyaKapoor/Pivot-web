package com.example.PivotVC_Web.Controller;

import com.example.PivotVC_Web.Entities.User;
import com.example.PivotVC_Web.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Long> login(
            @RequestParam String email,
            @RequestParam String password) {

        User user = userRepository.findByEmail(email).orElseThrow();

        if (user == null || !user.getPasswordHash().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(user.getId());
    }
}
