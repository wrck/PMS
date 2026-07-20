import { r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage } from "element-plus";
import { Fragment, createBlock, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, unref, withCtx, withDirectives, withModifiers } from "vue";
//#region src/api/system-schedule.ts
/**
* 定时任务监控 API。
* 对应后端 ScheduleMonitorController（/api/system/schedule）。
*/
/** 定时任务执行状态 */
var SCHEDULE_STATUS = {
	SUCCESS: "SUCCESS",
	FAIL: "FAIL"
};
/** 触发类型 */
var TRIGGER_TYPE = {
	AUTO: "AUTO",
	MANUAL: "MANUAL"
};
/** 获取顶部统计卡片数据 */
function getScheduleStatistic() {
	return get("/api/system/schedule/statistic");
}
/** 分页查询最近调度日志 */
function listScheduleLogs(params) {
	return get("/api/system/audit/schedule/page", {
		page: params.page,
		size: params.size,
		status: params.status,
		taskName: params.taskName
	});
}
/** 查询失败任务列表 */
function listFailedScheduleLogs(params = {}) {
	return get("/api/system/audit/schedule/failed", params);
}
//#endregion
//#region src/views/system/schedule/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "stat-value" };
var _hoisted_3 = { class: "stat-value" };
var _hoisted_4 = { class: "stat-value" };
var _hoisted_5 = {
	key: 1,
	class: "text-muted"
};
//#endregion
//#region src/views/system/schedule/index.vue
var schedule_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "SystemSchedule",
	__name: "index",
	setup(__props) {
		const statLoading = ref(false);
		const statistic = ref({});
		async function loadStatistic() {
			statLoading.value = true;
			try {
				statistic.value = await getScheduleStatistic();
			} catch (_unused) {} finally {
				statLoading.value = false;
			}
		}
		const activeTab = ref("recent");
		const recentLoading = ref(false);
		const recentData = ref([]);
		const recentTotal = ref(0);
		const statusOptions = [
			{
				value: "ALL",
				label: "全部"
			},
			{
				value: "SUCCESS",
				label: "成功"
			},
			{
				value: "FAILED",
				label: "失败"
			},
			{
				value: "MANUAL_TRIGGER",
				label: "手动触发"
			}
		];
		const filter = reactive({
			status: "ALL",
			dateRange: null,
			page: 1,
			size: 10
		});
		function buildParams() {
			const params = {
				page: filter.page,
				size: filter.size
			};
			if (filter.status === "SUCCESS") params.status = SCHEDULE_STATUS.SUCCESS;
			else if (filter.status === "FAILED") params.status = SCHEDULE_STATUS.FAIL;
			else if (filter.status === "MANUAL_TRIGGER") params.triggerType = TRIGGER_TYPE.MANUAL;
			if (filter.dateRange && filter.dateRange.length === 2) {
				params.startDate = filter.dateRange[0];
				params.endDate = filter.dateRange[1];
			}
			return params;
		}
		async function loadRecent() {
			recentLoading.value = true;
			try {
				var _res$records, _res$total;
				const res = await listScheduleLogs(buildParams());
				recentData.value = (_res$records = res === null || res === void 0 ? void 0 : res.records) !== null && _res$records !== void 0 ? _res$records : [];
				recentTotal.value = (_res$total = res === null || res === void 0 ? void 0 : res.total) !== null && _res$total !== void 0 ? _res$total : 0;
			} catch (_unused2) {} finally {
				recentLoading.value = false;
			}
		}
		function handleSearch() {
			filter.page = 1;
			loadRecent();
		}
		function handleReset() {
			filter.status = "ALL";
			filter.dateRange = null;
			filter.page = 1;
			loadRecent();
		}
		function handlePageChange(p) {
			filter.page = p;
			loadRecent();
		}
		function handleSizeChange(s) {
			filter.size = s;
			filter.page = 1;
			loadRecent();
		}
		const failedLoading = ref(false);
		const failedData = ref([]);
		const failedTotal = ref(0);
		const failedQuery = reactive({
			page: 1,
			size: 10
		});
		async function loadFailed() {
			failedLoading.value = true;
			try {
				var _res$records2, _res$total2;
				const res = await listFailedScheduleLogs({
					page: failedQuery.page,
					size: failedQuery.size
				});
				failedData.value = (_res$records2 = res === null || res === void 0 ? void 0 : res.records) !== null && _res$records2 !== void 0 ? _res$records2 : [];
				failedTotal.value = (_res$total2 = res === null || res === void 0 ? void 0 : res.total) !== null && _res$total2 !== void 0 ? _res$total2 : 0;
			} catch (_unused3) {} finally {
				failedLoading.value = false;
			}
		}
		function handleFailedPageChange(p) {
			failedQuery.page = p;
			loadFailed();
		}
		function handleFailedSizeChange(s) {
			failedQuery.size = s;
			failedQuery.page = 1;
			loadFailed();
		}
		function handleTabChange(name) {
			if (name === "failed" && failedData.value.length === 0) loadFailed();
		}
		function statusTagType(status) {
			if (status === SCHEDULE_STATUS.SUCCESS) return "success";
			if (status === SCHEDULE_STATUS.FAIL) return "danger";
			return "info";
		}
		function statusLabel(status) {
			if (status === SCHEDULE_STATUS.SUCCESS) return "成功";
			if (status === SCHEDULE_STATUS.FAIL) return "失败";
			return status !== null && status !== void 0 ? status : "-";
		}
		function triggerLabel(t) {
			if (t === TRIGGER_TYPE.AUTO) return "自动";
			if (t === TRIGGER_TYPE.MANUAL) return "手动";
			return t !== null && t !== void 0 ? t : "-";
		}
		function recentRowClass({ row }) {
			return row.status === SCHEDULE_STATUS.FAIL ? "row-failed" : "";
		}
		function formatDateTime(val) {
			if (!val) return "-";
			return val.replace("T", " ").slice(0, 19);
		}
		function handleManualRetry(_row) {
			ElMessage.info("手动重试功能待后端提供触发接口");
		}
		onMounted(() => {
			loadStatistic();
			loadRecent();
		});
		return (_ctx, _cache) => {
			const _component_el_card = resolveComponent("el-card");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_date_picker = resolveComponent("el-date-picker");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_pagination = resolveComponent("el-pagination");
			const _component_el_tab_pane = resolveComponent("el-tab-pane");
			const _component_el_tabs = resolveComponent("el-tabs");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				_cache[10] || (_cache[10] = createElementVNode("div", { class: "page-title" }, "定时任务监控", -1)),
				withDirectives((openBlock(), createBlock(_component_el_row, { gutter: 16 }, {
					default: withCtx(() => [
						createVNode(_component_el_col, {
							xs: 24,
							sm: 8
						}, {
							default: withCtx(() => [createVNode(_component_el_card, {
								shadow: "hover",
								class: "stat-card stat-total"
							}, {
								default: withCtx(() => {
									var _statistic$value$tota;
									return [_cache[4] || (_cache[4] = createElementVNode("div", { class: "stat-label" }, "总任务数", -1)), createElementVNode("div", _hoisted_2, toDisplayString((_statistic$value$tota = statistic.value.totalTasks) !== null && _statistic$value$tota !== void 0 ? _statistic$value$tota : 0), 1)];
								}),
								_: 1
							})]),
							_: 1
						}),
						createVNode(_component_el_col, {
							xs: 24,
							sm: 8
						}, {
							default: withCtx(() => [createVNode(_component_el_card, {
								shadow: "hover",
								class: "stat-card stat-success"
							}, {
								default: withCtx(() => {
									var _statistic$value$succ;
									return [_cache[5] || (_cache[5] = createElementVNode("div", { class: "stat-label" }, "最近 24h 成功", -1)), createElementVNode("div", _hoisted_3, toDisplayString((_statistic$value$succ = statistic.value.success24h) !== null && _statistic$value$succ !== void 0 ? _statistic$value$succ : 0), 1)];
								}),
								_: 1
							})]),
							_: 1
						}),
						createVNode(_component_el_col, {
							xs: 24,
							sm: 8
						}, {
							default: withCtx(() => [createVNode(_component_el_card, {
								shadow: "hover",
								class: "stat-card stat-fail"
							}, {
								default: withCtx(() => {
									var _statistic$value$fail;
									return [_cache[6] || (_cache[6] = createElementVNode("div", { class: "stat-label" }, "最近 24h 失败", -1)), createElementVNode("div", _hoisted_4, toDisplayString((_statistic$value$fail = statistic.value.failed24h) !== null && _statistic$value$fail !== void 0 ? _statistic$value$fail : 0), 1)];
								}),
								_: 1
							})]),
							_: 1
						})
					]),
					_: 1
				})), [[_directive_loading, statLoading.value]]),
				createVNode(_component_el_card, { shadow: "never" }, {
					default: withCtx(() => [createVNode(_component_el_tabs, {
						modelValue: activeTab.value,
						"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => activeTab.value = $event),
						onTabChange: handleTabChange
					}, {
						default: withCtx(() => [createVNode(_component_el_tab_pane, {
							label: "最近日志",
							name: "recent"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_form, {
									inline: true,
									onSubmit: _cache[2] || (_cache[2] = withModifiers(() => {}, ["prevent"]))
								}, {
									default: withCtx(() => [
										createVNode(_component_el_form_item, { label: "状态" }, {
											default: withCtx(() => [createVNode(_component_el_select, {
												modelValue: filter.status,
												"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => filter.status = $event),
												placeholder: "全部",
												style: { "width": "160px" }
											}, {
												default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(statusOptions, (o) => {
													return createVNode(_component_el_option, {
														key: o.value,
														label: o.label,
														value: o.value
													}, null, 8, ["label", "value"]);
												}), 64))]),
												_: 1
											}, 8, ["modelValue"])]),
											_: 1
										}),
										createVNode(_component_el_form_item, { label: "日期范围" }, {
											default: withCtx(() => [createVNode(_component_el_date_picker, {
												modelValue: filter.dateRange,
												"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => filter.dateRange = $event),
												type: "daterange",
												"value-format": "YYYY-MM-DD",
												"range-separator": "至",
												"start-placeholder": "开始日期",
												"end-placeholder": "结束日期",
												style: { "width": "260px" }
											}, null, 8, ["modelValue"])]),
											_: 1
										}),
										createVNode(_component_el_form_item, null, {
											default: withCtx(() => [createVNode(_component_el_button, {
												type: "primary",
												icon: "Search",
												onClick: handleSearch
											}, {
												default: withCtx(() => [..._cache[7] || (_cache[7] = [createTextVNode("查询", -1)])]),
												_: 1
											}), createVNode(_component_el_button, {
												icon: "Refresh",
												onClick: handleReset
											}, {
												default: withCtx(() => [..._cache[8] || (_cache[8] = [createTextVNode("重置", -1)])]),
												_: 1
											})]),
											_: 1
										})
									]),
									_: 1
								}),
								withDirectives((openBlock(), createBlock(_component_el_table, {
									data: recentData.value,
									border: "",
									stripe: "",
									"row-class-name": recentRowClass
								}, {
									empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无调度日志" })]),
									default: withCtx(() => [
										createVNode(_component_el_table_column, {
											prop: "taskName",
											label: "任务名称",
											"min-width": "160",
											"show-overflow-tooltip": ""
										}),
										createVNode(_component_el_table_column, {
											prop: "taskGroup",
											label: "分组",
											width: "120",
											"show-overflow-tooltip": ""
										}),
										createVNode(_component_el_table_column, {
											label: "状态",
											width: "90",
											align: "center"
										}, {
											default: withCtx(({ row }) => [createVNode(_component_el_tag, {
												type: statusTagType(row.status),
												size: "small"
											}, {
												default: withCtx(() => [createTextVNode(toDisplayString(statusLabel(row.status)), 1)]),
												_: 2
											}, 1032, ["type"])]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "触发类型",
											width: "90",
											align: "center"
										}, {
											default: withCtx(({ row }) => [createTextVNode(toDisplayString(triggerLabel(row.triggerType)), 1)]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "开始时间",
											width: "160"
										}, {
											default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDateTime(row.startTime)), 1)]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "耗时(ms)",
											width: "100",
											align: "right"
										}, {
											default: withCtx(({ row }) => {
												var _row$costMs;
												return [createTextVNode(toDisplayString((_row$costMs = row.costMs) !== null && _row$costMs !== void 0 ? _row$costMs : "-"), 1)];
											}),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											prop: "errorMessage",
											label: "异常信息",
											"min-width": "220",
											"show-overflow-tooltip": ""
										}),
										createVNode(_component_el_table_column, {
											label: "操作",
											width: "100",
											fixed: "right"
										}, {
											default: withCtx(({ row }) => [row.status === unref(SCHEDULE_STATUS).FAIL ? (openBlock(), createBlock(_component_el_button, {
												key: 0,
												link: "",
												type: "primary",
												onClick: ($event) => handleManualRetry(row)
											}, {
												default: withCtx(() => [..._cache[9] || (_cache[9] = [createTextVNode(" 重试 ", -1)])]),
												_: 1
											}, 8, ["onClick"])) : (openBlock(), createElementBlock("span", _hoisted_5, "-"))]),
											_: 1
										})
									]),
									_: 1
								}, 8, ["data"])), [[_directive_loading, recentLoading.value]]),
								createVNode(_component_el_pagination, {
									class: "pagination",
									background: "",
									"current-page": filter.page,
									"page-size": filter.size,
									total: recentTotal.value,
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
						}), createVNode(_component_el_tab_pane, {
							label: "失败列表",
							name: "failed"
						}, {
							default: withCtx(() => [withDirectives((openBlock(), createBlock(_component_el_table, {
								data: failedData.value,
								border: "",
								stripe: ""
							}, {
								empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无失败任务" })]),
								default: withCtx(() => [
									createVNode(_component_el_table_column, {
										prop: "taskName",
										label: "任务名称",
										"min-width": "160",
										"show-overflow-tooltip": ""
									}),
									createVNode(_component_el_table_column, {
										label: "状态",
										width: "90",
										align: "center"
									}, {
										default: withCtx(({ row }) => [createVNode(_component_el_tag, {
											type: statusTagType(row.status),
											size: "small"
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(statusLabel(row.status)), 1)]),
											_: 2
										}, 1032, ["type"])]),
										_: 1
									}),
									createVNode(_component_el_table_column, {
										label: "触发类型",
										width: "90",
										align: "center"
									}, {
										default: withCtx(({ row }) => [createTextVNode(toDisplayString(triggerLabel(row.triggerType)), 1)]),
										_: 1
									}),
									createVNode(_component_el_table_column, {
										label: "开始时间",
										width: "160"
									}, {
										default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDateTime(row.startTime)), 1)]),
										_: 1
									}),
									createVNode(_component_el_table_column, {
										label: "耗时(ms)",
										width: "100",
										align: "right"
									}, {
										default: withCtx(({ row }) => {
											var _row$costMs2;
											return [createTextVNode(toDisplayString((_row$costMs2 = row.costMs) !== null && _row$costMs2 !== void 0 ? _row$costMs2 : "-"), 1)];
										}),
										_: 1
									}),
									createVNode(_component_el_table_column, {
										prop: "errorMessage",
										label: "异常信息",
										"min-width": "240",
										"show-overflow-tooltip": ""
									})
								]),
								_: 1
							}, 8, ["data"])), [[_directive_loading, failedLoading.value]]), createVNode(_component_el_pagination, {
								class: "pagination",
								background: "",
								"current-page": failedQuery.page,
								"page-size": failedQuery.size,
								total: failedTotal.value,
								"page-sizes": [
									10,
									20,
									50
								],
								layout: "total, sizes, prev, pager, next, jumper",
								onCurrentChange: handleFailedPageChange,
								onSizeChange: handleFailedSizeChange
							}, null, 8, [
								"current-page",
								"page-size",
								"total"
							])]),
							_: 1
						})]),
						_: 1
					}, 8, ["modelValue"])]),
					_: 1
				})
			]);
		};
	}
}), [["__scopeId", "data-v-e08dcbd0"]]);
//#endregion
export { schedule_default as default };

//# sourceMappingURL=schedule-CEdzMBj2.js.map