package com.example.OA.mvc.controller.forum;

import com.example.OA.model.Reply;
import com.example.OA.model.Topic;
import com.example.OA.model.User;
import com.example.OA.model.VO.ReplyVO;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.forum.ReplyService;
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
import org.springframework.web.context.annotation.ApplicationScope;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * Created by aa on 2017/10/31.
 */

@RestController
@RequestMapping("reply")
public class ReplyController extends CommonController{

    @Autowired
    ReplyService replyService;

    //新增回复
    @RequestMapping(value = "add_reply.do",method = RequestMethod.POST)
    public ServerResponse add(@Valid Reply reply , BindingResult bindingResult, HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(bindingResult.hasErrors())
        {
            throw new AppException(Error.PARAMS_ERROR,bindingResult.getFieldError().getDefaultMessage());
        }
        User user = getUserBySubject(subject);
        if(reply != null)
        {
            reply.setAuthor(user.getId());
            reply.setIp( getIpAddr(request));
            System.out.println("ip:"+reply.getIp());
            return replyService.add(reply);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //修改 回复状态
    @RequestMapping(value = "update_reply.do",method = RequestMethod.POST)
    public ServerResponse updateReplyStatus(@RequestParam(value = "replyId",required = true) Integer replyId,
                                            @RequestParam(value = "statusName",required = true) String statusName) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        Short status = null;
        try{
            status = Const.ReplyStatus.getReplyStatus(statusName).getCode();
        }catch (Exception e)
        {
            throw e;
        }
        return replyService.updateReplyStatus(replyId,status);
    }

    //获得该用户的所有回复
    @RequestMapping(value = "all_reply_by_user.do",method = RequestMethod.POST)
    public PageInfo<ReplyVO> getAllReplyByUser(@RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                                         @RequestParam(value = "pageSize",required = false,defaultValue = "3")Integer pageSize)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        return replyService.getAllReplyByUser(user.getId(),pageNum,pageSize);
    }

    //获得该用户的所有回复
    @RequestMapping(value = "all_reply_by_topic.do",method = RequestMethod.POST)
    public PageInfo<ReplyVO> getAllReplyByTopic(@RequestParam(value = "topicId",required = true)Integer topicId,
                                       @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                                      @RequestParam(value = "pageSize",required = false,defaultValue = "3")Integer pageSize)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return replyService.getAllReplyByTopic(topicId,pageNum,pageSize);
    }

    @RequestMapping(value = "all_reply.do",method = RequestMethod.POST)
    public PageInfo<ReplyVO> getAll(@RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                           @RequestParam(value = "pageSize",required = false,defaultValue = "3")Integer pageSize)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return replyService.getAll(pageNum,pageSize);
    }

    @RequestMapping(value = "get_reply_by_id.do",method = RequestMethod.POST)
    public ReplyVO getReplyById(@RequestParam(value = "replyId",required = true)Integer replyId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return replyService.getReplyById(replyId);
    }

    @RequestMapping(value = "get_reply_status.do",method = RequestMethod.POST)
    public List<Const.ReplyStatus> getAllReplyStatus()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return Arrays.asList(Const.ReplyStatus.values());
    }
}
