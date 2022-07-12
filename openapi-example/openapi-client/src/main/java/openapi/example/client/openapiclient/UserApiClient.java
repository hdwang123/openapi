package openapi.example.client.openapiclient;

import openapi.client.sdk.annotation.OpenApiMethod;
import openapi.client.sdk.annotation.OpenApiRef;
import openapi.example.client.model.Gender;
import openapi.example.client.model.User;
import openapi.sdk.common.enums.CryModeEnum;

import java.util.List;
import java.util.Map;

/**
 * @author wanghuidong
 * 时间： 2022/6/1 18:50
 */
@OpenApiRef(value = "userApi")
public interface UserApiClient {

    @OpenApiMethod("getUserById")
    User getUserById(Long id);

    @OpenApiMethod("saveUser")
    Boolean saveUser(User user);

    @OpenApiMethod("batchSaveUser")
    void batchSaveUser(List<User> users);

    @OpenApiMethod("batchSaveUser2")
    void batchSaveUser(User[] users);

    @OpenApiMethod(value = "listUsers")
    List<User> listUsers(List<Long> ids);

    @OpenApiMethod("listUsers2")
    List<User> listUsers2(Long[] ids);

    @OpenApiMethod("listUsers3")
    List<User> listUsers3(long[] ids);

    @OpenApiMethod(value = "getAllUsers", retDecrypt = "false", cryModeEnum = CryModeEnum.ASYMMETRIC_CRY)
    List<User> getAllUsers();

    @OpenApiMethod(value = "getAllUsersMap")
    Map<Long, User> getAllUsersMap();

    @OpenApiMethod("addUser")
    User addUser(String name, String phone, String email, Gender gender);

    @OpenApiMethod("addUsers")
    User addUser(Long id, String name, List<User> users);
}
