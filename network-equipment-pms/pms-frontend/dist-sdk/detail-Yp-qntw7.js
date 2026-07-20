import { d as useRouter, u as useRoute } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { c as getAcceptanceByProject, d as listMilestones, h as updateMilestoneProgress, i as createMilestone, m as updateMilestone, n as approveAcceptance, o as deleteMilestone, p as rejectAcceptance, t as applyAcceptance, u as getProject } from "./project-Brd7mmQb.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives } from "vue";
//#region src/views/project/detail/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "header-content" };
var _hoisted_3 = { class: "header-title" };
var _hoisted_4 = { class: "progress-row" };
var _hoisted_5 = { class: "toolbar" };
var _hoisted_6 = {
	key: 0,
	class: "milestone-tip"
};
var _hoisted_7 = { class: "acceptance-wrap" };
var _hoisted_8 = {
	key: 0,
	class: "acceptance-empty"
};
var _hoisted_9 = {
	key: 1,
	class: "acceptance-tip"
};
var _hoisted_10 = { class: "report-text" };
var _hoisted_11 = {
	key: 0,
	class: "acceptance-actions"
};
//#endregion
//#region src/views/project/detail/index.vue
var detail_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const route = useRoute();
		const router = useRouter();
		const projectId = computed(() => Number(route.params.id));
		const loading = ref(false);
		const project = ref(null);
		const activeTab = ref("basic");
		const statusOptions = [
			{
				value: "PENDING",
				label: "待审批",
				tagType: "info"
			},
			{
				value: "APPROVED",
				label: "已立项",
				tagType: "warning"
			},
			{
				value: "IN_PROGRESS",
				label: "执行中",
				tagType: "primary"
			},
			{
				value: "INITIAL_ACCEPTANCE",
				label: "初验",
				tagType: "warning"
			},
			{
				value: "FINAL_ACCEPTANCE",
				label: "终验中",
				tagType: "danger"
			},
			{
				value: "COMPLETED",
				label: "已完成",
				tagType: "success"
			},
			{
				value: "CLOSED",
				label: "已关闭",
				tagType: "info"
			},
			{
				value: "REJECTED",
				label: "已驳回",
				tagType: "danger"
			}
		];
		const typeOptions = [
			{
				value: "NETWORK_DEVICE",
				label: "网络设备"
			},
			{
				value: "SECURITY",
				label: "安全设备"
			},
			{
				value: "DATACENTER",
				label: "数据中心"
			}
		];
		const priorityOptions = [
			{
				value: 1,
				label: "高"
			},
			{
				value: 2,
				label: "中"
			},
			{
				value: 3,
				label: "低"
			}
		];
		const milestoneTypeOptions = [
			{
				value: "START",
				label: "启动"
			},
			{
				value: "DELIVERY",
				label: "到货"
			},
			{
				value: "INSTALL",
				label: "安装"
			},
			{
				value: "INITIAL_ACCEPTANCE",
				label: "初验"
			},
			{
				value: "FINAL_ACCEPTANCE",
				label: "终验"
			},
			{
				value: "OTHER",
				label: "其他"
			}
		];
		function getStatusMeta(status) {
			var _statusOptions$find;
			return (_statusOptions$find = statusOptions.find((s) => s.value === status)) !== null && _statusOptions$find !== void 0 ? _statusOptions$find : {
				label: status !== null && status !== void 0 ? status : "-",
				tagType: "info"
			};
		}
		function getTypeLabel(type) {
			var _ref, _typeOptions$find$lab, _typeOptions$find;
			return (_ref = (_typeOptions$find$lab = (_typeOptions$find = typeOptions.find((t) => t.value === type)) === null || _typeOptions$find === void 0 ? void 0 : _typeOptions$find.label) !== null && _typeOptions$find$lab !== void 0 ? _typeOptions$find$lab : type) !== null && _ref !== void 0 ? _ref : "-";
		}
		function getPriorityLabel(priority) {
			var _priorityOptions$find, _priorityOptions$find2;
			return (_priorityOptions$find = (_priorityOptions$find2 = priorityOptions.find((p) => p.value === priority)) === null || _priorityOptions$find2 === void 0 ? void 0 : _priorityOptions$find2.label) !== null && _priorityOptions$find !== void 0 ? _priorityOptions$find : "-";
		}
		function getMilestoneTypeLabel(type) {
			var _ref2, _milestoneTypeOptions, _milestoneTypeOptions2;
			return (_ref2 = (_milestoneTypeOptions = (_milestoneTypeOptions2 = milestoneTypeOptions.find((t) => t.value === type)) === null || _milestoneTypeOptions2 === void 0 ? void 0 : _milestoneTypeOptions2.label) !== null && _milestoneTypeOptions !== void 0 ? _milestoneTypeOptions : type) !== null && _ref2 !== void 0 ? _ref2 : "-";
		}
		function formatDate(date) {
			if (!date) return "-";
			return date.length > 10 ? date.substring(0, 10) : date;
		}
		async function loadProject() {
			loading.value = true;
			try {
				project.value = await getProject(projectId.value);
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		function goBack() {
			router.back();
		}
		const milestoneLoading = ref(false);
		const milestones = ref([]);
		async function loadMilestones() {
			milestoneLoading.value = true;
			try {
				var _await$listMilestones;
				milestones.value = (_await$listMilestones = await listMilestones(projectId.value)) !== null && _await$listMilestones !== void 0 ? _await$listMilestones : [];
			} catch (_unused2) {} finally {
				milestoneLoading.value = false;
			}
		}
		function isMilestoneDone(m) {
			if (m.status) {
				const s = m.status.toUpperCase();
				if (s === "COMPLETED" || s === "DONE" || s === "FINISHED") return true;
				if (s === "PENDING" || s === "IN_PROGRESS" || s === "TODO") return false;
			}
			return !!m.actualDate;
		}
		function getMilestoneStatusMeta(m) {
			if (isMilestoneDone(m)) return {
				label: "已完成",
				tagType: "success"
			};
			if (m.status) {
				const s = m.status.toUpperCase();
				if (s === "IN_PROGRESS") return {
					label: "进行中",
					tagType: "warning"
				};
				if (s === "PENDING" || s === "TODO") return {
					label: "待开始",
					tagType: "info"
				};
				return {
					label: m.status,
					tagType: "info"
				};
			}
			return {
				label: "待开始",
				tagType: "info"
			};
		}
		const allMilestonesDone = computed(() => {
			if (milestones.value.length === 0) return false;
			return milestones.value.every(isMilestoneDone);
		});
		const milestoneDialogVisible = ref(false);
		const milestoneDialogTitle = ref("");
		const milestoneSubmitting = ref(false);
		const milestoneFormRef = ref();
		function createEmptyMilestoneForm() {
			return {
				id: void 0,
				projectId: projectId.value,
				name: "",
				type: "OTHER",
				plannedDate: "",
				description: ""
			};
		}
		const milestoneForm = reactive(createEmptyMilestoneForm());
		const milestoneRules = {
			name: [{
				required: true,
				message: "请输入里程碑名称",
				trigger: "blur"
			}],
			type: [{
				required: true,
				message: "请选择里程碑类型",
				trigger: "change"
			}],
			plannedDate: [{
				required: true,
				message: "请选择计划日期",
				trigger: "change"
			}]
		};
		function handleAddMilestone() {
			milestoneDialogTitle.value = "新增里程碑";
			Object.assign(milestoneForm, createEmptyMilestoneForm());
			milestoneDialogVisible.value = true;
		}
		function handleEditMilestone(row) {
			milestoneDialogTitle.value = "编辑里程碑";
			Object.assign(milestoneForm, createEmptyMilestoneForm(), row);
			milestoneDialogVisible.value = true;
		}
		async function handleSubmitMilestone() {
			if (!milestoneFormRef.value) return;
			await milestoneFormRef.value.validate(async (valid) => {
				if (!valid) return;
				milestoneSubmitting.value = true;
				try {
					const payload = {
						...milestoneForm,
						projectId: projectId.value
					};
					if (milestoneForm.id) {
						await updateMilestone(payload);
						ElMessage.success("更新成功");
					} else {
						await createMilestone(payload);
						ElMessage.success("新增成功");
					}
					milestoneDialogVisible.value = false;
					loadMilestones();
				} catch (_unused3) {} finally {
					milestoneSubmitting.value = false;
				}
			});
		}
		function handleDeleteMilestone(row) {
			if (!row.id) return;
			ElMessageBox.confirm(`确定删除里程碑「${row.name}」吗？`, "提示", { type: "warning" }).then(async () => {
				await deleteMilestone(row.id);
				ElMessage.success("删除成功");
				loadMilestones();
			}).catch(() => {});
		}
		const progressDialogVisible = ref(false);
		const progressSubmitting = ref(false);
		const progressFormRef = ref();
		const progressForm = reactive({
			milestoneId: void 0,
			milestoneName: "",
			actualDate: "",
			description: ""
		});
		const progressRules = { actualDate: [{
			required: true,
			message: "请选择实际完成日期",
			trigger: "change"
		}] };
		function handleUpdateProgress(row) {
			var _row$actualDate, _row$description;
			progressForm.milestoneId = row.id;
			progressForm.milestoneName = row.name;
			progressForm.actualDate = (_row$actualDate = row.actualDate) !== null && _row$actualDate !== void 0 ? _row$actualDate : "";
			progressForm.description = (_row$description = row.description) !== null && _row$description !== void 0 ? _row$description : "";
			progressDialogVisible.value = true;
		}
		async function handleSubmitProgress() {
			if (!progressFormRef.value || !progressForm.milestoneId) return;
			const milestoneId = progressForm.milestoneId;
			await progressFormRef.value.validate(async (valid) => {
				if (!valid) return;
				progressSubmitting.value = true;
				try {
					await updateMilestoneProgress(milestoneId, {
						actualDate: progressForm.actualDate,
						description: progressForm.description
					});
					ElMessage.success("进度更新成功");
					progressDialogVisible.value = false;
					loadMilestones();
				} catch (_unused4) {} finally {
					progressSubmitting.value = false;
				}
			});
		}
		const members = ref([]);
		const acceptanceLoading = ref(false);
		const acceptance = ref(null);
		async function loadAcceptance() {
			acceptanceLoading.value = true;
			try {
				acceptance.value = await getAcceptanceByProject(projectId.value);
			} catch (_unused5) {
				acceptance.value = null;
			} finally {
				acceptanceLoading.value = false;
			}
		}
		const acceptanceStatusMeta = computed(() => {
			var _acceptance$value;
			const s = (_acceptance$value = acceptance.value) === null || _acceptance$value === void 0 || (_acceptance$value = _acceptance$value.status) === null || _acceptance$value === void 0 ? void 0 : _acceptance$value.toUpperCase();
			if (s === "APPROVED") return {
				label: "已通过",
				tagType: "success"
			};
			if (s === "REJECTED") return {
				label: "已驳回",
				tagType: "danger"
			};
			if (s === "PENDING") return {
				label: "待审批",
				tagType: "warning"
			};
			return {
				label: s !== null && s !== void 0 ? s : "未知",
				tagType: "info"
			};
		});
		const applyDialogVisible = ref(false);
		const applySubmitting = ref(false);
		const applyFormRef = ref();
		const applyForm = reactive({ report: "" });
		const applyRules = { report: [{
			required: true,
			message: "请填写终验报告",
			trigger: "blur"
		}] };
		function openApplyDialog() {
			applyForm.report = "";
			applyDialogVisible.value = true;
		}
		async function handleSubmitApply() {
			if (!applyFormRef.value) return;
			await applyFormRef.value.validate(async (valid) => {
				if (!valid) return;
				applySubmitting.value = true;
				try {
					await applyAcceptance({
						projectId: projectId.value,
						report: applyForm.report
					});
					ElMessage.success("终验申请已提交");
					applyDialogVisible.value = false;
					loadAcceptance();
				} catch (_unused6) {} finally {
					applySubmitting.value = false;
				}
			});
		}
		const opinionDialogVisible = ref(false);
		const opinionDialogTitle = ref("");
		const opinionSubmitting = ref(false);
		const opinionFormRef = ref();
		const opinionForm = reactive({
			acceptanceId: void 0,
			opinion: ""
		});
		const opinionRules = { opinion: [{
			required: true,
			message: "请填写审批意见",
			trigger: "blur"
		}] };
		function openApproveDialog() {
			var _acceptance$value2;
			if (!((_acceptance$value2 = acceptance.value) === null || _acceptance$value2 === void 0 ? void 0 : _acceptance$value2.id)) return;
			opinionDialogTitle.value = "通过终验";
			opinionForm.acceptanceId = acceptance.value.id;
			opinionForm.opinion = "";
			opinionDialogVisible.value = true;
		}
		function openRejectDialog() {
			var _acceptance$value3;
			if (!((_acceptance$value3 = acceptance.value) === null || _acceptance$value3 === void 0 ? void 0 : _acceptance$value3.id)) return;
			opinionDialogTitle.value = "驳回终验";
			opinionForm.acceptanceId = acceptance.value.id;
			opinionForm.opinion = "";
			opinionDialogVisible.value = true;
		}
		async function handleSubmitOpinion(approve) {
			if (!opinionFormRef.value || !opinionForm.acceptanceId) return;
			const acceptanceId = opinionForm.acceptanceId;
			await opinionFormRef.value.validate(async (valid) => {
				if (!valid) return;
				opinionSubmitting.value = true;
				try {
					if (approve) {
						await approveAcceptance(acceptanceId, { opinion: opinionForm.opinion });
						ElMessage.success("已通过终验");
					} else {
						await rejectAcceptance(acceptanceId, { opinion: opinionForm.opinion });
						ElMessage.success("已驳回终验");
					}
					opinionDialogVisible.value = false;
					loadAcceptance();
				} catch (_unused7) {} finally {
					opinionSubmitting.value = false;
				}
			});
		}
		onMounted(async () => {
			await loadProject();
			loadMilestones();
			loadAcceptance();
		});
		return (_ctx, _cache) => {
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_page_header = resolveComponent("el-page-header");
			const _component_el_progress = resolveComponent("el-progress");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_descriptions_item = resolveComponent("el-descriptions-item");
			const _component_el_descriptions = resolveComponent("el-descriptions");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_tab_pane = resolveComponent("el-tab-pane");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_table = resolveComponent("el-table");
			const _component_WarningFilled = resolveComponent("WarningFilled");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_tabs = resolveComponent("el-tabs");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_date_picker = resolveComponent("el-date-picker");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return withDirectives((openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_page_header, {
					icon: null,
					onBack: goBack
				}, {
					content: withCtx(() => {
						var _project$value$name, _project$value, _project$value2, _project$value3;
						return [createElementVNode("div", _hoisted_2, [
							createElementVNode("span", _hoisted_3, toDisplayString((_project$value$name = (_project$value = project.value) === null || _project$value === void 0 ? void 0 : _project$value.name) !== null && _project$value$name !== void 0 ? _project$value$name : "项目详情"), 1),
							((_project$value2 = project.value) === null || _project$value2 === void 0 ? void 0 : _project$value2.code) ? (openBlock(), createBlock(_component_el_tag, {
								key: 0,
								class: "header-code",
								type: "info",
								effect: "plain",
								size: "small"
							}, {
								default: withCtx(() => [createTextVNode(toDisplayString(project.value.code), 1)]),
								_: 1
							})) : createCommentVNode("", true),
							((_project$value3 = project.value) === null || _project$value3 === void 0 ? void 0 : _project$value3.status) ? (openBlock(), createBlock(_component_el_tag, {
								key: 1,
								type: getStatusMeta(project.value.status).tagType,
								class: "header-status"
							}, {
								default: withCtx(() => [createTextVNode(toDisplayString(getStatusMeta(project.value.status).label), 1)]),
								_: 1
							}, 8, ["type"])) : createCommentVNode("", true)
						])];
					}),
					_: 1
				}),
				createVNode(_component_el_card, {
					shadow: "never",
					class: "progress-card"
				}, {
					default: withCtx(() => {
						var _project$value$progre, _project$value4, _project$value$progre2, _project$value5;
						return [createElementVNode("div", _hoisted_4, [_cache[18] || (_cache[18] = createElementVNode("span", { class: "progress-label" }, "项目进度", -1)), createVNode(_component_el_progress, {
							percentage: Number((_project$value$progre = (_project$value4 = project.value) === null || _project$value4 === void 0 ? void 0 : _project$value4.progress) !== null && _project$value$progre !== void 0 ? _project$value$progre : 0),
							"stroke-width": 14,
							status: Number((_project$value$progre2 = (_project$value5 = project.value) === null || _project$value5 === void 0 ? void 0 : _project$value5.progress) !== null && _project$value$progre2 !== void 0 ? _project$value$progre2 : 0) >= 100 ? "success" : "",
							style: { "flex": "1" }
						}, null, 8, ["percentage", "status"])])];
					}),
					_: 1
				}),
				createVNode(_component_el_card, { shadow: "never" }, {
					default: withCtx(() => [createVNode(_component_el_tabs, {
						modelValue: activeTab.value,
						"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => activeTab.value = $event)
					}, {
						default: withCtx(() => [
							createVNode(_component_el_tab_pane, {
								label: "基本信息",
								name: "basic"
							}, {
								default: withCtx(() => [project.value ? (openBlock(), createBlock(_component_el_descriptions, {
									key: 0,
									column: 3,
									border: ""
								}, {
									default: withCtx(() => [
										createVNode(_component_el_descriptions_item, { label: "项目编号" }, {
											default: withCtx(() => [createTextVNode(toDisplayString(project.value.code || "-"), 1)]),
											_: 1
										}),
										createVNode(_component_el_descriptions_item, { label: "项目名称" }, {
											default: withCtx(() => [createTextVNode(toDisplayString(project.value.name || "-"), 1)]),
											_: 1
										}),
										createVNode(_component_el_descriptions_item, { label: "项目类型" }, {
											default: withCtx(() => [createTextVNode(toDisplayString(getTypeLabel(project.value.type)), 1)]),
											_: 1
										}),
										createVNode(_component_el_descriptions_item, { label: "客户名称" }, {
											default: withCtx(() => [createTextVNode(toDisplayString(project.value.customerName || "-"), 1)]),
											_: 1
										}),
										createVNode(_component_el_descriptions_item, { label: "客户联系人" }, {
											default: withCtx(() => [createTextVNode(toDisplayString(project.value.customerContact || "-"), 1)]),
											_: 1
										}),
										createVNode(_component_el_descriptions_item, { label: "客户电话" }, {
											default: withCtx(() => [createTextVNode(toDisplayString(project.value.customerPhone || "-"), 1)]),
											_: 1
										}),
										createVNode(_component_el_descriptions_item, { label: "合同编号" }, {
											default: withCtx(() => [createTextVNode(toDisplayString(project.value.contractNo || "-"), 1)]),
											_: 1
										}),
										createVNode(_component_el_descriptions_item, { label: "合同金额" }, {
											default: withCtx(() => [createTextVNode(toDisplayString(project.value.contractAmount != null ? `￥${Number(project.value.contractAmount).toFixed(2)}` : "-"), 1)]),
											_: 1
										}),
										createVNode(_component_el_descriptions_item, { label: "优先级" }, {
											default: withCtx(() => [createTextVNode(toDisplayString(getPriorityLabel(project.value.priority)), 1)]),
											_: 1
										}),
										createVNode(_component_el_descriptions_item, { label: "项目经理" }, {
											default: withCtx(() => [createTextVNode(toDisplayString(project.value.managerName || "-"), 1)]),
											_: 1
										}),
										createVNode(_component_el_descriptions_item, { label: "计划开始日期" }, {
											default: withCtx(() => [createTextVNode(toDisplayString(formatDate(project.value.planStartDate)), 1)]),
											_: 1
										}),
										createVNode(_component_el_descriptions_item, { label: "计划结束日期" }, {
											default: withCtx(() => [createTextVNode(toDisplayString(formatDate(project.value.planEndDate)), 1)]),
											_: 1
										}),
										createVNode(_component_el_descriptions_item, { label: "项目状态" }, {
											default: withCtx(() => [createVNode(_component_el_tag, { type: getStatusMeta(project.value.status).tagType }, {
												default: withCtx(() => [createTextVNode(toDisplayString(getStatusMeta(project.value.status).label), 1)]),
												_: 1
											}, 8, ["type"])]),
											_: 1
										}),
										createVNode(_component_el_descriptions_item, { label: "创建时间" }, {
											default: withCtx(() => [createTextVNode(toDisplayString(formatDate(project.value.createTime)), 1)]),
											_: 1
										}),
										createVNode(_component_el_descriptions_item, {
											label: "项目描述",
											span: 3
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(project.value.description || "-"), 1)]),
											_: 1
										})
									]),
									_: 1
								})) : (openBlock(), createBlock(_component_el_empty, {
									key: 1,
									description: "暂无项目信息"
								}))]),
								_: 1
							}),
							createVNode(_component_el_tab_pane, {
								label: "里程碑管理",
								name: "milestone"
							}, {
								default: withCtx(() => [createElementVNode("div", _hoisted_5, [createVNode(_component_el_button, {
									type: "primary",
									icon: "Plus",
									onClick: handleAddMilestone
								}, {
									default: withCtx(() => [..._cache[19] || (_cache[19] = [createTextVNode("新增里程碑", -1)])]),
									_: 1
								}), milestones.value.length > 0 ? (openBlock(), createElementBlock("span", _hoisted_6, " 已完成 " + toDisplayString(milestones.value.filter(isMilestoneDone).length) + " / " + toDisplayString(milestones.value.length), 1)) : createCommentVNode("", true)]), withDirectives((openBlock(), createBlock(_component_el_table, {
									data: milestones.value,
									border: "",
									stripe: ""
								}, {
									empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无里程碑数据" })]),
									default: withCtx(() => [
										createVNode(_component_el_table_column, {
											prop: "name",
											label: "名称",
											"min-width": "160",
											"show-overflow-tooltip": ""
										}),
										createVNode(_component_el_table_column, {
											label: "类型",
											width: "120",
											align: "center"
										}, {
											default: withCtx(({ row }) => [createTextVNode(toDisplayString(getMilestoneTypeLabel(row.type)), 1)]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "计划日期",
											width: "120",
											align: "center"
										}, {
											default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDate(row.plannedDate)), 1)]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "实际日期",
											width: "120",
											align: "center"
										}, {
											default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDate(row.actualDate)), 1)]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "状态",
											width: "110",
											align: "center"
										}, {
											default: withCtx(({ row }) => [createVNode(_component_el_tag, { type: getMilestoneStatusMeta(row).tagType }, {
												default: withCtx(() => [createTextVNode(toDisplayString(getMilestoneStatusMeta(row).label), 1)]),
												_: 2
											}, 1032, ["type"])]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											prop: "description",
											label: "描述",
											"min-width": "180",
											"show-overflow-tooltip": ""
										}),
										createVNode(_component_el_table_column, {
											label: "操作",
											width: "240",
											fixed: "right"
										}, {
											default: withCtx(({ row }) => [
												createVNode(_component_el_button, {
													link: "",
													type: "primary",
													onClick: ($event) => handleUpdateProgress(row)
												}, {
													default: withCtx(() => [..._cache[20] || (_cache[20] = [createTextVNode("更新进度", -1)])]),
													_: 1
												}, 8, ["onClick"]),
												createVNode(_component_el_button, {
													link: "",
													type: "primary",
													onClick: ($event) => handleEditMilestone(row)
												}, {
													default: withCtx(() => [..._cache[21] || (_cache[21] = [createTextVNode("编辑", -1)])]),
													_: 1
												}, 8, ["onClick"]),
												createVNode(_component_el_button, {
													link: "",
													type: "danger",
													onClick: ($event) => handleDeleteMilestone(row)
												}, {
													default: withCtx(() => [..._cache[22] || (_cache[22] = [createTextVNode("删除", -1)])]),
													_: 1
												}, 8, ["onClick"])
											]),
											_: 1
										})
									]),
									_: 1
								}, 8, ["data"])), [[_directive_loading, milestoneLoading.value]])]),
								_: 1
							}),
							createVNode(_component_el_tab_pane, {
								label: "项目成员",
								name: "member"
							}, {
								default: withCtx(() => [createVNode(_component_el_table, {
									data: members.value,
									border: "",
									stripe: ""
								}, {
									empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无成员数据" })]),
									default: withCtx(() => [
										createVNode(_component_el_table_column, {
											type: "index",
											label: "#",
											width: "50"
										}),
										createVNode(_component_el_table_column, {
											prop: "name",
											label: "姓名",
											"min-width": "120"
										}),
										createVNode(_component_el_table_column, {
											prop: "role",
											label: "角色",
											"min-width": "120"
										}),
										createVNode(_component_el_table_column, {
											prop: "phone",
											label: "电话",
											"min-width": "140"
										}),
										createVNode(_component_el_table_column, {
											prop: "email",
											label: "邮箱",
											"min-width": "180"
										})
									]),
									_: 1
								}, 8, ["data"])]),
								_: 1
							}),
							createVNode(_component_el_tab_pane, {
								label: "终验交付",
								name: "acceptance"
							}, {
								default: withCtx(() => {
									var _acceptance$value$sta;
									return [withDirectives((openBlock(), createElementBlock("div", _hoisted_7, [!acceptance.value ? (openBlock(), createElementBlock("div", _hoisted_8, [createVNode(_component_el_empty, { description: "暂无终验申请记录" }, {
										default: withCtx(() => [allMilestonesDone.value ? (openBlock(), createBlock(_component_el_button, {
											key: 0,
											type: "primary",
											icon: "Check",
											onClick: openApplyDialog
										}, {
											default: withCtx(() => [..._cache[23] || (_cache[23] = [createTextVNode(" 申请终验 ", -1)])]),
											_: 1
										})) : (openBlock(), createElementBlock("div", _hoisted_9, [createVNode(_component_el_icon, null, {
											default: withCtx(() => [createVNode(_component_WarningFilled)]),
											_: 1
										}), _cache[24] || (_cache[24] = createTextVNode(" 需完成全部里程碑后才可申请终验 ", -1))]))]),
										_: 1
									})])) : (openBlock(), createElementBlock(Fragment, { key: 1 }, [createVNode(_component_el_descriptions, {
										column: 2,
										border: ""
									}, {
										default: withCtx(() => [
											createVNode(_component_el_descriptions_item, { label: "终验状态" }, {
												default: withCtx(() => [createVNode(_component_el_tag, { type: acceptanceStatusMeta.value.tagType }, {
													default: withCtx(() => [createTextVNode(toDisplayString(acceptanceStatusMeta.value.label), 1)]),
													_: 1
												}, 8, ["type"])]),
												_: 1
											}),
											createVNode(_component_el_descriptions_item, { label: "申请人" }, {
												default: withCtx(() => [createTextVNode(toDisplayString(acceptance.value.applicantName || "-"), 1)]),
												_: 1
											}),
											createVNode(_component_el_descriptions_item, { label: "申请日期" }, {
												default: withCtx(() => [createTextVNode(toDisplayString(formatDate(acceptance.value.applyDate)), 1)]),
												_: 1
											}),
											createVNode(_component_el_descriptions_item, { label: "审批日期" }, {
												default: withCtx(() => [createTextVNode(toDisplayString(formatDate(acceptance.value.acceptDate)), 1)]),
												_: 1
											}),
											createVNode(_component_el_descriptions_item, {
												label: "终验报告",
												span: 2
											}, {
												default: withCtx(() => [createElementVNode("pre", _hoisted_10, toDisplayString(acceptance.value.report || "-"), 1)]),
												_: 1
											}),
											createVNode(_component_el_descriptions_item, {
												label: "审批意见",
												span: 2
											}, {
												default: withCtx(() => [createTextVNode(toDisplayString(acceptance.value.opinion || "-"), 1)]),
												_: 1
											})
										]),
										_: 1
									}), ((_acceptance$value$sta = acceptance.value.status) === null || _acceptance$value$sta === void 0 ? void 0 : _acceptance$value$sta.toUpperCase()) === "PENDING" ? (openBlock(), createElementBlock("div", _hoisted_11, [createVNode(_component_el_button, {
										type: "success",
										icon: "Check",
										onClick: openApproveDialog
									}, {
										default: withCtx(() => [..._cache[25] || (_cache[25] = [createTextVNode("通过终验", -1)])]),
										_: 1
									}), createVNode(_component_el_button, {
										type: "danger",
										icon: "Close",
										onClick: openRejectDialog
									}, {
										default: withCtx(() => [..._cache[26] || (_cache[26] = [createTextVNode("驳回终验", -1)])]),
										_: 1
									})])) : createCommentVNode("", true)], 64))])), [[_directive_loading, acceptanceLoading.value]])];
								}),
								_: 1
							})
						]),
						_: 1
					}, 8, ["modelValue"])]),
					_: 1
				}),
				createVNode(_component_el_dialog, {
					modelValue: milestoneDialogVisible.value,
					"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => milestoneDialogVisible.value = $event),
					title: milestoneDialogTitle.value,
					width: "520px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[5] || (_cache[5] = ($event) => milestoneDialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[27] || (_cache[27] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: milestoneSubmitting.value,
						onClick: handleSubmitMilestone
					}, {
						default: withCtx(() => [..._cache[28] || (_cache[28] = [createTextVNode("确定", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "milestoneFormRef",
						ref: milestoneFormRef,
						model: milestoneForm,
						rules: milestoneRules,
						"label-width": "90px"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, {
								label: "名称",
								prop: "name"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: milestoneForm.name,
									"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => milestoneForm.name = $event),
									placeholder: "请输入里程碑名称"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "类型",
								prop: "type"
							}, {
								default: withCtx(() => [createVNode(_component_el_select, {
									modelValue: milestoneForm.type,
									"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => milestoneForm.type = $event),
									placeholder: "请选择",
									style: { "width": "100%" }
								}, {
									default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(milestoneTypeOptions, (opt) => {
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
							createVNode(_component_el_form_item, {
								label: "计划日期",
								prop: "plannedDate"
							}, {
								default: withCtx(() => [createVNode(_component_el_date_picker, {
									modelValue: milestoneForm.plannedDate,
									"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => milestoneForm.plannedDate = $event),
									type: "date",
									"value-format": "YYYY-MM-DD",
									placeholder: "请选择计划日期",
									style: { "width": "100%" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "描述" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: milestoneForm.description,
									"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => milestoneForm.description = $event),
									type: "textarea",
									rows: 3,
									placeholder: "请输入里程碑描述"
								}, null, 8, ["modelValue"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue", "title"]),
				createVNode(_component_el_dialog, {
					modelValue: progressDialogVisible.value,
					"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => progressDialogVisible.value = $event),
					title: "更新里程碑进度",
					width: "520px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[9] || (_cache[9] = ($event) => progressDialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[29] || (_cache[29] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: progressSubmitting.value,
						onClick: handleSubmitProgress
					}, {
						default: withCtx(() => [..._cache[30] || (_cache[30] = [createTextVNode("确定", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "progressFormRef",
						ref: progressFormRef,
						model: progressForm,
						rules: progressRules,
						"label-width": "100px"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, { label: "里程碑" }, {
								default: withCtx(() => [createElementVNode("span", null, toDisplayString(progressForm.milestoneName), 1)]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "实际日期",
								prop: "actualDate"
							}, {
								default: withCtx(() => [createVNode(_component_el_date_picker, {
									modelValue: progressForm.actualDate,
									"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => progressForm.actualDate = $event),
									type: "date",
									"value-format": "YYYY-MM-DD",
									placeholder: "请选择实际完成日期",
									style: { "width": "100%" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "完成情况",
								prop: "description"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: progressForm.description,
									"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => progressForm.description = $event),
									type: "textarea",
									rows: 3,
									placeholder: "请描述完成情况"
								}, null, 8, ["modelValue"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: applyDialogVisible.value,
					"onUpdate:modelValue": _cache[13] || (_cache[13] = ($event) => applyDialogVisible.value = $event),
					title: "申请终验",
					width: "560px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[12] || (_cache[12] = ($event) => applyDialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[31] || (_cache[31] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: applySubmitting.value,
						onClick: handleSubmitApply
					}, {
						default: withCtx(() => [..._cache[32] || (_cache[32] = [createTextVNode("提交申请", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "applyFormRef",
						ref: applyFormRef,
						model: applyForm,
						rules: applyRules,
						"label-width": "90px"
					}, {
						default: withCtx(() => [createVNode(_component_el_form_item, {
							label: "终验报告",
							prop: "report"
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: applyForm.report,
								"onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => applyForm.report = $event),
								type: "textarea",
								rows: 6,
								placeholder: "请填写终验报告内容"
							}, null, 8, ["modelValue"])]),
							_: 1
						})]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: opinionDialogVisible.value,
					"onUpdate:modelValue": _cache[17] || (_cache[17] = ($event) => opinionDialogVisible.value = $event),
					title: opinionDialogTitle.value,
					width: "520px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[15] || (_cache[15] = ($event) => opinionDialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[33] || (_cache[33] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: opinionDialogTitle.value === "通过终验" ? "success" : "danger",
						loading: opinionSubmitting.value,
						onClick: _cache[16] || (_cache[16] = ($event) => handleSubmitOpinion(opinionDialogTitle.value === "通过终验"))
					}, {
						default: withCtx(() => [..._cache[34] || (_cache[34] = [createTextVNode(" 确定 ", -1)])]),
						_: 1
					}, 8, ["type", "loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "opinionFormRef",
						ref: opinionFormRef,
						model: opinionForm,
						rules: opinionRules,
						"label-width": "90px"
					}, {
						default: withCtx(() => [createVNode(_component_el_form_item, {
							label: "审批意见",
							prop: "opinion"
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: opinionForm.opinion,
								"onUpdate:modelValue": _cache[14] || (_cache[14] = ($event) => opinionForm.opinion = $event),
								type: "textarea",
								rows: 4,
								placeholder: "请填写审批意见"
							}, null, 8, ["modelValue"])]),
							_: 1
						})]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue", "title"])
			])), [[_directive_loading, loading.value]]);
		};
	}
}), [["__scopeId", "data-v-43e8bc40"]]);
//#endregion
export { detail_default as default };

//# sourceMappingURL=detail-Yp-qntw7.js.map