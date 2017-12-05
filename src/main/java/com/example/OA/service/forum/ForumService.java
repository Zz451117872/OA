package com.example.OA.service.forum;

import com.example.OA.dao.ForumMapper;
import com.example.OA.dao.TopicMapper;
import com.example.OA.model.Forum;
import com.example.OA.model.Topic;
import com.example.OA.model.VO.ForumVO;
import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.service.CommonService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
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

    //添加版块
    public ServerResponse add(Forum forum) {
        if(forum != null)
        {
            String forumName = forum.getForumName();//版块名称是唯一的
            if(forumMapper.getByForumName(forumName) == null)
            {
                forum.setCreateTime(new Date());
                int result = forumMapper.insertSelective(forum);
                if(result > 0)
                {
                    return ServerResponse.createBySuccess();
                }
                return ServerResponse.createByErrorMessage("database inner error");
            }
            throw new AppException(Error.DATA_VERIFY_ERROR,"版块名已存在");
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    //更新版块
    public ServerResponse update(Forum forum) {
        if(forum != null)
        {
            String forumName = forum.getForumName();
            Forum temp = forumMapper.getByForumName(forumName);
            if(temp == null || (temp != null && temp.getId() == forum.getId()) )
            {
                forum.setUpdateTime(new Date());
                forum.setReplyCount(null);//一些数据不可以修改
                forum.setTopCount(null);
                forum.setLastTopic(null);
                int result = forumMapper.updateByPrimaryKeySelective(forum);
                if(result > 0)
                {
                    return ServerResponse.createBySuccess();
                }
                return ServerResponse.createByErrorMessage("database inner error");
            }
            throw new AppException(Error.DATA_VERIFY_ERROR,"版块名已存在");
        }
        throw new AppException(Error.PARAMS_ERROR);
    }


    //所有版块
    public PageInfo<ForumVO> getAllForum(Integer pageNum, Integer pageSize) {
        try{
            PageHelper.startPage(pageNum,pageSize);
            List<Forum> forumList = forumMapper.getAll();
            PageInfo pageInfo = new PageInfo(forumList);
            pageInfo.setList(convertForumVOs(forumList));
            return pageInfo;
        }catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    private List<ForumVO> convertForumVOs(List<Forum> forumList) {
        try{
            if(forumList != null && !forumList.isEmpty())
            {
                List<ForumVO> result = Lists.newArrayList();
                for(Forum forum : forumList)
                {
                    result.add(convertForumVO(forum));
                }
                return result;
            }
            return null;
        }catch (Exception e)
        {
            throw e;
        }
    }

    private ForumVO convertForumVO(Forum forum) {
        try{
            if(forum != null)
            {
                ForumVO result = new ForumVO();
                result.setId(forum.getId());
                result.setForumName(forum.getForumName());
                result.setCreateTime(forum.getCreateTime());
                result.setUpdateTime(forum.getUpdateTime());
                result.setDescription(forum.getDescription());
                result.setReplyCount(forum.getReplyCount());
                result.setTopCount(forum.getTopCount());
                result.setSorts(forum.getSorts());
                Topic topic = topicMapper.selectByPrimaryKey(forum.getLastTopic());
                String lastTopicName = topic == null ? null:topic.getTitle();
                result.setLastTopic(forum.getLastTopic());
                result.setLastTopicName(lastTopicName);
                return result;
            }
            return null;
        }catch (Exception e)
        {
            throw e;
        }
    }

    //通过主键或者名称获取版块
    public ForumVO getByIdOrName(Integer forumId, String forumName) {
            if (forumId != null || forumName != null) {
                Forum forum = forumMapper.getByIdOrName(forumId,forumName);
                return convertForumVO(forum);
            }
            throw new AppException(Error.PARAMS_ERROR, "param error");
    }

    //删除版块
    public void deleteForumById(Integer forumId) {

    }
}
