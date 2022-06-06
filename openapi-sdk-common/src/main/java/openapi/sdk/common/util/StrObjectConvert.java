package openapi.sdk.common.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import openapi.sdk.common.model.BusinessException;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * 字符串对象转换工具类
 *
 * @author wanghuidong
 */
public class StrObjectConvert {

    /**
     * 字符串转对象
     *
     * @param str  字符串
     * @param type 对象类型
     * @return 对象
     */
    public static Object strToObj(String str, Type type) {
        String classTypeName = type.getTypeName();
        boolean isClassType = false;
        boolean isParameterizedType = false;
        if (type instanceof Class) {
            isClassType = true;
        }
        if (type instanceof ParameterizedType) {
            isParameterizedType = true;
        }
        Object ins = null;
        if (isClassType && ClassUtil.isBasicType((Class) type)) {
            //是基本类型（包括包装类）：Boolean, Character, Byte, Short, Integer, Long, Float, Double, Void
            if (classTypeName.equals(Void.class.getName()) || classTypeName.equals(void.class.getName())) {
                //void类型单独处理
                ins = null;
            } else {
                //使用hutool类型转换工具将字符串转换为对象
                ins = Convert.convert(type, str);
            }
        } else if (classTypeName.equals(String.class.getName())) {
            ins = str;
        } else if (isClassType && ((Class) type).isArray()) {
            Class elementType = ((Class) type).getComponentType();
            //数组类型先转成List
            List list = JSONUtil.toList(str, elementType);
            //构建一个数组对象，然后把List数据赋值给数组
            Object array = Array.newInstance(elementType, list.size());
            for (int i = 0; i < list.size(); i++) {
                Array.set(array, i, list.get(i));
            }
            ins = array;
        } else if (isParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Class rawType = (Class) parameterizedType.getRawType();
            if (Collection.class.isAssignableFrom(rawType)) {
                if (rawType.getName().equals(List.class.getName())) {
                    //Collection类型都转换为List(注：不支持set等、也不支持List里套List)
                    Type componentType = parameterizedType.getActualTypeArguments()[0];
                    if (componentType instanceof Class) {
                        ins = JSONUtil.toList(str, (Class) componentType);
                    } else {
                        throw new BusinessException("不支持泛型类里套泛型的参数");
                    }
                } else {
                    throw new BusinessException("不支持除List以外的集合类型参数");
                }
            } else {
                throw new BusinessException("不支持除List以外的参数化类型参数");
            }
        } else {
            //对象转换
            ins = JSONUtil.toBean(str, type, false);
        }
        return ins;
    }

    /**
     * 对象转字符串
     *
     * @param obj  对象
     * @param type 对象类型
     * @return 字符串
     */
    public static String objToStr(Object obj, Type type) {
        String str = null;
        if (ObjectUtil.isBasicType(obj)) {
            //是基本类型（包括包装类）：Boolean, Character, Byte, Short, Integer, Long, Float, Double, Void
            str = String.valueOf(obj);
        } else if (type.getTypeName().equals(String.class.getName())) {
            //字符串类型，无需转换
            str = (String) obj;
        } else {
            //对象转换
            str = JSONUtil.toJsonStr(obj);
        }
        return str;
    }
}
