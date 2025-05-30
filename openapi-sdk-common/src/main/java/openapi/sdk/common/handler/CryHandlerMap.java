package openapi.sdk.common.handler;

import openapi.sdk.common.enums.AsymmetricCryAlgo;
import openapi.sdk.common.enums.SymmetricCryAlgo;
import openapi.sdk.common.handler.asymmetric.RSAAsymmetricCryHandler;
import openapi.sdk.common.handler.asymmetric.SM2AsymmetricCryHandler;
import openapi.sdk.common.handler.symmetric.AESSymmetricCryHandler;
import openapi.sdk.common.handler.symmetric.SM4SymmetricCryHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 加密处理器集合
 */
public class CryHandlerMap {

    /**
     * 定义所有的非对称加密处理器
     */
    private static Map<String, AsymmetricCryHandler> asymmetricMap = new HashMap<String, AsymmetricCryHandler>() {{
        put(AsymmetricCryAlgo.RSA, new RSAAsymmetricCryHandler());
        put(AsymmetricCryAlgo.SM2, new SM2AsymmetricCryHandler());
    }};

    /**
     * 定义所有的对称加密处理器
     */
    private static Map<String, SymmetricCryHandler> symmetricMap = new HashMap<String, SymmetricCryHandler>() {{
        put(SymmetricCryAlgo.AES, new AESSymmetricCryHandler());
        put(SymmetricCryAlgo.SM4, new SM4SymmetricCryHandler());
    }};

    /**
     * 增加非对称加密处理器
     *
     * @param algoName 算法名称
     * @param handler  加密处理器
     */
    public static void addAsymmetricCryHandler(String algoName, AsymmetricCryHandler handler) {
        asymmetricMap.put(algoName, handler);
    }

    /**
     * 获取非对称加密处理器
     *
     * @param algoName 算法名称
     * @return 加密处理器
     */
    public static AsymmetricCryHandler getAsymmetricCryHandler(String algoName) {
        return asymmetricMap.get(algoName);
    }

    /**
     * 增加对称加密处理器
     *
     * @param algoName 算法名称
     * @param handler  加密处理器
     */
    public static void addSymmetricCryHandler(String algoName, SymmetricCryHandler handler) {
        symmetricMap.put(algoName, handler);
    }

    /**
     * 获取对称加密处理器
     *
     * @param algoName 算法名称
     * @return 加密处理器
     */
    public static SymmetricCryHandler getSymmetricCryHandler(String algoName) {
        return symmetricMap.get(algoName);
    }


}
