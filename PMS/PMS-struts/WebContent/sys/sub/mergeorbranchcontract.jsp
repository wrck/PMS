<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<head>
<dp:base />
<script type="text/javascript">
$(function(){
	var firstTab = "<s:property value='navTabList[0].basicDataId'/>";
	$("."+firstTab).removeClass("hideDiv");
	
	
	$("#mergeContractCheck").click(function(){
		checkMergeContract();
	});
});
/**
 * 查询合同信息
 */
function checkMergeContract(){
	var mergeContract = $("#mergeContract").val();
	if(mergeContract != ''){
		$.ajax({
			url:"checkMergeContract.action",
			type:"post",
			dataType:"json",
			data:{mergeContractNo:mergeContract},
			success:function(data){
				var result = data.result;
				if(result == 404){
					alert("您输入的合同已创建了项目，请重新输入");
					$("#mergeContract").val("");
					$("#mergeContract").focus();
				}else{
					
				dealWith(data.contractList);
				}
			}
		});
	}
}

function dealWith(objs){
	var len = objs.length;
	if(len > 0){
		var html = '';
		for(var i = 0 ;i < len ;i ++){
			html += '<tr>';
			html += '<td><input name="selected" type="checkbox" value="'+ objs[i].contractNo +'"></td>';
			html += '<td>'+objs[i].contractNo +'</td>';
			html += '<td>'+objs[i].projectName +'</td>';
			html += '<td>'+objs[i].customerName +'</td>';
			html += '<td>'+objs[i].orderCreateString +'</td>';
			html += '<td>'+objs[i].marketName +'</td>';
			html += '<td>'+objs[i].salesManName +'</td>';
			html += '</tr>';
		}
		
		$("#mergeTable").append(html);
		$(".hideMark").show();
	}else{
		alert("查询不出该合同号，请重新输入！");
		$("#mergeContract").val("");
		$("#mergeContract").focus();
		$(".hideMark").hide();
		
	}
}
/**
 * 查询结果清空
 */
function mergeEmpty(){
	if(confirm("是否将查询结果全部清空?")){
		$("#mergeTable").html("");
	}
}
function mergeContractNo(){
	
	var str="";
	$("input[name=selected]:checked").each(function(){ //由于复选框一般选中的是多个,所以可以循环输出
		   str += $(this).val();
	});
   	if(str == ""){
   		alert("请至少选择一条合同数据进行合并，谢谢！");
   		return false;
   	}
	
	if(confirm("是否确认将选中的合同号合并到当前项目中?")){
		$("#mergeFrom").submit();
	}
}

//拆分
function checkBranchQuantity(obj){
	 var branch = parseInt($("#branchQuantity"+obj).val());
	 if(branch <= 0 && $("#branchQuantity"+obj).val().length > 1){
		 alert("请输入正整数类型的数量，谢谢！");
		 $("#branchQuantity"+obj).val(0);
	 }
	 var project = parseInt($("#projectQuantity"+obj).val());
	if(project < branch){
		alert("拆分出去的产品数量不能大于原项目产品数量!谢谢");
		$("#branchQuantity"+obj).val(0);
	}
}

function branchContractNo(){
	var size = "<s:property value='orderDataList.size()'/>";
	var b = false;
	for(var i = 0 ; i < size ;i ++){
		if(parseInt($("#branchQuantity"+i).val()) > 0){
			b = true;
		}
	}
	if(!b){
		alert("请至少选择一条产品数据拆分到新项目当中，谢谢!");
	}else{
		if(confirm("请确认是否拆分新项目？拆分后不可修改")){
			$("#branchFrom").submit();
		}
	}
}
</script>
</head>
<body>
	<div class="panel panel-default">
   		<div class="panel-body">
			<nav class="navbar navbar-default" role="navigation" style="margin-top: 20px;">
				<div>
				    <ul class="nav navbar-nav">
				    	<s:iterator value="navTabList" var="nav" status="index">
					   		<s:if test="%{#index.index == 0}">
				    			<li name="navli" class="active nav<s:property value='#index.index'/>" onclick="clickNavLi(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
				    		</s:if>
				    		<s:else>
						   		<li name="navli" class="nav<s:property value='#index.index'/>" onclick="clickNavLi(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
				    		</s:else>
				    	</s:iterator>
				   </ul>
				</div>
			</nav>	    
   		</div>
   		<!-- 合同合并 -->
		<div class="navDiv hideDiv merge" id="merge">
			<s:form cssClass="form-horizontal" id="mergeFrom" action="module/MergeContract.action" role="form"> 
				<s:hidden name="projectId" value="%{project.projectId}"/>	
				<div class="form-group">
					<div class="col-sm-5" style="padding-left: 45px">
						<input type="text" id="mergeContract" placeholder="请输入要合并进当前项目的合同号,已创建项目的合同号不可合并!" class="form-control"/>
					</div>
					<div>
						<a href="javascript:void(0)" id = "mergeContractCheck" title="支持合并多个合同" class="btn btn-warning">
				          	<span class="glyphicon glyphicon-search"></span> <s:text name="sys.query"></s:text>
				        </a>
					</div>
				</div>
				<div>
					<table class="table">
						<thead class="hideMark">
							<tr>
							<th><input type="checkbox" name="checkall" value="all" onclick="checkAll();"></th>
							<th><s:text name="pm.project.contractNo"></s:text></th>
							<th><s:text name="pm.project.projectName"></s:text></th>
							<th><s:text name="pm.project.customerName"></s:text></th>
							<th><s:text name="pm.project.orderCreateTime"></s:text></th>
							<th><s:text name="pm.project.marketName"></s:text></th>
							<th><s:text name="pm.salesman"></s:text></th>
							</tr>
						</thead>
						<tbody id="mergeTable">
							
						</tbody>
					</table>
				</div>
				<button type="button" onclick="mergeContractNo()" class="btn btn-warning form-group-margin"><s:text name="sys.confirm"></s:text></button>
				
				<button type="button" onclick="mergeEmpty()" class="btn btn-warning form-group-margin"><s:text name="sys.empty"></s:text></button>
			</s:form>
		</div>
		<!-- 项目拆分 X-->
		<div class="navDiv hideDiv branch" id="branch">
			<s:form cssClass="form-horizontal" id="branchFrom" action="module/BranchContract.action" role="form"> 
				<s:hidden name="projectId" value="%{project.projectId}"/>
				<s:hidden name="project.projectCode"></s:hidden>
				<table class="table table-striped">
					<thead>
						<tr>
							<th><s:text name="pm.orderdata.contractNo"></s:text></th>
							<th><s:text name="pm.orderdata.itemCode"></s:text></th>
							<th><s:text name="pm.orderdata.itemName"></s:text></th>
							<th><s:text name="project.product.quantity"></s:text></th>
							<th><s:text name="pm.branch.quantity"></s:text></th>
							<th><s:text name="pm.orderdata.orderQuantity"></s:text></th>
							<th><s:text name="pm.orderdata.deliverQuantity"></s:text></th>
							<th><s:text name="pm.orderdata.openQuantity"></s:text></th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="orderDataList" status="index" var="p">
							<tr>
								<td><s:property value="#p.contractNo"/>
									<s:hidden name="productList[%{#index.index}].id" value="%{#p.id}"> </s:hidden>
									<s:hidden name="productList[%{#index.index}].contractNo" value="%{#p.contractNo}"> </s:hidden>
									<s:hidden name="productList[%{#index.index}].itemCode" value="%{#p.itemCode}"> </s:hidden>
									<s:hidden name="productList[%{#index.index}].projectQuantity" id="projectQuantity%{#index.index}" value="%{#p.projectQuantity}"> </s:hidden>
									<s:hidden name="productList[%{#index.index}].orderQuantity" value="%{#p.orderQuantity}"> </s:hidden>
									<s:hidden name="productList[%{#index.index}].deliverQuantity" value="%{#p.deliverQuantity}"> </s:hidden>
									<s:hidden name="productList[%{#index.index}].openQuantity" value="%{#p.openQuantity}"> </s:hidden>
									<s:hidden name="productList[%{#index.index}].itemName" value="%{#p.itemName}"> </s:hidden>
								</td>
								<td><s:property value="#p.itemCode"/></td>
								<td><s:property value="#p.itemName"/></td>
								<td><s:property value="#p.projectQuantity"/></td>
								<td><s:textfield name="productList[%{#index.index}].branchQuantity" onchange="checkBranchQuantity(%{#index.index})" id="branchQuantity%{#index.index}" cssStyle="width:65px" value="0" cssClass="form-control"/> </td>
								<td><s:property value="#p.orderQuantity"/></td>
								<td><s:property value="#p.deliverQuantity"/></td>
								<td><s:property value="#p.openQuantity"/></td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
				<div class="form-group">
					<label for="branchMark" class="col-sm-1 control-label"><s:text name="pm.branch.mark"></s:text></label>	
					<div class="col-sm-5">
						<s:textfield name="mergeBranchMark" id="branchMark" placeholder="请输入简单备注信息,将追加在新创建项目的项目名称后面!" cssClass="form-control"></s:textfield>
					</div>
				</div>
				
				<button type="button" onclick="branchContractNo()" class="btn btn-warning form-group-margin"><s:text name="sys.confirm"></s:text></button>
					
				<button type="reset" class="btn btn-warning form-group-margin"><s:text name="sys.reset"></s:text></button>
			</s:form>
		</div>
	</div>
	
</body>
</html>