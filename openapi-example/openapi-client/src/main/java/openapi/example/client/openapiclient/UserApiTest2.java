package openapi.example.client.openapiclient;

import lombok.extern.slf4j.Slf4j;
import openapi.client.sdk.OpenApiClient;
import openapi.client.sdk.OpenApiClientBuilder;
import openapi.example.client.model.Role;
import openapi.example.client.model.User;
import openapi.sdk.common.model.AsymmetricCryEnum;
import openapi.sdk.common.model.OutParams;
import openapi.sdk.common.model.SymmetricCryEnum;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserApiTest2 {

    @Autowired
    UserApiClient userApiClient;

    @Autowired
    RoleApiClient roleApiClient;

    public void getRoleById(){
        Role role = roleApiClient.getRoleById(1000L);
        log.info("返回值：" + role);
    }

    public void getUserById() {
        User user = userApiClient.getUserById(10001L);
        log.info("返回值：" + user);
    }

    public void saveUser() {
        User user = new User();
        user.setId(1L);
        user.setName("张三");
        Boolean aBoolean = userApiClient.saveUser(user);
        log.info("返回值：" + aBoolean);
    }

    public void batchSaveUser() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        user.setName("张三");
        users.add(user);
        userApiClient.batchSaveUser(users);
        log.info("返回值：void");
    }

    public void batchSaveUser2() {
        User[] users = new User[1];
        User user = new User();
        user.setId(1L);
        user.setName("张三");
        users[0] = user;
        userApiClient.batchSaveUser(users);
        log.info("返回值：void");
    }

    public void listUsers() {
        List<Long> ids = new ArrayList<>();
        ids.add(2L);
        ids.add(3L);
        List<User> users = userApiClient.listUsers(ids);
        log.info("返回值：" + users);
    }

    public void listUsers2() {
        Long[] ids = new Long[]{
                2L, 3L
        };
        List<User> users = userApiClient.listUsers2(ids);
        log.info("返回值：" + users);

    }

    public void listUsers3() {
        long[] ids = new long[]{
                2L, 3L
        };
        List<User> users = userApiClient.listUsers3(ids);
        log.info("返回值：" + users);
    }

    public void getAllUsers() {
        List<User> users = userApiClient.getAllUsers();
        log.info("返回值：" + users);
    }

    public void addUser() {
        //为了精确调用到想要的重载方法，这里将第一个参数转成了Object对象
        User user = userApiClient.addUser("展昭", "13312341234", "1331234@qq.com");
        log.info("返回值：" + user);
    }

    public void addUsers() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        user.setName("张三");
        users.add(user);
        User userRet = userApiClient.addUser(5L, "李寻欢", users);
        log.info("返回值：" + userRet);
    }
}
