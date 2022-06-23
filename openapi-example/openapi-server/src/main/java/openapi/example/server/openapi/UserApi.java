package openapi.example.server.openapi;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.example.server.model.User;
import openapi.server.sdk.annotation.OpenApiMethod;
import openapi.server.sdk.annotation.OpenApi;
import openapi.server.sdk.doc.annotation.OpenApiDoc;

import java.util.ArrayList;
import java.util.List;

/**
 * 对外开放的接口：用户api
 *
 * @author wanghuidong
 */
@Slf4j
@OpenApiDoc(cnName = "用户API", describe = "用户对外服务接口")
@OpenApi("userApi")
public class UserApi {

    @OpenApiDoc(cnName = "查询用户", describe = "根据用户ID查询用户", retCnName = "用户", retDescribe = "用户信息")
    @OpenApiMethod("getUserById")
    public User getUserById(@OpenApiDoc(cnName = "用户ID") Long id) {
        log.info("getUserById：id=" + id);
        User user = new User();
        user.setId(1L);
        user.setName("张三");
        return user;
    }

    @OpenApiDoc(cnName = "保存用户", describe = "保存用户信息", retCnName = "是否保存成功", retDescribe = "保存用户是否成功")
    @OpenApiMethod("saveUser")
    public Boolean saveUser(@OpenApiDoc(cnName = "用户", describe = "用户信息") User user) {
        log.info("saveUser:" + JSONUtil.toJsonStr(user));
        return true;
    }

    @OpenApiDoc(ignore = true)
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

    @OpenApiDoc(cnName = "添加用户", describe = "添加多个用户信息",retCnName = "返回添加的第一个用户")
    @OpenApiMethod("addUsers")
    public User addUser(@OpenApiDoc(cnName = "用户ID") Long id,
                        @OpenApiDoc(cnName = "用户名") String name,
                        @OpenApiDoc(cnName = "用户列表", describe = "多个用户信息") List<User> users) {
        List<User> list = new ArrayList<>();
        User user = new User(id, name);
        list.add(user);
        list.addAll(users);
        log.info("addUser:users={}", id, name, JSONUtil.toJsonStr(list));
        return user;
    }
}
