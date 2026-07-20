import { a as put, i as post, n as del, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { t as init } from "./echarts-D5mnZUDD.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, nextTick, onBeforeUnmount, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withKeys, withModifiers } from "vue";
//#region src/api/risk.ts
function listRisks(params) {
	return get("/api/governance/risk", params);
}
function createRisk(data) {
	return post("/api/governance/risk", data);
}
function updateRisk(data) {
	return put("/api/governance/risk", data);
}
function markOccurred(id) {
	return post(`/api/governance/risk/${id}/mark-occurred`);
}
function getRiskMatrix(projectId) {
	return get("/api/governance/risk/matrix", { projectId });
}
function deleteRisk(id) {
	return del(`/api/governance/risk/${id}`);
}
//#endregion
//#region src/views/risk/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "matrix-section" };
var _hoisted_3 = { class: "toolbar" };
//#endregion
//#region src/views/risk/index.vue
var risk_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "RiskRegister",
	__name: "index",
	setup(__props) {
		const statusOptions = [
			{
				value: "OPEN",
				label: "待处理",
				tagType: "info"
			},
			{
				value: "IN_PROGRESS",
				label: "处理中",
				tagType: "primary"
			},
			{
				value: "CLOSED",
				label: "已关闭",
				tagType: "success"
			},
			{
				value: "ESCALATED",
				label: "已升级",
				tagType: "danger"
			}
		];
		const priorityOptions = [
			{
				value: "LOW",
				label: "低",
				tagType: "success"
			},
			{
				value: "MEDIUM",
				label: "中",
				tagType: "warning"
			},
			{
				value: "HIGH",
				label: "高",
				tagType: "danger"
			}
		];
		const categoryOptions = [
			{
				value: "TECHNICAL",
				label: "技术"
			},
			{
				value: "EXTERNAL",
				label: "外部"
			},
			{
				value: "ORGANIZATIONAL",
				label: "组织"
			},
			{
				value: "PM",
				label: "项目管理"
			}
		];
		const mitigationOptions = [
			{
				value: "AVOID",
				label: "规避"
			},
			{
				value: "MITIGATE",
				label: "缓解"
			},
			{
				value: "TRANSFER",
				label: "转移"
			},
			{
				value: "ACCEPT",
				label: "接受"
			}
		];
		function getStatusMeta(status) {
			var _statusOptions$find;
			return (_statusOptions$find = statusOptions.find((s) => s.value === status)) !== null && _statusOptions$find !== void 0 ? _statusOptions$find : {
				label: status !== null && status !== void 0 ? status : "-",
				tagType: "info"
			};
		}
		function getPriorityMeta(priority) {
			var _priorityOptions$find;
			return (_priorityOptions$find = priorityOptions.find((p) => p.value === priority)) !== null && _priorityOptions$find !== void 0 ? _priorityOptions$find : {
				label: priority !== null && priority !== void 0 ? priority : "-",
				tagType: "info"
			};
		}
		function getCategoryLabel(category) {
			var _ref, _categoryOptions$find, _categoryOptions$find2;
			return (_ref = (_categoryOptions$find = (_categoryOptions$find2 = categoryOptions.find((c) => c.value === category)) === null || _categoryOptions$find2 === void 0 ? void 0 : _categoryOptions$find2.label) !== null && _categoryOptions$find !== void 0 ? _categoryOptions$find : category) !== null && _ref !== void 0 ? _ref : "-";
		}
		const matrixChartRef = ref();
		let chartInstance = null;
		const matrixData = ref([]);
		const cellDialogVisible = ref(false);
		const cellDialogTitle = ref("");
		const cellRisks = ref([]);
		function scoreToPriority(score) {
			if (score <= 6) return "LOW";
			if (score <= 12) return "MEDIUM";
			return "HIGH";
		}
		async function loadMatrix() {
			try {
				const matrix = await getRiskMatrix(query.projectId);
				const flat = [];
				if (Array.isArray(matrix)) matrix.forEach((row) => {
					if (Array.isArray(row)) flat.push(...row);
				});
				matrixData.value = flat;
				nextTick(() => renderMatrix());
			} catch (_unused) {}
		}
		function renderMatrix() {
			if (!matrixChartRef.value) return;
			if (!chartInstance) {
				chartInstance = init(matrixChartRef.value);
				chartInstance.on("click", handleMatrixClick);
			}
			const data = matrixData.value.map((cell) => {
				const score = cell.likelihood * cell.impact;
				return {
					value: [
						cell.impact - 1,
						cell.likelihood - 1,
						score
					],
					cellData: cell
				};
			});
			chartInstance.setOption({
				tooltip: { formatter: (params) => {
					const cell = params.data.cellData;
					const score = cell.likelihood * cell.impact;
					return [
						`概率：${cell.likelihood}`,
						`影响：${cell.impact}`,
						`分数：${score}（${scoreToPriority(score)}）`,
						`风险数量：${cell.count}`
					].join("<br/>");
				} },
				grid: {
					left: 60,
					right: 30,
					top: 30,
					bottom: 50
				},
				xAxis: {
					type: "category",
					data: [
						"1",
						"2",
						"3",
						"4",
						"5"
					],
					name: "影响",
					nameLocation: "middle",
					nameGap: 30,
					splitArea: { show: true }
				},
				yAxis: {
					type: "category",
					data: [
						"1",
						"2",
						"3",
						"4",
						"5"
					],
					name: "概率",
					nameLocation: "middle",
					nameGap: 40,
					splitArea: { show: true }
				},
				visualMap: {
					show: false,
					min: 1,
					max: 25,
					calculable: true,
					orient: "horizontal",
					left: "center",
					bottom: "0",
					inRange: { color: [
						"#67C23A",
						"#E6A23C",
						"#F56C6C"
					] }
				},
				series: [{
					name: "风险矩阵",
					type: "heatmap",
					data,
					label: {
						show: true,
						formatter: (params) => {
							return params.data.cellData.count;
						}
					},
					emphasis: { itemStyle: {
						shadowBlur: 10,
						shadowColor: "rgba(0, 0, 0, 0.5)"
					} }
				}]
			}, { notMerge: true });
		}
		function handleMatrixClick(params) {
			var _params$data;
			const cell = params === null || params === void 0 || (_params$data = params.data) === null || _params$data === void 0 ? void 0 : _params$data.cellData;
			if (!cell || !cell.risks || cell.risks.length === 0) {
				ElMessage.info("该格子暂无风险");
				return;
			}
			cellDialogTitle.value = `概率${cell.likelihood} × 影响${cell.impact}（共${cell.count}项）`;
			cellRisks.value = cell.risks;
			cellDialogVisible.value = true;
		}
		function handleResize() {
			chartInstance === null || chartInstance === void 0 || chartInstance.resize();
		}
		const loading = ref(false);
		const tableData = ref([]);
		const total = ref(0);
		const query = reactive({
			page: 1,
			size: 10,
			projectId: void 0,
			status: "",
			priority: ""
		});
		async function loadData() {
			loading.value = true;
			try {
				var _res$records, _res$total;
				const params = {
					page: query.page,
					size: query.size
				};
				if (query.projectId) params.projectId = query.projectId;
				if (query.status) params.status = query.status;
				if (query.priority) params.priority = query.priority;
				const res = await listRisks(params);
				tableData.value = (_res$records = res.records) !== null && _res$records !== void 0 ? _res$records : [];
				total.value = (_res$total = res.total) !== null && _res$total !== void 0 ? _res$total : 0;
			} catch (_unused2) {} finally {
				loading.value = false;
			}
		}
		function handleSearch() {
			query.page = 1;
			loadData();
			loadMatrix();
		}
		function handleReset() {
			query.projectId = void 0;
			query.status = "";
			query.priority = "";
			query.page = 1;
			loadData();
			loadMatrix();
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
		const dialogVisible = ref(false);
		const dialogTitle = ref("");
		const submitting = ref(false);
		const formRef = ref();
		function createEmptyForm() {
			return {
				id: void 0,
				projectId: void 0,
				description: "",
				category: "TECHNICAL",
				likelihood: 3,
				impact: 3,
				mitigation: "MITIGATE",
				contingencyPlan: "",
				ownerId: void 0,
				status: "OPEN",
				reviewDate: ""
			};
		}
		const form = reactive(createEmptyForm());
		const rules = {
			projectId: [{
				required: true,
				message: "请输入项目 ID",
				trigger: "blur"
			}],
			description: [{
				required: true,
				message: "请输入风险描述",
				trigger: "blur"
			}],
			category: [{
				required: true,
				message: "请选择风险分类",
				trigger: "change"
			}],
			likelihood: [{
				required: true,
				message: "请选择概率",
				trigger: "change"
			}],
			impact: [{
				required: true,
				message: "请选择影响",
				trigger: "change"
			}],
			mitigation: [{
				required: true,
				message: "请选择缓解策略",
				trigger: "change"
			}]
		};
		function handleAdd() {
			dialogTitle.value = "新建风险";
			Object.assign(form, createEmptyForm());
			dialogVisible.value = true;
		}
		function handleEdit(row) {
			var _row$contingencyPlan, _row$reviewDate;
			dialogTitle.value = "编辑风险";
			Object.assign(form, createEmptyForm(), {
				id: row.id,
				projectId: row.projectId,
				description: row.description,
				category: row.category,
				likelihood: row.likelihood,
				impact: row.impact,
				mitigation: row.mitigation,
				contingencyPlan: (_row$contingencyPlan = row.contingencyPlan) !== null && _row$contingencyPlan !== void 0 ? _row$contingencyPlan : "",
				ownerId: row.ownerId,
				status: row.status,
				reviewDate: (_row$reviewDate = row.reviewDate) !== null && _row$reviewDate !== void 0 ? _row$reviewDate : ""
			});
			dialogVisible.value = true;
		}
		async function handleSubmit() {
			if (!formRef.value) return;
			await formRef.value.validate(async (valid) => {
				if (!valid) return;
				submitting.value = true;
				try {
					const payload = {
						id: form.id,
						projectId: form.projectId,
						description: form.description,
						category: form.category,
						likelihood: form.likelihood,
						impact: form.impact,
						mitigation: form.mitigation,
						contingencyPlan: form.contingencyPlan,
						ownerId: form.ownerId,
						status: form.status,
						reviewDate: form.reviewDate
					};
					if (form.id) {
						await updateRisk(payload);
						ElMessage.success("更新成功");
					} else {
						await createRisk(payload);
						ElMessage.success("新建成功");
					}
					dialogVisible.value = false;
					loadData();
					loadMatrix();
				} catch (_unused3) {} finally {
					submitting.value = false;
				}
			});
		}
		function handleMarkOccurred(row) {
			var _row$riskNo;
			if (!row.id) return;
			ElMessageBox.confirm(`确认将风险「${(_row$riskNo = row.riskNo) !== null && _row$riskNo !== void 0 ? _row$riskNo : row.description}」标记为已发生并转为问题吗？`, "标记已发生", { type: "warning" }).then(async () => {
				await markOccurred(row.id);
				ElMessage.success("已标记为发生并转为问题");
				loadData();
				loadMatrix();
			}).catch(() => {});
		}
		function handleDelete(row) {
			var _row$riskNo2;
			if (!row.id) return;
			ElMessageBox.confirm(`确定删除风险「${(_row$riskNo2 = row.riskNo) !== null && _row$riskNo2 !== void 0 ? _row$riskNo2 : row.description}」吗？`, "提示", { type: "warning" }).then(async () => {
				await deleteRisk(row.id);
				ElMessage.success("删除成功");
				loadData();
				loadMatrix();
			}).catch(() => {});
		}
		onMounted(() => {
			loadData();
			loadMatrix();
			window.addEventListener("resize", handleResize);
		});
		onBeforeUnmount(() => {
			window.removeEventListener("resize", handleResize);
			chartInstance === null || chartInstance === void 0 || chartInstance.dispose();
			chartInstance = null;
		});
		return (_ctx, _cache) => {
			const _component_el_card = resolveComponent("el-card");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_pagination = resolveComponent("el-pagination");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_date_picker = resolveComponent("el-date-picker");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, { shadow: "never" }, {
					header: withCtx(() => [..._cache[17] || (_cache[17] = [createElementVNode("span", { class: "page-title" }, "风险登记册", -1)])]),
					default: withCtx(() => [createElementVNode("div", _hoisted_2, [createElementVNode("div", {
						ref_key: "matrixChartRef",
						ref: matrixChartRef,
						class: "matrix-chart"
					}, null, 512), _cache[18] || (_cache[18] = createElementVNode("div", { class: "matrix-legend" }, [
						createElementVNode("div", { class: "legend-item" }, [createElementVNode("span", {
							class: "legend-color",
							style: { "background": "#67C23A" }
						}), createElementVNode("span", null, "LOW（分数 ≤ 6）")]),
						createElementVNode("div", { class: "legend-item" }, [createElementVNode("span", {
							class: "legend-color",
							style: { "background": "#E6A23C" }
						}), createElementVNode("span", null, "MEDIUM（分数 7-12）")]),
						createElementVNode("div", { class: "legend-item" }, [createElementVNode("span", {
							class: "legend-color",
							style: { "background": "#F56C6C" }
						}), createElementVNode("span", null, "HIGH（分数 13-25）")]),
						createElementVNode("div", { class: "legend-tip" }, "点击格子查看该区间的风险列表")
					], -1))])]),
					_: 1
				}),
				createVNode(_component_el_card, { shadow: "never" }, {
					default: withCtx(() => [
						createVNode(_component_el_form, {
							inline: true,
							onSubmit: _cache[3] || (_cache[3] = withModifiers(() => {}, ["prevent"]))
						}, {
							default: withCtx(() => [
								createVNode(_component_el_form_item, { label: "项目 ID" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: query.projectId,
										"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => query.projectId = $event),
										modelModifiers: { number: true },
										placeholder: "请输入项目 ID",
										clearable: "",
										style: { "width": "160px" },
										onKeyup: withKeys(handleSearch, ["enter"])
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "状态" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: query.status,
										"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => query.status = $event),
										placeholder: "全部状态",
										clearable: "",
										style: { "width": "140px" }
									}, {
										default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(statusOptions, (opt) => {
											return createVNode(_component_el_option, {
												key: opt.value,
												label: opt.label,
												value: opt.value
											}, null, 8, ["label", "value"]);
										}), 64))]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "优先级" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: query.priority,
										"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => query.priority = $event),
										placeholder: "全部优先级",
										clearable: "",
										style: { "width": "140px" }
									}, {
										default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(priorityOptions, (opt) => {
											return createVNode(_component_el_option, {
												key: opt.value,
												label: opt.label,
												value: opt.value
											}, null, 8, ["label", "value"]);
										}), 64))]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, null, {
									default: withCtx(() => [createVNode(_component_el_button, {
										type: "primary",
										icon: "Search",
										onClick: handleSearch
									}, {
										default: withCtx(() => [..._cache[19] || (_cache[19] = [createTextVNode("查询", -1)])]),
										_: 1
									}), createVNode(_component_el_button, {
										icon: "Refresh",
										onClick: handleReset
									}, {
										default: withCtx(() => [..._cache[20] || (_cache[20] = [createTextVNode("重置", -1)])]),
										_: 1
									})]),
									_: 1
								})
							]),
							_: 1
						}),
						createElementVNode("div", _hoisted_3, [createVNode(_component_el_button, {
							type: "primary",
							icon: "Plus",
							onClick: handleAdd
						}, {
							default: withCtx(() => [..._cache[21] || (_cache[21] = [createTextVNode("新建风险", -1)])]),
							_: 1
						})]),
						withDirectives((openBlock(), createBlock(_component_el_table, {
							data: tableData.value,
							border: "",
							stripe: ""
						}, {
							empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无风险数据" })]),
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									prop: "riskNo",
									label: "风险编号",
									width: "130"
								}),
								createVNode(_component_el_table_column, {
									prop: "description",
									label: "风险描述",
									"min-width": "200",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									label: "分类",
									width: "100",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createTextVNode(toDisplayString(getCategoryLabel(row.category)), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									prop: "likelihood",
									label: "概率",
									width: "80",
									align: "center"
								}),
								createVNode(_component_el_table_column, {
									prop: "impact",
									label: "影响",
									width: "80",
									align: "center"
								}),
								createVNode(_component_el_table_column, {
									prop: "score",
									label: "分数",
									width: "80",
									align: "center"
								}),
								createVNode(_component_el_table_column, {
									label: "优先级",
									width: "100",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, {
										type: getPriorityMeta(row.priority).tagType,
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(getPriorityMeta(row.priority).label), 1)]),
										_: 2
									}, 1032, ["type"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "状态",
									width: "100",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, {
										type: getStatusMeta(row.status).tagType,
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(getStatusMeta(row.status).label), 1)]),
										_: 2
									}, 1032, ["type"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									prop: "ownerName",
									label: "负责人",
									width: "110"
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
											default: withCtx(() => [..._cache[22] || (_cache[22] = [createTextVNode("编辑", -1)])]),
											_: 1
										}, 8, ["onClick"]),
										row.status !== "CLOSED" ? (openBlock(), createBlock(_component_el_button, {
											key: 0,
											link: "",
											type: "warning",
											onClick: ($event) => handleMarkOccurred(row)
										}, {
											default: withCtx(() => [..._cache[23] || (_cache[23] = [createTextVNode(" 标记已发生 ", -1)])]),
											_: 1
										}, 8, ["onClick"])) : createCommentVNode("", true),
										createVNode(_component_el_button, {
											link: "",
											type: "danger",
											onClick: ($event) => handleDelete(row)
										}, {
											default: withCtx(() => [..._cache[24] || (_cache[24] = [createTextVNode("删除", -1)])]),
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
					"onUpdate:modelValue": _cache[14] || (_cache[14] = ($event) => dialogVisible.value = $event),
					title: dialogTitle.value,
					width: "640px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[13] || (_cache[13] = ($event) => dialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[25] || (_cache[25] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: submitting.value,
						onClick: handleSubmit
					}, {
						default: withCtx(() => [..._cache[26] || (_cache[26] = [createTextVNode("确定", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "formRef",
						ref: formRef,
						model: form,
						rules,
						"label-width": "100px"
					}, {
						default: withCtx(() => [createVNode(_component_el_row, { gutter: 16 }, {
							default: withCtx(() => [
								createVNode(_component_el_col, { span: 12 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "项目 ID",
										prop: "projectId"
									}, {
										default: withCtx(() => [createVNode(_component_el_input_number, {
											modelValue: form.projectId,
											"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => form.projectId = $event),
											min: 1,
											"controls-position": "right",
											style: { "width": "100%" }
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 12 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "风险分类",
										prop: "category"
									}, {
										default: withCtx(() => [createVNode(_component_el_select, {
											modelValue: form.category,
											"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => form.category = $event),
											placeholder: "请选择",
											style: { "width": "100%" }
										}, {
											default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(categoryOptions, (opt) => {
												return createVNode(_component_el_option, {
													key: opt.value,
													label: opt.label,
													value: opt.value
												}, null, 8, ["label", "value"]);
											}), 64))]),
											_: 1
										}, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 24 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "风险描述",
										prop: "description"
									}, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: form.description,
											"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => form.description = $event),
											type: "textarea",
											rows: 2,
											placeholder: "请输入风险描述"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 12 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "概率",
										prop: "likelihood"
									}, {
										default: withCtx(() => [createVNode(_component_el_input_number, {
											modelValue: form.likelihood,
											"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => form.likelihood = $event),
											min: 1,
											max: 5,
											style: { "width": "100%" }
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 12 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "影响",
										prop: "impact"
									}, {
										default: withCtx(() => [createVNode(_component_el_input_number, {
											modelValue: form.impact,
											"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => form.impact = $event),
											min: 1,
											max: 5,
											style: { "width": "100%" }
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 12 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "缓解策略",
										prop: "mitigation"
									}, {
										default: withCtx(() => [createVNode(_component_el_select, {
											modelValue: form.mitigation,
											"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => form.mitigation = $event),
											placeholder: "请选择",
											style: { "width": "100%" }
										}, {
											default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(mitigationOptions, (opt) => {
												return createVNode(_component_el_option, {
													key: opt.value,
													label: opt.label,
													value: opt.value
												}, null, 8, ["label", "value"]);
											}), 64))]),
											_: 1
										}, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 12 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, { label: "负责人 ID" }, {
										default: withCtx(() => [createVNode(_component_el_input_number, {
											modelValue: form.ownerId,
											"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => form.ownerId = $event),
											min: 1,
											"controls-position": "right",
											style: { "width": "100%" }
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 12 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, { label: "复审日期" }, {
										default: withCtx(() => [createVNode(_component_el_date_picker, {
											modelValue: form.reviewDate,
											"onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => form.reviewDate = $event),
											type: "date",
											"value-format": "YYYY-MM-DD",
											placeholder: "选择复审日期",
											style: { "width": "100%" }
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 24 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, { label: "应急预案" }, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: form.contingencyPlan,
											"onUpdate:modelValue": _cache[12] || (_cache[12] = ($event) => form.contingencyPlan = $event),
											type: "textarea",
											rows: 3,
											placeholder: "请输入应急预案"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								})
							]),
							_: 1
						})]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue", "title"]),
				createVNode(_component_el_dialog, {
					modelValue: cellDialogVisible.value,
					"onUpdate:modelValue": _cache[16] || (_cache[16] = ($event) => cellDialogVisible.value = $event),
					title: cellDialogTitle.value,
					width: "640px"
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[15] || (_cache[15] = ($event) => cellDialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[27] || (_cache[27] = [createTextVNode("关闭", -1)])]),
						_: 1
					})]),
					default: withCtx(() => [createVNode(_component_el_table, {
						data: cellRisks.value,
						border: "",
						stripe: "",
						size: "small"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_table_column, {
								prop: "riskNo",
								label: "风险编号",
								width: "130"
							}),
							createVNode(_component_el_table_column, {
								prop: "description",
								label: "风险描述",
								"min-width": "200",
								"show-overflow-tooltip": ""
							}),
							createVNode(_component_el_table_column, {
								label: "分数",
								width: "80",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createTextVNode(toDisplayString(row.likelihood * row.impact), 1)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "优先级",
								width: "100",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, {
									type: getPriorityMeta(row.priority).tagType,
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(getPriorityMeta(row.priority).label), 1)]),
									_: 2
								}, 1032, ["type"])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "状态",
								width: "100",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, {
									type: getStatusMeta(row.status).tagType,
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(getStatusMeta(row.status).label), 1)]),
									_: 2
								}, 1032, ["type"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["data"])]),
					_: 1
				}, 8, ["modelValue", "title"])
			]);
		};
	}
}), [["__scopeId", "data-v-6e305a35"]]);
//#endregion
export { risk_default as default };

//# sourceMappingURL=risk-DTpsLOz3.js.map