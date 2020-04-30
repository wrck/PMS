<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal-header" style="border-bottom: none">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
        <li class="fa fa-remove"></li>
    </button>
    <!--<h5 class="modal-title"></h5>-->
    <ul class="nav nav-tabs">
        <li class="active"><a href="#tab-content-approve" data-toggle="tab" id="nav-tab-list" aria-expanded="true"><i class="fa fa-file-code-o"></i>&nbsp;流程审批</a></li>
        <li class=""><a href="#tab-content-png" data-toggle="tab" id="nav-tab-edit" aria-expanded="false"><i class="fa fa-image"></i>&nbsp;流程运行图示</a></li>
    </ul>
</div>
<div class="modal-body nav-tabs-custom" style="height: 600px;">
    <div class="tab-content" style="padding: 0px;">
		<div class="tab-pane active" id="tab-content-approve">
            <form name="taskInfo_form" id="taskInfo_form" class="form-horizontal bv-form" novalidate="novalidate">
                <div class="col-xs-12">
                    <div class="form-group">
                        <label class="col-xs-3 control-label">请假天数</label>
                        <div class="col-xs-7">
                           <input type="text" class="form-control" id="workDays" value="12" name="workDays" placeholder="请填写请假天数">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-xs-3 control-label">开始时间</label>
                        <div class="input-group col-xs-7" style="padding-left: 15px; padding-right: 15px;">
                            <span class="input-group-addon"><i class="fa fa-calendar"></i></span>
                            <input type="text" placeholder="请填写请假时间" name="beginDate" id="beginDate" data-flag="datepicker" class="form-control" data-format="yyyy-mm-dd" value="2017-09-06" >
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-xs-3 control-label">结束时间</label>
                        <div class="input-group col-xs-7" style="padding-left: 15px; padding-right: 15px;">
                            <span class="input-group-addon"><i class="fa fa-calendar"></i></span>
                            <input type="text" placeholder="请填写请假时间" name="endDate" id="endDate" data-flag="datepicker" class="form-control" data-format="yyyy-mm-dd" value="2017-09-18" >
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-xs-3 control-label">请假类型</label>
                        <div class="input-group col-xs-7" style="padding-left: 15px; padding-right: 15px;">
                            <select name="vacType" id="vacType">
                                <option>--请选择--</option>
                                <option value="0" selected="selected">年假</option>
                                <option value="1">事假</option>
                                <option value="2">病假</option>
                            <select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-xs-3 control-label">请假原因</label>
                        <div class="col-xs-7">
                           <input type="text" class="form-control" id="reason" value="test" name="reason" placeholder="请填写请假原因">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">操作</label>
                        <div class="col-sm-7">
                            <label for="reApply" class="control-label"> 
                                <input id="reApply" type="radio" name="reApply" data-flag="icheck" value="true"> 重提
                            </label> &nbsp; 
                            <label for="noReApply" class="control-label"> 
                                <input id="noReApply" type="radio" name="reApply" data-flag="icheck" value="false"> 终止
                            </label>
                        </div>
                    </div>
                </div>
                <div class="col-xs-12 text-center" id="taskInfo_form_btn" hidden>
                    <button type="submit" class="btn btn-primary" data-btn-type="submitTask"><i class="fa fa-save"></i>&nbsp;提交</button>
                    &nbsp;&nbsp;
                    <button type="button" class="btn btn-default" data-btn-type="retTolist"><i class="fa fa-reply"></i>&nbsp;返回待办</button>
                </div>
            </form>
            
            <form name="task_form" id="task_form" class="form-horizontal bv-form" novalidate="novalidate">
                <div class="col-xs-12">
					<div class="form-group">
                        <label class="col-sm-3 control-label">审批结果</label>
                        <div class="col-sm-7">
                            <label for="agree" class="control-label"> 
                                <input id="agree" type="radio" name="isPass" data-flag="icheck" value="true"> 同意
                            </label> &nbsp; 
                            <label for=""disagree"" class="control-label">
                                <input id="disagree" type="radio" name="isPass" data-flag="icheck" value="false"> 拒绝
                            </label>
                        </div>
                    </div>
					<div class="form-group has-feedback">
					    <label for="content" class="control-label col-xs-3">审批意见</label>
					    <div class="col-xs-7">
					        <textarea name="content" id="content" data-flag="icheck" placeholder="请填写审批意见" class="form-control"></textarea>
					    </div>
					</div>
	            </div>
	            <div class="col-xs-12 text-center" id="task_form_btn" hidden>
	                <button type="submit" class="btn btn-primary" data-btn-type="submitTask"><i class="fa fa-save"></i>&nbsp;提交</button>
	                &nbsp;&nbsp;
	                <button type="button" class="btn btn-default" data-btn-type="retTolist"><i class="fa fa-reply"></i>&nbsp;返回待办</button>
                </div>
            </form>
            <table id="instanceInfoTable" class="table table-bordered table-striped dataTable">
            </table>
		</div>
		<div class="tab-pane" id="tab-content-png">
            <div id="imageContainer" class="text-center" align="center">
                <img src="">
            </div>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/static/plugins/datepicker/bootstrap-datepicker.js"></script>
<script>
    var taskType = "${taskType}";
    var taskId = "${taskId}";
    var taskDefKey = "${taskDefKey}";
    var businessKey = "${businessKey}";
    var processInstanceId = "${processInstanceId}";
    var xmlPath = "instance/xml/${processInstanceId}";
    var diagramPath = "instance/diagram/${processInstanceId}";
    if (diagramPath != 0) {
        $("#imageContainer img").attr("src", diagramPath);
    }
    var form = null;
    var formId = "task_form";
    $(function(){
    	if (taskDefKey.indexOf("modify") >=0 ) {
    		formId = "taskInfo_form";
            $("#task_form").hide();
        }
    	$("#" + formId + "_btn").show();
    	
        //数据校验
        $("#" + formId).bootstrapValidator({
            message : '请输入有效值',
            feedbackIcons : {
                valid : 'glyphicon glyphicon-ok',
                invalid : 'glyphicon glyphicon-remove',
                validating : 'glyphicon glyphicon-refresh'
            },
            submitHandler : function(validator,roleform, submitButton) {
            	modals.confirm('确认提交该流程？', function () {
                    // TODO 传参类型待定
                    var params = form.getFormSimpleData();
                    params.businessKey = businessKey;
                    var url = basePath + '/workflow/'+ taskType +'/complete/' + taskId +".json";
                    if (taskDefKey.indexOf("modify") >=0 ) {
                    	url = basePath + '/workflow/'+ taskType +'/' + processInstanceId + '/' + taskId +".json?_method=PUT";
                    	params.id = businessKey;
                    }
                    ajaxPost(url, params, function (data, status) {
                    	if(data.status){
                            modals.correct(data.message);
                            taskTable.reloadRowData();
                        }else{
                            modals.error(data.message);
                        }
                    });
                });
            },
            fields : {
            	isPass : {
                    validators : {
                        notEmpty : {
                            message : '请选择审批结果'
                        }
                    }
                },
                content: {
                    validators : {
                        notEmpty : {
                            message : '请输入审批意见'
                        }
                    }
                }
            }
        });
        
        form= $("#" + formId).form();
    })
</script>
