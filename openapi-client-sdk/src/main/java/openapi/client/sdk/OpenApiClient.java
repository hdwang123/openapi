package openapi.client.sdk;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.client.sdk.constant.ClientConstant;
import openapi.sdk.common.constant.Constant;
import openapi.sdk.common.handler.asymmetric.AsymmetricCryHandler;
import openapi.sdk.common.handler.symmetric.SymmetricCryHandler;
import openapi.sdk.common.model.*;
import openapi.sdk.common.util.Base64Util;
import openapi.sdk.common.util.CommonUtil;
import openapi.sdk.common.util.StrObjectConvert;
import openapi.sdk.common.util.SymmetricCryUtil;

import java.util.UUID;

/**
 * 对外开放api客户端
 * 注：推荐使用{@link OpenApiClientBuilder}构建对象
 *
 * @author wanghuidong
 */
@Slf4j
public class OpenApiClient {

    /**
     * openapi基础路径,例如(http://localhost)
     */
    private String baseUrl;

    /**
     * 本系统私钥
     */
    private String selfPrivateKey;

    /**
     * 远程系统的公钥
     */
    private String remotePublicKey;

    /**
     * 非对称加密算法
     */
    private AsymmetricCryEnum asymmetricCryEnum;

    /**
     * 返回值是否需要解密
     */
    private boolean retDecrypt;

    /**
     * 是否启用对称加密(内容采用对称加密，对称加密密钥采用非对称加密)
     */
    private boolean enableSymmetricCry;

    /**
     * 对称加密算法
     */
    private SymmetricCryEnum symmetricCryEnum;

    /**
     * 调用者ID
     */
    private String callerId;

    /**
     * API接口名称
     */
    private String api;

    /**
     * 非对称加密处理器
     */
    private AsymmetricCryHandler asymmetricCryHandler;

    /**
     * 对称加密处理器
     */
    private SymmetricCryHandler symmetricCryHandler;

    /**
     * HTTP建立连接超时时间（单位秒）
     */
    private int httpConnectionTimeout;

    /**
     * HTTP数据传输超时时间（单位秒）
     */
    private int httpReadTimeout;

    /**
     * 日志前缀
     */
    private ThreadLocal<String> logPrefix = new ThreadLocal<>();

    /**
     * openapi客户端
     *
     * @param baseUrl         openapi基础路径
     * @param selfPrivateKey  本系统私钥
     * @param remotePublicKey 远程系统的公钥
     */
    public OpenApiClient(String baseUrl, String selfPrivateKey, String remotePublicKey) {
        this(baseUrl, selfPrivateKey, remotePublicKey, AsymmetricCryEnum.RSA);
    }

    /**
     * openapi客户端
     *
     * @param baseUrl           openapi基础路径
     * @param selfPrivateKey    本系统私钥
     * @param remotePublicKey   远程系统的公钥
     * @param asymmetricCryEnum 非对称加密算法
     */
    public OpenApiClient(String baseUrl, String selfPrivateKey, String remotePublicKey, AsymmetricCryEnum asymmetricCryEnum) {
        this(baseUrl, selfPrivateKey, remotePublicKey, asymmetricCryEnum, true);
    }

    /**
     * openapi客户端
     *
     * @param baseUrl           openapi基础路径
     * @param selfPrivateKey    本系统私钥
     * @param remotePublicKey   远程系统的公钥
     * @param asymmetricCryEnum 非对称加密算法
     * @param retDecrypt        返回值是否需要解密
     */
    public OpenApiClient(String baseUrl, String selfPrivateKey, String remotePublicKey, AsymmetricCryEnum asymmetricCryEnum, boolean retDecrypt) {
        this(baseUrl, selfPrivateKey, remotePublicKey, asymmetricCryEnum, retDecrypt, false, SymmetricCryEnum.AES);
    }

    /**
     * openapi客户端
     *
     * @param baseUrl            openapi基础路径
     * @param selfPrivateKey     本系统私钥
     * @param remotePublicKey    远程系统的公钥
     * @param asymmetricCryEnum  非对称加密算法
     * @param retDecrypt         返回值是否需要解密
     * @param enableSymmetricCry 是否启用对称加密(内容采用对称加密，对称加密密钥采用非对称加密)
     * @param symmetricCryEnum   对称加密算法
     */
    public OpenApiClient(String baseUrl, String selfPrivateKey, String remotePublicKey, AsymmetricCryEnum asymmetricCryEnum,
                         boolean retDecrypt, boolean enableSymmetricCry, SymmetricCryEnum symmetricCryEnum) {
        this(baseUrl, selfPrivateKey, remotePublicKey, asymmetricCryEnum, retDecrypt, enableSymmetricCry, symmetricCryEnum, null, null);
    }

    /**
     * openapi客户端
     *
     * @param baseUrl            openapi基础路径
     * @param selfPrivateKey     本系统私钥
     * @param remotePublicKey    远程系统的公钥
     * @param asymmetricCryEnum  非对称加密算法
     * @param retDecrypt         返回值是否需要解密
     * @param enableSymmetricCry 是否启用对称加密(内容采用对称加密，对称加密密钥采用非对称加密)
     * @param symmetricCryEnum   对称加密算法
     * @param callerId           调用者ID
     * @param api                接口名称
     */
    public OpenApiClient(String baseUrl, String selfPrivateKey, String remotePublicKey, AsymmetricCryEnum asymmetricCryEnum,
                         boolean retDecrypt, boolean enableSymmetricCry, SymmetricCryEnum symmetricCryEnum,
                         String callerId, String api) {
        this(baseUrl, selfPrivateKey, remotePublicKey, asymmetricCryEnum, retDecrypt, enableSymmetricCry, symmetricCryEnum,
                callerId, api, ClientConstant.HTTP_CONNECTION_TIMEOUT, ClientConstant.HTTP_READ_TIMEOUT);
    }

    /**
     * openapi客户端
     *
     * @param baseUrl               openapi基础路径
     * @param selfPrivateKey        本系统私钥
     * @param remotePublicKey       远程系统的公钥
     * @param asymmetricCryEnum     非对称加密算法
     * @param retDecrypt            返回值是否需要解密
     * @param enableSymmetricCry    是否启用对称加密(内容采用对称加密，对称加密密钥采用非对称加密)
     * @param symmetricCryEnum      对称加密算法
     * @param callerId              调用者ID
     * @param api                   接口名称
     * @param httpConnectionTimeout 设置HTTP建立连接超时时间（单位秒）
     * @param httpReadTimeout       设置HTTP数据传输超时时间（单位秒）
     */
    public OpenApiClient(String baseUrl, String selfPrivateKey, String remotePublicKey, AsymmetricCryEnum asymmetricCryEnum,
                         boolean retDecrypt, boolean enableSymmetricCry, SymmetricCryEnum symmetricCryEnum,
                         String callerId, String api, int httpConnectionTimeout, int httpReadTimeout) {
        this.baseUrl = baseUrl;
        this.selfPrivateKey = selfPrivateKey;
        this.remotePublicKey = remotePublicKey;
        this.asymmetricCryEnum = asymmetricCryEnum;
        this.retDecrypt = retDecrypt;
        this.enableSymmetricCry = enableSymmetricCry;
        this.symmetricCryEnum = symmetricCryEnum;
        this.callerId = callerId;
        this.api = api;
        this.httpConnectionTimeout = httpConnectionTimeout;
        this.httpReadTimeout = httpReadTimeout;
        this.asymmetricCryHandler = AsymmetricCryHandler.handlerMap.get(asymmetricCryEnum);
        this.symmetricCryHandler = SymmetricCryHandler.handlerMap.get(symmetricCryEnum);

        //初始化信息打印
        if (log.isDebugEnabled()) {
            log.debug("OpenApiClient init:{}", this);
            logCryModel(this.enableSymmetricCry);
        }
        //重要日志改成info级别
        log.info("OpenApiClient init succeed. hashcode={}", this.hashCode());
    }

    /**
     * 调用openapi
     * 注：推荐使用其它重载方法
     *
     * @param inParams 入参
     * @return 返回值
     */
    public OutParams callOpenApi(InParams inParams) {
        //再次检查入参，可能有直接调用此函数的
        checkInParams(inParams.getCallerId(), inParams.getApi(), inParams.getMethod());

        //没有设置uuid则给设置一个
        if (StrUtil.isBlank(inParams.getUuid())) {
            inParams.setUuid(UUID.randomUUID().toString());
        }

        //设置日志前缀
        logPrefix.set(String.format("uuid=%s:", inParams.getUuid()));
        log.debug("{}入参：{}", logPrefix.get(), inParams);


        //加密&加签
        encryptAndSign(inParams);

        //调用openapi 并 处理返回值
        OutParams outParams = doCall(inParams);
        log.debug("{}出参：{}", logPrefix.get(), outParams);
        return outParams;
    }

    /**
     * 调用openapi
     * 注：请用{@link OpenApiClientBuilder}构建{@link OpenApiClient}对象
     *
     * @param method API方法名
     * @param params API方法参数
     * @return 返回值
     */
    public OutParams callOpenApi(String method, Object... params) {
        //检查方法参数
        checkInParams(callerId, api, method);

        //构建InParams对象
        InParams inParams = new InParams();
        inParams.setUuid(UUID.randomUUID().toString());
        inParams.setCallerId(callerId);
        inParams.setApi(api);
        inParams.setMethod(method);

        //设置入参的body
        setInParamsBody(inParams, params);

        //调用openapi
        return this.callOpenApi(inParams);
    }

    /**
     * 调用openapi
     * 注：请用{@link OpenApiClientBuilder}构建{@link OpenApiClient}对象
     *
     * @param api    API接口名
     * @param method API方法名
     * @param params API方法参数
     * @return 返回值
     */
    public OutParams callOpenApi(String api, String method, Object... params) {
        //检查方法参数
        checkInParams(callerId, api, method);

        //构建InParams对象
        InParams inParams = new InParams();
        inParams.setUuid(UUID.randomUUID().toString());
        inParams.setCallerId(callerId);
        inParams.setApi(api);
        inParams.setMethod(method);

        //设置入参的body
        setInParamsBody(inParams, params);

        //调用openapi
        return this.callOpenApi(inParams);
    }

    /**
     * 调用openapi
     *
     * @param callerId 调用者ID
     * @param api      API接口名
     * @param method   API方法名
     * @param params   API方法参数
     * @return 返回值
     */
    public OutParams callOpenApi(String callerId, String api, String method, Object... params) {
        //检查方法参数
        checkInParams(callerId, api, method);

        //构建InParams对象
        InParams inParams = new InParams();
        inParams.setUuid(UUID.randomUUID().toString());
        inParams.setCallerId(callerId);
        inParams.setApi(api);
        inParams.setMethod(method);

        //设置入参的body
        setInParamsBody(inParams, params);

        //调用openapi
        return this.callOpenApi(inParams);
    }

    /**
     * 设置入参的body
     *
     * @param inParams 入参
     * @param params   方法参数
     */
    private void setInParamsBody(InParams inParams, Object[] params) {
        String body;
        boolean multiParam;
        if (params == null || params.length == 0) {
            //无参函数
            body = StrUtil.EMPTY;
            multiParam = false;
        } else if (params.length == 1) {
            //单参函数
            body = StrObjectConvert.objToStr(params[0], params[0].getClass());
            multiParam = false;
        } else {
            //多参函数
            body = JSONUtil.toJsonStr(params);
            multiParam = true;
        }
        inParams.setBody(body);
        inParams.setMultiParam(multiParam);
    }

    /**
     * 加密&加签
     *
     * @param inParams 入参
     */
    private void encryptAndSign(InParams inParams) {
        //加密
        String body = inParams.getBody();
        if (StrUtil.isNotBlank(body)) {
            if (this.enableSymmetricCry) {
                //启用对称加密，则内容采用对称加密，需先生成对称密钥，密钥采用非对称加密后传输
                //生成对称密钥key
                byte[] keyBytes = SymmetricCryUtil.getKey(symmetricCryEnum);

                //转成base64不会有问题，如果采用new String("utf-8"),再转回来字节数会变大
                String key = Base64Util.bytesToBase64(keyBytes);

                //对key使用非对称加密
                String cryKey = this.asymmetricCryHandler.cry(remotePublicKey, key);
                inParams.setSymmetricCryKey(cryKey);

                //对内容进行对称加密
                body = this.symmetricCryHandler.cry(body, keyBytes);
            } else {
                //仅采用非对称加密
                body = this.asymmetricCryHandler.cry(remotePublicKey, body);
            }
            inParams.setBody(body);
        }

        //加签
        String signContent = CommonUtil.getSignContent(inParams);
        String sign = this.asymmetricCryHandler.sign(selfPrivateKey, signContent);
        inParams.setSign(sign);
    }

    /**
     * 调用远程openapi接口
     *
     * @param inParams 入参
     * @return 结果
     */
    private OutParams doCall(InParams inParams) {
        String url = CommonUtil.completeUrl(baseUrl, Constant.OPENAPI_PATH);
        String body = JSONUtil.toJsonStr(inParams);
        log.debug("{}调用openapi入参:{}", logPrefix.get(), inParams);
        String ret = HttpRequest.post(url)
                .setConnectionTimeout(httpConnectionTimeout * 1000)
                .setReadTimeout(httpReadTimeout * 1000)
                .contentType(ContentType.JSON.getValue())
                .header(Header.ACCEPT, ContentType.JSON.getValue())
                .body(body)
                .execute()
                .body();
        log.debug("{}调用openapi出参：{}", logPrefix.get(), ret);
        if (StrUtil.isBlank(ret)) {
            throw new BusinessException("返回值为空");
        }
        OutParams outParams = JSONUtil.toBean(ret, OutParams.class);
        if (OutParams.isSuccess(outParams)) {
            //判断是否需要解密数据
            if (retDecrypt) {
                decryptData(outParams);

                //对称加密密钥清空
                outParams.setSymmetricCryKey(null);
            }
        } else {
            throw new BusinessException("调用openapi异常:" + outParams);
        }
        return outParams;
    }

    /**
     * 解密数据
     *
     * @param outParams 返回值
     */
    private void decryptData(OutParams outParams) {
        try {
            String data = outParams.getData();
            if (StrUtil.isNotBlank(data)) {
                String decryptedData = null;
                if (enableSymmetricCry) {
                    String key = this.asymmetricCryHandler.deCry(selfPrivateKey, outParams.getSymmetricCryKey());
                    byte[] keyBytes = Base64Util.base64ToBytes(key);
                    decryptedData = this.symmetricCryHandler.deCry(data, keyBytes);
                } else {
                    decryptedData = this.asymmetricCryHandler.deCry(selfPrivateKey, data);
                }
                outParams.setData(decryptedData);
            }
        } catch (BusinessException be) {
            String errorMsg = "解密失败：" + be.getMessage();
            log.error(logPrefix.get() + errorMsg, be);
            throw new BusinessException(errorMsg);
        } catch (Exception ex) {
            log.error(logPrefix.get() + "解密失败", ex);
            throw new BusinessException("解密失败");
        }
    }

    /**
     * 检查入参
     *
     * @param callerId 调用者ID
     * @param api      API接口名
     * @param method   API方法名
     */
    private void checkInParams(String callerId, String api, String method) {
        if (StrUtil.isBlank(callerId)) {
            throw new BusinessException("调用者ID不能为空");
        }
        if (StrUtil.isBlank(api)) {
            throw new BusinessException("API接口名不能为空");
        }
        if (StrUtil.isBlank(method)) {
            throw new BusinessException("API方法名不能为空");
        }
    }

    /**
     * 记录加密模式
     *
     * @param enableSymmetricCry 是否启用对称加密
     */
    private void logCryModel(boolean enableSymmetricCry) {
        if (enableSymmetricCry) {
            log.debug("启用对称加密，采用非对称加密{}+对称加密{}模式", asymmetricCryEnum, symmetricCryEnum);
        } else {
            log.debug("未启用对称加密，仅采用非对称加密{}模式", asymmetricCryEnum);
        }
    }

    @Override
    public String toString() {
        return String.format("\nopenApiClient hashCode:%x,\nbaseUrl:%s,\nselfPrivateKey:%s,\nremotePublicKey:%s," +
                        "\nasymmetricCryEnum:%s,\nretDecrypt:%s;\nenableSymmetricCry:%s,\nsymmetricCryEnum:%s," +
                        "\ncallerId:%s,\napi:%s,\nhttpConnectionTimeout:%s,\nhttpReadTimeout:%s",
                this.hashCode(), baseUrl, selfPrivateKey, remotePublicKey,
                asymmetricCryEnum, retDecrypt, enableSymmetricCry, symmetricCryEnum,
                callerId, api, httpConnectionTimeout, httpReadTimeout);
    }

}
