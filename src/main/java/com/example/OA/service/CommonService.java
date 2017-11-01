package com.example.OA.service;

import com.example.OA.dao.UserMapper;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by aa on 2017/11/1.
 */
@Service
public class CommonService {

    @Autowired
    UserMapper userMapper;

    public void verification(Integer userId ) {
        if(userId != null)
        {
            if(userMapper.selectByPrimaryKey(userId) == null)
                throw new AppException(Error.UN_AUTHORIZATION);
        }
        throw new AppException(Error.PARAMS_ERROR,"param error");
    }
}
