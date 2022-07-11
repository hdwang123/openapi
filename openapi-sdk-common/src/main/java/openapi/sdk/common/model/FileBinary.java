package openapi.sdk.common.model;

import lombok.Data;

/**
 * 文件类型
 *
 * @author wanghuidong
 * 时间： 2022/7/11 19:10
 */
@Data
public class FileBinary extends Binary {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

}
