package com.example.OA.mvc.controller.activiti.taskListener;

import java.util.ArrayList;
import java.util.List;

import com.example.OA.dao.activiti.UserTaskMapper;
import com.example.OA.model.activiti.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class UserTaskListener implements TaskListener {


	private static final Logger logger = Logger.getLogger(UserTaskListener.class);
    @Autowired
    protected RepositoryService repositoryService;

	@Autowired
	private UserTaskMapper userTaskMapper;
    
	@Override
	public void notify(DelegateTask delegateTask) {
		String processDefinitionId = delegateTask.getProcessDefinitionId();
		ProcessDefinition processDefinition = this.repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
		String processDefinitionKey = processDefinition.getKey();
		String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
		try {
			List<UserTask> taskList = userTaskMapper.getByPdfKey(processDefinitionKey);
			for(UserTask userTask : taskList){
				String taskKey = userTask.getTaskdefkey();
				String taskType = userTask.getTasktype();
				String ids = userTask.getCandidateIds();

				if(taskDefinitionKey.equals(taskKey)){
					switch (taskType){
						case "assignee" : {
							delegateTask.setAssignee(ids);
							logger.info("assignee id: "+ids);
							break;
						}
						case "candidateUser" : {
							String[] userIds = ids.split(",");
							List<String> users = new ArrayList<String>();
							for(int i=0; i<userIds.length;i++){
								users.add(userIds[i]);
							}
							delegateTask.addCandidateUsers(users);
							logger.info("候选人审批 ids: "+ids);
							break;
						}
						case "candidateGroup" : {
							String[] groupIds = ids.split(",");
							List<String> groups = new ArrayList<String>();
							for(int i=0; i<groupIds.length;i++){
								groups.add(groupIds[i]);
							}
							delegateTask.addCandidateGroups(groups);
							logger.info("候选组审批 ids: "+ids);
							break;
						}
					}
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
