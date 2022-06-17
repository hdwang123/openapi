package openapi.server.sdk.model;

import lombok.Data;
import openapi.server.sdk.annotation.OpenApiMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

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
     * 方法参数类型(Class类型信息不完整，无法提取List里元素的类型)
     */
    private Type[] paramTypes;

    /**
     * 方法的注解
     */
    private OpenApiMethod openApiMethod;

    @Override
    public String toString() {
        return String.format("%s:%s:%s", bean.getClass().getSimpleName(), method.getName(), Arrays.asList(paramTypes));
    }
}
