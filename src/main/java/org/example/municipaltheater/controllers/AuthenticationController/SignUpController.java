package org.example.municipaltheater.controllers.AuthenticationController;

import org.example.municipaltheater.models.APIResponse;
import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import static org.example.municipaltheater.models.RegisteredUsers.Role.*;

import org.example.municipaltheater.repositories.DifferentUsersRepositories.AdministratorsRepository;
import org.example.municipaltheater.services.AuthenticationServices.EmailService;
import org.example.municipaltheater.services.RegisteredUsersServices.UsersService;
import org.example.municipaltheater.utils.DefinedExceptions.*;
import org.example.municipaltheater.repositories.DifferentUsersRepositories.RegisteredUsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
public class SignUpController {

    private final UsersService UserService;
    private final RegisteredUsersRepository UserRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AdministratorsRepository AdminRepo;

    @Autowired
    public SignUpController(UsersService userService, RegisteredUsersRepository userRepo, PasswordEncoder passwordEncoder, EmailService emailService, AdministratorsRepository adminRepo) {
        UserService = userService;
        UserRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        AdminRepo = adminRepo;
    }

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<APIResponse<RegisteredUser>> createUser(@RequestBody @Valid RegisteredUser user) {
        try {
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (UserRepo.findByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "This username is already taken.", null));
            }
            if (UserRepo.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "This email is already taken.", null));
            }
            if (!user.getEmail().matches(emailRegex)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "This email format is invalid.", null));
            }
            if (user.getEmail().equals("jyzelaydi123@gmail.com")) {
                user.setRole(ADMIN);
            } else {
                user.setRole(USER);
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            System.out.println(user.getPassword());
            RegisteredUser addedUser = UserService.saveUser(user);
            emailService.SendVerifEmail(user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>(HttpStatus.CREATED.value(), "User registered successfully.", addedUser));
        } catch (OAlreadyExistsException | OServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (OConstrainViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Validation failed: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + e.getMessage(), null));
        }
    }
}
