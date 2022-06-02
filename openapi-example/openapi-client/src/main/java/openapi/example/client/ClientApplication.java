package openapi.example.client;

import lombok.extern.slf4j.Slf4j;
import openapi.example.client.openapiclient.UserApiClient;
import openapi.example.client.openapiclient.UserApiTest;
import openapi.example.client.openapiclient.UserApiTest2;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * 项目启动
 *
 * @author wanghuidong
 */
@Slf4j
@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder().sources(ClientApplication.class).web(WebApplicationType.NONE).run(args);
        UserApiTest test = context.getBean(UserApiTest.class);
        test.getUserById();
        test.saveUser();
        test.batchSaveUser();
        test.batchSaveUser2();
        test.listUsers();
        test.listUsers2();
        test.listUsers3();
        test.getAllUsers();
        test.addUser();
        test.addUsers();

        UserApiTest2 test2 =  context.getBean(UserApiTest2.class);
        test2.getRoleById();
        test2.getUserById();
        test2.saveUser();
        test2.batchSaveUser();
        test2.batchSaveUser2();
        test2.listUsers();
        test2.listUsers2();
        test2.listUsers3();
        test2.getAllUsers();
        test2.addUser();
        test2.addUsers();
    }
}
