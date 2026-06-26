<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<html>
<dp:base />
<head>
</head>
<body>
    <style>
        .fixedHoverOperationTableFadeOut.notiframe, .notiframe .fixedHoverOperationTable {
            height: 200px;
        }
        
        .fixedHoverOperationTable.active {
            position: fixed;
            bottom: 50px;
            width: 523px;
            /* width: auto; */
            background: rgb(247 244 244);
            background: #fff;
            padding: 50px 5px;
            border-radius: 20px;
            box-shadow: 1px 1px 10px 5px rgba(0,0,0,0.1);
        }
        
        .fixedHoverOperationTable.active tr td {
            border-top: none!important;
        }
        
        .fixedHoverOperationTable tr td .fixedHoverOperationGroup {
            margin-bottom: 1rem;
            display: inline-block;
        }
        
        
        .fixedHoverOperationTable.active tr td .fixedHoverOperationGroup {
            margin-bottom: 1rem;
            display: inline-block;
            width: 100%;
        }
        
        .fixedHoverOperationTable tr td .fixedHoverOperationGroup #updateSoftVersion {
            display: none;
        }
        
        .fixedHoverOperationTable.active tr td .fixedHoverOperationGroup #updateSoftVersion {
            display: inline-block;
        }
    </style>
    <div style="text-align: right;">
    <c:if test="${currentDisplayUser.isHasAnyRole(1,11,12,13)}">
        <a onclick="softversion(this)" class="btn btn-default" href="javascript:void(0)">编辑</a>
        <a id="updateSoftVersion" class="btn btn-default" href="javascript:void(0)" onclick="updateSoftVersion()" style="display: none">保存</a>
    </c:if>
		<a onclick="checkhistsoftversion()" class="btn btn-default" href="javascript:void(0)">查看历史版本</a>
		<!-- <a onclick="checkShipmentInfo1()" class="btn btn-default" href="javascript:void(0)">查看发货序列号</a> -->
	</div>	
	<div id="softversionSee">
		<display:table name="softversionList" pagesize="${softversionList.size()}" export="true" id="softversionList"
			size="${softversionList.size()}" sort="external" requestURI="module/sub/checkSoftVersion.action" 
			decorator="com.dp.plat.decorators.Wrapper" class="displayTable table"
			partialList="true" >
			<display:column property="contractNo" titleKey="pm.shipment.contractNo"></display:column>
			<display:column property="barCodeRelation" titleKey="pm.shipment.barCode"></display:column>
			<display:column property="itemCodeRelation" titleKey="pm.shipment.itemCode"></display:column>
			<display:column property="itemNameRelation" titleKey="pm.shipment.itemName"></display:column>
			<display:column property="conp" titleKey="prob.info.conp"></display:column>	
			<display:column property="cpld" titleKey="prob.info.cpld"></display:column>	
			<display:column property="boot" titleKey="prob.info.boot"></display:column>	
			<display:column property="pcb" titleKey="prob.info.pcb"></display:column>
            <display:column title="受影响技术公告" media="html">
                <c:forEach items="${softversionList.affectedProbs}" var="prob">
                <div class="c affectedProbs">
                    <a href="module/prob_edit.action?prob.probId=${prob.probId}" target="_blank" title="${prob.theme}">${prob.probNum}</a>
                </div>
                </c:forEach>
            </display:column>
            <display:column title="受影响技术公告" media="excel" >
<%-- 为了导出内容能够换行，请保持</c:forEach>前没有空格，不要格式化对齐 --%>
<c:forEach items="${softversionList.affectedProbs}" var="prob">${prob.probNum}：${prob.theme}
</c:forEach>
            </display:column>
            <display:setProperty name="export.excel.filename" value="${project.projectCode}-${project.projectName}-版本信息.xls" />
		</display:table>
	</div>
	<div id="softversionEdit" style="display: none">
		<s:form id="softversionForm" >
         <div class="fixedHoverOperationTableFadeOut">
             <table class="table fixedHoverOperationTable">
                <tr>
                    <!-- 更新说明 -->
                    <td><s:text name="prob.info.change.remark"></s:text></td>
                    <td colspan="3">
                        <s:hidden name="softChangeLog.projectId" value="%{project.projectId}"></s:hidden>
                        <s:textarea rows="5" name="softChangeLog.changeRemark" cssClass="form-control" ></s:textarea>
                    </td>
                    <td colspan="4"><span id = "changeRemarkMsg"></span></td>
                </tr>
                <tr>
                    <!-- 简化更新方式 -->
                    <td>批量更新</td>
                    <td colspan="4">
                        <span class="fixedHoverOperationGroup">
                            <input style="width: 120px;display: inline;" type="text" id="softItemCode" class="form-control" placeholder='产品编码（选填）'/>
                            <select style="width: 130px;display: inline;" class="form-control" id="softType">
                                <option value="conp">软件版本号</option>
                                <option value="cpld">CPLD版本号</option>
                                <option value="boot">ConBoot版本号</option>
                                <option value="pcb">PCB版本号</option>
                            </select>
                        </span>
                        <span class="fixedHoverOperationGroup">
                            <input style="width: 200px;display: inline;" type="text" id="oldSoftVersion" class="form-control" placeholder="指定旧版本，*匹配全部"/>
                            <span style="display: inline;">----></span>
                            <input style="width: 200px;display: inline;" type="text" id="newSoftVersion" class="form-control"/>
                        </span>
                        <span class="fixedHoverOperationGroup">
                            <a href="javascript:void(0)" class="btn btn-default"  onclick="bacthUpdate(this)">批量操作</a>
                            <a href="javascript:void(0)" class="btn btn-default"  onclick="bacthReset(this)" style="margin-left:1rem">重置</a>
                        <c:if test="${currentDisplayUser.isHasAnyRole(1,11,12,13)}">
                            <a id="updateSoftVersion" class="btn btn-default" href="javascript:void(0)" onclick="updateSoftVersion()" style="float: right;margin-right: 1rem;">保存</a>
                        </c:if>
                        </span>
                    </td>
                    <td colspan="4"></td>
                </tr>
            </table>
        </div>
		<table id="softVersionEditTable" class="table localSearchTable">
            <thead class="position-sticky">
                <tr>
                    <td colspan="9" class="localSearchTable_searchDivWrapper"></td>
                </tr>
    			<tr>
    				<th><s:text name="pm.shipment.contractNo"></s:text></th>
    				<th><s:text name="pm.shipment.barCode"></s:text></th>
    			<%-- 	<th><s:text name="pm.shipment.itemCode"></s:text></th> --%>
                    <th class="hidden"><s:text name="pm.shipment.itemCode"></s:text></th>
    				<th><s:text name="pm.shipment.itemName"></s:text></th>
    				<th><s:text name="prob.info.conp"></s:text></th>
    				<th width="100px"><s:text name="prob.info.cpld"></s:text></th>
    				<th width="100px"><s:text name="prob.info.boot"></s:text></th>
    				<th width="100px"><s:text name="prob.info.pcb"></s:text></th>
    				<th width="125px"><s:textfield placeholder="执行更新时间" title="软件版本升级执行时间，若默认今天，可不填" name="executeTimeHeader" cssClass="form-control softUpdateExecuteTime"/></th>
    			</tr>
            </thead>
			<s:iterator value="softversionList" var="bar" status="index">
				<tr class="softversion_itemCode itemCode_<s:property value="itemCode"/> search_line search_line_<s:property value="barCode"/> ">
					<td class="search_condition search_contractNo" data-name="contractNo">
						<s:property value="contractNo"/> 
						<s:hidden name="softversionList[%{#index.index}].projectId" value="%{project.projectId}"></s:hidden>
                        <s:hidden name="softversionList[%{#index.index}].contractNo" value="%{project.contractNo}"></s:hidden>
					</td>
					<td class="search_condition search_barCode" data-name="barCode"><s:property value="barCode"/> 
                        <s:if test="#bar.barCode2 != null">
                            <br><span class='text-danger'>(<s:property value="#bar.barCode2"/>)</span>
                        </s:if>
						<s:hidden name="softversionList[%{#index.index}].barCode" value="%{#bar.barCode}"></s:hidden>
					</td>
					<%-- <td><s:property value="itemCode"/> </td> --%>
                    <td class="hidden search_condition search_itemName" data-name="itemName"><s:property value="itemCode"/> </td>
					<td class="search_condition search_itemName" data-name="itemName"><s:property value="itemName"/> </td>
					<td>
						<s:textfield name="softversionList[%{#index.index}].conp"  bakValue="%{#bar.conp}"  value="%{#bar.conp}" cssClass="form-control software clearconp"/> 
						<s:hidden name="softversionList[%{#index.index}].conpChange" value="0" cssClass="softChangeFlag"></s:hidden>
						<s:if test="%{#bar.conpChange == 1}">
							<s:hidden name="softversionList[%{#index.index}].conpBak" value="%{#bar.conpBak}->%{#bar.conp}"></s:hidden>
						</s:if>
						<s:else>
							<s:hidden name="softversionList[%{#index.index}].conpBak" value="%{#bar.conp}"></s:hidden>
						</s:else>
					</td>
					<td>
						<s:textfield name="softversionList[%{#index.index}].cpld" bakValue="%{#bar.cpld}" value="%{#bar.cpld}" cssClass="form-control software"/>  
						<s:hidden name="softversionList[%{#index.index}].cpldChange" value="0" cssClass="softChangeFlag"></s:hidden>
						<s:if test="%{#bar.cpldChange == 1}">
							<s:hidden name="softversionList[%{#index.index}].cpldBak" value="%{#bar.cpldBak}->%{#bar.cpld}"></s:hidden>
						</s:if>
						<s:else>
							<s:hidden name="softversionList[%{#index.index}].cpldBak" value="%{#bar.cpld}"></s:hidden>
						</s:else>
					</td>
					<td>
						<s:textfield name="softversionList[%{#index.index}].boot" bakValue="%{#bar.boot}" value="%{#bar.boot}" cssClass="form-control software"/>  
						<s:hidden name="softversionList[%{#index.index}].bootChange" value="0" cssClass="softChangeFlag"></s:hidden>
						<s:if test="%{#bar.bootChange == 1}">
							<s:hidden name="softversionList[%{#index.index}].bootBak" value="%{#bar.bootBak}->%{#bar.boot}"></s:hidden>
						</s:if>
						<s:else>
							<s:hidden name="softversionList[%{#index.index}].bootBak" value="%{#bar.boot}"></s:hidden>
						</s:else>
					</td>
					<td>
						<s:textfield name="softversionList[%{#index.index}].pcb" bakValue="%{#bar.pcb}" value="%{#bar.pcb}" cssClass="form-control software"/>  
						<s:hidden name="softversionList[%{#index.index}].pcbChange" value="0" cssClass="softChangeFlag"></s:hidden>
						<s:if test="%{#bar.pcbChange == 1}">
							<s:hidden name="softversionList[%{#index.index}].pcbBak" value="%{#bar.pcbBak}->%{#bar.pcb}"></s:hidden>
						</s:if>
						<s:else>
							<s:hidden name="softversionList[%{#index.index}].pcbBak" value="%{#bar.pcb}"></s:hidden>
						</s:else>
					</td>
					<td><s:textfield name="softversionList[%{#index.index}].executeTime" cssClass="form-control softUpdateExecuteTime"/></td>
				</tr>
			</s:iterator>
		</table>
        <%-- <table class="table fixedHoverOperationTable">
            <tr>
                <!-- 更新说明 -->
                <td><s:text name="prob.info.change.remark"></s:text></td>
                <td colspan="3">
                    <s:hidden name="softChangeLog.projectId" value="%{project.projectId}"></s:hidden>
                    <s:textarea rows="5" name="softChangeLog.changeRemark" cssClass="form-control" ></s:textarea>
                </td>
                <td colspan="4"><span id = "changeRemarkMsg"></span></td>
            </tr>
            <tr>
                <!-- 简化更新方式 -->
                <td>批量更新</td>
                <td colspan="4">
                    <span class="fixedHoverOperationGroup">
                        <input style="width: 49%;display: inline;" type="text" id="softItemCode" class="form-control" placeholder='产品编码（选填）'/>
                        <select style="width: 49%;display: inline;" class="form-control" id="softType">
                            <option value="conp">软件版本号</option>
                            <option value="cpld">CPLD版本号</option>
                            <option value="boot">ConBoot版本号</option>
                            <option value="pcb">PCB版本号</option>
                        </select>
                    </span>
                    <span class="fixedHoverOperationGroup">
                        <input style="width: 100%;display: inline;" type="text" id="oldSoftVersion" class="form-control"/>
                        <span style="display: inline;">----></span>
                        <input style="width: 100%;display: inline;" type="text" id="newSoftVersion" class="form-control"/>
                    </span>
                    <span class="fixedHoverOperationGroup">
                        <a href="javascript:void(0)" onclick="bacthUpdate(this)">批量操作</a>
                        <a href="javascript:void(0)" onclick="bacthReset(this)" style="margin-left:1rem">重置</a>
                    </span>
                </td>
                <td colspan="4"></td>
            </tr>
        </table> --%>
		</s:form>
	</div>
	<div class="backTop">
	    <i class='glyphicon glyphicon-arrow-up'></i>
	</div>
	<div class="rollBottom">
        <i class='glyphicon glyphicon-arrow-down'></i>
    </div>
    <dp:script type="text/javascript" src="js/prob/renderCascade.js"></dp:script>
    <dp:script type="text/javascript" src="js/prob/render.js"></dp:script>
    <script type="text/javascript">
        var isIframe = window.parent != window.self;
        $(()=>{
        	alterSoftWares();
        	
        	if (isIframe) {
        		$(".fixedHoverOperationTable").addClass("active");
        	} else {
        		$(".fixedHoverOperationTableFadeOut").addClass("notiframe");
        	}
        });
        function softversion(_this){
            if($("#softversionSee").is(":visible")){
                $(_this).text('取消');
                $("#softversionSee").hide();
                $("#softversionEdit").show();
                $("#updateSoftVersion").show();
            }else{
                $(_this).text('编辑');
                $("#softversionSee").show();
                $("#softversionEdit").hide();
                $("#updateSoftVersion").hide();
            }
            if (!isIframe) {
                $(".fixedHoverOperationTable.active").removeClass("active");
            }
            initLocalSearchTable($("#softVersionEditTable"), {cols: 4, $wrapper:$("#softVersionEditTable .localSearchTable_searchDivWrapper")});
        }
        var loadingHtml = "<div style='height:180px;display:-webkit-flex;display: flex;justify-content:center;align-items:center;'>" + 
            "   <img src='./images/loading-circle.gif'/>" + 
            "</div>";
        // 全局缓存和等待队列
        const requestCheckSoftVersionCache = {};
        const requestCheckSoftVersionQueue = {}; // 用于存储相同请求的多个回调
        function checkSoftVersion(softVersion, callback) {
        	softVersion = softVersion || {};
        	var callbackTrigger = softVersion;
            
            // 生成唯一缓存键（基于参数）
            var cacheKey = JSON.stringify($.extend({}, softVersion));

            // 检查是否已有缓存结果
            if (requestCheckSoftVersionCache[cacheKey] !== undefined) {
                // 缓存命中，直接异步回调
                setTimeout(function () {
                    callback.call(callbackTrigger, requestCheckSoftVersionCache[cacheKey]);
                }, 0);
                return;
            }

            // 检查是否已有相同请求正在执行（即已在队列中）
            if (requestCheckSoftVersionQueue[cacheKey]) {
                // 已有请求在进行中，将当前回调加入队列
                requestCheckSoftVersionQueue[cacheKey].push(callback);
                return;
            }

            // 首次请求，初始化队列并发起 AJAX
            requestCheckSoftVersionQueue[cacheKey] = [callback];
            
            // 显示加载中提示
            //var layerIndex = layer.load(1);
            $.ajax({
                url:"sys/probAjax_parserSoftVersion.action",
                type:"post",
                dataType:"json",
                async : true,
                data: flattenObject({
                    softVersion: softVersion
                }),
                success:function(data){
                    var affectedType = softVersion.affectedType;
                    var versionMap = JSON.parse(data.result);
                    console.log(versionMap);
                    
                    // 保存缓存
                    requestCheckSoftVersionCache[cacheKey] = versionMap;

                    // 遍历队列，执行所有等待的回调
                    var callbacks = requestCheckSoftVersionQueue[cacheKey];
                    if (callbacks && callbacks.length > 0) {
                        callbacks.forEach(function (cb) {
                            if (typeof cb === 'function') {
                                cb.call(callbackTrigger, versionMap);
                            }
                        });
                    }
                },
                complete: function(xhr, status) {
                    // 清理队列（无论成功失败都清理）
                    //layer.close(layerIndex);
                    delete requestCheckSoftVersionQueue[cacheKey];
                }
            });
        }
        function updateSoftVersion(){
            $changeRemark = $("textarea[name='softChangeLog.changeRemark']");
            $(".software").each(function(){
                $(this).val($(this).val().trim());
            });
            // 去除.bin后缀，软件版本只保留不带设备类型部分信息
            var checkConp = true;
            var errorAlertClass = "alert-danger";
            var successAlertClass = "alert-success";
            var checkCount = $(".clearconp").length;
            var checkedCount = 0;
            var errorCount = 0;
            $(".clearconp").removeClass(errorAlertClass).each(function(){
            	var $conp = $(this);
                var conp = $conp.val().trim();
                var temp = conp;
                temp = temp.replace(".bin", "");
                temp = temp.split("-");
                if (temp.length >= 1) {
                    temp = temp[temp.length - 1];
                }
                temp = $.trim(temp);
                $conp.val(temp);
                
                // 20251222去掉软件版本校验，强制true
                var isChecked = $conp.hasClass(successAlertClass) && temp == ($conp.data("checkedConp") || "") || true;
                if (temp != '' && (conp != temp || !isChecked)) {
                	conp = temp;
                	checkConp = false;
                	checkSoftVersion({manualEntry:conp}, function(versionMap) {
                		checkedCount++;
                		var checkResult = checkSoftVersionRanges(this, versionMap);
                        if (checkResult.status == false) {
                            $conp.removeClass(successAlertClass).addClass(errorAlertClass);
                            errorCount++;
                        } else {
                            $conp.removeClass(errorAlertClass).addClass(successAlertClass);
                            // 缓存校验后的值
                            $conp.data("checkedConp", conp);
                        }
                		if (checkCount == checkedCount && errorCount == 0) {
                            updateSoftVersion();
                        } else if (checkCount == checkedCount) {
                            alert("输入的版本号格式有误，请检查确认后再保存!");
                        }
                		
                		if (true) {
                			return;
                		}
                		
                		var entryMap = versionMap[conp] || {};
                		var vserionKeys = Object.keys(entryMap);
                		console.log(vserionKeys);
                		
                		var mergerdVersionParts = {};
                		var partCounts = {};
                		var originVersionParts = [];
                		var isSeriesValid = true; 
                		for (var softKey in entryMap) {
                			var entrySofts = entryMap[softKey] || [];
                            var soft = entrySofts[0] || {};
                           
                            // 软件系列信息是否完整
                            var seriesParts = soft.seriesParts || {};
                            var seriesPartKeys = Object.keys(seriesParts);
                            if (seriesPartKeys.length < 2) {
                                isSeriesValid = false;
                                break;
                            }
                            
                            // 软件版本信息处理
                            var versionParts = soft.versionParts || {};
                            var versionPartsKeys = Object.keys(soft.markAllParts || versionParts);
                            for (const partKey of versionPartsKeys) {
                            	var part = $.trim(versionParts[partKey]);
                            	// 初始化
                            	partCounts[partKey] = (partCounts[partKey] || 0);
                            	mergerdVersionParts[partKey] = mergerdVersionParts[partKey] || [];
                            	// 如果有值进行赋值
                            	if (part != '') {
                                	partCounts[partKey] = (partCounts[partKey] || 0) + 1;
                                	mergerdVersionParts[partKey].push(part);
                                	originVersionParts.push(part);
                            	}
                            }
                		}
                		
                		
                		// 检查各软件版本部分是否有重复，特殊部位允许重复
                		var ignorePartKeys = ['PATCHxx', 'LATCHxx'];
                		var isPartRepeat = false;
                		for (var partKey in partCounts) {
							if ($.inArray(partKey, ignorePartKeys) == -1 && partCounts[partKey] > 1) {
                                isPartRepeat = true;
                                break;
                            }
						}
                		
                		// 如果软件系列完整、软件版本重复则添加错误计数
                		if (!isSeriesValid || isPartRepeat) {
                            $conp.removeClass(successAlertClass).addClass(errorAlertClass);
                            errorCount++;
                        } else {
                            // 将检查后的各部分进行重新拼合，判断是否与输入版本保持一致
                            let versionJoinParts = [];
                            let orderedVersionParts = [].concat(originVersionParts);
                            for (let partKey in mergerdVersionParts) {
                            	// 判断是否是非重复字段，如果不重复则从order中取值
                            	if ($.inArray(partKey, ignorePartKeys) == -1) {
                                    let partValues = mergerdVersionParts[partKey] || [];
                                    versionJoinParts = versionJoinParts.concat(partValues);
                                    // 将已拼接的字段从原始顺序值中移除
                                    const index = orderedVersionParts.indexOf(partValues[0]);
                                    if (index !== -1) {
                                    	orderedVersionParts.splice(index, 1); // 原地修改数组
                                    }
                            	} else {
                            		// 如果是重复字段，为了保证维持原始顺序后续直接用原始顺序链接，因为'PATCHxx', 'LATCHxx'在拆分成多段之后后面的Patch会提前
                                    // 例如PATHC01PATHC02PATHC03，会被解析成2段，第一段PATCH01LATCH02，第二段PATCH03.在解析后会认为PATCH重复导致最终PATCH01PATCH03LATCH02
                            		break;
                            	}
                            }
                            
                            // 将剩余部分全部加入
                            versionJoinParts = versionJoinParts.concat(orderedVersionParts);
                            var versionJoin = versionJoinParts.join("");
                            if (versionJoin != conp) {
                                $conp.removeClass(successAlertClass).addClass(errorAlertClass);
                                errorCount++;
                            } else {
                                $conp.removeClass(errorAlertClass).addClass(successAlertClass);
                                // 缓存校验后的值
                                $conp.data("checkedConp", conp);
                            }
                        }
                		
                		
                		/* for (var softKey in entryMap) {
                			if (conp.startsWith(softKey)) {
                				var entrySofts = entryMap[softKey] || [];
                				var soft = entrySofts[0] || {};
                				var seriesParts = soft.seriesParts || {};
                				var seriesPartKeys = Object.keys(seriesParts);
                				if (seriesPartKeys.length < 2) {
                                    $conp.removeClass(successAlertClass).addClass(errorAlertClass);
                                    errorCount++;
                                } else {
                                	$conp.removeClass(errorAlertClass).addClass(successAlertClass);
                                }
                			}
                		} */
                		
                		if (checkCount == checkedCount && errorCount == 0) {
                			updateSoftVersion();
                		} else if (checkCount == checkedCount) {
                			alert("输入的版本号格式有误，请检查确认后再保存!");
                		}
                	})
                } else {
                	checkedCount++;
                }
            });
            
            if (!checkConp) {
            	return false;
            }
            
            if($changeRemark.val().trim() == ''){
                $("#changeRemarkMsg").text("请填写更新说明").addClass("redMark");
                $changeRemark.focus();
                $changeRemark.blur(function(){
                    if($changeRemark.val().trim() != ''){
                        $("#changeRemarkMsg").text("").removeClass("redMark");
                    }
                });
                return false;
            }
            $changeRemark.val($changeRemark.val().replace(/\n/g,"<br>"));
            
            // 检查是否有变更的内容，如果没有进行提示
            var $softChangeFlag = $(".softChangeFlag").filter(function() {
                return this.value == '1'
            })
            if ($softChangeFlag.length == 0) {
            	alert("没有待保存的版本信息，请更新版本信息后再提交！");
            	return false;
            }
            
            // 解析成json格式
            var assembly = function(res, name, val) {
                res = res || {};
                var sind = name.indexOf('.');
                var sp_name = sind > -1 ? name.substring(0, sind) : name;
                var sc_name = sind > -1 ? name.substring(sind + 1) : "";
                var listMatchs = sp_name.match(/(\w+)\[(\d+)\]/) || [];
                var spl_name = listMatchs[1];
                var spl_index = listMatchs[2];
                // 是否是数组
                if (spl_name) {
                    res[spl_name] = res[spl_name] || [];
                    res[spl_name][spl_index] = sind > -1 ? assembly(res[spl_name][spl_index], sc_name, val) : val;
                } else {
                    res[sp_name] = sind > -1 ? assembly(res[sp_name], sc_name, val) : val;
                }
                return res;
            };
            // 封装成JSON,避免list数量多时会导致超过请求参数个数限制的问题
            var softVersionJson = {};
            var barCodes = [];
            $.each($("#softversionForm").serializeArray(), function() {
                var entity = $(this)[0];
                //console.log(entity);
                var key = entity.name;
                var value = entity.value;
                /* // 过滤参数为空的情况
                if (value == '') {
                    return;
                } */
                if (key.indexOf("barCode") > -1 && $.inArray(value, barCodes) > -1) {
                    return;
                }
                softVersionJson = assembly(softVersionJson, key, value);
            })
            softVersionJson = JSON.stringify(softVersionJson);
            $.ajax({
                url :"updateSoftVersion.action",
                type :"post",
                dataType :"json",
                //data : $("#softversionForm").serialize(),
                data : {softVersionJson: softVersionJson},
                success:function(data){
                    var result = data.result;
                    if(result == 310){
                        alert('更新成功');
                        $(".softversionDiv").html(loadingHtml);
                        
                        // 回调上级页面的方法
                        if (window.parent.updateSoftVersionCallback) {
                            window.parent.updateSoftVersionCallback(data);
                        }
                        if (typeof checkSoftVesion == 'function') {
                            checkSoftVesion();
                        } else {
                        	window.location.reload()
                        }
                        //window.location.href="module/ProjectModify.action?project.paramId="+$("#paramId").val()+"&result="+result;
                    }else{
                        alert('更新失败');
                    }
                }
            });
        }
        //检索软件版本变更历史版本
        function checkhistsoftversion(){
            projectId = $("#projectId").val() || $("#projectId", parent.document).val() || "${project.projectId}";
            popWindow('module/sub/checkhistsoftversion.action?softChangeLog.projectId='+projectId+'&redirect='+window.location.href, 1100, 650,'查询历史软件版本', 'BudgetUpload', true);
            return false;
        }
        function checkShipmentInfo1(){
            var projectId = $("#projectId").val() || $("#projectId", parent.document).val() || "${project.projectId}";
            var contractNo = $("#contractNo").val() || $("#contractNo", parent.document).val() || "${project.contractNo}";
            var projectState = "${project.projectState}";
            var officeCode = "${project.column001}";
            popWindow('module/sub/checkShipmentInfo.action?project.projectId='+projectId+'&project.contractNo='+contractNo+'&project.projectState='+projectState+'&project.column001='+officeCode+'&redirect='+window.location.href, 1100, 650,'发货清单', 'BudgetUpload', true);
            return false;
        }
        
        //渲染软件版本更新表单
        function alterSoftWares(){
            $(".softUpdateExecuteTime").each(function(){
                date_picker(this.id);
            });
            
            $("input[name='executeTimeHeader']").change(function(){
                time = this.value;
                $(".softUpdateExecuteTime").each(function(){
                    $(this).val(time);
                });
            });
            
            $(".software").each(function(){
                $(this).blur(function(){
                    bakvalue = $(this).attr("bakValue").trim();
                    if(bakvalue !=  $(this).val().trim()){
                        $("#"+this.id+"Change").val(1);
                    }else{
                        $("#"+this.id+"Change").val(0);
                    }
                });
            });
        }
        
        function bacthUpdate(_this){
            var $container = $(_this).parents(".table:first");
            var softItemCode = $("#softItemCode", $container).val().trim();
            softType = $("#softType", $container).val();
            oldSoftVersion = $("#oldSoftVersion", $container).val().trim();
            newSoftVersion = $("#newSoftVersion", $container).val().trim();
            newSoftVersion = newSoftVersion.replace(".bin", "");
            /* // 去除版本型号
            var temp = newSoftVersion.split("-");
            if (temp.length > 1) {
                newSoftVersion = temp[temp.length - 1];
            } */
            if(newSoftVersion!= '' &&  oldSoftVersion != newSoftVersion){
                var $softs = $("input[name$='." + softType + "'].software", softItemCode ? (".itemCode_" + softItemCode) : undefined).filter(":visible");
                $softs.each(function() {
                    var soft = $(this);
                    if(soft.val() == oldSoftVersion || oldSoftVersion == "*"){
                        soft.val(newSoftVersion);
                        bakvalue = soft.attr("bakValue").trim();
                        if(bakvalue !=  newSoftVersion){
                            $("#"+soft[0].id+"Change").val(1);
                        }else{
                            $("#"+soft[0].id+"Change").val(0);
                        }
                    }
                })
                /* size = $("#softversionEdit table tr").size() - 3;
                for(i = 0; i < size ; i ++){
                    if (softItemCode) {
                        soft = $(".itemCode_" + softItemCode + " input[name='softversionList["+i+"]."+softType+"']");
                    } else {
                        soft = $("input[name='softversionList["+i+"]."+softType+"']");
                    }
                    if(soft.val() == oldSoftVersion){
                        soft.val(newSoftVersion);
                        bakvalue = soft.attr("bakValue").trim();
                        if(bakvalue !=  newSoftVersion){
                            $("#"+soft[0].id+"Change").val(1);
                        }else{
                            $("#"+soft[0].id+"Change").val(0);
                        }
                    }
                }        */
            }
        }
        
        function bacthReset(){
            $(".software").each(function() {
                var balvalue = $(this).attr("bakvalue").trim() || "";
                $(this).val(balvalue);
            });
            $(".softChangeFlag").val(0);
        }
    </script>
</body>
</html>