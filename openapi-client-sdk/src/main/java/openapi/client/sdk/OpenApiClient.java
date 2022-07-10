package openapi.client.sdk;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.sdk.common.constant.Constant;
import openapi.sdk.common.constant.Header;
import openapi.sdk.common.enums.AsymmetricCryEnum;
import openapi.sdk.common.enums.SymmetricCryEnum;
import openapi.sdk.common.exception.OpenApiClientException;
import openapi.sdk.common.handler.AsymmetricCryHandler;
import openapi.sdk.common.handler.SymmetricCryHandler;
import openapi.sdk.common.model.InParams;
import openapi.sdk.common.model.OutParams;
import openapi.sdk.common.util.*;

import java.util.*;

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
    private final String baseUrl;

    /**
     * 本系统私钥
     */
    private final String selfPrivateKey;

    /**
     * 远程系统的公钥
     */
    private final String remotePublicKey;

    /**
     * 非对称加密算法
     */
    private final AsymmetricCryEnum asymmetricCryEnum;

    /**
     * 返回值是否需要解密
     */
    private final boolean retDecrypt;

    /**
     * 是否启用对称加密(内容采用对称加密，对称加密密钥采用非对称加密)
     */
    private final boolean enableSymmetricCry;

    /**
     * 对称加密算法
     */
    private final SymmetricCryEnum symmetricCryEnum;

    /**
     * 调用者ID
     */
    private final String callerId;

    /**
     * API接口名称
     */
    private final String api;

    /**
     * 非对称加密处理器
     */
    private final AsymmetricCryHandler asymmetricCryHandler;

    /**
     * 对称加密处理器
     */
    private final SymmetricCryHandler symmetricCryHandler;

    /**
     * HTTP建立连接超时时间（单位秒）
     */
    private int httpConnectionTimeout;

    /**
     * HTTP数据传输超时时间（单位秒）
     */
    private int httpReadTimeout;

    /**
     * HTTP请求代理域名
     */
    private String httpProxyHost;

    /**
     * HTTP请求代理端口
     */
    private Integer httpProxyPort;

    /**
     * 获取HTTP建立连接超时时间（单位秒）
     *
     * @return HTTP建立连接超时时间（单位秒）
     */
    public int getHttpConnectionTimeout() {
        return httpConnectionTimeout;
    }

    /**
     * 设置HTTP建立连接超时时间（单位秒）
     *
     * @param httpConnectionTimeout HTTP建立连接超时时间（单位秒）
     */
    public void setHttpConnectionTimeout(int httpConnectionTimeout) {
        this.httpConnectionTimeout = httpConnectionTimeout;
    }

    /**
     * 获取HTTP数据传输超时时间（单位秒）
     *
     * @return HTTP数据传输超时时间（单位秒）
     */
    public int getHttpReadTimeout() {
        return httpReadTimeout;
    }

    /**
     * 设置HTTP数据传输超时时间（单位秒）
     *
     * @param httpReadTimeout HTTP数据传输超时时间（单位秒）
     */
    public void setHttpReadTimeout(int httpReadTimeout) {
        this.httpReadTimeout = httpReadTimeout;
    }

    /**
     * 获取HTTP请求代理域名
     *
     * @return HTTP请求代理域名
     */
    public String getHttpProxyHost() {
        return httpProxyHost;
    }

    /**
     * 设置HTTP请求代理域名
     *
     * @param httpProxyHost HTTP请求代理域名
     */
    public void setHttpProxyHost(String httpProxyHost) {
        this.httpProxyHost = httpProxyHost;
    }

    /**
     * 获取HTTP请求代理端口
     *
     * @return HTTP请求代理端口
     */
    public Integer getHttpProxyPort() {
        return httpProxyPort;
    }

    /**
     * 设置HTTP请求代理端口
     *
     * @param httpProxyPort HTTP请求代理端口
     */
    public void setHttpProxyPort(Integer httpProxyPort) {
        this.httpProxyPort = httpProxyPort;
    }

    /**
     * 日志前缀
     */
    private final ThreadLocal<String> logPrefix = new ThreadLocal<>();

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
        this.baseUrl = baseUrl;
        this.selfPrivateKey = selfPrivateKey;
        this.remotePublicKey = remotePublicKey;
        this.asymmetricCryEnum = asymmetricCryEnum;
        this.retDecrypt = retDecrypt;
        this.enableSymmetricCry = enableSymmetricCry;
        this.symmetricCryEnum = symmetricCryEnum;
        this.callerId = callerId;
        this.api = api;
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
            List<String> paramStrList = new ArrayList<>();
            for (Object param : params) {
                String paramStr = StrObjectConvert.objToStr(param, param.getClass());
                paramStrList.add(paramStr);
            }
            body = JSONUtil.toJsonStr(paramStrList);
            multiParam = true;
        }
        inParams.setBody(body);
        inParams.setBodyBytes(CompressUtil.compressText(body));
        inParams.setMultiParam(multiParam);
    }

    /**
     * 加密&加签
     *
     * @param inParams 入参
     */
    private void encryptAndSign(InParams inParams) {
        //加密
        long startTime = System.nanoTime();
        byte[] bodyBytes = inParams.getBodyBytes();
        if (ArrayUtil.isNotEmpty(bodyBytes)) {
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
                bodyBytes = this.symmetricCryHandler.cry(bodyBytes, keyBytes);
            } else {
                //仅采用非对称加密
                bodyBytes = this.asymmetricCryHandler.cry(remotePublicKey, bodyBytes);
            }
            inParams.setBodyBytes(bodyBytes);
        }
        this.logCostTime("加密", startTime);

        //加签
        startTime = System.nanoTime();
        byte[] signContent = CommonUtil.getSignContent(inParams);
        String sign = this.asymmetricCryHandler.sign(selfPrivateKey, signContent);
        inParams.setSign(sign);
        this.logCostTime("加签", startTime);
    }

    /**
     * 调用远程openapi接口
     *
     * @param inParams 入参
     * @return 结果
     */
    private OutParams doCall(InParams inParams) {
        long startTime = System.nanoTime();
        String url = CommonUtil.completeUrl(baseUrl, Constant.OPENAPI_PATH);
        Map<String, String> headers = this.getHeaders(inParams);
        byte[] bodyBytes = inParams.getBodyBytes();
        log.debug("{}调用openapi入参:{}", logPrefix.get(), inParams);
        //构造http请求对象
        HttpRequest request = HttpRequest.post(url)
                .setConnectionTimeout(httpConnectionTimeout * 1000)
                .setReadTimeout(httpReadTimeout * 1000)
                .addHeaders(headers)
                .contentType(ContentType.OCTET_STREAM.getValue())
                .body(bodyBytes);
        //设置http代理
        if (StrUtil.isNotBlank(this.httpProxyHost) && this.httpProxyPort != null) {
            request.setHttpProxy(httpProxyHost, httpProxyPort);
        }
        //执行http请求
        HttpResponse response = request.execute();
        OutParams outParams = getOutParams(response);
        log.debug("{}调用openapi出参：{}", logPrefix.get(), outParams);
        this.logCostTime("调用openapi", startTime);

        if (OutParams.isSuccess(outParams)) {
            //判断是否需要解密数据
            if (retDecrypt) {
                //解密数据
                decryptData(outParams);

                //对称加密密钥清空
                outParams.setSymmetricCryKey(null);
            }

            //返回值字节数组转成字符串
            if (ArrayUtil.isNotEmpty(outParams.getDataBytes())) {
                outParams.setData(CompressUtil.decompressToText(outParams.getDataBytes()));
            }
        } else {
            throw new OpenApiClientException("调用openapi异常:" + outParams);
        }
        return outParams;
    }

    /**
     * 获取出参
     *
     * @param response HTTP响应
     * @return 出参
     */
    private OutParams getOutParams(HttpResponse response) {
        OutParams outParams = new OutParams();
        outParams.setUuid(response.header(Header.Response.UUID));
        outParams.setCode(Integer.valueOf(response.header(Header.Response.CODE)));
        outParams.setMessage(response.header(Header.Response.MESSAGE));
        outParams.setSymmetricCryKey(response.header(Header.Response.SYMMETRIC_CRY_KEY));
        outParams.setDataBytes(response.bodyBytes());
        return outParams;
    }

    /**
     * 构建请求头信息
     *
     * @param inParams 入参
     * @return 请求头
     */
    private Map<String, String> getHeaders(InParams inParams) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Header.Request.UUID, inParams.getUuid());
        headers.put(Header.Request.CALLER_ID, inParams.getCallerId());
        headers.put(Header.Request.API, inParams.getApi());
        headers.put(Header.Request.METHOD, inParams.getMethod());
        headers.put(Header.Request.SIGN, inParams.getSign());
        headers.put(Header.Request.SYMMETRIC_CRY_KEY, inParams.getSymmetricCryKey());
        headers.put(Header.Request.MULTI_PARAM, String.valueOf(inParams.isMultiParam()));
        return headers;
    }

    /**
     * 解密数据
     *
     * @param outParams 返回值
     */
    private void decryptData(OutParams outParams) {
        try {
            long startTime = System.nanoTime();
            byte[] dataBytes = outParams.getDataBytes();
            if (ArrayUtil.isNotEmpty(dataBytes)) {
                byte[] decryptedDataBytes;
                if (enableSymmetricCry) {
                    String key = this.asymmetricCryHandler.deCry(selfPrivateKey, outParams.getSymmetricCryKey());
                    byte[] keyBytes = Base64Util.base64ToBytes(key);
                    decryptedDataBytes = this.symmetricCryHandler.deCry(dataBytes, keyBytes);
                } else {
                    decryptedDataBytes = this.asymmetricCryHandler.deCry(selfPrivateKey, dataBytes);
                }
                outParams.setDataBytes(decryptedDataBytes);
            }
            this.logCostTime("解密", startTime);
        } catch (OpenApiClientException be) {
            String errorMsg = "解密失败：" + be.getMessage();
            log.error(logPrefix.get() + errorMsg, be);
            throw new OpenApiClientException(errorMsg);
        } catch (Exception ex) {
            log.error(logPrefix.get() + "解密失败", ex);
            throw new OpenApiClientException("解密失败");
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
            throw new OpenApiClientException("调用者ID不能为空");
        }
        if (StrUtil.isBlank(api)) {
            throw new OpenApiClientException("API接口名不能为空");
        }
        if (StrUtil.isBlank(method)) {
            throw new OpenApiClientException("API方法名不能为空");
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

    /**
     * 记录操作的耗时
     *
     * @param operate   操作
     * @param startTime 操作开始时间
     */
    private void logCostTime(String operate, long startTime) {
        log.debug("{}{}耗时:{}ms", logPrefix.get(), operate, (System.nanoTime() - startTime) / 100_0000);
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
