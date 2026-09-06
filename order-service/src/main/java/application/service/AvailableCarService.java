package application.service;

import infrastructure.grpc.CarInventoryGrpcClient;
import infrastructure.web.dto.AvailableCarDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AvailableCarService {

    private final CarInventoryGrpcClient carInventoryGrpcClient;

    public AvailableCarService(CarInventoryGrpcClient carInventoryGrpcClient) {
        this.carInventoryGrpcClient = carInventoryGrpcClient;
    }

    public List<AvailableCarDto> getAvailableCars() {
        return carInventoryGrpcClient.getAvailableCars();
    }

    public AvailableCarDto getAvailableCarById(UUID id) {
        return carInventoryGrpcClient.getAvailableCarById(id);
    }
}
