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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(value = "add_forum",method = RequestMethod.POST)
    public Forum add(Forum forum) {//要做表单验证
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {    //认证检查 ，授权检查
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(forum != null)
        {
            return forumService.add(getUserId(subject),forum);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //更新版块
    @RequestMapping(value = "update_forum",method = RequestMethod.POST)
    public Forum update(Forum forum) {  //表单验证
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(forum != null && forum.getId() != null)
        {
            return forumService.update(getUserId(subject),forum);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //获取该版块的所有主题
    @RequestMapping(value = "all_topic_forum",method = RequestMethod.POST)
    public List<Topic> getAllTopic(Integer forumId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(forumId != null )
        {
            return forumService.getAllTopic(getUserId(subject),forumId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //获取该版块最后发布的主题
    @RequestMapping(value = "last_topic_forum",method = RequestMethod.POST)
    public Topic getLastTopicByForum(Integer forumId) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(forumId != null )
        {
            return forumService.getLastTopicByForum(getUserId(subject),forumId);
        }
        throw new AppException(Error.PARAMS_ERROR);
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
    public Forum get(Integer forumId,String forumName) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(forumId != null)
        {
            return forumService.getByIdOrName(forumId,forumName);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }
}
