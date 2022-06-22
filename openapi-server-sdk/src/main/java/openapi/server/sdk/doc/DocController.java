package openapi.server.sdk.doc;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import openapi.sdk.common.constant.Constant;
import openapi.server.sdk.doc.model.*;
import openapi.server.sdk.model.ApiHandler;
import openapi.server.sdk.model.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
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
    private static final List<String> ignoreAddPropertyTypes = Arrays.asList(
            String.class.getName(),
            List.class.getName(),
            Collection.class.getName(),
            Map.class.getName()
    );

    /**
     * 返回接口文档数据
     *
     * @return 接口文档数据
     */
    @GetMapping(value = Constant.DOC_PATH)
    public String doc() {
        Map<String, Api> apiMap = new HashMap<>();
        for (ApiHandler apiHandler : context.getApiHandlers()) {
            String beanName = apiHandler.getBeanName();

            Api api = apiMap.get(beanName);
            if (api == null) {
                Class apiClass = apiHandler.getBean().getClass();

                api = new Api();
                api.setOpenApiName(apiHandler.getOpenApiName());
                api.setName(apiClass.getSimpleName());
                api.setFullName(apiClass.getName());
                apiMap.put(beanName, api);
            }
            Method method = getMethod(apiHandler);
            api.getMethods().add(method);
        }
        Collection<Api> apiList = apiMap.values();
        return JSONUtil.toJsonStr(apiList);
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
        method.setName(apiHandler.getMethod().getName());

        RetVal retVal = getRetVal(apiHandler.getMethod().getGenericReturnType());
        method.setRetVal(retVal);

        for (int i = 0; i < apiHandler.getParamTypes().length; i++) {
            Type type = apiHandler.getParamTypes()[i];
            Parameter parameter = apiHandler.getParameters()[i];
            Param param = new Param();
            param.setType(type.getTypeName());
            param.setName(parameter.getName());
            param.setProperties(getProperties(type));
            method.getParams().add(param);
        }
        return method;
    }

    /**
     * 获取返回值信息
     *
     * @param type 返回值类型
     * @return 返回值信息
     */
    private RetVal getRetVal(Type type) {
        RetVal retVal = new RetVal();
        retVal.setRetType(type.getTypeName());
        retVal.setProperties(getProperties(type));
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
            if (ignoreAddPropertyTypes.contains(type.getTypeName())) {
                //忽略的类型直接返回
                return null;
            }
            if (((Class) type).isArray()) {
                //数组类型直接返回
                return null;
            }

            properties = new ArrayList<>();
            Field[] fields = ReflectUtil.getFields((Class) type);
            for (Field field : fields) {
                Property property = new Property();
                property.setName(field.getName());
                property.setType(field.getGenericType().getTypeName());
                //递归设置属性
                property.setProperties(getProperties(field.getGenericType()));
                properties.add(property);
            }
        }
        return properties;
    }

}
