package openapi.sdk.common.model;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * openapi入参
 *
 * @author wanghuidong
 * @date 2022/5/26 15:09
 */
@Data
public class InParams {

    /**
     * 流水线ID
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
     * 请求体内容（内容密文）
     */
    private String body;

    /**
     * 签名（根据内容密文生成）
     */
    private String sign;

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
