package com.dp.plat.core.controller.admin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.JobExecutionContextImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.SyncLog;
import com.dp.plat.core.service.ISyncLogService;
import com.dp.plat.core.vo.PageParam;

/**
 * 同步日志管理Controller
 * 
 * @author w02611
 *
 */

@Controller()
@RequestMapping(Consts.URLPath.SYSTEM_MANAGER + "synclog")
public class SyncLogController {
	@Resource
	private ISyncLogService syncLogService;

	@RequestMapping
	public void listView() {
	}

	@RequestMapping("/list")
	public String findAll(PageParam<Object> pageParam, SyncLog data, Model model) {
		pageParam.setModel(data);
		pageParam.setTotal(syncLogService.countBySelectivePageable(null));
		pageParam.setFiltered(syncLogService.countBySelectivePageable(pageParam));
		List<Object> dataList = new ArrayList<>();
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		dataList = syncLogService.selectBySelectivePageable(pageParam);
		model.addAttribute("data", dataList);
		return Consts.URLPath.SYSTEM_MANAGER + "synclog";
	}

	@RequestMapping("{id}")
	public String getOne(@PathVariable("id") Integer id, Model model) {
		model.addAttribute("id", id);
		return Consts.URLPath.SYSTEM_MANAGER + "synclog_detail";
	}
	
	@RequestMapping(value = "{id}", method = RequestMethod.POST)
	public String findOne(@PathVariable("id") Integer id, Model model) {
		SyncLog sysLog = syncLogService.selectByPrimaryKey(id);
		model.addAttribute("syncLog", sysLog);
		return Consts.URLPath.SYSTEM_MANAGER + "synclog_detail";
	}
	
	@RequestMapping("/syncData")
    public void syncData(String type, Model model) throws InvocationTargetException, IllegalAccessException {
	    List<Exception> errors = new ArrayList<Exception>();
	    boolean status = false;
	    try {
            JobDetailImpl job = SpringContext.getBean(type, JobDetailImpl.class);
            MethodInvokingJobDetailFactoryBean invoker = (MethodInvokingJobDetailFactoryBean) job.getJobDataMap().get("methodInvoker");
            invoker.invoke();
            model.addAttribute("status", true);
	    } catch (Exception e) {
	        errors.add(e);
	        try {
    	        JobDetailImpl jobDetail = null;
                try {
                    CronTriggerFactoryBean trigger = SpringContext.getBean(type, CronTriggerFactoryBean.class);
                    jobDetail = (JobDetailImpl) trigger.getJobDataMap().get("jobDetail");
                } catch (Exception e1) {
                    errors.add(e1);
                    try {
                        jobDetail = (JobDetailImpl) SpringContext.getBean(type, JobDetailFactoryBean.class).getJobDataMap().get("jobDetail");;
                    } catch (Exception e2) {
                        errors.add(e2);
                        try {
                            Class<? extends Job> jobClass = (Class<? extends Job>) getClass().getClassLoader().loadClass(type);
                            jobDetail = new JobDetailImpl();
                            jobDetail.setName(jobClass.getSimpleName());
                            jobDetail.setGroup("MANUAL_TRIGGER");
                            jobDetail.setJobClass(jobClass);
                        } catch (Exception e3) {
                            errors.add(e3);
                        }
                    }
                }
                if (jobDetail != null) {
                    jobDetail.setGroup("MANUAL_TRIGGER");
                    String[] beanNamesForType = SpringContext.getApplicationContext().getBeanNamesForType(SchedulerFactoryBean.class);
                    SchedulerFactoryBean schedulerFactoryBean = null;
                    if (beanNamesForType.length != 0) {
                        schedulerFactoryBean = SpringContext.getBean(beanNamesForType[0], SchedulerFactoryBean.class);
                    } else {
                        schedulerFactoryBean = SpringContext.getBean(SchedulerFactoryBean.class);
                    }
                    Scheduler scheduler = (Scheduler) schedulerFactoryBean.getObject();
    //                // 异步执行，调用调度器
    //                scheduler.addJob(jobDetail, true);
    //                scheduler.triggerJob(jobDetail.getName(), jobDetail.getGroup(), jobDetail.getJobDataMap());
                    // 同步执行
                    SimpleTriggerImpl trigger = new SimpleTriggerImpl(jobDetail.getName() + "Trigger", jobDetail.getGroup());
    //                trigger.setJobName(jobDetail.getName());
    //                trigger.setJobGroup(jobDetail.getGroup());
                    Job job = (Job) jobDetail.getJobClass().newInstance();
                    JobExecutionContext jobContext = new JobExecutionContextImpl(scheduler, new TriggerFiredBundle(jobDetail, trigger, null, false, null, null, null, null), job);
                    job.execute(jobContext);
                    model.addAttribute("status", true);
                    return;
                }
	        } catch (Exception e4) {
	            errors.add(e4);
            }
	        model.addAttribute("status", false);
	        List<Object> message = new ArrayList<Object>(errors.size());
	        for (Exception error : errors) {
	            message.add(ExceptionUtils.getRootCauseMessage(error));
	        }
	        model.addAttribute("message", StringUtils.join(message, "\r\n"));
        }
    }
}
