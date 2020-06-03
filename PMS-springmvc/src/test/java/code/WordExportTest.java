package code;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.fileupload.util.Streams;

import com.alibaba.fastjson.JSON;
import com.dp.plat.core.util.FileUtil;
import com.dp.plat.pms.springmvc.util.DocUtil;
import com.dp.plat.util.WordUtil;

public class WordExportTest {

	@org.junit.Test
	public static void main(String[] args) {
//		SettlementVO settlementVO = new SettlementVO();
//		settlementVO.setProgressDesc("啥的手机卡的好时机啊，待机时间奥科吉");
//		DispatchVO dispatch = new DispatchVO();
//		dispatch.setSmsProjectCode("1566151651515");
//		dispatch.setDispatchName("dskhadjk圣诞节上的数量级ad ");
//		dispatch.setCustomInfoByKey("smsOrderExecNumber", "16551881xx16x");
//		dispatch.setContractNos("3132156d5s");
//		dispatch.setCollectedRatio(12.2);
//		dispatch.setType("frameworkAgreement");
//		dispatch.setCustomInfoByKey("frameworkAgreementMemo", "1.安全咨询类服务项目金额大于等于10万，按照3:6:1进行付款，项目金额小于10万无预付款。\r\n 2.安全运维服务项目周期1年，按照合理合情的原则，背靠背支付。\r\n 3.特殊项目，特殊申请。\r\n");
//		settlementVO.setDispatch(dispatch);
		String map = "{'id':1870,'createBy':'w02611','createTime':'2020-05-18 11:48:42','updateBy':null,'updateTime':null,'orgId':1,'settleSeq':'2020-102-16-云南电网昆明供电局业务系统等级保护测评服务项目-12-3121','dispatchId':1611,'dispatchSeq':'2020-102-16','progressDesc':'长沙长沙','progressRatio':null,'acceptanceDesc':'长沙踩刹车','acceptanceRatio':null,'ratio':'12','amount':'3121','memo':'1212','confirmTime':null,'paymentTime':null,'remark':null,'state':null,'sseId':0,'year':null,'quarter':null,'month':null,'customInfo':{'smsProjectName':'云南电网昆明供电局业务系统等级保护测评服务项目','smsProjectAmount':'57000.14','collectedRatio':0,'smsProjectCode':'16201814072102N','smsOrderExecNumber':'1620181408141X305','smsSubmitTime':'2014-07-21 09:46:52','contractNos':'61020141229Y'},'disabled':false,'projectId':null,'officeCodes':null,'projectTypes':null,'smsProjectCode':null,'smsSubmitTime':null,'smsProjectAmount':null,'smsProjectName':null,'smsOrderExecNumber':null,'contractNos':null,'collectedAmount':null,'deliveredAmount':null,'contractAmount':null,'settledAmount':null,'collectedRatio':null,'settledRatio':null,'dispatch':{'id':1611,'createBy':'w02611','createTime':'2020-05-18 10:27:08','updateBy':'w02611','updateTime':'2020-05-18 10:27:53','orgId':1,'dispatchName':'云南电网昆明供电局业务系统等级保护测评服务项目','dispatchNo':'SS20200518202010216','dispatchSeq':'2020-102-16','contractNos':'61020141229Y','projectIds':'32600','type':'frameworkAgreement','state':50,'peopleNum':0,'callbackState':null,'facilitatorId':102,'facilitatorCode':'102','facilitatorName':'石家庄赛伦计算机科技有限公司','bankInfo':'中国民生银行石家庄平安南大街支行','bankAccount':'627114612','officeCode':'162018','profitDepCode':'','dutyPerson':null,'officeDutyPerson':null,'isAccrued':null,'isInvoiced':null,'dispatchAmount':'','prepaidInfo':'','prepaidRule':'安全咨询类服务项目金额大于等于 10 万，按照 3:6:1 进行付款，项目金额小于 10万无预付款','acceptanceInfo':'','reason':'','remark':'','dispatchTime':'2020-05-18 10:27:40','smsProjectCode':'16201814072102N','smsSubmitTime':'2014-07-21 09:46:52','smsProjectAmount':'57000.14','effectiveFrom':'2020-05-18 10:27:08','effectiveTo':null,'disabled':false,'dispatched':true,'settled':false,'customInfo':{'smsOrderExecNumber':'1620181408141X305'},'projectId':null,'officeCodes':null,'projectTypes':null,'typeName':'第三方服务','stateName':null,'createName':'w02611-闻人材柯','officeName':'昆明办事处','collectContractNos':null,'collectedAmount':null,'deliveredAmount':null,'contractAmount':null,'settledAmount':'3,121.00','collectedRatio':null,'settledRatio':null}}";
		File doc = new DocUtil().createDoc((Map<?, ?>) JSON.parse(map), "/template/", "项目信息单.ftl", "02项目信息单-sklajdlsaj");
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream("C:\\Users\\user\\Desktop\\ess.docx");
			Streams.copy(new FileInputStream(doc), outputStream , true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
