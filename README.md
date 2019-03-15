# data-dealer
data-dealer

### 需求方数据交易请求
1. 提供买方，卖方，数据信息
2. 验证信息
3. 调用合约处理交易
4. 返回ontid（该ontid和keystore的ontid一致）
```text
url：/api/v1/data/dealer/buy
method：POST
```

请求：
```json
{
	"ontid":"did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL",
	"password": "123456",
	"supplyOntid": "did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuO",
	"productIds": "[1,2,3]",
	"price": "10000"
}
```
| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    ontid|   String|  买方ontid  |
|    password|   String|  买方密码  |
|    supplyOntid|   String|  卖方ontid  |
|    productIds|   List|  数据id  |
|    price|   Double|  数据总价  |

返回：
```json
{
	"action":"purchase",
	"version":"v1",
	"error":0,
	"desc":"SUCCESS",
	"result": "did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL"
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    action|   String|  动作标志  |
|    version|   String|  版本号  |
|    error|   int|  错误码  |
|    desc|   String|  成功为SUCCESS，失败为错误描述  |
|    result|   String|  成功返回ontid，失败返回""  |

Result purchaseData(@RequestBody LinkedHashMap<String, Object> obj)