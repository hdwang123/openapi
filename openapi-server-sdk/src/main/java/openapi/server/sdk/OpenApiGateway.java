package openapi.server.sdk;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.*;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.sdk.common.handler.asymmetric.AsymmetricCryHandler;
import openapi.sdk.common.handler.symmetric.SymmetricCryHandler;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
    private Map<String, ApiHandler> apiHandlerMap = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private OpenApiConfig config;

    /**
     * 日志前缀
     */
    private ThreadLocal<String> logPrefix = new ThreadLocal<>();

    private AsymmetricCryEnum asymmetricCryEnum;
    private String selfPrivateKey;
    private boolean retEncrypt;
    private boolean enableSymmetricCry;
    private SymmetricCryEnum symmetricCryEnum;

    /**
     * 非对称加密处理器
     */
    private AsymmetricCryHandler asymmetricCryHandler;

    /**
     * 对称加密处理器
     */
    private SymmetricCryHandler symmetricCryHandler;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        //初始化配置信息
        initConfig();

        //初始化所有的openapi处理器
        initApiHandlers();

        //打印基本信息
        String handlersStr = getHandlersStr();
        log.debug("OpenApiGateway Init: \nSelfPrivateKey:{},\nAsymmetricCry:{},\nretEncrypt:{},\nenableSymmetricCry:{},\nSymmetricCry:{},\nApiHandlers:\n{}",
                selfPrivateKey, asymmetricCryEnum, retEncrypt, enableSymmetricCry, symmetricCryEnum, handlersStr);
        if (enableSymmetricCry) {
            log.debug("启用对称加密，采用非对称加密{}+对称加密{}模式", asymmetricCryEnum, symmetricCryEnum);
        } else {
            log.debug("未启用对称加密，仅采用非对称加密{}模式", asymmetricCryEnum);
        }
    }

    private String getHandlersStr() {
        Collection<ApiHandler> handlers = apiHandlerMap.values();
        String handlersStr = StrUtil.EMPTY;
        if (CollUtil.isEmpty(handlers)) {
            handlersStr = "未找到ApiHandler,请确保注解@OpenApi所声明的bean可被spring扫描到";
        } else {
            for (ApiHandler handler : handlers) {
                handlersStr += handler + "\n";
            }
        }
        return handlersStr;
    }

    /**
     * 初始化配置信息
     */
    private void initConfig() {
        this.selfPrivateKey = config.getSelfPrivateKey();
        this.asymmetricCryEnum = config.getAsymmetricCry();
        this.retEncrypt = config.retEncrypt();
        this.enableSymmetricCry = config.enableSymmetricCry();
        this.symmetricCryEnum = config.getSymmetricCry();
        this.asymmetricCryHandler = AsymmetricCryHandler.handlerMap.get(this.asymmetricCryEnum);
        this.symmetricCryHandler = SymmetricCryHandler.handlerMap.get(this.symmetricCryEnum);
    }

    /**
     * 初始化所有的openapi处理器
     */
    private void initApiHandlers() {
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
                        apiHandlerMap.put(handlerKey, apiHandler);
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
    @PostMapping(value = Constant.OPENAPI_PATH, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
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
            if (outParams == null) {
                outParams = OutParams.error("系统异常");
            }
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
        ApiHandler apiHandler = apiHandlerMap.get(handlerKey);
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
                throw new BusinessException("入参转换异常:" + ex.getMessage());
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
        String callerPublicKey = config.getCallerPublicKey(inParams.getCallerId());
        log.debug("{}caller({}) publicKey:{}", logPrefix.get(), inParams.getCallerId(), callerPublicKey);
        String signContent = CommonUtil.getSignContent(inParams);
        boolean verify = this.asymmetricCryHandler.verifySign(callerPublicKey, signContent, inParams.getSign());
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
                String key = this.asymmetricCryHandler.deCry(selfPrivateKey, inParams.getSymmetricCryKey());
                byte[] keyBytes = Base64Util.base64ToBytes(key);
                decryptedBody = this.symmetricCryHandler.deCry(inParams.getBody(), keyBytes);
            } else {
                //仅非对称加密模式
                decryptedBody = this.asymmetricCryHandler.deCry(selfPrivateKey, inParams.getBody());
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
        boolean enableSymmetricCry = this.enableSymmetricCry;
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
        boolean retEncrypt = this.retEncrypt;
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
            log.debug("{}caller({}) publicKey:{}", logPrefix.get(), inParams.getCallerId(), callerPublicKey);

            //加密返回值
            boolean enableSymmetricCry = isEnableSymmetricCry(apiHandler);
            if (enableSymmetricCry) {
                //启用对称加密模式
                byte[] keyBytes = SymmetricCryUtil.getKey(symmetricCryEnum);
                String key = Base64Util.bytesToBase64(keyBytes);
                String cryKey = this.asymmetricCryHandler.cry(callerPublicKey, key);
                outParams.setSymmetricCryKey(cryKey);
                retStr = this.symmetricCryHandler.cry(retStr, keyBytes);
            } else {
                //仅采用非对称加密模式
                retStr = this.asymmetricCryHandler.cry(callerPublicKey, retStr);
            }
        } catch (BusinessException be) {
            throw new BusinessException("返回值加密异常:" + be.getMessage());
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
