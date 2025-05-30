package openapi.sdk.common.util;

import cn.hutool.crypto.KeyUtil;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * 对称加密工具类
 *
 * @author wanghuidong
 */
public class SymmetricCryUtil {

    /**
     * 获取一个对称加密密钥
     *
     * @param symmetricCryAlgo 对称加密算法
     * @return 对称加密密钥
     */
    public static byte[] getKey(String symmetricCryAlgo) {
        //随机数作为种子
        String key = UUID.randomUUID().toString();

        //SM4密钥长度也为128位(可以与AES采用项目算法产生密钥), SecureRandom当种子一样时产生一样的序列
        SecretKey secretKey = KeyUtil.generateKey(symmetricCryAlgo, 128, new SecureRandom(key.getBytes()));
        return secretKey.getEncoded();
    }
}
