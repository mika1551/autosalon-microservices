package application.service;

import domain.enums.ComponentType;
import domain.exception.DomainValidationException;
import domain.exception.IncompatibleComponentException;
import domain.model.CarConfiguration;
import domain.model.ComponentOption;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service

public class ConfiguratorService {
    public void validateConfiguration(CarConfiguration configuration, Set<ComponentType> requiredTypes){
        if (configuration == null){
            throw new DomainValidationException("Configuration is null");
        }
        if (configuration.getCarModel() == null || configuration.getCarModel().isEmpty()){
            throw new DomainValidationException("CarModel is empty");
        }
        if (configuration.getBasePrice() == null){
            throw new DomainValidationException("BasePrice is null");
        }
        Map<ComponentType, ComponentOption> selected = configuration.getSelectedOptions();
        if (selected == null || selected.isEmpty()){
            throw new DomainValidationException("Component options are empty");
        }
        if (requiredTypes != null){
            for (ComponentType type : requiredTypes){
                if (!selected.containsKey(type)){
                    throw new DomainValidationException("Component type not found: " + type);
                }
            }
        }
        for (ComponentOption option : selected.values()){
            if (option.getCompatibleCarModels() != null && !option.getCompatibleCarModels().contains(configuration.getCarModel())){
                throw new IncompatibleComponentException("Component " + option.getName() + " incompatible with model " + configuration.getCarModel());
            }
        }
    }
}
