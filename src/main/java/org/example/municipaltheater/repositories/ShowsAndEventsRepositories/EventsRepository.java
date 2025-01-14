package org.example.municipaltheater.repositories.ShowsAndEventsRepositories;

import org.example.municipaltheater.models.EventModels.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsRepository extends MongoRepository<Event, String> {
    List<Event> findByEventNameContainingIgnoreCase(String eventName);
    Event findByEventPhotoURL(String eventPhotoURL);
    boolean existsByEventDescription(String eventDescription);
    boolean existsByEventName(String eventName);

}

