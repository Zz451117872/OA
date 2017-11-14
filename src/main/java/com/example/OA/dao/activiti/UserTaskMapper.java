package com.example.OA.dao.activiti;


import com.example.OA.model.activiti.UserTask;

import java.util.List;

public interface UserTaskMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserTask record);

    int insertSelective(UserTask record);

    UserTask selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserTask record);

    int updateByPrimaryKey(UserTask record);

    //////////////////////////

    List<UserTask> getByPdfKey(String processDefinitionKey);

    void deleteAll();
}