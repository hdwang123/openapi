package openapi.sdk.common.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TruncateUtilTest {

    @Test
    public void arrayAndCollectionShouldHandleNullElements() {
        Assert.assertEquals("[null, value]", TruncateUtil.truncate(new Object[]{null, "value"}));
        Assert.assertEquals("[null, value]", TruncateUtil.truncate(Arrays.asList(null, "value")));
    }
}
