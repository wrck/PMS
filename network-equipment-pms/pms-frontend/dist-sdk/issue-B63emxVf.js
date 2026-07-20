import { i as post, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withKeys, withModifiers } from "vue";
//#region src/api/issue.ts
function listIssues(params) {
	return get("/api/governance/issue", params);
}
function createIssue(data) {
	return post("/api/governance/issue", data);
}
function assignIssue(id, assigneeId) {
	return post(`/api/governance/issue/${id}/assign`, { assigneeId });
}
function resolveIssue(id) {
	return post(`/api/governance/issue/${id}/resolve`);
}
function closeIssue(id) {
	return post(`/api/governance/issue/${id}/close`);
}
function escalateIssue(id, reason) {
	return post(`/api/governance/issue/${id}/escalate`, { reason });
}
//#endregion
//#region src/views/issue/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "toolbar" };
//#endregion
//#region src/views/issue/index.vue
var issue_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "IssueLog",
	__name: "index",
	setup(__props) {
		const statusOptions = [
			{
				value: "OPEN",
				label: "待处理",
				tagType: "warning"
			},
			{
				value: "IN_PROGRESS",
				label: "处理中",
				tagType: "primary"
			},
			{
				value: "RESOLVED",
				label: "已解决",
				tagType: "success"
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
		function sourceText(row) {
			if (row.sourceRiskNo) return `风险：${row.sourceRiskNo}`;
			if (row.sourceChangeNo) return `变更：${row.sourceChangeNo}`;
			return "-";
		}
		const loading = ref(false);
		const tableData = ref([]);
		const total = ref(0);
		const query = reactive({
			page: 1,
			size: 10,
			projectId: void 0,
			status: "",
			assigneeId: void 0
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
				if (query.assigneeId) params.assigneeId = query.assigneeId;
				const res = await listIssues(params);
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
			query.assigneeId = void 0;
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
				description: "",
				priority: "MEDIUM",
				targetResolveDate: ""
			};
		}
		const createForm = reactive(createEmptyForm());
		const createRules = {
			projectId: [{
				required: true,
				message: "请输入项目 ID",
				trigger: "blur"
			}],
			description: [{
				required: true,
				message: "请输入问题描述",
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
					await createIssue({
						projectId: createForm.projectId,
						description: createForm.description,
						priority: createForm.priority,
						targetResolveDate: createForm.targetResolveDate,
						status: "OPEN"
					});
					ElMessage.success("新建成功");
					createVisible.value = false;
					loadData();
				} catch (_unused2) {} finally {
					createSubmitting.value = false;
				}
			});
		}
		const assignVisible = ref(false);
		const assignSubmitting = ref(false);
		const assignForm = reactive({
			id: 0,
			assigneeId: void 0
		});
		function handleAssign(row) {
			if (!row.id) return;
			assignForm.id = row.id;
			assignForm.assigneeId = void 0;
			assignVisible.value = true;
		}
		async function handleAssignSubmit() {
			if (!assignForm.assigneeId) {
				ElMessage.warning("请输入负责人 ID");
				return;
			}
			assignSubmitting.value = true;
			try {
				await assignIssue(assignForm.id, assignForm.assigneeId);
				ElMessage.success("分派成功");
				assignVisible.value = false;
				loadData();
			} catch (_unused3) {} finally {
				assignSubmitting.value = false;
			}
		}
		function handleResolve(row) {
			var _row$issueNo;
			if (!row.id) return;
			ElMessageBox.confirm(`确认将问题「${(_row$issueNo = row.issueNo) !== null && _row$issueNo !== void 0 ? _row$issueNo : row.description}」标记为已解决吗？`, "解决问题", { type: "warning" }).then(async () => {
				await resolveIssue(row.id);
				ElMessage.success("已标记为解决");
				loadData();
			}).catch(() => {});
		}
		function handleClose(row) {
			var _row$issueNo2;
			if (!row.id) return;
			ElMessageBox.confirm(`确认关闭问题「${(_row$issueNo2 = row.issueNo) !== null && _row$issueNo2 !== void 0 ? _row$issueNo2 : row.description}」吗？`, "关闭问题", { type: "warning" }).then(async () => {
				await closeIssue(row.id);
				ElMessage.success("已关闭");
				loadData();
			}).catch(() => {});
		}
		function handleEscalate(row) {
			if (!row.id) return;
			ElMessageBox.prompt("请输入升级原因（将触发创建变更请求）", "升级问题", {
				confirmButtonText: "确定",
				cancelButtonText: "取消",
				inputType: "textarea",
				inputPlaceholder: "请输入升级原因",
				inputValidator: (val) => !!(val === null || val === void 0 ? void 0 : val.trim()) || "升级原因不能为空"
			}).then(async ({ value }) => {
				await escalateIssue(row.id, value);
				ElMessage.success("已升级并创建变更请求");
				loadData();
			}).catch(() => {});
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
			const _component_el_date_picker = resolveComponent("el-date-picker");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, { shadow: "never" }, {
					header: withCtx(() => [..._cache[13] || (_cache[13] = [createElementVNode("span", { class: "page-title" }, "问题日志", -1)])]),
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
								createVNode(_component_el_form_item, { label: "负责人 ID" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: query.assigneeId,
										"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => query.assigneeId = $event),
										modelModifiers: { number: true },
										placeholder: "请输入负责人 ID",
										clearable: "",
										style: { "width": "160px" },
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
							default: withCtx(() => [..._cache[16] || (_cache[16] = [createTextVNode("新建问题", -1)])]),
							_: 1
						})]),
						withDirectives((openBlock(), createBlock(_component_el_table, {
							data: tableData.value,
							border: "",
							stripe: ""
						}, {
							empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无问题数据" })]),
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									prop: "issueNo",
									label: "问题编号",
									width: "130"
								}),
								createVNode(_component_el_table_column, {
									prop: "description",
									label: "问题描述",
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
									width: "110",
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
									prop: "raisedByName",
									label: "提出人",
									width: "100"
								}),
								createVNode(_component_el_table_column, {
									prop: "assigneeName",
									label: "负责人",
									width: "100"
								}),
								createVNode(_component_el_table_column, {
									label: "目标解决日期",
									width: "120",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDate(row.targetResolveDate)), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "源关联",
									width: "150"
								}, {
									default: withCtx(({ row }) => [createTextVNode(toDisplayString(sourceText(row)), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "操作",
									width: "260",
									fixed: "right"
								}, {
									default: withCtx(({ row }) => [
										row.status === "OPEN" || row.status === "IN_PROGRESS" ? (openBlock(), createBlock(_component_el_button, {
											key: 0,
											link: "",
											type: "primary",
											onClick: ($event) => handleAssign(row)
										}, {
											default: withCtx(() => [..._cache[17] || (_cache[17] = [createTextVNode(" 分派 ", -1)])]),
											_: 1
										}, 8, ["onClick"])) : createCommentVNode("", true),
										row.status === "OPEN" || row.status === "IN_PROGRESS" ? (openBlock(), createBlock(_component_el_button, {
											key: 1,
											link: "",
											type: "success",
											onClick: ($event) => handleResolve(row)
										}, {
											default: withCtx(() => [..._cache[18] || (_cache[18] = [createTextVNode(" 解决 ", -1)])]),
											_: 1
										}, 8, ["onClick"])) : createCommentVNode("", true),
										row.status === "RESOLVED" ? (openBlock(), createBlock(_component_el_button, {
											key: 2,
											link: "",
											type: "primary",
											onClick: ($event) => handleClose(row)
										}, {
											default: withCtx(() => [..._cache[19] || (_cache[19] = [createTextVNode(" 关闭 ", -1)])]),
											_: 1
										}, 8, ["onClick"])) : createCommentVNode("", true),
										row.status !== "CLOSED" ? (openBlock(), createBlock(_component_el_button, {
											key: 3,
											link: "",
											type: "danger",
											onClick: ($event) => handleEscalate(row)
										}, {
											default: withCtx(() => [..._cache[20] || (_cache[20] = [createTextVNode(" 升级 ", -1)])]),
											_: 1
										}, 8, ["onClick"])) : createCommentVNode("", true)
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
					"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => createVisible.value = $event),
					title: "新建问题",
					width: "560px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[8] || (_cache[8] = ($event) => createVisible.value = false) }, {
						default: withCtx(() => [..._cache[21] || (_cache[21] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: createSubmitting.value,
						onClick: handleCreateSubmit
					}, {
						default: withCtx(() => [..._cache[22] || (_cache[22] = [createTextVNode(" 确定 ", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "createFormRef",
						ref: createFormRef,
						model: createForm,
						rules: createRules,
						"label-width": "110px"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, {
								label: "项目 ID",
								prop: "projectId"
							}, {
								default: withCtx(() => [createVNode(_component_el_input_number, {
									modelValue: createForm.projectId,
									"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => createForm.projectId = $event),
									min: 1,
									"controls-position": "right",
									style: { "width": "100%" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "问题描述",
								prop: "description"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: createForm.description,
									"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => createForm.description = $event),
									type: "textarea",
									rows: 4,
									placeholder: "请输入问题描述"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "优先级",
								prop: "priority"
							}, {
								default: withCtx(() => [createVNode(_component_el_select, {
									modelValue: createForm.priority,
									"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => createForm.priority = $event),
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
							}),
							createVNode(_component_el_form_item, { label: "目标解决日期" }, {
								default: withCtx(() => [createVNode(_component_el_date_picker, {
									modelValue: createForm.targetResolveDate,
									"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => createForm.targetResolveDate = $event),
									type: "date",
									"value-format": "YYYY-MM-DD",
									placeholder: "选择目标解决日期",
									style: { "width": "100%" }
								}, null, 8, ["modelValue"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: assignVisible.value,
					"onUpdate:modelValue": _cache[12] || (_cache[12] = ($event) => assignVisible.value = $event),
					title: "分派问题",
					width: "420px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[11] || (_cache[11] = ($event) => assignVisible.value = false) }, {
						default: withCtx(() => [..._cache[23] || (_cache[23] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: assignSubmitting.value,
						onClick: handleAssignSubmit
					}, {
						default: withCtx(() => [..._cache[24] || (_cache[24] = [createTextVNode(" 确定 ", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, { "label-width": "100px" }, {
						default: withCtx(() => [createVNode(_component_el_form_item, {
							label: "负责人 ID",
							required: ""
						}, {
							default: withCtx(() => [createVNode(_component_el_input_number, {
								modelValue: assignForm.assigneeId,
								"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => assignForm.assigneeId = $event),
								min: 1,
								"controls-position": "right",
								style: { "width": "100%" }
							}, null, 8, ["modelValue"])]),
							_: 1
						})]),
						_: 1
					})]),
					_: 1
				}, 8, ["modelValue"])
			]);
		};
	}
}), [["__scopeId", "data-v-cbce7486"]]);
//#endregion
export { issue_default as default };

//# sourceMappingURL=issue-B63emxVf.js.map