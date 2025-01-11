package org.example.municipaltheater.controllers;

import org.example.municipaltheater.models.APIResponse;
import org.example.municipaltheater.models.EventModels.*;
import org.example.municipaltheater.models.EventModels.SearchQuery;
import org.example.municipaltheater.services.EventsAndShowsServices.EventsService;
import org.example.municipaltheater.utils.DefinedExceptions;
import org.example.municipaltheater.utils.ResponseGenerator;
import org.example.municipaltheater.interfaces.EventsInterfaces.EventMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/Events")
@CrossOrigin(origins = "*")
public class EventsController {

    @Autowired
    private EventsService eventService;

    @Autowired
    private EventMapper eventMapper;

    @GetMapping(value = "/All")
    public ResponseEntity<APIResponse<List<Event>>> GetAllEvents() {
        List<Event> events = eventService.findAllEvents();
        if (events.isEmpty()) {
            return ResponseGenerator.Response(HttpStatus.OK, "The Events list is empty.", null);
        } else {
            return ResponseGenerator.Response(HttpStatus.OK, "Events retrieved successfully.", events);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<APIResponse<EventUpdateDTO>> GetEvent(@PathVariable String id) {
        return eventService.findEventByID(id)
                .map(event -> {
                    EventUpdateDTO eventDTO = eventMapper.EventToEventUpdateDTO(event);
                    return ResponseGenerator.Response(HttpStatus.OK, "Event found.", eventDTO);
                })
                .orElseGet(() -> ResponseGenerator.Response(HttpStatus.NOT_FOUND, "This event wasn't found, ID: " + id, null));
    }

    @PostMapping(value = "/Add")
    public ResponseEntity<APIResponse<Event>> AddEvent(@RequestBody @Valid EventUpdateDTO eventUpdateDTO) {
        try {
            Event event = eventMapper.EventUpdateDTOToEvent(eventUpdateDTO);
            Event addedEvent = eventService.saveEvent(event);
            return ResponseGenerator.Response(HttpStatus.CREATED, "Event added successfully.", addedEvent);
        } catch (DefinedExceptions.OAlreadyExistsException e) {
            return ResponseGenerator.Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        } catch (Exception e) {
            return ResponseGenerator.Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }

    @PutMapping("/Update/{id}")
    public ResponseEntity<APIResponse<Event>> UpdateEvent(@PathVariable String id, @RequestBody @Valid EventUpdateDTO eventUpdateDTO) {
        try {
            Event updatedEvent = eventService.updateEvent(id, eventMapper.EventUpdateDTOToEvent(eventUpdateDTO));
            return ResponseGenerator.Response(HttpStatus.OK, "The Event was updated successfully.", updatedEvent);
        } catch (DefinedExceptions.ONotFoundException e) {
            return ResponseGenerator.Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            return ResponseGenerator.Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }


    @DeleteMapping(value = "/Delete/{id}")
    public ResponseEntity<APIResponse<Void>> DeleteEvent(@PathVariable String id) {
        try {
            boolean isDeleted = eventService.deleteEventByID(id);
            if (isDeleted) {
                return ResponseGenerator.Response(HttpStatus.OK, "Event deleted successfully.", null);
            } else {
                return ResponseGenerator.Response(HttpStatus.NOT_FOUND, "This event wasn't found, ID: " + id, null);
            }
        } catch (Exception e) {
            return ResponseGenerator.Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }

    @PostMapping("/Search")
    public ResponseEntity<?> searchEvents(@RequestBody Map<String, String> request) {
        String query = request.get("searchQuery");
        List<Event> matchingEvents = eventService.searchEvents(query);
        if (matchingEvents == null) {
            matchingEvents = new ArrayList<>();
        }
        return ResponseEntity.ok(new SearchQuery(matchingEvents));
    }
}
