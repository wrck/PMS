import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { n as Graph, t as register } from "./es-0oWOj6Bi.js";
import { a as getEntityList, c as rollbackByBackupId, d as saveRelations, i as getEntityDesign, l as rollbackLastDdl, n as deleteEntity, o as listDdlBackups, r as generateDdl, s as publishEntity, t as checkTableName, u as saveEntityDesign } from "./lowcode-entity-CBgvn79e.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, normalizeClass, onBeforeUnmount, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, shallowRef, toDisplayString, watch, withCtx, withDirectives, withModifiers } from "vue";
//#region src/components/EntityDesigner/IndexPanel.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$3 = { class: "index-panel" };
var _hoisted_2$3 = { class: "panel-toolbar" };
var _hoisted_3$3 = {
	key: 0,
	class: "panel-tip"
};
var _hoisted_4$2 = {
	key: 0,
	class: "index-edit-form"
};
//#endregion
//#region src/components/EntityDesigner/IndexPanel.vue
var IndexPanel_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "IndexPanel",
	props: {
		fields: {},
		modelValue: {}
	},
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		/**
		* 索引配置面板。
		*
		* <p>展示实体的复合索引列表，支持新增/编辑/删除索引。
		* 每个索引包含：索引名 + 多字段选择（el-select multiple）+ 是否唯一。
		* 字段列表从父级 FieldPanel 传入（当前实体的所有字段）。</p>
		*
		* <p>索引数据通过 v-model（indexes）双向绑定，由 entity-designer 持有；
		* 当前为本地维护（内存态），后续可扩展到后端持久化。</p>
		*/
		const props = __props;
		const emit = __emit;
		/** 可选字段选项（字段名 + 类型提示） */
		const fieldOptions = computed(() => props.fields.map((f) => ({
			label: `${f.name} (${f.fieldType})`,
			value: f.name
		})));
		/** 内部可编辑副本（与 modelValue 同步） */
		const indexes = computed({
			get: () => props.modelValue || [],
			set: (v) => emit("update:modelValue", v)
		});
		/** 当前编辑中的索引（新增/编辑共用） */
		const editing = ref(null);
		const editingIndex = ref(-1);
		function startAdd() {
			editing.value = {
				name: "",
				fields: [],
				unique: false
			};
			editingIndex.value = -1;
		}
		function startEdit(idx) {
			const src = indexes.value[idx];
			editing.value = {
				...src,
				fields: [...src.fields]
			};
			editingIndex.value = idx;
		}
		function cancelEdit() {
			editing.value = null;
			editingIndex.value = -1;
		}
		function saveEdit() {
			if (!editing.value) return;
			if (!editing.value.name.trim()) return;
			if (editing.value.fields.length === 0) return;
			const list = [...indexes.value];
			if (editingIndex.value >= 0) list[editingIndex.value] = { ...editing.value };
			else list.push({ ...editing.value });
			indexes.value = list;
			cancelEdit();
		}
		function removeIndex(idx) {
			const list = [...indexes.value];
			list.splice(idx, 1);
			indexes.value = list;
		}
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_switch = resolveComponent("el-switch");
			const _component_el_form = resolveComponent("el-form");
			return openBlock(), createElementBlock("div", _hoisted_1$3, [
				createElementVNode("div", _hoisted_2$3, [createVNode(_component_el_button, {
					type: "primary",
					size: "small",
					icon: "Plus",
					onClick: startAdd,
					disabled: !!editing.value
				}, {
					default: withCtx(() => [..._cache[3] || (_cache[3] = [createTextVNode(" 新增索引 ", -1)])]),
					_: 1
				}, 8, ["disabled"]), __props.fields.length === 0 ? (openBlock(), createElementBlock("span", _hoisted_3$3, "请先添加字段")) : createCommentVNode("", true)]),
				createVNode(_component_el_table, {
					data: indexes.value,
					size: "small",
					border: "",
					"empty-text": "暂无索引"
				}, {
					default: withCtx(() => [
						createVNode(_component_el_table_column, {
							label: "索引名",
							prop: "name",
							"min-width": "120"
						}),
						createVNode(_component_el_table_column, {
							label: "字段",
							"min-width": "160"
						}, {
							default: withCtx(({ row }) => [(openBlock(true), createElementBlock(Fragment, null, renderList(row.fields, (f) => {
								return openBlock(), createBlock(_component_el_tag, {
									key: f,
									size: "small",
									type: "info",
									class: "idx-field-tag"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(f), 1)]),
									_: 2
								}, 1024);
							}), 128))]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							label: "唯一",
							width: "60",
							align: "center"
						}, {
							default: withCtx(({ row }) => [createVNode(_component_el_tag, {
								type: row.unique ? "danger" : "info",
								size: "small"
							}, {
								default: withCtx(() => [createTextVNode(toDisplayString(row.unique ? "是" : "否"), 1)]),
								_: 2
							}, 1032, ["type"])]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							label: "操作",
							width: "120",
							align: "center"
						}, {
							default: withCtx(({ $index }) => [createVNode(_component_el_button, {
								size: "small",
								link: "",
								type: "primary",
								onClick: ($event) => startEdit($index),
								disabled: !!editing.value
							}, {
								default: withCtx(() => [..._cache[4] || (_cache[4] = [createTextVNode("编辑", -1)])]),
								_: 1
							}, 8, ["onClick", "disabled"]), createVNode(_component_el_button, {
								size: "small",
								link: "",
								type: "danger",
								onClick: ($event) => removeIndex($index),
								disabled: !!editing.value
							}, {
								default: withCtx(() => [..._cache[5] || (_cache[5] = [createTextVNode("删除", -1)])]),
								_: 1
							}, 8, ["onClick", "disabled"])]),
							_: 1
						})
					]),
					_: 1
				}, 8, ["data"]),
				editing.value ? (openBlock(), createElementBlock("div", _hoisted_4$2, [createVNode(_component_el_form, {
					model: editing.value,
					"label-width": "80px",
					size: "small"
				}, {
					default: withCtx(() => [
						createVNode(_component_el_form_item, {
							label: "索引名",
							required: ""
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: editing.value.name,
								"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => editing.value.name = $event),
								placeholder: "如 idx_user_status"
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, {
							label: "字段",
							required: ""
						}, {
							default: withCtx(() => [createVNode(_component_el_select, {
								modelValue: editing.value.fields,
								"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => editing.value.fields = $event),
								multiple: "",
								filterable: "",
								placeholder: "选择索引字段（可多选）",
								style: { "width": "100%" }
							}, {
								default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(fieldOptions.value, (opt) => {
									return openBlock(), createBlock(_component_el_option, {
										key: opt.value,
										label: opt.label,
										value: opt.value
									}, null, 8, ["label", "value"]);
								}), 128))]),
								_: 1
							}, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, { label: "唯一索引" }, {
							default: withCtx(() => [createVNode(_component_el_switch, {
								modelValue: editing.value.unique,
								"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => editing.value.unique = $event)
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, null, {
							default: withCtx(() => [createVNode(_component_el_button, {
								type: "primary",
								size: "small",
								onClick: saveEdit
							}, {
								default: withCtx(() => [..._cache[6] || (_cache[6] = [createTextVNode("保存", -1)])]),
								_: 1
							}), createVNode(_component_el_button, {
								size: "small",
								onClick: cancelEdit
							}, {
								default: withCtx(() => [..._cache[7] || (_cache[7] = [createTextVNode("取消", -1)])]),
								_: 1
							})]),
							_: 1
						})
					]),
					_: 1
				}, 8, ["model"])])) : createCommentVNode("", true)
			]);
		};
	}
}), [["__scopeId", "data-v-0ad4d2fd"]]);
//#endregion
//#region src/components/EntityDesigner/FieldPanel.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$2 = { class: "field-panel" };
var _hoisted_2$2 = {
	key: 1,
	class: "col-dash"
};
var _hoisted_3$2 = {
	key: 1,
	class: "col-dash"
};
//#endregion
//#region src/components/EntityDesigner/FieldPanel.vue
var FieldPanel_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "FieldPanel",
	props: {
		entity: {},
		fields: {},
		indexes: {}
	},
	emits: [
		"update:entity",
		"update:fields",
		"update:indexes"
	],
	setup(__props, { emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const activeTab = ref("entity");
		const formData = reactive({ ...props.entity });
		watch(() => props.entity, (val) => {
			Object.assign(formData, val);
		}, { deep: true });
		watch(formData, () => {
			emit("update:entity", { ...formData });
		}, { deep: true });
		/** 索引列表 v-model 代理（透传到 IndexPanel） */
		const indexProxy = computed({
			get: () => props.indexes || [],
			set: (v) => emit("update:indexes", v)
		});
		function addField() {
			const newField = {
				name: "new_field",
				label: "新字段",
				fieldType: "STRING",
				length: 255,
				nullable: 1,
				primaryKey: 0,
				indexed: 0,
				uniqueFlag: 0,
				sortOrder: props.fields.length
			};
			emit("update:fields", [...props.fields, newField]);
		}
		function removeField(index) {
			const updated = [...props.fields];
			updated.splice(index, 1);
			emit("update:fields", updated);
		}
		const FIELD_TYPES = [
			"STRING",
			"INTEGER",
			"LONG",
			"DECIMAL",
			"BOOLEAN",
			"DATE",
			"DATETIME",
			"TEXT"
		];
		/** 主键策略可选项 */
		const PK_STRATEGIES = [
			{
				label: "AUTO_INCREMENT（自增）",
				value: "AUTO_INCREMENT"
			},
			{
				label: "UUID（36位）",
				value: "UUID"
			},
			{
				label: "SNOWFLAKE（雪花）",
				value: "SNOWFLAKE"
			},
			{
				label: "BUSINESS（业务主键）",
				value: "BUSINESS"
			}
		];
		return (_ctx, _cache) => {
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_tab_pane = resolveComponent("el-tab-pane");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_checkbox = resolveComponent("el-checkbox");
			const _component_el_switch = resolveComponent("el-switch");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_tabs = resolveComponent("el-tabs");
			return openBlock(), createElementBlock("div", _hoisted_1$2, [createVNode(_component_el_tabs, {
				modelValue: activeTab.value,
				"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => activeTab.value = $event)
			}, {
				default: withCtx(() => [
					createVNode(_component_el_tab_pane, {
						label: "实体属性",
						name: "entity"
					}, {
						default: withCtx(() => [createVNode(_component_el_form, {
							model: formData,
							"label-width": "90px",
							size: "small"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_form_item, { label: "实体编码" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: formData.code,
										"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => formData.code = $event),
										placeholder: "如 device"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "实体名称" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: formData.name,
										"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => formData.name = $event),
										placeholder: "如 设备"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "物理表名" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: formData.tableName,
										"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => formData.tableName = $event),
										placeholder: "pms_lc_device"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "描述" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: formData.description,
										"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => formData.description = $event),
										type: "textarea",
										rows: 2
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "业务类型" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: formData.bizType,
										"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => formData.bizType = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["model"])]),
						_: 1
					}),
					createVNode(_component_el_tab_pane, {
						label: "字段管理",
						name: "fields"
					}, {
						default: withCtx(() => [createVNode(_component_el_button, {
							type: "primary",
							size: "small",
							onClick: addField,
							style: { "margin-bottom": "10px" }
						}, {
							default: withCtx(() => [..._cache[7] || (_cache[7] = [createTextVNode(" 新增字段 ", -1)])]),
							_: 1
						}), createVNode(_component_el_table, {
							data: props.fields,
							size: "small",
							border: ""
						}, {
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									label: "字段名",
									prop: "name",
									width: "120"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_input, {
										modelValue: row.name,
										"onUpdate:modelValue": ($event) => row.name = $event,
										size: "small"
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "显示名",
									prop: "label",
									width: "120"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_input, {
										modelValue: row.label,
										"onUpdate:modelValue": ($event) => row.label = $event,
										size: "small"
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "类型",
									prop: "fieldType",
									width: "110"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_select, {
										modelValue: row.fieldType,
										"onUpdate:modelValue": ($event) => row.fieldType = $event,
										size: "small"
									}, {
										default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(FIELD_TYPES, (t) => {
											return createVNode(_component_el_option, {
												key: t,
												label: t,
												value: t
											}, null, 8, ["label", "value"]);
										}), 64))]),
										_: 1
									}, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "长度",
									prop: "length",
									width: "70"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_input_number, {
										modelValue: row.length,
										"onUpdate:modelValue": ($event) => row.length = $event,
										size: "small",
										min: 1,
										"controls-position": "right"
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "精度",
									prop: "scale",
									width: "70"
								}, {
									default: withCtx(({ row }) => [row.fieldType === "DECIMAL" ? (openBlock(), createBlock(_component_el_input_number, {
										key: 0,
										modelValue: row.scale,
										"onUpdate:modelValue": ($event) => row.scale = $event,
										size: "small",
										min: 0,
										max: 30,
										"controls-position": "right"
									}, null, 8, ["modelValue", "onUpdate:modelValue"])) : (openBlock(), createElementBlock("span", _hoisted_2$2, "—"))]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "默认值",
									prop: "defaultValue",
									width: "120"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_input, {
										modelValue: row.defaultValue,
										"onUpdate:modelValue": ($event) => row.defaultValue = $event,
										size: "small",
										placeholder: "无"
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "PK",
									prop: "primaryKey",
									width: "50"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_checkbox, {
										modelValue: row.primaryKey,
										"onUpdate:modelValue": ($event) => row.primaryKey = $event,
										"true-value": 1,
										"false-value": 0
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "主键策略",
									width: "140"
								}, {
									default: withCtx(({ row }) => [row.primaryKey === 1 ? (openBlock(), createBlock(_component_el_select, {
										key: 0,
										modelValue: row.pkStrategy,
										"onUpdate:modelValue": ($event) => row.pkStrategy = $event,
										size: "small",
										placeholder: "选择策略",
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(PK_STRATEGIES, (s) => {
											return createVNode(_component_el_option, {
												key: s.value,
												label: s.label,
												value: s.value
											}, null, 8, ["label", "value"]);
										}), 64))]),
										_: 1
									}, 8, ["modelValue", "onUpdate:modelValue"])) : (openBlock(), createElementBlock("span", _hoisted_3$2, "—"))]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "可空",
									prop: "nullable",
									width: "50"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_checkbox, {
										modelValue: row.nullable,
										"onUpdate:modelValue": ($event) => row.nullable = $event,
										"true-value": 1,
										"false-value": 0
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "索引",
									prop: "indexed",
									width: "55"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_switch, {
										modelValue: row.indexed,
										"onUpdate:modelValue": ($event) => row.indexed = $event,
										"active-value": 1,
										"inactive-value": 0,
										size: "small"
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "唯一",
									prop: "uniqueFlag",
									width: "55"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_switch, {
										modelValue: row.uniqueFlag,
										"onUpdate:modelValue": ($event) => row.uniqueFlag = $event,
										"active-value": 1,
										"inactive-value": 0,
										size: "small"
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "操作",
									width: "70"
								}, {
									default: withCtx(({ $index }) => [createVNode(_component_el_button, {
										type: "danger",
										size: "small",
										link: "",
										onClick: ($event) => removeField($index)
									}, {
										default: withCtx(() => [..._cache[8] || (_cache[8] = [createTextVNode("删除", -1)])]),
										_: 1
									}, 8, ["onClick"])]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["data"])]),
						_: 1
					}),
					createVNode(_component_el_tab_pane, {
						label: "索引配置",
						name: "indexes"
					}, {
						default: withCtx(() => [createVNode(IndexPanel_default, {
							fields: props.fields,
							modelValue: indexProxy.value,
							"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => indexProxy.value = $event)
						}, null, 8, ["fields", "modelValue"])]),
						_: 1
					})
				]),
				_: 1
			}, 8, ["modelValue"])]);
		};
	}
}), [["__scopeId", "data-v-1c27d556"]]);
//#endregion
//#region src/components/EntityDesigner/RelationConfigDialog.vue
var RelationConfigDialog_default = /* @__PURE__ */ defineComponent({
	__name: "RelationConfigDialog",
	props: {
		modelValue: { type: Boolean },
		fromEntityId: {},
		toEntityId: {}
	},
	emits: ["update:modelValue", "confirm"],
	setup(__props, { emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const visible = ref(props.modelValue);
		watch(() => props.modelValue, (v) => {
			visible.value = v;
		});
		watch(visible, (v) => emit("update:modelValue", v));
		const form = ref({
			fromEntityId: 0,
			toEntityId: 0,
			relationType: "ONE_TO_MANY",
			fromFieldName: "",
			toFieldName: "",
			onDelete: "RESTRICT",
			onUpdate: "RESTRICT"
		});
		watch(() => props.fromEntityId, (v) => {
			form.value.fromEntityId = v;
		});
		watch(() => props.toEntityId, (v) => {
			form.value.toEntityId = v;
		});
		function onConfirm() {
			emit("confirm", { ...form.value });
			visible.value = false;
		}
		function onClose() {
			emit("update:modelValue", false);
		}
		return (_ctx, _cache) => {
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_dialog = resolveComponent("el-dialog");
			return openBlock(), createBlock(_component_el_dialog, {
				modelValue: visible.value,
				"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => visible.value = $event),
				title: "配置关联",
				width: "500px",
				onClose
			}, {
				footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[4] || (_cache[4] = ($event) => visible.value = false) }, {
					default: withCtx(() => [..._cache[6] || (_cache[6] = [createTextVNode("取消", -1)])]),
					_: 1
				}), createVNode(_component_el_button, {
					type: "primary",
					onClick: onConfirm
				}, {
					default: withCtx(() => [..._cache[7] || (_cache[7] = [createTextVNode("确定", -1)])]),
					_: 1
				})]),
				default: withCtx(() => [createVNode(_component_el_form, {
					model: form.value,
					"label-width": "100px"
				}, {
					default: withCtx(() => [
						createVNode(_component_el_form_item, { label: "关联类型" }, {
							default: withCtx(() => [createVNode(_component_el_select, {
								modelValue: form.value.relationType,
								"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => form.value.relationType = $event),
								placeholder: "选择关联类型"
							}, {
								default: withCtx(() => [
									createVNode(_component_el_option, {
										label: "一对一",
										value: "ONE_TO_ONE"
									}),
									createVNode(_component_el_option, {
										label: "一对多",
										value: "ONE_TO_MANY"
									}),
									createVNode(_component_el_option, {
										label: "多对一",
										value: "MANY_TO_ONE"
									}),
									createVNode(_component_el_option, {
										label: "多对多",
										value: "MANY_TO_MANY"
									})
								]),
								_: 1
							}, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, { label: "外键字段" }, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: form.value.fromFieldName,
								"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => form.value.fromFieldName = $event),
								placeholder: "如 user_id"
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						form.value.relationType === "MANY_TO_MANY" ? (openBlock(), createBlock(_component_el_form_item, {
							key: 0,
							label: "反向字段"
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: form.value.toFieldName,
								"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => form.value.toFieldName = $event),
								placeholder: "如 role_id"
							}, null, 8, ["modelValue"])]),
							_: 1
						})) : createCommentVNode("", true),
						createVNode(_component_el_form_item, { label: "级联策略" }, {
							default: withCtx(() => [createVNode(_component_el_select, {
								modelValue: form.value.onDelete,
								"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => form.value.onDelete = $event),
								placeholder: "选择级联策略"
							}, {
								default: withCtx(() => [
									createVNode(_component_el_option, {
										label: "级联删除 (CASCADE)",
										value: "CASCADE"
									}),
									createVNode(_component_el_option, {
										label: "置空 (SET_NULL)",
										value: "SET_NULL"
									}),
									createVNode(_component_el_option, {
										label: "禁止 (RESTRICT)",
										value: "RESTRICT"
									})
								]),
								_: 1
							}, 8, ["modelValue"])]),
							_: 1
						})
					]),
					_: 1
				}, 8, ["model"])]),
				_: 1
			}, 8, ["modelValue"]);
		};
	}
});
//#endregion
//#region src/components/EntityDesigner/EntityNode.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$1 = { class: "entity-header" };
var _hoisted_2$1 = { class: "entity-name" };
var _hoisted_3$1 = {
	key: 0,
	class: "entity-status"
};
var _hoisted_4$1 = { class: "entity-table" };
var _hoisted_5$1 = { class: "entity-fields" };
var _hoisted_6$1 = {
	key: 0,
	class: "field-key"
};
var _hoisted_7$1 = { class: "field-name" };
var _hoisted_8$1 = { class: "field-type" };
var _hoisted_9$1 = {
	key: 0,
	class: "field-empty"
};
//#endregion
//#region src/components/EntityDesigner/EntityNode.vue
var EntityNode_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "EntityNode",
	props: {
		node: {},
		graph: {}
	},
	setup(__props) {
		/**
		* 实体节点（X6 自定义 Vue 节点，通过 @antv/x6-vue-shape 注册）。
		*
		* <p>由 x6-vue-shape 以 props.node（X6 Node 实例）+ props.graph 注入；
		* 实体数据从 node.getData() 读取，监听 change:data 实现选中态等响应式更新。</p>
		*
		* <p>渲染：实体名标题 + 物理表名 + 字段列表（PK 字段标红徽章）。
		* 选中态（data.selected）时边框高亮，供多实体画布点击高亮使用。</p>
		*/
		const props = __props;
		/** 响应式读取节点数据（监听 change:data 以响应 setData 触发的选中态变更） */
		const nodeData = ref(props.node.getData() || {});
		const onDataChange = () => {
			nodeData.value = props.node.getData() || {};
		};
		props.node.on("change:data", onDataChange);
		onBeforeUnmount(() => {
			props.node.off("change:data", onDataChange);
		});
		const fields = computed(() => nodeData.value.fields || []);
		const isSelected = computed(() => !!nodeData.value.selected);
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", { class: normalizeClass(["entity-node", { selected: isSelected.value }]) }, [
				createElementVNode("div", _hoisted_1$1, [createElementVNode("span", _hoisted_2$1, toDisplayString(nodeData.value.entityName), 1), nodeData.value.status ? (openBlock(), createElementBlock("span", _hoisted_3$1, toDisplayString(nodeData.value.status), 1)) : createCommentVNode("", true)]),
				createElementVNode("div", _hoisted_4$1, toDisplayString(nodeData.value.tableName), 1),
				createElementVNode("div", _hoisted_5$1, [(openBlock(true), createElementBlock(Fragment, null, renderList(fields.value, (field) => {
					return openBlock(), createElementBlock("div", {
						key: field.name,
						class: "field-row"
					}, [
						field.primaryKey === 1 ? (openBlock(), createElementBlock("span", _hoisted_6$1, "PK")) : createCommentVNode("", true),
						createElementVNode("span", _hoisted_7$1, toDisplayString(field.name), 1),
						createElementVNode("span", _hoisted_8$1, toDisplayString(field.fieldType), 1)
					]);
				}), 128)), fields.value.length === 0 ? (openBlock(), createElementBlock("div", _hoisted_9$1, "（暂无字段）")) : createCommentVNode("", true)])
			], 2);
		};
	}
}), [["__scopeId", "data-v-e1352a40"]]);
//#endregion
//#region src/views/lowcode/entity-designer/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "entity-designer" };
var _hoisted_2 = { class: "entity-list-panel" };
var _hoisted_3 = { class: "panel-header" };
var _hoisted_4 = ["onDragstart", "onClick"];
var _hoisted_5 = { class: "entity-item-name" };
var _hoisted_6 = { class: "entity-item-code" };
var _hoisted_7 = { class: "canvas-panel" };
var _hoisted_8 = { class: "toolbar" };
var _hoisted_9 = { class: "property-panel" };
var _hoisted_10 = { key: 0 };
var _hoisted_11 = { style: { "margin-bottom": "10px" } };
/** 自定义实体节点 shape 名（通过 x6-vue-shape 注册） */
var ENTITY_NODE_SHAPE = "entity-node";
//#endregion
//#region src/views/lowcode/entity-designer/index.vue
var entity_designer_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "EntityDesignerView",
	__name: "index",
	setup(__props) {
		const entityList = ref([]);
		const currentEntity = ref({
			code: "",
			name: "",
			tableName: "",
			description: "",
			bizType: "",
			status: "DRAFT"
		});
		const currentFields = ref([]);
		const currentRelations = ref([]);
		/** 当前实体的复合索引列表（本地维护，后续可扩展到后端持久化） */
		const currentIndexes = ref([]);
		const relationDialogVisible = ref(false);
		const pendingRelation = ref(null);
		const ddlDialogVisible = ref(false);
		const ddlResult = ref(null);
		const loading = ref(false);
		const rollbackDialogVisible = ref(false);
		const rollbackBackups = ref([]);
		const rollbackLoading = ref(false);
		/** 备份类型显示文案映射 */
		const backupTypeText = {
			CREATE: "建表",
			ALTER: "改表",
			DROP_COLUMN: "删列"
		};
		const graphRef = shallowRef(null);
		const canvasContainer = ref();
		async function loadEntityList() {
			loading.value = true;
			try {
				entityList.value = await getEntityList();
			} catch (e) {
				ElMessage.error("加载实体列表失败");
			} finally {
				loading.value = false;
			}
		}
		async function selectEntity(entity) {
			if (!entity.id) return;
			const design = await getEntityDesign(entity.id);
			currentEntity.value = design.entity;
			currentFields.value = design.fields;
			currentRelations.value = design.relations || [];
			currentIndexes.value = [];
			renderGraph();
		}
		/** 构造实体节点数据（供 EntityNode vue-shape 渲染） */
		function buildEntityNodeData(entity, fields, selected = false) {
			return {
				entityId: entity.id,
				entityName: entity.name,
				tableName: entity.tableName,
				status: entity.status,
				fields,
				selected
			};
		}
		/** 计算实体节点高度（标题+表名+字段行） */
		function entityNodeHeight(fields) {
			return 56 + Math.min(fields.length, 8) * 22;
		}
		function renderGraph() {
			if (!graphRef.value) return;
			graphRef.value.clearCells();
			graphRef.value.addNode({
				shape: ENTITY_NODE_SHAPE,
				x: 120,
				y: 80,
				width: 240,
				height: entityNodeHeight(currentFields.value),
				data: buildEntityNodeData(currentEntity.value, currentFields.value, true)
			});
		}
		/** 将指定 entityId 的节点设为选中，其余取消选中 */
		function highlightNode(entityId) {
			if (!graphRef.value) return;
			graphRef.value.getNodes().forEach((node) => {
				const data = node.getData() || {};
				const isTarget = data.entityId === entityId;
				if (data.selected !== isTarget) node.setData({
					...data,
					selected: isTarget
				});
			});
		}
		/** 画布节点点击：加载该实体到右侧编辑面板并高亮（多实体画布下生效） */
		async function onNodeClick({ node }) {
			const data = node.getData();
			if (!data || !data.entityId) return;
			if (!entityList.value.find((e) => e.id === data.entityId)) return;
			try {
				const design = await getEntityDesign(data.entityId);
				currentEntity.value = design.entity;
				currentFields.value = design.fields;
				currentRelations.value = design.relations || [];
				currentIndexes.value = [];
				highlightNode(data.entityId);
			} catch (_unused) {
				ElMessage.error("加载实体设计失败");
			}
		}
		/** 加载全部实体到画布，形成多实体 ER 全景图（自动网格布局 + 关联边） */
		async function loadAllEntities() {
			if (!graphRef.value) return;
			loading.value = true;
			try {
				const list = await getEntityList();
				const designs = [];
				for (const e of list) {
					if (!e.id) continue;
					const design = await getEntityDesign(e.id);
					designs.push({
						entity: design.entity,
						fields: design.fields,
						relations: design.relations || []
					});
				}
				graphRef.value.clearCells();
				const cols = Math.max(1, Math.ceil(Math.sqrt(designs.length)));
				const gapX = 300;
				const gapY = 280;
				designs.forEach((d, i) => {
					const col = i % cols;
					const row = Math.floor(i / cols);
					graphRef.value.addNode({
						shape: ENTITY_NODE_SHAPE,
						x: col * gapX + 40,
						y: row * gapY + 40,
						width: 240,
						height: entityNodeHeight(d.fields),
						data: buildEntityNodeData(d.entity, d.fields, false)
					});
				});
				const addedEdges = /* @__PURE__ */ new Set();
				for (const d of designs) for (const rel of d.relations) {
					const key = `${rel.fromEntityId}->${rel.toEntityId}`;
					if (addedEdges.has(key)) continue;
					addedEdges.add(key);
					const sourceNode = graphRef.value.getNodes().find((n) => {
						var _n$getData;
						return ((_n$getData = n.getData()) === null || _n$getData === void 0 ? void 0 : _n$getData.entityId) === rel.fromEntityId;
					});
					const targetNode = graphRef.value.getNodes().find((n) => {
						var _n$getData2;
						return ((_n$getData2 = n.getData()) === null || _n$getData2 === void 0 ? void 0 : _n$getData2.entityId) === rel.toEntityId;
					});
					if (sourceNode && targetNode) graphRef.value.addEdge({
						source: sourceNode,
						target: targetNode,
						attrs: { line: {
							stroke: "#67c23a",
							strokeWidth: 2,
							targetMarker: {
								name: "classic",
								size: 6
							}
						} },
						labels: rel.relationType ? [{
							text: rel.relationType,
							fontSize: 10,
							fill: "#909399"
						}] : []
					});
				}
				graphRef.value.zoomToFit({
					padding: 20,
					maxScale: 1
				});
				ElMessage.success(`已加载 ${designs.length} 个实体`);
			} catch (_unused2) {
				ElMessage.error("加载全部实体失败");
			} finally {
				loading.value = false;
			}
		}
		async function saveDesign() {
			if (!currentEntity.value.code || !currentEntity.value.tableName) {
				ElMessage.warning("请填写实体编码和物理表名");
				return;
			}
			if (!/^pms_lc_[a-z][a-z0-9_]*$/.test(currentEntity.value.tableName)) {
				ElMessage.warning("物理表名必须以 pms_lc_ 开头，小写字母+数字+下划线");
				return;
			}
			if (await checkTableName(currentEntity.value.tableName, currentEntity.value.id)) {
				ElMessage.error("物理表名已存在");
				return;
			}
			const design = {
				entity: currentEntity.value,
				fields: currentFields.value,
				relations: currentRelations.value
			};
			try {
				const saved = await saveEntityDesign(design);
				currentEntity.value = saved;
				ElMessage.success("保存成功");
				await loadEntityList();
			} catch (e) {
				ElMessage.error("保存失败");
			}
		}
		async function previewDdl() {
			if (!currentEntity.value.id) {
				ElMessage.warning("请先保存实体");
				return;
			}
			try {
				ddlResult.value = await generateDdl(currentEntity.value.id);
				ddlDialogVisible.value = true;
			} catch (e) {
				ElMessage.error("生成 DDL 失败");
			}
		}
		/** 打开 DDL 回滚对话框并加载备份列表 */
		async function openRollbackDialog() {
			if (!currentEntity.value.id) {
				ElMessage.warning("请先保存实体");
				return;
			}
			rollbackDialogVisible.value = true;
			await loadRollbackBackups();
		}
		async function loadRollbackBackups() {
			if (!currentEntity.value.id) return;
			rollbackLoading.value = true;
			try {
				rollbackBackups.value = await listDdlBackups(currentEntity.value.id);
			} catch (e) {
				ElMessage.error("加载 DDL 备份列表失败");
				rollbackBackups.value = [];
			} finally {
				rollbackLoading.value = false;
			}
		}
		/** 回滚最近一次 DDL 操作（二次确认） */
		async function rollbackLast() {
			if (!currentEntity.value.id) return;
			try {
				await ElMessageBox.confirm("确认回滚最近一次 DDL 操作？该操作可能删除/重建物理表，请谨慎确认。", "回滚最近 DDL", {
					type: "warning",
					confirmButtonText: "确认回滚",
					cancelButtonText: "取消"
				});
				const type = await rollbackLastDdl(currentEntity.value.id);
				ElMessage.success(`已回滚最近一次 DDL（${backupTypeText[type] || type}）`);
				await loadRollbackBackups();
			} catch (e) {}
		}
		/** 按备份记录 ID 回滚（二次确认） */
		async function rollbackByBackup(backup) {
			if (!backup.id) return;
			const typeText = backupTypeText[backup.backupType || ""] || backup.backupType;
			try {
				await ElMessageBox.confirm(`确认回滚该备份记录（${typeText}，表 ${backup.tableName}）？该操作可能删除/重建物理表，请谨慎确认。`, "按备份回滚", {
					type: "warning",
					confirmButtonText: "确认回滚",
					cancelButtonText: "取消"
				});
				await rollbackByBackupId(backup.id);
				ElMessage.success("回滚成功");
				await loadRollbackBackups();
			} catch (e) {}
		}
		async function publish() {
			if (!currentEntity.value.id) {
				ElMessage.warning("请先保存实体");
				return;
			}
			try {
				const { value: changeLog } = await ElMessageBox.prompt("请输入变更说明", "发布实体", {
					confirmButtonText: "发布",
					cancelButtonText: "取消"
				});
				await publishEntity(currentEntity.value.id, changeLog || "");
				ElMessage.success("发布成功");
				await loadEntityList();
				await selectEntity(currentEntity.value);
			} catch (e) {}
		}
		async function removeEntity(entity) {
			if (!entity.id) return;
			try {
				await ElMessageBox.confirm(`确认删除实体 ${entity.name}？`, "提示", { type: "warning" });
				await deleteEntity(entity.id);
				ElMessage.success("删除成功");
				await loadEntityList();
			} catch (e) {}
		}
		function newEntity() {
			currentEntity.value = {
				code: "",
				name: "",
				tableName: "",
				description: "",
				bizType: "",
				status: "DRAFT"
			};
			currentFields.value = [];
			currentRelations.value = [];
			currentIndexes.value = [];
			if (graphRef.value) graphRef.value.clearCells();
		}
		function onEdgeConnected({ source, target }) {
			var _source$cell$getData, _target$cell$getData;
			const fromEntityId = (_source$cell$getData = source.cell.getData()) === null || _source$cell$getData === void 0 ? void 0 : _source$cell$getData.entityId;
			const toEntityId = (_target$cell$getData = target.cell.getData()) === null || _target$cell$getData === void 0 ? void 0 : _target$cell$getData.entityId;
			if (fromEntityId && toEntityId) {
				pendingRelation.value = {
					from: fromEntityId,
					to: toEntityId
				};
				relationDialogVisible.value = true;
			}
		}
		async function onRelationConfirm(relation) {
			if (!pendingRelation.value) return;
			try {
				await saveRelations(pendingRelation.value.from, [relation]);
				ElMessage.success("关联已保存");
				const entity = entityList.value.find((x) => x.id === pendingRelation.value.from);
				if (entity) await selectEntity(entity);
			} catch (e) {
				ElMessage.error("保存关联失败");
			}
		}
		function onEntityDragStart(e, entity) {
			var _e$dataTransfer;
			(_e$dataTransfer = e.dataTransfer) === null || _e$dataTransfer === void 0 || _e$dataTransfer.setData("entityId", String(entity.id));
		}
		async function onCanvasDrop(e) {
			var _e$dataTransfer2;
			e.preventDefault();
			const entityId = Number((_e$dataTransfer2 = e.dataTransfer) === null || _e$dataTransfer2 === void 0 ? void 0 : _e$dataTransfer2.getData("entityId"));
			if (!entityId) return;
			const entity = entityList.value.find((x) => x.id === entityId);
			if (!entity) return;
			try {
				var _canvasContainer$valu, _graphRef$value;
				const design = await getEntityDesign(entity.id);
				const rect = (_canvasContainer$valu = canvasContainer.value) === null || _canvasContainer$valu === void 0 ? void 0 : _canvasContainer$valu.getBoundingClientRect();
				const x = e.clientX - ((rect === null || rect === void 0 ? void 0 : rect.left) || 0) - 120;
				const y = e.clientY - ((rect === null || rect === void 0 ? void 0 : rect.top) || 0) - 50;
				(_graphRef$value = graphRef.value) === null || _graphRef$value === void 0 || _graphRef$value.addNode({
					shape: ENTITY_NODE_SHAPE,
					x: Math.max(0, x),
					y: Math.max(0, y),
					width: 240,
					height: entityNodeHeight(design.fields),
					data: buildEntityNodeData(entity, design.fields, false)
				});
			} catch (err) {
				ElMessage.error("加载实体设计失败");
			}
		}
		function initGraph() {
			if (!canvasContainer.value) return;
			graphRef.value = new Graph({
				container: canvasContainer.value,
				background: { color: "#f5f5f5" },
				grid: {
					visible: true,
					size: 10
				},
				interacting: {
					nodeMovable: true,
					edgeMovable: true
				},
				mousewheel: {
					enabled: true,
					modifiers: ["ctrl"]
				},
				connecting: {
					allowBlank: false,
					allowLoop: false,
					allowMulti: true,
					router: "orth",
					connector: "rounded",
					createEdge() {
						return this.createEdge({
							shape: "edge",
							attrs: { line: {
								stroke: "#409eff",
								strokeWidth: 2
							} }
						});
					}
				}
			});
			graphRef.value.on("edge:connected", ({ edge }) => {
				const sourceNode = edge.getSourceNode();
				const targetNode = edge.getTargetNode();
				if (sourceNode && targetNode) onEdgeConnected({
					source: { cell: sourceNode },
					target: { cell: targetNode }
				});
			});
			graphRef.value.on("node:click", ({ node }) => {
				onNodeClick({ node });
			});
		}
		onMounted(() => {
			register({
				shape: ENTITY_NODE_SHAPE,
				width: 240,
				height: 120,
				component: EntityNode_default
			});
			initGraph();
			loadEntityList();
		});
		onBeforeUnmount(() => {
			var _graphRef$value2;
			(_graphRef$value2 = graphRef.value) === null || _graphRef$value2 === void 0 || _graphRef$value2.dispose();
		});
		return (_ctx, _cache) => {
			var _pendingRelation$valu, _pendingRelation$valu2;
			const _component_el_button = resolveComponent("el-button");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_scrollbar = resolveComponent("el-scrollbar");
			const _component_el_alert = resolveComponent("el-alert");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_table = resolveComponent("el-table");
			const _directive_loading = resolveDirective("loading");
			return withDirectives((openBlock(), createElementBlock("div", _hoisted_1, [
				createElementVNode("div", _hoisted_2, [createElementVNode("div", _hoisted_3, [_cache[8] || (_cache[8] = createElementVNode("span", null, "实体列表", -1)), createVNode(_component_el_button, {
					type: "primary",
					size: "small",
					onClick: newEntity
				}, {
					default: withCtx(() => [..._cache[7] || (_cache[7] = [createTextVNode("新建", -1)])]),
					_: 1
				})]), createVNode(_component_el_scrollbar, null, {
					default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(entityList.value, (entity) => {
						return openBlock(), createElementBlock("div", {
							key: entity.id,
							class: normalizeClass(["entity-item", { active: entity.id === currentEntity.value.id }]),
							draggable: "true",
							onDragstart: ($event) => onEntityDragStart($event, entity),
							onClick: ($event) => selectEntity(entity)
						}, [
							createElementVNode("div", _hoisted_5, toDisplayString(entity.name), 1),
							createElementVNode("div", _hoisted_6, toDisplayString(entity.code), 1),
							createVNode(_component_el_tag, {
								size: "small",
								type: entity.status === "PUBLISHED" ? "success" : "info"
							}, {
								default: withCtx(() => [createTextVNode(toDisplayString(entity.status), 1)]),
								_: 2
							}, 1032, ["type"]),
							createVNode(_component_el_button, {
								type: "danger",
								size: "small",
								link: "",
								onClick: withModifiers(($event) => removeEntity(entity), ["stop"])
							}, {
								default: withCtx(() => [..._cache[9] || (_cache[9] = [createTextVNode("删除", -1)])]),
								_: 1
							}, 8, ["onClick"])
						], 42, _hoisted_4);
					}), 128))]),
					_: 1
				})]),
				createElementVNode("div", _hoisted_7, [createElementVNode("div", _hoisted_8, [
					createVNode(_component_el_button, {
						type: "primary",
						size: "small",
						onClick: saveDesign
					}, {
						default: withCtx(() => [..._cache[10] || (_cache[10] = [createTextVNode("保存", -1)])]),
						_: 1
					}),
					createVNode(_component_el_button, {
						size: "small",
						onClick: previewDdl
					}, {
						default: withCtx(() => [..._cache[11] || (_cache[11] = [createTextVNode("DDL 预览", -1)])]),
						_: 1
					}),
					createVNode(_component_el_button, {
						type: "success",
						size: "small",
						onClick: publish
					}, {
						default: withCtx(() => [..._cache[12] || (_cache[12] = [createTextVNode("发布", -1)])]),
						_: 1
					}),
					createVNode(_component_el_button, {
						type: "warning",
						size: "small",
						onClick: loadAllEntities
					}, {
						default: withCtx(() => [..._cache[13] || (_cache[13] = [createTextVNode("加载全部实体", -1)])]),
						_: 1
					}),
					createVNode(_component_el_button, {
						type: "danger",
						size: "small",
						onClick: openRollbackDialog
					}, {
						default: withCtx(() => [..._cache[14] || (_cache[14] = [createTextVNode("回滚 DDL", -1)])]),
						_: 1
					})
				]), createElementVNode("div", {
					ref_key: "canvasContainer",
					ref: canvasContainer,
					class: "canvas-container",
					onDrop: onCanvasDrop,
					onDragover: _cache[0] || (_cache[0] = withModifiers(() => {}, ["prevent"]))
				}, null, 544)]),
				createElementVNode("div", _hoisted_9, [createVNode(FieldPanel_default, {
					entity: currentEntity.value,
					fields: currentFields.value,
					indexes: currentIndexes.value,
					"onUpdate:entity": _cache[1] || (_cache[1] = ($event) => currentEntity.value = $event),
					"onUpdate:fields": _cache[2] || (_cache[2] = ($event) => currentFields.value = $event),
					"onUpdate:indexes": _cache[3] || (_cache[3] = ($event) => currentIndexes.value = $event)
				}, null, 8, [
					"entity",
					"fields",
					"indexes"
				])]),
				createVNode(_component_el_dialog, {
					modelValue: ddlDialogVisible.value,
					"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => ddlDialogVisible.value = $event),
					title: "DDL 预览",
					width: "700px"
				}, {
					default: withCtx(() => [ddlResult.value ? (openBlock(), createElementBlock("div", _hoisted_10, [ddlResult.value.hasJunctionTable ? (openBlock(), createBlock(_component_el_alert, {
						key: 0,
						type: "info",
						title: "检测到多对多关联，已自动生成中间表 DDL",
						closable: false,
						style: { "margin-bottom": "10px" }
					})) : createCommentVNode("", true), (openBlock(true), createElementBlock(Fragment, null, renderList(ddlResult.value.ddlStatements, (sql, i) => {
						return openBlock(), createElementBlock("pre", {
							key: i,
							class: "ddl-block"
						}, toDisplayString(sql) + ";", 1);
					}), 128))])) : createCommentVNode("", true)]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: rollbackDialogVisible.value,
					"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => rollbackDialogVisible.value = $event),
					title: "DDL 回滚",
					width: "720px"
				}, {
					default: withCtx(() => [
						createVNode(_component_el_alert, {
							type: "warning",
							title: "回滚会直接修改物理表结构（可能 DROP/重建表），属于高危操作，请谨慎确认。",
							closable: false,
							style: { "margin-bottom": "12px" }
						}),
						createElementVNode("div", _hoisted_11, [createVNode(_component_el_button, {
							type: "danger",
							size: "small",
							onClick: rollbackLast
						}, {
							default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode("回滚最近一次", -1)])]),
							_: 1
						}), createVNode(_component_el_button, {
							size: "small",
							onClick: loadRollbackBackups
						}, {
							default: withCtx(() => [..._cache[16] || (_cache[16] = [createTextVNode("刷新", -1)])]),
							_: 1
						})]),
						withDirectives((openBlock(), createBlock(_component_el_table, {
							data: rollbackBackups.value,
							border: "",
							size: "small",
							"empty-text": "暂无 DDL 备份记录"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									prop: "id",
									label: "ID",
									width: "70"
								}),
								createVNode(_component_el_table_column, {
									label: "类型",
									width: "90"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, {
										size: "small",
										type: row.backupType === "CREATE" ? "success" : row.backupType === "ALTER" ? "warning" : "danger"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(backupTypeText[row.backupType] || row.backupType), 1)]),
										_: 2
									}, 1032, ["type"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									prop: "tableName",
									label: "物理表",
									width: "180"
								}),
								createVNode(_component_el_table_column, {
									prop: "createTime",
									label: "备份时间",
									width: "170"
								}),
								createVNode(_component_el_table_column, {
									label: "操作",
									width: "110",
									fixed: "right"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_button, {
										type: "danger",
										size: "small",
										link: "",
										onClick: ($event) => rollbackByBackup(row)
									}, {
										default: withCtx(() => [..._cache[17] || (_cache[17] = [createTextVNode("回滚此备份", -1)])]),
										_: 1
									}, 8, ["onClick"])]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["data"])), [[_directive_loading, rollbackLoading.value]])
					]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(RelationConfigDialog_default, {
					modelValue: relationDialogVisible.value,
					"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => relationDialogVisible.value = $event),
					"from-entity-id": ((_pendingRelation$valu = pendingRelation.value) === null || _pendingRelation$valu === void 0 ? void 0 : _pendingRelation$valu.from) || 0,
					"to-entity-id": ((_pendingRelation$valu2 = pendingRelation.value) === null || _pendingRelation$valu2 === void 0 ? void 0 : _pendingRelation$valu2.to) || 0,
					onConfirm: onRelationConfirm
				}, null, 8, [
					"modelValue",
					"from-entity-id",
					"to-entity-id"
				])
			])), [[_directive_loading, loading.value]]);
		};
	}
}), [["__scopeId", "data-v-f08bd169"]]);
//#endregion
export { entity_designer_default as default };

//# sourceMappingURL=entity-designer-CbS0YA6A.js.map