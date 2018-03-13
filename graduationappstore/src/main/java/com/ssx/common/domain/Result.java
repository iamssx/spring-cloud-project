package com.ssx.common.domain;

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

    public ResultCode getCode() {
        return code;
    }

    public void setCode(ResultCode code) {
        this.code = code;
        this.description = code.toString();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
}
