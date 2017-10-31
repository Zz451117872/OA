package com.example.OA.mvc.exception;

public enum Error {
    UNKNOW_EXCEPTION(0, "unknow exception"),
    UN_AUTHORIZATION(10, "un authorization"),
    PARAMS_ERROR(100, "parasm valid %s"),
    USERNAME_OR_PASSWORD_ERROR(1000, "username or password error"),
    INVALID_PARAMS(10000, "invalid params: %s"),
    EXISTSED(1001,"target existsed"),
    NO_EXISTS(1002,"target NO existsed"),
    ;

    private int code;
    private String msg;

    Error(int code) {
        this.code = code;
        this.msg = this.name();
    }

    Error(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }

}
