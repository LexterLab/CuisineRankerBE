package com.cranker.cranker.user.model;

import com.cranker.cranker.profile_pic.model.ProfilePicture;
import com.cranker.cranker.role.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Generated
    @ColumnDefault(value = "false")
    private Boolean isVerified;

    @Generated
    @ColumnDefault(value = "false")
    private Boolean isTwoFactorEnabled;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pic_id")
    private ProfilePicture selectedPic;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "user_pictures",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "pic_id", referencedColumnName = "id")
    )
    private List<ProfilePicture> profilePictures;
}