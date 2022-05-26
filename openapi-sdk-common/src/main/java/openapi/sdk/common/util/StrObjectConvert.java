package openapi.sdk.common.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import openapi.sdk.common.model.BusinessException;

import java.util.Collection;
import java.util.List;

/**
 * 字符串对象转换工具类
 *
 * @author wanghuidong
 * @date 2022/5/26 23:02
 */
public class StrObjectConvert {

    /**
     * 字符串转对象
     *
     * @param str       字符串
     * @param classType 对象类型
     * @return 对象
     */
    public static Object strToObj(String str, Class classType) {
        Object ins = null;
        if (classType.getName().equals(Long.class.getName())) {
            ins = Long.parseLong(str);
        } else if (classType.getName().equals(Integer.class.getName())) {
            ins = Integer.parseInt(str);
        } else if (classType.getName().equals(Byte.class.getName())) {
            ins = Byte.parseByte(str);
        } else if (classType.getName().equals(Float.class.getName())) {
            ins = Float.parseFloat(str);
        } else if (classType.getName().equals(Double.class.getName())) {
            ins = Double.parseDouble(str);
        } else if (classType.getName().equals(Boolean.class.getName())) {
            ins = Boolean.parseBoolean(str);
        } else if (classType.getName().equals(Character.class.getName())) {
            ins = str.charAt(0);
        } else if (classType.getName().equals(String.class.getName())) {
            //字符串类型，无需转换
            ins = str;
        } else if (classType.isArray()) {
            throw new BusinessException("不支持数组类型的参数");
        } else if (Collection.class.isAssignableFrom(classType)) {
            if (classType.getName().equals(List.class.getName())) {
                //Collection类型都转换为List(注：不支持set等)
                ins = JSONUtil.toList(str, classType);
            } else {
                throw new BusinessException("不支持除List以外的集合类型参数");
            }
        } else {
            //对象转换
            ins = JSONUtil.toBean(str, classType);
        }
        return ins;
    }

    /**
     * 对象转字符串
     *
     * @param obj       对象
     * @param classType 对象类型
     * @return 字符串
     */
    public static String objToStr(Object obj, Class classType) {
        String str = null;
        if (classType.getName().equals(Long.class.getName())) {
            str = String.valueOf(obj);
        } else if (classType.getName().equals(Integer.class.getName())) {
            str = String.valueOf(obj);
        } else if (classType.getName().equals(Byte.class.getName())) {
            str = String.valueOf(obj);
        } else if (classType.getName().equals(Float.class.getName())) {
            str = String.valueOf(obj);
        } else if (classType.getName().equals(Double.class.getName())) {
            str = String.valueOf(obj);
        } else if (classType.getName().equals(Boolean.class.getName())) {
            str = String.valueOf(obj);
        } else if (classType.getName().equals(Character.class.getName())) {
            str = String.valueOf(obj);
        } else if (classType.getName().equals(String.class.getName())) {
            //字符串类型，无需转换
            str = (String) obj;
        } else {
            //对象转换
            str = JSONUtil.toJsonStr(obj);
        }
        return str;
    }
}
