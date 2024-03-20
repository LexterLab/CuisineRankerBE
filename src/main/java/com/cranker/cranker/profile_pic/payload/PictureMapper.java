package com.cranker.cranker.profile_pic.payload;

import com.cranker.cranker.profile_pic.model.ProfilePicture;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PictureMapper {
    PictureMapper INSTANCE = Mappers.getMapper(PictureMapper.class);

    @Mapping(expression = "java(picture.getCategory().getName())", target = "categoryName")
    PicturesDTO entityToDTO(ProfilePicture picture);

    List<PicturesDTO> entityToDTO(Iterable<ProfilePicture> pictures);
}
