package org.example.municipaltheater.interfaces.EventsInterfaces;

import org.example.municipaltheater.models.EventModels.EventUpdateDTO;
import org.example.municipaltheater.models.EventModels.*;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EventMapper {

    Event EventUpdateDTOToEvent(EventUpdateDTO eventUpdateDTO);
    EventUpdateDTO EventToEventUpdateDTO(Event event);
    void updateEventFromDTO(EventUpdateDTO eventUpdateDTO, @MappingTarget Event event);
}
