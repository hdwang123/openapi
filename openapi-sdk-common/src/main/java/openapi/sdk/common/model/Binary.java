package openapi.sdk.common.model;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.SneakyThrows;
import openapi.sdk.common.util.TruncateUtil;

/**
 * 二进制数据
 * <p>
 * 此类型用于提升二进制数据的传输效率，目前仅支持将此类（或其子类）放置在方法参数上或作为返回值方可提升传输效率
 * </p>
 * <pre>
 * 如果方法参数中含有此类型参数，则传输的数据格式如下：
 * ParamsSize 4字节(存储参数长度)
 * PramsData  参数数据
 * BinaryCount 1字节（二进制数据个数）
 * BinarySize 8字节（二进制数据长度）
 * BinaryData 二进制数据
 * ......
 * </pre>
 *
 * @author wanghuidong
 * 时间： 2022/7/11 19:08
 */
@Data
public class Binary {

    /**
     * 数据长度（多少字节）
     */
    private long length;

    /**
     * 数据
     */
    private byte[] data;

    /**
     * 数据字符串表示（用于打日志）
     */
    private String dataStr;

    /**
     * 设置数据
     *
     * @param data 数据
     */
    public void setData(byte[] data) {
        this.data = data;
        this.length = data == null ? 0 : data.length;
    }

    @SneakyThrows
    @Override
    public String toString() {
        Binary binary = this.getClass().newInstance();
        BeanUtil.copyProperties(this, binary);
        binary.setDataStr(TruncateUtil.truncate(binary.getData()));
        binary.setData(null);
        return JSONUtil.toJsonStr(binary);
    }
}
