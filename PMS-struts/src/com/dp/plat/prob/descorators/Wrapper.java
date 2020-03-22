package com.dp.plat.prob.descorators;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.displaytag.decorator.TableDecorator;

import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.User;
import com.dp.plat.prob.bean.Prob;
import com.dp.plat.prob.bean.ProbReadLog;
import com.dp.plat.prob.bean.ProbRestore;
import com.dp.plat.prob.bean.ProbRestoreWeekly;
import com.dp.plat.prob.bean.SoftVersion;
import com.dp.plat.prob.util.HTMLRegexUtil;
import com.dp.plat.util.Base64Util;
import com.dp.plat.util.DateUtil;
import com.dp.plat.util.MessageUtil;

/**
 * 作为displayTag的装饰器，在JSP页面起作用 通过getPageContext()能获取JSP页面的上下文，进而获取项目绝对路径等信息 eg:
 * getPageContext().getRequest().getServletContext().getContextPath()
 * 
 */
public class Wrapper extends TableDecorator {

	public String getWeeklyer() {
		ProbRestoreWeekly weekly = (ProbRestoreWeekly) getCurrentRowObject();
		return "<a href='module/download.action?fileId=" + weekly.getFileId() + "'>" + weekly.getFileName() + "</a>";
	}

	public String getProjectNamea() {
		ProbRestore probRestore = (ProbRestore) getCurrentRowObject();
		int projectId = probRestore.getProjectId();
		String projectName = probRestore.getProjectName();
		if (projectId == 0) {// 没有项目
			return projectName;
		} else {
			String paramId = Base64Util.EncodeBase64(projectId);
			return "<a target='_blank' href='" + getPageContext().getRequest().getServletContext().getContextPath()
					+ "/module/ProjectModify.action?project.paramId=" + paramId + "&result=310' >" + projectName
					+ "</a>";
		}
	}

	public String getSoftCheckBox() {
		SoftVersion softVersion = (SoftVersion) getCurrentRowObject();
		StringBuilder ver = new StringBuilder();
		ver.append("conp-");
		if (softVersion.getConp() != null) {
			ver.append(softVersion.getConp());
		}
		ver.append(",");
		ver.append("boot-");
		if (softVersion.getBoot() != null) {
			ver.append(softVersion.getBoot());
		}
		ver.append(",");
		ver.append("cpld-");
		if (softVersion.getCpld() != null) {
			ver.append(softVersion.getCpld());
		}
		ver.append(",");
		ver.append("pcb-");
		if (softVersion.getPcb() != null) {
			ver.append(softVersion.getPcb());
		}
		if (ver.length() == 22) {
			return null;
		}
		return "<input type='checkbox' name='softVersionCodes' value='" + ver.toString() + "'/>";
	}

	public String getProjectCheckBox() {
		ProbRestore probRestore = (ProbRestore) getCurrentRowObject();
		int index = getListIndex();
		String html = "<input type='checkbox' name='probRestoreTaskList[" + index + "].ischecked' value='0'>"
				+ "<input type='hidden' name='probRestoreTaskList[" + index + "].serialNum' value='"
				+ probRestore.getSerialNum() + "'>" + "<input type='hidden' name='probRestoreTaskList[" + index
				+ "].itemModel' value='" + probRestore.getItemModel() + "'>"
				+ "<input type='hidden' name='probRestoreTaskList[" + index + "].conp' value='" + probRestore.getConp()
				+ "'>" + "<input type='hidden' name='probRestoreTaskList[" + index + "].boot' value='"
				+ probRestore.getBoot() + "'>" + "<input type='hidden' name='probRestoreTaskList[" + index
				+ "].cpld' value='" + probRestore.getCpld() + "'>" + "<input type='hidden' name='probRestoreTaskList["
				+ index + "].pcb' value='" + probRestore.getPcb() + "'>"
				+ "<input type='hidden' name='probRestoreTaskList[" + index + "].projectName' value='"
				+ probRestore.getProjectName() + "'>" + "<input type='hidden' name='probRestoreTaskList[" + index
				+ "].contractNo' value='" + probRestore.getContractNo() + "'>"
				+ "<input type='hidden' name='probRestoreTaskList[" + index + "].officeCode' value='"
				+ probRestore.getOfficeCode() + "'>" + "<input type='hidden' name='probRestoreTaskList[" + index
				+ "].projectId' value='" + probRestore.getProjectId() + "'>";
		return html;
	}

	public String getProbNumCheck() {
		Prob prob = (Prob) getCurrentRowObject();
		User currectUser = UserContext.getUserContext().getUser();
		String html = "";
		String status = prob.getStatus();
		if (currectUser.isHasRole(MessageUtil.ROLE_PROB_ADMIN)) {
			if ("1".equals(status) || "8".equals(status)) {
				html = "<a href='module/prob_input.action?prob.probId=" + prob.getProbId() + "'>审批</a>";
			} else if ("14".equals(prob.getWatch())) { // 只有跟踪公告才需要发布任务
				html = "<a href='module/prob_edit.action?prob.probId=" + prob.getProbId() + "'>发布任务</a>";
			}
		} else if (currectUser.isHasRole(MessageUtil.ROLE_PROB_RD)
				&& prob.getTrackingUser().equals(currectUser.getUsername()) && "6".equals(status)) {
			html = "<a href='module/prob_input.action?prob.probId=" + prob.getProbId() + "'>编辑</a>";
		} else {
			html = "<a href='module/prob_edit.action?prob.probId=" + prob.getProbId() + "'>查看</a>";
		}
		return html;
	}

	public String getProbEdit() {
		Prob prob = (Prob) getCurrentRowObject();
		return "<a href='module/prob_edit.action?prob.probId=" + prob.getProbId() + "'>" + prob.getProbNum() + "</a>";
	}

	public String getProbOperate() {
		Prob prob = (Prob) getCurrentRowObject();
		User currectUser = UserContext.getUserContext().getUser();
		String html = "";
		if (currectUser.isHasRole(MessageUtil.ROLE_PROB_SUPPORTER)) {
			html += "<a href='module/prob_edit.action?prob.probId=" + prob.getProbId() + "' class='btn-link'>查看</a>";
		}
		if (currectUser.isHasRole(MessageUtil.ROLE_PROB_RD)
				&& currectUser.getUsername().equals(prob.getTrackingUser())) {
			html += "<a href='module/prob_input.action?prob.probId=" + prob.getProbId() + "' class='btn-link'>编辑</a>";
		}
		String status = prob.getStatus();
		if (currectUser.isHasRole(MessageUtil.ROLE_PROB_ADMIN)) {
			if ("8".equals(status) || "1".equals(status)) {
				html += "<a href='module/prob_input.action?prob.probId=" + prob.getProbId()
						+ "' class='btn-link'>审批</a>";
			} else if (("4".equals(status) || "5".equals(status)) && "14".equals(prob.getWatch())) {
				html += "<a href='module/prob_edit.action?prob.probId=" + prob.getProbId()
						+ "' class='btn-link'>发布任务</a>";
			}
			html += "<a href='javascript:void(0)' class='btn-link' onclick='deleteProb(" + prob.getProbId()
					+ ")'>删除</a>";
		}
		if (html.isEmpty()) {
			if (currectUser.isHasRole(MessageUtil.ROLE_PROB_RD)) {
				html += "<a href='module/prob_input.action?prob.probId=" + prob.getProbId()
						+ "' class='btn-link'>查看</a>";
			} else {
				html += "<a href='module/prob_edit.action?prob.probId=" + prob.getProbId()
						+ "' class='btn-link'>查看</a>";
			}
		}
		return html;
	}

	public String getProbRestoreBox() {
		ProbRestore probRestore = (ProbRestore) getCurrentRowObject();
		return "<input type='checkbox' name='id' value='" + probRestore.getId() + "'/>";
	}

	public String getProbRestoreDelete() {
		ProbRestore probRestore = (ProbRestore) getCurrentRowObject();
		return "<a href='JavaScript:void(0)' onclick='deleteSingle(" + probRestore.getId() + ")'>删除</a>";
	}

	public String getSimplifyDesc() {
		Prob prob = (Prob) getCurrentRowObject();
		return StringUtils.trimToEmpty(HTMLRegexUtil.simplifyHTML(prob.getDesc(), "(<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>)|( style=[^>]*)|(\\&nbsp\\;)|(<(?=a|\\/a|span|\\/span|font|\\/font|img).*?>)"));
	}
	
	public String getSimplifySolution() {
		Prob prob = (Prob) getCurrentRowObject();
		return StringUtils.trimToEmpty(HTMLRegexUtil.simplifyHTML(prob.getSolution(), "(<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>)|( style=[^>]*)|(\\&nbsp\\;)|(<(?=a|\\/a|span|\\/span|h\\d+|\\/h\\d+|font|\\/font|img).*?>)"));
	}
	
	public String getProbReadCommitInterval() {
		ProbReadLog probReadLog = (ProbReadLog) getCurrentRowObject();
		if (probReadLog.getCommitTime() == null || probReadLog.getStatus() != 1) {
			return "";
		}
		Calendar firstTime = Calendar.getInstance();
		firstTime.setTime(probReadLog.getFirstTime());
		
		Calendar commitTime = Calendar.getInstance();
		commitTime.setTime(probReadLog.getCommitTime());
		
		return DateUtil.getFormatedDateInterval(firstTime, commitTime);
//		if (commitTime.compareTo(firstTime) <= 0) {
//			return "";
//		}
//		
//		int year = commitTime.get(Calendar.YEAR) - firstTime.get(Calendar.YEAR);
//		int month = commitTime.get(Calendar.MONTH) - firstTime.get(Calendar.MONTH);
//		int day = commitTime.get(Calendar.DAY_OF_MONTH) - firstTime.get(Calendar.DAY_OF_MONTH);
//		int hour = commitTime.get(Calendar.HOUR_OF_DAY) - firstTime.get(Calendar.HOUR_OF_DAY);
//		int minute = commitTime.get(Calendar.MINUTE) - firstTime.get(Calendar.MINUTE);
//		int second = commitTime.get(Calendar.SECOND) - firstTime.get(Calendar.SECOND);
//		
//		StringBuilder str = new StringBuilder();
//		if (second > 0) {
//			str.insert(0, "秒").insert(0, second);
//		} else if (second < 0) {
//			minute--;
//			str.insert(0, "秒").insert(0, second + 60);
//		}
//		if (minute > 0) {
//			str.insert(0, "分").insert(0, minute);
//		} else if (minute < 0) {
//			hour--;
//			str.insert(0, "分").insert(0, minute + 60);
//		}
//		if (hour > 0) {
//			str.insert(0, "小时").insert(0, hour);
//		} else if (hour < 0) {
//			day--;
//			str.insert(0, "小时").insert(0, hour + 24);
//		}
//		if (day > 0) {
//			str.insert(0, "天").insert(0, day);
//		} else if (day < 0) {
//			month--;
//			int fMonth = firstTime.get(Calendar.MONTH);
//			// 找前一个月
//			fMonth = fMonth > 0 ? fMonth : fMonth + 12;
//			int daysOfMonth = getDaysByYearMonth(firstTime.get(Calendar.YEAR), fMonth);
//			str.insert(0, "天").insert(0, day + daysOfMonth);
//		}
//		
//		if (month > 0) {
//			str.insert(0, "个月").insert(0, month);
//		} else if (month < 0) {
//			year--;
//			str.insert(0, "个月").insert(0, month + 12);
//		}
//		if (year > 0) {
//			str.insert(0, "年").insert(0, year);
//		}
//		return str.toString();
	}
	
//	public static void main(String[] args) throws ParseException {
//		ProbReadLog probReadLog = new ProbReadLog();
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		probReadLog.setCommitTime(dateFormat.parse("2019-08-1 15:12:20"));
//		probReadLog.setFirstTime(dateFormat.parse("2018-08-01 15:12:29"));
//		
//		Calendar firstTime = Calendar.getInstance();
//		firstTime.setTime(probReadLog.getFirstTime());
//		
//		Calendar commitTime = Calendar.getInstance();
//		commitTime.setTime(probReadLog.getCommitTime());
//		
//		int year = commitTime.get(Calendar.YEAR) - firstTime.get(Calendar.YEAR);
//		int month = commitTime.get(Calendar.MONTH) - firstTime.get(Calendar.MONTH);
//		int day = commitTime.get(Calendar.DAY_OF_MONTH) - firstTime.get(Calendar.DAY_OF_MONTH);
//		int hour = commitTime.get(Calendar.HOUR_OF_DAY) - firstTime.get(Calendar.HOUR_OF_DAY);
//		int minute = commitTime.get(Calendar.MINUTE) - firstTime.get(Calendar.MINUTE);
//		int second = commitTime.get(Calendar.SECOND) - firstTime.get(Calendar.SECOND);
//		
//		StringBuilder str = new StringBuilder();
//		if (second > 0) {
//			str.insert(0, "秒").insert(0, second);
//		} else if (second < 0) {
//			minute--;
//			str.insert(0, "秒").insert(0, second + 60);
//		}
//		if (minute > 0) {
//			str.insert(0, "分").insert(0, minute);
//		} else if (minute < 0) {
//			hour--;
//			str.insert(0, "分").insert(0, minute + 60);
//		}
//		if (hour > 0) {
//			str.insert(0, "小时").insert(0, hour);
//		} else if (hour < 0) {
//			day--;
//			str.insert(0, "小时").insert(0, hour + 24);
//		}
//		if (day > 0) {
//			str.insert(0, "天").insert(0, day);
//		} else if (day < 0) {
//			month--;
//			int fMonth = firstTime.get(Calendar.MONTH);
//			// 找前一个月
//			fMonth = fMonth > 0 ? fMonth : fMonth + 12;
//			int daysOfMonth = getDaysByYearMonth(firstTime.get(Calendar.YEAR), fMonth);
//			str.insert(0, "天").insert(0, day + daysOfMonth);
//		}
//		
//		if (month > 0) {
//			str.insert(0, "个月").insert(0, month);
//		} else if (month < 0) {
//			year--;
//			str.insert(0, "个月").insert(0, month + 12);
//		}
//		if (year > 0) {
//			str.insert(0, "年").insert(0, year);
//		}
//		
//		System.out.println(str);
//	}
//	
	public static int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }
	
}
