package com.dp.plat.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dp.plat.context.HttpContext;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.MailSenderInfo;
import com.dp.plat.data.bean.Role;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.bean.UserMenu;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.ProjectBatchCgMbParam;
import com.dp.plat.service.DepartmentManageService;
import com.dp.plat.service.UserManageService;
import com.dp.plat.util.ProjectUtils;
import com.dp.plat.util.test.SimpleMailSender;

public class UserManageAction extends BaseAction {
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
	
	private List<User> dealWith(List<User> userlist2,
			Map<String, String> roleMap) {
		StringBuilder roleName = null;
		for(User user : userlist2){
			roleName = new StringBuilder();
			for(String roleId : roleMap.keySet()){
				if(user.getRoleids() != null && user.getRoleids().indexOf(roleId) != -1){
					roleName.append(roleMap.get(roleId));
					roleName.append(",");
				}
			}
			if(roleName.length()>0){
				roleName.deleteCharAt(roleName.length()-1);
			}
			user.setRoleName(roleName.toString());
		}
		return userlist2;
	}
	private Map<String, String> dealWith(List<Role> rolelist2) {
		Map<String, String> map = new HashMap<String, String>();
		for(Role role :rolelist2){
			map.put(";"+role.getId()+";", role.getRoleName());
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
			userManageService.addUserInfo(user , usermenuids);
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
		userManageService.updatepwdbyuser(user);
		// 邮件通知用户
		user = userManageService.queryUserByUserName(user.getUsername());
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("pms@dptech.com");
		mailInfo.setPassword("2Bk29UamZr");
		mailInfo.setFromAddress("pms@dptech.com");
		mailInfo.setToAddress(user.getEmail());
		mailInfo.setSubject("PMS系统账号密码重置");
		mailInfo.setContent("尊敬的用户 " + user.getRealName()
				+ " 您好：<br><br>	您的系统密码已重置！" + "<br>您的账号：" + user.getUsername()
				+ "。<br>您的密码为：1q2w3e4r"
				+ "。<br>请登陆后立即修改密码，谢谢！ 该邮件为系统自动发出，请勿回复！  IT部");
		SimpleMailSender.sendHtmlMail(mailInfo);// 发送html格式
		java.lang.System.out.println("发送成功！");

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
