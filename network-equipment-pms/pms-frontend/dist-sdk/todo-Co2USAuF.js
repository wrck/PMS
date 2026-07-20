import { i as post, n as del, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { m as getUserPage } from "./system-CuVYDpvc.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives } from "vue";
//#region src/api/workflow.ts
function getProcessDefinitions(params) {
	return get("/api/workflow/definition/list", params);
}
/** Deploy a BPMN process file (multipart/form-data) */
function deployProcess(file) {
	const formData = new FormData();
	formData.append("file", file);
	return post("/api/workflow/deploy", formData);
}
function deleteDeployment(deploymentId) {
	return del(`/api/workflow/deployment/${deploymentId}`);
}
/** Build the direct URL for a process diagram image (used in <img :src>) */
function getProcessDiagramUrl(processInstanceId) {
	return `/api/workflow/diagram/${processInstanceId}`;
}
function getTodoTasks(params) {
	return get("/api/workflow/task/todo", params);
}
function getDoneTasks(params) {
	return get("/api/workflow/task/done", params);
}
function completeTask(data) {
	return post("/api/workflow/task/complete", data);
}
function transferTask(data) {
	return post("/api/workflow/task/transfer", data);
}
function getProcessHistory(processInstanceId) {
	return get(`/api/workflow/history/${processInstanceId}`);
}
//#endregion
//#region src/views/workflow/todo/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "toolbar" };
var _hoisted_3 = { class: "toolbar" };
var _hoisted_4 = { class: "toolbar" };
var _hoisted_5 = {
	key: 0,
	class: "task-info"
};
var _hoisted_6 = { class: "history-item" };
var _hoisted_7 = { class: "history-node" };
var _hoisted_8 = {
	key: 0,
	class: "history-user"
};
var _hoisted_9 = {
	key: 1,
	class: "history-duration"
};
var _hoisted_10 = {
	key: 0,
	class: "history-comment"
};
var _hoisted_11 = { class: "diagram-wrapper" };
var _hoisted_12 = ["src"];
//#endregion
//#region src/views/workflow/todo/index.vue
var todo_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const activeTab = ref("todo");
		const loading = ref(false);
		const total = ref(0);
		const query = reactive({
			page: 1,
			size: 10
		});
		const todoData = ref([]);
		const doneData = ref([]);
		const defData = ref([]);
		const handleVisible = ref(false);
		const handleLoading = ref(false);
		const handleSubmitting = ref(false);
		const currentTask = ref(null);
		const handleComment = ref("");
		const historyData = ref([]);
		const transferVisible = ref(false);
		const transferSubmitting = ref(false);
		const transferTaskId = ref("");
		const targetUserId = ref(void 0);
		const userOptions = ref([]);
		const diagramVisible = ref(false);
		const diagramUrl = ref("");
		async function loadData() {
			loading.value = true;
			try {
				if (activeTab.value === "todo") {
					const res = await getTodoTasks(query);
					todoData.value = res.records;
					total.value = res.total;
				} else if (activeTab.value === "done") {
					const res = await getDoneTasks(query);
					doneData.value = res.records;
					total.value = res.total;
				} else {
					const res = await getProcessDefinitions(query);
					defData.value = res.records;
					total.value = res.total;
				}
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		function handleTabChange() {
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
		async function loadUsers() {
			try {
				const res = await getUserPage({
					page: 1,
					size: 100
				});
				userOptions.value = res.records;
			} catch (_unused2) {}
		}
		async function openHandleDialog(row) {
			currentTask.value = row;
			handleComment.value = "";
			historyData.value = [];
			handleVisible.value = true;
			if (row.processInstanceId) {
				handleLoading.value = true;
				try {
					historyData.value = await getProcessHistory(row.processInstanceId);
				} catch (_unused3) {
					historyData.value = [];
				} finally {
					handleLoading.value = false;
				}
			}
		}
		async function doCompleteTask(approved) {
			var _currentTask$value;
			if (!((_currentTask$value = currentTask.value) === null || _currentTask$value === void 0 ? void 0 : _currentTask$value.id)) return;
			handleSubmitting.value = true;
			try {
				await completeTask({
					taskId: currentTask.value.id,
					variables: { approved },
					comment: handleComment.value
				});
				ElMessage.success(approved ? "审批通过" : "已驳回");
				handleVisible.value = false;
				loadData();
			} catch (_unused4) {} finally {
				handleSubmitting.value = false;
			}
		}
		function openTransferDialog(row) {
			if (!row.id) return;
			transferTaskId.value = row.id;
			targetUserId.value = void 0;
			transferVisible.value = true;
		}
		async function handleTransferSubmit() {
			const uid = targetUserId.value;
			if (!uid) {
				ElMessage.warning("请选择转办用户");
				return;
			}
			transferSubmitting.value = true;
			try {
				await transferTask({
					taskId: transferTaskId.value,
					targetUserId: uid
				});
				ElMessage.success("转办成功");
				transferVisible.value = false;
				loadData();
			} catch (_unused5) {} finally {
				transferSubmitting.value = false;
			}
		}
		function viewDiagram(row) {
			if (!row.processInstanceId) {
				ElMessage.warning("该任务无流程实例信息");
				return;
			}
			diagramUrl.value = getProcessDiagramUrl(row.processInstanceId);
			diagramVisible.value = true;
		}
		function handleDeleteDeployment(row) {
			if (!row.deploymentId) {
				ElMessage.warning("该流程无部署ID");
				return;
			}
			ElMessageBox.confirm(`确定删除流程「${row.name}」的部署吗？`, "提示", { type: "warning" }).then(async () => {
				await deleteDeployment(row.deploymentId);
				ElMessage.success("删除成功");
				loadData();
			}).catch(() => {});
		}
		async function handleDeploy(options) {
			const file = options.file;
			if (!file) return;
			if (!/\.bpmn(\.xml)?|\.xml$/i.test(file.name)) {
				ElMessage.warning("请上传 BPMN 或 XML 流程文件");
				options.onError(/* @__PURE__ */ new Error("invalid file type"));
				return;
			}
			try {
				await deployProcess(file);
				ElMessage.success("流程部署成功");
				if (activeTab.value !== "definition") activeTab.value = "definition";
				loadData();
				options.onSuccess({});
			} catch (_unused6) {
				options.onError(/* @__PURE__ */ new Error("deploy failed"));
			}
		}
		onMounted(() => {
			loadUsers();
			loadData();
		});
		return (_ctx, _cache) => {
			const _component_el_tab_pane = resolveComponent("el-tab-pane");
			const _component_el_tabs = resolveComponent("el-tabs");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_upload = resolveComponent("el-upload");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_pagination = resolveComponent("el-pagination");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_descriptions_item = resolveComponent("el-descriptions-item");
			const _component_el_descriptions = resolveComponent("el-descriptions");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_timeline_item = resolveComponent("el-timeline-item");
			const _component_el_timeline = resolveComponent("el-timeline");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_form = resolveComponent("el-form");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, { shadow: "never" }, {
					header: withCtx(() => [..._cache[10] || (_cache[10] = [createElementVNode("span", { class: "page-title" }, "工作流中心", -1)])]),
					default: withCtx(() => [
						createVNode(_component_el_tabs, {
							modelValue: activeTab.value,
							"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => activeTab.value = $event),
							onTabChange: handleTabChange
						}, {
							default: withCtx(() => [
								createVNode(_component_el_tab_pane, {
									label: "待办任务",
									name: "todo"
								}),
								createVNode(_component_el_tab_pane, {
									label: "已办任务",
									name: "done"
								}),
								createVNode(_component_el_tab_pane, {
									label: "流程定义",
									name: "definition"
								})
							]),
							_: 1
						}, 8, ["modelValue"]),
						activeTab.value === "todo" ? (openBlock(), createElementBlock(Fragment, { key: 0 }, [createElementVNode("div", _hoisted_2, [createVNode(_component_el_button, {
							icon: "Refresh",
							onClick: loadData
						}, {
							default: withCtx(() => [..._cache[11] || (_cache[11] = [createTextVNode("刷新", -1)])]),
							_: 1
						})]), withDirectives((openBlock(), createBlock(_component_el_table, {
							data: todoData.value,
							border: "",
							stripe: ""
						}, {
							empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无待办任务" })]),
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									prop: "name",
									label: "任务名称",
									"min-width": "160",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "processDefinitionName",
									label: "流程名称",
									"min-width": "160",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "businessKey",
									label: "业务编号",
									"min-width": "150",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "startUserName",
									label: "申请人",
									"min-width": "110"
								}),
								createVNode(_component_el_table_column, {
									prop: "createTime",
									label: "创建时间",
									"min-width": "160"
								}),
								createVNode(_component_el_table_column, {
									label: "操作",
									width: "170",
									fixed: "right"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_button, {
										link: "",
										type: "primary",
										onClick: ($event) => openHandleDialog(row)
									}, {
										default: withCtx(() => [..._cache[12] || (_cache[12] = [createTextVNode("办理", -1)])]),
										_: 1
									}, 8, ["onClick"]), createVNode(_component_el_button, {
										link: "",
										type: "warning",
										onClick: ($event) => openTransferDialog(row)
									}, {
										default: withCtx(() => [..._cache[13] || (_cache[13] = [createTextVNode("转办", -1)])]),
										_: 1
									}, 8, ["onClick"])]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["data"])), [[_directive_loading, loading.value]])], 64)) : activeTab.value === "done" ? (openBlock(), createElementBlock(Fragment, { key: 1 }, [createElementVNode("div", _hoisted_3, [createVNode(_component_el_button, {
							icon: "Refresh",
							onClick: loadData
						}, {
							default: withCtx(() => [..._cache[14] || (_cache[14] = [createTextVNode("刷新", -1)])]),
							_: 1
						})]), withDirectives((openBlock(), createBlock(_component_el_table, {
							data: doneData.value,
							border: "",
							stripe: ""
						}, {
							empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无已办任务" })]),
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									prop: "name",
									label: "任务名称",
									"min-width": "160",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "processDefinitionName",
									label: "流程名称",
									"min-width": "160",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "businessKey",
									label: "业务编号",
									"min-width": "150",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "endTime",
									label: "完成时间",
									"min-width": "160"
								}),
								createVNode(_component_el_table_column, {
									prop: "duration",
									label: "耗时",
									"min-width": "130"
								}),
								createVNode(_component_el_table_column, {
									label: "操作",
									width: "130",
									fixed: "right"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_button, {
										link: "",
										type: "primary",
										onClick: ($event) => viewDiagram(row)
									}, {
										default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode("查看流程图", -1)])]),
										_: 1
									}, 8, ["onClick"])]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["data"])), [[_directive_loading, loading.value]])], 64)) : (openBlock(), createElementBlock(Fragment, { key: 2 }, [createElementVNode("div", _hoisted_4, [createVNode(_component_el_upload, {
							"http-request": handleDeploy,
							"show-file-list": false,
							accept: ".bpmn,.bpmn.xml,.xml"
						}, {
							default: withCtx(() => [createVNode(_component_el_button, {
								type: "primary",
								icon: "Upload"
							}, {
								default: withCtx(() => [..._cache[16] || (_cache[16] = [createTextVNode("部署流程", -1)])]),
								_: 1
							})]),
							_: 1
						}), createVNode(_component_el_button, {
							icon: "Refresh",
							onClick: loadData
						}, {
							default: withCtx(() => [..._cache[17] || (_cache[17] = [createTextVNode("刷新", -1)])]),
							_: 1
						})]), withDirectives((openBlock(), createBlock(_component_el_table, {
							data: defData.value,
							border: "",
							stripe: ""
						}, {
							empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无流程定义" })]),
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									prop: "name",
									label: "流程名称",
									"min-width": "180",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "key",
									label: "流程Key",
									"min-width": "150"
								}),
								createVNode(_component_el_table_column, {
									prop: "version",
									label: "版本",
									width: "90",
									align: "center"
								}),
								createVNode(_component_el_table_column, {
									prop: "category",
									label: "分类",
									"min-width": "120",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "deployTime",
									label: "部署时间",
									"min-width": "160"
								}),
								createVNode(_component_el_table_column, {
									label: "状态",
									width: "100"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, {
										type: row.suspended ? "danger" : "success",
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(row.suspended ? "已挂起" : "激活"), 1)]),
										_: 2
									}, 1032, ["type"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "操作",
									width: "120",
									fixed: "right"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_button, {
										link: "",
										type: "danger",
										onClick: ($event) => handleDeleteDeployment(row)
									}, {
										default: withCtx(() => [..._cache[18] || (_cache[18] = [createTextVNode("删除部署", -1)])]),
										_: 1
									}, 8, ["onClick"])]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["data"])), [[_directive_loading, loading.value]])], 64)),
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
					modelValue: handleVisible.value,
					"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => handleVisible.value = $event),
					title: "办理任务",
					width: "640px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [
						createVNode(_component_el_button, { onClick: _cache[2] || (_cache[2] = ($event) => handleVisible.value = false) }, {
							default: withCtx(() => [..._cache[19] || (_cache[19] = [createTextVNode("取消", -1)])]),
							_: 1
						}),
						createVNode(_component_el_button, {
							type: "danger",
							loading: handleSubmitting.value,
							onClick: _cache[3] || (_cache[3] = ($event) => doCompleteTask(false))
						}, {
							default: withCtx(() => [..._cache[20] || (_cache[20] = [createTextVNode("驳回", -1)])]),
							_: 1
						}, 8, ["loading"]),
						createVNode(_component_el_button, {
							type: "success",
							loading: handleSubmitting.value,
							onClick: _cache[4] || (_cache[4] = ($event) => doCompleteTask(true))
						}, {
							default: withCtx(() => [..._cache[21] || (_cache[21] = [createTextVNode("通过", -1)])]),
							_: 1
						}, 8, ["loading"])
					]),
					default: withCtx(() => [
						currentTask.value ? (openBlock(), createElementBlock("div", _hoisted_5, [createVNode(_component_el_descriptions, {
							column: 2,
							border: "",
							size: "small"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_descriptions_item, { label: "任务名称" }, {
									default: withCtx(() => [createTextVNode(toDisplayString(currentTask.value.name), 1)]),
									_: 1
								}),
								createVNode(_component_el_descriptions_item, { label: "流程名称" }, {
									default: withCtx(() => [createTextVNode(toDisplayString(currentTask.value.processDefinitionName), 1)]),
									_: 1
								}),
								createVNode(_component_el_descriptions_item, { label: "业务编号" }, {
									default: withCtx(() => [createTextVNode(toDisplayString(currentTask.value.businessKey || "-"), 1)]),
									_: 1
								}),
								createVNode(_component_el_descriptions_item, { label: "申请人" }, {
									default: withCtx(() => [createTextVNode(toDisplayString(currentTask.value.startUserName || "-"), 1)]),
									_: 1
								}),
								createVNode(_component_el_descriptions_item, {
									label: "创建时间",
									span: 2
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(currentTask.value.createTime), 1)]),
									_: 1
								})
							]),
							_: 1
						})])) : createCommentVNode("", true),
						_cache[22] || (_cache[22] = createElementVNode("h4", { class: "sub-title" }, "审批意见", -1)),
						createVNode(_component_el_input, {
							modelValue: handleComment.value,
							"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => handleComment.value = $event),
							type: "textarea",
							rows: 3,
							placeholder: "请输入审批意见"
						}, null, 8, ["modelValue"]),
						_cache[23] || (_cache[23] = createElementVNode("h4", { class: "sub-title" }, "流程历史", -1)),
						withDirectives((openBlock(), createBlock(_component_el_timeline, null, {
							default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(historyData.value, (item) => {
								return openBlock(), createBlock(_component_el_timeline_item, {
									key: item.id,
									timestamp: item.endTime || item.startTime,
									placement: "top",
									type: item.endTime ? "success" : "primary"
								}, {
									default: withCtx(() => [createElementVNode("div", _hoisted_6, [
										createElementVNode("span", _hoisted_7, toDisplayString(item.activityName), 1),
										item.assigneeName ? (openBlock(), createElementBlock("span", _hoisted_8, "办理人：" + toDisplayString(item.assigneeName), 1)) : createCommentVNode("", true),
										item.duration ? (openBlock(), createElementBlock("span", _hoisted_9, "耗时：" + toDisplayString(item.duration), 1)) : createCommentVNode("", true)
									]), item.comment ? (openBlock(), createElementBlock("div", _hoisted_10, toDisplayString(item.comment), 1)) : createCommentVNode("", true)]),
									_: 2
								}, 1032, ["timestamp", "type"]);
							}), 128)), !handleLoading.value && historyData.value.length === 0 ? (openBlock(), createBlock(_component_el_empty, {
								key: 0,
								description: "暂无历史记录"
							})) : createCommentVNode("", true)]),
							_: 1
						})), [[_directive_loading, handleLoading.value]])
					]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: transferVisible.value,
					"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => transferVisible.value = $event),
					title: "转办任务",
					width: "460px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[7] || (_cache[7] = ($event) => transferVisible.value = false) }, {
						default: withCtx(() => [..._cache[24] || (_cache[24] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: transferSubmitting.value,
						onClick: handleTransferSubmit
					}, {
						default: withCtx(() => [..._cache[25] || (_cache[25] = [createTextVNode("确定", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, { "label-width": "90px" }, {
						default: withCtx(() => [createVNode(_component_el_form_item, { label: "转办给" }, {
							default: withCtx(() => [createVNode(_component_el_select, {
								modelValue: targetUserId.value,
								"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => targetUserId.value = $event),
								placeholder: "请选择用户",
								filterable: "",
								style: { "width": "100%" }
							}, {
								default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(userOptions.value, (u) => {
									return openBlock(), createBlock(_component_el_option, {
										key: u.id,
										label: u.nickname,
										value: u.id
									}, null, 8, ["label", "value"]);
								}), 128))]),
								_: 1
							}, 8, ["modelValue"])]),
							_: 1
						})]),
						_: 1
					})]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: diagramVisible.value,
					"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => diagramVisible.value = $event),
					title: "流程图",
					width: "900px",
					"destroy-on-close": ""
				}, {
					default: withCtx(() => [createElementVNode("div", _hoisted_11, [createElementVNode("img", {
						src: diagramUrl.value,
						alt: "流程图",
						class: "diagram-img"
					}, null, 8, _hoisted_12)])]),
					_: 1
				}, 8, ["modelValue"])
			]);
		};
	}
}), [["__scopeId", "data-v-57614843"]]);
//#endregion
export { todo_default as default };

//# sourceMappingURL=todo-Co2USAuF.js.map