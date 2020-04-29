package com.dp.plat.pms.springmvc.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.DispatchType;
import com.dp.plat.pms.springmvc.dao.DispatchProjectMapper;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.vo.DispatchVO;
import com.dp.plat.pms.springmvc.vo.ProjectVO;

/**
 *
 * Created by CodeGenerator
 */
@Service("dispatchProjectService")
public class DispatchProjectService extends AbstractBaseService<DispatchProjectMapper, DispatchProject>
		implements IDispatchProjectService {
	
	@Autowired
	private IProjectHeaderService projectHeaderService;

	@Override
	public void insertOrUpdateSelective(DispatchProject dispatch) {
		dao.insertOrUpdateSelective(dispatch);
	}

	@Override
	@Transactional
	public void dispatchSubmit(Integer id, DispatchVO dispatch) {
		String dispatchSeq = dispatch.getDispatchSeq();
		String facilitatorCode = dispatch.getFacilitatorCode();
		String dispatchNo = dispatch.getDispatchNo();
		Date dispatchTime = new Date();
		if (StringUtils.isBlank(dispatchSeq)) {
			dispatchSeq = generateDispatchSeq(facilitatorCode);
		}
		if (DispatchType.FRAMEWORK_AGREEMENT.equals(dispatch.getType()) && StringUtils.isBlank(dispatchNo)) {
			dispatchNo = generateDispatchNo(dispatchTime, dispatchSeq);
		}
		DispatchProject temp = new DispatchProject();
		temp.setId(id);
		temp.setDispatchTime(dispatchTime);
		temp.setDispatchSeq(dispatchSeq);
		temp.setDispatchNo(dispatchNo);
		// 派单执行中
		temp.setState(50);
		temp.setDispatched(true);
		this.updateByPrimaryKeySelective(temp);
	}
	
	/**
	 * 生成派单编号
	 * @param facilitatorCode 
	 * @return dispatchSeq
	 */
	@Override
	public String generateDispatchSeq(String facilitatorCode) {
		if (StringUtils.isBlank(facilitatorCode)) {
			return null;
		}
		// 查询服务商的项目派单顺序
		DispatchProject temp = new DispatchProject();
		temp.setDispatched(true);
		temp.setFacilitatorCode(facilitatorCode);
		long count = this.countBySelective(temp) + 1;
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String seqFormat = SystemConfig.systemVariables.getOrDefault("pm.project.disptachSeq.format", "%02d");
//		String seqFormat = SystemConfig.systemVariables.getOrDefault("pm.project.disptachSeq.format", "{100:'%02d', 256:'%02x'}");
//		Map<Integer, String> seqFormats = (Map<Integer, String>) JSON.parse(seqFormat);
//		Integer minInt = Integer.MAX_VALUE;
//		for (Entry<Integer, String> format : seqFormats.entrySet()) {
//			Integer key = format.getKey();
//			if (Long.valueOf(count).intValue() < key && key < minInt) {
//				minInt = key;
//				seqFormat = format.getValue();
//			}
//		}
		Object[] seqs = new Object[] { year, facilitatorCode, String.format(seqFormat, count) };
		String dispatchSeq = StringUtils.join(seqs, "-");
		return dispatchSeq;
	}
	
	/**
	 * 生成框架协议派单合同
	 * @param dispatchTime 
	 * @param dispatchSeq 
	 * @return dispatchNo
	 */
	@Override
	public String generateDispatchNo(Date dispatchTime, String dispatchSeq) {
		String dispatchTimeStr = DateFormatUtils.format(dispatchTime, "yyyyMMdd");
		String dispatchNo = ProjectConstant.DispatchNOPrefix.AF + dispatchTimeStr + dispatchSeq.replaceAll("-", "");
		return dispatchNo;
	}

	@Override
	public List<DispatchVO> selectDispatchVOWithAmountBySelective(DispatchVO dispatchProject) {
		return dao.selectDispatchVOWithAmountBySelective(dispatchProject);
	}

	@Override
	public List<DispatchVO> selectDispatchVOWithAmountBySelectivePageable(PageParam<Object> pageParam) {
		return dao.selectDispatchVOWithAmountBySelectivePageable(pageParam);
	}

	@Override
	public PermissionResult checkPermission(DispatchVO v, String... permissions) {
		if (!UserContext.checkPermission(permissions)) {
			return new PermissionResult(Boolean.FALSE, "没有权限进行该操作！");
		}
		Boolean isPermit = false;
		String permissionType = "";
		if (!UserContext.checkPermission("project:*") && v != null) {
			ProjectVO project = new ProjectVO();
			project.setProjectId(v.getProjectId());
			Map<String, Boolean> permission = projectHeaderService.checkPermission(project);;
			Boolean allPerm = permission.get("all");
			if (Boolean.TRUE.equals(allPerm)) {
				isPermit = true;
				permissionType = "all";
			} else {
				String perms = StringUtils.join(permissions, ",");
				if (Boolean.TRUE.equals(permission.get("edit")) && perms.matches(".*dispatch:(add|edit|delete|upload|import|list|detail)\\b,?.*")) {
					isPermit = true;
					permissionType = "edit";
				}
				if (Boolean.TRUE.equals(permission.get("view")) && perms.matches(".*dispatch:(list|detail)\\b,?.*")) {
					isPermit = true;
					permissionType = "view";
				}
			}
		} else {
			isPermit = true;
			permissionType= "all";
		}
		return new PermissionResult(isPermit, null, permissionType);
	}
	
	
	
}
