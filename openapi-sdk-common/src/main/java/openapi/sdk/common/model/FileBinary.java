package openapi.sdk.common.model;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.SneakyThrows;
import openapi.sdk.common.annotation.OpenApiDoc;
import openapi.sdk.common.util.CommonUtil;
import openapi.sdk.common.util.TruncateUtil;

/**
 * 文件类型
 * <p>
 * 用于文件传输，用在方法参数上或方法返回值当中，可以提升文件传输效率
 * </p>
 *
 * @author wanghuidong
 * 时间： 2022/7/11 19:10
 */
@OpenApiDoc(cnName = "文件对象")
@Data
public class FileBinary extends Binary {

    /**
     * 文件名
     */
    @OpenApiDoc(cnName = "文件名")
    private String fileName;

    /**
     * 文件类型
     */
    @OpenApiDoc(cnName = "文件类型")
    private String fileType;

    @SneakyThrows
    @Override
    public String toString() {
        FileBinary binary = CommonUtil.cloneInstance(this);
        long length = binary.getLength();
        binary.setDataStr(TruncateUtil.truncate(binary.getData()));
        binary.setData(null);
        binary.setLength(length);
        return JSONUtil.toJsonStr(binary);
    }
}
