package com.example.OA.service;

import com.example.OA.mapper.UserMapper;
import com.example.OA.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by aa on 2017/10/30.
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User getByUsername(String username)
    {
        return userMapper.getByUsername(username);
    }

    public User add(String username, String password)
    {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userMapper.insert(user);
        return user;
    }

    public User login(String username, String password)
    {
        return userMapper.getByUsername(username);
    }
}
