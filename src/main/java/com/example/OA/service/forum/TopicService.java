package com.example.OA.service.forum;

import com.example.OA.dao.ForumMapper;
import com.example.OA.dao.ReplyMapper;
import com.example.OA.dao.TopicMapper;
import com.example.OA.model.Forum;
import com.example.OA.model.Reply;
import com.example.OA.model.Topic;
import com.example.OA.mvc.common.Const;
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
public class TopicService extends CommonService{

    @Autowired
    TopicMapper topicMapper;

    @Autowired
    ForumMapper forumMapper;

    @Autowired
    ReplyMapper replyMapper;

    public Topic add(Topic topic) {
        //增加 主题后，对应的版块 主题数量加1
        if(topic != null)
        {
            topic.setStatus(Const.TopicStatus.APPLY.getCode());
            topic.setCreateTime(new Date());
            int result = topicMapper.insertSelective(topic); //添加主题
            if(result >= 1)
            {
                Forum forum = forumMapper.selectByPrimaryKey(topic.getForumId());
                if(forum != null)
                {               //更新主题 所属版块信息
                    forum.setTopCount(forum.getTopCount()+1);
                    forum.setLastTopic(topic.getId());
                    forumMapper.updateByPrimaryKeySelective(forum);
                    return topic;
                }
                throw new AppException(Error.NO_EXISTS,"target not exist");
            }
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public int updateTopicStatus(Integer topicId,Short status) {
        if(topicId != null && status != null)
        {
            Topic topic = topicMapper.selectByPrimaryKey(topicId);
            if(topic != null)
            {
                topic.setStatus(status);
                topic.setCreateTime(new Date());
                topicMapper.updateByPrimaryKeySelective(topic);
                return topicId;
            }
            throw new AppException(Error.NO_EXISTS,"target not existed");
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }


    public List<Reply> getAllReply(Integer topicId) {
        if(topicId != null)
        {
            if(topicMapper.selectByPrimaryKey(topicId) != null)
            {
                return replyMapper.getAllByTopic(topicId);
            }
            throw new AppException(Error.NO_EXISTS,"target not existed");
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public Reply getLastReplyByTopic(Integer topicId) {
        if(topicId != null)
        {
            Topic topic = topicMapper.selectByPrimaryKey(topicId);
            if(topic != null)
            {
              Integer replyId = topic.getLastReply();
                if(replyId != null)
                {
                    return replyMapper.selectByPrimaryKey(replyId);
                }
                throw new AppException(Error.NO_EXISTS,"target not existed");
            }
            throw new AppException(Error.NO_EXISTS,"target not existed");
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }

    public List<Topic> getAllTopicByAuthor(Integer userId) {

        if(userId != null)
        {
            return topicMapper.getAllByAuthor(userId);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }


    public Topic getByIdOrName(Integer topicId, String topicName) {
        if(topicId != null || StringUtils.isNotBlank(topicName))
        {
            return topicMapper.getByIdOrName(topicId,topicName);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }
}
