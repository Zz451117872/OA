package com.example.OA.service.manager;

import com.example.OA.mapper.PrivilegeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by aa on 2017/10/31.
 */
@Service
public class PrivilegeService {

    @Autowired
    PrivilegeMapper privilegeMapper;
}
