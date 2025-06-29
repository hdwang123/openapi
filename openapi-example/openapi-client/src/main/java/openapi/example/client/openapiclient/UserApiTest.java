package openapi.example.client.openapiclient;

import lombok.extern.slf4j.Slf4j;
import openapi.client.sdk.OpenApiClient;
import openapi.client.sdk.OpenApiClientBuilder;
import openapi.example.client.model.Gender;
import openapi.example.client.model.User;
import openapi.sdk.common.enums.AsymmetricCryAlgo;
import openapi.sdk.common.enums.CryModeEnum;
import openapi.sdk.common.model.OutParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wanghuidong
 */
@Slf4j
@Component
public class UserApiTest {

    @Value("${keys.local.sm2.privateKey}")
    private String privateKey;

    @Value("${keys.remote.sm2.publicKey}")
    private String remotePublicKey;

    @Value("${openapi.client.config.baseUrl}")
    private String baseUrl;

    /**
     * 定义OpenApiClient
     */
    OpenApiClient apiClient = null;

    @PostConstruct
    public void init() {
        apiClient = new OpenApiClientBuilder(baseUrl, privateKey, remotePublicKey, "001", "userApi")
                .asymmetricCry(AsymmetricCryAlgo.SM2)
                .retDecrypt(true)
                .cryModeEnum(CryModeEnum.SYMMETRIC_CRY)
                .enableCompress(false)
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
        //存在方法级别配置，需构建新的client
        OpenApiClient client = new OpenApiClientBuilder(baseUrl, privateKey, remotePublicKey, "001", "userApi")
                .retDecrypt(false)
                .asymmetricCry(AsymmetricCryAlgo.SM2)
                .cryModeEnum(CryModeEnum.ASYMMETRIC_CRY)
                .httpReadTimeout(10)
                .enableCompress(true)
                .build();
        OutParams outParams = client.callOpenApi("getAllUsers");
        log.info("返回值：" + outParams);
    }

    public void addUser() {
        //为了精确调用到想要的重载方法，这里将第一个参数转成了Object对象
        OutParams outParams = apiClient.callOpenApi("addUser", (Object) "展昭", "13312341234", "1331234@qq.com", Gender.MALE);
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
