package openapi.sdk.common.util;

import cn.hutool.core.util.StrUtil;
import openapi.sdk.common.model.InParams;

/**
 * 通用工具类
 *
 * @author wanghuidong
 * 时间： 2022/5/29 10:16
 */
public class CommonUtil {

    /**
     * 获取待签名的内容
     *
     * @param inParams 入参
     * @return 签名的内容
     */
    public static String getSignContent(InParams inParams) {
        //使用数据+uuid作为签名的内容，保证无参函数调用也能经过签名的验证
        String body = StrUtil.isBlank(inParams.getBody()) ? StrUtil.EMPTY : inParams.getBody();
        return body + inParams.getUuid();
    }
}
