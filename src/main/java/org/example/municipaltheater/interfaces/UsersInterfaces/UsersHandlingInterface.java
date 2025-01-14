package org.example.municipaltheater.interfaces.UsersInterfaces;

import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsersHandlingInterface {
    UserDetails loadUserByUsername(String username);
    Page<RegisteredUser> findAllUsers(Pageable pageable);
    RegisteredUser saveUser(RegisteredUser user);
    Optional<RegisteredUser> findUserById(String id);
    RegisteredUser updateUser(String id, RegisteredUser updatedUser);
    boolean deleteUserByID(String id);
}
