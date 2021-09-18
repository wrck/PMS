<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
<dp:base />
<head>
<script type="text/javascript" src="js/prob/render.js"></script>
<script type="text/javascript">
	$(function(){
		$("#confirm").click(function(){
			var softVersionCodes = '';
			var $checked = $("input[name='softVersionCodes']:checked");
			if($checked.length == 0) {
				alert("请选择需要确认的版本!");
				return false;
			}
			$checked.each(function(){
				softVersionCodes += $(this).val();
				softVersionCodes += ', ';
			});
			if(softVersionCodes!=''){
				$.ajax({
					url:"module/sub/submitSoftVersion.action",
					type:'post',
					data:{'softVersionCodes':softVersionCodes},
					dataType:'json',
					success:function(data){
						renderSoftVersions(data.result, {
							$container: $("#softVersionList", window.top.document), 
							readOnly: false, 
							ignoreSub: false, 
							onlyAppend: true
						});
						/* var softversionList =JSON.parse(data.result);
						var html = "";
						var prevCount = $("#softVersionList .softVersion", window.top.document).length;
						for(var i in softversionList){
							var softversion = softversionList[i];
							var index = parseInt(i) + prevCount;
							html += "<span class='softVersion'>";
							if(softversion.conp){
								html += "<input type='hidden' name='softVersionList["+index+"].conp' value='"+softversion.conp+"'>"
								+"conp:"+softversion.conp;
							}
							if(softversion.boot){
								html += "<input type='hidden' name='softVersionList["+index+"].boot' value='"+softversion.boot+"'>"
								+"boot:"+softversion.boot;
							}
							if(softversion.cpld){
								html += "<input type='hidden' name='softVersionList["+index+"].cpld' value='"+softversion.cpld+"'>"
								+"cpld:"+softversion.cpld;
							}
							if(softversion.pcb){
								html += "<input type='hidden' name='softVersionList["+index+"].pcb' value='"+softversion.pcb+"'>"
								+"pcb:"+softversion.pcb;
							}
							html+="<br>";
							html += "</span>"
						}
						$("#softVersionList",window.top.document).append(html); */
						closeThis();
					}
				})
				return true;
			}
			return false;
		});
	});
	function closeThis(){
		parent.closeWindow("BudgetUpload");
	}
	
	function checkAll(){
		$("#checkHeader").attr("onclick","checknoAll()");
		$("input[name='softVersionCodes']").each(function(){
			$(this).prop("checked","checked");
		});
	}
	function checknoAll(){
		$("#checkHeader").attr("onclick","checkAll()");
		$("input[name='softVersionCodes']").each(function(){
			$(this).removeAttr("checked");
		});
	}
</script>
</head>
<body>
	<div>
		<div class="row">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
					<div class="panel panel-default">
						<!-- 请求路径改为绝对路径 ，避免因路径问题产生其他资源加载问题
					求绝对路径行号
					${pageContext.request.contextPath}
					< %=request.getContextPath()%>
				 -->
				<form method="post" action="${pageContext.request.contextPath }/module/sub/toCheckSoftVersion.action" id="QueryForm"
					class="form-horizontal" name="QueryForm">
						<s:hidden name="redirect"></s:hidden>
						<div class="panel-body">
							<div class="form-group">
								<label for="conp" class="col-xs-1 col-sm-1 col-md-1 col-lg-1"><s:text name="prob.info.conp"></s:text></label>
								<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
								<s:select list="#{'like':'包含','not like':'不包含', 'between':'区间范围', 'regexp':'正则匹配' }" name="softVersion.conpCondition"
									cssClass="form-control"></s:select>
								</div>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<s:textfield id="conp" name="softVersion.conp" cssClass="form-control"></s:textfield>
								</div>
								<label for="cpld" class="col-xs-1 col-sm-1 col-md-1 col-lg-1"><s:text name="prob.info.cpld"></s:text></label>
								<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
								<s:select list="#{'like':'包含','not like':'不包含', 'between':'区间范围', 'regexp':'正则匹配' }" name="softVersion.cpldCondition"
									cssClass="form-control"></s:select>
								</div>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<s:textfield id="cpld" name="softVersion.cpld" cssClass="form-control"></s:textfield>
								</div>
							</div>
							<div class="form-group">
								<label for="boot" class="col-xs-1 col-sm-1 col-md-1 col-lg-1"><s:text name="prob.info.boot"></s:text></label>
								<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
								<s:select list="#{'like':'包含','not like':'不包含', 'between':'区间范围', 'regexp':'正则匹配' }" name="softVersion.bootCondition"
									cssClass="form-control"></s:select>
								</div>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<s:textfield id="boot" name="softVersion.boot" cssClass="form-control"></s:textfield>
								</div>
								<label for="pcb" class="col-xs-1 col-sm-1 col-md-1 col-lg-1"><s:text name="prob.info.pcb"></s:text></label>
								<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
								<s:select list="#{'like':'包含','not like':'不包含', 'between':'区间范围', 'regexp':'正则匹配' }" name="softVersion.pcbCondition"
									cssClass="form-control"></s:select>
								</div>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
									<s:textfield id="pcb" name="softVersion.pcb" cssClass="form-control"></s:textfield>
								</div>
							</div>
							<div class="form-group">
								 <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
								   	<button type="submit" id="submit"  class="btn btn-default"><s:text name='sys.query' /></button>
							    </div>
							     <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
								   	<button type="button" id="confirm"  class="btn btn-default"><s:text name='prob.info.confirm.check' /></button>
							    </div>
							     <div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
									<a class="btn btn-default"  href="javascirpt:void(0)" onclick="closeThis()"><s:text name='sys.back' /></a>
							    </div>
							</div>
						</div>
						
						<s:if test="%{softVersionList != null}">
						<div class="panel-body">
							<display:table name="softVersionList" pagesize="${softVersionList.size() }"
								export="false" size="${softVersionList.size() }" sort="external"
								requestURI="module/sub/toCheckSoftVersion.action"
								decorator="com.dp.plat.decorators.Wrapper"
								class="table table-striped" partialList="true" >
								
								<display:column property="softCheckBox" titleKey="prob.info.checkbox"></display:column>
								<s:if test="%{softVersion.cpld != null&&softVersion.cpld != ''}">
									<display:column property="cpld" titleKey="prob.info.cpld"></display:column>
								</s:if>
								<s:if test="%{softVersion.conp != null&&softVersion.conp != ''}">
									<display:column property="conp" titleKey="prob.info.conp"></display:column>
								</s:if>
								<s:if test="%{softVersion.boot != null&&softVersion.boot != ''}">
									<display:column property="boot" titleKey="prob.info.boot"></display:column>
								</s:if>
								<s:if test="%{softVersion.pcb != null&&softVersion.pcb != ''}">
									<display:column property="pcb" titleKey="prob.info.pcb"></display:column>
								</s:if>
							</display:table>
						</div>
						</s:if>
						</form>
					</div>
			</div>
		</div>
	</div>
</body>
</html>