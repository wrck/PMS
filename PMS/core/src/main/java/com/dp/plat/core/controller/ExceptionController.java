/**
 * 
 */
package com.dp.plat.core.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dp.plat.core.annotation.SystemControllerLog;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.util.JsoupUtil;

/**
 * 用于处理ExceptionHandler无法捕获的异常以及404页面
 * 
 * <p>
 * 一般的异常都能够被<code>ExceptionHandler</code>
 * 捕获，一些特殊的异常则通过web.xml配置的error-page进行处理，使用error-page时会由
 * <code>ShiroHttpServletRequest
 * <code>转发，此时无法使用sitemesh对500进行装饰，所以通过to500进行跳转;404同理
 * </p>
 * 
 * @author w02611
 * @see ExceptionHandler
 */
@Controller
public class ExceptionController {

	@RequestMapping("/500")
	public String error500(String errorLogId, String error, Model model) {
		if (StringUtils.isNotBlank(errorLogId)) {
			model.addAttribute("errorLogId", errorLogId);
		}
		if (StringUtils.isNotBlank(error)) {
			model.addAttribute("error", JsoupUtil.clean(error, Safelist.basic()));
		}
		return "500";
	}

	@RequestMapping("/404")
	public String error404() {
		return "404";
	}

	@RequestMapping("/to500")
	public ModelAndView to500(HttpServletRequest request, HttpServletResponse response) {
		String errorLogId = String.valueOf(request.getAttribute("errorLogId"));
		String error = (String) request.getAttribute("error");
		ModelAndView modelAndView = new ModelAndView("redirect:/500.html");
		modelAndView.addObject("errorLogId", errorLogId);
		modelAndView.addObject("error", error);

		// 判断是否为ajax请求
		if (request != null && ((request.getHeader("accept") != null
				&& request.getHeader("accept").indexOf("application/json") > -1)
				|| (request.getHeader("X-Requested-With") != null
						&& request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1))) {
			try {
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setStatus(500);
				// PrintWriter writer = response.getWriter();
				// writer.write(JSON.toJSONString(modelAndView.getModelMap()));
				// writer.flush();
				// writer.close();
				modelAndView = null;
			} catch (Exception e) {
			}
		}
		return modelAndView;
	}

	@RequestMapping("/to404")
	public ModelAndView to404(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("redirect:/404.html");
		if (request != null && ((request.getHeader("accept") != null
				&& request.getHeader("accept").indexOf("application/json") > -1)
				|| (request.getHeader("X-Requested-With") != null
						&& request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1))) {
			try {
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setStatus(404);
				// PrintWriter writer = response.getWriter();
				// writer.write(JSON.toJSONString(modelAndView.getModelMap()));
				// writer.flush();
				// writer.close();
				modelAndView = null;
			} catch (Exception e) {
			}
		}
		return modelAndView;
	}

	/**
	 * 无权限访问返回页面
	 * 
	 * @return
	 */
	@RequestMapping("/unauthorized")
	public String unauthorized() {
		return "unauthorized";
	}

	/**
	 * 违规操作，跳转至该路径，记录信息后重定向至404
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/illegal")
	@SystemControllerLog(description = "【违规操作】$user.realName$$illegalName$")
	public String errorIllegal(HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, String> illegalName = MapUtils.putAll(new HashMap<String, String>(),
				new String[] { 
					"JSDebugger", "调用前端审查工具",
					"SQLInject", "进行SQL注入"
				});
		Principal principal = UserContext.getCurrentPrincipal();
		UserInfo user = principal.getUserInfo();
		String illegal = request.getParameter("illegal");
		String fromUrl = StringUtils.defaultIfBlank(request.getParameter("fromUrl"), request.getHeader("referer"));
		model.addAttribute("illegal", illegal);
		model.addAttribute("illegalName", illegalName.getOrDefault(illegal, illegal));
		model.addAttribute("user", user);
		model.addAttribute("fromUrl", fromUrl);
		model.addAttribute("status", request.getParameter("status"));
		model.addAttribute("error", request.getParameter("error"));
		return "404";
	}
}
