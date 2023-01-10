package com.dp.plat.action;

import java.util.Date;
import java.util.List;

import com.dp.plat.data.bean.Role;
import com.dp.plat.data.bean.RoleMenuPower;
import com.dp.plat.data.bean.UserMenu;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.service.RoleManageService;
import com.dp.plat.service.UserManageService;

/**
 * 角色管理
 * @author admin
 *
 */
public class RoleManageAction extends BaseAction{
	private static final long serialVersionUID = 1L;
	private Role role;
	private List<Role>roleList;
	private DisplayParam displayParam;
	private RoleManageService roleManageService;
	private UserManageService userManageService;
	private String errorMessage;
	private List<UserMenu> userMenuList;//系统菜单集合
	private List<RoleMenuPower>rolemenuidList;
	
	@Override
	public String execute() throws Exception {
		if(displayParam == null){
			displayParam = new DisplayParam();
		}
		if(roleList!=null){
			roleList.clear();
		}
		if(role==null){
			role=new Role();
		}
		displayParam.getParam();
		roleList=roleManageService.queryRoleList(displayParam, role);
		return SUCCESS;
	}
	
	public String add() throws Exception {
		userMenuList = userManageService.queryUserMenuList();
		return INPUT;
	}
	
	public String addSubmit() throws Exception {//封装一个提交字段格式检查的工具类
		//提交数据的验证
		if(rolemenuidList==null||rolemenuidList.size()==0){
			setErrmsg("角色菜单权限填写错误");
			return ERROR;
		}
		if(role==null||role.getRoleName()==null||role.getRoleName().equals("")||role.getRoleName().contains(" ")){
			setErrmsg("角色名称填写错误");
			return ERROR;
		}
		//默认页面的添加
		role.setDefaultPage("module/Welcome1.action");	//最好不要写死，后期需要调整
		if(role.getStatus()==0){
			role.setEffectiveTo(new Date());
		}
		int submitId=roleManageService.addRoleSubmit(role,rolemenuidList);
		if(submitId>0){
			return SUCCESS;
		}else{
			return ERROR;
		}
		
	}
	
	public String edit() throws Exception {  
		if(role==null||role.getId()==0){
			return ERROR;
		}
		if(displayParam == null){
			displayParam = new DisplayParam();
		}
		if(roleList!=null){
			roleList.clear();
		}
		if(rolemenuidList!=null){
			rolemenuidList.clear();
		}
		displayParam.getParam();
		roleList=roleManageService.queryRoleList(displayParam, role);
		rolemenuidList=roleManageService.queryRoleMenuPowerList(role);
        if (roleList.get(0) == null/* ||rolemenuidList==null||rolemenuidList.size()==0 */){
			return ERROR;
		}else{
			role=roleList.get(0);
		}
		userMenuList = userManageService.queryUserMenuList();
		return INPUT;
	}
	
	public String editSubmit() throws Exception {
		if(rolemenuidList==null||rolemenuidList.size()==0){
			setErrmsg("角色菜单权限填写错误");
			return ERROR;
		}
		if(role==null||role.getId()==0){
			return ERROR;
		}
		//提交数据的验证
		if(role.getRoleName()==null||role.getRoleName().equals("")||role.getRoleName().contains(" ")){
			setErrmsg("角色名称填写错误");
			return ERROR;
		}
		//默认页面的添加
		role.setDefaultPage("module/Welcome1.action");	//最好不要写死，后期需要调整
		if(role.getStatus()==0){
			role.setEffectiveTo(new Date());
		}
		int result=roleManageService.updateRoleSubmit(role,rolemenuidList);
		if(result<=0){
			return ERROR;
		}else{
			return SUCCESS;
		}
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}
	
	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}

	public RoleManageService getRoleManageService() {
		return roleManageService;
	}

	public void setRoleManageService(RoleManageService roleManageService) {
		this.roleManageService = roleManageService;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public UserManageService getUserManageService() {
		return userManageService;
	}

	public void setUserManageService(UserManageService userManageService) {
		this.userManageService = userManageService;
	}

	public List<UserMenu> getUserMenuList() {
		return userMenuList;
	}

	public void setUserMenuList(List<UserMenu> userMenuList) {
		this.userMenuList = userMenuList;
	}

	public List<RoleMenuPower> getRolemenuidList() {
		return rolemenuidList;
	}

	public void setRolemenuidList(List<RoleMenuPower> rolemenuidList) {
		this.rolemenuidList = rolemenuidList;
	}

	
	
}
