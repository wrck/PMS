package com.dp.plat.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.context.HttpContext;
import com.dp.plat.dao.LoginDao;
import com.dp.plat.data.bean.Role;
import com.dp.plat.data.bean.RoleMenuPower;
import com.dp.plat.data.bean.User;
import com.dp.plat.param.LoginParam;
import com.dp.plat.util.StringEscUtil;
import com.dp.plat.util.UserUtil;

public class LoginServiceImpl extends BaseServiceImpl implements LoginService {
	
	private LoginDao loginDao;
	
	@Override
	public boolean login(LoginParam loginParam, String ip) {
		boolean result = false;
		try {
			String username = loginParam.getUsername();
			User user = loginDao.querUser(username);
			//判断用户密码是否正确
			String envirment = loginDao.querySysArg("sys.envirment.argu");
			String pwd = loginParam.getPassword();
			if(!"1".equals(envirment)){// 测试环境忽略密码
				pwd = user.getPassword();
			}
			if(user != null && user.getPassword().equalsIgnoreCase(pwd)){
				Map<String,Integer> permissionMap =  loginDao.queryUserMenuMap(user.getId());
				if ("-1".equals(user.getAreapower())) {
					user.setAreapower(user.getDpNo());
				} else if (!user.getAreapower().contains(user.getDpNo())) {
					user.setAreapower(user.getAreapower() + "," + user.getDpNo());
				}
				String areaPower = processAreaPower(user.getAreapower());
				user.setAreapower(areaPower);
				
				Role role=new Role();
				Map<Integer, Map<String, Integer>>roleMenuPowerMap=new HashMap<Integer, Map<String,Integer>>();
				for(String roleId : user.getRoleids().split(",") ){
					role.setId(Integer.parseInt(roleId.substring(1, roleId.length()-1)));
					List<RoleMenuPower> roleMenuPowerList=loginDao.queryRoleMenuPowerList(role);
					for(RoleMenuPower roleMenuPower:roleMenuPowerList){
						Map<String, Integer>menuPowerMap=new HashMap<String, Integer>();
						String[] menuPowerArr=roleMenuPower.getMenuPower().split(",");
						for(String str:menuPowerArr){//8：增加 	1：删除 	4：查找 	2：更新
							if(str.equals("8")){
								menuPowerMap.put("insert", 1);
							}
							if(str.equals("1")){
								menuPowerMap.put("delete", 1);
							}
							if(str.equals("4")){
								menuPowerMap.put("select", 1);
							}
							if(str.equals("2")){
								menuPowerMap.put("update", 1);
							}
						}
						
						if(!menuPowerMap.containsKey("insert")){
							menuPowerMap.put("insert", 0);
						}
						if(!menuPowerMap.containsKey("delete")){
							menuPowerMap.put("delete", 0);
						}
						if(!menuPowerMap.containsKey("select")){
							menuPowerMap.put("select", 0);
						}
						if(!menuPowerMap.containsKey("update")){
							menuPowerMap.put("update", 0);
						}
						roleMenuPowerMap.put(roleMenuPower.getMenuId(), menuPowerMap);
						
					}
				}
				
				String defaultPage = loginDao.queryUserDefaultPage(user.getId());
				getUserContext().login(user, ip, permissionMap,defaultPage,roleMenuPowerMap);
				//记录日志
				log("登录");
				return true;
			}
			addErrmsg(StringEscUtil.getText("sys.login.emperror"));
		} catch (Exception e) {
			e.printStackTrace();
			addErrmsg("Login Error!");
			result = false;
		}
		return result;
	}

	@Override
	public boolean loginCas(LoginParam loginParam, String ip){
		boolean result = false;
		try {
			String username = loginParam.getUsername();
			User user = loginDao.querUser(username);
			if(user==null){
				return false;				
			}else{
				if ("-1".equals(user.getAreapower())) {
					user.setAreapower(user.getDpNo());
				} else if (!user.getAreapower().contains(user.getDpNo())) {
					user.setAreapower(user.getAreapower() + "," + user.getDpNo());
				}
				String areaPower = processAreaPower(user.getAreapower());
				user.setAreapower(areaPower);
				
				Map<String,Integer> permissionMap =  loginDao.queryUserMenuMap(user.getId());
				
				Role role=new Role();
				Map<Integer, Map<String, Integer>>roleMenuPowerMap=new HashMap<Integer, Map<String,Integer>>();
				for(String roleId : user.getRoleids().split(",") ){
					role.setId(Integer.parseInt(roleId.substring(1, roleId.length()-1)));
					List<RoleMenuPower> roleMenuPowerList=loginDao.queryRoleMenuPowerList(role);
					for(RoleMenuPower roleMenuPower:roleMenuPowerList){
						Map<String, Integer>menuPowerMap=new HashMap<String, Integer>();
						String[] menuPowerArr=roleMenuPower.getMenuPower().split(",");
						for(String str:menuPowerArr){//8：增加 	1：删除 	4：查找 	2：更新
							if(str.equals("8")){
								menuPowerMap.put("insert", 1);
							}
							if(str.equals("1")){
								menuPowerMap.put("delete", 1);
							}
							if(str.equals("4")){
								menuPowerMap.put("select", 1);
							}
							if(str.equals("2")){
								menuPowerMap.put("update", 1);
							}
						}
						
						if(!menuPowerMap.containsKey("insert")){
							menuPowerMap.put("insert", 0);
						}
						if(!menuPowerMap.containsKey("delete")){
							menuPowerMap.put("delete", 0);
						}
						if(!menuPowerMap.containsKey("select")){
							menuPowerMap.put("select", 0);
						}
						if(!menuPowerMap.containsKey("update")){
							menuPowerMap.put("update", 0);
						}
						roleMenuPowerMap.put(roleMenuPower.getMenuId(), menuPowerMap);
						
					}
				}
				
				String defaultPage = loginDao.queryUserDefaultPage(user.getId());
				getUserContext().login(user, ip, permissionMap,defaultPage,roleMenuPowerMap);
				//记录日志
				log("登录");
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			addErrmsg("Login Error!");
			result = false;
		}
		return result;
	}
	@Override
	public void logout() {
		try {
			HttpContext.invalidateSession();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public LoginDao getLoginDao() {
		return loginDao;
	}

	public void setLoginDao(LoginDao loginDao) {
		this.loginDao = loginDao;
	}

	@Override
	public String querySysArg(String code) {
		return loginDao.querySysArg(code);
	}

	/**
	 * 市场和用服相同办事处的权限进行补充
	 * @param areaPower
	 * @return 补充后的areaPower
	 */
	private String processAreaPower(String areaPower) {
//		if (StringUtils.isNotBlank(areaPower)) {
//			Set<String> newAreaList = new HashSet<>();
//			List<String> areaList = Arrays.asList(StringUtils.split(areaPower, ","));
//			newAreaList.addAll(areaList);
//			for (String area : areaList) {
//				String newArea = null;
//				if (area.length() > 6) {
//					area = area.substring(0, 6);
//				}
//				if (area.startsWith("16")) {
//					newArea = area.replaceFirst("16", "31");
//				} else if (area.startsWith("31")) {
//					newArea = area.replaceFirst("31", "16");
//				}
//				if (StringUtils.isNotBlank(newArea) && !newAreaList.contains(newArea)) {
//					newAreaList.add(newArea);
//				}
//			}
//			areaPower = StringUtils.join(newAreaList, ",");
//		}
//		return areaPower;
		return UserUtil.processAreaPower(areaPower);
	}
}
