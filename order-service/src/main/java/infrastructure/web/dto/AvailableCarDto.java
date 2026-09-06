package infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AvailableCarDto(

        UUID id,
        String brand,
        String model,
        String color,
        int year,
        BigDecimal basePrice,
        String bodyType,
        String fuelType,
        String engineType,
        String transmissionType,
        int horsePower,
        String driveType,
        boolean availableForSale

) {
}
