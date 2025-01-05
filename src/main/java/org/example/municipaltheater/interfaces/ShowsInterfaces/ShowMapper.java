package org.example.municipaltheater.interfaces.ShowsInterfaces;

import org.example.municipaltheater.models.ShowModels.*;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShowMapper {

    Show ShowUpdateDTOToShow(ShowUpdateDTO showUpdateDTO);
    ShowUpdateDTO ShowToShowUpdateDTO(Show show);
    void updateShowFromDTO(ShowUpdateDTO showUpdateDTO, @MappingTarget Show show);
}
