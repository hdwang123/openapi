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

    public void getUserById() {
        try {
            String baseUrl = "http://localhost:8080";
            OpenApiClient apiClient = new OpenApiClient(baseUrl, privateKey, remotePublicKey, AsymmetricCryEnum.RSA, true,true, SymmetricCryEnum.AES);
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

    public void listUsers() {
        try {
            String baseUrl = "http://localhost:8080";
            OpenApiClient apiClient = new OpenApiClient(baseUrl, privateKey, remotePublicKey, AsymmetricCryEnum.RSA, true,true, SymmetricCryEnum.AES);
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
            String baseUrl = "http://localhost:8080";
            OpenApiClient apiClient = new OpenApiClient(baseUrl, privateKey, remotePublicKey, AsymmetricCryEnum.RSA, true,true, SymmetricCryEnum.AES);
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
}
