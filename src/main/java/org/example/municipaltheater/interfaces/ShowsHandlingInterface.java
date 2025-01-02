package org.example.municipaltheater.interfaces;

import org.example.municipaltheater.models.EventsAndShows.Show;

import java.util.List;
import java.util.Optional;

public interface ShowsHandlingInterface {

    List<Show> GetAllShows();
    Show AddShow(Show show);
    Optional<Show> GetShow(String id);
    Show UpdateShow(String id, Show updatedShow);
    void DeleteShow(String id);

}


