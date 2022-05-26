package openapi.server.sdk.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开放api方法注解，标识一个开放api的方法
 * 注：目前仅支持一个参数的方法，参数类型可以是基本类型、字符串、javabean、List等
 *
 * @author wanghuidong
 * @date 2022/5/26 15:20
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface OpenApiMethod {

    /**
     * 开放api方法名称，可以与方法名不同
     *
     * @return
     */
    String value() default "";
}
