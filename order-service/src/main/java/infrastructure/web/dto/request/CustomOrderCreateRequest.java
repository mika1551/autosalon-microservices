package infrastructure.web.dto.request;

import java.util.UUID;

public record CustomOrderCreateRequest(String carModel, UUID configurationId) {
}
