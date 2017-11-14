package com.example.OA.dao.activiti;

import com.example.OA.model.activiti.Leave;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LeaveMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Leave record);

    int insertSelective(Leave record);

    Leave selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Leave record);

    int updateByPrimaryKey(Leave record);

    //==================================
    List<Leave> getByApplicationAndStatus(@Param("application") Integer application, @Param("status")Integer status);
}