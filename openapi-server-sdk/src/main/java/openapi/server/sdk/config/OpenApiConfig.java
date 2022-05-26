package openapi.server.sdk.config;


import openapi.sdk.common.model.AsymmetricCryEnum;

/**
 * 开放api配置类接口，由引入者去实现
 *
 * @author wanghuidong
 * @date 2022/5/26 16:53
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
}
