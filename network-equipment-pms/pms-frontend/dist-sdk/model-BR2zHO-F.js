import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { S as updateModel, g as listModels, p as getCategoryTree, s as createModel, u as deleteModel } from "./asset-DozMltVh.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { createBlock, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withKeys, withModifiers } from "vue";
//#region src/views/asset/model/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "toolbar" };
//#endregion
//#region src/views/asset/model/index.vue
var model_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const loading = ref(false);
		const tableData = ref([]);
		const total = ref(0);
		const categoryTree = ref([]);
		const query = reactive({
			categoryId: void 0,
			name: "",
			page: 1,
			size: 10
		});
		const dialogVisible = ref(false);
		const dialogTitle = ref("");
		const formRef = ref();
		const submitting = ref(false);
		const isEdit = ref(false);
		const form = reactive(createEmptyForm());
		const categoryTreeProps = {
			label: "name",
			value: "id",
			children: "children"
		};
		const rules = {
			categoryId: [{
				required: true,
				message: "请选择所属分类",
				trigger: "change"
			}],
			name: [{
				required: true,
				message: "请输入型号名称",
				trigger: "blur"
			}],
			code: [{
				required: true,
				message: "请输入型号编码",
				trigger: "blur"
			}],
			unit: [{
				required: true,
				message: "请输入单位",
				trigger: "blur"
			}],
			status: [{
				required: true,
				message: "请选择状态",
				trigger: "change"
			}]
		};
		function createEmptyForm() {
			return {
				categoryId: 0,
				code: "",
				name: "",
				brand: "",
				spec: "",
				standardPrice: 0,
				unit: "",
				status: 1,
				remark: ""
			};
		}
		async function loadCategories() {
			try {
				categoryTree.value = await getCategoryTree() || [];
			} catch (_unused) {}
		}
		async function loadData() {
			loading.value = true;
			try {
				const res = await listModels({
					categoryId: query.categoryId,
					name: query.name || void 0,
					page: query.page,
					size: query.size
				});
				tableData.value = res.records || [];
				total.value = res.total || 0;
			} catch (_unused2) {} finally {
				loading.value = false;
			}
		}
		function handleSearch() {
			query.page = 1;
			loadData();
		}
		function handleReset() {
			query.categoryId = void 0;
			query.name = "";
			query.page = 1;
			loadData();
		}
		function handleAdd() {
			isEdit.value = false;
			dialogTitle.value = "新增型号";
			Object.assign(form, createEmptyForm());
			dialogVisible.value = true;
		}
		function handleEdit(row) {
			isEdit.value = true;
			dialogTitle.value = "编辑型号";
			Object.assign(form, createEmptyForm(), row);
			dialogVisible.value = true;
		}
		async function handleSubmit() {
			if (!formRef.value) return;
			await formRef.value.validate(async (valid) => {
				if (!valid) return;
				submitting.value = true;
				try {
					if (isEdit.value && form.id) {
						await updateModel({ ...form });
						ElMessage.success("更新成功");
					} else {
						await createModel({ ...form });
						ElMessage.success("新增成功");
					}
					dialogVisible.value = false;
					loadData();
				} catch (_unused3) {} finally {
					submitting.value = false;
				}
			});
		}
		function handleDelete(row) {
			if (!row.id) return;
			ElMessageBox.confirm(`确定删除型号「${row.name}」吗？`, "提示", { type: "warning" }).then(async () => {
				await deleteModel(row.id);
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
		function formatPrice(val) {
			if (val === void 0 || val === null) return "-";
			return `¥${val.toLocaleString("zh-CN", {
				minimumFractionDigits: 2,
				maximumFractionDigits: 2
			})}`;
		}
		function statusTagType(status) {
			return status === 1 ? "success" : "info";
		}
		function statusLabel(status) {
			return status === 1 ? "启用" : "禁用";
		}
		onMounted(() => {
			loadCategories();
			loadData();
		});
		return (_ctx, _cache) => {
			const _component_el_tree_select = resolveComponent("el-tree-select");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_pagination = resolveComponent("el-pagination");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_switch = resolveComponent("el-switch");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				_cache[22] || (_cache[22] = createElementVNode("div", { class: "page-title" }, "设备型号管理", -1)),
				createVNode(_component_el_card, { shadow: "never" }, {
					default: withCtx(() => [
						createVNode(_component_el_form, {
							inline: true,
							onSubmit: _cache[2] || (_cache[2] = withModifiers(() => {}, ["prevent"]))
						}, {
							default: withCtx(() => [
								createVNode(_component_el_form_item, { label: "分类" }, {
									default: withCtx(() => [createVNode(_component_el_tree_select, {
										modelValue: query.categoryId,
										"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => query.categoryId = $event),
										data: categoryTree.value,
										props: categoryTreeProps,
										"node-key": "id",
										"check-strictly": "",
										clearable: "",
										"default-expand-all": "",
										placeholder: "请选择分类",
										style: { "width": "220px" }
									}, null, 8, ["modelValue", "data"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "型号名称" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: query.name,
										"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => query.name = $event),
										placeholder: "型号名称/编码",
										clearable: "",
										style: { "width": "200px" },
										onKeyup: withKeys(handleSearch, ["enter"])
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, null, {
									default: withCtx(() => [createVNode(_component_el_button, {
										type: "primary",
										icon: "Search",
										onClick: handleSearch
									}, {
										default: withCtx(() => [..._cache[14] || (_cache[14] = [createTextVNode("查询", -1)])]),
										_: 1
									}), createVNode(_component_el_button, {
										icon: "Refresh",
										onClick: handleReset
									}, {
										default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode("重置", -1)])]),
										_: 1
									})]),
									_: 1
								})
							]),
							_: 1
						}),
						createElementVNode("div", _hoisted_2, [createVNode(_component_el_button, {
							type: "primary",
							icon: "Plus",
							onClick: handleAdd
						}, {
							default: withCtx(() => [..._cache[16] || (_cache[16] = [createTextVNode("新增型号", -1)])]),
							_: 1
						})]),
						withDirectives((openBlock(), createBlock(_component_el_table, {
							data: tableData.value,
							border: "",
							stripe: ""
						}, {
							empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无数据" })]),
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									type: "index",
									label: "#",
									width: "50"
								}),
								createVNode(_component_el_table_column, {
									prop: "name",
									label: "型号名称",
									"min-width": "160",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "code",
									label: "型号编码",
									"min-width": "140",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "brand",
									label: "品牌",
									"min-width": "120",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "categoryName",
									label: "分类",
									"min-width": "140",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "spec",
									label: "规格参数",
									"min-width": "180",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									label: "标准单价",
									width: "130",
									align: "right"
								}, {
									default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatPrice(row.standardPrice)), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									prop: "unit",
									label: "单位",
									width: "90",
									align: "center"
								}),
								createVNode(_component_el_table_column, {
									label: "状态",
									width: "90",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, { type: statusTagType(row.status) }, {
										default: withCtx(() => [createTextVNode(toDisplayString(statusLabel(row.status)), 1)]),
										_: 2
									}, 1032, ["type"])]),
									_: 1
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
										default: withCtx(() => [..._cache[17] || (_cache[17] = [createTextVNode("编辑", -1)])]),
										_: 1
									}, 8, ["onClick"]), createVNode(_component_el_button, {
										link: "",
										type: "danger",
										onClick: ($event) => handleDelete(row)
									}, {
										default: withCtx(() => [..._cache[18] || (_cache[18] = [createTextVNode("删除", -1)])]),
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
				}),
				createVNode(_component_el_dialog, {
					modelValue: dialogVisible.value,
					"onUpdate:modelValue": _cache[13] || (_cache[13] = ($event) => dialogVisible.value = $event),
					title: dialogTitle.value,
					width: "600px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[12] || (_cache[12] = ($event) => dialogVisible.value = false) }, {
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
						"label-width": "100px"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, {
								label: "分类",
								prop: "categoryId"
							}, {
								default: withCtx(() => [createVNode(_component_el_tree_select, {
									modelValue: form.categoryId,
									"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => form.categoryId = $event),
									data: categoryTree.value,
									props: categoryTreeProps,
									"node-key": "id",
									"check-strictly": "",
									"default-expand-all": "",
									placeholder: "请选择所属分类",
									style: { "width": "100%" }
								}, null, 8, ["modelValue", "data"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "型号名称",
								prop: "name"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.name,
									"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => form.name = $event),
									placeholder: "请输入型号名称",
									maxlength: "100"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "型号编码",
								prop: "code"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.code,
									"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => form.code = $event),
									placeholder: "请输入型号编码",
									maxlength: "50",
									disabled: isEdit.value
								}, null, 8, ["modelValue", "disabled"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "品牌",
								prop: "brand"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.brand,
									"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => form.brand = $event),
									placeholder: "请输入品牌",
									maxlength: "50"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "规格参数",
								prop: "spec"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.spec,
									"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => form.spec = $event),
									type: "textarea",
									rows: 3,
									placeholder: "请输入规格参数",
									maxlength: "500"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "标准单价",
								prop: "standardPrice"
							}, {
								default: withCtx(() => [createVNode(_component_el_input_number, {
									modelValue: form.standardPrice,
									"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => form.standardPrice = $event),
									min: 0,
									precision: 2,
									step: 100,
									style: { "width": "200px" }
								}, null, 8, ["modelValue"]), _cache[19] || (_cache[19] = createElementVNode("span", { class: "form-tip" }, "元", -1))]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "单位",
								prop: "unit"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.unit,
									"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => form.unit = $event),
									placeholder: "如：台/套/个",
									maxlength: "20",
									style: { "width": "200px" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "状态",
								prop: "status"
							}, {
								default: withCtx(() => [createVNode(_component_el_switch, {
									modelValue: form.status,
									"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => form.status = $event),
									"active-value": 1,
									"inactive-value": 0,
									"active-text": "启用",
									"inactive-text": "禁用"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "备注",
								prop: "remark"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.remark,
									"onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => form.remark = $event),
									type: "textarea",
									rows: 2,
									placeholder: "备注信息",
									maxlength: "200"
								}, null, 8, ["modelValue"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue", "title"])
			]);
		};
	}
}), [["__scopeId", "data-v-241eadf0"]]);
//#endregion
export { model_default as default };

//# sourceMappingURL=model-BR2zHO-F.js.map