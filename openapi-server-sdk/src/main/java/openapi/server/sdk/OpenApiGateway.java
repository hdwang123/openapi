package openapi.server.sdk;

import cn.hutool.core.util.*;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.*;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.sdk.common.model.*;
import openapi.sdk.common.util.CommonUtil;
import openapi.sdk.common.util.SymmetricCryUtil;
import openapi.server.sdk.model.ApiHandler;
import openapi.server.sdk.model.OpenApi;
import openapi.server.sdk.model.OpenApiMethod;
import openapi.sdk.common.constant.Constant;
import openapi.sdk.common.util.Base64Util;
import openapi.sdk.common.util.StrObjectConvert;
import openapi.server.sdk.config.OpenApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 对外开放api网关入口
 * <p>
 * 功能
 * 1.负责对外开放接口(基于HTTP对外提供服务)
 * 2.实现接口的参数与返回值的加解密（使用非对称加密：RSA/SM2，或对称加密：AES/SM4）
 * 3.实现接口的验签（服务端会校验客户端的签名，确保调用者身份以及数据不被篡改）
 * 4.实现非对称加密+对称加密联合模式（内容对称加密，对称加密密钥采用非对称加密）
 * <p>
 *
 * @author wanghuidong
 */
@Slf4j
@RestController
public class OpenApiGateway {

    /**
     * 定义api处理器映射
     * key: api_method
     * value: ApiHandler
     */
    private Map<String, ApiHandler> handlerMap = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private OpenApiConfig config;

    /**
     * 日志前缀
     */
    private ThreadLocal<String> logPrefix = new ThreadLocal<>();

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        //初始化所有的openapi处理器
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(OpenApi.class);
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Object bean = entry.getValue();
            Class c = bean.getClass();
            //获取开放api名称
            OpenApi openApi = (OpenApi) c.getAnnotation(OpenApi.class);
            String openApiName = openApi.value();
            //遍历方法
            Method[] methods = c.getDeclaredMethods();
            if (ArrayUtil.isNotEmpty(methods)) {
                for (Method method : methods) {
                    if (method.isAnnotationPresent(OpenApiMethod.class)) {
                        //获取开放api方法名称
                        OpenApiMethod openApiMethod = method.getAnnotation(OpenApiMethod.class);
                        String openApiMethodName = openApiMethod.value();

                        //获取方法参数类型
                        Type[] types = method.getGenericParameterTypes();

                        //保存处理器到Map中
                        String handlerKey = getHandlerKey(openApiName, openApiMethodName);
                        ApiHandler apiHandler = new ApiHandler();
                        apiHandler.setBean(bean);
                        apiHandler.setMethod(method);
                        apiHandler.setParamTypes(types);
                        apiHandler.setOpenApiMethod(openApiMethod);
                        handlerMap.put(handlerKey, apiHandler);
                    }
                }
            }
        }
    }


    /**
     * 调用具体的方法
     *
     * @param inParams 入参
     * @return 出参
     */
    @PostMapping(Constant.OPENAPI_PATH)
    public OutParams callMethod(@RequestBody InParams inParams) {
        //设置日志前缀
        logPrefix.set(String.format("uuid=%s:", inParams.getUuid()));
        OutParams outParams = null;
        try {
            log.debug("{}接收到请求：{}", logPrefix.get(), inParams);

            //获取openapi处理器
            ApiHandler apiHandler = getApiHandler(inParams);

            //获取方法参数
            List<Object> params = getParam(inParams, apiHandler);

            //调用目标方法
            return outParams = doCall(apiHandler, params, inParams);
        } catch (BusinessException be) {
            log.error(logPrefix.get() + be.getMessage());
            return outParams = OutParams.error(be.getMessage());
        } catch (Exception ex) {
            log.error(logPrefix.get() + "系统异常：", ex);
            return outParams = OutParams.error("系统异常");
        } finally {
            outParams.setUuid(inParams.getUuid());
            log.debug(logPrefix.get() + "调用完毕：" + outParams);
        }
    }

    /**
     * 获取openapi处理器
     *
     * @param inParams 入参
     * @return openapi处理器
     */
    private ApiHandler getApiHandler(InParams inParams) {
        String handlerKey = getHandlerKey(inParams.getApi(), inParams.getMethod());
        ApiHandler apiHandler = handlerMap.get(handlerKey);
        if (apiHandler == null) {
            throw new BusinessException("找不到指定的opeapi处理器");
        }
        return apiHandler;
    }

    /**
     * 获取方法参数
     *
     * @param inParams   入参
     * @param apiHandler openapi处理器
     * @return 方法参数
     */
    private List<Object> getParam(InParams inParams, ApiHandler apiHandler) {
        List<Object> params = new ArrayList<>();
        //验签
        verifySign(inParams);

        //解密
        if (StrUtil.isNotBlank(inParams.getBody())) {
            String decryptedBody = decryptBody(inParams, apiHandler);

            try {
                Type[] paramTypes = apiHandler.getParamTypes();
                if (inParams.isMultiParam()) {
                    //多参支持
                    List<Object> list = JSONUtil.toList(decryptedBody, Object.class);
                    if (list.size() != paramTypes.length) {
                        throw new BusinessException("参数个数不匹配");
                    }
                    for (int i = 0; i < list.size(); i++) {
                        String str = StrObjectConvert.objToStr(list.get(i), paramTypes[i]);
                        params.add(StrObjectConvert.strToObj(str, paramTypes[i]));
                    }
                } else {
                    if (paramTypes.length == 1) {
                        //单参
                        Type paramType = paramTypes[0];
                        params.add(StrObjectConvert.strToObj(decryptedBody, paramType));
                    } else {
                        //无参
                    }
                }
            } catch (BusinessException be) {
                throw new BusinessException("入参转换异常：" + be.getMessage());
            } catch (Exception ex) {
                log.error(logPrefix.get() + "入参转换异常", ex);
                throw new BusinessException("入参转换异常");
            }
        }
        return params;
    }

    /**
     * 验签
     *
     * @param inParams 入参
     */
    private void verifySign(InParams inParams) {
        boolean verify;
        String callerPublicKey = config.getCallerPublicKey(inParams.getCallerId());
        String signContent = CommonUtil.getSignContent(inParams);
        if (config.getAsymmetricCry() == AsymmetricCryEnum.RSA) {
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, null, callerPublicKey);
            verify = sign.verify(signContent.getBytes(StandardCharsets.UTF_8), Base64Util.base64ToBytes(inParams.getSign()));
        } else if (config.getAsymmetricCry() == AsymmetricCryEnum.SM2) {
            SM2 sm2 = SmUtil.sm2(null, callerPublicKey);
            verify = sm2.verifyHex(HexUtil.encodeHexStr(signContent), inParams.getSign());
        } else {
            throw new BusinessException("不支持的非对称加密算法");
        }
        if (!verify) {
            throw new BusinessException("验签失败");
        }
    }

    /**
     * 解密入参体
     *
     * @param inParams   入参
     * @param apiHandler openapi处理器
     * @return 解密后的入参体
     */
    private String decryptBody(InParams inParams, ApiHandler apiHandler) {
        String decryptedBody = null;
        try {
            boolean enableSymmetricCry = isEnableSymmetricCry(apiHandler);
            if (enableSymmetricCry) {
                //启用对称加密模式
                log.debug("{}启用对称加密，采用非对称加密{}+对称加密{}模式", logPrefix.get(), config.getAsymmetricCry(), config.getSymmetricCry());
                String key = asymmetricDeCry(inParams.getSymmetricCryKey());
                byte[] keyBytes = Base64Util.base64ToBytes(key);
                decryptedBody = symmetricDeCry(inParams.getBody(), keyBytes);
            } else {
                //仅非对称加密模式
                log.debug("{}未启用对称加密，仅采用非对称加密{}模式", logPrefix.get(), config.getAsymmetricCry());
                decryptedBody = asymmetricDeCry(inParams.getBody());
            }
        } catch (BusinessException be) {
            throw new BusinessException("解密失败：" + be.getMessage());
        } catch (Exception ex) {
            log.error(logPrefix.get() + "解密失败", ex);
            throw new BusinessException("解密失败");
        }
        return decryptedBody;
    }

    /**
     * 判断是否启用对称加密
     *
     * @param apiHandler openapi处理器
     * @return 是否启用对称加密
     */
    private boolean isEnableSymmetricCry(ApiHandler apiHandler) {
        boolean enableSymmetricCry = config.enableSymmetricCry();
        if (StrUtil.isNotBlank(apiHandler.getOpenApiMethod().enableSymmetricCry())) {
            enableSymmetricCry = Boolean.parseBoolean(apiHandler.getOpenApiMethod().enableSymmetricCry());
        }
        return enableSymmetricCry;
    }


    /**
     * 调用目标方法
     *
     * @param apiHandler openapi处理器
     * @param paramList  方法参数
     * @param inParams   openapi入参
     * @return 返回结果
     */
    private OutParams doCall(ApiHandler apiHandler, List<Object> paramList, InParams inParams) {
        try {
            OutParams outParams = new OutParams();
            Object[] params = paramList.stream().toArray();
            log.debug("{}调用API:{},入参：{}", logPrefix.get(), apiHandler, params);
            Object ret = apiHandler.getMethod().invoke(apiHandler.getBean(), params);
            log.debug("{}调用API:{},出参：{}", logPrefix.get(), apiHandler, ret);
            String retStr = StrUtil.EMPTY;
            if (ret != null) {
                retStr = StrObjectConvert.objToStr(ret, ret.getClass());
            }
            if (StrUtil.isNotBlank(retStr)) {
                //判断返回值是否需要加密
                boolean retEncrypt = isRetEncrypt(apiHandler);
                if (retEncrypt) {
                    retStr = encryptRet(inParams, retStr, outParams, apiHandler);
                }
            }
            return outParams.setSuccess(retStr);
        } catch (BusinessException be) {
            throw new BusinessException("调用opeapi处理器异常:" + be.getMessage());
        } catch (Exception ex) {
            log.error(logPrefix.get() + "调用opeapi处理器异常", ex);
            throw new BusinessException("调用opeapi处理器异常");
        }
    }

    /**
     * 判断返回值是否需要加密
     *
     * @param apiHandler openapi处理器
     * @return 是否需要加密
     */
    private boolean isRetEncrypt(ApiHandler apiHandler) {
        boolean retEncrypt = config.retEncrypt();
        if (StrUtil.isNotBlank(apiHandler.getOpenApiMethod().retEncrypt())) {
            retEncrypt = Boolean.parseBoolean(apiHandler.getOpenApiMethod().retEncrypt());
        }
        return retEncrypt;
    }

    /**
     * 加密返回值
     *
     * @param inParams   openapi入参
     * @param retStr     返回值
     * @param outParams  openapi出参
     * @param apiHandler openapi处理器
     * @return 加密后的返回值
     */
    private String encryptRet(InParams inParams, String retStr, OutParams outParams, ApiHandler apiHandler) {
        try {
            //获取调用者公钥
            String callerPublicKey = config.getCallerPublicKey(inParams.getCallerId());

            //加密返回值
            boolean enableSymmetricCry = isEnableSymmetricCry(apiHandler);
            if (enableSymmetricCry) {
                //启用对称加密模式
                log.debug("{}启用对称加密，采用非对称加密{}+对称加密{}模式", logPrefix.get(), config.getAsymmetricCry(), config.getSymmetricCry());
                byte[] keyBytes = SymmetricCryUtil.getKey(config.getSymmetricCry());
                String key = Base64Util.bytesToBase64(keyBytes);
                outParams.setSymmetricCryKey(asymmetricCry(key, callerPublicKey));
                retStr = symmetricCry(retStr, keyBytes);
            } else {
                //仅采用非对称加密模式
                log.debug("{}未启用对称加密，仅采用非对称加密{}模式", logPrefix.get(), config.getAsymmetricCry());
                retStr = asymmetricCry(retStr, callerPublicKey);
            }
        } catch (BusinessException be) {
            throw new BusinessException("返回值加密异常:" + be.getMessage());
        } catch (Exception ex) {
            throw new BusinessException("返回值加密异常", ex);
        }
        return retStr;
    }

    /**
     * 对称加密
     *
     * @param content  内容（普通字符串）
     * @param keyBytes 密钥
     * @return 加密后的内容（Base64字符串）
     */
    private String symmetricCry(String content, byte[] keyBytes) {
        SymmetricCryEnum symmetricCryEnum = config.getSymmetricCry();
        if (symmetricCryEnum == SymmetricCryEnum.AES) {
            AES aes = new AES(keyBytes);
            content = aes.encryptBase64(content);
        } else if (symmetricCryEnum == SymmetricCryEnum.SM4) {
            SM4 sm4 = new SM4(keyBytes);
            content = sm4.encryptBase64(content);
        } else {
            throw new RuntimeException("不支持的对称加密算法");
        }
        return content;
    }

    /**
     * 对称解密
     *
     * @param content  内容 (Hex（16进制）或Base64表示的字符串)
     * @param keyBytes 密钥
     * @return 解密后的内容 (普通字符串)
     */
    private String symmetricDeCry(String content, byte[] keyBytes) {
        SymmetricCryEnum symmetricCryEnum = config.getSymmetricCry();
        if (symmetricCryEnum == SymmetricCryEnum.AES) {
            AES aes = new AES(keyBytes);
            content = aes.decryptStr(content);
        } else if (symmetricCryEnum == SymmetricCryEnum.SM4) {
            SM4 sm4 = new SM4(keyBytes);
            content = sm4.decryptStr(content);
        } else {
            throw new RuntimeException("不支持的对称加密算法");
        }
        return content;
    }

    /**
     * 非对称加密
     *
     * @param content         内容（普通字符串）
     * @param callerPublicKey 调用者公钥
     * @return 加密后的内容（RSA:Base64字符串,SM2:ASCII字符串）
     */
    private String asymmetricCry(String content, String callerPublicKey) {
        if (config.getAsymmetricCry() == AsymmetricCryEnum.RSA) {
            RSA rsa = new RSA(null, callerPublicKey);
            byte[] encrypt = rsa.encrypt(StrUtil.bytes(content, CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
            content = Base64Util.bytesToBase64(encrypt);
        } else if (config.getAsymmetricCry() == AsymmetricCryEnum.SM2) {
            SM2 sm2 = SmUtil.sm2(null, callerPublicKey);
            content = sm2.encryptBcd(content, KeyType.PublicKey);
        } else {
            throw new BusinessException("不支持的非对称加密算法");
        }
        return content;
    }

    /**
     * 非对称解密
     *
     * @param content 内容（RSA:Base64字符串,SM2:ASCII字符串）
     * @return 解密后的内容（普通字符串）
     */
    private String asymmetricDeCry(String content) {
        String decryptedBody;
        String selfPrivateKey = config.getSelfPrivateKey();
        if (config.getAsymmetricCry() == AsymmetricCryEnum.RSA) {
            RSA rsa = new RSA(selfPrivateKey, null);
            byte[] bodyBytes = Base64Util.base64ToBytes(content);
            byte[] decrypt = rsa.decrypt(bodyBytes, KeyType.PrivateKey);
            decryptedBody = new String(decrypt, StandardCharsets.UTF_8);
        } else if (config.getAsymmetricCry() == AsymmetricCryEnum.SM2) {
            SM2 sm2 = SmUtil.sm2(selfPrivateKey, null);
            decryptedBody = StrUtil.utf8Str(sm2.decryptFromBcd(content, KeyType.PrivateKey));
        } else {
            throw new BusinessException("不支持的非对称加密算法");
        }
        return decryptedBody;
    }


    /**
     * 获取api处理器Map的key
     *
     * @param openApiName       开放api名称
     * @param openApiMethodName 开放api方法名称
     * @return 处理器Map的key
     */
    private String getHandlerKey(String openApiName, String openApiMethodName) {
        return openApiName + "_" + openApiMethodName;
    }


}
