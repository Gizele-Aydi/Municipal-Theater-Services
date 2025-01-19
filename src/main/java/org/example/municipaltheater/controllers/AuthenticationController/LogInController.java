package org.example.municipaltheater.controllers.AuthenticationController;

import jakarta.servlet.http.HttpServletRequest;
import org.example.municipaltheater.models.Authentication.request.LogInRequest;
import org.example.municipaltheater.models.Authentication.response.JWTResponse;
import org.example.municipaltheater.models.Authentication.response.BlackListedToken;
import org.example.municipaltheater.repositories.TokensRepositories.BlackListedTokensRepository;
import org.example.municipaltheater.utils.JWTUtils;
import org.example.municipaltheater.security.services.UserDetailsImpl;

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
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LogInController {

    private final AuthenticationManager AuthManager;
    private final JWTUtils JWTUtils;
    private final BlackListedTokensRepository BTokenRepo;

    @Autowired
    public LogInController(AuthenticationManager authManager, org.example.municipaltheater.utils.JWTUtils jwtUtils, BlackListedTokensRepository bTokenRepo){
        AuthManager = authManager;
        JWTUtils = jwtUtils;
        BTokenRepo = bTokenRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LogInRequest loginRequest) {
        Authentication authentication = AuthManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = JWTUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getRole().name();
        return ResponseEntity.ok(new JWTResponse(jwt, userDetails.getUserID(), userDetails.getUsername(), userDetails.getEmail(), role));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String jwt = extractJwtFromRequest(request);
        
        if (jwt != null && JWTUtils.validateJwtToken(jwt)) {
            BlackListedToken blacklistedToken = new BlackListedToken(jwt, JWTUtils.getExpirationFromToken(jwt));
            BTokenRepo.save(blacklistedToken);
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok().body("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("Invalid token");
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

}
