package openapi.example.client.handler;

import openapi.sdk.common.handler.SymmetricCryHandler;
import openapi.sdk.common.handler.symmetric.SM4SymmetricCryHandler;
import org.springframework.stereotype.Component;

@Component("customS")
public class CustomSymmetricCryHandler implements SymmetricCryHandler {

    SymmetricCryHandler symmetricCryHandler = new SM4SymmetricCryHandler();

    @Override
    public byte[] generateKey() {
        return symmetricCryHandler.generateKey();
    }

    @Override
    public String cry(String content, byte[] keyBytes) {
        return symmetricCryHandler.cry(content, keyBytes);
    }

    @Override
    public byte[] cry(byte[] content, byte[] keyBytes) {
        return symmetricCryHandler.cry(content, keyBytes);
    }

    @Override
    public String deCry(String content, byte[] keyBytes) {
        return symmetricCryHandler.deCry(content, keyBytes);
    }

    @Override
    public byte[] deCry(byte[] content, byte[] keyBytes) {
        return symmetricCryHandler.deCry(content, keyBytes);
    }
}
