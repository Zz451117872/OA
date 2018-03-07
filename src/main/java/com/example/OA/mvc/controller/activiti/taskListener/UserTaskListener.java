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


/*
用户任务节点的 监听器，部署在工作流中，在用户任务创建时被触发，用于对用户任务的 代理人，候选人，候选组进行初始化
 */
@Component
public class UserTaskListener implements TaskListener {


	private static final Logger logger = Logger.getLogger(UserTaskListener.class);
    @Autowired
    protected RepositoryService repositoryService;

	@Autowired
	private UserTaskMapper userTaskMapper;
    
	@Override
	public void notify(DelegateTask delegateTask) {
		//获取流程定义
		String processDefinitionId = delegateTask.getProcessDefinitionId();
		ProcessDefinition processDefinition = this.repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
		//获取流程定义key
		String processDefinitionKey = processDefinition.getKey();
		//获取任务定义key
		String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
		try {		//查询 流程定义key为processDefinitionKey的所有任务，并对任务定义key为taskDefinitionKey
					//的任务 进行初始化
			List<UserTask> taskList = userTaskMapper.getByPdfKey(processDefinitionKey);
			for(UserTask userTask : taskList){
				String taskKey = userTask.getTaskdefkey();
				String taskType = userTask.getTasktype();
				String ids = userTask.getCandidateIds();

				if(taskDefinitionKey.equals(taskKey)){
					switch (taskType){  //任务类型为 代理 人
						case "assignee" : {
							delegateTask.setAssignee(ids);
							logger.info("assignee id: "+ids);
							break;
						}
						case "candidateUser" : {	//任务类型为 候选人
							String[] userIds = ids.split(",");
							List<String> users = new ArrayList<String>();
							for(int i=0; i<userIds.length;i++){
								users.add(userIds[i]);
							}
							delegateTask.addCandidateUsers(users);
							logger.info("候选人审批 ids: "+ids);
							break;
						}
						case "candidateGroup" : {	//任务类型为 候选组
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
