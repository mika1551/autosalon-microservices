package domain.model;

import domain.enums.ComponentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "component_options")

public class ComponentOption extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private ComponentType type;

    private String name;

    private BigDecimal priceDifference;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "component_option_models", joinColumns = @JoinColumn(name = "option_id"))
    @Column(name = "model")
    private Set<String> compatibleCarModels = new HashSet<>();


    public ComponentOption(UUID id,
                           ComponentType type,
                           String name,
                           BigDecimal priceDifference,
                           Set<String> compatibleCarModels) {
        setId(id);
        this.type = type;
        this.name = name;
        this.priceDifference = priceDifference;
        this.compatibleCarModels = compatibleCarModels;
    }
}