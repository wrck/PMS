<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<template v-for="field in formFieldList">
	<template v-if="field.searchable">
		<template v-if="isPermit(field)">
			<template v-if="field.type == 'hidden'">
				<input :id="field.cssId || field.field" type="hidden" data-type="search" class="form-control flex-grow-2" :class="field.cssClass" :name="field.field" :data-alias="field.alias"
						:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
						:disabled="field.disabled" :readonly="field.readonly"
			>
			</template>
			<template v-else-if="field.type == 'textarea'">
				<div class="form-group display-flex" :class="getGroupClass(field) || groupTextareaClass">
					<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.title}}</label>
					<textarea :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
							:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle" rows="3" style="resize:none;" draggable="false"
							:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
							></textarea>
				</div>
			</template>
			<template v-else-if="field.type == 'date'">
				<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
					<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.title}}</label>
					<input :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
							:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
							data-flag="datepicker" :data-format="field.extData.format || field.render" autocomplete="off"
							:data-start-date="field.extData.startDate" :data-end-date="field.extData.endDate"
							:data-max-date="field.extData.maxDate" :data-min-date="field.extData.minDate"
							:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
					>
				</div>
			</template>
			<template v-else-if="field.type == 'datetime'">
				<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
					<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.title}}</label>
					<input :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
							:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle"
							data-flag="datetimepicker" data-format="field.extData.format || field.render" autocomplete="off"
							:data-start-date="field.extData.startDate" :data-end-date="field.extData.endDate"
							:data-max-date="field.extData.maxDate" :data-min-date="field.extData.minDate"
							:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
					>
				</div>
			</template>
			<template v-else-if="field.type == 'daterange'">
				<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
					<label :for="field.cssId || field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.title}}</label>
					<button type="button" class="btn border-gray daterange-btn form-control" :id="(field.cssId || field.field) + 'DaterangeBtn'"
						:data-start-date="field.extData.startDate" :data-end-date="field.extData.endDate"
						:data-max-date="field.extData.maxDate" :data-min-date="field.extData.minDate"
						:data-format="field.extData.format || field.render"
					>
		                <i class="fa fa-calendar mr-0.5"></i>&nbsp;
		                <span class="daterange-span">不限</span>
		                <i class="fa fa-caret-down"></i>
		                <input :id="input.cssId || input.field" v-for="(input, index) in field.inputs" type="hidden" class="form-control flex-grow-2" :class="'daterange-input-' + (index%2 ? 'end' : 'start')" :class="getSelfClass(input) || input.cssClass" :name="input.field" :data-alias="input.alias" data-type="search"
							:value="getFieldValue(input)" :placeholder="input.title || input.name" :style="input.cssStyle" 
							:disabled="input.disabled" :readonly="input.readonly" :required="input.required" autocomplete="off">
	                </button>
				</div>
			</template>
			<template v-else-if="field.type == 'select'">
				<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
					<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.title}}</label>
					<select :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
							:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="'width:100%;' + (field.cssStyle || '')"
							:disabled="field.disabled || field.readonly" :readonly="field.readonly" :required="field.required"
							:data-src-data="field.extData['src-data']" :data-text="field.extKey" :data-value="field.extValue" :data-store-source="field.extData['store-source']"
							:data-blank="field.extData.blank || false" :data-blank-value="field.extData['blank-value']" :data-blank-text="field.extData['blank-text']"
							:data-select2-config="JSON.stringify(field.extData['select2-config'])">
						<template v-if="((field.extData || {}).data || field.extData).length">
							<option :value="item[field.extValue]" v-for="item in ((field.extData || {}).data || field.extData)" :selected="item[field.extValue] == getFieldValue(field)" >{{item[field.extKey]}}</option>
						</template>
						<template v-else-if="field.extData.blank">
							<option blank="true" :value="field.extData['blank-value']">{{field.extData['blank-text']}}</option>
						</template>
					</select>
				</div>
			</template>
			<template v-else-if="field.type == 'urlSelector'">
				<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
					<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.title}}</label>
					<select :id="field.cssId || field.field" type="search" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
							:value="getFieldValue(field)" :data-selected="getFieldValue(field)" :placeholder="field.title || field.name" :style="'width:100%;' + (field.cssStyle || '')"
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
					<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.title}}</label>
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
					<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.title}}</label>
					<input :id="input.cssId || input.field" v-for="input in field.inputs" :type="dataType == 'table' && input.searchable ? 'search' : 'text'" class="form-control flex-grow-2" :class="getSelfClass(input) || input.cssClass" :name="input.field" :data-alias="input.alias"
							:value="getFieldValue(input)" :placeholder="input.title || input.name" :style="input.cssStyle" 
							:disabled="input.disabled" :readonly="input.readonly" :required="input.required" autocomplete="off">
				</div>
			</template>
			<template v-else-if="field.type == 'range'">
				<div class="form-group display-flex" :class="getGroupClass(field) || groupClass">
					<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.title}}</label>
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
					<label :for="field.field" style="text-align: right;" class="control-label flex-shrink-0" :style="{width: maxLabelWidth}">{{field.title}}</label>
					<input :id="field.cssId || field.field" :type="dataType == 'table' && field.searchable ? 'search' : field.type" class="form-control flex-grow-2" :class="getSelfClass(field) || field.cssClass" :name="field.field" :data-alias="field.alias"
							:value="getFieldValue(field)" :placeholder="field.title || field.name" :style="field.cssStyle" 
							:disabled="field.disabled" :readonly="field.readonly" :required="field.required" autocomplete="off"
							:defaultValue="(field.extData || {})['defaultValue']" :max="(field.extData || {})['max']" :min="(field.extData || {})['min']" :step="(field.extData || {})['step']"
							>
				</div>
			</template>
		</template>
	</template>
</template>
<script type="text/javascript">
	var formVueConfig = {
		el: "#app",
		data: {
			formCols: 4,
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
			targetValue: {},
			model: "",
			permissionType: "",
			permissions: [],
			roles: [],
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
			var $container = this.$root.$el;
			$("input[type='range']", $container).not(".daterange-inited").each(function(index, item) {
				_this.rangeChange({currentTarget: item});
			});
			$(".daterange-btn", $container).not(".daterange-inited").each(function(index, item) {
				_this.dateRangePicker({currentTarget: item});
			});
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
					} else {
						field["extData"] = '';
					}
					if (field.type == 'inputs' || field.type == 'daterange') {
						// inputs 拥有相同的标签，在一个组内进行显示，以下参数需要拆分，用空格相隔
						var keys = ['alias', 'name', 'title', 'titleKey', 'cssId', 'cssClass', 'cssStyle'];
						var mutliField = this.parseValue(field, 'field', false) || [];
						typeof mutliField == 'string' && (mutliField = mutliField.split(" "));
						//var mutliField = (field.field || "").split(" ");
						// 如果是时间范围选择，并且单字段，则拆分为后缀Start，End的两个字段
						if (field.type == 'daterange' && mutliField.length == 1) {
							var prefix = mutliField[0];
							mutliField = [prefix + "Start", prefix + "End"];
							field.field = mutliField.join(" ");
						}
						var inputs = [];
						for(var i in mutliField) {
							var input = $.extend({}, field);
							input['field'] = mutliField[i];
							for(var k in keys) {
								var key = keys[k];
								//var values = (field[key] || "").split(" ");
								var values = this.parseValue(field, key, false) || [];
								if(key == 'cssClass') {
									values = values['selfClass'] || values;
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
	 		isPermit:function(field) {
				field = field || this.field || {};
				/*var permissionType = this.permissionType || "";
	 			var permissions = this.permissions || [];
 				var model = this.model || "";
 				var permission = model + "." + field.field + "." + (this.isCreate ? "add" : "edit");
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
 				try {
	 				value = value || field.extData["defaultValue"];
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
	 				value = this.getDataValue(field[key]);
	 				update = update == false ? false : true; 
	 				if (update) {
	 					field[key] = value || "";
	 				}
	 			} catch(e){}
	 			return value || "";
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
	 			}
	 		},
	 		dateRangePicker: function(event) {
	 			if (!this.checkDateRangePicker(event)) {
	 				return;
	 			}
	 			var target = event.currentTarget;
	 			var startDate = $(target).data('startDate') || undefined;
	 			var endDate = $(target).data('endDate') || undefined;
	 			var minDate = $(target).data('minDate') || "2010-01-01";
	 			var maxDate = $(target).data('maxDate') || undefined;
	 			var format = $(target).data('format') || 'YYYY-MM-DD';
	 			var value = target.value;
	 			
	 			minDate = moment(minDate, minDate ? format : null);
	 			maxDate = moment(maxDate, maxDate ? format : null);
	 			var currentDate = moment();
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
	 		    }, function (start, end, label) {
	 		        if (label == '不限') {
	 		        	$('.daterange-span', this.element).html("不限");
	 		            $(".daterange-input-start", this.element).val("");
	 		            $(".daterange-input-end", this.element).val("");
	 		        } else {
	 		            $('.daterange-span', this.element).html(start.format(format) + ' - ' + end.format(format));
	 		            $(".daterange-input-start", this.element).val(start.format(format));
	 		            //$(".daterange-input-end", this.element).val(end.add(1, "days").format("YYYY-MM-DD"));
	 		            $(".daterange-input-end", this.element).val(end.format('YYYY-MM-DD')+" 23:59:59");
	 		        }
	 		    });
	 			$(target).addClass("daterange-inited");
	 		}
	 	}
	}
</script>