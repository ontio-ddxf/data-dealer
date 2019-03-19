# Data Dealer

* [手机注册Ontid](#手机注册Ontid)
* [手机密码登录](#手机密码登录)
* [需求方购买数据](#需求方购买数据)
* [需求方取消购买](#需求方取消购买)
* [需求方确认收货](#需求方确认收货)
* [提供方发货](#提供方发货)
* [提供方取消订单](#提供方取消订单)

## 接口规范

### 注册

#### 手机注册Ontid

1. 提交号码，验证码，密码
2. 返回 `ONT ID`

```text
url：/api/v1/ontid/register/phone
method：POST
```

- - 请求：

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

- - 响应：

```json
{
    "action":"register",
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
url：/api/v1/ontid/login/password
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
**|    phone|   String|  手机号码  |
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
|    result|   String|  成功返回ontid，失败返回""  |**


### 需求方购买数据

1. 提供买方，卖方，数据信息
2. 验证信息
3. 调用合约处理交易
4. 返回ontid

```text
url：/api/v1/datadealer/buyer/buy
method：POST
```

- 请求：

```json
{
	"ontid":"did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL",
	"password": "123456",
	"supplyOntid": "did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuO",
	"productIds": [1,2,3],
	"price": "10000"
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    ontid|   String|  买方ontid  |
|    password|   String|  买方密码  |
|    supplyOntid|   String|  卖方ontid  |
|    productIds|   List|  数据id  |
|    price|   String|  数据总价  |

- 响应：

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


### 需求方取消购买

1. 提供买方信息，订单ID
2. 验证信息
3. 调用合约取消交易
4. 返回ontid

```text
url：/api/v1/datadealer/buyer/cancel
method：POST
```

- 请求：

```json
{
	"ontid":"did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL",
	"password": "123456",
	"orderId": 5765
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    ontid|   String|  买方ontid  |
|    password|   String|  买方密码  |
|    orderId|   Integer|  订单ID  |

- 响应：

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


### 需求方确认收货

1. 提供买方信息，订单ID
2. 验证信息
3. 调用合约提取数据，确认交易
4. 返回ontid

```text
url：/api/v1/datadealer/buyer/confirm
method：POST
```

- 请求：

```json
{
	"ontid":"did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL",
	"password": "123456",
	"orderId": 5765
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    ontid|   String|  买方ontid  |
|    password|   String|  买方密码  |
|    orderId|   Integer|  订单ID  |

- 响应：

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


### 提供方发货

1. 提供卖方信息，订单ID，url，一次性密码
2. 获取买方信息
3. 将一次性密码和数据地址用买方公钥加密,并锁到只能合约
4. 返回ontid

```text
url：/api/v1/datadealer/seller/sell
method：POST
```

- 请求：

```json
{
	"ontid":"did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL",
	"password":"123456",
	"orderId": 123,
	"url": "http://data.com",
	"dataPwd": "654321"
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    ontid|   String|  卖方ontid  |
|    password|   String|  卖方密码  |
|    orderId|   Integer|  订单id  |
|    url|   String|  数据url  |
|    dataPwd|   String|  一次性密码  |

- 响应：
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


### 提供方取消订单

1. 提供卖方信息，订单ID
2. 验证信息
3. 调用合约取消交易
4. 返回ontid

```text
url：/api/v1/datadealer/seller/cancel
method：POST
```

- 请求：

```json
{
	"ontid":"did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL",
	"password":"123456",
	"orderId": 123
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    ontid|   String|  卖方ontid  |
|    password|   String|  卖方密码  |
|    orderId|   Integer|  订单id  |

- 响应：
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