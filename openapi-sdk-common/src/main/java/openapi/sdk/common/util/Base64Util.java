package openapi.sdk.common.util;

import cn.hutool.core.util.StrUtil;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author wanghuidong
 */
public class Base64Util {

    /**
     * 字节数组转Base64编码
     *
     * @param bytes 字节数组
     * @return Base64编码
     */
    public static String bytesToBase64(byte[] bytes) {
        byte[] encodedBytes = Base64.getEncoder().encode(bytes);
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Base64编码转字节数组
     *
     * @param base64Str Base64编码
     * @return 字节数组
     */
    public static byte[] base64ToBytes(String base64Str) {
        byte[] bytes = base64Str.getBytes(StandardCharsets.UTF_8);
        return Base64.getDecoder().decode(bytes);
    }

    /**
     * 将普通字符串转换为Base64字符串
     *
     * @param str 普通字符串
     * @return Base64字符串
     */
    public static String strToBase64(String str) {
        if (StrUtil.isEmpty(str)) {
            return StrUtil.EMPTY;
        }
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        return bytesToBase64(bytes);
    }

    /**
     * 将Base64字符串转换为普通字符串
     *
     * @param base64Str Base64字符串
     * @return 普通字符串
     */
    public static String base64ToStr(String base64Str) {
        if (StrUtil.isEmpty(base64Str)) {
            return StrUtil.EMPTY;
        }
        byte[] bytes = base64ToBytes(base64Str);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
