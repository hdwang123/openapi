package openapi.example.server.model;

import lombok.Data;

/**
 * 文件信息
 *
 * @author wanghuidong
 * 时间： 2022/6/5 20:34
 */
@Data
public class FileInfo {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件内容base64表示
     */
    private String fileBase64;

}
