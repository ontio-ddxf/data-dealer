# Data Dealer

## 接口规范

### 注册

#### 手机注册

1. [获取验证码](#获取验证码)
2. 提交号码，验证码，密码
3. 返回 `ONT ID`

```text
url：/api/v0.0.1/ontid/register/phone
method：POST
```

- - 请求：

```json
{
	"number":"86*15821703553",
	"verifyCode": "123456",
	"password":"12345678"
}
```

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
| number     | String | 手机号码    |
| verifyCode | String | 手机验证码  |
| password   | String | 设置密码    |

- - 响应：

```json
{
    "action":"register",
	"version":"v0.0.1",
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

#### KeyStore 注册

1. [获取验证码](#获取验证码)
2. 提交号码，验证码，keystore，keystore对应的密码
3. 返回ontid（该ontid和keystore的ontid一致）

```text
url：/api/v0.0.1/ontid/binding
method：POST
```

- 请求：

```json
{
	"phone":"86*15821703553",
	"verifyCode":"123456",
	"keystore":"keystore",
	"password":"12345678"
}
```

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
| phone      | String | 手机号码    |
| verifyCode | String | 手机验证码  |
| keystore   | String | keystore    |
| password   | String | 设置密码    |

- 响应：

```json
{
	"action":"binding",
	"version":"v0.0.1",
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

#### 手机验证码登录

1. [获取验证码](#获取验证码)
2. 提交号码，验证码
3. 返回ontid（该ontid和keystore的ontid一致）

```text
url：/api/v0.0.1/ontid/login/phone
method：POST
```

- 请求：

```json
{
	"phone":"86*15821703553",
	"verifyCode": "123456"
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    phone|   String|  手机号码  |
|    verifyCode|   String|  手机验证码  |

- 响应：

```json
{
	"action":"login",
	"version":"v0.0.1",
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

### 手机密码登录

1. 提交号码，密码
2. 返回ontid

```text
url：/api/v0.0.1/ontid/login/password
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
    "version":"v0.0.1",
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

### 需求方数据交易请求

1. 提供买方，卖方，数据信息
2. 验证信息
3. 调用合约处理交易
4. 返回ontid（该ontid和keystore的ontid一致）

```text
url：/api/v0.0.1/data/dealer/buy
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
	"version":"v0.0.1",
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

### 数据提供方数据交易请求

1. 提供卖方信息，交易号
2. 获取买方信息
3. 将一次性密码和数据地址用买方公钥加密
4. 返回ontid

```text
url：/api/v0.0.1/data/dealer/supply
method：POST
```

- 请求：

```json
{
	"ontid":"did:ont:AcrgWfbSPxMR1BNxtenRCCGpspamMWhLuL",
	"orderId": 123
}
```

| Field_Name|     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    ontid|   String|  卖方ontid  |
|    orderId|   Integer|  订单id  |

- 响应：
```json
{
	"action":"purchase",
	"version":"v0.0.1",
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
