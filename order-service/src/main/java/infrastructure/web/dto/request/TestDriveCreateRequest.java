package infrastructure.web.dto.request;

import java.util.Date;
import java.util.UUID;

public record TestDriveCreateRequest( UUID carId, Date date) {
}
