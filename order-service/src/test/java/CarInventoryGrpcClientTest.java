import domain.exception.EntityNotFoundException;
import grpc.car.CarInventoryServiceGrpc;
import grpc.car.CarResponse;
import grpc.car.GetAvailableCarByIdRequest;
import grpc.car.GetAvailableCarsRequest;
import grpc.car.GetAvailableCarsResponse;
import infrastructure.exception.GrpcServiceUnavailableException;
import infrastructure.grpc.CarInventoryGrpcClient;
import infrastructure.web.dto.AvailableCarDto;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CarInventoryGrpcClientTest {

    private final CarInventoryServiceGrpc.CarInventoryServiceBlockingStub blockingStub = mock(
            CarInventoryServiceGrpc.CarInventoryServiceBlockingStub.class
    );
    private final CarInventoryGrpcClient client = new CarInventoryGrpcClient();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "blockingStub", blockingStub);
        when(blockingStub.withDeadlineAfter(anyLong(), eq(TimeUnit.SECONDS))).thenReturn(blockingStub);
    }

    @Test
    void getAvailableCarsMapsGrpcResponseToDto() {
        UUID carId = UUID.randomUUID();
        when(blockingStub.getAvailableCars(any(GetAvailableCarsRequest.class)))
                .thenReturn(GetAvailableCarsResponse.newBuilder()
                        .addCars(carResponse(carId))
                        .build());

        List<AvailableCarDto> result = client.getAvailableCars();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(carId);
        assertThat(result.getFirst().brand()).isEqualTo("BMW");
        assertThat(result.getFirst().basePrice()).isEqualByComparingTo(new BigDecimal("3500000"));
    }

    @Test
    void getAvailableCarsThrowsServiceUnavailableWhenStorageUnavailable() {
        when(blockingStub.getAvailableCars(any(GetAvailableCarsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));

        assertThatThrownBy(client::getAvailableCars)
                .isInstanceOf(GrpcServiceUnavailableException.class);
    }

    @Test
    void getAvailableCarByIdThrowsNotFoundWhenStorageReturnsNotFound() {
        UUID carId = UUID.randomUUID();
        when(blockingStub.getAvailableCarById(any(GetAvailableCarByIdRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        assertThatThrownBy(() -> client.getAvailableCarById(carId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private static CarResponse carResponse(UUID id) {
        return CarResponse.newBuilder()
                .setId(id.toString())
                .setBrand("BMW")
                .setModel("320i")
                .setColor("Black")
                .setYear(2024)
                .setBasePrice("3500000")
                .setBodyType("Sedan")
                .setFuelType("Petrol")
                .setEngineType("ICE")
                .setTransmissionType("Automatic")
                .setHorsePower(184)
                .setDriveType("RWD")
                .setAvailableForSale(true)
                .build();
    }
}
