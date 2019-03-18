package com.ontology.utils;


import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhouq
 * @date 2017/11/23
 */
@Component
public class HelperUtil {

    private static final Logger logger = LoggerFactory.getLogger(HelperUtil.class);

    /**
     * check param whether is null or ''
     *
     * @param params
     * @return boolean
     */
    public static Boolean isEmptyOrNull(Object... params) {
        if (params != null) {
            for (Object val : params) {
                if ("".equals(val) || val == null) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public static Boolean isNotEmptyAndNotNull(Object... params) {
        return !(HelperUtil.isEmptyOrNull(params));
    }
    /**
     * merge byte[] head and byte[] tail ->byte[head+tail] rs
     *
     * @param head
     * @param tail
     * @return byte[]
     */
    public static byte[] byteMerrage(byte[] head, byte[] tail) {
        byte[] temp = new byte[head.length + tail.length];
        System.arraycopy(head, 0, temp, 0, head.length);
        System.arraycopy(tail, 0, temp, head.length, tail.length);
        return temp;
    }


    public static String byteToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String sTemp;
        for (int i = 0; i < bytes.length; i++) {
            sTemp = Integer.toHexString(0xFF & bytes[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * get current method name
     *
     * @return
     */
    public static String currentMethod() {
        return new Exception("").getStackTrace()[1].getMethodName();
    }


    public static String getBasicAuthHeader(String appId, String appKey) {

        String auth = appId + ":" + appKey;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        return authHeader;

    }


    /**
     * 从请求头中获取账户信息，目前是email
     *
     * @param httpServletRequest
     * @return
     */
//    public static String getIdentityInfoFromHeader(HttpServletRequest httpServletRequest) {
//        String token = httpServletRequest.getHeader(Constant.HTTPHEADER_AUTHORIZATION);
//        String email = JwtUtil.getClaim(token, Constant.JWT_EMAIL);
//        return email;
//    }

    /**
     * 从请求头中获取请求信息
     *
     * @param httpServletRequest
     * @return
     */
    public static String getParamFromHeader(HttpServletRequest httpServletRequest, String headerKey) {
        String param = httpServletRequest.getHeader(headerKey);
        return param;
    }

    /**
     * 设置包含账户email的token到返回头中
     *
     * @param httpServletResponse
     * @param email
     */
//    public static void setResponseToken(HttpServletResponse httpServletResponse, String email) {
//        // 从Header中Authorization返回AccessToken，时间戳为当前时间戳
//        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
//        String token = JwtUtil.sign(email, currentTimeMillis);
//        httpServletResponse.setHeader(Constant.HTTPHEADER_AUTHORIZATION, token);
//    }
//
//    public static void setTempResponseToken(HttpServletResponse httpServletResponse, String email){
//        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
//        String token = JwtUtil.signTemp(email, currentTimeMillis);
//        httpServletResponse.setHeader(Constant.HTTPHEADER_AUTHORIZATION, token);
//    }


    /**
     * 设置包含账户email的临时token到返回头中
     *
     * @param httpServletResponse
     * @param email
     */
/*    public static void setResponseTemporaryToken(HttpServletResponse httpServletResponse, String email) {
        // 从Header中Authorization返回AccessToken，时间戳为当前时间戳
        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        String token = JwtUtil.signforTempToken(email, currentTimeMillis);
        httpServletResponse.setHeader(Constant.HTTPHEADER_AUTHORIZATION, token);
    }*/


    /**
     * 生成随机业务号
     * yyyyMMddHHmmss+busType+4位随机码
     *
     * @param busType
     * @return
     */
    public static String generateTxId(String busType) {
        SimpleDateFormat sfp = new SimpleDateFormat("yyyyMMddHHmmss");
        sfp.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
        String date = sfp.format(new Date());
        String a = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        String str = date + busType + a;
        return str;
    }


    /**
     * 生成随机请求号，与底层通信用
     * yyyyMMdd+6位随机码
     *
     * @return
     */
    public static String generateMsgId() {
        SimpleDateFormat sfp = new SimpleDateFormat("yyyyMMdd");
        sfp.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
        String date = sfp.format(new Date());
        String a = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        String str = date + a;
        return str;
    }


    /**
     * 根据当前时间生成流水表的主键
     * 加锁机制，避免生成用一主键
     *
     * @return
     */
    public static long generateOptSeq() {
        long currentTime = System.currentTimeMillis();
        return currentTime;
    }

    /**
     * 获取真实请求ip
     *
     * @param request
     * @return
     */
    public static String getHttpReqRealIp(HttpServletRequest request) {

        String ip = "";
        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }
        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }
        //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }

        if (ip.contains(":")) {
            int index = ip.indexOf(":");
            ip = ip.substring(0, index);
        }
        return ip;
    }


    /**
     * 获取服务器真实ip
     *
     * @return
     */
    public static String getServerIp() {
        String ipAddr = "";
        // 获取操作系统类型
        String sysType = System.getProperties().getProperty("os.name");
        try {
            if (sysType.toLowerCase().startsWith("win")) {  // 如果是Windows系统，获取本地IP地址
                ipAddr = InetAddress.getLocalHost().getHostAddress();
            } else {
                Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
                while (allNetInterfaces.hasMoreElements()) {
                    NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                    if ("eth0".equals(netInterface.getName())) {
                        Enumeration addresses = netInterface.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            InetAddress ip = (InetAddress) addresses.nextElement();
                            if (ip != null && ip instanceof Inet4Address) {
                                ipAddr = ip.getHostAddress();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("error...", e);
        }
        return ipAddr;
    }

    /**
     * 发送http post 请求
     *
     * @param urlParam
     * @param reqBody
     * @return
     */
    public static String sendHttpPostReq(String urlParam, String reqBody, Map<String, String> headerMap) throws IOException {

        HttpURLConnection connection = null;
        OutputStream out = null;
        BufferedReader responseReader = null;
        // 发送请求
        try {
            URL url = new URL(urlParam);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            //设置超时时间
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Content-type", "application/json");
            //添加请求头信息
            if (!HelperUtil.isEmptyOrNull(headerMap)) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            //写入参数
            out = connection.getOutputStream();
            out.write(reqBody.getBytes("UTF-8"));
            out.flush();

            //获取输出
            StringBuffer sb = new StringBuffer();
            String readLine = "";
            responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine);
            }

            return sb.toString();
        } catch (IOException e) {
            int code = connection.getResponseCode();
            logger.error("responseCode:{}....", code, e);
            throw e;
/*            if (code == 400) {
                StringBuilder errorSb = new StringBuilder();
                errorSb.append(code);
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8"));
                String readLine = "";
                while ((readLine = errorReader.readLine()) != null) {
                    errorSb.append(readLine);
                }
                logger.error("sendHttpPostReq error response:{}", errorSb.toString());
                return errorSb.toString();
            } else {
                throw e;
            }*/
        } finally {
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
            if (out != null) {
                out.close();
            }
            if (responseReader != null) {
                responseReader.close();
            }
        }

    }

    /**
     * generate hex-string
     *
     * @param address
     * @param availableBalance
     * @param currentBalance
     * @return
     */
    public static String generateDBMAC(String address, BigDecimal availableBalance, BigDecimal currentBalance) {
        StringBuilder stringBuilder = new StringBuilder()
                .append(address)
                .append(availableBalance.doubleValue())
                .append(currentBalance.doubleValue());
        byte[] encodedhash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            encodedhash = digest.digest(
                    stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            logger.error("error...", e);
        }
        if (encodedhash == null) return null;
        return HelperUtil.byteToHexString(encodedhash);
    }

    public static List<String> parseMultiChoiceString(String data){
        List<String> result = new ArrayList<>();
        if(HelperUtil.isEmptyOrNull())
            return result;
        Collections.addAll(result, data.split("\\|\\|"));
        return result;
    }

    public static Date getUtcDate(Long timestamp){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Date date = new Date(timestamp);
        return date;
    }

//    public static void main(String[] args){
//        List<String> list1 = Arrays.asList("12","123");
//        List<String> list2 = Arrays.asList("45","24234");
//        List<String> signInprocessBusNoList = new ArrayList<>(ActivityEnum.withdraw_operate_pendingforsign.getBusNoList());
//        signInprocessBusNoList.addAll(ActivityEnum.withdraw_operate_inprocess.getBusNoList());
//    }
}
