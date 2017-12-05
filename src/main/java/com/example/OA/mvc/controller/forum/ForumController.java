package com.example.OA.mvc.controller.forum;

import com.example.OA.model.Forum;
import com.example.OA.model.Topic;
import com.example.OA.model.VO.ForumVO;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.controller.CommonController;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.forum.ForumService;
import com.github.pagehelper.PageInfo;
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

    /*
    添加 或者修改 版块
    forum：使用注解进行表单验证
     */
    @RequestMapping(value = "add_or_update_forum.do",method = RequestMethod.POST)
    public ServerResponse addOrUpdate(@Valid Forum forum , BindingResult bindingResult) {
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
               return forumService.update(forum);
            } else {
              return  forumService.add(forum);
                }
            }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //获取所有版块
    @RequestMapping(value = "all_forum.do",method = RequestMethod.POST)
    public PageInfo<ForumVO> getAllForum(@RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                                         @RequestParam(value = "pageSize",required = false,defaultValue = "3")Integer pageSize) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        return forumService.getAllForum(pageNum,pageSize);
    }

    //根据 主键或者版块名称 获取版块信息
    @RequestMapping(value = "get_forum.do",method = RequestMethod.POST)
    public ForumVO get(@RequestParam(value = "forumId",required = false) Integer forumId,
                     @RequestParam(value = "forumName",required = false) String forumName) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        if(forumId == null && forumName == null)
            throw new AppException(Error.PARAMS_ERROR);
        return forumService.getByIdOrName(forumId,forumName);
    }

    @RequestMapping(value = "delete_forum.do",method = RequestMethod.POST)
    public void delete(@RequestParam(value = "forumId",required = true) Integer forumId)
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()) {
            throw new AppException(Error.UN_AUTHORIZATION);
        }
        forumService.deleteForumById(forumId);
        return;
    }
}
