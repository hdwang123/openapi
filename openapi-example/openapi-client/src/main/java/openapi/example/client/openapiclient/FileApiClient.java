package openapi.example.client.openapiclient;

import openapi.client.sdk.annotation.OpenApiMethod;
import openapi.client.sdk.annotation.OpenApiRef;
import openapi.example.client.model.FileInfo;


/**
 * 文件API引用
 *
 * @author wanghuidong
 * 时间： 2022/6/5 20:32
 */
@OpenApiRef("fileApi")
public interface FileApiClient {

    @OpenApiMethod(value = "upload", httpReadTimeout = 60)
    void upload(FileInfo fileInfo);

    @OpenApiMethod(value = "download", httpConnectionTimeout = 5, httpReadTimeout = 20)
    FileInfo download(Long id);
}
