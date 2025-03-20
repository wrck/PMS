package com.dp.plat.action;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.JobExecutionContextImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.dp.plat.context.SpringContext;
import com.dp.plat.data.bean.OperateLog;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.service.OpLogService;
import com.dp.plat.util.UploadFileUtil;

public class OperateLogAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	private DisplayParam displayParam;
	private List<OperateLog> list = new ArrayList<OperateLog>();
	private OpLogService logger;
	private ArrayList<String> selected = new ArrayList<String>();

	@Override
	public String execute() throws Exception {
		displayParam.getParam();
		list = logger.queryLogList(displayParam);
		return SUCCESS;
	}
	
	
	private XSSFWorkbook workbook = null;
	private XSSFSheet worksheet=null;
	private String templatefile;

    private HashMap resultMap;

    private String taskName;
	
	public String exportlog(){
		String projectpath = this.getClass().getClassLoader().getResource("").getPath();
		projectpath = projectpath.replace("%20", " ");
		templatefile = projectpath.replace("WEB-INF/classes/", "template/日志.xlsx");
		try {
			workbook = new XSSFWorkbook(new FileInputStream(templatefile));
			worksheet=workbook.getSheet("sheet1");
			
			List<com.dp.plat.data.OperateLog> loglist = new ArrayList<com.dp.plat.data.OperateLog>();
			long ta = System.currentTimeMillis();
			loglist = logger.queryLogAllList(displayParam);
			long tb = System.currentTimeMillis();
			System.out.println("数据库查询时间为:"+(tb-ta));
			long sum = 0;
			for (int i = 0; i < loglist.size(); i++) {
				long t1 = System.currentTimeMillis();
				XSSFRow row = worksheet.createRow(i+1);
				XSSFCell cell = null;
				cell = row.createCell(0);
				cell.setCellValue(loglist.get(i).getUsername());
				cell = row.createCell(1);
				cell.setCellValue(loglist.get(i).getRealName());
				cell = row.createCell(2);
				cell.setCellValue(loglist.get(i).getIp());
				cell = row.createCell(3);
				cell.setCellValue(loglist.get(i).getTime());
				cell = row.createCell(4);
				cell.setCellValue(loglist.get(i).getInfo());
				
				long t2 = System.currentTimeMillis();
				
				sum = sum + (t2-t1);
				
			}
			
			System.out.println("表格封装时间："+sum);
			
			long tx = System.currentTimeMillis();
			String root = ServletActionContext.getServletContext().getRealPath("/");
//			String pathFile = root+"upload/payment/日志.xlsx";
//			File file = new File(root+"upload/payment");
			String pathFile = root + UploadFileUtil.UPLOAD_PATH + "/payment/日志.xlsx";
			File file = new File(root+ UploadFileUtil.UPLOAD_PATH + "/payment");
			if(!file.exists()){
				file.mkdirs();
			}
			File file1 = new File(pathFile);
			FileOutputStream fos = new FileOutputStream (file1);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			worksheet.setPrintGridlines(true) ;
			workbook.write (bos);
			
			long ty = System.currentTimeMillis();
			System.out.println("文件输出时间:"+(ty-tx));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		} catch (IOException e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		
		
		return SUCCESS;
	}
	
	/**
     * ajax 触发同步任务
     * 
     * @return
     * @throws IOException
     */
    public synchronized String syncTask() {
        resultMap = new HashMap<>();
        List<Exception> errors = new ArrayList<Exception>();
        try {
            ApplicationContext applicationContext = SpringContext.getApplicationContext();
            JobDetailImpl jobDetail = null;
            try {
                CronTriggerFactoryBean trigger = applicationContext.getBean(taskName, CronTriggerFactoryBean.class);
                jobDetail = (JobDetailImpl) trigger.getJobDataMap().get("jobDetail");
            } catch (Exception e) {
                errors.add(e);
                try {
                    jobDetail = (JobDetailImpl) applicationContext.getBean(taskName, JobDetailFactoryBean.class).getJobDataMap().get("jobDetail");;
                } catch (Exception e2) {
                    errors.add(e2);
                    try {
                        Class<? extends Job> jobClass = (Class<? extends Job>) getClass().getClassLoader().loadClass(taskName);
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
                String[] beanNamesForType = applicationContext.getBeanNamesForType(SchedulerFactoryBean.class);
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
                JobExecutionContext jobContext = new JobExecutionContextImpl(scheduler,
                        new TriggerFiredBundle(jobDetail, trigger, null, false, null, null, null, null), job);
                job.execute(jobContext);
                resultMap.put("success", true);
                return SUCCESS;
            }
        } catch (Exception e) {
            errors.add(e);
        }
        resultMap.put("success", false);
        List<Object> message = new ArrayList<Object>(errors.size());
        for (Exception e : errors) {
            message.add(ExceptionUtils.getRootCauseMessage(e));
        }
        resultMap.put("message", message);
        return SUCCESS;
    }
	
	public String getDownloadLogName(){
		   String downFileName= "";
		   try {
			   //downFileName = "/upload/payment/日志.xlsx";
			   downFileName = "/" + UploadFileUtil.UPLOAD_PATH + "/payment/日志.xlsx";
		   } catch (Exception e) {
		    e.printStackTrace();
		   }
		   return downFileName;
	}
	public InputStream getInputLogStream() throws FileNotFoundException, UnsupportedEncodingException {
		   String name=this.getDownloadLogName();
		   String realPath=name;
		   InputStream in=ServletActionContext.getServletContext().getResourceAsStream(realPath);
		   if(null==in){
		    java.lang.System.out.println("Can not find a java.io.InputStream with the name [inputStream] in the invocation stack. Check the <param name=\"inputName\"> tag specified for this action.检查action中文件下载路径是否正确.");   
		   }
		   return ServletActionContext.getServletContext().getResourceAsStream(realPath);
	}
	
	
	
	public ArrayList<String> getSelected()
	{
		return selected;
	}

	public void setSelected(ArrayList<String> selected)
	{
		this.selected = selected;
	}

	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public List<OperateLog> getList() {
		return list;
	}

	public OpLogService getLogger() {
		return logger;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}

	public void setList(List<OperateLog> list) {
		this.list = list;
	}

	public void setLogger(OpLogService logger) {
		this.logger = logger;
	}

    public HashMap getResultMap() {
        return resultMap;
    }

    public void setResultMap(HashMap resultMap) {
        this.resultMap = resultMap;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
	
}
