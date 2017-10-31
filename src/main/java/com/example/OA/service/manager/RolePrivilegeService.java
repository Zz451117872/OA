package com.example.OA.service.manager;

import com.example.OA.mapper.RolePrivilegeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by aa on 2017/10/31.
 */
@Service
public class RolePrivilegeService {

    @Autowired
    RolePrivilegeMapper rolePrivilegeMapper;
}
