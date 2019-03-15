package com.onchain.model;

import lombok.Data;

/**
 * Created by ZhouQ on 2017/8/28.
 */
@Data
public class Result {
    public String Action;
    public int Error;
    public String Desc;
    public Object Result;
    public String Version;

    public Result() {
    }

    public Result(String action, int error, String desc, Object result) {
        Action = action;
        Error = error;
        Desc = desc;
        Result = result;
        Version = "v1";
    }

    private Result(String action, int error, String desc, Object result, String version) {
        Action = action;
        Error = error;
        Desc = desc;
        Result = result;
        Version = version;
    }
}
