package org.example.municipaltheater.models.ShowModels;

import java.util.List;

public class SearchQuery {

    private List<Show> matchingShows;

    public SearchQuery(List<Show> matchingShows) {
        this.matchingShows = matchingShows;
    }

    public List<Show> getMatchingShows() {
        return matchingShows;
    }
}
