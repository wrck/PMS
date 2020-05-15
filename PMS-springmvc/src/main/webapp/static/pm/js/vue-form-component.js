var FormInputs = {
		name: "formInputs",
		components: {
		    'form-input': FormInput
		},
		data: function() {
			return {
				fieldList: [],
				targetValue: {},
				targetName: "",
			};	
		},
		template: `<div>
						<template v-for="field in formFieldList">
							<form-input :field="field" :form-cols="formCols" :isCreated="isCreated" :formColsGroupClass="formColsGroupClass" :dataType="dataType" :maxLabelWidth="maxLabelWidth" :target-name="targetName" :target-value="targetValue" :permissionType="permissionType" :permissions="permissions" :roles="roles" :model="model" :timestamp="timestamp"></form-input>
						</template>
					</div>`,
		props: {
		    formColsGroupClass: {
		    	type: Object,
		    	default: /*function() {
					return */{
						1: {
							formGroupClass: "col-xs-12 col-sm-12 col-md-12",
							formGroupTextareaClass: "col-xs-12 col-sm-12 col-md-12",
						},
						2: {
							formGroupClass: "col-xs-12 col-sm-12 col-md-6",
							formGroupTextareaClass: "col-xs-12 col-sm-12 col-md-6",
						},
						3: {
							formGroupClass: "col-xs-12 col-sm-8 col-md-4",
							formGroupTextareaClass: "col-xs-12 col-sm-12 col-md-12",
						},
						4: {
							formGroupClass: "col-xs-12 col-sm-6 col-md-3",
							formGroupTextareaClass: "col-xs-12 col-sm-12 col-md-6",
						}
					}
		    	/*}*/
		    },
		    formCols: {
		    	type: Number,
		    	default: 4
		    },
		    isCreated: {
		    	type: Boolean,
		    	default: false
		    },
			fieldList: {
				type: Array,
				default: []
			},
		    dataType: {
				type: String,
				default: "form"
			},
		    targetName: {
				type: String,
				default: ""
			},
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
			/* var fieldList = this.fieldList;
			for (var i in fieldList) {
				var field = fieldList[i];
				if (field['extData']) {
					this.parseValue(field, "extData");
				}
				if (field.type == 'inputs') {
					// inputs 拥有相同的标签，在一个组内进行显示，以下参数需要拆分，用空格相隔
					var keys = ['alias', 'name', 'title', 'titleKey', 'cssId', 'cssClass', 'cssStyle'];
					var mutliField = this.parseValue(field, 'field', false) || [];
					typeof mutliField == 'string' && (mutliField = mutliField.split(" "));
					//var mutliField = (field.field || "").split(" ");
					var inputs = [];
					for(var i in mutliField) {
						var input = $.extend({}, field);
						input['field'] = mutliField[i];
						for(var k in keys) {
							var key = keys[k];
							//var values = (field[key] || "").split(" ");
							var values = this.parseValue(field, key, false) || [];
							if(key == 'cssClass') {
								values = values['selfClass'];
							}
							typeof values == 'string' && (values = values.split(" "));
							var value = null;
							if (i < values.length) {
								value = values[i];
							}
							input[key] = value;
						}
						inputs.push(input);
					}
					field.inputs = inputs;
				}
			} */
		},
		updated: function() {
			console.log("updated");
		},
		mounted: function() {
			console.log("mounted");
			var _this = this;
			$("input[type='range']").each(function(index, item) {
				_this.rangeChange({currentTarget: item});
			})
		},
		computed: {
			groupClass: function() {
				var groupClass = this.formGroupClass;
				if (!groupClass && this.formCols) {
					groupClass = this.formColsGroupClass[this.formCols].formGroupClass;
				}
				return groupClass;
			},
			groupTextareaClass: function() {
				var groupTextareaClass = this.formGroupTextareaClass;
				if (!groupTextareaClass && this.formCols) {
					groupTextareaClass = this.formColsGroupClass[this.formCols].formGroupTextareaClass;
				}
				return groupTextareaClass;
			},
			formFieldList: function() {
				var fieldList = this.fieldList;
				for (var i in fieldList) {
					var field = fieldList[i];
					if (field['extData']) {
						this.parseValue(field, "extData");
					}
					if (field.type == 'inputs') {
						// inputs 拥有相同的标签，在一个组内进行显示，以下参数需要拆分，用空格相隔
						var keys = ['alias', 'name', 'title', 'titleKey', 'cssId', 'cssClass', 'cssStyle'];
						var mutliField = this.parseValue(field, 'field', false) || [];
						typeof mutliField == 'string' && (mutliField = mutliField.split(" "));
						//var mutliField = (field.field || "").split(" ");
						var inputs = [];
						for(var i in mutliField) {
							var input = $.extend({}, field);
							input['field'] = mutliField[i];
							for(var k in keys) {
								var key = keys[k];
								//var values = (field[key] || "").split(" ");
								var values = this.parseValue(field, key, false) || [];
								if(key == 'cssClass') {
									values = values['selfClass'];
								}
								typeof values == 'string' && (values = values.split(" "));
								var value = null;
								if (i < values.length) {
									value = values[i];
								}
								input[key] = value;
							}
							inputs.push(input);
						}
						field.inputs = inputs;
					}
				}
				return fieldList;
			},
	 		maxLabelWidth: function() {
	 			if (this.dataType == "form") {
		 			var fieldList = this.fieldList || [];
		 			var width = "";
		 			var maxLen = 0;
		 			for (var i = 0; i < fieldList.length; i++) {
						var field = fieldList[i];
						var name = field.name || "";
						if (maxLen < name.length) {
							maxLen = name.length;
						}
					}
		 			return maxLen + "rem";
	 			}
	 			return null;
	 		}
	 	},
	 	methods: {
	 		getGroupClass: function(field) {
	 			if (field.cssClass && field.groupClass == undefined) {
		 			var cssClass = this.getDataValue(field.cssClass);
		 			field.groupClass =  cssClass['groupClass'];
	 			}
	 			return field.groupClass
	 		},
	 		getSelfClass: function(field) {
	 			if (field.cssClass && field.selfClass == undefined) {
		 			var cssClass = this.getDataValue(field.cssClass);
		 			field.selfClass =  cssClass['selfClass'] || '';
	 			}
	 			return field.selfClass
	 		},
	 		getFieldValue: function(field) {
	 			var value;
	 			if (field.type == 'computed' && field.render) {
	 				try {
	 					console.log("render")
	 					var render = eval(field.render);
	 					value = render(this.targetValue, field);
	 				}catch(e){
	 					console.error(e);
	 				}
	 			}
	 			try {
	 				value = value || eval("this.targetValue." + field.field);
	 			} catch(e) {}
 				try {
	 				value = value || eval("this.targetValue." + field.alias);
 				} catch(e) {}
 				if (value && field.type == 'date') {
 					value = new Date(value).Format('yyyy-MM-dd');
 				}
	 			return value;
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
	 		parseValue: function(field, key, update) {
	 			var value = field[key];
	 			try {
	 				var value = this.getDataValue(field[key]);
	 				update = update == false ? false : true; 
	 				if (update) {
	 					field[key] = value;
	 				}
	 			} catch(e){}
	 			return value;
	 		},
	 		rangeChange: function(event) {
	 			var target = event.currentTarget;
	 			var max = $(target).attr('max') || 100;
	 			var min = $(target).attr('min') || 0;
	 			var value = target.value;
	 			var process = value / ((max - min) || 1) * 100;
	 			$(target).css('background', 'linear-gradient(to right, #5bc0de, #5bc0de ' + process + '%, white 0%, white)');
	 			var $tip = $(target).siblings(".range-slider-tip");
	 			$tip.text(value);
	 			var tipWidth =$tip.width() / 2 + "px";
	 			$tip.css("left", "calc(" + process + "% + 1rem - " + tipWidth + " - " + process / 100 * 2 + "rem)");
	 		},
	 		isPermit: function(type, data, ext) {
	 			var permissionType = data.permissionType || this.permissionType || "";
	 			var permissions = this.permissions || [];
 				var model = data.type || data.model || this.model || "";
 				var permission = "";
	 			if (type == 'btn') {
					permission = model + ":" + ext.id;
		 		} else if (type == 'field') {
	 				permission = model + "." + data.field + "." + this.isCreated ? "add" : "update";
	 			}
	 			console.log(permission);
	 			if ((permissionType == "all" 
	 					|| permissionType == "edit" && RegExp(/:(add|edit|upload)\b,?/).match(permission) 
	 					|| permissionType == "view" && RegExp(/:(list|detail|download|batchDownload)\b,?/).match(permission))
	 					&& $.inArray(permission, permissions) > -1) {
					return true;
				}
	 		}
	 	}
	};
