package com.ontology.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author zhouq
 * @version 1.0
 * @date 2017/12/26
 */
@Component
public class ConstantParam {


    public static final String CN = "CN";

    public static final String EN = "EN";


    public static final String MOBILE_CLAIMCONTEXT = "claim:mobile_authentication";

    public static final String EMAIL_CLAIMCONTEXT = "claim:email_authentication";

    /**
     * H5参数
     */
    public static final String H5URL_CLAIMCONTEXT = "context=";

    public static final String H5URL_ONTID = "ontid=";

    public static final String H5URL_DEVICECODE = "deviceCode=";

    public static final String H5URL_LANG= "lang=";

    public static final String H5URL_SEPARATOR = "&";

    public static final String H5URL_LANGEN = "en_us";

    public static final String H5URL_LANGCN = "zh_hans";

    /**
     * trustanchor列表分类标识
     */
    public static final String SOCIALMEDIA_CERTIFICATION = "SocialMediaCertification";

    public static final String ORGANIZATION_CERTIFICATION = "OrganizationCertification";

    public static final String APPNATIVE = "AppNative";

    /**
     * ontpass平台接口uri
     */
    public static final String ONTPASS_CHECKDEVICECODE_URL = "/api/v1/ontpass/devicecode/check";

    public static final String ONTPASS_QUERYCLAIMCONTEXT_URL = "/api/v1/ontpass/claimcontext/query";
    /**
     * trustanchor平台接口uri
     */
    public static final String TRUSTANCHOR_ISSUECLAIM_URL = "/api/v1/trustanchor/claim/issue";



    /**
     * 平台标识
     */
    public static final String SENDCLOUD = "SendCloud";

    public static final String PAASOO = "PaaSoo";

    /**
     * paasoo 短信参数
     */
    public static final String PAASOO_SMS_SEND_URI = "https://api2.paasoo.com/json";

    public static final String PAASOO_SMS_APIUSER = "tgkucfgc";

    public static final String PAASOO_SMS_APISECRET = "Mk4cT2k5m815";

    public static final String PAASOO_VER_SMS_TEXTSTART_EN = "[ONTO] Your phone number verification code is:";

    public static final String PAASOO_VER_SMS_TEXTEND_EN = ".This code is valid for 5 minutes. Do not reveal the verification code to others.";

    public static final String PAASOO_VER_SMS_TEXTSTART_CN = "【ONTO】您的短信验证码为：";

    public static final String PAASOO_VER_SMS_TEXTEND_CN = "，请在五分钟内完成验证。请勿将验证码泄露给其他人。";

    /**
     * senecloud 短信参数
     */
    public static final String SENDCLOUD_SMS_SEND_URL = "http://www.sendcloud.net/smsapi/send";

    public static final String SENDCLOUD_SMS_APIUSER = "ontology_002";

    public static final String SENDCLOUD_SMS_APIKEY = "1UZRHJ38cI5dyboGvYXIswIExLaZ2CdU";

    /**
     * sendcloud 邮件参数
     */
    public static final String SENDCLOUD_EMAIL_TEMPLATEMAIL_URL = "http://api.sendcloud.net/apiv2/mail/sendtemplate";

    public static final String SENDCLOUD_EMAIL_APIUSER = "ontology_001";

    public static final String SENDCLOUD_EMAIL_APIKEY = "AI6d0idHjpOBguIn";


    public static final String ONT_QUERYBALANCE_URL = "/api/v1/balance/";

    public static final String EXPLORER_ONT_HOLDERLIST_URL = "/getAssetHolder?qid=1&contract=0100000000000000000000000000000000000000&from=0&count=1000000";



    /**
     * employment claim context
     */
    public static final String CAREER_CARDCONTEXT = "claimcard:career_expert";

    /**
     * employment claim context
     */
    public static final String EMPLOYMENT_CLAIMCONTEXT = "claim:employment_authentication";

    /**
     * github claim context
     */
    public static final String GITHUB_CLAIMCONTEXT = "claim:github_authentication";

    /**
     * twitter claim context
     */
    public static final String TWITTER_CLAIMCONTEXT = "claim:twitter_authentication";

    /**
     * linkedin claim context
     */
    public static final String LINKEDIN_CLAIMCONTEXT = "claim:linkedin_authentication";

    /**
     * facebook claim context
     */
    public static final String FACEBOOK_CLAIMCONTEXT = "claim:facebook_authentication";

    /**
     * cfca claim context
     */
    public static final String CFCA_CLAIMCONTEXT = "claim:cfca_authentication";


    public static final String SENSETIME_CLAIMCONTEXT = "claim:sensetime_authentication";

    /**
     * identitymind claim context
     */
    public static final String IDM_ID_CLAIMCONTEXT = "claim:idm_idcard_authentication";
    public static final String IDM_PP_CLAIMCONTEXT = "claim:idm_passport_authentication";
    public static final String IDM_DL_CLAIMCONTEXT = "claim:idm_dl_authentication";

    /**
     * url of Ontology official explorer for verify claim
     */
    public static final String VERIFYURL_PREFIX = "https://explorer.ont.io/claimverify";

    /**
     * request param
     */
    public static final String STANDARD_DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * request param
     */
    public static final String CLAIM_DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * request param
     */
    public static final String BEGINTIME_SUFFIX = " 00:00:00";

    /**
     * request param
     */
    public static final String ENDTIME_SUFFIX = " 23:59:59";

    /**
     * request param
     */
    public static final String DEVICECODE = "device";

    /**
     * request param
     */
    public static final String CLAIM = "Claim";

    /**
     * request param
     */
    public static final String AUTH = "Auth";

    /**
     * request param
     */
    public static final String SYSTEMINFO = "SystemInfo";




    public static final String ALL = "ALL";


    /**
     * claim issued from app.
     * include: github,twitter,linkedin,facebook,cfca claim
     *
     */
    public static final List<String> APPENTRANCE_CLAIMCONTEXT_LIST = new ArrayList<String>(
            Arrays.asList(ConstantParam.GITHUB_CLAIMCONTEXT, ConstantParam.TWITTER_CLAIMCONTEXT,
                    ConstantParam.LINKEDIN_CLAIMCONTEXT, ConstantParam.FACEBOOK_CLAIMCONTEXT,
                    ConstantParam.CFCA_CLAIMCONTEXT, ConstantParam.IDM_ID_CLAIMCONTEXT,
                    ConstantParam.IDM_DL_CLAIMCONTEXT, ConstantParam.IDM_PP_CLAIMCONTEXT,
                    ConstantParam.MOBILE_CLAIMCONTEXT, ConstantParam.EMAIL_CLAIMCONTEXT,
                    ConstantParam.SENSETIME_CLAIMCONTEXT, ConstantParam.SFP_CLAIMCONTEXT_DL,
                    ConstantParam.SFP_CLAIMCONTEXT_ID, ConstantParam.SFP_CLAIMCONTEXT_PP)
    );


    /**
     * send transaction url of assetservice
     *
     */
    public static final String SENDTXN_URL = "/api/v1/assetservice/txn/send";

    /**
     * 获取配置参数url
     */
    public static final String QUERYCONFIG_URL = "/api/v1/onto/appconfig/query";


    public static final Pattern ONTID_PATTERN = Pattern.compile("did:ont:.{34}");


    public static final String ONT_SCHEMA = "ont";

    public static final String QRCODE_OPE_LOGIN = "signin";

    public static final String QRCODE_OPE_KYC = "kyc";

    /**
     * shuftipro claim context
     */
    public static final String SFP_CLAIMCONTEXT_ID = "claim:sfp_idcard_authentication";
    public static final String SFP_CLAIMCONTEXT_PP = "claim:sfp_passport_authentication";
    public static final String SFP_CLAIMCONTEXT_DL = "claim:sfp_dl_authentication";


}
