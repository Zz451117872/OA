package com.example.OA.service.manager;

import com.example.OA.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by aa on 2017/10/31.
 */
@Service
public class UserRoleService {

    @Autowired
    UserRoleMapper userRoleMapper;
}
