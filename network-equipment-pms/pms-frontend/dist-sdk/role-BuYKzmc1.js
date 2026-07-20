import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { _ as updateRole, p as getRolePage, r as createRole, s as deleteRole } from "./system-CuVYDpvc.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { createBlock, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withKeys, withModifiers } from "vue";
//#region src/views/system/role/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "toolbar" };
//#endregion
//#region src/views/system/role/index.vue
var role_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const loading = ref(false);
		const tableData = ref([]);
		const total = ref(0);
		const query = reactive({
			page: 1,
			size: 10,
			keyword: ""
		});
		const dialogVisible = ref(false);
		const dialogTitle = ref("");
		const formRef = ref();
		const submitting = ref(false);
		const form = reactive(createEmptyForm());
		const rules = {
			name: [{
				required: true,
				message: "请输入角色名称",
				trigger: "blur"
			}],
			code: [{
				required: true,
				message: "请输入角色编码",
				trigger: "blur"
			}]
		};
		function createEmptyForm() {
			return {
				name: "",
				code: "",
				sort: 0,
				status: 1,
				remark: ""
			};
		}
		async function loadData() {
			loading.value = true;
			try {
				const res = await getRolePage(query);
				tableData.value = res.records;
				total.value = res.total;
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		function handleSearch() {
			query.page = 1;
			loadData();
		}
		function handleReset() {
			query.keyword = "";
			query.page = 1;
			loadData();
		}
		function handleAdd() {
			dialogTitle.value = "新增角色";
			Object.assign(form, createEmptyForm());
			dialogVisible.value = true;
		}
		function handleEdit(row) {
			dialogTitle.value = "编辑角色";
			Object.assign(form, row);
			dialogVisible.value = true;
		}
		async function handleSubmit() {
			if (!formRef.value) return;
			await formRef.value.validate(async (valid) => {
				if (!valid) return;
				submitting.value = true;
				try {
					if (form.id) {
						await updateRole(form);
						ElMessage.success("更新成功");
					} else {
						await createRole(form);
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
			ElMessageBox.confirm(`确定删除角色「${row.name}」吗？`, "提示", { type: "warning" }).then(async () => {
				await deleteRole(row.id);
				ElMessage.success("删除成功");
				loadData();
			}).catch(() => {});
		}
		function handlePageChange(p) {
			query.page = p;
			loadData();
		}
		function handleSizeChange(s) {
			query.size = s;
			query.page = 1;
			loadData();
		}
		onMounted(loadData);
		return (_ctx, _cache) => {
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_pagination = resolveComponent("el-pagination");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_switch = resolveComponent("el-switch");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_card, { shadow: "never" }, {
				default: withCtx(() => [
					createVNode(_component_el_form, {
						inline: true,
						onSubmit: _cache[1] || (_cache[1] = withModifiers(() => {}, ["prevent"]))
					}, {
						default: withCtx(() => [createVNode(_component_el_form_item, { label: "关键字" }, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: query.keyword,
								"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => query.keyword = $event),
								placeholder: "角色名称/编码",
								clearable: "",
								onKeyup: withKeys(handleSearch, ["enter"])
							}, null, 8, ["modelValue"])]),
							_: 1
						}), createVNode(_component_el_form_item, null, {
							default: withCtx(() => [createVNode(_component_el_button, {
								type: "primary",
								icon: "Search",
								onClick: handleSearch
							}, {
								default: withCtx(() => [..._cache[9] || (_cache[9] = [createTextVNode("查询", -1)])]),
								_: 1
							}), createVNode(_component_el_button, {
								icon: "Refresh",
								onClick: handleReset
							}, {
								default: withCtx(() => [..._cache[10] || (_cache[10] = [createTextVNode("重置", -1)])]),
								_: 1
							})]),
							_: 1
						})]),
						_: 1
					}),
					createElementVNode("div", _hoisted_2, [createVNode(_component_el_button, {
						type: "primary",
						icon: "Plus",
						onClick: handleAdd
					}, {
						default: withCtx(() => [..._cache[11] || (_cache[11] = [createTextVNode("新增角色", -1)])]),
						_: 1
					})]),
					withDirectives((openBlock(), createBlock(_component_el_table, {
						data: tableData.value,
						border: "",
						stripe: ""
					}, {
						default: withCtx(() => [
							createVNode(_component_el_table_column, {
								type: "index",
								label: "#",
								width: "50"
							}),
							createVNode(_component_el_table_column, {
								prop: "name",
								label: "角色名称",
								"min-width": "140"
							}),
							createVNode(_component_el_table_column, {
								prop: "code",
								label: "角色编码",
								"min-width": "140"
							}),
							createVNode(_component_el_table_column, {
								prop: "sort",
								label: "排序",
								width: "90"
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
								prop: "remark",
								label: "备注",
								"min-width": "180",
								"show-overflow-tooltip": ""
							}),
							createVNode(_component_el_table_column, {
								prop: "createTime",
								label: "创建时间",
								"min-width": "160"
							}),
							createVNode(_component_el_table_column, {
								label: "操作",
								width: "160",
								fixed: "right"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_button, {
									link: "",
									type: "primary",
									onClick: ($event) => handleEdit(row)
								}, {
									default: withCtx(() => [..._cache[12] || (_cache[12] = [createTextVNode("编辑", -1)])]),
									_: 1
								}, 8, ["onClick"]), createVNode(_component_el_button, {
									link: "",
									type: "danger",
									onClick: ($event) => handleDelete(row)
								}, {
									default: withCtx(() => [..._cache[13] || (_cache[13] = [createTextVNode("删除", -1)])]),
									_: 1
								}, 8, ["onClick"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["data"])), [[_directive_loading, loading.value]]),
					createVNode(_component_el_pagination, {
						class: "pagination",
						background: "",
						"current-page": query.page,
						"page-size": query.size,
						total: total.value,
						"page-sizes": [
							10,
							20,
							50
						],
						layout: "total, sizes, prev, pager, next, jumper",
						onCurrentChange: handlePageChange,
						onSizeChange: handleSizeChange
					}, null, 8, [
						"current-page",
						"page-size",
						"total"
					])
				]),
				_: 1
			}), createVNode(_component_el_dialog, {
				modelValue: dialogVisible.value,
				"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => dialogVisible.value = $event),
				title: dialogTitle.value,
				width: "520px",
				"destroy-on-close": ""
			}, {
				footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[7] || (_cache[7] = ($event) => dialogVisible.value = false) }, {
					default: withCtx(() => [..._cache[14] || (_cache[14] = [createTextVNode("取消", -1)])]),
					_: 1
				}), createVNode(_component_el_button, {
					type: "primary",
					loading: submitting.value,
					onClick: handleSubmit
				}, {
					default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode("确定", -1)])]),
					_: 1
				}, 8, ["loading"])]),
				default: withCtx(() => [createVNode(_component_el_form, {
					ref_key: "formRef",
					ref: formRef,
					model: form,
					rules,
					"label-width": "80px"
				}, {
					default: withCtx(() => [
						createVNode(_component_el_form_item, {
							label: "角色名称",
							prop: "name"
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: form.name,
								"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => form.name = $event),
								placeholder: "请输入角色名称"
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, {
							label: "角色编码",
							prop: "code"
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: form.code,
								"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => form.code = $event),
								disabled: !!form.id,
								placeholder: "请输入角色编码"
							}, null, 8, ["modelValue", "disabled"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, {
							label: "排序",
							prop: "sort"
						}, {
							default: withCtx(() => [createVNode(_component_el_input_number, {
								modelValue: form.sort,
								"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => form.sort = $event),
								min: 0
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, {
							label: "状态",
							prop: "status"
						}, {
							default: withCtx(() => [createVNode(_component_el_switch, {
								modelValue: form.status,
								"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => form.status = $event),
								"active-value": 1,
								"inactive-value": 0
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, {
							label: "备注",
							prop: "remark"
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: form.remark,
								"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => form.remark = $event),
								type: "textarea",
								rows: 2,
								placeholder: "备注信息"
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
}), [["__scopeId", "data-v-5fe9e220"]]);
//#endregion
export { role_default as default };

//# sourceMappingURL=role-BuYKzmc1.js.map