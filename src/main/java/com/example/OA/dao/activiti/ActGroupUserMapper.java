package com.example.OA.dao.activiti;


import com.example.OA.model.activiti.ActGroupUserKey;

public interface ActGroupUserMapper {
    int deleteByPrimaryKey(ActGroupUserKey key);

    int insert(ActGroupUserKey record);

    int insertSelective(ActGroupUserKey record);
}