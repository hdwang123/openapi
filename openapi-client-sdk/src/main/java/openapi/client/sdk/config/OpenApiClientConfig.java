package openapi.client.sdk.config;


import lombok.Data;
import openapi.client.sdk.constant.ClientConstant;
import openapi.sdk.common.enums.AsymmetricCryAlgo;
import openapi.sdk.common.enums.CryModeEnum;
import openapi.sdk.common.enums.SymmetricCryAlgo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 开放api客户端配置类，由引入者添加配置
 * <p>
 * yml配置文件中添加openapi配置示例：
 * </p>
 * <blockquote><pre>
 * openapi:
 *   client:
 *     config:
 *       openApiRefPath: openapi.example.client.openapiclient
 *       baseUrl: http://localhost:8080
 *       selfPrivateKey: ${keys.local.rsa.privateKey}
 *       remotePublicKey: ${keys.remote.rsa.publicKey}
 *       asymmetricCryAlgo: RSA
 *       retDecrypt: true
 *       cryModeAlgo: SYMMETRIC_CRY
 *       symmetricCryAlgo: AES
 *       callerId: "001"
 *       httpConnectionTimeout: 3
 *       httpReadTimeout: 5
 *       enableCompress: true
 *       httpProxyHost: 127.0.0.1
 *       httpProxyPort: 8888
 * </pre></blockquote>
 *
 * @author wanghuidong
 */
@Data
@Component
@ConfigurationProperties(prefix = "openapi.client.config")
public class OpenApiClientConfig {
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
    private String asymmetricCryAlgo = AsymmetricCryAlgo.RSA;

    /**
     * 返回值是否需要解密
     */
    private boolean retDecrypt = true;

    /**
     * 加密模式
     */
    private CryModeEnum cryModeEnum = CryModeEnum.SYMMETRIC_CRY;

    /**
     * 对称加密算法
     */
    private String symmetricCryAlgo = SymmetricCryAlgo.AES;

    /**
     * 调用者ID
     */
    private String callerId;

    /**
     * HTTP建立连接超时时间（单位秒）
     */
    private int httpConnectionTimeout = ClientConstant.HTTP_CONNECTION_TIMEOUT;

    /**
     * HTTP数据传输超时时间（单位秒）
     */
    private int httpReadTimeout = ClientConstant.HTTP_READ_TIMEOUT;

    /**
     * HTTP请求代理域名
     */
    private String httpProxyHost;

    /**
     * HTTP请求代理端口
     */
    private Integer httpProxyPort;

    /**
     * HTTP传输的数据是否启用压缩
     */
    private boolean enableCompress = false;

    /**
     * 自定义非对称加密处理器（bean名称）
     */
    private String customAsymmetricCryHandler;

    /**
     * 自定义对称加密处理器（bean名称）
     */
    private String customSymmetricCryHandler;
}
