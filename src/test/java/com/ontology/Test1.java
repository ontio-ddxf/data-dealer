package com.ontology;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.ontology.dao.Order;
import com.ontology.mapper.OrderMapper;
import com.ontology.utils.Base64ConvertUtil;
import com.ontology.utils.SDKUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Test1 {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    private SDKUtil sdkUtil;

    @Test
    public void createPayer() throws Exception {
        sdkUtil.createPayerAcct();
    }
    @Test
    public void testJSON(){
        List argsList = new ArrayList();
        List productIds = new ArrayList();
        productIds.add("sjodaijdoaqwdqwdq");
        productIds.add("drsdfdrwfwe");
        productIds.add("kpodisufisdfdfdf");
        List priceList = new ArrayList();
        priceList.add("100");
        priceList.add("500");
        priceList.add("1000");
        Map arg0 = new HashMap();
        arg0.put("name","data_demander");
        arg0.put("value","bytearray:"+Arrays.toString("AR9cMgFaPNDw82v1aGjmB18dfA4BvtmoeN".getBytes()));
        Map arg1 = new HashMap();
        arg1.put("name","data_provider");
        arg1.put("value","bytearray:"+Arrays.toString("AKRwxnCzPgBHRKczVxranWimQBFBsVkb1y".getBytes()));
        Map arg2 = new HashMap();
        arg2.put("name","token_contract_address");
        arg2.put("value","bytearray:"+Arrays.toString("d7b6a47966770c1545bf74c16426b26c0a238b16".getBytes()));
        Map arg3 = new HashMap();
        arg3.put("name","data_id_list");
        arg3.put("value","list:"+JSON.toJSONString(productIds));
        Map arg4 = new HashMap();
        arg4.put("name","price_list");
        arg4.put("value","list:"+JSON.toJSONString(priceList));
        Map arg5 = new HashMap();
        arg5.put("name","wait_send_enc_list_time");
        arg5.put("value","int:5000");
        Map arg6 = new HashMap();
        arg6.put("name","wait_receive_enc_list_time");
        arg6.put("value","int:5000");
        argsList.add(arg0);
        argsList.add(arg1);
        argsList.add(arg2);
        argsList.add(arg3);
        argsList.add(arg4);
        argsList.add(arg5);
        argsList.add(arg6);
        System.out.println(JSON.toJSONString(argsList));
    }
    @Test
    public void testBlance() throws Exception {
        sdkUtil.queryBlance("AcdBfqe7SG8xn4wfGrtUbbBDxw2x1e8UKm");
    }

    @Test
    public void testEvent() throws Exception {
        Object o = sdkUtil.checkEvent("2fb2d3eb8d4874f1cc26fd720ba27500670acae1278cf5a453819f9305c7c12b");
        System.out.println(o.toString());
    }

    @Test
    public void testGetPk() throws Exception {
        sdkUtil.getWalletPk();
    }

    @Test
    public void testHexString() throws Exception {
        // 方法名，十六进制转成字节，再转成String
        System.out.println(new String(Helper.hexToBytes("7472616e73666572"),"utf-8"));
        // 地址，解析十六进制字符串，再Base58加密
        System.out.println(Address.parse("66d5850e8a78e67325428ad77f91c913f7919ace").toBase58());
        System.out.println(Address.parse("281bc93aaa774edf88a3bdf7680fc8d119452607").toBase58());
        // ExchangeId 不用解析，十六进制字符串，调用时当作字节数组作为参数
        System.out.println("cab8d1c0eee964aeb39099be215cab6107bcca7c");
        // 数据地址，十六进制转成字节，再转成String
        System.out.println(new String(Helper.hexToBytes("687474703a2f2f64617461312e636f6d23687474703a2f2f64617461322e636f6d23"),"utf-8"));
        // 超时时间
        System.out.println(new String(Helper.hexToBytes("5802"),"utf-8"));
    }

    @Test
    public void testJsonArray(){
        String[] s = {"aa","bb","cc"};
        System.out.println(JSON.toJSONString(s));
    }
    @Test
    public void testList(){
        List productIds = new ArrayList();
        productIds.add("ijiojojsdjsiod");
        productIds.add("weijodjqoiwewq");
        productIds.add("ijiojojasdnasdjojsdjsiod");
        Map arg3 = new HashMap();
        arg3.put("name","data_id_list");
        arg3.put("value","list:"+JSON.toJSONString(productIds));
        System.out.println(JSON.toJSONString(arg3));
    }

    @Test
    public void testMap() {
        List argsList = new ArrayList();
        Map arg0 = new HashMap();
        arg0.put("name","arg0");
        arg0.put("value","String:ont");
        Map arg1 = new HashMap();
        arg1.put("name","arg1");
        arg1.put("value","Address:AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ");
        Map arg2 = new HashMap();
        arg2.put("name","arg2");
        arg2.put("value","Address:AecaeSEBkt5GcBCxwz1F41TvdjX3dnKBkJ");
        Map arg3 = new HashMap();
        arg3.put("name","arg3");
        arg3.put("value","1");
        argsList.add(arg0);
        argsList.add(arg1);
        argsList.add(arg2);
        argsList.add(arg3);
        Map str = new HashMap();
        Map parms = new HashMap();
        Map invokeConfig = new HashMap();
        List functions = new ArrayList();
        Map function = new HashMap();

        function.put("operation","test");
        function.put("args",argsList);

        functions.add(function);

        invokeConfig.put("contractHash","sjjdlajdioOOIJJSOdojoij");
        invokeConfig.put("functions",functions);
        invokeConfig.put("payer","jdsoahoahddsa");
        invokeConfig.put("gasLimit",20000);
        invokeConfig.put("gasPrice",500);

        parms.put("invokeConfig",invokeConfig);
        parms.put("ontid","did:ont:test");

        str.put("action","invoke");
        str.put("params",parms);
        String result = JSON.toJSONString(str);
        System.out.println(result);

        String example = "{" +
                "\"action\": \"invokeRead\"," +
                "\"params\": {" +

                "\"login\": true," +
                "\"url\": \"http://127.0.0.1:80/rawtransaction/txhash\"," +
                "\"message\": \"will pay 1 ONT in this transaction\"," +

                "\"invokeConfig\": {" +
                "\"contractHash\": \"cd948340ffcf11d4f5494140c93885583110f3e9\"," +
                "\"functions\": [{" +
                "\"operation\": \"transferNativeAsset\"," +
                "\"args\": [{" +
                "\"name\": \"arg0\"," +
                "\"value\": \"String:ont\"" +
                "}, {" +
                "\"name\": \"arg1\"," +
                "\"value\": \"Address:AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ\"" +
                "}, {" +
                "\"name\": \"arg2\"," +
                "\"value\": \"Address:AecaeSEBkt5GcBCxwz1F41TvdjX3dnKBkJ\"" +
                "}, {" +
                "\"name\": \"arg3\"," +
                "\"value\": 1" +
                "" +
                "}]" + //args
                "}]," + //functions
                "\"payer\": \"AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ\"," +
                "\"gasLimit\": 30000," +
                "\"gasPrice\": 500," +
                "\"signature\": {" +
                "\"m\": 1," +
                "\"signers\": [\"AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ\"]" +
                "}" +
                "}" + // invokeConfig

                "}" + // params
                "}"; // str
    }

    @Test
    public void testOrder() {
        Order order = new Order();
        order.setOrderId("90a3b7cd-167d-4cd8-b975-00960b3295e3");
        order = orderMapper.selectOne(order);
        order.setState("testUpdate");
        orderMapper.updateByPrimaryKey(order);
//        order.setBuyerOntid("iouowqoijidasd");
//        order.setSellerOntid("joijijojklkj");
//        order.setBuyDate(new Date());
//        order.setBuyTx("jojsdajosjdasd");
//        order.setState("bought");
//        orderMapper.insertSelective(order);
    }
}
