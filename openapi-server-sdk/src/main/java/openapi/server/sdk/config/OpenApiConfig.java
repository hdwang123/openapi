package openapi.server.sdk.config;


import openapi.sdk.common.model.AsymmetricCryEnum;
import openapi.sdk.common.model.SymmetricCryEnum;

/**
 * 开放api配置类接口，由引入者去实现
 *
 * @author wanghuidong
 */
public interface OpenApiConfig {

    /**
     * 获取采用的非对称加密算法(rsa,sm2)
     *
     * @return 加密算法
     * @see AsymmetricCryEnum
     */
    AsymmetricCryEnum getAsymmetricCry();

    /**
     * 获取调用者的公钥
     *
     * @param callerId 调用者ID
     * @return 调用者公钥
     */
    String getCallerPublicKey(String callerId);

    /**
     * 获取本系统的私钥
     *
     * @return 本系统私钥
     */
    String getSelfPrivateKey();

    /**
     * 获取返回值是否加密
     *
     * @return 返回值是否加密
     */
    boolean retEncrypt();

    /**
     * 是否启用对称加密
     * 注：内容采用对称加密，对称加密密钥采用非对称加密
     *
     * @return 是否启用对称加密
     */
    default boolean enableSymmetricCry() {
        //默认不启用
        return false;
    }

    /**
     * 获取对称加密算法(AES或SM4)
     *
     * @return 对称加密算法
     */
    default SymmetricCryEnum getSymmetricCryEnum() {
        return SymmetricCryEnum.AES;
    }
}
