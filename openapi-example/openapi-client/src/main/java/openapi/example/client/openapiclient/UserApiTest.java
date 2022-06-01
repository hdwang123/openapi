package openapi.example.client.openapiclient;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.client.sdk.OpenApiClientBuilder;
import openapi.example.client.model.User;
import openapi.client.sdk.OpenApiClient;
import openapi.sdk.common.model.AsymmetricCryEnum;
import openapi.sdk.common.model.InParams;
import openapi.sdk.common.model.OutParams;
import openapi.sdk.common.model.SymmetricCryEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author wanghuidong
 */
@Slf4j
@Component
public class UserApiTest {

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

    public void saveUser() {
        User user = new User();
        user.setId(1L);
        user.setName("张三");
        OutParams outParams = apiClient.callOpenApi("saveUser", user);
        log.info("返回值：" + outParams);
    }

    public void batchSaveUser() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        user.setName("张三");
        users.add(user);
        OutParams outParams = apiClient.callOpenApi("batchSaveUser", users);
        log.info("返回值：" + outParams);
    }

    public void batchSaveUser2() {
        User[] users = new User[1];
        User user = new User();
        user.setId(1L);
        user.setName("张三");
        users[0] = user;
        //仅一个参数且是数组类型，必须转成Object类型，否则会被识别为多个参数
        OutParams outParams = apiClient.callOpenApi("batchSaveUser2", (Object) users);
        log.info("返回值：" + outParams);
    }

    public void listUsers() {
        List<Long> ids = new ArrayList<>();
        ids.add(2L);
        ids.add(3L);
        OutParams outParams = apiClient.callOpenApi("listUsers", ids);
        log.info("返回值：" + outParams);
    }

    public void listUsers2() {
        Long[] ids = new Long[]{
                2L, 3L
        };
        //仅一个参数且是数组类型，必须转成Object类型，否则会被识别为多个参数
        OutParams outParams = apiClient.callOpenApi("listUsers2", (Object) ids);
        log.info("返回值：" + outParams);

    }

    public void listUsers3() {
        long[] ids = new long[]{
                2L, 3L
        };
        //仅一个参数且是数组类型,long这种基本类型非包装类型数组不需要强转Object
        OutParams outParams = apiClient.callOpenApi("listUsers3", ids);
        log.info("返回值：" + outParams);
    }

    public void getAllUsers() {
        OutParams outParams = apiClient.callOpenApi("getAllUsers");
        log.info("返回值：" + outParams);
    }

    public void addUser() {
        //为了精确调用到想要的重载方法，这里将第一个参数转成了Object对象
        OutParams outParams = apiClient.callOpenApi("addUser", (Object) "展昭", "13312341234", "1331234@qq.com");
        log.info("返回值：" + outParams);
    }

    public void addUsers() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        user.setName("张三");
        users.add(user);
        OutParams outParams = apiClient.callOpenApi("addUsers", 5L, "李寻欢", users);
        log.info("返回值：" + outParams);
    }
}
