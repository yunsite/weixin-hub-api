package wxhub;

/**
 * Exception for http invocation.
 *
 * @author Tigerwang
 */
public class HttpException extends RuntimeException {
    protected int errorCode;
    
    /**
     * Construct exception with empty message
     */
    public HttpException() {
        super();
    }

    /**
     * Construct with error code.
     */
    public HttpException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }
    
    /**
     * Construct exception with error message
     *
     * @param  message      exception message
     */
    public HttpException(String message) {
        super(message);
    }

    /**
     * Construct exception with error code and message.
     */
    public HttpException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Construct exception with nested exception and message
     *
     * @param  message      exception message
     * @param  nest         nested exception
     */
    public HttpException(String message, Throwable nest) {
        super(message, nest);
    }
    
    /**
     * Construct exception with error code, message and nested exception.
     */
    public HttpException(int errorCode, String message, Throwable nest) {
        super(message, nest);
        this.errorCode = errorCode;
    }
    
    
    /**
     * Get error code.
     */
    public int getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}