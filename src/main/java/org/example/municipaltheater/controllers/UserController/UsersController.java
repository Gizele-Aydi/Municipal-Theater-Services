package org.example.municipaltheater.controllers.UserController;

import org.example.municipaltheater.models.APIResponse;
import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.example.municipaltheater.models.RegisteredUsers.UserUpdateDTO;
import org.example.municipaltheater.services.RegisteredUsersServices.UsersService;
import org.example.municipaltheater.utils.DefinedExceptions.*;
import org.example.municipaltheater.interfaces.UsersInterfaces.*;
import org.example.municipaltheater.utils.ResponseGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UsersController {

    private final UsersService UserService;
    private final UserMapper UserMapper;

    @Autowired
    public UsersController(UsersService usersService, UserMapper userMapper) {
        this.UserService = usersService;
        UserMapper = userMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<APIResponse<Page<Map<String, Object>>>> getAllUsersWithFilteredFields(Pageable pageable) {
        Page<Map<String, Object>> users = UserService.findAllUsersWithFilteredFields(pageable);
        if (users.isEmpty()) {
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "The Users' list is empty.", null));
        } else {
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Users were successfully retrieved.", users));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Map<String, Object>>> getUserWithFilteredFields(@PathVariable String id) {
        try {
            Map<String, Object> user = UserService.findUserByIdWithFilteredFields(id);
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "User found.", user));
        } catch (ONotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse<RegisteredUser>> updateUser(@PathVariable String id, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        try {
            RegisteredUser userToUpdate = UserMapper.userUpdateDTOToRegisteredUser(userUpdateDTO);
            userToUpdate.setRole(null);
            userToUpdate.setBookedTickets(null);
            userToUpdate.setHistory(null);
            RegisteredUser updatedUser = UserService.updateUser(id, userToUpdate);
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "The user was updated successfully.", updatedUser));
        } catch (ONotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (OServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseGenerator.Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }

@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse<Void>> DeleteUser(@PathVariable String id) {
        try {
            boolean isDeleted = UserService.deleteUserByID(id);
            if (isDeleted) {
                return ResponseGenerator.Response(HttpStatus.OK, "The user was deleted successfully.", null);
            } else {
                return ResponseGenerator.Response(HttpStatus.NOT_FOUND, "This user wasn't found, ID: " + id, null);
            }
        } catch (Exception e) {
            return ResponseGenerator.Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }
}
