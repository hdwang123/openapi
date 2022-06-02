package openapi.client.sdk.config;


import lombok.Data;
import openapi.sdk.common.model.AsymmetricCryEnum;
import openapi.sdk.common.model.SymmetricCryEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 开放api客户端配置类，由引入者添加配置
 *
 * 详细配置如下：
 * openapi:
 *   client:
 *     config:
 *       openApiRefPath: openapi.example.client.openapiclient
 *       baseUrl: http://localhost:8080
 *       selfPrivateKey: ${your selfPrivateKey}
 *       remotePublicKey: ${your remotePublicKey}
 *       asymmetricCryEnum: RSA
 *       retDecrypt: true
 *       enableSymmetricCry: true
 *       symmetricCryEnum: AES
 *       callerId: 001
 *
 * @author wanghuidong
 */
@Data
@Component
@ConfigurationProperties(prefix = "openapi.client.config")
public class OpenApiConfig {
    /**
     * 接口所在路径(包名)
     */
    private String openApiRefPath;
    /**
     * openapi基础路径,例如(http://localhost)
     */
    private String baseUrl;

    /**
     * 本系统私钥
     */
    private String selfPrivateKey;

    /**
     * 远程系统的公钥
     */
    private String remotePublicKey;

    /**
     * 非对称加密算法
     */
    private AsymmetricCryEnum asymmetricCryEnum = AsymmetricCryEnum.RSA;

    /**
     * 返回值是否需要解密
     */
    private boolean retDecrypt = true;

    /**
     * 是否启用对称加密(内容采用对称加密，对称加密密钥采用非对称加密)
     */
    private boolean enableSymmetricCry = true;

    /**
     * 对称加密算法
     */
    private SymmetricCryEnum symmetricCryEnum = SymmetricCryEnum.AES;

    /**
     * 调用者ID
     */
    private String callerId;
}
