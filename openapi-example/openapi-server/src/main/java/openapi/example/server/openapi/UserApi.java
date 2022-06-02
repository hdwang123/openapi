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

    @OpenApiMethod("batchSaveUser")
    public void batchSaveUser(List<User> users) {
        log.info("batchSaveUser:" + JSONUtil.toJsonStr(users));
        log.info(JSONUtil.toJsonStr(users.get(0)));
    }

    @OpenApiMethod("batchSaveUser2")
    public void batchSaveUser(User[] users) {
        log.info("batchSaveUser2:" + JSONUtil.toJsonStr(users));
    }

    @OpenApiMethod(value = "listUsers")
    public List<User> listUsers(List<Long> ids) {
        log.info("listUsers: ids=" + JSONUtil.toJsonStr(ids));
        List<User> users = new ArrayList<>();
        users.add(new User(2L, "李四"));
        users.add(new User(3L, "王五"));
        return users;
    }

    @OpenApiMethod("listUsers2")
    public List<User> listUsers2(Long[] ids) {
        log.info("listUsers: ids=" + JSONUtil.toJsonStr(ids));
        List<User> users = new ArrayList<>();
        users.add(new User(2L, "李四"));
        users.add(new User(3L, "王五"));
        return users;
    }

    @OpenApiMethod("listUsers3")
    public List<User> listUsers3(long[] ids) {
        log.info("listUsers3: ids=" + JSONUtil.toJsonStr(ids));
        List<User> users = new ArrayList<>();
        users.add(new User(2L, "李四"));
        users.add(new User(3L, "王五"));
        return users;
    }

    @OpenApiMethod(value = "getAllUsers", retEncrypt = "false", enableSymmetricCry = "false")
    public List<User> getAllUsers() {
        log.info("getAllUsers");
        List<User> users = new ArrayList<>();
        users.add(new User(1L, "张三"));
        users.add(new User(2L, "李四"));
        users.add(new User(3L, "王五"));
        return users;
    }

    @OpenApiMethod("addUser")
    public User addUser(String name, String phone, String email) {
        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        user.setEmail(email);
        log.info("addUser:user={}", JSONUtil.toJsonStr(user));
        return user;
    }

    @OpenApiMethod("addUsers")
    public User addUser(Long id, String name, List<User> users) {
        List<User> list = new ArrayList<>();
        User user = new User(id, name);
        list.add(user);
        list.addAll(users);
        log.info("addUser:users={}", id, name, JSONUtil.toJsonStr(list));
        return user;
    }
}
