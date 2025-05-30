package openapi.sdk.common.handler.symmetric;

import cn.hutool.crypto.symmetric.SM4;
import openapi.sdk.common.enums.SymmetricCryAlgo;
import openapi.sdk.common.handler.SymmetricCryHandler;
import openapi.sdk.common.util.SymmetricCryUtil;

/**
 * SM4对称加密处理器
 *
 * @author wanghuidong
 * 时间： 2022/6/4 13:57
 */
public class SM4SymmetricCryHandler implements SymmetricCryHandler {
    @Override
    public byte[] generateKey() {
        return SymmetricCryUtil.getKey(SymmetricCryAlgo.SM4);
    }

    @Override
    public String cry(String content, byte[] keyBytes) {
        SM4 sm4 = new SM4(keyBytes);
        return sm4.encryptBase64(content);
    }

    @Override
    public byte[] cry(byte[] content, byte[] keyBytes) {
        SM4 sm4 = new SM4(keyBytes);
        return sm4.encrypt(content);
    }

    @Override
    public String deCry(String content, byte[] keyBytes) {
        SM4 sm4 = new SM4(keyBytes);
        return sm4.decryptStr(content);
    }

    @Override
    public byte[] deCry(byte[] content, byte[] keyBytes) {
        SM4 sm4 = new SM4(keyBytes);
        return sm4.decrypt(content);
    }
}
