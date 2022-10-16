package xyz.cofe.cbuffer.page;

public class PageDataError extends Error {
    public PageDataError(String message) {
        super(message);
    }

    public PageDataError(String message, Throwable cause) {
        super(message, cause);
    }

    public PageDataError(Throwable cause) {
        super(cause);
    }

    protected PageDataError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
