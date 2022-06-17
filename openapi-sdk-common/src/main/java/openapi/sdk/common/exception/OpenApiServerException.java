package openapi.sdk.common.exception;

/**
 * OpenApi服务端异常
 *
 * @author wanghuidong
 * 时间： 2022/6/17 21:11
 */
public class OpenApiServerException extends OpenApiException {
    public OpenApiServerException(String errorMsg) {
        super(errorMsg);
    }

    public OpenApiServerException(String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
    }
}
