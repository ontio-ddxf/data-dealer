package com.onchain.utils;

/**
 * 常量
 */
public class Constant {

    public static final String HTTPHEADER_LANGUAGE = "Lang";

    public static final String HTTPHEADER_AUTHORIZATION = "Authorization";

    public static final String HTTPHEADER_GACODE = "GA-Code";

    public static final String HTTPHEADER_EMAILCODE = "Email-Verify-Code";

    public static final String HTTPHEADER_EMAIL_VERIFY_CODE = "Email-Verify-Code";

    public static final String HTTPHEADER_GEETEST_CHALLENGE = "Geetest-Challenge";

    public static final String HTTPHEADER_GEETEST_VALIADATE = "Geetest-Validate";

    public static final String HTTPHEADER_GEETEST_SECODE = "Geetest-Secode";

    public static final String HTTP_HEADER_SECURE = "Secure-Key";

    public static final String CN = "CN";

    public static final String EN = "EN";

    public static final String EMAIL = "email";

    // 用户登录，没有做ga的时候
    public static final String GUEST = "guest";

    public static final String ZIP_SUFFIX = ".zip";

    /**
     * redis-OK
     */
    public final static String OK = "OK";

    /**
     * redis过期时间，以秒为单位，一分钟
     */
    public final static int EXRP_MINUTE = 60;

    /**
     * redis过期时间，以秒为单位，一小时
     */
    public final static int EXRP_HOUR = 60 * 60;

    /**
     * redis过期时间，以秒为单位，一天
     */
    public final static int EXRP_DAY = 60 * 60 * 24;

    /**
     * redis-key-前缀-shiro:cache:
     */
    public final static String PREFIX_SHIRO_CACHE = "shiro:cache:";

    /**
     * redis-key-前缀-shiro:access_token:
     */
    public final static String PREFIX_SHIRO_ACCESS_TOKEN = "shiro:access_token:";

    /**
     * redis-key-前缀-shiro:refresh_token:
     */
    public final static String PREFIX_SHIRO_REFRESH_TOKEN = "shiro:refresh_token:";

    /**
     * JWT-account:
     */
    public final static String JWT_EMAIL = "email";
    /**
     * JWT-account:
     */
    public final static String JWT_CUSTID = "custId";
    /**
     * JWT-account:
     */
    public final static String JWT_ACCTID = "acctId";

    /**
     * JWT-currentTimeMillis:
     */
    public final static String CURRENT_TIME_MILLIS = "currentTimeMillis";

    /**
     * default pagination size
     */
    public static final int DEFAULT_PAGINATION_SIZE = 10;

    /**
     * SCRYPT param
     */
    public static final int SCRYPT_N = 16384;

    public static final int SCRYPT_R = 8;

    public static final int SCRYPT_P = 1;


    /**
     * 谷歌认证密钥
     */
    //当测试authTest时候，把genSecretTest生成的secret值赋值给它
    public static final String GA_SECRET = "PEZDT26SGTBIRSZJ";


    /**
     * GEETEST开发者APPID,APPKEY
     */
    public static final String CAPTCHA_ID = "da94bb735ac8ba0e2d99df336ea5f525";

    public static final String CAPTCHA_KEY = "acf0102d81fdd21b148c7d80226259c2";

    /**
     * sendcloud 邮件参数
     */
    public static final String SENDCLOUD_EMAIL_TEMPLATEMAIL_URL = "http://api.sendcloud.net/apiv2/mail/sendtemplate";

    public static final String SENDCLOUD_EMAIL_APIUSER = "ontology_001";

    public static final String SENDCLOUD_EMAIL_APIKEY = "AI6d0idHjpOBguIn";

    /**
     * senecloud 短信参数
     */
    public static final String SENDCLOUD_SMS_SEND_URL = "http://www.sendcloud.net/smsapi/send";

    public static final String SENDCLOUD_SMS_APIUSER = "ontology_002";

    public static final String SENDCLOUD_SMS_APIKEY = "1UZRHJ38cI5dyboGvYXIswIExLaZ2CdU";

    /**
     * sdk手续费
     */
    public static final long GAS_PRICE = 500;
    public static final long GAS_Limit = 20000;

    /**
     * 发送手机验证码频率和验证码有效期
     */
    public static final long SEND_VERIFY_CODE_LIMIT = 60 * 1000;
    public static final long VERIFY_CODE_VALIDITY = 5 * 60 * 1000;

    public static final long EXPIRED_SECOND = 5 * 60;

    /**
     * 测试网络
     */
    public static final String TEST_ONTID_NET = "http://139.219.136.188:10330";
}
