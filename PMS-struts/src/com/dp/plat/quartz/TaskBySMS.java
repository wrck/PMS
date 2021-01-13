package com.dp.plat.quartz;

import java.io.IOException;
import java.sql.SQLException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dp.plat.job.GainPresalesInfoBySMS;
import com.dp.plat.job.GainPrjPropertyBySMS;
import com.dp.plat.job.GainPrjRealProjectLineBySMS;
import com.dp.plat.job.PlanGetBySMS;
/**
 * 将从SMS系统获取信息的定时任务放在一起执行，同步程序作普通bean处理
 * @author admin
 *
 */
public class TaskBySMS implements Job{
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			/**
			 * 获取测试类借货信息
			 */
			GainPresalesInfoBySMS.work();
			/**
			 * 同步项目相关信息和销售信息
			 */
			GainPrjPropertyBySMS.work();
			
			/**
			 * 同步项目真实设备清单
			 */
			GainPrjRealProjectLineBySMS.work();
			/**
			 * 抓取SMS收款计划数据
			 */
			PlanGetBySMS.work();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			new TaskBySMS().execute(null);
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
