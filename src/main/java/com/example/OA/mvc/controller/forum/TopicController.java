package com.example.OA.mvc.controller.forum;

import com.example.OA.model.Forum;
import com.example.OA.model.Reply;
import com.example.OA.model.Topic;
import com.example.OA.model.User;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.forum.TopicService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("topic")
public class TopicController extends CommonController {

    @Autowired
    TopicService topicService;

    //添加主题
    @RequestMapping(value = "add_topic",method = RequestMethod.POST)
    public Topic add(Topic topic) {//要进行表单验证
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(topic != null)
        {
            topic.setAuthor(getUserId(subject));
            return topicService.add(topic);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //修改主题 状态
    @RequestMapping(value = "update_topic",method = RequestMethod.POST)
    public int updateTopicStatus(Integer topicId,Short status) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(topicId != null && status != null )
        {
            try{
                Const.TopicStatus.codeof(status);//对传入的状态值进行验证
            }catch (Exception e)
            {
                throw new AppException(Error.PARAMS_ERROR,"status error");
            }
            return topicService.updateTopicStatus(topicId,status);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //获取该主题下的所有回复
    @RequestMapping(value = "get_all_reply",method = RequestMethod.POST)
    public List<Reply> getAllReply(Integer topicId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(topicId != null )
        {
            return topicService.getAllReply(topicId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //获取该主题 的最后的回复
    @RequestMapping(value = "get_last_reply",method = RequestMethod.POST)
    public Reply getLastReplyByTopic(Integer topicId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(topicId != null )
        {
            return topicService.getLastReplyByTopic(topicId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "all_topic_author",method = RequestMethod.POST)
    public List<Topic> getAllTopicByAuthor(Integer userId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(userId != null )
        {
            return topicService.getAllTopicByAuthor(userId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    @RequestMapping(value = "topic_id_or_name",method = RequestMethod.POST)
    public Topic getByIdOrName(Integer topicId,String topicName)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(topicId != null || topicName != null)
        {
            return topicService.getByIdOrName(topicId,topicName);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }
}
