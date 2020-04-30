package com.dp.plat.activiti.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.ProcessInstance;

import com.dp.plat.activiti.entity.BaseVO;
import com.dp.plat.activiti.entity.CommentVO;
import com.dp.plat.activiti.entity.ProcessInstanceEntity;
import com.dp.plat.activiti.entity.Vacation;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;

/**
 * 
 * @author w02611
 *
 */
public interface IProcessService {

	/**
	 * 查询代办任务
	 * 
	 * @param user
	 * @param model
	 * @return
	 */
	public List<BaseVO> findTodoTask(User user, PageParam<BaseVO> page) throws Exception;

	/**
	 * 签收任务
	 * 
	 * @param user
	 * @param taskId
	 */
	public void claim(User user, String taskId) throws Exception;

	/**
	 * 取消签收任务
	 * @param taskId
	 */
	public void unclaim(String taskId);
	
	/**
	 * 委派任务
	 * 
	 * @param userId
	 * @throws Exception
	 */
	public void delegateTask(String userId, String taskId) throws Exception;

	/**
	 * 转办任务
	 * 
	 * @param userId
	 * @param taskId
	 * @throws Exception
	 */
	public void transferTask(String userId, String taskId) throws Exception;

	/**
	 * 完成任务
	 * 
	 * @param taskId
	 * @param content
	 * @param userid
	 * @param completeFlag
	 */
	public void complete(String taskId, String content, String userid, Map<String, Object> variables) throws Exception;

	/**
	 * 撤销任务
	 * 
	 * @param historyTaskId
	 * @throws Exception
	 */
	public Integer revoke(String historyTaskId, String processInstanceId) throws Exception;

	/**
	 * @param instanceId
	 * @param userId
	 * @return
	 */
	public Object withdrawTask(String instanceId, String userId);
	
	/**
	 * 获取评论
	 * 
	 * @param processInstanceId
	 * @return
	 * @throws Exception
	 */
	public List<CommentVO> getComments(String processInstanceId) throws Exception;

	/**
	 * 跳转（包括回退和向前）至指定活动节点
	 * 
	 * @param currentTaskId
	 *            当前任务节点Id
	 * @param targetTaskDefinitionKey
	 *            目标任务节点（在模型定义里面的节点名称）
	 * @throws Exception
	 */
	public void moveTo(String currentTaskId, String targetTaskDefinitionKey) throws Exception;

	/**
	 * 跳转（包括回退和向前）至指定活动节点
	 * 
	 * @param currentTaskEntity
	 *            当前任务节点
	 * @param targetTaskDefinitionKey
	 *            目标任务节点（在模型定义里面的节点名称）
	 * @throws Exception
	 */
	public void moveTo(TaskEntity currentTaskEntity, String targetTaskDefinitionKey) throws Exception;

	/**
	 * 显示流程图,带流程跟踪
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public InputStream getDiagram(String processInstanceId) throws Exception;

	/**
	 * 显示图片-通过流程ID，不带流程跟踪(没有乱码问题)
	 * 
	 * @param resourceType
	 * @param processInstanceId
	 * @return
	 */
	public InputStream getDiagramByProInstanceId_noTrace(String resourceType, String processInstanceId)
			throws Exception;

	/**
	 * 显示图片-通过部署ID，不带流程跟踪(没有乱码啊问题)
	 * 
	 * @param resourceType
	 * @param processInstanceId
	 * @return
	 * @throws Exception
	 */
	public InputStream getDiagramByProDefinitionId_noTrace(String resourceType, String processDefinitionId)
			throws Exception;

	/**
	 * 读取已结束中的流程-admin查看
	 *
	 * @return
	 */
	public List<BaseVO> findFinishedProcessInstances(PageParam<BaseVO> page) throws Exception;

	/**
	 * 各个审批人员查看自己完成的任务
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public List<BaseVO> findFinishedTaskInstances(User user, PageParam<BaseVO> page) throws Exception;

	/**
	 * 查看正在运行的请假流程
	 * 
	 * @param vacation
	 * @return
	 * @throws Exception
	 */
	List<BaseVO> listRuningVacation(Vacation vacation, PageParam<Object> page) throws Exception;

	/**
	 * 管理运行中流程
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<ProcessInstance> listRuningProcess(PageParam<ProcessInstanceEntity> page) throws Exception;

	/**
	 * 激活流程实例
	 * 
	 * @param processInstanceId
	 * @throws Exception
	 */
	public void activateProcessInstance(String processInstanceId) throws Exception;

	/**
	 * 挂起流程实例
	 * 
	 * @param processInstanceId
	 * @throws Exception
	 */
	public void suspendProcessInstance(String processInstanceId) throws Exception;

	/**
	 * 测试 - 动态创建流程信息
	 * 
	 * @throws Exception
	 */
	public void addProcessByDynamic() throws Exception;

	/**
	 * 判断流程能否撤回
	 * @param processInstanceId
	 * @param userId
	 * @return
	 */
	Result canWithdraw(String processInstanceId, String userId);

	/**
	 * 删除流程
	 * @param processInstanceId
	 * @param deleteReason
	 */
	public void deleteProcess(String processInstanceId, String deleteReason);

}
