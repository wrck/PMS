var TabPane = {
		name: "tabPane",
		data: function() {
			return {};	
		},
		template: '<h1>自定义组件!</h1>',
//		template: `<div class="tab-pane fade" :id="navTab.type + 'Tab' + timestamp" 
//			:data-url="navTab.url" :data-type="navTab.type" :data-title="navTab.title"
//			:data-draw-type="navTab.drawType" :data-timestamp="timestamp" :data-table-config="JSON.stringify(navTab.tableConfig)"
//			>
//			<!--:data-url="parseUrl(navTab)" :data-url="navTab.url" :data-config="JSON.stringify(navTab)" -->
//			
//			<!-- <div class="box box-primary mb-0"> -->
//				<div class="box-body">
//					<div class="overlay"><i class="fa fa-refresh fa-spin"></i></div>
//					<div :id="navTab.type + 'SearchDiv' + timestamp" v-if="(navTab.operations || []).length > 0" class="searchDiv">
//						<div class="btn-group operate-btn-group">
//	                         <button type="button" class="btn btn-default" v-for="btn in navTab.operations" v-if="navTab.permissionType && isPermit('btn', navTab, btn)" :data-btn-type="btn.id" @click="btn.events['click']($event, navTab)">{{btn.text}}</button>
//	                     </div>
//					</div>
//				</div>
//			<!-- </div> -->
//		</div>`,
		props: {
			cssId: {
			    type: String
		    },
		    navTab: {
			    type: Object,
			    required: true
		    },
//			url: {
//			    type: String,
//			    required: true
//		    },
//			params: Array,
//			type: {
//			    type: String,
//			    required: true
//		    },
//			title: {
//			    type: String,
//			    required: true
//		    },
//			operations: Array,
//		    timestamp: Number,
//			drawType: String,
//			tableConfig: Object,
			targetValue: {
				type: Object
			},
			model: {
				type: String,
				default: ""
			},
			permissionType: {
				type: String,
				default: ""
			},
			permissions: {
				type: Array,
				default: []
			},
			roles: {
				type: Array,
				default: []
			}
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
			console.log("mounted");
		},
		/* computed: {
			tabContainerId: function() {
				return (this.tabContentId || "") + "_tab" + this.timestamp
			},
			tabWrapperId: function() {
				return (this.navTabWrapper || "") + "_wrapper" + this.timestamp
			},
			navTabList: function() {
				return this.navTabTransfer();
			}
		}, */
		methods: {
			/* navTabTransfer: function() {
				console.log("transfer");
				var tabList = this.tabList || [];
				var navTabList = [];
				for (var i = 0; i < tabList.length; i++) {
					var tab = tabList[i];
					var navTab = this.parseValue(tab, 'extData') || {};
					this.id = tab.id;
					this.url = this.parseUrl(navTab);
					this.type = this.type || tab.field;
					this.title = tab.title || tab.name;
					this.permissionType = tab.permissionType || "";
					navTabList.push(navTab);
				}
				return navTabList;
			}, */
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
	 			console.log("refreshNavTab");
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
	 			}
	 		}
		}
	};
Vue.component('tab-pane', TabPane);