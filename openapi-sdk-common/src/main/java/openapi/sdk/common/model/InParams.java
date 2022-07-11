package openapi.sdk.common.model;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import openapi.sdk.common.enums.DataType;
import openapi.sdk.common.util.TruncateUtil;

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
     * 请求体内容（传入内容明文）
     */
    private String body;

    /**
     * 请求体内容（字节数组形式，由sdk生成的内容密文也保存至此）
     */
    private byte[] bodyBytes;

    /**
     * 用于日志打印
     */
    private String bodyBytesStr;

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

    /**
     * 传输的数据类型
     */
    private DataType dataType;

    @Override
    public String toString() {
        InParams inParams = new InParams();
        BeanUtil.copyProperties(this, inParams);
        inParams.setBody(TruncateUtil.truncate(inParams.getBody()));
        inParams.setBodyBytesStr(TruncateUtil.truncate(inParams.getBodyBytes()));
        inParams.setBodyBytes(null);
        return JSONUtil.toJsonStr(inParams);
    }

}
