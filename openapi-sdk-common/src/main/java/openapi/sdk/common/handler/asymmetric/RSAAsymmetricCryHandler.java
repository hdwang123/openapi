package openapi.sdk.common.handler.asymmetric;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import openapi.sdk.common.util.Base64Util;

import java.nio.charset.StandardCharsets;

/**
 * RSA非对称加密处理器
 *
 * @author wanghuidong
 * 时间： 2022/6/4 13:30
 */
public class RSAAsymmetricCryHandler implements AsymmetricCryHandler {
    @Override
    public String sign(String privateKey, String content) {
        Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, privateKey, null);
        //签名
        byte[] data = content.getBytes(StandardCharsets.UTF_8);
        byte[] signed = sign.sign(data);
        return Base64Util.bytesToBase64(signed);
    }

    @Override
    public boolean verifySign(String publicKey, String content, String sign) {
        Sign signObj = SecureUtil.sign(SignAlgorithm.SHA256withRSA, null, publicKey);
        return signObj.verify(content.getBytes(StandardCharsets.UTF_8), Base64Util.base64ToBytes(sign));
    }

    @Override
    public String cry(String publicKey, String content) {
        RSA rsa = new RSA(null, publicKey);
        byte[] encrypt = rsa.encrypt(StrUtil.bytes(content, CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
        return Base64Util.bytesToBase64(encrypt);
    }

    @Override
    public String deCry(String privateKey, String content) {
        RSA rsa = new RSA(privateKey, null);
        byte[] dataBytes = Base64Util.base64ToBytes(content);
        byte[] decrypt = rsa.decrypt(dataBytes, KeyType.PrivateKey);
        return new String(decrypt, StandardCharsets.UTF_8);
    }
}
