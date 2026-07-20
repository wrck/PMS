import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { _ as rejectTask, a as completeTask, d as getAgentPage, h as getTaskPage, i as assignOemTask, o as confirmTask, r as assignAgentTask, t as acceptTask, v as reportTaskProgress, y as startTask } from "./implementation-DHYgyd55.js";
import { d as listMilestones, f as listProjects } from "./project-Brd7mmQb.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, watch, withCtx, withDirectives, withModifiers } from "vue";
//#region src/views/implementation/task/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "toolbar" };
//#endregion
//#region src/views/implementation/task/index.vue
var task_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const activeTab = ref("OEM");
		const loading = ref(false);
		const tableData = ref([]);
		const total = ref(0);
		const query = reactive({
			page: 1,
			size: 10,
			projectId: void 0,
			taskType: "OEM",
			status: void 0
		});
		const statusOptions = [
			{
				label: "待接单",
				value: "PENDING"
			},
			{
				label: "已接单",
				value: "ACCEPTED"
			},
			{
				label: "进行中",
				value: "IN_PROGRESS"
			},
			{
				label: "已完成",
				value: "COMPLETED"
			},
			{
				label: "已确认",
				value: "CONFIRMED"
			},
			{
				label: "已驳回",
				value: "REJECTED"
			}
		];
		const statusTagType = {
			PENDING: "info",
			ACCEPTED: "warning",
			IN_PROGRESS: "primary",
			COMPLETED: "success",
			CONFIRMED: "success",
			REJECTED: "danger"
		};
		function statusLabel(status) {
			var _ref, _statusOptions$find$l, _statusOptions$find;
			return (_ref = (_statusOptions$find$l = (_statusOptions$find = statusOptions.find((s) => s.value === status)) === null || _statusOptions$find === void 0 ? void 0 : _statusOptions$find.label) !== null && _statusOptions$find$l !== void 0 ? _statusOptions$find$l : status) !== null && _ref !== void 0 ? _ref : "-";
		}
		const projectOptions = ref([]);
		const agentOptions = ref([]);
		const milestoneOptions = ref([]);
		const assignVisible = ref(false);
		const assignSubmitting = ref(false);
		const assignFormRef = ref();
		const assignForm = reactive(createEmptyAssignForm());
		function createEmptyAssignForm() {
			return {
				taskName: "",
				projectId: void 0,
				milestoneId: void 0,
				engineerId: void 0,
				engineerName: "",
				agentId: void 0,
				planStartDate: "",
				planEndDate: ""
			};
		}
		const assignRules = computed(() => {
			const rules = {
				taskName: [{
					required: true,
					message: "请输入任务名称",
					trigger: "blur"
				}],
				projectId: [{
					required: true,
					message: "请选择关联项目",
					trigger: "change"
				}]
			};
			if (activeTab.value === "OEM") rules.engineerId = [{
				required: true,
				message: "请输入工程师ID",
				trigger: "blur"
			}];
			else rules.agentId = [{
				required: true,
				message: "请选择代理商",
				trigger: "change"
			}];
			return rules;
		});
		const progressVisible = ref(false);
		const progressSubmitting = ref(false);
		const progressForm = reactive({
			taskId: 0,
			progressPercent: 0,
			workLog: "",
			photoUrlsText: ""
		});
		async function loadProjects() {
			try {
				const res = await listProjects({
					page: 1,
					size: 100
				});
				projectOptions.value = res.records;
			} catch (_unused) {}
		}
		async function loadAgents() {
			try {
				const res = await getAgentPage({
					page: 1,
					size: 100
				});
				agentOptions.value = res.records.map((a) => ({
					id: a.id,
					agentName: a.agentName
				}));
			} catch (_unused2) {}
		}
		async function loadMilestones(projectId) {
			try {
				milestoneOptions.value = await listMilestones(projectId);
			} catch (_unused3) {
				milestoneOptions.value = [];
			}
		}
		async function loadData() {
			loading.value = true;
			try {
				query.taskType = activeTab.value;
				const res = await getTaskPage(query);
				tableData.value = res.records;
				total.value = res.total;
			} catch (_unused4) {} finally {
				loading.value = false;
			}
		}
		function handleSearch() {
			query.page = 1;
			loadData();
		}
		function handleReset() {
			query.projectId = void 0;
			query.status = void 0;
			query.page = 1;
			loadData();
		}
		function handleTabChange() {
			query.page = 1;
			query.status = void 0;
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
		function handleAssign() {
			Object.assign(assignForm, createEmptyAssignForm());
			milestoneOptions.value = [];
			assignVisible.value = true;
		}
		async function handleProjectChange(projectId) {
			assignForm.milestoneId = void 0;
			milestoneOptions.value = [];
			if (projectId) await loadMilestones(projectId);
		}
		async function handleAssignSubmit() {
			if (!assignFormRef.value) return;
			await assignFormRef.value.validate(async (valid) => {
				if (!valid) return;
				assignSubmitting.value = true;
				try {
					if (activeTab.value === "OEM") {
						await assignOemTask({
							taskName: assignForm.taskName,
							projectId: assignForm.projectId,
							milestoneId: assignForm.milestoneId,
							engineerId: assignForm.engineerId,
							engineerName: assignForm.engineerName,
							planStartDate: assignForm.planStartDate,
							planEndDate: assignForm.planEndDate
						});
						ElMessage.success("原厂任务分配成功");
					} else {
						await assignAgentTask({
							taskName: assignForm.taskName,
							projectId: assignForm.projectId,
							milestoneId: assignForm.milestoneId,
							agentId: assignForm.agentId,
							planStartDate: assignForm.planStartDate,
							planEndDate: assignForm.planEndDate
						});
						ElMessage.success("代理商任务分配成功");
					}
					assignVisible.value = false;
					loadData();
				} catch (_unused5) {} finally {
					assignSubmitting.value = false;
				}
			});
		}
		function handleAccept(row) {
			if (!row.id) return;
			ElMessageBox.confirm(`确定接单任务「${row.taskName}」吗？`, "接单确认", { type: "warning" }).then(async () => {
				await acceptTask(row.id);
				ElMessage.success("接单成功");
				loadData();
			}).catch(() => {});
		}
		function handleStart(row) {
			if (!row.id) return;
			ElMessageBox.confirm(`确定开始执行任务「${row.taskName}」吗？`, "开始确认", { type: "warning" }).then(async () => {
				await startTask(row.id);
				ElMessage.success("任务已开始");
				loadData();
			}).catch(() => {});
		}
		function handleProgress(row) {
			var _row$progress;
			if (!row.id) return;
			progressForm.taskId = row.id;
			progressForm.progressPercent = (_row$progress = row.progress) !== null && _row$progress !== void 0 ? _row$progress : 0;
			progressForm.workLog = "";
			progressForm.photoUrlsText = "";
			progressVisible.value = true;
		}
		async function handleProgressSubmit() {
			if (!progressForm.workLog) {
				ElMessage.warning("请填写工作日志");
				return;
			}
			progressSubmitting.value = true;
			try {
				const photoUrls = progressForm.photoUrlsText.split(/[\n,，]/).map((s) => s.trim()).filter((s) => s.length > 0);
				await reportTaskProgress(progressForm.taskId, {
					progressPercent: progressForm.progressPercent,
					workLog: progressForm.workLog,
					photoUrls
				});
				ElMessage.success("进度上报成功");
				progressVisible.value = false;
				loadData();
			} catch (_unused6) {} finally {
				progressSubmitting.value = false;
			}
		}
		function handleComplete(row) {
			if (!row.id) return;
			ElMessageBox.prompt("请输入完成说明", "完成任务", {
				confirmButtonText: "确定",
				cancelButtonText: "取消",
				inputType: "textarea",
				inputPlaceholder: "请输入完成情况说明"
			}).then(async ({ value }) => {
				await completeTask(row.id, value);
				ElMessage.success("任务已完成");
				loadData();
			}).catch(() => {});
		}
		function handleConfirm(row) {
			if (!row.id) return;
			ElMessageBox.prompt("请输入确认意见", "确认任务", {
				confirmButtonText: "确定",
				cancelButtonText: "取消",
				inputType: "textarea",
				inputPlaceholder: "请输入确认意见"
			}).then(async ({ value }) => {
				await confirmTask(row.id, value);
				ElMessage.success("任务已确认");
				loadData();
			}).catch(() => {});
		}
		function handleReject(row) {
			if (!row.id) return;
			ElMessageBox.prompt("请输入驳回原因", "驳回任务", {
				confirmButtonText: "确定",
				cancelButtonText: "取消",
				inputType: "textarea",
				inputPlaceholder: "请输入驳回原因",
				inputValidator: (val) => !!(val === null || val === void 0 ? void 0 : val.trim()) || "驳回原因不能为空"
			}).then(async ({ value }) => {
				await rejectTask(row.id, value);
				ElMessage.success("任务已驳回");
				loadData();
			}).catch(() => {});
		}
		function assigneeText(row) {
			if (row.taskType === "AGENT") return row.agentName || "-";
			return row.engineerName || "-";
		}
		watch(activeTab, () => {
			query.taskType = activeTab.value;
		});
		onMounted(async () => {
			await Promise.all([loadProjects(), loadAgents()]);
			loadData();
		});
		return (_ctx, _cache) => {
			const _component_el_tab_pane = resolveComponent("el-tab-pane");
			const _component_el_tabs = resolveComponent("el-tabs");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_progress = resolveComponent("el-progress");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_pagination = resolveComponent("el-pagination");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_date_picker = resolveComponent("el-date-picker");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _component_el_slider = resolveComponent("el-slider");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, { shadow: "never" }, {
					header: withCtx(() => [..._cache[19] || (_cache[19] = [createElementVNode("span", { class: "page-title" }, "实施任务管理", -1)])]),
					default: withCtx(() => [
						createVNode(_component_el_tabs, {
							modelValue: activeTab.value,
							"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => activeTab.value = $event),
							onTabChange: handleTabChange
						}, {
							default: withCtx(() => [createVNode(_component_el_tab_pane, {
								label: "原厂实施",
								name: "OEM"
							}), createVNode(_component_el_tab_pane, {
								label: "代理商实施",
								name: "AGENT"
							})]),
							_: 1
						}, 8, ["modelValue"]),
						createVNode(_component_el_form, {
							inline: true,
							onSubmit: _cache[3] || (_cache[3] = withModifiers(() => {}, ["prevent"]))
						}, {
							default: withCtx(() => [
								createVNode(_component_el_form_item, { label: "项目" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: query.projectId,
										"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => query.projectId = $event),
										placeholder: "全部项目",
										clearable: "",
										filterable: "",
										style: { "width": "200px" }
									}, {
										default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(projectOptions.value, (p) => {
											return openBlock(), createBlock(_component_el_option, {
												key: p.id,
												label: p.name,
												value: p.id
											}, null, 8, ["label", "value"]);
										}), 128))]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "状态" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: query.status,
										"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => query.status = $event),
										placeholder: "全部状态",
										clearable: "",
										style: { "width": "160px" }
									}, {
										default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(statusOptions, (s) => {
											return createVNode(_component_el_option, {
												key: s.value,
												label: s.label,
												value: s.value
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
										default: withCtx(() => [..._cache[20] || (_cache[20] = [createTextVNode("查询", -1)])]),
										_: 1
									}), createVNode(_component_el_button, {
										icon: "Refresh",
										onClick: handleReset
									}, {
										default: withCtx(() => [..._cache[21] || (_cache[21] = [createTextVNode("重置", -1)])]),
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
							onClick: handleAssign
						}, {
							default: withCtx(() => [..._cache[22] || (_cache[22] = [createTextVNode("分配任务", -1)])]),
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
									prop: "taskName",
									label: "任务名称",
									"min-width": "180",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									label: "类型",
									width: "100"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, {
										type: row.taskType === "AGENT" ? "warning" : "primary",
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(row.taskType === "AGENT" ? "代理商" : "原厂"), 1)]),
										_: 2
									}, 1032, ["type"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									prop: "projectName",
									label: "关联项目",
									"min-width": "160",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									label: "工程师/代理商",
									"min-width": "130"
								}, {
									default: withCtx(({ row }) => [createTextVNode(toDisplayString(assigneeText(row)), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "状态",
									width: "110"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, {
										type: statusTagType[row.status] || "info",
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(statusLabel(row.status)), 1)]),
										_: 2
									}, 1032, ["type"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "进度",
									width: "170"
								}, {
									default: withCtx(({ row }) => {
										var _row$progress2;
										return [createVNode(_component_el_progress, {
											percentage: (_row$progress2 = row.progress) !== null && _row$progress2 !== void 0 ? _row$progress2 : 0,
											"stroke-width": 14,
											"text-inside": true
										}, null, 8, ["percentage"])];
									}),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									prop: "planStartDate",
									label: "计划开始",
									"min-width": "120"
								}),
								createVNode(_component_el_table_column, {
									prop: "planEndDate",
									label: "计划结束",
									"min-width": "120"
								}),
								createVNode(_component_el_table_column, {
									label: "操作",
									width: "220",
									fixed: "right"
								}, {
									default: withCtx(({ row }) => [
										row.status === "PENDING" ? (openBlock(), createBlock(_component_el_button, {
											key: 0,
											link: "",
											type: "primary",
											onClick: ($event) => handleAccept(row)
										}, {
											default: withCtx(() => [..._cache[23] || (_cache[23] = [createTextVNode("接单", -1)])]),
											_: 1
										}, 8, ["onClick"])) : createCommentVNode("", true),
										row.status === "ACCEPTED" ? (openBlock(), createBlock(_component_el_button, {
											key: 1,
											link: "",
											type: "primary",
											onClick: ($event) => handleStart(row)
										}, {
											default: withCtx(() => [..._cache[24] || (_cache[24] = [createTextVNode("开始", -1)])]),
											_: 1
										}, 8, ["onClick"])) : createCommentVNode("", true),
										row.status === "IN_PROGRESS" ? (openBlock(), createElementBlock(Fragment, { key: 2 }, [createVNode(_component_el_button, {
											link: "",
											type: "primary",
											onClick: ($event) => handleProgress(row)
										}, {
											default: withCtx(() => [..._cache[25] || (_cache[25] = [createTextVNode("上报进度", -1)])]),
											_: 1
										}, 8, ["onClick"]), createVNode(_component_el_button, {
											link: "",
											type: "success",
											onClick: ($event) => handleComplete(row)
										}, {
											default: withCtx(() => [..._cache[26] || (_cache[26] = [createTextVNode("完成", -1)])]),
											_: 1
										}, 8, ["onClick"])], 64)) : createCommentVNode("", true),
										row.status === "COMPLETED" ? (openBlock(), createElementBlock(Fragment, { key: 3 }, [createVNode(_component_el_button, {
											link: "",
											type: "success",
											onClick: ($event) => handleConfirm(row)
										}, {
											default: withCtx(() => [..._cache[27] || (_cache[27] = [createTextVNode("确认", -1)])]),
											_: 1
										}, 8, ["onClick"]), createVNode(_component_el_button, {
											link: "",
											type: "danger",
											onClick: ($event) => handleReject(row)
										}, {
											default: withCtx(() => [..._cache[28] || (_cache[28] = [createTextVNode("驳回", -1)])]),
											_: 1
										}, 8, ["onClick"])], 64)) : createCommentVNode("", true)
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
					modelValue: assignVisible.value,
					"onUpdate:modelValue": _cache[13] || (_cache[13] = ($event) => assignVisible.value = $event),
					title: activeTab.value === "OEM" ? "分配原厂实施任务" : "分配代理商实施任务",
					width: "560px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[12] || (_cache[12] = ($event) => assignVisible.value = false) }, {
						default: withCtx(() => [..._cache[29] || (_cache[29] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: assignSubmitting.value,
						onClick: handleAssignSubmit
					}, {
						default: withCtx(() => [..._cache[30] || (_cache[30] = [createTextVNode("确定", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "assignFormRef",
						ref: assignFormRef,
						model: assignForm,
						rules: assignRules.value,
						"label-width": "100px"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, {
								label: "任务名称",
								prop: "taskName"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: assignForm.taskName,
									"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => assignForm.taskName = $event),
									placeholder: "请输入任务名称"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "关联项目",
								prop: "projectId"
							}, {
								default: withCtx(() => [createVNode(_component_el_select, {
									modelValue: assignForm.projectId,
									"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => assignForm.projectId = $event),
									placeholder: "请选择项目",
									filterable: "",
									style: { "width": "100%" },
									onChange: handleProjectChange
								}, {
									default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(projectOptions.value, (p) => {
										return openBlock(), createBlock(_component_el_option, {
											key: p.id,
											label: p.name,
											value: p.id
										}, null, 8, ["label", "value"]);
									}), 128))]),
									_: 1
								}, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "里程碑",
								prop: "milestoneId"
							}, {
								default: withCtx(() => [createVNode(_component_el_select, {
									modelValue: assignForm.milestoneId,
									"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => assignForm.milestoneId = $event),
									placeholder: "请选择里程碑",
									clearable: "",
									style: { "width": "100%" }
								}, {
									default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(milestoneOptions.value, (m) => {
										return openBlock(), createBlock(_component_el_option, {
											key: m.id,
											label: m.name,
											value: m.id
										}, null, 8, ["label", "value"]);
									}), 128))]),
									_: 1
								}, 8, ["modelValue"])]),
								_: 1
							}),
							activeTab.value === "OEM" ? (openBlock(), createElementBlock(Fragment, { key: 0 }, [createVNode(_component_el_form_item, {
								label: "工程师ID",
								prop: "engineerId"
							}, {
								default: withCtx(() => [createVNode(_component_el_input_number, {
									modelValue: assignForm.engineerId,
									"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => assignForm.engineerId = $event),
									min: 1,
									"controls-position": "right",
									style: { "width": "100%" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}), createVNode(_component_el_form_item, {
								label: "工程师姓名",
								prop: "engineerName"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: assignForm.engineerName,
									"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => assignForm.engineerName = $event),
									placeholder: "请输入工程师姓名"
								}, null, 8, ["modelValue"])]),
								_: 1
							})], 64)) : (openBlock(), createBlock(_component_el_form_item, {
								key: 1,
								label: "代理商",
								prop: "agentId"
							}, {
								default: withCtx(() => [createVNode(_component_el_select, {
									modelValue: assignForm.agentId,
									"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => assignForm.agentId = $event),
									placeholder: "请选择代理商",
									filterable: "",
									style: { "width": "100%" }
								}, {
									default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(agentOptions.value, (a) => {
										return openBlock(), createBlock(_component_el_option, {
											key: a.id,
											label: a.agentName,
											value: a.id
										}, null, 8, ["label", "value"]);
									}), 128))]),
									_: 1
								}, 8, ["modelValue"])]),
								_: 1
							})),
							createVNode(_component_el_form_item, {
								label: "计划开始",
								prop: "planStartDate"
							}, {
								default: withCtx(() => [createVNode(_component_el_date_picker, {
									modelValue: assignForm.planStartDate,
									"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => assignForm.planStartDate = $event),
									type: "date",
									"value-format": "YYYY-MM-DD",
									placeholder: "选择计划开始日期",
									style: { "width": "100%" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "计划结束",
								prop: "planEndDate"
							}, {
								default: withCtx(() => [createVNode(_component_el_date_picker, {
									modelValue: assignForm.planEndDate,
									"onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => assignForm.planEndDate = $event),
									type: "date",
									"value-format": "YYYY-MM-DD",
									placeholder: "选择计划结束日期",
									style: { "width": "100%" }
								}, null, 8, ["modelValue"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["model", "rules"])]),
					_: 1
				}, 8, ["modelValue", "title"]),
				createVNode(_component_el_dialog, {
					modelValue: progressVisible.value,
					"onUpdate:modelValue": _cache[18] || (_cache[18] = ($event) => progressVisible.value = $event),
					title: "上报进度",
					width: "520px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[17] || (_cache[17] = ($event) => progressVisible.value = false) }, {
						default: withCtx(() => [..._cache[31] || (_cache[31] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: progressSubmitting.value,
						onClick: handleProgressSubmit
					}, {
						default: withCtx(() => [..._cache[32] || (_cache[32] = [createTextVNode("提交", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, { "label-width": "100px" }, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, { label: "完成进度" }, {
								default: withCtx(() => [createVNode(_component_el_slider, {
									modelValue: progressForm.progressPercent,
									"onUpdate:modelValue": _cache[14] || (_cache[14] = ($event) => progressForm.progressPercent = $event),
									"show-input": "",
									max: 100
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "工作日志",
								required: ""
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: progressForm.workLog,
									"onUpdate:modelValue": _cache[15] || (_cache[15] = ($event) => progressForm.workLog = $event),
									type: "textarea",
									rows: 4,
									placeholder: "请输入工作日志"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "照片URL" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: progressForm.photoUrlsText,
									"onUpdate:modelValue": _cache[16] || (_cache[16] = ($event) => progressForm.photoUrlsText = $event),
									type: "textarea",
									rows: 2,
									placeholder: "多个URL用逗号或换行分隔"
								}, null, 8, ["modelValue"])]),
								_: 1
							})
						]),
						_: 1
					})]),
					_: 1
				}, 8, ["modelValue"])
			]);
		};
	}
}), [["__scopeId", "data-v-241099aa"]]);
//#endregion
export { task_default as default };

//# sourceMappingURL=task-BPLO5Zmt.js.map