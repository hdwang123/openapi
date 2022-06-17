package openapi.example.client.openapiclient;

import openapi.client.sdk.annotation.OpenApiMethod;
import openapi.client.sdk.annotation.OpenApiRef;
import openapi.example.client.model.Role;

/**
 * @author wanghuidong
 * 时间： 2022/6/2 0:15
 */
@OpenApiRef("roleApi")
public interface RoleApiClient {

    @OpenApiMethod("getRoleById")
    Role getRoleById(Long id);

}
