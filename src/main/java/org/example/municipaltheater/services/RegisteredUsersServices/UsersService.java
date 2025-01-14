package org.example.municipaltheater.services.RegisteredUsersServices;

import org.example.municipaltheater.interfaces.UsersInterfaces.UsersHandlingInterface;
import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.example.municipaltheater.repositories.DifferentUsersRepositories.RegisteredUsersRepository;
import org.example.municipaltheater.utils.DefinedExceptions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.slf4j.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UsersService implements UserDetailsService, UsersHandlingInterface {

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);
    private final RegisteredUsersRepository UserRepo;

    @Autowired
    public UsersService(RegisteredUsersRepository UserRepo) {
        this.UserRepo = UserRepo;
    }

    public Page<RegisteredUser> findAllUsers(Pageable pageable) {
        logger.info("Fetching all users with pagination: page {} size {}", pageable.getPageNumber(), pageable.getPageSize());
        return UserRepo.findAll(pageable);
    }

    public RegisteredUser saveUser(@Valid RegisteredUser user) {
        logger.info("Attempting to save user: {}", user.getUsername());
        List<String> missingFields = getMissingFields(user);
        if (!missingFields.isEmpty()) {
            throw new OServiceException(String.join(" and ", missingFields) + " is/are empty");
        }
        validateUserUniqueness(user);
        return UserRepo.save(user);
    }

    public Optional<RegisteredUser> findUserById(String id) {
        logger.info("Fetching user by ID: {}", id);
        Optional<RegisteredUser> user = UserRepo.findById(id);
        if (user.isEmpty()) {
            throw new ONotFoundException("This user wasn't found. ID: " + id);
        }
        return user;
    }

    public RegisteredUser updateUser(String id, @Valid RegisteredUser updatedUser) {
        logger.info("Attempting to update user with ID: {}", id);
        List<String> missingFields = getMissingFields(updatedUser);
        if (!missingFields.isEmpty()) {
            throw new OServiceException(String.join(" and ", missingFields) + " is/are empty");
        }
        Optional<RegisteredUser> existingUserOpt = UserRepo.findById(id);
        if (existingUserOpt.isEmpty()) {
            throw new ONotFoundException("This user wasn't found. ID: " + id);
        }
        RegisteredUser user = existingUserOpt.get();
        boolean isUpdated = false;

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(user.getUsername())) {
            user.setUsername(updatedUser.getUsername());
            isUpdated = true;
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            user.setEmail(updatedUser.getEmail());
            isUpdated = true;
        }
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().equals(user.getPassword())) {
            user.setPassword(updatedUser.getPassword());
            isUpdated = true;
        }
        if (updatedUser.getPhoneNum() != null && !updatedUser.getPhoneNum().equals(user.getPhoneNum())) {
            user.setPhoneNum(updatedUser.getPhoneNum());
            isUpdated = true;
        }
        if (!isUpdated) {
            throw new OServiceException("There were no changes in the fields of the user.");
        }
        return UserRepo.save(user);
    }

    public boolean deleteUserByID(String id) {
        logger.info("Attempting to delete user with ID: {}", id);
        if (!UserRepo.existsById(id)) {
            throw new ONotFoundException("This user wasn't found. ID: " + id);
        }
        UserRepo.deleteById(id);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username: {}", username);
        RegisteredUser user = UserRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("This user with the following Username wasn't found: " + username));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    private void validateUserUniqueness(RegisteredUser user) {
        if (UserRepo.findByUsername(user.getUsername()).isPresent()) {
            throw new OAlreadyExistsException("Username is already taken.");
        }
        if (UserRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new OAlreadyExistsException("Email is already registered.");
        }
        if (user.getPhoneNum() != null && UserRepo.findByPhoneNum(user.getPhoneNum()).isPresent()) {
            throw new OAlreadyExistsException("Phone number is already registered.");
        }
    }

    private List<String> getMissingFields(RegisteredUser user) {
        return Stream.of(
                user.getUsername() == null || user.getUsername().isEmpty() ? "Username" : null,
                user.getEmail() == null || user.getEmail().isEmpty() ? "Email" : null,
                user.getPassword() == null || user.getPassword().isEmpty() ? "Password" : null
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
