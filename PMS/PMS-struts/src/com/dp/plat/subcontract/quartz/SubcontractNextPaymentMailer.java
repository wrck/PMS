package com.dp.plat.subcontract.quartz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dp.plat.context.SpringContext;
import com.dp.plat.dao.UserManageDao;
import com.dp.plat.data.bean.User;
import com.dp.plat.subcontract.constant.SubcontractConstant.SubcontractTemplate;
import com.dp.plat.subcontract.dao.SubcontractDao;
import com.dp.plat.subcontract.exception.SubcontractException;
import com.dp.plat.subcontract.vo.SubcontractProjectVO;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.NotificationTemplateUtil;

/**
 * 邮件提醒项目转包下次付款申请，距离上次10个月开始，每隔1个月提醒一次
 * 
 * @author admin
 *
 */
public class SubcontractNextPaymentMailer implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		ApplicationContext applicationContext = null;
		if (SpringContext.getApplicationContext() != null) {
			applicationContext = SpringContext.getApplicationContext();
		} else {
			applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		}
		SubcontractDao subcontractDao = applicationContext.getBean("subcontractDao", SubcontractDao.class);
		List<SubcontractProjectVO> subcontractProjectList = subcontractDao.queryNextPaymentTask();
		for (SubcontractProjectVO subcontractProject : subcontractProjectList) {
			String[] nextAssignPer = new String[3];
			String tos = null;
			try {
				nextAssignPer = getNextAssignPer(applicationContext, MessageUtil.ROLE_SERVICEMANAGER,
						subcontractProject.getOfficeCode());
				tos = nextAssignPer[2];
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (StringUtils.isBlank(tos)) {
				continue;
			}
			
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("templateCode", SubcontractTemplate.PAYMENT_NEXT_NOTIFY_CODE);
			context.put("tos", tos);
			context.put("username", nextAssignPer[1]);
			context.put("subcontractName", subcontractProject.getSubcontractName());
			context.put("lastPaymentTime", dateFormat.format(subcontractProject.getLastPaymentTime()));
			context.put("monthDiff", subcontractProject.getMonthDiff());
			NotificationTemplateUtil.keepMailByDao(context);
		}
	}

	private String[] getNextAssignPer(ApplicationContext applicationContext, int roleId, String dpNo) {
		// 获取有效的回访人员或工程人员
		Map<String, String> params = new HashMap<>();
		params.put("roleid", String.valueOf(roleId));
		params.put("dpNo", dpNo);
		UserManageDao userManageDao = applicationContext.getBean("userManageDao", UserManageDao.class);
		List<User> userList = userManageDao.queryUserWithRoleIdAndDpNo(params);

		if (userList.isEmpty()) {
			params.remove("dpNo");
			params.put("areaPower", dpNo);
			userList = userManageDao.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
		}
		List<String> nextAssignPer = new ArrayList<>(userList.size());
		List<String> nextAssignName = new ArrayList<>(userList.size());
		List<String> nextAssignEmail = new ArrayList<>(userList.size());
		for (User userObj : userList) {
			nextAssignPer.add(userObj.getUsername());
			nextAssignName.add(userObj.getUsername() + "-" + userObj.getRealName());
			nextAssignEmail.add(userObj.getEmail());
		}

		if (nextAssignPer.size() <= 0) {
			throw new SubcontractException("获取下一级审核人员出错");
		}
		String[] nextAssigen = new String[] { StringUtils.join(nextAssignPer, ","),
				StringUtils.join(nextAssignName, ","), StringUtils.join(nextAssignEmail, ";") };
		return nextAssigen;
	}

	public static void main(String[] args) throws JobExecutionException {
		new SubcontractNextPaymentMailer().execute(null);
	}
}
