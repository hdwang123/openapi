package openapi.client.sdk;

import openapi.sdk.common.exception.OpenApiClientException;
import org.junit.Assert;
import org.junit.Test;

public class OpenApiClientBuilderTest {

    @Test(expected = OpenApiClientException.class)
    public void timeoutMustBePositive() {
        new OpenApiClientBuilder("http://localhost", "private", "public", "caller")
                .httpReadTimeout(0)
                .build();
    }

    @Test
    public void toStringShouldNotExposeKeys() {
        OpenApiClient client = new OpenApiClientBuilder(
                "http://localhost", "private-secret", "public-secret", "caller", "api"
        ).build();

        String text = client.toString();
        Assert.assertFalse(text.contains("private-secret"));
        Assert.assertFalse(text.contains("public-secret"));
        Assert.assertTrue(text.contains("******"));
    }
}
