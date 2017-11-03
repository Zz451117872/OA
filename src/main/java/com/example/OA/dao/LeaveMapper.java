package com.example.OA.dao;

import com.example.OA.model.Leave;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

public interface LeaveMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Leave record);

    int insertSelective(Leave record);

    Leave selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Leave record);

    int updateByPrimaryKey(Leave record);
    
    //----------------------------------

    Leave getByIdAndApplication(@Param("leaveId") Integer leaveId, @Param("userId")Integer userId);

    int updateLeaveStatus(@Param("leaveId")Integer leaveId, @Param("status")Integer status);

    List<Leave> getByApplicationAndStatus(@Param("userId")Integer userId, @Param("status")Integer status);

    List<Leave> getAllByStatus(Integer status);

    List<Leave> myApplicationEorStatus(@Param("userId")Integer userId,@Param("status")Integer status);

    List<Leave> getByApplication(Integer userId);
}