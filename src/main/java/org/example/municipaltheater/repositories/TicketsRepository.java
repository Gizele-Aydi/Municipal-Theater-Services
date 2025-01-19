package org.example.municipaltheater.repositories;

import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.example.municipaltheater.models.ShowModels.Show;
import org.example.municipaltheater.models.ShowModels.Ticket;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TicketsRepository extends MongoRepository<Ticket, String> {
    Optional<Ticket> findByShowAndUser(Show show, RegisteredUser user);
    @Query("{ 'show.$id': ?0 }")
    List<Ticket> findByShowId(String showId);
    List<Ticket> findByShow(Show show);
    void deleteByShow(Show show);
}

