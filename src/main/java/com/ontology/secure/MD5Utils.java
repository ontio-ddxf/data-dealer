package com.ontology.secure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

/**
 * @author zhouq
 * @version 1.0
 * @date 2018/8/27
 */
public class MD5Utils {

    private static final Logger logger = LoggerFactory.getLogger(MD5Utils.class);

    private static final String UTF8 = "utf-8";

    /**
     * MD5加密
     *
     * @param origin 字符
     * @return
     */
    public static byte[] MD5Encode(String origin) throws Exception {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] rs = md.digest(origin.getBytes(UTF8));
            return rs;
        } catch (Exception e) {
            logger.error("error...", e);
            throw e;
        }
    }


}
