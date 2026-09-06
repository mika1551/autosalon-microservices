package application.service;

import domain.enums.CustomOrderStatus;
import domain.exception.DomainValidationException;
import domain.exception.EntityNotFoundException;
import domain.model.CarConfiguration;
import domain.model.CustomOrder;
import domain.repository.CarConfigurationRepository;
import domain.repository.CustomOrderRepository;
import domain.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomOrderService {
    private final CustomOrderRepository customOrderRepository;
    private final UserRepository userRepository;
    private final CarConfigurationRepository carConfigurationRepository;

    public CustomOrderService(CustomOrderRepository customOrderRepository,
                              UserRepository userRepository,
                              CarConfigurationRepository carConfigurationRepository) {
        this.customOrderRepository = customOrderRepository;
        this.userRepository = userRepository;
        this.carConfigurationRepository = carConfigurationRepository;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CustomOrder createOrder(UUID clientId, String carModel, UUID configurationId) {
        CarConfiguration configuration = carConfigurationRepository.findById(configurationId)
                .orElseThrow(() -> new EntityNotFoundException("Config not found: " + configurationId));

        UUID managerId = userRepository.findAnyManager()
                .orElseThrow(() -> new DomainValidationException("No manager available"))
                .getId();

        CustomOrder order = new CustomOrder(
                UUID.randomUUID(),
                clientId,
                managerId,
                carModel,
                configuration,
                CustomOrderStatus.CREATED
        );
        return customOrderRepository.save(order);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or @security.isCustomOrderOwner(#id)")
    public CustomOrder getById(UUID id) {
        return customOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CustomOrder not found: " + id));
    }

    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    public List<CustomOrder> getVisibleOrders(UUID currentUserId) {
        if (hasRole("ADMIN") || hasRole("MANAGER")) {
            return customOrderRepository.findAll();
        }
        return customOrderRepository.findByClientId(currentUserId);
    }

    @PreAuthorize("hasAnyRole('MANAGER','WAREHOUSE_ADMIN','ADMIN')")
    public CustomOrder updateStatus(UUID id, CustomOrderStatus status) {
        CustomOrder order = getById(id);
        order.setStatus(status);
        return customOrderRepository.save(order);
    }

    @PreAuthorize("hasRole('ADMIN') or @security.isCustomOrderOwner(#id)")
    public CustomOrder cancelOrder(UUID id) {
        CustomOrder order = getById(id);
        order.setStatus(CustomOrderStatus.CANCELED);
        return customOrderRepository.save(order);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(UUID id) {
        customOrderRepository.deleteById(id);
    }

    private boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        String authority = "ROLE_" + roleName;
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> authority.equals(grantedAuthority.getAuthority()));
    }
}
