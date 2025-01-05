package org.example.municipaltheater.interfaces.ShowsInterfaces;

import org.example.municipaltheater.models.ShowModels.Show;

import java.util.List;
import java.util.Optional;

public interface ShowsHandlingInterface {

    List<Show> findAllShows();
    Show saveShow(Show show);
    Optional<Show> findShowByID(String id);
    Show updateShow(String id, Show show);
    boolean deleteShowByID(String id);

}


