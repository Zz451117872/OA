package com.example.OA.service.manager;

import com.example.OA.dao.PartMapper;
import com.example.OA.dao.UserMapper;
import com.example.OA.model.Part;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.Error;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by aa on 2017/10/31.
 */
@Service
public class PartService {

    @Autowired
    PartMapper partMapper;

    @Autowired
    UserMapper userMapper;

    public String addPart(Part part) {
        if(part != null) {
            String partName = part.getPartName();
            if (StringUtils.isNotBlank(partName) && partMapper.getByPartname(partName) == null) {
                part.setCreateTime(new Date());
                partMapper.insert(part);
                return partName;
            }
            throw new AppException(Error.TARGET_EXISTSED);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public String updatePart(Part part) {
        if(part != null) {
            if (partMapper.selectByPrimaryKey(part.getId()) != null) {
                part.setUpdateTime(new Date());
                partMapper.updateByPrimaryKeySelective(part);
                return part.getPartName();
            }
            throw new AppException(Error.TARGET_NO_EXISTS);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }

    public String deleteById(Integer partId) {
        //删掉部门，要级联删除该部门的所有员工？
        if(partId != null) {
            if (partMapper.selectByPrimaryKey(partId) != null) {

                int result = partMapper.deleteByPrimaryKey(partId);
                if(result >= 1)
                {
                    userMapper.deleteByPartId(partId);
                    return "success";
                }
                throw new AppException(Error.UNKNOW_EXCEPTION);
            }
            throw new AppException(Error.TARGET_NO_EXISTS);
        }
        throw new AppException(Error.PARAMS_ERROR);
    }
}
