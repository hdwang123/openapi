package openapi.sdk.common.model;

import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * openapi出参
 *
 * @author wanghuidong
 * @date 2022/5/26 14:56
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
     * 调用成功的结果
     *
     * @param data
     * @return
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
     * @param message
     * @return
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
