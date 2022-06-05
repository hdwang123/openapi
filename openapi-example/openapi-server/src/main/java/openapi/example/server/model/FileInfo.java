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
     * Base64字符串（由于hutool中JSONUtil对byte[]转换存在问题，故改用base64字符串传输）
     */
    private String fileContent;

}
