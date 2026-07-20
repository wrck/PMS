import { i as post, n as del, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { t as FileUploader_default } from "./FileUploader-DgB3jua6.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withModifiers } from "vue";
//#region src/api/punch-list.ts
function listPunchLists(params) {
	return get("/api/project/punch-list/list", params);
}
function createPunchList(data) {
	return post("/api/project/punch-list", data);
}
function resolvePunchList(id) {
	return post(`/api/project/punch-list/${id}/resolve`);
}
function verifyPunchList(id) {
	return post(`/api/project/punch-list/${id}/verify`);
}
function deletePunchList(id) {
	return del(`/api/project/punch-list/${id}`);
}
//#endregion
//#region src/views/punch-list/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "toolbar" };
//#endregion
//#region src/views/punch-list/index.vue
var punch_list_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const loading = ref(false);
		const tableData = ref([]);
		const total = ref(0);
		const query = reactive({
			page: 1,
			size: 10,
			projectId: void 0,
			severity: "",
			status: ""
		});
		const severityOptions = [
			{
				value: "SAFETY",
				label: "安全",
				tagType: "danger"
			},
			{
				value: "FUNCTIONAL",
				label: "功能",
				tagType: "warning"
			},
			{
				value: "COSMETIC",
				label: "外观",
				tagType: "info"
			}
		];
		const statusOptions = [
			{
				value: "OPEN",
				label: "待整改",
				tagType: "warning"
			},
			{
				value: "RESOLVED",
				label: "已整改",
				tagType: "primary"
			},
			{
				value: "VERIFIED",
				label: "已验证",
				tagType: "success"
			}
		];
		const walkdownOptions = [{
			value: "PRE_PUNCH",
			label: "预走场"
		}, {
			value: "FORMAL",
			label: "正式走场"
		}];
		function getSeverityMeta(severity) {
			var _severityOptions$find;
			return (_severityOptions$find = severityOptions.find((s) => s.value === severity)) !== null && _severityOptions$find !== void 0 ? _severityOptions$find : {
				label: severity !== null && severity !== void 0 ? severity : "-",
				tagType: "info"
			};
		}
		function getStatusMeta(status) {
			var _statusOptions$find;
			return (_statusOptions$find = statusOptions.find((s) => s.value === status)) !== null && _statusOptions$find !== void 0 ? _statusOptions$find : {
				label: status !== null && status !== void 0 ? status : "-",
				tagType: "info"
			};
		}
		function formatDateTime(val) {
			var _val$replace$slice;
			return (_val$replace$slice = val === null || val === void 0 ? void 0 : val.replace("T", " ").slice(0, 19)) !== null && _val$replace$slice !== void 0 ? _val$replace$slice : "-";
		}
		const dialogVisible = ref(false);
		const submitting = ref(false);
		const formRef = ref();
		function createEmptyForm() {
			return {
				id: void 0,
				projectId: void 0,
				milestoneId: void 0,
				severity: "FUNCTIONAL",
				title: "",
				description: "",
				walkdownStage: "PRE_PUNCH",
				assigneeId: void 0,
				assigneeName: "",
				deadline: "",
				attachmentIds: []
			};
		}
		const form = reactive(createEmptyForm());
		const rules = {
			projectId: [{
				required: true,
				message: "请输入项目 ID",
				trigger: "blur"
			}],
			severity: [{
				required: true,
				message: "请选择严重等级",
				trigger: "change"
			}],
			title: [{
				required: true,
				message: "请输入标题",
				trigger: "blur"
			}],
			walkdownStage: [{
				required: true,
				message: "请选择走场阶段",
				trigger: "change"
			}]
		};
		function handleUploaded(payload) {
			const id = typeof payload === "number" ? payload : payload === null || payload === void 0 ? void 0 : payload.id;
			if (typeof id === "number") form.attachmentIds.push(id);
		}
		async function loadData() {
			loading.value = true;
			try {
				var _res$records, _res$total;
				const params = {
					page: query.page,
					size: query.size
				};
				if (query.projectId) params.projectId = query.projectId;
				if (query.severity) params.severity = query.severity;
				if (query.status) params.status = query.status;
				const res = await listPunchLists(params);
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
			query.severity = "";
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
			Object.assign(form, createEmptyForm());
			dialogVisible.value = true;
		}
		async function handleSubmit() {
			if (!formRef.value) return;
			await formRef.value.validate(async (valid) => {
				if (!valid) return;
				submitting.value = true;
				try {
					await createPunchList({
						projectId: form.projectId,
						milestoneId: form.milestoneId,
						severity: form.severity,
						title: form.title,
						description: form.description,
						walkdownStage: form.walkdownStage,
						assigneeId: form.assigneeId,
						assigneeName: form.assigneeName,
						deadline: form.deadline,
						status: "OPEN",
						attachmentIds: form.attachmentIds
					});
					ElMessage.success("新建成功");
					dialogVisible.value = false;
					loadData();
				} catch (_unused2) {} finally {
					submitting.value = false;
				}
			});
		}
		function handleResolve(row) {
			if (!row.id) return;
			ElMessageBox.confirm(`确认缺陷「${row.title}」已完成整改吗？`, "整改确认", { type: "warning" }).then(async () => {
				await resolvePunchList(row.id);
				ElMessage.success("已标记为整改完成");
				loadData();
			}).catch(() => {});
		}
		function handleVerify(row) {
			if (!row.id) return;
			ElMessageBox.confirm(`确认验证通过缺陷「${row.title}」吗？`, "验证确认", { type: "warning" }).then(async () => {
				await verifyPunchList(row.id);
				ElMessage.success("验证通过");
				loadData();
			}).catch(() => {});
		}
		function handleDelete(row) {
			if (!row.id) return;
			ElMessageBox.confirm(`确定删除缺陷「${row.title}」吗？`, "提示", { type: "warning" }).then(async () => {
				await deletePunchList(row.id);
				ElMessage.success("删除成功");
				loadData();
			}).catch(() => {});
		}
		onMounted(loadData);
		return (_ctx, _cache) => {
			const _component_el_input_number = resolveComponent("el-input-number");
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
			const _component_el_col = resolveComponent("el-col");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_date_picker = resolveComponent("el-date-picker");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_card, { shadow: "never" }, {
				header: withCtx(() => [..._cache[14] || (_cache[14] = [createElementVNode("span", { class: "page-title" }, "Punch List 管理", -1)])]),
				default: withCtx(() => [
					createVNode(_component_el_form, {
						inline: true,
						onSubmit: _cache[3] || (_cache[3] = withModifiers(() => {}, ["prevent"]))
					}, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, { label: "项目 ID" }, {
								default: withCtx(() => [createVNode(_component_el_input_number, {
									modelValue: query.projectId,
									"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => query.projectId = $event),
									min: 1,
									controls: false,
									placeholder: "请输入项目 ID",
									style: { "width": "160px" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "严重等级" }, {
								default: withCtx(() => [createVNode(_component_el_select, {
									modelValue: query.severity,
									"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => query.severity = $event),
									placeholder: "全部等级",
									clearable: "",
									style: { "width": "160px" }
								}, {
									default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(severityOptions, (opt) => {
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
							createVNode(_component_el_form_item, { label: "状态" }, {
								default: withCtx(() => [createVNode(_component_el_select, {
									modelValue: query.status,
									"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => query.status = $event),
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
									default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode("查询", -1)])]),
									_: 1
								}), createVNode(_component_el_button, {
									icon: "Refresh",
									onClick: handleReset
								}, {
									default: withCtx(() => [..._cache[16] || (_cache[16] = [createTextVNode("重置", -1)])]),
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
						default: withCtx(() => [..._cache[17] || (_cache[17] = [createTextVNode("新建缺陷", -1)])]),
						_: 1
					})]),
					withDirectives((openBlock(), createBlock(_component_el_table, {
						data: tableData.value,
						border: "",
						stripe: ""
					}, {
						empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无缺陷数据" })]),
						default: withCtx(() => [
							createVNode(_component_el_table_column, {
								prop: "id",
								label: "ID",
								width: "70"
							}),
							createVNode(_component_el_table_column, {
								prop: "title",
								label: "标题",
								"min-width": "180",
								"show-overflow-tooltip": ""
							}),
							createVNode(_component_el_table_column, {
								label: "严重等级",
								width: "100",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, {
									type: getSeverityMeta(row.severity).tagType,
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(getSeverityMeta(row.severity).label), 1)]),
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
								prop: "assigneeName",
								label: "负责人",
								width: "110"
							}),
							createVNode(_component_el_table_column, {
								label: "截止日期",
								width: "120",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDateTime(row.deadline)), 1)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "创建时间",
								width: "160",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDateTime(row.createdAt)), 1)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "操作",
								width: "200",
								fixed: "right"
							}, {
								default: withCtx(({ row }) => [
									row.status === "OPEN" ? (openBlock(), createBlock(_component_el_button, {
										key: 0,
										link: "",
										type: "primary",
										onClick: ($event) => handleResolve(row)
									}, {
										default: withCtx(() => [..._cache[18] || (_cache[18] = [createTextVNode(" 整改 ", -1)])]),
										_: 1
									}, 8, ["onClick"])) : createCommentVNode("", true),
									row.status === "RESOLVED" ? (openBlock(), createBlock(_component_el_button, {
										key: 1,
										link: "",
										type: "success",
										onClick: ($event) => handleVerify(row)
									}, {
										default: withCtx(() => [..._cache[19] || (_cache[19] = [createTextVNode(" 验证 ", -1)])]),
										_: 1
									}, 8, ["onClick"])) : createCommentVNode("", true),
									createVNode(_component_el_button, {
										link: "",
										type: "danger",
										onClick: ($event) => handleDelete(row)
									}, {
										default: withCtx(() => [..._cache[20] || (_cache[20] = [createTextVNode("删除", -1)])]),
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
			}), createVNode(_component_el_dialog, {
				modelValue: dialogVisible.value,
				"onUpdate:modelValue": _cache[13] || (_cache[13] = ($event) => dialogVisible.value = $event),
				title: "新建缺陷",
				width: "640px",
				"destroy-on-close": ""
			}, {
				footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[12] || (_cache[12] = ($event) => dialogVisible.value = false) }, {
					default: withCtx(() => [..._cache[21] || (_cache[21] = [createTextVNode("取消", -1)])]),
					_: 1
				}), createVNode(_component_el_button, {
					type: "primary",
					loading: submitting.value,
					onClick: handleSubmit
				}, {
					default: withCtx(() => [..._cache[22] || (_cache[22] = [createTextVNode("确定", -1)])]),
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
										controls: false,
										placeholder: "请输入项目 ID",
										style: { "width": "100%" }
									}, null, 8, ["modelValue"])]),
									_: 1
								})]),
								_: 1
							}),
							createVNode(_component_el_col, { span: 12 }, {
								default: withCtx(() => [createVNode(_component_el_form_item, { label: "里程碑 ID" }, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: form.milestoneId,
										"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => form.milestoneId = $event),
										min: 1,
										controls: false,
										placeholder: "可选",
										style: { "width": "100%" }
									}, null, 8, ["modelValue"])]),
									_: 1
								})]),
								_: 1
							}),
							createVNode(_component_el_col, { span: 12 }, {
								default: withCtx(() => [createVNode(_component_el_form_item, {
									label: "严重等级",
									prop: "severity"
								}, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: form.severity,
										"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => form.severity = $event),
										placeholder: "请选择",
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(severityOptions, (opt) => {
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
								default: withCtx(() => [createVNode(_component_el_form_item, {
									label: "走场阶段",
									prop: "walkdownStage"
								}, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: form.walkdownStage,
										"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => form.walkdownStage = $event),
										placeholder: "请选择",
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(walkdownOptions, (opt) => {
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
									label: "标题",
									prop: "title"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: form.title,
										"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => form.title = $event),
										placeholder: "请输入缺陷标题"
									}, null, 8, ["modelValue"])]),
									_: 1
								})]),
								_: 1
							}),
							createVNode(_component_el_col, { span: 24 }, {
								default: withCtx(() => [createVNode(_component_el_form_item, { label: "描述" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: form.description,
										"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => form.description = $event),
										type: "textarea",
										rows: 3,
										placeholder: "请输入缺陷描述"
									}, null, 8, ["modelValue"])]),
									_: 1
								})]),
								_: 1
							}),
							createVNode(_component_el_col, { span: 12 }, {
								default: withCtx(() => [createVNode(_component_el_form_item, { label: "负责人 ID" }, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: form.assigneeId,
										"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => form.assigneeId = $event),
										min: 1,
										controls: false,
										placeholder: "可选",
										style: { "width": "100%" }
									}, null, 8, ["modelValue"])]),
									_: 1
								})]),
								_: 1
							}),
							createVNode(_component_el_col, { span: 12 }, {
								default: withCtx(() => [createVNode(_component_el_form_item, { label: "截止日期" }, {
									default: withCtx(() => [createVNode(_component_el_date_picker, {
										modelValue: form.deadline,
										"onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => form.deadline = $event),
										type: "date",
										"value-format": "YYYY-MM-DD",
										placeholder: "选择截止日期",
										style: { "width": "100%" }
									}, null, 8, ["modelValue"])]),
									_: 1
								})]),
								_: 1
							}),
							createVNode(_component_el_col, { span: 24 }, {
								default: withCtx(() => [createVNode(_component_el_form_item, { label: "缺陷照片" }, {
									default: withCtx(() => [createVNode(FileUploader_default, {
										"biz-type": "PUNCH_LIST",
										onUploaded: handleUploaded
									})]),
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
			}, 8, ["modelValue"])]);
		};
	}
}), [["__scopeId", "data-v-bf0ce12a"]]);
//#endregion
export { punch_list_default as default };

//# sourceMappingURL=punch-list-BuiV2gdd.js.map