package com.cranker.cranker.profile_pic.repository;

import com.cranker.cranker.profile_pic.model.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, Long> {
    Optional<ProfilePicture> findByNameIgnoreCase(String name);
    List<ProfilePicture> findAllByCategoryName(String name);
}
