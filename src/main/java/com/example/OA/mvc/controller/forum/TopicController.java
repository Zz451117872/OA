package com.example.OA.mvc.controller.forum;

import com.example.OA.model.Reply;
import com.example.OA.model.Topic;
import com.example.OA.model.User;
import com.example.OA.model.VO.TopicVO;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.forum.TopicService;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("topic")
public class TopicController extends CommonController {

    @Autowired
    TopicService topicService;

    //添加主题
    @RequestMapping(value = "add_topic.do",method = RequestMethod.POST)
    public ServerResponse add(@Valid Topic topic , BindingResult bindingResult, HttpServletRequest request) {
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
            topic.setIp( getIpAddr(request));
            System.out.println("ip:"+topic.getIp());
            topic.setAuthor(user.getId());
            return topicService.add(topic);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //修改主题 状态
    @RequestMapping(value = "update_topic.do",method = RequestMethod.POST)
    public ServerResponse updateTopicStatus(@RequestParam(value = "topicId",required = true) Integer topicId,
                                 @RequestParam(value = "statusName",required = true) String statusName) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
            Short status = null;
            try{
                status = Const.TopicStatus.getTopicStatus(statusName).getCode();
            }catch (Exception e)
            {
                throw new AppException(Error.PARAMS_ERROR,"status error");
            }
            return topicService.updateTopicStatus(topicId,status);
    }

    //获取该 作者 发表的所有主题 ，不填充 主题 的回复信息
    @RequestMapping(value = "all_topic_author.do",method = RequestMethod.POST)
    public PageInfo<TopicVO> getAllTopicByAuthor(@RequestParam(value = "userId",required = true) Integer userId,
                                                 @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                                                 @RequestParam(value = "pageSize",required = false,defaultValue = "3")Integer pageSize)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return topicService.getAllTopicByAuthor(userId,pageNum,pageSize);
    }

    //获取该 版块 的所有主题
    @RequestMapping(value = "get_topic_by_forum.do",method = RequestMethod.POST)
    public PageInfo<TopicVO> getAllTopicByForum(@RequestParam(value = "forumId",required = true) Integer forumId,
                                        @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                                        @RequestParam(value = "pageSize",required = false,defaultValue = "3")Integer pageSize)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return topicService.getAllTopicByForum(forumId,pageNum,pageSize);
    }

    //通过 主题 id 或者 名字 获取 主题信息，并填充 该主题的 所有回复 信息
    @RequestMapping(value = "topic_id_or_name.do",method = RequestMethod.POST)
    public TopicVO getByIdOrName(@RequestParam(value = "topicId",required = false) Integer topicId,
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

    //获取所有主题，不填充 主题 的 回复信息
    @RequestMapping(value = "all_topic.do",method = RequestMethod.POST)
    public PageInfo<TopicVO> getAll(@RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                              @RequestParam(value = "pageSize",required = false,defaultValue = "3")Integer pageSize)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return topicService.getAll(pageNum,pageSize);
    }

    @RequestMapping(value = "all_topic_status.do",method = RequestMethod.POST)
    public List<Const.TopicStatus> getAllTopicStatus()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return Arrays.asList(Const.TopicStatus.values());
    }
}
