package openapi.server.sdk.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开放api方法注解，标识一个开放api的方法
 * <p>
 * 注：目前支持的参数类型有基本类型、字符串、数组、普通javabean、List等
 * </p>
 *
 * @author wanghuidong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface OpenApiMethod {

    /**
     * 开放api方法名称，可以与方法名不同
     *
     * @return 开放api方法名称
     */
    String value() default "";

    /**
     * 返回值是否需要加密(true:需要，false:不需要，默认由{@link openapi.server.sdk.config.OpenApiConfig}决定)
     *
     * @return 返回值是否需要加密
     */
    String retEncrypt() default "";

    /**
     * 是否启用对称加密，(true:启用，false:不启用，默认由{@link openapi.server.sdk.config.OpenApiConfig}决定)
     * 注：内容采用对称加密，对称加密密钥采用非对称加密
     *
     * @return 是否启用对称加密
     */
    String enableSymmetricCry() default "";
}
