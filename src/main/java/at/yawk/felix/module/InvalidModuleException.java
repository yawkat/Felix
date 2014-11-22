package at.yawk.felix.module;

/**
 * @author yawkat
 */
public class InvalidModuleException extends Error {
    public InvalidModuleException() {}

    public InvalidModuleException(String message) {
        super(message);
    }

    public InvalidModuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidModuleException(Throwable cause) {
        super(cause);
    }
}
