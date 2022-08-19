package openapi.sdk.common.model;

import lombok.Getter;
import lombok.Setter;
import openapi.sdk.common.annotation.OpenApiDoc;

/**
 * 文件类型
 * <p>
 * 用于文件传输，用在方法参数上或方法返回值当中，可以提升文件传输效率
 * </p>
 *
 * @author wanghuidong
 * 时间： 2022/7/11 19:10
 */
@Getter
@Setter
@OpenApiDoc(cnName = "文件对象")
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
}
