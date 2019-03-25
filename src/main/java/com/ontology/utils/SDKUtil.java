package com.ontology.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.Base64;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.*;
import com.github.ontio.common.Helper;
import com.github.ontio.core.ontid.Attribute;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.nativevm.abi.Struct;
import com.ontology.ConfigParam;
import com.ontology.dao.OntId;
import com.ontology.secure.ECIES;

import com.ontology.secure.SecureConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SDK 入口类
 *
 * @author 12146
 */
@Component
public class SDKUtil {

    @Autowired
    ConfigParam param;
    @Autowired
    SecureConfig secureConfig;


    public Map<String, String> createOntId(String pwd) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Account payerAcct = getPayerAcct();
        HashMap<String, String> res = new HashMap<>();
        Identity identity = ontSdk.getWalletMgr().createIdentity(pwd);
        String txhash = ontSdk.nativevm().ontId().sendRegister(identity,pwd,payerAcct,Constant.GAS_Limit, Constant.GAS_PRICE);
        ontSdk.getWalletMgr().getWallet().clearIdentity();
        ontSdk.getWalletMgr().writeWallet();
        Map keystore = WalletQR.exportIdentityQRCode(ontSdk.getWalletMgr().getWallet(), identity);
        keystore.put("publicKey",identity.controls.get(0).publicKey);
        res.put("ontid", identity.ontid);
        res.put("keystore", JSON.toJSONString(keystore));
        res.put("tx", txhash);
        ontSdk.getWalletMgr().getWallet().clearIdentity();
        return res;
    }

    public String createOntIdWithWif(String wif, String pwd) throws Exception {
        OntSdk ontSdk = getOntSdk();
        byte[] bytes = com.github.ontio.account.Account.getPrivateKeyFromWIF(wif);
        Identity identity = ontSdk.getWalletMgr().createIdentityFromPriKey(pwd, Helper.toHexString(bytes));
        Map keystore = WalletQR.exportIdentityQRCode(ontSdk.getWalletMgr().getWallet(), identity);
        ontSdk.getWalletMgr().getWallet().clearIdentity();
        return JSON.toJSONString(keystore);
    }

    public String checkOntId(String keystore, String pwd) throws Exception {
        Account account = exportAccount(keystore, pwd);
        return Common.didont + Address.addressFromPubKey(account.serializePublicKey()).toBase58();
    }

    private Account exportAccount(String keystoreBefore, String pwd) throws Exception {
        OntSdk ontSdk = getOntSdk();
        String keystore = keystoreBefore.replace("\\", "");
        JSONObject jsonObject = JSON.parseObject(keystore);
        String key = jsonObject.getString("key");
        String address = jsonObject.getString("address");
        String saltStr = jsonObject.getString("salt");

        int scrypt = jsonObject.getJSONObject("scrypt").getIntValue("n");
        String privateKey = Account.getGcmDecodedPrivateKey(key, pwd, address, Base64.decodeFast(saltStr), scrypt, ontSdk.getWalletMgr().getSignatureScheme());
        return new Account(Helper.hexToBytes(privateKey), ontSdk.getWalletMgr().getSignatureScheme());
    }

    public String exportWif(String keystore, String pwd) throws Exception {
        Account account = exportAccount(keystore, pwd);
        return account.exportWif();
    }

    public String decryptData(String keystore, String pwd, String[] data) throws Exception {
        Account account = exportAccount(keystore, pwd);
        byte[] decrypt = ECIES.Decrypt(account, data);
        return new String(decrypt);
    }


    private OntSdk wm;

    private OntSdk getOntSdk() throws Exception {
        if (wm == null) {
            wm = OntSdk.getInstance();
            wm.setRestful(param.RESTFUL_URL);
            wm.openWalletFile("wallet.json");
        }
        if (wm.getWalletMgr() == null) {
            wm.openWalletFile("wallet.json");
        }
        return wm;
    }

    public String checkOntIdDDO(String ontidStr) throws Exception {
        return getOntSdk().nativevm().ontId().sendGetDDO(ontidStr);
    }

    public Account getAccount(String keystore, String pwd) throws Exception {
        Account account = exportAccount(keystore, pwd);
        return account;
    }

    public String invokeContract(byte[] params, Account Acct,Account payerAcct, long gaslimit, long gasprice, boolean preExec) throws Exception{
        OntSdk ontSdk = getOntSdk();
        if(payerAcct == null){
            throw new SDKException("params should not be null");
        }
        if(gaslimit < 0 || gasprice< 0){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }

        Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse("04e1d2914999b485896f6c42b729565f8f92d625"),"send_token",params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);

        ontSdk.addSign(tx, payerAcct);
        ontSdk.addSign(tx,Acct);

        Object result = null;
        if(preExec) {
            result = ontSdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        }else {
            result = ontSdk.getConnect().sendRawTransaction(tx.toHexString());
        }
        return tx.hash().toString();
    }
    public Object invokeContract(String str, Account acct,Account payerAcct, boolean preExec) throws Exception{
        OntSdk ontSdk = getOntSdk();
        Transaction[] txs1 = ontSdk.makeTransactionByJson(str);
        ontSdk.addSign(txs1[0], acct);
        ontSdk.addSign(txs1[0], payerAcct);
        Object result = null;
        if(preExec) {
            result = ontSdk.getConnect().sendRawTransactionPreExec(txs1[0].toHexString());
            return result;
        }else {
            result = ontSdk.getConnect().sendRawTransaction(txs1[0].toHexString());
        }
        return txs1[0].hash().toString();
    }
    public Account getPayerAcct() throws Exception {
        OntSdk ontSdk = getOntSdk();
//        Account account = new Account(Helper.hexToBytes(secureConfig.getWalletJavaPrivateKey()), ontSdk.getWalletMgr().getSignatureScheme());
//        Account account = ontSdk.getWalletMgr().getWallet().getAccounts()[0];
        Account acct = ontSdk.getWalletMgr().getAccount("AGW2QrJZMf2ZWuG7bzwczYE1yHACXjnpZG", secureConfig.getWalletPwd());
        return acct;
    }

    public Object checkEvent(String txHash) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Object event = ontSdk.getConnect().getSmartCodeEvent(txHash);
        return event;
    }

    public String getPublicKeys(String ontid) throws Exception {
        OntSdk ontSdk = getOntSdk();
        return ontSdk.nativevm().ontId().sendGetPublicKeys(ontid);
    }

    public int getBlockHeight(String txHash) throws Exception {
        OntSdk ontSdk = getOntSdk();
        int height = ontSdk.getConnect().getBlockHeightByTxHash(txHash);
        return height;
    }

    public void queryBlance() throws Exception {
        OntSdk ontSdk = getOntSdk();
//        String s = ontSdk.neovm().oep4().queryBalanceOf("AKRwxnCzPgBHRKczVxranWimQBFBsVkb1y");
        long ontBalance = ontSdk.nativevm().ont().queryBalanceOf("AR9cMgFaPNDw82v1aGjmB18dfA4BvtmoeN");
        long ongBalance = ontSdk.nativevm().ong().queryBalanceOf("AR9cMgFaPNDw82v1aGjmB18dfA4BvtmoeN");
        System.out.println("ont:"+ontBalance);
        System.out.println("ong:"+ongBalance);
    }

    public String getDDO(String ontid) throws Exception {
        OntSdk ontSdk = getOntSdk();
        String DDO = ontSdk.nativevm().ontId().sendGetDDO(ontid);
        return DDO;
    }

    public String addAttributes(OntId ontId, String password, String key, String valueType, String value) throws Exception {
        OntSdk ontSdk = getOntSdk();
        String ontid = ontId.getOntid();
        String keystore = ontId.getKeystore().replace("\\", "");
        JSONObject jsonObject = JSON.parseObject(keystore);
        String saltStr = jsonObject.getString("salt");
        byte[] salt = java.util.Base64.getDecoder().decode(saltStr);
        Attribute[] attributes = new Attribute[1];
        attributes[0] = new Attribute(key.getBytes(),valueType.getBytes(),value.getBytes());
        Account payerAcct = getPayerAcct();

        // sendAddAttributes
        if (ontid == null || ontid.equals("") || password == null || attributes == null || attributes.length == 0 || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        String addr = ontid.replace(Common.didont, "");

        // Transaction tx = makeAddAttributes(ontid, password, salt,attributes, payerAcct.getAddressU160().toBase58(), 20000, 500);
        String contractAddress = "0000000000000000000000000000000000000003";
        String publicKey = jsonObject.getString("publicKey");
//        AccountInfo info = ontSdk.getWalletMgr().getAccountInfo(ontid, password,salt);
//        Account account = ontSdk.getWalletMgr().getAccount(ontid, password, salt);
//        String pubKey = Helper.toHexString(account.serializePublicKey());
//        password = null;
        byte[] pk = Helper.hexToBytes(publicKey);
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid.getBytes());
        struct.add(attributes.length);
        for (int i = 0; i < attributes.length; i++) {
            struct.add(attributes[i].key, attributes[i].valueType, attributes[i].value);
        }
        struct.add(pk);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = ontSdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"addAttributes",args,payerAcct.getAddressU160().toBase58(),20000, 500);

        Account account = exportAccount(keystore, password);
        ontSdk.signTx(tx, new Account[][]{{account}});
        ontSdk.addSign(tx, payerAcct);
        boolean b = ontSdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public void getWalletPk() throws Exception {
        OntSdk ontSdk = getOntSdk();
        String privateKey = Account.getGcmDecodedPrivateKey("hiqZ0Ecsd+OTy3AAzLNBIhdYVVYmDiR77meHuMT/v9EJmQIgHWRv54cDAjO1bP28", "87654321",
                "AGW2QrJZMf2ZWuG7bzwczYE1yHACXjnpZG", Base64.decodeFast("oZ7Ulp8ltLc87z/i+dSLeA=="), 16384, ontSdk.getWalletMgr().getSignatureScheme());
        System.out.println(Base64ConvertUtil.encode(privateKey));
    }

    public Account getPayerAcct2() throws Exception {
        OntSdk ontSdk = getOntSdk();
        System.out.println(secureConfig.getWalletJavaPrivateKey());

        Account account = new Account(Helper.hexToBytes(secureConfig.getWalletJavaPrivateKey()), ontSdk.getWalletMgr().getSignatureScheme());
        System.out.println(JSON.toJSONString(account));
        return account;
    }
}
