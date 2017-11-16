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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by aa on 2017/11/1.
 */
@Service
public class ReplyService extends CommonService{

    @Autowired
    ReplyMapper replyMapper;

    @Autowired
    TopicMapper topicMapper;

    @Autowired
    ForumMapper forumMapper;

    //更新回复状态
    public int updateReplyStatus(Integer replyId,Short status) {
        if(replyId != null && status != null)
        {
            if(replyMapper.selectByPrimaryKey(replyId) != null)
            {
                try{
                    Const.ReplyStatus.codeof(status); //检查传入状态值是否合法
                }catch (Exception e)
                {
                    throw new AppException(Error.PARAMS_ERROR,"status error");
                }
                replyMapper.setStatusById(replyId,status);
                return replyId;
            }
            throw new AppException(Error.DATA_VERIFY_ERROR,"回复不存在");
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //添加一个回复
    @Transactional
    public Reply add(Reply reply) {
        //增加一个回复后，对应的主题，版块 回复数加1
        if(reply != null)
        {
            reply.setReplayTime(new Date());
            reply.setStatus(Const.ReplyStatus.ENABLE.getCode());
            int result = replyMapper.insert(reply);
            if(result >= 1)
            {
                Topic topic = topicMapper.selectByPrimaryKey(reply.getTopicId());
                if(topic != null)
                {
                    topic.setReplyCount(topic.getReplyCount()+1);//主题 回复数加1
                    topic.setLastReply(reply.getId());             //新增的回复就是 该主题的最后的回复
                    topicMapper.updateByPrimaryKey(topic);
                    Forum forum = forumMapper.selectByPrimaryKey(topic.getForumId());
                    if(forum != null)
                    {
                        forum.setReplyCount(forum.getReplyCount()+1);//版块 回复数加1
                        forumMapper.updateByPrimaryKey(forum);
                        return reply;
                    }
                    throw new AppException(Error.DATA_VERIFY_ERROR,"版块不存在");
                }
                throw new AppException(Error.DATA_VERIFY_ERROR,"主题不存在");
            }
            throw new AppException(Error.UNKNOW_EXCEPTION,"datebase error");
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //获取该用户的所有回复
    public List<Reply> getAllReplyByUser(Integer id) {
        if(id != null)
        {
            return replyMapper.getAllReplyByUser(id);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }


}
