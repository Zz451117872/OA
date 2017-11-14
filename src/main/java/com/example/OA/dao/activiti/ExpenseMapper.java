package com.example.OA.dao.activiti;


import com.example.OA.model.activiti.Expense;

public interface ExpenseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Expense record);

    int insertSelective(Expense record);

    Expense selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Expense record);

    int updateByPrimaryKey(Expense record);
}