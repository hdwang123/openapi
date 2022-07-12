package openapi.example.client;

import lombok.extern.slf4j.Slf4j;
import openapi.example.client.openapiclient.FileApiTest;
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
        long startTime = System.nanoTime();
//        UserApiTest test = context.getBean(UserApiTest.class);
//        test.getUserById();
//        test.saveUser();
//        test.batchSaveUser();
//        test.batchSaveUser2();
//        test.listUsers();
//        test.listUsers2();
//        test.listUsers3();
//        test.getAllUsers();
//        test.addUser();
//        test.addUsers();
//
//        UserApiTest2 test2 = context.getBean(UserApiTest2.class);
//        test2.getRoleById();
//        test2.getUserById();
//        test2.saveUser();
//        test2.batchSaveUser();
//        test2.batchSaveUser2();
//        test2.listUsers();
//        test2.listUsers2();
//        test2.listUsers3();
//        test2.getAllUsers();
//        test2.getAllUsersMap();
//        test2.addUser();
//        test2.addUsers();

        FileApiTest fileApiTest = context.getBean(FileApiTest.class);
//        fileApiTest.uploadTest();
//        fileApiTest.downloadTest();
        fileApiTest.batchUploadTest();
        fileApiTest.batchUpload2Test();

        long endTime = System.nanoTime();
        log.debug("耗时：{}ms", (endTime - startTime) / 100_0000);
    }
}
