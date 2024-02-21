package com.cranker.cranker.user;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserResponseMapper {
    UserResponseMapper INSTANCE = Mappers.getMapper(UserResponseMapper.class);
    @Mapping(expression = "java(user.getFirstName() + ' ' + user.getLastName())", target = "name")
    UserDTO entityToDTO(User user);

    List<UserDTO> entityToDTO(Iterable<User> users);

    User dtoToEntity(UserDTO userDTO);

    List<User> dtoToEntity(Iterable<UserDTO> userResponseDTOS);
}
