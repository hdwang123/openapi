package openapi.sdk.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import openapi.sdk.common.constant.Constant;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 截短工具类，针对大对象日志打印前提供截短功能
 *
 * @author wanghuidong
 * 时间： 2022/7/8 21:31
 */
public class TruncateUtil {

    /**
     * 空数组
     */
    private static final String emptyArray = "[]";

    /**
     * 截短字符串
     *
     * @param src 原始字符串
     * @return 截短后的字符串
     */
    public static String truncate(String src) {
        if (src != null && src.length() > Constant.MAX_LOG_LENGTH) {
            return src.substring(0, Constant.OVER_MAX_LOG_KEEP_LENGTH) + "(truncated...)";
        }
        return src;
    }

    /**
     * 截短对象字符串
     *
     * @param obj 对象
     * @return 截短后的对象字符串
     */
    public static String truncate(Object obj) {
        if (obj != null) {
            return truncate(obj.toString());
        }
        return null;
    }

    /**
     * 截短字节数组字符串
     *
     * @param bytes 字节数组
     * @return 截短后的字符串
     */
    public static String truncate(byte[] bytes) {
        if (ArrayUtil.isNotEmpty(bytes)) {
            //16进制字符表示，易读性比base64好, 一个字节->两个字符
            if (bytes.length > Constant.MAX_LOG_LENGTH / 2) {
                byte[] subBytes = ArrayUtil.sub(bytes, 0, Constant.OVER_MAX_LOG_KEEP_LENGTH / 2);
                return Convert.toHex(subBytes) + "(truncated...)";
            }
            return Convert.toHex(bytes);
        }
        return null;
    }


    /**
     * 截短数组字符串
     *
     * @param array 数组
     * @return 截短后的数组字符串
     */
    public static String truncate(Object[] array) {
        if (ArrayUtil.isNotEmpty(array)) {
            String[] strArray = new String[array.length];
            for (int i = 0; i < array.length; i++) {
                strArray[i] = truncate(array[i]);
            }
            return strArray.toString();
        }
        return emptyArray;
    }

    /**
     * 截短集合字符串
     *
     * @param coll 集合
     * @return 截短后的集合字符串
     */
    public static String truncate(Collection coll) {
        if (CollUtil.isNotEmpty(coll)) {
            List list = new LinkedList();
            Iterator iterator = coll.iterator();
            while (iterator.hasNext()) {
                list.add(truncate(iterator.next()));
            }
            return list.toString();
        }
        return emptyArray;
    }
}
