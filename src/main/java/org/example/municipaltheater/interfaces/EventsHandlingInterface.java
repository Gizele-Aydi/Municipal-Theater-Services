package org.example.municipaltheater.interfaces;

import org.example.municipaltheater.models.EventsAndShows.Event;
import org.example.municipaltheater.models.EventsAndShows.EventDTO;
import org.example.municipaltheater.models.EventsAndShows.EventUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface EventsHandlingInterface {

    List<Event> findAllEvents();
    Event saveEvent(EventDTO eventDTO);
    Optional<Event> findEventByID(String id);
    Event updateEvent(String id, EventUpdateDTO eventUpdateDTO);
    boolean deleteEventByID(String id);
}