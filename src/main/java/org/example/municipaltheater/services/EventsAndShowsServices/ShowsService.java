package org.example.municipaltheater.services.EventsAndShowsServices;

import org.example.municipaltheater.interfaces.ShowsHandlingInterface;
import org.example.municipaltheater.models.EventsAndShows.Show;
import org.example.municipaltheater.repositories.ShowsAndEventsRepositories.ShowsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowsService implements ShowsHandlingInterface {

    @Autowired
    private ShowsRepository ShowRepo;

    public List<Show> GetAllShows() {
        return ShowRepo.findAll();
    }

    public Show AddShow(Show show) {
        return ShowRepo.save(show);
    }

    public Optional<Show> GetShow(String id) {
        return ShowRepo.findById(id);
    }

    public Show UpdateShow(String id, Show updatedShow) {
        return ShowRepo.findById(id).map(existingShow -> {
            existingShow.setShowName(updatedShow.getShowName());
            existingShow.setShowDate(updatedShow.getShowDate());
            existingShow.setShowDescription(updatedShow.getShowDescription());
            return ShowRepo.save(existingShow);
        }).orElseThrow(() -> new RuntimeException("This Show wasn't found, ID: " + id));
    }

    public void DeleteShow(String id) {
        if (ShowRepo.existsById(id)) {
            ShowRepo.deleteById(id);
        } else {
            throw new RuntimeException("Show not found with ID: " + id);
        }
    }
}
