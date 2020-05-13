package xyz.cofe.text.tparse;

/**
 * Внешная API Функция вернула неожиданный результат
 */
public class MapResultError extends ImplementError {
    /**
     * Конструктор
     */
    public MapResultError(){
        super();
    }

    /**
     * Конструктор
     * @param message Сообщение о ошибке
     */
    public MapResultError( String message ){
        super(message);
    }

    /**
     * Конструктор
     * @param message Сообщение о ошибке
     * @param cause Причина
     */
    public MapResultError( String message, Throwable cause ){
        super(message, cause);
    }

    /**
     * Конструктор
     * @param cause Причина
     */
    public MapResultError( Throwable cause ){
        super(cause);
    }

    protected MapResultError( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ){
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
