var TabPane = {
		name: "tabPane",
		data: function() {
			return {
				isUpdated: false,
				tabId: this.cssId || (this.navTab.type + 'Tab' + this.timestamp) 
			};
		},
		template: `<div class="tab-pane fade" :id="tabId" 
			:data-url="navTab.url" :data-type="navTab.type" :data-title="navTab.title"
			:data-draw-type="navTab.drawType" :data-timestamp="timestamp" :data-table-config="JSON.stringify(navTab.tableConfig)"
			>
			<!--:data-url="parseUrl(navTab)" :data-url="navTab.url" :data-config="JSON.stringify(navTab)" -->
			
			<!-- <div class="box box-primary mb-0"> -->
				<div class="box-body">
					<div class="overlay"><i class="fa fa-refresh fa-spin"></i></div>
					<div :id="navTab.type + 'SearchDiv' + timestamp" v-if="(navTab.operations || []).length > 0" class="searchDiv">
						<div class="btn-group operate-btn-group">
	                         <button type="button" class="btn btn-default" v-for="btn in navTab.operations" v-if="isPermit && checkPermit(btn)" :data-btn-type="btn.id" @click="btn.events['click']($event, navTab)">{{btn.text}}</button>
	                     </div>
					</div>
				</div>
			<!-- </div> -->
		</div>`,
		props: {
			cssId: {
			    type: String
		    },
		    navTab: {
			    type: Object,
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
			},
			timestamp:  {
				type: Number,
				default: function() {
					return new Date().getTime();
				}
			},
			
		},
		created: function(e) {
			console.log("created", this.tabId);
			if (this.navTab.created) {
				this.navTab.created.call(this);
			}
		},
		beforeUpdate: function() {
			if (this.isUrlUpdated && !this.isUpdated) {
				this.refreshNavTab(null, this.navTab);
				this.isUpdated = true;
			}
		},
		updated: function() {
			console.log("updated", this.tabId);
			if (this.navTab.updated) {
				this.navTab.updated.call(this);
			}
		},
		mounted: function() {
			console.log("mounted", this.tabId);
		},
		computed: {
			isUrlUpdated: function() {
				var url = $("#" + this.tabId).data("url");
				var isUrlUpdated = this.navTab.url != url;
				if (isUrlUpdated) {
					this.isUpdated = false;
					$("#" + this.tabId).data("url", this.navTab.url);
				}
				return isUrlUpdated;
			},
			isPermit:function(btn) {
				return this.permissionType != '' ? true : false;
			}
			/*isPermit: function(btn) {
	 			var permissionType = this.navTab.permissionType || "";
	 			var permissions = this.permissions || [];
 				var model = this.navTab.type || this.model || "";
 				var permission = model + ":" + btn.id;
	 			console.log(permission);
	 			if ((permissionType == "all" 
	 					|| permissionType == "edit" && RegExp(/:(add|edit|upload)\b,?/).match(permission) 
	 					|| permissionType == "view" && RegExp(/:(list|detail|download|batchDownload)\b,?/).match(permission))
	 					&& $.inArray(permission, permissions) > -1) {
					return true;
				}
	 			return false;
			}*/
		},
		methods: {
			checkPermit: function(btn) {
	 			var permissionType = this.permissionType || "";
	 			var permissions = this.permissions || [];
 				var model = this.navTab.type || this.navTab.model || this.model || "";
 				var permission = model + ":" + btn.id;
 				var checkPermitCallback = this.navTab.checkPermit;
	 			console.log(permission);
	 			var isPermit = false;
	 			if ((permissionType == "all" 
	 					|| permissionType == "edit" && RegExp(/:(add|edit|upload|delete|import)\b,?/).test(permission) 
	 					|| (permissionType == "edit" || permissionType == "view") && RegExp(/:(list|detail|download|batchDownload)\b,?/).test(permission))
	 					&& ($.inArray(permission, permissions) > -1 || $.inArray(model + ":*", permissions) > -1)) {
	 				isPermit = true;
				}
	 			if (typeof checkPermitCallback == 'function') {
	 				try {
	 					isPermit = checkPermitCallback.call(this, btn) || isPermit;
	 				} catch(e) {}
	 			}
	 			return isPermit;
			},
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
					var param = params[i] || "";
					var kv = this.parseParam(param, targetValue);
					param = kv.key || param;
					eval("var " + param + " = targetValue[param] || '';");
					navTab.paramsValue = navTab.paramsValue || {};
					navTab.paramsValue[param] = targetValue[param] || "";
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
			    var key = relations[0];
			    var value = {};
			    
//			    // 将param解析为值对象
//				var subValue = value;
//				paramsValue = paramsValue || {};
//				var relations = param.split(/(\.)|(\[)|(\]\[)|(\])/g).filter(function(item, index) {
//					return item
//				});
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
	 			console.log("refreshNavTab");
	 			var tab;
	 			if (e) {
		 			tab = $(e.target);
		 			if ($(e.target).hasClass("tab-operation")) {
		 				tab = $(e.target).parent();
		 			}
	 			} else {
	 				tab = $("[href='#" + this.tabId + "']");
	 			}
//	 			var tab = $(e.target);
//	 			if ($(e.target).hasClass("tab-operation")) {
//	 				tab = $(e.target).parent();
//	 			}
	 			// 获取已激活的标签页的名称
	 			var activeTab = $(tab).text(); 
	 			var tabId = $(tab).attr("href") || this.tabId;
	 			var $container = $(tab).parents(".tab-content:first");
	 			var $tabPane = $(tabId).length ? $(tabId) : $container;
	 			$tabPane.data("vm", this);
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
