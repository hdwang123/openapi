package openapi.sdk.common.handler;

import openapi.sdk.common.enums.SymmetricCryEnum;
import openapi.sdk.common.handler.symmetric.AESSymmetricCryHandler;
import openapi.sdk.common.handler.symmetric.SM4SymmetricCryHandler;

import java.util.EnumMap;
import java.util.Map;

/**
 * 对称加密处理器
 *
 * @author wanghuidong
 * 时间： 2022/6/4 13:17
 */
public interface SymmetricCryHandler {

    /**
     * 定义所有的对称加密处理器
     */
    Map<SymmetricCryEnum, SymmetricCryHandler> handlerMap = new EnumMap<SymmetricCryEnum, SymmetricCryHandler>(SymmetricCryEnum.class) {{
        put(SymmetricCryEnum.AES, new AESSymmetricCryHandler());
        put(SymmetricCryEnum.SM4, new SM4SymmetricCryHandler());
    }};

    /**
     * 对称加密
     *
     * @param content  内容（普通字符串）
     * @param keyBytes 对称密钥
     * @return 加密后的内容（Base64字符串）
     */
    String cry(String content, byte[] keyBytes);

    /**
     * 对称加密
     *
     * @param content  内容（字节数组）
     * @param keyBytes 对称密钥
     * @return 密文（字节数组）
     */
    byte[] cry(byte[] content, byte[] keyBytes);

    /**
     * 对称解密
     *
     * @param content  内容 (Hex（16进制）或Base64表示的字符串)
     * @param keyBytes 对称密钥
     * @return 解密后的内容 (普通字符串)
     */
    String deCry(String content, byte[] keyBytes);

    /**
     * 对称解密
     *
     * @param content  内容密文（字节数组）
     * @param keyBytes 对称密钥
     * @return 内容明文（字节数组）
     */
    byte[] deCry(byte[] content, byte[] keyBytes);
}
