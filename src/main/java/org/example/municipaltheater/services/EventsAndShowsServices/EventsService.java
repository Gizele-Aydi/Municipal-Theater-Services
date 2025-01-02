package org.example.municipaltheater.services.EventsAndShowsServices;

import org.example.municipaltheater.interfaces.EventMapper;
import org.example.municipaltheater.interfaces.EventsHandlingInterface;
import org.example.municipaltheater.models.EventsAndShows.Event;
import org.example.municipaltheater.models.EventsAndShows.EventDTO;
import org.example.municipaltheater.models.EventsAndShows.EventUpdateDTO;
import org.example.municipaltheater.repositories.ShowsAndEventsRepositories.EventsRepository;
import org.example.municipaltheater.utils.DefinedExceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventsService implements EventsHandlingInterface {

    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private EventsRepository EventRepo;

    public List<Event> findAllEvents() {
        return EventRepo.findAll();
    }

    public Event saveEvent(EventDTO eventDTO) {
        Event event = eventMapper.eventDTOToEvent(eventDTO);
        if (EventRepo.existsByEventNameAndEventDate(event.getEventName(), event.getEventDate())) {
            throw new DefinedExceptions.OAlreadyExistsException("Such an event already exists.");
        }
        return EventRepo.save(event);
    }

    public Optional<Event> findEventByID(String id) {
        return EventRepo.findById(id);
    }

    public Event updateEvent(String id, EventUpdateDTO eventUpdateDTO) {
        Optional<Event> eventOptional = EventRepo.findById(id);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            eventMapper.updateEventFromDTO(eventUpdateDTO, event);
            return EventRepo.save(event);
        } else {
            throw new DefinedExceptions.ONotFoundException("This event wasn't found, ID: " + id);
        }
    }

    public boolean deleteEventByID(String id) {
        if (EventRepo.existsById(id)) {
            EventRepo.deleteById(id);
            return true;
        } else {
            throw new DefinedExceptions.ONotFoundException("This event wasn't found, ID: " + id);
        }
    }

}
