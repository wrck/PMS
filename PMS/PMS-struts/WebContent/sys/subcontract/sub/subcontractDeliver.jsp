<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<html>
<dp:base />
<head>
</head>
<body>
	<div style="text-align: left;">
		<display:table style="text-align: left;"
            name="subcontractDeliverList" pagesize="${subcontractDeliverList.size()}" export="false" id="subcontractDeliverTable"
            size="${subcontractDeliverList.size()}" sort="external"
            decorator="com.dp.plat.subcontract.decorators.SubcontractDecorator" class="displayTable table table-condensed table-hover table-striped" 
            partialList="true" >
            <display:column property="subcontractFileName" titleKey="file.name"></display:column>
            <display:column property="typeName" titleKey="pm.subcontract.deliver.type"></display:column>
            <display:column property="uploadName" titleKey="file.uploadby"></display:column>
            <display:column property="uploadTime" titleKey="file.uploadtime" format="{0,date,yyyy-MM-dd HH:mm:ss}"></display:column>
        	<display:column titleKey="pm.subcontract.operate" headerClass="deliverOperator" class="deliverOperator">
        		<a href='javascript:void(0)' onclick='deleteDeliverById("${subcontractDeliverTable.id}", this)'>删除</a>
        	</display:column>
        </display:table>
        <script>
        $(function() {
        	var isDisabledFile = $("#subcontractForm").find("[type='file']").attr("disabled");
        	if(isDisabledFile) {
        		$("#subcontractDeliverTable .deliverOperator").remove();
        	}
        })
        function deleteDeliverById(deliverId, e){
    		if(!confirm("是否确认删除？")){
    			return;
    		}
    		var params = [];
    		if(!$.isArray(deliverId)) {
    			deliverId = [deliverId];
    		}
    		for (var i = 0; i < deliverId.length; i++) {
				var id = deliverId[i];
				params.push($.param({
					"subcontractDeliverVO.ids": id
					})
				)
			}
    		$.ajax({
    			url :"module/s/subcontractAjax_deleteSubcontractDeliver.action",
    			type :"post",
    			dataType :"json",
    			data : params.join("&"),
    			success:function(data){
    				if(data.deliverid != 0){
    					$(".nav li[name='navli'].active").dblclick();
    				}else{
    					alert("删除失败！");
    				}
    			}
    		});
    	}
        </script>
	</div>
</body>
</html>