package org.example.municipaltheater.repositories.DifferentUsersRepositories;


import org.example.municipaltheater.models.DifferentUsers.RegisteredUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredUsersRepository extends MongoRepository<RegisteredUser, String> {
}

