package com.example.OA.mvc.controller.forum;

import com.example.OA.model.Forum;
import com.example.OA.model.Topic;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.forum.ForumService;
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

/**
 * Created by aa on 2017/10/31.
 */
@RestController
@RequestMapping("forum")
public class ForumController extends CommonController {

    @Autowired
    ForumService forumService;

    //添加版块
    @RequestMapping(value = "add_or_update_forum",method = RequestMethod.POST)
    public void addOrUpdate(@Valid Forum forum , BindingResult bindingResult) {//要做表单验证
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {    //认证检查 ，授权检查
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(bindingResult.hasErrors())
        {
            throw new AppException(Error.PARAMS_ERROR,bindingResult.getFieldError().getDefaultMessage());
        }
        if (forum != null) {
            if (forum.getId() != null) {
                forumService.update(forum);
            } else {
                forumService.add(forum);
                }
            }
        throw new AppException(Error.PARAMS_ERROR);
    }


    //获取该版块的所有主题
    @RequestMapping(value = "all_topic_forum",method = RequestMethod.POST)
    public List<Topic> getAllTopic(@RequestParam(value = "forumId", required = true) Integer forumId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return forumService.getAllTopic(forumId);
    }

    //获取该版块最后发布的主题
    @RequestMapping(value = "last_topic_forum",method = RequestMethod.POST)
    public Topic getLastTopicByForum(@RequestParam(value = "forumId", required = true) Integer forumId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return forumService.getLastTopicByForum(forumId);
    }

    //获取所有版块
    @RequestMapping(value = "all_forum",method = RequestMethod.POST)
    public List<Forum> getAllForum() {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return forumService.getAllForum();
    }

    //根据 主键或者版块名称 获取版块信息
    @RequestMapping(value = "get_forum",method = RequestMethod.POST)
    public Forum get(@RequestParam(value = "forumId",required = true) Integer forumId,
                     @RequestParam(value = "forumName",required = true) String forumName) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return forumService.getByIdOrName(forumId,forumName);
    }
}
