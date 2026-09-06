package infrastructure.spec;

import domain.enums.ComponentType;
import domain.model.CarConfiguration;
import domain.model.ComponentOption;
import jakarta.persistence.criteria.MapJoin;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public final class CarConfigurationSpecification {

    private static Specification<CarConfiguration> hasBrand(String brand) {
        if (brand == null || brand.isBlank()) {
            return null;
        }
        String prefix = brand.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("carModel")), prefix);
    }

    private static Specification<CarConfiguration> hasOptionId(UUID optionId) {
        if (optionId == null) {
            return null;
        }
        return (root, query, cb) -> {
            query.distinct(true);
            MapJoin<CarConfiguration, ComponentType, ComponentOption> join = root.joinMap("selectedOptions");
            return cb.equal(join.value().get("id"), optionId);
        };
    }

    public static Specification<CarConfiguration> hasAllOptionIds(List<UUID> optionIds) {
        if (optionIds == null || optionIds.isEmpty()) return null;
        Specification<CarConfiguration> spec = hasOptionId(optionIds.getFirst());
        for (int i = 1; i < optionIds.size(); i++) {
            spec = spec.and(hasOptionId(optionIds.get(i)));
        }
        return spec;
    }

    public static Specification<CarConfiguration> hasComponentType(ComponentType type) {
        if (type == null) {
            return null;
        }
        return (root, query, cb) -> {
            query.distinct(true);
            MapJoin<CarConfiguration, ComponentType, ComponentOption> join =
                    root.joinMap("selectedOptions");
            return cb.equal(join.key(), type);
        };
    }

    public static Specification<CarConfiguration> hasAllComponentTypes(List<ComponentType> types) {
        if (types == null || types.isEmpty()) return null;
        Specification<CarConfiguration> spec = hasComponentType(types.getFirst());
        for (int i = 1; i < types.size(); i++) {
            spec = spec.and(hasComponentType(types.get(i)));
        }
        return spec;
    }

    public static Specification<CarConfiguration> filter(String brand, List<UUID> optionIds, List<ComponentType> types) {
        return Specification.where(hasBrand(brand))
                .and(hasAllOptionIds(optionIds))
                .and(hasAllComponentTypes(types));
    }
}
