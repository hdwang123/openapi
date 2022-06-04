package openapi.sdk.common.util;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 非对称加密工具类
 *
 * @author wanghuidong
 */
@Slf4j
public class AsymmetricCryUtil {

    /**
     * 生成SM2的公私密钥
     *
     * @return 密钥对
     */
    public static openapi.sdk.common.model.KeyPair generateSM2Keys() {
        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        String privateKey = Base64Util.bytesToBase64(pair.getPrivate().getEncoded());
        String publicKey = Base64Util.bytesToBase64(pair.getPublic().getEncoded());
        return new openapi.sdk.common.model.KeyPair(privateKey, publicKey);
    }

    /**
     * 生成RSA的公私密钥
     *
     * @return 密钥对
     */
    public static openapi.sdk.common.model.KeyPair generateRSAKeys() {
        //生成公私钥对
        KeyPair pair = SecureUtil.generateKeyPair("RSA");
        String privateKey = Base64Util.bytesToBase64(pair.getPrivate().getEncoded());
        String publicKey = Base64Util.bytesToBase64(pair.getPublic().getEncoded());
        return new openapi.sdk.common.model.KeyPair(privateKey, publicKey);
    }
}
