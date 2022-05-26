package openapi.server.sdk;

import cn.hutool.core.util.*;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.*;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.sdk.common.constant.Constant;
import openapi.sdk.common.model.AsymmetricCryEnum;
import openapi.sdk.common.model.BusinessException;
import openapi.sdk.common.model.InParams;
import openapi.sdk.common.model.OutParams;
import openapi.sdk.common.util.Base64Util;
import openapi.sdk.common.util.StrObjectConvert;
import openapi.server.sdk.config.OpenApiConfig;
import openapi.server.sdk.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 对外开放api网关入口
 * <p>
 * 功能
 * 1.负责对外开放接口
 * 2.实现接口的参数与返回值的加解密
 * 3.实现接口的验签
 * <p>
 *
 * @author wanghuidong
 * @date 2022/5/26 14:40
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

                        //获取方法参数
                        Class[] classes = method.getParameterTypes();

                        //保存处理器到Map中
                        String handlerKey = getHandlerKey(openApiName, openApiMethodName);
                        ApiHandler apiHandler = new ApiHandler();
                        apiHandler.setBean(bean);
                        apiHandler.setMethod(method);
                        apiHandler.setParamClasses(classes);
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
        OutParams outParams = null;
        try {
            log.info("接收到请求：" + inParams);

            //获取openapi处理器
            ApiHandler apiHandler = getApiHandler(inParams);

            //获取方法参数
            Object param = getParam(inParams, apiHandler);

            //调用目标方法
            return outParams = doCall(apiHandler, param, inParams);
        } catch (BusinessException be) {
            log.error(be.getMessage());
            return outParams = OutParams.error(be.getMessage());
        } catch (Exception ex) {
            log.error("系统异常：", ex);
            return outParams = OutParams.error("系统异常");
        } finally {
            outParams.setUuid(inParams.getUuid());
            log.info("调用完毕：" + outParams);
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
    private Object getParam(InParams inParams, ApiHandler apiHandler) {
        Object param = null;
        if (StrUtil.isNotBlank(inParams.getBody())) {
            //验签
            boolean verify = false;
            String callerPublicKey = config.getCallerPublicKey(inParams.getCallerId());
            if (config.getAsymmetricCry() == AsymmetricCryEnum.RSA) {
                Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, null, callerPublicKey);
                verify = sign.verify(inParams.getBody().getBytes(StandardCharsets.UTF_8), Base64Util.base64ToBytes(inParams.getSign()));
            } else if (config.getAsymmetricCry() == AsymmetricCryEnum.SM2) {
                SM2 sm2 = SmUtil.sm2(null, callerPublicKey);
                verify = sm2.verifyHex(HexUtil.encodeHexStr(inParams.getBody()), inParams.getSign());
            } else {
                throw new BusinessException("不支持的非对称加密算法");
            }
            if (!verify) {
                throw new BusinessException("验签失败");
            }

            //解密
            String decryptedBody = null;
            try {
                String selfPrivateKey = config.getSelfPrivateKey();
                if (config.getAsymmetricCry() == AsymmetricCryEnum.RSA) {
                    RSA rsa = new RSA(selfPrivateKey, null);
                    byte[] bodyBytes = Base64Util.base64ToBytes(inParams.getBody());
                    byte[] decrypt = rsa.decrypt(bodyBytes, KeyType.PrivateKey);
                    decryptedBody = new String(decrypt, StandardCharsets.UTF_8);
                } else if (config.getAsymmetricCry() == AsymmetricCryEnum.SM2) {
                    SM2 sm2 = SmUtil.sm2(selfPrivateKey, null);
                    decryptedBody = StrUtil.utf8Str(sm2.decryptFromBcd(inParams.getBody(), KeyType.PrivateKey));
                } else {
                    throw new BusinessException("不支持的非对称加密算法");
                }
            } catch (Exception ex) {
                log.error("解密失败", ex);
                throw new BusinessException("解密失败");
            }

            try {
                //当前仅支持一个参数的方法
                Class paramClass = apiHandler.getParamClasses()[0];
                param = StrObjectConvert.strToObj(decryptedBody, paramClass);
            } catch (Exception ex) {
                log.error("入参转换异常", ex);
                throw new BusinessException("入参转换异常");
            }
        }
        return param;
    }

    /**
     * 调用目标方法
     *
     * @param apiHandler openapi处理器
     * @param param      方法参数
     * @param inParams   openapi入参
     * @return 返回结果
     */
    private OutParams doCall(ApiHandler apiHandler, Object param, InParams inParams) {
        try {
            Object ret = apiHandler.getMethod().invoke(apiHandler.getBean(), param);
            String retStr = StrObjectConvert.objToStr(ret, ret.getClass());
            //返回值需要加密
            if (config.retEncrypt()) {
                retStr = encryptRet(inParams, retStr);
            }
            return OutParams.success(retStr);
        } catch (Exception ex) {
            log.error("调用opeapi处理器异常", ex);
            throw new BusinessException("调用opeapi处理器异常");
        }
    }

    /**
     * 加密返回值
     *
     * @param inParams openapi入参
     * @param retStr   返回值
     * @return 加密后的返回值
     */
    private String encryptRet(InParams inParams, String retStr) {
        try {
            String callerPublicKey = config.getCallerPublicKey(inParams.getCallerId());
            if (config.getAsymmetricCry() == AsymmetricCryEnum.RSA) {
                RSA rsa = new RSA(null, callerPublicKey);
                byte[] encrypt = rsa.encrypt(StrUtil.bytes(retStr, CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
                retStr = Base64Util.bytesToBase64(encrypt);
            } else if (config.getAsymmetricCry() == AsymmetricCryEnum.SM2) {
                SM2 sm2 = SmUtil.sm2(null, callerPublicKey);
                retStr = sm2.encryptBcd(retStr, KeyType.PublicKey);
            } else {
                throw new BusinessException("不支持的非对称加密算法");
            }
        } catch (Exception ex) {
            throw new BusinessException("返回值加密异常", ex);
        }
        return retStr;
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
