package openapi.example.server;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.sdk.common.model.KeyPair;
import openapi.sdk.common.util.AsymmetricCryUtil;
import org.junit.Test;

/**
 * @author wanghuidong
 */
@Slf4j
public class GenerateKeys {

    @Test
    public void generateKeys() {
        KeyPair keyPair = AsymmetricCryUtil.generateRSAKeys();
        log.info("RSA密钥对：\n" + JSONUtil.toJsonStr(keyPair));

        keyPair = AsymmetricCryUtil.generateSM2Keys();
        log.info("SM2密钥对：\n" + JSONUtil.toJsonStr(keyPair));
    }
}
