package com.ontology.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author zhouq
 * @version 1.0
 * @date 2018/8/27
 */
public class HMACSha256 {

    private static final Logger logger = LoggerFactory.getLogger(HMACSha256.class);

    /**
     * sha256_HMAC加密
     *
     * @param message 消息
     * @param secret  秘钥
     * @return 加密后字符串
     */
    public static byte[] sha256_HMAC(String message, String secret) throws Exception {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
            return bytes;
        } catch (Exception e) {
            logger.error("error...", e);
            throw e;
        }
    }

}
