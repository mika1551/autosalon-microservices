package infrastructure.grpc;

import domain.model.Car;
import domain.repository.CarRepository;
import grpc.car.CarInventoryServiceGrpc;
import grpc.car.CarResponse;
import grpc.car.GetAvailableCarByIdRequest;
import grpc.car.GetAvailableCarsRequest;
import grpc.car.GetAvailableCarsResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@GrpcService
public class CarInventoryGrpcService extends CarInventoryServiceGrpc.CarInventoryServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(CarInventoryGrpcService.class);

    private final CarRepository carRepository;

    public CarInventoryGrpcService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public void getAvailableCars(
            GetAvailableCarsRequest request,
            StreamObserver<GetAvailableCarsResponse> responseObserver
    ) {
        logger.info("gRPC request: get available cars");

        GetAvailableCarsResponse response = GetAvailableCarsResponse.newBuilder()
                .addAllCars(
                        carRepository.findAvailableForSale().stream()
                                .filter(car -> !car.isRemoved())
                                .map(this::toResponse)
                                .toList()
                )
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAvailableCarById(
            GetAvailableCarByIdRequest request,
            StreamObserver<CarResponse> responseObserver
    ) {
        logger.info("gRPC request: get available car by id {}", request.getId());

        UUID carId;

        try {
            carId = UUID.fromString(request.getId());
        } catch (IllegalArgumentException ex) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Invalid car id: " + request.getId())
                            .asRuntimeException()
            );
            return;
        }

        carRepository.findById(carId)
                .filter(car -> car.isAvailableForSale() && !car.isRemoved())
                .map(this::toResponse)
                .ifPresentOrElse(
                        response -> {
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(
                                Status.NOT_FOUND
                                        .withDescription("Available car not found: " + request.getId())
                                        .asRuntimeException()
                        )
                );
    }

    private CarResponse toResponse(Car car) {
        return CarResponse.newBuilder()
                .setId(toString(car.getId()))
                .setBrand(toString(car.getBrand()))
                .setModel(toString(car.getModel()))
                .setColor(toString(car.getColor()))
                .setYear(toInt(car.getYear()))
                .setBasePrice(toString(car.getBasePrice()))
                .setBodyType(toString(car.getBodyType()))
                .setFuelType(toString(car.getFuelType()))
                .setEngineType(toString(car.getEngineType()))
                .setTransmissionType(toString(car.getTransmissionType()))
                .setHorsePower(toInt(car.getHorsePower()))
                .setDriveType(toString(car.getDriveType()))
                .setAvailableForSale(car.isAvailableForSale())
                .build();
    }

    private String toString(Object value) {
        return value == null ? "" : value.toString();
    }

    private int toInt(Integer value) {
        return value == null ? 0 : value;
    }
}
