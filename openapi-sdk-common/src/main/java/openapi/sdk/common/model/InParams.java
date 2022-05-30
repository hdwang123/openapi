package openapi.sdk.common.model;

import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * openapi入参
 *
 * @author wanghuidong
 */
@Data
public class InParams {

    /**
     * 流水号
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
     * 请求体内容（传入内容明文，由sdk生成内容密文）
     */
    private String body;

    /**
     * 签名（由sdk根据内容密文生成）
     */
    private String sign;

    /**
     * 对称加密Key(由sdk生成)
     */
    private String symmetricCryKey;

    /**
     * 是否是多参方法（由sdk判断）
     */
    private boolean multiParam;

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
