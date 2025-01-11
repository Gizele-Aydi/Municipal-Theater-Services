package org.example.municipaltheater.models.EventModels;

import java.util.List;

public class SearchQuery {

    private List<Event> matchingEvents;

    public SearchQuery(List<Event> matchingEvents) {
        this.matchingEvents = matchingEvents;
    }

    public List<Event> getMatchingEvents() {
        return matchingEvents;
    }
}
