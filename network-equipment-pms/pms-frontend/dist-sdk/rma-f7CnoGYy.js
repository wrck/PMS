import { i as post, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withModifiers } from "vue";
//#region src/api/rma.ts
function listRmas(params) {
	return get("/api/asset/rma/list", params);
}
function createRma(data) {
	return post("/api/asset/rma", data);
}
function checkWarranty(id) {
	return post(`/api/asset/rma/${id}/check-warranty`);
}
function issueRma(id) {
	return post(`/api/asset/rma/${id}/issue`);
}
function markReturning(id) {
	return post(`/api/asset/rma/${id}/returning`);
}
function inspectRma(id, data) {
	return post(`/api/asset/rma/${id}/inspect`, data);
}
function closeRma(id) {
	return post(`/api/asset/rma/${id}/close`);
}
function getRmaKpi(startDate, endDate) {
	const params = {};
	if (startDate) params.startDate = startDate;
	if (endDate) params.endDate = endDate;
	return get("/api/asset/rma/kpi", params);
}
//#endregion
//#region src/views/rma/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "kpi-card" };
var _hoisted_3 = { class: "kpi-value" };
var _hoisted_4 = { class: "kpi-card" };
var _hoisted_5 = { class: "kpi-value" };
var _hoisted_6 = { class: "kpi-card" };
var _hoisted_7 = { class: "kpi-value" };
var _hoisted_8 = { class: "kpi-card" };
var _hoisted_9 = { class: "kpi-value" };
var _hoisted_10 = { class: "toolbar" };
//#endregion
//#region src/views/rma/index.vue
var rma_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const loading = ref(false);
		const tableData = ref([]);
		const total = ref(0);
		const kpi = ref({
			mttrHours: 0,
			firstPassRate: 0,
			total: 0,
			closed: 0
		});
		const query = reactive({
			page: 1,
			size: 10,
			ticketStatus: "",
			assetId: void 0
		});
		const statusOptions = [
			{
				value: "REGISTERED",
				label: "已登记",
				tagType: "info"
			},
			{
				value: "WARRANTY_CHECKED",
				label: "已校验保修",
				tagType: "warning"
			},
			{
				value: "RMA_ISSUED",
				label: "已签发 RMA",
				tagType: "primary"
			},
			{
				value: "RETURNING",
				label: "返回中",
				tagType: "warning"
			},
			{
				value: "INSPECTED",
				label: "已检验",
				tagType: "success"
			},
			{
				value: "CLOSED",
				label: "已关闭",
				tagType: "info"
			}
		];
		const warrantyTagType = {
			IN_WARRANTY: "success",
			OUT_OF_WARRANTY: "danger"
		};
		function warrantyLabel(status) {
			if (status === "IN_WARRANTY") return "保内";
			if (status === "OUT_OF_WARRANTY") return "保外";
			return "-";
		}
		function statusMeta(status) {
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
		function nextActionLabel(status) {
			switch (status) {
				case "REGISTERED": return "校验保修";
				case "WARRANTY_CHECKED": return "签发 RMA";
				case "RMA_ISSUED": return "标记返回";
				case "RETURNING": return "到货检验";
				case "INSPECTED": return "关闭";
				default: return "";
			}
		}
		const dialogVisible = ref(false);
		const submitting = ref(false);
		const formRef = ref();
		function createEmptyForm() {
			return {
				assetId: void 0,
				sn: "",
				faultDescription: ""
			};
		}
		const form = reactive(createEmptyForm());
		const rules = {
			assetId: [{
				required: true,
				message: "请输入资产 ID",
				trigger: "blur"
			}],
			faultDescription: [{
				required: true,
				message: "请输入故障描述",
				trigger: "blur"
			}]
		};
		const inspectVisible = ref(false);
		const inspectSubmitting = ref(false);
		const inspectForm = reactive({
			id: 0,
			inspectionResult: "",
			updateAsset: false
		});
		async function loadKpi() {
			try {
				kpi.value = await getRmaKpi("2024-01-01", "2026-12-31");
			} catch (_unused) {}
		}
		async function loadData() {
			loading.value = true;
			try {
				var _res$records, _res$total;
				const params = {
					page: query.page,
					size: query.size
				};
				if (query.ticketStatus) params.ticketStatus = query.ticketStatus;
				if (query.assetId) params.assetId = query.assetId;
				const res = await listRmas(params);
				tableData.value = (_res$records = res.records) !== null && _res$records !== void 0 ? _res$records : [];
				total.value = (_res$total = res.total) !== null && _res$total !== void 0 ? _res$total : 0;
			} catch (_unused2) {} finally {
				loading.value = false;
			}
		}
		function handleSearch() {
			query.page = 1;
			loadData();
		}
		function handleReset() {
			query.ticketStatus = "";
			query.assetId = void 0;
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
					await createRma({
						assetId: form.assetId,
						sn: form.sn,
						faultDescription: form.faultDescription,
						ticketStatus: "REGISTERED"
					});
					ElMessage.success("新建成功");
					dialogVisible.value = false;
					loadData();
					loadKpi();
				} catch (_unused3) {} finally {
					submitting.value = false;
				}
			});
		}
		function handleNext(row) {
			if (!row.id) return;
			switch (row.ticketStatus) {
				case "REGISTERED":
					confirmAction(row, "校验保修", checkWarranty);
					break;
				case "WARRANTY_CHECKED":
					confirmAction(row, "签发 RMA", issueRma);
					break;
				case "RMA_ISSUED":
					confirmAction(row, "标记返回", markReturning);
					break;
				case "RETURNING":
					openInspectDialog(row);
					break;
				case "INSPECTED":
					confirmAction(row, "关闭", closeRma);
					break;
			}
		}
		function confirmAction(row, actionName, fn) {
			var _row$rmaNo;
			if (!row.id) return;
			ElMessageBox.confirm(`确认对 RMA「${(_row$rmaNo = row.rmaNo) !== null && _row$rmaNo !== void 0 ? _row$rmaNo : row.id}」执行「${actionName}」操作吗？`, actionName, { type: "warning" }).then(async () => {
				await fn(row.id);
				ElMessage.success(`${actionName}成功`);
				loadData();
				loadKpi();
			}).catch(() => {});
		}
		function openInspectDialog(row) {
			var _row$id;
			inspectForm.id = (_row$id = row.id) !== null && _row$id !== void 0 ? _row$id : 0;
			inspectForm.inspectionResult = "";
			inspectForm.updateAsset = false;
			inspectVisible.value = true;
		}
		async function handleInspectSubmit() {
			if (!inspectForm.inspectionResult) {
				ElMessage.warning("请填写检验结果");
				return;
			}
			inspectSubmitting.value = true;
			try {
				await inspectRma(inspectForm.id, {
					inspectionResult: inspectForm.inspectionResult,
					updateAsset: inspectForm.updateAsset
				});
				ElMessage.success("检验完成");
				inspectVisible.value = false;
				loadData();
				loadKpi();
			} catch (_unused4) {} finally {
				inspectSubmitting.value = false;
			}
		}
		onMounted(() => {
			loadKpi();
			loadData();
		});
		return (_ctx, _cache) => {
			const _component_el_card = resolveComponent("el-card");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_pagination = resolveComponent("el-pagination");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _component_el_switch = resolveComponent("el-switch");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_row, { gutter: 12 }, {
					default: withCtx(() => [
						createVNode(_component_el_col, { span: 6 }, {
							default: withCtx(() => [createVNode(_component_el_card, { shadow: "never" }, {
								default: withCtx(() => [createElementVNode("div", _hoisted_2, [_cache[12] || (_cache[12] = createElementVNode("div", { class: "kpi-label" }, "MTTR（小时）", -1)), createElementVNode("div", _hoisted_3, toDisplayString(kpi.value.mttrHours.toFixed(1)), 1)])]),
								_: 1
							})]),
							_: 1
						}),
						createVNode(_component_el_col, { span: 6 }, {
							default: withCtx(() => [createVNode(_component_el_card, { shadow: "never" }, {
								default: withCtx(() => [createElementVNode("div", _hoisted_4, [_cache[13] || (_cache[13] = createElementVNode("div", { class: "kpi-label" }, "一次通过率（%）", -1)), createElementVNode("div", _hoisted_5, toDisplayString(kpi.value.firstPassRate.toFixed(1)) + "%", 1)])]),
								_: 1
							})]),
							_: 1
						}),
						createVNode(_component_el_col, { span: 6 }, {
							default: withCtx(() => [createVNode(_component_el_card, { shadow: "never" }, {
								default: withCtx(() => [createElementVNode("div", _hoisted_6, [_cache[14] || (_cache[14] = createElementVNode("div", { class: "kpi-label" }, "RMA 总数", -1)), createElementVNode("div", _hoisted_7, toDisplayString(kpi.value.total), 1)])]),
								_: 1
							})]),
							_: 1
						}),
						createVNode(_component_el_col, { span: 6 }, {
							default: withCtx(() => [createVNode(_component_el_card, { shadow: "never" }, {
								default: withCtx(() => [createElementVNode("div", _hoisted_8, [_cache[15] || (_cache[15] = createElementVNode("div", { class: "kpi-label" }, "已关闭数", -1)), createElementVNode("div", _hoisted_9, toDisplayString(kpi.value.closed), 1)])]),
								_: 1
							})]),
							_: 1
						})
					]),
					_: 1
				}),
				createVNode(_component_el_card, { shadow: "never" }, {
					header: withCtx(() => [..._cache[16] || (_cache[16] = [createElementVNode("span", { class: "page-title" }, "RMA 返修管理", -1)])]),
					default: withCtx(() => [
						createVNode(_component_el_form, {
							inline: true,
							onSubmit: _cache[2] || (_cache[2] = withModifiers(() => {}, ["prevent"]))
						}, {
							default: withCtx(() => [
								createVNode(_component_el_form_item, { label: "工单状态" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: query.ticketStatus,
										"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => query.ticketStatus = $event),
										placeholder: "全部状态",
										clearable: "",
										style: { "width": "180px" }
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
								createVNode(_component_el_form_item, { label: "资产 ID" }, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: query.assetId,
										"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => query.assetId = $event),
										min: 1,
										controls: false,
										placeholder: "请输入资产 ID",
										style: { "width": "160px" }
									}, null, 8, ["modelValue"])]),
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
						createElementVNode("div", _hoisted_10, [createVNode(_component_el_button, {
							type: "primary",
							icon: "Plus",
							onClick: handleAdd
						}, {
							default: withCtx(() => [..._cache[19] || (_cache[19] = [createTextVNode("新建 RMA", -1)])]),
							_: 1
						})]),
						withDirectives((openBlock(), createBlock(_component_el_table, {
							data: tableData.value,
							border: "",
							stripe: ""
						}, {
							empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无 RMA 数据" })]),
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									prop: "rmaNo",
									label: "RMA 编号",
									width: "150"
								}),
								createVNode(_component_el_table_column, {
									prop: "assetId",
									label: "资产 ID",
									width: "100"
								}),
								createVNode(_component_el_table_column, {
									prop: "sn",
									label: "SN",
									width: "140",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "faultDescription",
									label: "故障描述",
									"min-width": "200",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									label: "保修状态",
									width: "100",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, {
										type: warrantyTagType[row.warrantyStatus] || "info",
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(warrantyLabel(row.warrantyStatus)), 1)]),
										_: 2
									}, 1032, ["type"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "工单状态",
									width: "120",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, {
										type: statusMeta(row.ticketStatus).tagType,
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(statusMeta(row.ticketStatus).label), 1)]),
										_: 2
									}, 1032, ["type"])]),
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
									width: "140",
									fixed: "right"
								}, {
									default: withCtx(({ row }) => [nextActionLabel(row.ticketStatus) ? (openBlock(), createBlock(_component_el_button, {
										key: 0,
										link: "",
										type: "primary",
										onClick: ($event) => handleNext(row)
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(nextActionLabel(row.ticketStatus)), 1)]),
										_: 2
									}, 1032, ["onClick"])) : createCommentVNode("", true)]),
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
					"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => dialogVisible.value = $event),
					title: "新建 RMA",
					width: "520px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[6] || (_cache[6] = ($event) => dialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[20] || (_cache[20] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: submitting.value,
						onClick: handleSubmit
					}, {
						default: withCtx(() => [..._cache[21] || (_cache[21] = [createTextVNode("确定", -1)])]),
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
								label: "资产 ID",
								prop: "assetId"
							}, {
								default: withCtx(() => [createVNode(_component_el_input_number, {
									modelValue: form.assetId,
									"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => form.assetId = $event),
									min: 1,
									controls: false,
									placeholder: "请输入资产 ID",
									style: { "width": "100%" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "SN" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.sn,
									"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => form.sn = $event),
									placeholder: "请输入序列号"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "故障描述",
								prop: "faultDescription"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.faultDescription,
									"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => form.faultDescription = $event),
									type: "textarea",
									rows: 4,
									placeholder: "请输入故障描述"
								}, null, 8, ["modelValue"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: inspectVisible.value,
					"onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => inspectVisible.value = $event),
					title: "到货检验",
					width: "520px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[10] || (_cache[10] = ($event) => inspectVisible.value = false) }, {
						default: withCtx(() => [..._cache[22] || (_cache[22] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: inspectSubmitting.value,
						onClick: handleInspectSubmit
					}, {
						default: withCtx(() => [..._cache[23] || (_cache[23] = [createTextVNode(" 提交 ", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, { "label-width": "120px" }, {
						default: withCtx(() => [createVNode(_component_el_form_item, {
							label: "检验结果",
							required: ""
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: inspectForm.inspectionResult,
								"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => inspectForm.inspectionResult = $event),
								type: "textarea",
								rows: 4,
								placeholder: "请输入检验结果"
							}, null, 8, ["modelValue"])]),
							_: 1
						}), createVNode(_component_el_form_item, { label: "是否更新资产状态" }, {
							default: withCtx(() => [createVNode(_component_el_switch, {
								modelValue: inspectForm.updateAsset,
								"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => inspectForm.updateAsset = $event)
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
}), [["__scopeId", "data-v-2038fae9"]]);
//#endregion
export { rma_default as default };

//# sourceMappingURL=rma-f7CnoGYy.js.map