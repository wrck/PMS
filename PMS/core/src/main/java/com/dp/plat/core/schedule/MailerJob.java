package com.dp.plat.core.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.support.mail.MailSenderInfo;
import com.dp.plat.support.mail.MailUtil;
import com.dp.plat.support.mail.entity.MailInfo;
import com.dp.plat.support.mail.service.IMailInfoService;

public class MailerJob {
	@Resource
	private IMailInfoService mailInfoService;

	protected void execute() {
		StringBuilder sendSuccessIds = new StringBuilder();
		List<Integer> sendFailedIds = new ArrayList<>();
		Integer failedCount = Integer.valueOf(SystemConfig.systemVariables.getOrDefault("sys.mail.sendFailed.maxCount", "3"));
		List<MailSenderInfo> senderInfos = mailInfoService.queryUnSendMails(failedCount);
		List<MailInfo> successMails = new ArrayList<MailInfo>();
		for (MailSenderInfo senderInfo : senderInfos) {
			try {
				boolean success = MailUtil.sendMailWithAttachments(senderInfo);
				if (success) {
					sendSuccessIds.append(senderInfo.getId()).append(",");
					successMails.add(senderInfo);
				} else {
					sendFailedIds.add(senderInfo.getId());
				}
			} catch (Exception e) {
				ExceptionHandler.insertException(e);
			}
		}
		// 进行合并
		if (!senderInfos.isEmpty()) {
			mailInfoService.updateMailInfoWhenSend(Arrays.asList(senderInfos.toArray(new MailInfo[]{})));
		}
//		if (!successMails.isEmpty()) {
////			sendSuccessIds.deleteCharAt(sendSuccessIds.length() - 1);
////			mailInfoService.updateMailWhenSendSuccess(sendSuccessIds.toString());
//			mailInfoService.updateMailInfoWhenSendSuccess(successMails);
//		}
//		if (!sendFailedIds.isEmpty()) {
//			mailInfoService.updateMailFailedCount(StringUtils.join(sendFailedIds, ","));
//		}
	}
}
