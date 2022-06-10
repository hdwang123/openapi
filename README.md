# openapi

<a href = "https://github.com/hdwang123/openapi/blob/main/README-en.md">
ENGLISH README
</a>

## 致力于提供一个能够快速搭建开放api的sdk

## 背景

对外服务的接口为了安全起见，往往需要进行相应的安全处理：数据加密传输和身份认证。 数据加密传输有对称加密和非对称加密两种，为了更加安全起见采用非对称加密比较好些，身份认证则采用数字签名可以实现。
开发此sdk就是为了能够快速地实现项目中api的安全开放。

## 基于框架

spring-boot

## 主要依赖包

cn.hutool.hutool-all

## 功能

1.负责对外开放接口（<font size=1>基于HTTP对外提供服务</font>）  
2.实现接口的参数与返回值的加解密（<font size=1>使用非对称加密：RSA/SM2，或对称加密：AES/SM4</font>）  
3.实现接口的验签（<font size=1>服务端会校验客户端的签名，确保调用者身份以及数据不被篡改</font>）

## 程序流程图

<img src="https://github.com/hdwang123/openapi/blob/main/doc/openapi.png" />   
<img src="https://github.com/hdwang123/openapi/blob/main/doc/openapi2.png" />   

## 使用方法

### 服务端

#### 1.引入openapi-server-sdk

````
<dependency>
    <groupId>io.github.hdwang123</groupId>
    <artifactId>openapi-server-sdk</artifactId>
    <version>1.2.3</version>
</dependency>
````

#### 2.实现OpenApiConfig接口进行配置

<font size=1 color=#ff6600>注：默认没有任何配置实现类，启动项目会报错，必须手动配置一个OpenApiConfig实现类
</font>

````
@Component
public class OpenApiConfigImpl implements OpenApiConfig {

    @Value("${keys.local.rsa.privateKey}")
    private String privateKey;

    @Value("${keys.local.rsa.publicKey}")
    private String publicKey;

    @Value("${keys.remote.rsa.publicKey}")
    private String callerPublicKey;

    @Override
    public AsymmetricCryEnum getAsymmetricCry() {
        return AsymmetricCryEnum.RSA;
    }

    @Override
    public String getCallerPublicKey(String callerId) {
        //TODO 根据调用者ID查找调用者的公钥（可以将所有调用者的公钥存到数据库中）
        return callerPublicKey;
    }

    @Override
    public String getSelfPrivateKey() {
        return privateKey;
    }

    @Override
    public boolean retEncrypt() {
        return true;
    }
}
````

#### 3.自定义开放API

<font size=1  color=#ff6600>注：被@OpenApi标识的类必须处于spring包的扫描路径下，方可注入容器中
</font>

````
@Slf4j
@OpenApi("userApi")
public class UserApi {

    @OpenApiMethod("getUserById")
    public User getUserById(Long id) {
        log.info("getUserById：id=" + id);
        User user = new User();
        user.setId(1L);
        user.setName("张三");
        return user;
    }
}
````

### 客户端

#### 1.引入openapi-client-sdk

````
<dependency>
    <groupId>io.github.hdwang123</groupId>
    <artifactId>openapi-client-sdk</artifactId>
    <version>1.2.3</version>
</dependency>
````

#### 2.调用openapi

##### 方式一

直接调用OpenApiClient

````
@Slf4j
@Component
public class UserApiClient {

    @Value("${keys.local.rsa.privateKey}")
    private String privateKey;

    @Value("${keys.local.rsa.publicKey}")
    private String publicKey;

    @Value("${keys.remote.rsa.publicKey}")
    private String remotePublicKey;

    String baseUrl = "http://localhost:8080";

    /**
     * 定义OpenApiClient
     */
    OpenApiClient apiClient = null;

    @PostConstruct
    public void init() {
        apiClient = new OpenApiClientBuilder(baseUrl, privateKey, remotePublicKey, "001", "userApi")
                .asymmetricCry(AsymmetricCryEnum.RSA)
                .retDecrypt(true)
                .enableSymmetricCry(true)
                .symmetricCry(SymmetricCryEnum.AES)
                .build();
    }


    public void getUserById() {
        OutParams outParams = apiClient.callOpenApi("getUserById", 10001);
        log.info("返回值：" + outParams);
    }
}
````

##### 方式二

使用注解@OpenApiRef定义服务引用，在需要的地方注入即可  
1.定义配置

````
openapi:
  client:
    config:
      openApiRefPath: openapi.example.client.openapiclient
      baseUrl: http://localhost:8080
      selfPrivateKey: ${keys.local.rsa.privateKey}
      remotePublicKey: ${keys.remote.rsa.publicKey}
      asymmetricCryEnum: RSA
      retDecrypt: true
      enableSymmetricCry: true
      symmetricCryEnum: AES
      callerId: "001"
````

2.定义服务引用

````
@OpenApiRef(value = "userApi")
public interface UserApiClient {

    @OpenApiMethod("getUserById")
    User getUserById(Long id);
}
````

3.注入服务引用调用远程openapi服务

````
@Component
public class UserApiTest2 {

    @Autowired
    UserApiClient userApiClient;

    public void getUserById() {
        User user = userApiClient.getUserById(10001L);
        log.info("返回值：" + user);
    }
}    
````

## 版本记录

### v1.0.0

初版，支持非对称加解密(RSA或SM2)和接口验签功能

### v1.1.0

新增对称加密模式(AES或SM4)，即：内容采用对称加密以提高加解密速度，对称加密的密钥用非对称加密后传输

### v1.1.1

1.签名优化，将请求流水号加到签名内容中，保证无参方法的调用也是经过验签的  
2.方法新增数组类型参数的支持  
3.注解OpenApiMethod新增属性，支持方法级别的配置

### v1.1.2

1.方法多参数支持  
2.修复类型转换错误

### v1.1.3

1.OpenApiClient构造优化，新增OpenApiClientBuilder类  
2.OpenApiClient调用优化，新增多个callOpenApi重载方法

### v1.1.4

完善日志打印：SDK日志加上uuid,方便定位请求

### v1.1.5

完善日志打印：SDK打印初始配置信息

### v1.2.0

openapi-client-sdk新增服务引用方式调用openapi

### v1.2.1

加解密方法重构：改成加解密处理器，方便以后算法扩展

### v1.2.2

1.优化类型转换代码  
2.增加HTTP超时时间设置

### v1.2.3

1.修复网址拼接bug  
2.代码优化  