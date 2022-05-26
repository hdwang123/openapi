package openapi.client.sdk;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.*;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import openapi.sdk.common.constant.Constant;
import openapi.sdk.common.model.AsymmetricCryEnum;
import openapi.sdk.common.model.BusinessException;
import openapi.sdk.common.model.InParams;
import openapi.sdk.common.model.OutParams;
import openapi.sdk.common.util.Base64Util;

import java.nio.charset.StandardCharsets;

import static openapi.sdk.common.util.Base64Util.bytesToBase64;

/**
 * 对外开放api客户端
 *
 * @author wanghuidong
 * @date 2022/5/26 18:08
 */
@Slf4j
public class OpenApiClient {

    private String baseUrl;
    private String selfPrivateKey;
    private String remotePublicKey;
    private AsymmetricCryEnum asymmetricCryEnum;
    private boolean retDecrypt;

    /**
     * 客户端系统的私钥
     *
     * @param baseUrl           openapi基础路径
     * @param selfPrivateKey    本系统私钥
     * @param remotePublicKey   远程系统的公钥
     * @param asymmetricCryEnum 非对称加密算法
     * @param retDecrypt        返回值是否需要解密
     */
    public OpenApiClient(String baseUrl, String selfPrivateKey, String remotePublicKey, AsymmetricCryEnum asymmetricCryEnum, boolean retDecrypt) {
        this.baseUrl = baseUrl;
        this.selfPrivateKey = selfPrivateKey;
        this.remotePublicKey = remotePublicKey;
        this.asymmetricCryEnum = asymmetricCryEnum;
        this.retDecrypt = retDecrypt;
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
     * 加密&加签
     *
     * @param inParams 入参
     */
    private void encryptAndSign(InParams inParams) {
        String body = inParams.getBody();
        if (StrUtil.isNotBlank(body)) {
            //加密
            if (asymmetricCryEnum == AsymmetricCryEnum.RSA) {
                RSA rsa = new RSA(null, remotePublicKey);
                byte[] encrypt = rsa.encrypt(StrUtil.bytes(body, CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
                body = bytesToBase64(encrypt);
            } else if (asymmetricCryEnum == AsymmetricCryEnum.SM2) {
                SM2 sm2 = SmUtil.sm2(null, remotePublicKey);
                body = sm2.encryptBcd(body, KeyType.PublicKey);
            } else {
                throw new BusinessException("不支持的非对称加密算法");
            }
            inParams.setBody(body);

            //加签
            String signedStr = null;
            if (asymmetricCryEnum == AsymmetricCryEnum.RSA) {
                Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, selfPrivateKey, null);
                //签名
                byte[] data = body.getBytes(StandardCharsets.UTF_8);
                byte[] signed = sign.sign(data);
                signedStr = bytesToBase64(signed);
            } else if (asymmetricCryEnum == AsymmetricCryEnum.SM2) {
                SM2 sm2 = SmUtil.sm2(selfPrivateKey, null);
                signedStr = sm2.signHex(HexUtil.encodeHexStr(body));
            } else {
                throw new BusinessException("不支持的非对称加密算法");
            }
            inParams.setSign(signedStr);
        }
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
        log.info("调用openapi入参:" + inParams);
        String ret = HttpUtil.post(url, body);
        log.info("调用openapi返回值：" + ret);
        if (StrUtil.isBlank(ret)) {
            throw new BusinessException("返回值为空");
        }
        OutParams outParams = JSONUtil.toBean(ret, OutParams.class);
        if (OutParams.isSuccess(outParams)) {
            log.info("调用openapi成功");
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
        //解密
        String decryptedData = null;
        try {
            if (asymmetricCryEnum == AsymmetricCryEnum.RSA) {
                RSA rsa = new RSA(selfPrivateKey, null);
                byte[] dataBytes = Base64Util.base64ToBytes(outParams.getData());
                byte[] decrypt = rsa.decrypt(dataBytes, KeyType.PrivateKey);
                decryptedData = new String(decrypt, StandardCharsets.UTF_8);
            } else if (asymmetricCryEnum == AsymmetricCryEnum.SM2) {
                SM2 sm2 = SmUtil.sm2(selfPrivateKey, null);
                decryptedData = StrUtil.utf8Str(sm2.decryptFromBcd(outParams.getData(), KeyType.PrivateKey));
            } else {
                throw new BusinessException("不支持的非对称加密算法");
            }
            outParams.setData(decryptedData);
        } catch (Exception ex) {
            log.error("解密失败", ex);
            throw new BusinessException("解密失败");
        }
    }


}
