package com.dp.plat.subcontract.quartz;

import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.SessionScope;

import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.dao.LoginDao;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.bean.WorkflowCommonParam;
import com.dp.plat.subcontract.constant.SubcontractConstant.TaskKey;
import com.dp.plat.subcontract.dao.SubcontractDao;
import com.dp.plat.subcontract.entity.SubcontractPayment;
import com.dp.plat.subcontract.entity.SubcontractProject;
import com.dp.plat.subcontract.service.SubcontractService;
import com.dp.plat.util.UserUtil;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * SSE付款信息自动更新同步
 * 
 * @author admin
 */
public class SubcontractPaymentAutoUpdate implements Job {

    private ApplicationContext applicationContext = null;
    
    private Map<String, User> users = new HashMap<>();

    @SuppressWarnings("unchecked")
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            Reader readerSSE = Resources.getResourceAsReader("sqlMapConfigSSE.xml");
            SqlMapClient sqlMapSSE = SqlMapClientBuilder.buildSqlMapClient(readerSSE);
            List<Map<String, Object>> list = sqlMapSSE.queryForList("querySSESubcontractPaymentList");
            readerSSE.close();
            
            Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
            SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
            
            sqlMap.delete("deleteSSESubcontractPayment");
            sqlMap.insert("insertSSESubcontractPayment", list);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (SpringContext.getApplicationContext() != null) {
            applicationContext = SpringContext.getApplicationContext();
        } else {
            applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        }
        // 向上下文注册session，service层需要UserContext，scope=session，
        ((AbstractRefreshableApplicationContext) applicationContext).getBeanFactory().registerScope("session", new SessionScope());
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
       
        UserContext userContext = UserContext.getUserContext();
        
        SubcontractService subcontractService = applicationContext.getBean("subcontractService", SubcontractService.class);
        SubcontractDao subcontractDao = applicationContext.getBean("subcontractDao", SubcontractDao.class);
        
        List<SubcontractPayment> paymentList = subcontractService.querySSESubcontractPaymentList();
        Set<Integer> subcontractIds = new HashSet<>();
        for (Iterator<SubcontractPayment> iterator = paymentList.iterator(); iterator.hasNext();) {
            try {
                SubcontractPayment subcontractPayment = iterator.next();
                Integer subcontractId = subcontractPayment.getSubcontractId();
                SubcontractProject subcontract = new SubcontractProject();
                subcontract.setId(subcontractId);
                
                userContext = login(subcontractPayment.getCreateBy());
    
                HashMap<String, Object> params = new HashMap<>();
                params.put("checkOffice", "true");
                WorkflowCommonParam workflowCommonParam = subcontractService.queryCurrentWorkFlowCommonParam(subcontractId, TaskKey.APPLY_PAYMENT, "smRole", params);
                if (workflowCommonParam != null) {
                    subcontractService.applyPaymentFlow(workflowCommonParam, subcontract);
                } else {
                    //iterator.remove();
                }
    
                userContext.logout();
                if (!subcontractIds.contains(subcontractId)) {
                    subcontractIds.add(subcontractId);
                }
            } catch(Exception e) {
                iterator.remove();
            }
        }
        if (!paymentList.isEmpty()) {
            subcontractService.saveSubcontractPayment(paymentList);
        }
        if (!subcontractIds.isEmpty()) {
            subcontractDao.deleteEmptySubcontractPayment(StringUtils.join(subcontractIds, ","));
        }
        // 更新同步SSE付款信息后未付款的付款时间和备注
        subcontractDao.updateSSESubcontractPaymentTime();
    }
    
    private UserContext login(String username) {
        UserContext userContext = UserContext.getUserContext();
        User user = null;
        if (users.containsKey(username)) {
            user = users.get(username);
        } else {
            LoginDao loginDao = applicationContext.getBean("loginDao", LoginDao.class);
            user = loginDao.querUser(username);
            if (user != null) {
                String areaPower = StringUtils.trimToEmpty(user.getAreapower());
                if ("-1".equals(areaPower)) {
                    user.setAreapower(user.getDpNo());
                } else if (!areaPower.contains(user.getDpNo())) {
                    user.setAreapower(areaPower + "," + user.getDpNo());
                }
                areaPower = UserUtil.processAreaPower(areaPower);
                user.setAreapower(areaPower);
                users.put(username, user);
            }
        }
        
        userContext.logout();
        if (user != null) {
            userContext.login(user, "", null, null, null);
        }
        return userContext;
    }
    
    public static void main(String[] args) throws JobExecutionException {
        new SubcontractPaymentAutoUpdate().execute(null);
    }
}
