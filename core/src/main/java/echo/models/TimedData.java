package echo.models;

import java.time.LocalDateTime;

public final class TimedData<T> {
    private final T data;
    private final LocalDateTime stamp;

    public TimedData(T data) {
        this.data = data;
        this.stamp = LocalDateTime.now();
    }

    public T getData() {
        return data;
    }

    public LocalDateTime getStamp() {
        return stamp;
    }
}
