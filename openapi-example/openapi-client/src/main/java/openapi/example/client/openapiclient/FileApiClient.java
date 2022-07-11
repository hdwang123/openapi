package openapi.example.client.openapiclient;

import openapi.client.sdk.annotation.OpenApiMethod;
import openapi.client.sdk.annotation.OpenApiRef;
import openapi.sdk.common.model.FileBinary;


/**
 * 文件API引用
 *
 * @author wanghuidong
 * 时间： 2022/6/5 20:32
 */
@OpenApiRef("fileApi")
public interface FileApiClient {

    @OpenApiMethod(value = "upload", httpReadTimeout = 60)
    void upload(Long id, FileBinary file1, FileBinary file2);

    @OpenApiMethod(value = "download", httpConnectionTimeout = 5, httpReadTimeout = 600)
    FileBinary download(Long id);
}
