package openapi.example.server;

import openapi.sdk.common.util.AsymmetricCryUtil;
import org.junit.Test;

/**
 * @author wanghuidong
 */
public class GenerateKeys {

    @Test
    public void generateKeys() {
        AsymmetricCryUtil.generateRSAKeys();
        AsymmetricCryUtil.generateSM2Keys();
    }
}
