package com.cranker.cranker.recipe;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "recipes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String preparation;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private String type;

    @Column(nullable = false)
    private Integer prepTimeInMinutes;

    @Column(nullable = false)
    private Integer cookTimeInMinutes;

    @Transient
    private Integer totalTimeInMinutes;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeIngredient> recipeIngredients;

    public void setType(RecipeType type) {
        this.type = type.getName();
    }
}
