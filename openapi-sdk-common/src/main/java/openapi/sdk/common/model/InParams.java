package openapi.sdk.common.model;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import openapi.sdk.common.constant.Constant;
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
        if (this.getBody() != null && this.getBody().length() > Constant.MAX_LOG_LENGTH) {
            //数据超过指定长度则截断，防止打印日志卡死
            InParams inParams = new InParams();
            BeanUtil.copyProperties(this, inParams);
            inParams.setBody(TruncateUtil.truncate(inParams.getBody()));
            return JSONUtil.toJsonStr(inParams);
        }
        return JSONUtil.toJsonStr(this);
    }

}
