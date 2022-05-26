package openapi.client;

import lombok.extern.slf4j.Slf4j;
import openapi.client.openapiclient.UserApiClient;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 项目启动
 *
 * @author wanghuidong
 * @date 2022/5/26 19:39
 */
@Slf4j
@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder().sources(ClientApplication.class).web(WebApplicationType.NONE).run(args);
        UserApiClient client = context.getBean(UserApiClient.class);
        client.getUserById();
        client.saveUser();
        client.listUsers();
//        client.listUsers2();
    }
}
