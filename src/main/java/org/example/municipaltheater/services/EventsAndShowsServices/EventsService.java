package org.example.municipaltheater.services.EventsAndShowsServices;

import jakarta.validation.ValidationException;
import org.example.municipaltheater.interfaces.EventsInterfaces.EventsHandlingInterface;
import org.example.municipaltheater.models.EventModels.Event;
import org.example.municipaltheater.repositories.ShowsAndEventsRepositories.EventsRepository;
import org.example.municipaltheater.utils.DefinedExceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EventsService implements EventsHandlingInterface {

    @Autowired
    private EventsRepository EventRepo;

    public List<Event> findAllEvents() {
        return EventRepo.findAll();
    }

    public Event saveEvent(Event event) {
        StringBuilder errorMessages = getStringBuilder(event.getEventName(), event.getEventDate(), event.getEventDescription());
        if (!errorMessages.isEmpty()) {
            throw new ValidationException(errorMessages.toString().trim());
        }
        if (EventRepo.existsByEventNameAndEventDate(event.getEventName(), event.getEventDate())) {
            throw new DefinedExceptions.OAlreadyExistsException("Such an event already exists.");
        }
        if (isEventPhotoURLInUse(event.getEventPhotoURL(), null)) {
            throw new ValidationException("This photo URL is already in use by another event.");
        }

        System.out.println("Saving event with photo URL: " + event.getEventPhotoURL());
        return EventRepo.save(event);
    }

    public Optional<Event> findEventByID(String id) {
        return EventRepo.findById(id);
    }

    public Event updateEvent(String id, Event event) {
        StringBuilder errorMessages = getStringBuilder(event.getEventName(), event.getEventDate(), event.getEventDescription());
        if (!errorMessages.isEmpty()) {
            throw new ValidationException(errorMessages.toString().trim());
        }
        Optional<Event> eventOptional = EventRepo.findById(id);
        if (eventOptional.isPresent()) {
            Event existingEvent = eventOptional.get();
            boolean isChanged = false;
            if (!existingEvent.getEventName().equals(event.getEventName())) {
                existingEvent.setEventName(event.getEventName());
                isChanged = true;
            }
            if (!existingEvent.getEventDate().equals(event.getEventDate())) {
                existingEvent.setEventDate(event.getEventDate());
                isChanged = true;
            }
            if (!existingEvent.getEventDescription().equals(event.getEventDescription())) {
                existingEvent.setEventDescription(event.getEventDescription());
                isChanged = true;
            }
            if (event.getEventDuration() != null && !event.getEventDuration().equals(existingEvent.getEventDuration())) {
                existingEvent.setEventDuration(event.getEventDuration());
                isChanged = true;
            }
            if (event.getEventStartTime() != null && !event.getEventStartTime().equals(existingEvent.getEventStartTime())) {
                existingEvent.setEventStartTime(event.getEventStartTime());
                isChanged = true;
            }
            if (event.getEventPhotoURL() != null && !event.getEventPhotoURL().equals(existingEvent.getEventPhotoURL())) {
                if (isEventPhotoURLInUse(event.getEventPhotoURL(), id)) {
                    throw new ValidationException("This photo URL is already used by another event.");
                }
                existingEvent.setEventPhotoURL(event.getEventPhotoURL());
                isChanged = true;
            }
            if (!isChanged) {
                throw new ValidationException("There were no changes in the fields of the event.");
            }
            return EventRepo.save(existingEvent);
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

    public List<Event> searchEvents(String searchQuery) {
        List<Event> results = EventRepo.findByEventNameContainingIgnoreCase(searchQuery);
        return results;
    }

    private static StringBuilder getStringBuilder(String eventName, Date eventDate, String eventDescription) {
        StringBuilder errorMessages = new StringBuilder();
        if (eventName == null || eventName.trim().isEmpty()) {
            errorMessages.append("Event name is required. ");
        }
        if (eventDate == null) {
            errorMessages.append("Event date is required. ");
        }
        if (eventDescription == null || eventDescription.trim().isEmpty()) {
            errorMessages.append("Event description is required. ");
        }
        return errorMessages;
    }

    private boolean isEventPhotoURLInUse(String eventPhotoURL, String eventId) {
        if (eventPhotoURL != null && !eventPhotoURL.trim().isEmpty()) {
            Event existingEvent = EventRepo.findByEventPhotoURL(eventPhotoURL);
            return existingEvent != null && !existingEvent.getEventID().equals(eventId);
        }
        return false;
    }
}
