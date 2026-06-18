package openapi.sdk.common.handler;

import openapi.sdk.common.enums.AsymmetricCryAlgo;
import openapi.sdk.common.enums.SymmetricCryAlgo;
import openapi.sdk.common.handler.asymmetric.RSAAsymmetricCryHandler;
import openapi.sdk.common.handler.asymmetric.SM2AsymmetricCryHandler;
import openapi.sdk.common.handler.symmetric.AESSymmetricCryHandler;
import openapi.sdk.common.handler.symmetric.SM4SymmetricCryHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加密处理器集合
 */
public final class CryHandlerMap {

    /**
     * 定义所有的非对称加密处理器
     */
    private static final Map<String, AsymmetricCryHandler> ASYMMETRIC_HANDLERS = new ConcurrentHashMap<>();

    /**
     * 定义所有的对称加密处理器
     */
    private static final Map<String, SymmetricCryHandler> SYMMETRIC_HANDLERS = new ConcurrentHashMap<>();

    static {
        ASYMMETRIC_HANDLERS.put(AsymmetricCryAlgo.RSA, new RSAAsymmetricCryHandler());
        ASYMMETRIC_HANDLERS.put(AsymmetricCryAlgo.SM2, new SM2AsymmetricCryHandler());
        SYMMETRIC_HANDLERS.put(SymmetricCryAlgo.AES, new AESSymmetricCryHandler());
        SYMMETRIC_HANDLERS.put(SymmetricCryAlgo.SM4, new SM4SymmetricCryHandler());
    }

    private CryHandlerMap() {
    }

    /**
     * 增加非对称加密处理器
     *
     * @param algoName 算法名称
     * @param handler  加密处理器
     */
    public static void addAsymmetricCryHandler(String algoName, AsymmetricCryHandler handler) {
        if (algoName != null && handler != null) {
            ASYMMETRIC_HANDLERS.put(algoName, handler);
        }
    }

    /**
     * 获取非对称加密处理器
     *
     * @param algoName 算法名称
     * @return 加密处理器
     */
    public static AsymmetricCryHandler getAsymmetricCryHandler(String algoName) {
        return ASYMMETRIC_HANDLERS.get(algoName);
    }

    /**
     * 增加对称加密处理器
     *
     * @param algoName 算法名称
     * @param handler  加密处理器
     */
    public static void addSymmetricCryHandler(String algoName, SymmetricCryHandler handler) {
        if (algoName != null && handler != null) {
            SYMMETRIC_HANDLERS.put(algoName, handler);
        }
    }

    /**
     * 获取对称加密处理器
     *
     * @param algoName 算法名称
     * @return 加密处理器
     */
    public static SymmetricCryHandler getSymmetricCryHandler(String algoName) {
        return SYMMETRIC_HANDLERS.get(algoName);
    }


}
