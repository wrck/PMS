package com.dp.plat.job;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dp.plat.data.bean.MailSenderInfo;
import com.dp.plat.util.StringEscUtil;
import com.dp.plat.util.test.SimpleMailSender;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class Mailer implements Job {
    private static final JobLogger logger = new JobLogger(Mailer.class);

    @SuppressWarnings("unchecked")
    public void work() throws IOException, SQLException {
        logger.logStart();
        Reader reader = null;
        int successCount = 0;
        int failCount = 0;
        try {
            reader = Resources.getResourceAsReader("sqlMapConfig.xml");
            SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
            List<MailSenderInfo> mailInfos = sqlMap.queryForList("query_waiting_mail");
            logger.logInfo("查询到 {} 封待发送邮件", mailInfos.size());
            
            StringBuilder sb = new StringBuilder();
            for (MailSenderInfo mailInfo : mailInfos) {
                try {
                    if ((StringUtils.isBlank(mailInfo.getTos()) || mailInfo.getTos().length() <= 2) 
                            && (StringUtils.isBlank(mailInfo.getCcs()) || mailInfo.getCcs().length() <= 2)
                            && StringUtils.isBlank(mailInfo.getBcc())) {
                        mailInfo.setSubject(mailInfo.getSubject() + "---邮件无收件人提醒");
                        mailInfo.setTos(StringEscUtil.getText("plat.develop.mail.tos"));
                        mailInfo.setCcs(null);
                        mailInfo.setBcc(null);
                    }
                    if (SimpleMailSender.sendMail(mailInfo)) {
                        sb.append(mailInfo.getId());
                        sb.append(",");
                        sqlMap.update("update_waiting_mail", String.valueOf(mailInfo.getId()));
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch(Exception e) {
                    failCount++;
                    logger.logError("发送邮件 ID=" + mailInfo.getId() + " 失败", e);
                }
            }

            if (sb.length() > 0) {
                sb.delete(sb.length() - 1, sb.length());
                sqlMap.update("update_waiting_mail", sb.toString());
            }
        } catch (Exception e) {
            logger.logError(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.logError("关闭Reader失败", e);
            }
        }
        logger.logInfo("发送完成，成功: {}, 失败: {}", successCount, failCount);
    }

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            this.work();
        } catch (Exception e) {
            logger.logError(e);
        }
    }

    public static void main(String[] args) {
        try {
            new Mailer().execute(null);
        } catch (JobExecutionException e) {
            logger.logError(e);
        }
    }
}
