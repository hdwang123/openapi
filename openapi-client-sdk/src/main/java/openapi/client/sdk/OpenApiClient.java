package openapi.client.sdk;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.*;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.sdk.common.constant.Constant;
import openapi.sdk.common.model.*;
import openapi.sdk.common.util.Base64Util;
import openapi.sdk.common.util.CommonUtil;
import openapi.sdk.common.util.StrObjectConvert;
import openapi.sdk.common.util.SymmetricCryUtil;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 对外开放api客户端
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
    public OpenApiClient(String baseUrl, String selfPrivateKey, String remotePublicKey, AsymmetricCryEnum asymmetricCryEnum, boolean retDecrypt, boolean enableSymmetricCry, SymmetricCryEnum symmetricCryEnum) {
        this.baseUrl = baseUrl;
        this.selfPrivateKey = selfPrivateKey;
        this.remotePublicKey = remotePublicKey;
        this.asymmetricCryEnum = asymmetricCryEnum;
        this.retDecrypt = retDecrypt;
        this.enableSymmetricCry = enableSymmetricCry;
        this.symmetricCryEnum = symmetricCryEnum;
    }

    /**
     * 调用openapi
     *
     * @param inParams 入参
     * @return 返回值
     */
    public OutParams callOpenApi(InParams inParams) {
        //加密&加签
        encryptAndSign(inParams);

        //调用openapi 并 处理返回值
        OutParams outParams = doCall(inParams);
        return outParams;
    }

    /**
     * 调用openapi
     *
     * @param callerId 调用者ID
     * @param api      接口名
     * @param method   方法名
     * @param params   方法参数
     * @return 返回值
     */
    public OutParams callOpenApi(String callerId, String api, String method, Object... params) {
        InParams inParams = new InParams();
        inParams.setUuid(UUID.randomUUID().toString());
        inParams.setCallerId(callerId);
        inParams.setApi(api);
        inParams.setMethod(method);

        //设置入参的body
        setInParamsBody(inParams, params);
        log.debug("入参：{}", inParams);

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
                log.debug("启用对称加密，采用非对称加密{}+对称加密{}模式", asymmetricCryEnum, symmetricCryEnum);

                //生成对称密钥key
                byte[] keyBytes = SymmetricCryUtil.getKey(symmetricCryEnum);

                //转成base64不会有问题，如果采用new String("utf-8"),再转回来字节数会变大
                String key = Base64Util.bytesToBase64(keyBytes);

                //对key使用非对称加密
                inParams.setSymmetricCryKey(asymmetricCry(key));

                //对内容进行对称加密
                body = symmetricCry(body, keyBytes);
            } else {
                log.debug("未启用对称加密，仅采用非对称加密{}模式", asymmetricCryEnum);
                //仅采用非对称加密
                body = asymmetricCry(body);
            }
            inParams.setBody(body);
        }

        //加签
        String signContent = CommonUtil.getSignContent(inParams);
        inParams.setSign(sign(signContent));
    }

    /**
     * 加签
     *
     * @param content 内容
     * @return 签名
     */
    private String sign(String content) {
        String signedStr = null;
        if (asymmetricCryEnum == AsymmetricCryEnum.RSA) {
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, selfPrivateKey, null);
            //签名
            byte[] data = content.getBytes(StandardCharsets.UTF_8);
            byte[] signed = sign.sign(data);
            signedStr = Base64Util.bytesToBase64(signed);
        } else if (asymmetricCryEnum == AsymmetricCryEnum.SM2) {
            SM2 sm2 = SmUtil.sm2(selfPrivateKey, null);
            signedStr = sm2.signHex(HexUtil.encodeHexStr(content));
        } else {
            throw new BusinessException("不支持的非对称加密算法");
        }
        return signedStr;
    }


    /**
     * 调用远程openapi接口
     *
     * @param inParams 入参
     * @return 结果
     */
    private OutParams doCall(InParams inParams) {
        String url = URLUtil.completeUrl(baseUrl, Constant.OPENAPI_PATH);
        String body = JSONUtil.toJsonStr(inParams);
        log.debug("调用openapi入参:" + inParams);
        String ret = HttpUtil.post(url, body);
        log.debug("调用openapi返回值：" + ret);
        if (StrUtil.isBlank(ret)) {
            throw new BusinessException("返回值为空");
        }
        OutParams outParams = JSONUtil.toBean(ret, OutParams.class);
        if (OutParams.isSuccess(outParams)) {
            //判断是否需要解密数据
            if (retDecrypt) {
                decryptData(outParams);
            }
        } else {
            throw new BusinessException("调用openapi异常:" + outParams.getMessage());
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
                    log.debug("启用对称加密，采用非对称加密{}+对称加密{}模式", asymmetricCryEnum, symmetricCryEnum);
                    String key = asymmetricDeCry(outParams.getSymmetricCryKey());
                    byte[] keyBytes = Base64Util.base64ToBytes(key);
                    decryptedData = symmetricDeCry(data, keyBytes);
                } else {
                    log.debug("未启用对称加密，仅采用非对称加密{}模式", asymmetricCryEnum);
                    decryptedData = asymmetricDeCry(data);
                }
                outParams.setData(decryptedData);
            }
        } catch (BusinessException be) {
            String errorMsg = "解密失败：" + be.getMessage();
            log.error(errorMsg, be);
            throw new BusinessException(errorMsg);
        } catch (Exception ex) {
            log.error("解密失败", ex);
            throw new BusinessException("解密失败");
        }
    }

    /**
     * 对称加密
     *
     * @param content  内容（普通字符串）
     * @param keyBytes 密码
     * @return 加密后的内容（Base64字符串）
     */
    private String symmetricCry(String content, byte[] keyBytes) {
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
     * @param content 内容 （普通字符串）
     * @return 加密后的内容（RSA:Base64字符串,SM2:ASCII字符串）
     */
    private String asymmetricCry(String content) {
        if (asymmetricCryEnum == AsymmetricCryEnum.RSA) {
            RSA rsa = new RSA(null, remotePublicKey);
            byte[] encrypt = rsa.encrypt(StrUtil.bytes(content, CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
            content = Base64Util.bytesToBase64(encrypt);
        } else if (asymmetricCryEnum == AsymmetricCryEnum.SM2) {
            SM2 sm2 = SmUtil.sm2(null, remotePublicKey);
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
        String decryptedData;
        if (asymmetricCryEnum == AsymmetricCryEnum.RSA) {
            RSA rsa = new RSA(selfPrivateKey, null);
            byte[] dataBytes = Base64Util.base64ToBytes(content);
            byte[] decrypt = rsa.decrypt(dataBytes, KeyType.PrivateKey);
            decryptedData = new String(decrypt, StandardCharsets.UTF_8);
        } else if (asymmetricCryEnum == AsymmetricCryEnum.SM2) {
            SM2 sm2 = SmUtil.sm2(selfPrivateKey, null);
            decryptedData = StrUtil.utf8Str(sm2.decryptFromBcd(content, KeyType.PrivateKey));
        } else {
            throw new BusinessException("不支持的非对称加密算法");
        }
        return decryptedData;
    }


}
