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
import java.util.Optional;

@RestController
@RequestMapping("/Users")
@CrossOrigin(origins = "*")
public class UsersController {

    private final UsersService UserService;
    private final UserMapper userMapper;

    @Autowired
    public UsersController(UsersService usersService, UserMapper userMapper) {
        this.UserService = usersService;
        this.userMapper = userMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/All")
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
    @PutMapping("/Update/{id}")
    public ResponseEntity<APIResponse<RegisteredUser>> UpdateUser(@PathVariable String id, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        try {
            RegisteredUser updatedUser = UserService.updateUser(id, userMapper.userUpdateDTOToRegisteredUser(userUpdateDTO));
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "The User was updated successfully.", updatedUser));
        } catch (ONotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (OServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseGenerator.Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage(), null);
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/Delete/{id}")
    public ResponseEntity<APIResponse<Void>> DeleteUser(@PathVariable String id) {
        try {
            boolean isDeleted = UserService.deleteUserByID(id);
            if (isDeleted) {
                return ResponseGenerator.Response(HttpStatus.OK, "User deleted successfully.", null);
            } else {
                return ResponseGenerator.Response(HttpStatus.NOT_FOUND, "This user wasn't found, ID: " + id, null);
            }
        } catch (Exception e) {
            return ResponseGenerator.Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }
}
