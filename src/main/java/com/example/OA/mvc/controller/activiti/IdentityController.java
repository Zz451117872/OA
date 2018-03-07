package com.example.OA.mvc.controller.activiti;

import java.util.List;
import com.example.OA.model.activiti.ActGroup;
import com.example.OA.model.activiti.ActUser;
import com.example.OA.mvc.common.ServerResponse;
import com.google.common.collect.Lists;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 用户、组控制器，未测试
 */
@RestController
@RequestMapping("/identity")
public class IdentityController {

    @Autowired
    IdentityService identityService;

    /**
     * 组列表
     */
    @RequestMapping("group_list")
    public List<ActGroup> groupList() {
       try{
           List<Group> groupList = identityService.createGroupQuery().list();
           List<ActGroup> result = Lists.newArrayList();
           if(groupList != null && !groupList.isEmpty())
           {
               for (Group group : groupList)
               {
                   result.add(convertGroup(group));
               }
               return result;
           }
           return result;
       }catch (Exception e)
       {
           throw e;
       }
    }



    /**
     * 保存Group
     */
    @RequestMapping(value = "save_group", method = RequestMethod.POST)
    public ActGroup saveGroup(String groupId, String groupName, String type) {
       try{
           Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
           if (group == null) {
               group = identityService.newGroup(groupId);
           }
           group.setName(groupName);
           group.setType(type);
           identityService.saveGroup(group);
           return convertGroup(group);
       }catch (Exception e)
       {
           throw e;
       }
    }

    /**
     * 删除Group
     */
    @RequestMapping(value = "delete_group", method = RequestMethod.GET)
    public ServerResponse deleteGroup(String groupId) {

        try{
            identityService.deleteGroup(groupId);
            return ServerResponse.createBySuccess();
        }catch (Exception e)
        {
            throw e;
        }
    }

    /**
     * 用户列表
     */
    @RequestMapping("user_list.do")
    public List<ActUser> userList() {

        List<User> userList = identityService.createUserQuery().list();
        List<ActUser> result = Lists.newArrayList();
        if(userList != null && !userList.isEmpty())
        {
//            Map<String, List<Group>> groupOfUserMap = new HashMap<String, List<Group>>();
            for (User user : userList) {
 //               List<Group> groupList = identityService.createGroupQuery().groupMember(user.getId()).list();
//                groupOfUserMap.put(user.getId(), groupList);
                result.add(converUser(user));
            }
            return result;
        }
        return result;
    }



    /**
     * 保存User
     */
    @RequestMapping(value = "save_user", method = RequestMethod.POST)
    public ActUser saveUser(@RequestParam("userId") String userId,
                           @RequestParam("firstName") String firstName,
                           @RequestParam("lastName") String lastName,
                           @RequestParam(value = "password", required = false) String password,
                           @RequestParam(value = "email", required = false) String email) {
        User user = identityService.createUserQuery().userId(userId).singleResult();
        if (user == null) {
            user = identityService.newUser(userId);
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        if (StringUtils.isNotBlank(password)) {
            user.setPassword(password);
        }
        identityService.saveUser(user);
       return converUser(user);
    }

    /**
     * 删除User
     */
    @RequestMapping(value = "delete_user", method = RequestMethod.GET)
    public ServerResponse deleteUser(@PathVariable("userId") String userId) {

        try{
            identityService.deleteUser(userId);
            return ServerResponse.createBySuccess();
        }catch (Exception e)
        {
            throw e;
        }
    }

    /**
     * 为用户设置所属组
     */
    @RequestMapping(value = "set_group", method = RequestMethod.POST)
    public ServerResponse groupForUser(@RequestParam("userId") String userId
            , @RequestParam("groupIds") String[] groupIds) {

       try{
           List<Group> groupInDb = identityService.createGroupQuery().groupMember(userId).list();
           for (Group group : groupInDb) {
               identityService.deleteMembership(userId, group.getId());
           }
           for (String group : groupIds) {
               identityService.createMembership(userId, group);
           }
           return ServerResponse.createBySuccess();
       }catch (Exception e){
           throw  e;
       }
    }


    private ActUser converUser(User user) {
        ActUser actUser = new ActUser();
        actUser.setId(user.getId());
        actUser.setEmail(user.getEmail());
        actUser.setFirst(user.getFirstName());
        actUser.setPwd(user.getPassword());
        actUser.setLast(user.getLastName());
        return actUser;
    }

    private ActGroup convertGroup(Group group) {
        ActGroup actGroup = new ActGroup();
        actGroup.setId(group.getId());
        actGroup.setName(group.getName());
        actGroup.setType(group.getType());
        return actGroup;
    }
}
