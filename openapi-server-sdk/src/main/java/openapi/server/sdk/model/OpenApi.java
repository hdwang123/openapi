package openapi.server.sdk.model;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开放api注解，标识一个开放api
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
