package com.cranker.cranker.user.model;

import com.cranker.cranker.user.model.constant.AvailableProvider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "social_users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SocialUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private String provider;

    @Column(nullable = false, unique = true)
    private String providerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void setProvider(AvailableProvider provider) {
        this.provider = provider.getName();
    }
}
