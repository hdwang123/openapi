package openapi.server.sdk.model;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import openapi.sdk.common.enums.DataType;

/**
 * OpenApi请求对象，包装了一些请求参数
 *
 * @author wanghuidong
 * 时间： 2022/7/28 16:00
 */
@Data
public class OpenApiRequest {
    /**
     * 请求流水号
     */
    private String uuid;

    /**
     * 调用者ID
     */
    private String callerId;

    /**
     * 接口名
     */
    private String api;

    /**
     * 方法名
     */
    private String method;

    /**
     * 传输的数据类型
     */
    private DataType dataType;

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
