package openapi.client.sdk.proxy;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.client.sdk.OpenApiClient;
import openapi.client.sdk.OpenApiClientBuilder;
import openapi.client.sdk.config.OpenApiClientConfig;
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
    private OpenApiClientConfig config;

    /**
     * 构造函数
     *
     * @param openApiClient openapi客户端
     * @param config        开放api客户端配置
     */
    public OpenApiRefProxyInvocationHandler(OpenApiClient openApiClient, OpenApiClientConfig config) {
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
                OpenApiClient apiClient = this.openApiClient;
                boolean configDif = this.methodConfigDif(openApiMethod);
                if (configDif) {
                    String api = method.getDeclaringClass().getAnnotation(OpenApiRef.class).value();
                    boolean retDecrypt = this.retDecrypt(openApiMethod);
                    boolean enableSymmetricCry = this.enableSymmetricCry(openApiMethod);
                    int httpConnectionTimeout = this.httpConnectionTimeout(openApiMethod);
                    int httpReadTimeout = this.httpReadTimeout(openApiMethod);
                    apiClient = new OpenApiClientBuilder(config.getBaseUrl(), config.getSelfPrivateKey(), config.getRemotePublicKey(), config.getCallerId(), api)
                            .asymmetricCry(config.getAsymmetricCryEnum())
                            .retDecrypt(retDecrypt)
                            .enableSymmetricCry(enableSymmetricCry)
                            .symmetricCry(config.getSymmetricCryEnum())
                            .httpConnectionTimeout(httpConnectionTimeout)
                            .httpReadTimeout(httpReadTimeout)
                            .build();
                }
                //调用远程openapi
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

    /**
     * 判断方法级别的配置与默认配置是否不同
     *
     * @param openApiMethod API方法注解
     * @return API方法配置是否与默认不同
     */
    private boolean methodConfigDif(OpenApiMethod openApiMethod) {
        boolean retDecryptDif = StrUtil.isNotBlank(openApiMethod.retDecrypt()) && Boolean.parseBoolean(openApiMethod.retDecrypt()) != config.isRetDecrypt();
        if (retDecryptDif) {
            return true;
        }
        boolean enableSymmetricCryDif = StrUtil.isNotBlank(openApiMethod.enableSymmetricCry()) && Boolean.parseBoolean(openApiMethod.enableSymmetricCry()) != config.isEnableSymmetricCry();
        if (enableSymmetricCryDif) {
            return true;
        }
        boolean httpConnectionTimeoutDif = openApiMethod.httpConnectionTimeout() != -1 && openApiMethod.httpConnectionTimeout() != config.getHttpConnectionTimeout();
        if (httpConnectionTimeoutDif) {
            return true;
        }
        boolean httpReadTimeoutDif = openApiMethod.httpReadTimeout() != -1 && openApiMethod.httpReadTimeout() != config.getHttpReadTimeout();
        if (httpReadTimeoutDif) {
            return true;
        }
        return false;
    }

    /**
     * 获取返回值是否需要解密
     *
     * @param openApiMethod API方法注解
     * @return 返回值是否需要解密
     */
    private boolean retDecrypt(OpenApiMethod openApiMethod) {
        boolean retDecrypt = config.isRetDecrypt();
        if (StrUtil.isNotBlank(openApiMethod.retDecrypt())) {
            retDecrypt = Boolean.parseBoolean(openApiMethod.retDecrypt());
        }
        return retDecrypt;
    }

    /**
     * 获取是否采用对称加密模式
     *
     * @param openApiMethod API方法注解
     * @return 是否采用对称加密模式
     */
    private boolean enableSymmetricCry(OpenApiMethod openApiMethod) {
        boolean enableSymmetricCry = config.isEnableSymmetricCry();
        if (StrUtil.isNotBlank(openApiMethod.enableSymmetricCry())) {
            enableSymmetricCry = Boolean.parseBoolean(openApiMethod.enableSymmetricCry());
        }
        return enableSymmetricCry;
    }

    /**
     * 获取HTTP连接超时时间
     *
     * @param openApiMethod API方法注解
     * @return HTTP连接超时时间
     */
    private int httpConnectionTimeout(OpenApiMethod openApiMethod) {
        int timeout = config.getHttpConnectionTimeout();
        if (openApiMethod.httpConnectionTimeout() != -1) {
            timeout = openApiMethod.httpConnectionTimeout();
        }
        return timeout;
    }

    /**
     * 获取HTTP数据传输超时时间
     *
     * @param openApiMethod API方法注解
     * @return HTTP数据传输超时时间
     */
    private int httpReadTimeout(OpenApiMethod openApiMethod) {
        int timeout = config.getHttpReadTimeout();
        if (openApiMethod.httpReadTimeout() != -1) {
            timeout = openApiMethod.httpReadTimeout();
        }
        return timeout;
    }
}
