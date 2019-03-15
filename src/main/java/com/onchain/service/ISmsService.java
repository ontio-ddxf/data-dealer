package com.onchain.service;

import com.onchain.model.Result;

/**
 * @author zhouq
 * @version 1.0
 * @date 2018/1/17
 */
public interface ISmsService {

    Result sendCode(String phone) throws Exception;

    /**
     * 验证手机验证码
     *
     * @param phone
     * @param code
     * @return
     */
    void verifyPhone(String action,String phone, String code) throws Exception;


}
