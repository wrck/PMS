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
</style>
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
</script>
</head>
<body>
	<div class="container-flux">
		<s:form method="post" action="module/prob_list.action" id="mainForm"
					cssClass="form-horizontal" name="mainForm" enctype="multipart/form-data">
		<div class="form-group">
			<label for="probNum" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="prob.info.num"></s:text></label>
			<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
				<s:textfield id="probNum" name="prob.probNum" cssClass="form-control"></s:textfield>
			</div>
			<label for="theme" class="pull-left control-label"><s:text name="prob.info.theme"></s:text></label>
			<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
				<s:textfield id="theme" name="prob.theme" cssClass="form-control"></s:textfield>
			</div>
			<label for="status" class="pull-left control-label"><s:text name="prob.info.status"></s:text></label>
			<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
				<s:select id="status" name="prob.status" list="statusList" listKey="basicDataId" listValue="basicDataName"
						headerKey="" headerValue="--请选择--"	 cssClass="form-control"></s:select>
			</div>
		</div>
		<div class="form-group">
            <label for="productType" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="prob.info.product.type"></s:text></label>
            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                <s:textfield id="productType" name="prob.productType" cssClass="form-control"></s:textfield>
            </div>
            <label for="desc" class="pull-left control-label"><s:text name="prob.info.desc"></s:text></label>
            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                <s:textfield id="desc" name="prob.desc" cssClass="form-control"></s:textfield>
            </div>
            <label for="status" class="pull-left control-label"><s:text name="prob.info.affected.version"></s:text></label>
            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                <s:textfield id="affectedVersion" name="prob.affectedVersion" cssClass="form-control"></s:textfield>
            </div>
            <div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
                <input type="hidden" name="prob.visibleRange" value="-1">
                <button type="submit" id="submit"  class="btn btn-default  btn-block btn-sm"><s:text name='sys.query' /></button>
            </div>
        </div>
		<%-- <div class="form-group">
			<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
			   	<button type="submit" id="submit"  class="btn btn-default  btn-block btn-sm"><s:text name='sys.query' /></button>
		    </div>
		</div> --%>
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
                </div>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="text-align: left;">
				<display:table name="probList" pagesize="${displayParam.pagesize }"
					export="true" size="${displayParam.totalcount }" sort="external"
					requestURI="module/prob_list.action"
					decorator="com.dp.plat.prob.descorators.Wrapper"
					class="table table-striped" partialList="true" >
					<s:if test="%{user.isHasRole(20) && !user.isHasRole(19) && !user.isHasRole(18)}">
						<display:column property="probNum" titleKey="prob.info.num" sortable="true" url="/module/prob_input.action" paramId="prob.probId" paramProperty="probId"></display:column>
					</s:if>
					<s:else>
						<display:column property="probEdit" titleKey="prob.info.num" sortable="true"></display:column>
					</s:else>
					<display:column property="theme" titleKey="prob.info.theme" sortable="true"></display:column>
					<%-- <display:column property="priority" titleKey="prob.info.level" sortable="true"></display:column>
					<display:column property="watchName" titleKey="prob.info.watchType" sortable="true"></display:column>
					<display:column property="statusName" titleKey="prob.info.status" sortable="true"></display:column>
					<display:column property="createTime" titleKey="prob.info.createTime" sortable="true" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
					<display:column property="updateTime" titleKey="prob.info.updateTime"  sortable="true" format="{0,date,yyyy-MM-dd HH:mm}"></display:column> --%>
					
					<display:column property="simplifyDesc" titleKey="prob.info.desc" class="desc"></display:column>
					<%-- <display:column property="affectedVersion" titleKey="prob.info.affected.version"></display:column> --%>
					<display:column property="productType" titleKey="prob.info.product.type"></display:column>
					<display:column property="simplifySolution" titleKey="prob.info.solution" class="solution"></display:column>
					
					<display:column property="createTime" titleKey="prob.info.createTime" sortable="true" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
					<display:column property="trackingUsername" titleKey="prob.tracking.user"></display:column>
					<display:column property="probOperate" titleKey="display.operate"></display:column>
				</display:table>
			</div>
		</div>
		</s:form>
	</div>
</body>
</html>