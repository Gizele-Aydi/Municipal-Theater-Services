package org.example.municipaltheater.services.EventsAndShowsServices;

import org.example.municipaltheater.interfaces.EventsInterfaces.EventsHandlingInterface;
import org.example.municipaltheater.models.EventModels.Event;
import org.example.municipaltheater.repositories.ShowsAndEventsRepositories.EventsRepository;
import org.example.municipaltheater.utils.DefinedExceptions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.util.*;
import java.util.stream.*;

@Service
public class EventsService implements EventsHandlingInterface {

    private static final Logger logger = LoggerFactory.getLogger(EventsService.class);
    private final EventsRepository eventRepo;

    @Autowired
    public EventsService(EventsRepository eventRepo) {
        this.eventRepo = eventRepo;
    }

    public List<Event> findAllEvents() {
        logger.info("Fetching all events");
        return eventRepo.findAll();
    }

    public Event saveEvent(@Valid Event event) {
        logger.info("Attempting to save event: {}", event.getEventName());
        List<String> missingFields = getMissingFields(event);
        if (!missingFields.isEmpty()) {
            throw new OServiceException(String.join(" and ", missingFields) + " is/are empty");
        }
        if (eventRepo.existsByEventName(event.getEventName())) {
            throw new OAlreadyExistsException("An event with the same name already exists.");
        }
        if (eventRepo.existsByEventDescription(event.getEventDescription())) {
            throw new OAlreadyExistsException("An event with the same description already exists.");
        }
        if (isEventPhotoURLInUse(event.getEventPhotoURL(), null)) {
            throw new OServiceException("This photo URL is already in use by another event.");
        }

        return eventRepo.save(event);
    }

    public Optional<Event> findEventByID(String id) {
        logger.info("Fetching event by ID: {}", id);
        Optional<Event> event = eventRepo.findById(id);
        if (event.isEmpty()) {
            throw new ONotFoundException("This event wasn't found, ID: " + id);
        }
        return event;
    }

    public Event updateEvent(String id, @Valid Event event) {
        logger.info("Attempting to update event with ID: {}", id);
        List<String> missingFields = getMissingFields(event);
        if (!missingFields.isEmpty()) {
            throw new OServiceException(String.join(" and ", missingFields) + " is/are empty");
        }
        Optional<Event> existingEventOpt = eventRepo.findById(id);
        if (existingEventOpt.isEmpty()) {
            throw new ONotFoundException("This event wasn't found, ID: " + id);
        }
        Event existingEvent = existingEventOpt.get();
        boolean isChanged = false;
        if (event.getEventName() != null && !event.getEventName().equals(existingEvent.getEventName())) {
            existingEvent.setEventName(event.getEventName());
            isChanged = true;
        }
        if (event.getEventDate() != null && !event.getEventDate().equals(existingEvent.getEventDate())) {
            existingEvent.setEventDate(event.getEventDate());
            isChanged = true;
        }
        if (event.getEventDescription() != null && !event.getEventDescription().equals(existingEvent.getEventDescription())) {
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
                throw new OServiceException("This photo URL is already used by another event.");
            }
            existingEvent.setEventPhotoURL(event.getEventPhotoURL());
            isChanged = true;
        }
        if (!isChanged) {
            throw new OServiceException("There were no changes in the fields of the event.");
        }

        return eventRepo.save(existingEvent);
    }

    public boolean deleteEventByID(String id) {
        logger.info("Attempting to delete event with ID: {}", id);
        if (!eventRepo.existsById(id)) {
            throw new ONotFoundException("This event wasn't found, ID: " + id);
        }
        eventRepo.deleteById(id);
        return true;
    }

    public List<Event> searchEvents(String searchQuery) {
        logger.info("Searching events with query: {}", searchQuery);
        return eventRepo.findByEventNameContainingIgnoreCase(searchQuery);
    }

    private boolean isEventPhotoURLInUse(String eventPhotoURL, String eventId) {
        if (eventPhotoURL != null && !eventPhotoURL.trim().isEmpty()) {
            Event existingEvent = eventRepo.findByEventPhotoURL(eventPhotoURL);
            return existingEvent != null && !existingEvent.getEventID().equals(eventId);
        }
        return false;
    }

    private List<String> getMissingFields(Event event) {
        return Stream.of(
                event.getEventName() == null || event.getEventName().isEmpty() ? "Event Name" : null,
                event.getEventDate() == null ? "Event Date" : null,
                event.getEventDescription() == null || event.getEventDescription().isEmpty() ? "Event Description" : null
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
