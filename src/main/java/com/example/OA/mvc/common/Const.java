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

    // 业务状态
    public enum  BusinessStatus{
        APPLICATION(201,"审批中"),
        REJECTED(202,"已拒绝"),
        PASSED(203,"已通过"),
        CANCELED(204,"已取消"),
        CLOSED(205,"已关闭");
        private String value;
        private int code;

        BusinessStatus(int code,String value)
        {
            this.code = code;
            this.value = value;
        }
        public String getValue() {
            return value;
        }
        public int getCode() {
            return code;
        }

        public static BusinessStatus codeof(int code)
        {
            for(BusinessStatus businessStatus : values())
            {
                if(businessStatus.getCode() == code)
                {
                    return businessStatus;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }




    //回复状态
    public enum  ReplyStatus{
        APPLY((short)1,"申请"),
        INVALID((short)100,"无效的"),
        PASS((short)2,"通过");
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

        public static ReplyStatus getReplyStatus(String replyStatusStr)
        {
            for(ReplyStatus replyStatus : values())
            {
                if(replyStatus.name().equals(replyStatusStr))
                {
                    return replyStatus;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
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
        INVALID((short)100,"无效的"),
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

        public static TopicStatus getTopicStatus(String topicStatusStr)
        {
            for(TopicStatus topicStatus : values())
            {
                if(topicStatus.name().equals(topicStatusStr))
                {
                    return topicStatus;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
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

}
