package com.example.OA.service.activiti;

import com.example.OA.model.Leave;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by aa on 2017/11/3.
 */
@Service
public class LeaveService {



    public Leave application(Leave leave) {
        return null;
    }

    public Leave cancleApplication(Integer userId, Integer leaveId) {
        return null;
    }

    public List<Leave> myApplication(Integer userId) {
        return null;
    }

    public List<Leave> myApplicationHistory(Integer userId) {
        return null;
    }

    public List<Leave> getAllByStatus(Integer status) {
        return null;
    }

    public Leave getById(Integer leaveId) {
        return  null;
    }

    public Leave getByApplicationIdOrName(Integer userId, Integer userId1) {
        return null;
    }

    public List<Leave> needIDispose(Integer userId) {
        return null;
    }
}
