import { d as useRouter } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { t as MobileListCard_default } from "./MobileListCard-DJUDXCEp.js";
import { a as createProject, f as listProjects, g as updateProject, r as approveProject, s as deleteProject } from "./project-Brd7mmQb.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withKeys, withModifiers } from "vue";
//#region src/views/project/list/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "toolbar" };
var _hoisted_3 = { class: "mobile-card-list" };
var _hoisted_4 = { class: "mobile-cards" };
//#endregion
//#region src/views/project/list/index.vue
var list_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const router = useRouter();
		const loading = ref(false);
		const tableData = ref([]);
		const total = ref(0);
		const query = reactive({
			page: 1,
			size: 10,
			projectName: "",
			status: ""
		});
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
		function formatDate(date) {
			if (!date) return "-";
			return date.length > 10 ? date.substring(0, 10) : date;
		}
		const dialogVisible = ref(false);
		const dialogTitle = ref("");
		const submitting = ref(false);
		const formRef = ref();
		function createEmptyForm() {
			return {
				id: void 0,
				name: "",
				type: "NETWORK_DEVICE",
				customerName: "",
				customerContact: "",
				customerPhone: "",
				contractNo: "",
				contractAmount: void 0,
				planStartDate: "",
				planEndDate: "",
				managerName: "",
				priority: 2,
				description: ""
			};
		}
		const form = reactive(createEmptyForm());
		const dateRange = ref(null);
		const rules = {
			name: [{
				required: true,
				message: "请输入项目名称",
				trigger: "blur"
			}],
			type: [{
				required: true,
				message: "请选择项目类型",
				trigger: "change"
			}]
		};
		async function loadData() {
			loading.value = true;
			try {
				var _res$records, _res$total;
				const params = {
					page: query.page,
					size: query.size
				};
				if (query.projectName) params.projectName = query.projectName;
				if (query.status) params.status = query.status;
				const res = await listProjects(params);
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
			query.projectName = "";
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
		function handleAdd() {
			dialogTitle.value = "新建项目";
			Object.assign(form, createEmptyForm());
			dateRange.value = null;
			dialogVisible.value = true;
		}
		function handleEdit(row) {
			dialogTitle.value = "编辑项目";
			Object.assign(form, createEmptyForm(), row);
			dateRange.value = row.planStartDate && row.planEndDate ? [row.planStartDate, row.planEndDate] : null;
			dialogVisible.value = true;
		}
		function handleDateRangeChange(val) {
			if (val && val.length === 2) {
				form.planStartDate = val[0];
				form.planEndDate = val[1];
			} else {
				form.planStartDate = "";
				form.planEndDate = "";
			}
		}
		async function handleSubmit() {
			if (!formRef.value) return;
			await formRef.value.validate(async (valid) => {
				if (!valid) return;
				submitting.value = true;
				try {
					const payload = { ...form };
					if (form.id) {
						await updateProject(payload);
						ElMessage.success("更新成功");
					} else {
						await createProject(payload);
						ElMessage.success("新建成功");
					}
					dialogVisible.value = false;
					loadData();
				} catch (_unused2) {} finally {
					submitting.value = false;
				}
			});
		}
		function handleApprove(row) {
			if (!row.id) return;
			ElMessageBox.confirm(`确认通过项目「${row.name}」的立项审批吗？`, "立项审批", { type: "warning" }).then(async () => {
				await approveProject(row.id);
				ElMessage.success("审批通过");
				loadData();
			}).catch(() => {});
		}
		function handleDelete(row) {
			if (!row.id) return;
			ElMessageBox.confirm(`确定删除项目「${row.name}」吗？`, "提示", { type: "warning" }).then(async () => {
				await deleteProject(row.id);
				ElMessage.success("删除成功");
				loadData();
			}).catch(() => {});
		}
		function viewDetail(row) {
			if (!row.id) return;
			router.push(`/project/detail/${row.id}`);
		}
		const mobileColumns = [
			{
				prop: "code",
				label: "项目编号"
			},
			{
				prop: "type",
				label: "项目类型",
				formatter: (_r, v) => getTypeLabel(v),
				subtitle: true
			},
			{
				prop: "customerName",
				label: "客户名称"
			},
			{
				prop: "status",
				label: "状态",
				render: "tag",
				tagType: (row) => getStatusMeta(row.status).tagType,
				formatter: (row) => getStatusMeta(row.status).label
			},
			{
				prop: "managerName",
				label: "项目经理"
			},
			{
				prop: "planStartDate",
				label: "计划开始",
				formatter: (_r, v) => formatDate(v)
			},
			{
				prop: "planEndDate",
				label: "计划结束",
				formatter: (_r, v) => formatDate(v)
			}
		];
		const mobileOperations = [
			{
				label: "详情",
				type: "primary",
				onClick: (row) => viewDetail(row)
			},
			{
				label: "编辑",
				type: "primary",
				onClick: (row) => handleEdit(row)
			},
			{
				label: "审批",
				type: "warning",
				show: (row) => row.status === "PENDING",
				onClick: (row) => handleApprove(row)
			},
			{
				label: "删除",
				type: "danger",
				onClick: (row) => handleDelete(row)
			}
		];
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
			const _component_el_progress = resolveComponent("el-progress");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_pagination = resolveComponent("el-pagination");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_date_picker = resolveComponent("el-date-picker");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_card, { shadow: "never" }, {
				header: withCtx(() => [..._cache[16] || (_cache[16] = [createElementVNode("span", { class: "page-title" }, "项目管理", -1)])]),
				default: withCtx(() => [
					createVNode(_component_el_form, {
						inline: true,
						onSubmit: _cache[2] || (_cache[2] = withModifiers(() => {}, ["prevent"]))
					}, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, { label: "项目名称" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: query.projectName,
									"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => query.projectName = $event),
									placeholder: "请输入项目名称",
									clearable: "",
									style: { "width": "200px" },
									onKeyup: withKeys(handleSearch, ["enter"])
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "项目状态" }, {
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
									default: withCtx(() => [..._cache[17] || (_cache[17] = [createTextVNode("查询", -1)])]),
									_: 1
								}), createVNode(_component_el_button, {
									icon: "Refresh",
									onClick: handleReset
								}, {
									default: withCtx(() => [..._cache[18] || (_cache[18] = [createTextVNode("重置", -1)])]),
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
						default: withCtx(() => [..._cache[19] || (_cache[19] = [createTextVNode("新建项目", -1)])]),
						_: 1
					})]),
					createElementVNode("div", _hoisted_3, [withDirectives((openBlock(), createBlock(_component_el_table, {
						data: tableData.value,
						border: "",
						stripe: ""
					}, {
						empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无项目数据" })]),
						default: withCtx(() => [
							createVNode(_component_el_table_column, {
								prop: "code",
								label: "项目编号",
								"min-width": "140"
							}),
							createVNode(_component_el_table_column, {
								prop: "name",
								label: "项目名称",
								"min-width": "180",
								"show-overflow-tooltip": ""
							}),
							createVNode(_component_el_table_column, {
								label: "项目类型",
								width: "120"
							}, {
								default: withCtx(({ row }) => [createTextVNode(toDisplayString(getTypeLabel(row.type)), 1)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								prop: "customerName",
								label: "客户名称",
								"min-width": "150",
								"show-overflow-tooltip": ""
							}),
							createVNode(_component_el_table_column, {
								label: "状态",
								width: "110",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, { type: getStatusMeta(row.status).tagType }, {
									default: withCtx(() => [createTextVNode(toDisplayString(getStatusMeta(row.status).label), 1)]),
									_: 2
								}, 1032, ["type"])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "进度",
								width: "160",
								align: "center"
							}, {
								default: withCtx(({ row }) => {
									var _row$progress, _row$progress2;
									return [createVNode(_component_el_progress, {
										percentage: Number((_row$progress = row.progress) !== null && _row$progress !== void 0 ? _row$progress : 0),
										"stroke-width": 10,
										status: Number((_row$progress2 = row.progress) !== null && _row$progress2 !== void 0 ? _row$progress2 : 0) >= 100 ? "success" : ""
									}, null, 8, ["percentage", "status"])];
								}),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "计划开始日期",
								width: "120",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDate(row.planStartDate)), 1)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "计划结束日期",
								width: "120",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDate(row.planEndDate)), 1)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								prop: "managerName",
								label: "项目经理",
								width: "110"
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
										onClick: ($event) => viewDetail(row)
									}, {
										default: withCtx(() => [..._cache[20] || (_cache[20] = [createTextVNode("查看详情", -1)])]),
										_: 1
									}, 8, ["onClick"]),
									createVNode(_component_el_button, {
										link: "",
										type: "primary",
										onClick: ($event) => handleEdit(row)
									}, {
										default: withCtx(() => [..._cache[21] || (_cache[21] = [createTextVNode("编辑", -1)])]),
										_: 1
									}, 8, ["onClick"]),
									row.status === "PENDING" ? (openBlock(), createBlock(_component_el_button, {
										key: 0,
										link: "",
										type: "warning",
										onClick: ($event) => handleApprove(row)
									}, {
										default: withCtx(() => [..._cache[22] || (_cache[22] = [createTextVNode(" 立项审批 ", -1)])]),
										_: 1
									}, 8, ["onClick"])) : createCommentVNode("", true),
									createVNode(_component_el_button, {
										link: "",
										type: "danger",
										onClick: ($event) => handleDelete(row)
									}, {
										default: withCtx(() => [..._cache[23] || (_cache[23] = [createTextVNode("删除", -1)])]),
										_: 1
									}, 8, ["onClick"])
								]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["data"])), [[_directive_loading, loading.value]]), createElementVNode("div", _hoisted_4, [createVNode(MobileListCard_default, {
						data: tableData.value,
						columns: mobileColumns,
						operations: mobileOperations,
						"title-prop": "name",
						"title-icon": "Folder",
						"empty-text": "暂无项目数据"
					}, null, 8, ["data"])])]),
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
				"onUpdate:modelValue": _cache[15] || (_cache[15] = ($event) => dialogVisible.value = $event),
				title: dialogTitle.value,
				width: "680px",
				"destroy-on-close": ""
			}, {
				footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[14] || (_cache[14] = ($event) => dialogVisible.value = false) }, {
					default: withCtx(() => [..._cache[24] || (_cache[24] = [createTextVNode("取消", -1)])]),
					_: 1
				}), createVNode(_component_el_button, {
					type: "primary",
					loading: submitting.value,
					onClick: handleSubmit
				}, {
					default: withCtx(() => [..._cache[25] || (_cache[25] = [createTextVNode("确定", -1)])]),
					_: 1
				}, 8, ["loading"])]),
				default: withCtx(() => [createVNode(_component_el_form, {
					ref_key: "formRef",
					ref: formRef,
					model: form,
					rules,
					"label-width": "110px",
					class: "responsive-form"
				}, {
					default: withCtx(() => [createVNode(_component_el_row, {
						gutter: 16,
						class: "form-two-col-md"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_col, {
								xs: 24,
								sm: 12
							}, {
								default: withCtx(() => [createVNode(_component_el_form_item, {
									label: "项目名称",
									prop: "name"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: form.name,
										"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => form.name = $event),
										placeholder: "请输入项目名称"
									}, null, 8, ["modelValue"])]),
									_: 1
								})]),
								_: 1
							}),
							createVNode(_component_el_col, {
								xs: 24,
								sm: 12
							}, {
								default: withCtx(() => [createVNode(_component_el_form_item, {
									label: "项目类型",
									prop: "type"
								}, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: form.type,
										"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => form.type = $event),
										placeholder: "请选择",
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(typeOptions, (opt) => {
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
							createVNode(_component_el_col, {
								xs: 24,
								sm: 12
							}, {
								default: withCtx(() => [createVNode(_component_el_form_item, { label: "客户名称" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: form.customerName,
										"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => form.customerName = $event),
										placeholder: "请输入客户名称"
									}, null, 8, ["modelValue"])]),
									_: 1
								})]),
								_: 1
							}),
							createVNode(_component_el_col, {
								xs: 24,
								sm: 12
							}, {
								default: withCtx(() => [createVNode(_component_el_form_item, { label: "客户联系人" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: form.customerContact,
										"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => form.customerContact = $event),
										placeholder: "请输入客户联系人"
									}, null, 8, ["modelValue"])]),
									_: 1
								})]),
								_: 1
							}),
							createVNode(_component_el_col, {
								xs: 24,
								sm: 12
							}, {
								default: withCtx(() => [createVNode(_component_el_form_item, { label: "客户电话" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: form.customerPhone,
										"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => form.customerPhone = $event),
										placeholder: "请输入客户电话"
									}, null, 8, ["modelValue"])]),
									_: 1
								})]),
								_: 1
							}),
							createVNode(_component_el_col, {
								xs: 24,
								sm: 12
							}, {
								default: withCtx(() => [createVNode(_component_el_form_item, { label: "合同编号" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: form.contractNo,
										"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => form.contractNo = $event),
										placeholder: "请输入合同编号"
									}, null, 8, ["modelValue"])]),
									_: 1
								})]),
								_: 1
							}),
							createVNode(_component_el_col, {
								xs: 24,
								sm: 12
							}, {
								default: withCtx(() => [createVNode(_component_el_form_item, { label: "合同金额" }, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: form.contractAmount,
										"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => form.contractAmount = $event),
										min: 0,
										precision: 2,
										controls: false,
										style: { "width": "100%" },
										placeholder: "请输入合同金额"
									}, null, 8, ["modelValue"])]),
									_: 1
								})]),
								_: 1
							}),
							createVNode(_component_el_col, {
								xs: 24,
								sm: 12
							}, {
								default: withCtx(() => [createVNode(_component_el_form_item, { label: "项目经理" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: form.managerName,
										"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => form.managerName = $event),
										placeholder: "请输入项目经理"
									}, null, 8, ["modelValue"])]),
									_: 1
								})]),
								_: 1
							}),
							createVNode(_component_el_col, {
								xs: 24,
								sm: 12
							}, {
								default: withCtx(() => [createVNode(_component_el_form_item, { label: "优先级" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: form.priority,
										"onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => form.priority = $event),
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
								default: withCtx(() => [createVNode(_component_el_form_item, { label: "计划起止日期" }, {
									default: withCtx(() => [createVNode(_component_el_date_picker, {
										modelValue: dateRange.value,
										"onUpdate:modelValue": _cache[12] || (_cache[12] = ($event) => dateRange.value = $event),
										type: "daterange",
										"value-format": "YYYY-MM-DD",
										"range-separator": "至",
										"start-placeholder": "开始日期",
										"end-placeholder": "结束日期",
										style: { "width": "100%" },
										onChange: handleDateRangeChange
									}, null, 8, ["modelValue"])]),
									_: 1
								})]),
								_: 1
							}),
							createVNode(_component_el_col, { span: 24 }, {
								default: withCtx(() => [createVNode(_component_el_form_item, { label: "项目描述" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: form.description,
										"onUpdate:modelValue": _cache[13] || (_cache[13] = ($event) => form.description = $event),
										type: "textarea",
										rows: 3,
										placeholder: "请输入项目描述"
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
			}, 8, ["modelValue", "title"])]);
		};
	}
}), [["__scopeId", "data-v-678f3823"]]);
//#endregion
export { list_default as default };

//# sourceMappingURL=list-CY7yAVtM.js.map