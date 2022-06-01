package openapi.client.sdk.proxy;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.client.sdk.model.OpenApiRef;
import openapi.sdk.common.model.BusinessException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * bean工厂的后置处理器，用于动态注册OpenApiRef代理对象，当存在配置openapi.config.openApiRefPath时生效
 *
 * @author wanghuidong
 * 时间： 2022/6/1 22:12
 */
@Slf4j
@Component
@ConditionalOnProperty("openapi.config.openApiRefPath")
public class OpenApiRefProxyRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, ResourceLoaderAware, EnvironmentAware {

    /**
     * 环境对象，用来获取各种配置
     */
    private Environment environment;

    /**
     * 资源加载对象，用来获取各种资源
     */
    private ResourcePatternResolver resolver;
    private MetadataReaderFactory metadataReaderFactory;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 扫描自定义注解，获取Class
        Set<Class<?>> interClazzSet = this.getTypesAnnotatedWith(OpenApiRef.class);

        //将 class包装为BeanDefinition ，注册到Spring的Ioc容器中
        for (Class<?> interClazz : interClazzSet) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(interClazz);
            GenericBeanDefinition definition = (GenericBeanDefinition) beanDefinitionBuilder.getRawBeanDefinition();
            //设置构造方法的参数  对于Class<?>,既可以设置为Class,也可以传Class的完全类名
            definition.getConstructorArgumentValues().addGenericArgumentValue(interClazz);

            //Bean的类型，指定为某个代理接口的类型
            definition.setBeanClass(OpenApiRefProxyFactoryBean.class);
            //表示 根据代理接口的类型来自动装配
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            registry.registerBeanDefinition(interClazz.getName(), definition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    /**
     * 获取指定注解的接口类
     *
     * @param openApiRefClass 注解类
     * @return 接口类
     */
    public Set<Class<?>> getTypesAnnotatedWith(Class<OpenApiRef> openApiRefClass) {
        String scanPath = environment.getProperty("openapi.config.openApiRefPath");
        if (StrUtil.isBlank(scanPath)) {
            throw new BusinessException("OpenApiRef接口所在路径为空");
        }
        Set<Class<?>> classes = new HashSet<Class<?>>();
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    .concat(ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(scanPath))
                            .concat("/**/*.class"));
            Resource[] resources = resolver.getResources(packageSearchPath);
            MetadataReader metadataReader = null;
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    try {
                        // 当类型是接口再添加到集合
                        if (metadataReader.getClassMetadata().isInterface()) {
                            Class interClass = Class.forName(metadataReader.getClassMetadata().getClassName());
                            if (interClass.isAnnotationPresent(openApiRefClass)) {
                                classes.add(interClass);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return classes;
        } catch (Exception ex) {
            log.error("扫描包下的资源异常", ex);
            return classes;
        }
    }
}
