package com.example.OA.dao.activiti;


import com.example.OA.model.activiti.SalaryAdjust;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SalaryAdjustMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SalaryAdjust record);

    int insertSelective(SalaryAdjust record);

    SalaryAdjust selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SalaryAdjust record);

    int updateByPrimaryKey(SalaryAdjust record);

    //////////////////////
    List<SalaryAdjust> getByApplicationOrStatus(@Param("application") Integer application, @Param("status")Integer status);

}