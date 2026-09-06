package domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "test_drive_requests")

public class TestDriveRequest extends BaseEntity {

    @Column(name = "client_id", nullable = false)
    private UUID clientId;


    @Column(name = "car_id", nullable = false)
    private UUID carId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public TestDriveRequest(UUID id, UUID clientId, UUID carId, Date date) {
        setId(id);
        this.clientId = clientId;
        this.carId = carId;
        this.date = date;
    }


}
