package com.ssx.common.domain;

import lombok.Data;

@Data
public class Result<T> {

    public static final Result ERROR = new Result();
    public static final Result SUCCESS = new Result();
    static {
        SUCCESS.setCode(ResultCode.SUCCESS);
        SUCCESS.setDescription(ResultCode.SUCCESS.toString());
        ERROR.setCode(ResultCode.ERROR);
        ERROR.setDescription(ResultCode.ERROR.toString());
    }

    private ResultCode code = ResultCode.SUCCESS;
    private String description;
    private T entity;

    public void setCode(ResultCode code) {
        this.code = code;
        this.description = code.toString();
    }


}
