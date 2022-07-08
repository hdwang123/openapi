package openapi.sdk.common.constant;

/**
 * 常量
 *
 * @author wanghuidong
 */
public class Constant {

    /**
     * openapi接口路径
     */
    public static final String OPENAPI_PATH = "/openapi/call";

    /**
     * openapi接口文档数据接口
     */
    public static final String DOC_PATH = "/openapi/doc";

    /**
     * 最大日志长度,默认5K字符,超出将被截断，截取长度由OVER_MAX_LOG_KEEP_LENGTH决定
     */
    public static final int MAX_LOG_LENGTH = 5_000;

    /**
     * 超出最大日志长度保留的长度
     */
    public static final int OVER_MAX_LOG_KEEP_LENGTH = 100;
}
