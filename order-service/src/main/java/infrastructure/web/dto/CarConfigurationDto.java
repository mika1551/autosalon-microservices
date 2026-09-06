package infrastructure.web.dto;

import domain.enums.ComponentType;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class CarConfigurationDto {

    private UUID id;
    private String carModel;
    private BigDecimal basePrice;
    private Map<ComponentType, UUID> selectedOptions;

    public CarConfigurationDto() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public Map<ComponentType, UUID> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(Map<ComponentType, UUID> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

}
