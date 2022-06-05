package openapi.example.client.openapiclient;

import cn.hutool.core.io.FileUtil;
import openapi.example.client.model.FileInfo;
import openapi.sdk.common.util.Base64Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author wanghuidong
 * 时间： 2022/6/5 20:41
 */
@Component
public class FileApiTest {

    //路径：XXX\openapi-client\target\classes\test\
    private static final String dir = FileApiTest.class.getResource("/test").getPath();

    @Autowired
    FileApi fileApi;


    public void uploadTest() {
        File src = new File(dir, "001.txt");
        byte[] fileBytes = FileUtil.readBytes(src);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileContent(Base64Util.bytesToBase64(fileBytes));
        fileInfo.setFileName(src.getName());
        fileApi.upload(fileInfo);
    }


    public void downloadTest() {
        FileInfo fileInfo = fileApi.download(1L);
        File dest = new File(dir, "download/" + fileInfo.getFileName());
        byte[] fileBytes = Base64Util.base64ToBytes(fileInfo.getFileContent());
        //下载路径：XXX\openapi-client\target\classes\test\download
        FileUtil.writeBytes(fileBytes, dest);
    }
}
