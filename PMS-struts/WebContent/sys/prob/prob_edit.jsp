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
<link rel="stylesheet" type="text/css" href="js/summernote/dist/summernote.css" />
<link rel="stylesheet" type="text/css" href="statics/plugins/select2/select2.min.css" />
<dp:link rel="stylesheet" type="text/css" href="css/prob/prob.css" />
<style>
	.probRestoreBoxId{
		display: none;
	}
	.modal-body .form-group{
		margin-right:0;
		margin-left:0;
	}
</style>
<script type="text/javascript" src="js/summernote/dist/summernote.min.js"></script>
<script type="text/javascript" src="js/summernote/dist/lang/summernote-zh-CN.min.js"></script>
<dp:script type="text/javascript" src="js/summernote/summernote-util.js"></dp:script>
<script type="text/javascript" src="statics/plugins/select2/select2.js"></script>
<script type="text/javascript" src="statics/plugins/select2/i18n/zh-CN.js"></script>
<dp:script type="text/javascript" src="js/prob/renderCascade.js"></dp:script>
<dp:script type="text/javascript" src="js/prob/render.js"></dp:script>
<script type="text/javascript">
	$(function(){
		//加载日期控件
		
		date_picker3("startdate");
		date_picker3("duedate");
		
	    
		$("#submit").click(function(){
			//对要必须的数据进行选择
			if(checkPost()){
				return true;
			}
			return false;
		});
		
		$("#confirm").click(function(){
			$(this).bootstrapBtn("loading");
			return true;
		});
		
		$(document).on("change","input[type='file'][name='upload']",function(){
			var path = $(this).val().split("\\");
			var end = path.length > 1 ? path.length-1 : 0;
			path = path[end].split("\/");
			end = path.length > 1 ? path.length-1 : 0;
			var fileName = path[end];
			$(this).hide();
			$(this).before("<input type='file' name='upload' class='form-control'>");
			$(this).after("<span class='text-primary'>"+fileName+"</span><a href='javascript:void(0)' onclick='deleteUnUploadFile(this)' title='删除'><img alt='删除' src='images/delete_profile.gif'>");
		});
		
	});
	
	function edit(_this){
		$editDiv = $(".editDiv");
		if($editDiv.is(":visible")){
			$(_this).text("编辑");
			$(".checkDiv").show();
			$editDiv.hide();
			$("#submit").hide();
			$("#title").text("查看技术公告");
		}else{
			$(_this).text("取消");
			$(".checkDiv").hide();
			$editDiv.show();
			$("#submit").show();
			$("#desc").focus();
			$("#solution").focus();
			$("#solution").blur();
			$("#title").text("编辑技术公告");
			
			// 初始化软件版本的输入框
			initSoftVersionInputs('manualSoftVersion', {
				typeContainer: 'softVersionTypes', 
				inputContainer: 'manualEntry',
				initTypeKey: 'conplat',
			});
			renderSoftVersions(softVersionJson, {$container: $("#softVersionList")});
			initProbProductBySelect2("probProducts", $("#probProductList"));
			initProbSelectBySelect2("relatedSceneTypes", $("#relatedSceneTypeList"));
		}
	}
	//检索版本
	function querySoftVersion(){
		popWindow('module/sub/toCheckSoftVersion.action', 900, 650,'查询软件版本', 'BudgetUpload', true);
		return false;
	}
	//检索设备
	function checkProject(){
		probId = $("#probId").val();
		popWindow('module/sub/checkProject.action?probRestore.probId='+probId+'&firstCheck=true&redirect='+window.location.href, 1300, 650,'设备清单', 'BudgetUpload', true);
		return false;
	}
	
	//用户管理任务
	function manageRestoreTask(){
		probId = $("#probId").val();
		popWindow('module/sub/managePrivateTask.action?probRestore.probId='+probId+'&redirect='+window.location.href, 1300, 650,'子任务管理', 'BudgetUpload', true);
		return false;
	}
	function manageAllRestoreTask(){
		probId = $("#probId").val();
		popWindow('module/sub/manageAllTask.action?probRestore.probId='+probId+'&probRestore.restoreStatus=30&redirect='+window.location.href, 1300, 650,'子任务管理', 'BudgetUpload', true);
		return false;
	}
	
	//查询阅读记录
    function queryReadLog(){
        probId = $("#probId").val();
        popWindow('module/sub/prob_readLog.action?probReadLog.probId='+probId, 1300, 650,'阅读记录', 'ProbReadLog', true);
        return false;
    }
	//确认阅读
    function readSure(){
    	probId = $("#probId").val();
    	var $btn = $("#readSureBtn");
    	$.ajax({
            url:"module/sub/probAjax_readSure.action",
            type:"post",
            dataType:"json",
            data:{"probReadLog.probId": probId},
            beforeSend: function()  {
            	$btn.bootstrapBtn("loading");
            },
            success:function(data){
                if (data.result == "success") {
                	alert("已确认！");
                } else {
                	alert("确认失败！");
                }      
            },
            complete:function(){
                window.location.reload();
            }
        });
    }
	
	/*
	* 删除未上传的文件
	*/
	function deleteUnUploadFile(_this){
		$(_this).prev().remove();
		$(_this).prev().remove();
		$(_this).remove();
	}
	/* 删除已上传的文件 */
	function deleteFile(fileId){
		$.ajax({
			url:"deleteFile.action",
			type:"post",
			dataType:"json",
			data:{fileId:fileId},
			success:function(data){
				alert(data.message);		
			},
			complete:function(){
				window.location.reload();
			}
		});
	}
	function checkAll(){
		$("#checkHeader").attr("onclick","checknoAll()");
		$(".probRestoreBoxId input[name='id']").each(function(){
			$(this).prop("checked","checked");
		});
	}
	function checknoAll(){
		$("#checkHeader").attr("onclick","checkAll()");
		$(".probRestoreBoxId input[name='id']").each(function(){
			$(this).removeAttr("checked");
		});
	}
	function deleteSingle(probRestoreId){
		if(confirm("确认要删除该子任务？")){
			$.ajax({
				url:'bacthDeleteProbRestores.action',
				type:'post',
				data:{'probRestoreIds':probRestoreId},
				dataType:'json',
				success: function(data){
					if(data.result == '200'){
						window.location.reload(true);
					}else{
						alert(data.result)
					}
				}
			})
		}
	}
	/* 手动输入软件版本 */
    function manualEntry() {
        $("#manualSoftVersion").show();
    }
	function clearSoftVersion() {
		$("#softVersionList").find(".softVersion").remove();
    }
	var softVersionJson = `${prob.affectedVersion}`;
	var probProductsJson = `${prob.customInfo.probProductList}`;
	var relatedSceneTypesJson = `${prob.customInfo.relatedSceneTypesJson}`;
	$(document).ready(function(){
		renderSoftVersions(softVersionJson, {
			$container: $("#affectedVersionList"), 
			readOnly: true, 
			ignoreSub: true
		});
        renderProbProducts(probProductsJson, {
            $container: $("#affectedProbProductList")
        });
        renderCommonLabel(relatedSceneTypesJson, {
            $container: $("#affectedRelatedSceneTypeList"),
            labelClass: 'label-info',
            key: 'id',
            text: 'text',
        });
		$('textarea').bind('input propertychange blur', function() {
			$(this).css('height','0px');
			$(this).css('height',this.scrollHeight + 'px');
		});
		
		$('#solution').summernote({       
	        focus: true,   
	        lang:'zh-CN',
	        placeholder:'请输入解决方案',
	        minHeight:'100px',
	        // 重写图片上传  
		   	callbacks: {
		   		onImageUpload: function(files) {  
		   			saveImageUpload(files,this);
		    	}
		   	}
		});
		$('#desc').summernote({       
	        focus: true,   
	        lang:'zh-CN',
	        placeholder:'请输入技术公告描述',
	        minHeight:'100px',
	        // 重写图片上传  
		   	callbacks: {
		   		onImageUpload: function(files) {  
		   			saveImageUpload(files,this);
		    	}
		   	}
		});
		$("#mainForm").submit(function(){
			/* var manualEntry = $("#manualEntry").val();
	        if (manualEntry) {
	            var index = $(".softVersion").length;
	            $("#manualEntry").attr("name", "softVersionList[" + index + "].manualEntry");
	        } else {
	            $("#manualEntry").attr("name", "");
	        } */
			$("input[name='prob.solution']").val($('#solution').summernote('code'));
			$("input[name='prob.desc']").val($('#desc').summernote('code'));
		})
		$('#solution').summernote('code',$("input[name='prob.solution']").val());
		$('#desc').summernote('code',$("input[name='prob.desc']").val());
		
		var ti = 0;
		$('#batchDelete').click(function(){
			var text = ["批量删除","取消"];
			ti = (ti%2 + 1)%2;
			$(this).text(text[ti]);
			$("#confirmDelete").toggle();
			$(".probRestoreBoxId").toggle();
			$("#checkHeader").removeAttr("checked");
			checknoAll();
		});
		$('#confirmDelete').click(function(){
			if($(".probRestoreBoxId input[name='id']:checked").length==0){
				alert("请选择需要删除的子任务！");
				return false;
			}
			var probRestoreIds = '0';
			$(".probRestoreBoxId input[name='id']:checked").each(function(){
				probRestoreIds += "," + $(this).val();
			});
			if(confirm("确认要删除所选子任务？")){
				$.ajax({
					url:'bacthDeleteProbRestores.action',
					type:'post',
					data:{'probRestoreIds':probRestoreIds},
					dataType:'json',
					success: function(data){
						if(data.result == '200'){
							window.location.reload(true);
						}else{
							alert(data.result)
						}
					}
				})
			}
		});
	});
</script>
</head>
<body>
	<div class="container-flux">
		<div class="row">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
				<div class="listView divHeader">
					<img src="images/right_zhishi.gif" border="0">
					<span id="title">
						<s:text name="prob.manage.check"></s:text>
					</span>
					<s:if test="user.isHasRole(18) == true">
						<span style="padding-left: 80%;"><a href="javascript:void(0)" id="edit" onclick="edit(this)">编辑</a></span>
					</s:if>
				</div>
				<s:form method="post" action="module/prob_edit.action" id="checkForm"
					cssClass="form-horizontal" name="checkForm" enctype="multipart/form-data">
					<s:hidden name="prob.probId" id="probId"></s:hidden>
					<div class="panel panel-default checkDiv">
						<!-- 信息查看 -->
						<div class="panel-body " style="background-color: beige;">
							<div class="form-group">
								<label for="num" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 form-control-label"><span class="redmark">*</span><s:text name="prob.info.num"></s:text></label>
								<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
									<s:property value="prob.probNum"/>	
								</div>
								<label for="watch" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 form-control-label"><span class="redmark">*</span><s:text name="prob.info.watch"></s:text></label>
								<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
									<s:property value="prob.watchName"/>
								</div>
							</div>
							<div class="form-group">
								<label for="theme" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 form-control-label"><span class="redmark">*</span><s:text name="prob.info.theme"></s:text></label>
								<div class="col-xs-9 col-sm-9 col-md-9 col-lg-9">
									<s:property value="prob.theme"/>	
								</div>
							</div>
							<div class="form-group">
								<label for="status" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 form-control-label"><span class="redmark">*</span><s:text name="prob.info.status"></s:text></label>
								<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
									<s:property value="prob.statusName"/>	
								</div>
								<label for="startdate" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 form-control-label"><s:text name="prob.info.start.date"></s:text></label>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<s:date name="prob.startdate" format="yyyy-MM-dd"/>	
								</div>
							</div>
							<div class="form-group">
								<label for="priority" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 form-control-label"><span class="redmark">*</span><s:text name="prob.info.level"></s:text></label>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<s:property value="prob.priorityName"/>	
								</div>
								<label for="duedate" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 form-control-label"><s:text name="prob.info.due.date"></s:text></label>
								<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
									<s:date name="prob.duedate" format="yyyy-MM-dd"/>	
								</div>
							</div>
                            <div class="form-group">
                                <label for="desc" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 form-control-label"><s:text name="prob.info.desc"></s:text></label>
                                <div class="col-xs-9 col-sm-9 col-md-9 col-lg-9">
                                    <s:property value="prob.desc"  escapeHtml="false"/> 
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="solution" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 form-control-label"><s:text name="prob.info.solution"></s:text></label>
                                <div class="col-xs-9 col-sm-9 col-md-9 col-lg-9">
                                    <s:property value="prob.solution" escapeHtml="false"/>  
                                </div>
                            </div>
							<div class="form-group">
								<label for="conp" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 form-control-label"><span class="redmark">*</span><s:text name="prob.info.affected.version"></s:text></label>
								<%-- <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<s:iterator value="softVersionList" var="software">
										<s:if test="#software.conp != null">
											conp:<s:property value="#software.conp"/>
										</s:if>
										<s:if test="#software.boot != null">
											boot:<s:property value="#software.boot"/>
										</s:if>
										<s:if test="#software.cpld != null">
											cpld:<s:property value="#software.cpld"/>
										</s:if>
										<s:if test="#software.pcb != null">
											pcb:<s:property value="#software.pcb"/>
										</s:if>
										<s:if test="#software.manualEntry != null">
                                            <s:property value="#software.manualEntry"/>
                                        </s:if>
										<br/>
									</s:iterator>
								</div> --%>
								<div class="col-xs-9 col-sm-9 col-md-9 col-lg-9" >
                                    <div id="affectedVersionList" class="softVersionList"></div>
                                </div>
								<s:hidden name="prob.status"></s:hidden>
								<s:if test="%{user.isHasRole(18) && (prob.status == 4 || prob.status == 5) && prob.watch == 14}">
									<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2 text-right">
										<a href="javascript:void(0)" onclick="checkProject()"><s:text name="prob.info.release.tasks"></s:text></a>
									</div>
								</s:if>
							</div>
                            <div class="form-group">
                                <label for="productType" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 form-control-label"><s:text name="prob.info.product.type"></s:text></label>
                                <div class="col-xs-9 col-sm-9 col-md-9 col-lg-9 probProductList" id="affectedProbProductList">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="relatedSceneTypes" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 form-control-label"><s:text name="prob.info.related.scene.types"></s:text></label>
                                <div class="col-xs-9 col-sm-9 col-md-9 col-lg-9 relatedSceneTypeList" id="affectedRelatedSceneTypeList">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="attachments" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 form-control-label"><s:text name="prob.info.attachments.down"></s:text></label>
                                <div class="col-xs-9 col-sm-9 col-md-9 col-lg-9">
                                    <s:iterator value="fileMap" var="file">
                                        <a href="module/download.action?fileId=<s:property value='key'/>" title="点击下载"> <s:property value="value"/> </a>    
                                        <s:if test="(user.getUsername() == prob.trackingUser) || (user.isHasRole(18) == true )">
                                            <a href="javascript:void(0)" onclick="deleteFile(<s:property value='key'/>)" title="删除"> 
                                                <img alt="删除" src="images/delete_profile.gif">
                                            </a>    
                                        </s:if>
                                    </s:iterator>
                                </div>
                            </div>
							<div class="form-group">
								<label for="solution" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 form-control-label"><s:text name="prob.tracking.user"></s:text></label>
								<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
									<s:property value="prob.trackingUsername"/>
								</div>
								<s:if test="%{prob.readStatus == 0}">
	                                <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
	                                    <a id="readSureBtn" href="javascript:void(0)" class="btn btn-info btn-sm" onclick="readSure()">阅读完毕</a>
	                                </div>
                                </s:if>
                                <s:if test="%{prob.readStatus == 1 || user.isHasRole(18) || user.isHasRole(19)}">
                                    <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                                        <a href="javascript:void(0)" class="btn btn-info btn-sm" onclick="queryReadLog()">阅读记录</a>
                                    </div>
                                </s:if>
							</div>
						</div>
					</div>
					<div class="panel panel-default checkDiv">
						<div class="listView divHeader">
							<img src="images/right_zhishi.gif" border="0">
							<s:text name="prob.info.restore.task"></s:text>
							<s:if test="(user.isHasRole(18) == false)&&(user.isHasRole(19) == false)">
							<!-- 非管理员管理个人任务 -->	
								<span style="padding-left: 80%;"><a href="javascript:void(0)" id="manageRestoreTask" onclick="manageRestoreTask()">管理任务</a></span>
							</s:if>
							<s:else>
							<!-- 管理员管理所有任务 -->
								<span style="padding-left: 80%;"><a href="javascript:void(0)" id="manageAllRestoreTask" onclick="manageAllRestoreTask()">管理任务</a></span>
							</s:else>
						</div>
						<div class="panel-body" >
							<div class="form-group">
								<%-- <label class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="prob.info.serial.num"></s:text></label>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<s:textfield name="probRestore.serialNum" cssClass="form-control"></s:textfield>
								</div> --%>
								<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="pm.project.projectName"></s:text></label>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<s:textfield name="probRestore.projectName" cssClass="form-control"></s:textfield>
								</div>
								<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="pm.contract"></s:text></label>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<s:textfield name="probRestore.contractNo" cssClass="form-control"></s:textfield>
								</div>
								<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="pm.officearea"></s:text></label>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<s:select name="probRestore.officeCode" id="officeCode"
												listKey="departmentNum" cssClass="form-control" headerKey=""
												headerValue="--请选择--" 
												listValue="departmentName" list="%{departmentList}" theme="simple" />
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="prob.info.status"></s:text></label>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<s:select name="probRestore.restoreStatus" list="restoreStatuList" listKey="basicDataId" listValue="basicDataName"
										cssClass="form-control" headerKey="" headerValue="--请选择--" ></s:select>		
								</div>
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
							   		<button type="submit" id="confirm" class="btn btn-default  btn-block btn-sm"><s:text name='sys.query' /></button>
						    	</div>	
					    	<s:if test="user.isHasRole(18)">	
						    	<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2 btn-group">
					    			<button type="button" id="batchDelete" class="col-xs-6 btn btn-default btn-sm">批量<s:text name='prob.info.delete' /></button>
					    			<button type="button" id="confirmDelete" class="col-xs-6 btn btn-danger btn-sm" style="display:none">确认<s:text name='prob.info.delete' /></button>
						    	</div>
					    	</s:if>					
							</div>
						</div>
					</div>
				</s:form>
				<div class="panel panel-default checkDiv">
					<div class="panel-body" >
						<div class="form-group">
							<display:table name="probRestoreTaskList" pagesize="${restoreDisplayParam.pagesize }"
								export="true" size="${restoreDisplayParam.totalcount }" sort="external"
								requestURI="module/prob_edit.action" id="probRestoreTaskList"
								decorator="com.dp.plat.decorators.Wrapper"
								class="table table-striped" partialList="true" >
							<s:if test="user.isHasRole(18) == true">
								<display:column property="probRestoreBox" titleKey="prob.info.checkbox" class="probRestoreBoxId" headerClass="probRestoreBoxId"></display:column>
							</s:if>
								<%-- <display:column property="serialNum" titleKey="prob.info.serial.num" sortable="true"></display:column>
								<display:column property="itemModel" titleKey="prob.info.product.type" sortable="true"></display:column> --%>
								<display:column property="projectNamea" media="html" titleKey="pm.project.projectName"></display:column>
								<display:column property="projectName"  media="excel" titleKey="pm.project.projectName" ></display:column>
								<display:column property="restoreStatusName" titleKey="prob.info.restore.status" sortable="true"></display:column>
								<display:column property="officeName" titleKey="pm.officearea"></display:column>
								<display:column property="createTime" titleKey="prob.info.createTime" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
								<display:column property="updateTime" titleKey="prob.info.updateTime" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
								<display:column property="restoreRemark" titleKey="prob.info.remark"></display:column>
								<display:column property="assignee" titleKey="prob.info.assignee"></display:column>
							<s:if test="user.isHasRole(18) == true">
								<display:column property="probRestoreDelete" titleKey="prob.info.delete"></display:column>
							</s:if>
							</display:table>
						</div>
					</div>
					<div class="panel-footer clearfix">
					 	<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
					 		<button type="button" style="width: 80px" class="btn btn-default btn-block btn-sm" onclick="javascript:history.back()"><s:text name='sys.back' /></button>
					 	</div>
					</div>
				</div>
					<%-- <div class="panel panel-default checkDiv">
						<div class="listView divHeader">
							<img src="images/right_zhishi.gif" border="0">
							<s:text name="prob.task.weekly.list"></s:text>
						</div>
						<div class="panel-body" >
							<div class="form-group">
								<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
									<display:table name="weeklyList" pagesize="${weeklyList.size() }"
										export="true" size="${weeklyList.size() }" sort="external"
										requestURI="module/prob_edit.action" id="weeklyList"
										decorator="com.dp.plat.decorators.Wrapper"
										class="table table-striped" partialList="true" >
										<display:column property="weeklyer" titleKey="file.name"></display:column>
										<display:column property="uploadUser" titleKey="file.uploadby" ></display:column>
										<display:column property="uploadTime" titleKey="file.uploadtime" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
									</display:table>
								</div>
							</div>
						</div>
					</div> --%>
					
				<s:if test="user.isHasRole(18) == true">	
				<s:form method="post" action="module/prob_update.action" id="mainForm"
					cssClass="form-horizontal" name="mainForm" enctype="multipart/form-data">		
					<div class="panel panel-default editDiv" style="display: none;">
						<!-- 编辑表单 -->
						<div class="panel-body">
							<s:hidden name="prob.probId" id="probId"></s:hidden>
							<div class="form-group">
								<!--<s:if test="prob.probId != 0">-->
									<label for="num" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><span class="redmark">*</span><s:text name="prob.info.num"></s:text></label>
									<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
										<s:textfield id="num" name="prob.probNum" 
											cssClass="form-control"></s:textfield>
									</div>
								<!--</s:if>-->
								<label for="watch" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><span class="redmark">*</span><s:text name="prob.info.watch"></s:text></label>
								<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
									<s:select id="watch" name="prob.watch" list="watchList" listKey="basicDataId" listValue="basicDataName"
										cssClass="form-control"></s:select>
								</div>
							</div>
							<div class="form-group">
								<label for="theme" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><span class="redmark">*</span><s:text name="prob.info.theme"></s:text></label>
								<div class="col-xs-9 col-sm-9 col-md-9 col-lg-9">
									<s:textfield id="theme" name="prob.theme" placeholder="请输入主题，为必填项"
										cssClass="form-control"></s:textfield>
									<span id="themeMsg"></span>	
								</div>
							</div>
							<div class="form-group">
								<label for="desc" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><s:text name="prob.info.desc"></s:text></label>
								<div class="col-xs-9 col-sm-9 col-md-9 col-lg-9">
									<%-- <s:textarea id="desc"  name="prob.desc" placeholder="请输入技术公告描述"  
									cssClass="form-control"   cssStyle="overflow-y:hidden;min-height:48px;" ></s:textarea>
								 --%>
									<s:hidden name="prob.desc"/>
									<div id="desc" name="prob.desc">
									</div>
								 </div>
							</div>
							<div class="form-group">
								<label for="solution" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><s:text name="prob.info.solution"></s:text></label>
								<div class="col-xs-9 col-sm-9 col-md-9 col-lg-9">
									<s:hidden name="prob.solution"/>
									<div id="solution" name="prob.solution">
									</div>
									<%-- <s:textarea id="solution" name="prob.solution" placeholder="请输入解决方案" 
										cssClass="form-control" cssStyle="overflow-y:hidden;min-height:48px;" 
									></s:textarea> --%>
								</div>
							</div>
							<div class="form-group">
                                <label for="priority" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><span class="redmark">*</span><s:text name="prob.info.level"></s:text></label>
                                <div class="col-xs-4">
                                    <s:select id="priority" name="prob.priority" list="priorityList" listKey="basicDataId" listValue="basicDataName"
                                        cssClass="form-control"></s:select>
                                </div>
                                <label for="status" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><span class="redmark">*</span><s:text name="prob.info.status"></s:text></label>
                                <div class="col-xs-2">
                                    <s:select id="status" name="prob.status" list="statusList" listKey="basicDataId" listValue="basicDataName"
                                        cssClass="form-control"></s:select>
                                </div>
                                <label for="visibleRange" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><span class="redmark">*</span><s:text name="prob.info.visibleRange"></s:text></label>
                                <div class="col-xs-1">
                                    <input type="radio" name="prob.visibleRange" id="mainForm_prob_visibleRange0" value="0" ${prob.visibleRange == 0 ? "checked='checked'" : ""}>
                                    <label for="mainForm_prob_visibleRange0">全部</label>
                                    <br>
                                    <input type="radio" name="prob.visibleRange" id="mainForm_prob_visibleRange1" value="1" ${prob.visibleRange == 1 ? "checked='checked'" : ""}>
                                    <label for="mainForm_prob_visibleRange1">仅搜索</label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="startdate" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><s:text name="prob.info.start.date"></s:text></label>
                                <div class="col-xs-4">
                                    <s:textfield id="startdate" name="prob.startdate" placeholder="请选择开始日期"
                                        cssClass="form-control" ></s:textfield>
                                </div>
                                <label for="duedate" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="prob.info.due.date"></s:text></label>
                                <div class="col-xs-4">
                                    <s:textfield id="duedate" placeholder="请选择计划完成日期" name="prob.duedate"
                                        cssClass="form-control"></s:textfield>
                                </div>
                            </div>
							<%--<div class="form-group">
								<label for="productType" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><s:text name="prob.info.product.type"></s:text></label>
								<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
									<s:textfield id="productType" name="prob.productType" cssClass="form-control"></s:textfield>
								</div>
								<label for="attachments" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="prob.info.attachments"></s:text></label>
								<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
									<input type="file" name="upload" class="form-control"/>
									<s:iterator value="fileMap" var="file">
							 			<a href="module/download.action?fileId=<s:property value='key'/>" title="点击下载"> <s:property value="value"/> </a>  	
							 			<a href="javascript:void(0)" onclick="deleteFile(<s:property value='key'/>)" title="删除"> 
							 				<img alt="删除" src="images/delete_profile.gif">
							 			</a>  	
								 	</s:iterator>
								</div>
							</div> --%>
							<%-- <div class="form-group">
								<label for="conp" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><span class="redmark">*</span><s:text name="prob.info.affected.version"></s:text></label>
								<div class="col-xs-5 col-sm-5 col-md-5 col-lg-5" id="softversionlist">
								    <div id="manualsoftversion">
                                        <s:textfield id="manualentry" cssclass="form-control"></s:textfield>
                                    </div>
                                    <s:iterator value="softversionlist" var="software" status="status">
                                        <span class='softversion'>
                                            <s:if test="#software.conp != null">
                                                <input type="hidden" name="softversionlist[${status.index}].conp" value="${software.conp}"> 
                                                conp:<s:property value="#software.conp"/>
                                            </s:if>
                                            <s:if test="#software.boot != null">
                                                <input type="hidden" name="softversionlist[${status.index}].boot" value="${software.boot}"> 
                                                boot:<s:property value="#software.boot"/>
                                            </s:if>
                                            <s:if test="#software.cpld != null">
                                                <input type="hidden" name="softversionlist[${status.index}].cpld" value="${software.cpld}">
                                                cpld:<s:property value="#software.cpld"/>
                                            </s:if>
                                            <s:if test="#software.pcb != null">
                                                <input type="hidden" name="softversionlist[${status.index}].pcb" value="${software.pcb}">
                                                pcb:<s:property value="#software.pcb"/>
                                            </s:if>
                                            <s:if test="#software.manualentry != null">
                                                <input type="hidden" name="softversionlist[${status.index}].manualentry" value="${software.manualentry}">
                                                <s:property value="#software.manualentry"/>
                                            </s:if>
                                            <br/>
                                        </span>
                                    </s:iterator>
								</div>
								<!--old 已确认 状态可以检索版本 -->
								<!--new 新创建或待确认的技术公告允许修改  -->
								<％-- <s:if test="%{prob.status == 0 || prob.status == 1 || prob.status == 8}">  --％>
									<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
										<a href="javascript:void(0)" onclick="querySoftVersion()">点击查找</a>
									</label>
									<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
                                        <a href="javascript:void(0)" onclick="manualentry()">手动输入</a>
                                    </label>
									<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
                                        <a href="javascript:void(0)" onclick="clearsoftversion()">清除影响版本</a>
                                    </label>
								<％-- </s:if>  --％>
							</div> --%>
							<div class="form-group">
								<label for="conp" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><span class="redmark">*</span><s:text name="prob.info.affected.version"></s:text></label>
								<!--old 已确认 状态可以检索版本 -->
								<!--new 新创建或待确认的技术公告允许修改  -->
								<%-- <s:if test="%{prob.status == 0 || prob.status == 1 || prob.status == 8}">  --%>
									<!-- <label class="col-xs-1 col-sm-1 col-md-1 col-lg-1 softVersionLink">
										<a href="javascript:void(0)" onclick="querySoftVersion()">点击查找</a>
									</label> -->
									<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1 softVersionLink">
                                        <a href="javascript:void(0)" onclick="manualEntry()">手动输入</a>
                                    </label>
									<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1 softVersionLink">
                                        <a href="javascript:void(0)" onclick="clearSoftVersion()">清除影响版本</a>
                                    </label>
								<%-- </s:if>  --%>
							</div>
							<div class="form-group">
								<label for="conp" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"></label>
								<div class="col-xs-9 col-sm-9 col-md-9 col-lg-9">
                                    <div id="manualSoftVersion">
                                    	<s:select id="affectedType" cssClass="form-control" style="width: 120px" list="#{1:'盒式系列',2:'框式系列',-1:'其它系列'}" headerKey="0" headerValue="所有系列"></s:select>
                                        <div id="softVersionTypes" class="display-flex softVersionTypes"></div>
                                        <%-- <s:textfield id="manualEntry" cssClass="form-control softVersionInput" placeholder="请填写包含的版本"></s:textfield> --%>
                                        <s:textfield id="manualEntryStart" cssClass="form-control softVersionInput softVersionInputPart" placeholder="请填写包含的起始版本"></s:textfield>
                                        <s:textfield id="manualEntryEnd" cssClass="form-control softVersionInput softVersionInputPart" placeholder="请填写包含的结束版本"></s:textfield>
                                        <button type="button" id="manualSubmit" class="btn btn-default">添加</button>
                                    </div>
                                    <div id="softVersionList" class="softVersionList"></div>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="productType" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><span class="redmark">*</span><s:text name="prob.info.product.type"></s:text></label>
                                <div id="probProductList" class="col-xs-9 col-sm-9 col-md-9 col-lg-9 probProductList">
                                    <%-- <s:textfield id="productType" name="prob.productType" cssClass="form-control"></s:textfield> --%>
                                    <s:hidden id="probProducts_hidden" name="prob.customInfo.probProductList" ></s:hidden>
                                    <%-- <s:select id="probProducts" list="#{}" name="prob.customInfo.probProductItems" data-selected="%{prob.customInfo.probProductItems}" multiple="true" cssClass="form-control select2" ></s:select> --%>
                                    <s:select id="probProducts" list="#{}" multiple="true" cssClass="form-control select2" ></s:select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="relatedSceneTypes" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><s:text name="prob.info.related.scene.types"></s:text></label>
                                <div id="relatedSceneTypeList" class="col-xs-9 col-sm-9 col-md-9 col-lg-9 relatedSceneTypeList">
                                    <%-- <s:textfield id="productType" name="prob.productType" cssClass="form-control"></s:textfield> --%>
                                    <s:hidden id="relatedSceneTypesJson_hidden" name="prob.customInfo.relatedSceneTypesJson" ></s:hidden>
                                    <s:hidden id="relatedSceneTypes_hidden" name="prob.customInfo.relatedSceneTypes" ></s:hidden>
                                    <s:hidden id="relatedSceneTypesName_hidden" name="prob.customInfo.relatedSceneTypesName" ></s:hidden>
                                    <s:select id="relatedSceneTypes" name="prob.relatedSceneTypes" list="relatedSceneTypeList" listKey="basicDataId" listValue="basicDataName"  multiple="true" cssClass="form-control select2" ></s:select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="attachments" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><s:text name="prob.info.attachments"></s:text></label>
                                <div class="col-xs-4">
                                    <input type="file" name="upload" class="form-control"/>
                                    <s:iterator value="fileMap" var="file">
                                        <a href="module/download.action?fileId=<s:property value='key'/>" title="点击下载"> <s:property value="value"/> </a>    
                                        <s:if test="user.isHasRole(20) == true && (user.getUsername() == prob.trackingUser || prob.probId == 0)">
                                            <a href="javascript:void(0)" onclick="deleteFile(<s:property value='key'/>)" title="删除"> 
                                                <img alt="删除" src="images/delete_profile.gif">
                                            </a>
                                        </s:if>
                                        &nbsp;  
                                    </s:iterator>
                                </div>
                            </div>
						</div>
						<div class="panel-footer clearfix">
							<div class="form-group">
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 col-xs-offset-5">
									<button type="button" style="width: 80px" class="btn btn-default btn-block btn-sm" onclick="edit('#edit')"><s:text name='sys.back' /></button>
							    </div>
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
								   	<button type="submit" id="submit" style="width: 80px;display: none" class="btn btn-default  btn-block btn-sm"><s:text name='prob.info.update' /></button>
							    </div>
						    </div>
						</div>
					</div>
				</s:form>
				</s:if>
			</div>
		</div>
	</div>
</body>
</html>