<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<template v-if="navTabList.length > 0">
	<div :id="tabContainerId" class="tab-content box box-primary mt-1">
		<ul :id="tabWrapperId" class="nav nav-tabs">
			<li v-for="navTab in navTabList">
				<a :href="'#' + navTab.type + 'Tab' + timestamp" data-toggle="tab" class="tab-bg-primary" aria-expanded="true" @click.once="refreshNavTab($event, navTab)">{{navTab.title}}<span class="tab-operation fa fa-refresh ml-05" @click.stop="refreshNavTab($event, navTab)"></span></a>
			</li>
		</ul>
		<tab-pane :ref="'#' + navTab.type + 'Tab' + timestamp" v-for="navTab in navTabList" :nav-tab="navTab" :timestamp="timestamp" :target-value="targetValue" :permissions="permissions" :roles="roles" :model="model"></tab-pane>
		<%--
		<div class="tab-pane fade" v-for="navTab in navTabList" :id="navTab.type + 'Tab' + timestamp" 
			:data-url="navTab.url" :data-type="navTab.type" :data-title="navTab.title"
			:data-draw-type="navTab.drawType" :data-timestamp="timestamp" :data-table-config="JSON.stringify(navTab.tableConfig)"
			>
			<!--:data-url="parseUrl(navTab)" :data-url="navTab.url" :data-config="JSON.stringify(navTab)" -->
			
			<!-- <div class="box box-primary mb-0"> -->
				<div class="box-body">
					<div class="overlay"><i class="fa fa-refresh fa-spin"></i></div>
					<div :id="navTab.type + 'SearchDiv' + timestamp" v-if="(navTab.operations || []).length > 0" class="searchDiv">
						<div class="btn-group operate-btn-group">
	                         <button type="button" class="btn btn-default" v-for="btn in navTab.operations" v-if="navTab.permissionType && isPermit('btn', navTab, btn)" :data-btn-type="btn.id" @click="btn.events['click']($event, navTab)">{{btn.text}}</button>
	                     </div>
					</div>
				</div>
			<!-- </div> -->
		</div>
		 --%>
	</div>
</template>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/pm/js/vue-tab-pane-component.js"></script>
<script type="text/javascript">
	var tabVueConfig = {
		el: "#app",
		components: {
		    // <runoob> 将只在父模板可用
		    'tab-pane': TabPane
		},
		data: {
			timestamp: new Date().getTime(),
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
			} */],
			model: "",
			permissions: [],
			roles: []
		},
		created: function(e) {
			console.log(this._data);
			/* var fieldList = this.fieldList;
			for ( var i in fieldList) {
				var field = fieldList[i];
				if (field["extData"]) {
					this.parseValue(field, "extData");
				}
			} */
		},
		updated: function() {
			console.log("updated");
		},
		mounted: function() {
			console.log("mounted");
			if (this.navTabList.length > 0) {
				var e = document.createEvent("MouseEvents");
                e.initEvent("click", true, true);//这里的click可以换成你想触发的行为
				$('a[data-toggle="tab"]:first', $("#" + this.tabContainerId))[0].dispatchEvent(e);
				
				/* $('a[data-toggle="tab"]:first', $("#" + this.tabContainerId)).click();
				this.refreshNavTab({target: $('a[data-toggle="tab"]:first', $("#" + this.tabContainerId))[0]}); */
			}
		},
		computed: {
			tabContainerId: function() {
				return (this.tabContentId || "") + "_tab" + this.timestamp
			},
			tabWrapperId: function() {
				return (this.navTabWrapper || "") + "_wrapper" + this.timestamp
			},
			navTabList: function() {
				return this.navTabTransfer();
			}
		},
		methods: {
			navTabTransfer: function() {
				console.log("transfer");
				var tabList = this.tabList || [];
				var navTabList = [];
				for (var i = 0; i < tabList.length; i++) {
					var tab = tabList[i];
					var navTab = this.parseValue(tab, 'extData') || {};
					navTab.id = tab.id;
					navTab.url = this.parseUrl(navTab);
					navTab.type = navTab.type || tab.field;
					navTab.title = tab.title || tab.name;
					navTab.permissionType = tab.permissionType || "";
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
					navTab.paramsValue = navTab.paramsValue || {};
					navTab.paramsValue[param] = targetValue[param] || "";
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
	 		},
	 		refreshNavTab: function(e, navTab) {
	 			var tab = $(e.target);
	 			if ($(e.target).hasClass("tab-operation")) {
	 				tab = $(e.target).parent();
	 			}
	 			// 获取已激活的标签页的名称
	 			var activeTab = $(tab).text(); 
	 			var tabId = $(tab).attr("href");
	 			var $container = $(tab).parents(".tab-content:first");
	 			//if($(tabId, $container).hasClass("loaded") == '' && !$(tabId + " .overlay:first", $container).hasClass("loading")){
		 			this.$refs[tabId][0].refreshNavTab(e, navTab);
	 			//} else {
	 			//	try {
	 			//		$(tabId, $container).find(".dataTables_scrollBody table").dataTable().api().columns.adjust();
	 			//	} catch(e) {}
	 			//}
	 			
	 			/* console.log("refreshNavTab");
	 			var tab = $(e.target);
	 			if ($(e.target).hasClass("tab-operation")) {
	 				tab = $(e.target).parent();
	 			}
	 			// 获取已激活的标签页的名称
	 			var activeTab = $(tab).text(); 
	 			var tabId = $(tab).attr("href");
	 			var $container = $(tab).parents(".tab-content:first");
	 			if($(tabId, $container).hasClass("loaded") == '' && !$(tabId + " .overlay:first", $container).hasClass("loading")){
	 	            $(tabId + " .overlay", $container).addClass("loading");
	 	            var config = $(tabId, $container).data("config") || $(tabId, $container).data() || (navTab || {}).tableConfig;
	 	            config.container = $container;
	 				initTabData.call(this, config, false, navTab);
	 			} else {
	 				$(tabId + " .overlay", $container).addClass("loading");
	 	            var config = $(tabId, $container).data("config") || $(tabId, $container).data() || (navTab || {}).tableConfig;
	 	            config.container = $container;
	 				initTabData.call(this, config, true, navTab);
	 			} */
	 		}
		}
	};
</script>