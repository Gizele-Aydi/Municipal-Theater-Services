package org.example.municipaltheater.interfaces;

import org.example.municipaltheater.models.EventsAndShows.Event;
import org.example.municipaltheater.models.EventsAndShows.EventDTO;

import org.example.municipaltheater.models.EventsAndShows.EventUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface EventMapper {

    Event eventDTOToEvent(EventDTO eventDTO);
    EventDTO eventToEventDTO(Event event);
    void updateEventFromDTO(EventUpdateDTO eventUpdateDTO, @MappingTarget Event event);

}

