package com.ontology.model;

import lombok.Data;

/**
 * Created by ZhouQ on 2017/8/28.
 */
@Data
public class Result {
    public String Action;
    public int Code;
    public String Msg;
    public Object Result;
    public String Version;

    public Result() {
    }

    public Result(String action, int code, String msg, Object result) {
        Action = action;
        Code = code;
        Msg = msg;
        Result = result;
        Version = "v1";
    }

    private Result(String action, int code, String msg, Object result, String version) {
        Action = action;
        Code = code;
        Msg = msg;
        Result = result;
        Version = version;
    }
}
