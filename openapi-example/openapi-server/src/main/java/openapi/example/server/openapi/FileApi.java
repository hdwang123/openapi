package openapi.example.server.openapi;

import cn.hutool.core.io.FileUtil;
import openapi.sdk.common.model.FileBinary;
import openapi.server.sdk.annotation.OpenApi;
import openapi.server.sdk.annotation.OpenApiMethod;

import java.io.File;

/**
 * 文件API
 *
 * @author wanghuidong
 * 时间： 2022/6/5 20:32
 */
@OpenApi("fileApi")
public class FileApi {

    private static final String dir = FileApi.class.getResource("/test").getPath();

    @OpenApiMethod("upload")
    public void upload(FileBinary fileBinary) {
        File dest = new File(dir, "upload/" + fileBinary.getFileName());
        byte[] fileBytes = fileBinary.getData();
        FileUtil.writeBytes(fileBytes, dest);
    }

    @OpenApiMethod("download")
    public FileBinary download(Long id) {
        File src = new File(dir, "002.txt");
        byte[] fileBytes = FileUtil.readBytes(src);
        FileBinary fileBinary = new FileBinary();
        fileBinary.setData(fileBytes);
        fileBinary.setFileName(src.getName());
        return fileBinary;
    }
}
