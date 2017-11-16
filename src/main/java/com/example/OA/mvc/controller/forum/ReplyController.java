package com.example.OA.mvc.controller.forum;

import com.example.OA.model.Reply;
import com.example.OA.model.Topic;
import com.example.OA.model.User;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.forum.ReplyService;
import com.example.OA.service.forum.TopicService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.ApplicationScope;

import javax.validation.Valid;
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
    @RequestMapping(value = "add_reply",method = RequestMethod.POST)
    public Reply add(@Valid Reply reply , BindingResult bindingResult) {  //要进行表单验证
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
            return replyService.add(reply);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //修改 回复状态
    @RequestMapping(value = "update_reply",method = RequestMethod.POST)
    public int updateReplyStatus(@RequestParam(value = "replyId",required = true) Integer replyId,
                                 @RequestParam(value = "status",required = true) Short status) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return replyService.updateReplyStatus(replyId,status);
    }

    //获得该用户的所有回复
    @RequestMapping(value = "all_reply_by_user",method = RequestMethod.POST)
    public List<Reply> getAllReplyByUser()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        User user = getUserBySubject(subject);
        return replyService.getAllReplyByUser(user.getId());
    }
}
