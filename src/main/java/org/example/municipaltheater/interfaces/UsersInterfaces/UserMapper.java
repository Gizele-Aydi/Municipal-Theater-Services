package org.example.municipaltheater.interfaces.UsersInterfaces;

import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.example.municipaltheater.models.RegisteredUsers.UserUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userID", ignore = true)
    RegisteredUser userUpdateDTOToRegisteredUser(UserUpdateDTO userUpdateDTO);
    UserUpdateDTO registeredUserToUserUpdateDTO(RegisteredUser registeredUser);
}
