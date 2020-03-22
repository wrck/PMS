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
<script type="text/javascript" src="js/summernote/dist/summernote.min.js"></script>
<script type="text/javascript" src="js/summernote/dist/lang/summernote-zh-CN.js"></script>
<script type="text/javascript" src="js/summernote/summernote-util.js"></script>
<style>
	.modal-body .form-group{
		margin-right:0;
		margin-left:0;
	}
	.form-text{
	    display: block;
	    width: 100%;
	    padding: 6px 12px;
	    font-size: 12px;
	    line-height: 1.42857143;
	    color: #555;
	    background-color: #fff;
	    background-image: none;
	    border: 1px solid #ccc;
	    border-radius: 4px;
	    -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
	    box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
	    -webkit-transition: border-color ease-in-out .15s,-webkit-box-shadow ease-in-out .15s;
	    -o-transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
	    transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
	}
	
	.form-text[readonly]{
		background-color: #eee;
    	opacity: 1;
	}
	#manualSoftVersion {
	    padding: 0 15px;
	    margin-left: -15px;
        width: calc(80% + 24px);
        display: none;
	}
</style>
<script type="text/javascript">
	$(function(){
		//加载日期控件
		
		date_picker3("startdate");
		date_picker3("duedate");
		
	/* 	desc = document.getElementById("desc");
	    autoTextarea(desc);// 调用 */
		
	    /* solution = document.getElementById("solution");
	    autoTextarea(solution); */
	    
		$("#create").click(function(){
			$("#mainForm").attr("action","module/prob_save.action");
			$("input[name='prob.solution']").val($('#solution').summernote('code'));
			$("input[name='prob.desc']").val($('#desc').summernote('code'));
			//对要必须的数据进行选择
			if(checkPost()){
				$("#mainForm").submit();
				return true;
			}
			return false;
		});
	    $("#update").click(function(){
	    	$("#mainForm").attr("action","module/prob_update.action");
	    	$("input[name='prob.solution']").val($('#solution').summernote('code'));
			$("input[name='prob.desc']").val($('#desc').summernote('code'));
			//对要必须的数据进行选择
			if(checkPost()){
				$("#mainForm").submit();
				return true;
			}
			return false;
		});
		$("#reject").click(function(){
			var probId = $("#probId").val();
			var remark = $("#remark").val();
			$("input[name='prob.solution']").val($('#solution').summernote('code'));
			$("input[name='prob.desc']").val($('#desc').summernote('code'));
			//对要必须的数据进行选择
            if(!checkPost()){
                return false;
            }
			var params = $("#mainForm").serializeArray();
			params.push({'name':'prob.status','value':6});
			$.ajax({
				url:"probAudit.action",
				type:"post",
				dataType:"json",
				//data:{"prob.probId":probId,"prob.status":"6","prob.remark":remark},
				data: params,
				success: function(data){
					if(data.result == "success"){
						window.location.href = "module/Workspace!probTask.action";
					}else{
						alert(data.result);
					}
				}
			});
		});	
		
		$("#pass").click(function(){
			var probId = $("#probId").val();
			var remark = $("#remark").val();
			$("input[name='prob.solution']").val($('#solution').summernote('code'));
			$("input[name='prob.desc']").val($('#desc').summernote('code'));
			//对要必须的数据进行选择
            if(!checkPost()){
                return false;
            }
			var params = $("#mainForm").serializeArray();
			params.push({'name':'prob.status','value':4});
			$.ajax({
				url:"probAudit.action",
				type:"post",
				dataType:"json",
				//data:{"prob.probId":probId,"prob.status":"4","prob.remark":remark},
				data: params,
				success: function(data){
					if(data.result == "success"){
						window.location.href = "module/Workspace!probTask.action";
					}else{
						alert(data.result);
					}
				}
			});
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
	/*
	* 检查要提交的参数
	*/
	function checkPost(){
		fields = new Array('num','theme');
		for(i = 0 ;i < fields.length ; i++){
			if(!checkField(fields[i])){
				return false;
			}
		}
		var manualEntry = $("#manualEntry").val();
		if (manualEntry) {
			var index = $(".softVersion").length;
			$("#manualEntry").attr("name", "softVersionList[" + index + "].manualEntry");
		} else {
			$("#manualEntry").attr("name", "");
		}
		return true;
	}
	
	function checkField(fieldId){
		$field = $("#"+fieldId);
		if($field.val() == ''){
			$("#"+fieldId+"Msg").text("此字段为必须输入项").addClass("redMark");
			$field.focus();
			return false;
		}else{
			$("#"+fieldId+"Msg").text("").removeClass("redMark");
			return true;
		}
		return true;
	}
	
	function querySoftVersion(){
		popWindow('module/sub/toCheckSoftVersion.action?redirect='+window.location.href, 900, 650,'查询软件版本', 'BudgetUpload', true);
		return false;
	}
	/* 手动输入软件版本 */
	function manualEntry() {
		$("#manualSoftVersion").show();
	}
	function clearSoftVersion() {
		$("#softVersionList").find(".softVersion").remove();
	}
	$(document).ready(function(){
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
		
		$('#theme').val($("#theme").val() || '<s:text name="prob.info.theme.default"></s:text>');
		$('#solution').summernote('code',$("input[name='prob.solution']").val() || '<s:text name="prob.info.solution.template"></s:text>');
		$('#desc').summernote('code',$("input[name='prob.desc']").val() || '<s:text name="prob.info.desc.template"></s:text>');
		
		var isProbAdmin = $("#isProbAdmin").val();
		var isRdRole = "<s:property value='user.isHasRole(20)'/>" == "true";
		var isPadRole = "<s:property value='user.isHasRole(18)'/>" == "true";
		var currentUser = "<s:property value='user.getUsername()'/>";
		var trackingUser = "<s:property value='prob.trackingUser'/>";
		var probId = "<s:property value='prob.probId'/>";
		var status = "<s:property value='prob.status'/>";
		
		//if(isProbAdmin != 3){
		//if(!isRdRole || (isRdRole && (currentUser == trackingUser))){
		if((currentUser != trackingUser && probId != 0 && !isPadRole) || (isPadRole && (status != 1 && status != 8))){
			$("input[type='text']").each(function(){
				$(this).attr("readonly",true);
			});
			$("input[type='file'], input[type='radio']").each(function(){
				$(this).attr("disabled",true);
			});
			$("select").each(function(){
				$(this).attr("disabled",true);
			});
			$('#solution').summernote('destroy');
			$('#desc').summernote('destroy');
			
			$(".form-text").each(function(){
				$(this).attr("readonly",true);
			});
		}
		
		if(isRdRole){
			$("#startdate").val(CurentDate());
		}
		//if(isProbAdmin == 1){
			
		if(isPadRole && (status == 1 || status == 8)){
			$('#remark').removeAttr("readonly");
			if ($('#remark').length > 0) {
			    autoTextarea(document.getElementById("remark"));
			}
			$("input[type='file'][name='upload']").each(function(){
                $(this).removeAttr("disabled");
            });
		}
		
	});
	
</script>
</head>
<body>
	<div class="container-flux">
	
		<div class="row">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
				<div class="listView divHeader">
					<img src="images/right_zhishi.gif" border="0">
					<s:if test="prob.probId != 0">
						<s:text name="prob.manage.edit"></s:text>
					</s:if>
					<s:else>
						<s:text name="prob.manage.create"></s:text>
					</s:else>
				</div>
				<s:form method="post" action="module/prob_save.action" id="mainForm"
					cssClass="form-horizontal" name="mainForm" enctype="multipart/form-data">
					<s:hidden name="isContinue" value="0" id="isContinue"></s:hidden>
					<s:hidden name="isProbAdmin" id="isProbAdmin"></s:hidden>
					<s:hidden name="prob.probId" id="probId"></s:hidden>
					<div class="panel panel-default">
						<div class="panel-body">
							<!-- <div class="form-group">
								<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1"></div>
								<label class="redMark col-xs-8 col-sm-8 col-md-8 col-lg-8"> 备注：请先选择受影响的软件版本，再填写技术公告的其他信息。</label>
							</div> -->
							<div class="form-group">
								<label for="num" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><span class="redmark">*</span><s:text name="prob.info.num"></s:text></label>
								<div class="col-xs-4">
									<s:textfield id="num" name="prob.probNum"
										cssClass="form-control"></s:textfield>
								</div>
								
								<label for="watch" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><span class="redmark">*</span><s:text name="prob.info.watch"></s:text></label>
								<div class="col-xs-4">
									<s:select id="watch" name="prob.watch" list="watchList" listKey="basicDataId" listValue="basicDataName"
										cssClass="form-control"></s:select>
								</div>
							</div>
							<div class="form-group">
								<label for="theme" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><span class="redmark">*</span><s:text name="prob.info.theme"></s:text></label>
								<div class="col-xs-9">
									<s:textfield id="theme" name="prob.theme" placeholder="请输入主题，为必填项" 
										cssClass="form-control"></s:textfield>
									<span id="themeMsg"></span>	
								</div>
							</div>
							<div class="form-group">
								<label for="desc" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><s:text name="prob.info.desc"></s:text></label>
								<div class="col-xs-9">
									<s:hidden name="prob.desc"/>
									<div id="desc" class="form-text" name="prob.desc">
									</div>
									<%-- <s:textarea id="desc" name="prob.desc" placeholder="请输入技术公告描述" rows="3"
										cssClass="form-control"></s:textarea> --%>
								</div>
							</div>
							<div class="form-group">
								<label for="solution" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><s:text name="prob.info.solution"></s:text></label>
								<div class="col-xs-9">
									<s:hidden name="prob.solution"/>
									<div id="solution" class="form-text" name="prob.solution">
									</div>
									<%-- <s:textarea id="solution" name="prob.solution" placeholder="请输入解决方案" rows="3"
										cssClass="form-control"></s:textarea> --%>
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
										cssClass="form-control" disabled="true"></s:select>
								</div>
								<label for="status" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><span class="redmark">*</span><s:text name="prob.info.visibleRange"></s:text></label>
								<%-- <div class="col-xs-1">
									<s:radio list="#{'0':'全部','1':'仅搜索'}" name="prob.visibleRange" value="prob.visibleRange" />
								</div> --%>
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
							<div class="form-group">
								<label for="productType" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><s:text name="prob.info.product.type"></s:text></label>
								<div class="col-xs-4">
									<s:textfield id="productType" name="prob.productType" cssClass="form-control"></s:textfield>
								</div>
								<label for="attachments" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="prob.info.attachments"></s:text></label>
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
							<div class="form-group">
								<label for="conp" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><span class="redmark">*</span><s:text name="prob.info.affected.version"></s:text></label>
								<div class="col-xs-5 col-sm-5 col-md-5 col-lg-5" id="softVersionList">
                                    <div id="manualSoftVersion">
                                        <s:textfield id="manualEntry" cssClass="form-control"></s:textfield>
                                    </div>
									<s:iterator value="softVersionList" var="software" status="status">
										<span class='softVersion'>
											<s:if test="#software.conp != null">
												<input type="hidden" name="softVersionList[${status.index}].conp" value="${software.conp}"> 
												conp:<s:property value="#software.conp"/>
											</s:if>
											<s:if test="#software.boot != null">
												<input type="hidden" name="softVersionList[${status.index}].boot" value="${software.boot}"> 
												boot:<s:property value="#software.boot"/>
											</s:if>
											<s:if test="#software.cpld != null">
												<input type="hidden" name="softVersionList[${status.index}].cpld" value="${software.cpld}">
												cpld:<s:property value="#software.cpld"/>
											</s:if>
											<s:if test="#software.pcb != null">
												<input type="hidden" name="softVersionList[${status.index}].pcb" value="${software.pcb}">
												pcb:<s:property value="#software.pcb"/>
											</s:if>
											<s:if test="#software.manualEntry != null">
	                                            <input type="hidden" name="softVersionList[${status.index}].manualEntry" value="${software.manualEntry}">
	                                            <s:property value="#software.manualEntry"/>
	                                        </s:if>
											<br/>
										</span>
									</s:iterator>
								</div>
								<s:if test="(user.isHasRole(20) == true && (user.getUsername() == prob.trackingUser || prob.probId == 0)) || ((user.isHasRole(18) == true) && (prob.status == 1 || prob.status == 8))">
									<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
										<a href="javascript:void(0)" onclick="querySoftVersion()">点击查找</a>
									</label>
									<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
                                        <a href="javascript:void(0)" onclick="manualEntry()">手动输入</a>
                                    </label>
									<label class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
                                        <a href="javascript:void(0)" onclick="clearSoftVersion()">清除影响版本</a>
                                    </label>
								</s:if>
							</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-body">
							<s:if test="prob.probId != 0">
								<div class="form-group">
							   		<label for="remark" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><s:text name="prob.info.audit"></s:text></label>
									<div class="col-xs-9">
										<s:textarea id="remark" name="prob.remark" cssClass="form-control" readonly="true"></s:textarea>
									</div>
							   	</div>
						   	</s:if>
							<div class="form-group text-center">
								
								<a href="module/prob_list.action" style="width: 80px;" class="btn btn-default btn-sm" ><s:text name='sys.back' /></a>
								<span style="width:30px;display:inline-block;"></span>

								<!-- 研发人员发布任务 -->
								<s:if test="user.isHasRole(20)">
									<s:if test="prob.probId == 0">
										<button type="button" id="create" style="width: 80px;" class="btn btn-default btn-sm"><s:text name='prob.info.add' /></button>
									</s:if>
									<s:elseif test="user.getUsername() == prob.trackingUser">
										<button type="button" id="update" style="width: 80px;" class="btn btn-default btn-sm"><s:text name='prob.info.update' /></button>
							  		</s:elseif>
						   	  	</s:if>
							    <!-- 技术公告员审批 -->
							    <s:if test="%{(user.isHasRole(18) == true ) && (prob.status == 1 || prob.status == 8)}">
								   	<button type="button" id="reject" style="width: 80px;" class="btn btn-default btn-sm"><s:text name='prob.info.reject' /></button>
								   	<span style="width:30px;display:inline-block;"></span>
								   	<button type="button" id="pass" style="width: 80px;" class="btn btn-default btn-sm"><s:text name='prob.info.pass' /></button>
							    </s:if>
						    </div>
						</div>
					</div>
				</s:form>
			</div>
			
		</div>
	</div>
</body>
</html>