package com.dp.plat.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.dp.plat.dao.WorkflowDao;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class WorkFlowServiceTest {

    @Mock
    private RepositoryService repositoryService;
    
    @Mock
    private RuntimeService runtimeService;
    
    @Mock
    private TaskService taskService;
    
    @Mock
    private HistoryService historyService;
    
    @Mock
    private WorkflowDao workflowDao;
    
    private WorkFlowServiceImpl workFlowService;
    
    @Before
    public void setUp() {
        workFlowService = new WorkFlowServiceImpl();
        workFlowService.setRepositoryService(repositoryService);
        workFlowService.setRuntimeService(runtimeService);
        workFlowService.setTaskService(taskService);
        workFlowService.setHistoryService(historyService);
        workFlowService.setWorkflowDao(workflowDao);
    }

    @Test
    public void testListDeployments_Empty() {
        // 模拟返回空列表
        DeploymentQuery mockQuery = Mockito.mock(DeploymentQuery.class);
        Mockito.when(repositoryService.createDeploymentQuery()).thenReturn(mockQuery);
        Mockito.when(mockQuery.list()).thenReturn(new ArrayList<>());
        
        List<Deployment> deployments = workFlowService.listDeployments();
        
        assertNotNull(deployments);
        assertTrue(deployments.isEmpty());
    }

    @Test
    public void testListProcessDefinition_Empty() {
        // 模拟返回空列表
        ProcessDefinitionQuery mockQuery = Mockito.mock(ProcessDefinitionQuery.class);
        Mockito.when(repositoryService.createProcessDefinitionQuery()).thenReturn(mockQuery);
        Mockito.when(mockQuery.list()).thenReturn(new ArrayList<>());
        
        List<ProcessDefinition> definitions = workFlowService.listProcessDefinition();
        
        assertNotNull(definitions);
        assertTrue(definitions.isEmpty());
    }

    @Test
    public void testListDeployments_WithMockData() {
        // 模拟返回有数据的列表
        List<Deployment> mockDeployments = new ArrayList<>();
        Deployment mockDeployment = Mockito.mock(Deployment.class);
        mockDeployments.add(mockDeployment);
        
        DeploymentQuery mockQuery = Mockito.mock(DeploymentQuery.class);
        Mockito.when(repositoryService.createDeploymentQuery()).thenReturn(mockQuery);
        Mockito.when(mockQuery.list()).thenReturn(mockDeployments);
        
        List<Deployment> deployments = workFlowService.listDeployments();
        
        assertNotNull(deployments);
        assertEquals(1, deployments.size());
    }

    @Test
    public void testListProcessDefinition_WithMockData() {
        // 模拟返回有数据的列表
        List<ProcessDefinition> mockDefinitions = new ArrayList<>();
        ProcessDefinition mockDefinition = Mockito.mock(ProcessDefinition.class);
        mockDefinitions.add(mockDefinition);
        
        ProcessDefinitionQuery mockQuery = Mockito.mock(ProcessDefinitionQuery.class);
        Mockito.when(repositoryService.createProcessDefinitionQuery()).thenReturn(mockQuery);
        Mockito.when(mockQuery.list()).thenReturn(mockDefinitions);
        
        List<ProcessDefinition> definitions = workFlowService.listProcessDefinition();
        
        assertNotNull(definitions);
        assertEquals(1, definitions.size());
    }

    @Test
    public void testDelDeployment() {
        String deploymentId = "test-deployment-id";
        
        // 验证删除操作被调用
        workFlowService.delDeployment(deploymentId);
        
        Mockito.verify(repositoryService).deleteDeployment(deploymentId, true);
    }

    @Test
    public void testFindPersonalTask_Empty() {
        String userId = "test-user";
        
        // 模拟返回空任务列表
        TaskQuery mockQuery = Mockito.mock(TaskQuery.class);
        Mockito.when(taskService.createTaskQuery()).thenReturn(mockQuery);
        Mockito.when(mockQuery.taskAssignee(userId)).thenReturn(mockQuery);
        Mockito.when(mockQuery.list()).thenReturn(new ArrayList<Task>());
        
        List<Task> tasks = workFlowService.findPersonalTask(userId);
        
        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
    }
}
