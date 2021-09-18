<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<dp:base />
<head>
<style type="text/css">
.pccSubmitDiv {
	margin-top: 10px;
	height: 120px;
}

.headerSpan {
	font-size: 14px;
	font-weight: 700;
}

a span {
	font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
	font-size: 14px;
	line-height: 1.428571429;
}

.pmclnotice {
	line-height: 24px;
	background: #F7F7F7;
	border: 1px dashed #CCC;
	padding: 15px;
	margin: 15px 0;
	color: #666;
	clear: both;
}

.headerLi {
	float: left;
	font-size: 12px;
	height: 30px;
	line-height: 30px;
	margin-right: 20px;
}

.pmclquescontent {
	line-height: 1.5;
	margin: 0 0 14px 10px;
	font-size: 14px;
	color: #333;
}

.content_pm_proplem {
	width: 820px;
	padding: 10px;
	width: 820px;
	padding-bottom: 15px;
	border-bottom: #e3ebeb 1px solid;
	margin-top: 10px;
}

.content_pm_proplem_type {
	color: #999;
	font-size: 12px;
	margin-left: 10px;
}

.content_pm_sort {
	font-weight: bold;
	color: #1473CB;
}

.mainDiv {
	height: auto !important;
	height: 500px;
	min-height: 500px;
	border: 2px solid #88ABDA;
	color: #999;
	padding: 26px 33px;
	text-align: left;
	border-radius: 10px 10px 10px 10px;
}
.tag:before {
    content: "*";
    vertical-align: middle;
}
.tag-must:before {
    color: red;
}
</style>
<script type="text/javascript">
	$(function() {
		$("#pmCLChoseQuesButt").change(function() {
			$("#projectWarrantyCallbackForm #warrantyCallbackProjectId").val(null);
			$("#projectWarrantyCallbackForm").submit();
			//window.location.href = window.location.pathname + "?project.projectId=" + $("#projectId").val() + "&pmClosedLoopQuesnaire.id=" + $(this).val();
		});
		//问卷保存草稿
		$("#quesnaire_draft").click(function(){
			$("#cbCLForm").submit();
		});
		//问卷提交
		$("#quesnaire_submit").click(function(){
			$("#quesnaireState").val(1);
			$("#cbCLForm").submit();
		});
		
		//submitFlow
		$("#submitButton").click(function(){
			for (var i = 0; i < $(".tag-must").length; i++) {
				var _this = $(".tag-must")[i];
				var $inpt = $(_this).next().find("[name*='projectWarrantyCallback.'], #pmCLChoseQuesButt");
				var name = $inpt.attr("name");
				var type = $inpt.attr("type");
                var $target = $("[name='" + name + "']" + (type == 'radio' || type == 'checkbox' ? ":checked" : ""));
                var value = $target.val();
				if (!value) {
					alert($.trim($(_this).text()).replace(/ |\r|\n/g, "") + "不能为空！");
					$target.focus();
					return false;
				}
			}
			for (var i = 0; i < $(".pmclquescontent").length; i++) {
				var _this = $(".pmclquescontent")[i];
				var $opt = $(".yl_one_item", $(_this)).find("[name^='pmClQuesnaireResultLineList']:first");
				var name = $opt.attr("name") || "";
				var type = $opt.attr("type");
				var $target = $("[name='" + name + "']" + (type == 'radio' || type == 'checkbox' ? ":checked" : ""), $(".yl_one_item", $(_this)));
				var $redmark = $(".redmark", $(_this));
				var value = $target.val();
                if (!value && $redmark.length > 0) {
                	var title = $.trim($redmark.text()) + $.trim($(".yl_title span:first", $(_this)).text());
                    alert(title.replace(/ |\r|\n/g, "") + "不能为空！");
                    $target.focus();
                    return false;
                }
			}
			$(this).parents("form:first").submit();
		});
		
		$("span.quesTypeScore").each(function(){
			if($(this).text().indexOf("|")>-1){
				var typeValue=$(this).text().split("|")[0];			 
				
				if($(this).attr("scoreType")=="3"){
					if($(this).text().split("|")[1]=="30"){  
						if($(this).next("span").text()){
							score30Value=parseInt($(this).next().text());						
						}
					}
					
					if($(this).text().split("|")[1]=="10"){  
						if($(this).next("span").text()){
							score10Value=parseInt($(this).next().text());						
						}
					}
				}
				$(this).text(typeValue);
			}
		});
		
		function adjustFrame() {
			var cbh = $("body").outerHeight(true) + 20;
	        $("#subcontractCallbackFrame", parent.document).height(cbh);
		}
		adjustFrame();
		
		//date_picker3("processTime");
		date_picker3("callbackTime");
        $("#callbackTime").datepicker('option',{showButtonPanel:true});
        
        date_picker3("nextCallbackTime");
        $("#nextCallbackTime").datepicker('option',{showButtonPanel:true});
		
		<%-- uploadCallback("${projectWarrantyCallback.deliverFileIds}"); --%>
	});
	var uploadDialog = 'AjaxUpload';
	function uploadTaskFile(projectId ,taskId){
	    popWindow('module/sub/upload.action?isAjax=true', 700, 450,'上传附件', uploadDialog, true);
	    
	    /* var projectId = projectId || $("#projectId").val();
        var column010 = "projectWarrantyCallback";
        var column011 = "";//$("#projectType").val();
        var contractNo = '${project.contractNo}';
        var eventKey = "warrantyCallbackTask-create";
	    popWindow("module/sub/ToUploadDeliverableFile.action?projectDeliver.projectId=" + projectId + "&projectDeliver.contractNo=" + contractNo + "&projectDeliver.column010=" + column010 +
            "&projectDeliver.column011=" + column011 + "&projectDeliver.eventKey=" + eventKey, 700, 450,'上传交付件', 'BudgetUpload', true); */
	    return false;
	}
	function uploadCallback(fileIds) {
		fileIds = $.trim(fileIds);
		if (!fileIds) {
			return;
		}
		var deliverFileIdArr = [];
		var deliverFileIds = $.trim($("#deliverFileIds").val());
		if (deliverFileIds) {
			deliverFileIdArr = deliverFileIds.split(",");
		}
		var fileIdArr = fileIds ? fileIds.split(",") : [];
		deliverFileIdArr = $.unique($.merge(deliverFileIdArr, fileIdArr)).sort()
		deliverFileIds = deliverFileIdArr.join(",");
		$("#deliverFileIds").val(deliverFileIds);
		showUploadFile(deliverFileIds);
		closeWindow(uploadDialog);
		
	}
	function showUploadFile(fileIds) {
		$.ajax({
            url : $("base").attr("href") + '/ajax/queryFile.action',
            type : 'POST',
            cache: false,
            data: {fileIds: fileIds},
            success : function(result) {
            	var fileList = result.fileList;
            	var $fileNames = $("#fileNames");
            	var tags = [];
            	for (var i = 0; i < fileList.length; i++) {
					var file = fileList[i];
					var fileName = file.fileName;
					var fileId = file.id;
					var filePath = file.filePath;
					var tag = '<a href="module/download.action?fileId=' + fileId + '" title="点击下载">' + fileName + '</a>';
					tags.push(tag);
				}
            	$("#fileNames").html(tags.join(" | "));
            }
		});
	}
</script>
</head>
<body>
    <s:form id="projectWarrantyCallbackForm" name="projectWarrantyCallbackForm" action="warrantyCallback_createProjectWarrantyCallback.action" method="post" class="warrantyCallbackForm">
	<div>
	    <%-- <s:if test="subcontractCallback.quesnaireState == 1"> --%>
	    <div class="panel panel-default">
		    <div class="panel-body">
                    <s:hidden id="projectId" name="project.projectId"></s:hidden>
                    <%-- <s:hidden id="warrantyCallbackId" name="projectWarrantyCallback.id"></s:hidden> --%>
                    <s:hidden id="warrantyCallbackProjectId" name="projectWarrantyCallback.projectId" value="%{project.projectId}"></s:hidden>
                    <s:hidden name="redirect"/>
                    
                    <!-- 项目基本信息 -->
                    <table id="projectWarrantyCallbackTable" class="table table-bordered table-hover table-striped noBorder">
                        <tr>
                            <td><s:text name="pm.project.projectCode"></s:text>:</td>
                            <td><s:property value="project.projectCode"/></td>
                            <td><s:text name="pm.project.contractNo"></s:text>:</td>
                            <td><s:property value="project.contractNo"/></td>
                        </tr>
                        <tr>
                            <td><s:text name="pm.project.projectName"></s:text>:</td>
                            <td><s:property value="project.projectName"/></td>
                            <td><s:text name="pm.project.officeName"></s:text>:</td>
                            <td><s:property value="project.officeName"/></td>
                        </tr>
                        <tr>
                            <td><s:text name="pm.project.implement"/>:</td>
                            <td><s:property value="projectWarrantyCallback.serviceName"/></td>
                            <td><s:text name="pm.project.warrantyCallback.industryName"/>:</td>
                            <td><s:property value="projectWarrantyCallback.industryName"/></td>
                        </tr>
                        <tr>
                            <td><s:text name="pm.project.agentChannel"/>:</td>
                            <td><s:property value="projectWarrantyCallback.agentChannel"/></td>
                            <td><s:text name="pm.project.finalCustomerName"/>:</td>
                            <td><s:property value="projectWarrantyCallback.finalCustomerName"/></td>
                        </tr>
                        <tr>
                            <td><s:text name="pm.project.programManager"/>A:</td>
                            <td><s:property value="project.programManagerCodeforjson"/></td>
                            <td><s:text name="pm.project.programManager"/>B:</td>
                            <td><s:property value="project.programManagerCodeforjsonB"/></td>
                        </tr>
                        <tr>
                            <td><s:text name="pm.project.warrantyCallback.customer"/>:</td>
                            <td>
                            	<div class="display-flex">
	                                <span class="display-flex-1" style="display:inline-block;"><s:property value="projectWarrantyCallback.customer1"/></span>
                            		<span class="display-flex-1" style="display:inline-block;"><s:property value="projectWarrantyCallback.customerContact1"/></span>
                            	</div>
                            </td>
                            <td><s:text name="pm.project.warrantyCallback.customer"/>2:</td>
                            <td>
                            	<div class="display-flex">
	                                <span class="display-flex-1" style="display:inline-block;"><s:property value="projectWarrantyCallback.customer2"/></span>
                            		<span class="display-flex-1" style="display:inline-block;"><s:property value="projectWarrantyCallback.customerContact2"/></span>
                            	</div>
                            </td>
                        </tr>
                        <tr>
	                        <td class="tag"><s:text name="pm.project.warrantyStatus"></s:text>:</td>
	                        <td>
	                            <div class="display-flex">
	                                <span class="display-flex-1" style="display:inline-block;">
	                                    <s:select name="projectWarrantyCallback.warrantyStatus" id="warrantyStatus" list="#{-1:'维保内', 0:'部分保内', 1:'维保外'}" value="%{projectWarrantyCallback.warrantyState.warrantyStatus}" headerKey="" headerValue="--请选择---" placeholder="维保状态" cssClass="form-control hidden" cssStyle="width: 180px;"/>
	                                    <%-- <span title="${projectWarrantyCallback.warrantyState.warrantyStatusDesc}">${projectWarrantyCallback.warrantyState.warrantyStatusName}</span> --%>
	                                    <s:property value="projectWarrantyCallback.warrantyState.warrantyStatusName"/>
	                                    <s:if test="projectWarrantyCallback.warrantyState.warrantyStatusName != null">
	                                        <span class="glyphicon glyphicon-question-sign" style="color:#bbb;" title="${projectWarrantyCallback.warrantyState.warrantyStatusDesc}"></span>
	                                        <!-- <br> -->
	                                        <span class="nowrap">
	                                            (<s:date name="projectWarrantyCallback.warrantyState.warrantyStartTime" format="yyyy-MM-dd" />~<s:date name="projectWarrantyCallback.warrantyState.warrantyEndTime" format="yyyy-MM-dd" />)
	                                        </span>
	                                    </s:if>
	                                </span>
	                            </div>
	                        </td>
	                        <td class="tag">续保记录:</td>
	                        <td>
	                            <div class="display-flex">
	                                <span class="display-flex-1" style="display:inline-block;">
	                                    <s:if test="projectWarrantyCallback.warrantyState.hasRenewal == 1">
	                                    	<%-- 有续保记录
	                                        <span class="glyphicon glyphicon-question-sign" style="color:#bbb;" title="${projectWarrantyCallback.warrantyState.renewalDesc}"></span> --%>
	                                        <!-- <br> -->
	                                        <pre style="border:none; background:transparent;"><s:property value="projectWarrantyCallback.warrantyState.renewalDesc"/></pre>
	                                    </s:if>
	                                    <s:else>无续保记录</s:else>
	                                </span>
	                            </div>
	                        </td>
	                        <td class="tag hidden">维保等级:</td>
	                        <td class="hidden">
	                            <div class="display-flex">
	                                <span class="display-flex-1" style="display:inline-block;">
	                                    <s:hidden name="projectWarrantyCallback.warrantyState.warrantyGrade"/>
	                                    <s:property value="projectWarrantyCallback.warrantyState.warrantyGradeName"/>
	                                    <s:if test="projectWarrantyCallback.warrantyState.warrantyGradeName != null">
	                                        <span class="glyphicon glyphicon-question-sign" style="color:#bbb;" title="${projectWarrantyCallback.warrantyState.warrantyGradeDesc}"></span>
	                                        <!-- <br> -->
	                                        <span class="nowrap">
	                                            <s:if test="projectWarrantyCallback.warrantyState.warrantyGradeEndTime != null">
	                                                <%-- (截止日期<s:date name="projectWarrantyCallback.warrantyState.warrantyGradeEndTime" format="yyyy-MM-dd" />) --%>
	                                                (<s:date name="projectWarrantyCallback.warrantyState.warrantyGradeStartTime" format="yyyy-MM-dd" />~<s:date name="projectWarrantyCallback.warrantyState.warrantyGradeEndTime" format="yyyy-MM-dd" />)
	                                            </s:if>
	                                            <s:elseif test="projectWarrantyCallback.warrantyState.warrantyEndTime != null">
	                                                <%-- (截止日期<s:date name="projectWarrantyCallback.warrantyState.warrantyEndTime" format="yyyy-MM-dd" />) --%>
	                                                (<s:date name="projectWarrantyCallback.warrantyState.warrantyStartTime" format="yyyy-MM-dd" />~<s:date name="projectWarrantyCallback.warrantyState.warrantyEndTime" format="yyyy-MM-dd" />)
	                                            </s:elseif>
	                                        </span>
	                                    </s:if>
	                                </span>
	                            </div>
	                        </td>
                        </tr>
                        <tr>
                            <td class="tag tag-must"><s:text name="pm.project.warrantyCallback.callbackTime"></s:text>:</td>
                            <td>
                                <s:textfield name="projectWarrantyCallback.callbackTime" id="callbackTime" placeholder="本次回访时间" cssClass="form-control" cssStyle="width: 180px;" autocomplete="off"/>
                            </td>
                            <td class="tag"><s:text name="pm.project.warrantyCallback.nextCallbackTime"></s:text>:</td>
                            <td>
                                <s:textfield name="projectWarrantyCallback.nextCallbackTime" id="nextCallbackTime" placeholder="下次回访时间" cssClass="form-control" cssStyle="width: 180px;" autocomplete="off"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="tag tag-must"><s:text name="pm.project.warrantyCallback.renewalIntention"></s:text>:</td>
                            <td><s:radio list="#{0:'无', 1:'有', 2:'待定'}" name="projectWarrantyCallback.renewalIntention"/></td>
                            <td class="tag"><s:text name="pm.remark"></s:text>:</td>
                            <td><s:textarea name="projectWarrantyCallback.remark" cssClass="form-control"/></td>
                        </tr>
                        <%-- <tr>
                            <td class="tag tag-must"><s:text name="pm.project.warrantyCallback.state"></s:text>:</td>
                            <td><s:radio list="#{false:'无', true:'有'}" name="projectWarrantyCallback.state"/></td>
                            <td class="tag"><s:text name="pm.remark"></s:text>:</td>
                            <td><s:textarea name="projectWarrantyCallback.remark" cssClass="form-control"/></td>
                        </tr> --%>
                        
                        <%-- <tr>
                            <td class="tag tag-must"><s:text name="pm.project.agentChannel"></s:text>:</td>
                            <td><s:textarea name="projectWarrantyCallback.agentChannel" cssClass="form-control"/></td>
                            <td class="tag tag-must">
                                <a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="uploadTaskFile(<s:property value='project.projectId' />)">
                                    <span class="glyphicon glyphicon-upload"></span> 上传附件</a>
                                    <br>
                                    <span>（督查表、设备配置信息等）</span>
                            </td>
                            <td>
                                <s:hidden name="projectWarrantyCallback.deliverFileIds" id="deliverFileIds"/>
                                <div id="fileNames"></div>
                            </td>
                        </tr> --%>
                        <%-- <tr>
                            <td colspan="2">
                                <s:if test="projectWarrantyCallback.id == null || projectWarrantyCallback.id == 0">
                                    <div class="text-left">
                                        <button id="submitButton" type="button" class="btn btn-info" style="margin-right:4px;">
                                            <span class="glyphicon glyphicon-floppy-disk" style="font-size:12px;"> 保存</span>
                                        </button>
                                    </div>
                                </s:if>
                            </td>
                        </tr> --%>
                    </table>
                    <s:if test="pmClosedLoopQuesnaireList != null">
                        <p class="text-info" style="margin-top:-0.75em;">提示：请先选择问卷，否则将丢失已填数据</p>
                    </s:if>
                    <s:else>
                        <div class="btn-group btn-group-sm">
                            <button id="submitButton" type="button" class="btn btn-info submitButton" style="margin-right:4px;" data-loading-text="正在处理...">
                                <span class="glyphicon glyphicon-floppy-disk" style="font-size:12px;"></span> 保存
                            </button>
                        </div>
                    </s:else>
                <%-- </s:form> --%>
            </div>
        </div>
	    <%-- </s:if>
	    <s:else> --%>
        <div id="quesnairePanel" >
	 		<!-- <div class="panel panel-default">
				<div class="panel-body"> -->
					<div class="form-group form-group-query">
                        <s:if test="pmClosedLoopQuesnaireList != null">
                            <label for="pmCLQuesName" style="width: 90px;" class="tag tag-must"><s:text name="pm.cl.questionnaireName" /></label>
                            <label>
                                <s:select id="pmCLChoseQuesButt" cssClass="form-control  btn-info" cssStyle="width: 160px; display: inline-block;" list="pmClosedLoopQuesnaireList" listKey="id" listValue="questionnaireTemplateName" headerKey="0" headerValue="--请选择--" name="pmClosedLoopQuesnaire.id"></s:select>
                            </label>
                            <div class="btn-group btn-group-sm" style="margin-left:80px;">
                                <button id="submitButton" type="button" class="btn btn-info submitButton" style="margin-right:4px;" data-loading-text="正在处理...">
                                    <span class="glyphicon glyphicon-floppy-disk" style="font-size:12px;"></span> 保存
                                </button>
                            </div>
                        </s:if>
                    </div>
				<!-- </div>
			</div> -->
		<%-- </s:else> --%>
		<s:if test="projectWarrantyCallback != null && cbForm.pmClosedLoopQuesnaireLineList.size()>0">
			<div id="cbCLDiv" style="margin-top: 21px;">
				<div class="panel-group" id="cbaccordion">
					<div class="panel panel-default">
						<div class="panel-heading">
						     <span class="headerSpan"><s:property value="pmClosedLoopQuesnaire.questionnaireTemplateName" /></span>
						</div>
						<div id="cbcollapseOne" class="panel-collapse collapse in">
							<div class="panel-body">
								<!-- 回访问卷描述 -->
								<!--  <div class="pmclnotice">
									<div class="info clearfix" style="height: 30px">
										<ul>
											<%-- <li class="headerLi"><s:text name="pm.cl.createdPerson"></s:text>：<span
												class="color-blue"><s:property
														value="pmClosedLoopQuesnaire.createdPerson" /></span></li>
											<li class="headerLi"><s:text name="pm.cl.createdTime"></s:text>：<span
												class="color-blue"><s:date
														name="pmClosedLoopQuesnaire.createdTime"
														format="yyyy-MM-dd" /></span></li> --%>
											<li class="headerLi"><span><s:text
														name="pm.cl.questionnaireScore"></s:text>:<s:property
														value="pmClosedLoopQuesnaire.questionnaireScore" /></span></li>
											<li class="headerLi"><span><s:text
														name="pm.cl.questionnairePassScore"></s:text>:<s:property
														value="pmClosedLoopQuesnaire.questionnairePassScore" /></span></li>
											<li class="headerLi"><span><s:text
														name="pm.cl.quesTyle"></s:text>:<s:property
														value="pmClosedLoopQuesnaire.quesTypeName" /></span></li>
										</ul>
									</div>
								</div>
                                -->

								<%-- <s:form method="post" action="querySubcontractCallback.action" id="cbCLForm" class="warrantyCallbackForm"> --%>
									<s:hidden name="pmClQuesnaireResultHeader.quesnaireTemplateHeaderId" value="%{pmClosedLoopQuesnaire.id}"></s:hidden>
									<s:hidden name="pmClQuesnaireResultHeader.quesTotalScore" value="%{pmClosedLoopQuesnaire.questionnaireScore}"></s:hidden>
									<s:hidden name="pmClQuesnaireResultHeader.quesPassScore" value="%{pmClosedLoopQuesnaire.questionnairePassScore}"></s:hidden>
									<s:hidden name="projectWarrantyCallback.id"></s:hidden>
									<s:hidden name="pmClQuesnaireResultHeader.status" value="-1" id="quesnaireState"></s:hidden>
									<!-- 问卷已提交 -->
									<s:if test="projectWarrantyCallback.quesnaireState == 1">
										<div> 
											<div class="pmclnotice">
												<span><s:text name="pm.cl.markRule"></s:text>：</span><br/>
												<s:iterator value="pmClosedLoopQuesnaire.markList" id="objmark" status="indexmark">
													<span class="glyphicon glyphicon-star" style="color:#2aabd2;font-size:8px;"></span><s:property value="%{#objmark.markExplain}"/><br/>
												</s:iterator><br/>
												<ul>
												<li class="headerLi">本次测评结果：</li>								
													<li class="headerLi">
														<span>（<s:text name="pm.cl.testTime"></s:text>：<s:date name="pmClQuesnaireResultHeader.createdTime" format="yyyy-MM-dd HH:mm:ss"/></span>
													</li>
													<li class="headerLi">
														<span><s:text name="pm.cl.testPerson"></s:text>：<s:property value="pmClQuesnaireResultHeader.createdPerson"/>）</span>
													</li>								
												</ul>
												<s:iterator value="cbForm.quesResultMarkList" id="objRMark" status="indexRmark">
													<s:if test="#indexRmark.odd">
														<span class="glyphicon glyphicon-star" style="color:red;font-size:8px;"></span><span class="quesTypeScore"><s:property value="%{#objRMark}"/></span>得分：
													</s:if>
													<s:else>
														<s:property value="%{#objRMark}"/><br/>
													</s:else>						
												</s:iterator>					
												<span class="glyphicon glyphicon-star" style="color:red;font-size:8px;"></span><s:text name="pm.cl.testTotalScore"></s:text>：<s:property value="pmClQuesnaireResultHeader.quesMarkScore"/><br/>
												<span class="glyphicon glyphicon-star" style="color:red;font-size:8px;"></span><s:text name="pm.cl.testTotalResult"></s:text>：					
												<s:if test="pmClQuesnaireResultHeader.quesMarkResult==-1">
													 测评不通过
												</s:if><s:else>
													测评通过
												</s:else>					
											</div>
										</div>
									</s:if> 
									<s:iterator value="cbForm.pmClosedLoopQuesnaireLineList" id="obja"
										status="indexa">
										<input type="hidden"
											name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].quesnaireTemplateLineId"
											value="<s:property value="%{#obja.id}"/>" />
										<input type="hidden"
											name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].quesTemplateLineNum"
											value="<s:property value="%{#obja.questionNum}"/>" />
										<input type="hidden"
											name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].quesTypeForCB"
											value="<s:property value="%{#obja.questionTypeForCB}"/>" />

										<div class="pmclquescontent">
											<div class="content_pm_proplem">
												<div class="yl_header">
													<table border="0" cellpadding="0" cellspacing="0"
														width="100%">
														<tbody>
															<tr>
																<td valign="top" style="width: 10px;"><s:if
																		test="#obja.questionType==3">
																		<div>
																			<span class="" style="color: black;">*<s:property value="#obja.questionNum" />.</span>
																		</div>
																	</s:if>
																	<s:else>
																		<div>
																			<span class="redmark">*<s:property value="#obja.questionNum" />.</span>
																			
																		</div>
																	</s:else>
																</td>
																<td>
																	<div class="yl_title" id="q_t_6416">
																		<p style="margin-right: 0px;">
																			<span><s:property
																					value="#obja.questionContent" /> </span>
																			<s:if test="#obja.questionType==1">
																				<span class="content_pm_proplem_type">[<s:text
																						name="pm.cl.quesOne"></s:text>]
																				</span>
																			</s:if>
																			<s:if test="#obja.questionType==3">
																				<span class="content_pm_proplem_type">[<s:text
																						name="pm.cl.quesAnw"></s:text>]
																				</span>
																			</s:if>
																			<s:iterator value="cbForm.quesTypeList" id="objqt"
																				status="indexqt">
																				<s:if
																					test="#obja.questionTypeForCB==#objqt.basicDataId">
																					<span class="content_pm_proplem_type">[<s:property
																							value="%{#objqt.basicDataName}" />]
																					</span>
																				</s:if>
																			</s:iterator>
																			<span class="content_pm_proplem_type">[<span><s:property
																						value="#obja.questionScore" /></span>分]
																			</span>
																		</p>
																	</div>
																	<div class="yl_tip"></div>
																</td>
															</tr>
														</tbody>
													</table>
												</div>

												<div class="yl_one_item">
													<table class="yl_one_item_tbl">
														<tbody>
															<tr>
																<s:iterator value="cbForm.pmClosedLoopQuesnaireOptList" id="objOpt" status="indexOpt">
                                                                    <s:if test="#objOpt.questionId==(#obja.id)">
																		<s:if test="#obja.questionType==1">
																			<s:if test="cbForm.pmClQuesnaireResultLineList==null">
																				<td>
                                                                                    <input type="radio" value="<s:property value="%{#objOpt.id}"/>"
    																					id="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"
    																					name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionTemplateOptId"
																					    style="margin-right: 10px;">
                                                                                </td>
																				<td>
                                                                                    <label style="margin-right: 28px;" 
                                                                                        for="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"><s:property
																							value="%{#objOpt.questionOptionsContent}" /><span
																						style="font-weight: 100; font-size: 8px;"
																						class="content_pm_proplem_type">（<s:property
																								value="%{#objOpt.questionOptionScore}" />分）
																					</span></label>
                                                                                </td>
																			</s:if>
																			<s:else>
																				<s:iterator value="cbForm.pmClQuesnaireResultLineList"
																					id="objRLine" status="indexRLine">
																					<s:if test="#objRLine.quesnaireTemplateLineId==#obja.id">
																						<s:if test="#objRLine.questionTemplateOptId==#objOpt.id">
																							<s:if test="#objRLine.quesEvaResult==-1">
																								<td><input type="radio" checked="checked"
																									value="<s:property value="%{#objOpt.id}"/>"
																									id="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"
																									name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionTemplateOptId"
																									style="margin-right: 10px;"></td>
																								<td style="color: red;"><label
																									style="margin-right: 28px;"
																									for="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"><s:property
																											value="%{#objOpt.questionOptionsContent}" /><span
																										style="font-weight: 100; font-size: 8px;"
																										class="content_pm_proplem_type">（<s:property
																												value="%{#objOpt.questionOptionScore}" />分）
																									</span></label></td>
																							</s:if>
																							<s:else>
																								<td><input type="radio" checked="checked"
																									value="<s:property value="%{#objOpt.id}"/>"
																									id="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"
																									name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionTemplateOptId"
																									style="margin-right: 10px;"></td>
																								<td><label style="margin-right: 28px;"
																									for="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"><s:property
																											value="%{#objOpt.questionOptionsContent}" /><span
																										style="font-weight: 100; font-size: 8px;"
																										class="content_pm_proplem_type">（<s:property
																												value="%{#objOpt.questionOptionScore}" />分）
																									</span></label></td>
																							</s:else>
																						</s:if>
																						<s:else>
																							<td><input type="radio"
																								value="<s:property value="%{#objOpt.id}"/>"
																								id="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"
																								name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionTemplateOptId"
																								style="margin-right: 10px;"></td>
																							<td><label style="margin-right: 28px;"
																								for="cbopt_<s:property value="%{#obja.id}"/>_<s:property value="%{#objOpt.questionOptionNum}"/>"><s:property
																										value="%{#objOpt.questionOptionsContent}" /><span
																									style="font-weight: 100; font-size: 8px;"
																									class="content_pm_proplem_type">（<s:property
																											value="%{#objOpt.questionOptionScore}" />分）
																								</span></label></td>
																						</s:else>
																					</s:if>
																				</s:iterator>
																			</s:else>
																		</s:if>
																	</s:if>
																</s:iterator>
																<s:if test="#obja.questionType==3">
																	<s:if test="cbForm.pmClQuesnaireResultLineList==null">
																		<td><textarea
																				name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionAnswer"
																				id="" name="" textSize="small" class="form-control"
																				rows="3" style="float: left; width: 350px;"></textarea><span
																			class="content_pm_proplem_type">[双击可放大或缩小]</span></td>
																	</s:if>
																	<s:else>
																		<s:iterator value="cbForm.pmClQuesnaireResultLineList"
																			id="objRLine" status="indexRLine">
																			<s:if
																				test="#objRLine.quesnaireTemplateLineId==#obja.id">
																				<td><textarea
																						name="pmClQuesnaireResultLineList[<s:property value="%{#indexa.index}"/>].questionAnswer"
																						id="" name="" textSize="small"
																						class="form-control" rows="3"
																						style="float: left; width: 350px;"><s:property
																							value="%{#objRLine.questionAnswer}" /></textarea><span
																					class="content_pm_proplem_type">[双击可放大或缩小]</span></td>
																			</s:if>
																		</s:iterator>
																	</s:else>
																</s:if>
															</tr>
														</tbody>
													</table>
												</div>
											</div>
										</div>
									</s:iterator>
									<%-- <s:if test="projectWarrantyCallback.quesnaireState != 1">
										<!-- 新问卷或者草稿 -->
										<div>
											<div class="btn-group btn-group-sm" style="margin-left:20px;">
											  <button id="quesnaire_draft"  type="button" class="btn btn-info" style="margin-right:4px;">
											  	<span class="glyphicon glyphicon-plus" style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="pm.cl.quesDraft"></s:text></span>
											  </button>
											</div>
											<div class="btn-group btn-group-sm" style="margin-left:20px;">
											  <button id="quesnaire_submit"  checkButton='check' type="button" class="btn btn-info" style="margin-right:4px;">
											  	<span class="glyphicon glyphicon-ok" style="font-size:12px;"></span><span style="font-size:12px;">&nbsp;&nbsp;<s:text name="pm.cl.quesSubmit"></s:text></span>
											  </button>
											</div>
										</div>
									</s:if> --%>
                                    <%-- <s:if test="%{projectWarrantyCallback.state == false && (user.isHasRole(10) || user.isHasRole(13) || user.isHasRole(14))}">
                                        <div class="btn-group btn-group-sm" style="margin-left:20px;">
                                            <button id="submitButton" type="button" class="btn btn-info" style="margin-right:4px;">
                                                <span class="glyphicon glyphicon-floppy-disk" style="font-size:12px;"></span> <s:text name="pm.cl.quesSubmit"></s:text>
                                            </button>
                                        </div>
                                    </s:if> --%>
								<%-- </s:form> --%>
							</div>
						</div>
					</div>
				</div>
			</div>
		</s:if>
		</div>
	</div>
    </s:form>
</body>
</html>