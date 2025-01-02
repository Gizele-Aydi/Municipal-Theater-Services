package org.example.municipaltheater.controllers;

import org.example.municipaltheater.models.EventsAndShows.Show;
import org.example.municipaltheater.services.EventsAndShowsServices.ShowsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/Shows")
@CrossOrigin(origins = "*")
public class ShowsController {

    @Autowired
    private ShowsService ShowService;

    @GetMapping(value="/All")
    public ResponseEntity<List<Show>> GetAllShows() {
        List<Show> shows = ShowService.GetAllShows();
        return ResponseEntity.ok(shows);
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<Show> GetShowByID(@PathVariable String id) {
        return ShowService.GetShow(id).map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping(value="/Add")
    public ResponseEntity<Show> AddShow(@Valid @RequestBody Show show) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ShowService.AddShow(show));
    }

    @PutMapping(value="/Update/{id}")
    public ResponseEntity<Show> UpdateShow(@PathVariable String id, @Valid @RequestBody Show show) {
        return ResponseEntity.ok(ShowService.UpdateShow(id, show));
    }

    @DeleteMapping(value="/Delete/{id}")
    public ResponseEntity<?> DeleteShow(@PathVariable String id) {
        ShowService.DeleteShow(id);
        return ResponseEntity.ok().body("Show deleted successfully.");
    }
}
