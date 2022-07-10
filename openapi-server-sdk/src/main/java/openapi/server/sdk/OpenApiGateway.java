package openapi.server.sdk;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.sdk.common.constant.Constant;
import openapi.sdk.common.constant.Header;
import openapi.sdk.common.enums.AsymmetricCryEnum;
import openapi.sdk.common.enums.CryModeEnum;
import openapi.sdk.common.enums.SymmetricCryEnum;
import openapi.sdk.common.exception.OpenApiServerException;
import openapi.sdk.common.handler.AsymmetricCryHandler;
import openapi.sdk.common.handler.SymmetricCryHandler;
import openapi.sdk.common.model.InParams;
import openapi.sdk.common.model.OutParams;
import openapi.sdk.common.util.*;
import openapi.server.sdk.annotation.OpenApi;
import openapi.server.sdk.annotation.OpenApiMethod;
import openapi.server.sdk.config.OpenApiConfig;
import openapi.server.sdk.model.ApiHandler;
import openapi.server.sdk.model.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
    private final Map<String, ApiHandler> apiHandlerMap = new HashMap<>();

    @Autowired
    private Context context;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private OpenApiConfig config;

    /**
     * 日志前缀
     */
    private final ThreadLocal<String> logPrefix = new ThreadLocal<>();

    private AsymmetricCryEnum asymmetricCryEnum;
    private String selfPrivateKey;
    private boolean retEncrypt;
    private CryModeEnum cryModeEnum;
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
        if (log.isDebugEnabled()) {
            String handlersStr = getHandlersStr();
            log.debug("OpenApiGateway Init: \nSelfPrivateKey:{},\nAsymmetricCry:{},\nretEncrypt:{},\ncryModeEnum:{},\nSymmetricCry:{},\nApiHandlers:\n{}",
                    selfPrivateKey, asymmetricCryEnum, retEncrypt, cryModeEnum, symmetricCryEnum, handlersStr);
            logCryMode(this.cryModeEnum);
        }
        //重要日志改成info级别
        log.info("OpenApiGateway init succeed. path={}", Constant.OPENAPI_PATH);
    }

    /**
     * 获取所有的openapi处理器的字符串表示
     *
     * @return openapi处理器列表
     */
    private String getHandlersStr() {
        Collection<ApiHandler> handlers = apiHandlerMap.values();
        StringBuilder handlersStr = new StringBuilder(StrUtil.EMPTY);
        if (CollUtil.isEmpty(handlers)) {
            handlersStr = new StringBuilder("未找到ApiHandler,请确保注解@OpenApi所声明的bean可被spring扫描到");
        } else {
            for (ApiHandler handler : handlers) {
                handlersStr.append(handler).append("\n");
            }
        }
        return handlersStr.toString();
    }

    /**
     * 初始化配置信息
     */
    private void initConfig() {
        this.selfPrivateKey = config.getSelfPrivateKey();
        this.asymmetricCryEnum = config.getAsymmetricCry();
        this.retEncrypt = config.retEncrypt();
        this.cryModeEnum = config.getCryMode();
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
            String beanName = entry.getKey();
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

                        //获取方法参数
                        Parameter[] parameters = method.getParameters();

                        //保存处理器到Map中
                        String handlerKey = getHandlerKey(openApiName, openApiMethodName);
                        ApiHandler apiHandler = new ApiHandler();
                        apiHandler.setOpenApiName(openApiName);
                        apiHandler.setOpenApiMethodName(openApiMethodName);
                        apiHandler.setBeanName(beanName);
                        apiHandler.setBean(bean);
                        apiHandler.setMethod(method);
                        apiHandler.setParamTypes(types);
                        apiHandler.setParameters(parameters);
                        apiHandler.setOpenApiMethod(openApiMethod);
                        apiHandlerMap.put(handlerKey, apiHandler);
                    }
                }
            }
        }
        //将openapi处理器保存到上下文对象中去
        context.setApiHandlers(this.apiHandlerMap.values());
    }


    /**
     * 调用具体的方法
     *
     * @param request  请求对象
     * @param response 响应对象
     */
    @PostMapping(value = Constant.OPENAPI_PATH,
            consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE}
    )
    public void callMethod(HttpServletRequest request, HttpServletResponse response) {
        OutParams outParams = null;
        InParams inParams = null;
        try {
            //获取入参
            inParams = getInParams(request);
            log.debug("{}接收到请求：{}", logPrefix.get(), inParams);

            //获取openapi处理器
            ApiHandler apiHandler = getApiHandler(inParams);

            //获取方法参数
            List<Object> params = getParam(inParams, apiHandler);

            //调用目标方法
            outParams = doCall(apiHandler, params, inParams);
        } catch (OpenApiServerException be) {
            log.error(logPrefix.get() + be.getMessage());
            outParams = OutParams.error(be.getMessage());
        } catch (Exception ex) {
            log.error(logPrefix.get() + "系统异常：", ex);
            outParams = OutParams.error("系统异常");
        } finally {
            if (outParams == null) {
                outParams = OutParams.error("系统异常");
            }
            outParams.setUuid(inParams != null ? inParams.getUuid() : null);

            //写返回值到响应
            writeOutParams(response, outParams);

            log.debug(logPrefix.get() + "调用完毕：" + outParams);
        }
    }

    /**
     * 获取入参
     *
     * @param request 请求对象
     * @return 入参
     */
    private InParams getInParams(HttpServletRequest request) {
        InParams inParams = new InParams();
        try {
            //获取请求头
            inParams.setUuid(request.getHeader(Header.Request.UUID));

            //设置日志前缀
            logPrefix.set(String.format("uuid=%s:", inParams.getUuid()));

            inParams.setCallerId(request.getHeader(Header.Request.CALLER_ID));
            inParams.setApi(request.getHeader(Header.Request.API));
            inParams.setMethod(request.getHeader(Header.Request.METHOD));
            inParams.setSign(request.getHeader(Header.Request.SIGN));
            inParams.setSymmetricCryKey(request.getHeader(Header.Request.SYMMETRIC_CRY_KEY));
            inParams.setMultiParam(Boolean.parseBoolean(request.getHeader(Header.Request.MULTI_PARAM)));

            //获取请求体
            InputStream inputStream = request.getInputStream();
            byte[] inputBytes = IoUtil.readBytes(inputStream);
            inParams.setBodyBytes(inputBytes);
        } catch (Exception ex) {
            log.error(logPrefix.get() + "从请求流读取数据异常", ex);
            throw new OpenApiServerException("从请求流读取数据异常:" + ex.getMessage());
        }
        return inParams;
    }

    /**
     * 写返回值到响应
     *
     * @param response  HTTP响应对象
     * @param outParams 返回值
     */
    private void writeOutParams(HttpServletResponse response, OutParams outParams) {
        try {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.addHeader(Header.Response.UUID, outParams.getUuid());
            response.addHeader(Header.Response.CODE, String.valueOf(outParams.getCode()));
            response.addHeader(Header.Response.MESSAGE, outParams.getMessage());
            response.addHeader(Header.Response.SYMMETRIC_CRY_KEY, outParams.getSymmetricCryKey());
            if (ArrayUtil.isEmpty(outParams.getDataBytes())) {
                return;
            }
            IoUtil.write(response.getOutputStream(), true, outParams.getDataBytes());
        } catch (Exception ex) {
            log.error(logPrefix.get() + "写返回值到响应流异常", ex);
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
            throw new OpenApiServerException("找不到指定的opeapi处理器");
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
        if (ArrayUtil.isNotEmpty(inParams.getBodyBytes())) {
            String decryptedBody = decryptBody(inParams, apiHandler);

            try {
                Type[] paramTypes = apiHandler.getParamTypes();
                if (inParams.isMultiParam()) {
                    //多参支持
                    List<String> list = JSONUtil.toList(decryptedBody, String.class);
                    if (list.size() != paramTypes.length) {
                        throw new OpenApiServerException("参数个数不匹配");
                    }
                    for (int i = 0; i < list.size(); i++) {
                        params.add(StrObjectConvert.strToObj(list.get(i), paramTypes[i]));
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
            } catch (OpenApiServerException be) {
                throw new OpenApiServerException("入参转换异常：" + be.getMessage());
            } catch (Exception ex) {
                log.error(logPrefix.get() + "入参转换异常", ex);
                throw new OpenApiServerException("入参转换异常:" + ex.getMessage());
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
        long startTime = System.nanoTime();
        String callerPublicKey = config.getCallerPublicKey(inParams.getCallerId());
        log.debug("{}caller({}) publicKey:{}", logPrefix.get(), inParams.getCallerId(), callerPublicKey);
        byte[] signContent = CommonUtil.getSignContent(inParams);
        boolean verify = this.asymmetricCryHandler.verifySign(callerPublicKey, signContent, inParams.getSign());
        this.logCostTime("验签", startTime);
        if (!verify) {
            throw new OpenApiServerException("验签失败");
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
        long startTime = System.nanoTime();
        byte[] bodyBytes = inParams.getBodyBytes();
        try {
            CryModeEnum cryModeEnum = this.getCryModeEnum(apiHandler);
            if (cryModeEnum == CryModeEnum.SymmetricCry) {
                String key = this.asymmetricCryHandler.deCry(selfPrivateKey, inParams.getSymmetricCryKey());
                byte[] keyBytes = Base64Util.base64ToBytes(key);
                bodyBytes = this.symmetricCryHandler.deCry(bodyBytes, keyBytes);
            } else if (cryModeEnum == CryModeEnum.AsymmetricCry) {
                bodyBytes = this.asymmetricCryHandler.deCry(selfPrivateKey, bodyBytes);
            } else {
                //不加密模式CryModeEnum.NONE
            }
        } catch (OpenApiServerException be) {
            throw new OpenApiServerException("解密失败：" + be.getMessage());
        } catch (Exception ex) {
            log.error(logPrefix.get() + "解密失败", ex);
            throw new OpenApiServerException("解密失败");
        }
        this.logCostTime("解密", startTime);
        return CompressUtil.decompressToText(bodyBytes);
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
            OutParams outParams = OutParams.success();

            long startTime = System.nanoTime();
            Object[] params = paramList.toArray();

            log.debug("{}调用API:{},入参：{}", logPrefix.get(), apiHandler, TruncateUtil.truncate(params));
            Object ret = apiHandler.getMethod().invoke(apiHandler.getBean(), params);
            log.debug("{}调用API:{},出参：{}", logPrefix.get(), apiHandler, TruncateUtil.truncate(ret));
            this.logCostTime("调用API", startTime);

            String retStr = StrUtil.EMPTY;
            if (ret != null) {
                retStr = StrObjectConvert.objToStr(ret, ret.getClass());
            }
            outParams.setData(retStr);
            byte[] retBytes = null;
            if (StrUtil.isNotBlank(retStr)) {
                //转成Byte[]
                retBytes = CompressUtil.compressText(retStr);

                //判断返回值是否需要加密
                boolean retEncrypt = isRetEncrypt(apiHandler);
                if (retEncrypt) {
                    retBytes = encryptRet(inParams, retBytes, outParams, apiHandler);
                }
            }
            outParams.setDataBytes(retBytes);
            return outParams;
        } catch (OpenApiServerException be) {
            throw new OpenApiServerException("调用opeapi处理器异常:" + be.getMessage());
        } catch (Exception ex) {
            log.error(logPrefix.get() + "调用opeapi处理器异常", ex);
            throw new OpenApiServerException("调用opeapi处理器异常");
        }
    }

    /**
     * 加密返回值
     *
     * @param inParams   openapi入参
     * @param retBytes   返回值（字节数组表示）
     * @param outParams  openapi出参
     * @param apiHandler openapi处理器
     * @return 加密后的返回值
     */
    private byte[] encryptRet(InParams inParams, byte[] retBytes, OutParams outParams, ApiHandler apiHandler) {
        try {
            long startTime = System.nanoTime();
            //获取调用者公钥
            String callerPublicKey = config.getCallerPublicKey(inParams.getCallerId());
            log.debug("{}caller({}) publicKey:{}", logPrefix.get(), inParams.getCallerId(), callerPublicKey);

            //加密返回值
            CryModeEnum cryModeEnum = this.getCryModeEnum(apiHandler);
            if (cryModeEnum == CryModeEnum.SymmetricCry) {
                //启用对称加密模式
                byte[] keyBytes = SymmetricCryUtil.getKey(symmetricCryEnum);
                String key = Base64Util.bytesToBase64(keyBytes);
                String cryKey = this.asymmetricCryHandler.cry(callerPublicKey, key);
                outParams.setSymmetricCryKey(cryKey);
                retBytes = this.symmetricCryHandler.cry(retBytes, keyBytes);
            } else if (cryModeEnum == CryModeEnum.AsymmetricCry) {
                //仅采用非对称加密模式
                retBytes = this.asymmetricCryHandler.cry(callerPublicKey, retBytes);
            } else {
                //不加密模式CryModeEnum.NONE
            }
            this.logCostTime("加密", startTime);
        } catch (OpenApiServerException be) {
            throw new OpenApiServerException("返回值加密异常:" + be.getMessage());
        } catch (Exception ex) {
            throw new OpenApiServerException("返回值加密异常", ex);
        }
        return retBytes;
    }

    /**
     * 获取加密模式
     *
     * @param apiHandler openapi处理器
     * @return 加密模式
     */
    private CryModeEnum getCryModeEnum(ApiHandler apiHandler) {
        CryModeEnum cryModeEnum = this.cryModeEnum;
        if (CryModeEnum.UNKNOWN != apiHandler.getOpenApiMethod().cryModeEnum()) {
            cryModeEnum = apiHandler.getOpenApiMethod().cryModeEnum();
        }
        logCryMode(cryModeEnum);
        return cryModeEnum;
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
        log.debug("{}返回值是否需要加密：{}", logPrefix.get(), retEncrypt);
        return retEncrypt;
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

    /**
     * 记录加密模式
     *
     * @param cryModeEnum 加密模式
     */
    private void logCryMode(CryModeEnum cryModeEnum) {
        if (cryModeEnum == CryModeEnum.SymmetricCry) {
            log.debug("{}采用非对称加密{}+对称加密{}模式", logPrefix.get(), asymmetricCryEnum, symmetricCryEnum);
        } else if (cryModeEnum == CryModeEnum.AsymmetricCry) {
            log.debug("{}仅采用非对称加密{}模式", logPrefix.get(), asymmetricCryEnum);
        } else {
            log.debug("{}采用不加密模式,签名用的非对称加密{}", logPrefix.get(), asymmetricCryEnum);
        }
    }

    /**
     * 记录操作的耗时
     *
     * @param operate   操作
     * @param startTime 操作开始时间
     */
    private void logCostTime(String operate, long startTime) {
        log.debug("{}{}耗时:{}ms", logPrefix.get(), operate, (System.nanoTime() - startTime) / 100_0000);
    }
}
