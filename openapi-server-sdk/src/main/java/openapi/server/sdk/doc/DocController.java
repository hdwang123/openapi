package openapi.server.sdk.doc;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import openapi.sdk.common.constant.Constant;
import openapi.server.sdk.doc.annotation.OpenApiDoc;
import openapi.server.sdk.doc.model.*;
import openapi.server.sdk.model.ApiHandler;
import openapi.server.sdk.model.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 文档控制器
 *
 * @author wanghuidong
 * 时间： 2022/6/21 20:10
 */
@RestController
public class DocController {

    @Autowired
    private Context context;

    /**
     * 忽略添加属性的类型
     */
    private static final List<Class> ignoreAddPropertyTypes = Arrays.asList(
            String.class,
            Collection.class,
            Map.class
    );

    /**
     * 返回接口文档数据
     *
     * @return 接口文档数据
     */
    @GetMapping(value = Constant.DOC_PATH, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String doc() {
        Map<String, Api> apiMap = new HashMap<>();
        for (ApiHandler apiHandler : context.getApiHandlers()) {
            String beanName = apiHandler.getBeanName();

            Api api = apiMap.get(beanName);
            if (api == null) {
                api = getApi(apiHandler);
                if (api == null) {
                    continue;
                }
                apiMap.put(beanName, api);
            }
            Method method = getMethod(apiHandler);
            if (method != null) {
                api.getMethods().add(method);
            }
        }
        Collection<Api> apiList = apiMap.values();
        return JSONUtil.toJsonStr(apiList);
    }

    /**
     * 获取API信息
     *
     * @param apiHandler openapi处理器
     * @return API信息
     */
    private Api getApi(ApiHandler apiHandler) {
        Api api;
        Class apiClass = apiHandler.getBean().getClass();

        api = new Api();
        api.setOpenApiName(apiHandler.getOpenApiName());
        api.setName(apiClass.getSimpleName());
        api.setFullName(apiClass.getName());

        if (apiClass.isAnnotationPresent(OpenApiDoc.class)) {
            OpenApiDoc apiDoc = (OpenApiDoc) apiClass.getAnnotation(OpenApiDoc.class);
            api.setCnName(apiDoc.cnName());
            api.setDescribe(apiDoc.describe());
            if (apiDoc.ignore()) {
                //取消文档的生成
                api = null;
            }
        }
        return api;
    }

    /**
     * 获取方法信息
     *
     * @param apiHandler openapi处理器
     * @return 方法信息
     */
    private Method getMethod(ApiHandler apiHandler) {
        Method method = new Method();
        method.setOpenApiMethodName(apiHandler.getOpenApiMethodName());
        java.lang.reflect.Method m = apiHandler.getMethod();
        method.setName(m.getName());

        if (m.isAnnotationPresent(OpenApiDoc.class)) {
            OpenApiDoc apiDoc = m.getAnnotation(OpenApiDoc.class);
            method.setCnName(apiDoc.cnName());
            method.setDescribe(apiDoc.describe());
            if (apiDoc.ignore()) {
                //取消文档的生成
                return null;
            }
        }

        RetVal retVal = getRetVal(apiHandler);
        method.setRetVal(retVal);

        for (int i = 0; i < apiHandler.getParamTypes().length; i++) {
            Type type = apiHandler.getParamTypes()[i];
            Parameter parameter = apiHandler.getParameters()[i];
            Param param = new Param();
            param.setType(type.getTypeName());
            param.setName(parameter.getName());
            param.setProperties(getProperties(type));

            if (parameter.isAnnotationPresent(OpenApiDoc.class)) {
                OpenApiDoc apiDoc = parameter.getAnnotation(OpenApiDoc.class);
                param.setCnName(apiDoc.cnName());
                param.setDescribe(apiDoc.describe());
            }
            method.getParams().add(param);
        }
        return method;
    }

    /**
     * 获取返回值信息
     *
     * @param apiHandler openapi处理器
     * @return 返回值信息
     */
    private RetVal getRetVal(ApiHandler apiHandler) {
        java.lang.reflect.Method method = apiHandler.getMethod();
        Type type = method.getGenericReturnType();
        RetVal retVal = new RetVal();
        retVal.setRetType(type.getTypeName());
        retVal.setProperties(getProperties(type));

        if (method.isAnnotationPresent(OpenApiDoc.class)) {
            OpenApiDoc apiDoc = method.getAnnotation(OpenApiDoc.class);
            retVal.setCnName(apiDoc.retCnName());
            retVal.setDescribe(apiDoc.retDescribe());
        }
        return retVal;
    }

    /**
     * 获取指定类型里的属性信息
     *
     * @return 属性信息
     */
    private List<Property> getProperties(Type type) {
        List<Property> properties = null;
        if (type instanceof Class) {
            if (ClassUtil.isBasicType((Class) type)) {
                //基本类型直接返回
                return null;
            }
            for (Class ignoreType : ignoreAddPropertyTypes) {
                //忽略的类型(及其子类)直接返回
                if (ignoreType.isAssignableFrom((Class) type)) {
                    return null;
                }
            }
            if (((Class) type).isArray()) {
                //数组类型则获取元素的属性
                Class elementType = ((Class) type).getComponentType();
                return getProperties(elementType);
            }

            properties = new ArrayList<>();
            Field[] fields = ReflectUtil.getFields((Class) type);
            for (Field field : fields) {
                Property property = new Property();
                property.setName(field.getName());
                property.setType(field.getGenericType().getTypeName());
                //递归设置属性
                property.setProperties(getProperties(field.getGenericType()));

                if (field.isAnnotationPresent(OpenApiDoc.class)) {
                    OpenApiDoc apiDoc = field.getAnnotation(OpenApiDoc.class);
                    property.setCnName(apiDoc.cnName());
                    property.setDescribe(apiDoc.describe());
                    if (apiDoc.ignore()) {
                        //取消文档的生成
                        continue;
                    }
                }

                properties.add(property);
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Class rawType = (Class) parameterizedType.getRawType();
            //判断是集合类（Collection/List/Set等）
            if (Collection.class.isAssignableFrom(rawType)) {
                //取第一个泛型参数(集合元素)的属性
                Type componentType = parameterizedType.getActualTypeArguments()[0];
                return getProperties(componentType);
            }
        }
        return properties;
    }

}
