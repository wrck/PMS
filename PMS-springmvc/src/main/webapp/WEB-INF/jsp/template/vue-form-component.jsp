<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<template v-for="field in fieldList">
	<template v-if="field.type == 'hidden' || !field.visible">
		<input :id="field.cssId || field.field" type="hidden" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
				:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
				:disabled="field.disabled" :readonly="field.readonly"
		>
	</template>
	<template v-else-if="field.type == 'textarea'">
		<div class="form-group display-flex" :class="formGroupTextareaClass">
			<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
			<textarea :id="field.cssId || field.field" :type="field.type" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
					:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle" rows="2" style="resize:none;" draggable="false"
					:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
					></textarea>
		</div>
	</template>
	<template v-else-if="field.type == 'date'">
		<div class="form-group display-flex" :class="formGroupClass">
			<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
			<input :id="field.cssId || field.field" type="text" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
					:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
					data-flag="datepicker" :data-format="field.render" autocomplete="off"
					:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
			>
		</div>
	</template>
	<template v-else-if="field.type == 'datetime'">
		<div class="form-group display-flex" :class="formGroupClass">
			<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
			<input :id="field.cssId || field.field" type="text" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
					:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
					data-flag="datetimepicker" data-format="field.render" autocomplete="off"
					:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
			>
		</div>
	</template>
	<template v-else-if="field.type == 'select'">
		<div class="form-group display-flex" :class="formGroupClass">
			<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
			<select :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
					:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
					:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required">
				<option :value="item[field.extValue]" v-for="item in getDataValue(field.extData)" :selected="item[field.extValue] == getFieldValue(field)" >{{item[field.extKey]}}</option>
			</select>
		</div>
	</template>
	<template v-else-if="field.type == 'urlSelector'">
		<div class="form-group display-flex" :class="formGroupClass">
			<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
			<select :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
					:value="getFieldValue(field)" :data-selected="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
					:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
					:data-flag="field.type" :data-src="parseValue(field, 'extData').src || field.extData" :data-autoload="field.extData['autoload']"
					:data-src-data="field.extData['src-data']" :data-text="field.extKey" :data-value="field.extValue" :data-store-source="field.extData['store-source']"
					:data-blank="field.extData.blank || false" :data-blank-value="field.extData['blank-value']" :data-blank-text="field.extData['blank-text']"
					:data-select2-config="JSON.stringify(field.extData['select2-config'])"
					>
			</select>
		</div>
	</template>
	<template v-else>
		<div class="form-group display-flex" :class="formGroupClass">
			<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.name}}</label>
			<input :id="field.cssId || field.field" :type="dataType == 'table' && field.searchable ? 'search' : field.type" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
					:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle" 
					:disabled="field.disabled" :readonly="field.readonly" :required="field.required" autocomplete="off">
		</div>
	</template>
</template>
<script type="text/javascript">
	var formVueConfig = {
		el: "#app",
		data: {
			formGroupClass: "",
			fieldList: [],
			dataType: "table",
			targetName: "",
			targetValue: {}
		},
		computed: {
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
	 		getFieldValue: function(field) {
	 			var value;
	 			try {
	 				value = eval("this.targetValue." + field.field);
	 			} catch(e) {}
	 				try {
	 				value = value || eval("this.targetValue." + field.alias);
	 				} catch(e) {}
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
	 		parseValue: function(field, key) {
	 			try {
	 				field[key] = this.getDataValue(field[key]);
	 			} catch(e){}
	 			return field[key];
	 		}
	 	}
	}
</script>