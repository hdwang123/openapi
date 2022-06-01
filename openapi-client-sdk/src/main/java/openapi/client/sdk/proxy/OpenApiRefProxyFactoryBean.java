package openapi.client.sdk.proxy;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.client.sdk.OpenApiClient;
import openapi.client.sdk.OpenApiClientBuilder;
import openapi.client.sdk.config.OpenApiConfig;
import openapi.client.sdk.model.OpenApiRef;
import openapi.sdk.common.model.BusinessException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

/**
 * OpenApiRef代理工厂，用于创建OpenApiRef代理对象
 *
 * @author wanghuidong
 * 时间： 2022/6/1 22:00
 */
@Slf4j
public class OpenApiRefProxyFactoryBean<T> implements FactoryBean<T> {

    @Autowired
    private OpenApiConfig config;

    private Class<T> interClass;

    public OpenApiRefProxyFactoryBean(Class<T> interClass) {
        this.interClass = interClass;
    }

    @Override
    public T getObject() throws Exception {
        if (StrUtil.isBlank(config.getBaseUrl())) {
            throw new BusinessException("openapi基础路径未配置");
        }
        if (StrUtil.isBlank(config.getSelfPrivateKey())) {
            throw new BusinessException("本系统私钥未配置");
        }
        if (StrUtil.isBlank(config.getRemotePublicKey())) {
            throw new BusinessException("远程系统的公钥未配置");
        }
        if (StrUtil.isBlank(config.getCallerId())) {
            throw new BusinessException("调用者ID未配置");
        }

        //构建OpenApiClient
        OpenApiRef openApiRef = interClass.getAnnotation(OpenApiRef.class);
        String api = openApiRef.value();
        if (StrUtil.isBlank(api)) {
            throw new BusinessException("api名称为空");
        }
        OpenApiClient apiClient = new OpenApiClientBuilder(config.getBaseUrl(), config.getSelfPrivateKey(), config.getRemotePublicKey(), config.getCallerId(), api)
                .asymmetricCry(config.getAsymmetricCryEnum())
                .retDecrypt(config.isRetDecrypt())
                .enableSymmetricCry(config.isEnableSymmetricCry())
                .symmetricCry(config.getSymmetricCryEnum())
                .build();
        OpenApiRefProxyInvocationHandler invocationHandler = new OpenApiRefProxyInvocationHandler(apiClient);

        //动态创建OpenApiRef接口的代理对象
        return (T) Proxy.newProxyInstance(interClass.getClassLoader(), new Class[]{interClass}, invocationHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return interClass;
    }
}
