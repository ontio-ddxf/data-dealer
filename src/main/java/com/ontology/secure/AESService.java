package com.ontology.secure;


import com.github.ontio.common.Helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * @author zhouq
 * @version 1.0
 * @date 2018/2/6
 * 對appid，appsecret處理
 */
@Component
public class AESService {

    private static Logger logger = LoggerFactory.getLogger(AESService.class);

    private static final String KEY = "ONtology*&1onchain764";

    private static final String IV = "78Hfghjkqwert78^";


    public static void main(String[] args) {
        // TODO Auto-generated method stub

        String content = "Ontology#@OntPass001";
        System.out.println("加密前："+content);
        byte[ ] encrypted=AES_CBC_Encrypt(content.getBytes());
        String rs = byteToHexString(encrypted);
        System.out.println("加密后："+rs);

        byte[] decrypted = AES_CBC_Decrypt(Helper.hexToBytes("2a9078940afe7d368db2dd4bd0e2e8aacfd1ca6cbef9e19c13d826accd656bda829da9a4df792b3c87233655d4f15a7e"));
        String str2 = new String(decrypted);
        System.out.println("解密后：" + str2);

        String claim = "eyJraWQiOiJkaWQ6b250OkFScjZBcEsyNEVVN251Zk5ENHMxU1dwd1VMSEJlcnRwSmIja2V5cy0xIiwidHlwIjoiSldULVgiLCJhbGciOiJPTlQtRVMyNTYifQ==.eyJjbG0tcmV2Ijp7InR5cCI6IkF0dGVzdENvbnRyYWN0IiwiYWRkciI6IjM2YmI1YzA1M2I2YjgzOWM4ZjZiOTIzZmU4NTJmOTEyMzliOWZjY2MifSwic3ViIjoiZGlkOm9udDpBTDJ4MkdUUkZEN0N5cTUzS2RZVjVSRUtuZ1l2cHk0WWVtIiwidmVyIjoidjEuMCIsImNsbSI6eyJOYXRpb25hbGl0eSI6IkNOIiwiTmFtZSI6InNua3Mgc2doaSIsIkVtYWlsIjoiMTU0Njk0QDUxNjYubm4iLCJQaG9uZSI6Iis4NiA1MTY0NTQ4NzY0MyIsIklzc3Vlck5hbWUiOiJJZGVudGl0eU1pbmQifSwiaXNzIjoiZGlkOm9udDpBUnI2QXBLMjRFVTdudWZORDRzMVNXcHdVTEhCZXJ0cEpiIiwiZXhwIjoxNTY2MjgzOTY0LCJpYXQiOjE1MzQ3NDc5NjksIkBjb250ZXh0IjoiY2xhaW06aWRtX2lkY2FyZF9hdXRoZW50aWNhdGlvbiIsImp0aSI6IjdmNDJlZDZmNTkyMDk4MWZmMTczZWEzMmM2ZDgwNmJjOTJjOGMyOWE3NTYxNmQ0ZjViNmE4ZWIzNTVmNDBkZGQifQ==.AQn8reEPE00zpekmbYLy2Cl5gjyyk/cFF4hKXhoFYFy0LgyPArmoNZd41pzCyCZok7al2wi7Hg3mgdQY+lcaJ8U=\\.eyJQcm9vZiI6eyJUeXBlIjoiTWVya2xlUHJvb2YiLCJNZXJrbGVSb290IjoiYmM4NWViNDhlNmRjOWI3MTZmMDVkMmMyMTc0MTJiODJjNTY0ZmY5YzUwOWZiZDgwY2U4ZDRlYjlkZDlkYTM0ZiIsIlR4bkhhc2giOiJmNjVmOWYwMGExODZlZTkxYzI2ZTdkODlmNzQyOTRmOGYwMzhiZWRjODdmZmVmNDc4YzYzMGYzNzdmYjBjMmM3IiwiQmxvY2tIZWlnaHQiOjE1NTY3NSwiTm9kZXMiOlt7IlRhcmdldEhhc2giOiIwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwIiwiRGlyZWN0aW9uIjoiTGVmdCJ9LHsiVGFyZ2V0SGFzaCI6Ijc3YzVkYmZiY2EyN2NjZGVjZTc2NWVjMDU4MzZjNTYxYTU3MWYwMThiZmRlZWQ3OGI3ZDJhZWVjZDA5ODA3YWUiLCJEaXJlY3Rpb24iOiJMZWZ0In0seyJUYXJnZXRIYXNoIjoiYmNmODE5MmQ5NmUzOGJkYTM2YWIxZjY1YmRiMTEwMWE4Njk3NDhlNDRkYzM4ZDk5ZDAwODM3OGUyMDEyNjRmNCIsIkRpcmVjdGlvbiI6IkxlZnQifSx7IlRhcmdldEhhc2giOiI0MmRlNzg3M2VjMGZkODM4ZTA1YzgyZTNjZDM3MzJlYmM3M2VlOTNjZGQ5MTE2YTEyNGRlZGU0MTQ3NDZhZjY2IiwiRGlyZWN0aW9uIjoiTGVmdCJ9LHsiVGFyZ2V0SGFzaCI6IjQ0NmRkZDdmMTU4MTZhYzljYjhkY2ZhZDZiOGMxYzM0NDEyNTk2ODc4MmU4MjQyNTVlZmJlZmU1MjM2YTY1OTUiLCJEaXJlY3Rpb24iOiJMZWZ0In0seyJUYXJnZXRIYXNoIjoiY2NjOGE0MGU0OGIzODAxZjA3NGJhNjRmN2YwNWE4ZTk2MjM0ODQ2NjFiOTRjM2FkMDc2YmFjNWU2YWQ0ZDRhOSIsIkRpcmVjdGlvbiI6IkxlZnQifSx7IlRhcmdldEhhc2giOiJhNjAxYWU2ZGVkMDA4N2QyNzI4MmZiMjc5YmQ2ZDIzNGU1M2UzYzE1NmI3YmU5YjRjNzdmODQ4MzA5YTZmZjU2IiwiRGlyZWN0aW9uIjoiTGVmdCJ9XSwiQ29udHJhY3RBZGRyIjoiMzZiYjVjMDUzYjZiODM5YzhmNmI5MjNmZTg1MmY5MTIzOWI5ZmNjYyJ9fQ==";
//        String claim = "eyJraWQiOiJkaWQ6b250OkFScjZBcEsyNEVVN251Zk5ENHMxU1dwd1VMSEJlcnRwSmIja2V5cy0xIiwidHlwIjoiSldULVgiLCJhbGciOiJPTlQtRVMyNTYifQ==.eyJjbG0tcmV2Ijp7InR5cCI6IkF0dGVzdENvbnRyYWN0IiwiYWRkciI6IjM2YmI1YzA1M2I2YjgzOWM4ZjZiOTIzZmU4NTJmOTEyMzliOWZjY2MifSwic3ViIjoiZGlkOm9udDpUQTh5clZlVjl5RkxSZW1YYktlTUVhY2hQZWY4bmZidGlWIiwidmVyIjoidjEuMCIsImNsbSI6eyJUd2l0dGVyQ3JlYXRlVGltZSI6IldlZCBGZWIgMjggMDM6MjI6NTEgKzAwMDAgMjAxOCIsIk5hbWUiOiJsZWV3aTnlnKjkuIrmtbciLCJJZCI6IjQyNDIwOTU2MiIsIkhvbWVQYWdlIjoiaHR0cHM6Ly90d2l0dGVyLmNvbS9sZWV3aTlfc2hhbmdoYWkiLCJUd2l0dGVyVXJsIjoiaHR0cHM6Ly90d2l0dGVyLmNvbS9sZWV3aTlfc2hhbmdoYWkvc3RhdHVzLzk2ODY4NzkxNzg1MzAzNjU0NCIsIklzc3Vlck5hbWUiOiJPbnRvbG9neSJ9LCJpc3MiOiJkaWQ6b250OkFScjZBcEsyNEVVN251Zk5ENHMxU1dwd1VMSEJlcnRwSmIiLCJleHAiOjE1NjU4NTczNzMsImlhdCI6MTUzNDMyMTM3NCwiQGNvbnRleHQiOiJjbGFpbTp0d2l0dGVyX2F1dGhlbnRpY2F0aW9uIiwianRpIjoiYTk4Y2ZhNTkyM2Q1ZmZmZGJlYmRjNjM5MTIyZmMxMzlkN2I4NjE1MTZiOGNkOTQxYmNmMTBlMDUyNDhmNDIzMyJ9.AfsE+bDRrfLuvaPrcLIMpNGUfTsC/0jsZT1+hXxh3nHwo+ITyk2u8UplS+yifoN+H/lc9yD9C1vbUrB/WRc8PDw=\\\\.eyJQcm9vZiI6eyJUeXBlIjoiTWVya2xlUHJvb2YiLCJNZXJrbGVSb290IjoiMjEyYThhZTM0MDg3MjFmNTg0NmUxYmFkODE2M2JhNjI1YmI2YjhkMTFjMWM3ZmYyZDlhOGMwMzRkMmZkYmEzZiIsIlR4bkhhc2giOiI4MGVjODA2ZDcwZWY2MzZmYzBmMzllYjA0ZDRkYWQ0ZjkxMzU3MzBhMjAyYTcxMjA0ZTEzNjhhOTgwMmU2NDY4IiwiQmxvY2tIZWlnaHQiOjE0MDQwNSwiTm9kZXMiOlt7IlRhcmdldEhhc2giOiIwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwIiwiRGlyZWN0aW9uIjoiTGVmdCJ9LHsiVGFyZ2V0SGFzaCI6ImU1N2FhYzRjNmI3ZDg4NjkxM2RkY2ZlMjdmZGJlMTBiNDI1ZjU2ZDA1NzM1Nzc3YmIxOWVkNWMyZGM3ZWMzMTUiLCJEaXJlY3Rpb24iOiJMZWZ0In0seyJUYXJnZXRIYXNoIjoiMDQwYjE4ZDhmZGI3YmViZjBhNjA1MDJiZWY1YjY1Zjk1MTQyMGFjZmFjMjA1NjA4MDQ4Yjk1ZThmNjEyN2QyNiIsIkRpcmVjdGlvbiI6IkxlZnQifSx7IlRhcmdldEhhc2giOiI3NjA2NzgxZTA2YmExYWY1ZjRiMGI4ZGRiY2FlNDcxZDY1ODIwZjhiYTY3MTM5ZDViNWY1OTZmNzZhOGYwYzA5IiwiRGlyZWN0aW9uIjoiTGVmdCJ9LHsiVGFyZ2V0SGFzaCI6IjQyZmI2OGRiZGNmZTFkYjg0OTRhMmE4YmE5YzFhOTMxMjA1YTI3NDBjMDNmZmZjZjM4NGRiZTQzYjA2Y2I5Y2QiLCJEaXJlY3Rpb24iOiJMZWZ0In0seyJUYXJnZXRIYXNoIjoiYjQ0MzI2MTIwYTY1YzQ4MzdiNmQ3Mzk1MmE1YTJiY2IxNWQ4ZGE3OThjNmFkOWZhNjQ4NDdjYmQ4YjJkNmMzMCIsIkRpcmVjdGlvbiI6IkxlZnQifSx7IlRhcmdldEhhc2giOiIyMzE2MGI3YTgxZTE2ZTVkYTE0NGNhNWQ2YTJiYzZmZjhkYzRlNzNhY2RlNGMwZmE2ZmEwYTRkOWJhZWEyMDMyIiwiRGlyZWN0aW9uIjoiTGVmdCJ9LHsiVGFyZ2V0SGFzaCI6ImE2MDFhZTZkZWQwMDg3ZDI3MjgyZmIyNzliZDZkMjM0ZTUzZTNjMTU2YjdiZTliNGM3N2Y4NDgzMDlhNmZmNTYiLCJEaXJlY3Rpb24iOiJMZWZ0In1dLCJDb250cmFjdEFkZHIiOiIzNmJiNWMwNTNiNmI4MzljOGY2YjkyM2ZlODUyZjkxMjM5YjlmY2NjIn19";
//        String claim = "eyJraWQiOiJkaWQ6b250OkFScjZBcEsyNEVVN251Zk5ENHMxU1dwd1VMSEJlcnRwSmIja2V5cy0xIiwidHlwIjoiSldULVgiLCJhbGciOiJPTlQtRVMyNTYifQ==.eyJjbG0tcmV2Ijp7InR5cCI6IkF0dGVzdENvbnRyYWN0IiwiYWRkciI6IjM2YmI1YzA1M2I2YjgzOWM4ZjZiOTIzZmU4NTJmOTEyMzliOWZjY2MifSwic3ViIjoiZGlkOm9udDpUQTh5clZlVjl5RkxSZW1YYktlTUVhY2hQZWY4bmZidGlWIiwidmVyIjoidjEuMCIsImNsbSI6eyJJZCI6IjQyNDIwOTU2MiIsIk5hbWUiOiJsZWV3aTnlnKjkuIrmtbciLCJBbGlhcyI6ImxlZXdpOV9zaGFuZ2hhaSIsIkJpbyI6ImNvZGluZyIsIkF2YXRhciI6Imh0dHBzOi8vcGJzLnR3aW1nLmNvbS9wcm9maWxlX2ltYWdlcy82Mjc0NTQ0MTMyMTMzMTUwNzMvTkRhTUdHX2Ffbm9ybWFsLmpwZyIsIkxvY2F0aW9uIjoiIiwiV2ViU2l0ZSI6IiIsIkhvbWVQYWdlIjoiaHR0cHM6Ly90d2l0dGVyLmNvbS9sZWV3aTlfc2hhbmdoYWkiLCJUd2l0dGVyVXJsIjoiaHR0cHM6Ly90d2l0dGVyLmNvbS9sZWV3aTlfc2hhbmdoYWkvc3RhdHVzLzk2ODY4NzkxNzg1MzAzNjU0NCIsIlR3aXR0ZXJDcmVhdGVUaW1lIjoiV2VkIEZlYiAyOCAwMzoyMjo1MSArMDAwMCAyMDE4IiwiSXNzdWVyTmFtZSI6Ik9udG9sb2d5In0sImlzcyI6ImRpZDpvbnQ6QVJyNkFwSzI0RVU3bnVmTkQ0czFTV3B3VUxIQmVydHBKYiIsImV4cCI6MTU2NTk0MzAyNiwiaWF0IjoxNTM0NDA3MDI5LCJAY29udGV4dCI6ImNsYWltOnR3aXR0ZXJfYXV0aGVudGljYXRpb24iLCJqdGkiOiI5ZDdkZmNiNzdkZDgzZGVkZWZlYTgxOGI3ODQ4N2RhOTc3MDU0YzVjZDlkY2UzZDUzZDFmZjQ3YjM2Yjk5N2UwIn0=.AWMmRuEt45jU8WKFNkLdwjAiOLnjF36BjjHMDeHZCS3wVSR8TXqTfn4GqjdaojFSpCYj4XGZJ82guDoScxzOyu0=\\.eyJQcm9vZiI6eyJUeXBlIjoiTWVya2xlUHJvb2YiLCJNZXJrbGVSb290IjoiZmM0ZmJjN2UwN2NlNTZhZGE0NWFjZmU5MTJmYzczNGQ5ZDcwNTM3ZGRmNDdiZjEyNmI2MzM4ODY1YjBlMDI0MCIsIlR4bkhhc2giOiJkMzJhY2JiZDdiZTQ0OTBhMDIyNTdhZGZmZjViOWY3ZGY1YzM4YjgzY2JjYmJiMGZhZTU3ZTdlY2ZjMTU5NDc4IiwiQmxvY2tIZWlnaHQiOjE0MzU3MCwiTm9kZXMiOlt7IlRhcmdldEhhc2giOiI3N2M1ZGJmYmNhMjdjY2RlY2U3NjVlYzA1ODM2YzU2MWE1NzFmMDE4YmZkZWVkNzhiN2QyYWVlY2QwOTgwN2FlIiwiRGlyZWN0aW9uIjoiTGVmdCJ9LHsiVGFyZ2V0SGFzaCI6ImIxMWJkNDQzMjAxMDIxMzIwNDUxMjk2NWYyZGY1NDFhMjIzYTkzYWFiNzg5YjA0YTZiNzU5YmM0NjBiNDI3ZGMiLCJEaXJlY3Rpb24iOiJMZWZ0In0seyJUYXJnZXRIYXNoIjoiOWQyYTgyM2Y1ZmY0NzllNTg3ZDc0OGUzNjc5MjY2ZTcyODY1MTk0N2RiM2UzMDc2YWQ4ZmRjZjM1MWI3Mzg2YyIsIkRpcmVjdGlvbiI6IkxlZnQifSx7IlRhcmdldEhhc2giOiJlMTQ0ZDg2ZDNiZDBiMGYwZGY1MmY2MzdlOTAyYTM4MTNjZDczN2M3NGU0ODdhY2M1OGM0MDc3MDQ2N2Q5ZjUxIiwiRGlyZWN0aW9uIjoiTGVmdCJ9LHsiVGFyZ2V0SGFzaCI6ImU2NmFkYjc5ZGZmNzM3NjE0MTEyYTcxYzM0N2RiNTk0ZmNiZWI5ODAwYmM5YjRhMjQ1NDdmNjdiNmM1MzNmODAiLCJEaXJlY3Rpb24iOiJMZWZ0In0seyJUYXJnZXRIYXNoIjoiMjMxNjBiN2E4MWUxNmU1ZGExNDRjYTVkNmEyYmM2ZmY4ZGM0ZTczYWNkZTRjMGZhNmZhMGE0ZDliYWVhMjAzMiIsIkRpcmVjdGlvbiI6IkxlZnQifSx7IlRhcmdldEhhc2giOiJhNjAxYWU2ZGVkMDA4N2QyNzI4MmZiMjc5YmQ2ZDIzNGU1M2UzYzE1NmI3YmU5YjRjNzdmODQ4MzA5YTZmZjU2IiwiRGlyZWN0aW9uIjoiTGVmdCJ9XSwiQ29udHJhY3RBZGRyIjoiMzZiYjVjMDUzYjZiODM5YzhmNmI5MjNmZTg1MmY5MTIzOWI5ZmNjYyJ9fQ==";
//        String claim = "eyJraWQiOiJkaWQ6b250OkFScjZBcEsyNEVVN251Zk5ENHMxU1dwd1VMSEJlcnRwSmIja2V5cy0xIiwidHlwIjoiSldULVgiLCJhbGciOiJPTlQtRVMyNTYifQ==.eyJjbG0tcmV2Ijp7InR5cCI6IkF0dGVzdENvbnRyYWN0IiwiYWRkciI6IjM2YmI1YzA1M2I2YjgzOWM4ZjZiOTIzZmU4NTJmOTEyMzliOWZjY2MifSwic3ViIjoiZGlkOm9udDpUQTh5clZlVjl5RkxSZW1YYktlTUVhY2hQZWY4bmZidGlWIiwidmVyIjoidjEuMCIsImNsbSI6eyJJZCI6IjQyNDIwOTU2MiIsIk5hbWUiOiJsZWV3aTnlnKjkuIrmtbciLCJBbGlhcyI6ImxlZXdpOV9zaGFuZ2hhaSIsIkJpbyI6ImNvZGluZyIsIkF2YXRhciI6Imh0dHBzOi8vcGJzLnR3aW1nLmNvbS9wcm9maWxlX2ltYWdlcy82Mjc0NTQ0MTMyMTMzMTUwNzMvTkRhTUdHX2Ffbm9ybWFsLmpwZyIsIkxvY2F0aW9uIjoiIiwiV2ViU2l0ZSI6IiIsIkhvbWVQYWdlIjoiaHR0cHM6Ly90d2l0dGVyLmNvbS9sZWV3aTlfc2hhbmdoYWkiLCJUd2l0dGVyVXJsIjoiaHR0cHM6Ly90d2l0dGVyLmNvbS9sZWV3aTlfc2hhbmdoYWkvc3RhdHVzLzk2ODY4NzkxNzg1MzAzNjU0NCIsIlR3aXR0ZXJDcmVhdGVUaW1lIjoiV2VkIEZlYiAyOCAwMzoyMjo1MSArMDAwMCAyMDE4IiwiSXNzdWVyTmFtZSI6Ik9udG9sb2d5In0sImlzcyI6ImRpZDpvbnQ6QVJyNkFwSzI0RVU3bnVmTkQ0czFTV3B3VUxIQmVydHBKYiIsImV4cCI6MTU2NTk0MzAyNiwiaWF0IjoxNTM0NDA3MDI5LCJAY29udGV4dCI6ImNsYWltOnR3aXR0ZXJfYXV0aGVudGljYXRpb24iLCJqdGkiOiI5ZDdkZmNiNzdkZDgzZGVkZWZlYTgxOGI3ODQ4N2RhOTc3MDU0YzVjZDlkY2UzZDUzZDFmZjQ3YjM2Yjk5N2UwIn0=.AWMmRuEt45jU8WKFNkLdwjAiOLnjF36BjjHMDeHZCS3wVSR8TXqTfn4GqjdaojFSpCYj4XGZJ82guDoScxzOyu0=";

        String[] aa = claim.split("\\.");
        System.out.println("length:" + aa.length);
        String head = new String(Base64.getDecoder().decode(aa[0]));
        String payload = new String(Base64.getDecoder().decode(aa[1]));
        String signature = aa[2];
        String merkleproof = new String(Base64.getDecoder().decode(aa[3]));
        System.out.println("head:" + head);
        System.out.println("payload:" + payload);
        System.out.println("signature:"+signature);
        System.out.println("merkleproof:" + merkleproof);

    }

    public static byte[] AES_CBC_Encrypt(byte[] content) {

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(KEY.getBytes());

            // keyGenerator.init(128, new SecureRandom(KEY.getBytes()));
            keyGenerator.init(128, random);

            SecretKey key = keyGenerator.generateKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes()));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("AES_CBC_Encrypt error...", e);
        }
        return null;
    }

    public static byte[] AES_CBC_Decrypt(byte[] content) {

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(KEY.getBytes());

            //key长可设为128，192，256位，这里只能设为128
//            keyGenerator.init(128, new SecureRandom(KEY.getBytes()));
            keyGenerator.init(128, random);

            SecretKey key = keyGenerator.generateKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes()));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("AES_CBC_Decrypt error...", e);
        }
        return null;
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
}