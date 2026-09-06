package infrastructure.web.dto;

import domain.enums.CustomOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CustomOrderDto {
    private UUID id;
    private UUID clientId;
    private UUID managerId;
    private String carModel;
    private UUID configurationId;
    private CustomOrderStatus status;
}
