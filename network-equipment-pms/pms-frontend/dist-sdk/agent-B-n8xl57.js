import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { b as updateAgent, d as getAgentPage, f as getScoresByAgent, l as deleteAgent, s as createAgent, u as evaluateAgent } from "./implementation-DHYgyd55.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { createBlock, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withKeys, withModifiers } from "vue";
//#region src/views/implementation/agent/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "toolbar" };
var _hoisted_3 = { key: 1 };
//#endregion
//#region src/views/implementation/agent/index.vue
var agent_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
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
		const submitting = ref(false);
		const formRef = ref();
		const form = reactive(createEmptyForm());
		const rules = {
			agentName: [{
				required: true,
				message: "请输入代理商名称",
				trigger: "blur"
			}],
			agentCode: [{
				required: true,
				message: "请输入代理商编码",
				trigger: "blur"
			}]
		};
		function createEmptyForm() {
			return {
				agentName: "",
				agentCode: "",
				contactPerson: "",
				contactPhone: "",
				contactEmail: "",
				address: "",
				qualification: "",
				status: 1,
				remark: ""
			};
		}
		const evalVisible = ref(false);
		const evalSubmitting = ref(false);
		const evalForm = reactive({
			agentId: 0,
			agentName: "",
			taskId: void 0,
			responseSpeedScore: 8,
			constructionQualityScore: 8,
			documentCompletenessScore: 8,
			comment: ""
		});
		const historyVisible = ref(false);
		const historyLoading = ref(false);
		const historyData = ref([]);
		const historyAgentName = ref("");
		async function loadData() {
			loading.value = true;
			try {
				const res = await getAgentPage(query);
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
			dialogTitle.value = "新增代理商";
			Object.assign(form, createEmptyForm());
			dialogVisible.value = true;
		}
		function handleEdit(row) {
			dialogTitle.value = "编辑代理商";
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
						await updateAgent(form);
						ElMessage.success("更新成功");
					} else {
						await createAgent(form);
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
			ElMessageBox.confirm(`确定删除代理商「${row.agentName}」吗？`, "提示", { type: "warning" }).then(async () => {
				await deleteAgent(row.id);
				ElMessage.success("删除成功");
				loadData();
			}).catch(() => {});
		}
		function handleEvaluate(row) {
			if (!row.id) return;
			evalForm.agentId = row.id;
			evalForm.agentName = row.agentName;
			evalForm.taskId = void 0;
			evalForm.responseSpeedScore = 8;
			evalForm.constructionQualityScore = 8;
			evalForm.documentCompletenessScore = 8;
			evalForm.comment = "";
			evalVisible.value = true;
		}
		async function handleEvalSubmit() {
			evalSubmitting.value = true;
			try {
				await evaluateAgent({
					agentId: evalForm.agentId,
					taskId: evalForm.taskId,
					responseSpeedScore: evalForm.responseSpeedScore,
					constructionQualityScore: evalForm.constructionQualityScore,
					documentCompletenessScore: evalForm.documentCompletenessScore,
					comment: evalForm.comment
				});
				ElMessage.success("评价成功");
				evalVisible.value = false;
				loadData();
			} catch (_unused3) {} finally {
				evalSubmitting.value = false;
			}
		}
		async function handleViewHistory(row) {
			if (!row.id) return;
			historyAgentName.value = row.agentName;
			historyVisible.value = true;
			historyLoading.value = true;
			try {
				historyData.value = await getScoresByAgent(row.id);
			} catch (_unused4) {
				historyData.value = [];
			} finally {
				historyLoading.value = false;
			}
		}
		function avgScore(row) {
			var _row$responseSpeedSco, _row$constructionQual, _row$documentComplete;
			return ((((_row$responseSpeedSco = row.responseSpeedScore) !== null && _row$responseSpeedSco !== void 0 ? _row$responseSpeedSco : 0) + ((_row$constructionQual = row.constructionQualityScore) !== null && _row$constructionQual !== void 0 ? _row$constructionQual : 0) + ((_row$documentComplete = row.documentCompletenessScore) !== null && _row$documentComplete !== void 0 ? _row$documentComplete : 0)) / 3).toFixed(1);
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
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_pagination = resolveComponent("el-pagination");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_switch = resolveComponent("el-switch");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_rate = resolveComponent("el-rate");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, { shadow: "never" }, {
					header: withCtx(() => [..._cache[21] || (_cache[21] = [createElementVNode("span", { class: "page-title" }, "代理商管理", -1)])]),
					default: withCtx(() => [
						createVNode(_component_el_form, {
							inline: true,
							onSubmit: _cache[1] || (_cache[1] = withModifiers(() => {}, ["prevent"]))
						}, {
							default: withCtx(() => [createVNode(_component_el_form_item, { label: "关键字" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: query.keyword,
									"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => query.keyword = $event),
									placeholder: "代理商名称/编码",
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
									default: withCtx(() => [..._cache[22] || (_cache[22] = [createTextVNode("查询", -1)])]),
									_: 1
								}), createVNode(_component_el_button, {
									icon: "Refresh",
									onClick: handleReset
								}, {
									default: withCtx(() => [..._cache[23] || (_cache[23] = [createTextVNode("重置", -1)])]),
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
							default: withCtx(() => [..._cache[24] || (_cache[24] = [createTextVNode("新增代理商", -1)])]),
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
									prop: "agentName",
									label: "代理商名称",
									"min-width": "160",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "agentCode",
									label: "编码",
									"min-width": "120"
								}),
								createVNode(_component_el_table_column, {
									prop: "contactPerson",
									label: "联系人",
									"min-width": "100"
								}),
								createVNode(_component_el_table_column, {
									prop: "contactPhone",
									label: "联系电话",
									"min-width": "130"
								}),
								createVNode(_component_el_table_column, {
									prop: "contactEmail",
									label: "邮箱",
									"min-width": "180",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "address",
									label: "地址",
									"min-width": "180",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "qualification",
									label: "资质",
									"min-width": "120",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									label: "综合评分",
									width: "110",
									align: "center"
								}, {
									default: withCtx(({ row }) => [row.overallScore != null ? (openBlock(), createBlock(_component_el_tag, {
										key: 0,
										type: "warning",
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(row.overallScore.toFixed(1)), 1)]),
										_: 2
									}, 1024)) : (openBlock(), createElementBlock("span", _hoisted_3, "-"))]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "状态",
									width: "90"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, {
										type: row.status === 1 ? "success" : "info",
										size: "small"
									}, {
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
											onClick: ($event) => handleEdit(row)
										}, {
											default: withCtx(() => [..._cache[25] || (_cache[25] = [createTextVNode("编辑", -1)])]),
											_: 1
										}, 8, ["onClick"]),
										createVNode(_component_el_button, {
											link: "",
											type: "warning",
											onClick: ($event) => handleEvaluate(row)
										}, {
											default: withCtx(() => [..._cache[26] || (_cache[26] = [createTextVNode("评价", -1)])]),
											_: 1
										}, 8, ["onClick"]),
										createVNode(_component_el_button, {
											link: "",
											type: "info",
											onClick: ($event) => handleViewHistory(row)
										}, {
											default: withCtx(() => [..._cache[27] || (_cache[27] = [createTextVNode("评价记录", -1)])]),
											_: 1
										}, 8, ["onClick"]),
										createVNode(_component_el_button, {
											link: "",
											type: "danger",
											onClick: ($event) => handleDelete(row)
										}, {
											default: withCtx(() => [..._cache[28] || (_cache[28] = [createTextVNode("删除", -1)])]),
											_: 1
										}, 8, ["onClick"])
									]),
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
					"onUpdate:modelValue": _cache[12] || (_cache[12] = ($event) => dialogVisible.value = $event),
					title: dialogTitle.value,
					width: "560px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[11] || (_cache[11] = ($event) => dialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[29] || (_cache[29] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: submitting.value,
						onClick: handleSubmit
					}, {
						default: withCtx(() => [..._cache[30] || (_cache[30] = [createTextVNode("确定", -1)])]),
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
								label: "代理商名称",
								prop: "agentName"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.agentName,
									"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => form.agentName = $event),
									placeholder: "请输入代理商名称"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "代理商编码",
								prop: "agentCode"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.agentCode,
									"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => form.agentCode = $event),
									disabled: !!form.id,
									placeholder: "请输入代理商编码"
								}, null, 8, ["modelValue", "disabled"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "联系人",
								prop: "contactPerson"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.contactPerson,
									"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => form.contactPerson = $event),
									placeholder: "请输入联系人"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "联系电话",
								prop: "contactPhone"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.contactPhone,
									"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => form.contactPhone = $event),
									placeholder: "请输入联系电话"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "邮箱",
								prop: "contactEmail"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.contactEmail,
									"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => form.contactEmail = $event),
									placeholder: "请输入邮箱"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "地址",
								prop: "address"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.address,
									"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => form.address = $event),
									placeholder: "请输入地址"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "资质",
								prop: "qualification"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.qualification,
									"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => form.qualification = $event),
									placeholder: "请输入资质信息"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "状态",
								prop: "status"
							}, {
								default: withCtx(() => [createVNode(_component_el_switch, {
									modelValue: form.status,
									"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => form.status = $event),
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
									"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => form.remark = $event),
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
				}, 8, ["modelValue", "title"]),
				createVNode(_component_el_dialog, {
					modelValue: evalVisible.value,
					"onUpdate:modelValue": _cache[19] || (_cache[19] = ($event) => evalVisible.value = $event),
					title: `评价代理商 - ${evalForm.agentName}`,
					width: "520px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[18] || (_cache[18] = ($event) => evalVisible.value = false) }, {
						default: withCtx(() => [..._cache[31] || (_cache[31] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: evalSubmitting.value,
						onClick: handleEvalSubmit
					}, {
						default: withCtx(() => [..._cache[32] || (_cache[32] = [createTextVNode("提交评价", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, { "label-width": "120px" }, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, { label: "关联任务ID" }, {
								default: withCtx(() => [createVNode(_component_el_input_number, {
									modelValue: evalForm.taskId,
									"onUpdate:modelValue": _cache[13] || (_cache[13] = ($event) => evalForm.taskId = $event),
									min: 1,
									"controls-position": "right",
									placeholder: "可选",
									style: { "width": "100%" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "响应速度评分" }, {
								default: withCtx(() => [createVNode(_component_el_rate, {
									modelValue: evalForm.responseSpeedScore,
									"onUpdate:modelValue": _cache[14] || (_cache[14] = ($event) => evalForm.responseSpeedScore = $event),
									max: 10,
									"show-score": ""
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "施工质量评分" }, {
								default: withCtx(() => [createVNode(_component_el_rate, {
									modelValue: evalForm.constructionQualityScore,
									"onUpdate:modelValue": _cache[15] || (_cache[15] = ($event) => evalForm.constructionQualityScore = $event),
									max: 10,
									"show-score": ""
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "文档完整性评分" }, {
								default: withCtx(() => [createVNode(_component_el_rate, {
									modelValue: evalForm.documentCompletenessScore,
									"onUpdate:modelValue": _cache[16] || (_cache[16] = ($event) => evalForm.documentCompletenessScore = $event),
									max: 10,
									"show-score": ""
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "评价意见" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: evalForm.comment,
									"onUpdate:modelValue": _cache[17] || (_cache[17] = ($event) => evalForm.comment = $event),
									type: "textarea",
									rows: 3,
									placeholder: "请输入评价意见"
								}, null, 8, ["modelValue"])]),
								_: 1
							})
						]),
						_: 1
					})]),
					_: 1
				}, 8, ["modelValue", "title"]),
				createVNode(_component_el_dialog, {
					modelValue: historyVisible.value,
					"onUpdate:modelValue": _cache[20] || (_cache[20] = ($event) => historyVisible.value = $event),
					title: `评价记录 - ${historyAgentName.value}`,
					width: "760px",
					"destroy-on-close": ""
				}, {
					default: withCtx(() => [withDirectives((openBlock(), createBlock(_component_el_table, {
						data: historyData.value,
						border: "",
						stripe: "",
						size: "small"
					}, {
						empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无评价记录" })]),
						default: withCtx(() => [
							createVNode(_component_el_table_column, {
								prop: "scoreTime",
								label: "评价时间",
								"min-width": "160"
							}),
							createVNode(_component_el_table_column, {
								prop: "taskId",
								label: "任务ID",
								width: "100"
							}),
							createVNode(_component_el_table_column, {
								prop: "responseSpeedScore",
								label: "响应速度",
								width: "100",
								align: "center"
							}),
							createVNode(_component_el_table_column, {
								prop: "constructionQualityScore",
								label: "施工质量",
								width: "100",
								align: "center"
							}),
							createVNode(_component_el_table_column, {
								prop: "documentCompletenessScore",
								label: "文档完整性",
								width: "110",
								align: "center"
							}),
							createVNode(_component_el_table_column, {
								label: "综合评分",
								width: "100",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, {
									type: "warning",
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(avgScore(row)), 1)]),
									_: 2
								}, 1024)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								prop: "scorer",
								label: "评价人",
								"min-width": "100"
							}),
							createVNode(_component_el_table_column, {
								prop: "comment",
								label: "评价意见",
								"min-width": "180",
								"show-overflow-tooltip": ""
							})
						]),
						_: 1
					}, 8, ["data"])), [[_directive_loading, historyLoading.value]])]),
					_: 1
				}, 8, ["modelValue", "title"])
			]);
		};
	}
}), [["__scopeId", "data-v-609968b9"]]);
//#endregion
export { agent_default as default };

//# sourceMappingURL=agent-B-n8xl57.js.map