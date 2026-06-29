package com.dp.plat.project;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.activiti.engine.delegate.VariableScope;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;

import com.dp.plat.data.bean.Project;
import com.dp.plat.maintenance.aop.ProjectStateUpdateAspect;
import com.dp.plat.subcontract.listener.SubcontractInspectionListener;
import com.dp.plat.subcontract.service.SubcontractService;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ProjectTest {
    
    @Mock
    private ApplicationContext applicationContext;
    
    @Mock
    private SubcontractService projectService;
    
    @Mock
    private SubcontractInspectionListener context;
    
    @Mock
    private VariableScope variableScope;
    

    @Test
    public void assigneExpr() throws Exception {
        Date mockDate = new Date();
        ProjectStateUpdateAspect aspect = new ProjectStateUpdateAspect();
        Project executeProject = new Project();
        executeProject.setProjectId(1);
        executeProject.setExecutionState("45");
        
        Project closeProject = new Project();
        closeProject.setProjectId(1);
        closeProject.setCloseProcessState("3");
        
        HashMap<String, Object> taskDefinedVariables = new HashMap<String, Object>();        
        String script = "use org.apache.commons.lang3.ObjectUtils;\r\n"
                + "use com.dp.plat.data.bean.Project;\r\n"
                + "use java.util.Date;\r\n" + 
                "\r\n" + 
                "        let project = ObjectUtils.defaultIfNull(entity.project, new Project());\r\n" + 
                "        let executionState = ObjectUtils.defaultIfNull(project.executionState, entity.executionState);\r\n" + 
                "        p(executionState);\r\n" + 
                "        if (executionState != nil && executionState != '') {\r\n"
                + "p('executionState不为空');" + 
                "          setCustomInfoByKey(project, 'executionStateTime', new Date());\r\n"
                + "         return true;" + 
                "        }" + 
                "\r\np('executionState为空');"
                + "return false;";
        ;
        System.out.println(script);
        taskDefinedVariables.put("scripts", Collections.singletonMap("executionState", Collections.singletonMap("script", script)));
        
        Mockito.mockConstructionWithAnswer(Date.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return mockDate.getTime();
            }
        });
        
        Object enable = aspect.execScripts(executeProject, taskDefinedVariables);
        assertEquals(mockDate, executeProject.getCustomInfoByKey("executionStateTime"));
        
        enable = aspect.execScripts(closeProject, taskDefinedVariables);
        assertEquals(null, closeProject.getCustomInfoByKey("executionStateTime"));
        
    }
    
}
