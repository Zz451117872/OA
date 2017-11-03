package com.example.OA.dao;

import com.example.OA.model.Dispose;

import java.util.List;

public interface DisposeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Dispose record);

    int insertSelective(Dispose record);

    Dispose selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Dispose record);

    int updateByPrimaryKey(Dispose record);

    //----------------------
    List<Dispose> getByAuditer(String username);

    List<Dispose> getAll();

    List<String> getAuditerNameByLeave(Integer leaveId);

    List<Dispose> getByLeave(Integer leaveId);
}