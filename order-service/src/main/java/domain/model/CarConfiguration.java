package domain.model;

import domain.enums.ComponentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "car_configurations")

public class CarConfiguration extends BaseEntity {

    private String carModel;
    private BigDecimal basePrice;

    @ManyToMany
    @JoinTable(
            name = "car_configuration_options",
            joinColumns = @JoinColumn(name = "configuration_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    @MapKeyColumn(name = "component_type")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<ComponentType, ComponentOption> selectedOptions =  new EnumMap<>(ComponentType.class);

    public void selectOption(ComponentType componentType, ComponentOption option) {
        selectedOptions.put(option.getType(), option);
    }
    public  BigDecimal calculateTotalPrice() {
        BigDecimal total = basePrice;
        for (ComponentOption option : selectedOptions.values()) {
            total = total.add(option.getPriceDifference());
        }
        return total;
    }
}