package openapi.client.sdk.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * bean扫描配置
 *
 * @author wanghuidong
 */
@ComponentScans(value =
        {@ComponentScan(value = "openapi.client.sdk")})
@EntityScan(basePackages = {"openapi.client.sdk"})
@Configuration
public class BeanScanConfig implements EnvironmentAware {

    @Override
    public void setEnvironment(Environment environment) {
        System.out.println("############################ init openapi-client-sdk BeanScanConfig ##############################");
    }
}
