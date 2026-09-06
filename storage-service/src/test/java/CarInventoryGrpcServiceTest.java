import domain.model.Car;
import domain.repository.CarRepository;
import grpc.car.CarResponse;
import grpc.car.GetAvailableCarByIdRequest;
import grpc.car.GetAvailableCarsRequest;
import grpc.car.GetAvailableCarsResponse;
import infrastructure.grpc.CarInventoryGrpcService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CarInventoryGrpcServiceTest {

    @Test
    void getAvailableCarsReturnsOnlyNotRemovedAvailableCars() {
        UUID availableCarId = UUID.randomUUID();
        TestCarRepository repository = new TestCarRepository(List.of(
                car(availableCarId, "BMW", true, false),
                car(UUID.randomUUID(), "Audi", true, true)
        ));
        CarInventoryGrpcService service = new CarInventoryGrpcService(repository);
        CapturingObserver<GetAvailableCarsResponse> observer = new CapturingObserver<>();

        service.getAvailableCars(GetAvailableCarsRequest.newBuilder().build(), observer);

        assertThat(observer.completed).isTrue();
        assertThat(observer.error).isNull();
        assertThat(observer.value.getCarsList()).hasSize(1);
        assertThat(observer.value.getCars(0).getId()).isEqualTo(availableCarId.toString());
        assertThat(observer.value.getCars(0).getBrand()).isEqualTo("BMW");
    }

    @Test
    void getAvailableCarByIdReturnsCarWhenAvailable() {
        UUID carId = UUID.randomUUID();
        TestCarRepository repository = new TestCarRepository(List.of(car(carId, "BMW", true, false)));
        CarInventoryGrpcService service = new CarInventoryGrpcService(repository);
        CapturingObserver<CarResponse> observer = new CapturingObserver<>();

        service.getAvailableCarById(
                GetAvailableCarByIdRequest.newBuilder()
                        .setId(carId.toString())
                        .build(),
                observer
        );

        assertThat(observer.completed).isTrue();
        assertThat(observer.error).isNull();
        assertThat(observer.value.getId()).isEqualTo(carId.toString());
        assertThat(observer.value.getAvailableForSale()).isTrue();
    }

    @Test
    void getAvailableCarByIdReturnsNotFoundWhenCarUnavailable() {
        UUID carId = UUID.randomUUID();
        TestCarRepository repository = new TestCarRepository(List.of(car(carId, "BMW", false, false)));
        CarInventoryGrpcService service = new CarInventoryGrpcService(repository);
        CapturingObserver<CarResponse> observer = new CapturingObserver<>();

        service.getAvailableCarById(
                GetAvailableCarByIdRequest.newBuilder()
                        .setId(carId.toString())
                        .build(),
                observer
        );

        assertThat(statusCode(observer.error)).isEqualTo(Status.Code.NOT_FOUND);
    }

    @Test
    void getAvailableCarByIdReturnsInvalidArgumentWhenIdInvalid() {
        TestCarRepository repository = new TestCarRepository(List.of());
        CarInventoryGrpcService service = new CarInventoryGrpcService(repository);
        CapturingObserver<CarResponse> observer = new CapturingObserver<>();

        service.getAvailableCarById(
                GetAvailableCarByIdRequest.newBuilder()
                        .setId("wrong-id")
                        .build(),
                observer
        );

        assertThat(statusCode(observer.error)).isEqualTo(Status.Code.INVALID_ARGUMENT);
    }

    private static Status.Code statusCode(Throwable throwable) {
        return Status.fromThrowable(throwable).getCode();
    }

    private static Car car(UUID id, String brand, boolean availableForSale, boolean removed) {
        Car car = new Car();
        car.setId(id);
        car.setBrand(brand);
        car.setModel("320i");
        car.setColor("Black");
        car.setYear(2024);
        car.setBasePrice(new BigDecimal("3500000"));
        car.setBodyType("Sedan");
        car.setFuelType("Petrol");
        car.setEngineType("ICE");
        car.setTransmissionType("Automatic");
        car.setHorsePower(184);
        car.setDriveType("RWD");
        car.setAvailableForSale(availableForSale);
        car.setRemoved(removed);
        return car;
    }

    private static class CapturingObserver<T> implements StreamObserver<T> {

        private T value;
        private Throwable error;
        private boolean completed;

        @Override
        public void onNext(T value) {
            this.value = value;
        }

        @Override
        public void onError(Throwable throwable) {
            this.error = throwable;
        }

        @Override
        public void onCompleted() {
            this.completed = true;
        }
    }

    private static class TestCarRepository implements CarRepository {

        private final List<Car> cars = new ArrayList<>();

        TestCarRepository(List<Car> cars) {
            this.cars.addAll(cars);
        }

        @Override
        public Car save(Car entity) {
            cars.add(entity);
            return entity;
        }

        @Override
        public Optional<Car> findById(UUID id) {
            return cars.stream()
                    .filter(car -> car.getId().equals(id))
                    .findFirst();
        }

        @Override
        public List<Car> findAll() {
            return cars;
        }

        @Override
        public void deleteById(UUID id) {
            cars.removeIf(car -> car.getId().equals(id));
        }

        @Override
        public List<Car> findAvailableForSale() {
            return cars.stream()
                    .filter(Car::isAvailableForSale)
                    .toList();
        }

        @Override
        public List<Car> findAvailableForTestDrive() {
            return cars.stream()
                    .filter(Car::isAvailableForTestDrive)
                    .toList();
        }
    }
}
