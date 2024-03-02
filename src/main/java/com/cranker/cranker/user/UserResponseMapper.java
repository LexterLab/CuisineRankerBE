package com.cranker.cranker.user;

import com.cranker.cranker.user.payload.UserDTO;
import com.cranker.cranker.user.payload.UserRequestDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserResponseMapper {
    UserResponseMapper INSTANCE = Mappers.getMapper(UserResponseMapper.class);
    @Mapping(expression = "java(user.getFirstName() + ' ' + user.getLastName())", target = "name")
    UserDTO entityToDTO(User user);

    UserRequestDTO entityToRequestDTO(User user);

    List<UserDTO> entityToDTO(Iterable<User> users);

    User dtoToEntity(UserDTO userDTO);

    void updateUserFromDto(UserRequestDTO dto, @MappingTarget User entity);

    List<User> dtoToEntity(Iterable<UserDTO> userResponseDTOS);


}
