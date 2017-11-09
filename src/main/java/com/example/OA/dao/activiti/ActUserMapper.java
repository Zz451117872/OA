package com.example.OA.dao.activiti;


import com.example.OA.model.activiti.ActUser;

public interface ActUserMapper {
    int deleteByPrimaryKey(String id);

    int insert(ActUser record);

    int insertSelective(ActUser record);

    ActUser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ActUser record);

    int updateByPrimaryKey(ActUser record);
}