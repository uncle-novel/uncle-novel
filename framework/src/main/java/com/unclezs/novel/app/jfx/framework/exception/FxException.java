package com.unclezs.novel.app.jfx.framework.exception;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 15:15
 */
public class FxException extends RuntimeException {
    public FxException() {
    }

    public FxException(String message) {
        super(message);
    }

    public FxException(String message, Throwable cause) {
        super(message, cause);
    }

    public FxException(Throwable cause) {
        super(cause);
    }

    public FxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
