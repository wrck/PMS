import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { c as createSettlement, d as getAgentPage, g as rejectSettlement, m as getSettlementPage, n as approveSettlement, p as getSettlementDetail } from "./implementation-DHYgyd55.js";
import { f as listProjects } from "./project-Brd7mmQb.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withModifiers } from "vue";
//#region src/views/implementation/settlement/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "toolbar" };
var _hoisted_3 = { class: "amount-emph" };
var _hoisted_4 = { class: "summary" };
var _hoisted_5 = { class: "amount-emph" };
var _hoisted_6 = { class: "amount-emph" };
//#endregion
//#region src/views/implementation/settlement/index.vue
var settlement_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const loading = ref(false);
		const tableData = ref([]);
		const total = ref(0);
		const query = reactive({
			page: 1,
			size: 10,
			agentId: void 0,
			status: void 0
		});
		const statusOptions = [
			{
				label: "待审批",
				value: "PENDING"
			},
			{
				label: "已通过",
				value: "APPROVED"
			},
			{
				label: "已驳回",
				value: "REJECTED"
			},
			{
				label: "已推送",
				value: "PUSHED"
			}
		];
		const statusTagType = {
			PENDING: "info",
			APPROVED: "success",
			REJECTED: "danger",
			PUSHED: "primary"
		};
		function statusLabel(status) {
			var _ref, _statusOptions$find$l, _statusOptions$find;
			return (_ref = (_statusOptions$find$l = (_statusOptions$find = statusOptions.find((s) => s.value === status)) === null || _statusOptions$find === void 0 ? void 0 : _statusOptions$find.label) !== null && _statusOptions$find$l !== void 0 ? _statusOptions$find$l : status) !== null && _ref !== void 0 ? _ref : "-";
		}
		function money(v) {
			if (v == null) return "-";
			return v.toLocaleString("zh-CN", {
				minimumFractionDigits: 2,
				maximumFractionDigits: 2
			});
		}
		const agentOptions = ref([]);
		const projectOptions = ref([]);
		const formVisible = ref(false);
		const formSubmitting = ref(false);
		const formRef = ref();
		const form = reactive({
			agentId: void 0,
			projectId: void 0,
			taskId: void 0,
			taxRate: 13,
			remark: "",
			details: []
		});
		const rules = {
			agentId: [{
				required: true,
				message: "请选择代理商",
				trigger: "change"
			}],
			projectId: [{
				required: true,
				message: "请选择项目",
				trigger: "change"
			}],
			taxRate: [{
				required: true,
				message: "请输入税率",
				trigger: "blur"
			}]
		};
		function detailAmount(d) {
			const qty = Number(d.workQuantity) || 0;
			const price = Number(d.unitPrice) || 0;
			return Math.round(qty * price * 100) / 100;
		}
		const totalAmount = computed(() => {
			return Math.round(form.details.reduce((sum, d) => sum + detailAmount(d), 0) * 100) / 100;
		});
		const taxAmount = computed(() => {
			return Math.round(totalAmount.value * (Number(form.taxRate) || 0) / 100 * 100) / 100;
		});
		const totalWithTax = computed(() => {
			return Math.round((totalAmount.value + taxAmount.value) * 100) / 100;
		});
		function addDetailRow() {
			form.details.push({
				itemName: "",
				workQuantity: 1,
				unit: "",
				unitPrice: 0,
				amount: 0
			});
		}
		function removeDetailRow(index) {
			form.details.splice(index, 1);
		}
		function resetForm() {
			form.agentId = void 0;
			form.projectId = void 0;
			form.taskId = void 0;
			form.taxRate = 13;
			form.remark = "";
			form.details = [];
		}
		const detailVisible = ref(false);
		const detailLoading = ref(false);
		const detailData = ref(null);
		async function loadData() {
			loading.value = true;
			try {
				const res = await getSettlementPage(query);
				tableData.value = res.records;
				total.value = res.total;
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		async function loadOptions() {
			try {
				const [agentRes, projectRes] = await Promise.all([getAgentPage({
					page: 1,
					size: 100
				}), listProjects({
					page: 1,
					size: 100
				})]);
				agentOptions.value = agentRes.records;
				projectOptions.value = projectRes.records;
			} catch (_unused2) {}
		}
		function handleSearch() {
			query.page = 1;
			loadData();
		}
		function handleReset() {
			query.agentId = void 0;
			query.status = void 0;
			query.page = 1;
			loadData();
		}
		function handleCreate() {
			resetForm();
			addDetailRow();
			formVisible.value = true;
		}
		async function handleSubmit() {
			if (!formRef.value) return;
			await formRef.value.validate(async (valid) => {
				if (!valid) return;
				if (form.details.length === 0) {
					ElMessage.warning("请至少添加一条结算明细");
					return;
				}
				if (form.details.some((d) => {
					var _d$itemName;
					return !((_d$itemName = d.itemName) === null || _d$itemName === void 0 ? void 0 : _d$itemName.trim());
				})) {
					ElMessage.warning("请填写所有明细的项目名称");
					return;
				}
				formSubmitting.value = true;
				try {
					await createSettlement({
						settlement: {
							agentId: form.agentId,
							projectId: form.projectId,
							taskId: form.taskId,
							taxRate: form.taxRate,
							totalAmount: totalAmount.value,
							taxAmount: taxAmount.value,
							totalWithTax: totalWithTax.value,
							remark: form.remark
						},
						details: form.details.map((d) => ({
							...d,
							amount: detailAmount(d)
						}))
					});
					ElMessage.success("结算单创建成功");
					formVisible.value = false;
					loadData();
				} catch (_unused3) {} finally {
					formSubmitting.value = false;
				}
			});
		}
		async function handleView(row) {
			if (!row.id) return;
			detailVisible.value = true;
			detailLoading.value = true;
			try {
				detailData.value = await getSettlementDetail(row.id);
			} catch (_unused4) {
				detailData.value = row;
			} finally {
				detailLoading.value = false;
			}
		}
		function handleApprove(row) {
			if (!row.id) return;
			ElMessageBox.prompt("请输入审批意见", "审批通过", {
				confirmButtonText: "确定",
				cancelButtonText: "取消",
				inputType: "textarea",
				inputPlaceholder: "请输入审批意见"
			}).then(async ({ value }) => {
				await approveSettlement(row.id, value);
				ElMessage.success("审批通过");
				loadData();
			}).catch(() => {});
		}
		function handleRejectSettlement(row) {
			if (!row.id) return;
			ElMessageBox.prompt("请输入驳回原因", "驳回结算单", {
				confirmButtonText: "确定",
				cancelButtonText: "取消",
				inputType: "textarea",
				inputPlaceholder: "请输入驳回原因",
				inputValidator: (val) => !!(val === null || val === void 0 ? void 0 : val.trim()) || "驳回原因不能为空"
			}).then(async ({ value }) => {
				await rejectSettlement(row.id, value);
				ElMessage.success("已驳回");
				loadData();
			}).catch(() => {});
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
		onMounted(async () => {
			await loadOptions();
			loadData();
		});
		return (_ctx, _cache) => {
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_pagination = resolveComponent("el-pagination");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _component_el_descriptions_item = resolveComponent("el-descriptions-item");
			const _component_el_descriptions = resolveComponent("el-descriptions");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, { shadow: "never" }, {
					header: withCtx(() => [..._cache[11] || (_cache[11] = [createElementVNode("span", { class: "page-title" }, "结算管理", -1)])]),
					default: withCtx(() => [
						createVNode(_component_el_form, {
							inline: true,
							onSubmit: _cache[2] || (_cache[2] = withModifiers(() => {}, ["prevent"]))
						}, {
							default: withCtx(() => [
								createVNode(_component_el_form_item, { label: "代理商" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: query.agentId,
										"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => query.agentId = $event),
										placeholder: "全部代理商",
										clearable: "",
										filterable: "",
										style: { "width": "200px" }
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
								}),
								createVNode(_component_el_form_item, { label: "状态" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: query.status,
										"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => query.status = $event),
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
										default: withCtx(() => [..._cache[12] || (_cache[12] = [createTextVNode("查询", -1)])]),
										_: 1
									}), createVNode(_component_el_button, {
										icon: "Refresh",
										onClick: handleReset
									}, {
										default: withCtx(() => [..._cache[13] || (_cache[13] = [createTextVNode("重置", -1)])]),
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
							onClick: handleCreate
						}, {
							default: withCtx(() => [..._cache[14] || (_cache[14] = [createTextVNode("新建结算", -1)])]),
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
									prop: "settlementNo",
									label: "结算单号",
									"min-width": "160",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "agentName",
									label: "代理商",
									"min-width": "140",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "projectName",
									label: "关联项目",
									"min-width": "160",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									label: "总金额",
									width: "130",
									align: "right"
								}, {
									default: withCtx(({ row }) => [createTextVNode(toDisplayString(money(row.totalAmount)), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "税额",
									width: "120",
									align: "right"
								}, {
									default: withCtx(({ row }) => [createTextVNode(toDisplayString(money(row.taxAmount)), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "含税总额",
									width: "140",
									align: "right"
								}, {
									default: withCtx(({ row }) => [createElementVNode("span", _hoisted_3, toDisplayString(money(row.totalWithTax)), 1)]),
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
									prop: "applyTime",
									label: "申请时间",
									"min-width": "160"
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
											onClick: ($event) => handleView(row)
										}, {
											default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode("查看", -1)])]),
											_: 1
										}, 8, ["onClick"]),
										row.status === "PENDING" ? (openBlock(), createBlock(_component_el_button, {
											key: 0,
											link: "",
											type: "success",
											onClick: ($event) => handleApprove(row)
										}, {
											default: withCtx(() => [..._cache[16] || (_cache[16] = [createTextVNode("审批通过", -1)])]),
											_: 1
										}, 8, ["onClick"])) : createCommentVNode("", true),
										row.status === "PENDING" ? (openBlock(), createBlock(_component_el_button, {
											key: 1,
											link: "",
											type: "danger",
											onClick: ($event) => handleRejectSettlement(row)
										}, {
											default: withCtx(() => [..._cache[17] || (_cache[17] = [createTextVNode("驳回", -1)])]),
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
					modelValue: formVisible.value,
					"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => formVisible.value = $event),
					title: "新建结算单",
					width: "820px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[8] || (_cache[8] = ($event) => formVisible.value = false) }, {
						default: withCtx(() => [..._cache[23] || (_cache[23] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: formSubmitting.value,
						onClick: handleSubmit
					}, {
						default: withCtx(() => [..._cache[24] || (_cache[24] = [createTextVNode("确定", -1)])]),
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
							createVNode(_component_el_row, { gutter: 20 }, {
								default: withCtx(() => [
									createVNode(_component_el_col, { span: 12 }, {
										default: withCtx(() => [createVNode(_component_el_form_item, {
											label: "代理商",
											prop: "agentId"
										}, {
											default: withCtx(() => [createVNode(_component_el_select, {
												modelValue: form.agentId,
												"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => form.agentId = $event),
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
										})]),
										_: 1
									}),
									createVNode(_component_el_col, { span: 12 }, {
										default: withCtx(() => [createVNode(_component_el_form_item, {
											label: "关联项目",
											prop: "projectId"
										}, {
											default: withCtx(() => [createVNode(_component_el_select, {
												modelValue: form.projectId,
												"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => form.projectId = $event),
												placeholder: "请选择项目",
												filterable: "",
												style: { "width": "100%" }
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
										})]),
										_: 1
									}),
									createVNode(_component_el_col, { span: 12 }, {
										default: withCtx(() => [createVNode(_component_el_form_item, {
											label: "关联任务ID",
											prop: "taskId"
										}, {
											default: withCtx(() => [createVNode(_component_el_input_number, {
												modelValue: form.taskId,
												"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => form.taskId = $event),
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
											label: "税率(%)",
											prop: "taxRate"
										}, {
											default: withCtx(() => [createVNode(_component_el_input_number, {
												modelValue: form.taxRate,
												"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => form.taxRate = $event),
												min: 0,
												max: 100,
												precision: 2,
												"controls-position": "right",
												style: { "width": "100%" }
											}, null, 8, ["modelValue"])]),
											_: 1
										})]),
										_: 1
									})
								]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "结算明细" }, {
								default: withCtx(() => [createVNode(_component_el_table, {
									data: form.details,
									border: "",
									size: "small",
									style: { "width": "100%" }
								}, {
									default: withCtx(() => [
										createVNode(_component_el_table_column, {
											label: "项目名称",
											"min-width": "160"
										}, {
											default: withCtx(({ row }) => [createVNode(_component_el_input, {
												modelValue: row.itemName,
												"onUpdate:modelValue": ($event) => row.itemName = $event,
												placeholder: "项目名称",
												size: "small"
											}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "数量",
											width: "110"
										}, {
											default: withCtx(({ row }) => [createVNode(_component_el_input_number, {
												modelValue: row.workQuantity,
												"onUpdate:modelValue": ($event) => row.workQuantity = $event,
												min: 0,
												precision: 2,
												size: "small",
												"controls-position": "right",
												style: { "width": "100%" }
											}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "单位",
											width: "100"
										}, {
											default: withCtx(({ row }) => [createVNode(_component_el_input, {
												modelValue: row.unit,
												"onUpdate:modelValue": ($event) => row.unit = $event,
												placeholder: "单位",
												size: "small"
											}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "单价",
											width: "130"
										}, {
											default: withCtx(({ row }) => [createVNode(_component_el_input_number, {
												modelValue: row.unitPrice,
												"onUpdate:modelValue": ($event) => row.unitPrice = $event,
												min: 0,
												precision: 2,
												size: "small",
												"controls-position": "right",
												style: { "width": "100%" }
											}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "金额",
											width: "120",
											align: "right"
										}, {
											default: withCtx(({ row }) => [createTextVNode(toDisplayString(money(detailAmount(row))), 1)]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "操作",
											width: "80",
											align: "center"
										}, {
											default: withCtx(({ $index }) => [createVNode(_component_el_button, {
												link: "",
												type: "danger",
												size: "small",
												onClick: ($event) => removeDetailRow($index)
											}, {
												default: withCtx(() => [..._cache[18] || (_cache[18] = [createTextVNode("删除", -1)])]),
												_: 1
											}, 8, ["onClick"])]),
											_: 1
										})
									]),
									_: 1
								}, 8, ["data"]), createVNode(_component_el_button, {
									icon: "Plus",
									size: "small",
									class: "add-row-btn",
									onClick: addDetailRow
								}, {
									default: withCtx(() => [..._cache[19] || (_cache[19] = [createTextVNode("添加明细", -1)])]),
									_: 1
								})]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "备注",
								prop: "remark"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.remark,
									"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => form.remark = $event),
									type: "textarea",
									rows: 2,
									placeholder: "备注信息"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "合计" }, {
								default: withCtx(() => [createElementVNode("div", _hoisted_4, [
									createElementVNode("span", null, [_cache[20] || (_cache[20] = createTextVNode("总金额：", -1)), createElementVNode("b", null, toDisplayString(money(totalAmount.value)), 1)]),
									createElementVNode("span", null, [_cache[21] || (_cache[21] = createTextVNode("税额：", -1)), createElementVNode("b", null, toDisplayString(money(taxAmount.value)), 1)]),
									createElementVNode("span", null, [_cache[22] || (_cache[22] = createTextVNode("含税总额：", -1)), createElementVNode("b", _hoisted_5, toDisplayString(money(totalWithTax.value)), 1)])
								])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: detailVisible.value,
					"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => detailVisible.value = $event),
					title: "结算单详情",
					width: "820px",
					"destroy-on-close": ""
				}, {
					default: withCtx(() => {
						var _detailData$value;
						return [withDirectives((openBlock(), createElementBlock("div", null, [
							detailData.value ? (openBlock(), createBlock(_component_el_descriptions, {
								key: 0,
								column: 3,
								border: ""
							}, {
								default: withCtx(() => [
									createVNode(_component_el_descriptions_item, { label: "结算单号" }, {
										default: withCtx(() => [createTextVNode(toDisplayString(detailData.value.settlementNo), 1)]),
										_: 1
									}),
									createVNode(_component_el_descriptions_item, { label: "代理商" }, {
										default: withCtx(() => [createTextVNode(toDisplayString(detailData.value.agentName), 1)]),
										_: 1
									}),
									createVNode(_component_el_descriptions_item, { label: "关联项目" }, {
										default: withCtx(() => [createTextVNode(toDisplayString(detailData.value.projectName), 1)]),
										_: 1
									}),
									createVNode(_component_el_descriptions_item, { label: "税率" }, {
										default: withCtx(() => [createTextVNode(toDisplayString(detailData.value.taxRate) + "%", 1)]),
										_: 1
									}),
									createVNode(_component_el_descriptions_item, { label: "总金额" }, {
										default: withCtx(() => [createTextVNode(toDisplayString(money(detailData.value.totalAmount)), 1)]),
										_: 1
									}),
									createVNode(_component_el_descriptions_item, { label: "税额" }, {
										default: withCtx(() => [createTextVNode(toDisplayString(money(detailData.value.taxAmount)), 1)]),
										_: 1
									}),
									createVNode(_component_el_descriptions_item, { label: "含税总额" }, {
										default: withCtx(() => [createElementVNode("span", _hoisted_6, toDisplayString(money(detailData.value.totalWithTax)), 1)]),
										_: 1
									}),
									createVNode(_component_el_descriptions_item, { label: "状态" }, {
										default: withCtx(() => [createVNode(_component_el_tag, {
											type: statusTagType[detailData.value.status] || "info",
											size: "small"
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(statusLabel(detailData.value.status)), 1)]),
											_: 1
										}, 8, ["type"])]),
										_: 1
									}),
									createVNode(_component_el_descriptions_item, { label: "申请时间" }, {
										default: withCtx(() => [createTextVNode(toDisplayString(detailData.value.applyTime), 1)]),
										_: 1
									}),
									createVNode(_component_el_descriptions_item, {
										label: "备注",
										span: 3
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(detailData.value.remark || "-"), 1)]),
										_: 1
									})
								]),
								_: 1
							})) : createCommentVNode("", true),
							_cache[25] || (_cache[25] = createElementVNode("h4", { class: "detail-sub-title" }, "结算明细", -1)),
							createVNode(_component_el_table, {
								data: ((_detailData$value = detailData.value) === null || _detailData$value === void 0 ? void 0 : _detailData$value.details) || [],
								border: "",
								stripe: "",
								size: "small"
							}, {
								empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无明细" })]),
								default: withCtx(() => [
									createVNode(_component_el_table_column, {
										type: "index",
										label: "#",
										width: "50"
									}),
									createVNode(_component_el_table_column, {
										prop: "itemName",
										label: "项目名称",
										"min-width": "160"
									}),
									createVNode(_component_el_table_column, {
										prop: "workQuantity",
										label: "数量",
										width: "100",
										align: "right"
									}),
									createVNode(_component_el_table_column, {
										prop: "unit",
										label: "单位",
										width: "90"
									}),
									createVNode(_component_el_table_column, {
										prop: "unitPrice",
										label: "单价",
										width: "120",
										align: "right"
									}),
									createVNode(_component_el_table_column, {
										label: "金额",
										width: "130",
										align: "right"
									}, {
										default: withCtx(({ row }) => [createTextVNode(toDisplayString(money(row.amount)), 1)]),
										_: 1
									})
								]),
								_: 1
							}, 8, ["data"])
						])), [[_directive_loading, detailLoading.value]])];
					}),
					_: 1
				}, 8, ["modelValue"])
			]);
		};
	}
}), [["__scopeId", "data-v-bf2a0e26"]]);
//#endregion
export { settlement_default as default };

//# sourceMappingURL=settlement-C7qQNhpq.js.map