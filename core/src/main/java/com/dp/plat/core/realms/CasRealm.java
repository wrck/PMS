package com.dp.plat.core.realms;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasAuthenticationException;
import org.apache.shiro.cas.CasToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.pojo.Menu;
import com.dp.plat.core.pojo.Role;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.service.IRoleService;
import com.dp.plat.core.service.IShiroService;
import com.dp.plat.core.util.MenuUtil;

public class CasRealm extends org.apache.shiro.cas.CasRealm {

	@Resource
	private IShiroService shiroService;
	
	@Resource
	private IRoleService roleService;

	/**
	 * 用户身份验证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
			throws AuthenticationException {
		CasToken casToken = (CasToken) authenticationToken;
        if (authenticationToken == null) {
            return null;
        }
        
        String ticket = (String)casToken.getCredentials();
        if (!StringUtils.hasText(ticket)) {
            return null;
        }
        
        TicketValidator ticketValidator = ensureTicketValidator();

        try {
            // contact CAS server to validate service ticket
            Assertion casAssertion = ticketValidator.validate(ticket, getCasService());
            // get principal, user id and attributes
            AttributePrincipal casPrincipal = casAssertion.getPrincipal();
            String userId = casPrincipal.getName();
            Map<String, Object> attributes = casPrincipal.getAttributes();
            // refresh authentication token (user id + remember me)
            casToken.setUserId(userId);
            String rememberMeAttributeName = getRememberMeAttributeName();
            String rememberMeStringValue = (String)attributes.get(rememberMeAttributeName);
            boolean isRemembered = rememberMeStringValue != null && Boolean.parseBoolean(rememberMeStringValue);
            if (isRemembered) {
                casToken.setRememberMe(true);
            }
            // create simple authentication info
            User user = shiroService.queryUserByName(userId);
            Principal principal = new Principal(user);
            List<Object> principals = CollectionUtils.asList(principal, attributes);
            PrincipalCollection principalCollection = new SimplePrincipalCollection(principals, getName());
            return new SimpleAuthenticationInfo(principalCollection, ticket);
        } catch (TicketValidationException e) { 
            throw new CasAuthenticationException("Unable to validate ticket [" + ticket + "]", e);
        }
	}

	/**
	 * 查询用户权限，并进行授权操作
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// 1. 从 PrincipalCollection 中来获取登录用户的信息
		Principal principal = (Principal) principals.getPrimaryPrincipal();

		// 2.1查询用户角色
//		Set<String> roles = shiroService.queryUserRoleByName(principal.getUserName());
		Set<String> roles = shiroService.queryUserRoleByNameAndCompId(principal.getUserName(), principal.getCompId());
		Role maxRole = roleService.selectRoleByRoleName(roles.iterator().next());
		
		// 2.2查询用户权限字符串集合
//		Set<String> permissions = shiroService.queryPermissionByUsername(principal.getUserName());
		Set<String> permissions = shiroService.queryPermissionByUsernameAndCompId(principal.getUserName(), principal.getCompId());
		
		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(principal.getUserId());
		userInfo.setCompID(principal.getCompId());
		
		HttpServletRequest httpRequest = HttpContext.getCurrentRequest();
		if (httpRequest != null) {
			if (org.apache.commons.lang.StringUtils.isNotBlank(httpRequest.getContextPath())) {
				String menus = principal.getMenus();
				if (!menus.contains("href='" + httpRequest.getContextPath())) {
					List<Menu> nodes = shiroService.queryUserMenuByUserIdAndCompId(userInfo);
					principal.setMenus(MenuUtil.drow(nodes, httpRequest.getContextPath()));
				}
			}
		}
		
		// 3. 创建 SimpleAuthorizationInfo, 并设置其 reles 属性.
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addRoles(roles);
		info.addStringPermissions(permissions);
		
		// 3.1 将权限更新到当前用户中
		principal.setRoles(roles);
		principal.setPermissions(permissions);
		principal.setMaxRole(maxRole);
		
		// 4. 返回 SimpleAuthorizationInfo 对象.
		return info;
	}
	
	@Override
	public void doClearCache(PrincipalCollection principals) {
		super.doClearCache(principals);
	}
}