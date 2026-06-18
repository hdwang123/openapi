package openapi.sdk.common.util;

import openapi.sdk.common.exception.OpenApiException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class BinaryUtilTest {

    @Test
    public void emptyCollectionShouldNotBeTreatedAsBinaryCollection() {
        Assert.assertFalse(TypeUtil.isBinaryCollection(Collections.emptyList()));
        Assert.assertFalse(BinaryUtil.isBinaryParam(null));
    }

    @Test(expected = OpenApiException.class)
    public void malformedBinaryHeaderShouldBeRejected() {
        BinaryUtil.getParamLength(new byte[]{0, 0, 0});
    }

    @Test(expected = OpenApiException.class)
    public void outOfRangeBinaryLengthShouldBeRejected() {
        BinaryUtil.getBinaryDataBytes(new byte[8], 1);
    }
}
