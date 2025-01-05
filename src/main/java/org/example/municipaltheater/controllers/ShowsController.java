package org.example.municipaltheater.controllers;

import org.example.municipaltheater.models.APIResponse;
import org.example.municipaltheater.models.ShowModels.*;
import org.example.municipaltheater.services.EventsAndShowsServices.ShowsService;
import org.example.municipaltheater.utils.DefinedExceptions;
import org.example.municipaltheater.utils.ResponseGenerator;
import org.example.municipaltheater.interfaces.ShowsInterfaces.ShowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Shows")
@CrossOrigin(origins = "*")
public class ShowsController {

    @Autowired
    private ShowsService ShowService;

    @Autowired
    private ShowMapper showMapper;

    @GetMapping(value = "/All")
    public ResponseEntity<APIResponse<List<Show>>> GetAllShows() {
        List<Show> shows = ShowService.findAllShows();
        if (shows.isEmpty()) {
            return ResponseGenerator.Response(HttpStatus.OK, "The Shows list is empty", null);
        } else {
            return ResponseGenerator.Response(HttpStatus.OK, "Shows retrieved successfully", shows);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<APIResponse<Show>> GetShow(@PathVariable String id) {
        return ShowService.findShowByID(id).map(show -> ResponseGenerator.Response(HttpStatus.OK, "Show found.", show))
                .orElseGet(() -> ResponseGenerator.Response(HttpStatus.NOT_FOUND, "This show wasn't found, ID: " + id, null));
    }

    @PostMapping(value = "/Add")
    public ResponseEntity<APIResponse<Show>> AddShow(@RequestBody ShowUpdateDTO showUpdateDTO) {
        try {
            Show show = showMapper.ShowUpdateDTOToShow(showUpdateDTO);
            Show addedShow = ShowService.saveShow(show);
            return ResponseGenerator.Response(HttpStatus.CREATED, "Show added successfully", addedShow);
        } catch (DefinedExceptions.OAlreadyExistsException e) {
            return ResponseGenerator.Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        } catch (Exception e) {
            return ResponseGenerator.Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }

    @PutMapping("/Update/{id}")
    public ResponseEntity<APIResponse<Show>> UpdateShow(@PathVariable String id, @RequestBody ShowUpdateDTO showUpdateDTO) {
        try {
            Show show = showMapper.ShowUpdateDTOToShow(showUpdateDTO);
            Show updatedShow = ShowService.updateShow(id, show);
            return ResponseGenerator.Response(HttpStatus.OK, "The Show was updated successfully.", updatedShow);
        } catch (DefinedExceptions.ONotFoundException e) {
            return ResponseGenerator.Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            return ResponseGenerator.Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }

    @DeleteMapping(value = "/Delete/{id}")
    public ResponseEntity<APIResponse<Void>> deleteShow(@PathVariable String id) {
        try {
            boolean isDeleted = ShowService.deleteShowByID(id);
            if (isDeleted) {
                return ResponseGenerator.Response(HttpStatus.OK, "Show deleted successfully", null);
            } else {
                return ResponseGenerator.Response(HttpStatus.NOT_FOUND, "This show wasn't found, ID: " + id, null);
            }
        } catch (Exception e) {
            return ResponseGenerator.Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }
}
