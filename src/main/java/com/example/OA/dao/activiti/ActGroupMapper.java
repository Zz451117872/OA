package com.example.OA.dao.activiti;


import com.example.OA.model.activiti.ActGroup;

public interface ActGroupMapper {
    int deleteByPrimaryKey(String id);

    int insert(ActGroup record);

    int insertSelective(ActGroup record);

    ActGroup selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ActGroup record);

    int updateByPrimaryKey(ActGroup record);
}