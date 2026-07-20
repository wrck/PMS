import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { f as getMenuTree, g as updateMenu, n as createMenu, o as deleteMenu } from "./system-CuVYDpvc.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, watch, withCtx, withDirectives } from "vue";
//#region src/views/system/menu/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "toolbar" };
//#endregion
//#region src/views/system/menu/index.vue
var menu_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const loading = ref(false);
		const treeData = ref([]);
		const dialogVisible = ref(false);
		const dialogTitle = ref("");
		const formRef = ref();
		const submitting = ref(false);
		const form = reactive(createEmptyForm());
		const menuTreeWithRoot = computed(() => [{
			id: 0,
			name: "顶级菜单",
			children: treeData.value
		}]);
		const menuTypeOptions = [
			{
				value: 0,
				label: "目录"
			},
			{
				value: 1,
				label: "菜单"
			},
			{
				value: 2,
				label: "按钮"
			},
			{
				value: 3,
				label: "低代码页面"
			}
		];
		/** 低代码页面类型选项 */
		const lowcodePageTypeOptions = [
			{
				value: "form",
				label: "表单"
			},
			{
				value: "list",
				label: "列表"
			},
			{
				value: "tab",
				label: "标签页"
			},
			{
				value: "related-page",
				label: "关联页"
			}
		];
		const rules = {
			name: [{
				required: true,
				message: "请输入菜单名称",
				trigger: "blur"
			}],
			type: [{
				required: true,
				message: "请选择菜单类型",
				trigger: "change"
			}],
			pageType: [{
				required: true,
				message: "请选择低代码页面类型",
				trigger: "change"
			}],
			pageCode: [{
				required: true,
				message: "请输入低代码配置编码",
				trigger: "blur"
			}]
		};
		function createEmptyForm() {
			return {
				parentId: 0,
				name: "",
				path: "",
				component: "",
				icon: "",
				type: 1,
				permission: "",
				sort: 0,
				visible: 1,
				status: 1,
				pageType: "form",
				pageCode: ""
			};
		}
		async function loadData() {
			loading.value = true;
			try {
				treeData.value = await getMenuTree();
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		function handleAdd(row) {
			dialogTitle.value = "新增菜单";
			Object.assign(form, createEmptyForm());
			if (row === null || row === void 0 ? void 0 : row.id) form.parentId = row.id;
			dialogVisible.value = true;
		}
		function handleEdit(row) {
			dialogTitle.value = "编辑菜单";
			Object.assign(form, createEmptyForm(), row);
			dialogVisible.value = true;
		}
		/**
		* 当低代码页面类型或编码变化时，自动生成路由 path 为 /lowcode/{pageType}/{pageCode}。
		* 同时为权限标识生成默认值 lowcode:page:{pageType}:{pageCode}（仅在用户未自定义时）。
		*/
		watch(() => [
			form.type,
			form.pageType,
			form.pageCode
		], ([type, pageType, pageCode]) => {
			if (type === 3 && pageType && pageCode) {
				form.path = `/lowcode/${pageType}/${pageCode}`;
				const autoPerm = `lowcode:page:${pageType}:${pageCode}`;
				if (!form.permission || /^lowcode:page:[a-z-]+:.+$/.test(form.permission)) form.permission = autoPerm;
			}
		});
		async function handleSubmit() {
			if (!formRef.value) return;
			await formRef.value.validate(async (valid) => {
				if (!valid) return;
				submitting.value = true;
				try {
					if (form.id) {
						await updateMenu(form);
						ElMessage.success("更新成功");
					} else {
						await createMenu(form);
						ElMessage.success("新增成功");
					}
					dialogVisible.value = false;
					loadData();
				} catch (_unused2) {} finally {
					submitting.value = false;
				}
			});
		}
		function handleDelete(row) {
			if (!row.id) return;
			ElMessageBox.confirm(`确定删除菜单「${row.name}」吗？`, "提示", { type: "warning" }).then(async () => {
				await deleteMenu(row.id);
				ElMessage.success("删除成功");
				loadData();
			}).catch(() => {});
		}
		function typeLabel(type) {
			var _menuTypeOptions$find, _menuTypeOptions$find2;
			return (_menuTypeOptions$find = (_menuTypeOptions$find2 = menuTypeOptions.find((o) => o.value === type)) === null || _menuTypeOptions$find2 === void 0 ? void 0 : _menuTypeOptions$find2.label) !== null && _menuTypeOptions$find !== void 0 ? _menuTypeOptions$find : "";
		}
		function typeTagType(type) {
			if (type === 0) return "primary";
			if (type === 1) return "success";
			if (type === 3) return "danger";
			return "warning";
		}
		onMounted(loadData);
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_tree_select = resolveComponent("el-tree-select");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_radio = resolveComponent("el-radio");
			const _component_el_radio_group = resolveComponent("el-radio-group");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_switch = resolveComponent("el-switch");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_card, { shadow: "never" }, {
				default: withCtx(() => [createElementVNode("div", _hoisted_2, [createVNode(_component_el_button, {
					type: "primary",
					icon: "Plus",
					onClick: _cache[0] || (_cache[0] = ($event) => handleAdd())
				}, {
					default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode("新增菜单", -1)])]),
					_: 1
				}), createVNode(_component_el_button, {
					icon: "Refresh",
					onClick: loadData
				}, {
					default: withCtx(() => [..._cache[16] || (_cache[16] = [createTextVNode("刷新", -1)])]),
					_: 1
				})]), withDirectives((openBlock(), createBlock(_component_el_table, {
					data: treeData.value,
					"row-key": "id",
					"tree-props": { children: "children" },
					border: "",
					stripe: "",
					"default-expand-all": ""
				}, {
					default: withCtx(() => [
						createVNode(_component_el_table_column, {
							prop: "name",
							label: "菜单名称",
							"min-width": "180"
						}),
						createVNode(_component_el_table_column, {
							label: "类型",
							width: "90"
						}, {
							default: withCtx(({ row }) => [createVNode(_component_el_tag, { type: typeTagType(row.type) }, {
								default: withCtx(() => [createTextVNode(toDisplayString(typeLabel(row.type)), 1)]),
								_: 2
							}, 1032, ["type"])]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							prop: "icon",
							label: "图标",
							width: "100"
						}),
						createVNode(_component_el_table_column, {
							prop: "path",
							label: "路由路径",
							"min-width": "140"
						}),
						createVNode(_component_el_table_column, {
							prop: "component",
							label: "组件",
							"min-width": "180"
						}),
						createVNode(_component_el_table_column, {
							prop: "permission",
							label: "权限标识",
							"min-width": "160"
						}),
						createVNode(_component_el_table_column, {
							prop: "sort",
							label: "排序",
							width: "80"
						}),
						createVNode(_component_el_table_column, {
							label: "状态",
							width: "90"
						}, {
							default: withCtx(({ row }) => [createVNode(_component_el_tag, { type: row.status === 1 ? "success" : "info" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(row.status === 1 ? "启用" : "禁用"), 1)]),
								_: 2
							}, 1032, ["type"])]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							label: "操作",
							width: "220",
							fixed: "right"
						}, {
							default: withCtx(({ row }) => [
								createVNode(_component_el_button, {
									link: "",
									type: "primary",
									onClick: ($event) => handleAdd(row)
								}, {
									default: withCtx(() => [..._cache[17] || (_cache[17] = [createTextVNode("新增", -1)])]),
									_: 1
								}, 8, ["onClick"]),
								createVNode(_component_el_button, {
									link: "",
									type: "primary",
									onClick: ($event) => handleEdit(row)
								}, {
									default: withCtx(() => [..._cache[18] || (_cache[18] = [createTextVNode("编辑", -1)])]),
									_: 1
								}, 8, ["onClick"]),
								createVNode(_component_el_button, {
									link: "",
									type: "danger",
									onClick: ($event) => handleDelete(row)
								}, {
									default: withCtx(() => [..._cache[19] || (_cache[19] = [createTextVNode("删除", -1)])]),
									_: 1
								}, 8, ["onClick"])
							]),
							_: 1
						})
					]),
					_: 1
				}, 8, ["data"])), [[_directive_loading, loading.value]])]),
				_: 1
			}), createVNode(_component_el_dialog, {
				modelValue: dialogVisible.value,
				"onUpdate:modelValue": _cache[14] || (_cache[14] = ($event) => dialogVisible.value = $event),
				title: dialogTitle.value,
				width: "600px",
				"destroy-on-close": ""
			}, {
				footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[13] || (_cache[13] = ($event) => dialogVisible.value = false) }, {
					default: withCtx(() => [..._cache[20] || (_cache[20] = [createTextVNode("取消", -1)])]),
					_: 1
				}), createVNode(_component_el_button, {
					type: "primary",
					loading: submitting.value,
					onClick: handleSubmit
				}, {
					default: withCtx(() => [..._cache[21] || (_cache[21] = [createTextVNode("确定", -1)])]),
					_: 1
				}, 8, ["loading"])]),
				default: withCtx(() => [createVNode(_component_el_form, {
					ref_key: "formRef",
					ref: formRef,
					model: form,
					rules,
					"label-width": "90px"
				}, {
					default: withCtx(() => [
						createVNode(_component_el_form_item, {
							label: "上级菜单",
							prop: "parentId"
						}, {
							default: withCtx(() => [createVNode(_component_el_tree_select, {
								modelValue: form.parentId,
								"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => form.parentId = $event),
								data: menuTreeWithRoot.value,
								props: {
									label: "name",
									value: "id",
									children: "children"
								},
								"node-key": "id",
								"check-strictly": "",
								"default-expand-all": "",
								placeholder: "请选择上级菜单",
								style: { "width": "100%" }
							}, null, 8, ["modelValue", "data"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, {
							label: "菜单类型",
							prop: "type"
						}, {
							default: withCtx(() => [createVNode(_component_el_radio_group, {
								modelValue: form.type,
								"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => form.type = $event)
							}, {
								default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(menuTypeOptions, (o) => {
									return createVNode(_component_el_radio, {
										key: o.value,
										value: o.value
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(o.label), 1)]),
										_: 2
									}, 1032, ["value"]);
								}), 64))]),
								_: 1
							}, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, {
							label: "菜单名称",
							prop: "name"
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: form.name,
								"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => form.name = $event),
								placeholder: "请输入菜单名称"
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, {
							label: "图标",
							prop: "icon"
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: form.icon,
								"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => form.icon = $event),
								placeholder: "Element Plus 图标名称，如 User"
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						form.type === 3 ? (openBlock(), createBlock(_component_el_form_item, {
							key: 0,
							label: "页面类型",
							prop: "pageType"
						}, {
							default: withCtx(() => [createVNode(_component_el_select, {
								modelValue: form.pageType,
								"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => form.pageType = $event),
								placeholder: "请选择低代码页面类型",
								style: { "width": "100%" }
							}, {
								default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(lowcodePageTypeOptions, (o) => {
									return createVNode(_component_el_option, {
										key: o.value,
										label: o.label,
										value: o.value
									}, null, 8, ["label", "value"]);
								}), 64))]),
								_: 1
							}, 8, ["modelValue"])]),
							_: 1
						})) : createCommentVNode("", true),
						form.type === 3 ? (openBlock(), createBlock(_component_el_form_item, {
							key: 1,
							label: "页面编码",
							prop: "pageCode"
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: form.pageCode,
								"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => form.pageCode = $event),
								placeholder: "低代码配置编码，如 tpl_project_create"
							}, null, 8, ["modelValue"])]),
							_: 1
						})) : createCommentVNode("", true),
						form.type !== 2 ? (openBlock(), createBlock(_component_el_form_item, {
							key: 2,
							label: "路由路径",
							prop: "path"
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: form.path,
								"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => form.path = $event),
								placeholder: form.type === 3 ? "由页面类型和编码自动生成" : "如 /system/user",
								disabled: form.type === 3
							}, null, 8, [
								"modelValue",
								"placeholder",
								"disabled"
							])]),
							_: 1
						})) : createCommentVNode("", true),
						form.type === 1 ? (openBlock(), createBlock(_component_el_form_item, {
							key: 3,
							label: "组件路径",
							prop: "component"
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: form.component,
								"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => form.component = $event),
								placeholder: "如 system/user/index"
							}, null, 8, ["modelValue"])]),
							_: 1
						})) : createCommentVNode("", true),
						form.type !== 0 ? (openBlock(), createBlock(_component_el_form_item, {
							key: 4,
							label: "权限标识",
							prop: "permission"
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: form.permission,
								"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => form.permission = $event),
								placeholder: form.type === 3 ? "如 lowcode:page:form:myCode（可自定义）" : "如 system:user:list"
							}, null, 8, ["modelValue", "placeholder"])]),
							_: 1
						})) : createCommentVNode("", true),
						createVNode(_component_el_form_item, {
							label: "排序",
							prop: "sort"
						}, {
							default: withCtx(() => [createVNode(_component_el_input_number, {
								modelValue: form.sort,
								"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => form.sort = $event),
								min: 0
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, {
							label: "是否显示",
							prop: "visible"
						}, {
							default: withCtx(() => [createVNode(_component_el_switch, {
								modelValue: form.visible,
								"onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => form.visible = $event),
								"active-value": 1,
								"inactive-value": 0
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, {
							label: "状态",
							prop: "status"
						}, {
							default: withCtx(() => [createVNode(_component_el_switch, {
								modelValue: form.status,
								"onUpdate:modelValue": _cache[12] || (_cache[12] = ($event) => form.status = $event),
								"active-value": 1,
								"inactive-value": 0
							}, null, 8, ["modelValue"])]),
							_: 1
						})
					]),
					_: 1
				}, 8, ["model"])]),
				_: 1
			}, 8, ["modelValue", "title"])]);
		};
	}
}), [["__scopeId", "data-v-47045a70"]]);
//#endregion
export { menu_default as default };

//# sourceMappingURL=menu-C2RO2-ml.js.map