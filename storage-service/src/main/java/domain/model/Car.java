package domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cars")


public class Car extends BaseEntity {

    private String color;
    private String model;
    private String brand;
    private Integer year;
    private BigDecimal basePrice;
    private BigDecimal discountPrice;
    private String bodyType;
    private Integer mileageKm;
    private String fuelType;
    private String engineType;
    private String transmissionType;
    private Integer horsePower;
    private String driveType;
    private boolean availableForSale;
    private boolean availableForTestDrive;


    @Column(name = "engine_volume_l")
    private Integer engineVolumeL;

}
