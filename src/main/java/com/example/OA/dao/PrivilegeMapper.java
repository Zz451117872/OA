package com.example.OA.dao;

import com.example.OA.model.Privilege;

public interface PrivilegeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Privilege record);

    int insertSelective(Privilege record);

    Privilege selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Privilege record);

    int updateByPrimaryKey(Privilege record);

    //-------------------------------
    Privilege getByUrl(String url);
}