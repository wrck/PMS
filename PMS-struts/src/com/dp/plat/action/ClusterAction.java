package com.dp.plat.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.dp.plat.context.UserContext;
import com.dp.plat.security.util.ASEUtil;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.StringEscUtil;

public class ClusterAction extends BaseAction{
	private static final long serialVersionUID = 1L;
	
	private static final String signKey = "dptech.pms.cluster.server";

	private BasicDataService basicDataService;
	
	private String signature;
	
	public String refreshCacheData() {
	    if (StringUtils.isBlank(signature) && UserContext.getUserContext().isHasRole(MessageUtil.ROLE_ADMIN)) {
	        return SUCCESS;
	    }
	    
	    String currentServerName = StringEscUtil.getText("sys.server.name");
	    
	    ASEUtil.decrypt(signature, signKey + currentServerName);
	    basicDataService.refreshCacheData();
		return SUCCESS;
	}
	
	public static String notifyCluster() {
	    String currentServerName = StringEscUtil.getText("sys.server.name");
        List<Object> signs = new ArrayList<Object>();
        signs.add(currentServerName);
        signs.add(System.currentTimeMillis());
        String signature = ASEUtil.encrypt(StringUtils.join(signs, "$$"), signKey + currentServerName);
        return signature;
	}
}
