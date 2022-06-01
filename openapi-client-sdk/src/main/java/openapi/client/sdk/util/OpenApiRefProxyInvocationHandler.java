package openapi.client.sdk.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.client.sdk.OpenApiClient;
import openapi.client.sdk.model.OpenApiMethod;
import openapi.sdk.common.model.BusinessException;
import openapi.sdk.common.model.OutParams;
import openapi.sdk.common.util.StrObjectConvert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * OpenApiRef代理调用处理器
 *
 * @author wanghuidong
 * 时间： 2022/6/1 19:17
 */
@Slf4j
public class OpenApiRefProxyInvocationHandler implements InvocationHandler {

    /**
     * openapi客户端
     */
    private OpenApiClient openApiClient;

    /**
     * 构造函数
     *
     * @param openApiClient openapi客户端
     */
    public OpenApiRefProxyInvocationHandler(OpenApiClient openApiClient) {
        this.openApiClient = openApiClient;
    }

    /**
     * 调用目标方法
     *
     * @param proxy  创建的OpenApiRef代理对象
     * @param method 被执行的方法
     * @param args   方法参数
     * @return 返回值
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            //如果调用Object基类里的函数,则调用本对象里的函数（本对象 代理 目标接口, 也继承了Object对象）
            //解决idea调试的时候代理对象显示 "null" 问题（idea调用toString函数显示代理对象）
            return method.invoke(this, args);
        } else {
            //远程调用指定的openapi方法
            if (method.isAnnotationPresent(OpenApiMethod.class)) {
                OpenApiMethod openApiMethod = method.getAnnotation(OpenApiMethod.class);
                String methodName = openApiMethod.value();
                if (StrUtil.isBlank(methodName)) {
                    throw new BusinessException("api方法名称为空");
                }
                OutParams outParams = openApiClient.callOpenApi(methodName, args);
                Class<?> returnClass = method.getReturnType();
                if (OutParams.isSuccess(outParams)) {
                    return StrObjectConvert.strToObj(outParams.getData(), returnClass);
                } else {
                    throw new BusinessException("返回失败：" + outParams.getMessage());
                }
            } else {
                log.warn("{}非OpenApiMethod,不进行代理", method.getName());
            }
        }
        return null;
    }

}
