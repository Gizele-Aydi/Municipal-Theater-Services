package org.example.municipaltheater.repositories;

import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.example.municipaltheater.models.ShowModels.Show;
import org.example.municipaltheater.models.ShowModels.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TicketsRepository extends MongoRepository<Ticket, String> {
    Optional<Ticket> findByShowAndUser(Show show, RegisteredUser user);
    List<Ticket> findByShow(Show show);
    List<Ticket> findByUser(RegisteredUser user);

}

