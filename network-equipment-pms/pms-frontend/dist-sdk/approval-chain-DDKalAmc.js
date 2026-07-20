import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { l as getAllRoles } from "./system-CuVYDpvc.js";
import { a as serializeLevels, i as parseLevels, n as deleteApprovalChain, o as updateApprovalChain, r as getApprovalChainList, t as createApprovalChain } from "./lowcode-approval-chain-DzxfcTVv.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives } from "vue";
//#region src/views/lowcode/approval-chain/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { style: { "padding": "16px" } };
var _hoisted_2 = { style: {
	"display": "flex",
	"justify-content": "space-between",
	"align-items": "center"
} };
var _hoisted_3 = { style: {
	"display": "flex",
	"gap": "8px",
	"align-items": "center"
} };
var _hoisted_4 = {
	key: 0,
	class: "empty-levels"
};
//#endregion
//#region src/views/lowcode/approval-chain/index.vue
var approval_chain_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "ApprovalChainView",
	__name: "index",
	setup(__props) {
		/**
		* 低代码发布多级审批链配置页（借鉴 OutSystems LifeTime 多级审批）。
		*
		* <p>按 configType 筛选审批链列表，支持新建/编辑/删除审批链。
		* 编辑对话框中可视化维护级别列表（level/approverRole/name），可增删级别。</p>
		*/
		/** 支持的配置类型 */
		const configTypeOptions = [
			{
				label: "表单 FORM",
				value: "FORM"
			},
			{
				label: "列表 LIST",
				value: "LIST"
			},
			{
				label: "实体 ENTITY",
				value: "ENTITY"
			},
			{
				label: "微流 MICROFLOW",
				value: "MICROFLOW"
			},
			{
				label: "连接器 CONNECTOR",
				value: "CONNECTOR"
			},
			{
				label: "规则 RULE",
				value: "RULE"
			},
			{
				label: "标签页 TAB",
				value: "TAB"
			},
			{
				label: "关联页 RELATED_PAGE",
				value: "RELATED_PAGE"
			}
		];
		const list = ref([]);
		const loading = ref(false);
		/** 筛选的 configType（空字符串表示全部） */
		const filterConfigType = ref("");
		/** 角色列表，用于 approverRole 下拉 */
		const roles = ref([]);
		/** 编辑对话框 */
		const dialogVisible = ref(false);
		const saving = ref(false);
		const current = ref(null);
		/** 结构化级别列表（编辑器内部操作，保存时序列化为 levels JSON） */
		const levelsArr = ref([]);
		const isEdit = computed(() => {
			var _current$value;
			return !!((_current$value = current.value) === null || _current$value === void 0 ? void 0 : _current$value.id);
		});
		const filteredList = computed(() => {
			if (!filterConfigType.value) return list.value;
			return list.value.filter((c) => c.configType === filterConfigType.value);
		});
		async function load() {
			loading.value = true;
			try {
				list.value = await getApprovalChainList();
			} finally {
				loading.value = false;
			}
		}
		async function loadRoles() {
			try {
				roles.value = await getAllRoles();
			} catch (e) {
				console.warn("加载角色列表失败：", e);
				roles.value = [];
			}
		}
		function configTypeLabel(code) {
			var _configTypeOptions$fi, _configTypeOptions$fi2;
			return (_configTypeOptions$fi = (_configTypeOptions$fi2 = configTypeOptions.find((o) => o.value === code)) === null || _configTypeOptions$fi2 === void 0 ? void 0 : _configTypeOptions$fi2.label) !== null && _configTypeOptions$fi !== void 0 ? _configTypeOptions$fi : code;
		}
		function levelSummary(chain) {
			const levels = parseLevels(chain.levels);
			if (levels.length === 0) return "—";
			return levels.map((l) => `${l.level}. ${l.name}(${l.approverRole})`).join(" → ");
		}
		function openNew() {
			current.value = {
				configType: "FORM",
				name: "",
				levels: "[]",
				enabled: 1
			};
			levelsArr.value = [];
			dialogVisible.value = true;
		}
		function openEdit(row) {
			current.value = { ...row };
			levelsArr.value = parseLevels(row.levels);
			dialogVisible.value = true;
		}
		function addLevel() {
			var _roles$value$0$roleCo, _roles$value$;
			const nextLevel = levelsArr.value.length + 1;
			levelsArr.value.push({
				level: nextLevel,
				approverRole: (_roles$value$0$roleCo = (_roles$value$ = roles.value[0]) === null || _roles$value$ === void 0 ? void 0 : _roles$value$.roleCode) !== null && _roles$value$0$roleCo !== void 0 ? _roles$value$0$roleCo : "",
				name: `第${nextLevel}级审批`
			});
		}
		function removeLevel(idx) {
			levelsArr.value.splice(idx, 1);
			levelsArr.value.forEach((l, i) => l.level = i + 1);
		}
		function moveLevel(idx, delta) {
			const target = idx + delta;
			if (target < 0 || target >= levelsArr.value.length) return;
			const tmp = levelsArr.value[idx];
			levelsArr.value[idx] = levelsArr.value[target];
			levelsArr.value[target] = tmp;
			levelsArr.value.forEach((l, i) => l.level = i + 1);
		}
		function validate() {
			const c = current.value;
			if (!c) return false;
			if (!c.name.trim()) {
				ElMessage.warning("请填写审批链名称");
				return false;
			}
			if (levelsArr.value.length === 0) {
				ElMessage.warning("请至少添加一个审批级别");
				return false;
			}
			for (const l of levelsArr.value) {
				var _l$approverRole, _l$name;
				if (!((_l$approverRole = l.approverRole) === null || _l$approverRole === void 0 ? void 0 : _l$approverRole.trim())) {
					ElMessage.warning(`第 ${l.level} 级审批角色不能为空`);
					return false;
				}
				if (!((_l$name = l.name) === null || _l$name === void 0 ? void 0 : _l$name.trim())) {
					ElMessage.warning(`第 ${l.level} 级名称不能为空`);
					return false;
				}
			}
			return true;
		}
		async function save(closeAfter = true) {
			var _c$enabled;
			if (!validate()) return;
			const c = current.value;
			if (!c) return;
			const sorted = [...levelsArr.value].sort((a, b) => a.level - b.level);
			const payload = {
				id: c.id,
				configType: c.configType,
				name: c.name,
				levels: serializeLevels(sorted),
				enabled: (_c$enabled = c.enabled) !== null && _c$enabled !== void 0 ? _c$enabled : 1
			};
			saving.value = true;
			try {
				if (isEdit.value && c.id) {
					await updateApprovalChain(c.id, payload);
					ElMessage.success("更新成功");
				} else {
					await createApprovalChain(payload);
					ElMessage.success("创建成功");
				}
				await load();
				if (closeAfter) dialogVisible.value = false;
			} catch (e) {
				ElMessage.error("保存失败：" + (e instanceof Error ? e.message : String(e)));
			} finally {
				saving.value = false;
			}
		}
		async function remove(row) {
			if (!row.id) return;
			try {
				await ElMessageBox.confirm(`确认删除审批链「${row.name}」？`, "确认", { type: "warning" });
				await deleteApprovalChain(row.id);
				ElMessage.success("删除成功");
				await load();
			} catch (_unused) {}
		}
		async function toggleEnabled(row) {
			if (!row.id) return;
			const newEnabled = row.enabled === 1 ? 0 : 1;
			try {
				await updateApprovalChain(row.id, {
					...row,
					enabled: newEnabled
				});
				ElMessage.success(newEnabled === 1 ? "已启用" : "已停用");
				await load();
			} catch (e) {
				ElMessage.error("切换失败：" + (e instanceof Error ? e.message : String(e)));
			}
		}
		onMounted(() => {
			load();
			loadRoles();
		});
		return (_ctx, _cache) => {
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_switch = resolveComponent("el-switch");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_divider = resolveComponent("el-divider");
			const _component_el_button_group = resolveComponent("el-button-group");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_card, { shadow: "never" }, {
				header: withCtx(() => [createElementVNode("div", _hoisted_2, [_cache[8] || (_cache[8] = createElementVNode("span", null, "多级审批链配置", -1)), createElementVNode("div", _hoisted_3, [createVNode(_component_el_select, {
					modelValue: filterConfigType.value,
					"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => filterConfigType.value = $event),
					placeholder: "按配置类型筛选",
					clearable: "",
					style: { "width": "200px" }
				}, {
					default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(configTypeOptions, (o) => {
						return createVNode(_component_el_option, {
							key: o.value,
							label: o.label,
							value: o.value
						}, null, 8, ["label", "value"]);
					}), 64))]),
					_: 1
				}, 8, ["modelValue"]), createVNode(_component_el_button, {
					type: "primary",
					onClick: openNew
				}, {
					default: withCtx(() => [..._cache[7] || (_cache[7] = [createTextVNode("新建审批链", -1)])]),
					_: 1
				})])])]),
				default: withCtx(() => [withDirectives((openBlock(), createBlock(_component_el_table, { data: filteredList.value }, {
					default: withCtx(() => [
						createVNode(_component_el_table_column, {
							label: "名称",
							prop: "name",
							"min-width": "140",
							"show-overflow-tooltip": ""
						}),
						createVNode(_component_el_table_column, {
							label: "配置类型",
							width: "160"
						}, {
							default: withCtx(({ row }) => [createVNode(_component_el_tag, { size: "small" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(configTypeLabel(row.configType)), 1)]),
								_: 2
							}, 1024)]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							label: "审批级别",
							"min-width": "280",
							"show-overflow-tooltip": ""
						}, {
							default: withCtx(({ row }) => [createTextVNode(toDisplayString(levelSummary(row)), 1)]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							label: "启用",
							width: "80"
						}, {
							default: withCtx(({ row }) => [createVNode(_component_el_switch, {
								"model-value": row.enabled === 1,
								onChange: ($event) => toggleEnabled(row)
							}, null, 8, ["model-value", "onChange"])]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							label: "操作",
							width: "180"
						}, {
							default: withCtx(({ row }) => [createVNode(_component_el_button, {
								size: "small",
								onClick: ($event) => openEdit(row)
							}, {
								default: withCtx(() => [..._cache[9] || (_cache[9] = [createTextVNode("编辑", -1)])]),
								_: 1
							}, 8, ["onClick"]), createVNode(_component_el_button, {
								size: "small",
								type: "danger",
								onClick: ($event) => remove(row)
							}, {
								default: withCtx(() => [..._cache[10] || (_cache[10] = [createTextVNode("删除", -1)])]),
								_: 1
							}, 8, ["onClick"])]),
							_: 1
						})
					]),
					_: 1
				}, 8, ["data"])), [[_directive_loading, loading.value]])]),
				_: 1
			}), createVNode(_component_el_dialog, {
				modelValue: dialogVisible.value,
				"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => dialogVisible.value = $event),
				title: isEdit.value ? "编辑审批链" : "新建审批链",
				width: "720px",
				"close-on-click-modal": false,
				"destroy-on-close": ""
			}, {
				footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[4] || (_cache[4] = ($event) => dialogVisible.value = false) }, {
					default: withCtx(() => [..._cache[16] || (_cache[16] = [createTextVNode("取消", -1)])]),
					_: 1
				}), createVNode(_component_el_button, {
					type: "primary",
					loading: saving.value,
					onClick: _cache[5] || (_cache[5] = ($event) => save(true))
				}, {
					default: withCtx(() => [..._cache[17] || (_cache[17] = [createTextVNode("保存", -1)])]),
					_: 1
				}, 8, ["loading"])]),
				default: withCtx(() => [current.value ? (openBlock(), createBlock(_component_el_form, {
					key: 0,
					model: current.value,
					"label-width": "100px"
				}, {
					default: withCtx(() => [
						createVNode(_component_el_form_item, {
							label: "审批链名称",
							required: ""
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: current.value.name,
								"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => current.value.name = $event),
								placeholder: "如 FORM 三级审批"
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, {
							label: "配置类型",
							required: ""
						}, {
							default: withCtx(() => [createVNode(_component_el_select, {
								modelValue: current.value.configType,
								"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => current.value.configType = $event),
								style: { "width": "100%" },
								disabled: isEdit.value
							}, {
								default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(configTypeOptions, (o) => {
									return createVNode(_component_el_option, {
										key: o.value,
										label: o.label,
										value: o.value
									}, null, 8, ["label", "value"]);
								}), 64))]),
								_: 1
							}, 8, ["modelValue", "disabled"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, { label: "启用" }, {
							default: withCtx(() => [createVNode(_component_el_switch, {
								"model-value": current.value.enabled === 1,
								"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => current.value.enabled = $event ? 1 : 0)
							}, null, 8, ["model-value"])]),
							_: 1
						}),
						createVNode(_component_el_divider, { "content-position": "left" }, {
							default: withCtx(() => [..._cache[11] || (_cache[11] = [createTextVNode("审批级别", -1)])]),
							_: 1
						}),
						levelsArr.value.length === 0 ? (openBlock(), createElementBlock("div", _hoisted_4, " 暂无审批级别，点击下方\"添加级别\"按钮新增 ")) : createCommentVNode("", true),
						(openBlock(true), createElementBlock(Fragment, null, renderList(levelsArr.value, (l, idx) => {
							return openBlock(), createElementBlock("div", {
								key: idx,
								class: "level-row"
							}, [
								createVNode(_component_el_tag, {
									class: "level-tag",
									type: "info"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(l.level), 1)]),
									_: 2
								}, 1024),
								createVNode(_component_el_input, {
									modelValue: l.name,
									"onUpdate:modelValue": ($event) => l.name = $event,
									placeholder: "级别名称（如 主管审批）",
									style: { "width": "200px" }
								}, null, 8, ["modelValue", "onUpdate:modelValue"]),
								createVNode(_component_el_select, {
									modelValue: l.approverRole,
									"onUpdate:modelValue": ($event) => l.approverRole = $event,
									placeholder: "审批角色编码",
									filterable: "",
									"allow-create": "",
									"default-first-option": "",
									style: { "width": "200px" }
								}, {
									default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(roles.value, (r) => {
										var _r$roleCode;
										return openBlock(), createBlock(_component_el_option, {
											key: r.id,
											label: `${r.roleName} (${r.roleCode})`,
											value: (_r$roleCode = r.roleCode) !== null && _r$roleCode !== void 0 ? _r$roleCode : ""
										}, null, 8, ["label", "value"]);
									}), 128))]),
									_: 1
								}, 8, ["modelValue", "onUpdate:modelValue"]),
								createVNode(_component_el_button_group, null, {
									default: withCtx(() => [createVNode(_component_el_button, {
										size: "small",
										disabled: idx === 0,
										onClick: ($event) => moveLevel(idx, -1)
									}, {
										default: withCtx(() => [..._cache[12] || (_cache[12] = [createTextVNode("↑", -1)])]),
										_: 1
									}, 8, ["disabled", "onClick"]), createVNode(_component_el_button, {
										size: "small",
										disabled: idx === levelsArr.value.length - 1,
										onClick: ($event) => moveLevel(idx, 1)
									}, {
										default: withCtx(() => [..._cache[13] || (_cache[13] = [createTextVNode("↓", -1)])]),
										_: 1
									}, 8, ["disabled", "onClick"])]),
									_: 2
								}, 1024),
								createVNode(_component_el_button, {
									size: "small",
									type: "danger",
									onClick: ($event) => removeLevel(idx)
								}, {
									default: withCtx(() => [..._cache[14] || (_cache[14] = [createTextVNode("删除", -1)])]),
									_: 1
								}, 8, ["onClick"])
							]);
						}), 128)),
						createVNode(_component_el_button, {
							type: "primary",
							plain: "",
							size: "small",
							style: { "margin-top": "8px" },
							onClick: addLevel
						}, {
							default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode(" + 添加级别 ", -1)])]),
							_: 1
						})
					]),
					_: 1
				}, 8, ["model"])) : createCommentVNode("", true)]),
				_: 1
			}, 8, ["modelValue", "title"])]);
		};
	}
}), [["__scopeId", "data-v-c1733d66"]]);
//#endregion
export { approval_chain_default as default };

//# sourceMappingURL=approval-chain-DDKalAmc.js.map