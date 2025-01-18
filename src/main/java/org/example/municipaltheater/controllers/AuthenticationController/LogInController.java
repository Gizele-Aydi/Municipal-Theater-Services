package org.example.municipaltheater.controllers.AuthenticationController;

import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.example.municipaltheater.repositories.DifferentUsersRepositories.RegisteredUsersRepository;
import org.example.municipaltheater.models.Authentication.request.LogInRequest;
import org.example.municipaltheater.models.Authentication.response.JWTResponse;
import org.example.municipaltheater.utils.JWTUtils;
import org.example.municipaltheater.security.services.UserDetailsImpl;
import org.example.municipaltheater.models.APIResponse;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
public class LogInController {

    private final AuthenticationManager AuthManager;
    private final RegisteredUsersRepository UserRepo;
    private final JWTUtils JWTUtils;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public LogInController(AuthenticationManager authManager, RegisteredUsersRepository userRepo, org.example.municipaltheater.utils.JWTUtils jwtUtils, BCryptPasswordEncoder encoder){
        AuthManager = authManager;
        UserRepo = userRepo;
        JWTUtils = jwtUtils;
        this.encoder = encoder;
    }

    @PostMapping("/LogIn")
    public ResponseEntity<APIResponse<JWTResponse>> authenticateUser(@Valid @RequestBody LogInRequest loginRequest) {
        System.out.println(loginRequest.getUsername());
        System.out.println(loginRequest.getPassword());
        Authentication authentication = AuthManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = JWTUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getRole().name();
        JWTResponse jwtResponse = new JWTResponse(jwt, userDetails.getUserID(), userDetails.getUsername(), userDetails.getEmail(), role);
        APIResponse<JWTResponse> response = new APIResponse<>(200, "Log IN successful: ", jwtResponse);
        return ResponseEntity.ok(response);
    }
}
