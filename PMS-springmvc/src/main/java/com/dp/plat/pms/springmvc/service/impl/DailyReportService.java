package com.dp.plat.pms.springmvc.service.impl;

import java.util.Collection;
import com.dp.plat.core.service.impl.AbstractBaseService;
import java.util.Map;
import com.dp.plat.pms.springmvc.util.PermissionUtils;
import org.springframework.stereotype.Service;
import com.dp.plat.pms.springmvc.service.IDailyReportService;
import com.dp.plat.core.context.UserContext;
import java.util.HashSet;
import java.util.Set;
import com.dp.plat.pms.springmvc.vo.TaskVO;
import com.dp.plat.pms.springmvc.constant.RoleConstant;
import com.dp.plat.pms.springmvc.dao.DailyReportMapper;
import org.apache.commons.lang3.StringUtils;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.pms.springmvc.vo.DailyReportVO;
import com.dp.plat.pms.springmvc.entity.DailyReport;

/**
 *
 * Created by CodeGenerator
 */
@Service("dailyReportService")
public class DailyReportService extends AbstractBaseService<DailyReportMapper, DailyReport> implements IDailyReportService {

    @Override
    public PermissionResult checkPermission(DailyReportVO v, String... permissions) {
        if (!UserContext.checkPermission(permissions)) {
            return new PermissionResult(Boolean.FALSE, "没有权限进行该操作！");
        }
        Boolean isPermit = false;
        String permissionType = "";
        Collection<String> permissionSet = null;
        PermissionResult result = null;
        if (v != null && (v.getProjectId() != null && v.getProjectId() > 0 || v.getId() != null)) {
            // 允许访问的项目类型
            if (!UserContext.hasAnyRoles(RoleConstant.ROLE_PM_ADMIN, RoleConstant.ROLE_ADMIN)) {
                Principal user = UserContext.getCurrentPrincipal();
                String projectTypes = StringUtils.defaultString(user.getUserInfo().getCustom4(), "-1");
                v.setProjectTypes(projectTypes);
                // 非子项目管理员，添加允许访问的办事处权限
                String officeCodes = StringUtils.defaultString(user.getUserInfo().getCustom5(), "-1");
                if (!UserContext.hasRole(RoleConstant.ROLE_PM_SUB_ADMIN)) {
                    v.setOfficeCodes(officeCodes);
                    
                }
                // 添加指派的项目成员
                v.setMemberCode(user.getUserName());
            }
            Map<String, Object> permission = this.checkPermissionMap(v, permissions);
            result = new PermissionUtils().checkPermit(permission, permissions);
            if (!result.isPermit() && UserContext.checkPermission("dailyReport:*")) {
                result.setStatus(true);
                result.setPermissionType("view");
            }
        } else {
            isPermit = true;
            permissionType = "edit";
        }
        return result != null ? result : new PermissionResult(isPermit, permissionType, permissionSet);
    }

    @Override
    public Map<String, Object> checkPermissionMap(DailyReportVO v, String... permissions) {
        Map<String, Object> permissionMap;
        if (permissions != null) {
            Set<String> permissTypes = new HashSet<String>(permissions.length);
            for (String permission : permissions) {
                if (StringUtils.isNotBlank(permission)) {
                    String type = permission.split(":")[0];
                    permissTypes.add(type);
                }
            }
            permissionMap = dao.checkPermission(v, StringUtils.join(permissTypes, ":|") + ":", UserContext.getCurrentPrincipal());
        } else {
            permissionMap = dao.checkPermission(v, UserContext.getCurrentPrincipal());
        }
        // 已闭环的项目，不允许修改，只有项目管理员、区域负责人才可以重新打开
        String closedState = SystemConfig.systemVariables.getOrDefault("pm.project.closed.state", "100");
        if (closedState.equals(permissionMap.get("maxState"))) {
            permissionMap.put("all", Boolean.FALSE);
            permissionMap.put("edit", Boolean.FALSE);
        }
        return permissionMap;
    }
}
