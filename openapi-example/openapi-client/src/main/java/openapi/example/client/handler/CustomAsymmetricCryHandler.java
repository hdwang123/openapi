package openapi.example.client.handler;

import openapi.sdk.common.handler.AsymmetricCryHandler;
import openapi.sdk.common.handler.asymmetric.SM2AsymmetricCryHandler;
import org.springframework.stereotype.Component;

@Component("customA")
public class CustomAsymmetricCryHandler implements AsymmetricCryHandler {

    AsymmetricCryHandler asymmetricCryHandler = new SM2AsymmetricCryHandler();

    @Override
    public String sign(String privateKey, String content) {
        return asymmetricCryHandler.sign(privateKey,content);
    }

    @Override
    public String sign(String privateKey, byte[] content) {
        return asymmetricCryHandler.sign(privateKey,content);
    }

    @Override
    public boolean verifySign(String publicKey, String content, String sign) {
        return asymmetricCryHandler.verifySign(publicKey,content,sign);
    }

    @Override
    public boolean verifySign(String publicKey, byte[] content, String sign) {
        return asymmetricCryHandler.verifySign(publicKey,content,sign);
    }

    @Override
    public String cry(String publicKey, String content) {
        return asymmetricCryHandler.cry(publicKey,content);
    }

    @Override
    public byte[] cry(String publicKey, byte[] content) {
        return asymmetricCryHandler.cry(publicKey,content);
    }

    @Override
    public String deCry(String privateKey, String content) {
        return asymmetricCryHandler.deCry(privateKey,content);
    }

    @Override
    public byte[] deCry(String privateKey, byte[] content) {
        return asymmetricCryHandler.deCry(privateKey,content);
    }
}
