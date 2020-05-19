package echo.mapper;

public class SimpleMapperException extends Exception {
    public SimpleMapperException(String message, Exception e) {
        super(message, e);
    }
}
