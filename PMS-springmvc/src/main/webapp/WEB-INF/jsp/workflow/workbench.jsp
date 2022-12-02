<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %> 
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="system.title" /></title>
<cssTag>
    <!-- DataTables -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/media/css/dataTables.bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/extensions/Select/css/select.bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datatables/extensions/RowReorder/css/rowReorder.bootstrap.min.css">
	
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
	
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/select2/select2.min.css">

<style>
#searchDiv {
    display: none;
    margin-bottom: 1rem;
}
span.warn{
    color:red;
    margin-left: 3px;
}
li.active span.warn{
    color:white;
}
.quickApprove, .quickApprovePop {
    /* height: 1.8rem; */
    color: black;
}

</style>
</cssTag>
</head>
<body>
    <!-- Content Header (Page header) -->
    <section class="content-header">
        <h1></h1>
        <ol class="breadcrumb">
        </ol>
    </section>

    <!-- Main content -->
    <section class="content">
        <div class="row">
            <div class="col-sm-12">
                <div class="tab-content" style="border:1px solid #ddd;background-color: white;">
                    <ul class="nav nav-tabs">
                        <li><a href="#tab-content-toDo" data-toggle="tab" id="nav-tab-toDo" aria-expanded="false">待办任务</a></li>
                        <li><a href="#tab-content-finished" data-toggle="tab" id="nav-tab-finished" aria-expanded="true">已办任务</a></li>
                    </ul>
                    <div class="tab-pane" id="tab-content-toDo">
                        <div class="box box-primary">
                            <div class="box-body">
                                <div id="toDo-searchDiv">
                                </div>
                                <table id="toDoTable"
                                    class="table table-bordered table-striped table-hover dataTable">
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane" id="tab-content-finished">
                        <div class="box box-primary">
                            <!-- /.box-header -->
                            <div class="box-body">
                                <div id="finished-searchDiv">
                                    <div id="operate-btn-group" class="btn-group operate-btn-group">
                                    </div>
                                </div>
                                <table id="finishedTable" class="table table-bordered table-striped table-hover dataTable">
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <!-- /.content -->
    <!-- 批量审批计划选择弹出窗 -->
    <div id="selectPlanWin" class="modal fade in" aria-labelledby="myModalLabel" aria-hidden="true" style="display: none;">
        <div class="modal-dialog" style="width: 960px;">
            <div class="modal-content">
                <div class="modal-header">
                	<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><li class="fa fa-remove"></li></button>
                	<h5 class="modal-title">当前活动绩效考核</h5>
                </div>
                <div class="modal-body" id="modRestDiv">
                    <div class="box-body">
                        <div class="form-group">
                            <div class="col-sm-12">
                                <select id="plansForQuickHandleTask" class="form-control"></select>
                            </div>
                        </div>
                    </div>
                    <div class="box-footer text-right">
                        <button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">关闭</button>
                        <button type="button" class="btn btn-primary" data-btn-type="selectPlan">确认</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
<jsTag> 
    <!-- DataTables -->
    <script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/jquery.dataTables.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/datatables/media/js/dataTables.bootstrap.min.js"></script>
	
	<script src="${pageContext.request.contextPath}/static/common/js/dataTablesExt.js"></script>
	
	<!-- 表单验证相关 -->
    <script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>
	
	<script src="${pageContext.request.contextPath}/static/plugins/select2/select2.min.js"></script>
	
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script src="${pageContext.request.contextPath}/static/pm/js/router.js"></script>
<script>
    var toDoTable,auditTable,appealTable,finishedTable;
    var winId = "taskWin";
    var showWinId = "showWin";
    var splitStr = " -- ";
    $(function(){
    	var userId = "<shiro:principal property='userId'></shiro:principal>";
    	//初始化tab激活项
    	initTabByStorage();
    	
        var commonConfig = {
            searching:true,
            rowId: 'taskId',
            /* language:{
            	sEmptyTable: "本部门绩效考核还未发起，请耐心等待"
            }, */
            "columns" : [
                {
                    title : "任务名称",
                    data : "taskName",
                    name : "res.TASK_DEF_KEY_",
                    class: "text-nowrap",
                    visible: true,
                    //sortable: false,
                },
                {
                    title : "类别",
                    data : "taskDesc",
                    name : "res.DESCRIPTION_",
                    class: "text-center text-nowrap",
                    visible: true,
                    render: function(data, type, row) {
                    	return (data || "").split(splitStr)[0];
                    }
                },
                {
                    title : "审核内容",
                    data : "dataType",
                    name : "pw.dataType",
                    visible: true,
                    //sortable: false,
                    class: "text-center",
                    render:function(data, type, row){
                    	var entity = row.entity || {};
                    	var contents = [];
                        switch (data){
                            case 'projectTask':
                            	contents.push((entity.customInfo || {}).eventValue);
                            	contents.push(entity.taskName);
                            	break;
                            case 'industryAsset':
                            	contents.push(entity.assetType);
                            	contents.push(entity.assetName);
                            break;
                            case 'industryLeak':
                            	contents.push(entity.leakType);
                            	contents.push(entity.leakLevel);
                            	contents.push(entity.leakName);
							break;
                            case 'settlement':
                            	contents.push((entity.customInfo || {}).purchId || ((entity.dispatch || {}).customInfo || {}).purchId);
                            	contents.push((entity.dispatch || {}).dispatchNo);
                                contents.push(entity.settleSeq);
                                contents.push(entity.acceptanceDesc);
                                contents.push(entity.progressDesc);
                            break;
                            case null:return '';break;
                        }
                        return contents.filter(function(item,index) {
                        	return item ? true : false;
                        }).join(splitStr);
                    }
                },
                {
                    title : "当前办理人",
                    data : "assigneeName",
                    name : "RES.ASSIGNEE_",
                    class: "text-center",
                    visible: true,
                    //sortable: false,
                },
                {
                    title : "任务开始时间",
                    data : "beginTime",
                    name : "RES.CREATE_TIME",
                    class: "text-center",
                    visible: true,
                    //sortable: false,
                },
                /* {
                    title : "截止时间",
                    data : "dueTime",
                    name : "RES.DUE_DATE_",
                    class: "text-center",
                    visible: true,
                    //sortable: false,
                }, */
             ],
        }
        var toDo_config = $.extend(true,{},commonConfig,{
            "columnDefs" : [ {
                // 定义操作列,######以下是重点########
                targets : 5,//操作按钮目标列
                data : "formUrl",
                title:"操作",
                sortable: false,
                class: "text-center text-nowrap",
                render : function(data, type, row) {
                    var id = row.taskId;
                    var html = "";
                    html += "<a class='btn btn-xs btn-success' href='"+basePath+data+"?taskId="+row.taskId+"'><i class='fa fa-location-arrow'></i>办理</a>";
                    if (row.dataType != row.objType) {
                    	var url = pm.router.html(row.objType).detail(row.objId);
	                    html += "<a class='btn btn-xs btn-warning' href='"+url+"'><i class='fa fa-link'></i> 链接</a>";
	                }
                    html += "<a class='btn btn-xs btn-info' onClick = 'showProcessInfo("+row.procInstId+")'><i class='fa fa-fw fa-cloud-download'></i>流程明细</a>";
                    return html;
                }
            } ],
            customDrawCallback: function(){
                initWarnNum(toDoTable,"toDo");
            }
        });
        var finished_config = {
            searching: true,
            rowId: 'taskId',
            "columns" : [].concat(commonConfig.columns, [
                {
                    title : "办理时间",
                    data : "endTime",
                    name : "RES.END_TIME_",
                    class: "text-center",
                    visible: true,
                    //sortable: false,
                },
             ]),
            "columnDefs" : [ {
                // 定义操作列,######以下是重点########
                targets : 6,//操作按钮目标列
                data : "formUrl",
                title:"操作",
                sortable: false,
                class: "text-center text-nowrap",
                render : function(data, type, row) {
                    var id = row.taskId;
                    var html = "";
                    html += "<a class='btn btn-xs btn-success' href='"+basePath+data+"'><i class='fa fa-location-arrow'></i> 查看</a>";
                    if (row.canWithdraw) {
	                    html += "<a class='btn btn-xs btn-danger' data-btn-type='withdraw'><i class='fa fa-undo'></i> 撤回</a>";
	                }
                    if (row.dataType != row.objType) {
                    	var url = pm.router.html(row.objType).detail(row.objId);
	                    html += "<a class='btn btn-xs btn-warning' href='"+url+"'><i class='fa fa-link'></i> 链接</a>";
	                }
	                html += "<a class='btn btn-xs btn-info' onClick = 'showProcessInfo("+row.procInstId+")'><i class='fa fa-fw fa-cloud-download'></i> 流程明细</a>";
                    return html;
                }
            } ],
            customDrawCallback: function() {
                initWarnNum(finishedTable, "finished", false);
            }
        };
        toDoTable = new CommonTable("toDoTable", basePath+"/workflow/workbench/toDoList.json", "toDo-searchDiv", toDo_config);
        //auditTable = new CommonTable("auditTable", basePath+"/workflow/workbench/listOthersTask.json", "audit-searchDiv", audit_config);
        //appealTable = new CommonTable("appealTable", basePath+"/workflow/workbench/listAppealTask.json", "appeal-searchDiv", appeal_config);
        finishedTable = new CommonTable("finishedTable", basePath+"/workflow/workbench/finishedTaskList.json", "finished-searchDiv", finished_config);

        $(document).on("click", "button[data-btn-type],a[data-btn-type]", function() {
            var action  = $(this).data("btn-type");
            var priority = $(this).data("priority");
            var tableWrapper = $(this).parents(".dataTables_wrapper").attr("id")||"";
            var tableId = tableWrapper.replace("_wrapper", "");
            var dataTable = eval(tableId);
            switch(action) {
                case "quickApprove":
                    var rowIds = dataTable.getCheckedRowsId();
                    if (!rowIds) {
                        modals.info("请选择需要审批的任务！");
                    }
                    var tasks = [];
                    for (var i = 0; i < rowIds.length; i++) {
                        var rowId = rowIds[i];
                        var task = {taskId: rowId};
                        $("tr#" + rowId + " .quickApprove", $("#" + tableId)).each(function() {
                            task[this.name] = this.value;
                        });
                        tasks.push(task);
                    }
                    //console.log(tasks);
                    var tasksStr = JSON.stringify(tasks);
                    //console.log(tasksStr);
                    ajaxPost(basePath + "/workflow/complete/batch.json", {approvalData: tasksStr}, function(data) {
                        modals.info(data.message);
                        dataTable.reloadData();
                    });
                    break;
                case "quickHandleTask" :
                    ajaxPost(basePath+"/workflow/modals/listPlansInSummary.json",{"priority":priority},function(data){
                        var text = "";
                        if(priority == 1){
                            text = "一考";
                        }else if(priority == 2){
                            text = "二考";
                        }else if(priority == 3){
                            text = "三考";
                        }else if(priority == 99){
                        	text = "复核";
                        }
                        if(data.planList.length == 0){
                            modals.info("暂无需办理的"+text+"任务。")
                            return false;
                        }else if(data.planList.length > 1){
                            $("#plansForQuickHandleTask").empty();
                            var planArr = data.planList;
                            for(var i in planArr){
                                $("#plansForQuickHandleTask").append("<option value='"+planArr[i].id+"'>"+planArr[i].name+"</option>")
                            }
                            $("button[data-btn-type='selectPlan']").data("priority",priority);
                            modals.showWin("selectPlanWin");
                        }else{
                            modals.openWin({
                            	backdrop: 'static', 
                            	keyboard: false,
                                winId:showWinId,
                                width:1080,
                                url:basePath + "/workflow/modals/quickHandleTask?planId="+data.planList[0].id+"&priority="+priority,
                                hideFunc:function(){
                                    auditTable.reloadData();
                                }
                            });
                        }
                    })
                    break;
                case "selectPlan":
                    var planId = $("#plansForQuickHandleTask").val();
                    modals.hideWin("selectPlanWin");
                    modals.openWin({
                    	backdrop: 'static', 
                    	keyboard: false,
                        winId:showWinId,
                        width:1080,
                        url:basePath + "/workflow/modals/quickHandleTask?planId="+planId+"&priority="+priority,
                        hideFunc:function(){
                            auditTable.reloadData();
                        }
                    });
                    break;
                case "withdraw": 
                	var row = finishedTable.getSelectedRowData();
                	$(this).button("loading");
                	ajaxPost(basePath+"/workflow/withdraw/" + row.procInstId + "/" + userId + ".json", {}, function(data) {
                		if (data.success) {
                			modals.correct("撤回成功！");
                			window.location.reload();
                		} else {
                			modals.error(data.message);
                		}
                		$(this).button("reset");
                	});
                	break;
            }
        });

        $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
            var tabId = $(e.target).attr("href");
            var table = tabId.replace("#tab-content-","");
            try {
                eval(table+ "Table").table.columns.adjust();
            } catch(e) {
                //console.log(e);
            }
            if(typeof(Storage)!=="undefined"){
            	sessionStorage.tabFlag = table;
            }
        });
    });
    //初始化任务数量提醒
    function initWarnNum(typeTable, type, noWarn){
        var total = typeTable.table.page.info().recordsTotal;
        $("#nav-tab-"+type + " .banner").remove();
        if(total > 0){
        	var bannerClass = noWarn == false ? "" : "warn";
            $("#nav-tab-"+type).append("<span class='banner " + bannerClass + "'>("+total+")</span>");
        }
    }

    //流程明细
    function showProcessInfo(procInstId){
         modals.openWin({
             winId: "flowId",
             title: "流程明细",
             width: "1000px",
             url:basePath+"/workflow/modals/instance/"+procInstId + ".html"
         });
    }
    function quickApproveContent() {
        $("select.quickApprove").popover({
            content : '<textarea class="quickApprovePop" name="content"></textarea>',
            trigger : 'click',
            placement : 'top',
            html : true,
        });
        $("#" + auditTable.tableId).on("shown.bs.popover", "select.quickApprove", function() {
            var $this = $(this);
            var popoverId = $this.attr("aria-describedby");
            var oldContent = $this.parents("tr").find("textarea.quickApprove").val();
            $("#" + popoverId).find("textarea.quickApprovePop").val(oldContent).focus();
            $("#" + popoverId).find("textarea.quickApprovePop").blur(function() {
                $this.popover('hide');
            })
        });
        $("#" + auditTable.tableId).on("hide.bs.popover", "select.quickApprove", function() {
            var popoverId = $(this).attr("aria-describedby");
            var content = $("#" + popoverId).find("textarea.quickApprovePop").val();
            if (content) {
                $(this).parents("tr").find("textarea.quickApprove").val(content);
            }
        });
    }
    function initTabByStorage(){
    	if(typeof(Storage)!=="undefined" && sessionStorage.tabFlag){
   			$("section.content ul.nav.nav-tabs li.active").removeClass("active");
   			$("section.content .tab-pane.active").removeClass("active");
   			$("#nav-tab-"+sessionStorage.tabFlag).parent().addClass("active");
   			$("#tab-content-"+sessionStorage.tabFlag).addClass("active");
    	}else{
    		$("#nav-tab-toDo").parent().addClass("active");
   			$("#tab-content-toDo").addClass("active");
    	}
    }
</script>
</jsTag>
</html>