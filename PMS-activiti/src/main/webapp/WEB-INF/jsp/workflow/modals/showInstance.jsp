<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal-header" style="padding-bottom: 0;">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
        <li class="fa fa-remove"></li>
    </button>
    <!--<h5 class="modal-title"></h5>-->
    <ul class="nav nav-tabs">
        <li class="active"><a href="#tab-content-xml" data-toggle="tab" id="nav-tab-list" aria-expanded="true"><i class="fa fa-file-code-o"></i>&nbsp;流程明细</a></li>
        <li class=""><a href="#tab-content-img" data-toggle="tab" id="nav-tab-edit" aria-expanded="false"><i class="fa fa-image" style="font-size: 1.05rem"></i>&nbsp;png</a></li>
    </ul>
</div>
<div class="modal-body nav-tabs-custom" style="height: 75vh;padding: 0px;">
    <div class="tab-content" style="padding: 0 15px;">
		<div class="tab-pane active" id="tab-content-xml" style="min-height: 75vh; font-size: 1rem;">
            <table id="instanceInfoTable" class="table table-bordered table-striped dataTable">
            </table>
		</div>
		<div class="tab-pane" id="tab-content-img">
            <div id="imageContainer" class="text-center" align="center">
                <img src="" style="max-width:100%;">
            </div>
        </div>
    </div>
</div>
<script>
$(function(){
    var processInstanceId = "${processInstanceId != 0 && processInstanceId != null ? processInstanceId : 0}";
    var infoPath = basePath+"/workflow/instance/info/${processInstanceId}/list.json";
    var diagramPath = basePath+"/workflow/instance/diagram/${processInstanceId}";
    var instanceInfoTable;
    if (processInstanceId != 0) {
        $("#imageContainer img").attr("src", diagramPath);
        ajaxPost(infoPath, null, function (result) {
            instanceInfoTable = new CommonLocalTable("instanceInfoTable", result.data, 
                {
                searching:false,
                paging: false,
                info: false,
                lengthChange: false,
                rowId: 'id',
                "columns" : [
                    {
                        title : "任务ID",
                        data : "id",
                        visible: true,
                        sortable: false,
                    },
                    {
                        title : "任务名称",
                        data : "activityName",
                        visible: true,
                        sortable: false,
                    },
                    {
                        title : "处理人",
                        data : "assigneeName",
                        visible: true,
                        sortable: false,
                    },
                    {
                        title : "任务状态",
                        data : "activityState",
                        visible: true,
                        sortable: false,
                        render: fnRenderActivityState
                    },
                    {
                        title : "审批结果",
                        data : "approved",
                        visible: true,
                        sortable: false,
                        render: fnRenderApproved
                    },
                    {
                        title : "审批意见",
                        data : "suggestion",
                        visible: true,
                        sortable: false,
                    }]
            });
        })
    }
    
    //任务状态
    function fnRenderActivityState(value, type, rowObj) {
        if (value == 0) {
            return '<span class="text-success">已执行</span>'
        } else if (value == 1) {
            return '<span class="text-danger">待执行</span>';
        }else{
            return '<span class="text-warning">未执行</span>';
        }
        return value;
    }

    //审批结果
    function fnRenderApproved(value,type,rowObj){
        if(value=='true'){
            return '<span class="text-success">同意</span>';
        }else if(value=='false'){
            return '<span class="text-danger">拒绝</span>';
        }else if(value){
            return '<span class="text-primary">'+value+'</span>';
        }else if(rowObj.activityType == "startEvent"){
            return '<span class="text-info">开始</span>';
        }else if(rowObj.activityType == "endEvent"){
            return '<span class="text-info">结束</span>';
        }else if(rowObj.activityState == 0){
            return '<span class="text-primary">提交</span>';
        }else {
            return value;
        }
    }
})
</script>
