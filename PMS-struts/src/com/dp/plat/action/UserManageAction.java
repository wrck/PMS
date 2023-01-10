package com.dp.plat.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dp.plat.context.HttpContext;
import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.Role;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.bean.UserMenu;
import com.dp.plat.exception.CustomRuntimeException;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.ProjectBatchCgMbParam;
import com.dp.plat.service.DepartmentManageService;
import com.dp.plat.service.PasswordService;
import com.dp.plat.service.UserManageService;
import com.dp.plat.util.MailUtil;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.PasswordUtil;
import com.dp.plat.util.ProjectUtils;
import com.opensymphony.xwork2.Preparable;

public class UserManageAction extends BaseAction implements Preparable {
	private static final long serialVersionUID = 1L;
	private DisplayParam displayParam;
	private UserManageService userManageService;
	private DepartmentManageService departmentManageService;
	private List<User> userlist;
	private User user;
	private Role role;
	private List<Role> rolelist;//用户角色集合
	private List<UserMenu> userMenuList;//系统菜单集合
	private String usermenuids;//用户具有的菜单id串
	private List<Department> departments;
	private List<Department> departmentPowers;//用户数据权限
	private String username;
	private int result;
	
	private String newMemberCode;
	private String changeType;//用户角色变更类型，service、program、both，用于更新项目的服务经理、项目经理
	
	
	@Override
    public void prepare() throws Exception {
	    if (!UserContext.getUserContext().isHasAnyRole(MessageUtil.ROLE_ADMIN, MessageUtil.ROLE_ENGINEEMANAGER, MessageUtil.ROLE_ENGINEEMANAGER_LEADER)) {
	        setErrmsg("没有访问权限！");
	        throw new CustomRuntimeException(getErrmsg());
	    }
    }

	// 用户管理列表
	public String execute() throws Exception {
		if(displayParam == null){
			displayParam = new DisplayParam();
		}
		if(user == null){
			user = new User();
		}
		//查询
		rolelist = userManageService.queryRolelist();
		Map<String, String> roleMap = dealWith(rolelist);
		displayParam.getParam();
		userlist = userManageService.queryUserList(displayParam, user);
		userlist = dealWith(userlist , roleMap);
		String roleIds = user.getRoleids();
		if(roleIds != null&&!"".equals(roleIds)){
			user.setRoleids(roleIds.substring(1, roleIds.length()-1));
		}
		return SUCCESS;
	}
	
    private List<User> dealWith(List<User> userlist2, Map<String, String> roleMap) {
        StringBuilder roleName = null;
        for (User user : userlist2) {
            roleName = new StringBuilder();
            for (String roleId : roleMap.keySet()) {
                if (user.getRoleids() != null && user.getRoleids().indexOf(roleId) != -1) {
                    roleName.append(roleMap.get(roleId));
                    roleName.append(",");
                }
            }
            if (roleName.length() > 0) {
                roleName.deleteCharAt(roleName.length() - 1);
            }
            user.setRoleName(roleName.toString());
        }
        return userlist2;
    }

    private Map<String, String> dealWith(List<Role> rolelist2) {
        Map<String, String> map = new HashMap<String, String>();
        for (Role role : rolelist2) {
            map.put(";" + role.getId() + ";", role.getRoleName());
        }
        return map;
    }
    
	//增加用户数据
	public String add() {
		if(user==null){
			rolelist = userManageService.queryRolelist();
			userMenuList = userManageService.queryUserMenuList();
			Department department = new Department();
			departments = departmentManageService.queryAllDepartments(department);
 			department.setIsparam(1);
 			departmentPowers = departmentManageService.queryAllDepartments(department);
			return INPUT;
		}else{
			if(checkSubmitData(user.getUsername())||checkSubmitData(user.getRealName())||checkSubmitData(user.getEmail())||checkSubmitData(usermenuids)||checkSubmitData(user.getDefaultPage())){
				setErrmsg("填写错误");
				return ERROR;
			}
			String randomPassword = PasswordUtil.generatePass();
			user.setPassword(PasswordUtil.encryptMD5Password(randomPassword, user.getUsername()));
            userManageService.addUserInfo(user, usermenuids);
            
            if (user.getId() > 0 && !UserContext.getUserContext().isCas()) {
                Map<String, Object> context = new HashMap<String, Object>();
                context.put("templateCode", "userAddOrRestPwdMailInfo");
                context.put("title", "帐号已开通");
                context.put("realName", user.getRealName());
                context.put("userName", user.getUsername());
                context.put("randomPassword", randomPassword);
                context.put("content", context.get("title"));
                context.put("tos", user.getEmail());
                context.put("beforeSplit", "${");
                context.put("afterSplit", "}");
                MailUtil.keepMailWithTemplate(context, true);
//            NotificationTemplateUtil.keepMail(context);
            }
		}
		
		return SUCCESS;
	}
	
	/**
	 * 检查用户账号是否重复
	 * @return
	 */
	public String checkUsername(){
		result = userManageService.queryUserSizeByUserName(username);
		return SUCCESS;
	}
	//更新用户数据
	public String edit() {
		if(user.getId()!=0 && user.getUsername()==null){//查询原有用户数据
			rolelist = userManageService.queryRolelist();
			userMenuList = userManageService.queryUserMenuList();
			user = userManageService.queryUserByUserId(user.getId());
			usermenuids = userManageService.queryUserMenuidsByUserid(user.getId());
			Department department = new Department();
			departments = departmentManageService.queryAllDepartments(department);
			department.setIsparam(1);
 			departmentPowers = departmentManageService.queryAllDepartments(department);
			return INPUT;
		}else if(user.getId()!=0&&user.getUsername()!=null){
			if(checkSubmitData(user.getUsername())||checkSubmitData(user.getRealName())||checkSubmitData(user.getEmail())||checkSubmitData(usermenuids)||checkSubmitData(user.getDefaultPage())){
				setErrmsg("填写错误");
				return ERROR;
			}
			if(user.getStatus()==0){
				user.setEffectiveTo(new Date());
			}
			// 是否需要变更项目的服务经理和项目经理
			if(StringUtils.isNotBlank(changeType)){
				ProjectBatchCgMbParam batchCgMb = new ProjectBatchCgMbParam();
				batchCgMb.setChangeType(changeType);
				batchCgMb.setNewMemberName(newMemberCode);
				batchCgMb.setDpName(user.getDpName());
				batchCgMb.setOldMemberCode(user.getUsername());
				batchCgMb.setNewMemberCode(newMemberCode.split("-")[0]);
				System.out.println(batchCgMb);
				String result = ProjectUtils.updateServiceAndProgramMember(batchCgMb);
				System.out.println(result);
			}
			
			userManageService.updateUserInfo(user, usermenuids);
		}
		
		return SUCCESS;
	}

	public String submit() {
		
		return SUCCESS;
	}


	/**
	 * 用户密码重置
	 * 
	 * @return
	 */
	public String pwdreset() {
		// 密码重置
	    String randomPassword = PasswordUtil.generatePass();
	    user = userManageService.queryUserByUserId(user.getId());
        user.setPassword(PasswordUtil.encryptMD5Password(randomPassword, user.getUsername()));
        user.setPwdoverdue(new Date());
        userManageService.updatepwdbyuser(user);
        
        // 邮件通知用户
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("templateCode", "userAddOrRestPwdMailInfo");
        context.put("title", "帐号密码已重置");
        context.put("realName", user.getRealName());
        context.put("userName", user.getUsername());
        context.put("randomPassword", randomPassword);
        context.put("content", context.get("title"));
        context.put("tos", user.getEmail());
        context.put("beforeSplit", "${");
        context.put("afterSplit", "}");
        MailUtil.keepMailWithTemplate(context, true);
        
        
        PasswordService passwordService = SpringContext.getBean("passwordService", PasswordService.class);
        passwordService.forcedOffline(user.getUsername());
        result = 1;
		return SUCCESS;
	}

	public Map<String, String> getStatusList() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("0", HttpContext.getMessage("sys.wraper.inactive"));
		result.put("1", HttpContext.getMessage("sys.wraper.active"));
		return result;
	}
	
	public String findUser(){
		user = userManageService.queryUserByUserName(username);
		return SUCCESS;
	}
	
	public boolean checkSubmitData(String submitData){
		return submitData==null||submitData.equals("")||submitData.contains(" ");
	}

	public void setUserManageService(UserManageService userManageService) {
		this.userManageService = userManageService;
	}

	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}

	public List<User> getUserlist() {
		return userlist;
	}

	public void setUserlist(List<User> userlist) {
		this.userlist = userlist;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<Role> getRolelist() {
		return rolelist;
	}

	public void setRolelist(List<Role> rolelist) {
		this.rolelist = rolelist;
	}
	public List<UserMenu> getUserMenuList() {
		return userMenuList;
	}
	public void setUserMenuList(List<UserMenu> userMenuList) {
		this.userMenuList = userMenuList;
	}
	public String getUsermenuids() {
		return usermenuids;
	}
	public void setUsermenuids(String usermenuids) {
		this.usermenuids = usermenuids;
	}
	public List<Department> getDepartments() {
		return departments;
	}
	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public void setDepartmentManageService(
			DepartmentManageService departmentManageService) {
		this.departmentManageService = departmentManageService;
	}

	public List<Department> getDepartmentPowers() {
		return departmentPowers;
	}
	public void setDepartmentPowers(List<Department> departmentPowers) {
		this.departmentPowers = departmentPowers;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * @return the newMemberCode
	 */
	public String getNewMemberCode() {
		return newMemberCode;
	}
	/**
	 * @param newMemberCode the newMemberCode to set
	 */
	public void setNewMemberCode(String newMemberCode) {
		this.newMemberCode = newMemberCode;
	}
	/**
	 * @return the changeType
	 */
	public String getChangeType() {
		return changeType;
	}
	/**
	 * @param changeType the changeType to set
	 */
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}
}
