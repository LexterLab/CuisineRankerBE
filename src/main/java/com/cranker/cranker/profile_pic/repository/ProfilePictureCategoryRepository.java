package com.cranker.cranker.profile_pic.repository;

import com.cranker.cranker.profile_pic.model.ProfilePictureCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilePictureCategoryRepository extends JpaRepository<ProfilePictureCategory, Long> {
}
