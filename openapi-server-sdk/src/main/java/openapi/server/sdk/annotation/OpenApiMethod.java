package openapi.server.sdk.annotation;

import openapi.sdk.common.enums.CryModeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一个服务端开放 API 方法。
 * <p>
 * 注：目前支持的参数类型有基本类型、字符串、数组、普通javabean、List等
 * </p>
 *
 * @author wanghuidong
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OpenApiMethod {

    String INHERIT_BOOLEAN = "";

    /**
     * 开放 API 方法名称，可以与 Java 方法名不同；为空时使用 Java 方法名。
     *
     * @return 开放api方法名称
     */
    String value() default "";

    /**
     * 返回值是否需要加密(true:需要，false:不需要，默认由{@link openapi.server.sdk.config.OpenApiConfig}决定)
     *
     * @return 返回值是否需要加密
     */
    String retEncrypt() default INHERIT_BOOLEAN;

    /**
     * 加密模式(默认由{@link openapi.server.sdk.config.OpenApiConfig}决定)
     *
     * @return 加密模式
     */
    CryModeEnum cryModeEnum() default CryModeEnum.UNKNOWN;

    /**
     * HTTP传输的数据是否启用压缩(true:压缩，false:不压缩，默认由{@link openapi.server.sdk.config.OpenApiConfig}决定)
     *
     * @return 是否启用压缩
     */
    String enableCompress() default INHERIT_BOOLEAN;

}
