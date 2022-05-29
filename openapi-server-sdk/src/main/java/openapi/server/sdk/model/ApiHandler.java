package openapi.server.sdk.model;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * openapi处理器（具体的处理方法）
 *
 * @author wanghuidong
 */
@Data
public class ApiHandler {

    /**
     * openapi处理对象
     */
    private Object bean;

    /**
     * openapi处理方法
     */
    private Method method;

    /**
     * 方法参数类型
     */
    private Class[] paramClasses;

    /**
     * 方法的注解
     */
    private OpenApiMethod openApiMethod;
}
