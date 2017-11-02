package com.example.OA.service.forum;

import com.example.OA.dao.ForumMapper;
import com.example.OA.dao.TopicMapper;
import com.example.OA.model.Forum;
import com.example.OA.model.Topic;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.CommonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by aa on 2017/11/1.
 */
@Service
public class ForumService extends CommonService{

    @Autowired
    ForumMapper forumMapper;

    @Autowired
    TopicMapper topicMapper;

    public Forum add(Forum forum) {
        if(forum != null)
        {
            String forumName = forum.getForumName();//版块名称是唯一的
            if(forumMapper.getByForumName(forumName) == null)
            {
                forum.setCreateTime(new Date());
                forumMapper.insertSelective(forum);
                return forum;
            }
            throw new AppException(Error.EXISTSED,"target existed");
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public Forum update(Forum forum) {
        if(forum != null)
        {
            String forumName = forum.getForumName();
            if(forumMapper.getByForumName(forumName) == null)
            {
                forum.setUpdateTime(new Date());
                forum.setReplyCount(null);//一些数据不可以修改
                forum.setTopCount(null);
                forum.setLastTopic(null);
                forumMapper.updateByPrimaryKeySelective(forum);
                return forum;
            }
            throw new AppException(Error.EXISTSED,"target existed");
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }


    public List<Topic> getAllTopic(Integer forumId) {
        if(forumId != null)
        {
            if(forumMapper.selectByPrimaryKey(forumId) != null)
            {
                return topicMapper.getAllByForum(forumId);
            }
            throw new AppException(Error.NO_EXISTS,"target not existed");
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public Topic getLastTopicByForum(Integer forumId) {
        if(forumId != null)
        {
            Forum forum = forumMapper.selectByPrimaryKey(forumId);
            if(forum != null)
            {
                Integer topicId = forum.getLastTopic();
                if(topicId != null)
                {
                    return topicMapper.selectByPrimaryKey(topicId);
                }
                throw new AppException(Error.NO_EXISTS,"target not existed");
            }
            throw new AppException(Error.NO_EXISTS,"target not existed");
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public List<Forum> getAllForum() {
        return forumMapper.getAll();
    }

    public Forum getByIdOrName(Integer forumId, String forumName) {
        if(forumId != null || StringUtils.isNotBlank(forumName))
        {
            return forumMapper.getByIdOrName(forumId,forumName);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }
}
