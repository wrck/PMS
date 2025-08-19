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
<link rel="stylesheet" type="text/css" href="statics/plugins/select2/select2.min.css" />
<dp:link rel="stylesheet" type="text/css" href="css/prob/prob.css" />
<style>
	.btn-link{
		margin-right:0.5rem;
	}
    .form-horizontal .form-group.form-group-query{
        margin: 0px;
    }
    .form-inline .form-group-query {
        white-space: nowrap;
    }
    .form-groups {
        margin-bottom: 15px;
    }
</style>
<script type="text/javascript" src="statics/plugins/select2/select2.js"></script>
<script type="text/javascript" src="statics/plugins/select2/i18n/zh-CN.js"></script>
<dp:script type="text/javascript" src="js/prob/renderCascade.js"></dp:script>
<dp:script type="text/javascript" src="js/prob/render.js"></dp:script>
<script type="text/javascript">
	function deleteProb(probId){
		if(confirm("确认要删除该技术公告？")){
			$.ajax({
				url:"module/prob_delete.action",
				type:"post",
				data:{"prob.probId":probId},
				dataType:"html",
				success:function(data){
					$("html").html(data);
				}
			})
		}
	}
	
	function autoCompleteComponent() {
        $.ajax({
            url:"module/s/probAjax_listComponent.action",
            type:"post",
            dataType:"json",
            data:{"productComponent.state":true, "result": "json"},
            success:function(data){
                var list = JSON.parse(data.result || data);
                $.each(list, function(index, item) {
                	item.label = item.type + ":" + item.name + ":" + item.version;
                });
                $("#componentName").autocomplete({
                    minLength: 0,
                    source: list,
                    change: function( event, ui ) {
                        var item = ui.item || {};
                        //$("#componentName").val(item.label);
                        $("#componentId").val(item.id);
                        return false;
                    },
                    focus: function( event, ui ) {
                        //var item = ui.item || {};
                        //$("#componentName").val(item.label);
                        //$("#componentId").val(item.id);
                        return false;
                    },
                    /* search: function( event, ui ) {
                        var item = ui.item || {};
                        //$("#componentName").val(item.label);
                        //$("#componentId").val(item.id);
                        $(this).val($.trim($(this).val()));
                        return true;
                    }, */
                    select: function( event, ui ) {
                        var item = ui.item || {};
                        $("#componentName").val(item.label);
                        $("#componentId").val(item.id);
                        return false;
                    }
                })
            },
            error:function(XMLHttpRequest,textStatus,errorThrown){
                alert("获取产品组件失败！错误信息如下：<br>"+XMLHttpRequest.responseText);
            }
        })
        $("#componentName").tooltip();
    }
	
	function autoCompleteTrackingUser() {
        $.ajax({
            url:'queryalluser.action',
            type:'post',
            dataType:'json',
            data:{roleid :20},
            success:function(data){
                var list = data.allusernameList || data.personList || [];
                $.each(list, function(index, item) {
                    item.value = item.username;
                    item.label = item.username + "-" + item.realName;
                });
                $("#trackingUserName").autocomplete({
                    minLength: 0,
                    source: list,
                    change: function( event, ui ) {
                        var item = ui.item || {};
                        //$("#trackingUserName").val(item.label);
                        $("#trackingUser").val(item.value);
                        return false;
                    },
                    focus: function( event, ui ) {
                        //var item = ui.item || {};
                        //$("#trackingUserName").val(item.label);
                        //$("#trackingUser").val(item.value);
                        return false;
                    },
                    /* search: function( event, ui ) {
                        var item = ui.item || {};
                        //$("#componentName").val(item.label);
                        //$("#componentId").val(item.id);
                        $(this).val($.trim($(this).val()));
                        return true;
                    }, */
                    select: function( event, ui ) {
                        var item = ui.item || {};
                        $("#trackingUserName").val(item.label);
                        $("#trackingUser").val(item.value);
                        return false;
                    }
                })
            },
            error:function(XMLHttpRequest,textStatus,errorThrown){
                alert("获取用户失败！错误信息如下：<br>"+XMLHttpRequest.responseText);
            }
        })
    }
    
    $(function(){
        autoCompleteComponent();
        autoCompleteTrackingUser();
        //initProbProductBySelect2("probProducts", null, {maxSelectedLength: 2});
    })
</script>

</head>
<body>
	<div class="container-flux">
		<s:form method="post" action="module/prob_list.action" id="mainForm" cssClass="form-horizontal" name="mainForm" enctype="multipart/form-data">
        <div class="form-groups clearfix">
            <div class="form-group form-group-query col-xs-12 col-sm-6 col-md-3 col-lg-2">
                <label for="probNum" class="control-label"><s:text name="prob.info.num"></s:text></label>
    			<s:textfield id="probNum" name="prob.probNum" cssClass="form-control"></s:textfield>
    		</div>
    		<div class="form-group form-group-query col-xs-12 col-sm-6 col-md-3 col-lg-2">
                <label for="theme" class="control-label"><s:text name="prob.info.theme"></s:text></label>
    			<s:textfield id="theme" name="prob.theme" cssClass="form-control"></s:textfield>
    		</div>
    		<div class="form-group form-group-query col-xs-12 col-sm-6 col-md-3 col-lg-2">
                <label for="watch" class="control-label"><s:text name="prob.info.watchType"></s:text></label>
                <s:select id="watch" name="prob.watch" list="watchList" listKey="basicDataId" listValue="basicDataName"
                        headerKey="" headerValue="--请选择--"   cssClass="form-control"></s:select>
            </div>
    		<div class="form-group form-group-query col-xs-12 col-sm-6 col-md-3 col-lg-2">
                <label for="status" class="control-label"><s:text name="prob.info.status"></s:text></label>
    			<s:select id="status" name="prob.status" list="statusList" listKey="basicDataId" listValue="basicDataName"
    					headerKey="" headerValue="--请选择--"	 cssClass="form-control"></s:select>
    		</div>
            <div class="form-group form-group-query col-xs-12 col-sm-6 col-md-3 col-lg-2">
                <label for="status" class="control-label"><s:text name="prob.tracking.user"></s:text></label>
                <s:textfield id="trackingUserName" name="prob.customInfo.trackingUserName" cssClass="form-control"></s:textfield>
                <s:hidden id="trackingUser" name="prob.customInfo.trackingUserSearch"></s:hidden>
    		</div>
    		<div class="form-group form-group-query col-xs-12 col-sm-6 col-md-3 col-lg-2">
                <label for="productType" class="control-label"><s:text name="prob.info.product.type"></s:text></label>
                <s:textfield id="productType" name="prob.productType" cssClass="form-control"></s:textfield>
            </div>
            <div class="form-group form-group-query col-xs-12 col-sm-6 col-md-3 col-lg-2">
                <label for="desc" class="control-label"><s:text name="prob.info.desc"></s:text></label>
                <s:textfield id="desc" name="prob.desc" cssClass="form-control"></s:textfield>
            </div>
            <div class="form-group form-group-query col-xs-12 col-sm-6 col-md-3 col-lg-2">
                <label for="status" class="control-label"><s:text name="prob.info.affected.type"></s:text></label>
                <s:select id="affectedType" name="prob.affectedType" cssClass="form-control" list="#{1:'盒式系列',2:'框式系列',-1:'其它系列'}" headerKey="" headerValue="--请选择--"></s:select>
            </div>
            <div class="form-group form-group-query col-xs-12 col-sm-6 col-md-3 col-lg-2">
                <label for="status" class="control-label"><s:text name="prob.info.affected.version"></s:text></label>
                <s:textfield id="affectedVersion" name="prob.affectedVersion" cssClass="form-control"></s:textfield>
            </div>
            <div class="form-group form-group-query col-xs-12 col-sm-6 col-md-3 col-lg-2">
                <label for="submit" class="control-label" style="width: 100%;">　</label>
                <input type="hidden" name="prob.visibleRange" value="-1">
                <button type="submit" id="submit"  class="btn btn-default btn-sm"><s:text name='sys.query' /></button>
            </div>
        </div>
		<div class="row">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
				<div class="listView divHeader">
					<img src="images/right_zhishi.gif" border="0">
					<s:text name="prob.manage.list"></s:text>
				</div>
				<div class="list-button">
					<s:if test="%{user.isHasRole(20)}">
						<a href="module/prob_input.action" class="btn btn-default">
							<s:text name='prob.manage.create' />
						</a>
					</s:if>
					<s:if test="%{user.isHasRole(18) || user.isHasRole(1)}">
						<a href="module/prob_export.action" class="btn btn-default">
							<s:text name='prob.manage.export' />
						</a>
					</s:if>
					<s:if test="%{user.isHasRole(19) || user.isHasRole(1)}">
                        <a href="module/prob_importSoftVersion.action" class="btn btn-default">
                            <s:text name='prob.manage.importSoftVersion' />
                        </a>
                        <a href="module/prob_statistics.action" class="btn btn-default">
                            <s:text name='prob.manage.statistics' />
                        </a>
	                </s:if>
                    <span class="display-inline-flex" style="width: 46rem;">
                        <s:textfield id="componentName" cssClass="form-control" placeholder="产品组件快速检索" title="仅用于检索公司产品涉及的第三方组件，不作为技术公告的筛选条件"></s:textfield>
                    </span>
                </div>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="text-align: left;">
				<display:table name="probList" pagesize="${displayParam.pagesize }"
					export="true" size="${displayParam.totalcount }" sort="external"
					requestURI="module/prob_list.action"
					decorator="com.dp.plat.decorators.Wrapper"
					class="table table-striped" partialList="true" >
					<s:if test="%{user.isHasRole(20) && !user.isHasRole(19) && !user.isHasRole(18)}">
						<display:column property="probNum" titleKey="prob.info.num" sortable="true" url="/module/prob_input.action" paramId="prob.probId" paramProperty="probId" media="html"></display:column>
					</s:if>
					<s:else>
						<display:column property="probEdit" titleKey="prob.info.num" sortable="true"  media="html"></display:column>
					</s:else>
                    <display:column property="probNum" titleKey="prob.info.num" media="excel"></display:column>
					<display:column property="theme" titleKey="prob.info.theme" sortable="true"></display:column>
					<display:column property="priority" titleKey="prob.info.level" sortable="true"></display:column>
					<display:column property="watchName" titleKey="prob.info.watchType" sortable="true"></display:column>
					<display:column property="statusName" titleKey="prob.info.status" sortable="true"></display:column>
					<display:column property="createTime" titleKey="prob.info.createTime" sortable="true" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
					<display:column property="updateTime" titleKey="prob.info.updateTime"  sortable="true" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
					<display:column property="trackingUsername" titleKey="prob.tracking.user"></display:column>
					<display:column property="probOperate" titleKey="display.operate" media="html"></display:column>
				</display:table>
			</div>
		</div>
		</s:form>
	</div>
</body>
</html>