package com.cmpe275.openhome.controller;

import com.cmpe275.openhome.exception.BadRequestException;
import com.cmpe275.openhome.model.AuthProvider;
import com.cmpe275.openhome.model.User;
import com.cmpe275.openhome.payload.ApiResponse;
import com.cmpe275.openhome.payload.AuthResponse;
import com.cmpe275.openhome.payload.LoginRequest;
import com.cmpe275.openhome.payload.SignUpRequest;
import com.cmpe275.openhome.repository.UserRepository;
import com.cmpe275.openhome.security.TokenProvider;
import com.cmpe275.openhome.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AuthService authService;


    @PostMapping("/api/auth/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/api/auth/signup")
        public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        User result = null;
        try {
            User user = new User();
            user.setName(signUpRequest.getName());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(signUpRequest.getPassword());
            user.setProvider(AuthProvider.local);
            result = authService.signupUser(user);
        } catch (Exception e) {
            //send a failure status code like 500
            e.printStackTrace();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully@"));
    }

    @RequestMapping(value = "/api/auth/verify", method = RequestMethod.POST )
    public ResponseEntity<?> verify( @RequestBody Map<String, Object> payload) {
        System.out.println(payload);
        String authcode = (String) payload.get("authcode");
        try {
            if (authService.verify(authcode)) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("verified");
            } else {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("Invalid authcode");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(e);
        }
    }

}
