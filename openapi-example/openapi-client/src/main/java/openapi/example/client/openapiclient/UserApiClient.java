package openapi.example.client.openapiclient;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
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
public class UserApiClient {

    @Value("${keys.local.rsa.privateKey}")
    private String privateKey;

    @Value("${keys.local.rsa.publicKey}")
    private String publicKey;

    @Value("${keys.remote.rsa.publicKey}")
    private String remotePublicKey;

    String baseUrl = "http://localhost:8080";


    public void getUserById() {
        try {
            OpenApiClient apiClient = new OpenApiClient(baseUrl, privateKey, remotePublicKey, AsymmetricCryEnum.RSA, true, true, SymmetricCryEnum.AES);
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
            OpenApiClient apiClient = new OpenApiClient(baseUrl, privateKey, remotePublicKey, AsymmetricCryEnum.RSA, true, true, SymmetricCryEnum.AES);
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

    public void batchSaveUser() {
        try {
            OpenApiClient apiClient = new OpenApiClient(baseUrl, privateKey, remotePublicKey, AsymmetricCryEnum.RSA, true, true, SymmetricCryEnum.AES);
            InParams inParams = new InParams();
            inParams.setUuid(UUID.randomUUID().toString());
            inParams.setCallerId("001");
            inParams.setApi("userApi");
            inParams.setMethod("batchSaveUser");

            List<User> users = new ArrayList<>();
            User user = new User();
            user.setId(1L);
            user.setName("张三");
            users.add(user);
            inParams.setBody(JSONUtil.toJsonStr(users));
            log.info("入参：" + inParams);
            OutParams outParams = apiClient.callOpenApi(inParams);
            log.info("返回值：" + outParams);
        } catch (Exception ex) {
            log.error("异常", ex);
        }
    }

    public void batchSaveUser2() {
        try {
            OpenApiClient apiClient = new OpenApiClient(baseUrl, privateKey, remotePublicKey, AsymmetricCryEnum.RSA, true, true, SymmetricCryEnum.AES);
            InParams inParams = new InParams();
            inParams.setUuid(UUID.randomUUID().toString());
            inParams.setCallerId("001");
            inParams.setApi("userApi");
            inParams.setMethod("batchSaveUser2");

            User[] users = new User[1];
            User user = new User();
            user.setId(1L);
            user.setName("张三");
            users[0] = user;
            inParams.setBody(JSONUtil.toJsonStr(users));
            log.info("入参：" + inParams);
            OutParams outParams = apiClient.callOpenApi(inParams);
            log.info("返回值：" + outParams);
        } catch (Exception ex) {
            log.error("异常", ex);
        }
    }

    public void listUsers() {
        try {
            OpenApiClient apiClient = new OpenApiClient(baseUrl, privateKey, remotePublicKey, AsymmetricCryEnum.RSA, false, false, null);
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

    public void listUsers2() {
        try {
            OpenApiClient apiClient = new OpenApiClient(baseUrl, privateKey, remotePublicKey, AsymmetricCryEnum.RSA, true, true, SymmetricCryEnum.AES);
            InParams inParams = new InParams();
            inParams.setUuid(UUID.randomUUID().toString());
            inParams.setCallerId("001");
            inParams.setApi("userApi");
            inParams.setMethod("listUsers2");

            Long[] ids = new Long[]{
                    2L, 3L
            };
            inParams.setBody(JSONUtil.toJsonStr(ids));
            log.info("入参：" + inParams);
            OutParams outParams = apiClient.callOpenApi(inParams);
            log.info("返回值：" + outParams);
        } catch (Exception ex) {
            log.error("异常", ex);
        }
    }

    public void listUsers3() {
        try {
            OpenApiClient apiClient = new OpenApiClient(baseUrl, privateKey, remotePublicKey, AsymmetricCryEnum.RSA, true, true, SymmetricCryEnum.AES);
            InParams inParams = new InParams();
            inParams.setUuid(UUID.randomUUID().toString());
            inParams.setCallerId("001");
            inParams.setApi("userApi");
            inParams.setMethod("listUsers3");

            long[] ids = new long[]{
                    2L, 3L
            };
            inParams.setBody(JSONUtil.toJsonStr(ids));
            log.info("入参：" + inParams);
            OutParams outParams = apiClient.callOpenApi(inParams);
            log.info("返回值：" + outParams);
        } catch (Exception ex) {
            log.error("异常", ex);
        }
    }

    public void getAllUsers() {
        try {
            OpenApiClient apiClient = new OpenApiClient(baseUrl, privateKey, remotePublicKey, AsymmetricCryEnum.RSA, true, true, SymmetricCryEnum.AES);
            InParams inParams = new InParams();
            inParams.setUuid(UUID.randomUUID().toString());
            inParams.setCallerId("001");
            inParams.setApi("userApi");
            inParams.setMethod("getAllUsers");
            log.info("入参：" + inParams);
            OutParams outParams = apiClient.callOpenApi(inParams);
            log.info("返回值：" + outParams);
        } catch (Exception ex) {
            log.error("异常", ex);
        }
    }


    public void addUser() {
        try {
            OpenApiClient apiClient = new OpenApiClient(baseUrl, privateKey, remotePublicKey, AsymmetricCryEnum.RSA, true, true, SymmetricCryEnum.AES);
            List<User> users = new ArrayList<>();
            User user = new User();
            user.setId(1L);
            user.setName("张三");
            users.add(user);
            OutParams outParams = apiClient.callOpenApi("001", "userApi", "addUser", 5, "展昭", users);
            log.info("返回值：" + outParams);
        } catch (Exception ex) {
            log.error("异常", ex);
        }
    }
}
