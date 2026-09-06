package infrastructure.grpc;

import domain.exception.EntityNotFoundException;
import grpc.car.CarInventoryServiceGrpc;
import grpc.car.CarResponse;
import grpc.car.GetAvailableCarByIdRequest;
import grpc.car.GetAvailableCarsRequest;
import infrastructure.exception.GrpcServiceUnavailableException;
import infrastructure.web.dto.AvailableCarDto;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component

public class CarInventoryGrpcClient {

    private static final Logger logger = LoggerFactory.getLogger(CarInventoryGrpcClient.class);
    private static final long TIMEOUT_SECONDS = 2;

    @GrpcClient("storageService")
    private CarInventoryServiceGrpc.CarInventoryServiceBlockingStub blockingStub;

    public List<AvailableCarDto> getAvailableCars() {
        logger.info("gRPC client request: get available cars from StorageService");

        try {
            return blockingStub.withDeadlineAfter(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .getAvailableCars(GetAvailableCarsRequest.newBuilder().build())
                    .getCarsList()
                    .stream()
                    .map(this::toDto)
                    .toList();
        } catch (StatusRuntimeException ex) {
            throw mapException(ex, "StorageService is unavailable while loading available cars");
        }
    }

    public AvailableCarDto getAvailableCarById(UUID id) {
        logger.info("gRPC client request: get available car by id {} from StorageService", id);

        try {
            CarResponse response = blockingStub.withDeadlineAfter(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .getAvailableCarById(
                            GetAvailableCarByIdRequest.newBuilder()
                                    .setId(id.toString())
                                    .build()
                    );

            return toDto(response);
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new EntityNotFoundException("Available car not found: " + id);
            }
            throw mapException(ex, "StorageService is unavailable while loading available car: " + id);
        }
    }

    private RuntimeException mapException(StatusRuntimeException ex, String message) {
        Status.Code code = ex.getStatus().getCode();

        if (code == Status.Code.UNAVAILABLE || code == Status.Code.DEADLINE_EXCEEDED) {
            logger.error(message, ex);
            return new GrpcServiceUnavailableException(message, ex);
        }

        logger.error("Unexpected gRPC error from StorageService", ex);
        return new GrpcServiceUnavailableException(message, ex);
    }

    private AvailableCarDto toDto(CarResponse response) {
        return new AvailableCarDto(
                UUID.fromString(response.getId()),
                response.getBrand(),
                response.getModel(),
                response.getColor(),
                response.getYear(),
                toBigDecimal(response.getBasePrice()),
                response.getBodyType(),
                response.getFuelType(),
                response.getEngineType(),
                response.getTransmissionType(),
                response.getHorsePower(),
                response.getDriveType(),
                response.getAvailableForSale()
        );
    }

    private BigDecimal toBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value);
    }
}
