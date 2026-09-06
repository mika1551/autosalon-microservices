import application.service.AvailableCarService;
import infrastructure.web.controller.AvailableCarController;
import infrastructure.web.dto.AvailableCarDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AvailableCarControllerTest {

    private final AvailableCarService availableCarService = mock(AvailableCarService.class);
    private final AvailableCarController controller = new AvailableCarController(availableCarService);

    @Test
    void getAvailableCarsReturnsCarsFromService() {
        AvailableCarDto car = car(UUID.randomUUID());
        when(availableCarService.getAvailableCars()).thenReturn(List.of(car));

        List<AvailableCarDto> result = controller.getAvailableCars();

        assertThat(result).containsExactly(car);
        verify(availableCarService).getAvailableCars();
    }

    @Test
    void getAvailableCarByIdReturnsCarFromService() {
        UUID carId = UUID.randomUUID();
        AvailableCarDto car = car(carId);
        when(availableCarService.getAvailableCarById(carId)).thenReturn(car);

        AvailableCarDto result = controller.getAvailableCarById(carId);

        assertThat(result).isEqualTo(car);
        verify(availableCarService).getAvailableCarById(carId);
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
