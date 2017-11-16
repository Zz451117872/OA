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
import org.springframework.transaction.annotation.Transactional;

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

    //添加主题
    @Transactional
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
                throw new AppException(Error.DATA_VERIFY_ERROR,"对应版块不存在");
            }
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //更新主题状态
    public int updateTopicStatus(Integer topicId,Short status) {
        if(topicId != null && status != null)
        {
            Topic topic = topicMapper.selectByPrimaryKey(topicId);
            if(topic != null)
            {
                topic.setStatus(status);
                topic.setUpdateTime(new Date());
                topicMapper.updateByPrimaryKeySelective(topic);
                return topicId;
            }
            throw new AppException(Error.DATA_VERIFY_ERROR,"主题不存在");
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //获取该主题的所有回复
    public List<Reply> getAllReply(Integer topicId) {
        if(topicId != null)
        {
            if(topicMapper.selectByPrimaryKey(topicId) != null)
            {
                return replyMapper.getAllByTopic(topicId);
            }
            throw new AppException(Error.DATA_VERIFY_ERROR,"主题不存在");
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //获取该主题的最后回复
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
                throw new AppException(Error.TARGET_NO_EXISTS,"没有回复");
            }
            throw new AppException(Error.DATA_VERIFY_ERROR,"主题不存在");
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //获取该用户的所有主题
    public List<Topic> getAllTopicByAuthor(Integer userId) {
        if(userId != null)
        {
            return topicMapper.getAllByAuthor(userId);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //通过主键或者名称 获取主题
    public Topic getByIdOrName(Integer topicId, String topicName) {
        if(topicId != null || StringUtils.isNotBlank(topicName))
        {
            return topicMapper.getByIdOrName(topicId,topicName);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }
}
