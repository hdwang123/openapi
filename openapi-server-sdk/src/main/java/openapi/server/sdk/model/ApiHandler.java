package openapi.server.sdk.model;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * openapi处理器（具体的处理方法）
 *
 * @author wanghuidong
 * @date 2022/5/26 15:31
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
}
