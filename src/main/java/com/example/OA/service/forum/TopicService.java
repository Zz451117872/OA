package com.example.OA.service.forum;

import com.example.OA.dao.ForumMapper;
import com.example.OA.dao.ReplyMapper;
import com.example.OA.dao.TopicMapper;
import com.example.OA.dao.UserMapper;
import com.example.OA.model.Forum;
import com.example.OA.model.Reply;
import com.example.OA.model.Topic;
import com.example.OA.model.User;
import com.example.OA.model.VO.TopicVO;
import com.example.OA.mvc.common.Const;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.CommonService;
import com.example.OA.util.IpUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.catalina.Loader;
import org.apache.commons.lang3.StringUtils;
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
public class TopicService extends CommonService{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TopicMapper topicMapper;

    @Autowired
    ForumMapper forumMapper;

    @Autowired
    ReplyMapper replyMapper;

    @Autowired
    UserMapper userMapper;

    //添加主题
    @Transactional
    public ServerResponse add(Topic topic) {
        //增加 主题后，对应的版块 主题数量加1
        try{
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
                        return ServerResponse.createBySuccess();
                    }
                    throw new AppException(Error.DATA_VERIFY_ERROR,"对应版块不存在");
                }
            }
            throw new AppException(Error.PARAMS_ERROR);
        }catch (AppException e)
        {
            throw e;
        }catch (Exception e)
        {
            logger.debug(e.getMessage());
            throw  new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    //更新主题状态
    public ServerResponse updateTopicStatus(Integer topicId, Short status) {
        try{
            if(topicId != null && status != null)
            {
                Topic topic = topicMapper.selectByPrimaryKey(topicId);
                if(topic != null)
                {
                    topic.setStatus(status);
                    topic.setUpdateTime(new Date());
                    topicMapper.updateByPrimaryKeySelective(topic);
                    return ServerResponse.createBySuccess();
                }
                throw new AppException(Error.DATA_VERIFY_ERROR,"主题不存在");
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

    //获取该用户的所有主题
    public PageInfo<TopicVO> getAllTopicByAuthor(Integer userId,Integer pageNum,Integer pageSize) {
        try{
            if(userId != null)
            {
                PageHelper.startPage(pageNum,pageSize);
                List<Topic> topics = topicMapper.getAllByAuthor(userId);
                PageInfo pageInfo = new PageInfo(topics);
                pageInfo.setList(convertTopicVOs(topics));
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

    public PageInfo<TopicVO> getAllTopicByForum(Integer forumId, Integer pageNum, Integer pageSize) {
        try{
            if(forumId != null)
            {
                PageHelper.startPage(pageNum,pageSize);
                List<Topic> topics = topicMapper.getAllTopicByForum(forumId);
                PageInfo pageInfo =  new PageInfo(topics);
                pageInfo.setList(convertTopicVOs(topics));
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

    //通过主键或者名称 获取主题
    public TopicVO getByIdOrName(Integer topicId, String topicName) {
        try{
            if(topicId != null || StringUtils.isNotBlank(topicName))
            {
                Topic topic =  topicMapper.getByIdOrName(topicId,topicName);
                return convertTopicVO(topic);
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

    public PageInfo<TopicVO> getAll(Integer pageNum, Integer pageSize) {
        try{
            PageHelper.startPage(pageNum,pageSize);
            List<Topic> topics = topicMapper.getAll();
            PageInfo pageInfo = new PageInfo(topics);
            pageInfo.setList(convertTopicVOs(topics));
            return pageInfo;
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new AppException(Error.UNKNOW_EXCEPTION);
        }
    }

    private List<TopicVO> convertTopicVOs(List<Topic> topics) {
        try{
            if( topics != null && !topics.isEmpty())
            {
                List<TopicVO> result = Lists.newArrayList();
                for(Topic topic : topics)
                {
                    result.add(convertTopicVO(topic));
                }
                return result;
            }
            return null;
        }catch (Exception e)
        {
            throw e;
        }
    }

    private TopicVO convertTopicVO(Topic topic) {
        try{
            if(topic != null )
            {
                TopicVO topicVO = new TopicVO();
                topicVO.setId(topic.getId());
                topicVO.setTitle(topic.getTitle());
                topicVO.setContent(topic.getContent());
                topicVO.setCreateTime(topic.getCreateTime());
                topicVO.setUpdateTime(topic.getUpdateTime());
                topicVO.setIp(IpUtil.getLocation(topic.getIp()));
                User user = userMapper.selectByPrimaryKey(topic.getAuthor());
                topicVO.setAuthorName(user.getUsername());
                Forum forum = forumMapper.selectByPrimaryKey(topic.getForumId());
                topicVO.setForumName(forum.getForumName());
                topicVO.setLastReply(topic.getLastReply());
                topicVO.setReplyCount(topic.getReplyCount());
                topicVO.setStatus(Const.TopicStatus.codeof(topic.getStatus()).getValue());
                topicVO.setStatusName(Const.TopicStatus.codeof(topic.getStatus()).name());
                return  topicVO;
            }
            return null;
        }catch (IOException e)
        {
            throw new AppException(Error.DATA_VERIFY_ERROR);
        }catch (Exception e)
        {
            throw e;
        }
    }


}
