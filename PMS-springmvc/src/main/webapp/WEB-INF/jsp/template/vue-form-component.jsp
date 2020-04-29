<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<template v-for="field in formFieldList">
	<template v-if="field.type == 'hidden' || !field.visible">
		<input :id="field.cssId || field.field" type="hidden" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
				:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
				:disabled="field.disabled" :readonly="field.readonly"
		>
	</template>
	<template v-else-if="field.type == 'textarea'">
		<div class="form-group display-flex" :class="getGroupClass(field) || groupTextareaClass">
			<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
			<textarea :id="field.cssId || field.field" :type="field.type" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
					:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle" rows="3" style="resize:none;" draggable="false"
					:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
					></textarea>
		</div>
	</template>
	<template v-else-if="field.type == 'date'">
		<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
			<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
			<input :id="field.cssId || field.field" type="text" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
					:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
					data-flag="datepicker" :data-format="field.render" autocomplete="off"
					:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
			>
		</div>
	</template>
	<template v-else-if="field.type == 'datetime'">
		<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
			<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
			<input :id="field.cssId || field.field" type="text" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
					:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
					data-flag="datetimepicker" data-format="field.render" autocomplete="off"
					:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
			>
		</div>
	</template>
	<template v-else-if="field.type == 'select'">
		<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
			<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
			<select :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
					:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
					:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required">
				<option :value="item[field.extValue]" v-for="item in getDataValue(field.extData)" :selected="item[field.extValue] == getFieldValue(field)" >{{item[field.extKey]}}</option>
			</select>
		</div>
	</template>
	<template v-else-if="field.type == 'urlSelector'">
		<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
			<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
			<select :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
					:value="getFieldValue(field)" :data-selected="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
					:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
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
			<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
			<input :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
					:value="getFieldValue(field)" :data-selected="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
					:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
					:data-flag="field.type" :data-src="(parseValue(field, 'extData') || {}).src" :data-autoload="field.extData['autoload']"
					:data-src-data="field.extData['src-data']" :data-text="field.extKey" :data-value="field.extValue" :data-store-source="field.extData['store-source']"
					:data-autocomplete-config="JSON.stringify(field.extData['autocomplete-config'])"
			/>
		</div>
	</template>
	<template v-else-if="field.type == 'inputs'">
		<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
			<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
			<input :id="input.cssId || input.field" v-for="input in field.inputs" :type="dataType == 'table' && input.searchable ? 'search' : 'text'" class="form-control flex-grow-2" :class="getSelfClass(input) || input.cssClass" :name="input.field" :data-alias="input.alias"
					:value="getFieldValue(input)" :placeholder="input.title || input.name" :style="input.cssStyle" 
					:disabled="input.disabled" :readonly="input.readonly" :required="input.required" autocomplete="off">
		</div>
	</template>
	<template v-else-if="field.type == 'range'">
		<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
			<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
			<div class="flex-grow-2 range-slider form-control no-border pl-0 pr-0">
				<input :id="field.cssId || field.field" :type="dataType == 'table' && field.searchable ? 'search' : field.type" class="progress-bar-striped" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
						:value="getFieldValue(field) || 0" :placeholder="field.title || field.name" :style="field.cssStyle" @input="rangeChange($event, field)" @change="rangeChange($event, field)"
						:defaultValue="parseValue(field, 'extData').defaultValue" :max="field.extData['max']" :min="field.extData['min']" :step="field.extData['step']"
						:disabled="field.disabled" :readonly="field.readonly" :required="field.required" autocomplete="off">
				<small class="range-slider-tip">{{getFieldValue(field) || field.extData['min'] || 0}}</small>
			</div>
		</div>
	</template>
	<template v-else>
		<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
			<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
			<input :id="field.cssId || field.field" :type="dataType == 'table' && field.searchable ? 'search' : field.type" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
					:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle" 
					:disabled="field.disabled" :readonly="field.readonly" :required="field.required" autocomplete="off">
		</div>
	</template>
</template>
<script type="text/javascript">
	var formVueConfig = {
		el: "#app",
		data: {
			formCols: 2,
			formColsGroupClass: {
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
		    },
			//formGroupClass: formColsGroupClass[4].formGroupClass,
			//formGroupTextareaClass: formColsGroupClass[4].formGroupTextareaClass,
			fieldList: [],
			dataType: "table",
			targetName: "",
			targetValue: {}
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
	 		}
	 	}
	}
</script>