import { d as useRouter, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withKeys, withModifiers } from "vue";
//#region src/api/system-audit.ts
/** 分页查询登录日志 */
function getLoginLogPage(params) {
	return get("/api/system/audit/login/page", params);
}
/** 分页查询异常日志 */
function getExceptionLogPage(params) {
	return get("/api/system/audit/exception/page", params);
}
//#endregion
//#region src/views/system/audit/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = {
	key: 1,
	class: "text-muted"
};
var _hoisted_3 = { class: "jump-card" };
var _hoisted_4 = { class: "stack-pre" };
//#endregion
//#region src/views/system/audit/index.vue
var audit_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "SystemAudit",
	__name: "index",
	setup(__props) {
		const router = useRouter();
		const activeTab = ref("login");
		const loginLoading = ref(false);
		const loginData = ref([]);
		const loginTotal = ref(0);
		const loginQuery = reactive({
			page: 1,
			size: 10,
			username: "",
			status: ""
		});
		async function loadLogin() {
			loginLoading.value = true;
			try {
				var _res$records, _res$total;
				const params = {
					page: loginQuery.page,
					size: loginQuery.size
				};
				if (loginQuery.username) params.username = loginQuery.username;
				if (loginQuery.status) params.status = loginQuery.status;
				const res = await getLoginLogPage(params);
				loginData.value = (_res$records = res === null || res === void 0 ? void 0 : res.records) !== null && _res$records !== void 0 ? _res$records : [];
				loginTotal.value = (_res$total = res === null || res === void 0 ? void 0 : res.total) !== null && _res$total !== void 0 ? _res$total : 0;
			} catch (_unused) {} finally {
				loginLoading.value = false;
			}
		}
		function handleLoginSearch() {
			loginQuery.page = 1;
			loadLogin();
		}
		function handleLoginReset() {
			loginQuery.username = "";
			loginQuery.status = "";
			loginQuery.page = 1;
			loadLogin();
		}
		function handleLoginPageChange(p) {
			loginQuery.page = p;
			loadLogin();
		}
		function handleLoginSizeChange(s) {
			loginQuery.size = s;
			loginQuery.page = 1;
			loadLogin();
		}
		function loginStatusTag(status) {
			if (status === "SUCCESS") return "success";
			if (status === "FAIL") return "danger";
			return "info";
		}
		function loginStatusLabel(status) {
			if (status === "SUCCESS") return "成功";
			if (status === "FAIL") return "失败";
			return status !== null && status !== void 0 ? status : "-";
		}
		const exceptionLoading = ref(false);
		const exceptionData = ref([]);
		const exceptionTotal = ref(0);
		const exceptionQuery = reactive({
			page: 1,
			size: 10,
			username: "",
			requestUri: ""
		});
		const stackVisible = ref(false);
		const stackLoading = ref(false);
		const currentException = ref(null);
		async function loadException() {
			exceptionLoading.value = true;
			try {
				var _res$records2, _res$total2;
				const params = {
					page: exceptionQuery.page,
					size: exceptionQuery.size
				};
				if (exceptionQuery.username) params.username = exceptionQuery.username;
				if (exceptionQuery.requestUri) params.requestUri = exceptionQuery.requestUri;
				const res = await getExceptionLogPage(params);
				exceptionData.value = (_res$records2 = res === null || res === void 0 ? void 0 : res.records) !== null && _res$records2 !== void 0 ? _res$records2 : [];
				exceptionTotal.value = (_res$total2 = res === null || res === void 0 ? void 0 : res.total) !== null && _res$total2 !== void 0 ? _res$total2 : 0;
			} catch (_unused2) {} finally {
				exceptionLoading.value = false;
			}
		}
		function handleExceptionSearch() {
			exceptionQuery.page = 1;
			loadException();
		}
		function handleExceptionReset() {
			exceptionQuery.username = "";
			exceptionQuery.requestUri = "";
			exceptionQuery.page = 1;
			loadException();
		}
		function handleExceptionPageChange(p) {
			exceptionQuery.page = p;
			loadException();
		}
		function handleExceptionSizeChange(s) {
			exceptionQuery.size = s;
			exceptionQuery.page = 1;
			loadException();
		}
		function handleRowClick(row) {
			if (!row.stackTrace) return;
			currentException.value = row;
			stackVisible.value = true;
			stackLoading.value = false;
		}
		function goScheduleMonitor() {
			router.push("/system/schedule");
		}
		function handleTabChange(name) {
			if (name === "exception" && exceptionData.value.length === 0) loadException();
		}
		function formatDateTime(val) {
			if (!val) return "-";
			return val.replace("T", " ").slice(0, 19);
		}
		onMounted(() => {
			loadLogin();
		});
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
			const _component_el_tab_pane = resolveComponent("el-tab-pane");
			const _component_Promotion = resolveComponent("Promotion");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_tabs = resolveComponent("el-tabs");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_descriptions_item = resolveComponent("el-descriptions-item");
			const _component_el_descriptions = resolveComponent("el-descriptions");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				_cache[17] || (_cache[17] = createElementVNode("div", { class: "page-title" }, "审计日志", -1)),
				createVNode(_component_el_card, { shadow: "never" }, {
					default: withCtx(() => [createVNode(_component_el_tabs, {
						modelValue: activeTab.value,
						"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => activeTab.value = $event),
						onTabChange: handleTabChange
					}, {
						default: withCtx(() => [
							createVNode(_component_el_tab_pane, {
								label: "登录日志",
								name: "login"
							}, {
								default: withCtx(() => [
									createVNode(_component_el_form, {
										inline: true,
										onSubmit: _cache[2] || (_cache[2] = withModifiers(() => {}, ["prevent"]))
									}, {
										default: withCtx(() => [
											createVNode(_component_el_form_item, { label: "用户名" }, {
												default: withCtx(() => [createVNode(_component_el_input, {
													modelValue: loginQuery.username,
													"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => loginQuery.username = $event),
													placeholder: "用户名",
													clearable: "",
													style: { "width": "180px" },
													onKeyup: withKeys(handleLoginSearch, ["enter"])
												}, null, 8, ["modelValue"])]),
												_: 1
											}),
											createVNode(_component_el_form_item, { label: "结果" }, {
												default: withCtx(() => [createVNode(_component_el_select, {
													modelValue: loginQuery.status,
													"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => loginQuery.status = $event),
													placeholder: "全部",
													clearable: "",
													style: { "width": "140px" }
												}, {
													default: withCtx(() => [createVNode(_component_el_option, {
														label: "成功",
														value: "SUCCESS"
													}), createVNode(_component_el_option, {
														label: "失败",
														value: "FAIL"
													})]),
													_: 1
												}, 8, ["modelValue"])]),
												_: 1
											}),
											createVNode(_component_el_form_item, null, {
												default: withCtx(() => [createVNode(_component_el_button, {
													type: "primary",
													icon: "Search",
													onClick: handleLoginSearch
												}, {
													default: withCtx(() => [..._cache[8] || (_cache[8] = [createTextVNode("查询", -1)])]),
													_: 1
												}), createVNode(_component_el_button, {
													icon: "Refresh",
													onClick: handleLoginReset
												}, {
													default: withCtx(() => [..._cache[9] || (_cache[9] = [createTextVNode("重置", -1)])]),
													_: 1
												})]),
												_: 1
											})
										]),
										_: 1
									}),
									withDirectives((openBlock(), createBlock(_component_el_table, {
										data: loginData.value,
										border: "",
										stripe: ""
									}, {
										empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无登录日志" })]),
										default: withCtx(() => [
											createVNode(_component_el_table_column, {
												prop: "username",
												label: "用户名",
												"min-width": "140",
												"show-overflow-tooltip": ""
											}),
											createVNode(_component_el_table_column, {
												label: "登录时间",
												width: "170"
											}, {
												default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDateTime(row.loginTime)), 1)]),
												_: 1
											}),
											createVNode(_component_el_table_column, {
												prop: "loginIp",
												label: "IP",
												"min-width": "140",
												"show-overflow-tooltip": ""
											}),
											createVNode(_component_el_table_column, {
												prop: "loginLocation",
												label: "登录地点",
												"min-width": "140",
												"show-overflow-tooltip": ""
											}),
											createVNode(_component_el_table_column, {
												label: "登录结果",
												width: "100",
												align: "center"
											}, {
												default: withCtx(({ row }) => [createVNode(_component_el_tag, {
													type: loginStatusTag(row.status),
													size: "small"
												}, {
													default: withCtx(() => [createTextVNode(toDisplayString(loginStatusLabel(row.status)), 1)]),
													_: 2
												}, 1032, ["type"])]),
												_: 1
											}),
											createVNode(_component_el_table_column, {
												prop: "browser",
												label: "浏览器",
												"min-width": "160",
												"show-overflow-tooltip": ""
											}),
											createVNode(_component_el_table_column, {
												prop: "os",
												label: "操作系统",
												"min-width": "140",
												"show-overflow-tooltip": ""
											}),
											createVNode(_component_el_table_column, {
												prop: "message",
												label: "提示消息",
												"min-width": "180",
												"show-overflow-tooltip": ""
											})
										]),
										_: 1
									}, 8, ["data"])), [[_directive_loading, loginLoading.value]]),
									createVNode(_component_el_pagination, {
										class: "pagination",
										background: "",
										"current-page": loginQuery.page,
										"page-size": loginQuery.size,
										total: loginTotal.value,
										"page-sizes": [
											10,
											20,
											50
										],
										layout: "total, sizes, prev, pager, next, jumper",
										onCurrentChange: handleLoginPageChange,
										onSizeChange: handleLoginSizeChange
									}, null, 8, [
										"current-page",
										"page-size",
										"total"
									])
								]),
								_: 1
							}),
							createVNode(_component_el_tab_pane, {
								label: "异常日志",
								name: "exception"
							}, {
								default: withCtx(() => [
									createVNode(_component_el_form, {
										inline: true,
										onSubmit: _cache[5] || (_cache[5] = withModifiers(() => {}, ["prevent"]))
									}, {
										default: withCtx(() => [
											createVNode(_component_el_form_item, { label: "用户名" }, {
												default: withCtx(() => [createVNode(_component_el_input, {
													modelValue: exceptionQuery.username,
													"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => exceptionQuery.username = $event),
													placeholder: "用户名",
													clearable: "",
													style: { "width": "180px" },
													onKeyup: withKeys(handleExceptionSearch, ["enter"])
												}, null, 8, ["modelValue"])]),
												_: 1
											}),
											createVNode(_component_el_form_item, { label: "请求路径" }, {
												default: withCtx(() => [createVNode(_component_el_input, {
													modelValue: exceptionQuery.requestUri,
													"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => exceptionQuery.requestUri = $event),
													placeholder: "请求 URI",
													clearable: "",
													style: { "width": "220px" },
													onKeyup: withKeys(handleExceptionSearch, ["enter"])
												}, null, 8, ["modelValue"])]),
												_: 1
											}),
											createVNode(_component_el_form_item, null, {
												default: withCtx(() => [createVNode(_component_el_button, {
													type: "primary",
													icon: "Search",
													onClick: handleExceptionSearch
												}, {
													default: withCtx(() => [..._cache[10] || (_cache[10] = [createTextVNode(" 查询 ", -1)])]),
													_: 1
												}), createVNode(_component_el_button, {
													icon: "Refresh",
													onClick: handleExceptionReset
												}, {
													default: withCtx(() => [..._cache[11] || (_cache[11] = [createTextVNode("重置", -1)])]),
													_: 1
												})]),
												_: 1
											})
										]),
										_: 1
									}),
									withDirectives((openBlock(), createBlock(_component_el_table, {
										data: exceptionData.value,
										border: "",
										stripe: "",
										"highlight-current-row": "",
										onRowClick: handleRowClick
									}, {
										empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无异常日志" })]),
										default: withCtx(() => [
											createVNode(_component_el_table_column, {
												label: "时间",
												width: "170"
											}, {
												default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDateTime(row.occurTime)), 1)]),
												_: 1
											}),
											createVNode(_component_el_table_column, {
												prop: "username",
												label: "用户名",
												"min-width": "120",
												"show-overflow-tooltip": ""
											}),
											createVNode(_component_el_table_column, {
												prop: "exceptionType",
												label: "异常类名",
												"min-width": "220",
												"show-overflow-tooltip": ""
											}),
											createVNode(_component_el_table_column, {
												prop: "exceptionMessage",
												label: "异常信息",
												"min-width": "260",
												"show-overflow-tooltip": ""
											}),
											createVNode(_component_el_table_column, {
												prop: "requestMethod",
												label: "方法",
												width: "80",
												align: "center"
											}),
											createVNode(_component_el_table_column, {
												prop: "requestUri",
												label: "请求路径",
												"min-width": "220",
												"show-overflow-tooltip": ""
											}),
											createVNode(_component_el_table_column, {
												prop: "requestIp",
												label: "IP",
												"min-width": "140",
												"show-overflow-tooltip": ""
											}),
											createVNode(_component_el_table_column, {
												label: "操作",
												width: "100",
												fixed: "right"
											}, {
												default: withCtx(({ row }) => [row.stackTrace ? (openBlock(), createBlock(_component_el_button, {
													key: 0,
													link: "",
													type: "primary",
													onClick: withModifiers(($event) => handleRowClick(row), ["stop"])
												}, {
													default: withCtx(() => [..._cache[12] || (_cache[12] = [createTextVNode(" 堆栈 ", -1)])]),
													_: 1
												}, 8, ["onClick"])) : (openBlock(), createElementBlock("span", _hoisted_2, "-"))]),
												_: 1
											})
										]),
										_: 1
									}, 8, ["data"])), [[_directive_loading, exceptionLoading.value]]),
									createVNode(_component_el_pagination, {
										class: "pagination",
										background: "",
										"current-page": exceptionQuery.page,
										"page-size": exceptionQuery.size,
										total: exceptionTotal.value,
										"page-sizes": [
											10,
											20,
											50
										],
										layout: "total, sizes, prev, pager, next, jumper",
										onCurrentChange: handleExceptionPageChange,
										onSizeChange: handleExceptionSizeChange
									}, null, 8, [
										"current-page",
										"page-size",
										"total"
									])
								]),
								_: 1
							}),
							createVNode(_component_el_tab_pane, {
								label: "调度日志",
								name: "schedule"
							}, {
								default: withCtx(() => [createElementVNode("div", _hoisted_3, [
									createVNode(_component_el_icon, {
										size: 48,
										class: "jump-icon"
									}, {
										default: withCtx(() => [createVNode(_component_Promotion)]),
										_: 1
									}),
									_cache[14] || (_cache[14] = createElementVNode("div", { class: "jump-title" }, "调度日志", -1)),
									_cache[15] || (_cache[15] = createElementVNode("div", { class: "jump-desc" }, " 调度日志详情请在「定时任务监控」页面查看，支持统计卡片、状态/日期筛选与失败列表。 ", -1)),
									createVNode(_component_el_button, {
										type: "primary",
										icon: "Link",
										onClick: goScheduleMonitor
									}, {
										default: withCtx(() => [..._cache[13] || (_cache[13] = [createTextVNode(" 前往定时任务监控 ", -1)])]),
										_: 1
									})
								])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["modelValue"])]),
					_: 1
				}),
				createVNode(_component_el_dialog, {
					modelValue: stackVisible.value,
					"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => stackVisible.value = $event),
					title: "异常堆栈详情",
					width: "720px",
					"destroy-on-close": ""
				}, {
					default: withCtx(() => {
						var _currentException$val;
						return [withDirectives((openBlock(), createElementBlock("div", null, [
							currentException.value ? (openBlock(), createBlock(_component_el_descriptions, {
								key: 0,
								column: 1,
								border: "",
								size: "small"
							}, {
								default: withCtx(() => [
									createVNode(_component_el_descriptions_item, { label: "时间" }, {
										default: withCtx(() => [createTextVNode(toDisplayString(formatDateTime(currentException.value.occurTime)), 1)]),
										_: 1
									}),
									createVNode(_component_el_descriptions_item, { label: "用户名" }, {
										default: withCtx(() => [createTextVNode(toDisplayString(currentException.value.username || "-"), 1)]),
										_: 1
									}),
									createVNode(_component_el_descriptions_item, { label: "异常类型" }, {
										default: withCtx(() => [createTextVNode(toDisplayString(currentException.value.exceptionType || "-"), 1)]),
										_: 1
									}),
									createVNode(_component_el_descriptions_item, { label: "异常消息" }, {
										default: withCtx(() => [createTextVNode(toDisplayString(currentException.value.exceptionMessage || "-"), 1)]),
										_: 1
									}),
									createVNode(_component_el_descriptions_item, { label: "请求路径" }, {
										default: withCtx(() => [createTextVNode(toDisplayString(currentException.value.requestMethod) + " " + toDisplayString(currentException.value.requestUri), 1)]),
										_: 1
									})
								]),
								_: 1
							})) : createCommentVNode("", true),
							_cache[16] || (_cache[16] = createElementVNode("div", { class: "stack-title" }, "完整堆栈", -1)),
							createElementVNode("pre", _hoisted_4, toDisplayString(((_currentException$val = currentException.value) === null || _currentException$val === void 0 ? void 0 : _currentException$val.stackTrace) || "无堆栈信息"), 1)
						])), [[_directive_loading, stackLoading.value]])];
					}),
					_: 1
				}, 8, ["modelValue"])
			]);
		};
	}
}), [["__scopeId", "data-v-332c48a7"]]);
//#endregion
export { audit_default as default };

//# sourceMappingURL=audit-Bccg0yed.js.map