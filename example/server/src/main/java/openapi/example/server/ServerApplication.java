package openapi.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动
 *
 * @author wanghuidong
 * @date 2022/5/26 19:39
 */
@SpringBootApplication(scanBasePackages = "openapi")
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
