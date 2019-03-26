package com.ontology.secure;


import com.ontology.utils.Base64ConvertUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * 通信加密使用的参数
 */
@Configuration
@Component
public class SecureConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecureConfig.class);

    /**
     * 用来写测试用例的token，生产环境要删除
     */
    @Value("${test.token}")
    public String TEST_TOKEN;

    @Autowired
    private Environment environment;

    public boolean isProEnv() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            if (activeProfile.equals("pro")) {
                return true;
            }
        }
        return false;
    }

    public boolean backHole(String token) {
        if (!isProEnv()
                && TEST_TOKEN.equals(token)) {
            return true;
        }
        return false;
    }


    private static String RSA_PUBLIC_KEY;

    @Value("${rsa.publicKey}")
    public void setRsaPublicKey(String rsaPublicKey) {
        SecureConfig.RSA_PUBLIC_KEY = rsaPublicKey;
    }

    public String getRsaPublicKey() {
        try {
            return Base64ConvertUtil.decode(SecureConfig.RSA_PUBLIC_KEY);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }


    private static String RSA_PRIVATE_KEY;

    @Value("${rsa.privateKey}")
    public void setRsaPrivateKey(String privateKey) {
        SecureConfig.RSA_PRIVATE_KEY = privateKey;
    }

    public String getRsaPrivateKey() {
        try {
            return Base64ConvertUtil.decode(SecureConfig.RSA_PRIVATE_KEY);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }

    private static String WALLET_GO_PUBLIC_KEY;

    @Value("${wallet.goPublicKey}")
    public void setWalletGoPublicKey(String publicKey) {
        SecureConfig.WALLET_GO_PUBLIC_KEY = publicKey;
    }

    public String getWalletGoPublicKey() {
        try {
            return Base64ConvertUtil.decode(SecureConfig.WALLET_GO_PUBLIC_KEY);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }

    private static String WALLET_JAVA_PRIVATE_KEY;

    @Value("${wallet.javaPrivateKey}")
    public void setWalletJavaPrivateKey(String privateKey) {
        SecureConfig.WALLET_JAVA_PRIVATE_KEY = privateKey;
    }

    public String getWalletJavaPrivateKey() {
        try {
            return Base64ConvertUtil.decode(SecureConfig.WALLET_JAVA_PRIVATE_KEY);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }

    private static String AES_IV;

    @Value("${aes.iv}")
    public void setAesIvString(String aesIv) {
        SecureConfig.AES_IV = aesIv;
    }

    public String getAesIv() {
        try {
            return Base64ConvertUtil.decode(SecureConfig.AES_IV);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }

    private static String AES_KEY;

    @Value("${aes.key}")
    public void setAesKey(String key) {
        SecureConfig.AES_KEY = key;
    }

    public String getAesKey() {
        try {
            return Base64ConvertUtil.decode(SecureConfig.AES_KEY);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }

    private static String CONTRACT_HASH;
    @Value("${contract.hash}")
    public void setContractHash(String contractHash) {
        SecureConfig.CONTRACT_HASH = contractHash;
    }

    public String getContractHash() {
        try {
            return SecureConfig.CONTRACT_HASH;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }
}
