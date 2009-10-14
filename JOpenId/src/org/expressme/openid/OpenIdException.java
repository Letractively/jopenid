package org.expressme.openid;

/**
 * Exception for any open id authentication.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class OpenIdException extends RuntimeException {

    public OpenIdException() {
        super();
    }

    public OpenIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenIdException(String message) {
        super(message);
    }

    public OpenIdException(Throwable cause) {
        super(cause);
    }

}
