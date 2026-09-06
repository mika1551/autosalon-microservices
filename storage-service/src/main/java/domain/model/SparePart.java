package domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "spare_parts")

public class SparePart extends BaseEntity {

    private String color;
    private String brand;
    private String name;
    private BigDecimal price;

    @ElementCollection
    @CollectionTable(name = "spare_part_models", joinColumns = @JoinColumn(name = "spare_part_id"))
    @Column(name = "model")
    private Set<String> compatibleCarModels;

    public SparePart(UUID id, String color, String brand, String name, BigDecimal price, Set<String> compatibleCarModels) {

        setId(id);
        this.color = color;
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.compatibleCarModels = compatibleCarModels;

    }
}