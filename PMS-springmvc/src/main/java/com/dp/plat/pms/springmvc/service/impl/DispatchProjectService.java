package com.dp.plat.pms.springmvc.service.impl;

import com.dp.plat.pms.springmvc.vo.ProjectVO;
import java.util.Date;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import org.apache.commons.lang3.time.DateFormatUtils;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import org.springframework.stereotype.Service;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_ADMIN;
import java.util.Calendar;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.DispatchType;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.core.config.SystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_SUB_ADMIN;
import java.util.List;
import com.dp.plat.core.service.impl.AbstractBaseService;
import java.util.Map;
import com.dp.plat.pms.springmvc.util.PermissionUtils;
import static com.dp.plat.core.param.RoleConstant.ROLE_ADMIN;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.pms.springmvc.constant.RoleConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.dp.plat.pms.springmvc.vo.DispatchVO;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.dao.DispatchProjectMapper;

/**
 *
 * Created by CodeGenerator
 */
@Service("dispatchProjectService")
public class DispatchProjectService extends AbstractBaseService<DispatchProjectMapper, DispatchProject> implements IDispatchProjectService {

    @Autowired
    private IProjectHeaderService projectHeaderService;

    @Override
    public void insertOrUpdateSelective(DispatchProject dispatch) {
        if (dispatch.getId() != null) {
            this.updateByPrimaryKeySelective(dispatch);
        } else {
            this.insertSelective(dispatch);
        }
    //		dao.insertOrUpdateSelective(dispatch);
    }

    @Override
    @Transactional
    public void dispatchSubmit(Integer id, DispatchVO dispatch) {
        String dispatchType = dispatch.getType();
        if (StringUtils.isBlank(dispatchType)) {
            throw new RuntimeException("请先选择派单类型！");
        }
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
	 * 
	 * @param facilitatorCode
	 * @return dispatchSeq
	 */
    @Override
    public String generateDispatchSeq(String facilitatorCode) {
        if (StringUtils.isBlank(facilitatorCode)) {
            return null;
        }
        // 查询服务商的项目派单顺序
        DispatchVO temp = new DispatchVO();
        temp.setDispatched(true);
        temp.setFacilitatorCode(facilitatorCode);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        temp.setDispatchYear(year);
        long count = this.countBySelective(temp) + 1;
        String seqFormat = SystemConfig.systemVariables.getOrDefault("pm.project.disptachSeq.format", "%02d");
        // String seqFormat =
        // SystemConfig.systemVariables.getOrDefault("pm.project.disptachSeq.format",
        // "{100:'%02d', 256:'%02x'}");
        // Map<Integer, String> seqFormats = (Map<Integer, String>)
        // JSON.parse(seqFormat);
        // Integer minInt = Integer.MAX_VALUE;
        // for (Entry<Integer, String> format : seqFormats.entrySet()) {
        // Integer key = format.getKey();
        // if (Long.valueOf(count).intValue() < key && key < minInt) {
        // minInt = key;
        // seqFormat = format.getValue();
        // }
        // }
        Object[] seqs = new Object[] { year, facilitatorCode, String.format(seqFormat, count) };
        String dispatchSeq = StringUtils.join(seqs, "-");
        return dispatchSeq;
    }

    /**
	 * 生成框架协议派单合同
	 * 
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
        PermissionResult result = null;
        if (!UserContext.checkPermission("project:*") && v != null) {
            ProjectVO project = new ProjectVO();
            project.setProjectId(v.getProjectId());
            project.setProjectIds(v.getProjectIds());
//            Map<String, Object> permission = projectHeaderService.checkPermissionMap(project, permissions);
//            PermissionResult checkPermit = new PermissionUtils("dispatch:", new String[] { ROLE_ADMIN, ROLE_PM_ADMIN, ROLE_PM_SUB_ADMIN }).checkPermit(permission, permissions);
            PermissionResult projectPermit = projectHeaderService.checkPermission(project, permissions);
			String[] allPermitRoles = PermissionUtils.getRetainAllRoles(new String[] { ROLE_ADMIN, ROLE_PM_ADMIN, ROLE_PM_SUB_ADMIN }, projectPermit.getRoles());
			PermissionResult checkPermit = new PermissionUtils("dispatch:" , allPermitRoles)
					.checkPermit(projectPermit.getPermissionMap(), permissions);
            isPermit = checkPermit.isPermit();
            permissionType = checkPermit.getPermissionType();
            result = checkPermit;
        } else {
            isPermit = true;
            permissionType = "all";
        }
        return result != null ? result : new PermissionResult(isPermit, null, permissionType);
    }
}
