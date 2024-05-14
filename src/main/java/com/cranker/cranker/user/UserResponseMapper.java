package com.cranker.cranker.user;

import com.cranker.cranker.friendship.Friendship;
import com.cranker.cranker.friendship.FriendshipDTO;
import com.cranker.cranker.friendship.FriendshipResponse;
import com.cranker.cranker.user.payload.UserDTO;
import com.cranker.cranker.user.payload.UserRequestDTO;
import com.cranker.cranker.user.payload.UserResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserResponseMapper {
    UserResponseMapper INSTANCE = Mappers.getMapper(UserResponseMapper.class);
    @Mapping(expression = "java(user.getFirstName() + ' ' + user.getLastName())", target = "name")
    @Mapping(expression = "java(user.getSelectedPic().getUrl())", target = "profilePicURL")
    UserDTO entityToDTO(User user);

    @Mapping(target = "users", source = "userDTOS")
    @Mapping(target = "pageNo", expression = "java(userPage.getNumber())")
    @Mapping(target = "pageSize", expression = "java(userPage.getSize())")
    @Mapping(target = "totalElements", expression = "java(userPage.getTotalElements())")
    @Mapping(target = "totalPages", expression = "java(userPage.getTotalPages())")
    @Mapping(target = "last", expression = "java(userPage.isLast())")
    UserResponse pageToUserResponse(Page<User> userPage, List<UserDTO> userDTOS);

    UserRequestDTO entityToRequestDTO(User user);

    List<UserDTO> entityToDTO(Iterable<User> users);

    User dtoToEntity(UserDTO userDTO);

    void updateUserFromDto(UserRequestDTO dto, @MappingTarget User entity);

    List<User> dtoToEntity(Iterable<UserDTO> userResponseDTOS);


}
