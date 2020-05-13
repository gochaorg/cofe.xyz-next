package xyz.cofe.text.tparse;

/**
 * Ошибка реализации парсера,
 * т.е. когда нарушается api между xparse бибилотекой и клиентской стороной
 */
public class ImplementError extends Error {
    /**
     * Конструктор
     */
    public ImplementError(){
        super();
    }

    /**
     * Конструктор
     * @param message Сообщение о ошибке
     */
    public ImplementError( String message ){
        super(message);
    }

    /**
     * Конструктор
     * @param message Сообщение о ошибке
     * @param cause Причина
     */
    public ImplementError( String message, Throwable cause ){
        super(message, cause);
    }

    /**
     * Конструктор
     * @param cause Причина
     */
    public ImplementError( Throwable cause ){
        super(cause);
    }

    protected ImplementError( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ){
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
