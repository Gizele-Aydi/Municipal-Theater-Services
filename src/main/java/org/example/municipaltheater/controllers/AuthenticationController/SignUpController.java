package org.example.municipaltheater.controllers.AuthenticationController;

import org.example.municipaltheater.models.APIResponse;
import org.example.municipaltheater.services.AuthenticationServices.EmailService;
import org.example.municipaltheater.services.RegisteredUsersServices.UsersService;
import org.example.municipaltheater.utils.DefinedExceptions.*;
import org.example.municipaltheater.utils.ResponseGenerator;
import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.example.municipaltheater.repositories.DifferentUsersRepositories.RegisteredUsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.Valid;

@RestController
public class SignUpController {

    private final UsersService UserService;
    private final RegisteredUsersRepository UserRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public SignUpController(UsersService userService, RegisteredUsersRepository userRepo, PasswordEncoder passwordEncoder, EmailService emailService) {
        UserService = userService;
        UserRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostMapping(value = "/SignUp", consumes = "application/json")
    public ResponseEntity<APIResponse<RegisteredUser>> CreateUser(@RequestBody @Valid RegisteredUser user){
        try {
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (UserRepo.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Email is already used.", null));
            }
            if (user.getPhoneNum() != null && UserRepo.findByPhoneNum(user.getPhoneNum()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Phone number is already used.", null));
            }
            if (!user.getEmail().matches(emailRegex)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid email format.", null));
            }


            user.setPassword(passwordEncoder.encode(user.getPassword()));
            RegisteredUser addedUser = UserService.saveUser(user);
            emailService.SendVerifEmail(user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new APIResponse<>(HttpStatus.CREATED.value(), "User registered successfully.", addedUser));

        } catch (OAlreadyExistsException | OServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (OConstrainViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Validation failed: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseGenerator.Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }


}