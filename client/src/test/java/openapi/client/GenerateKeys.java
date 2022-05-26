package openapi.client;

import openapi.sdk.common.util.AsymmetricCryUtil;
import org.junit.Test;

/**
 * @author wanghuidong
 * @date 2022/5/26 19:57
 */
public class GenerateKeys {

    @Test
    public void generateKeys() {
        AsymmetricCryUtil.generateRSAKeys();
        AsymmetricCryUtil.generateSM2Keys();
    }
}
