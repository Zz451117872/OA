package com.example.OA.service.activiti;

import com.example.OA.model.Dispose;
import com.example.OA.model.User;
import com.example.OA.mvc.common.VO.DisposeVO;
import com.example.OA.mvc.common.VO.LeaveVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by aa on 2017/11/3.
 */
@Service
public class DisposeService {
    public Dispose auditApplication(Integer userId, Integer leaveId) {
        return null;
    }

    public List<LeaveVO> getByAuditer(Integer userId) {
        return null;
    }

    public List<User> getAuditerByLeave(Integer leaveId) {
        return null;
    }

    public List<Dispose> getAll() {
        return null;
    }
}
