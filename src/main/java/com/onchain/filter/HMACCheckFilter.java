package com.onchain.filter;

import com.onchain.dao.Developer;
import com.onchain.exception.OntIdException;
import com.onchain.mapper.DeveloperMapper;
import com.onchain.secure.MD5Utils;
import com.onchain.secure.RSAUtil;
import com.onchain.utils.ConstantParam;
import com.onchain.utils.ErrorInfo;
import com.onchain.utils.Helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import static com.onchain.utils.Constant.EXPIRED_SECOND;

/**
 * @author zhouq
 * @version 1.0
 * @date 2018/8/27
 */
@Slf4j
@Order(2)
@Component
public class HMACCheckFilter implements Filter {

    private static final String TEST_URI = "/api/v1/ontid/test/";

    @Autowired
    private DeveloperMapper developerMapper;

    @Autowired
    private RSAUtil rsaUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        MyHttpRequest myrequest = new MyHttpRequest((HttpServletRequest) servletRequest);
        String appVersion = myrequest.getHeader("Version");
        //只针对有风险的API做检查
        String reqUri = myrequest.getRequestURI();
        if (!reqUri.contains(TEST_URI)) {
            try {
                checkReqHeaderHmac(myrequest);
            } catch (Exception e) {
                log.error("header-error:{}", e.toString());
                throw new OntIdException("HMAC verify", ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
            }
        }
        chain.doFilter(myrequest, response);
    }

    @Override
    public void destroy() {

    }


    /**
     * hmac检查
     *
     * @param myrequest
     */
    private void checkReqHeaderHmac(MyHttpRequest myrequest) throws Exception {

        //header = string.Format("{ont}:{0}:{1}:{2}:{3}", ont APPId, signature, nonce, requestTimeStamp)
        String authParam = myrequest.getHeader("Authorization");
        log.info("header-authorization:{}", authParam);

        String[] authParamArray = authParam.split(":");
        if (!ConstantParam.ONT_SCHEMA.equals(authParamArray[0])) {
            throw new OntIdException("HMAC verify", ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }

        //从db根据appid获取appkey
        String appId = authParamArray[1];
        Developer developer = new Developer();
        developer.setAppId(appId);
        developer = developerMapper.selectOne(developer);
        if (Helper.isEmptyOrNull(developer)) {
            throw new OntIdException("HMAC verify", ErrorInfo.VERIFY_FAILED.descCN(), ErrorInfo.VERIFY_FAILED.descEN(), ErrorInfo.VERIFY_FAILED.code());
        }

        String encrpAppSecret = developer.getAppSecret();
        log.info("appId: {}, encrpAppSecret: {}", appId, encrpAppSecret);
        if (Helper.isEmptyOrNull(encrpAppSecret)) {
            throw new OntIdException("HMAC verify", ErrorInfo.VERIFY_FAILED.descCN(), ErrorInfo.VERIFY_FAILED.descEN(), ErrorInfo.VERIFY_FAILED.code());
        }

        String appSecret = rsaUtil.decryptByPrivateKey(encrpAppSecret);
        // log.info("appSecret:{}", appSecret);

        //检查请求时间是否超过阈值
        String reqTimeStamp = authParamArray[4];
        long nowTimeStamp = System.currentTimeMillis() / 1000L;
        //   log.info("reqTimeStamp:{}, nowTimeStamp:{}", reqTimeStamp, nowTimeStamp);
        //时间阈值
        if (nowTimeStamp - Long.valueOf(reqTimeStamp) > EXPIRED_SECOND) {
            log.error("request timestamp expired");
            throw new OntIdException("HMAC verify", ErrorInfo.EXPIRES.descCN(), ErrorInfo.EXPIRES.descEN(), ErrorInfo.EXPIRES.code());
        }

        String localSignature = "";
        String reqPayLoadBase64MD5Str = "";

        if (myrequest.getMethod().equals(RequestMethod.POST.name())) {
            String body = myrequest.getBody();
            log.info("payload:{}", body);
            //对payload进行MD5+Base64计算
            reqPayLoadBase64MD5Str = Base64.getEncoder().encodeToString(MD5Utils.MD5Encode(body));
        } else {
            throw new OntIdException("HMAC verify", ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
        //log.info("payload-Base64MD5Str:" + reqPayLoadBase64MD5Str);

        String reqUri = myrequest.getRequestURI();
        String method = myrequest.getMethod();
        //log.info("requestUri:{}, method:{}", reqUri, method);
        //构造待签名数据
        StringBuilder sigRawData = new StringBuilder();
        sigRawData.append(appId);
        sigRawData.append(method);
        sigRawData.append(reqUri);
        sigRawData.append(reqTimeStamp);
        sigRawData.append(authParamArray[3]);
        sigRawData.append(reqPayLoadBase64MD5Str);
        log.info("hmac-rawdata:{}", sigRawData.toString());
        //hmac签名
        localSignature = Base64.getEncoder().encodeToString(HMACSha256.sha256_HMAC(sigRawData.toString(), appSecret));


        String reqSignature = authParamArray[2];
        log.info("localSignature:{}, reqSignature:{}", localSignature, reqSignature);
        //签名比对
//        if (!reqSignature.equals(localSignature)) {
//            throw new OntIdException("HMAC verify", ErrorInfo.VERIFY_FAILED.descCN(), ErrorInfo.VERIFY_FAILED.descEN(), ErrorInfo.VERIFY_FAILED.code());
//        }
    }

    /**
     * 构造401错误返回
     *
     * @param language
     * @param response
     * @throws Exception
     */
    private void constructErroResponse(String language, ServletResponse response) {
//        if (!verifyFlag) {
//            String language = myrequest.getHeader("Language");
//            //认证失败，构造401错误返回
//            constructErroResponse(language, response);
//            return;
//        }

        //log.info("construct 401 Response");
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json; charset=utf-8");
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

//        Result result = new Result();
//        if (ConstantParam.CN.equals(language)) {
//            result = Helper.result("", ErrorInfo.IDENTITY_VERIFY_FAILED.code(), ErrorInfo.IDENTITY_VERIFY_FAILED.descCN(), VERSION, false);
//        } else {
//            result = Helper.result("", ErrorInfo.IDENTITY_VERIFY_FAILED.code(), ErrorInfo.IDENTITY_VERIFY_FAILED.descEN(), VERSION, false);
//        }
//        try {
//            httpResponse.getWriter().write(mapper.writeValueAsString(result));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

}
