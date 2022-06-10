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

    private String fileName;

    /**
     * 文件内容
     */
    private byte[] fileBytes;

}
