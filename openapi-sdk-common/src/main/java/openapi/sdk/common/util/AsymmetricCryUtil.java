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
        byte[] privateKey = pair.getPrivate().getEncoded();
        byte[] publicKey = pair.getPublic().getEncoded();
        return new openapi.sdk.common.model.KeyPair(Base64Util.bytesToBase64(privateKey), Base64Util.bytesToBase64(publicKey));
    }

    /**
     * 生成RSA的公私密钥
     *
     * @return 密钥对
     */
    public static openapi.sdk.common.model.KeyPair generateRSAKeys() {
        //生成公私钥对
        KeyPair pair = SecureUtil.generateKeyPair("RSA");
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
        return new openapi.sdk.common.model.KeyPair(Base64Util.bytesToBase64(privateKey.getEncoded()), Base64Util.bytesToBase64(publicKey.getEncoded()));
    }
}
