package org.example.municipaltheater.repositories.ShowsAndEventsRepositories;

import org.example.municipaltheater.models.EventsAndShows.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Repository
public interface EventsRepository extends MongoRepository<Event, String> {
    boolean existsByEventNameAndEventDateAndEventDescription(@NotBlank(message = "Event name is required.") String eventName, @NotNull(message = "Event date is required.") Date eventDate, @NotBlank(message = "Event description is required.") String eventDescription);

    boolean existsByEventNameAndEventDate(@NotBlank(message = "Event name is required.") String eventName, @NotNull(message = "Event date is required.") Date eventDate);
}
