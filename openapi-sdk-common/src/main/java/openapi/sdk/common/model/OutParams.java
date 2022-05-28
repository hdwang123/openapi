package openapi.sdk.common.model;

import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * openapi出参
 *
 * @author wanghuidong
 */
@Data
public class OutParams<T> {

    /**
     * 流水线ID
     */
    private String uuid;

    /**
     * 网关返回码
     */
    private Integer code;

    /**
     * 网关返回消息
     */
    private String message;

    /**
     * 目标接口返回值
     */
    private String data;

    /**
     * 对称加密Key(由sdk生成)
     */
    private String symmetricCryKey;

    /**
     * 调用成功的结果
     *
     * @param data 数据
     * @return 输出参数
     */
    public static OutParams success(String data) {
        OutParams outParams = new OutParams();
        outParams.code = 200;
        outParams.data = data;
        return outParams;
    }

    /**
     * 调用失败的结果
     *
     * @param message 错误消息
     * @return 输出参数
     */
    public static OutParams error(String message) {
        OutParams outParams = new OutParams();
        outParams.code = -1;
        outParams.message = message;
        return outParams;
    }

    /**
     * 判断openapi调用是否成功
     *
     * @param outParams 调用结果
     * @return 是否调用成功
     */
    public static boolean isSuccess(OutParams outParams) {
        return outParams != null && outParams.code == 200;
    }


    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }

}
