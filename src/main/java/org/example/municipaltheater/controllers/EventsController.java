package org.example.municipaltheater.controllers;

import org.example.municipaltheater.models.EventsAndShows.*;
import org.example.municipaltheater.services.EventsAndShowsServices.EventsService;
import org.example.municipaltheater.utils.DefinedExceptions;
import org.example.municipaltheater.utils.ResponseGenerator;
import org.example.municipaltheater.utils.ResponseGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/Events")
@CrossOrigin(origins = "*")
public class EventsController {

    @Autowired
    private EventsService EventService;

    @GetMapping(value = "/All")
    public ResponseEntity<APIResponse<List<Event>>> GetAllEvents() {
        List<Event> events = EventService.findAllEvents();
        if (events.isEmpty()) {
            return ResponseGenerator.Response(HttpStatus.NOT_FOUND, "The Events list is empty", null);
        } else {
            return ResponseGenerator.Response(HttpStatus.OK, "Events retrieved successfully", events);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<APIResponse<Event>> GetEvent(@PathVariable String id) {
        return EventService.findEventByID(id)
                .map(event -> ResponseGenerator.Response(HttpStatus.OK, "Event found", event))
                .orElseGet(() -> ResponseGenerator.Response(HttpStatus.NOT_FOUND, "This event wasn't found, ID: " + id, null));
    }

    @PostMapping(value = "/Add")
    public ResponseEntity<APIResponse<Event>> AddEvent(@RequestBody @Valid EventDTO eventDTO) {
        try {
            Event AddedEvent = EventService.saveEvent(eventDTO);
            return ResponseGenerator.Response(HttpStatus.CREATED, "Event added successfully", AddedEvent);
        } catch (DefinedExceptions.OAlreadyExistsException e) {
            return ResponseGenerator.Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        } catch (Exception e) {
            return ResponseGenerator.Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }

    @PutMapping("/Update/{id}")
    public ResponseEntity<APIResponse<Event>> UpdateEvent(@PathVariable String id, @RequestBody @Valid EventUpdateDTO eventUpdateDTO) {
        try {
            Event UpdatedEvent = EventService.updateEvent(id, eventUpdateDTO);
            return ResponseGenerator.Response(HttpStatus.OK, "Event updated successfully", UpdatedEvent);
        } catch (DefinedExceptions.ONotFoundException e) {
            return ResponseGenerator.Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            return ResponseGenerator.Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }

    @DeleteMapping(value = "/Delete/{id}")
    public ResponseEntity<APIResponse<Void>> deleteEvent(@PathVariable String id) {
        try {
            boolean isDeleted = EventService.deleteEventByID(id);
            if (isDeleted) {
                return ResponseGenerator.Response(HttpStatus.OK, "Event deleted successfully", null);
            } else {
                return ResponseGenerator.Response(HttpStatus.NOT_FOUND, "This event wasn't found, ID: " + id, null);
            }
        } catch (Exception e) {
            return ResponseGenerator.Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }
}


