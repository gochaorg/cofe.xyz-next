package xyz.cofe.collection;

/**
 * Ошибка когда целевой объект proxy объекта не доустпен (null)
 */
public class TargetNotAvailable extends Error {
    /**
     * Конструктор
     */
    public TargetNotAvailable() {
        super("target not available");
    }

    /**
     * Конструктор
     * @param message сообщение
     */
    public TargetNotAvailable(String message) {
        super(message);
    }

    /**
     * Конструктор
     * @param message сообщение
     * @param cause причина
     */
    public TargetNotAvailable(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Конструктор
     * @param cause причина
     */
    public TargetNotAvailable(Throwable cause) {
        super(cause);
    }

    /**
     * Конструктор
     * @param message сообщение
     * @param cause причина
     * @param enableSuppression ...
     * @param writableStackTrace ...
     */
    public TargetNotAvailable(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
