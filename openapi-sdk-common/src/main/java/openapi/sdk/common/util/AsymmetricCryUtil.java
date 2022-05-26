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
 * @date 2022/5/26 19:51
 */
@Slf4j
public class AsymmetricCryUtil {

    /**
     * 生成SM2的公私密钥
     */
    public static void generateSM2Keys() {
        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        byte[] privateKey = pair.getPrivate().getEncoded();
        byte[] publicKey = pair.getPublic().getEncoded();
        log.info("SM2私钥：\n" + Base64Util.bytesToBase64(privateKey));
        log.info("SM2公钥：\n" + Base64Util.bytesToBase64(publicKey));

    }

    /**
     * 生成RSA的公私密钥
     */
    public static void generateRSAKeys() {
        //生成公私钥对
        KeyPair pair = SecureUtil.generateKeyPair("RSA");
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
        log.info("RSA私钥：\n" + Base64Util.bytesToBase64(privateKey.getEncoded()));
        log.info("RSA公钥：\n" + Base64Util.bytesToBase64(publicKey.getEncoded()));
    }
}
