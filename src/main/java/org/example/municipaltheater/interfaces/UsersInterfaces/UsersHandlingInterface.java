package org.example.municipaltheater.interfaces.UsersInterfaces;

import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UsersHandlingInterface {
    Page<Map<String, Object>> findAllUsersWithFilteredFields(Pageable pageable);
    RegisteredUser saveUser(RegisteredUser user);
    Map<String, Object> findUserByIdWithFilteredFields(String id);
    Optional<RegisteredUser> findUserProfileById(String userId);
    RegisteredUser updateUser(String id, RegisteredUser updatedUser);
    boolean deleteUserByID(String id);
    Map<String, Object> findBookedTicketsForUser(RegisteredUser user);
    List<Map<String, Object>> findBookingHistoryByUser(RegisteredUser user);

}
