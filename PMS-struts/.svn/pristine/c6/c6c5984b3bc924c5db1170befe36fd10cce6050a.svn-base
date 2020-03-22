window.onload = sys_init;
/**
 * ��ȡ��Ŀ��
 */
function getObjectName(){
	var urlPaht = window.document.location.href;
	var pathName = window.document.location.pathname; 
	var post = urlPaht.indexOf(pathName);
	var localhostPaht = urlPaht.substring(0, post);
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1); 
	return projectName;
}

function minxueCheckSubmit(submitData,warnning,submitButton,checkItem){	//submitButton:提交按钮  checkItem:可选检查项
		if(checkItem[0]==1){	//空字符串检查
			if(submitData==null||submitData==""){
			alert(warnning+"填写错误");
			submitButton.removeAttr("disabled");
				return true;
			}
		}
		
		if(checkItem[1]==1){	//空格检查
			if (submitData.indexOf(" ") >=0) {	
				alert(warnning+"输入空格");
				submitButton.removeAttr("disabled");
				 return true;
			 }
		}
			
		return false;
		
	}

/**
 * �رյ�ǰҳ�棬ˢ�¸�ҳ�棬��������ҳ����޸ĶԸ�ҳ�����Ϣ��Ӱ��ʱ����ҳ��ر�ʱ���»ص���ҳ��ʱˢ�¸�ҳ�����Ϣ
 */
function winclose(){
	window.close();
	opener.location.reload();
	
}
/**
 * ҳ����ת
 */
function submitAction(action){
	window.location.href = getObjectName() + action; 
}
function sys_init()
{
	sys_inerinit();
	
	try
	{
		for(var i=0;i<sys_initcollback.length;i++)
		{
			var func = sys_initcollback[i];
			func();
		}
	}
	catch(e)
	{}
	
	try
	{
		showErrorMsgInInit();
	}
	catch(e)
	{
	}
}

var sys_initcollback = [];

function sys_addinit(func)
{
	sys_initcollback[sys_initcollback.length]=func;
}

function sys_inerinit()
{
	var metas = document.getElementsByTagName("meta");

	var cur_module = null;
	var cur_group = null;
	var cur_function = null; 
	var netm = 0;
	var netmgroup = null;
	var cur_child = null;
	

	for(var i=0;i < metas.length;i++) 
	{
		if(metas[i].name == "module")
		{
			cur_module = metas[i].content;
		}
		else if(metas[i].name == "group")
		{
			cur_group = metas[i].content;
		}
		else if(metas[i].name == "function")
		{
			cur_function = metas[i].content;
		}
		else if(metas[i].name == "netm")
		{
			netm = metas[i].content;
		}
		else if(metas[i].name == "netmgroup")
		{
			netmgroup = metas[i].content;
		}else if(metas[i].name == "child"){
			cur_child = metas[i].content;
		}
	}

	if(null == cur_module || null == cur_group || null == cur_function)
	{
		return;
	} 
	
	if(netm == 1)
	{
		if(netmgroup != null)
		{
			cur_group = netmgroup;
		}
	}
	
	var container = getObj("menu_container");
	
	if (container!=null){
		for(var i=0;i<container.childNodes.length;i++){
			var oGroup = container.childNodes[i];
			if("TABLE" != oGroup.nodeName.toUpperCase()){
				continue;
			}
			
			oGroup = oGroup.rows[0].cells[0];
	
			var grouptitle = "";
			var find = false;
			for(var j=0;j<oGroup.childNodes.length;j++){
				var obj = oGroup.childNodes[j];
	
				if("TABLE" == obj.nodeName.toUpperCase()){
					//title
					grouptitle = getText(obj);
					if(grouptitle == cur_group){
						find = true;
						break;
					}
				}else{//不属于同一级父菜单下隐藏三级子菜单
					var oDiv = getFirst(oGroup, "DIV");
					if (null == oDiv) {
						break;
					}

					var oTabContain = getFirst(oDiv, "TABLE");
					if (null == oTabContain) {
						break;
					}

					var oTab = getFirst(oTabContain.rows[0].cells[0], "TABLE");
					if (null == oTab) {
						break;
					}

					for (var k = 0; k < oTab.rows.length; k++) {
						var oTr = oTab.rows[k];
						if (getText(oTr) == cur_function) {
							oTr.className = "select";
							oTr.onmouseover = "";
							oTr.onmouseout = "";

							var oImg = getFirst(oTr.cells[0], "IMG");
							if(oImg != null){
								oImg.className = "noDisplay";
							}

							var oLink = getFirst(oTr.cells[1], "A");
							if(oLink != null){
								oLink.className = "selectedMenuLink";
							}
							// break;
						} else {
							var childtable = null;
							if (oTr.cells.length > 1) {
								childtable = getFirst(oTr.cells[1], "TABLE");
							}

							if (childtable != null) {
								var b = true;
								for (var l = 0; l < childtable.rows.length; l++) {
									var childTr = childtable.rows[l];

									if (getText(childTr) == cur_function) {
										childTr.cells[1].className = "selectchild";
										childTr.onmouseover = "";
										childTr.onmouseout = "";

										// var oImg = getFirst(childTr.cells[0], "IMG");
										// oImg.className = "noDisplay";

										var oLink = getFirst(childTr.cells[1], "A");
										oLink.className = "selectedMenuLink";
										b = false;
										// break;
									}
								}
								if (b) {
									childtable.className = "noDisplay";
								}
							}
						}
						var childtable = null;
						if (oTr.cells.length > 1) {
							childtable = getFirst(oTr.cells[1], "TABLE");
						}

						if (childtable != null && cur_child != null) {
							var b = true;
							for (var j = 0; j < childtable.rows.length; j++) {
								var childTr = childtable.rows[j];
								var text = getText(childTr) + "";
								if (text != null && cur_child.indexOf(text) > 0) {// 包含
									b = false;
									break;
								}
							}
							if (!b) {
								childtable.className = "display";
							}
						}
					}
					break;
				}
			}
			if (find == false) {
				continue;
			}

			var oDiv = getFirst(oGroup, "DIV");
			if (null == oDiv) {
				break;
			}

			var oTabContain = getFirst(oDiv, "TABLE");
			if (null == oTabContain) {
				break;
			}

			var oTab = getFirst(oTabContain.rows[0].cells[0], "TABLE");
			if (null == oTab) {
				break;
			}

			for (var k = 0; k < oTab.rows.length; k++) {
				var oTr = oTab.rows[k];
				if (getText(oTr) == cur_function) {
					oTr.className = "select";
					oTr.onmouseover = "";
					oTr.onmouseout = "";

					var oImg = getFirst(oTr.cells[0], "IMG");
					oImg.className = "noDisplay";

					var oLink = getFirst(oTr.cells[1], "A");
					oLink.className = "selectedMenuLink";

					// break;
				} else {
					var childtable = null;
					if (oTr.cells.length > 1) {
						childtable = getFirst(oTr.cells[1], "TABLE");
					}

					if (childtable != null) {
						var b = true;
						for (var l = 0; l < childtable.rows.length; l++) {
							var childTr = childtable.rows[l];

							if (getText(childTr) == cur_function) {
								childTr.cells[1].className = "selectchild";
								childTr.onmouseover = "";
								childTr.onmouseout = "";

								// var oImg = getFirst(childTr.cells[0], "IMG");
								// oImg.className = "noDisplay";

								var oLink = getFirst(childTr.cells[1], "A");
								oLink.className = "selectedMenuLink";
								b = false;
								// break;
							}
						}
						if (b) {
							childtable.className = "noDisplay";
						}
					}
				}
				var childtable = null;
				if (oTr.cells.length > 1) {
					childtable = getFirst(oTr.cells[1], "TABLE");
				}

				if (childtable != null && cur_child != null) {
					var b = true;
					for (var j = 0; j < childtable.rows.length; j++) {
						var childTr = childtable.rows[j];
						var text = getText(childTr) + "";
						if (text != null && cur_child.indexOf(text) > 0) {// 包含
							b = false;
							break;
						}
					}
					if (!b) {
						childtable.className = "display";
					}
				}
			}
			//break;
		}
	}
	
}

function getObj(id)
{
	return "string" == typeof id ? document.getElementById(id) : id;
}

function getText(obj)
{
	var str = "";
	if(obj.textContent == undefined)
	{
		str = obj.innerText;
	}
	else
	{
		str = obj.textContent;
	}
	
	return str.replace(/\s+/g, " ").trim();
}
String.prototype.trim = function ( )
{
    var str = this;
    str = str.replace(/^\s*/, "");
    str = str.replace(/\s*$/, "");
    return str;
}

function getFirst(obj, tag)
{
	var oFirst = null;
	for(var k=0;k < obj.childNodes.length;k++)
	{
		var otemp = obj.childNodes[k];
		
		if(tag == otemp.nodeName.toUpperCase())
		{
			//menuitem
			oFirst = otemp;
			break;
		}
	}
	
	return oFirst;
}

function checkSelect()
{
	var objs=document.getElementsByName("selected");
	for(var i=0;i<objs.length;i++)
	{
		if(objs[i].checked)
		{
			return true;
		}
	}
	return false;
}

function doAction(formid, action, url, target)
{
	var oldmethod = getObj(formid).method;
	var oldtarget = getObj(formid).target;
	var oldaction = getObj(formid).action;
	getObj(formid).method="post";
	

	try
	{
		if(false == doActionCheck(formid, action))
		{
			getObj(formid).method = oldmethod;
			getObj(formid).target = oldtarget;
			getObj(formid).action = oldaction;
			return;
		}
	}
	catch(e)
	{
	}
	
	if(getObj(formid).onsubmit==null || true == getObj(formid).onsubmit())
	{
		
		if(action != "")
		{
			getObj(formid).action=url;
		}
		if (target == '' || target == null || target == undefined)
		{
			
		}
		else
		{
			getObj(formid).target=target;
		}
		getObj(formid).submit();

		getObj(formid).method = oldmethod;
		getObj(formid).target = oldtarget;
		getObj(formid).action = oldaction;
	}
	return;
}

var sys_submitcollback = [];
function sys_addsubmit(func)
{
	sys_submitcollback[sys_submitcollback.length]=func;
}

function sys_submit(oForm)
{
	var cansub = true;

	try
	{
		for(var i=0;i<sys_submitcollback.length;i++)
		{
			var func = sys_submitcollback[i];
			cansub = func(oForm);
			if(false == cansub)
			{
				break;
			}
		}
	}
	catch(e)
	{
	}

	if(false == cansub)
	{
		return false;
	}
	return true;
}

function getObjsByName(name)
{
	return document.getElementsByName(name);
}

function getRadioValue(name)
{ 
      var objs = document.getElementsByName(name); 
      for (i=0;i<objs.length;i++)
      {
            if (objs[i].checked) 
            { 
                  return objs[i].value; 
            } 
       }
}

function parseNum(str)
{
	str = str.trim();
	str = str.replace("^0+","");
	if("" == str)
	{
		str = "0";
	}
	try
	{
		var rst = parseInt(str);
		if(isNaN(rst))
		{
			return null;
		}
		return rst;
	}
	catch(e)
	{
		return null;
	}
}

function setCheckedRadio(name, value)
{
	var objs = document.getElementsByName(name);
	if(null != objs)
	{
		var i;
		for(i=0;i<objs.length; i++)
		{
			if(objs[i].value == value)
			{
				objs[i].checked = true;
				break;
			}
		}
	}
}

function validateIP4(IPstr)
{
	var IPPattern = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/
	if(!IPPattern.test(IPstr))return false;

	var IPArray = IPstr.split(".");
	if(IPArray.length != 4)return false;
	
	var ip1 = parseInt(IPArray[0]);
	var ip2 = parseInt(IPArray[1]);
	var ip3 = parseInt(IPArray[2]);
	var ip4 = parseInt(IPArray[3]);
	if ( ip1<0 || ip1>255
		|| ip2<0 || ip2>255
		|| ip3<0 || ip3>255
		|| ip4<0 || ip4>255 )
	{
		return false;
	}
	
	if ( (ip1+ip2+ip3+ip4)==0 )
	{
		return false;
	}
	
	return true;
}

function IpToLong(IPstr)
{
	var IPArray = IPstr.split(".");
	
	var ip1 = parseInt(IPArray[0]);
	var ip2 = parseInt(IPArray[1]);
	var ip3 = parseInt(IPArray[2]);
	var ip4 = parseInt(IPArray[3]);
	
	return ip1*0x1000000+ip2*0x10000+ip3*0x100+ip4;
}
function LongToIp(IP)
{
	var ip1 = (IP/0x1000000)&0xFF;
	var ip2 = (IP/0x10000)&0xFF;
	var ip3 = (IP/0x100)&0xFF;
	var ip4 = IP&0xFF;	
	
	return ip1+"."+ip2+"."+ip3+"."+ip4;
}
function IpandMask(IP, MASK)
{
	var ip1 = (IP/0x1000000)&0xFF;
	var ip2 = (IP/0x10000)&0xFF;
	var ip3 = (IP/0x100)&0xFF;
	var ip4 = IP&0xFF;	

	var mask1 = (MASK/0x1000000)&0xFF;
	var mask2 = (MASK/0x10000)&0xFF;
	var mask3 = (MASK/0x100)&0xFF;
	var mask4 = MASK&0xFF;	

	return (ip1&mask1)*0x1000000+(ip2&mask2)*0x10000+(ip3&mask3)*0x100+(ip4&mask4);
}

function setFocus(id)
{
	getObj(id).focus();
}
function createObj(tab)
{
	return document.createElement(tab);
}
function setSelectValue(id, value)
{
	var sel = getObj(id);
	if(null != sel)
	{
		var i;
		for(i=0;i<sel.options.length; i++)
		{
			if(sel.options[i].value == value)
			{
				sel.options[i].selected = true;
				break;
			}
		}
	}
}
function getSelectValue(id)
{
	var sel = getObj(id);
	return sel.options[sel.selectedIndex].value;
}
/* page sys_env*/
var sys_env = "";
function sys_addenv(env)
{
	sys_env+=env;
}
function addaddenvparam(env, param, value)
{
	var checkstr = param+"=[\\w\\+%\\-@\\*.:]*&";
	var newvalue = param+"="+encodeURI(value)+"&";
	if("" == value)
	{
		newvalue = "";
	}
	try
	{
		var reg = new RegExp(checkstr, "g");
		if(reg.test(env))
		{
			env = env.replace(reg, newvalue);
		}
		else
		{
			env+=newvalue;
		}
	}catch(e)
	{
		alert(e);
	}
	return env;
}

function exportExcel(url)
{
		var par = sys_env;
		window.location=url+"?export=xls&" + par;	
}

function exportExcel2(url,param)
{
		var par = sys_env;
		window.location=url+"?export=xls&"+param+"&" + par;	
}

function redirectWithPara(url, custPara)
{
		var par = sys_env;
		window.location=url+"?" + custPara + "&" + par;	
}

function openWindow(url,features)
{
	popup=window.open(url, '_blank', features);
	popup.focus();
}	

var orderid = 0;
function ipinfo(id, val, time)
{
	if(null == val)
	{
		var str = "<SPAN style=\"CURSOR: hand\" id=ipinfo_"+id+"_all onclick=\"ipinfo_resolveall('"+id+"');\"><IMG style=\"BORDER: none;\" alt=\"IP Information\" align=absMiddle src=\"images/resolvedns.gif\"></SPAN>";
		document.write(str);
	}
	else
	{
		var str = "<SPAN style=\"CURSOR: hand\" id=ipinfo_"+id+"_"+orderid+" onclick=\"ipinfo_resolve(this, '"+id+
			"', "+val+", "+time+");\"><IMG style=\"BORDER: none;\" alt=\"IP Information\" align=absMiddle src=\"images/resolvedns.gif\"></SPAN>"+
			"<input type=hidden name='_ipinfo_"+id+"' oid='"+orderid+"' value='"+val+"' time='"+time+"' disabled/>";
		document.write(str);
		orderid++;
	}
}
function ipinfo_resolveall(id)
{
	var objs = getObjsByName("_ipinfo_"+id);
	for(var i=0;i<objs.length; i++)
	{
		var div = getObj("ipinfo_"+id+"_"+objs[i].getAttribute("oid"));
		ipinfo_resolve(div, id, objs[i].getAttribute("value"), objs[i].getAttribute("time"));
	}
}

function ipinfo_resolve(dev, id, val, time)
{
	g_queue.add("sys/sub/NameReverseFind.action?nameReverseFindParam.ip="+val+"&nameReverseFindParam.time="+time, ipinfo_resolvecb, dev);
}

function ipinfo_resolvecb(oXmlHttp, obj)
{
	obj.innerHTML = oXmlHttp.responseText;
}

function userinfo(revquery,userip,starttime,endtime ,title)
{
	if(revquery == true)
	{
		var str = "<a href=\"javascript: popWindow('uag/sub/UagTopUserRevQuery.action?revQueryParam.userip=" 
			+ userip + "&revQueryParam.begintime=" + starttime 
			+ "&revQueryParam.endtime=" + endtime + "', 780, 400,'" + title 
			+ "','UagTopUserRevQuery', true)\"><IMG src=\"images/people.gif\" align=\"absMiddle\"  alt=" 
			+ title + " style=\"border:none\" /></a>";
		document.write(str);
	}
	else
	{
		var str = "";
		document.write(str);
	}
}
function gradeQueryWait()
{
	popWindow('sys/sub/GradeQuerying.action', 500, 200,'Query...','dialog', true);
}

function initExternalLinks(externalGroups) {
    /*if ($(".externalLinks").length > 0) {
        return;
    }*/
//    var externalGroups = [{
//		id:"external",
//        text: '站点导航',
//        childern: [{
//            title: '备件管理系统',
//            url: 'http://spms.dptech.com'
//        }, {
//            title: '技术支援部办公平台',
//            url: 'http://10.26.2.50'
//        }, {
//            title: '在线学习考试系统',
//            url: 'http://learning.dptech.com'
//        }]
//    },{
//		id:"externale2",
//        text: '站点导航2',
//        childern: [{
//            title: '备件管理系统',
//            url: 'http://spms.dptech.com'
//        }, {
//            title: '技术支援部办公平台',
//            url: 'http://10.26.2.50'
//        }, {
//            title: '在线学习考试系统',
//            url: 'http://learning.dptech.com'
//        }]
//    }];
	externalGroups = $.isArray(externalGroups) ? externalGroups : [];
    for (var gi in externalGroups) {
        var externalGroup = externalGroups[gi];
		if ($("#externalLinks-" + externalGroup.id).length > 0) {
			continue;
		}
        var $m = $(".menuTopMargin:last").clone().addClass("externalLinks").attr("id", "externalLinks-" + externalGroup.id);
        var $b = $(".menuTopMargin:last").next().clone();
        $m.find(".menuTitleCell .menuTitle:eq(1)").text(externalGroup.text);

        var $f = $m.find(".menuCell>.menuList tr:first").clone();
        var $l = $m.find(".menuCell>.menuList tr:last").clone();
        $m.find(".menuCell .menuList tr").remove();
        var externalLinks = $.isArray(externalGroup.childern) ? externalGroup.childern : [];
        for (var i in externalLinks) {
            var externalLink = externalLinks[i];
            var $tr = $f.clone();
            var $temp = $tr.find("a.menuList");
            $temp.text(externalLink.title);
            $temp.attr("href", externalLink.url);
            $temp.attr("target", "_blank");
            $tr.find("td:last").html($temp);
            $tr.appendTo($m.find(".menuCell>.menuList tbody"));
            $l.clone().appendTo($m.find(".menuCell>table.menuList tbody"));
        }
        $m.appendTo($("#menu_container"));
        $b.appendTo($("#menu_container"));
    }
    $("#menu_container br").appendTo($("#menu_container"));
}