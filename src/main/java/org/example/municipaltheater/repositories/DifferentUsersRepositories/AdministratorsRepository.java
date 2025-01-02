package org.example.municipaltheater.repositories.DifferentUsersRepositories;


import org.example.municipaltheater.models.DifferentUsers.Administrator;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministratorsRepository extends MongoRepository<Administrator, String> {
}

