package cc.w0rm.ghost.common.http;

/**
 * @author : xuyang
 * @date : 2019-11-26 11:19
 */
public class OKHttpException extends RuntimeException {

    private static final long serialVersionUID = -7914753721686803153L;

    public OKHttpException(Throwable cause) {
        super(cause);
    }

    public OKHttpException(String message) {
        super(message);
    }

    public OKHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
