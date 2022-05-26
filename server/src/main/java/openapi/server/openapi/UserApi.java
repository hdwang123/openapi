package openapi.server.openapi;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.server.model.User;
import openapi.server.sdk.model.OpenApi;
import openapi.server.sdk.model.OpenApiMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * 对外开放的接口：用户api
 *
 * @author wanghuidong
 * @date 2022/5/26 19:43
 */
@Slf4j
@OpenApi("userApi")
public class UserApi {

    /**
     * 根据用户ID查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
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
        log.info("保存用户成功:" + JSONUtil.toJsonStr(user));
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

    /**
     * 暂不支持该类型的参数
     *
     * @param ids
     * @return
     */
    @OpenApiMethod("listUsers2")
    public List<User> listUsers2(Long[] ids) {
        log.info("listUsers: ids=" + ids);
        List<User> users = new ArrayList<>();
        users.add(new User(2L, "李四"));
        users.add(new User(3L, "王五"));
        return users;
    }

}
