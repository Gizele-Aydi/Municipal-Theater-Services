package org.example.municipaltheater.repositories.DifferentUsersRepositories;

import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministratorsRepository extends MongoRepository<RegisteredUser, String> {
}

