package com.example.OA.mvc.common;

public class Const {

    public static String salt = "geelysdafaqj23ou89ZXcj@#$@#$#@KJdjklj;D../dSF.";
    //回复状态
    public interface ReplyStatus{

    }

    //主题状态
    public interface TopicStatus{

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
