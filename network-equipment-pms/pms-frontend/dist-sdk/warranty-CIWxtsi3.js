import { a as put, i as post, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, createBlock, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withModifiers } from "vue";
//#region src/api/warranty.ts
function listWarranties(params) {
	return get("/api/asset/warranty/list", params);
}
function createWarranty(data) {
	return post("/api/asset/warranty", data);
}
/** 续保 — 通过通用更新接口修改截止日期和时长 */
function renewWarranty(id, data) {
	return put("/api/asset/warranty", {
		id,
		durationMonths: data.durationMonths,
		endDate: data.endDate
	});
}
/** 退网 — 通过通用更新接口将截止日期设为今天 */
function decommissionAsset(id) {
	return put("/api/asset/warranty", {
		id,
		endDate: (/* @__PURE__ */ new Date()).toISOString().slice(0, 10)
	}).then(() => true);
}
//#endregion
//#region src/views/warranty/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "toolbar" };
//#endregion
//#region src/views/warranty/index.vue
var warranty_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const loading = ref(false);
		const tableData = ref([]);
		const total = ref(0);
		const query = reactive({
			page: 1,
			size: 10,
			assetId: void 0,
			expiringDays: void 0
		});
		const expiringOptions = [
			{
				value: 30,
				label: "30 天内"
			},
			{
				value: 60,
				label: "60 天内"
			},
			{
				value: 90,
				label: "90 天内"
			}
		];
		const slaOptions = [
			{
				value: "BASIC",
				label: "基础版",
				tagType: "info"
			},
			{
				value: "PREMIUM",
				label: "高级版",
				tagType: "warning"
			},
			{
				value: "PLATINUM",
				label: "铂金版",
				tagType: "danger"
			}
		];
		function getSlaMeta(level) {
			var _slaOptions$find;
			return (_slaOptions$find = slaOptions.find((s) => s.value === level)) !== null && _slaOptions$find !== void 0 ? _slaOptions$find : {
				label: level !== null && level !== void 0 ? level : "-",
				tagType: "info"
			};
		}
		function formatDateTime(val) {
			var _val$replace$slice;
			return (_val$replace$slice = val === null || val === void 0 ? void 0 : val.replace("T", " ").slice(0, 19)) !== null && _val$replace$slice !== void 0 ? _val$replace$slice : "-";
		}
		function remainingDays(endDate) {
			if (!endDate) return Number.POSITIVE_INFINITY;
			const end = new Date(endDate).getTime();
			if (Number.isNaN(end)) return Number.POSITIVE_INFINITY;
			const diff = end - Date.now();
			return Math.ceil(diff / 864e5);
		}
		function remainingMeta(days) {
			if (days < 0) return {
				color: "#909399",
				label: "已过期"
			};
			if (days < 30) return {
				color: "#f56c6c",
				label: `${days} 天`
			};
			if (days < 60) return {
				color: "#e6a23c",
				label: `${days} 天`
			};
			if (days < 90) return {
				color: "#f0c040",
				label: `${days} 天`
			};
			return {
				color: "#67c23a",
				label: `${days} 天`
			};
		}
		const dialogVisible = ref(false);
		const submitting = ref(false);
		const formRef = ref();
		function createEmptyForm() {
			return {
				assetId: void 0,
				startDate: "",
				durationMonths: 12,
				slaLevel: "BASIC",
				contractNo: ""
			};
		}
		const form = reactive(createEmptyForm());
		const rules = {
			assetId: [{
				required: true,
				message: "请输入资产 ID",
				trigger: "blur"
			}],
			startDate: [{
				required: true,
				message: "请选择起始日期",
				trigger: "change"
			}],
			durationMonths: [{
				required: true,
				message: "请输入月数",
				trigger: "blur"
			}],
			slaLevel: [{
				required: true,
				message: "请选择 SLA 等级",
				trigger: "change"
			}]
		};
		const renewVisible = ref(false);
		const renewSubmitting = ref(false);
		const renewForm = reactive({
			id: 0,
			durationMonths: 12,
			endDate: ""
		});
		const renewRules = {
			durationMonths: [{
				required: true,
				message: "请输入续保月数",
				trigger: "blur"
			}],
			endDate: [{
				required: true,
				message: "请选择结束日期",
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
				if (query.assetId) params.assetId = query.assetId;
				if (query.expiringDays) params.expiringDays = query.expiringDays;
				const res = await listWarranties(params);
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
			query.assetId = void 0;
			query.expiringDays = void 0;
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
					const start = new Date(form.startDate);
					const end = new Date(start);
					end.setMonth(end.getMonth() + form.durationMonths);
					await createWarranty({
						assetId: form.assetId,
						startDate: form.startDate,
						endDate: end.toISOString().slice(0, 10),
						durationMonths: form.durationMonths,
						slaLevel: form.slaLevel,
						contractNo: form.contractNo
					});
					ElMessage.success("新建成功");
					dialogVisible.value = false;
					loadData();
				} catch (_unused2) {} finally {
					submitting.value = false;
				}
			});
		}
		function handleRenew(row) {
			if (!row.id) return;
			renewForm.id = row.id;
			renewForm.durationMonths = 12;
			renewForm.endDate = "";
			renewVisible.value = true;
		}
		async function handleRenewSubmit() {
			if (!renewForm.durationMonths || !renewForm.endDate) {
				ElMessage.warning("请填写完整的续保信息");
				return;
			}
			renewSubmitting.value = true;
			try {
				await renewWarranty(renewForm.id, {
					durationMonths: renewForm.durationMonths,
					endDate: renewForm.endDate
				});
				ElMessage.success("续保成功");
				renewVisible.value = false;
				loadData();
			} catch (_unused3) {} finally {
				renewSubmitting.value = false;
			}
		}
		function handleDecommission(row) {
			if (!row.id) return;
			ElMessageBox.confirm(`确认对资产「${row.assetId}」执行退网操作吗？退网后质保将失效。`, "退网确认", { type: "warning" }).then(async () => {
				await decommissionAsset(row.id);
				ElMessage.success("退网成功");
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
			const _component_el_date_picker = resolveComponent("el-date-picker");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, { shadow: "never" }, {
					header: withCtx(() => [..._cache[14] || (_cache[14] = [createElementVNode("span", { class: "page-title" }, "质保期管理", -1)])]),
					default: withCtx(() => [
						createVNode(_component_el_form, {
							inline: true,
							onSubmit: _cache[2] || (_cache[2] = withModifiers(() => {}, ["prevent"]))
						}, {
							default: withCtx(() => [
								createVNode(_component_el_form_item, { label: "资产 ID" }, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: query.assetId,
										"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => query.assetId = $event),
										min: 1,
										controls: false,
										placeholder: "请输入资产 ID",
										style: { "width": "160px" }
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "即将到期" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: query.expiringDays,
										"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => query.expiringDays = $event),
										placeholder: "全部",
										clearable: "",
										style: { "width": "160px" }
									}, {
										default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(expiringOptions, (opt) => {
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
							default: withCtx(() => [..._cache[17] || (_cache[17] = [createTextVNode("新建质保", -1)])]),
							_: 1
						})]),
						withDirectives((openBlock(), createBlock(_component_el_table, {
							data: tableData.value,
							border: "",
							stripe: ""
						}, {
							empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无质保数据" })]),
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									prop: "id",
									label: "ID",
									width: "70"
								}),
								createVNode(_component_el_table_column, {
									prop: "assetId",
									label: "资产 ID",
									width: "100"
								}),
								createVNode(_component_el_table_column, {
									label: "起始日期",
									width: "120",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDateTime(row.startDate)), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "结束日期",
									width: "120",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDateTime(row.endDate)), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "SLA 等级",
									width: "110",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, {
										type: getSlaMeta(row.slaLevel).tagType,
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(getSlaMeta(row.slaLevel).label), 1)]),
										_: 2
									}, 1032, ["type"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									prop: "contractNo",
									label: "合同号",
									"min-width": "140",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									label: "剩余天数",
									width: "120",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, {
										color: remainingMeta(remainingDays(row.endDate)).color,
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(remainingMeta(remainingDays(row.endDate)).label), 1)]),
										_: 2
									}, 1032, ["color"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "操作",
									width: "160",
									fixed: "right"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_button, {
										link: "",
										type: "primary",
										onClick: ($event) => handleRenew(row)
									}, {
										default: withCtx(() => [..._cache[18] || (_cache[18] = [createTextVNode("续保", -1)])]),
										_: 1
									}, 8, ["onClick"]), createVNode(_component_el_button, {
										link: "",
										type: "danger",
										onClick: ($event) => handleDecommission(row)
									}, {
										default: withCtx(() => [..._cache[19] || (_cache[19] = [createTextVNode("退网", -1)])]),
										_: 1
									}, 8, ["onClick"])]),
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
					"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => dialogVisible.value = $event),
					title: "新建质保",
					width: "520px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[8] || (_cache[8] = ($event) => dialogVisible.value = false) }, {
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
							createVNode(_component_el_form_item, {
								label: "起始日期",
								prop: "startDate"
							}, {
								default: withCtx(() => [createVNode(_component_el_date_picker, {
									modelValue: form.startDate,
									"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => form.startDate = $event),
									type: "date",
									"value-format": "YYYY-MM-DD",
									placeholder: "选择起始日期",
									style: { "width": "100%" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "质保月数",
								prop: "durationMonths"
							}, {
								default: withCtx(() => [createVNode(_component_el_input_number, {
									modelValue: form.durationMonths,
									"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => form.durationMonths = $event),
									min: 1,
									max: 120,
									style: { "width": "100%" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "SLA 等级",
								prop: "slaLevel"
							}, {
								default: withCtx(() => [createVNode(_component_el_select, {
									modelValue: form.slaLevel,
									"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => form.slaLevel = $event),
									placeholder: "请选择",
									style: { "width": "100%" }
								}, {
									default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(slaOptions, (opt) => {
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
							createVNode(_component_el_form_item, { label: "合同号" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.contractNo,
									"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => form.contractNo = $event),
									placeholder: "请输入合同号"
								}, null, 8, ["modelValue"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: renewVisible.value,
					"onUpdate:modelValue": _cache[13] || (_cache[13] = ($event) => renewVisible.value = $event),
					title: "质保续保",
					width: "460px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[12] || (_cache[12] = ($event) => renewVisible.value = false) }, {
						default: withCtx(() => [..._cache[22] || (_cache[22] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: renewSubmitting.value,
						onClick: handleRenewSubmit
					}, {
						default: withCtx(() => [..._cache[23] || (_cache[23] = [createTextVNode("确定", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, {
						model: renewForm,
						rules: renewRules,
						"label-width": "100px"
					}, {
						default: withCtx(() => [createVNode(_component_el_form_item, {
							label: "续保月数",
							prop: "durationMonths"
						}, {
							default: withCtx(() => [createVNode(_component_el_input_number, {
								modelValue: renewForm.durationMonths,
								"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => renewForm.durationMonths = $event),
								min: 1,
								max: 120,
								style: { "width": "100%" }
							}, null, 8, ["modelValue"])]),
							_: 1
						}), createVNode(_component_el_form_item, {
							label: "结束日期",
							prop: "endDate"
						}, {
							default: withCtx(() => [createVNode(_component_el_date_picker, {
								modelValue: renewForm.endDate,
								"onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => renewForm.endDate = $event),
								type: "date",
								"value-format": "YYYY-MM-DD",
								placeholder: "选择结束日期",
								style: { "width": "100%" }
							}, null, 8, ["modelValue"])]),
							_: 1
						})]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue"])
			]);
		};
	}
}), [["__scopeId", "data-v-21e08fc9"]]);
//#endregion
export { warranty_default as default };

//# sourceMappingURL=warranty-CIWxtsi3.js.map