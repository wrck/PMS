package com.dp.plat.prob.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.dp.plat.context.UserContext;
import com.dp.plat.dao.BasicDataDao;
import com.dp.plat.dao.UserManageDao;
import com.dp.plat.data.bean.NotificationTemplate;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ReportLineData;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.data.bean.User;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.prob.bean.Prob;
import com.dp.plat.prob.bean.ProbFile;
import com.dp.plat.prob.bean.ProbReadLog;
import com.dp.plat.prob.bean.ProbRestore;
import com.dp.plat.prob.bean.ProbRestoreWeekly;
import com.dp.plat.prob.bean.ProbStatistic;
import com.dp.plat.prob.bean.SoftVersion;
import com.dp.plat.prob.dao.ProbManageDao;
import com.dp.plat.prob.param.ProbParam;
import com.dp.plat.service.BaseServiceImpl;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.NotificationTemplateUtil;
import com.dp.plat.util.StringEscUtil;

public class ProbManageServiceImpl extends BaseServiceImpl implements ProbManageService {
	private ProbManageDao probManageDao;
	private UserManageDao userManageDao;
	private BasicDataDao basicDataDao;

	public void setUserManageDao(UserManageDao userManageDao) {
		this.userManageDao = userManageDao;
	}

	public void setProbManageDao(ProbManageDao probManageDao) {
		this.probManageDao = probManageDao;
	}

	public void setBasicDataDao(BasicDataDao basicDataDao) {
		this.basicDataDao = basicDataDao;
	}

	@Override
	public int saveProb(Prob prob, List<SoftVersion> softVersionList, String root) throws IOException {
		int probId = probManageDao.saveProb(prob);
		if (softVersionList != null && softVersionList.size() > 0) {
			// 失效原有版本信息
			probManageDao.updateInvalidSoftVersion(probId);
			// 新增版本
			probManageDao.saveSoftVersion(softVersionList, probId);
		}

		/*
		 * //查询所有服务经理邮件地址 String spmail =
		 * basicDataDao.querySysArg("prob.release.mail");
		 */

		// 缺陷通知发送给技术公告员，进行审核
		String probAdmin = userManageDao.queryServiceMails(MessageUtil.ROLE_PROB_ADMIN);

		// 发布邮件通知
		prob.setProbId(probId);
		prob = probManageDao.queryOneProb(prob);
		// this.keepRelaseEmail(prob, softVersionList, null, root, null,
		// probAdmin);
		this.keepRelaseEmail(prob, root, null, probAdmin, defaultParaMap(prob, softVersionList, 3, null));
		return probId;
	}

	@Override
	public List<Prob> queryProbList(Prob prob, DisplayParam displayParam) {
		return probManageDao.queryProbList(prob, displayParam);
	}

	@Override
	public Prob queryOneProb(Prob prob) {
		return probManageDao.queryOneProb(prob);
	}

	@Override
	public void updateProb(Prob prob, List<SoftVersion> softVersionList) {
		int isRrobAdmin = 3;
		// 更新主表信息,技术公告管理员可更新状态，其他人员都将状态改为待确认
		if (!UserContext.getUserContext().isHasRole(MessageUtil.ROLE_PROB_ADMIN)) {
			prob.setStatus("8");
		}
		probManageDao.updateProb(prob);
		// 更新影响版本
		if (softVersionList != null && softVersionList.size() > 0) {
			// 失效原有版本信息
			probManageDao.updateInvalidSoftVersion(prob.getProbId());
			// 新增版本
			probManageDao.saveSoftVersion(softVersionList, prob.getProbId());
		} else {
			// 失效原有版本信息
			probManageDao.updateInvalidSoftVersion(prob.getProbId());
		}
		String root = ServletActionContext.getServletContext().getRealPath("/");

		prob = probManageDao.queryOneProb(prob);

		String bccs = "";
		// 技术公告管理员，通知sp,tsc,xteam,pdt_lb群组
		if (UserContext.getUserContext().isHasRole(MessageUtil.ROLE_PROB_ADMIN)) {
			isRrobAdmin = 1;
			String bcc = "";
			// 已拒绝状态，即驳回整改只通知任务创建者
			if ("6".equals(prob.getStatus())) {
				User trackUser = userManageDao.queryUserByUserName(prob.getTrackingUser());
				bcc = trackUser.getEmail();
				if (StringUtils.isNotBlank(bcc)) {
					bccs += ";" + bcc;
				}
			} else {// 审批通过，通知tsc，sp,pdt_ld,xteam群组
				// 总部二线
				bcc = basicDataDao.querySysArg("prob.execute.mail");
				if (StringUtils.isNotBlank(bcc)) {
					bccs += ";" + bcc;
				}
				// 全体用服
				bcc = basicDataDao.querySysArg("prob.release.mail");
				if (StringUtils.isNotBlank(bcc) && prob.getVisibleRange() != 1) { // 仅搜索的公告不发送sp群组
					bccs += ";" + bcc;
				}
				// 维护经理
				bcc = basicDataDao.querySysArg("prob.xteam.mail");
				if (StringUtils.isNotBlank(bcc)) {
					bccs += ";" + bcc;
				}
				// 产品经理
				bcc = basicDataDao.querySysArg("prob.pdt_ld.mail");
				if (StringUtils.isNotBlank(bcc)) {
					bccs += ";" + bcc;
				}
			}
		} else {// 其他权限更新，通知技术公告管理员
			// 产品任务管理员
			bccs = userManageDao.queryServiceMails(MessageUtil.ROLE_PROB_ADMIN);
		}

		// 发布邮件通知
		this.keepRelaseEmail(prob, root, null, bccs, defaultParaMap(prob, softVersionList, isRrobAdmin, null));
	}

	@Override
	public List<SoftVersion> checkSoftVersionList(SoftVersion softVersion) {
		return probManageDao.checkSoftVersionList(softVersion);
	}

	@Override
	public List<SoftVersion> querySoftVersionList(int probId) {
		return probManageDao.querySoftVersionList(probId);
	}

	@Override
	public void updateProbSoftVersion(List<SoftVersion> softVersionList, int probId) {
		if (softVersionList != null && softVersionList.size() > 0) {
			// 失效原有版本信息
			probManageDao.updateInvalidSoftVersion(probId);
			// 新增版本
			probManageDao.saveSoftVersion(softVersionList, probId);
		} else {
			// 失效原有版本信息
			probManageDao.updateInvalidSoftVersion(probId);
		}
	}

	@Override
	public Map<Integer, String> queryProbFileMap(int probId) {
		return probManageDao.queryProbFileMap(probId);
	}

	@Override
	public List<ProbRestore> queryProbRestoreList(ProbRestore probRestore, DisplayParam restoreDisplayParam) {
		return probManageDao.queryProbRestoreList(probRestore, restoreDisplayParam);
	}

	@Override
	public void insertBatchProbRestoreTask(ProbRestore probRestore, List<ProbRestore> probRestoreTaskList, String root)
			throws IOException {
		/*
		 * // 0.0初次发布任务将技术公告状态改为解决中 int processSize =
		 * probManageDao.queryProbRestoreProcessSize(probRestore.getProbId());
		 * Prob prob = new Prob(); prob.setProbId(probRestore.getProbId());
		 * 
		 * if (processSize == 0) {// 初次发布任务 prob.setStatus("5");
		 * probManageDao.updateProb(prob); }
		 */

		// 发布任务时间状态改为解决中
		Prob prob = new Prob();
		prob.setProbId(probRestore.getProbId());
		prob.setStatus("5");
		probManageDao.updateProb(prob);

		// 0.1插入流程过程数据记录
		int processId = probManageDao.insertProbRestoreProcess(probRestore);
		// 0.2插入子任务
		probRestore.setProcessId(processId);
		probManageDao.insertBatchProbRestoreTask(probRestore, probRestoreTaskList);
		// 0.3邮件通知
		prob = probManageDao.queryOneProb(prob);
		String bccs = "";
		/*
		 * if(probRestore.getAssignee() != null &&
		 * !"".equals(probRestore.getAssignee())){//有办理人的直接通知办理人 bccs =
		 * userManageDao.queryUserByUserName(probRestore.getAssignee()).getEmail
		 * (); }else{//找服务经理 String officeCodes =
		 * findOfficeCodes(probRestoreTaskList); bccs =
		 * userManageDao.queryServiceMails(officeCodes.substring(1,
		 * officeCodes.length() -1) , MessageUtil.ROLE_SERVICEMANAGER); }
		 */

		// 总部二线
		String bcc = basicDataDao.querySysArg("prob.execute.mail");
		if (StringUtils.isNotBlank(bcc)) {
			bccs += ";" + bcc;
		}
		// 维护经理
		bcc = basicDataDao.querySysArg("prob.xteam.mail");
		if (StringUtils.isNotBlank(bcc)) {
			bccs += ";" + bcc;
		}
		// 产品经理
		bcc = basicDataDao.querySysArg("prob.pdt_ld.mail");
		if (StringUtils.isNotBlank(bcc)) {
			bccs += ";" + bcc;
		}
		// 用服人员，技术跟踪公告发送给有任务的办事处，其他公告发送给全体用服
		if ("14".equals(prob.getWatch())) {
			String officeCodes = findOfficeCodes(probRestoreTaskList);
			bcc = userManageDao.queryServiceMails(officeCodes.substring(1, officeCodes.length() - 1), null);
			if (StringUtils.isNotBlank(bcc)) {
				bccs += ";" + bcc;
			}
		} else {
			// 全体用服
			bcc = basicDataDao.querySysArg("prob.release.mail");
			if (StringUtils.isNotBlank(bcc) && prob.getVisibleRange() != 1) { // 仅搜索的公告不发送sp群组
				bccs += ";" + bcc;
			}
		}
		Map<String, Object> moreInfo = new HashMap<>();
		moreInfo.put("probRestore", probRestore);
		// this.keepRelaseEmail(prob, null, probRestore.getRestoreRemark(),
		// root, null, bccs);
		this.keepRelaseEmail(prob, root, null, bccs, defaultParaMap(prob, null, 1, moreInfo));
	}

	/**
	 * 找出发布的子跟踪任务涉及的办事处
	 * 
	 * @param probRestoreTaskList
	 * @return
	 */
	private String findOfficeCodes(List<ProbRestore> probRestoreTaskList) {
		Set<String> officeCodes = new HashSet<String>();
		for (ProbRestore restore : probRestoreTaskList) {
			if (restore.getIschecked() == 1 && restore.getOfficeCode() != null && !"".equals(restore.getOfficeCode())) {// 说明要创建子任务
				officeCodes.add(restore.getOfficeCode());
			}
		}
		String[] objects = new String[officeCodes.size()];
		objects = officeCodes.toArray(objects);
		return Arrays.toString(objects);
	}

	@Override
	public List<ProbRestore> queryProbRestoreTaskList(ProbRestore probRestore, DisplayParam restoreDisplayParam)
			throws UnsupportedEncodingException {
		return probManageDao.queryProbRestoreTaskList(probRestore, restoreDisplayParam);
	}

	@Override
	public List<ProbRestore> queryProbRestoreTaskProjectList(ProbRestore probRestore, DisplayParam restoreDisplayParam)
			throws UnsupportedEncodingException {
		return probManageDao.queryProbRestoreTaskProjectList(probRestore, restoreDisplayParam);
	}

	@Override
	public void updateProbRestoreTask(ProbRestore probRestore, String restoreIds, int isProbAdmin) throws IOException {
		// 0.1插入流程过程数据记录
		int processId = probManageDao.insertProbRestoreProcess(probRestore);
		// 0.2更新办理人
		if (probRestore.getAssignee() != null && !"".equals(probRestore.getAssignee())) {
			probManageDao.updateProbRestoreAssignee(probRestore, restoreIds);
		}
		// 0.3更新技术公告的状态,更新流程流转记录ID
		if (isProbAdmin != 1 && isProbAdmin != 2) {// 如果不是管理员/技术支持人员，更新子任务的办理人
			String assignee = (probRestore.getAssignee() != null && !"".equals(probRestore.getAssignee()))
					? probRestore.getAssignee() : getLoginName();
			probManageDao.updateProbRestore(processId, restoreIds, assignee);
		} else {
			probManageDao.updateProbRestore(processId, restoreIds, null);
		}
		// 0.4发送邮件通知
		Prob prob = new Prob();
		prob.setProbId(probRestore.getProbId());
		prob = probManageDao.queryOneProb(prob);
		Map<String, Object> moreInfo = new HashMap<String, Object>();
		moreInfo.put("probRestore", probRestore);
		String bccs = "";
		if (isProbAdmin != 1 && isProbAdmin != 2 && probRestore.getRestoreStatus() != 10) {// 非管理员、技术支持人员用户更新任务
			List<ShipmentInfo> softVersionChangeList = probManageDao
					.queryHistSoftVersionListByProbRestoreIds(restoreIds);
			moreInfo.put("softVersionChangeList", softVersionChangeList);
			// 任务执行阶段，发给tsc群组
			String bcc = basicDataDao.querySysArg("prob.execute.mail");
			if (StringUtils.isNotBlank(bcc)) {
				bccs += ";" + bcc;
			}
			// 产品任务管理员
			bcc = userManageDao.queryServiceMails(MessageUtil.ROLE_PROB_ADMIN);
			if (StringUtils.isNotBlank(bcc)) {
				bccs += ";" + bcc;
			}
			// 技术支援人员
			bcc = userManageDao.queryServiceMails(MessageUtil.ROLE_PROB_SUPPORTER);
			if (StringUtils.isNotBlank(bcc)) {
				bccs += ";" + bcc;
			}
			// 办事处内用服
			bcc = probManageDao.queryOfficeMailsByProbRestoreIds(restoreIds);
			if (StringUtils.isNotBlank(bcc)) {
				bccs += ";" + bcc;
			}
		} else if (isProbAdmin != 1 && isProbAdmin != 2 && probRestore.getRestoreStatus() == 10) {// 服务经理指派任务给项目经理
			bccs = probManageDao.queryProbAssigneeEmails(restoreIds);
		} else {// 管理员处理任务，发送给相关责任人
			String bcc = basicDataDao.querySysArg("prob.execute.mail");
			if (StringUtils.isNotBlank(bcc)) {
				bccs += ";" + bcc;
			}
			// 产品任务管理员
			bcc = userManageDao.queryServiceMails(MessageUtil.ROLE_PROB_ADMIN);
			if (StringUtils.isNotBlank(bcc)) {
				bccs += ";" + bcc;
			}
			// 技术支援人员
			bcc = userManageDao.queryServiceMails(MessageUtil.ROLE_PROB_SUPPORTER);
			if (StringUtils.isNotBlank(bcc)) {
				bccs += ";" + bcc;
			}
			/*
			 * // 子任务相关负责人 bcc =
			 * probManageDao.queryProbAssigneeEmails(restoreIds); if
			 * (StringUtils.isNotBlank(bcc)) { bccs += ";" + bcc; }
			 */
			// 办事处内用服
			bcc = probManageDao.queryOfficeMailsByProbRestoreIds(restoreIds);
			if (StringUtils.isNotBlank(bcc)) {
				bccs += ";" + bcc;
			}
		}
		if (StringUtils.isNotBlank(bccs)) {
			String root = ServletActionContext.getServletContext().getRealPath("/");
			this.keepRelaseEmail(prob, root, null, bccs, defaultParaMap(prob, null, isProbAdmin, moreInfo));
		}
	}

	@Override
	public void deleteProbInfo(int probId) {
		probManageDao.deleteProbInfo(probId);
	}

	@Override
	public String queryNextProbNum() {
		int val = probManageDao.queryNextVal();
		String num = "0000" + val;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		return "SP." + sdf.format(new Date()) + num.substring(num.length() - 4);
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> defaultParaMap(Prob prob, List<SoftVersion> softVersionList, int isProbAdmin,
			Map<String, Object> moreInfo) {
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("$currectUser$", getLoginName() + "-" + getRealname());
		paraMap.put("$taskType$", prob.getWatchName());
		paraMap.put("$operation$", "");
		paraMap.put("$status$", prob.getStatusName());
		paraMap.put("$priority$", prob.getPriorityName());
		paraMap.put("$productType$", prob.getProductType());
		paraMap.put("$description$", prob.getDesc());
		paraMap.put("$solution$", prob.getSolution());
		paraMap.put("$moreInfo$", "");
		paraMap.put("$trackUser$", prob.getTrackingUsername());
		paraMap.put("$todo$", "查看详情");
		// 影响版本
		if (softVersionList == null) {
			softVersionList = probManageDao.querySoftVersionList(prob.getProbId());
		}
		StringBuilder softversion = new StringBuilder();
		for (SoftVersion version : softVersionList) {
			if (version.getConp() != null) {
				softversion.append("conp:");
				softversion.append(version.getConp());
				softversion.append("  ");
			}
			if (version.getBoot() != null) {
				softversion.append("boot:");
				softversion.append(version.getBoot());
				softversion.append("  ");
			}
			if (version.getCpld() != null) {
				softversion.append("cpld:");
				softversion.append(version.getCpld());
				softversion.append("  ");
			}
			if (version.getPcb() != null) {
				softversion.append("pcb:");
				softversion.append(version.getPcb());
				softversion.append("  ");
			}
			if (StringUtils.isNotBlank(version.getManualEntry())) {
				softversion.append(version.getManualEntry());
				softversion.append("  ");
			}
			softversion.append("<br/>");
		}
		paraMap.put("$softversion$", softversion.toString());

		// 根据角色发送不同内容
		if (isProbAdmin == 1) {
			String content = "主题为『" + prob.getTheme() + "』的";
			switch (prob.getStatus()) {
			case "6":
				content = "驳回了" + content;
				paraMap.put("$moreInfo$", "审批意见：" + prob.getRemark());
				break;
			case "4":
				content = "审批通过了" + content;
				paraMap.put("$moreInfo$", "审批意见：" + prob.getRemark());
				break;
			case "5":
				ProbRestore probRestore = moreInfo == null ? null : (ProbRestore) moreInfo.get("probRestore");
				int restoreStatus = probRestore == null ? 0 : probRestore.getRestoreStatus();
				if (restoreStatus == 10) {
					content = "对" + content;
					paraMap.put("$taskType$", prob.getWatchName() + "发布了跟踪任务");
					if (probRestore != null && StringUtils.isNotBlank(probRestore.getRestoreRemark())) {
						paraMap.put("$moreInfo$", "备注说明：" + probRestore.getRestoreRemark());
					}
					paraMap.put("$todo$", "进行处理");
				} else if (restoreStatus == 31) {
					content = "直接闭环了" + content;
					paraMap.put("$taskType$", prob.getWatchName() + "的跟踪任务");
					if (probRestore != null && StringUtils.isNotBlank(probRestore.getRestoreRemark())) {
						paraMap.put("$moreInfo$", "备注说明：" + probRestore.getRestoreRemark());
					}
				} else {
					content = "对" + content;
					paraMap.put("$taskType$", prob.getWatchName() + "进行了更新");
				}
				break;
			case "10":
				content = "关闭了" + content;
				break;
			default:
				content = "对" + content;
				paraMap.put("$taskType$", prob.getWatchName() + "进行了更新");
			}
			paraMap.put("$operation$", content);
		} else if (isProbAdmin == 2) {
			ProbRestore probRestore = moreInfo == null ? null : (ProbRestore) moreInfo.get("probRestore");
			int restoreStatus = probRestore == null ? 0 : probRestore.getRestoreStatus();
			String content = "";
			paraMap.put("$taskType$", "");
			switch (restoreStatus) {
			case 10:
				content = "重新发布了办事处的跟踪任务";
				paraMap.put("$todo$", "进行处理");
				break;
			case 31:
				content = "审批通过了办事处的跟踪任务闭环申请";
				break;
			default:
				content = "对主题为『" + prob.getTheme() + "』的";
				paraMap.put("$taskType$", prob.getWatchName() + "进行了更新");
			}
			paraMap.put("$operation$", content);
			paraMap.put("$moreInfo$", "审批意见：" + (probRestore == null ? "" : probRestore.getRestoreRemark()));
		} else if (isProbAdmin == 3) {
			// 研发人员创建技术公告
			if ("1".equals(prob.getStatus())) {
				paraMap.put("$operation$", "发布了主题为『" + prob.getTheme() + "』的新");
			} else {
				paraMap.put("$operation$", "更新了主题为『" + prob.getTheme() + "』的");
			}
			paraMap.put("$todo$", "进行审批");
		} else {
			// 用服人员
			ProbRestore probRestore = moreInfo == null ? null : (ProbRestore) moreInfo.get("probRestore");
			int restoreStatus = probRestore == null ? 0 : probRestore.getRestoreStatus();
			String content = "主题为『" + prob.getTheme() + "』的技术公告的";
			paraMap.put("$taskType$", "");
			switch (restoreStatus) {
			case 10:
				content = "给你指派了" + content;
				paraMap.put("$taskType$", "跟踪任务");
				if (probRestore != null && StringUtils.isNotBlank(probRestore.getRestoreRemark())) {
					paraMap.put("$moreInfo$", "备注说明：" + probRestore.getRestoreRemark());
				}
				paraMap.put("$todo$", "进行处理");
				break;

			// 未接受和已处理都算闭环申请
			case 20:
			case 30:
				if (restoreStatus == 30) {
					content = "对跟踪任务提出了闭环申请";
				} else {
					content = "返回了无需跟踪的子任务";
				}
				StringBuilder moreContent = new StringBuilder();
				/*
				 * SoftChangeLog softChangeLog = (SoftChangeLog)
				 * moreInfo.get("softChangeLog");
				 * moreContent.append("更新记录：").append(softChangeLog.
				 * getChangeVersion()).append("<br/>");
				 * moreContent.append("更新说明：").append(softChangeLog.
				 * getChangeRemark()).append("<br/>");
				 */
				moreContent.append("更新内容如下：");
				moreContent.append("<table border='1' cellpadding='5px' cellspacing='0'>").append("<tr>").append("<th>")
						.append(StringEscUtil.getText("pm.orderdata.barcode")).append("</th>").append("<th>")
						.append(StringEscUtil.getText("prob.info.product.type")).append("</th>").append("<th>")
						.append(StringEscUtil.getText("prob.info.conp")).append("</th>").append("<th>")
						.append(StringEscUtil.getText("prob.info.boot")).append("</th>").append("<th>")
						.append(StringEscUtil.getText("prob.info.cpld")).append("</th>").append("<th>")
						.append(StringEscUtil.getText("prob.info.pcb")).append("</th>").append("<th>")
						.append(StringEscUtil.getText("prob.info.update.time")).append("</th>").append("</tr>");
				List<ShipmentInfo> softVersionChangeList = ((List<ShipmentInfo>) moreInfo.get("softVersionChangeList"));
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				for (ShipmentInfo shipmentInfo : softVersionChangeList) {
					moreContent.append("<tr>").append("<td>").append(shipmentInfo.getBarCode()).append("</td>")
							.append("<td>").append(shipmentInfo.getItemName()).append("</td>");
					if (shipmentInfo.getConpChange() == 0) {
						moreContent.append("<td>").append(shipmentInfo.getConp()).append("</td>");
					} else {
						moreContent.append("<td style='color: red;'>").append(shipmentInfo.getConpBak()).append("->")
								.append(shipmentInfo.getConp()).append("</td>");
					}
					if (shipmentInfo.getBootChange() == 0) {
						moreContent.append("<td>").append(shipmentInfo.getBoot()).append("</td>");
					} else {
						moreContent.append("<td style='color: red;'>").append(shipmentInfo.getBootBak()).append("->")
								.append(shipmentInfo.getBoot()).append("</td>");
					}
					if (shipmentInfo.getCpldChange() == 0) {
						moreContent.append("<td>").append(shipmentInfo.getCpld()).append("</td>");
					} else {
						moreContent.append("<td style='color: red;'>").append(shipmentInfo.getCpldBak()).append("->")
								.append(shipmentInfo.getCpld()).append("</td>");
					}
					if (shipmentInfo.getPcbChange() == 0) {
						moreContent.append("<td>").append(shipmentInfo.getPcb()).append("</td>");
					} else {
						moreContent.append("<td style='color: red;'>").append(shipmentInfo.getPcbBak()).append("->")
								.append(shipmentInfo.getPcb()).append("</td>");
					}
					String executeTime = shipmentInfo.getExecuteTime() == null ? ""
							: dateFormat.format(shipmentInfo.getExecuteTime());
					moreContent.append("<td>").append(executeTime).append("</td>").append("</tr>");
				}
				moreContent.append("</table>");

				String remark = "";
				if (probRestore != null && StringUtils.isNotBlank(probRestore.getRestoreRemark())) {
					remark = "备注说明：" + probRestore.getRestoreRemark() + "<br/>";
				}
				paraMap.put("$moreInfo$", remark + moreContent.toString());
				paraMap.put("$todo$", "进行审批");
				break;
			default:
				content = "对主题为『" + prob.getTheme() + "』的";
				paraMap.put("$taskType$", prob.getWatchName() + "的跟踪任务进行了更新");
			}
			paraMap.put("$operation$", content);
		}
		return paraMap;
	}

	/**
	 * 使用模板
	 * 
	 * @param prob
	 * @param root
	 * @param file
	 * @param bccs
	 * @param replceParams
	 */
	public void keepRelaseEmail(Prob prob, String root, String file, String bccs, Map<String, String> replceParams) {
		NotificationTemplate mailTemplate = NotificationTemplateUtil.getTemplate(MessageUtil.NOTIFICATION_CODE_PROB);
		String subject = mailTemplate.getNotificationSubject() + prob.getProbNum() + "-" + prob.getTheme();// 邮件主题
		String content = mailTemplate.getNotificationContent();
		for (String key : replceParams.keySet()) {
			String value = replceParams.get(key);
			if (value == null) {
				value = "";
			}
			content = content.replace(key, value);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("subject", subject);
		params.put("content", content);
		params.put("bcc", bccs);
		if (file == null) {
			List<ProbFile> files = probManageDao.queryProbFileList(prob.getAttachments());
			if (files.size() > 0) {
				StringBuilder attachFileNames = new StringBuilder();
				for (int i = 0; i < files.size(); i++) {
					if (i != 0) {
						attachFileNames.append("&&");
					}
					attachFileNames.append(root + files.get(i).getPath());
					attachFileNames.append(",");
					attachFileNames.append(files.get(i).getName());
				}
				params.put("attachFileNames", attachFileNames);
			}
		} else {
			params.put("attachFileNames", root + file);
		}

		NotificationTemplateUtil.keepMailNoTemplate(params);
	}

	/**
	 * 无模板
	 * 
	 * @param prob
	 * @param softVersionList
	 * @param remark
	 * @param root
	 * @param file
	 * @param bccs
	 * @throws IOException
	 */
	@Override
	public void keepRelaseEmail(Prob prob, List<SoftVersion> softVersionList, String remark, String root, String file,
			String bccs) throws IOException {
		String subject = "【工程项目管理系统-技术公告发布】" + prob.getProbNum() + "-" + prob.getTheme();// 邮件主题
		StringBuilder content = new StringBuilder();
		content.append("技术公告已由");
		content.append(getRealname());
		content.append("更新，请登录<a href='pms.dptech.com'>PMS系统</a>查看");
		content.append("<br/>");
		content.append(remark == null ? "" : remark);
		content.append("<br/>");
		content.append("<br/>");
		content.append("跟踪者：");
		content.append(prob.getTrackingUsername());// 跟踪者
		content.append("<br/>状态：");
		content.append(prob.getStatusName());
		content.append("<br/>严重级别：");
		content.append(prob.getPriorityName());
		content.append("<br/>影响版本：");
		if (softVersionList == null) {
			softVersionList = probManageDao.querySoftVersionList(prob.getProbId());
		}
		for (SoftVersion version : softVersionList) {
			if (version.getConp() != null) {
				content.append("conp:");
				content.append(version.getConp());
				content.append("  ");
			}
			if (version.getBoot() != null) {
				content.append("boot:");
				content.append(version.getBoot());
				content.append("  ");
			}
			if (version.getCpld() != null) {
				content.append("cpld:");
				content.append(version.getCpld());
				content.append("  ");
			}
			if (version.getPcb() != null) {
				content.append("pcb:");
				content.append(version.getPcb());
				content.append("  ");
			}
			if (version.getManualEntry() != null) {
				content.append(version.getManualEntry());
				content.append("  ");
			}
			content.append("<br/>");
		}
		content.append("产品类型：");
		content.append(prob.getProductType() == null ? "" : prob.getProductType());
		content.append("<br/>技术公告描述：");
		content.append(prob.getDesc());
		content.append("<br/>解决方案：");
		content.append(prob.getSolution());
		InputStream in = this.getClass().getResourceAsStream("/com/dp/plat/prob/util/signature.properties");
		Properties p = new Properties();
		p.load(in);
		content.append("<br/><br/><br/><p style='color: red;'>");
		content.append(p.getProperty("signature"));
		content.append("</p>");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("subject", subject);
		params.put("content", content);
		params.put("bcc", bccs);
		if (file == null) {
			List<ProbFile> files = probManageDao.queryProbFileList(prob.getAttachments());
			if (files.size() > 0) {
				StringBuilder attachFileNames = new StringBuilder();
				for (int i = 0; i < files.size(); i++) {
					if (i != 0) {
						attachFileNames.append("&&");
					}
					attachFileNames.append(root + files.get(i).getPath());
					attachFileNames.append(",");
					attachFileNames.append(files.get(i).getName());
				}
				params.put("attachFileNames", attachFileNames);
			}
		} else {
			params.put("attachFileNames", root + file);
		}

		NotificationTemplateUtil.keepMailNoTemplate(params);
	}

	@Override
	public void insertProbTaskWeekly(int fileId, int probId, String root, String weeklyPath) throws IOException {
		// 0.1保存周报
		probManageDao.insertProbTaskWeekly(fileId, probId);
		// 0.2发送邮件通知
		Prob prob = new Prob();
		prob.setProbId(probId);
		prob = probManageDao.queryOneProb(prob);
		String bccs = basicDataDao.querySysArg("prob.execute.mail");// tsc@dptechnology群组
		this.keepRelaseEmail(prob, null, "任务进展周报", root, weeklyPath, bccs);
	}

	@Override
	public List<ProbRestoreWeekly> queryProbWeekly(int probId, String username) {
		return probManageDao.queryProbWeekly(probId, username);
	}

	@Override
	public void bacthDeleteProbRestores(String probRestoreIds) {
		probManageDao.bacthDeleteProbRestores(probRestoreIds);
	}

	@Override
	public void updateProbStatus(Prob prob) {
		probManageDao.updateProbStatus(prob);
		String root = ServletActionContext.getServletContext().getRealPath("/");

		prob = probManageDao.queryOneProb(prob);
		String bccs = "";
		String bcc = "";
		// 已拒绝状态，即驳回整改只通知任务创建者
		if ("6".equals(prob.getStatus())) {
			User trackUser = userManageDao.queryUserByUserName(prob.getTrackingUser());
			bcc = trackUser.getEmail();
			if (StringUtils.isNotBlank(bcc)) {
				bccs += ";" + bcc;
			}
		} else {// 审批通过，通知tsc，sp,pdt_ld,xteam群组
			// 总部二线
			bcc = basicDataDao.querySysArg("prob.execute.mail");
			if (StringUtils.isNotBlank(bcc)) {
				bccs += ";" + bcc;
			}
			// 全体用服
			bcc = basicDataDao.querySysArg("prob.release.mail");
			if (StringUtils.isNotBlank(bcc) && prob.getVisibleRange() != 1) { // 仅搜索的公告不发送sp群组
				bccs += ";" + bcc;
			}
			// 维护经理
			bcc = basicDataDao.querySysArg("prob.xteam.mail");
			if (StringUtils.isNotBlank(bcc)) {
				bccs += ";" + bcc;
			}
			// 产品经理
			bcc = basicDataDao.querySysArg("prob.pdt_ld.mail");
			if (StringUtils.isNotBlank(bcc)) {
				bccs += ";" + bcc;
			}
		}

		this.keepRelaseEmail(prob, root, null, bccs, defaultParaMap(prob, null, 1, null));
	}

	@Override
	public List<ProbParam> queryExportProbList(Map<Object, Object> params) {
		return probManageDao.queryExportProbList(params);
	}

	@Override
	public void batchAddSoftVersion(List<Object> softVersions) {
		probManageDao.batchAddSoftVersion(softVersions);
	}

	@Override
	public List<ProbStatistic> queryProbStatisticList(ProbStatistic probStatistic, DisplayParam displayParam) {
		return probManageDao.queryProbStatisticList(probStatistic, displayParam);
	}

	@Override
	public List<ProbStatistic> queryProbStatisticListWithReport(ProbStatistic probStatistic, DisplayParam displayParam,
			List<ReportLineData> reportLineDatas) {
		return probManageDao.queryProbStatisticListWithReport(probStatistic, displayParam, reportLineDatas);
	}

	@Override
	public List<Project> queryProbStatisticProjectList(ProbStatistic probStatistic, DisplayParam displayParam) {
		return probManageDao.queryProbStatisticProjectList(probStatistic, displayParam);
	}

	@Override
	public void readLog(int probId, int status) {
		final ProbReadLog readLog = new ProbReadLog(probId, getLoginName(), status);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				probManageDao.insertProbReadLog(readLog);
			}
		});
		thread.start();
	}

	@Override
	public void insertProbReadLog(ProbReadLog probReadLog) {
		probManageDao.insertProbReadLog(probReadLog);
	}

	@Override
	public List<ProbReadLog> queryProbReadLogList(ProbReadLog probReadLog, DisplayParam displayParam) {
		UserContext userContext = UserContext.getUserContext();
		if (probReadLog == null) {
			probReadLog = new ProbReadLog();
		}
		if (!(userContext.isHasRole(MessageUtil.ROLE_ADMIN) || userContext.isHasRole(MessageUtil.ROLE_PROB_ADMIN)
				|| userContext.isHasRole(MessageUtil.ROLE_PROB_SUPPORTER))) {
			probReadLog.setReader(userContext.getUsername());
		}
		return probManageDao.queryProbReadLogList(probReadLog, displayParam);
	}

}
