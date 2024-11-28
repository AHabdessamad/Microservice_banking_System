package com.Bank.Banking.System.controller;

import com.Bank.Banking.System.BankingSystemApplication;
import com.Bank.Banking.System.service.UserService;
import com.Bank.Banking.System.config.JwtProvider;
import com.Bank.Banking.System.model.User;
import com.Bank.Banking.System.repository.UserRepository;
import com.Bank.Banking.System.request.LoginRequest;
import com.Bank.Banking.System.response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userservice;
    @Autowired
    private BankingSystemApplication bankingSystemApplication;

    @GetMapping("/check")
    public ResponseEntity<String> checkUser(){
        return new ResponseEntity<String>("From Check", HttpStatus.OK );
    }


    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(
            @RequestBody User user
    ) throws Exception{
        System.out.println("Sigin Up body" + user.getFullName());
        String email = user.getEmail();
        String password = user.getPassword();
        String fullName = user.getFullName();
        String role= user.getRole();

        System.out.println("Sigin Up body" + fullName);
        System.out.println(email);

        User isEmailExist = userRepository.findUserByEmail(email);
        if(isEmailExist != null){
            throw new Exception("Email is Already Exist");
        }

        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setFullName(fullName);
        createdUser.setRole(role);
        createdUser.setPassword(passwordEncoder.encode(password));
        User savedUser = userRepository.save(createdUser);

        userRepository.save(savedUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Register Successfully");

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest){
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();

        authResponse.setJwt(token);
        authResponse.setMessage("Login Successfully");

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = userservice.loadUserByUsername(email);

        System.out.println("sigin in userdetials"+ userDetails.getUsername());

        if(userDetails == null){
            System.out.println("user details is null");
            throw new BadCredentialsException("Invalid email or password");
        }

        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid password...");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
