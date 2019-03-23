# Data Dealer


### 登录接口
* [手机密码登录](#手机密码登录)

### 需求方接口
* [需求方手机注册Ontid](#需求方手机注册Ontid)
* [需求方查询订单](#需求方查询订单)
* [需求方购买数据](#需求方购买数据)
* [需求方取消购买](#需求方取消购买)

### 提供方接口
* [提供方手机注册Ontid](#提供方手机注册Ontid)
* [提供方查询订单](#提供方查询订单)
* [提供方发货](#提供方发货)
* [提供方确认收款](#提供方确认收款)

### 存证接口
* [添加ontid属性](#添加ontid属性)
* [获取DDO](#获取DDO)


## 接口规范


### 需求方手机注册Ontid

1. 提交手机号码，密码
2. 返回 `ONT ID`

```text
url：/api/v1/ontid/register/demander/phone
method：POST
```

- 请求：

```json
{
    "phone":"86*15821703553",
    "password":"12345678"
}
```

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
| phone     | String | 手机号码    |
| password   | String | 设置密码    |

- 响应：

```json
{
    "action":"demanderRegister",
    "version":"v1",
    "error":0,
    "desc":"SUCCESS",
    "result": "did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL"
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| action     | String | 动作标志                      |
| version    | String | 版本号                        |
| error      | int    | 错误码                        |
| desc       | String | 成功为SUCCESS，失败为错误描述 |
| result     | String | 成功返回ontid，失败返回""     |


### 提供方手机注册Ontid

1. 提交手机号码，密码
2. 返回 `ONT ID`

```text
url：/api/v1/ontid/register/provider/phone
method：POST
```

- 请求：

```json
{
    "phone":"86*15821703553",
    "password":"12345678"
}
```

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
| phone     | String | 手机号码    |
| password   | String | 设置密码    |

- 响应：

```json
{
    "action":"providerRegister",
    "version":"v1",
    "error":0,
    "desc":"SUCCESS",
    "result": "did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL"
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| action     | String | 动作标志                      |
| version    | String | 版本号                        |
| error      | int    | 错误码                        |
| desc       | String | 成功为SUCCESS，失败为错误描述 |
| result     | String | 成功返回ontid，失败返回""     |


### 手机密码登录

1. 提交号码，密码
2. 返回ontid

```text
url：/api/v1/ontid/login
method：POST
```

- 请求：

```json
{
    "phone":"86*15821703553",
    "password": "12345678"
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    phone|   String|  手机号码  |
|    password|   String|  用户密码  |

- 响应：

```json
{
    "action":"login",
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

### 需求方查询订单

1. 提供买方信息
2. 返回购买订单列表

```text
url：/api/v1/datadealer/buyer/list
method：POST
```

- 请求：

```json
{
	"dataDemander":"did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL"
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    dataDemander|   String|  数据需求方ontid  |

- 响应：
```json
{
	"action":"buyerList",
	"version":"v1",
	"error":0,
	"desc":"SUCCESS",
	"result": [{
              	"id": "WfbSPxMR1BNxtenRCCGps",
              	"sellerOntid": "did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL",
              	"dataIdList": ["6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b"],
              	"buyDate": "2019-3-20",
              	"state": "deliveredOnchain"
              }]
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    action|   String|  动作标志  |
|    version|   String|  版本号  |
|    error|   int|  错误码  |
|    desc|   String|  成功为SUCCESS，失败为错误描述  |
|    result|   String|  成功返回orderList，失败返回""  |


### 需求方购买数据

1. 提供买方，卖方，数据信息
2. 验证信息
3. 调用合约处理交易
4. 返回true

```text
url：/api/v1/datadealer/buyer/buy
method：POST
```

- 请求：

```json
{
	"dataDemander":"did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL",
	"password": "123456",
	"dataProvider": "did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuO",
	"tokenContractAddress": "16edbe366d1337eb510c2ff61099424c94aeef02",
	"dataIdList": ["6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b","d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35","4e07408562bedb8b60ce05c1decfe3ad16b72230967de01f640b7e4729b49fce"],
	"priceList": [1,1,1],
	"waitReceiveEncListTime": 5000
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    dataDemander|   String|  数据需求方ontid  |
|    password|   String|  数据需求方密码  |
|    dataProvider|   String|  数据提供方ontid  |
|    tokenContractAddress|   String|  同质化通证的合约地址，付款合约地址可以是ont、ong、OEP4  |
|    dataIdList|   List|  所购数据的 `SHA256` 哈希值列表  |
|    priceList|   List|  所购数据的价格列表  |
|    waitReceiveEncListTime|   Integer|  等待数据需求方从合约中取出提取数据所必须的加密信息列表的最大时间上限  |

- 响应：

```json
{
	"action":"buy",
	"version":"v1",
	"error":0,
	"desc":"SUCCESS",
	"result": {
	   "orderId": "WfbSPxMR1BNxtenRCCGps"
	}
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    action|   String|  动作标志  |
|    version|   String|  版本号  |
|    error|   int|  错误码  |
|    desc|   String|  成功为SUCCESS，失败为错误描述  |
|    result|   String|  成功返回true，失败返回""  |


### 需求方取消购买

1. 提供买方信息，订单ID
2. 验证信息
3. 调用合约取消交易
4. 返回true

```text
url：/api/v1/datadealer/buyer/cancel
method：POST
```

- 请求：

```json
{
	"dataDemander":"did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL",
	"password": "123456",
	"orderId": "WfbSPxMR1BNxtenRCCGps"
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    dataDemander|   String|  数据需求方ontid  |
|    password|   String|  数据需求方密码  |
|    orderId|   String|  订单ID  |

- 响应：

```json
{
	"action":"buyerCancel",
	"version":"v1",
	"error":0,
	"desc":"SUCCESS",
	"result": true
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    action|   String|  动作标志  |
|    version|   String|  版本号  |
|    error|   int|  错误码  |
|    desc|   String|  成功为SUCCESS，失败为错误描述  |
|    result|   String|  成功返回true，失败返回""  |



### 提供方查询订单

1. 提供卖方信息
2. 返回收到订单列表

```text
url：/api/v1/datadealer/seller/list
method：POST
```

- 请求：

```json
{
	"dataProvider":"did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuO"
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
| dataProvider|   String|  数据提供方ontid  |

- 响应：
```json
{
	"action":"sellerList",
	"version":"v1",
	"error":0,
	"desc":"SUCCESS",
	"result": [{
              	"id": "WfbSPxMR1BNxtenRCCGps",
              	"sellerOntid": "did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL",
              	"dataIdList": ["6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b"],
              	"buyDate": "2019-3-20",
              	"state": "deliveredOnchain"
              }]
 }
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    action|   String|  动作标志  |
|    version|   String|  版本号  |
|    error|   int|  错误码  |
|    desc|   String|  成功为SUCCESS，失败为错误描述  |
|    result|   String|  成功返回orderList，失败返回""  |


### 提供方发货

1. 提供卖方信息，订单ID，url，一次性密码
2. 获取买方信息
3. 将一次性密码和数据地址用买方公钥加密,并锁到只能合约
4. 返回true

```text
url：/api/v1/datadealer/seller/sell
method：POST
```

- 请求：

```json
{
	"dataProvider":"did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuO",
	"password":"123456",
	"orderId": "WfbSPxMR1BNxtenRCCGps",
	"encMessageList": ["http://data1.com","http://data2.com"]
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    dataProvider|   String|  数据提供方ontid  |
|    password|   String|  数据提供方密码  |
|    orderId|   String|  订单id  |
|    encMessageList|   List|  加密消息列表，数据提供方在其中添加数据需求方用于提取数据所必须的加密信息  |

- 响应：
```json
{
	"action":"deliver",
	"version":"v1",
	"error":0,
	"desc":"SUCCESS",
	"result": true
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    action|   String|  动作标志  |
|    version|   String|  版本号  |
|    error|   int|  错误码  |
|    desc|   String|  成功为SUCCESS，失败为错误描述  |
|    result|   String|  成功返回true，失败返回""  |


### 提供方确认收款



```text
url：/api/v1/datadealer/seller/confirm
method：POST
```

- 请求：

```json
{
	"dataProvider":"did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL",
	"password": "123456",
	"orderId": "WfbSPxMR1BNxtenRCCGps"
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    dataDemander|   String|  数据需求方ontid  |
|    password|   String|  数据需求方密码  |
|    orderId|   String|  订单ID  |

- 响应：

```json
{
	"action":"confirm",
	"version":"v1",
	"error":0,
	"desc":"SUCCESS",
	"result": true
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    action|   String|  动作标志  |
|    version|   String|  版本号  |
|    error|   int|  错误码  |
|    desc|   String|  成功为SUCCESS，失败为错误描述  |
|    result|   String|  成功返回true，失败返回""  |


### 添加ontid属性

1. 提供ontid，密码，属性
2. 验证信息，添加属性
3. 返回true

```text
url：/api/v1/ontid/addattribute
method：POST
```

- 请求：

```json
{
	"ontid":"did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL",
	"password":"123456",
	"key": "key99",
	"valueType": "value99",
	"value": "value01"
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    ontid|   String|  需要添加属性的ontid  |
|    password|   String|  ontid的密码  |
|    key|   Sting|  属性的key  |
|    key|   Sting|  属性的valueType  |
|    key|   Sting|  属性的value  |

- 响应：
```json
{
	"action":"addAttribute",
	"version":"v1",
	"error":0,
	"desc":"SUCCESS",
	"result": true
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    action|   String|  动作标志  |
|    version|   String|  版本号  |
|    error|   int|  错误码  |
|    desc|   String|  成功为SUCCESS，失败为错误描述  |
|    result|   String|  成功返回true，失败返回""  |


### 获取DDO

1. 提供ontid
2. 返回DDO

```text
url：/api/v1/ontid/getddo
method：POST
```

- 请求：

```json
{
	"ontid":"did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL"
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    ontid|   String|  ontid  |

- 响应：
```json
{
	"action":"getDDO",
	"version":"v1",
	"error":0,
	"desc":"SUCCESS",
	"result": {
              	"Attributes": [],
              	"OntId": "did:ont:AR9cMgFaPNDw82v1aGjmB18dfA4BvtmoeN",
              	"Owners": [{
              		"Type": "ECDSA",
              		"Curve": "P-256",
              		"Value": "0206590ae715755f0f1fd235726957f8e11cd0b13b22c67c2404a5e3fd8d303b65",
              		"PubKeyId": "did:ont:AR9cMgFaPNDw82v1aGjmB18dfA4BvtmoeN#keys-1"
              	}]
              }
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    action|   String|  动作标志  |
|    version|   String|  版本号  |
|    error|   int|  错误码  |
|    desc|   String|  成功为SUCCESS，失败为错误描述  |
|    result|   String|  成功返回DDO，失败返回""  |
