import { i as post, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage } from "element-plus";
import { Fragment, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withKeys, withModifiers } from "vue";
//#region src/api/change-request.ts
function listChangeRequests(params) {
	return get("/api/governance/change-request", params);
}
function createChangeRequest(data) {
	return post("/api/governance/change-request", data);
}
function approveChangeRequest(id, opinion) {
	return post(`/api/governance/change-request/${id}/approve`, { opinion });
}
function rejectChangeRequest(id, opinion) {
	return post(`/api/governance/change-request/${id}/reject`, { opinion });
}
function listBaselineHistory(changeRequestId) {
	return get(`/api/governance/change-request/${changeRequestId}/baseline-history`);
}
//#endregion
//#region src/views/change-request/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "toolbar" };
var _hoisted_3 = { class: "baseline-header" };
var _hoisted_4 = { class: "baseline-by" };
var _hoisted_5 = { class: "baseline-desc" };
var _hoisted_6 = { class: "baseline-change" };
var _hoisted_7 = { class: "old-value" };
var _hoisted_8 = { class: "new-value" };
//#endregion
//#region src/views/change-request/index.vue
var change_request_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "ChangeRequestManage",
	__name: "index",
	setup(__props) {
		const statusOptions = [
			{
				value: "SUBMITTED",
				label: "已提交",
				tagType: "info"
			},
			{
				value: "UNDER_REVIEW",
				label: "审核中",
				tagType: "warning"
			},
			{
				value: "CCB_APPROVED",
				label: "CCB 通过",
				tagType: "success"
			},
			{
				value: "CCB_REJECTED",
				label: "CCB 驳回",
				tagType: "danger"
			},
			{
				value: "IMPLEMENTING",
				label: "实施中",
				tagType: "primary"
			},
			{
				value: "CLOSED",
				label: "已关闭",
				tagType: "info"
			}
		];
		const priorityOptions = [
			{
				value: "LOW",
				label: "低",
				tagType: "info"
			},
			{
				value: "MEDIUM",
				label: "中",
				tagType: "success"
			},
			{
				value: "HIGH",
				label: "高",
				tagType: "warning"
			},
			{
				value: "CRITICAL",
				label: "紧急",
				tagType: "danger"
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
		function formatDate(date) {
			if (!date) return "-";
			return date.length > 10 ? date.substring(0, 10) : date;
		}
		const baselineTypeMap = {
			SCHEDULE: "进度基线",
			COST: "成本基线",
			SCOPE: "范围基线"
		};
		function baselineTypeLabel(type) {
			var _ref, _baselineTypeMap;
			return (_ref = (_baselineTypeMap = baselineTypeMap[type !== null && type !== void 0 ? type : ""]) !== null && _baselineTypeMap !== void 0 ? _baselineTypeMap : type) !== null && _ref !== void 0 ? _ref : "-";
		}
		const loading = ref(false);
		const tableData = ref([]);
		const total = ref(0);
		const query = reactive({
			page: 1,
			size: 10,
			projectId: void 0,
			status: ""
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
				const res = await listChangeRequests(params);
				tableData.value = (_res$records = res.records) !== null && _res$records !== void 0 ? _res$records : [];
				total.value = (_res$total = res.total) !== null && _res$total !== void 0 ? _res$total : 0;
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		function handleSearch() {
			query.page = 1;
			loadData();
		}
		function handleReset() {
			query.projectId = void 0;
			query.status = "";
			query.page = 1;
			loadData();
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
		const createVisible = ref(false);
		const createSubmitting = ref(false);
		const createFormRef = ref();
		function createEmptyForm() {
			return {
				projectId: void 0,
				title: "",
				description: "",
				impactScope: "",
				impactSchedule: "",
				impactCost: "",
				impactQuality: "",
				priority: "MEDIUM"
			};
		}
		const createForm = reactive(createEmptyForm());
		const createRules = {
			projectId: [{
				required: true,
				message: "请输入项目 ID",
				trigger: "blur"
			}],
			title: [{
				required: true,
				message: "请输入变更标题",
				trigger: "blur"
			}],
			priority: [{
				required: true,
				message: "请选择优先级",
				trigger: "change"
			}]
		};
		function handleAdd() {
			Object.assign(createForm, createEmptyForm());
			createVisible.value = true;
		}
		async function handleCreateSubmit() {
			if (!createFormRef.value) return;
			await createFormRef.value.validate(async (valid) => {
				if (!valid) return;
				createSubmitting.value = true;
				try {
					await createChangeRequest({
						projectId: createForm.projectId,
						title: createForm.title,
						description: createForm.description,
						impactScope: createForm.impactScope,
						impactSchedule: createForm.impactSchedule,
						impactCost: createForm.impactCost,
						impactQuality: createForm.impactQuality,
						priority: createForm.priority,
						status: "SUBMITTED"
					});
					ElMessage.success("新建成功");
					createVisible.value = false;
					loadData();
				} catch (_unused2) {} finally {
					createSubmitting.value = false;
				}
			});
		}
		const opinionVisible = ref(false);
		const opinionTitle = ref("");
		const opinionSubmitting = ref(false);
		const opinionForm = reactive({
			id: 0,
			opinion: ""
		});
		function handleApprove(row) {
			if (!row.id) return;
			opinionTitle.value = "审批通过";
			opinionForm.id = row.id;
			opinionForm.opinion = "";
			opinionVisible.value = true;
		}
		function handleReject(row) {
			if (!row.id) return;
			opinionTitle.value = "驳回变更";
			opinionForm.id = row.id;
			opinionForm.opinion = "";
			opinionVisible.value = true;
		}
		async function handleOpinionSubmit() {
			if (!opinionForm.opinion.trim()) {
				ElMessage.warning("请输入审批意见");
				return;
			}
			opinionSubmitting.value = true;
			try {
				if (opinionTitle.value === "审批通过") {
					await approveChangeRequest(opinionForm.id, opinionForm.opinion);
					ElMessage.success("审批通过");
				} else {
					await rejectChangeRequest(opinionForm.id, opinionForm.opinion);
					ElMessage.success("已驳回");
				}
				opinionVisible.value = false;
				loadData();
			} catch (_unused3) {} finally {
				opinionSubmitting.value = false;
			}
		}
		const baselineVisible = ref(false);
		const baselineLoading = ref(false);
		const baselineList = ref([]);
		const baselineTitle = ref("");
		async function handleViewBaseline(row) {
			var _row$crNo;
			if (!row.id) return;
			baselineTitle.value = `基线变更历史 - ${(_row$crNo = row.crNo) !== null && _row$crNo !== void 0 ? _row$crNo : ""}`;
			baselineVisible.value = true;
			baselineLoading.value = true;
			try {
				baselineList.value = await listBaselineHistory(row.id);
			} catch (_unused4) {
				baselineList.value = [];
			} finally {
				baselineLoading.value = false;
			}
		}
		function baselineTagType(type) {
			switch (type) {
				case "SCHEDULE": return "primary";
				case "COST": return "warning";
				case "SCOPE": return "success";
				default: return "info";
			}
		}
		onMounted(loadData);
		return (_ctx, _cache) => {
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
			const _component_el_card = resolveComponent("el-card");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _component_el_timeline_item = resolveComponent("el-timeline-item");
			const _component_el_timeline = resolveComponent("el-timeline");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, { shadow: "never" }, {
					header: withCtx(() => [..._cache[18] || (_cache[18] = [createElementVNode("span", { class: "page-title" }, "变更管理", -1)])]),
					default: withCtx(() => [
						createVNode(_component_el_form, {
							inline: true,
							onSubmit: _cache[2] || (_cache[2] = withModifiers(() => {}, ["prevent"]))
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
										style: { "width": "160px" }
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
						createElementVNode("div", _hoisted_2, [createVNode(_component_el_button, {
							type: "primary",
							icon: "Plus",
							onClick: handleAdd
						}, {
							default: withCtx(() => [..._cache[21] || (_cache[21] = [createTextVNode("新建变更", -1)])]),
							_: 1
						})]),
						withDirectives((openBlock(), createBlock(_component_el_table, {
							data: tableData.value,
							border: "",
							stripe: ""
						}, {
							empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无变更请求" })]),
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									prop: "crNo",
									label: "CR 编号",
									width: "140"
								}),
								createVNode(_component_el_table_column, {
									prop: "title",
									label: "变更标题",
									"min-width": "200",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "projectId",
									label: "项目 ID",
									width: "90",
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
									width: "120",
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
									prop: "requesterName",
									label: "申请人",
									width: "110"
								}),
								createVNode(_component_el_table_column, {
									label: "申请日期",
									width: "120",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDate(row.requestDate)), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "操作",
									width: "260",
									fixed: "right"
								}, {
									default: withCtx(({ row }) => [
										row.status === "SUBMITTED" || row.status === "UNDER_REVIEW" ? (openBlock(), createBlock(_component_el_button, {
											key: 0,
											link: "",
											type: "success",
											onClick: ($event) => handleApprove(row)
										}, {
											default: withCtx(() => [..._cache[22] || (_cache[22] = [createTextVNode(" 审批通过 ", -1)])]),
											_: 1
										}, 8, ["onClick"])) : createCommentVNode("", true),
										row.status === "SUBMITTED" || row.status === "UNDER_REVIEW" ? (openBlock(), createBlock(_component_el_button, {
											key: 1,
											link: "",
											type: "danger",
											onClick: ($event) => handleReject(row)
										}, {
											default: withCtx(() => [..._cache[23] || (_cache[23] = [createTextVNode(" 驳回 ", -1)])]),
											_: 1
										}, 8, ["onClick"])) : createCommentVNode("", true),
										createVNode(_component_el_button, {
											link: "",
											type: "primary",
											onClick: ($event) => handleViewBaseline(row)
										}, {
											default: withCtx(() => [..._cache[24] || (_cache[24] = [createTextVNode("基线历史", -1)])]),
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
					modelValue: createVisible.value,
					"onUpdate:modelValue": _cache[12] || (_cache[12] = ($event) => createVisible.value = $event),
					title: "新建变更请求",
					width: "680px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[11] || (_cache[11] = ($event) => createVisible.value = false) }, {
						default: withCtx(() => [..._cache[25] || (_cache[25] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: createSubmitting.value,
						onClick: handleCreateSubmit
					}, {
						default: withCtx(() => [..._cache[26] || (_cache[26] = [createTextVNode("确定", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "createFormRef",
						ref: createFormRef,
						model: createForm,
						rules: createRules,
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
											modelValue: createForm.projectId,
											"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => createForm.projectId = $event),
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
										label: "优先级",
										prop: "priority"
									}, {
										default: withCtx(() => [createVNode(_component_el_select, {
											modelValue: createForm.priority,
											"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => createForm.priority = $event),
											placeholder: "请选择",
											style: { "width": "100%" }
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
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 24 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "变更标题",
										prop: "title"
									}, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: createForm.title,
											"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => createForm.title = $event),
											placeholder: "请输入变更标题"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 24 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, { label: "变更描述" }, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: createForm.description,
											"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => createForm.description = $event),
											type: "textarea",
											rows: 2,
											placeholder: "请输入变更描述"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 12 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, { label: "影响范围" }, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: createForm.impactScope,
											"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => createForm.impactScope = $event),
											type: "textarea",
											rows: 2,
											placeholder: "请输入影响范围"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 12 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, { label: "影响进度" }, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: createForm.impactSchedule,
											"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => createForm.impactSchedule = $event),
											type: "textarea",
											rows: 2,
											placeholder: "请输入影响进度"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 12 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, { label: "影响成本" }, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: createForm.impactCost,
											"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => createForm.impactCost = $event),
											type: "textarea",
											rows: 2,
											placeholder: "请输入影响成本"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 12 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, { label: "影响质量" }, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: createForm.impactQuality,
											"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => createForm.impactQuality = $event),
											type: "textarea",
											rows: 2,
											placeholder: "请输入影响质量"
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
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: opinionVisible.value,
					"onUpdate:modelValue": _cache[15] || (_cache[15] = ($event) => opinionVisible.value = $event),
					title: opinionTitle.value,
					width: "480px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[14] || (_cache[14] = ($event) => opinionVisible.value = false) }, {
						default: withCtx(() => [..._cache[27] || (_cache[27] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: opinionSubmitting.value,
						onClick: handleOpinionSubmit
					}, {
						default: withCtx(() => [..._cache[28] || (_cache[28] = [createTextVNode(" 确定 ", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, { "label-width": "80px" }, {
						default: withCtx(() => [createVNode(_component_el_form_item, {
							label: "审批意见",
							required: ""
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: opinionForm.opinion,
								"onUpdate:modelValue": _cache[13] || (_cache[13] = ($event) => opinionForm.opinion = $event),
								type: "textarea",
								rows: 4,
								placeholder: "请输入审批意见"
							}, null, 8, ["modelValue"])]),
							_: 1
						})]),
						_: 1
					})]),
					_: 1
				}, 8, ["modelValue", "title"]),
				createVNode(_component_el_dialog, {
					modelValue: baselineVisible.value,
					"onUpdate:modelValue": _cache[17] || (_cache[17] = ($event) => baselineVisible.value = $event),
					title: baselineTitle.value,
					width: "640px"
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[16] || (_cache[16] = ($event) => baselineVisible.value = false) }, {
						default: withCtx(() => [..._cache[30] || (_cache[30] = [createTextVNode("关闭", -1)])]),
						_: 1
					})]),
					default: withCtx(() => [withDirectives((openBlock(), createElementBlock("div", null, [baselineList.value.length > 0 ? (openBlock(), createBlock(_component_el_timeline, { key: 0 }, {
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(baselineList.value, (item) => {
							return openBlock(), createBlock(_component_el_timeline_item, {
								key: item.id,
								timestamp: item.changedAt,
								placement: "top"
							}, {
								default: withCtx(() => [createVNode(_component_el_card, { shadow: "never" }, {
									default: withCtx(() => [
										createElementVNode("div", _hoisted_3, [createVNode(_component_el_tag, {
											type: baselineTagType(item.baselineType),
											size: "small"
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(baselineTypeLabel(item.baselineType)), 1)]),
											_: 2
										}, 1032, ["type"]), createElementVNode("span", _hoisted_4, "变更人：" + toDisplayString(item.changedBy), 1)]),
										createElementVNode("div", _hoisted_5, toDisplayString(item.changeDescription), 1),
										createElementVNode("div", _hoisted_6, [
											createElementVNode("span", _hoisted_7, toDisplayString(item.oldValue || "（空）"), 1),
											_cache[29] || (_cache[29] = createElementVNode("span", { class: "arrow" }, "→", -1)),
											createElementVNode("span", _hoisted_8, toDisplayString(item.newValue || "（空）"), 1)
										])
									]),
									_: 2
								}, 1024)]),
								_: 2
							}, 1032, ["timestamp"]);
						}), 128))]),
						_: 1
					})) : (openBlock(), createBlock(_component_el_empty, {
						key: 1,
						description: "暂无基线变更历史"
					}))])), [[_directive_loading, baselineLoading.value]])]),
					_: 1
				}, 8, ["modelValue", "title"])
			]);
		};
	}
}), [["__scopeId", "data-v-30d3f90a"]]);
//#endregion
export { change_request_default as default };

//# sourceMappingURL=change-request-BkGnK-B-.js.map