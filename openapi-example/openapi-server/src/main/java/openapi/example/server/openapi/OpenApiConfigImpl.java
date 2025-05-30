package openapi.example.server.openapi;

import openapi.sdk.common.enums.AsymmetricCryAlgo;
import openapi.sdk.common.enums.CryModeEnum;
import openapi.sdk.common.enums.SymmetricCryAlgo;
import openapi.sdk.common.handler.AsymmetricCryHandler;
import openapi.sdk.common.handler.SymmetricCryHandler;
import openapi.sdk.common.handler.asymmetric.SM2AsymmetricCryHandler;
import openapi.sdk.common.handler.symmetric.SM4SymmetricCryHandler;
import openapi.server.sdk.config.OpenApiConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * openapi的配置信息
 *
 * @author wanghuidong
 */
@Component
public class OpenApiConfigImpl implements OpenApiConfig {

    @Value("${keys.local.sm2.privateKey}")
    private String privateKey;

    @Value("${keys.remote.sm2.publicKey}")
    private String callerPublicKey;

    @Override
    public String getAsymmetricCry() {
        //设置非对称加密算法
        return AsymmetricCryAlgo.CUSTOM;
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
    public CryModeEnum getCryMode() {
        //设置加密模式
        return CryModeEnum.SYMMETRIC_CRY;
    }

    @Override
    public String getSymmetricCry() {
        //设置对称加密算法
        return SymmetricCryAlgo.CUSTOM;
    }

    @Override
    public boolean enableDoc() {
        //是否启用接口文档功能
        return true;
    }

    @Override
    public boolean enableCompress() {
        //HTTP传输内容不启用压缩
        return false;
    }

    @Override
    public AsymmetricCryHandler customAsymmetricCryHandler() {
        // 自定义加密算法
        return new SM2AsymmetricCryHandler();
    }

    @Override
    public SymmetricCryHandler customSymmetricCryHandler() {
        // 自定义加密算法
        return new SM4SymmetricCryHandler();
    }
}
