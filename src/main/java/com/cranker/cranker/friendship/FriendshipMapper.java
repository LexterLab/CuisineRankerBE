package com.cranker.cranker.friendship;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FriendshipMapper {
    FriendshipMapper INSTANCE = Mappers.getMapper(FriendshipMapper.class);

    @Mapping(target = "friendId", expression = "java(friendship.getFriend().getId())")
    @Mapping(target = "friendName", expression = "java(friendship.getFriend().getFirstName() + ' ' + friendship.getFriend().getLastName())")
    @Mapping(target = "friendImage", expression =  "java(friendship.getFriend().getSelectedPic().getUrl())")
    @Mapping(target = "updatedAtFormatted", expression = "java(friendship.getUpdatedAt().getDayOfMonth() + \" \" + java.time.format.DateTimeFormatter.ofPattern(\"MMMM\").format(friendship.getUpdatedAt()) + \" \" + friendship.getUpdatedAt().getYear())")
    FriendshipDTO friendshipToFriendshipDTOFriendVersion(Friendship friendship);

    @Mapping(target = "friendId", expression = "java(friendship.getFriend().getId())")
    @Mapping(target = "friendName", expression = "java(friendship.getFriend().getFirstName()  + ' ' + friendship.getFriend().getLastName())")
    @Mapping(target = "friendImage", expression =  "java(friendship.getFriend().getSelectedPic().getUrl())")
    @Mapping(target = "updatedAtFormatted", expression = "java(friendship.getUpdatedAt().getDayOfMonth() + \" \" + java.time.format.DateTimeFormatter.ofPattern(\"MMMM\").format(friendship.getUpdatedAt()) + \" \" + friendship.getUpdatedAt().getYear())")
    FriendshipDTO friendshipToFriendshipDTOUserVersion(Friendship friendship);



    @Mapping(target = "friendships", source = "friendshipDTOS")
    @Mapping(target = "pageNo", expression = "java(friendshipPage.getNumber())")
    @Mapping(target = "pageSize", expression = "java(friendshipPage.getSize())")
    @Mapping(target = "totalElements", expression = "java(friendshipPage.getTotalElements())")
    @Mapping(target = "totalPages", expression = "java(friendshipPage.getTotalPages())")
    @Mapping(target = "last", expression = "java(friendshipPage.isLast())")
    FriendshipResponse pageToFriendshipResponse(Page<Friendship> friendshipPage, List<FriendshipDTO> friendshipDTOS);
}
