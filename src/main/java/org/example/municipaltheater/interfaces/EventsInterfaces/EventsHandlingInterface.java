package org.example.municipaltheater.interfaces.EventsInterfaces;

import org.example.municipaltheater.models.EventModels.*;

import java.util.List;
import java.util.Optional;

public interface EventsHandlingInterface {

    List<Event> findAllEvents();
    Event saveEvent(Event event);
    Optional<Event> findEventByID(String id);
    Event updateEvent(String id, Event event);
    boolean deleteEventByID(String id);
}