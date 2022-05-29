package openapi.example.server.openapi;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.example.server.model.User;
import openapi.server.sdk.model.OpenApiMethod;
import openapi.server.sdk.model.OpenApi;

import java.util.ArrayList;
import java.util.List;

/**
 * 对外开放的接口：用户api
 *
 * @author wanghuidong
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

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 是否成功
     */
    @OpenApiMethod("saveUser")
    public Boolean saveUser(User user) {
        log.info("saveUser:" + JSONUtil.toJsonStr(user));
        return true;
    }

    /**
     * 列出指定用户列表
     *
     * @param ids 用户ID列表
     * @return 用户列表
     */
    @OpenApiMethod(value = "listUsers", retEncrypt = "false", enableSymmetricCry = "false")
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
     * @param ids 用户ID数据
     * @return 用户列表
     */
    @OpenApiMethod("listUsers2")
    public List<User> listUsers2(Long[] ids) {
        log.info("listUsers: ids=" + ids);
        List<User> users = new ArrayList<>();
        users.add(new User(2L, "李四"));
        users.add(new User(3L, "王五"));
        return users;
    }


    /**
     * 获取所有的用户
     *
     * @return 用户列表
     */
    @OpenApiMethod(value = "getAllUsers")
    public List<User> getAllUsers() {
        log.info("getAllUsers");
        List<User> users = new ArrayList<>();
        users.add(new User(1L, "张三"));
        users.add(new User(2L, "李四"));
        users.add(new User(3L, "王五"));
        return users;
    }

}
