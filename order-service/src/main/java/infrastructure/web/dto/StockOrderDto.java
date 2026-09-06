package infrastructure.web.dto;

import domain.enums.StockOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class StockOrderDto {
    private UUID id;
    private UUID clientId;
    private UUID carId;
    private UUID managerId;
    private StockOrderStatus status;
}
