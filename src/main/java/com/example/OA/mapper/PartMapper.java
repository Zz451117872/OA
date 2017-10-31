package com.example.OA.mapper;

import com.example.OA.model.Part;

public interface PartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Part record);

    int insertSelective(Part record);

    Part selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Part record);

    int updateByPrimaryKey(Part record);

    //-------------------------------------------------------
    Part getByPartname(String partName);
}