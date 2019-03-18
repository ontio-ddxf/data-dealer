package com.ontology.utils;

/**
 * @author zhouq
 * @version 1.0
 * @date 2017/12/11
 */
public enum ErrorInfo {

    /**
     * success
     */
    SUCCESS(0, "SUCCESS", "成功"),

    /**
     * param error
     */
    PARAM_ERROR(61001, "FAIL, param error.", "参数错误"),

    /**
     * already exist
     */
    ALREADY_EXIST(61002,"FAIL, already exist.", "已存在"),

    /**
     * not found in db
     */
    NOT_FOUND(61003,"FAIL, not found.", "未找到"),

    /**
     * not exist
     */
    NOT_EXIST(61004,"FAIL, not exist.", "不存在"),

    /**
     * no permission
     */
    NO_PERMISSION(61005,"FAIL, no permission", "权限错误"),

    /**
     * not register
     */
    NOT_REGISTRY(61006,"FAIL, not registry.", "未注册"),

    /**
     * expires
     */
    EXPIRES(61007,"FAIL, expires.", "已过期"),

    /**
     * revoked
     */
    REVOKED(61008,"FAIL, revoked.", "已注销"),

    /**
     * serialized error
     */
    SERIALIZE_ERROR(61009,"FAIL, serialized error.", "序列化错误"),

    /**
     * serialized error
     */
    TIME_EXCEEDED(61010,"FAIL, time exceeded error.", "次数超限"),



    /**
     * verify failed
     */
    VERIFY_FAILED(62001,"FAIL, verify fail.", "身份校验失败"),

    /**
     * error occur whern create
     */
    CREATE_FAIL(62002,"FAIL, create fail.", "创建失败"),

    /**
     * error occur whern communicate
     */
    COMM_FAIL(62003,"FAIL, communication fail.", "通信异常"),

    /**
     * error occur whern operate file
     */
    FILE_ERROR(62004,"FAIL, file operate fail.", "文件操作异常"),

    /**
     * error occur when operate db
     */
    DB_ERROR(62005,"FAIL, db operate fail.", "数据库操作异常"),

    /**
     * verify failed
     */
    SIG_VERIFY_FAILED(62006,"FAIL, verify signature fail.", "验签失败"),


    /**
     * inner error
     */
    INNER_ERROR(63001,"FAIL, inner error.", "内部异常"),

    /**
     * exception
     */
    EXCEPTION(63002,"FAIL, exception.", "异常"),

    /**
     * verify failed
     */
    CODE_VERIFY_FAILED(63003,"FAIL, verify devicecode fail.", "设备码校验失败"),

    /**
     * verify failed
     */
    IDENTITY_VERIFY_FAILED(63004,"FAIL, verify identity fail.", "身份认证失败");

    private int errorCode;
    private String errorDescEN;
    private String errorDescCN;

    ErrorInfo(int errorCode, String errorDescEN, String errorDescCN) {
        this.errorCode = errorCode;
        this.errorDescEN = errorDescEN;
        this.errorDescCN = errorDescCN;
    }

    public int code(){
        return errorCode;
    }

    public String descEN() {
        return errorDescEN;
    }

    public String descCN() {
        return errorDescCN;
    }



}
