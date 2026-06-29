<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<meta name="function" content="<s:text name='prob.manage' />">
<style>
    .btn-link{
        margin-right:0.5rem;
    }
    table thead th, .nowrap{
        white-space: nowrap;
    }
</style>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript">
    function initDatePicker() {
	    date_picker("startTime");
	    date_picker("endTime");
	    date_picker("startTime_");
	    date_picker("endTime_");
	}
    $(function(){
    	initDatePicker();
        var tabIndex = "${probStatistic.tabIndex}" ? "${probStatistic.tabIndex}" :0;
        $("#myTab a:eq("+tabIndex+")").tab("show");
        
        var flagIndex0 = true;
        var flagIndex2 = true;
        if (tabIndex == 2) {
            flagIndex2 = false;
        } else {
            flagIndex0 = false;
        }
        $(document).on("click", "#myTab a", function() {
        	var index = $(this).parent().index();
        	if (flagIndex2 && index == 2) {
        		flagIndex2 = false;
        		submitStatistic(2);
        	}
        	if (flagIndex0 && (index == 0 || index == 1)) {
                flagIndex0 = false;
                submitStatistic(0);
            }
        })
        
        $(document).on("click",".pagelinks a",function(e) {
        	var href = $(this).attr("href");
        	var params = href.split("?");
        	if (params.length > 0) {
        		params = params[1];
        		var data = [];
        		params.split('&').forEach(function(param){
        		  param = param.split('=');
        		  var obj = {};
        		  var name = param[0],
        		      val = param[1];
        		  if (val) {
        			  obj.name=name;
                      obj.value=val;
                      data.push(obj);
        		  }
        		})
        		var tabIndex = $(this).parents(".tab-pane").index(".tab-pane");
                submitStatistic(tabIndex, data);
                e.preventDefault();
        	}
        })
    });
    
    var flag1 = true;
    function submitStatistic(tabIndex, data){
        if(flag1){
        	tabIndex = tabIndex ? tabIndex : 0;
        	var params = {};
        	//var url = uri ? uri : "module/prob_statistics.action";
            if (tabIndex == 0) {
                params = $("#statisticForm").serializeArray();
            } else if (tabIndex == 1) {
                params = $("#reportForm").serializeArray();
            } else if (tabIndex == 2) {
            	params = $("#projectForm").serializeArray();
            } else if (tabIndex == 3) {
                params = $("#shipmentSoftForm").serializeArray();
                if (!checkShipmentSoftForm(params)) {
                	alert("避免数据量过多，请补充查询参数！");
                	return false;
                }
            } else if (tabIndex == 4) {
                params = $("#probAffectedProjectSoftForm").serializeArray();
            }
            params = data ? data : params;
            var id = $("#myTab a:eq("+tabIndex+")").attr("href").replace("#","");
        	flag1 = false;
        	$("button:eq("+tabIndex+")").button("loading");
            $.ajax({
                url:"module/prob_statistics.action",
                type:"post",
                dataType:"html",
                data: params,
                success:function(data){
                	if (tabIndex == 2) {
                		drawSuccessHtml(2, data);
                		initAutoComplete();
                	} else if (tabIndex == 3) {
                        drawSuccessHtml(3, data);
                        initAutoComplete();
                    } else if (tabIndex == 4) {
                        drawSuccessHtml(4, data);
                        initAutoComplete();
                    } else {
                		drawSuccessHtml(0, data);
                		drawSuccessHtml(1, data);
                		initDatePicker();
                	}
                },
                error:function(XMLHttpRequest,textStatus,errorThrown){
                	if (tabIndex == 2) {
                		drawErrorHtml(2, XMLHttpRequest);
                    } else {
                    	drawErrorHtml(0, XMLHttpRequest);
                        drawErrorHtml(1, XMLHttpRequest);
                    }
                },
                complete:function(data){
                	flag1 = true;
                	$("button:eq("+tabIndex+")").button("reset");
                }
            })
        }
        return false;
    }
    
    function drawSuccessHtml(tabIndex, data) {
    	var tempIndex = tabIndex;
        var id = $("#myTab a:eq("+ tempIndex +")").attr("href").replace("#","");
        var prefix = "id=\"" + id + "\">";
        var tempdata = data.substring(data.indexOf(prefix) + prefix.length, data.indexOf("<hr id=\"hr" + tempIndex +"\""));
        tempdata = $.trim(tempdata);
        if (!tempdata) {
        	tempdata = data.substring(data.indexOf("<body>") + 6, data.indexOf("</body>"));
        }
        $("#" + id).html(tempdata);
    }
    
    function drawErrorHtml(tabIndex, XMLHttpRequest) {
    	var id = $("#myTab a:eq("+ tabIndex +")").attr("href").replace("#","");
        $("#" + id).html("获取" + $("#myTab a:eq(" + tabIndex + ")").text() + "数据失败！错误信息如下：<br>"+XMLHttpRequest.responseText);
    }
    
    var realNameArrObj = {};
    var userNameArrObj = {};
    function queryRoleUser(roleid){
        $.ajax({
            url:'queryalluser.action',
            type:'post',
            dataType:'json',
            data:{roleid :roleid},
            success: function(json) {
            	var userlist = json.allusernameList;
            	var usernameArr2 = [];
            	var realnameArr2 = [];
                for(var i = 0;i < userlist.length;i++){
                    usernameArr2[i] = userlist[i].username;
                    realnameArr2[i] = userlist[i].username+"-"+userlist[i].realName;
                }
                userNameArrObj[roleid] = usernameArr2;
                realNameArrObj[roleid] = realnameArr2;
            }
        });
    }
    queryRoleUser(11);
    queryRoleUser(12);
    
    function initAutoComplete() {
        $(".sm").autocomplete({
  	        source: realNameArrObj[11],
  	    });
        $(".pm").autocomplete({
            source: realNameArrObj[12],
        });
    }
    initAutoComplete();
    function fillsm(){
    	var $sm = $(".sm:visible");
        var val = $sm.val();
        if(val == ""){
        	$sm.siblings(".smhide").val("");
        } else {
            var i=0;
            var realnameArr2 = realNameArrObj[11];
            var usernameArr2 = userNameArrObj[11];
            for(;i<realnameArr2.length;i++){
                if(realnameArr2[i]==val){
                    break;
                }
            }
            if(i==realnameArr2.length){
                return false;
            } else{
            	$sm.siblings(".smhide").val(usernameArr2[i]);
            }
        }
    }

    function fillpm(){
    	var $pm = $(".pm:visible");
        var val = $pm.val();
        if(val == ""){
        	$pm.siblings(".pmhide").val("");
        } else {
            var i=0;
            var realnameArr3 = realNameArrObj[12];
            var usernameArr3 = userNameArrObj[12];
            for(;i<realnameArr3.length;i++){
                if(realnameArr3[i]==val){
                    break;
                }
            }
            if(i==realnameArr3.length){
                return false;
            } else{
            	$pm.siblings(".pmhide").val(usernameArr3[i]);
            }
        }
    }

	function checkShipmentSoftForm(formData) {
	    // 获取表单所有字段的值
	    var formData = formData || [];
	    
	    // 将 excludeFields 转为 Set，便于快速查找
	    var excludeSet = new Set(["probStatistic.tabIndex", "probStatistic.filterItem", "probStatistic.version.conp"]);

	    // 遍历 formData，检查除了指定字段外，是否有其他字段具有非空值
	    for (var i = 0; i < formData.length; i++) {
	        var field = formData[i];
	        
	        // 跳过被排除的字段
	        if (excludeSet.has(field.name)) {
	            continue;
	        }
	        
	        // 检查该字段是否有值（非 null、undefined、空字符串）
	        if (field.value !== null && field.value !== undefined && field.value.trim() !== '') {
	            return true; // 找到至少一个有效值
	        }
	    }

	    return false; // 其他字段都没有值
	}
</script>
</head>
<body>
    <div class="container-flux">
        <div class="row">
            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="text-align: left;">
                <div id="myTabContent" class="tab-content">
                    <nav class="navbar navbar-default" role="navigation">
                        <ul id="myTab" class="nav navbar-nav">
                            <li><a href="#statistic" data-toggle="tab" class="tab-bg-primary"><s:text name='prob.statistic.tab.list' /></a></li>
                            <li><a href="#report" data-toggle="tab" class="tab-bg-primary"><s:text name='prob.statistic.tab.report' /></a></li>
                            <li><a href="#projectList" data-toggle="tab" class="tab-bg-primary"><s:text name='prob.statistic.tab.project' /></a></li>
                            <li><a href="#shipmentSoftList" data-toggle="tab" class="tab-bg-primary">软件版本记录</a></li>
                            <li><a href="#probAffectedProjectSoftList" data-toggle="tab" class="tab-bg-primary">受影响软件版本记录</a></li>
                        </ul>
                    </nav>
                    <div class="tab-pane fade" id="statistic">
                        <s:form method="post" action="module/prob_statistics.action" id="statisticForm"
	                    cssClass="form-horizontal" name="statisticForm" enctype="multipart/form-data">
	                        <div class="form-group">
	                            <s:hidden name="probStatistic.tabIndex" value="0"></s:hidden>
                                <label for="versionConp" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label">软件版本：</label>
                                <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                                    <s:textfield id="versionConp" placeholder="软件版本"  name="probStatistic.version.conp" cssClass="form-control" />
                                </div>
	                            <label for="executeTime" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label">统计日期：</label>
	                            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
	                                <s:textfield id="startTime" placeholder="开始时间" name="probStatistic.startTime" cssClass="form-control" >
	                                   <%--  <s:param name="value">
	                                        <s:date name="probStatistic.startTime" format="yyyy-MM-dd"/>
	                                    </s:param> --%>
	                                </s:textfield>
	                            </div>
	                            <div class="pull-left"><span style="margin: 8px 5px;display:inline-block;">----</span></div>
	                            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
	                                <s:textfield id="endTime" placeholder="结束时间"  name="probStatistic.endTime" cssClass="form-control">
	                                    <%-- <s:param name="value">
	                                        <s:date name="probStatistic.endTime" format="yyyy-MM-dd"></s:date>
	                                    </s:param> --%>
	                                </s:textfield>
	                            </div>
	                            <div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
	                                <button type="button" id="submit"  class="btn btn-default  btn-block btn-sm" onclick="submitStatistic(0)"><s:text name='sys.query' /></button>
	                            </div>
	                            <div class="pull-left">
	                                <label for="mainForm_probStatistic_autoAdjust" style="margin: 4px 5px;">
	                                    <s:checkbox name="probStatistic.autoAdjust"></s:checkbox><s:text name="prob.statistic.autoAdjust"></s:text>
	                                </label>
	                            </div>
	                        </div>
	                    </s:form>
                        <display:table name="probStatisticList" pagesize="${displayParam.pagesize}"
                            export="true" size="${displayParam.totalcount}" sort="external"
                            requestURI="module/prob_statistics.action"
                            decorator="com.dp.plat.decorators.Wrapper"
                            class="table table-striped" partialList="true" >
                            <display:column property="projectCode"  media="excel" titleKey="pm.project.projectCode" ></display:column>
                            <display:column property="projectName" escapeXml="true" media="html" titleKey="pm.project.projectName" url="/module/ProjectModify.action" paramId="project.projectId" paramProperty="projectId"></display:column>
                            <display:column property="projectName"  media="excel" titleKey="pm.project.projectName" ></display:column>
                            <display:column property="itemName" titleKey="pm.orderdata.model"></display:column>
                            <display:column property="softInfo" titleKey="prob.statistic.softInfo" style="white-space: pre-line;word-break: break-word;word-wrap: break-word;"></display:column>
                            <display:column property="officeName" titleKey="pm.officearea" class="nowrap"></display:column>
                            <display:column property="serviceManagerName" titleKey="pm.project.serviceManager" class="nowrap"></display:column>
                            <display:column property="programManagerNameA" titleKey="pm.project.programManagerA" class="nowrap"></display:column>
                            <display:column property="programManagerNameB" titleKey="pm.project.programManagerB" class="nowrap"></display:column>
                            <display:column property="updateCount" titleKey="prob.statistic.updateCount"></display:column>
                            <display:column property="executeTime" titleKey="prob.statistic.executeTime" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
                            <display:column property="probTheme" media="html" titleKey="prob.statistic.probTheme" url="/module/prob_edit.action" paramId="prob.probId" paramProperty="probId"></display:column>
                            <display:column property="probTheme" media="excel" titleKey="prob.statistic.probTheme" url="/module/prob_edit.action" paramId="prob.probId" paramProperty="probId"></display:column>
                        </display:table>
                    </div>
                    <hr id="hr0" style="display:none;">
                    <div class="tab-pane fade" id="report">
                        <s:form method="post" action="module/prob_statistics.action" id="reportForm"
                        cssClass="form-horizontal" name="reportForm" enctype="multipart/form-data">
                            <div class="form-group">
                                <s:hidden name="probStatistic.tabIndex" value="1"></s:hidden>
                                <label for="executeTime" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label">统计日期：</label>
                                <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                                    <s:textfield id="startTime_" placeholder="开始时间" name="probStatistic.startTime" cssClass="form-control" >
                                        <%-- <s:param name="value">
                                            <s:date name="probStatistic.startTime" format="yyyy-MM-dd"/>
                                        </s:param> --%>
                                    </s:textfield>
                                </div>
                                <div class="pull-left"><span style="margin: 8px 5px;display:inline-block;">----</span></div>
                                <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                                    <s:textfield id="endTime_" placeholder="结束时间"  name="probStatistic.endTime" cssClass="form-control">
                                        <%-- <s:param name="value">
                                            <s:date name="probStatistic.endTime" format="yyyy-MM-dd"></s:date>
                                        </s:param> --%>
                                    </s:textfield>
                                </div>
                                <div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
                                    <button type="button" id="submit"  class="btn btn-default  btn-block btn-sm" onclick="submitStatistic(1)"><s:text name='sys.query' /></button>
                                </div>
                                <div class="pull-left">
                                    <label for="mainForm_probStatistic_autoAdjust" style="margin: 4px 5px;">
                                        <s:checkbox name="probStatistic.autoAdjust"></s:checkbox><s:text name="prob.statistic.autoAdjust"></s:text>
                                    </label>
                                </div>
                            </div>
                        </s:form>
                        <table class="table">
                          ${result}
                        </table>
                        <span class="text-info">提示：条件值 —— 已更新软件版本的项目数，总值 —— 项目总数</span>
                    </div>
                    <hr id="hr1" style="display:none;">
                    <div class="tab-pane fade" id="projectList">
                        <s:form method="post" action="module/prob_statistics.action" id="projectForm"
                        cssClass="form-horizontal clearfix" name="projectForm" enctype="multipart/form-data">
                            <div class="col-xs-2">
                                <s:hidden name="probStatistic.tabIndex" value="2"/>
                                <label for="projectCode" class="control-label">项目编码：</label>
                                <div class="">
                                    <s:textfield id="projectCode" placeholder="项目编码" name="probStatistic.projectCode" cssClass="form-control" />
                                </div>
                            </div>
                            <div class="col-xs-2">
                                <label for="projectName" class="control-label">项目名称：</label>
                                <div class="">
                                    <s:textfield id="projectName" placeholder="项目名称" name="probStatistic.projectName" cssClass="form-control" />
                                </div>
                            </div>
                            <div class="col-xs-2">
                                <label for="officeName" class=" control-label">办事处：</label>
                                <div class="">
                                    <s:select cssClass="form-control" name="probStatistic.officeCode" id="officeCode" list="%{departmentList}" listKey="departmentNum" listValue="departmentName" headerKey="" headerValue="-请选择-" theme="simple"></s:select>
                                </div>
					        </div>
                            <div class="col-xs-2">
                                <label for="sm" class="control-label"><s:text name="pm.project.serviceManager" />：</label>
					            <div class="">
						            <s:textfield id="sm" onfocus="fillsm()" onblur="fillsm()"
						                placeholder="支持模糊搜索" cssClass="form-control" />
						            <s:hidden name="probStatistic.serviceManagerCode" value="" id="smhide" class="smhide"></s:hidden>
					            </div>
					        </div>
                            <div class="col-xs-4">
					            <label for="pm" class="control-label"><s:text name="pm.project.programManager" /></label>
					            <div class="clearfix">
						            <div class="form-group col-xs-8" style="margin-right:0px;">
							            <s:textfield id="pm" onfocus="fillpm()" onblur="fillpm()"
							                placeholder="支持模糊搜索" cssClass="form-control" />
							            <s:hidden name="probStatistic.programManagerCode" value="" id="pmhide" class="pmhide"></s:hidden>
							        </div>
							        <div class="col-xs-4">
	                                    <button type="button" id="submit"  class="btn btn-default  btn-block btn-sm" onclick="submitStatistic(2)"><s:text name='sys.query' /></button>
	                                </div>
                                </div>
                            </div>
                            <%-- <div class="form-group col-xs-2">
	                            <div class="col-xs-7">
	                                <button type="button" id="submit"  class="btn btn-default  btn-block btn-sm" onclick="submitStatistic(2)"><s:text name='sys.query' /></button>
	                            </div>
                            </div> --%>
                        </s:form>
                        <display:table name="probProjectList" pagesize="${displayParam.pagesize}"
                            export="true" size="${displayParam.totalcount}" sort="external"
                            requestURI="module/prob_statistics.action"
                            decorator="com.dp.plat.decorators.Wrapper"
                            class="table table-striped" partialList="true">
                            <display:column property="projectCode" titleKey="pm.project.projectCode" ></display:column>
                            <display:column property="projectName" escapeXml="true" media="html" titleKey="pm.project.projectName" url="/module/ProjectModify.action" paramId="project.projectId" paramProperty="projectId"></display:column>
                            <display:column property="projectName"  media="excel" titleKey="pm.project.projectName" ></display:column>
                            <display:column property="contractNo" decorator="com.dp.plat.decorators.ContractNoList" titleKey="pm.project.contractNo"></display:column>
                            <display:column property="officeName" titleKey="pm.officearea" class="nowrap"></display:column>
                            <display:column property="serviceManagerCodeforjson" titleKey="pm.project.serviceManager" class="nowrap"></display:column>
                            <display:column property="programManagerCodeforjson" titleKey="pm.project.programManagerA" class="nowrap"></display:column>
                            <display:column property="programManagerCodeforjsonB" titleKey="pm.project.programManagerB" class="nowrap"></display:column>
                        </display:table>
                        <hr id="hr2" style="display:none;">
                    </div>
                    <div class="tab-pane fade" id="shipmentSoftList">
                        <s:form method="post" action="module/prob_statistics.action" id="shipmentSoftForm"
                        cssClass="form-horizontal clearfix" name="projectForm" enctype="multipart/form-data">
                            <div class="col-xs-2">
                                <s:hidden name="probStatistic.tabIndex" value="3"/>
                                <s:hidden name="probStatistic.filterItem" value="true"/>
                                <label for="projectCode" class="control-label">项目编码：</label>
                                <div class="">
                                    <s:textfield id="projectCode" placeholder="项目编码" name="probStatistic.projectCode" cssClass="form-control" />
                                </div>
                            </div>
                            <div class="col-xs-2">
                                <label for="contractNo" class="control-label">合同号：</label>
                                <div class="">
                                    <s:textfield id="contractNo" placeholder="合同号" name="probStatistic.contractNo" cssClass="form-control" />
                                </div>
                            </div>
                            <div class="col-xs-2">
                                <label for="projectName" class="control-label">项目名称：</label>
                                <div class="">
                                    <s:textfield id="projectName" placeholder="项目名称" name="probStatistic.projectName" cssClass="form-control" />
                                </div>
                            </div>
                            <div class="col-xs-2">
                                <label for="officeName" class=" control-label">办事处：</label>
                                <div class="">
                                    <s:select cssClass="form-control" name="probStatistic.officeCode" id="officeCode" list="%{departmentList}" listKey="departmentNum" listValue="departmentName" headerKey="" headerValue="-请选择-" theme="simple"></s:select>
                                </div>
                            </div>
                            <div class="col-xs-2">
                                <label for="sm" class="control-label"><s:text name="pm.project.serviceManager" />：</label>
                                <div class="">
                                    <s:textfield id="sm" onfocus="fillsm()" onblur="fillsm()"
                                        placeholder="支持模糊搜索" cssClass="sm form-control" />
                                    <s:hidden name="probStatistic.serviceManagerCode" value="" id="smhide" class="smhide"></s:hidden>
                                </div>
                            </div>
                            <div class="col-xs-2">
                                <label for="pm" class="control-label"><s:text name="pm.project.programManager" /></label>
                                <div class="">
                                    <s:textfield id="pm" onfocus="fillpm()" onblur="fillpm()"
                                        placeholder="支持模糊搜索" cssClass="pm form-control" />
                                    <s:hidden name="probStatistic.programManagerCode" value="" id="pmhide" class="pmhide"></s:hidden>
                                </div>
                            </div>
                            <div class="col-xs-2">
                                <label for="barCode" class="control-label">序列号：</label>
                                <div class="">
                                    <s:textfield id="barCode" placeholder="序列号" name="probStatistic.barCode" cssClass="form-control" />
                                </div>
                            </div>
                            <div class="col-xs-2">
                                <label for="conp" class="control-label">软件版本：</label>
                                <div class="">
                                    <s:textfield id="conp" placeholder="软件版本" name="probStatistic.version.conp" cssClass="form-control" />
                                </div>
                            </div>
                            <div class="col-xs-2">
                                <label for="itemCode" class="control-label">产品编码：</label>
                                <div class="">
                                    <s:textfield id="itemCode" placeholder="产品编码" name="probStatistic.itemCode" cssClass="form-control" />
                                </div>
                            </div>
                            <div class="col-xs-1">
                                <label for="" class="control-label"></label>
                                <div class="">
                                    <button type="button" id="submit"  class="btn btn-default  btn-block btn-sm" onclick="submitStatistic(3)"><s:text name='sys.query' /></button>
                                </div>
                            </div>
                            <%-- <div class="form-group col-xs-2">
                                <div class="col-xs-7">
                                    <button type="button" id="submit"  class="btn btn-default  btn-block btn-sm" onclick="submitStatistic(3)"><s:text name='sys.query' /></button>
                                </div>
                            </div> --%>
                        </s:form>
                        <display:table id="shipmentSoftList" name="commonList" pagesize="${displayParam.pagesize}"
                            export="true" size="${displayParam.totalcount}" sort="external"
                            requestURI="module/prob_statistics.action"
                            decorator="com.dp.plat.decorators.Wrapper"
                            class="table table-striped" partialList="true">
                            <display:column property="projectCode" titleKey="pm.project.projectCode" ></display:column>
                            <display:column property="projectName" escapeXml="true" media="html" titleKey="pm.project.projectName" url="/module/ProjectModify.action?result=310" paramId="project.projectId" paramProperty="projectId"></display:column>
                            <display:column property="projectName"  media="excel" titleKey="pm.project.projectName" ></display:column>
                            <display:column property="contractNo" decorator="com.dp.plat.decorators.ContractNoList" titleKey="pm.project.contractNo"></display:column>
                            <display:column property="officeName" titleKey="pm.officearea" class="nowrap"></display:column>
                            <display:column property="serviceManagerCodeforjson" titleKey="pm.project.serviceManager" class="nowrap"></display:column>
                            <display:column property="programManagerCodeforjson" titleKey="pm.project.programManagerA" class="nowrap"></display:column>
                            <display:column property="programManagerCodeforjsonB" titleKey="pm.project.programManagerB" class="nowrap"></display:column>
                            <display:column property="barCodeRelation" titleKey="pm.shipment.barCode"></display:column>
                            <display:column property="itemCodeRelation" titleKey="pm.shipment.itemCode"></display:column>
                            <display:column property="itemNameRelation" titleKey="pm.shipment.itemName"></display:column>
                            <display:column property="conp" titleKey="prob.info.conp"></display:column> 
                            <display:column property="cpld" titleKey="prob.info.cpld"></display:column> 
                            <display:column property="boot" titleKey="prob.info.boot"></display:column> 
                            <display:column property="pcb" titleKey="prob.info.pcb"></display:column>
                        </display:table>
                        <hr id="hr3" style="display:none;">
                    </div>
                    <div class="tab-pane fade" id="probAffectedProjectSoftList">
                        <%-- <jsp:include page="./sub/prob_affectedProjectSoftVersion.jsp"></jsp:include> --%>
                        <%@include file="./sub/prob_affectedProjectSoftVersion.jsp" %>
                        <hr id="hr4" style="display:none;">
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>