package com.cmpe275.openhome.service;

import com.cmpe275.openhome.model.User;
import com.cmpe275.openhome.notification.EmailNotification;
import com.cmpe275.openhome.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    EmailNotification emailNotification;


    public User signupUser(User user) throws Exception {
        try {
            // Creating user's account
            UUID authcode = UUID.randomUUID();
            user.setName(user.getName());
            String email = user.getEmail();
            user.setEmail(email);
            user.setPassword(user.getPassword());
            user.setProvider(user.getProvider());
            user.setAuthcode(authcode.toString());

            if (user.getPassword()!=null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            if(email.substring(email.indexOf("@")).equals("@sjsu.edu")){
                user.setRole("host");
            }
            else{
                user.setRole("guest");
            }

            User result = userRepository.save(user);

            String baseURL = "http://localhost:8080";
            System.out.println("Frontend URL is " + baseURL);

            String verifyURL = "/registration-confirmation/?token=" + authcode.toString();
            String subject = String.format("OpenHome - Your account verification required");
            String text = String.format("Hi %s, \n\nPlease confirm your registration for OpenHome by clicking the link below. \n  %s  \n\n\n Thank you, \n Team OpenHome",
                   user.getName(), baseURL + verifyURL);

            emailNotification.sendEmail(email, subject, text);

            return result;

        }
        catch(Exception e) {
            throw e;
        }
    }

    public boolean verify(String authcode)  throws Exception {
        try {
            Optional<User> userOption = userRepository.findByAuthcode(authcode);


            if (userOption.isPresent()) {
                User user = userOption.get();
                user.setEmailVerified(true);
                System.out.println(user.getEmailVerified());
                userRepository.save(user);
                return true;
            } else { // if auth code not found in DB
                System.out.println("User not found with for the given token");
                return false;
            }
        }
        catch(Exception e) {
            throw e;
        }
    }
}
