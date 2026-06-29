package com.dp.plat.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dp.plat.job.ReportDataTask;

public class ReportTask implements Job{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		ReportDataTask.work();
	}

}
