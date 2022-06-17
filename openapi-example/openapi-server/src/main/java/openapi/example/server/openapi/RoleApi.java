package openapi.example.server.openapi;

import lombok.extern.slf4j.Slf4j;
import openapi.example.server.model.Role;
import openapi.server.sdk.annotation.OpenApi;
import openapi.server.sdk.annotation.OpenApiMethod;

/**
 * 对外开放的接口：角色api
 *
 * @author wanghuidong
 * 时间： 2022/6/2 0:13
 */
@Slf4j
@OpenApi("roleApi")
public class RoleApi {

    @OpenApiMethod("getRoleById")
    public Role getRoleById(Long id) {
        Role role = new Role(1L, "管理员");
        return role;
    }
}
