var NavTab = {
		name: "navTab",
		components: {
		    'tab-pane': TabPane
		},
		data: function() {
			return {
			};	
		},
		template: `
		<div v-if="navTabList.length > 0" :id="tabContainerId" class="tab-content box box-primary mt-1">
			<ul :id="tabWrapperId" class="nav nav-tabs">
				<template v-for="navTab in navTabList">
					<li v-if="navTab.isPermit">
						<a :href="'#' + (navTab.cssId || navTab.type) + 'Tab' + timestamp" data-toggle="tab" class="tab-bg-primary" aria-expanded="true" @click.once="refreshNavTab($event, navTab)">{{navTab.title}}<span class="tab-operation fa fa-refresh ml-05" @click.self.stop.prevent="refreshNavTab($event, navTab)"></span></a>
					</li>
				</template>
			</ul>
			<template v-for="navTab in navTabList">
				<tab-pane :ref="'#' + (navTab.cssId || navTab.type) + 'Tab' + timestamp" v-if="navTab.isPermit" :css-id="navTab.cssId" :nav-tab="navTab" :timestamp="timestamp" :target-value="targetValue" :model="model"></tab-pane>
			</template>
		</div>`,
		props: {
			targetValue: {
				type: Object,
				default: () => {},
				required: true
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
				default: () => []
			},
			roles: {
				type: Array,
				default: () => []
			},
			timestamp:  {
				type: Number,
				default: function() {
					return new Date().getTime();
				}
			},
			tabContentId: {
			    type: String,
				default: function() {
					return (this.model || "");
				}
		    },
		    navTabWrapper: {
			    type: String,
				default: function() {
					return (this.tabContentId || "");
				}
		    },
		    tabList: {
		    	type: Array,
				default: () => [],
			    required: true
		    },
		},
		created: function(e) {
			console.log("created");
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
			checkPermit: function(navTab) {
 				var model = navTab.type || "";
 				var permissions = this.permissions || [];
 				var permission = model + ":list";
 				var checkPermitCallback = navTab.checkPermit;
	 			console.log(permission);
	 			var isPermit = false;
	 			if ($.inArray(permission, permissions) > -1) {
	 				isPermit = true;
				}
	 			if (typeof checkPermitCallback == 'function') {
	 				try {
	 					isPermit = checkPermitCallback.call(this, navTab) || isPermit;
	 				} catch(e) {}
	 			}
	 			return isPermit;
			},
			navTabTransfer: function() {
				console.log("transfer");
				var tabList = this.tabList || [];
				var navTabList = [];
				for (var i = 0; i < tabList.length; i++) {
					var tab = tabList[i];
					var navTab = this.parseValue(tab, 'extData') || {};
					navTab.id = tab.id;
					navTab.cssId = tab.cssId;
					navTab.cssClass = tab.cssClass;
					navTab.cssType = tab.cssType;
					navTab.src = navTab.src || navTab.url;
					navTab.url = this.parseUrl(navTab);
					navTab.type = navTab.type || tab.field;
					navTab.title = tab.title || tab.name;
					navTab.permissionType = tab.permissionType || "";
					navTab.render = this.parseValue(tab, 'render');
					navTab.isPermit = this.checkPermit(navTab);
					if (navTab.isPermit) {
						navTabList.push(navTab);
					}
				}
				return navTabList;
			},
			parseUrl: function(navTab) {
				var targetValue = this.targetValue || {};
				var params = navTab.params || [];
				for (var i = 0; i < params.length; i++) {
					var paramName = params[i] || "";
					var kv = this.parseParam(paramName, targetValue);
					var param = kv.key || paramName;
					eval("var " + param + " = targetValue[param] || '';");
					navTab.paramsValue = navTab.paramsValue || {};
					try {
						var value = eval(paramName) || "";
						if (value) {
							navTab.paramsValue[paramName] = value;
						} else {
							navTab.paramsValue[param] = targetValue[param] || "";
						}
						
					} catch(e) {
						navTab.paramsValue[param] = targetValue[param] || "";
					}
				}
				var url = navTab.src || navTab.url;
				try {
					url = eval(url);
				} catch(e){}
				return url;
			},
			parseParam: function(param, paramsValue) {
				if (!param) {
			        return {};
			    }
				var relations = param.split(/(\.)|(\[)|(\]\[)|(\])/g).filter(function(item, index) {
					return item
				});
			    var key = relations[0];
			    var value = {};
			    
//			    // 将param解析为值对象
//				var subValue = value;
//				paramsValue = paramsValue || {};
//			    var tempParams = $.extend(true, {}, paramsValue);
//				var splitStr = "";
//			    for (var i in relations) {
//			        var tempKey = relations[i];
//					if ($.inArray(tempKey, [".", "[", "]"]) != -1) {
//						splitStr = tempKey;
//						continue;
//					}
//					if (splitStr == "[") {
//						tempKey = eval(tempKey);
//					}
//					var tempValue = tempParams[tempKey] || "";
//			        if (typeof tempValue == "object") {
//			        	// 更新值的子集
//			        	tempParams = tempValue;
//			        	
//			        	// 获取子对象进行赋值更新
//			        	subValue[tempKey] = subValue[tempKey] || {};
//			        	// 更新子对象为下一集
//						subValue = subValue[tempKey];
//			            continue;
//			        }
//			        subValue[tempKey] = tempValue;
//			        break;
//			    }
			    return {key, value};
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
	 			try {
	 				this.$refs[tabId][0].refreshNavTab(e, navTab);
	 			} catch(e) {}
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
//		computed: {
//			isPermit:function(btn) {
//				return this.permissionType != '' ? true : false;
//			}
//		},
//		methods: {
//			checkPermit: function(btn) {
//	 			var permissionType = this.permissionType || "";
//	 			var permissions = this.permissions || [];
// 				var model = this.navTab.type || this.model || "";
// 				var permission = model + ":" + btn.id;
// 				var checkPermitCallback = this.navTab.checkPermit;
//	 			console.log(permission);
//	 			var isPermit = false;
//	 			if ((permissionType == "all" 
//	 					|| permissionType == "edit" && RegExp(/:(add|edit|upload|delete|import)\b,?/).test(permission) 
//	 					|| permissionType == "view" && RegExp(/:(list|detail|download|batchDownload)\b,?/).test(permission))
//	 					&& $.inArray(permission, permissions) > -1) {
//	 				isPermit = true;
//				}
//	 			if (typeof checkPermitCallback == 'function') {
//	 				try {
//	 					isPermit = checkPermitCallback.call(this, btn) || isPermit;
//	 				} catch(e) {}
//	 			}
//	 			return isPermit;
//			},
//			parseUrl: function(navTab) {
//				var targetValue = this.targetValue || {};
//				var params = navTab.params || [];
//				for (var i = 0; i < params.length; i++) {
//					var param = params[i];
//					eval("var " + param + " = '" + (targetValue[param] || "") + "';");
//					navTab.paramsValue = navTab.paramsValue || {};
//					navTab.paramsValue[param] = targetValue[param] || "";
//				}
//				var url = navTab.url;
//				try {
//					url = eval(url);
//				} catch(e){}
//				return url;
//			},
//	 		getDataValue: function(key) {
//	 			var value;
//	 			try {
//	 				value = eval("this." + key);
//	 			} catch(e) {
//	 			}
// 				try {
//	 				value = value || JSON.parse(key);
// 				} catch(e) {
// 				}
//	 			try {
//	 				value = value || eval(key);
// 				} catch(e) {
// 					value = key;
// 				}
//	 			return value;
//	 		},
//	 		parseValue: function(field, key) {
//	 			try {
//	 				field[key] = this.getDataValue(field[key]);
//	 			} catch(e){}
//	 			return field[key];
//	 		},
//	 		refreshNavTab: function(e, navTab) {
//	 			console.log("refreshNavTab");
//	 			var tab = $(e.target);
//	 			if ($(e.target).hasClass("tab-operation")) {
//	 				tab = $(e.target).parent();
//	 			}
//	 			// 获取已激活的标签页的名称
//	 			var activeTab = $(tab).text(); 
//	 			var tabId = $(tab).attr("href");
//	 			var $container = $(tab).parents(".tab-content:first");
//	 			$container.data("vm", this);
//	 			if($(tabId, $container).hasClass("loaded") == '' && !$(tabId + " .overlay:first", $container).hasClass("loading")){
//	 	            $(tabId + " .overlay", $container).addClass("loading");
//	 	            var config = $(tabId, $container).data("config") || $(tabId, $container).data() || (navTab || {}).tableConfig;
//	 	            config.container = $container;
//	 				initTabData.call(this, config, false, navTab);
//	 			} else {
//	 				$(tabId + " .overlay", $container).addClass("loading");
//	 	            var config = $(tabId, $container).data("config") || $(tabId, $container).data() || (navTab || {}).tableConfig;
//	 	            config.container = $container;
//	 				initTabData.call(this, config, true, navTab);
//	 			}
//	 		}
//		}
	};
