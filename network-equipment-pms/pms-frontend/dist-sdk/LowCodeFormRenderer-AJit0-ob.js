import { m as __exportAll } from "./request-BQrAOfxW.js";
import { i as FieldType, o as LayoutType } from "./lowcode-F-suzo7c.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElCascader, ElCheckboxGroup, ElDatePicker, ElDivider, ElForm, ElFormItem, ElInput, ElInputNumber, ElRadioGroup, ElRate, ElSelect, ElSlider, ElSwitch, ElUpload } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createSlots, createTextVNode, createVNode, defineComponent, markRaw, mergeProps, openBlock, reactive, ref, renderList, resolveComponent, resolveDynamicComponent, toDisplayString, unref, watch, withCtx } from "vue";
//#region src/components/LowCodeFormRenderer/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = {
	key: 1,
	class: "form-title",
	style: { fontSize: "16px" }
};
var _hoisted_2 = { class: "el-upload__tip" };
/** Props 定义 */
var index_vue_vue_type_script_setup_true_lang_default = /*@__PURE__*/ defineComponent({
	__name: "index",
	props: {
		config: {},
		modelValue: {},
		disabled: {
			type: Boolean,
			default: false
		},
		componentRegistry: { default: () => ({}) },
		eventHandlers: { default: () => ({}) }
	},
	emits: [
		"update:modelValue",
		"submit",
		"validate-fail",
		"field-change"
	],
	setup(__props, { expose: __expose, emit: __emit }) {
		/**
		* 低代码表单渲染引擎。
		*
		* <p>根据传入的 {@link FormConfig} 动态渲染 Element Plus 表单，支持：
		* <ul>
		*   <li>grid / tabs / collapse 三种布局</li>
		*   <li>v-model 双向绑定到 modelValue</li>
		*   <li>el-form rules 校验（自动合并 required）</li>
		*   <li>disabled / readonly / hidden 字段</li>
		*   <li>18 种字段类型 + 自定义组件（type=custom 通过 componentRegistry 解析）</li>
		*   <li>change 事件回调（emit + props.eventHandlers）</li>
		* </ul>
		* </p>
		*/
		const props = __props;
		/** Emits 定义 */
		const emit = __emit;
		/** 表单 ref */
		const formRef = ref();
		/**
		* 内部维护的表单数据（响应式）。
		*
		* <p>值为 any 类型：动态表单字段的实际类型由运行时字段类型决定
		* （input → string、number → number、checkbox → array 等），
		* 使用 any 以兼容所有 Element Plus 组件的 v-model 类型签名。</p>
		*/
		const formData = reactive({ ...props.modelValue || {} });
		/**
		* 初始化字段默认值：将 config.fields 中的 defaultValue 写入未提供的字段。
		*/
		function initDefaults() {
			for (const field of props.config.fields || []) if (!(field.prop in formData)) if (field.defaultValue !== void 0 && field.defaultValue !== null) formData[field.prop] = field.defaultValue;
			else if (field.type === FieldType.CHECKBOX) formData[field.prop] = [];
			else formData[field.prop] = "";
		}
		watch(() => props.config, () => initDefaults(), {
			immediate: true,
			deep: false
		});
		watch(() => props.modelValue, (val) => {
			if (!val) return;
			let changed = false;
			for (const key of Object.keys(val)) if (formData[key] !== val[key]) {
				formData[key] = val[key];
				changed = true;
			}
			if (changed) {}
		}, { deep: true });
		watch(formData, (val) => {
			emit("update:modelValue", { ...val });
		}, { deep: true });
		/** 可见字段（过滤 hidden=true） */
		const visibleFields = computed(() => (props.config.fields || []).filter((f) => !f.hidden));
		/**
		* 生成 el-form rules：将 field.required 合并为 required 规则，并合并自定义 rules。
		*/
		const formRules = computed(() => {
			const rules = {};
			for (const field of props.config.fields || []) {
				const list = [];
				if (field.required) list.push({
					required: true,
					message: field.placeholder || `请填写${field.label}`,
					trigger: ["blur", "change"]
				});
				if (field.rules && Array.isArray(field.rules)) for (const r of field.rules) list.push({ ...r });
				if (list.length > 0) rules[field.prop] = list;
			}
			return rules;
		});
		/** 布局配置 */
		const layout = computed(() => props.config.layout || {
			type: LayoutType.GRID,
			gutter: 16
		});
		/**
		* 解析栅格 span 为 el-col 绑定属性。
		*
		* <p>向后兼容：span 为数字或缺省时按 :span= 渲染（缺省 24）；
		* span 为响应式断点对象时按 :xs= :sm= :md= :lg= :xl= 渲染。</p>
		*/
		function colProps(span) {
			if (span === void 0 || typeof span === "number") return { span: span !== null && span !== void 0 ? span : 24 };
			const result = {};
			if (span.xs !== void 0) result.xs = span.xs;
			if (span.sm !== void 0) result.sm = span.sm;
			if (span.md !== void 0) result.md = span.md;
			if (span.lg !== void 0) result.lg = span.lg;
			if (span.xl !== void 0) result.xl = span.xl;
			return result;
		}
		/** tabs 折叠面板激活项 */
		const activeTab = ref("");
		const activeCollapse = ref([]);
		/** 初始化 tabs/collapse 默认激活第一项 */
		watch(layout, (val) => {
			if (val.type === LayoutType.TABS && val.tabs && val.tabs.length > 0 && !activeTab.value) activeTab.value = val.tabs[0].name || val.tabs[0].title;
			if (val.type === LayoutType.COLLAPSE && val.collapse && val.collapse.length > 0) activeCollapse.value = val.collapse.map((c, i) => c.name || String(i));
		}, { immediate: true });
		/**
		* 将字段 id 列表转为字段对象列表。
		*/
		function resolveFields(ids) {
			const map = /* @__PURE__ */ new Map();
			for (const f of props.config.fields || []) map.set(f.id, f);
			return ids.map((id) => map.get(id)).filter((f) => !!f && !f.hidden);
		}
		/**
		* 解析字段对应的渲染组件。
		* - 布局组件（divider/title）返回特殊组件
		* - custom 类型从 componentRegistry 取
		* - 其余返回对应的 Element Plus 组件
		*/
		function resolveComponent$1(field) {
			switch (field.type) {
				case FieldType.INPUT:
				case FieldType.TEXTAREA:
				case FieldType.PASSWORD: return markRaw(ElInput);
				case FieldType.NUMBER: return markRaw(ElInputNumber);
				case FieldType.SELECT:
				case FieldType.RADIO:
				case FieldType.CHECKBOX: return markRaw(ElInput);
				case FieldType.DATE:
				case FieldType.DATETIME:
				case FieldType.DATERANGE: return markRaw(ElDatePicker);
				case FieldType.SWITCH: return markRaw(ElSwitch);
				case FieldType.RATE: return markRaw(ElRate);
				case FieldType.SLIDER: return markRaw(ElSlider);
				case FieldType.CASCADER: return markRaw(ElCascader);
				case FieldType.UPLOAD: return markRaw(ElUpload);
				case FieldType.CUSTOM: {
					var _field$props;
					const name = ((_field$props = field.props) === null || _field$props === void 0 ? void 0 : _field$props.componentName) || "";
					const comp = props.componentRegistry[name];
					if (!comp) {
						console.warn(`[LowCodeFormRenderer] 未注册的自定义组件: ${name}`);
						return markRaw(ElInput);
					}
					return markRaw(comp);
				}
				default: return markRaw(ElInput);
			}
		}
		/** 判断是否为 textarea 类型 */
		function isTextarea(field) {
			return field.type === FieldType.TEXTAREA;
		}
		/** 判断是否为 select 类型 */
		function isSelect(field) {
			return field.type === FieldType.SELECT;
		}
		/** 判断是否为 radio 类型 */
		function isRadio(field) {
			return field.type === FieldType.RADIO;
		}
		/** 判断是否为 checkbox 类型 */
		function isCheckbox(field) {
			return field.type === FieldType.CHECKBOX;
		}
		/** 获取日期选择器的 type 属性 */
		function dateType(field) {
			if (field.type === FieldType.DATETIME) return "datetime";
			if (field.type === FieldType.DATERANGE) return "daterange";
			return "date";
		}
		/** 字段 change 事件处理 */
		function handleFieldChange(field, value) {
			var _field$events;
			emit("field-change", field, value);
			const handlerName = (_field$events = field.events) === null || _field$events === void 0 ? void 0 : _field$events.change;
			if (handlerName && props.eventHandlers[handlerName]) props.eventHandlers[handlerName](value, field, formData);
		}
		/** 表单校验 */
		async function validate() {
			if (!formRef.value) return false;
			try {
				await formRef.value.validate();
				return true;
			} catch (errors) {
				emit("validate-fail", errors);
				return false;
			}
		}
		/** 提交表单：先校验，通过后 emit submit */
		async function submit() {
			if (await validate()) emit("submit", { ...formData });
		}
		/** 重置表单到初始值 */
		function resetFields() {
			var _formRef$value;
			(_formRef$value = formRef.value) === null || _formRef$value === void 0 || _formRef$value.resetFields();
			for (const field of props.config.fields || []) if (field.defaultValue !== void 0) formData[field.prop] = field.defaultValue;
			else formData[field.prop] = field.type === FieldType.CHECKBOX ? [] : "";
		}
		/** 清除校验状态 */
		function clearValidate() {
			var _formRef$value2;
			(_formRef$value2 = formRef.value) === null || _formRef$value2 === void 0 || _formRef$value2.clearValidate();
		}
		__expose({
			validate,
			submit,
			resetFields,
			clearValidate,
			getFormData: () => ({ ...formData }),
			formRef
		});
		return (_ctx, _cache) => {
			var _props$config$labelWi, _props$config$labelPo, _props$config$size;
			const _component_el_option = resolveComponent("el-option");
			const _component_el_radio = resolveComponent("el-radio");
			const _component_el_checkbox = resolveComponent("el-checkbox");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_tab_pane = resolveComponent("el-tab-pane");
			const _component_el_tabs = resolveComponent("el-tabs");
			const _component_el_collapse_item = resolveComponent("el-collapse-item");
			const _component_el_collapse = resolveComponent("el-collapse");
			return openBlock(), createBlock(unref(ElForm), {
				ref_key: "formRef",
				ref: formRef,
				model: formData,
				rules: formRules.value,
				"label-width": (_props$config$labelWi = __props.config.labelWidth) !== null && _props$config$labelWi !== void 0 ? _props$config$labelWi : 100,
				"label-position": (_props$config$labelPo = __props.config.labelPosition) !== null && _props$config$labelPo !== void 0 ? _props$config$labelPo : "right",
				size: (_props$config$size = __props.config.size) !== null && _props$config$size !== void 0 ? _props$config$size : "default",
				disabled: __props.disabled,
				class: "low-code-form-renderer"
			}, {
				default: withCtx(() => {
					var _layout$value$gutter;
					return [!layout.value.type || layout.value.type === "grid" ? (openBlock(), createBlock(_component_el_row, {
						key: 0,
						gutter: (_layout$value$gutter = layout.value.gutter) !== null && _layout$value$gutter !== void 0 ? _layout$value$gutter : 16
					}, {
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(visibleFields.value, (field) => {
							return openBlock(), createBlock(_component_el_col, mergeProps({ key: field.id }, { ref_for: true }, colProps(field.span)), {
								default: withCtx(() => {
									var _field$props2, _field$props3;
									return [field.type === "divider" ? (openBlock(), createBlock(unref(ElDivider), {
										key: 0,
										"content-position": ((_field$props2 = field.props) === null || _field$props2 === void 0 ? void 0 : _field$props2.contentPosition) || "center",
										"border-style": ((_field$props3 = field.props) === null || _field$props3 === void 0 ? void 0 : _field$props3.borderStyle) || "solid"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(field.label), 1)]),
										_: 2
									}, 1032, ["content-position", "border-style"])) : field.type === "title" ? (openBlock(), createElementBlock("h3", _hoisted_1, toDisplayString(field.label), 1)) : (openBlock(), createBlock(unref(ElFormItem), {
										key: 2,
										label: field.label,
										prop: field.prop
									}, {
										default: withCtx(() => {
											var _field$props$rows, _field$props4, _field$props$multiple, _field$props5, _field$props$filterab, _field$props6, _field$props10, _field$props11, _field$props12, _field$props13, _field$props14, _field$props$multiple2, _field$props15, _field$props16, _field$props17;
											return [isTextarea(field) ? (openBlock(), createBlock(unref(ElInput), mergeProps({
												key: 0,
												modelValue: formData[field.prop],
												"onUpdate:modelValue": ($event) => formData[field.prop] = $event,
												type: "textarea",
												placeholder: field.placeholder,
												disabled: field.disabled,
												readonly: field.readonly,
												clearable: field.clearable,
												rows: (_field$props$rows = (_field$props4 = field.props) === null || _field$props4 === void 0 ? void 0 : _field$props4.rows) !== null && _field$props$rows !== void 0 ? _field$props$rows : 3
											}, { ref_for: true }, field.props || {}, { onChange: (val) => handleFieldChange(field, val) }), null, 16, [
												"modelValue",
												"onUpdate:modelValue",
												"placeholder",
												"disabled",
												"readonly",
												"clearable",
												"rows",
												"onChange"
											])) : isSelect(field) ? (openBlock(), createBlock(unref(ElSelect), mergeProps({
												key: 1,
												modelValue: formData[field.prop],
												"onUpdate:modelValue": ($event) => formData[field.prop] = $event,
												placeholder: field.placeholder,
												disabled: field.disabled,
												clearable: field.clearable,
												multiple: (_field$props$multiple = (_field$props5 = field.props) === null || _field$props5 === void 0 ? void 0 : _field$props5.multiple) !== null && _field$props$multiple !== void 0 ? _field$props$multiple : false,
												filterable: (_field$props$filterab = (_field$props6 = field.props) === null || _field$props6 === void 0 ? void 0 : _field$props6.filterable) !== null && _field$props$filterab !== void 0 ? _field$props$filterab : false
											}, { ref_for: true }, field.props || {}, { onChange: (val) => handleFieldChange(field, val) }), {
												default: withCtx(() => {
													var _field$props7;
													return [(openBlock(true), createElementBlock(Fragment, null, renderList(((_field$props7 = field.props) === null || _field$props7 === void 0 ? void 0 : _field$props7.options) || [], (opt) => {
														return openBlock(), createBlock(_component_el_option, {
															key: String(opt.value),
															label: opt.label,
															value: opt.value
														}, null, 8, ["label", "value"]);
													}), 128))];
												}),
												_: 2
											}, 1040, [
												"modelValue",
												"onUpdate:modelValue",
												"placeholder",
												"disabled",
												"clearable",
												"multiple",
												"filterable",
												"onChange"
											])) : isRadio(field) ? (openBlock(), createBlock(unref(ElRadioGroup), {
												key: 2,
												modelValue: formData[field.prop],
												"onUpdate:modelValue": ($event) => formData[field.prop] = $event,
												disabled: field.disabled,
												onChange: (val) => handleFieldChange(field, val)
											}, {
												default: withCtx(() => {
													var _field$props8;
													return [(openBlock(true), createElementBlock(Fragment, null, renderList(((_field$props8 = field.props) === null || _field$props8 === void 0 ? void 0 : _field$props8.options) || [], (opt) => {
														return openBlock(), createBlock(_component_el_radio, {
															key: String(opt.value),
															value: opt.value
														}, {
															default: withCtx(() => [createTextVNode(toDisplayString(opt.label), 1)]),
															_: 2
														}, 1032, ["value"]);
													}), 128))];
												}),
												_: 2
											}, 1032, [
												"modelValue",
												"onUpdate:modelValue",
												"disabled",
												"onChange"
											])) : isCheckbox(field) ? (openBlock(), createBlock(unref(ElCheckboxGroup), {
												key: 3,
												modelValue: formData[field.prop],
												"onUpdate:modelValue": ($event) => formData[field.prop] = $event,
												disabled: field.disabled,
												onChange: (val) => handleFieldChange(field, val)
											}, {
												default: withCtx(() => {
													var _field$props9;
													return [(openBlock(true), createElementBlock(Fragment, null, renderList(((_field$props9 = field.props) === null || _field$props9 === void 0 ? void 0 : _field$props9.options) || [], (opt) => {
														return openBlock(), createBlock(_component_el_checkbox, {
															key: String(opt.value),
															value: opt.value
														}, {
															default: withCtx(() => [createTextVNode(toDisplayString(opt.label), 1)]),
															_: 2
														}, 1032, ["value"]);
													}), 128))];
												}),
												_: 2
											}, 1032, [
												"modelValue",
												"onUpdate:modelValue",
												"disabled",
												"onChange"
											])) : field.type === "date" || field.type === "datetime" || field.type === "daterange" ? (openBlock(), createBlock(unref(ElDatePicker), mergeProps({
												key: 4,
												modelValue: formData[field.prop],
												"onUpdate:modelValue": ($event) => formData[field.prop] = $event,
												type: dateType(field),
												placeholder: field.placeholder,
												disabled: field.disabled,
												readonly: field.readonly,
												clearable: field.clearable,
												format: ((_field$props10 = field.props) === null || _field$props10 === void 0 ? void 0 : _field$props10.format) || void 0,
												"value-format": ((_field$props11 = field.props) === null || _field$props11 === void 0 ? void 0 : _field$props11.valueFormat) || void 0
											}, { ref_for: true }, field.props || {}, { onChange: (val) => handleFieldChange(field, val) }), null, 16, [
												"modelValue",
												"onUpdate:modelValue",
												"type",
												"placeholder",
												"disabled",
												"readonly",
												"clearable",
												"format",
												"value-format",
												"onChange"
											])) : field.type === "upload" ? (openBlock(), createBlock(unref(ElUpload), {
												key: 5,
												action: ((_field$props12 = field.props) === null || _field$props12 === void 0 ? void 0 : _field$props12.action) || "/api/file/upload",
												limit: ((_field$props13 = field.props) === null || _field$props13 === void 0 ? void 0 : _field$props13.limit) || 5,
												accept: ((_field$props14 = field.props) === null || _field$props14 === void 0 ? void 0 : _field$props14.accept) || "",
												multiple: (_field$props$multiple2 = (_field$props15 = field.props) === null || _field$props15 === void 0 ? void 0 : _field$props15.multiple) !== null && _field$props$multiple2 !== void 0 ? _field$props$multiple2 : false,
												"list-type": ((_field$props16 = field.props) === null || _field$props16 === void 0 ? void 0 : _field$props16.listType) || "text",
												disabled: field.disabled
											}, createSlots({
												default: withCtx(() => [createVNode(_component_el_button, {
													type: "primary",
													disabled: field.disabled
												}, {
													default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("点击上传", -1)])]),
													_: 1
												}, 8, ["disabled"])]),
												_: 2
											}, [((_field$props17 = field.props) === null || _field$props17 === void 0 ? void 0 : _field$props17.tip) ? {
												name: "tip",
												fn: withCtx(() => [createElementVNode("div", _hoisted_2, toDisplayString(field.props.tip), 1)]),
												key: "0"
											} : void 0]), 1032, [
												"action",
												"limit",
												"accept",
												"multiple",
												"list-type",
												"disabled"
											])) : field.type === "custom" ? (openBlock(), createBlock(resolveDynamicComponent(resolveComponent$1(field)), mergeProps({
												key: 6,
												modelValue: formData[field.prop],
												"onUpdate:modelValue": ($event) => formData[field.prop] = $event,
												field,
												disabled: field.disabled
											}, { ref_for: true }, field.props || {}, { onChange: (val) => handleFieldChange(field, val) }), null, 16, [
												"modelValue",
												"onUpdate:modelValue",
												"field",
												"disabled",
												"onChange"
											])) : (openBlock(), createBlock(resolveDynamicComponent(resolveComponent$1(field)), mergeProps({
												key: 7,
												modelValue: formData[field.prop],
												"onUpdate:modelValue": ($event) => formData[field.prop] = $event,
												placeholder: field.placeholder,
												disabled: field.disabled,
												readonly: field.readonly,
												clearable: field.clearable
											}, { ref_for: true }, field.props || {}, { onChange: (val) => handleFieldChange(field, val) }), null, 16, [
												"modelValue",
												"onUpdate:modelValue",
												"placeholder",
												"disabled",
												"readonly",
												"clearable",
												"onChange"
											]))];
										}),
										_: 2
									}, 1032, ["label", "prop"]))];
								}),
								_: 2
							}, 1040);
						}), 128))]),
						_: 1
					}, 8, ["gutter"])) : layout.value.type === "tabs" ? (openBlock(), createBlock(_component_el_tabs, {
						key: 1,
						modelValue: activeTab.value,
						"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => activeTab.value = $event)
					}, {
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(layout.value.tabs || [], (tab, idx) => {
							return openBlock(), createBlock(_component_el_tab_pane, {
								key: idx,
								label: tab.title,
								name: tab.name || tab.title
							}, {
								default: withCtx(() => {
									var _layout$value$gutter2;
									return [createVNode(_component_el_row, { gutter: (_layout$value$gutter2 = layout.value.gutter) !== null && _layout$value$gutter2 !== void 0 ? _layout$value$gutter2 : 16 }, {
										default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(resolveFields(tab.fields), (field) => {
											return openBlock(), createBlock(_component_el_col, mergeProps({ key: field.id }, { ref_for: true }, colProps(field.span)), {
												default: withCtx(() => [createVNode(unref(ElFormItem), {
													label: field.label,
													prop: field.prop
												}, {
													default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(resolveComponent$1(field)), mergeProps({
														modelValue: formData[field.prop],
														"onUpdate:modelValue": ($event) => formData[field.prop] = $event,
														placeholder: field.placeholder,
														disabled: field.disabled,
														readonly: field.readonly,
														clearable: field.clearable
													}, { ref_for: true }, field.props || {}, { onChange: (val) => handleFieldChange(field, val) }), null, 16, [
														"modelValue",
														"onUpdate:modelValue",
														"placeholder",
														"disabled",
														"readonly",
														"clearable",
														"onChange"
													]))]),
													_: 2
												}, 1032, ["label", "prop"])]),
												_: 2
											}, 1040);
										}), 128))]),
										_: 2
									}, 1032, ["gutter"])];
								}),
								_: 2
							}, 1032, ["label", "name"]);
						}), 128))]),
						_: 1
					}, 8, ["modelValue"])) : layout.value.type === "collapse" ? (openBlock(), createBlock(_component_el_collapse, {
						key: 2,
						modelValue: activeCollapse.value,
						"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => activeCollapse.value = $event)
					}, {
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(layout.value.collapse || [], (group, idx) => {
							return openBlock(), createBlock(_component_el_collapse_item, {
								key: idx,
								title: group.title,
								name: group.name || String(idx)
							}, {
								default: withCtx(() => {
									var _layout$value$gutter3;
									return [createVNode(_component_el_row, { gutter: (_layout$value$gutter3 = layout.value.gutter) !== null && _layout$value$gutter3 !== void 0 ? _layout$value$gutter3 : 16 }, {
										default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(resolveFields(group.fields), (field) => {
											return openBlock(), createBlock(_component_el_col, mergeProps({ key: field.id }, { ref_for: true }, colProps(field.span)), {
												default: withCtx(() => [createVNode(unref(ElFormItem), {
													label: field.label,
													prop: field.prop
												}, {
													default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(resolveComponent$1(field)), mergeProps({
														modelValue: formData[field.prop],
														"onUpdate:modelValue": ($event) => formData[field.prop] = $event,
														placeholder: field.placeholder,
														disabled: field.disabled,
														readonly: field.readonly,
														clearable: field.clearable
													}, { ref_for: true }, field.props || {}, { onChange: (val) => handleFieldChange(field, val) }), null, 16, [
														"modelValue",
														"onUpdate:modelValue",
														"placeholder",
														"disabled",
														"readonly",
														"clearable",
														"onChange"
													]))]),
													_: 2
												}, 1032, ["label", "prop"])]),
												_: 2
											}, 1040);
										}), 128))]),
										_: 2
									}, 1032, ["gutter"])];
								}),
								_: 2
							}, 1032, ["title", "name"]);
						}), 128))]),
						_: 1
					}, 8, ["modelValue"])) : createCommentVNode("", true)];
				}),
				_: 1
			}, 8, [
				"model",
				"rules",
				"label-width",
				"label-position",
				"size",
				"disabled"
			]);
		};
	}
});
//#endregion
//#region src/components/LowCodeFormRenderer/index.vue
var LowCodeFormRenderer_exports = /* @__PURE__ */ __exportAll({ default: () => LowCodeFormRenderer_default });
var LowCodeFormRenderer_default = /*#__PURE__*/ _plugin_vue_export_helper_default(index_vue_vue_type_script_setup_true_lang_default, [["__scopeId", "data-v-31141696"]]);
//#endregion
export { LowCodeFormRenderer_exports as n, LowCodeFormRenderer_default as t };

//# sourceMappingURL=LowCodeFormRenderer-AJit0-ob.js.map