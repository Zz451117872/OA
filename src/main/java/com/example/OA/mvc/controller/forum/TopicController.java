package com.example.OA.mvc.controller.forum;

import com.example.OA.model.Reply;
import com.example.OA.model.Topic;
import com.example.OA.model.User;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.forum.TopicService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("topic")
public class TopicController extends CommonController {

    @Autowired
    TopicService topicService;

    //添加主题
    @RequestMapping(value = "add_topic",method = RequestMethod.POST)
    public Topic add(@Valid Topic topic , BindingResult bindingResult) {//要进行表单验证
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(bindingResult.hasErrors())
        {
            throw new AppException(Error.PARAMS_ERROR,bindingResult.getFieldError().getDefaultMessage());
        }
        User user = getUserBySubject(subject);
        if(topic != null)
        {
            topic.setAuthor(user.getId());
            return topicService.add(topic);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //修改主题 状态
    @RequestMapping(value = "update_topic",method = RequestMethod.POST)
    public int updateTopicStatus(@RequestParam(value = "topicId",required = true) Integer topicId,
                                 @RequestParam(value = "status",required = true) Short status) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
            try{
                Const.TopicStatus.codeof(status);//对传入的状态值进行验证
            }catch (Exception e)
            {
                throw new AppException(Error.PARAMS_ERROR,"status error");
            }
            return topicService.updateTopicStatus(topicId,status);
    }

    //获取该主题下的所有回复
    @RequestMapping(value = "get_all_reply",method = RequestMethod.POST)
    public List<Reply> getAllReply(@RequestParam(value = "topicId",required = true) Integer topicId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return topicService.getAllReply(topicId);
    }

    //获取该主题 的最后的回复
    @RequestMapping(value = "get_last_reply",method = RequestMethod.POST)
    public Reply getLastReplyByTopic(@RequestParam(value = "topicId",required = true) Integer topicId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return topicService.getLastReplyByTopic(topicId);
    }

    @RequestMapping(value = "all_topic_author",method = RequestMethod.POST)
    public List<Topic> getAllTopicByAuthor(@RequestParam(value = "userId",required = true) Integer userId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return topicService.getAllTopicByAuthor(userId);
    }

    @RequestMapping(value = "topic_id_or_name",method = RequestMethod.POST)
    public Topic getByIdOrName(@RequestParam(value = "topicId",required = false) Integer topicId,
                               @RequestParam(value = "topicName",required = false) String topicName)
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
