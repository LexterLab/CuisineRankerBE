package com.cranker.cranker.ingredient;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingredients")
@Getter
@Setter()
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private Double defaultAmount;
    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private String amountType;

    public void setAmountType(AmountType amountType) {
        this.amountType = amountType.getName();
    }
}
