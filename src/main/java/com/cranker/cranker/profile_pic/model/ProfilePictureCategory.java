package com.cranker.cranker.profile_pic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "profile_pic_categories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProfilePictureCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
}
