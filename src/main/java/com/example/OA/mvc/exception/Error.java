package com.example.OA.mvc.exception;

public enum Error {
    UNKNOW_EXCEPTION(0, "unknow exception"),
    UN_AUTHORIZATION(10, "un authorization"),
    UN_AUTHENTICATION(11,"un authentication"),
    PARAMS_ERROR(100, "parasm error"),
    DATA_VERIFY_ERROR(101, "数据验证错误"),
    TARGET_EXISTSED(1001,"target existsed"),
    TARGET_NO_EXISTS(1002,"target NO existsed"),
    WORKFLOW_INNER_ERROR(1003,"工作流内部错误"),
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
