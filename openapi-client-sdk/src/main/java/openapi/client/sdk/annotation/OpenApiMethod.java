package openapi.client.sdk.annotation;

import openapi.client.sdk.config.OpenApiClientConfig;
import openapi.sdk.common.enums.CryModeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一个客户端 OpenAPI 代理方法。
 * <p>
 * 注解中的方法级配置优先于 {@link OpenApiClientConfig} 中的全局配置；
 * 未显式配置的属性将继承全局配置。
 * </p>
 * <p>
 * 当前支持基本类型、字符串、数组、JavaBean、集合及二进制对象等参数类型。
 * 被标注的方法必须声明在使用
 * {@link OpenApiRef} 标注的接口中，才能通过客户端动态代理发起远程调用。
 * </p>
 *
 * @author wanghuidong
 * @see OpenApiRef
 * @see OpenApiClientConfig
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OpenApiMethod {

    /**
     * 字符串布尔配置的继承值。
     */
    String INHERIT_BOOLEAN = "";

    /**
     * HTTP 超时配置的继承值。
     */
    int INHERIT_TIMEOUT = -1;

    /**
     * 远程 OpenAPI 方法名称，可以与本地 Java 方法名不同。
     * <p>
     * 未配置或为空时，默认使用本地 Java 方法名。
     * </p>
     *
     * @return 远程 OpenAPI 方法名称
     */
    String value() default "";

    /**
     * 是否解密远程接口返回值。
     * <ul>
     *     <li>{@code "true"}：解密返回值</li>
     *     <li>{@code "false"}：不解密返回值</li>
     *     <li>空字符串：继承 {@link OpenApiClientConfig#isRetDecrypt()} 的配置</li>
     * </ul>
     *
     * @return 返回值是否需要解密
     */
    String retDecrypt() default INHERIT_BOOLEAN;

    /**
     * 请求和响应使用的加密模式。
     * {@link CryModeEnum#UNKNOWN} 表示继承
     * {@link OpenApiClientConfig#getCryModeEnum()} 的配置。
     *
     * @return 加密模式
     */
    CryModeEnum cryModeEnum() default CryModeEnum.UNKNOWN;

    /**
     * HTTP 建立连接超时时间，单位为秒。
     * {@link #INHERIT_TIMEOUT} 表示继承
     * {@link OpenApiClientConfig#getHttpConnectionTimeout()} 的配置。
     *
     * @return HTTP 建立连接超时时间
     */
    int httpConnectionTimeout() default INHERIT_TIMEOUT;

    /**
     * HTTP 数据读取超时时间，单位为秒。
     * {@link #INHERIT_TIMEOUT} 表示继承
     * {@link OpenApiClientConfig#getHttpReadTimeout()} 的配置。
     *
     * @return HTTP 数据读取超时时间
     */
    int httpReadTimeout() default INHERIT_TIMEOUT;

    /**
     * HTTP 传输数据是否启用压缩。
     * <ul>
     *     <li>{@code "true"}：启用压缩</li>
     *     <li>{@code "false"}：禁用压缩</li>
     *     <li>空字符串：继承 {@link OpenApiClientConfig#isEnableCompress()} 的配置</li>
     * </ul>
     *
     * @return 是否启用压缩
     */
    String enableCompress() default INHERIT_BOOLEAN;
}
