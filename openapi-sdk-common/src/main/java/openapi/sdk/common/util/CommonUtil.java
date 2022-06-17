package openapi.sdk.common.util;

import cn.hutool.core.util.StrUtil;
import openapi.sdk.common.exception.OpenApiException;
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

    /**
     * 拼接url地址
     *
     * @param baseUrl 基础路径
     * @param path    待拼接的路径
     * @return 完整的url地址
     */
    public static String completeUrl(String baseUrl, String path) {
        if (StrUtil.isBlank(baseUrl)) {
            throw new OpenApiException("URL基础路径不能为空");
        }
        String separator = "/";
        if (baseUrl.endsWith(separator)) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        if (StrUtil.isNotBlank(path)) {
            path = path.startsWith(separator) ? path.substring(1) : path;
            return baseUrl + separator + path;
        }
        return baseUrl;
    }
}
