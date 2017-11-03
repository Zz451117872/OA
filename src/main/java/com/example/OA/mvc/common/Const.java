package com.example.OA.mvc.common;

public class Const {

    public static String salt = "geelysdafaqj23ou89ZXcj@#$@#$#@KJdjklj;D../dSF.";

    // 请假单状态
    public enum  LeaveStatus{
        APPLICATION(1,"申请中"),
        APPROVED(10,"已批准"),
        CANCELED(100,"已取消"),
        CLOSED(10000,"已关闭"),
        REJECTED(101,"已拒绝");
        private String value;
        private Integer code;
        LeaveStatus(Integer code,String value)
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

        public static LeaveStatus codeof(Integer code)
        {
            for(LeaveStatus leaveStatus : values())
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

    //支付类型
    public enum PaymentTypeEnum{
        ONLINE_PAY(1,"在线支付");
        private String value;
        private int code;
        PaymentTypeEnum(int code,String value)
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

        public static PaymentTypeEnum codeof(int code)
        {
            for(PaymentTypeEnum paymentTypeEnum : values())
            {
                if(paymentTypeEnum.getCode() == code)
                {
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }
}
