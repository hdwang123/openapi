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
    private OpenApiConfig config;

    /**
     * OpenApiRef标注的接口类
     */
    private Class<T> interClass;

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
            throw new BusinessException(interClass.getName() + "api名称不能为空");
        }
        OpenApiClient apiClient = new OpenApiClientBuilder(config.getBaseUrl(), config.getSelfPrivateKey(), config.getRemotePublicKey(), config.getCallerId(), api)
                .asymmetricCry(config.getAsymmetricCryEnum())
                .retDecrypt(config.isRetDecrypt())
                .enableSymmetricCry(config.isEnableSymmetricCry())
                .symmetricCry(config.getSymmetricCryEnum())
                .httpConnectionTimeout(config.getHttpConnectionTimeout())
                .httpReadTimeout(config.getHttpReadTimeout())
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
    }
}
