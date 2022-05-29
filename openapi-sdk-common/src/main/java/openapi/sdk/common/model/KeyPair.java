package openapi.sdk.common.model;

import lombok.Data;

/**
 * 密钥对
 *
 * @author wanghuidong
 * 时间： 2022/5/29 10:17
 */
@Data
public class KeyPair {

    /**
     * 私钥（Base64字符串表示）
     */
    private String privateKey;

    /**
     * 公钥（Base64字符串表示）
     */
    private String publicKey;

    /**
     * 构造密钥对
     */
    public KeyPair() {
    }

    /**
     * 构造密钥对
     *
     * @param privateKey 私钥
     * @param publicKey  公钥
     */
    public KeyPair(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }
}
