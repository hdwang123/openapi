package openapi.server.sdk.doc;

import cn.hutool.json.JSONUtil;
import openapi.server.sdk.doc.model.Api;
import openapi.server.sdk.doc.model.Method;
import openapi.server.sdk.doc.model.Param;
import openapi.server.sdk.model.ApiHandler;
import openapi.server.sdk.model.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
     * 返回接口文档数据
     *
     * @return 接口文档数据
     */
    @GetMapping("/doc")
    public String doc() {
        Map<String, Api> apiMap = new HashMap<>();
        for (ApiHandler apiHandler : context.getApiHandlers()) {
            String beanName = apiHandler.getBeanName();
            Class apiClass = apiHandler.getBean().getClass();
            Api api = apiMap.get(beanName);
            if (api == null) {
                api = new Api();
                api.setName(apiClass.getSimpleName());
                api.setFullName(apiClass.getName());
                apiMap.put(beanName, api);
            }
            Method method = new Method();
            method.setName(apiHandler.getMethod().getName());
            method.setRetType(getTypeName(apiHandler.getMethod().getGenericReturnType()));

            for (int i = 0; i < apiHandler.getParamTypes().length; i++) {
                Type type = apiHandler.getParamTypes()[i];
                Parameter parameter = apiHandler.getParameters()[i];
                Param param = new Param();
                String typeName = getTypeName(type);
                param.setType(typeName);
                param.setName(parameter.getName());
                method.getParamList().add(param);
            }
            api.getMethodList().add(method);
        }
        Collection<Api> apiList = apiMap.values();
        return JSONUtil.toJsonStr(apiList);
    }

    /**
     * 获取类型名称
     *
     * @param type 类型
     * @return 类型名称
     */
    private String getTypeName(Type type) {
        String typeName = (type instanceof Class) ? ((Class) type).getName() : type.getTypeName();
        if (typeName.startsWith("[J")) {
            typeName = type.getTypeName();
        }
        if (typeName.startsWith("[L")) {
            typeName = typeName.substring(2, typeName.length() - 1) + "[]";
        }
        return typeName;
    }
}
