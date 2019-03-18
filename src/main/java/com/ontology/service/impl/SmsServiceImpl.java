package com.ontology.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ontology.ConfigParam;
import com.ontology.dao.SMS;
import com.ontology.exception.OntIdException;
import com.ontology.mapper.SMSMapper;
import com.ontology.model.Result;
import com.ontology.service.ISmsService;
import com.ontology.utils.Constant;
import com.ontology.utils.ConstantParam;
import com.ontology.utils.ErrorInfo;
import com.ontology.utils.Helper;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by ZhouQ on 2017/8/31.
 */
@Service("SmsServiceImpl")

public class SmsServiceImpl implements ISmsService {
    @Autowired
    private SMSMapper mapper;

    private Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Autowired
    private ConfigParam configParam;

    /**
     * sendcloud平台通过短信模板发送验证码
     *
     * @throws Exception
     */
    private void sendCloudCode(String phoneNumber, String verificationCode) throws Exception {
        String action = "getVerificationCode";

        Helper.verifyPhone(action, phoneNumber);

        //手机号格式：国家区号"*"手机号，sendcloud平台需去掉区号
        String nation = "";
        String scPhoneNumber = "";
        if (phoneNumber.startsWith("+86*") || phoneNumber.startsWith("86*")) {
            nation = "CN";
            scPhoneNumber = phoneNumber.substring(phoneNumber.indexOf("*") + 1, phoneNumber.length());
        } else {
            //国际短信格式：00+国际区号+手机号
            nation = "EN";
            scPhoneNumber = "00" + phoneNumber.replace("*", "");
        }
        logger.info("phnumber:{}, nation:{}", scPhoneNumber, nation);

        // 填充参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("smsUser", ConstantParam.SENDCLOUD_SMS_APIUSER);

        if (ConstantParam.CN.equals(nation)) {
            params.put("templateId", configParam.SC_SMS_VER_TEMPLATE_CN);
            params.put("msgType", "0");
        } else {
            params.put("templateId", configParam.SC_SMS_VER_TEMPLATE_EN);
            params.put("msgType", "2");
        }
        params.put("phone", scPhoneNumber);

        params.put("vars", "{\"code\":" + verificationCode + "}");

        // 对参数进行排序
        Map<String, String> sortedMap = new TreeMap<String, String>(new Comparator<String>() {
            public int compare(String arg0, String arg1) {
                // 忽略大小写
                return arg0.compareToIgnoreCase(arg1);
            }
        });
        sortedMap.putAll(params);

        // 计算签名
        StringBuilder sb = new StringBuilder();
        sb.append(ConstantParam.SENDCLOUD_SMS_APIKEY).append("&");
        for (String s : sortedMap.keySet()) {
            sb.append(String.format("%s=%s&", s, sortedMap.get(s)));
        }
        sb.append(ConstantParam.SENDCLOUD_SMS_APIKEY);
        String sig = DigestUtils.md5Hex(sb.toString());

        // 将所有参数和签名添加到post请求参数数组里
        List<NameValuePair> postparams = new ArrayList<NameValuePair>();
        for (String s : sortedMap.keySet()) {
            postparams.add(new BasicNameValuePair(s, sortedMap.get(s)));
        }
        postparams.add(new BasicNameValuePair("signature", sig));

        HttpPost httpPost = new HttpPost(ConstantParam.SENDCLOUD_SMS_SEND_URL);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postparams, "utf8"));
            CloseableHttpClient httpClient;
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000).setSocketTimeout(100000).build();
            httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String responseStr = EntityUtils.toString(entity);
            logger.info("####sendcloud sms response :{}", responseStr);

            JSONObject responseObj = JSON.parseObject(responseStr);
            if (responseObj.getInteger("statusCode") != 200) {
                logger.error("####sendcloud sms response", responseObj.toJSONString());
                httpPost.releaseConnection();

                if (Helper.isEmptyOrNull(responseObj.getString("message"))) {
                    action = responseObj.getString("message");
                }
                throw new OntIdException(action, ErrorInfo.COMM_FAIL.descCN(), ErrorInfo.COMM_FAIL.descEN(), ErrorInfo.COMM_FAIL.code());
            }

        } catch (Exception e) {
            logger.error("####sendcloud sms response error...", e);
            throw new OntIdException("sms error", ErrorInfo.COMM_FAIL.descCN(), ErrorInfo.COMM_FAIL.descEN(), ErrorInfo.COMM_FAIL.code());
        } finally {
            httpPost.releaseConnection();
        }
    }

    @Override
    public Result sendCode(String phone) throws Exception {
        String action = "getVerificationCode";

        String verificationCode = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        // 判断手机
        SMS sms = new SMS();
        sms.setPhone(phone);
        sms = mapper.selectOne(sms);
        if (sms != null) {
            //防刷，距离上次时间大于60秒
            long minTime = Constant.SEND_VERIFY_CODE_LIMIT + sms.getUpdateTime().getTime();
            if (new Date(minTime).after(new Date())) {
                throw new OntIdException(action, ErrorInfo.TIME_EXCEEDED.descCN(), ErrorInfo.TIME_EXCEEDED.descEN(), ErrorInfo.TIME_EXCEEDED.code());
            }
        }

        sendCloudCode(phone, verificationCode);

        //更新时间,和code
        int i;
        if (sms == null) {
            sms = new SMS();
            sms.setPhone(phone);
            sms.setVerifyCode(verificationCode);
            sms.setUpdateTime(new Date());
            mapper.insertSelective(sms);
        } else {
            sms.setVerifyCode(verificationCode);
            sms.setUpdateTime(new Date());
            mapper.updateByPrimaryKeySelective(sms);
        }

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), true);
    }

    private void sendPAASOOCode(String phone, String verificationCode) throws Exception {
        // 手机号格式：国家区号"*"手机号
        String paPhoneNumber = phone.replace("*", "");
        //中英文内容区别
        String nation = phone.startsWith("+86") || phone.startsWith("86") ? "CN" : "EN";
        String text = ConstantParam.CN.equals(nation) ? ConstantParam.PAASOO_VER_SMS_TEXTSTART_CN + verificationCode + ConstantParam.PAASOO_VER_SMS_TEXTEND_CN : ConstantParam.PAASOO_VER_SMS_TEXTSTART_EN + verificationCode + ConstantParam.PAASOO_VER_SMS_TEXTEND_EN;

        //发短信
        paasooSendSms(ConstantParam.PAASOO_SMS_APIUSER, ConstantParam.PAASOO_SMS_APISECRET, configParam.PS_SMS_SENDER, paPhoneNumber, text);
    }

    @Override
    public void verifyPhone(String action, String phone, String code) throws Exception {
        SMS sms = new SMS();
        sms.setPhone(phone);
        sms = mapper.selectOne(sms);
        if (sms != null) {
            long maxTime = Constant.VERIFY_CODE_VALIDITY + sms.getUpdateTime().getTime();
            if (new Date(maxTime).before(new Date())) {
                throw new OntIdException(action, ErrorInfo.EXPIRES.descCN(), ErrorInfo.EXPIRES.descEN(), ErrorInfo.EXPIRES.code());
            }
        } else {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        logger.info("requestCode & sqlCode", code + " & " + sms.getVerifyCode());
        if (!sms.getVerifyCode().equals(code)) {
            throw new OntIdException(action, ErrorInfo.VERIFY_FAILED.descCN(), ErrorInfo.VERIFY_FAILED.descEN(), ErrorInfo.VERIFY_FAILED.code());
        }
    }


    /**
     * 短信发送
     *
     * @param key    API帐号
     * @param secret API密码
     * @param from   SenderID
     * @param to     发送目标号码
     * @param text   发送内容
     * @return json  格式字符串
     * @throws Exception
     */
    private void paasooSendSms(String key, String secret, String from, String to, String text) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        params.put("secret", secret);
        params.put("from", from);
        params.put("to", to);
        params.put("text", text);
        get(ConstantParam.PAASOO_SMS_SEND_URI, params);
    }

    /**
     * 基于HttpClient 4.X的通用GET方法
     *
     * @param url       提交的URL
     * @param paramsMap 提交<参数，值>Map
     * @return 提交响应
     */
    private void get(String url, Map<String, String> paramsMap) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        InputStream is = null;
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                params.add(pair);
            }
            String str = EntityUtils.toString(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpGet httpGet = new HttpGet(url + "?" + str);
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            String result = EntityUtils.toString(entity);
            logger.info("PaaSoo send sms response :{}", result);
            JSONObject responseObj = JSONObject.parseObject(result);
            String status = responseObj.getString("status");
            if (!"0".equals(status)) {
                logger.error("PaaSoo send sms fail...status:{}", status);
                throw new OntIdException("getVerifyCode", ErrorInfo.COMM_FAIL.descCN(), ErrorInfo.COMM_FAIL.descEN(), ErrorInfo.COMM_FAIL.code());
            }
        } finally {
            if (is != null) {
                is.close();
            }
            if (response != null) {
                response.close();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        }

    }
}
