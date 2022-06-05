package openapi.example.client.openapiclient;

import cn.hutool.core.io.FileUtil;
import openapi.client.sdk.model.OpenApiMethod;
import openapi.client.sdk.model.OpenApiRef;
import openapi.example.client.model.FileInfo;


/**
 * 文件API引用
 *
 * @author wanghuidong
 * 时间： 2022/6/5 20:32
 */
@OpenApiRef("fileApi")
public interface FileApi {

    @OpenApiMethod("upload")
    void upload(FileInfo fileInfo);

    @OpenApiMethod("download")
    FileInfo download(Long id);
}
