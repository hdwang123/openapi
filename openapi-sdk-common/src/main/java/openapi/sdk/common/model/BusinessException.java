package openapi.sdk.common.model;

/**
 * 业务异常
 *
 * @author wanghuidong
 * @date 2022/5/18 14:59
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String errorMsg) {
        super(errorMsg);
    }

    public BusinessException(String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
    }
}
