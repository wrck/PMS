import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { l as deleteCategory, o as createCategory, p as getCategoryTree, x as updateCategory } from "./asset-DozMltVh.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, nextTick, onMounted, openBlock, reactive, ref, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withModifiers } from "vue";
//#region src/views/asset/category/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "card-header" };
var _hoisted_3 = { class: "tree-node" };
var _hoisted_4 = { class: "tree-node-label" };
var _hoisted_5 = { class: "tree-node-code" };
var _hoisted_6 = { class: "card-header" };
//#endregion
//#region src/views/asset/category/index.vue
var category_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const loading = ref(false);
		const treeData = ref([]);
		const treeRef = ref();
		const currentNodeId = ref(void 0);
		const filterText = ref("");
		const formRef = ref();
		const submitting = ref(false);
		const isEdit = ref(false);
		const form = reactive(createEmptyForm());
		const treeWithRoot = computed(() => [{
			id: 0,
			name: "顶级分类",
			children: treeData.value
		}]);
		const rules = {
			name: [{
				required: true,
				message: "请输入分类名称",
				trigger: "blur"
			}],
			code: [{
				required: true,
				message: "请输入分类编码",
				trigger: "blur"
			}],
			sort: [{
				required: true,
				message: "请输入排序",
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
				parentId: 0,
				code: "",
				name: "",
				sort: 0,
				status: 1
			};
		}
		const treeProps = {
			label: "name",
			children: "children"
		};
		async function loadData() {
			loading.value = true;
			try {
				treeData.value = await getCategoryTree() || [];
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		function filterNode(value, data) {
			var _data$name$includes, _data$name;
			if (!value) return true;
			return (_data$name$includes = (_data$name = data.name) === null || _data$name === void 0 ? void 0 : _data$name.includes(value)) !== null && _data$name$includes !== void 0 ? _data$name$includes : false;
		}
		function handleFilterText() {
			var _treeRef$value;
			(_treeRef$value = treeRef.value) === null || _treeRef$value === void 0 || _treeRef$value.filter(filterText.value);
		}
		function handleNodeClick(data) {
			currentNodeId.value = data.id;
			isEdit.value = true;
			Object.assign(form, createEmptyForm(), data);
		}
		function handleAddTop() {
			resetForm();
			isEdit.value = false;
			form.parentId = 0;
		}
		function handleAddChild(data) {
			var _data$id;
			if (!data.id) return;
			resetForm();
			isEdit.value = false;
			form.parentId = (_data$id = data.id) !== null && _data$id !== void 0 ? _data$id : 0;
		}
		function resetForm() {
			var _formRef$value;
			Object.assign(form, createEmptyForm());
			currentNodeId.value = void 0;
			(_formRef$value = formRef.value) === null || _formRef$value === void 0 || _formRef$value.clearValidate();
		}
		async function handleSubmit() {
			if (!formRef.value) return;
			await formRef.value.validate(async (valid) => {
				if (!valid) return;
				submitting.value = true;
				try {
					if (isEdit.value && form.id) {
						await updateCategory({ ...form });
						ElMessage.success("更新成功");
					} else {
						const created = await createCategory({ ...form });
						ElMessage.success("新增成功");
						if (created === null || created === void 0 ? void 0 : created.id) currentNodeId.value = created.id;
					}
					await loadData();
					await nextTick();
					if (currentNodeId.value) {
						var _treeRef$value2;
						(_treeRef$value2 = treeRef.value) === null || _treeRef$value2 === void 0 || _treeRef$value2.setCurrentKey(currentNodeId.value);
					}
				} catch (_unused2) {} finally {
					submitting.value = false;
				}
			});
		}
		function handleDelete(data) {
			if (!data.id) return;
			const tip = data.children && data.children.length > 0 ? `分类「${data.name}」包含子分类，删除后子分类也将被删除，确定继续吗？` : `确定删除分类「${data.name}」吗？`;
			ElMessageBox.confirm(tip, "提示", { type: "warning" }).then(async () => {
				await deleteCategory(data.id);
				ElMessage.success("删除成功");
				if (currentNodeId.value === data.id) {
					resetForm();
					isEdit.value = false;
				}
				loadData();
			}).catch(() => {});
		}
		function handleReset() {
			resetForm();
			isEdit.value = false;
		}
		onMounted(loadData);
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_tree = resolveComponent("el-tree");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_tree_select = resolveComponent("el-tree-select");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_switch = resolveComponent("el-switch");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_row = resolveComponent("el-row");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [_cache[13] || (_cache[13] = createElementVNode("div", { class: "page-title" }, "设备分类管理", -1)), createVNode(_component_el_row, {
				gutter: 12,
				class: "content-row"
			}, {
				default: withCtx(() => [createVNode(_component_el_col, { span: 9 }, {
					default: withCtx(() => [withDirectives((openBlock(), createBlock(_component_el_card, {
						shadow: "never",
						class: "tree-card"
					}, {
						header: withCtx(() => [createElementVNode("div", _hoisted_2, [_cache[8] || (_cache[8] = createElementVNode("span", null, "分类树", -1)), createVNode(_component_el_button, {
							type: "primary",
							icon: "Plus",
							size: "small",
							onClick: handleAddTop
						}, {
							default: withCtx(() => [..._cache[7] || (_cache[7] = [createTextVNode(" 新增顶级分类 ", -1)])]),
							_: 1
						})])]),
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: filterText.value,
							"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => filterText.value = $event),
							placeholder: "输入分类名称过滤",
							clearable: "",
							"prefix-icon": "Search",
							class: "filter-input",
							onInput: handleFilterText
						}, null, 8, ["modelValue"]), createVNode(_component_el_tree, {
							ref_key: "treeRef",
							ref: treeRef,
							data: treeData.value,
							props: treeProps,
							"node-key": "id",
							"highlight-current": "",
							"default-expand-all": "",
							"expand-on-click-node": false,
							"filter-node-method": filterNode,
							onNodeClick: handleNodeClick
						}, {
							default: withCtx(({ data }) => [createElementVNode("div", _hoisted_3, [createElementVNode("span", _hoisted_4, [
								createVNode(_component_el_tag, {
									type: data.status === 0 ? "info" : "success",
									size: "small",
									class: "status-dot"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(data.status === 0 ? "禁用" : "启用"), 1)]),
									_: 2
								}, 1032, ["type"]),
								createTextVNode(" " + toDisplayString(data.name) + " ", 1),
								createElementVNode("span", _hoisted_5, "（" + toDisplayString(data.code) + "）", 1)
							]), createElementVNode("span", {
								class: "tree-node-actions",
								onClick: _cache[1] || (_cache[1] = withModifiers(() => {}, ["stop"]))
							}, [createVNode(_component_el_button, {
								link: "",
								type: "primary",
								size: "small",
								onClick: ($event) => handleAddChild(data)
							}, {
								default: withCtx(() => [..._cache[9] || (_cache[9] = [createTextVNode(" 新增子级 ", -1)])]),
								_: 1
							}, 8, ["onClick"]), createVNode(_component_el_button, {
								link: "",
								type: "danger",
								size: "small",
								onClick: ($event) => handleDelete(data)
							}, {
								default: withCtx(() => [..._cache[10] || (_cache[10] = [createTextVNode(" 删除 ", -1)])]),
								_: 1
							}, 8, ["onClick"])])])]),
							_: 1
						}, 8, ["data"])]),
						_: 1
					})), [[_directive_loading, loading.value]])]),
					_: 1
				}), createVNode(_component_el_col, { span: 15 }, {
					default: withCtx(() => [createVNode(_component_el_card, { shadow: "never" }, {
						header: withCtx(() => [createElementVNode("div", _hoisted_6, [createElementVNode("span", null, toDisplayString(isEdit.value ? "编辑分类" : "新增分类"), 1), isEdit.value ? (openBlock(), createBlock(_component_el_button, {
							key: 0,
							icon: "Plus",
							size: "small",
							onClick: handleAddTop
						}, {
							default: withCtx(() => [..._cache[11] || (_cache[11] = [createTextVNode(" 切换为新增 ", -1)])]),
							_: 1
						})) : createCommentVNode("", true)])]),
						default: withCtx(() => [createVNode(_component_el_form, {
							ref_key: "formRef",
							ref: formRef,
							model: form,
							rules,
							"label-width": "100px",
							style: { "max-width": "560px" }
						}, {
							default: withCtx(() => [
								createVNode(_component_el_form_item, {
									label: "上级分类",
									prop: "parentId"
								}, {
									default: withCtx(() => [createVNode(_component_el_tree_select, {
										modelValue: form.parentId,
										"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => form.parentId = $event),
										data: treeWithRoot.value,
										props: {
											label: "name",
											value: "id",
											children: "children"
										},
										"node-key": "id",
										"check-strictly": "",
										"default-expand-all": "",
										placeholder: "请选择上级分类",
										style: { "width": "100%" }
									}, null, 8, ["modelValue", "data"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, {
									label: "分类名称",
									prop: "name"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: form.name,
										"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => form.name = $event),
										placeholder: "请输入分类名称",
										maxlength: "50"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, {
									label: "分类编码",
									prop: "code"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: form.code,
										"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => form.code = $event),
										placeholder: "请输入分类编码",
										maxlength: "50",
										disabled: isEdit.value
									}, null, 8, ["modelValue", "disabled"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, {
									label: "排序",
									prop: "sort"
								}, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: form.sort,
										"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => form.sort = $event),
										min: 0,
										max: 9999
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, {
									label: "状态",
									prop: "status"
								}, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: form.status,
										"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => form.status = $event),
										"active-value": 1,
										"inactive-value": 0,
										"active-text": "启用",
										"inactive-text": "禁用"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, null, {
									default: withCtx(() => [createVNode(_component_el_button, {
										type: "primary",
										loading: submitting.value,
										onClick: handleSubmit
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(isEdit.value ? "保存" : "新增"), 1)]),
										_: 1
									}, 8, ["loading"]), createVNode(_component_el_button, { onClick: handleReset }, {
										default: withCtx(() => [..._cache[12] || (_cache[12] = [createTextVNode("重置", -1)])]),
										_: 1
									})]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["model"])]),
						_: 1
					})]),
					_: 1
				})]),
				_: 1
			})]);
		};
	}
}), [["__scopeId", "data-v-11cce899"]]);
//#endregion
export { category_default as default };

//# sourceMappingURL=category-BqbI_uQV.js.map