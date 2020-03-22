/**
 * 
 */
package com.dp.plat.core.context;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;

import com.dp.plat.core.pojo.User;
import com.dp.plat.core.realms.Principal;

/**
 * @author w02611
 *
 */
public class UserContext {

	/**
	 * 获取当前用户的登录信息
	 * 
	 * @return
	 */
	public static Principal getCurrentPrincipal() {
		Principal principal = (Principal) SecurityUtils.getSubject().getPrincipal();
		return principal;
	}

	/**
	 * 获取当前用户信息
	 * @return
	 */
	public static User getCurrentUser() {
		Principal principal = (Principal) SecurityUtils.getSubject().getPrincipal();
		User user = new User();
		if (principal == null) {
			return user;
		}
		try {
			Field[] fields = User.class.getDeclaredFields();
			for (Field field : fields) {
				try {
					String fieldName = field.getName();
					String methodSubName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					Method method = principal.getClass().getDeclaredMethod("get" + methodSubName);
					Object value = method.invoke(principal);
					if (value == null) {
						continue;
					}
					
					method = User.class.getDeclaredMethod("set" + methodSubName, field.getType());
					method.invoke(user, value);
				} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					continue;
				}
			}
		} catch (SecurityException e) {
			user.setUserId(principal.getUserId());
			user.setUserName(principal.getUserName());
			user.setStatus(principal.getStatus());
			e.printStackTrace();
		}
		return user;
	}
	
	/**
	 * 判断当前用户是否有某个角色
	 * 
	 * @return
	 */
	public static boolean hasRole(String roleIdentifier) {
		PrincipalCollection principalCollection =  SecurityUtils.getSubject().getPrincipals();
		return SecurityUtils.getSecurityManager().hasRole(principalCollection, roleIdentifier);
	}
	
	/**
	 * 判断当前用户是否有某些角色中的一个
	 * 
	 * @return
	 */
	public static boolean hasAnyRoles(List<String> roleIdentifiers) {
		PrincipalCollection principalCollection =  SecurityUtils.getSubject().getPrincipals();
		boolean[] bs = SecurityUtils.getSecurityManager().hasRoles(principalCollection, roleIdentifiers);
		boolean hasAnyRoles = false;
		for (boolean b : bs) {
			hasAnyRoles = b || hasAnyRoles;
			if (hasAnyRoles) {
				break;
			}
		}
		return hasAnyRoles;
	}
	
	/**
	 * 判断当前用户是否有所有指定角色
	 * 
	 * @return
	 */
	public static boolean hasAllRoles(List<String> roleIdentifiers) {
		PrincipalCollection principalCollection =  SecurityUtils.getSubject().getPrincipals();
		return SecurityUtils.getSecurityManager().hasAllRoles(principalCollection, roleIdentifiers);
	}
}
