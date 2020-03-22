package com.dp.plat.core.realms;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.exception.CaptchaException;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UsernamePasswordCaptchaToken;
import com.dp.plat.core.service.IShiroService;
import com.dp.plat.core.util.PasswordUtil;
import com.dp.plat.support.CaptchaServlet;

public class ShiroRealm extends AuthorizingRealm {

	@Resource
	private IShiroService shiroService;

	/**
	 * 用户身份验证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
			throws AuthenticationException {
		// 1. 把 AuthenticationToken 转换为 UsernamePasswordToken
		UsernamePasswordCaptchaToken token = (UsernamePasswordCaptchaToken) authenticationToken;

		// 2. 从 UsernamePasswordToken 中来获取 username
		String username = token.getUsername();

		if (username == null) {
			throw new AccountException("用户名称不允许为空!");
		}
		Session session = SecurityUtils.getSubject().getSession();
		// 增加判断验证码逻辑
		String captcha = token.getCaptcha();
		String exitCode = (String) session.getAttribute(CaptchaServlet.KEY_CAPTCHA);
		if ("1".equals(SystemConfig.systemVariables.get("sys.envirment.argu"))
				|| "2".equals(SystemConfig.systemVariables.get("sys.envirment.argu"))) {
			if (exitCode != null && (null == captcha || !captcha.equalsIgnoreCase(exitCode))) {
				throw new CaptchaException("验证码错误！");
			}
		}

		//
		User user = shiroService.queryUserByName(username);
		// 3. 调用数据库的方法, 从数据库中查询 username 对应的用户记录

		// 4. 若用户不存在, 则可以抛出 UnknownAccountException 异常
		if (user == null) {
			throw new UnknownAccountException("用户名或密码错误！");
		}

		if (user.getStatus() == 2) {
			throw new DisabledAccountException("用户已被锁定！");
		}

		if (user.getStatus() == 0) {
			throw new DisabledAccountException("用户已被禁用！");
		}
		// List<Menu> nodes = shiroService.queryUserMenuByUsername(username);
		// session.setAttribute("node", MenuUtil.drow(nodes
		// ,(String)session.getAttribute("contextPath")));

		// 5. 根据用户信息的情况, 决定是否需要抛出其他的 AuthenticationException 异常.
		// TODO

		// 6. 根据用户的情况, 来构建 AuthenticationInfo 对象并返回. 通常使用的实现类为:
		// SimpleAuthenticationInfo
		// 以下信息是从数据库中获取的.
		// 1). principal: 认证的实体信息. 可以是 username, 也可以是数据表对应的用户的实体类对象.
		Principal principal = new Principal(user);
		// 2). credentials: 密码.
		Object credentials = user.getPassword();
		if ("0".equals(SystemConfig.systemVariables.get("sys.envirment.argu"))) {
			credentials = PasswordUtil.encryptMD5Password(new String(token.getPassword()), token.getUsername(), 1024);
		}

		// 3). realmName: 当前 realm 对象的 name. 调用父类的 getName() 方法即可
		String realmName = getName();
		// 4). 盐值.
		ByteSource credentialsSalt = ByteSource.Util.bytes(username);

		SimpleAuthenticationInfo info = null; // new
												// SimpleAuthenticationInfo(principal,
												// credentials, realmName);
		info = new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, realmName);
		return info;
	}

	/**
	 * 查询用户权限，并进行授权操作
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// 1. 从 PrincipalCollection 中来获取登录用户的信息
		Principal principal = (Principal) principals.getPrimaryPrincipal();

		// 2.1查询用户角色
		Set<String> roles = shiroService.queryUserRoleByName(principal.getUserName());
		// 2.2查询用户权限字符串集合
		Set<String> permissions = shiroService.queryPermissionByUsername(principal.getUserName());

		// 3. 创建 SimpleAuthorizationInfo, 并设置其 reles 属性.
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addRoles(roles);
		info.addStringPermissions(permissions);
		// 4. 返回 SimpleAuthorizationInfo 对象.
		return info;
	}
	
	@Override
	public void doClearCache(PrincipalCollection principals) {
		super.doClearCache(principals);
	}
}