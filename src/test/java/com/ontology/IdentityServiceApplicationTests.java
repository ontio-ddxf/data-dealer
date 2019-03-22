package com.ontology;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.ontio.account.Account;
import com.ontology.filter.HMACSha256;
import com.ontology.model.RequestBean;
import com.ontology.model.Result;
import com.ontology.secure.AESUtil;
import com.ontology.secure.RSAUtil;
import com.ontology.secure.SecureHttpMessageConverter;
import com.ontology.utils.Constant;
import com.ontology.secure.MD5Utils;
import com.ontology.utils.Base64ConvertUtil;
import com.ontology.utils.Helper;

import com.ontology.utils.SDKUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IdentityServiceApplicationTests {

    @LocalServerPort
    private int port;

    private String subType = SecureHttpMessageConverter.SubType;

    private URL base;

    @Autowired
    AESUtil utilAES;

    @Autowired
    RSAUtil utilRSA;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SDKUtil sdkUtil;

//    @Test
//    public void sdk() throws Exception {
//        Account payerAcct = sdkUtil.createPayerAcct();
//        PrivateKey privateKey = payerAcct.getPrivateKey();
//        System.out.println(privateKey.toString());
//    }
//    @Before
//    public void setUp() throws Exception {
////        port = 7070;
//        String url = String.format("http://localhost:%d/api/v1/ontid", port);
////        String url = "http://139.219.136.188:10330";
//
//        this.base = new URL(url);
//
////		org.apache.shiro.mgt.SecurityManager securityManger = mock(org.apache.shiro.mgt.SecurityManager.class, RETURNS_DEEP_STUBS);
////		ThreadContext.bind(securityManger);
//    }

    @Test
    public void contextLoads() {
//        String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDEFXqBqyJvX4jnFgHgTEkQGyouIs5wPC44ZydouoKogArV26gDk+OrdcaXLMDLrqSyFN0eoGgx8erMKTtK41S33xUsv8icd83ZEQauvaBHIsQxjBpcVHXPsmmhT6ZNi3yy18cUyeyn7qbyldfxDAORx9QZV2YL3OetptjUz02g5wIDAQAB";
//        try {
//            String encode = Base64ConvertUtil.encode(pubKey);
//            String rsaPublicKey = new SecureConfig().getRsaPublicKey();
//            System.out.println(encode.equalsIgnoreCase(rsaPublicKey));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        try {
            testEncrypt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testEncrypt() throws Exception {
        String data = "HV/pkrfQzTCCAlIwZ03lmmFlqaRdMnZXj9mvrI6Q6KT605F0LYpLSVTPMe1t+Wi8hpM+xntScw6JVFvuGoW8ivtgSevqQaq1CufdyNbwWCWhthmgstzqAAoR+Sgjk04TzBmpbcoY/JPO9Xijj5Vg1fPZu4+uSjC/7uRKAS7Yk46FcoY16ndrM93fF8gqYnISiZAYEjSi2PQeSRUIKxXHu5N3LJoHDM633eutzekbEajgwSks6Rskx8li3FtIBWvsK0Fs3uUch76CMlUxx0ZdnkcWP2t20WJ3Fe8OjVB5T3DIKnK1p43dHPSBh2eeX/e41i/FVY2PuF2liowhjQokcChbiAoRxtw19suFNxeW8dotXj5OPo/NyrP0EcxMao1lBsLUsOj7BVdyWlmP5SAEdOYjVGiqL9g+P6xQWFrZaIZQ8ny5GRHq/jJ3Ym6462JZUmnibtswBLiB57nm/y7TvNu9UsGzktfXde92XrfCTF38ji6VHU7/TL97EtBfevJB2d4rkwCVvAY5roRvlPLzlBpITviSm/kJG+Lsr+8P7hRe7Ord0BQ9Q0A96zLgtNRfB3/AbhsLXYtHdiGdFR4Ka/QLNr8G6kE54j7BGPHAYBw3FpeWzTrGVQCvi1gVtEzPP6/1jhjUD6dQfR2j//VvsrtqUn+Qx4fsDiS7SrmNpGk=";
//        utilAES.decryptData("")
        String key = "HpoN7wI9rbyArpWBUJbSNjRH1MxKb4O33Wmg8CavC6ALJ/24mBexubXODLr18ogpqLyUSQscCeKuNNhSVxltzls4VIDu/48ip6EVnla++NY6yeemS7+30txU7AACIzmCnU1v8Eshu866VaLpkwfb0+djJr/HheU5pfKSnxe0QnU=";
        String random = utilRSA.decryptByPrivateKey(key);
        String s = utilAES.decryptData(random, data);
        System.out.println(s);
    }

    @Test
    public void test() {
        try {
//            sendCode();
//            registerPhone();
//            postTransfer();
//            bind();
//            login();
//            addAppkey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCode() throws Exception {
        final String URI = this.base.toString() + "/getcode/phone";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", "86*15951496186");
        postHmac(URI, jsonObject);
    }

    public void registerPhone() throws Exception {
        final String URI = this.base.toString() + "/register/phone";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", "+86*15951496186");
        jsonObject.put("verifyCode", "726058");
        jsonObject.put("password", "123456");
        String json = JSON.toJSONString(jsonObject);

        postHmac(URI, jsonObject);
    }

    public void login() throws Exception {
        final String URI = this.base.toString() + "/login/phone";
//        final String URI = this.base.toString() + "/login/password";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", "+86*15951496186");
        jsonObject.put("verifyCode", "178540");
//        jsonObject.put("password", "123456");

        postHmac(URI, jsonObject);
    }

    public void edit() {
//        final String URI = this.base.toString() + "/edit/phone";
        final String URI = this.base.toString() + "/edit/password";
        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("newPhone", "+86*15951496186");
//        jsonObject.put("verifyCode", "356376");
//        jsonObject.put("oldPhone", "123");
//        jsonObject.put("password", "123456");

        jsonObject.put("phone", "+86*15951496186");
        jsonObject.put("oldPassword", "123");
        jsonObject.put("newPassword", "123456");
        String json = JSON.toJSONString(jsonObject);

        postDecrypt(URI, json);
    }

    public void export() {
//        final String URI = this.base.toString() + "/export/keystore";
//        final String URI = this.base.toString() + "/export/phone";
        final String URI = this.base.toString() + "/export/wif";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ontid", "did:ont:ANPn6GvsLEst9nzdaSoecA3g3gp3vnyy1Y");
        jsonObject.put("password", "123456");
        String json = JSON.toJSONString(jsonObject);

        postDecrypt(URI, json);
    }

    public void bind() throws Exception {
        final String URI = this.base.toString() + "/binding";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("verifyCode", "791479");
        String key = "{\"address\":\"ALke62hst6FUAZyWapTcYQPiHMUYswYYgR\",\"salt\":\"nsH44Zb4j0w5CZFzuXsTtA==\",\"label\":\"测试\",\"type\":\"A\",\"parameters\":{\"curve\":\"P-256\"},\"scrypt\":{\"dkLen\":64,\"n\":4096,\"p\":8,\"r\":8},\"key\":\"t5r0yjHy/Liy1sfhiJ8ZT4aGkZX1K8pegu5kdzGCJs/uquNd18vjiYbKdx4geT+v\",\"algorithm\":\"ECDSA\"}";
        String encode = Base64ConvertUtil.encode(key);
        jsonObject.put("keystore", encode);
        jsonObject.put("phone", "+86*15951496186");
        jsonObject.put("password", "11111111");
        String json = JSON.toJSONString(jsonObject);
        postHmac(URI, jsonObject);
//        postDecrypt(URI, json);
    }

    public void verify() {
        final String URI = this.base.toString() + "/verify";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ontid", "did:ont:ANPn6GvsLEst9nzdaSoecA3g3gp3vnyy1Y");
        jsonObject.put("password", "456");
        String json = JSON.toJSONString(jsonObject);

        postDecrypt(URI, json);
    }

    public void decryptData() {
        final String URI = this.base.toString() + "/decrypt/claim";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ontid", "did:ont:Ad8sSaGG9DwvPGPceCo7saghaKhWZEnxkC");
        jsonObject.put("password", "123456");
        JSONArray array = new JSONArray();
        array.add("");
        array.add("");
        array.add("");
        jsonObject.put("message", array);
        String json = JSON.toJSONString(jsonObject);

        postDecrypt(URI, json);
    }

    public void getTx() {
        final String URI = this.base.toString() + "/gettx/register/ontid";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ontid", "did:ont:ANPn6GvsLEst9nzdaSoecA3g3gp3vnyy1Y");
//        jsonObject.put("password", "123456");
        String json = JSON.toJSONString(jsonObject);

        postDecrypt(URI, json);
    }

    private void postDecrypt(String URI, String json) {
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        restTemplate.setMessageConverters(Arrays.asList(new SecureHttpMessageConverter()));
        final HttpHeaders headers = new HttpHeaders();
        String key = utilAES.generateKey();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String enKey = null;
        try {
//			json = ow.writeValueAsString(jsonObject);
            enKey = utilRSA.encryptByPublicKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        String enJson = utilAES.encryptData(key, json);
        RequestBean requestBean = new RequestBean(enJson);

        headers.set(Constant.HTTP_HEADER_SECURE, enKey);
        headers.setAccept(Arrays.asList(new MediaType("application", SecureHttpMessageConverter.SubType)));
        headers.setContentType(new MediaType("application", SecureHttpMessageConverter.SubType));

        final HttpEntity<RequestBean> entity = new HttpEntity<RequestBean>(requestBean, headers);
        final ResponseEntity<Result> response = restTemplate.postForEntity(URI, entity, Result.class);
        System.out.println(response.getBody().Result);
        HttpHeaders headers1 = response.getHeaders();
    }

    private static String AppId = "mdgDyjj4";
    private static String AppSecret = "cOLo1W+NlZy9wUzWuMARUg==";

    private void postHmac(String URI, JSONObject json) throws Exception {
        //加密
//        String key = utilAES.generateKey();
        String key = "dbe5baa7686a4e23";
        String enKey = null;
        try {
//			json = ow.writeValueAsString(jsonObject);
            enKey = utilRSA.encryptByPublicKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        String enJson = utilAES.encryptData(key, JSON.toJSONString(json));
        RequestBean requestBean = new RequestBean(enJson);


        //hmac
        String requestContentBase64String = "";

        byte[] md5 = MD5Utils.MD5Encode(JSON.toJSONString(requestBean));
        requestContentBase64String = Base64ConvertUtil.encode(md5);

        Long tsLong = System.currentTimeMillis() / 1000;
        String requestTimeStamp = tsLong.toString();

        UUID uuid = UUID.randomUUID();
        String nonce = Base64ConvertUtil.encode(uuid.toString().getBytes());

        String rawData = AppId + "POST" + "/api/v1/ontid/login/phone" + requestTimeStamp + nonce + requestContentBase64String;
        String signature = Base64ConvertUtil.encode(HMACSha256.sha256_HMAC(rawData, AppSecret));
        String authHMAC = String.format("ont:%s:%s:%s:%s", AppId, signature, nonce, requestTimeStamp);

        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));

//        restTemplate.setMessageConverters(Arrays.asList(new SecureHttpMessageConverter()));
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHMAC);


        headers.set(Constant.HTTP_HEADER_SECURE, enKey);
        headers.setAccept(Arrays.asList(new MediaType("application", SecureHttpMessageConverter.SubType)));
        headers.setContentType(new MediaType("application", SecureHttpMessageConverter.SubType));
        final HttpEntity<RequestBean> entity = new HttpEntity<RequestBean>(requestBean, headers);
        final ResponseEntity<Result> response = restTemplate.postForEntity(URI, entity, Result.class);
        System.out.println("http code:" + response.getStatusCode().value());
        System.out.println(response.getBody().Result);
        HttpHeaders headers1 = response.getHeaders();
    }


    private void postTransfer() {
        final String URI = this.base.toString() + "/test/forward";
        String ontid = "did:ont:AKfCVgiapngaP16a4qxjMFgFJRhwRrw4jk";
        JSONObject json = new JSONObject();
        json.put("ontid", ontid);
        //加密
        String key = utilAES.generateKey();
        String enKey = null;
        try {
            enKey = utilRSA.encryptByPublicKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        String enJson = utilAES.encryptData(key, JSON.toJSONString(json));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url", "/api/v1/ontid/gettx/register/ontid");
        jsonObject.put("secure", enKey);
        jsonObject.put("data", enJson);
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        final HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(jsonObject);
        final ResponseEntity<Result> response = restTemplate.postForEntity(URI, entity, Result.class);
        System.out.println("http code:" + response.getStatusCode().value());
        System.out.println(response.getBody().Result);
        HttpHeaders headers1 = response.getHeaders();
    }


    private void addAppkey() throws Exception {
//        String enSecret = "eb800d86ed6f400537eb33e99fc7d5f69f4f91fb3efdf6194b216f61214bc064";
//        String enSecret = "034462b91f044a6feeb28b1074997dfd32da7eb1635e98894ffa238bc84311d1";
//        String appSecret = new String(Objects.requireNonNull(AESService.AES_CBC_Decrypt(com.github.ontio.common.Helper.hexToBytes(enSecret))));
//        cOLo1W+NlZy9wUzWuMARUg==
//        7qLNf3Yw2mgqdf+B9CaZLw==
        //RSA對userid加密
//        String dbAppSecret = utilRSA.encryptByPublicKey(appSecret);
//        System.out.println("dbAppSecret : " + dbAppSecret);

        //新生成
        String salt = Helper.getRandomString(6);
        String appId = Helper.getRandomString(8);
        String appSecret = Base64.getEncoder().encodeToString(MD5Utils.MD5Encode(Base64.getEncoder().encodeToString(Helper.byteMerrage(salt.getBytes(), MD5Utils.MD5Encode(appId)))));
        //RSA對userid加密
        String dbAppSecret = utilRSA.encryptByPublicKey(appSecret);
        System.out.println("dbAppId & dbAppSecret : " + appId + " & " + dbAppSecret);
    }

}
