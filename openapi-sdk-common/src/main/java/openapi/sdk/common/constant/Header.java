package openapi.sdk.common.constant;

/**
 * HTTP头名称常量
 *
 * @author wanghuidong
 * 时间： 2022/7/9 11:48
 */
public interface Header {

    /**
     * 请求头名称常量
     */
    interface Request {
        String UUID = "openapi-uuid";
        String CALLER_ID = "openapi-callerId";
        String API = "openapi-api";
        String METHOD = "openapi-method";
        String SIGN = "openapi-sign";
        String SYMMETRIC_CRY_KEY = "openapi-symmetricCryKey";
        String MULTI_PARAM = "openapi-multiParam";
        String DATA_TYPE = "openapi-dataType";
    }

    /**
     * 响应头名称常量
     */
    interface Response {
        String UUID = "openapi-uuid";
        String CODE = "openapi-code";
        String MESSAGE = "openapi-message";
        String SYMMETRIC_CRY_KEY = "openapi-symmetricCryKey";
        String DATA_TYPE = "openapi-dataType";
    }
}
