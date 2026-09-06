package infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SparePartDto {
    private UUID id;
    private String color;
    private String brand;
    private String name;
    private BigDecimal price;
    private Set<String> compatibleCarModels;
}
