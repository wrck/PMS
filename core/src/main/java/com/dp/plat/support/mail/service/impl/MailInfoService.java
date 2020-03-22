/**
 * 
 */
package com.dp.plat.support.mail.service.impl;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.support.mail.MailSenderInfo;
import com.dp.plat.support.mail.NotificationTemplate;
import com.dp.plat.support.mail.dao.MailInfoMapper;
import com.dp.plat.support.mail.entity.MailInfo;
import com.dp.plat.support.mail.service.IMailInfoService;
import com.dp.plat.support.mail.vo.PageParam;

/**
 * @author w02611
 *
 */
@Service("mailInfoService")
public class MailInfoService implements IMailInfoService {

	@Autowired
	protected MailInfoMapper dao;

	@Override
	public int deleteByPrimaryKey(Object pk) {
		return dao.deleteByPrimaryKey(pk);
	}

	@Override
	public int insert(MailInfo record) {
		try {
			Class<?> objClass = record.getClass();
			Method method = objClass.getMethod("setCreateBy", String.class);
			method.invoke(record, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		return dao.insert(record);
	}

	@Override
	public MailInfo selectByPrimaryKey(Object pk) {
		return dao.selectByPrimaryKey(pk);
	}

	@Override
	public int updateByPrimaryKey(MailInfo record) {
		try {
			Class<?> objClass = record.getClass();
			Method method = objClass.getMethod("setUpdateBy", String.class);
			method.invoke(record, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		return dao.updateByPrimaryKey(record);
	}

	@Override
	public int updateByPrimaryKeySelective(MailInfo record) {
		try {
			Class<?> objClass = record.getClass();
			Method method = objClass.getMethod("setUpdateBy", String.class);
			method.invoke(record, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		return dao.updateByPrimaryKeySelective(record);
	}

	/**
	 * 查询满足条件的记录条数记录
	 * 
	 * @param pageParam
	 * @return
	 */
	public long countBySelectivePageable(PageParam<MailInfo> pageParam) {
		return dao.countBySelectivePageable(pageParam);
	}

	public long countBySelective(MailInfo t) {
		return dao.countBySelective(t);
	}

	/**
	 * 分页查询满足条件的记录
	 * 
	 * @param pageParam
	 * @return
	 */
	public List<MailInfo> selectBySelectivePageable(PageParam<MailInfo> pageParam) {
		return dao.selectBySelectivePageable(pageParam);
	}

	/**
	 * 查询满足条件的所有记录
	 * 
	 * @param record
	 * @return
	 */
	public List<MailInfo> selectBySelective(MailInfo record) {
		return dao.selectBySelective(record);
	}
	
	@Override
	public int insertSelective(MailInfo record) {
		if (record.getExpectSendTime() == null) {
			record.setExpectSendTime(new Date());
		}
		Class<?> objClass = record.getClass();
		try {
			Method method = objClass.getMethod("setCreateBy", String.class);
			method.invoke(record, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		try {
			Method method = objClass.getMethod("setCreateTime", Date.class);
			method.invoke(record, new Date());
		} catch (Exception e) {
		}
		return dao.insertSelective(record);
	}

	@Override
	public NotificationTemplate queryNotificationTemplate(String templateCode) {
		return dao.queryNotificationTemplate(templateCode);
	}

	@Override
	public List<MailSenderInfo> queryUnSendMails() {
		return dao.queryUnSendMails();
	}
	
	@Override
	public List<MailSenderInfo> queryUnSendMails(Integer failedCount) {
		return dao.queryUnSendMails(failedCount);
	}

	@Override
	public void updateMailWhenSendSuccess(String mailIds) {
		dao.updateMailWhenSendSuccess(mailIds);
	}

	@Override
	public void updateMailInfoWhenSendSuccess(List<MailInfo> successMails) {
		dao.updateMailInfoWhenSendSuccess(successMails);
	}

	@Override
	public void updateOneMailInfoWhenSendSuccess(MailInfo successMail) {
		dao.updateOneMailInfoWhenSendSuccess(successMail);
	}

	@Override
	public void updateMailFailedCount(String failedMailIds) {
		dao.updateMailFailedCount(failedMailIds);
	}

	
}
