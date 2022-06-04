package openapi.sdk.common.handler.asymmetric;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;

/**
 * SM2非对称加密处理器
 *
 * @author wanghuidong
 * 时间： 2022/6/4 13:31
 */
public class SM2AsymmetricCryHandler implements AsymmetricCryHandler {
    @Override
    public String sign(String privateKey, String content) {
        SM2 sm2 = SmUtil.sm2(privateKey, null);
        return sm2.signHex(HexUtil.encodeHexStr(content));
    }

    @Override
    public boolean verifySign(String publicKey, String content, String sign) {
        SM2 sm2 = SmUtil.sm2(null, publicKey);
        return sm2.verifyHex(HexUtil.encodeHexStr(content), sign);
    }

    @Override
    public String cry(String publicKey, String content) {
        SM2 sm2 = SmUtil.sm2(null, publicKey);
        return sm2.encryptBcd(content, KeyType.PublicKey);
    }

    @Override
    public String deCry(String privateKey, String content) {
        SM2 sm2 = SmUtil.sm2(privateKey, null);
        return StrUtil.utf8Str(sm2.decryptFromBcd(content, KeyType.PrivateKey));
    }
}
