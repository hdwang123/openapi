package openapi.example.server.openapi;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import openapi.example.server.model.FileInfo;
import openapi.sdk.common.util.Base64Util;
import openapi.server.sdk.model.OpenApi;
import openapi.server.sdk.model.OpenApiMethod;

import java.io.File;

/**
 * 文件API
 *
 * @author wanghuidong
 * 时间： 2022/6/5 20:32
 */
@OpenApi("fileApi")
public class FileApi {

    //路径：XXX\openapi-server\target\classes\test
    private static final String dir = FileApi.class.getResource("/test").getPath();

    @OpenApiMethod("upload")
    public void upload(FileInfo fileInfo) {
        File dest = new File(dir, "upload/" + fileInfo.getFileName());
        byte[] fileBytes = Base64Util.base64ToBytes(fileInfo.getFileContent());
        //上传到路径：XXX\openapi-server\target\classes\test\upload
        FileUtil.writeBytes(fileBytes, dest);
    }

    @OpenApiMethod("download")
    public FileInfo download(Long id) {
        File src = new File(dir, "002.txt");
        byte[] fileBytes = FileUtil.readBytes(src);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileContent(Base64Util.bytesToBase64(fileBytes));
        fileInfo.setFileName(src.getName());
        return fileInfo;
    }
}
