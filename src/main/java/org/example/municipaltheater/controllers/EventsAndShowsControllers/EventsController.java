package org.example.municipaltheater.controllers.EventsAndShowsControllers;

import org.example.municipaltheater.models.APIResponse;
import org.example.municipaltheater.models.EventModels.*;
import org.example.municipaltheater.services.EventsAndShowsServices.EventsService;
import org.example.municipaltheater.utils.DefinedExceptions.*;
import org.example.municipaltheater.interfaces.EventsInterfaces.EventMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.*;

@RestController
@Validated
@RequestMapping("/Events")
@CrossOrigin(origins = "*")
public class EventsController {

    private final EventsService EventService;
    private final EventMapper eventMapper;

    @Autowired
    public EventsController(EventsService eventService, EventMapper eventMapper) {
        this.EventService = eventService;
        this.eventMapper = eventMapper;
    }

    @GetMapping(value = "/All")
    public ResponseEntity<APIResponse<Event>> GetAllEvents() {
        List<Event> events = EventService.findAllEvents();
        if (events.isEmpty()) {
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "The Events' list is empty.", null));
        } else {
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Shows retrieved successfully", events));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/Add")
    public ResponseEntity<APIResponse<Event>> AddEvent(@RequestBody @Valid EventUpdateDTO eventUpdateDTO) {
        try {
            Event event = eventMapper.EventUpdateDTOToEvent(eventUpdateDTO);
            Event addedEvent = EventService.saveEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>(HttpStatus.CREATED.value(), "Event added successfully.", addedEvent));
        } catch (OAlreadyExistsException | OServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<APIResponse<EventUpdateDTO>> GetEvent(@PathVariable String id) {
        try {
            return EventService.findEventByID(id)
                    .map(event -> {
                        EventUpdateDTO eventDTO = eventMapper.EventToEventUpdateDTO(event);
                        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Event found.", eventDTO));
                    })
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), "This event wasn't found, ID: " + id, null)));
        } catch (ONotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/Update/{id}")
    public ResponseEntity<APIResponse<Event>> UpdateEvent(@PathVariable String id, @RequestBody @Valid EventUpdateDTO eventUpdateDTO) {
        try {
            Event updatedEvent = EventService.updateEvent(id, eventMapper.EventUpdateDTOToEvent(eventUpdateDTO));
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "The Event was updated successfully.", updatedEvent));
        } catch (ONotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (OServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/Delete/{id}")
    public ResponseEntity<APIResponse<Void>> DeleteEvent(@PathVariable String id) {
        try {
            boolean isDeleted = EventService.deleteEventByID(id);
            if (isDeleted) {
                return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Event deleted successfully.", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), "This event wasn't found, ID: " + id, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

    @PostMapping("/Search")
    public ResponseEntity<APIResponse<SearchQuery>> searchEvents(@RequestBody Map<String, String> request) {
        String query = request.get("searchQuery");
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Search query cannot be empty.", null));
        }
        List<Event> matchingEvents = EventService.searchEvents(query);
        if (matchingEvents.isEmpty()) {
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "No shows found for your search.", null));
        }
        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Search results retrieved successfully.", new SearchQuery(matchingEvents)));
    }
}
