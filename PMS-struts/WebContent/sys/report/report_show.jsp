<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<meta name="function" content="<s:text name='pm.data.analysis' />">
<!-- ECharts单文件引入 -->
<script src="js/echarts-all.js"></script>
<style type="text/css">
.panel-heading h3 a {
	color: blue;
}
</style>
<script type="text/javascript">
	$(function() {
/* 		$.ajaxSetup({
		  async: false
		  }); */
		// 基于准备好的dom，初始化echarts图表
		//项目經理跟踪率		
		var traceRate = echarts.init(document.getElementById('traceRate'));

		$.ajax({
			url : 'traceRate.action',
			type : 'post',
			dataType : 'json',
			data : {},
			beforeSend : function() {
				traceRate.showLoading({
					text : '正在努力的读取数据中...',
				});
			},
			success : function(result) {
				var data = eval('(' + result.data + ')') || {};

				// 	$("#gzlv").text(JSON.stringify(data));

				traceRate.setOption(data);
			},
			complete : function() {
				traceRate.hideLoading();
			}
		});
		//项目经理指派率
		var assignedRate = echarts
				.init(document.getElementById('assignedRate'));

		$.ajax({
			url : 'assignedRate.action',
			type : 'post',
			dataType : 'json',
			data : {},
			beforeSend : function() {
				assignedRate.showLoading({
					text : '正在努力的读取数据中...',
				});
			},
			success : function(result) {
				var data = eval('(' + result.data + ')') || {};

				// 	$("#gzlv").text(JSON.stringify(data));

				assignedRate.setOption(data);
			},
			complete : function() {
				assignedRate.hideLoading();
			}
		});

		//新增闭环比
		var closeRate = echarts.init(document.getElementById('closeRate'));
		$.ajax({
			url : 'closeRate.action',
			type : 'post',
			dataType : 'json',
			data : {},
			beforeSend : function() {
				closeRate.showLoading({
					text : '正在努力的读取数据中...',
				});
			},
			success : function(result) {
				var data = eval('(' + result.data + ')') || {};

				// 	$("#gzlv").text(JSON.stringify(data));

				closeRate.setOption(data);
			},
			complete : function() {
				closeRate.hideLoading();
			}
		});
		

		//质量管理
		var qualityScore = echarts
				.init(document.getElementById('qualityScore'));
		var qualitySize = echarts.init(document.getElementById('qualitySize'));

		$.ajax({
			url : 'quality.action',
			type : 'post',
			dataType : 'json',
			data : {},
			beforeSend : function() {
				qualityScore.showLoading({
					text : '正在努力的读取数据中...',
				});
				qualitySize.showLoading({
					text : '正在努力的读取数据中...',
				});
			},
			success : function(result) {
				var data = eval('(' + result.data + ')') || {};
				qualityScore.setOption(data);
				var datajson = eval('(' + result.dataJson + ')');
				qualitySize.setOption(datajson);
				var qualityTableHtml = result.qualityTableHtml;
				$("#qualityTable").html(qualityTableHtml);
			},
			complete : function() {
				qualityScore.hideLoading();
				qualitySize.hideLoading();
			}
		});
		
		//企业网各种实施方式占比
		var implRate = echarts.init(document.getElementById('implRate'));
		$.ajax({
			url : 'implRate.action',
			type : 'post',
			dataType : 'json',
			data : {},
			beforeSend : function() {
				implRate.showLoading({
					text : '正在努力的读取数据中...',
				});
			},
			success : function(result) {
				var data = eval('(' + result.data + ')') || {};

				// 	$("#gzlv").text(JSON.stringify(data));

				implRate.setOption(data);
			},
			complete : function() {
				implRate.hideLoading();
			}
		});
		$(".panel-title a").click(function() {
			if(!$(this).hasClass("noclick")){
				var className = this.className + "body";
				$(this).parent().parent().parent().children().each(function() {
					if (!$(this).hasClass("panel-heading")) {
						$(this).addClass("hideMark");
					}
					if ($(this).hasClass(className)) {
						$(this).removeClass("hideMark");
					}
				});
			}
		});
		
	});

	var defaultDataZoomOption = {
		grid: {
		    containLabel: true,
		    x: 80,    //left
		    y: 60,    //top
		    x2: 80,   //right
		    y2: 60,   //bottom
		    bottom: 60,
	    },
		dataZoom: {
            type: 'slider',
            show: true,
            top: "92%",
            realtime: false, //拖动滚动条时是否动态的更新图表数据
            //height: 30,//滚动条高度
            start: 70,//滚动条开始位置（共100等份）
            end:100//结束位置（共100等份）
        }
	};
	function fitDataZoom(data) {
		$.extend(true, data, defaultDataZoomOption);
		try {
    		var size = data.series[0].data.length;
    		var start = Math.max(100 - 30 / size * 100, 0);
    		data.dataZoom.start = start;
    		data.dataZoom.show = !!start;
    		// 预留出缩放框的高度
    		data.grid.y2 += (!!start ? ((data.dataZoom.height - 10) || 20) : 0);
		} catch(e) {}
		return data;
	}
	function officechange(linedivid, linedatatype, _this) {
		var charts = echarts.init(document.getElementById(linedivid));

		$.ajax({
			url : "loadLineData.action",
			type : 'post',
			dataType : 'json',
			data : {
				officeCode : _this.value == null ? 'total' : _this.value,
				dataTypeCode : linedatatype
			},
			beforeSend : function() {
				charts.showLoading({
					text : '正在努力的读取数据中...',
				});
			},
			success : function(result) {
				var data = eval('(' + result.data + ')') || {};
				fitDataZoom(data);
				charts.setOption(data);
			},
			complete : function() {
				charts.hideLoading();
			}
		});
	}
	
	function qualityofficechange( linedivid, linedatatype, _this){
		officechange(linedivid, linedatatype, _this);
		
		var charts = echarts.init(document.getElementById("qualitySizeLine"));
		$.ajax({
			url : "loadLine_qualityData.action",
			type : 'post',
			dataType : 'json',
			data : {
				officeCode : _this.value == null ? 'total' : _this.value,
				dataTypeCode : linedatatype
			},
			beforeSend : function() {
				charts.showLoading({
					text : '正在努力的读取数据中...',
				});
			},
			success : function(result) {
				var data = eval('(' + result.data + ')') || {};
				fitDataZoom(data);
				charts.setOption(data);
			},
			complete : function() {
				charts.hideLoading();
			}
		});
	}
	function implWayofficechange( lineDivId , linedatatype , _this ){
		var charts = echarts.init(document.getElementById(lineDivId));
		$.ajax({
			url : "loadLine_implData.action",
			type : 'post',
			dataType : 'json',
			data : {
				officeCode : _this.value == null ? 'total' : _this.value,
				dataTypeCode : linedatatype
			},
			beforeSend : function() {
				charts.showLoading({
					text : '正在努力的读取数据中...',
				});
			},
			success : function(result) {
				var data = eval('(' + result.data + ')') || {};
				fitDataZoom(data);
				charts.setOption(data);
			},
			complete : function() {
				charts.hideLoading();
			}
		});
	}
</script>
</head>
<body>
<nav class="navbar navbar-default" role="navigation" style="margin-top: 20px;">
	<div>
	    <ul class="nav navbar-nav">
	    	<s:iterator value="navTabList" var="nav" status="index">
				<s:if test="%{#index.index == 0 || navTabList.size() == 1}">
                    <li name="navli" class="active nav<s:property value='#index.index'/>" onclick="clickSwitchTab(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>','<s:property value='#nav.basicDataAttri1'/>')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
                </s:if>
                <s:else>
                    <li name="navli" class="nav<s:property value='#index.index'/>" onclick="clickSwitchTab(<s:property value='#index.index'/>,'<s:property value='#nav.basicDataId'/>','<s:property value='#nav.basicDataAttri1'/>')"><a href="javascript:void(0)"><s:property value='#nav.basicDataName'/></a></li>
                </s:else>
	    	</s:iterator>
		</ul>
	</div>
</nav>
<div class="navDiv 10">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">
				<a class="noclick" href="javascript:void(0)">全国项目统计综述</a>
			</h3>
		</div>
		<div class="panel-body">
			当前全国项目总数（已发货）:<s:property value="summarize.totalNum"/>个，其中工程类项目<s:property value="summarize.engineeringTypeNum"/>个、
			普通项目<s:property value="summarize.commonTypeNum"/>个；
			已指派项目经理项目<s:property value="summarize.assignedNum"/>个；在跟踪项目（项目经理开始跟踪项目）<s:property value="summarize.traceNum"/>个。</div>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">
				<a href="javascript:void(0)" class="bar"> 项目指派率 </a> 
				<a href="javascript:void(0)" class="table">数据透视表</a>
				<a href="javascript:void(0)" class="line"> 趋势图 </a>
			</h3>
		</div>
		<div class="panel-body barbody">
			<div id="assignedRate" style="height: 300px; width: 900px;"></div>
		</div>
		<div class="panel-body hideMark tablebody">
			<div style="height: 400px; width: 1100px;">
				<p class="redMark">条件值指办事处指定项目经理的项目数量，总值指办事处所有待跟踪状态的项目数量</p>
				<table class="table">
					${assignedTableHtml }
				</table>
			</div>
		</div>
		<div class="panel-body hideMark linebody">
			<div style="height: 25px;">
				请选择办事处：
				<s:select list="officeList" listValue="departmentName"
					listKey="departmentNum" headerKey="total" headerValue="-请选择-"
					onchange="officechange('assignedRateLine','assignedRate',this)"></s:select>
			</div>
			<div style="height: 275px; width: 900px;" id="assignedRateLine">

			</div>
		</div>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">
				<a href="javascript:void(0)" class="bar">项目经理跟踪率</a>
				<a href="javascript:void(0)" class="table">数据透视表</a>
				<a href="javascript:void(0)" class="line"> 趋势图 </a>
			</h3>
		</div>
		<div class="panel-body barbody">
			<div id="traceRate" style="height: 300px; width: 900px;"></div>
		</div>
		<div class="panel-body hideMark tablebody">
			<div style="height: 400px; width: 1100px;">
				<p class="redMark">条件值指办事处已制定工程计划的项目数量,总值指办事处所有指定项目经理的项目数量</p>
				<table class="table">
					${traceTableHtml }
				</table>
			</div>
		</div>
		<div class="panel-body hideMark linebody">
			<div style="height: 25px;">
				请选择办事处：
				<s:select list="officeList" listValue="departmentName"
					listKey="departmentNum" headerKey="total" headerValue="-请选择-"
					onchange="officechange('traceRateLine','traceRate',this)"></s:select>
			</div>
			<div style="height: 275px; width: 900px;" id="traceRateLine">

			</div>
		</div>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">
				<a href="javascript:void(0)" class="bar">闭环新增比</a>
				<a href="javascript:void(0)" class="table">数据透视表</a>
				<a href="javascript:void(0)" class="line"> 趋势图 </a>
			</h3>
		</div>
		<div class="panel-body barbody">
			<div id="closeRate" style="height: 300px; width: 900px;"></div>
		</div>
		<div class="panel-body hideMark tablebody">
			<!-- <p class="redMark">条件值指办事处当前季度不予跟踪或闭环的项目数量,总值指办事处当前季度新创建的项目数量</p> -->
			<p class="redMark">条件值指办事处当前季度闭环的项目数量,总值指办事处当前季度新创建的项目数量</p>
            <div style="height: 400px; width: 1100px;">
				<table class="table">
					${closeTableHtml}
				</table>
			</div>
		</div>
		<div class="panel-body hideMark linebody">
			<div style="height: 25px;">
				请选择办事处：
				<s:select list="officeList" listValue="departmentName"
					listKey="departmentNum" headerKey="total" headerValue="-请选择-"
					onchange="officechange('closeRateLine','closeRate',this)"></s:select>
			</div>
			<div style="height: 275px; width: 900px;" id="closeRateLine">

			</div>
		</div>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">
				<a href="javascript:void(0)" class="bar">企业网项目各类实施占比</a>
				<a href="javascript:void(0)" class="line"> 趋势图 </a>
			</h3>
		</div>
		<div class="panel-body barbody">
			<div id="implRate" style="height: 300px; width: 1000px;"></div>
		</div>
		<div class="panel-body hideMark linebody">
			<div style="height: 25px;">
				请选择办事处：
				<s:select list="officeList" listValue="departmentName"
					listKey="departmentNum" headerKey="total" headerValue="-请选择-"
					onchange="implWayofficechange('implRateLine','implRate',this)"></s:select>
			</div>
			<div style="height: 275px; width: 900px;" id="implRateLine">
				
			</div>
		</div>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">
				<a href="javascript:void(0)" class="bar">质量管理</a>
				<a href="javascript:void(0)" class="table">数据透视表</a>
				<a href="javascript:void(0)" class="line"> 趋势图 </a>
			</h3>
		</div>
		<div class="panel-body barbody">
			<div id="qualityScore" style="height: 300px; width: 900px;"></div>
			<div id="qualitySize" style="height: 300px; width: 900px;"></div>
		</div>
		<div class="panel-body hideMark tablebody">
			<div style=" width: 1100px;">
				<p class="redMark">此处统计时间指当前季度（非原厂服务不计入闭环平均分）</p>
				<table class="table" id="qualityTable">
					${qualityTableHtml }
				</table>
			</div>
		</div>
		<div class="panel-body hideMark linebody">
			<div style="height: 25px;">
				请选择办事处：
				<s:select list="officeList" listValue="departmentName"
					listKey="departmentNum" headerKey="total" headerValue="-请选择-"
					onchange="qualityofficechange('qualityRateLine','qualityRate',this)"></s:select>
			</div>
			<div style="height: 275px; width: 900px;" id="qualityRateLine">

			</div>
			<div style="height: 275px; width: 900px;" id="qualitySizeLine">
				
			</div>
		</div>
	</div>
	</div>
</body>
</html>