import { c as useUserStore, i as post, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { i as parseLevels, r as getApprovalChainList } from "./lowcode-approval-chain-DzxfcTVv.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, ref, resolveComponent, resolveDirective, toDisplayString, watch, withCtx, withDirectives } from "vue";
//#region src/api/lowcode-publish.ts
function approvePublish(id, approverId, approver) {
	return post(`/api/lowcode/publish/${id}/approve`, null, { params: {
		approverId,
		approver
	} });
}
function rejectPublish(id, reason, approverId, approver) {
	return post(`/api/lowcode/publish/${id}/reject`, null, { params: {
		reason,
		approverId,
		approver
	} });
}
function rollbackPublish(id, userId, userName) {
	return post(`/api/lowcode/publish/${id}/rollback`, null, { params: {
		userId,
		userName
	} });
}
function getPendingList() {
	return get("/api/lowcode/publish/pending");
}
//#endregion
//#region src/api/lowcode-gray-release.ts
function createGrayRelease(params) {
	return post("/api/lowcode/gray-release", params);
}
function updateGrayPercentage(id, newPercentage) {
	return post(`/api/lowcode/gray-release/${id}/percentage`, null, { params: { newPercentage } });
}
function releaseFull(id) {
	return post(`/api/lowcode/gray-release/${id}/full`);
}
function rollbackGray(id) {
	return post(`/api/lowcode/gray-release/${id}/rollback`);
}
function listGrayReleases(configType, configId) {
	return get("/api/lowcode/gray-release", {
		configType,
		configId
	});
}
//#endregion
//#region src/views/lowcode/publish-center/GrayReleaseManager.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$1 = { style: { "margin-bottom": "12px" } };
var _hoisted_2$1 = { style: {
	"margin-left": "12px",
	"color": "var(--el-text-color-secondary)",
	"font-size": "12px"
} };
//#endregion
//#region src/views/lowcode/publish-center/GrayReleaseManager.vue
var GrayReleaseManager_default = /* @__PURE__ */ defineComponent({
	__name: "GrayReleaseManager",
	props: {
		visible: { type: Boolean },
		configType: {},
		configId: {},
		publishRecordId: { default: null }
	},
	emits: ["update:visible", "changed"],
	setup(__props, { emit: __emit }) {
		/**
		* 灰度发布管理器（批次5-T4，借鉴华为 AppCube / OutSystems LifeTime）。
		*
		* <p>显示指定配置的灰度发布记录列表，支持：
		* <ul>
		*   <li>创建灰度（基于已 PUBLISHED 的发布记录）</li>
		*   <li>调整灰度比例（0-100 滑块）</li>
		*   <li>全量发布（置 100%）</li>
		*   <li>回滚灰度</li>
		* </ul></p>
		*/
		const props = __props;
		const emit = __emit;
		const loading = ref(false);
		const grayList = ref([]);
		/** 创建灰度对话框 */
		const createDialogVisible = ref(false);
		const createForm = ref({
			publishRecordId: 0,
			grayPercentage: 10,
			tenantWhitelist: "",
			createBy: ""
		});
		/** 调整比例对话框 */
		const percentageDialogVisible = ref(false);
		const percentageForm = ref({
			id: 0,
			grayPercentage: 10
		});
		const dialogVisible = computed({
			get: () => props.visible,
			set: (v) => emit("update:visible", v)
		});
		watch(() => props.visible, (v) => {
			if (v && props.configType && props.configId) loadList();
		});
		async function loadList() {
			loading.value = true;
			try {
				grayList.value = await listGrayReleases(props.configType, props.configId);
			} catch (e) {
				ElMessage.error("加载灰度记录失败");
				grayList.value = [];
			} finally {
				loading.value = false;
			}
		}
		function openCreateDialog() {
			createForm.value = {
				publishRecordId: props.publishRecordId || 0,
				grayPercentage: 10,
				tenantWhitelist: "",
				createBy: ""
			};
			createDialogVisible.value = true;
		}
		async function submitCreate() {
			if (!createForm.value.publishRecordId) {
				ElMessage.warning("请输入发布记录 ID");
				return;
			}
			try {
				await createGrayRelease({
					publishRecordId: createForm.value.publishRecordId,
					grayPercentage: createForm.value.grayPercentage,
					tenantWhitelist: createForm.value.tenantWhitelist || void 0,
					createBy: createForm.value.createBy || void 0
				});
				ElMessage.success("灰度发布已创建");
				createDialogVisible.value = false;
				await loadList();
				emit("changed");
			} catch (e) {
				ElMessage.error("创建失败");
			}
		}
		function openPercentageDialog(row) {
			percentageForm.value = {
				id: row.id,
				grayPercentage: row.grayPercentage
			};
			percentageDialogVisible.value = true;
		}
		async function submitPercentage() {
			try {
				await updateGrayPercentage(percentageForm.value.id, percentageForm.value.grayPercentage);
				ElMessage.success("比例已调整");
				percentageDialogVisible.value = false;
				await loadList();
				emit("changed");
			} catch (e) {
				ElMessage.error("调整失败");
			}
		}
		async function doReleaseFull(row) {
			try {
				await ElMessageBox.confirm(`确认将配置 ${row.configCode} v${row.version} 全量发布（100%）？全量后所有用户可见新版本。`, "全量发布", {
					confirmButtonText: "全量发布",
					cancelButtonText: "取消",
					type: "warning"
				});
				await releaseFull(row.id);
				ElMessage.success("已全量发布");
				await loadList();
				emit("changed");
			} catch (e) {
				if (e !== "cancel" && e !== "close") ElMessage.error("全量发布失败");
			}
		}
		async function doRollback(row) {
			try {
				await ElMessageBox.confirm(`确认回滚灰度 ${row.configCode} v${row.version}？回滚后用户将恢复使用旧版本。`, "灰度回滚", {
					confirmButtonText: "回滚",
					cancelButtonText: "取消",
					type: "error"
				});
				await rollbackGray(row.id);
				ElMessage.success("已回滚");
				await loadList();
				emit("changed");
			} catch (e) {
				if (e !== "cancel" && e !== "close") ElMessage.error("回滚失败");
			}
		}
		function statusTag(status) {
			if (status === "FULL") return "success";
			if (status === "ROLLED_BACK") return "info";
			return "warning";
		}
		function statusLabel(status) {
			if (status === "GRAYING") return "灰度中";
			if (status === "FULL") return "已全量";
			if (status === "ROLLED_BACK") return "已回滚";
			return status;
		}
		function fmtTime(t) {
			return t ? t.replace("T", " ").slice(0, 16) : "-";
		}
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_progress = resolveComponent("el-progress");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_slider = resolveComponent("el-slider");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return withDirectives((openBlock(), createBlock(_component_el_dialog, {
				modelValue: dialogVisible.value,
				"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => dialogVisible.value = $event),
				title: "灰度发布管理",
				width: "900px",
				"close-on-click-modal": false
			}, {
				default: withCtx(() => [
					createElementVNode("div", _hoisted_1$1, [
						createVNode(_component_el_button, {
							type: "primary",
							size: "small",
							onClick: openCreateDialog
						}, {
							default: withCtx(() => [..._cache[10] || (_cache[10] = [createTextVNode("创建灰度", -1)])]),
							_: 1
						}),
						createVNode(_component_el_button, {
							size: "small",
							onClick: loadList
						}, {
							default: withCtx(() => [..._cache[11] || (_cache[11] = [createTextVNode("刷新", -1)])]),
							_: 1
						}),
						createElementVNode("span", _hoisted_2$1, " 配置: " + toDisplayString(__props.configType) + " / " + toDisplayString(__props.configId), 1)
					]),
					createVNode(_component_el_table, {
						data: grayList.value,
						size: "small",
						border: "",
						"max-height": "400"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_table_column, {
								label: "ID",
								prop: "id",
								width: "60"
							}),
							createVNode(_component_el_table_column, {
								label: "版本",
								prop: "version",
								width: "70"
							}),
							createVNode(_component_el_table_column, {
								label: "比例",
								width: "120"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_progress, {
									percentage: row.grayPercentage,
									"stroke-width": 10,
									status: row.status === "FULL" ? "success" : ""
								}, null, 8, ["percentage", "status"])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "租户白名单",
								prop: "tenantWhitelist",
								"show-overflow-tooltip": ""
							}),
							createVNode(_component_el_table_column, {
								label: "状态",
								width: "100"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, {
									type: statusTag(row.status),
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(statusLabel(row.status)), 1)]),
									_: 2
								}, 1032, ["type"])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "开始时间",
								width: "140"
							}, {
								default: withCtx(({ row }) => [createTextVNode(toDisplayString(fmtTime(row.grayStartedAt)), 1)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "操作",
								width: "240"
							}, {
								default: withCtx(({ row }) => [
									row.status === "GRAYING" ? (openBlock(), createBlock(_component_el_button, {
										key: 0,
										link: "",
										type: "primary",
										size: "small",
										onClick: ($event) => openPercentageDialog(row)
									}, {
										default: withCtx(() => [..._cache[12] || (_cache[12] = [createTextVNode("调整比例", -1)])]),
										_: 1
									}, 8, ["onClick"])) : createCommentVNode("", true),
									row.status === "GRAYING" ? (openBlock(), createBlock(_component_el_button, {
										key: 1,
										link: "",
										type: "success",
										size: "small",
										onClick: ($event) => doReleaseFull(row)
									}, {
										default: withCtx(() => [..._cache[13] || (_cache[13] = [createTextVNode("全量发布", -1)])]),
										_: 1
									}, 8, ["onClick"])) : createCommentVNode("", true),
									row.status === "GRAYING" ? (openBlock(), createBlock(_component_el_button, {
										key: 2,
										link: "",
										type: "danger",
										size: "small",
										onClick: ($event) => doRollback(row)
									}, {
										default: withCtx(() => [..._cache[14] || (_cache[14] = [createTextVNode("回滚", -1)])]),
										_: 1
									}, 8, ["onClick"])) : createCommentVNode("", true)
								]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["data"]),
					createVNode(_component_el_dialog, {
						modelValue: createDialogVisible.value,
						"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => createDialogVisible.value = $event),
						title: "创建灰度发布",
						width: "500px",
						"append-to-body": ""
					}, {
						footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[4] || (_cache[4] = ($event) => createDialogVisible.value = false) }, {
							default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode("取消", -1)])]),
							_: 1
						}), createVNode(_component_el_button, {
							type: "primary",
							onClick: submitCreate
						}, {
							default: withCtx(() => [..._cache[16] || (_cache[16] = [createTextVNode("创建", -1)])]),
							_: 1
						})]),
						default: withCtx(() => [createVNode(_component_el_form, { "label-width": "140px" }, {
							default: withCtx(() => [
								createVNode(_component_el_form_item, { label: "发布记录 ID" }, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: createForm.value.publishRecordId,
										"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => createForm.value.publishRecordId = $event),
										min: 1,
										style: { "width": "100%" }
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "灰度比例 (%)" }, {
									default: withCtx(() => [createVNode(_component_el_slider, {
										modelValue: createForm.value.grayPercentage,
										"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => createForm.value.grayPercentage = $event),
										min: 0,
										max: 100,
										step: 5,
										"show-input": ""
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "租户白名单" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: createForm.value.tenantWhitelist,
										"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => createForm.value.tenantWhitelist = $event),
										type: "textarea",
										rows: 2,
										placeholder: "JSON 数组，如 [\"tenant1\",\"tenant2\"]"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "创建人" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: createForm.value.createBy,
										"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => createForm.value.createBy = $event),
										placeholder: "操作人用户名"
									}, null, 8, ["modelValue"])]),
									_: 1
								})
							]),
							_: 1
						})]),
						_: 1
					}, 8, ["modelValue"]),
					createVNode(_component_el_dialog, {
						modelValue: percentageDialogVisible.value,
						"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => percentageDialogVisible.value = $event),
						title: "调整灰度比例",
						width: "400px",
						"append-to-body": ""
					}, {
						footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[7] || (_cache[7] = ($event) => percentageDialogVisible.value = false) }, {
							default: withCtx(() => [..._cache[17] || (_cache[17] = [createTextVNode("取消", -1)])]),
							_: 1
						}), createVNode(_component_el_button, {
							type: "primary",
							onClick: submitPercentage
						}, {
							default: withCtx(() => [..._cache[18] || (_cache[18] = [createTextVNode("调整", -1)])]),
							_: 1
						})]),
						default: withCtx(() => [createVNode(_component_el_form, { "label-width": "100px" }, {
							default: withCtx(() => [createVNode(_component_el_form_item, { label: "新比例 (%)" }, {
								default: withCtx(() => [createVNode(_component_el_slider, {
									modelValue: percentageForm.value.grayPercentage,
									"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => percentageForm.value.grayPercentage = $event),
									min: 0,
									max: 100,
									step: 5,
									"show-input": ""
								}, null, 8, ["modelValue"])]),
								_: 1
							})]),
							_: 1
						})]),
						_: 1
					}, 8, ["modelValue"])
				]),
				_: 1
			}, 8, ["modelValue"])), [[_directive_loading, loading.value]]);
		};
	}
});
//#endregion
//#region src/views/lowcode/publish-center/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { style: { "padding": "16px" } };
var _hoisted_2 = {
	key: 1,
	class: "single-step"
};
//#endregion
//#region src/views/lowcode/publish-center/index.vue
var publish_center_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "PublishCenterView",
	__name: "index",
	setup(__props) {
		const userStore = useUserStore();
		const pendingList = ref([]);
		const loading = ref(false);
		/** 审批链映射：approvalChainId → 审批链对象（用于显示当前级别名称/总数） */
		const chainMap = ref(/* @__PURE__ */ new Map());
		/** 灰度发布管理对话框状态（批次5-T4） */
		const grayManagerVisible = ref(false);
		const grayManagerConfigType = ref("");
		const grayManagerConfigId = ref(0);
		const grayManagerPublishRecordId = ref(null);
		/** 打开灰度发布管理器（仅 PUBLISHED 状态可用） */
		function openGrayManager(row) {
			var _row$configId, _row$id;
			grayManagerConfigType.value = row.configType;
			grayManagerConfigId.value = (_row$configId = row.configId) !== null && _row$configId !== void 0 ? _row$configId : 0;
			grayManagerPublishRecordId.value = (_row$id = row.id) !== null && _row$id !== void 0 ? _row$id : null;
			grayManagerVisible.value = true;
		}
		async function loadPending() {
			loading.value = true;
			try {
				const [list, chains] = await Promise.all([getPendingList(), getApprovalChainList()]);
				pendingList.value = list;
				const map = /* @__PURE__ */ new Map();
				chains.forEach((c) => {
					if (c.id != null) map.set(c.id, c);
				});
				chainMap.value = map;
			} finally {
				loading.value = false;
			}
		}
		/**
		* 计算审批级别展示文本。
		*
		* <p>多级审批（approvalChainId 存在且 currentLevel 存在）：返回 "1/3 主管审批" 格式，
		* 其中 3 为审批链总级数，"主管审批"为当前级别名称；
		* 单步审批（无审批链）：返回 "单步审批"。</p>
		*/
		function approvalLevelText(row) {
			var _current$name;
			if (!row.approvalChainId || row.currentLevel == null) return "单步审批";
			const chain = chainMap.value.get(row.approvalChainId);
			if (!chain) return `${row.currentLevel}/?`;
			const levels = parseLevels(chain.levels);
			if (levels.length === 0) return `${row.currentLevel}/?`;
			const current = levels.find((l) => l.level === row.currentLevel);
			const total = levels.length;
			const name = (_current$name = current === null || current === void 0 ? void 0 : current.name) !== null && _current$name !== void 0 ? _current$name : "";
			return `${row.currentLevel}/${total} ${name}`.trim();
		}
		/** 状态文案与颜色映射（含多级审批进行中 APPROVING） */
		function statusTagType(status) {
			switch (status) {
				case "PUBLISHED": return "success";
				case "APPROVED": return "success";
				case "REJECTED": return "danger";
				case "APPROVING": return "warning";
				default: return "warning";
			}
		}
		function statusLabel(status) {
			switch (status) {
				case "DRAFT": return "草稿";
				case "SUBMITTED": return "待审批";
				case "APPROVING": return "审批中";
				case "APPROVED": return "已通过";
				case "REJECTED": return "已拒绝";
				case "PUBLISHED": return "已发布";
				default: return status;
			}
		}
		async function approve(id) {
			try {
				var _userStore$userInfo, _userStore$userInfo2;
				await approvePublish(id, ((_userStore$userInfo = userStore.userInfo) === null || _userStore$userInfo === void 0 ? void 0 : _userStore$userInfo.id) || 0, (_userStore$userInfo2 = userStore.userInfo) === null || _userStore$userInfo2 === void 0 ? void 0 : _userStore$userInfo2.username);
				ElMessage.success("审批通过");
				await loadPending();
			} catch (_unused) {}
		}
		async function reject(id) {
			try {
				var _userStore$userInfo3, _userStore$userInfo4;
				const { value: reason } = await ElMessageBox.prompt("请输入拒绝原因", "审批拒绝", {
					confirmButtonText: "拒绝",
					cancelButtonText: "取消"
				});
				await rejectPublish(id, reason, ((_userStore$userInfo3 = userStore.userInfo) === null || _userStore$userInfo3 === void 0 ? void 0 : _userStore$userInfo3.id) || 0, (_userStore$userInfo4 = userStore.userInfo) === null || _userStore$userInfo4 === void 0 ? void 0 : _userStore$userInfo4.username);
				ElMessage.success("已拒绝");
				await loadPending();
			} catch (_unused2) {}
		}
		async function rollback(id) {
			try {
				var _userStore$userInfo5, _userStore$userInfo6;
				await ElMessageBox.confirm("确认回滚到该版本？", "提示", { type: "warning" });
				await rollbackPublish(id, ((_userStore$userInfo5 = userStore.userInfo) === null || _userStore$userInfo5 === void 0 ? void 0 : _userStore$userInfo5.id) || 0, (_userStore$userInfo6 = userStore.userInfo) === null || _userStore$userInfo6 === void 0 ? void 0 : _userStore$userInfo6.username);
				ElMessage.success("回滚成功");
			} catch (_unused3) {}
		}
		onMounted(loadPending);
		return (_ctx, _cache) => {
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_card = resolveComponent("el-card");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_card, { shadow: "never" }, {
				header: withCtx(() => [..._cache[1] || (_cache[1] = [createElementVNode("span", null, "发布审批中心", -1)])]),
				default: withCtx(() => [withDirectives((openBlock(), createBlock(_component_el_table, { data: pendingList.value }, {
					default: withCtx(() => [
						createVNode(_component_el_table_column, {
							label: "配置类型",
							prop: "configType",
							width: "100"
						}),
						createVNode(_component_el_table_column, {
							label: "配置 ID",
							prop: "configId",
							width: "80"
						}),
						createVNode(_component_el_table_column, {
							label: "版本",
							prop: "version",
							width: "60"
						}),
						createVNode(_component_el_table_column, {
							label: "申请人",
							prop: "applicant",
							width: "100"
						}),
						createVNode(_component_el_table_column, {
							label: "变更说明",
							prop: "changeLog",
							"show-overflow-tooltip": ""
						}),
						createVNode(_component_el_table_column, {
							label: "审批级别",
							width: "140"
						}, {
							default: withCtx(({ row }) => [row.approvalChainId ? (openBlock(), createBlock(_component_el_tag, {
								key: 0,
								type: "primary",
								size: "small"
							}, {
								default: withCtx(() => [createTextVNode(toDisplayString(approvalLevelText(row)), 1)]),
								_: 2
							}, 1024)) : (openBlock(), createElementBlock("span", _hoisted_2, "单步审批"))]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							label: "状态",
							width: "100"
						}, {
							default: withCtx(({ row }) => [createVNode(_component_el_tag, { type: statusTagType(row.status) }, {
								default: withCtx(() => [createTextVNode(toDisplayString(statusLabel(row.status)), 1)]),
								_: 2
							}, 1032, ["type"])]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							label: "操作",
							width: "280"
						}, {
							default: withCtx(({ row }) => [
								createVNode(_component_el_button, {
									size: "small",
									type: "success",
									onClick: ($event) => approve(row.id)
								}, {
									default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("通过", -1)])]),
									_: 1
								}, 8, ["onClick"]),
								createVNode(_component_el_button, {
									size: "small",
									type: "danger",
									onClick: ($event) => reject(row.id)
								}, {
									default: withCtx(() => [..._cache[3] || (_cache[3] = [createTextVNode("拒绝", -1)])]),
									_: 1
								}, 8, ["onClick"]),
								createVNode(_component_el_button, {
									size: "small",
									type: "warning",
									disabled: row.status !== "PUBLISHED",
									onClick: ($event) => rollback(row.id)
								}, {
									default: withCtx(() => [..._cache[4] || (_cache[4] = [createTextVNode(" 回滚 ", -1)])]),
									_: 1
								}, 8, ["disabled", "onClick"]),
								row.status === "PUBLISHED" ? (openBlock(), createBlock(_component_el_button, {
									key: 0,
									link: "",
									type: "warning",
									size: "small",
									onClick: ($event) => openGrayManager(row)
								}, {
									default: withCtx(() => [..._cache[5] || (_cache[5] = [createTextVNode(" 灰度 ", -1)])]),
									_: 1
								}, 8, ["onClick"])) : createCommentVNode("", true)
							]),
							_: 1
						})
					]),
					_: 1
				}, 8, ["data"])), [[_directive_loading, loading.value]])]),
				_: 1
			}), createVNode(GrayReleaseManager_default, {
				visible: grayManagerVisible.value,
				"onUpdate:visible": _cache[0] || (_cache[0] = ($event) => grayManagerVisible.value = $event),
				"config-type": grayManagerConfigType.value,
				"config-id": grayManagerConfigId.value,
				"publish-record-id": grayManagerPublishRecordId.value
			}, null, 8, [
				"visible",
				"config-type",
				"config-id",
				"publish-record-id"
			])]);
		};
	}
}), [["__scopeId", "data-v-728d5518"]]);
//#endregion
export { publish_center_default as default };

//# sourceMappingURL=publish-center-CSVcBi3k.js.map