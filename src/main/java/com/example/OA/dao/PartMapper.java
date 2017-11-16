package com.example.OA.dao;

import com.example.OA.model.Part;

import java.util.List;

public interface PartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Part record);

    int insertSelective(Part record);

    Part selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Part record);

    int updateByPrimaryKey(Part record);

    //-------------------------------------------------------
    Part getByPartname(String partName);

    List<Part> getAll();
}