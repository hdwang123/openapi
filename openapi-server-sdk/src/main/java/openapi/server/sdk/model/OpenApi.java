package openapi.server.sdk.model;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开放api注解，标识一个开放api
 * 注：该注解已经集成@Component注解，直接将此注解标识在一个bean类上，然后确保项目能够扫描到这些bean的包即可
 *
 * @author wanghuidong
 */
@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
public @interface OpenApi {

    /**
     * 开放api名称，可以与bean名不同
     *
     * @return 开放api名称
     */
    String value() default "";
}
