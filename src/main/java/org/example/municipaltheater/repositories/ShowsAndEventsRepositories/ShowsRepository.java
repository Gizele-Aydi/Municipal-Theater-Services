package org.example.municipaltheater.repositories.ShowsAndEventsRepositories;

import org.example.municipaltheater.models.EventsAndShows.Show;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowsRepository extends MongoRepository<Show, String> {
}
