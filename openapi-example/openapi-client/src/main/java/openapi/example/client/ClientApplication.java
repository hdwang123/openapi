package openapi.example.client;

import lombok.extern.slf4j.Slf4j;
import openapi.example.client.openapiclient.UserApiClient;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

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
        UserApiClient client = context.getBean(UserApiClient.class);
        client.getUserById();
        client.saveUser();
        client.batchSaveUser();
        client.batchSaveUser2();
        client.listUsers();
        client.listUsers2();
        client.listUsers3();
        client.getAllUsers();
        client.addUser();
        client.testUserApi();
    }
}
