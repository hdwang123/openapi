# openapi

## 致力于提供一个能够快速搭建开放api的sdk

## 背景

对外服务的接口为了安全起见，往往需要进行相应的安全处理：数据加密传输和身份认证。 数据加密传输有对称加密和非对称加密两种，为了更加安全起见采用非对称加密比较好些，身份认证则采用数字签名可以实现。
开发此sdk就是为了能够快速地实现项目中api的安全开放。

## 基于框架

spring-boot

## 主要依赖包

cn.hutool.hutool-all

## 功能

1.负责对外开放接口（基于HTTP对外提供服务）  
2.实现接口的参数与返回值的加解密（使用RSA或国密SM2实现非对称加解密）  
3.实现接口的验签（服务端会校验客户端的签名，确保调用者身份以及数据不被篡改）

## 注意事项

目前OpenApiMethod仅支持单参数

## 使用方法

### 服务端

1.引入openapi-server-sdk

````
<dependency>
    <groupId>io.github.hdwang123</groupId>
    <artifactId>openapi-server-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
````

2.实现OpenApiConfig接口进行配置

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

3.自定义开放API

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

  @OpenApiMethod("saveUser")
  public Boolean saveUser(User user) {
      log.info("saveUser:" + JSONUtil.toJsonStr(user));
      return true;
  }

  @OpenApiMethod("listUsers")
  public List<User> listUsers(List<Long> ids) {
      log.info("listUsers: ids=" + ids);
      List<User> users = new ArrayList<>();
      users.add(new User(2L, "李四"));
      users.add(new User(3L, "王五"));
      return users;
  }

}
````

### 客户端

1.引入openapi-client-sdk

````
<dependency>
    <groupId>io.github.hdwang123</groupId>
    <artifactId>openapi-client-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
````

2.调用openapi

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

    public void getUserById() {
        try {
            String baseUrl = "http://localhost:8080";
            OpenApiClient apiClient = new OpenApiClient(baseUrl, privateKey, remotePublicKey, AsymmetricCryEnum.RSA, true);
            InParams inParams = new InParams();
            inParams.setUuid(UUID.randomUUID().toString());
            inParams.setCallerId("001");
            inParams.setApi("userApi");
            inParams.setMethod("getUserById");
            inParams.setBody("10001");
            log.info("入参：" + inParams);
            OutParams outParams = apiClient.callOpenApi(inParams);
            log.info("返回值：" + outParams);
        } catch (Exception ex) {
            log.error("异常", ex);
        }
    }

    public void saveUser() {
        try {
            String baseUrl = "http://localhost:8080";
            OpenApiClient apiClient = new OpenApiClient(baseUrl, privateKey, remotePublicKey, AsymmetricCryEnum.RSA, true);
            InParams inParams = new InParams();
            inParams.setUuid(UUID.randomUUID().toString());
            inParams.setCallerId("001");
            inParams.setApi("userApi");
            inParams.setMethod("saveUser");

            User user = new User();
            user.setId(1L);
            user.setName("张三");
            inParams.setBody(JSONUtil.toJsonStr(user));
            log.info("入参：" + inParams);
            OutParams outParams = apiClient.callOpenApi(inParams);
            log.info("返回值：" + outParams);
        } catch (Exception ex) {
            log.error("异常", ex);
        }
    }

    public void listUsers() {
        try {
            String baseUrl = "http://localhost:8080";
            OpenApiClient apiClient = new OpenApiClient(baseUrl, privateKey, remotePublicKey, AsymmetricCryEnum.RSA, true);
            InParams inParams = new InParams();
            inParams.setUuid(UUID.randomUUID().toString());
            inParams.setCallerId("001");
            inParams.setApi("userApi");
            inParams.setMethod("listUsers");

            List<Long> ids = new ArrayList<>();
            ids.add(2L);
            ids.add(3L);
            inParams.setBody(JSONUtil.toJsonStr(ids));
            log.info("入参：" + inParams);
            OutParams outParams = apiClient.callOpenApi(inParams);
            log.info("返回值：" + outParams);
        } catch (Exception ex) {
            log.error("异常", ex);
        }
    }

}
````

## 运行效果

### 客户端

````
2022-05-27 09:50:03 [main] DEBUG o.c.s.OpenApiClient - [doCall,119] - 调用openapi入参:{"method":"getUserById","sign":"EIp6piiN367IHsh/HLTGiJMfXix1P/3O9KB8K2ZYTl0GLXFENTNKSF/U8tEPqV4FTBdE5nFZMCcKSDVGwjBjUcudOgJlgk5EwsLv8rfdWn5DDr4UyowHY5MFaOVtskGmdmVNOUAyO+NtTZmtAkw6MJoPbY5UiJfDrWsWKyeUlN8=","body":"aNMNlmvV3O6pfdDyi09Wagfbd7oKlfQUNIJTtr+t9u3q+RzPHmO/8L/jfRGQnHb6hz4onps6+0cGexkshPjuZzNTnMFGBxtnb4hzXoxCSR3UHH/6FIt/Ha7k50D3Js2pjpuqzlkLKGmcfee2dy+h9zygniTGQ1gqU/t6+vB+O6o=","uuid":"491266bc-8ce7-4276-a017-f3dca0cda2f1","callerId":"001","api":"userApi"}
2022-05-27 09:50:04 [main] DEBUG o.c.s.OpenApiClient - [doCall,121] - 调用openapi返回值：{"uuid":"491266bc-8ce7-4276-a017-f3dca0cda2f1","code":200,"message":null,"data":"H97MQoLWy4EVtLfYeYxgko65+Xvy98CE+34IaqQuv7Z5l4pbAnCC+ZmB1tlIPYWSo6jxsuC0p91Mw6yHPJaqyo+yGUeB1dGCPdvItkoQtq6n+sMieAihf/EpcMnwpBnA2AAr+rrHkYaaXI8/A7NFTdsTP5SKmXrLl/VoJQbRWKk="}
2022-05-27 09:50:04 [main] DEBUG o.c.s.OpenApiClient - [doCall,127] - 调用openapi成功
2022-05-27 09:50:04 [main] DEBUG o.c.s.OpenApiClient - [doCall,119] - 调用openapi入参:{"method":"saveUser","sign":"bEpvLXMPOLm67FSas/YMO0S1z4SpbJEqtGH1HEtdODNOLlysfbnLoZCuO/PqiddXT+PPGKceBlY3JoDS13rgMJijv0Iv+3hwzYXF20GkM323uMWlqhfdZcarsdOKZ+kcIuxpN6CAddCcALAuI8h1claYmIYowd2vTrw/v3jTaJU=","body":"owdd99e5QMZVXntBLB+hbiGKJ+KUjSDsAN4KnA3+yyJZLOqaW36R+uoK9DMS0xtrapnWmM7HsbBiyZClnuNSFkY5Z0TzMjSUtpKs95u2lk6L0Eq6gmr3ATMlnDxGBki7aUE7kyTDIGrZxm0701yUtw6k+GJnNl2kL3jcZvI25T0=","uuid":"90622a6c-9092-49ad-8692-c6802f2e6070","callerId":"001","api":"userApi"}
2022-05-27 09:50:04 [main] DEBUG o.c.s.OpenApiClient - [doCall,121] - 调用openapi返回值：{"uuid":"90622a6c-9092-49ad-8692-c6802f2e6070","code":200,"message":null,"data":"cYuRjO+cRw86YyAIARVNaKftEzvTpZbWoYmhZPUkC2kZJ1LvoHJrmwXsywogQGTTIF6OaM3/2LlzuL+DGKYYOv+HR7Eh+4iqF+rU5LKL4HTMBpuwpqpVV+AXbLV+sX+pU8DKg478vprNjd3ogLBgzWbGXCrAcK2+waEjKkCzC2A="}
2022-05-27 09:50:04 [main] DEBUG o.c.s.OpenApiClient - [doCall,127] - 调用openapi成功
2022-05-27 09:50:04 [main] DEBUG o.c.s.OpenApiClient - [doCall,119] - 调用openapi入参:{"method":"listUsers","sign":"e25JgVqepoV7MxtrSEq2kTlNgdJigiBG5193PSa2FXAHhv/7+Wpa2yO09J/+a1mfWaG8wvNFfarpJfuRt32d0vtjn3LEsFbUsjTFSZjEVCKratfDoWo324Ma0Dsshcb8lNvqZZqfewS1Nilc5o1FRQ/zN/Bz7LXfccIpcxgqihI=","body":"L1PpTcSeXjQ+NK92BJ7g8GbqiwRm1zDMWMifLXkFd3otiulL80owym/JSoGXaagAf4K2tGUXGklyBi9JwQuACiTI5RFH1ChZtxRbkX+I1FqdL1tDZIvUYOgGAKhCTTGxd5pROJ7xLDiJj6qq0ZX1dU7vGVgS976MaFi16IYEkK4=","uuid":"e67fe69b-e1fe-4ee8-b885-d801ea0bdf9d","callerId":"001","api":"userApi"}
2022-05-27 09:50:04 [main] DEBUG o.c.s.OpenApiClient - [doCall,121] - 调用openapi返回值：{"uuid":"e67fe69b-e1fe-4ee8-b885-d801ea0bdf9d","code":200,"message":null,"data":"lfkznguWD1lAhR2S81ZcfvUd0tWgvkwZvCGn3sGMlIcg78Nv0L+NVy2lky3Zd/vHlIxspcXk3+DQmkmCWUtAP8qM7D2j+MR1WFtuKiOYV9Ee683CtNqDUoI7rxB/E6pzLDbpXqhMKnhe94Gxyg7mNnjne+MX+p19QaVzHBl/ilY="}
2022-05-27 09:50:04 [main] DEBUG o.c.s.OpenApiClient - [doCall,127] - 调用openapi成功
````

### 服务端

````
022-05-27 09:50:03 [http-nio-8080-exec-1] DEBUG o.s.s.OpenApiGateway - [callMethod,109] - 接收到请求：{"method":"getUserById","sign":"EIp6piiN367IHsh/HLTGiJMfXix1P/3O9KB8K2ZYTl0GLXFENTNKSF/U8tEPqV4FTBdE5nFZMCcKSDVGwjBjUcudOgJlgk5EwsLv8rfdWn5DDr4UyowHY5MFaOVtskGmdmVNOUAyO+NtTZmtAkw6MJoPbY5UiJfDrWsWKyeUlN8=","body":"aNMNlmvV3O6pfdDyi09Wagfbd7oKlfQUNIJTtr+t9u3q+RzPHmO/8L/jfRGQnHb6hz4onps6+0cGexkshPjuZzNTnMFGBxtnb4hzXoxCSR3UHH/6FIt/Ha7k50D3Js2pjpuqzlkLKGmcfee2dy+h9zygniTGQ1gqU/t6+vB+O6o=","uuid":"491266bc-8ce7-4276-a017-f3dca0cda2f1","callerId":"001","api":"userApi"}
2022-05-27 09:50:04 [http-nio-8080-exec-1] DEBUG o.s.s.OpenApiGateway - [callMethod,127] - 调用完毕：{"code":200,"data":"H97MQoLWy4EVtLfYeYxgko65+Xvy98CE+34IaqQuv7Z5l4pbAnCC+ZmB1tlIPYWSo6jxsuC0p91Mw6yHPJaqyo+yGUeB1dGCPdvItkoQtq6n+sMieAihf/EpcMnwpBnA2AAr+rrHkYaaXI8/A7NFTdsTP5SKmXrLl/VoJQbRWKk=","uuid":"491266bc-8ce7-4276-a017-f3dca0cda2f1"}
2022-05-27 09:50:04 [http-nio-8080-exec-2] DEBUG o.s.s.OpenApiGateway - [callMethod,109] - 接收到请求：{"method":"saveUser","sign":"bEpvLXMPOLm67FSas/YMO0S1z4SpbJEqtGH1HEtdODNOLlysfbnLoZCuO/PqiddXT+PPGKceBlY3JoDS13rgMJijv0Iv+3hwzYXF20GkM323uMWlqhfdZcarsdOKZ+kcIuxpN6CAddCcALAuI8h1claYmIYowd2vTrw/v3jTaJU=","body":"owdd99e5QMZVXntBLB+hbiGKJ+KUjSDsAN4KnA3+yyJZLOqaW36R+uoK9DMS0xtrapnWmM7HsbBiyZClnuNSFkY5Z0TzMjSUtpKs95u2lk6L0Eq6gmr3ATMlnDxGBki7aUE7kyTDIGrZxm0701yUtw6k+GJnNl2kL3jcZvI25T0=","uuid":"90622a6c-9092-49ad-8692-c6802f2e6070","callerId":"001","api":"userApi"}
2022-05-27 09:50:04 [http-nio-8080-exec-2] DEBUG o.s.s.OpenApiGateway - [callMethod,127] - 调用完毕：{"code":200,"data":"cYuRjO+cRw86YyAIARVNaKftEzvTpZbWoYmhZPUkC2kZJ1LvoHJrmwXsywogQGTTIF6OaM3/2LlzuL+DGKYYOv+HR7Eh+4iqF+rU5LKL4HTMBpuwpqpVV+AXbLV+sX+pU8DKg478vprNjd3ogLBgzWbGXCrAcK2+waEjKkCzC2A=","uuid":"90622a6c-9092-49ad-8692-c6802f2e6070"}
2022-05-27 09:50:04 [http-nio-8080-exec-3] DEBUG o.s.s.OpenApiGateway - [callMethod,109] - 接收到请求：{"method":"listUsers","sign":"e25JgVqepoV7MxtrSEq2kTlNgdJigiBG5193PSa2FXAHhv/7+Wpa2yO09J/+a1mfWaG8wvNFfarpJfuRt32d0vtjn3LEsFbUsjTFSZjEVCKratfDoWo324Ma0Dsshcb8lNvqZZqfewS1Nilc5o1FRQ/zN/Bz7LXfccIpcxgqihI=","body":"L1PpTcSeXjQ+NK92BJ7g8GbqiwRm1zDMWMifLXkFd3otiulL80owym/JSoGXaagAf4K2tGUXGklyBi9JwQuACiTI5RFH1ChZtxRbkX+I1FqdL1tDZIvUYOgGAKhCTTGxd5pROJ7xLDiJj6qq0ZX1dU7vGVgS976MaFi16IYEkK4=","uuid":"e67fe69b-e1fe-4ee8-b885-d801ea0bdf9d","callerId":"001","api":"userApi"}
2022-05-27 09:50:04 [http-nio-8080-exec-3] DEBUG o.s.s.OpenApiGateway - [callMethod,127] - 调用完毕：{"code":200,"data":"lfkznguWD1lAhR2S81ZcfvUd0tWgvkwZvCGn3sGMlIcg78Nv0L+NVy2lky3Zd/vHlIxspcXk3+DQmkmCWUtAP8qM7D2j+MR1WFtuKiOYV9Ee683CtNqDUoI7rxB/E6pzLDbpXqhMKnhe94Gxyg7mNnjne+MX+p19QaVzHBl/ilY=","uuid":"e67fe69b-e1fe-4ee8-b885-d801ea0bdf9d"}
````

## 版本记录

1.0.0  
初版，支持非对称加解密(RSA或SM2)和接口验签功能   

1.1.0  
新增对称加密模式(AES或SM4)，即：内容采用对称加密以提高加解密速度，对称加密的密钥用非对称加密后传输


