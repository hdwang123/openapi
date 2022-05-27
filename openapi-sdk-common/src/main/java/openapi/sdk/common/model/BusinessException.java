package openapi.sdk.common.model;

/**
 * 业务异常
 *
 * @author wanghuidong
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String errorMsg) {
        super(errorMsg);
    }

    public BusinessException(String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
    }
}
