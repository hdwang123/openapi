package openapi.sdk.common.util;

/**
 * 类型工具类
 *
 * @author wanghuidong
 * 时间： 2022/7/11 13:34
 */
public class TypeUtil extends cn.hutool.core.util.TypeUtil {

    /**
     * 判断是不是 byte[]类型
     *
     * @param clazz 类型
     * @return 是否是 byte[]类型
     */
    public static boolean isPrimitiveByteArray(Class clazz) {
        if (clazz == null) {
            return false;
        }
        if (clazz.isArray()) {
            Class elementClass = clazz.getComponentType();
            if (byte.class.equals(elementClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是不是 byte[] or Byte[] 类型
     *
     * @param clazz 类型
     * @return 是否是 byte[] or Byte[] 类型
     */
    public static boolean isByteArray(Class clazz) {
        if (clazz == null) {
            return false;
        }
        if (clazz.isArray()) {
            Class elementClass = clazz.getComponentType();
            if (byte.class.equals(elementClass) || Byte.class.equals(elementClass)) {
                return true;
            }
        }
        return false;
    }

}
