package infrastructure.exception;

public class GrpcServiceUnavailableException extends RuntimeException {

    public GrpcServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
