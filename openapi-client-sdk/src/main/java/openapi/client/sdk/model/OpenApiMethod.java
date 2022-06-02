package openapi.client.sdk.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开放api方法注解，标识一个开放api的方法
 * 注：目前仅支持的参数类型有基本类型、字符串、数组、普通javabean、List等
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
     * 返回值是否需要解密(true:需要，false:不需要，默认为空由OpenApiConfig决定)，优先级高于OpenApiConfig中的配置
     *
     * @return 返回值是否需要解密
     */
    String retDecrypt() default "";

    /**
     * 是否启用对称加密，(true:启用，false:不启用，默认为空由OpenApiConfig决定)，优先级高于OpenApiConfig中的配置
     * 注：内容采用对称加密，对称加密密钥采用非对称加密
     *
     * @return 是否启用对称加密
     */
    String enableSymmetricCry() default "";
}
