package com.ssx.common.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResultCode {

    SUCCESS(200000),
    ERROR(100000),

    //##########用户模块##############
    USERNAME_EXISTS(100100),
    USER_UN_EXISTS(100101),
    PASSWORD_ERROR(100102),
    USER_UN_LOGIN(100103),
    USER_HAS_LOGIN(100104),

    //##########评论模块##############
    COMMENT_TOO_LONG(100200),

    //##########资源模块##############
    RESOURCE_UN_EXIST(100300),
    RESOURCE_UNEXIST(100301),
    RESOURCE_NAME_REPEAT(100302);

    private final int code;

    private ResultCode(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }
}
