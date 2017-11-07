package com.example.OA.dao;

import com.example.OA.model.Privilege;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PrivilegeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Privilege record);

    int insertSelective(Privilege record);

    Privilege selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Privilege record);

    int updateByPrimaryKey(Privilege record);

    //-------------------------------
    Privilege getByUrl(String url);

    List<Privilege> getByIds(@Param("privilegeIds") List<Integer> privilegeIds);
}