<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<template v-if="navTabList.length > 0">
	<div :id="tabContentId" class="tab-content box box-primary mt-1">
		<ul :id="navTabWrapper" class="nav nav-tabs">
			<li v-for="navTab in navTabList"><a :href="'#' + navTab.type + 'Tab'" data-toggle="tab" class="tab-bg-primary" aria-expanded="true">{{navTab.title}}</a></li>
		</ul>
		<div class="tab-pane fade" v-for="navTab in navTabList" :id="navTab.type + 'Tab'" 
			:data-url="parseUrl(navTab)" :data-type="navTab.type" :data-title="navTab.title"
			:data-draw-type="navTab.drawType"
			>
			<!--:data-url="navTab.url" :data-config="JSON.stringify(navTab)" -->
			
			<!-- <div class="box box-primary mb-0"> -->
				<div class="box-body">
					<div class="overlay"><i class="fa fa-refresh fa-spin"></i></div>
					<div :id="navTab.type + 'SearchDiv'" v-if="(navTab.operations || []).length > 0" class="searchDiv">
						<div class="btn-group operate-btn-group">
	                         <button type="button" class="btn btn-default" v-for="btn in navTab.operations" :data-btn-type="btn.id" @click="btn.events['click']">{{btn.text}}</button>
	                     </div>
					</div>
				</div>
			<!-- </div> -->
		</div>
	</div>
</template>
<script type="text/javascript">
	var tabVueConfig = {
		el: "#app",
		data: {
			tabContentId: "",
			navTabWrapper: "",
			tabList: [/* {
				url: "pm.project.api.orderDetail(projectIds, projectType, contractNo)",
				params: ['projectIds', 'contractNo', 'projectType'],
				type: "task",
				title: "任务列表",
				operations:[{
					id: 'add',
					text: '新增',
					events: {
						click: function(e) {
							console.log(e);
						}
					}
				}]
			},{
				url: "/PMS/module/sub/querySubcontractPayment.action",
				drawType: "html",
				params: ['projectIds', 'contractNo', 'projectType'],
				type: "paymentInfo",
				title: "付款信息",
				operations:[]
			} */]
		},
		/* created: function(e) {
			var fieldList = this.fieldList;
			for ( var i in fieldList) {
				var field = fieldList[i];
				if (field["extData"]) {
					this.parseValue(field, "extData");
				}
			}
		}, */
		updated: function() {
			console.log("updated");
		},
		mounted: function() {
			if (this.navTabList.length > 0) {
				$('a[data-toggle="tab"]:first').click();
			}
		},
		computed: {
			navTabList: function() {
				return this.navTabTransfer();
			}
		},
		methods: {
			navTabTransfer: function() {
				var tabList = this.tabList || [];
				var navTabList = [];
				for (var i = 0; i < tabList.length; i++) {
					var tab = tabList[i];
					var navTab = this.parseValue(tab, 'extData') || {};
					navTab.type = tab.field;
					navTab.title = tab.title || tab.name;
					navTabList.push(navTab);
				}
				return navTabList;
			},
			parseUrl: function(navTab) {
				var targetValue = this.targetValue || {};
				var params = navTab.params || [];
				for (var i = 0; i < params.length; i++) {
					var param = params[i];
					eval("var " + param + " = '" + (targetValue[param] || "") + "';");
				}
				var url = navTab.url;
				try {
					url = eval(url);
				} catch(e){}
				return url;
			},
	 		getDataValue: function(key) {
	 			var value;
	 			try {
	 				value = eval("this." + key);
	 			} catch(e) {
	 			}
 				try {
	 				value = value || JSON.parse(key);
 				} catch(e) {
 				}
	 			try {
	 				value = value || eval(key);
 				} catch(e) {
 					value = key;
 				}
	 			return value;
	 		},
	 		parseValue: function(field, key) {
	 			try {
	 				field[key] = this.getDataValue(field[key]);
	 			} catch(e){}
	 			return field[key];
	 		}
		}
	};
</script>