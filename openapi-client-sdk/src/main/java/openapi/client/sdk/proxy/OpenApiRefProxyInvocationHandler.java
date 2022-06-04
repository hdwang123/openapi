package openapi.client.sdk.proxy;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.client.sdk.OpenApiClient;
import openapi.client.sdk.OpenApiClientBuilder;
import openapi.client.sdk.config.OpenApiConfig;
import openapi.client.sdk.model.OpenApiMethod;
import openapi.client.sdk.model.OpenApiRef;
import openapi.sdk.common.model.BusinessException;
import openapi.sdk.common.model.OutParams;
import openapi.sdk.common.util.StrObjectConvert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * OpenApiRef代理对象调用处理器
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
     * 开放api客户端配置
     */
    private OpenApiConfig config;

    /**
     * 构造函数
     *
     * @param openApiClient openapi客户端
     * @param config        开放api客户端配置
     */
    public OpenApiRefProxyInvocationHandler(OpenApiClient openApiClient, OpenApiConfig config) {
        this.openApiClient = openApiClient;
        this.config = config;
    }

    /**
     * 调用目标方法
     *
     * @param proxy  创建的OpenApiRef代理对象
     * @param method 被执行的方法
     * @param args   方法参数
     * @return 返回值
     * @throws Throwable 异常
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
                    throw new BusinessException(method.getName() + "api方法名称不能为空");
                }
                //方法级别的配置与默认配置不同，需要构建新的Client
                boolean retDecryptDif = StrUtil.isNotBlank(openApiMethod.retDecrypt()) && Boolean.parseBoolean(openApiMethod.retDecrypt()) != config.isRetDecrypt();
                boolean enableSymmetricCryDif = StrUtil.isNotBlank(openApiMethod.enableSymmetricCry()) && Boolean.parseBoolean(openApiMethod.enableSymmetricCry()) != config.isEnableSymmetricCry();
                OpenApiClient apiClient = this.openApiClient;
                if (retDecryptDif || enableSymmetricCryDif) {
                    String api = method.getDeclaringClass().getAnnotation(OpenApiRef.class).value();
                    apiClient = new OpenApiClientBuilder(config.getBaseUrl(), config.getSelfPrivateKey(), config.getRemotePublicKey(), config.getCallerId(), api)
                            .asymmetricCry(config.getAsymmetricCryEnum())
                            .retDecrypt(Boolean.parseBoolean(openApiMethod.retDecrypt()))
                            .enableSymmetricCry(Boolean.parseBoolean(openApiMethod.enableSymmetricCry()))
                            .symmetricCry(config.getSymmetricCryEnum())
                            .build();
                }
                OutParams outParams = apiClient.callOpenApi(methodName, args);
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
