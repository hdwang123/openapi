package openapi.example.client.openapiclient;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.sdk.common.model.FileBinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author wanghuidong
 * 时间： 2022/6/5 20:41
 */
@Slf4j
@Component
public class FileApiTest {

    private static final String dir = FileApiTest.class.getResource("/test").getPath();

    @Autowired
    FileApiClient fileApiClient;


    public void uploadTest() {
        log.info("upload start...");
        long startTime = System.currentTimeMillis();
        File src = new File(dir, "001_big.txt");
        byte[] fileBytes = FileUtil.readBytes(src);

        FileBinary fileBinary = new FileBinary();
        fileBinary.setData(fileBytes);
        fileBinary.setFileName(src.getName());
        fileApiClient.upload(fileBinary);
        log.info("upload end. costTime={}", System.currentTimeMillis() - startTime);
    }


    public void downloadTest() {
        log.info("download start...");
        long startTime = System.currentTimeMillis();
        FileBinary fileBinary = fileApiClient.download(1L);
        File dest = new File(dir, "download/" + fileBinary.getFileName());
        byte[] fileBytes = fileBinary.getData();
        FileUtil.writeBytes(fileBytes, dest);
        log.info("download end. costTime={}", System.currentTimeMillis() - startTime);
    }
}
