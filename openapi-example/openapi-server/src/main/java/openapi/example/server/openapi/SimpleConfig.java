package openapi.example.server.openapi;

import openapi.sdk.common.enums.AsymmetricCryAlgo;
import openapi.sdk.common.enums.SymmetricCryAlgo;
import openapi.server.sdk.config.OpenApiConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * openapi的配置信息
 *
 * @author wanghuidong
 */
@Component
public class SimpleConfig implements OpenApiConfig {

    @Value("${keys.local.sm2.privateKey}")
    private String privateKey;

    @Value("${keys.remote.sm2.publicKey}")
    private String callerPublicKey;

    @Override
    public String getAsymmetricCry() {
        //设置非对称加密算法
        return AsymmetricCryAlgo.SM2;
    }

    @Override
    public String getCallerPublicKey(String callerId) {
        //TODO 根据调用者ID查找调用者的公钥（可以将所有调用者的公钥存到数据库中）
        return callerPublicKey;
    }

    @Override
    public String getSelfPrivateKey() {
        //设置服务端私钥
        return privateKey;
    }

    @Override
    public boolean retEncrypt() {
        //设置返回值是否需要加密
        return true;
    }

    @Override
    public String getSymmetricCry() {
        return SymmetricCryAlgo.SM4;
    }
}
