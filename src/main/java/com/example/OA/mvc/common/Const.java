package com.example.OA.mvc.common;

import java.util.Date;

public class Const {

    public static String salt = "geelysdafaqj23ou89ZXcj@#$@#$#@KJdjklj;D../dSF.";

    public interface processDefinitionKey{
        String LEAVE = "leave";
        String SALARY = "salaryAdjuct";
        String EXPENSE = "expense";
    }

    //用户任务类型
    public interface userTaskType{
        String ASSIGNEE = "assignee";
        String CANDIDATEUSER = "candidateUser";
        String CANDIDATEGROUP = "candidateGroup";
    }

    //工作流业务类型
    public interface BusinessType{
        String LEAVE = "leave";
        String SALARY = "salary";
        String EXPENSE = "expense";
    }

    // 请假单状态
    public enum  WorkflowStatus{
        APPLICATION(1,"审批中"),
        APPROVED(10,"已批准"),
        CANCELED(100,"已取消"),
        CLOSED(10000,"已关闭"),
        REJECTED(101,"已拒绝");
        private String value;
        private Integer code;
        WorkflowStatus(Integer code,String value)
        {
            this.code = code;
            this.value = value;
        }
        public String getValue() {
            return value;
        }
        public Integer getCode() {
            return code;
        }

        public static WorkflowStatus codeof(Integer code)
        {
            for(WorkflowStatus leaveStatus : values())
            {
                if(leaveStatus.getCode() == code)
                {
                    return leaveStatus;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }



    //回复状态
    public enum  ReplyStatus{
        DISABLE((short)0,"不显示"),
        ENABLE((short)1,"显示");
        private String value;
        private Short code;
        ReplyStatus(Short code,String value)
        {
            this.code = code;
            this.value = value;
        }
        public String getValue() {
            return value;
        }
        public Short getCode() {
            return code;
        }

        public static ReplyStatus codeof(Short code)
        {
            for(ReplyStatus replyStatus : values())
            {
                if(replyStatus.getCode() == code)
                {
                    return replyStatus;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    //主题状态
    public enum TopicStatus{
        APPLY((short)1,"申请"),
        REJECT((short)0,"拒绝"),
        PASS((short)2,"通过"),
        CLOSED((short)10,"关闭");
        private String value;
        private Short code;

        TopicStatus(Short code,String value)
        {
            this.code = code;
            this.value = value;
        }
        public String getValue() {
            return value;
        }
        public Short getCode() {
            return code;
        }

        public static TopicStatus codeof(Short code)
        {
            for(TopicStatus topicStatus : values())
            {
                if(topicStatus.getCode() == code)
                {
                    return topicStatus;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }


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

        public static Error codeof(Short code)
        {
            for(Error error : values())
            {
                if(error.getCode() == code)
                {
                    return error;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    public enum PropertyType {
        S(String.class),
        I(Integer.class),
        L(Long.class),
        F(Float.class),
        N(Double.class),
        D(Date.class),
        SD(java.sql.Date.class),
        B(Boolean.class);

        private Class<?> clazz;

        private PropertyType(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Class<?> getValue() {
            return clazz;
        }
    }

}
