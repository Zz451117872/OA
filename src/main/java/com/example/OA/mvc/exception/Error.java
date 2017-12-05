package com.example.OA.mvc.exception;

public enum Error {
    UNKNOW_EXCEPTION(101, "unknow exception"),
    UN_AUTHORIZATION(102, "un authorization"),
    UN_AUTHENTICATION(103,"un authentication"),
    PARAMS_ERROR(104, "参数错误"),
    DATA_VERIFY_ERROR(105, "数据验证错误"),
    TARGET_EXISTSED(106,"目标已存在"),
    TARGET_NO_EXISTS(107,"目标不存在 "),
    WORKFLOW_INNER_ERROR(108,"工作流内部错误"),
    DATABASE_OPERATION(109,"数据库操作错误"),
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
