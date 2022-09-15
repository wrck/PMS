let GlobalTemplates = {
		hidden: `<input :id="field.cssId || field.field" type="hidden" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
						:value="fieldValue" :placeholder="field.title || field.name" :style="field.cssStyle"
						:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly"
				>`,
		textarea: `<div class="form-group display-flex" :class="getGroupClass(field) || groupTextareaClass">
							<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}"><span :class="{'redMark':field.required}">{{field.name}}</span></label>
							<textarea :id="field.cssId || field.field" :type="field.type" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
									:value="fieldValue" :placeholder="field.title || field.name" :style="field.cssStyle" :rows="(field.extData || {}).rows || 3" style="resize:none;" draggable="false"
									:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required" data-flag="autosize"
									></textarea>
						</div>`,
		date: `<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
							<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}"><span :class="{'redMark':field.required}">{{field.name}}</span></label>
							<input :id="field.cssId || field.field" type="text" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
									:value="fieldValue" :placeholder="field.title || field.name" :style="field.cssStyle"
									data-flag="datepicker" :data-format="field.extData.format || field.render" autocomplete="off"
									:data-date-start-date="field.extData.startDate" :data-date-end-date="field.extData.endDate"
									:data-date-today-btn="field.extData.todayBtn"
									:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required"
							>
						</div>`,
		datetime: `<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
							<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}"><span :class="{'redMark':field.required}">{{field.name}}</span></label>
							<input :id="field.cssId || field.field" type="text" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
									:value="fieldValue" :placeholder="field.title || field.name" :style="field.cssStyle"
									data-flag="datetimepicker" :data-format="field.extData.format || field.render" autocomplete="off"
									:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required"
									:data-start-date="field.extData.startDate" :data-end-date="field.extData.endDate"
									:data-max-date="field.extData.maxDate" :data-min-date="field.extData.minDate"
							>
						</div>`,
		daterange: `<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
							<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}" :values="fieldValue"><span :class="{'redMark':field.required}">{{field.name}}</span></label>
							<button type="button" class="btn border-gray daterange-btn form-control" :id="(field.cssId || field.field) + 'DaterangeBtn'" 
								data-toggle="daterangepicker"
								:data-start-date="field.extData.startDate" :data-end-date="field.extData.endDate"
								:data-max-date="field.extData.maxDate" :data-min-date="field.extData.minDate"
								:data-format="field.extData.format || field.render"
							>
				                <i class="fa fa-calendar mr-0.5"></i>&nbsp;
				                <span class="daterange-span">不限</span>
				                <i class="fa fa-caret-down"></i>
				                <input :id="input.cssId || input.field" v-for="(input, index) in field.inputs" type="hidden" class="form-control flex-grow-2" :class="'daterange-input-' + (index%2 ? 'end' : 'start') + ' ' + (getSelfClass(input) || input.cssClass || '')" :name="input.field" :data-alias="input.alias" data-type="search"
									:value="input.value" :placeholder="input.title || input.name" :style="input.cssStyle" 
									:disabled="input.disabled || fieldReadonly" :readonly="input.readonly || fieldReadonly" :required="input.required" autocomplete="off">
			                </button>
						</div>`,
		distpicker: `<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
							<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}" :values="fieldValue"><span :class="{'redMark':field.required}">{{field.name}}</span></label>
							<div :id="(field.cssId || field.field) + 'Distpicker'" class="distpicker display-flex row col-xs-12"
								data-toggle="distpicker" :data-placeholder="field.extData.placeholder"
								:data-auto-select="field.extData.autoSelect" 
								:data-province="field.extData.province"
								:data-city="field.extData.city"
								:data-district="field.extData.district"
								:data-value-type="field.extData.valueType"
								>
							  	<select :id="input.cssId || input.field" v-for="(input, index) in field.inputs" class="form-control flex-grow-2" :class="(getSelfClass(input) || input.cssClass || '')" :name="input.field" :data-alias="input.alias"
									:value="input.value" :data-selected="input.value" :placeholder="input.title || input.name" :style="input.cssStyle" 
									:disabled="input.disabled || fieldReadonly" :readonly="input.readonly || fieldReadonly" :required="input.required" autocomplete="off">
								</select>
							</div>
						</div>`,
		select: `<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
							<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}"><span :class="{'redMark':field.required}">{{field.name}}</span></label>
							<select :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
									:value="fieldValue" :data-selected="fieldValue" :placeholder="field.title || field.name" :style="field.cssStyle"
									:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required"
									:multiple="field.extData.multiple || (field.extData['select2-config'] || {}).multiple || false"
									:data-text="field.extKey" :data-value="field.extValue"
									:data-blank="field.extData.blank || false" :data-blank-value="field.extData['blank-value']" :data-blank-text="field.extData['blank-text']"
									:data-select2-config="JSON.stringify(field.extData['select2-config'])">
								<template v-if="((field.extData || {}).data || field.extData).length">
									<option :value="item[field.extValue]" v-for="item in ((field.extData || {}).data || field.extData)" :selected="item[field.extValue] == fieldValue" >{{item[field.extKey]}}</option>
								</template>
								<template v-else-if="field.extData.blank && !(field.extData.multiple || (field.extData['select2-config'] || {}).multiple || false)">
									<option blank="true" :value="field.extData['blank-value']">{{field.extData['blank-text']}}</option>
								</template>
							</select>
						</div>`,
		urlSelector: `<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
							<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}"><span :class="{'redMark':field.required}">{{field.name}}</span></label>
							<select :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
									:value="fieldValue" :data-selected="fieldValue" :placeholder="field.title || field.name" :style="field.cssStyle"
									:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required"
									:multiple="field.extData.multiple || (field.extData['select2-config'] || {}).multiple || false"
									:data-flag="field.type" :data-src="(parseValue(field, 'extData') || {}).src || field.extData" :data-autoload="field.extData['autoload']"
									:data-allow-clear="field.extData.allowClear || false" :data-src-data="field.extData['src-data']" :data-text="field.extKey" :data-value="field.extValue" :data-store-source="field.extData['store-source']"
									:data-blank="field.extData.blank || false" :data-blank-value="field.extData['blank-value']" :data-blank-text="field.extData['blank-text']"
									:data-select2-config="JSON.stringify(field.extData['select2-config'])"
									>
							</select>
						</div>`,
		autocomplete: `<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
							<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}"><span :class="{'redMark':field.required}">{{field.name}}</span></label>
							<input :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
									:value="fieldValue" :data-selected="fieldValue" :placeholder="field.title || field.name" :style="field.cssStyle"
									:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required"
									:data-flag="field.type" :data-src="(parseValue(field, 'extData') || {}).src" :data-autoload="field.extData['autoload']"
									:data-src-data="field.extData['src-data']" :data-text="field.extKey" :data-value="field.extValue" :data-store-source="field.extData['store-source']"
									:data-autocomplete-config="JSON.stringify(field.extData['autocomplete-config'])"
							/>
						</div>`,
		inputs: `<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
							<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}" :values="fieldValue"><span :class="{'redMark':field.required}">{{field.name}}</span></label>
							<input :id="input.cssId || input.field" v-for="input in field.inputs" :type="dataType == 'table' && input.searchable ? 'search' : 'text'" class="form-control flex-grow-2" :class="getSelfClass(input) || input.cssClass" :name="input.field" :data-alias="input.alias"
									:value="input.value" :placeholder="input.title || input.name" :style="input.cssStyle" 
									:data-flag="input.extData.dataFlag"
									:disabled="input.disabled || fieldReadonly" :readonly="input.readonly || fieldReadonly" 
									:required="input.required" autocomplete="off">
						</div>`,
		range: `<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
							<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}"><span :class="{'redMark':field.required}">{{field.name}}</span></label>
							<div class="flex-grow-2 range-slider form-control no-border pl-0 pr-0">
								<input :id="field.cssId || field.field" :type="dataType == 'table' && field.searchable ? 'search' : field.type" class="progress-bar-striped" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
										:value="fieldValue || 0" :placeholder="field.title || field.name" :style="field.cssStyle" @input="rangeChange($event, field)" @change="rangeChange($event, field)"
										:defaultValue="parseValue(field, 'extData').defaultValue" :max="field.extData['max']" :min="field.extData['min']" :step="field.extData['step']"
										:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required" autocomplete="off">
								<small class="range-slider-tip">{{fieldValue || field.extData['min'] || 0}}</small>
							</div>
						</div>`,
		radio: `<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
							<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}"><span :class="{'redMark':field.required}">{{field.name}}</span></label>
							<div class="flex-grow-2 form-control no-border pl-0 pr-0">
							<template v-for="item in ((parseValue(field, 'extData') || {}).data || field.extData)">
								<label :for="(field.cssId || field.field) + '_' + item[field.extValue]" style="text-align: right;" class="control-label flex-shrink-0 radio">{{item[field.extKey]}}
								<input :id="(field.cssId || field.field) + '_' + item[field.extValue]" :type="field.type" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
										:value="item[field.extValue]" :checked="item[field.extValue] == fieldValue" :placeholder="field.title || field.name" :style="field.cssStyle"
										:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required"
										data-flag="icheck" 
								/>
								</label>
							</template>
							</div>
						</div>`,
		file: `<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
							<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}"><span :class="{'redMark':field.required}">{{field.name}}</span></label>
							<input :id="(field.cssId || field.field) + '_hidden'" type="hidden" :name="field.field" :data-alias="field.alias"
								:value="fieldValue || (field.extData || {}).defaultValue" :placeholder="field.title || field.name"
								:disabled="field.disabled" :readonly="fieldReadonly" :required="field.required" autocomplete="off">
							<input :id="field.cssId || field.field" :type="dataType == 'table' && field.searchable ? 'search' : field.type" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" 
								:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required" autocomplete="off" :style="field.cssStyle" 
								:allowType="(field.extData || {}).allowType" :uploadUrl="(field.extData || {}).uploadUrl"
								:multiple="(field.extData || {}).multiple"
							>
							<button type="button" style="padding-top:5.5px;padding-bottom:5.5px;" class="btn btn-success flex-grow-1" @click="commonUploadFile($event, field)" :disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly">上传</button>
						</div>`,
		button: `<button :id="field.cssId || field.field" :type="field.render(targetValue, field) || 'button'" class="btn" :class="getSelfClass(field) || field.cssClass" :data-btn-type="field.field" style="margin:0 0.15rem" :style="field.cssStyle" :disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly">{{field.name}}</button>
				`,
		buttons: `<div class="form-group" :class="getGroupClass(field) || groupClass">
							<template v-for="input in field.inputs">
								<button :id="input.cssId || input.field" :type="field.render(targetValue, input)" :data-btn-type="input.field"
									class="btn" :class="getSelfClass(input) || input.cssClass" style="margin:0 0.15rem" :style="input.cssStyle" 
									:disabled="input.disabled || fieldReadonly" :readonly="input.readonly || fieldReadonly">{{input.name}}</button>
							</template>
						</div>`,
		default: `<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
							<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}"><span :class="{'redMark':field.required}">{{field.name}}</span></label>
							<input :id="field.cssId || field.field" :type="dataType == 'table' && field.searchable ? 'search' : field.type" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
									:value="fieldValue || (field.extData || {}).defaultValue" :placeholder="field.title || field.name" :style="field.cssStyle" 
									:disabled="field.disabled || fieldReadonly" :readonly="fieldReadonly" :required="field.required" autocomplete="off"
									:max="(field.extData || {})['max']" :min="(field.extData || {})['min']" :step="(field.extData || {})['step']"
							>
						</div>`
	}
var FormInput = {
		name: "formInput",
		data: function() {
			return {
//				fieldList: [],
//				targetValue: {},
//				targetName: "",
			};	
		},
		render: function (createElement) {
			if (this.isPermit) {
				var fieldType = this.fieldType;
				var template = GlobalTemplates[fieldType] || GlobalTemplates['default'];
				var options = this.$options;
				return Vue.compile(template, {
					outputSourceRange: true,
					shouldDecodeNewlines: options.shouldDecodeNewlines,
					shouldDecodeNewlinesForHref: options.shouldDecodeNewlinesForHref,
					delimiters: options.delimiters,
					comments: options.comments
				}, this).render.call(this, createElement);
			}
			return "";
		},
		props: {
		    formColsGroupClass: {
		    	type: Object,
		    	default: function() {
					return {
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
		    	}
		    },
		    formCols: {
		    	type: Number,
		    	default: 4
		    },
		    keyword: {
		    	type: String,
		    	default: "id"
		    },
		    isCreate: {
		    	type: Boolean,
		    	default: false
		    },
		    maxLabelWidth: {
		    	type: String,
	    		default: ""
		    },
			field: {
				type: Object,
				default: () => {}
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
				type: Object,
				default: () => {}
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
				default: ()=>[]
			},
			roles: {
				type: Array,
				default: ()=>[]
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
			console.log("updated", this.field.cssId || this.field.field);
			this.refreshInput(true);
		},
		mounted: function() {
			console.log("mounted", this.field.cssId || this.field.field);
			this.refreshInput();
		},
		computed: {
			fieldType: function() {
				var type = this.field.type || 'default';
				return !this.fieldVisible ? 'hidden' : type;
			},
			fieldValue: function() {
				var targetValue = this.targetValue || {};
				var field = this.field;
				var inputs = field.inputs;
				if (inputs) {
					for (var i = 0; i < inputs.length; i++) {
						var input = inputs[i];
						input.value = this.getFieldValue(input);
					}
				}
				return this.getFieldValue(field);
			},
			isPermit: function(_this, field) {
				field = field || this.field || {};
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
//			isPermit: {
//				get(_this, field) {
//					field = field || this.field || {};
//					var permissionType = this.permissionType || "";
//					var isPermit = permissionType != "";// == "all" || permissionType == "edit";
//					var checkPermitCallback = (field.extData || {}).checkPermitCallback;
//					if (typeof checkPermitCallback == 'function') {
//		 				try {
//		 					isPermit = isPermit && checkPermitCallback.call(this, field, isPermit);
//		 				} catch(e) {}
//		 			}
//		 			return isPermit;
//				},
//				set(v) {
//					
//				}
//			},
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
			fieldVisible: function(_this, field) {
				field = field || this.field || {};
				var permissionType = this.permissionType || "";
				var visible = field.visible;
				var visibleCallback = (field.extData || {}).visibleCallback;
				if (typeof visibleCallback == 'function') {
	 				try {
	 					visible = visible && visibleCallback.call(this, field, visible);
	 				} catch(e) {}
	 			}
				return visible;
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
		 			field.selfClass =  cssClass['selfClass'] || "";
	 			}
	 			return field.selfClass
	 		},
	 		getFieldValue: function(field) {
	 			var value = "";
	 			if (field.type == 'computed' && field.render) {
	 				try {
	 					console.log("render")
	 					var render = eval(field.render);
	 					value = render.call(this, this.targetValue, field);
	 				}catch(e){
	 					console.error(e);
	 				}
	 			} else {
	 				var separator = (field.extData || {}).separator || "-";
	 				if (!value) {
	 					try {
			 				var keys = field.alias.split(" ");
			 				var values = [];
			 				for(var i in keys) {
			 					var key = keys[i];
			 					try {
			 						var tv = eval("this.targetValue." + key);
			 						if (tv !== undefined && tv !== null && tv !== '') {
			 							values.push(tv);
			 						}
			 					} catch(e) {}
			 				}
			 				if (values.length == 1) {
			 					value = values[0];
			 				} else {
			 					value = values.join(separator);
			 				}
			 			} catch(e) {}
		 			}
	 				if (!value) {
	 					try {
			 				var keys = field.field.split(" ");
			 				var values = [];
			 				for(var i in keys) {
			 					var key = keys[i];
			 					try {
			 						var tv = eval("this.targetValue." + key);
			 						if (tv !== undefined && tv !== null && tv !== '') {
			 							values.push(tv);
			 						}
			 					} catch(e) {}
			 				}
			 				if (values.length == 1) {
			 					value = values[0];
			 				} else {
			 					value = values.join(separator);
			 				}
			 			} catch(e) {}
		 			}
	 				if (!value) {
	 					var defaultValue = (field.extData || {}).defaultValue;
	 					if (defaultValue != undefined) {
	 						value = defaultValue;
	 					}
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
	 				if (update && value != undefined) {
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
	 			$(target).addClass("range-inited");
	 		},
	 		//isPermit: function(type, data, ext) {
	 		checkPermit: function(type, data, ext) {
	 			var permissionType = data.permissionType || this.permissionType || "";
	 			var permissions = this.permissions || [];
 				var model = data.type || data.model || this.model || "";
 				var permission = "";
	 			if (type == 'btn') {
					permission = model + ":" + ext.id;
		 		} else if (type == 'field') {
	 				permission = model + "." + data.field + "." + this.isCreate ? "add" : "update";
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
						var $submit = $form.find("button[data-btn-type='save']");
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
	 		},
	 		checkDateRangePicker: function(event) {
	 			var _this = this;
	 			if ($.fn && $.fn.daterangepicker) {
	 				return true;
	 			} else {
	 				var $head = $('head');
	 				var cssUrl = basePath + "/static/plugins/daterangepicker/daterangepicker.css";
	 			    if($("link[href='"+cssUrl+"']").length==0){
	 			        $("<link>")
	 			          .appendTo($head)                               // *注意*：一定要先添加到DOM树中
	 			          .attr({type : 'text/css', rel : 'stylesheet'})  // 然后再设置href属性，否则在IE下可能
	 			          .attr('href', cssUrl);                          // 该css文件不生效
	 			    };
	 				$.when(
					    $.getScript(basePath + "/static/plugins/daterangepicker/moment.min.js"),
					    $.getScript(basePath + "/static/plugins/daterangepicker/daterangepicker.js") ,
					    $.Deferred(function(deferred){
					    	$(deferred.resolve);
					    })).done(function(a, b, c){
					    	_this.dateRangePicker(event);
					    }).fail(function(a, b, c) { 
					    	console.log(a, b, c)
					    }); 
	 				return false;
	 			}
	 		},
	 		dateRangePicker: function(event) {
	 			if (!this.checkDateRangePicker(event)) {
	 				return;
	 			}
	 			var target = event.currentTarget;
	 			var startDate = $(".daterange-input-start", this.element).val();
	 			var endDate = $(".daterange-input-end", this.element).val();
	 			startDate = startDate || $(target).data('startDate') || undefined;
	 			endDate = endDate || $(target).data('endDate') || undefined;
//	 			var startDate = $(target).data('startDate') || undefined;
//	 			var endDate = $(target).data('endDate') || undefined;
	 			var minDate = $(target).data('minDate') || "2010-01-01";
	 			var maxDate = $(target).data('maxDate') || undefined;
	 			var format = $(target).data('format') || 'YYYY-MM-DD';
	 			var value = target.value;
	 			
	 			minDate = moment(minDate, minDate ? format : null);
	 			maxDate = moment(maxDate, maxDate ? format : null);
	 			var currentDate = moment();
	 			var daterangepickerCallback = function (start, end, label) {
	 		        if (label == '不限') {
	 		        	$('.daterange-span', this.element).html("不限");
	 		            $(".daterange-input-start", this.element).val("");
	 		            $(".daterange-input-end", this.element).val("");
	 		        } else {
	 		            $('.daterange-span', this.element).html(start.format(format) + ' - ' + end.format(format));
	 		            $(".daterange-input-start", this.element).val(start.format(format));
	 		            //$(".daterange-input-end", this.element).val(end.add(1, "days").format("YYYY-MM-DD"));
	 		            $(".daterange-input-end", this.element).val(end.format(format)+" 23:59:59");
	 		        }
	 		    }
	 			$(target).daterangepicker({
	 				locale: {
	 					format : format || 'YYYY-MM-DD',
	 					applyLabel: '确认',
	 					cancelLabel: '取消',
	 					fromLabel : '起始时间',
	 					toLabel : '结束时间',
	 					customRangeLabel : '自定义',
	 					firstDay : 1,
	 					daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],  
	 		            monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月',  
	 		                    '七月', '八月', '九月', '十月', '十一月', '十二月' ], 
	 				},
	 				ranges: {
	 					'不限': [minDate, maxDate],
	 					'今天': [currentDate, currentDate],
	 					'昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
	 					'本周': [moment().startOf('week').add('1', 'day'), moment().endOf('week').add('1', 'day')],
	 					'上周': [moment().subtract(1, 'week').startOf('week').add('1', 'day'), moment().subtract(1, 'week').endOf('week').add('1', 'day')],
//	 					'最近7天': [moment().subtract(6, 'days'), currentDate],
//	 					'最近30天': [moment().subtract(29, 'days'), currentDate],
	 					'这个月': [moment().startOf('month'), moment().endOf('month')],
	 					'上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
	 				},
	 				opens: "left",
	 				linkedCalendars: false,
	 				showDropdowns: true,
	 				startDate: startDate ? moment(startDate, startDate ? format : null) : minDate,
	 				endDate: endDate ? moment(endDate, endDate ? format : null) : maxDate,
	 				minDate: minDate,
	 				maxDate: maxDate,
	 		    }, daterangepickerCallback);
	 			
	 			var picker = $(target).data("daterangepicker");
	 			if (picker) {
		 			picker.calculateChosenLabel();
		 			daterangepickerCallback.call(picker, picker.startDate, picker.endDate, picker.chosenLabel);
	 			}
	 			console.log($(target).data("daterangepicker"));
	 			$(target).addClass("daterange-inited");
	 		},
	 		checkDistPicker: function(event) {
	 			var _this = this;
	 			if ($.fn && $.fn.distpicker) {
	 				return true;
	 			} else {
	 				$.when(
					    $.getScript(basePath + "/static/plugins/distpicker/distpicker.min.js"),
					    $.Deferred(function(deferred){
					    	$(deferred.resolve);
					    })).done(function(a, b){
					    	_this.distpicker(event);
					    }).fail(function(a, b) { 
					    	console.log(a, b)
					    });
	 				return false;
	 			}
	 		},
	 		distpicker: function(event) {
	 			if (!this.checkDistPicker(event)) {
	 				return;
	 			}
	 			var target = event.currentTarget;
	 			var autoSelect = $(target).data("autoSelect");
	 			var placeholder = $(target).data("placeholder");
	 			var province = $(target).data("province");
	 			var city = $(target).data("city");
	 			var district = $(target).data("district");
	 			$(target).distpicker({
	 				placeholder: placeholder,
	 				autoSelect: autoSelect,
	 				province: province,
	 				city: city,
	 				district: district,
	 			});
	 			$(target).find("select").each(function() {
	 				$(this).val($(this).data("selected") || $(this).val()).trigger("change");
	 			});
	 			$(target).addClass("distpicker-inited");
	 		},
	 		refreshInput(triggerSelectChange) {
	 			var _this = this;
				var id = this.field.cssId || this.field.field;
				var $el = this.$el;
				var $container = this.$root.$el;
				if (this.field.type == 'range') {
					var $field = $("#" + id, $container);
					if ($field.length) {
						_this.rangeChange({currentTarget: $field[0]});
					} else {
						$("input[type='range']").not(".range-inited").each(function(index, item) {
							_this.rangeChange({currentTarget: item});
						})
					}
				} else if (this.field.type == 'daterange') {
					var $field = $("#" + id, $container);
					if ($field.length) {
						_this.dateRangePicker({currentTarget: $field[0]});
					} else {
						$(".daterange-btn").not(".daterange-inited").each(function(index, item) {
							_this.dateRangePicker({currentTarget: item});
						})
					}
				} else if (this.field.type == 'distpicker') {
					var $field = $("#" + id, $container);
					if ($field.length) {
						_this.distpicker({currentTarget: $field[0]});
					} else {
						$(".distpicker").not(".distpicker-inited").each(function(index, item) {
							_this.distpicker({currentTarget: item});
						})
					}
				} else {
					var $from = $($el).parents("form:first");
				    var baseForm = $from.data("baseForm") || {};
				    if (typeof baseForm.fillElemValue == 'function') {
				    	var $inputs = $($el).find('input[name], select[name], textarea[name], label[name]');
				    	$inputs = $inputs.length ? $inputs : $el;
				    	if ($inputs.length == 1) {
				    		baseForm.fillElemValue($inputs[0], this.fieldValue);
				    	}
				    }
//					if (triggerSelectChange && (this.field.type == 'select' || this.field.type == 'urlSelector')) {
//						var $field = $("#" + id, $container);
//						var field = this.field;
//						var multiple = field.extData.multiple || (field.extData['select2-config'] || {}).multiple || false;
//						var value = this.fieldValue || '';
//						if (multiple) {
//							value = value.split(",");
//						}
//						$field.val(value);
//						$field.trigger("change");
//					}
				}
				// 初始化事件
				if (this.field.extData && this.field.extData.events) {
					var $field = $el.id == id ? $($el) : $("#" + id, $el);
					$field = $field.length ? $field : $("#" + id, $container);
					if ($field.length) {
						try {
							var events = this.field.extData.events;
							for ( var key in events) {
								var event = events[key];
								$field.on(key, eval("("+ event + ")"));
							}
						} catch(e) {}
					}
				}
	 		}
	 	}
	};
