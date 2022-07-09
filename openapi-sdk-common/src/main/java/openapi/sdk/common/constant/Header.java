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
        String UUID = "uuid";
        String CALLER_ID = "callerId";
        String API = "api";
        String METHOD = "method";
        String SIGN = "sign";
        String SYMMETRIC_CRY_KEY = "symmetricCryKey";
        String MULTI_PARAM = "multiParam";
    }

    /**
     * 响应头名称常量
     */
    interface Response {
        String UUID = "uuid";
        String CODE = "code";
        String MESSAGE = "message";
        String SYMMETRIC_CRY_KEY = "symmetricCryKey";
    }
}
