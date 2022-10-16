package xyz.cofe.cbuffer.page;

public class PageError extends Error {
    public PageError(String message) {
        super(message);
    }

    public PageError(String message, Throwable cause) {
        super(message, cause);
    }

    public PageError(Throwable cause) {
        super(cause);
    }

    protected PageError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
