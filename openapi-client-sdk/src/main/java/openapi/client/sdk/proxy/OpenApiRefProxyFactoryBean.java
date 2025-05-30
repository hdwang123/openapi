package openapi.client.sdk.proxy;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.client.sdk.OpenApiClient;
import openapi.client.sdk.OpenApiClientBuilder;
import openapi.client.sdk.config.OpenApiClientConfig;
import openapi.client.sdk.annotation.OpenApiRef;
import openapi.sdk.common.exception.OpenApiClientException;
import openapi.sdk.common.handler.AsymmetricCryHandler;
import openapi.sdk.common.handler.SymmetricCryHandler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Proxy;

/**
 * OpenApiRef代理对象工厂，用于创建OpenApiRef所标注接口的代理对象
 *
 * @author wanghuidong
 * 时间： 2022/6/1 22:00
 */
@Slf4j
public class OpenApiRefProxyFactoryBean<T> implements FactoryBean<T> {

    /**
     * 开放api客户端配置
     */
    @Autowired
    private OpenApiClientConfig config;

    @Autowired
    private ApplicationContext context;

    /**
     * OpenApiRef标注的接口类
     */
    private final Class<T> interClass;

    /**
     * 构造器
     *
     * @param interClass OpenApiRef标注的接口类
     */
    public OpenApiRefProxyFactoryBean(Class<T> interClass) {
        this.interClass = interClass;
    }

    /**
     * 获取一个OpenApiRef代理对象
     *
     * @return OpenApiRef所标注接口的代理对象
     */
    @Override
    public T getObject() {
        //检查配置
        checkConfig();

        //构建OpenApiClient
        String api = interClass.getAnnotation(OpenApiRef.class).value();
        if (StrUtil.isBlank(api)) {
            throw new OpenApiClientException(interClass.getName() + "api名称不能为空");
        }
        OpenApiClient apiClient = new OpenApiClientBuilder(config.getBaseUrl(), config.getSelfPrivateKey(), config.getRemotePublicKey(), config.getCallerId(), api)
                .asymmetricCry(config.getAsymmetricCryAlgo())
                .retDecrypt(config.isRetDecrypt())
                .cryModeEnum(config.getCryModeEnum())
                .symmetricCry(config.getSymmetricCryAlgo())
                .httpConnectionTimeout(config.getHttpConnectionTimeout())
                .httpReadTimeout(config.getHttpReadTimeout())
                .httpProxyHost(config.getHttpProxyHost())
                .httpProxyPort(config.getHttpProxyPort())
                .enableCompress(config.isEnableCompress())
                .customAsymmetricCryHandler(getAsymmetricCryHandler(config.getCustomAsymmetricCryHandler()))
                .customSymmetricCryHandler(getSymmetricCryHandler(config.getCustomSymmetricCryHandler()))
                .build();

        //创建OpenApiRef代理调用处理器对象
        OpenApiRefProxyInvocationHandler invocationHandler = new OpenApiRefProxyInvocationHandler(apiClient, config);

        //动态创建OpenApiRef接口的代理对象
        return (T) Proxy.newProxyInstance(interClass.getClassLoader(), new Class[]{interClass}, invocationHandler);
    }


    /**
     * 获取代理对象的类型
     *
     * @return 代理对象的类型
     */
    @Override
    public Class<?> getObjectType() {
        return interClass;
    }

    /**
     * 检查配置
     */
    private void checkConfig() {
        if (StrUtil.isBlank(config.getBaseUrl())) {
            throw new OpenApiClientException("openapi基础路径未配置");
        }
        if (StrUtil.isBlank(config.getSelfPrivateKey())) {
            throw new OpenApiClientException("本系统私钥未配置");
        }
        if (StrUtil.isBlank(config.getRemotePublicKey())) {
            throw new OpenApiClientException("远程系统的公钥未配置");
        }
        if (StrUtil.isBlank(config.getCallerId())) {
            throw new OpenApiClientException("调用者ID未配置");
        }
    }

    private AsymmetricCryHandler getAsymmetricCryHandler(String handlerBeanName) {
        if (StrUtil.isBlank(handlerBeanName)) {
            return null;
        }
        try {
            return context.getBean(handlerBeanName, AsymmetricCryHandler.class);
        } catch (Exception ex) {
            throw new OpenApiClientException("找不到自定义的AsymmetricCryHandler", ex);
        }
    }

    private SymmetricCryHandler getSymmetricCryHandler(String handlerBeanName) {
        if (StrUtil.isBlank(handlerBeanName)) {
            return null;
        }
        try {
            return context.getBean(handlerBeanName, SymmetricCryHandler.class);
        } catch (Exception ex) {
            throw new OpenApiClientException("找不到自定义的SymmetricCryHandler", ex);
        }
    }
}
