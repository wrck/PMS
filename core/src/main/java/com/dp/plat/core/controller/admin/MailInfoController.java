//package com.dp.plat.core.controller.admin;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javax.annotation.Resource;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
//import com.dp.plat.core.param.Consts;
//import com.dp.plat.core.vo.PageParam;
//import com.dp.plat.support.mail.entity.MailInfo;
//import com.dp.plat.support.mail.service.IMailInfoService;
//
///**
// * 日志管理Controller
// * 
// * @author sunmengyuan
// *
// */
//
//@Controller()
//@RequestMapping(Consts.URLPath.SYSTEM_MANAGER + "mailInfo")
//public class MailInfoController {
//	@Resource
//	private IMailInfoService mailInfoService;
//
//	@RequestMapping
//	public void listView() {
//	}
//
//	@RequestMapping("/list")
//	public String getContractData(PageParam<MailInfo> pageParam, MailInfo data, Model model) {
//		pageParam.setModel(data);
//		pageParam.setTotal(mailInfoService.countBySelectivePageable(null));
//		List<MailInfo> dataList = new ArrayList<>();
//		if (pageParam.getPageSize() == -1L) {
//			pageParam.setPageSize(pageParam.getTotal());
//		} else {
//			pageParam.setFiltered(mailInfoService.countBySelectivePageable(pageParam));
//		}
//		dataList = mailInfoService.selectBySelectivePageable(pageParam);
//		model.addAttribute("data", dataList);
//		return Consts.URLPath.SYSTEM_MANAGER + "mailInfo";
//	}
//
//	@RequestMapping("{id}")
//	public String getOne(@PathVariable("id") Integer id, Model model) {
//		model.addAttribute("id", id);
//		return Consts.URLPath.SYSTEM_MANAGER + "mailInfo_detail";
//	}
//
//	@RequestMapping(value = "{id}", method = RequestMethod.POST)
//	public String findOne(@PathVariable("id") Integer id, Model model) {
//		MailInfo mailInfo = mailInfoService.selectByPrimaryKey(id);
//		model.addAttribute("mailInfo", mailInfo);
//		return Consts.URLPath.SYSTEM_MANAGER + "mailInfo_detail";
//	}
//
//	@RequestMapping("/detail")
//	public String create() {
//		return Consts.URLPath.SYSTEM_MANAGER + "modals/mailInfo_detail";
//	}
//
//	@RequestMapping(value = "/detail", method = RequestMethod.POST)
//	public String create(MailInfo mailInfo) {
//		mailInfo.setCreateTime(new Date());
//		mailInfoService.insertSelective(mailInfo);
//		return Consts.URLPath.SYSTEM_MANAGER + "mailInfo_detail";
//	}
//
//	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
//	public String update(@PathVariable("id") Integer id, MailInfo mailInfo) {
//		mailInfoService.updateByPrimaryKeySelective(mailInfo);
//		return Consts.URLPath.SYSTEM_MANAGER + "mailInfo_detail";
//	}
//	
//	@RequestMapping(value = "/invalid", method = RequestMethod.POST)
//	public void invalid(@RequestParam("id") Integer id, Model model) {
//		try {
//			MailInfo mailInfo = mailInfoService.selectByPrimaryKey(id);
//			mailInfo.setExpectSendTime(null);
//			mailInfoService.updateByPrimaryKey(mailInfo);
//			model.addAttribute("status", Boolean.TRUE);
//		} catch (Exception e) {
//			Integer errorLogId = ExceptionHandler.insertException(e);
//			String errorMessage = "<br>错误信息：" + e.getClass().getSimpleName() + "<br>错误ID:" + errorLogId;
//			model.addAttribute("status", Boolean.FALSE);
//			model.addAttribute("message", "操作失败！" + errorMessage);
//		}
//	}
//}
