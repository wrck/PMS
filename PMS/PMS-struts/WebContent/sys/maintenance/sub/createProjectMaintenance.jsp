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
.tag-must:not(.tag-must-ignore):before, .tag-must-plus:not(.tag-must-ignore):before {
    color: red;
}
.display-flex {
    display: flex;
}
.display-flex-1 {
    display: flex;
    flex: 1;
}
.ui-datepicker-buttonpane button.ui-datepicker-current {
    opacity: 1;
}
</style>
<script type="text/javascript">
	$(function() {
		$("#pmCLChoseQuesButt").change(function() {
			if (!$(this).val() || $(this).val() == '0') {
				$("#cbCLDiv").remove();
				return;
			}
			$("#projectMaintenanceForm #maintenanceProjectId").val(null);
			$("#projectMaintenanceForm").submit();
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
		var userOffice = "${user.dpNo}";
		$(".submitButton").click(function(){
			var customTos = $("#customTos").val() || "";
			customTos = customTos.replace(/ |\r|\n|\t/g, "").replace(/；/g, ";").replace(/;+/g,";");
			$("#customTos").val(customTos);
			var customCcs = $("#customCcs").val() || "";
            customCcs = customCcs.replace(/ |\r|\n|\t/g, "").replace(/；/g, ";").replace(/;+/g,";");
            $("#customCcs").val(customCcs);
            
            customTos = customTos.split(";");
            // var mailRegex = /^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+[\.][a-z]{2,3}([\.][a-z]{2})?$/i;
            var mailRegex = /^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@(dptech.com|dp.com)?$/i;
            for ( var i in customTos) {
            	var email = customTos[i];
            	if(email && !mailRegex.test(email)) {
            		alert("邮箱地址[" + email + "]不合法");
            		return false;
            	}
        	}
            customCcs = customCcs.split(";");
            for ( var i in customCcs) {
            	var email = customCcs[i];
                if(email && !mailRegex.test(email)) {
                    alert("邮箱地址[" + email + "]不合法");
                    return false;
                }
            }
            
            var hasSoft = $("#hasSoft1:checked").val();
            var softLogIds = $.trim($("#softLogIds").val());
            if (hasSoft == '1' && softLogIds == '') {
            	alert("涉及软件版本，至少通过弹窗更新一次有效的版本信息。");
            	return false;
            }
            
            
            var $tagMusts = $(".tag-must:visible,.tag-must-plus:visible").not(".tag-must-ignore")
			for (var i = 0; i < $tagMusts.length; i++) {
                var _this = $tagMusts[i];
				var $inpt = $(_this).next().find("[name*='projectMaintenance.'], #pmCLChoseQuesButt");
				var name = $inpt.attr("name");
				var type = $inpt.attr("type");
                var $target = $("[name='" + name + "']" + (type == 'radio' || type == 'checkbox' ? ":checked" : ""));
                var messageText = $.trim($(_this).text());
                var value = $target.val();
				if (!value) {
					alert(messageText + "不能为空！");
					$target.focus();
					return false;
				}
				if (name == "projectMaintenance.processTime" && value > '<s:date name="cbForm.currentDateTime" format="yyyy-MM-dd"/>') {
					alert(messageText + "不能大于当前时间！");
                    $target.focus();
                    return false;
				}
			}
			for (var i = 0; i < $(".pmclquescontent:visible").length; i++) {
                var _this = $(".pmclquescontent:visible")[i];
				var $opt = $(".yl_one_item", $(_this)).find("[name^='pmClQuesnaireResultLineList']:first");
				var name = $opt.attr("name") || "";
				var type = $opt.attr("type");
				var $target = $("[name='" + name + "']" + (type == 'radio' || type == 'checkbox' ? ":checked" : ""), $(".yl_one_item", $(_this)));
				var $redmark = $(".redmark", $(_this));
				var value = $target.val();
                if (!value && $redmark.length > 0) {
                	var title = $redmark.text() + $.trim($(".yl_title span:first", $(_this)).text());
                    alert(title + "不能为空！");
                    $target.focus();
                    return false;
                }
			}
			$(this).bootstrapBtn("loading");
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
		
		date_picker3("processTime");
        $("#processTime").datepicker('option',{showButtonPanel:true});
		
		//uploadCallback("${projectMaintenance.deliverFileIds}", "projectDeliver");
		showUploadFile("${projectMaintenance.deliverFileIds}", globalUploadType);
		
		$("#hasSoft1").click(function() {
			if (!$("#softLogIds").val()) {
				$("#checkSoftVesionBtn").click();
			}
		});
		updateSoftVersionCallback();
		
		var categoryWithSubMap = [];
		var subCategory = "${projectMaintenance.subCategory}";
		try {
    		categoryWithSubMap = "${cbForm.categoryWithSubMap}".replace(/=/g, "':'").replace(/\{/g, "{'").replace(/, /g, "', '").replace(/\}/g, "'}").replace(/\}', '\{/g, "}, {").replace(/':'\[\{/g, "':[{").replace(/\]'\}/g, "]}").replace(/\}\]'/g, "}]").replace(/'/g,'"');
    		categoryWithSubMap = JSON.parse(categoryWithSubMap);
		} catch (e) {
			categoryWithSubMap = [];
		}
        function changeCategory(){
            var index = $("#maintenanceCategory option:selected").index();
            $("#maintenanceSubCategory").html("<option value=''>请选择</option>");
            if(index > 0 && categoryWithSubMap.length >= index){
                index--;
                var categoryAttr = categoryWithSubMap[index].categoryAttr;
                $("#maintenanceCategory").data("projectTypes", categoryAttr);
                var subCategory = categoryWithSubMap[index].children;
                $(subCategory).each(function(){
                    $("#maintenanceSubCategory").append("<option value='"+this.subCategory+"'>"+this.subCategoryName+"</option>");                 
                });
            }
            var category = $("#maintenanceCategory option:selected").val();
            $(".tag[class*='Hidden']").show();
            $(".tag[class*='Hidden']").next().show();
            $(".tag[class*='Hidden']").next().find("input, select, textarea").prop("disabled", false);
            $(".tag[class*='Show']").addClass("hidden").hide();
            $(".tag[class*='Show']").next().hide();
            $(".tag[class*='Show']").next().find("input, select, textarea").prop("disabled", true);
            
            $("." + category + "Hidden").hide();
            $("." + category + "Hidden").next().hide();
            $("." + category + "Hidden").next().find("input, select, textarea").prop("disabled", true);
            $("." + category + "Show").removeClass("hidden").show();
            $("." + category + "Show").next().show();
            $("." + category + "Show").next().find("input, select, textarea").prop("disabled", false);
            
            /* $(".serviceSalesSupportHidden, .nonBusinessHidden").show();
            $(".serviceSalesSupportHidden, .nonBusinessHidden").next().show();
            $(".serviceSalesSupportHidden, .nonBusinessHidden").next().find("input, select, textarea").prop("disabled", false);
        	$(".serviceSalesSupportShow, .nonBusinessShow").addClass("hidden").hide();
            if (category == "serviceSalesSupport" || category == "nonBusiness") {
            	//$("#maintenanceTypeTag").removeClass("tag-must");
            	$("." + category + "Hidden").hide();
            	$("." + category + "Hidden").next().hide();
            	$("." + category + "Hidden").next().find("input, select, textarea").prop("disabled", true);
            	$("." + category + "Show").removeClass("hidden").show();
            }  *//* else {
            	//$("#maintenanceTypeTag").addClass("tag-must");
            	$(".serviceSalesSupportHidden, .nonBusinessHidden").show();
                $(".serviceSalesSupportHidden, .nonBusinessHidden").next().show();
                $(".serviceSalesSupportHidden, .nonBusinessHidden").next().find("input, select, textarea").prop("disabled", false);
            	$(".serviceSalesSupportShow, .nonBusinessShow").addClass("hidden").hide();
            }*/
            adjustColumns();
            if (category == "nonBusiness") {
            	$("#quesnairePanel .redmark").removeClass("redmark").addClass("noredmark");
            } else {
            	$("#quesnairePanel .noredmark").removeClass("noredmark").addClass("redmark");
            }
        }
        if ($("#maintenanceCategory").children().length == 2) {
        	$("#maintenanceCategory option:eq(1)").prop("selected", true);
        }
        changeCategory();
        $("#maintenanceSubCategory").val(subCategory);
        function changeSubCategory(){
        	var subCategory = $("#maintenanceSubCategory option:selected").val();
            $(".tag[class*='_Must']").removeClass("tag-must-plus");
            $(".tag[class*='_NotMust']").removeClass("tag-must-ignore");
            
            $("." + subCategory + "_Must").addClass("tag-must-plus");
            $("." + subCategory + "_NotMust").addClass("tag-must-ignore");
        }
        changeSubCategory();
        $("#maintenanceCategory").on("change", changeCategory);
        $("#maintenanceSubCategory").on("change", changeSubCategory);
        
        function adjustColumns() {
            //var $firstHiddenTr = $(".serviceSalesSupportHidden, .nonBusinessHidden").first().parent();
            var $firstHiddenTr = $(".tag[class*='Hidden']").first().parent();
            var $prevTr = $firstHiddenTr;
            $firstHiddenTr.nextAll().each(function() {
                $nextTr = $(this);
                while ($nextTr.find("td:visible").length == 0) {
                    $nextTr = $nextTr.next();
                    if ($nextTr.length == 0) {
                        break;
                    }
                }
                var length = getTrColumnLength($prevTr);
                if (length == 2) {
                	var $tds = $("td:visible:eq(0), td:visible:eq(1)", $nextTr);
                	var fromArr = $tds.data("from") || [];
                	fromArr.unshift($nextTr);
                	var idxArr = $tds.data("idx") || [];
                	idxArr.unshift($tds.index());
                	$tds.addClass("moved").data("from", fromArr).data("idx", idxArr).appendTo($prevTr);
                }
                while(getTrColumnLength($prevTr) > 4) {
                	var $tds = $("td.moved:visible:eq(-2), td.moved:visible:eq(-1)", $prevTr);
                	var fromArr = $tds.data("from");
                	var idxArr = $tds.data("idx");
                	$tds.removeClass("moved");
                	for (var i = 0; i < fromArr.length; i++) {
						var $from = fromArr[i];
						var idx = idxArr[i];
						if (idx == 2) {
	                		$tds.appendTo($from);
	                	} else {
	                		$tds.prependTo($from);
	                	}
					}
                }
                /* $("td.moved:visible", $prevTr).each(function() {
                	var $tds = $("td:visible:eq(0), td:visible:eq(1)", $prevTr);
                	var fromArr = $tds.data("from");
                	var idxArr = $tds.data("idx");
                	$tds.removeClass("moved");
                	for (var i = 0; i < fromArr.length; i++) {
						var $from = fromArr[i];
						var idx = idxArr[i];
						if (idx == 2) {
	                		$tds.appendTo($from);
	                	} else {
	                		$tds.prependTo($from);
	                	}
					}
                }); */
                $prevTr = $(this);
            })
        }
        
        function getTrColumnLength($tr) {
        	var length = $($tr).find("td:visible").length;
        	$tr.find("td:visible").each(function() {
            	var colspanDiff = parseInt($(this).attr("colspan") || 1) - 1;
            	length += colspanDiff;
            })
            return length;
        }
        
        function readOnly() {
        	if (!("${projectMaintenance.createBy}" == "${user.username}") && "${projectMaintenance.id}" != "") {
            	$("#projectMaintenanceTable").css("marginBottom", 0);
            	$("#projectMaintenanceTable .tag").addClass("nowrap");
            	$("#pmCLChoseQuesButt").parent().parent().hide();
            	$("p.text-info").hide();
            	$("input:visible,option:selected,textarea:visible,a.btn").not("input[type='radio']").each(function(item, idex){
            	    var id = $(this).attr("id");
            		var text = $(this).text() || $(this).val();
            	    if (id == "customCcs" || id == "customTos" ) {
            	    	text = text.replace(/;/g, "；");
            	    }
            		$(this).parent().text(text);
            	})
            	$("select:visible").each(function(item, idex){
            	    $(this).parent().text($(this).text());
            	})
            	$("input[type='radio']").attr("disabled", true);
            	$(".btn").remove();
            	$("#projectMaintenanceForm").children().unwrap();
        	}
        }
        readOnly();
	});
	var uploadDialog = 'AjaxUpload';
	var globalUploadType = "returnForm";// 已哪种方式上传附件：commonUpload-通用上传方法获取fileIds,returnForm-获取上传附件表单数据
	function uploadTaskFile(projectId ,taskId){
	    // popWindow('module/sub/upload.action?isAjax=true', 700, 450,'上传附件', uploadDialog, true);
	    
	    var uploadType = globalUploadType;
	    var projectId = projectId || $("#projectId").val();
        var column010 = "projectMaintenance";
        var projectType = $("#projectType").val();
        var column011 = $("#maintenanceCategory").data("projectTypes") || projectType;
        var contractNo = '${project.contractNo}';
        //var eventKey = "maintenanceTask-create";
        var eventKey = "";//"maintenanceCategory-" + ($("#maintenanceCategory").val() || "ALL");
	    popWindow("module/sub/maintenance_toUploadFile.action?projectDeliver.projectId=" + projectId + "&projectDeliver.contractNo=" + contractNo + "&projectDeliver.column010=" + column010 +
            "&projectDeliver.column011=" + column011 + "&projectDeliver.eventKey=" + eventKey + "&message=" + uploadType, 700, 450,'上传交付件', uploadDialog, true);
	    return false;
	}
	function uploadCallback(fileIds, uploadType) {
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
		$("#hasReport").val(!!deliverFileIds);
		showUploadFile(deliverFileIds, uploadType);
		closeWindow(uploadDialog);
	}
	function returnFormCallback($form, mergeCallback) {
        if (!$form) {
            return;
        }
        var fileNameArr = [];
        try {
        	$form.find("input[type='file']").each(function() {
                /* var fileName = $(this).val();
                if (fileName) {
                    fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                    fileNameArr.push(fileName);
                } */
                var files = this.files || [];
                for (var i = 0; i < files.length; i++) {
					var file = files[i];
					fileNameArr.push(file.name);
				}
            });
        } catch(e) {}
        if ($("#fileNames").html()) {
        	$("#fileNames").append(" | ");
        }
        $("#fileNames").append(fileNameArr.join(" | "));
        $("#hasReport").val(!!$.trim($("#fileNames").text()));
        if (!$("#uploadFormDiv").length) {
            $form.wrapInner("<div id='uploadFormDiv' class='hidden'></div>").children().unwrap().appendTo($("#fileNames").parent(), window.parent.document);
        } else if (mergeCallback) {
        	mergeCallback.call(this, $("#uploadFormDiv"), $form);
        }
        closeWindow(uploadDialog);
    }
	function showUploadFile(fileIds, uploadType) {
		var ctxPath = $("base").attr("href");
		if (typeof uploadType != "undefined") {
            var maintenanceId = $("#maintenanceId").val();
            var projectType = $("#projectType").val();
            $.ajax({
                url : $("base").attr("href") + '/maintenanceAjax_uploadFileList.action',
                type : 'POST',
                cache: false,
                data: {
                	message: uploadType,
                	"projectMaintenance.id": maintenanceId,
                	"projectMaintenance.projectType": projectType,
                	"projectMaintenance.deliverFileIds": fileIds
            	},
                success : function(result) {
                	try {
                		result = JSON.parse(result);
                	} catch (e) {
                		try {
                    		result = JSON.parse(result.result);
                		} catch (e) {
						}
					}
                    var type = result.type || [];
                    var fileList = result.fileList || [];
                    var projectDeliverList = result.projectDeliverList || [];
                    var $fileNames = $("#fileNames");
                    var tags = [];
                    if (type == "returnForm") {
                        var regex = RegExp(".*\\.(png|jpg|jpeg|gif)$", "i");
                    	for (var i = 0; i < projectDeliverList.length; i++) {
                            var file = projectDeliverList[i];
                            var fileName = file.deliverableName;
                            var fileId = file.id;
                            var filePath = file.deliverablePath;
                            var link = '<a href="module/DownloadFile.action?downname={fileName}&downpath={encodefilePath}">{fileName}</a>';
                            var viewTag = '<a href="{ctxPath}{filePath}" target="_blank">预览</a>';
                            var downloadTag = '<a href="module/DownloadFile.action?downname={fileName}&downpath={encodefilePath}">下载</a>';
                            var tag = "";
                            if(regex.test(fileName)) {
                            	tag += '<span class="hover-wrapper">' 
                        		   + link
                        		   + '<label class="hover-label">'
                            	   + viewTag
                            	   + "|"
                            	   + downloadTag
                            	   + "</label></span>";
                            } else {
                            	tag = link;
                            }
                            tag = tag.replace(/{fileName}/g, fileName)
                                .replace(/{encodefilePath}/g, encodeURI(filePath))
                                .replace(/{filePath}/g, filePath)
                                .replace(/{ctxPath}/g, ctxPath)
                            tags.push(tag);
                        }
                    } else {
                        for (var i = 0; i < fileList.length; i++) {
                            var file = fileList[i];
                            var fileName = file.fileName;
                            var fileId = file.id;
                            var filePath = file.filePath;
                            var tag = '<a href="module/download.action?fileId=' + fileId + '" title="点击下载">' + fileName + '</a>';
                            tags.push(tag);
                        }
                    }
                    $("#fileNames").html(tags.join(" | "));
                }
            });
        } else {
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
	}
    function checkSoftVesion(){
        var projectId = $("#maintenanceProjectId").val();
        var contractNo = $("#maintenanceContractNo").val();
        var params = $.param({"project.projectId":projectId,"project.contractNo":contractNo});
        popWindow("module/sub/checkSoftVersion.action?" + params, "90vw", 650, '版本信息', uploadDialog, true);
        return false;
    }
    
    function updateSoftVersionCallback(data) {
    	data = data || {};
    	var softLogIds = $.trim($("#softLogIds").val());
    	var softLogInfos = $.trim($("#softLogInfos").val());
    	if (softLogIds == '') {
    		softLogIds = [];
    		softLogInfos = [];
    	} else {
    		softLogIds = softLogIds.split(",");
    		softLogInfos = JSON.parse(softLogInfos);
    	}
    	var log = data.softChangeLog || {};
    	if (log.id) {
    		softLogIds.push(log.id);
    		softLogInfos.push({
    			changeVersion: log.changeVersion,
    			changeRemark: log.changeRemark,
    			versionAndCreateTime: log.versionAndCreateTime
    		})
    	}
    	$("#softLogIds").val(softLogIds.join(","));
    	$("#softLogInfos").val(JSON.stringify(softLogInfos));
    	var displayHtml = "";
    	for (let logInfo of softLogInfos) {
    		displayHtml += `<div class="softLogInfo" style="padding:0 1rem;"><div>\${logInfo.versionAndCreateTime}</div><div>\${logInfo.changeRemark}</div></div>`;
		}
    	$("#softLogInfosText").html(displayHtml);
    }
</script>
</head>
<body>
    <s:form id="projectMaintenanceForm" name="projectMaintenanceForm" action="maintenance_createProjectMaintenance.action" enctype="multipart/form-data" method="post" class="maintenanceForm">
	<div>
	    <%-- <s:if test="subcontractCallback.quesnaireState == 1"> --%>
	    <div class="panel panel-default">
		    <div class="panel-body">
                    <s:hidden id="maintenanceId" name="projectMaintenance.id"></s:hidden>
                    <s:if test="projectMaintenance.projectType == 10">
                        <s:hidden id="updateMaxId" name="projectMaintenance.maxId"></s:hidden>
                        <s:hidden id="projectId" name="project.projectId"></s:hidden>
                        <s:hidden id="maintenanceProjectId" name="projectMaintenance.projectId" value="%{project.projectId}"></s:hidden>
                        <s:hidden id="maintenanceContractNo" name="projectMaintenance.contractNo" value="%{project.contractNo}"></s:hidden>
                    </s:if>
                    <s:elseif test="projectMaintenance.projectType == 20">
                        <s:hidden id="projectId" name="presales.presalesId"></s:hidden>
                        <s:hidden id="maintenanceProjectId" name="projectMaintenance.projectId" value="%{presales.presalesId}"></s:hidden>
                        <s:hidden id="maintenanceContractNo" name="projectMaintenance.contractNo" value="%{presales.projectCode}"></s:hidden>
                    </s:elseif>
                    <s:else>
                        <s:hidden id="maintenanceProjectId" name="projectMaintenance.projectId" value="%{presales.presalesId || project.projectId}"></s:hidden>
                    </s:else>
                    <s:hidden id="projectType" name="projectMaintenance.projectType"></s:hidden>
                    <%-- <s:hidden name="projectMaintenance.userOffice" value="%{user.dpNo}" /> --%>
                    <s:hidden name="redirect"/>
                    
                    <!-- 项目基本信息 -->
                    <table id="projectMaintenanceTable" class="table table-bordered table-hover table-striped noBorder">
                        <s:if test="pmClosedLoopQuesnaireList != null">
                            <s:if test="projectMaintenance.projectType == 10">
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
                                    <%-- <td><s:text name="pm.project.programManager"/>A:</td>
                                    <td><s:property value="project.programManagerCodeforjson"/></td>
                                    <td><s:text name="pm.project.programManager"/>B:</td>
                                    <td><s:property value="project.programManagerCodeforjsonB"/></td> --%>
                                    <td><s:text name="pm.project.programManager"/></td>
                                    <td>
                                        <div class="display-flex">
                                            <span class="display-flex-1"><s:property value="project.programManagerCodeforjson"/></span>
                                            <span class="display-flex-1"><s:property value="project.programManagerCodeforjsonB"/></span>
                                        </div>
                                    </td>
                                    <td class="tag tag-must"><s:text name="pm.project.executionState"/>:</td>
                                    <td>
                                        <s:select list="projectExecutionStateList" id="projectExecutionState" name="projectMaintenance.projectExecutionState" value="%{projectMaintenance.id != null ? projectMaintenance.projectExecutionState : ''}" listKey="basicDataId" listValue="basicDataName" cssClass="form-control" headerValue="--请选择--" headerKey="" cssStyle="width: 180px;display: inline-block;" />
                                    </td>
                                </tr>
                            </s:if>
                            <s:elseif test="projectMaintenance.projectType == 20">
                                <tr>
                                    <td><s:text name="pm.project.projectCode"></s:text>:</td>
                                    <td><s:property value="presales.presalesCode"/></td>
                                    <td><s:text name="pm.project.contractNo"></s:text>:</td>
                                    <td><s:property value="presales.projectCode"/></td>
                                </tr>
                                <tr>
                                    <td><s:text name="pm.project.projectName"></s:text>:</td>
                                    <td><s:property value="presales.projectName"/></td>
                                    <td><s:text name="pm.project.officeName"></s:text>:</td>
                                    <td><s:property value="presales.officeName"/></td>
                                </tr>
                                <tr>
                                    <td><s:text name="pm.project.serviceManager"/></td>
                                    <td><s:property value="presales.serviceManagerName"/></td>
                                    <td><s:text name="pm.project.programManager"/></td>
                                    <td><s:property value="presales.projectManagerName"/></td>
                                </tr>
                            </s:elseif>
                            <s:elseif test="projectMaintenance.projectType == 40">
                                <tr>
                                	<%-- <s:hidden name="projectMaintenance.officeCode" value="%{user.dpNo}" /> --%>
                                    <td class="tag nonBusinessHidden"><s:text name="pm.project.projectCode"></s:text>:</td>
                                    <td><s:textfield name="projectMaintenance.projectCode" cssClass="form-control"/></td>
                                    <td class="tag nonBusinessHidden"><s:text name="pm.project.contractNo"></s:text>:</td>
                                    <td><s:textfield name="projectMaintenance.contractNo" cssClass="form-control"/></td>
                                </tr>
                                <tr>
                                    <td class="tag nonBusinessHidden"><s:text name="pm.project.projectName"></s:text>:</td>
                            		<td colspan="1">
                            			<s:textfield name="projectMaintenance.projectName" cssClass="form-control"/>
                         			</td>
                                    <td class="tag nonBusinessHidden"><s:text name="pm.project.officeName"></s:text>:</td>
                                    <td colspan="1"><s:select name="projectMaintenance.officeCode" id="officeCode"
                                        listKey="departmentNum" cssClass="form-control" headerKey="%{user.dpNo}"
                                        headerValue="--请选择--" cssStyle="width:163px"
                                        listValue="departmentName" list="%{departmentList}" value="%{user.dpNo}" theme="simple" /></td>
                                </tr>
                            </s:elseif>
                        </s:if>
                        <tr>
                            <s:if test="projectMaintenance.projectType == 30">
                                <s:hidden name="projectMaintenance.officeCode" value="%{user.dpNo}" />
                            </s:if>
                            <td class="tag tag-must"><s:text name="pm.project.maintenance.category"></s:text>:</td>
                            <td>
                                <s:select list="cbForm.categoryWithSubMap" id="maintenanceCategory" name="projectMaintenance.category" listKey="category" listValue="categoryName" cssClass="form-control" headerValue="--请选择--" headerKey="" cssStyle="width: 180px;display: inline-block;" />
                            </td>
                            <td class="tag tag-must"><s:text name="pm.project.maintenance.subCategory"></s:text>:</td>
                            <td>
                                <s:select list="cbForm.categoryWithSubMap" id="maintenanceSubCategory" name="projectMaintenance.subCategory" listKey="category" listValue="categoryName" cssClass="form-control" headerValue="--请选择--" headerKey="" cssStyle="width: 180px;display: inline-block;" />
                            </td>
                        </tr>
                        <tr>
                            <td id="maintenanceTypeTag" class="tag nonBusinessHidden"><s:text name="pm.project.maintenance.type"></s:text>:</td>
                            <td>
                                <s:select list="maintenanceTypeList" id="maintenanceType" name="projectMaintenance.type" listKey="basicDataId" listValue="basicDataName" cssClass="form-control" headerValue="--请选择--" headerKey="" cssStyle="width: 180px;display: inline-block;" />
                            </td>
                            <td class="tag tag-must"><s:text name="pm.project.maintenance.processTime"></s:text>:</td>
                            <td>
                                <s:textfield name="projectMaintenance.processTime" id="processTime" placeholder="处理时间" cssClass="form-control" cssStyle="width: 180px;" autocomplete="off"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="tag tag-must"><s:text name="pm.project.maintenance.transitHour"></s:text>:</td>
                            <td>
                                <s:textfield name="projectMaintenance.transitHour" id="transitHour" type="number" min="0" step="0.1" placeholder="在途耗时" cssClass="form-control" cssStyle="width: 180px;"/>
                            </td>
                            <td class="tag tag-must"><s:text name="pm.project.maintenance.processHour"></s:text>:</td>
                            <td>
                                <s:textfield name="projectMaintenance.processHour" id="processHour" type="number" min="0" step="0.1" placeholder="处理耗时" cssClass="form-control" cssStyle="width: 180px;"/>
                            </td>
                        </tr>
                        <s:if test="projectMaintenance.projectType == 10">
                            <tr>
                                <td class="tag"><s:text name="pm.project.maintenance.warrantyStatus"></s:text>:</td>
                                <td>
                                    <div class="display-flex">
                                        <span class="display-flex-1" style="display:inline-block;">
                                            <s:select name="projectMaintenance.warrantyStatus" id="warrantyStatus" list="#{-1:'维保内', 0:'部分保内', 1:'维保外'}" value="%{projectMaintenance.warrantyState.warrantyStatus}" headerKey="" headerValue="--请选择---" placeholder="维保状态" cssClass="form-control hidden" cssStyle="width: 180px;"/>
                                            <%-- <span title="${projectMaintenance.warrantyState.warrantyStatusDesc}">${projectMaintenance.warrantyState.warrantyStatusName}</span> --%>
                                            <s:property value="projectMaintenance.warrantyState.warrantyStatusName"/>
                                            <s:if test="projectMaintenance.warrantyState.warrantyStatusName != null">
                                                <span class="glyphicon glyphicon-question-sign" style="color:#bbb;" title="${projectMaintenance.warrantyState.warrantyStatusDesc}"></span>
                                                <br>
                                                <span class="nowrap">
                                                    (<s:date name="projectMaintenance.warrantyState.warrantyStartTime" format="yyyy-MM-dd" />~<s:date name="projectMaintenance.warrantyState.warrantyEndTime" format="yyyy-MM-dd" />)
                                                </span>
                                            </s:if>
                                        </span>
                                        <span class="display-flex-1" style="display:inline-block;">
                                            <s:hidden name="projectMaintenance.warrantyState.warrantyGrade"/>
                                            <s:property value="projectMaintenance.warrantyState.warrantyGradeName"/>
                                            <s:if test="projectMaintenance.warrantyState.warrantyGradeName != null">
                                                <span class="glyphicon glyphicon-question-sign" style="color:#bbb;" title="${projectMaintenance.warrantyState.warrantyGradeDesc}"></span>
                                                <br>
                                                <span class="nowrap">
                                                    <s:if test="projectMaintenance.warrantyState.warrantyGradeEndTime != null">
                                                        <%-- (截止日期<s:date name="projectMaintenance.warrantyState.warrantyGradeEndTime" format="yyyy-MM-dd" />) --%>
                                                        (<s:date name="projectMaintenance.warrantyState.warrantyGradeStartTime" format="yyyy-MM-dd" />~<s:date name="projectMaintenance.warrantyState.warrantyGradeEndTime" format="yyyy-MM-dd" />)
                                                    </s:if>
                                                    <s:elseif test="projectMaintenance.warrantyState.warrantyEndTime != null">
                                                        <%-- (截止日期<s:date name="projectMaintenance.warrantyState.warrantyEndTime" format="yyyy-MM-dd" />) --%>
                                                        (<s:date name="projectMaintenance.warrantyState.warrantyStartTime" format="yyyy-MM-dd" />~<s:date name="projectMaintenance.warrantyState.warrantyEndTime" format="yyyy-MM-dd" />)
                                                    </s:elseif>
                                                </span>
                                            </s:if>
                                        </span>
                                    </div>
                                </td>
                                <td class="tag"><s:text name="pm.project.wafService"></s:text>:</td>
                                <td>
                                    <div class="display-flex">
                                        <span class="display-flex-1" style="display:inline-block;">
                                            <s:hidden name="projectMaintenance.warrantyState.wafService"/>
                                            <s:property value="projectMaintenance.warrantyState.wafServiceName" default="无"/>
                                            <s:if test="projectMaintenance.warrantyState.wafServiceName != null">
                                                <span class="glyphicon glyphicon-question-sign" style="color:#bbb;" title="${projectMaintenance.warrantyState.warrantyServiceDesc}"></span>
                                                <br>
                                                <span class="nowrap">
                                                    (<s:date name="projectMaintenance.warrantyState.wafServiceStartTime" format="yyyy-MM-dd" />~<s:date name="projectMaintenance.warrantyState.wafServiceEndTime" format="yyyy-MM-dd" />)
                                                </span>
                                            </s:if>
                                        </span>
                                    </div>
                                </td>
                            </tr>
                        </s:if>
                        <s:elseif test="projectMaintenance.projectType == 40">
                            <tr>
                                <td class="tag tag-must projectImplementationShow afterSalesMaintenanceShow hidden"><s:text name="pm.project.maintenance.warrantyStatus"></s:text>:</td>
                                <td>
                                    <s:select name="projectMaintenance.warrantyStatus" id="warrantyStatus" list="#{-1:'维保内', 0:'部分保内', 1:'维保外'}" headerKey="" headerValue="--请选择---" placeholder="维保状态" cssClass="form-control" cssStyle="width: 180px;"/>
                                </td>
                                <td class="tag tag-must projectImplementationShow afterSalesMaintenanceShow hidden"><s:text name="pm.project.maintenance.industryName"></s:text>:</td>
                                <td>
                                    <s:textfield name="projectMaintenance.industryName" id="industryName" placeholder="行业" cssClass="form-control" cssStyle="width: 180px;"/>
                                </td>
                            </tr>
                        </s:elseif>
                        <tr>
                            <td class="tag"><s:text name="pm.project.maintenance.processDesc"></s:text>:</td>
                            <td><s:textarea name="projectMaintenance.processDesc" cssClass="form-control"/></td>
                            <td class="tag"><s:text name="pm.project.maintenance.processStep"></s:text>:</td>
                            <td><s:textarea name="projectMaintenance.processStep" cssClass="form-control"/></td>
                        </tr>
                        <tr>
                            <td class="tag serviceSalesSupportHidden nonBusinessHidden projectImplementation_09_Must afterSalesMaintenance_01_Must"><s:text name="pm.project.maintenance.itemModel"></s:text>:</td>
                            <td><s:textarea name="projectMaintenance.itemModel" cssClass="form-control"/></td>
                            <td class="tag serviceSalesSupportHidden nonBusinessHidden projectImplementation_09_Must afterSalesMaintenance_01_Must"><s:text name="pm.project.maintenance.softVersion"></s:text>:</td>
                            <td><s:textarea name="projectMaintenance.softVersion" cssClass="form-control"/></td>
                        </tr>
                        <tr>
                            <td class="tag serviceSalesSupportHidden nonBusinessHidden"><s:text name="pm.project.maintenance.enabledFeatures"></s:text>:</td>
                            <td><s:textarea name="projectMaintenance.enabledFeatures" cssClass="form-control"/></td>
                            <td class="tag"><s:text name="pm.remark"></s:text>:</td>
                            <td><s:textarea name="projectMaintenance.remark" cssClass="form-control"/></td>
                        </tr>
                        <tr>
                            <%-- <td class="tag tag-must"><s:text name="pm.project.maintenance.hasReport"></s:text>:</td>
                            <td><s:radio list="#{false:'无', true:'有'}" name="projectMaintenance.hasReport" /></td> --%>
                            <td class="tag"><s:text name="pm.project.maintenance.customTos"></s:text>:</td>
                            <td><s:textarea id="customTos" name="projectMaintenance.customTos" placeholder="额外邮件主送地址，用英文分号;分割"  cssClass="form-control"/></td>
                            <td class="tag"><s:text name="pm.project.maintenance.customCcs"></s:text>:</td>
                            <td><s:textarea id="customCcs" name="projectMaintenance.customCcs" placeholder="额外邮件抄送地址，用英文分号;分割"  cssClass="form-control"/></td>
                        </tr>
                        <tr>
                            <td class="tag"><s:text name="pm.project.maintenance.userOffice"></s:text>:</td>
                            <td colspan="1"><s:select name="projectMaintenance.userOffice" id="userOffice"
                                listKey="departmentNum" cssClass="form-control" headerKey="%{user.dpNo}"
                                headerValue="--请选择--" cssStyle="width:163px"
                                listValue="departmentName" list="%{departmentList}" value="%{user.dpNo}" theme="simple" /></td>
                            <td class="tag tag-must"><s:text name="pm.project.company"></s:text>:</td>
                            <td colspan="1">
                                <s:select name="projectMaintenance.compId" id="compId"
                                    listKey="id" cssClass="form-control" headerKey=""
                                    headerValue="--请选择--" cssStyle="width:163px"
                                    listValue="abbr" list="%{companyList}" value="%{projectMaintenance.compId || project.compId || 1}" theme="simple" />
                            </td>
                        </tr>
                        <tr>
                            <td class="tag tag-must serviceSalesSupportHidden nonBusinessHidden">
                                <a id="checkSoftVesionBtn" class="btn btn-success btn-xs" href="javascript:void(0)" onclick="checkSoftVesion(<s:property value='project.projectId' />)">
                                    <span class="glyphicon glyphicon-upload"></span> 软件版本</a>
                            </td>
                            <td colspan="3">
                                <div class="display-flex flex-algin-items-center">
                                    <div class="display-inline-flex nowrap">
                                        <s:radio list="#{0:'不涉及', 1:'涉及'}" id="hasSoft" name="projectMaintenance.customStrInfo.hasSoft" cssClass="fillHiddenName" listCssClass="fillHiddenName"/>
                                        <s:hidden name="projectMaintenance.customInfo.softLogIds" id="softLogIds"/>
                                        <s:hidden name="projectMaintenance.customInfo.softLogInfos" id="softLogInfos"/>
                                    </div>
                                    <div id="softLogInfosText" class="display-inline-flex" style="width: 100%;justify-content: space-around;">
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td class="tag serviceSalesSupportHidden nonBusinessHidden">
                                <a class="btn btn-success btn-xs" href="javascript:void(0)" onclick="uploadTaskFile(<s:property value='project.projectId' />)">
                                    <span class="glyphicon glyphicon-upload"></span> 上传附件</a>
                            </td>
                            <td colspan="3">
                                <s:hidden name="projectMaintenance.hasReport" id="hasReport"/>
                                <s:hidden name="projectMaintenance.deliverFileIds" id="deliverFileIds"/>
                                <div id="fileNames"></div>
                            </td>
                        </tr>
                    </table>
                    <s:if test="pmClosedLoopQuesnaireList != null">
                        <p class="text-info serviceSalesSupportHidden nonBusinessHidden" style="margin-top:-0.75em;">提示：请先选择问卷，否则将丢失已填数据</p>
                        <div class="btn-group btn-group-sm hidden serviceSalesSupportShow nonBusinessShow">
                            <button id="submitButton" type="button" class="btn btn-info submitButton" style="margin-right:4px;" data-loading-text="正在处理...">
                                <span class="glyphicon glyphicon-floppy-disk" style="font-size:12px;"></span> 保存
                            </button>
                        </div>
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
        
	    <div id="quesnairePanel" class="serviceSalesSupportHidden nonBusinessHidden">
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
        
		<s:if test="cbForm.pmClosedLoopQuesnaireLineList != null && cbForm.pmClosedLoopQuesnaireLineList.size()>0">
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

								<%-- <s:form method="post" action="querySubcontractCallback.action" id="cbCLForm" class="maintenanceForm"> --%>
									<s:hidden name="pmClQuesnaireResultHeader.quesnaireTemplateHeaderId" value="%{pmClosedLoopQuesnaire.id}"></s:hidden>
									<s:hidden name="pmClQuesnaireResultHeader.quesTotalScore" value="%{pmClosedLoopQuesnaire.questionnaireScore}"></s:hidden>
									<s:hidden name="pmClQuesnaireResultHeader.quesPassScore" value="%{pmClosedLoopQuesnaire.questionnairePassScore}"></s:hidden>
									<s:hidden name="pmClQuesnaireResultHeader.status" value="-1" id="quesnaireState"></s:hidden>
									<!-- 问卷已提交 -->
									<s:if test="projectMaintenance.quesnaireState == 1">
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
																			<span class="redmark" style="color: black;">*<s:property value="#obja.questionNum" />.</span>
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
									<s:if test="projectMaintenance.quesnaireState != 1">
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
									</s:if>
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