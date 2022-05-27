package openapi.server.sdk.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author wanghuidong
 * @date 2022/5/27 8:59
 */
@ComponentScans(value =
        {@ComponentScan(value = "openapi.server.sdk")})
@EntityScan(basePackages = {"openapi.server.sdk"})
@Configuration
public class BeanScanConfig implements EnvironmentAware {

    @Override
    public void setEnvironment(Environment environment) {
        System.out.println("############################ init openapi-server-sdk BeanScanConfig ##############################");
    }
}
