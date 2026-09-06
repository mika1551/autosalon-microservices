package infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CarDto {
    private UUID id;
    private String color;
    private String model;
    private String brand;
    private int year;
    private BigDecimal basePrice;
    private BigDecimal discountPrice;
    private String bodyType;
    private int mileageKm;
    private String fuelType;
    private String engineType;
    private String transmissionType;
    private int horsePower;
    private String driveType;
    private boolean availableForSale;
    private boolean availableForTestDrive;
}

