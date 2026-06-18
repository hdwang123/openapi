package openapi.sdk.common.util;

import openapi.sdk.common.model.InParams;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class CommonUtilTest {

    @Test
    public void getSignContentShouldUseUtf8AndSupportEmptyBody() {
        InParams inParams = new InParams();
        inParams.setUuid("流水号");

        Assert.assertArrayEquals(
                "流水号".getBytes(StandardCharsets.UTF_8),
                CommonUtil.getSignContent(inParams)
        );
    }

    @Test
    public void completeUrlShouldNormalizeSeparator() {
        Assert.assertEquals(
                "http://localhost/openapi",
                CommonUtil.completeUrl("http://localhost/", "/openapi")
        );
    }
}
