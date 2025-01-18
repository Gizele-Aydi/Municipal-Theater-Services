package org.example.municipaltheater.repositories.DifferentUsersRepositories;


import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegisteredUsersRepository extends MongoRepository<RegisteredUser, String> {
    Optional<RegisteredUser> findByUserID(String userID);
    Optional<RegisteredUser> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<Object> findByEmail(String email);
    boolean existsByEmail(String email);
    //Optional<User> findByVerificationCode(String verificationCode);

}

