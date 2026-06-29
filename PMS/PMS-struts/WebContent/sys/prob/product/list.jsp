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
	function deleteProbProduct(probProductId){
		if(confirm("确认要删除该产品组件？")){
			$.ajax({
				url:"module/probProduct_delete.action",
				type:"post",
				data:{"probProduct.id":probProductId},			    dataType:"html",
				success:function(data){
					$("html").html(data);
				}
			})
		}
	}
	
    function autoCompleteProbProduct() {
        $.ajax({
            url:"module/s/probAjax_listProbProduct.action",
            type:"post",
            dataType:"json",
            data:{"probProduct.status":1, "result": "json"},
            success:function(data){
                var list = JSON.parse(data.result || data);
                var probProducts = [];
                var products = [];
                var probProductList = [];
                var productList = []
                $.each(list, function(index, item) {
                	if ($.inArray(item.type, products) == -1) {
                		products.push(item.type);
                		productList.push({
                            label: item.type
                		});
                    }
                	if ($.inArray(item.name, probProducts) == -1) {
                		probProducts.push(item.name);
                		probProductList.push({
                			label: item.name
                		});
                	}
                	item.label = item.type + ":" + item.name;
                });
                $("#probProductType").autocomplete({
                    minLength: 0,
                    source: productList
                })
                $("#probProductName").autocomplete({
                    minLength: 0,
                    source: probProductList,
                    /* change: function( event, ui ) {
                    	var item = ui.item || {};
                        //$("#probProductName").val(item.label);
                        $("#probProductId").val(item.id);
                        return false;
                    }, */
                    /* focus: function( event, ui ) {
                    	//var item = ui.item || {};
                        //$("#probProductName").val(item.label);
                        //$("#probProductId").val(item.id);
                        return false;
                    }, */
                    /* search: function( event, ui ) {
                        var item = ui.item || {};
                        //$("#probProductName").val(item.label);
                        //$("#probProductId").val(item.id);
                        $(this).val($.trim($(this).val()));
                        return true;
                    }, */
                    /* select: function( event, ui ) {
                    	var item = ui.item || {};
                        $("#probProductName").val(item.label);
                        $("#probProductId").val(item.id);
                        return false;
                    } */
                })
                $("#probProductType").autocomplete({
                    minLength: 0,
                    source: productList
                })
            },
            error:function(XMLHttpRequest,textStatus,errorThrown){
                alert("获取产品组件失败！错误信息如下：<br>"+XMLHttpRequest.responseText);
            }
        })
    }
    
    $(function(){
    	autoCompleteProbProduct();
    })
</script>
</head>
<body>
	<div class="container-flux">
		<s:form method="post" action="module/probProduct_list.action" id="mainForm"
					cssClass="form-horizontal" name="mainForm" enctype="multipart/form-data">
		<div class="form-group">
			<label for="probProductType" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="probProduct.info.type"></s:text></label>
			<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                <s:textfield id="probProductType" name="probProduct.type" cssClass="form-control"></s:textfield>
            </div>
            <label for="probProductName" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="probProduct.info.name"></s:text></label>
            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
				<s:textfield id="probProductName" name="probProduct.name" cssClass="form-control"></s:textfield>
			</div>
            <label for="probProductState" class="col-xs-1 col-sm-1 col-md-1 col-lg-1 control-label"><s:text name="probProduct.info.name"></s:text></label>
            <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
                <s:select id="probProductState" list="#{true:'有效',false:'失效'}" name="probProduct.state" cssClass="form-control" headerKey="" headerValue="全部"></s:select>
            </div>
            <div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
                <button type="submit" id="submit" class="btn btn-default btn-block btn-sm"><s:text name='sys.query' /></button>
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
					<s:text name="probProduct.manage.list"></s:text>
				</div>
				<div class="list-button">
					<s:if test="%{user.isHasRole(22) || user.isHasRole(1)}">
						<a href="module/probProduct_input.action" class="btn btn-default">
							<s:text name='probProduct.manage.create' />
						</a>
                        <a href="module/probProduct_import.action" class="btn btn-default">
                            <s:text name='probProduct.manage.import' />
                        </a>
	                </s:if>
                </div>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="text-align: left;">
				<display:table name="commonList" pagesize="${displayParam.pagesize}"
					export="true" size="${displayParam.totalcount}" sort="external"
					requestURI="module/probProduct_list.action"
					decorator="com.dp.plat.decorators.Wrapper"
					class="table table-striped" partialList="true" >
                <s:if test="%{user.isHasRole(22) || user.isHasRole(1)}">
                    <display:column property="id" titleKey="probProduct.info.id" sortable="true" url="/module/probProduct_input.action" paramId="probProduct.id" paramProperty="id"></display:column>
                    <display:column property="type" titleKey="probProduct.info.type" sortable="true"></display:column>
					<display:column property="name" titleKey="probProduct.info.name" sortable="true" url="/module/probProduct_input.action" paramId="probProduct.id" paramProperty="id"></display:column>
				</s:if>
                <s:else>
                    <display:column property="id" titleKey="probProduct.info.id" sortable="true"></display:column>
                    <display:column property="type" titleKey="probProduct.info.type" sortable="true"></display:column>
                    <display:column property="name" titleKey="probProduct.info.name" sortable="true"></display:column>
                </s:else>
                    <display:column property="version" titleKey="probProduct.info.version" sortable="true"></display:column>
                    <display:column property="stateWrapper" titleKey="probProduct.info.status" sortable="true" media="html"></display:column>
					<display:column property="state" titleKey="probProduct.info.status" sortable="true" media="excel"></display:column>
                    <display:column property="createTime" titleKey="fnd.baisc.data.createTime" sortable="true" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
					<display:column property="updateTime" titleKey="fnd.basic.data.updateTime"  sortable="true" format="{0,date,yyyy-MM-dd HH:mm}"></display:column>
					<display:column property="createBy" titleKey="fnd.basic.data.createBy"></display:column>
				</display:table>
			</div>
		</div>
		</s:form>
	</div>
</body>
</html>