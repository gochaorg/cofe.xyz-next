package xyz.cofe.collection;

/**
 * Ошибка когда целевой объект proxy объекта не доустпен (null)
 */
public class TargetNotAvailable extends Error {
    public TargetNotAvailable() {
        super("target not available");
    }

    public TargetNotAvailable(String message) {
        super(message);
    }

    public TargetNotAvailable(String message, Throwable cause) {
        super(message, cause);
    }

    public TargetNotAvailable(Throwable cause) {
        super(cause);
    }

    public TargetNotAvailable(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
