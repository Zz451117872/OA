package com.example.OA.util;

import com.example.OA.mvc.common.Const;

/**
 * Created by aa on 2017/12/4.
 */
public class Test {

    public  static void main(String[] str)
    {
       String s = "INVALID";
        Const.TopicStatus topicStatus = Const.TopicStatus.getTopicStatus(s);
        System.out.println(topicStatus.getCode()+":"+topicStatus.getValue());
    }
}
