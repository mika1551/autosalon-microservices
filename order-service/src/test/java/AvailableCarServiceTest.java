import application.service.AvailableCarService;
import infrastructure.grpc.CarInventoryGrpcClient;
import infrastructure.web.dto.AvailableCarDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AvailableCarServiceTest {

    private final CarInventoryGrpcClient carInventoryGrpcClient = mock(CarInventoryGrpcClient.class);
    private final AvailableCarService service = new AvailableCarService(carInventoryGrpcClient);

    @Test
    void getAvailableCarsLoadsCarsThroughGrpcClient() {
        AvailableCarDto car = car(UUID.randomUUID());
        when(carInventoryGrpcClient.getAvailableCars()).thenReturn(List.of(car));

        List<AvailableCarDto> result = service.getAvailableCars();

        assertThat(result).containsExactly(car);
        verify(carInventoryGrpcClient).getAvailableCars();
    }

    @Test
    void getAvailableCarByIdLoadsCarThroughGrpcClient() {
        UUID carId = UUID.randomUUID();
        AvailableCarDto car = car(carId);
        when(carInventoryGrpcClient.getAvailableCarById(carId)).thenReturn(car);

        AvailableCarDto result = service.getAvailableCarById(carId);

        assertThat(result).isEqualTo(car);
        verify(carInventoryGrpcClient).getAvailableCarById(carId);
    }

    private static AvailableCarDto car(UUID id) {
        return new AvailableCarDto(
                id,
                "BMW",
                "320i",
                "Black",
                2024,
                new BigDecimal("3500000"),
                "Sedan",
                "Petrol",
                "ICE",
                "Automatic",
                184,
                "RWD",
                true
        );
    }
}
