package com.example.OA.service.forum;

import com.example.OA.dao.ForumMapper;
import com.example.OA.dao.ReplyMapper;
import com.example.OA.dao.TopicMapper;
import com.example.OA.dao.UserMapper;
import com.example.OA.model.Forum;
import com.example.OA.model.Reply;
import com.example.OA.model.Topic;
import com.example.OA.model.User;
import com.example.OA.model.VO.ReplyVO;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.CommonService;
import com.example.OA.util.IpUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by aa on 2017/11/1.
 */
@Service
public class ReplyService extends CommonService{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ReplyMapper replyMapper;

    @Autowired
    TopicMapper topicMapper;

    @Autowired
    ForumMapper forumMapper;

    @Autowired
    UserMapper userMapper;

    //更新回复状态
    public ServerResponse updateReplyStatus(Integer replyId,Short status) {
       try{
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
                   return ServerResponse.createBySuccess();
               }
               throw new AppException(Error.DATA_VERIFY_ERROR,"回复不存在");
           }
           throw new AppException(Error.PARAMS_ERROR);
       }catch (AppException e)
       {
           throw e;
       }catch (Exception e)
       {
           logger.debug(e.getMessage());
           throw new AppException(Error.UNKNOW_EXCEPTION);
       }
    }

    //添加一个回复
    @Transactional
    public ServerResponse add(Reply reply) {
        //增加一个回复后，对应的主题，版块 回复数加1
       try{
           if(reply != null)
           {
               reply.setReplayTime(new Date());
               reply.setStatus(Const.ReplyStatus.APPLY.getCode());
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
                           return ServerResponse.createBySuccess();
                       }
                       throw new AppException(Error.DATA_VERIFY_ERROR,"版块不存在");
                   }
                   throw new AppException(Error.DATA_VERIFY_ERROR,"主题不存在");
               }
               throw new AppException(Error.UNKNOW_EXCEPTION,"datebase error");
           }
           throw new AppException(Error.PARAMS_ERROR);
       }catch (AppException e)
       {
           throw e;
       }catch (Exception e)
       {
           e.printStackTrace();
           throw new AppException(Error.UNKNOW_EXCEPTION);
       }
    }

    //获取该用户的所有回复
    public PageInfo getAllReplyByUser(Integer id,Integer pageNum,Integer pageSize) {
        try{
            if(id != null)
            {
                PageHelper.startPage(pageNum,pageSize);
                List<Reply> replies = replyMapper.getAllReplyByUser(id);
                PageInfo pageInfo =  new PageInfo(replies);
                pageInfo.setList(convertReplyVOs(replies));
                return pageInfo;
            }
            throw new AppException(Error.PARAMS_ERROR);
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e)
        {
            logger.debug(e.getMessage());
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    public PageInfo getAllReplyByTopic(Integer topicId, Integer pageNum, Integer pageSize) {
        try{
            if(topicId != null)
            {
                PageHelper.startPage(pageNum,pageSize);
                List<Reply> replies = replyMapper.getAllReplyByTopic(topicId);
                PageInfo pageInfo = new PageInfo(replies);
                pageInfo.setList(convertReplyVOs(replies));
                return pageInfo;
            }
            throw new AppException(Error.PARAMS_ERROR);
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    public PageInfo<ReplyVO> getAll(Integer pageNum,Integer pageSize) {
        try{
            PageHelper.startPage(pageNum,pageSize);
            List<Reply> replies = replyMapper.getAll();
            PageInfo pageInfo = new PageInfo(replies);
            pageInfo.setList(convertReplyVOs(replies));
            return pageInfo;
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    public ReplyVO getReplyById(Integer replyId) {
        try{
            Reply reply = replyMapper.selectByPrimaryKey(replyId);
            return convertReplyVO(reply);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    public List<ReplyVO> convertReplyVOs(List<Reply> replies) {
        try{
            if(replies != null && !replies.isEmpty())
            {
                List<ReplyVO> result = Lists.newArrayList();
                for(Reply reply : replies)
                {
                    result.add(convertReplyVO(reply));
                }
                return result;
            }
            return null;
        }catch (Exception e)
        {
            throw e;
        }
    }

    public ReplyVO convertReplyVO(Reply reply) {
        try{
            if(reply != null)
            {
                ReplyVO replyVO = new ReplyVO();
                replyVO.setId(reply.getId());
                replyVO.setContent(reply.getContent());
                replyVO.setReplayTime(reply.getReplayTime());
                replyVO.setTitle(reply.getTitle());
                Topic topic = topicMapper.selectByPrimaryKey(reply.getTopicId());
                replyVO.setTopicTitle(topic.getTitle());
                replyVO.setTopicId(reply.getTopicId());
                User user = userMapper.selectByPrimaryKey(reply.getAuthor());
                replyVO.setAuthorName(user.getUsername());
                replyVO.setStatus(Const.ReplyStatus.codeof(reply.getStatus()).getValue());
                replyVO.setStatusName(Const.ReplyStatus.codeof(reply.getStatus()).name());
                replyVO.setIp(IpUtil.getLocation(reply.getIp()));

                return replyVO;
            }
            return null;
        }catch (IOException e)
        {
            e.printStackTrace();
            throw new AppException(Error.DATA_VERIFY_ERROR);
        }catch (Exception e)
        {
            throw e;
        }
    }

}
