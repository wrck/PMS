var FormInput = {
		name: "formInput",
		data: function() {
			return {
				fieldList: [],
				targetValue: {},
				targetName: "",
			};	
		},
		template: `<div v-if="isPermit">
						<template v-if="field.type == 'hidden' || !field.visible">
							<input :id="field.cssId || field.field" type="hidden" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
									:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
									:disabled="field.disabled" :readonly="fieldReadonly"
							>
						</template>
						<template v-else-if="field.type == 'textarea'">
							<div class="form-group display-flex" :class="getGroupClass(field) || groupTextareaClass">
								<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
								<textarea :id="field.cssId || field.field" :type="field.type" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
										:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle" :rows="(field.extData || {}).rows || 3" style="resize:none;" draggable="false"
										:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required"
										></textarea>
							</div>
						</template>
						<template v-else-if="field.type == 'date'">
							<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
								<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
								<input :id="field.cssId || field.field" type="text" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
										:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
										data-flag="datepicker" :data-format="field.render" autocomplete="off"
										:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required"
								>
							</div>
						</template>
						<template v-else-if="field.type == 'datetime'">
							<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
								<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
								<input :id="field.cssId || field.field" type="text" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
										:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
										data-flag="datetimepicker" data-format="field.render" autocomplete="off"
										:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required"
								>
							</div>
						</template>
						<template v-else-if="field.type == 'select'">
							<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
								<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
								<select :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
										:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
										:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required">
									<option :value="item[field.extValue]" v-if="getDataValue(field.extData).length"  v-for="item in getDataValue(field.extData)" :selected="item[field.extValue] == getFieldValue(field)" >{{item[field.extKey]}}</option>
								</select>
							</div>
						</template>
						<template v-else-if="field.type == 'urlSelector'">
							<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
								<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
								<select :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
										:value="getFieldValue(field)" :data-selected="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
										:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required"
										:data-flag="field.type" :data-src="(parseValue(field, 'extData') || {}).src || field.extData" :data-autoload="field.extData['autoload']"
										:data-src-data="field.extData['src-data']" :data-text="field.extKey" :data-value="field.extValue" :data-store-source="field.extData['store-source']"
										:data-blank="field.extData.blank || false" :data-blank-value="field.extData['blank-value']" :data-blank-text="field.extData['blank-text']"
										:data-select2-config="JSON.stringify(field.extData['select2-config'])"
										>
								</select>
							</div>
						</template>
						<template v-else-if="field.type == 'autocomplete'">
							<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
								<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
								<input :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
										:value="getFieldValue(field)" :data-selected="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
										:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required"
										:data-flag="field.type" :data-src="(parseValue(field, 'extData') || {}).src" :data-autoload="field.extData['autoload']"
										:data-src-data="field.extData['src-data']" :data-text="field.extKey" :data-value="field.extValue" :data-store-source="field.extData['store-source']"
										:data-autocomplete-config="JSON.stringify(field.extData['autocomplete-config'])"
								/>
							</div>
						</template>
						<template v-else-if="field.type == 'inputs'">
							<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
								<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
								<input :id="input.cssId || input.field" v-for="input in field.inputs" :type="dataType == 'table' && input.searchable ? 'search' : 'text'" class="form-control flex-grow-2" :class="getSelfClass(input) || input.cssClass" :name="input.field" :data-alias="input.alias"
										:value="getFieldValue(input)" :placeholder="input.title || input.name" :style="input.cssStyle" 
										:disabled="input.disabled" :readonly="input.readonly" :required="input.required" autocomplete="off">
							</div>
						</template>
						<template v-else-if="field.type == 'range'">
							<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
								<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
								<div class="flex-grow-2 range-slider form-control no-border pl-0 pr-0">
									<input :id="field.cssId || field.field" :type="dataType == 'table' && field.searchable ? 'search' : field.type" class="progress-bar-striped" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
											:value="getFieldValue(field) || 0" :placeholder="field.title || field.name" :style="field.cssStyle" @input="rangeChange($event, field)" @change="rangeChange($event, field)"
											:defaultValue="parseValue(field, 'extData').defaultValue" :max="field.extData['max']" :min="field.extData['min']" :step="field.extData['step']"
											:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required" autocomplete="off">
									<small class="range-slider-tip">{{getFieldValue(field) || field.extData['min'] || 0}}</small>
								</div>
							</div>
						</template>
						<template v-else-if="field.type == 'radio'">
							<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
								<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
								<div class="flex-grow-2 range-slider form-control no-border pl-0 pr-0">
								<template v-for="item in getDataValue(field.extData)">
									<label :for="(field.cssId || field.field) + '_' + item[field.extValue]" style="text-align: right;" class="control-label flex-shrink-0">{{item[field.extKey]}}
									<input :id="(field.cssId || field.field) + '_' + item[field.extValue]" :type="field.type" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
											:value="item[field.extValue]" :checked="item[field.extValue] == getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
											:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required"
											data-flag="icheck" 
									/>
									</label>
								</template>
								</div>
							</div>
						</template>
						<template v-else-if="field.type == 'file'">
							<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
								<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
								<input :id="(field.cssId || field.field) + '_hidden'" type="hidden" :name="field.field" :data-alias="field.alias"
									:value="getFieldValue(field) || (field.extData || {}).defaultValue" :placeholder="field.title || field.name"
									:disabled="field.disabled" :readonly="fieldReadonly" :required="field.required" autocomplete="off">
								<input :id="field.cssId || field.field" :type="dataType == 'table' && field.searchable ? 'search' : field.type" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" 
									:disabled="field.disabled" :readonly="fieldReadonly" :required="field.required" autocomplete="off"
									:allowType="(field.extData || {}).allowType" :uploadUrl="(field.extData || {}).uploadUrl"
									:multiple="(field.extData || {}).multiple"
								>
								<button type="button" class="btn btn-success flex-grow-1" @click="commonUploadFile($event, field)" :disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly">上传</button>
							</div>
						</template>
						<template v-else>
							<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
								<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
								<input :id="field.cssId || field.field" :type="dataType == 'table' && field.searchable ? 'search' : field.type" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
										:value="getFieldValue(field) || (field.extData || {}).defaultValue" :placeholder="field.title || field.name" :style="field.cssStyle" 
										:disabled="field.disabled" :readonly="fieldReadonly" :required="field.required" autocomplete="off">
							</div>
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
		    keyword: {
		    	type: String,
		    	default: "id"
		    },
		    isCreated: {
		    	type: Boolean,
		    	default: false
		    },
		    maxLabelWidth: {
		    	type: String,
	    		default: ""
		    },
			field: {
				type: Object,
				default: {}
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
		},
		updated: function() {
			console.log("updated");
		},
		mounted: function() {
			console.log("mounted");
			var _this = this;
			if (this.field.type == 'range') {
				$("input[type='range']").each(function(index, item) {
					_this.rangeChange({currentTarget: item});
				})
			}
		},
		computed: {
			isPermit:function(_this, field) {
				field = field || this.field || {};
				/*var permissionType = this.permissionType || "";
	 			var permissions = this.permissions || [];
 				var model = this.model || "";
 				var permission = model + "." + field.field + "." + (this.isCreated ? "add" : "edit");
	 			if ((permissionType == "all" 
	 					|| permissionType == "edit"
	 					|| permissionType == "view"
	 					&& $.inArray(permission, permissions) > -1) {
					return true;
				}*/
				var permissionType = this.permissionType || "";
				var isPermit = permissionType != "";// == "all" || permissionType == "edit";
				var checkPermitCallback = (field.extData || {}).checkPermitCallback;
				if (typeof checkPermitCallback == 'function') {
	 				try {
	 					isPermit = isPermit && checkPermitCallback.call(this, field, isPermit);
	 				} catch(e) {}
	 			}
	 			return isPermit;
			},
			fieldReadonly: function(_this, field) {
				field = field || this.field || {};
				var permissionType = this.permissionType || "";
				var readonly = field.readonly || permissionType == "view";
				var readonlyCallback = (field.extData || {}).readonlyCallback;
				if (typeof readonlyCallback == 'function') {
	 				try {
	 					readonly = readonly && readonlyCallback.call(this, field, readonly);
	 				} catch(e) {}
	 			}
				return readonly;
			},
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
	 					value = render.call(this, this.targetValue, field);
	 				}catch(e){
	 					console.error(e);
	 				}
	 			} else {
	 				if (!value) {
	 					try {
			 				var keys = field.field.split(" ");
			 				var values = [];
			 				for(var i in keys) {
			 					var key = keys[i];
			 					try {
			 						values.push(eval("this.targetValue." + key));
			 					} catch(e) {}
			 				}
			 				if (values.length == 1) {
			 					value = values[0];
			 				} else {
			 					value = values.join("-");
			 				}
			 			} catch(e) {}
		 			}
	 				if (!value) {
	 					try {
			 				var keys = field.alias.split(" ");
			 				var values = [];
			 				for(var i in keys) {
			 					var key = keys[i];
			 					try {
			 						values.push(eval("this.targetValue." + key));
			 					} catch(e) {}
			 				}
			 				if (values.length == 1) {
			 					value = values[0];
			 				} else {
			 					value = values.join("-");
			 				}
			 			} catch(e) {}
		 			}
	 				if (value && field.type == 'date') {
	 					value = new Date(value).Format('yyyy-MM-dd');
	 				}
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
	 		},
	 		commonUploadFile: function(event, field) {
	 			var $target = $(event.currentTarget);
	 			var $file = $target.prev();
	 			var _this = this;
	 			simpleAjaxUploadFile($file,function(result){
					var result = eval('(' + result + ')');
					//上传成功
					var files = result.data || [];
					var $fileHidden = $file.prev();
					var fileIds = $.trim($fileHidden.val() || "");
					fileIds = fileIds ? fileIds.split(",") : [];
					if(result.success){
						for ( var i in files) {
							var file = files[i];
							var fileId = file.id;
							if ($.inArray(fileId, fileIds) == -1) {
								fileIds.push(fileId);
							}
						}
						$fileHidden.val(fileIds.join(","));
						$file.val('');
						
						var $form = $target.parents("form:first");
						$submit = $form.find("button[data-btn-type='save']");
						$submit.click();
						
						var callback = (field.extData || {}).uploadFileCallback;
						if (typeof callback == 'function') {
							callback.call(_this, event, field);
						}
					} else {
						modals.error(result.message);
					}
					$target.removeAttr('disabled');
				})
	 		}
	 	}
	};
