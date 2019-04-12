package com.ontology.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.crypto.Digest;
import com.ontology.exception.OntIdException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhouq
 * @date 2018/02/27
 */
@Component
public class Helper {

    /**
     * check the param whether is null or ''
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


    /**
     * judge whether the string is in json format.
     *
     * @param str
     * @return
     */
    public static Boolean isJSONStr(String str) {
        try {
            JSONObject obj = JSONObject.parseObject(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param dateStr 字符串日期
     * @param format  如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long Date2TimeStamp(String dateStr, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dateStr).getTime() / 1000L;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * get current method name
     *
     * @return
     */
    public static String currentMethod() {
        return new Exception("").getStackTrace()[1].getMethodName();
    }


    //length用户要求产生字符串的长度
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * send http post request
     *
     * @param urlParam
     * @param reqParam
     * @return
     */
    public static String sendPost(String urlParam, String reqParam) throws IOException {

/*        StringBuffer sbParams = new StringBuffer();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Object> e : params.entrySet()) {
                sbParams.append(e.getKey());
                sbParams.append("=");
                sbParams.append(e.getValue());
                sbParams.append("&");
            }
        }*/
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
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
            connection.setRequestProperty("Content-type", "application/json");

            // 写入参数
            out = connection.getOutputStream();
            out.write(reqParam.getBytes("UTF-8"));
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
            e.printStackTrace();
            throw e;
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
     * get请求
     *
     * @param urlParam
     * @param params
     * @return
     * @throws IOException
     */
    public static String sendGet(String urlParam, Map<String, Object> params) throws IOException {

        // 构建请求参数
        StringBuffer sbParams = new StringBuffer();
        if (params != null && params.size() > 0) {
            sbParams.append("?");
            for (Map.Entry<String, Object> e : params.entrySet()) {
                sbParams.append(e.getKey());
                sbParams.append("=");
                sbParams.append(e.getValue());
                sbParams.append("&");
            }
        }
        HttpURLConnection connection = null;
        OutputStream out = null;
        BufferedReader responseReader = null;
        // 发送请求
        try {
            URL url = new URL(urlParam + sbParams.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //connection.setDoOutput(true);
            //设置超时时间
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            //connection.setRequestProperty("Content-type", "application/json");
            connection.connect();

            //获取输出
            StringBuffer sb = new StringBuffer();
            String readLine = "";
            responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine);
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
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
     * hash
     */
    public static String sha256(String data) {
        byte[] bytes = Digest.sha256(data.getBytes());
        return com.github.ontio.common.Helper.toHexString(bytes);
    }

    /**
     * verify phone
     */
    public static void verifyPhone(String action, String phone) {
        String[] split = phone.split("\\*");
        if (Helper.isEmptyOrNull(split) || split.length != 2 || phone.contains("+")) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
    }

    /**
     * 拼接JSON格式参数
     * lijie 2019/3/20
     */
    public static String getParams(String ontid, String contractHash, String method, List argsList, String payer) {
        Map map = new HashMap();
        Map parms = new HashMap();
        Map invokeConfig = new HashMap();
        List functions = new ArrayList();
        Map function = new HashMap();

        function.put("operation",method);
        function.put("args",argsList);

        functions.add(function);

        invokeConfig.put("contractHash",contractHash);
        invokeConfig.put("functions",functions);
        invokeConfig.put("payer",payer);
        invokeConfig.put("gasLimit",40000);
        invokeConfig.put("gasPrice",500);

        parms.put("invokeConfig",invokeConfig);
        parms.put("ontid",ontid);

        map.put("action","invoke");
        map.put("params",parms);
        return JSON.toJSONString(map);
    }
}
