package application.service;

import domain.enums.AssemblyOrderStatus;
import domain.event.OrderApprovedEvent;
import domain.event.OrderRejectedEvent;
import domain.event.OrderSentForApprovalEvent;
import domain.model.AssemblyOrder;
import domain.repository.CarRepository;
import domain.repository.SparePartRepository;
import infrastructure.messaging.OrderDecisionPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderApprovalProcessor {

    private final AssemblyOrderService assemblyOrderService;
    private final CarRepository carRepository;
    private final SparePartRepository sparePartRepository;
    private final OrderDecisionPublisher orderDecisionPublisher;

    public OrderApprovalProcessor(
            AssemblyOrderService assemblyOrderService,
            CarRepository carRepository,
            SparePartRepository sparePartRepository,
            OrderDecisionPublisher orderDecisionPublisher
    ) {
        this.assemblyOrderService = assemblyOrderService;
        this.carRepository = carRepository;
        this.sparePartRepository = sparePartRepository;
        this.orderDecisionPublisher = orderDecisionPublisher;
    }

    @Transactional
    public void process(OrderSentForApprovalEvent event) {
        var existingOrder = assemblyOrderService.findBySourceOrderIdAndSourceOrderType(
                event.orderId(),
                event.orderType()
        );

        if (existingOrder.isPresent()) {
            publishDecision(existingOrder.get(), event);
            return;
        }

        var assemblyOrder = assemblyOrderService.create(
                event.orderId(),
                event.orderType(),
                event.carModel(),
                Set.of(),
                null
        );

        if (canApprove(event)) {
            assemblyOrder = assemblyOrderService.updateStatus(assemblyOrder.getId(), AssemblyOrderStatus.ASSEMBLED);
            publishDecision(assemblyOrder, event);
            return;
        }

        assemblyOrder = assemblyOrderService.updateStatus(assemblyOrder.getId(), AssemblyOrderStatus.FAIL);
        publishDecision(assemblyOrder, event);
    }

    private boolean canApprove(OrderSentForApprovalEvent event) {
        if ("STOCK".equals(event.orderType())) {
            try {
                UUID carId = UUID.fromString(event.carModel());
                return carRepository.findById(carId)
                        .map(car -> car.isAvailableForSale() && !car.isRemoved())
                        .orElse(false);
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }

        if ("CUSTOM".equals(event.orderType())) {
            return !sparePartRepository.findByCompatibleCarModel(event.carModel()).isEmpty();
        }

        return false;
    }

    private void publishDecision(AssemblyOrder assemblyOrder, OrderSentForApprovalEvent event) {
        if (assemblyOrder.getStatus() == AssemblyOrderStatus.ASSEMBLED) {
            orderDecisionPublisher.publishApproved(new OrderApprovedEvent(
                    event.orderId(),
                    event.traceId(),
                    event.orderType(),
                    Instant.now()
            ));
            return;
        }

        if (assemblyOrder.getStatus() == AssemblyOrderStatus.FAIL) {
            orderDecisionPublisher.publishRejected(new OrderRejectedEvent(
                    event.orderId(),
                    event.traceId(),
                    event.orderType(),
                    "Storage cannot assemble order",
                    Instant.now()
            ));
        }
    }
}
