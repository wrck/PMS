import { i as post, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { t as JsonTreeDiff_default } from "./JsonTreeDiff-CvcFM07K.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, normalizeStyle, openBlock, ref, renderList, resolveComponent, resolveDirective, resolveDynamicComponent, toDisplayString, watch, withCtx, withDirectives, withModifiers } from "vue";
//#region src/api/lowcode-version.ts
function getVersionHistory(configType, configId) {
	return get("/api/lowcode/version/history", {
		configType,
		configId
	});
}
/** 查询版本树（按 parentVersionId 构建分支树，支持多分支） */
function getVersionTree(configType, configId) {
	return get("/api/lowcode/version/tree", {
		configType,
		configId
	});
}
function diffVersions(configType, configId, fromVersion, toVersion) {
	return get("/api/lowcode/version/diff", {
		configType,
		configId,
		fromVersion,
		toVersion
	});
}
function rollbackVersion(configType, configId, targetVersion, changeLog) {
	return post("/api/lowcode/version/rollback", null, { params: {
		configType,
		configId,
		targetVersion,
		changeLog
	} });
}
function promoteConfig(targetEnvironment, configCodes) {
	return post("/api/lowcode/version/promote", configCodes, { params: { targetEnvironment } });
}
/** 导出配置包（zip 二进制） */
function exportPackageZip(configCodes, targetEnvironment) {
	return post("/api/lowcode/version/export-package", {
		configCodes,
		targetEnvironment
	}, { responseType: "blob" });
}
/** 创建分支（基于指定版本创建新分支） */
function createBranch(params) {
	return post("/api/lowcode/version/branch", params);
}
/** 为版本添加标签 */
function addTag(params) {
	return post("/api/lowcode/version/tag", params);
}
/** 查询晋升管道状态（多个 configCode） */
function getPipelineStatus(configCodes) {
	return get("/api/lowcode/version/pipeline", { configCodes: configCodes.join(",") });
}
/** 晋升门禁预检（不实际晋升） */
function checkPromotionGate(params) {
	return post("/api/lowcode/version/gate-check", params);
}
/** 检测导入冲突 */
function detectImportConflicts(packageJson, targetEnvironment) {
	return post("/api/lowcode/version/import-conflicts", {
		packageJson,
		targetEnvironment
	});
}
/** 按解决方案导入配置包 */
function importWithResolution(packageJson, targetEnvironment, resolutions) {
	return post("/api/lowcode/version/import-resolve", {
		packageJson,
		targetEnvironment,
		resolutions
	});
}
/** 读取上传文件为文本 */
function readFileAsText(file) {
	return new Promise((resolve, reject) => {
		const reader = new FileReader();
		reader.onload = () => resolve(String(reader.result || ""));
		reader.onerror = () => reject(reader.error);
		reader.readAsText(file);
	});
}
/** 回滚预览（对比当前版本与目标版本差异） */
function getRollbackPreview(configType, configId, targetVersion) {
	return get("/api/lowcode/version/rollback-preview", {
		configType,
		configId,
		targetVersion
	});
}
/** 发布影响范围分析 */
function getPublishImpact(configType, configId, configCode) {
	return get("/api/lowcode/version/publish-impact", {
		configType,
		configId,
		configCode
	});
}
//#endregion
//#region src/views/lowcode/version-history/PromotionPipeline.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$2 = { class: "promotion-pipeline" };
var _hoisted_2$2 = {
	key: 0,
	class: "pipeline-toolbar"
};
var _hoisted_3$2 = {
	key: 2,
	class: "pipeline-grid"
};
var _hoisted_4$1 = { class: "pipeline-header" };
var _hoisted_5$1 = { class: "pipeline-col-name" };
var _hoisted_6$1 = { class: "pipeline-card-area" };
var _hoisted_7$1 = {
	key: 0,
	class: "version-card"
};
var _hoisted_8$1 = { class: "version-card-header" };
var _hoisted_9 = { class: "version-no" };
var _hoisted_10 = { class: "version-card-body" };
var _hoisted_11 = ["title"];
var _hoisted_12 = { class: "version-card-meta" };
var _hoisted_13 = { class: "version-card-actions" };
var _hoisted_14 = {
	key: 0,
	class: "pipeline-gate"
};
//#endregion
//#region src/views/lowcode/version-history/PromotionPipeline.vue
var PromotionPipeline_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "PromotionPipeline",
	props: {
		configCodes: { default: () => [] },
		initialCode: { default: "" }
	},
	emits: ["diff", "promoted"],
	setup(__props, { emit: __emit }) {
		/**
		* 晋升管道图（批次5-T2，借鉴 OutSystems LifeTime）。
		*
		* <p>横向三列布局：DEV → TEST → PROD，每列展示对应环境最新 ACTIVE 版本卡片。
		* 列间显示门禁检查状态图标（绿勾通过/红叉失败/警告部分通过），鼠标悬停显示失败原因。
		* 提供"晋升"按钮，点击先做门禁预检，通过后实际晋升。</p>
		*/
		const props = __props;
		const emit = __emit;
		const pipelineData = ref([]);
		const currentCode = ref(props.initialCode || "");
		const loading = ref(false);
		const ENV_COLUMNS = [
			{
				code: "DEV",
				name: "开发环境",
				tag: "info"
			},
			{
				code: "TEST",
				name: "测试环境",
				tag: "warning"
			},
			{
				code: "PROD",
				name: "生产环境",
				tag: "danger"
			}
		];
		const currentPipeline = computed(() => {
			if (!currentCode.value || pipelineData.value.length === 0) return null;
			return pipelineData.value.find((p) => p.configCode === currentCode.value) || null;
		});
		function getVersion(p, env) {
			if (!p) return null;
			if (env === "DEV") return p.devVersion;
			if (env === "TEST") return p.testVersion;
			if (env === "PROD") return p.prodVersion;
			return null;
		}
		function getGate(p, from, to) {
			if (!p) return null;
			if (from === "DEV" && to === "TEST") return p.devToTestGate;
			if (from === "TEST" && to === "PROD") return p.testToProdGate;
			return null;
		}
		function gateIcon(gate) {
			if (!gate) return {
				icon: "Minus",
				color: "#909399",
				label: "无门禁"
			};
			if (gate.passed) return {
				icon: "CircleCheck",
				color: "#67c23a",
				label: "门禁通过"
			};
			return {
				icon: "CircleClose",
				color: "#f56c6c",
				label: `门禁失败 (${gate.failureCount})`
			};
		}
		function gateTooltip(gate) {
			if (!gate) return "无门禁数据";
			if (gate.passed) return "门禁通过，可执行晋升";
			return "门禁失败：\n" + gate.failureSummaries.map((s, i) => `${i + 1}. ${s}`).join("\n");
		}
		function fmtTime(t) {
			return t ? t.replace("T", " ").slice(0, 16) : "-";
		}
		function statusTag(status) {
			return status === "ACTIVE" ? "success" : "info";
		}
		async function loadPipeline() {
			if (props.configCodes.length === 0) {
				ElMessage.warning("请先输入配置编码");
				return;
			}
			loading.value = true;
			try {
				pipelineData.value = await getPipelineStatus(props.configCodes);
				if (!currentCode.value && pipelineData.value.length > 0) currentCode.value = pipelineData.value[0].configCode;
			} catch (e) {
				ElMessage.error("加载管道状态失败");
				pipelineData.value = [];
			} finally {
				loading.value = false;
			}
		}
		async function promote(from, to) {
			if (!currentCode.value) return;
			try {
				const gate = await checkPromotionGate({
					sourceEnvironment: from,
					targetEnvironment: to,
					configCodes: [currentCode.value]
				});
				if (!gate.passed) {
					const reasons = gate.failures.map((f) => `• ${f.rule}: ${f.reason}`).join("\n");
					await ElMessageBox.alert(`门禁检查未通过，禁止晋升：\n${reasons}`, "晋升门禁失败", {
						confirmButtonText: "知道了",
						type: "error"
					});
					return;
				}
				await ElMessageBox.confirm(`确认将配置 '${currentCode.value}' 从 ${from} 晋升到 ${to}？`, "环境晋升", {
					confirmButtonText: "晋升",
					cancelButtonText: "取消",
					type: "warning"
				});
				await promoteConfig(to, [currentCode.value]);
				ElMessage.success("晋升成功");
				emit("promoted", currentCode.value);
				await loadPipeline();
			} catch (e) {
				if (e !== "cancel" && e !== "close") ElMessage.error("晋升失败");
			}
		}
		function diffVersion(env) {
			if (!currentCode.value) return;
			const v = getVersion(currentPipeline.value, env);
			if (v) emit("diff", {
				configCode: currentCode.value,
				version: v.version
			});
		}
		watch(() => props.configCodes, () => {
			if (props.configCodes.length > 0) loadPipeline();
		}, { immediate: true });
		return (_ctx, _cache) => {
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_tooltip = resolveComponent("el-tooltip");
			const _directive_loading = resolveDirective("loading");
			return withDirectives((openBlock(), createElementBlock("div", _hoisted_1$2, [pipelineData.value.length > 0 ? (openBlock(), createElementBlock("div", _hoisted_2$2, [createVNode(_component_el_select, {
				modelValue: currentCode.value,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => currentCode.value = $event),
				placeholder: "选择配置编码",
				size: "small",
				style: { "width": "240px" }
			}, {
				default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(pipelineData.value, (p) => {
					return openBlock(), createBlock(_component_el_option, {
						key: p.configCode,
						label: `${p.configCode}${p.configType ? " (" + p.configType + ")" : ""}`,
						value: p.configCode
					}, null, 8, ["label", "value"]);
				}), 128))]),
				_: 1
			}, 8, ["modelValue"]), createVNode(_component_el_button, {
				size: "small",
				onClick: loadPipeline
			}, {
				default: withCtx(() => [..._cache[1] || (_cache[1] = [createTextVNode("刷新", -1)])]),
				_: 1
			})])) : createCommentVNode("", true), pipelineData.value.length === 0 ? (openBlock(), createBlock(_component_el_empty, {
				key: 1,
				description: "请输入配置编码查询管道状态",
				"image-size": 80
			})) : currentPipeline.value ? (openBlock(), createElementBlock("div", _hoisted_3$2, [(openBlock(), createElementBlock(Fragment, null, renderList(ENV_COLUMNS, (col, idx) => {
				var _getVersion, _getVersion2, _getVersion4, _getVersion5, _getVersion6, _getVersion7, _getGate;
				return createElementVNode("div", {
					key: col.code,
					class: "pipeline-col"
				}, [
					createElementVNode("div", _hoisted_4$1, [createVNode(_component_el_tag, {
						type: col.tag,
						effect: "dark",
						size: "large"
					}, {
						default: withCtx(() => [createTextVNode(toDisplayString(col.code), 1)]),
						_: 2
					}, 1032, ["type"]), createElementVNode("span", _hoisted_5$1, toDisplayString(col.name), 1)]),
					createElementVNode("div", _hoisted_6$1, [getVersion(currentPipeline.value, col.code) ? (openBlock(), createElementBlock("div", _hoisted_7$1, [
						createElementVNode("div", _hoisted_8$1, [createElementVNode("span", _hoisted_9, "v" + toDisplayString((_getVersion = getVersion(currentPipeline.value, col.code)) === null || _getVersion === void 0 ? void 0 : _getVersion.version), 1), createVNode(_component_el_tag, {
							size: "small",
							type: statusTag((_getVersion2 = getVersion(currentPipeline.value, col.code)) === null || _getVersion2 === void 0 ? void 0 : _getVersion2.status)
						}, {
							default: withCtx(() => {
								var _getVersion3;
								return [createTextVNode(toDisplayString((_getVersion3 = getVersion(currentPipeline.value, col.code)) === null || _getVersion3 === void 0 ? void 0 : _getVersion3.status), 1)];
							}),
							_: 2
						}, 1032, ["type"])]),
						createElementVNode("div", _hoisted_10, [createElementVNode("div", {
							class: "version-card-row",
							title: (_getVersion4 = getVersion(currentPipeline.value, col.code)) === null || _getVersion4 === void 0 ? void 0 : _getVersion4.changeLog
						}, toDisplayString(((_getVersion5 = getVersion(currentPipeline.value, col.code)) === null || _getVersion5 === void 0 ? void 0 : _getVersion5.changeLog) || "（无变更说明）"), 9, _hoisted_11), createElementVNode("div", _hoisted_12, [createElementVNode("span", null, toDisplayString(((_getVersion6 = getVersion(currentPipeline.value, col.code)) === null || _getVersion6 === void 0 ? void 0 : _getVersion6.createBy) || "-"), 1), createElementVNode("span", null, toDisplayString(fmtTime((_getVersion7 = getVersion(currentPipeline.value, col.code)) === null || _getVersion7 === void 0 ? void 0 : _getVersion7.createTime)), 1)])]),
						createElementVNode("div", _hoisted_13, [createVNode(_component_el_button, {
							link: "",
							type: "primary",
							size: "small",
							onClick: ($event) => diffVersion(col.code)
						}, {
							default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("对比", -1)])]),
							_: 1
						}, 8, ["onClick"])])
					])) : (openBlock(), createBlock(_component_el_empty, {
						key: 1,
						description: "无版本",
						"image-size": 40
					}))]),
					idx < ENV_COLUMNS.length - 1 ? (openBlock(), createElementBlock("div", _hoisted_14, [
						createVNode(_component_el_tooltip, {
							content: gateTooltip(getGate(currentPipeline.value, col.code, ENV_COLUMNS[idx + 1].code)),
							placement: "top"
						}, {
							default: withCtx(() => [createVNode(_component_el_icon, {
								color: gateIcon(getGate(currentPipeline.value, col.code, ENV_COLUMNS[idx + 1].code)).color,
								size: 22
							}, {
								default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(gateIcon(getGate(currentPipeline.value, col.code, ENV_COLUMNS[idx + 1].code)).icon)))]),
								_: 2
							}, 1032, ["color"])]),
							_: 2
						}, 1032, ["content"]),
						createElementVNode("span", {
							class: "gate-label",
							style: normalizeStyle({ color: gateIcon(getGate(currentPipeline.value, col.code, ENV_COLUMNS[idx + 1].code)).color })
						}, toDisplayString(gateIcon(getGate(currentPipeline.value, col.code, ENV_COLUMNS[idx + 1].code)).label), 5),
						createVNode(_component_el_button, {
							size: "small",
							type: "primary",
							disabled: !((_getGate = getGate(currentPipeline.value, col.code, ENV_COLUMNS[idx + 1].code)) === null || _getGate === void 0 ? void 0 : _getGate.passed),
							onClick: ($event) => promote(col.code, ENV_COLUMNS[idx + 1].code)
						}, {
							default: withCtx(() => [createTextVNode(" 晋升到" + toDisplayString(ENV_COLUMNS[idx + 1].code), 1)]),
							_: 2
						}, 1032, ["disabled", "onClick"])
					])) : createCommentVNode("", true)
				]);
			}), 64))])) : createCommentVNode("", true)])), [[_directive_loading, loading.value]]);
		};
	}
}), [["__scopeId", "data-v-d7335ceb"]]);
//#endregion
//#region src/views/lowcode/version-history/ImportConflictResolver.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$1 = {
	key: 0,
	class: "conflict-summary"
};
var _hoisted_2$1 = { class: "version-meta" };
var _hoisted_3$1 = { class: "version-meta" };
//#endregion
//#region src/views/lowcode/version-history/ImportConflictResolver.vue
var ImportConflictResolver_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "ImportConflictResolver",
	props: {
		visible: { type: Boolean },
		file: {},
		targetEnvironment: {}
	},
	emits: ["update:visible", "imported"],
	setup(__props, { emit: __emit }) {
		/**
		* 导入冲突解决器（批次5-T3，借鉴 Appsmith Git 导入冲突解决）。
		*
		* <p>显示冲突项表格，每行展示源版本与目标版本对比，用户可选择解决方式：
		* <ul>
		*   <li>保留源版本（KEEP_SOURCE）: 用导入包的版本覆盖目标环境</li>
		*   <li>保留目标版本（KEEP_TARGET）: 跳过此项导入，保留目标环境现有版本</li>
		*   <li>跳过（SKIP）: 不导入此项</li>
		* </ul></p>
		*/
		const props = __props;
		const emit = __emit;
		const loading = ref(false);
		const importing = ref(false);
		const conflictData = ref(null);
		const packageJson = ref("");
		/** 冲突项的解决方式选择（key=configCode, value=KEEP_SOURCE|KEEP_TARGET|SKIP） */
		const resolutions = ref({});
		const dialogVisible = computed({
			get: () => props.visible,
			set: (v) => emit("update:visible", v)
		});
		const hasConflicts = computed(() => {
			var _conflictData$value;
			return (((_conflictData$value = conflictData.value) === null || _conflictData$value === void 0 ? void 0 : _conflictData$value.conflicts.length) || 0) > 0;
		});
		watch(() => props.visible, async (v) => {
			if (v && props.file) await loadConflicts();
		});
		async function loadConflicts() {
			if (!props.file) {
				ElMessage.warning("请先选择文件");
				return;
			}
			loading.value = true;
			try {
				const text = await readFileAsText(props.file);
				packageJson.value = text;
				const result = await detectImportConflicts(text, props.targetEnvironment);
				conflictData.value = result;
				const newRes = {};
				for (const c of result.conflicts) newRes[c.configCode] = "KEEP_SOURCE";
				resolutions.value = newRes;
			} catch (e) {
				ElMessage.error("检测冲突失败");
				conflictData.value = null;
			} finally {
				loading.value = false;
			}
		}
		async function submitImport() {
			if (!packageJson.value) {
				ElMessage.warning("配置包内容为空");
				return;
			}
			importing.value = true;
			try {
				var _conflictData$value2;
				await importWithResolution(packageJson.value, props.targetEnvironment, resolutions.value);
				ElMessage.success(`导入成功（共 ${(_conflictData$value2 = conflictData.value) === null || _conflictData$value2 === void 0 ? void 0 : _conflictData$value2.totalCount} 项）`);
				dialogVisible.value = false;
				emit("imported");
			} catch (e) {
				ElMessage.error("导入失败");
			} finally {
				importing.value = false;
			}
		}
		return (_ctx, _cache) => {
			const _component_el_alert = resolveComponent("el-alert");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return withDirectives((openBlock(), createBlock(_component_el_dialog, {
				modelValue: dialogVisible.value,
				"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => dialogVisible.value = $event),
				title: "导入冲突解决",
				width: "900px",
				"close-on-click-modal": false
			}, {
				footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[0] || (_cache[0] = ($event) => dialogVisible.value = false) }, {
					default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("取消", -1)])]),
					_: 1
				}), createVNode(_component_el_button, {
					type: "primary",
					loading: importing.value,
					disabled: loading.value || !conflictData.value,
					onClick: submitImport
				}, {
					default: withCtx(() => [..._cache[3] || (_cache[3] = [createTextVNode(" 确认导入 ", -1)])]),
					_: 1
				}, 8, ["loading", "disabled"])]),
				default: withCtx(() => {
					var _conflictData$value3;
					return [
						conflictData.value ? (openBlock(), createElementBlock("div", _hoisted_1$1, [createVNode(_component_el_alert, {
							title: `共 ${conflictData.value.totalCount} 项配置，无冲突 ${conflictData.value.noConflictCount} 项，有冲突 ${conflictData.value.conflicts.length} 项`,
							type: hasConflicts.value ? "warning" : "success",
							closable: false,
							"show-icon": ""
						}, null, 8, ["title", "type"])])) : createCommentVNode("", true),
						conflictData.value && !hasConflicts.value ? (openBlock(), createBlock(_component_el_empty, {
							key: 1,
							description: "无冲突，可直接导入",
							"image-size": 60
						})) : createCommentVNode("", true),
						hasConflicts.value ? (openBlock(), createBlock(_component_el_table, {
							key: 2,
							data: ((_conflictData$value3 = conflictData.value) === null || _conflictData$value3 === void 0 ? void 0 : _conflictData$value3.conflicts) || [],
							size: "small",
							border: "",
							"max-height": "400"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									label: "配置编码",
									prop: "configCode",
									width: "160"
								}),
								createVNode(_component_el_table_column, {
									label: "类型",
									prop: "configType",
									width: "100"
								}),
								createVNode(_component_el_table_column, {
									label: "源版本",
									width: "140"
								}, {
									default: withCtx(({ row }) => [createElementVNode("div", null, "v" + toDisplayString(row.sourceVersion), 1), createElementVNode("div", _hoisted_2$1, toDisplayString(row.sourceChangeLog || "（无说明）"), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "目标版本（已有）",
									width: "140"
								}, {
									default: withCtx(({ row }) => [createElementVNode("div", null, "v" + toDisplayString(row.targetVersion), 1), createElementVNode("div", _hoisted_3$1, toDisplayString(row.targetChangeLog || "（无说明）"), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "解决方式",
									width: "180"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_select, {
										modelValue: resolutions.value[row.configCode],
										"onUpdate:modelValue": ($event) => resolutions.value[row.configCode] = $event,
										size: "small"
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "保留源版本（覆盖）",
												value: "KEEP_SOURCE"
											}),
											createVNode(_component_el_option, {
												label: "保留目标版本（跳过）",
												value: "KEEP_TARGET"
											}),
											createVNode(_component_el_option, {
												label: "跳过",
												value: "SKIP"
											})
										]),
										_: 1
									}, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["data"])) : createCommentVNode("", true)
					];
				}),
				_: 1
			}, 8, ["modelValue"])), [[_directive_loading, loading.value]]);
		};
	}
}), [["__scopeId", "data-v-34f43b11"]]);
//#endregion
//#region src/views/lowcode/version-history/RollbackPreviewDialog.vue
var RollbackPreviewDialog_default = /* @__PURE__ */ defineComponent({
	__name: "RollbackPreviewDialog",
	props: {
		visible: { type: Boolean },
		configType: {},
		configId: {},
		targetVersion: { default: null },
		targetSnapshot: { default: "" },
		versionList: { default: () => [] }
	},
	emits: ["update:visible", "rolled"],
	setup(__props, { emit: __emit }) {
		/**
		* 回滚预览对话框（批次5-T5，借鉴 OutSystems LifeTime 回滚预览）。
		*
		* <p>回滚前展示：
		* <ul>
		*   <li>版本 Diff（复用 JsonTreeDiff 组件）</li>
		*   <li>发布影响范围分析（受影响的下游配置列表）</li>
		* </ul>
		* 用户确认后执行回滚。</p>
		*/
		const props = __props;
		const emit = __emit;
		const loading = ref(false);
		const rolling = ref(false);
		const diffResult = ref(null);
		const impactResult = ref(null);
		const oldSnapshot = ref(null);
		const newSnapshot = ref(null);
		const visibleComputed = computed({
			get: () => props.visible,
			set: (v) => emit("update:visible", v)
		});
		watch(() => props.visible, async (v) => {
			if (v && props.targetVersion) await loadPreview();
		});
		async function loadPreview() {
			if (!props.targetVersion) return;
			loading.value = true;
			try {
				diffResult.value = await getRollbackPreview(props.configType, props.configId, props.targetVersion);
				const currentVersion = props.versionList.find((v) => v.environment === "DEV" && v.status === "ACTIVE");
				if (currentVersion && props.targetSnapshot) try {
					oldSnapshot.value = JSON.parse(currentVersion.snapshot || "{}");
					newSnapshot.value = JSON.parse(props.targetSnapshot);
				} catch (_unused) {
					oldSnapshot.value = null;
					newSnapshot.value = null;
				}
				const targetVer = props.versionList.find((v) => v.version === props.targetVersion);
				if (targetVer === null || targetVer === void 0 ? void 0 : targetVer.configCode) impactResult.value = await getPublishImpact(props.configType, props.configId, targetVer.configCode);
			} catch (e) {
				ElMessage.error("加载预览失败");
				diffResult.value = null;
				impactResult.value = null;
			} finally {
				loading.value = false;
			}
		}
		async function confirmRollback() {
			if (!props.targetVersion) return;
			rolling.value = true;
			try {
				await rollbackVersion(props.configType, props.configId, props.targetVersion, `回滚到 v${props.targetVersion}`);
				ElMessage.success("回滚成功，已生成新版本");
				visibleComputed.value = false;
				emit("rolled");
			} catch (e) {
				ElMessage.error("回滚失败");
			} finally {
				rolling.value = false;
			}
		}
		function severityTag(s) {
			if (s === "HIGH") return "danger";
			if (s === "MEDIUM") return "warning";
			return "info";
		}
		return (_ctx, _cache) => {
			const _component_el_alert = resolveComponent("el-alert");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_tab_pane = resolveComponent("el-tab-pane");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_tabs = resolveComponent("el-tabs");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return withDirectives((openBlock(), createBlock(_component_el_dialog, {
				modelValue: visibleComputed.value,
				"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => visibleComputed.value = $event),
				title: "回滚预览",
				width: "900px",
				"close-on-click-modal": false
			}, {
				footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[0] || (_cache[0] = ($event) => visibleComputed.value = false) }, {
					default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("取消", -1)])]),
					_: 1
				}), createVNode(_component_el_button, {
					type: "warning",
					loading: rolling.value,
					disabled: loading.value,
					onClick: confirmRollback
				}, {
					default: withCtx(() => [..._cache[3] || (_cache[3] = [createTextVNode(" 确认回滚 ", -1)])]),
					_: 1
				}, 8, ["loading", "disabled"])]),
				default: withCtx(() => [createVNode(_component_el_alert, {
					type: "warning",
					closable: false,
					"show-icon": "",
					style: { "margin-bottom": "16px" }
				}, {
					title: withCtx(() => [createTextVNode(" 回滚到 v" + toDisplayString(__props.targetVersion) + "？以下为版本差异与影响范围分析，确认后将创建新版本（不删除历史）。 ", 1)]),
					_: 1
				}), createVNode(_component_el_tabs, null, {
					default: withCtx(() => [createVNode(_component_el_tab_pane, { label: "版本差异" }, {
						default: withCtx(() => [!diffResult.value || diffResult.value.entries.length === 0 ? (openBlock(), createBlock(_component_el_empty, {
							key: 0,
							description: "无差异",
							"image-size": 60
						})) : (openBlock(), createBlock(JsonTreeDiff_default, {
							key: 1,
							"old-data": oldSnapshot.value,
							"new-data": newSnapshot.value
						}, null, 8, ["old-data", "new-data"])), diffResult.value && diffResult.value.entries.length > 0 ? (openBlock(), createBlock(_component_el_table, {
							key: 2,
							data: diffResult.value.entries,
							size: "small",
							border: "",
							style: { "margin-top": "12px" }
						}, {
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									label: "类型",
									prop: "changeType",
									width: "80"
								}),
								createVNode(_component_el_table_column, {
									label: "字段路径",
									prop: "fieldPath"
								}),
								createVNode(_component_el_table_column, {
									label: "旧值",
									prop: "oldValue",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									label: "新值",
									prop: "newValue",
									"show-overflow-tooltip": ""
								})
							]),
							_: 1
						}, 8, ["data"])) : createCommentVNode("", true)]),
						_: 1
					}), createVNode(_component_el_tab_pane, { label: `影响范围${impactResult.value ? "(" + impactResult.value.totalImpacted + ")" : ""}` }, {
						default: withCtx(() => [!impactResult.value || impactResult.value.totalImpacted === 0 ? (openBlock(), createBlock(_component_el_empty, {
							key: 0,
							description: "无受影响配置",
							"image-size": 60
						})) : (openBlock(), createBlock(_component_el_table, {
							key: 1,
							data: impactResult.value.impactedConfigs,
							size: "small",
							border: ""
						}, {
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									label: "类型",
									prop: "configType",
									width: "100"
								}),
								createVNode(_component_el_table_column, {
									label: "编码",
									prop: "configCode"
								}),
								createVNode(_component_el_table_column, {
									label: "引用字段",
									prop: "referenceField",
									width: "120"
								}),
								createVNode(_component_el_table_column, {
									label: "状态",
									prop: "status",
									width: "100"
								}),
								createVNode(_component_el_table_column, {
									label: "严重度",
									width: "100"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, {
										type: severityTag(row.severity),
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(row.severity), 1)]),
										_: 2
									}, 1032, ["type"])]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["data"]))]),
						_: 1
					}, 8, ["label"])]),
					_: 1
				})]),
				_: 1
			}, 8, ["modelValue"])), [[_directive_loading, loading.value]]);
		};
	}
});
//#endregion
//#region src/views/lowcode/version-history/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "version-history" };
var _hoisted_2 = { style: {
	"display": "flex",
	"justify-content": "space-between",
	"align-items": "center"
} };
var _hoisted_3 = { class: "tree-node" };
var _hoisted_4 = { class: "tree-node-label" };
var _hoisted_5 = { class: "tree-node-meta" };
var _hoisted_6 = { style: { "margin-bottom": "12px" } };
var _hoisted_7 = { style: {
	"display": "flex",
	"justify-content": "space-between",
	"align-items": "center"
} };
var _hoisted_8 = {
	key: 1,
	style: {
		"text-align": "center",
		"padding": "40px",
		"color": "#909399"
	}
};
//#endregion
//#region src/views/lowcode/version-history/index.vue
var version_history_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "VersionHistoryView",
	__name: "index",
	setup(__props) {
		const configType = ref("ENTITY");
		const configId = ref();
		const versionList = ref([]);
		const versionTree = ref([]);
		const selectedVersions = ref([]);
		const diffResult = ref(null);
		const loading = ref(false);
		const diffMode = ref("tree");
		const oldSnapshot = ref(null);
		const newSnapshot = ref(null);
		const exportDialogVisible = ref(false);
		const importDialogVisible = ref(false);
		const exportCodes = ref("");
		const exportTargetEnv = ref("TEST");
		const importFile = ref(null);
		const importOverwrite = ref(false);
		/** 视图切换：列表 / 树形 / 管道图 */
		const activeTab = ref("list");
		const branchDialogVisible = ref(false);
		const tagDialogVisible = ref(false);
		const branchForm = ref({
			baseVersionId: 0,
			branchName: "",
			changeLog: ""
		});
		const tagForm = ref({
			versionId: 0,
			tag: ""
		});
		/** 管道图 configCodes 输入 */
		const pipelineCodesInput = ref("");
		/** 管道图传入的 configCodes 数组 */
		const pipelineCodes = ref([]);
		/** 冲突解决器对话框 */
		const conflictResolverVisible = ref(false);
		/** 冲突解决器目标环境 */
		const conflictResolverTargetEnv = ref("TEST");
		/** 回滚预览对话框 */
		const rollbackPreviewVisible = ref(false);
		const rollbackTargetVersion = ref(null);
		const rollbackTargetSnapshot = ref("");
		async function loadHistory() {
			if (!configId.value) {
				ElMessage.warning("请输入配置ID");
				return;
			}
			loading.value = true;
			try {
				versionList.value = await getVersionHistory(configType.value, configId.value);
				if (activeTab.value !== "list") await loadTree();
			} catch (e) {
				ElMessage.error("加载版本历史失败");
			} finally {
				loading.value = false;
			}
		}
		async function loadTree() {
			if (!configId.value) return;
			try {
				versionTree.value = await getVersionTree(configType.value, configId.value);
			} catch (e) {
				versionTree.value = [];
			}
		}
		/** 切换到树形/管道图视图时按需加载版本树 */
		async function onTabChange(tab) {
			if ((tab === "tree" || tab === "pipeline") && versionTree.value.length === 0 && configId.value) await loadTree();
		}
		/** 加载管道图 */
		async function loadPipeline() {
			const codes = pipelineCodesInput.value.split(",").map((s) => s.trim()).filter(Boolean);
			if (codes.length === 0) {
				ElMessage.warning("请输入至少一个配置编码");
				return;
			}
			pipelineCodes.value = codes;
		}
		async function showDiff() {
			if (selectedVersions.value.length !== 2) {
				ElMessage.warning("请选择两个版本进行对比");
				return;
			}
			const [from, to] = [...selectedVersions.value].sort((a, b) => a - b);
			await doDiff(from, to);
		}
		/** 对比指定版本与其前一版本（树形/管道图卡片点击触发） */
		async function diffSingle(version) {
			if (version.version <= 1) {
				ElMessage.warning("首个版本无前置版本可对比");
				return;
			}
			await doDiff(version.version - 1, version.version);
		}
		async function doDiff(from, to) {
			try {
				diffResult.value = await diffVersions(configType.value, configId.value, from, to);
				const fromVersion = versionList.value.find((v) => v.version === from);
				const toVersion = versionList.value.find((v) => v.version === to);
				if (fromVersion && toVersion) try {
					oldSnapshot.value = JSON.parse(fromVersion.snapshot || "{}");
					newSnapshot.value = JSON.parse(toVersion.snapshot || "{}");
				} catch (e) {
					oldSnapshot.value = null;
					newSnapshot.value = null;
				}
			} catch (e) {
				ElMessage.error("Diff 计算失败");
			}
		}
		async function rollback(version) {
			const fullVersion = versionList.value.find((v) => v.version === version.version);
			rollbackTargetVersion.value = version.version;
			rollbackTargetSnapshot.value = (fullVersion === null || fullVersion === void 0 ? void 0 : fullVersion.snapshot) || "";
			rollbackPreviewVisible.value = true;
		}
		/** 回滚预览对话框回滚成功回调 */
		async function onRollbackDone() {
			rollbackPreviewVisible.value = false;
			await loadHistory();
			if (activeTab.value !== "list") await loadTree();
		}
		async function onExport() {
			const codes = exportCodes.value.split(",").map((s) => s.trim()).filter(Boolean);
			if (codes.length === 0) {
				ElMessage.warning("请输入至少一个配置编码");
				return;
			}
			try {
				const blob = await exportPackageZip(codes, exportTargetEnv.value);
				const url = URL.createObjectURL(blob);
				const a = document.createElement("a");
				a.href = url;
				a.download = `lowcode-package-${exportTargetEnv.value}.zip`;
				a.click();
				URL.revokeObjectURL(url);
				exportDialogVisible.value = false;
				ElMessage.success("导出成功");
			} catch (e) {
				ElMessage.error("导出失败");
			}
		}
		async function onImport() {
			if (!importFile.value) {
				ElMessage.warning("请选择文件");
				return;
			}
			importDialogVisible.value = false;
			conflictResolverTargetEnv.value = "TEST";
			conflictResolverVisible.value = true;
		}
		/** 冲突解决器导入完成回调 */
		async function onConflictResolved() {
			conflictResolverVisible.value = false;
			importFile.value = null;
			if (configId.value) await loadHistory();
		}
		function onFileChange(file) {
			importFile.value = file.raw;
		}
		function changeTypeTag(type) {
			if (type === "ADDED") return "success";
			if (type === "REMOVED") return "danger";
			return "warning";
		}
		function changeTypeLabel(type) {
			if (type === "ADDED") return "新增";
			if (type === "REMOVED") return "删除";
			return "修改";
		}
		function handleSelectionChange(rows) {
			selectedVersions.value = rows.map((r) => r.version);
		}
		/** el-tree 配置：子节点字段为 children */
		const treeProps = {
			children: "children",
			label: "version"
		};
		/** 树节点显示文案 */
		function treeNodeLabel(data) {
			const branchSuffix = data.branch && data.branch !== "main" ? ` [${data.branch}]` : "";
			return `v${data.version}${branchSuffix}${data.changeLog ? " · " + data.changeLog : ""}`;
		}
		/** 环境标签颜色 */
		function envTagType(env) {
			if (env === "PROD") return "danger";
			if (env === "TEST") return "warning";
			return "info";
		}
		/** 解析标签字符串为数组 */
		function parseTags(tags) {
			if (!tags) return [];
			return tags.split(",").map((t) => t.trim()).filter(Boolean);
		}
		/** 打开创建分支对话框 */
		function openBranchDialog(version) {
			branchForm.value = {
				baseVersionId: version.versionId || 0,
				branchName: "",
				changeLog: ""
			};
			branchDialogVisible.value = true;
		}
		/** 打开添加标签对话框 */
		function openTagDialog(version) {
			tagForm.value = {
				versionId: version.versionId || 0,
				tag: ""
			};
			tagDialogVisible.value = true;
		}
		/** 提交创建分支 */
		async function submitBranch() {
			if (!branchForm.value.branchName) {
				ElMessage.warning("请输入分支名");
				return;
			}
			if (branchForm.value.branchName === "main") {
				ElMessage.warning("不能使用 main 作为分支名");
				return;
			}
			try {
				await createBranch({
					configType: configType.value,
					configId: configId.value,
					baseVersionId: branchForm.value.baseVersionId,
					branchName: branchForm.value.branchName,
					changeLog: branchForm.value.changeLog
				});
				ElMessage.success("分支创建成功");
				branchDialogVisible.value = false;
				await loadHistory();
				if (activeTab.value !== "list") await loadTree();
			} catch (e) {
				ElMessage.error("分支创建失败");
			}
		}
		/** 提交添加标签 */
		async function submitTag() {
			if (!tagForm.value.tag) {
				ElMessage.warning("请输入标签");
				return;
			}
			try {
				await addTag({
					configType: configType.value,
					configId: configId.value,
					versionId: tagForm.value.versionId,
					tag: tagForm.value.tag
				});
				ElMessage.success("标签添加成功");
				tagDialogVisible.value = false;
				await loadHistory();
				if (activeTab.value !== "list") await loadTree();
			} catch (e) {
				ElMessage.error("标签添加失败");
			}
		}
		const hasDiff = computed(() => diffResult.value && diffResult.value.entries.length > 0);
		return (_ctx, _cache) => {
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _component_el_upload = resolveComponent("el-upload");
			const _component_el_switch = resolveComponent("el-switch");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_tab_pane = resolveComponent("el-tab-pane");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_tree = resolveComponent("el-tree");
			const _component_el_tabs = resolveComponent("el-tabs");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_radio_button = resolveComponent("el-radio-button");
			const _component_el_radio_group = resolveComponent("el-radio-group");
			const _component_el_row = resolveComponent("el-row");
			const _directive_loading = resolveDirective("loading");
			return withDirectives((openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, { shadow: "never" }, {
					header: withCtx(() => [..._cache[26] || (_cache[26] = [createElementVNode("span", null, "版本历史与对比", -1)])]),
					default: withCtx(() => [createVNode(_component_el_form, { inline: "" }, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, { label: "配置类型" }, {
								default: withCtx(() => [createVNode(_component_el_select, {
									modelValue: configType.value,
									"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => configType.value = $event),
									style: { "width": "150px" }
								}, {
									default: withCtx(() => [
										createVNode(_component_el_option, {
											label: "实体",
											value: "ENTITY"
										}),
										createVNode(_component_el_option, {
											label: "表单",
											value: "FORM"
										}),
										createVNode(_component_el_option, {
											label: "列表",
											value: "LIST"
										}),
										createVNode(_component_el_option, {
											label: "微流",
											value: "MICROFLOW"
										}),
										createVNode(_component_el_option, {
											label: "规则",
											value: "RULE"
										}),
										createVNode(_component_el_option, {
											label: "连接器",
											value: "CONNECTOR"
										})
									]),
									_: 1
								}, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "配置ID" }, {
								default: withCtx(() => [createVNode(_component_el_input_number, {
									modelValue: configId.value,
									"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => configId.value = $event),
									min: 1
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, null, {
								default: withCtx(() => [createVNode(_component_el_button, {
									type: "primary",
									onClick: loadHistory
								}, {
									default: withCtx(() => [..._cache[27] || (_cache[27] = [createTextVNode("查询", -1)])]),
									_: 1
								})]),
								_: 1
							})
						]),
						_: 1
					})]),
					_: 1
				}),
				createVNode(_component_el_card, {
					shadow: "never",
					style: { "margin-top": "16px" }
				}, {
					header: withCtx(() => [..._cache[28] || (_cache[28] = [createElementVNode("span", null, "环境晋升", -1)])]),
					default: withCtx(() => [createVNode(_component_el_button, {
						type: "success",
						onClick: _cache[2] || (_cache[2] = ($event) => exportDialogVisible.value = true)
					}, {
						default: withCtx(() => [..._cache[29] || (_cache[29] = [createTextVNode("导出配置包", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "warning",
						onClick: _cache[3] || (_cache[3] = ($event) => importDialogVisible.value = true)
					}, {
						default: withCtx(() => [..._cache[30] || (_cache[30] = [createTextVNode("导入配置包", -1)])]),
						_: 1
					})]),
					_: 1
				}),
				createVNode(_component_el_dialog, {
					modelValue: exportDialogVisible.value,
					"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => exportDialogVisible.value = $event),
					title: "导出配置包",
					width: "500px"
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[6] || (_cache[6] = ($event) => exportDialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[31] || (_cache[31] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						onClick: onExport
					}, {
						default: withCtx(() => [..._cache[32] || (_cache[32] = [createTextVNode("导出", -1)])]),
						_: 1
					})]),
					default: withCtx(() => [createVNode(_component_el_form, { "label-width": "120px" }, {
						default: withCtx(() => [createVNode(_component_el_form_item, { label: "配置编码（逗号分隔）" }, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: exportCodes.value,
								"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => exportCodes.value = $event),
								placeholder: "如 entity_user,entity_role"
							}, null, 8, ["modelValue"])]),
							_: 1
						}), createVNode(_component_el_form_item, { label: "目标环境" }, {
							default: withCtx(() => [createVNode(_component_el_select, {
								modelValue: exportTargetEnv.value,
								"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => exportTargetEnv.value = $event),
								style: { "width": "200px" }
							}, {
								default: withCtx(() => [createVNode(_component_el_option, {
									label: "测试环境",
									value: "TEST"
								}), createVNode(_component_el_option, {
									label: "生产环境",
									value: "PROD"
								})]),
								_: 1
							}, 8, ["modelValue"])]),
							_: 1
						})]),
						_: 1
					})]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: importDialogVisible.value,
					"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => importDialogVisible.value = $event),
					title: "导入配置包",
					width: "500px"
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[9] || (_cache[9] = ($event) => importDialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[34] || (_cache[34] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						onClick: onImport
					}, {
						default: withCtx(() => [..._cache[35] || (_cache[35] = [createTextVNode("导入", -1)])]),
						_: 1
					})]),
					default: withCtx(() => [createVNode(_component_el_form, { "label-width": "120px" }, {
						default: withCtx(() => [createVNode(_component_el_form_item, { label: "选择文件" }, {
							default: withCtx(() => [createVNode(_component_el_upload, {
								"auto-upload": false,
								"on-change": onFileChange,
								limit: 1,
								accept: ".zip,.json"
							}, {
								default: withCtx(() => [createVNode(_component_el_button, { type: "primary" }, {
									default: withCtx(() => [..._cache[33] || (_cache[33] = [createTextVNode("选择文件", -1)])]),
									_: 1
								})]),
								_: 1
							})]),
							_: 1
						}), createVNode(_component_el_form_item, { label: "覆盖已存在" }, {
							default: withCtx(() => [createVNode(_component_el_switch, {
								modelValue: importOverwrite.value,
								"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => importOverwrite.value = $event)
							}, null, 8, ["modelValue"])]),
							_: 1
						})]),
						_: 1
					})]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_row, {
					gutter: 16,
					style: { "margin-top": "16px" }
				}, {
					default: withCtx(() => [createVNode(_component_el_col, { span: 12 }, {
						default: withCtx(() => [createVNode(_component_el_card, { shadow: "never" }, {
							header: withCtx(() => [createElementVNode("div", _hoisted_2, [_cache[37] || (_cache[37] = createElementVNode("span", null, "版本列表", -1)), createVNode(_component_el_button, {
								size: "small",
								onClick: showDiff,
								disabled: selectedVersions.value.length !== 2
							}, {
								default: withCtx(() => [..._cache[36] || (_cache[36] = [createTextVNode(" 对比选中版本 ", -1)])]),
								_: 1
							}, 8, ["disabled"])])]),
							default: withCtx(() => [createVNode(_component_el_tabs, {
								modelValue: activeTab.value,
								"onUpdate:modelValue": _cache[15] || (_cache[15] = ($event) => activeTab.value = $event),
								onTabChange
							}, {
								default: withCtx(() => [
									createVNode(_component_el_tab_pane, {
										label: "列表",
										name: "list"
									}, {
										default: withCtx(() => [createVNode(_component_el_table, {
											data: versionList.value,
											onSelectionChange: handleSelectionChange,
											"row-key": "version",
											size: "small"
										}, {
											default: withCtx(() => [
												createVNode(_component_el_table_column, {
													type: "selection",
													"reserve-selection": true,
													width: "40"
												}),
												createVNode(_component_el_table_column, {
													label: "版本",
													prop: "version",
													width: "60"
												}),
												createVNode(_component_el_table_column, {
													label: "变更说明",
													prop: "changeLog",
													"show-overflow-tooltip": ""
												}),
												createVNode(_component_el_table_column, {
													label: "环境",
													prop: "environment",
													width: "70"
												}, {
													default: withCtx(({ row }) => [createVNode(_component_el_tag, {
														size: "small",
														type: envTagType(row.environment)
													}, {
														default: withCtx(() => [createTextVNode(toDisplayString(row.environment), 1)]),
														_: 2
													}, 1032, ["type"])]),
													_: 1
												}),
												createVNode(_component_el_table_column, {
													label: "操作人",
													prop: "createBy",
													width: "100"
												}),
												createVNode(_component_el_table_column, {
													label: "时间",
													prop: "createTime",
													width: "160"
												}, {
													default: withCtx(({ row }) => {
														var _row$createTime;
														return [createTextVNode(toDisplayString((_row$createTime = row.createTime) === null || _row$createTime === void 0 ? void 0 : _row$createTime.replace("T", " ").slice(0, 16)), 1)];
													}),
													_: 1
												}),
												createVNode(_component_el_table_column, {
													label: "操作",
													width: "80"
												}, {
													default: withCtx(({ row }) => [createVNode(_component_el_button, {
														type: "warning",
														size: "small",
														link: "",
														onClick: ($event) => rollback(row)
													}, {
														default: withCtx(() => [..._cache[38] || (_cache[38] = [createTextVNode("回滚", -1)])]),
														_: 1
													}, 8, ["onClick"])]),
													_: 1
												})
											]),
											_: 1
										}, 8, ["data"])]),
										_: 1
									}),
									createVNode(_component_el_tab_pane, {
										label: "版本树",
										name: "tree"
									}, {
										default: withCtx(() => [versionTree.value.length === 0 ? (openBlock(), createBlock(_component_el_empty, {
											key: 0,
											description: "暂无版本数据",
											"image-size": 60
										})) : (openBlock(), createBlock(_component_el_tree, {
											key: 1,
											data: versionTree.value,
											props: treeProps,
											"node-key": "version",
											"default-expand-all": "",
											"expand-on-click-node": false
										}, {
											default: withCtx(({ data }) => {
												var _data$createTime;
												return [createElementVNode("div", _hoisted_3, [
													createElementVNode("span", _hoisted_4, [createElementVNode("strong", null, toDisplayString(treeNodeLabel(data)), 1)]),
													createVNode(_component_el_tag, {
														size: "small",
														type: envTagType(data.environment),
														class: "tree-node-tag"
													}, {
														default: withCtx(() => [createTextVNode(toDisplayString(data.environment), 1)]),
														_: 2
													}, 1032, ["type"]),
													(openBlock(true), createElementBlock(Fragment, null, renderList(parseTags(data.tags), (t) => {
														return openBlock(), createBlock(_component_el_tag, {
															key: t,
															size: "small",
															type: "success",
															effect: "plain",
															class: "tree-node-tag"
														}, {
															default: withCtx(() => [createTextVNode(toDisplayString(t), 1)]),
															_: 2
														}, 1024);
													}), 128)),
													createElementVNode("span", _hoisted_5, toDisplayString(data.createBy || "-") + " · " + toDisplayString((_data$createTime = data.createTime) === null || _data$createTime === void 0 ? void 0 : _data$createTime.replace("T", " ").slice(0, 16)), 1),
													createElementVNode("span", {
														class: "tree-node-actions",
														onClick: _cache[11] || (_cache[11] = withModifiers(() => {}, ["stop"]))
													}, [
														createVNode(_component_el_button, {
															link: "",
															type: "primary",
															size: "small",
															onClick: ($event) => diffSingle(data)
														}, {
															default: withCtx(() => [..._cache[39] || (_cache[39] = [createTextVNode("对比", -1)])]),
															_: 1
														}, 8, ["onClick"]),
														createVNode(_component_el_button, {
															link: "",
															type: "warning",
															size: "small",
															onClick: ($event) => rollback(data)
														}, {
															default: withCtx(() => [..._cache[40] || (_cache[40] = [createTextVNode("回滚", -1)])]),
															_: 1
														}, 8, ["onClick"]),
														createVNode(_component_el_button, {
															link: "",
															type: "success",
															size: "small",
															onClick: ($event) => openBranchDialog(data)
														}, {
															default: withCtx(() => [..._cache[41] || (_cache[41] = [createTextVNode("分支", -1)])]),
															_: 1
														}, 8, ["onClick"]),
														createVNode(_component_el_button, {
															link: "",
															type: "info",
															size: "small",
															onClick: ($event) => openTagDialog(data)
														}, {
															default: withCtx(() => [..._cache[42] || (_cache[42] = [createTextVNode("标签", -1)])]),
															_: 1
														}, 8, ["onClick"])
													])
												])];
											}),
											_: 1
										}, 8, ["data"]))]),
										_: 1
									}),
									createVNode(_component_el_tab_pane, {
										label: "晋升管道图",
										name: "pipeline"
									}, {
										default: withCtx(() => [createElementVNode("div", _hoisted_6, [createVNode(_component_el_input, {
											modelValue: pipelineCodesInput.value,
											"onUpdate:modelValue": _cache[12] || (_cache[12] = ($event) => pipelineCodesInput.value = $event),
											placeholder: "输入配置编码（逗号分隔），如 entity_user,entity_role",
											style: { "width": "400px" }
										}, null, 8, ["modelValue"]), createVNode(_component_el_button, {
											type: "primary",
											size: "small",
											onClick: loadPipeline,
											style: { "margin-left": "8px" }
										}, {
											default: withCtx(() => [..._cache[43] || (_cache[43] = [createTextVNode(" 加载管道 ", -1)])]),
											_: 1
										})]), createVNode(PromotionPipeline_default, {
											"config-codes": pipelineCodes.value,
											onDiff: _cache[13] || (_cache[13] = (p) => doDiff(p.version - 1, p.version)),
											onPromoted: _cache[14] || (_cache[14] = () => loadHistory())
										}, null, 8, ["config-codes"])]),
										_: 1
									})
								]),
								_: 1
							}, 8, ["modelValue"])]),
							_: 1
						})]),
						_: 1
					}), createVNode(_component_el_col, { span: 12 }, {
						default: withCtx(() => [createVNode(_component_el_card, { shadow: "never" }, {
							header: withCtx(() => [createElementVNode("div", _hoisted_7, [_cache[46] || (_cache[46] = createElementVNode("span", null, "版本差异", -1)), diffResult.value ? (openBlock(), createBlock(_component_el_radio_group, {
								key: 0,
								modelValue: diffMode.value,
								"onUpdate:modelValue": _cache[16] || (_cache[16] = ($event) => diffMode.value = $event),
								size: "small"
							}, {
								default: withCtx(() => [createVNode(_component_el_radio_button, { value: "tree" }, {
									default: withCtx(() => [..._cache[44] || (_cache[44] = [createTextVNode("树形视图", -1)])]),
									_: 1
								}), createVNode(_component_el_radio_button, { value: "flat" }, {
									default: withCtx(() => [..._cache[45] || (_cache[45] = [createTextVNode("扁平表格", -1)])]),
									_: 1
								})]),
								_: 1
							}, 8, ["modelValue"])) : createCommentVNode("", true)])]),
							default: withCtx(() => [!diffResult.value ? (openBlock(), createBlock(_component_el_empty, {
								key: 0,
								description: "请选择两个版本进行对比",
								"image-size": 60
							})) : !hasDiff.value ? (openBlock(), createElementBlock("div", _hoisted_8, " 两个版本无差异 ")) : diffMode.value === "tree" ? (openBlock(), createBlock(JsonTreeDiff_default, {
								key: 2,
								"old-data": oldSnapshot.value,
								"new-data": newSnapshot.value
							}, null, 8, ["old-data", "new-data"])) : (openBlock(), createBlock(_component_el_table, {
								key: 3,
								data: diffResult.value.entries,
								size: "small",
								border: ""
							}, {
								default: withCtx(() => [
									createVNode(_component_el_table_column, {
										label: "类型",
										prop: "changeType",
										width: "80"
									}, {
										default: withCtx(({ row }) => [createVNode(_component_el_tag, {
											size: "small",
											type: changeTypeTag(row.changeType)
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(changeTypeLabel(row.changeType)), 1)]),
											_: 2
										}, 1032, ["type"])]),
										_: 1
									}),
									createVNode(_component_el_table_column, {
										label: "字段路径",
										prop: "fieldPath"
									}),
									createVNode(_component_el_table_column, {
										label: "旧值",
										prop: "oldValue",
										"show-overflow-tooltip": ""
									}),
									createVNode(_component_el_table_column, {
										label: "新值",
										prop: "newValue",
										"show-overflow-tooltip": ""
									})
								]),
								_: 1
							}, 8, ["data"]))]),
							_: 1
						})]),
						_: 1
					})]),
					_: 1
				}),
				createVNode(_component_el_dialog, {
					modelValue: branchDialogVisible.value,
					"onUpdate:modelValue": _cache[20] || (_cache[20] = ($event) => branchDialogVisible.value = $event),
					title: "创建分支",
					width: "500px"
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[19] || (_cache[19] = ($event) => branchDialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[47] || (_cache[47] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						onClick: submitBranch
					}, {
						default: withCtx(() => [..._cache[48] || (_cache[48] = [createTextVNode("创建", -1)])]),
						_: 1
					})]),
					default: withCtx(() => [createVNode(_component_el_form, { "label-width": "120px" }, {
						default: withCtx(() => [createVNode(_component_el_form_item, { label: "分支名" }, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: branchForm.value.branchName,
								"onUpdate:modelValue": _cache[17] || (_cache[17] = ($event) => branchForm.value.branchName = $event),
								placeholder: "如 hotfix-v1.2、feature-x"
							}, null, 8, ["modelValue"])]),
							_: 1
						}), createVNode(_component_el_form_item, { label: "变更说明" }, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: branchForm.value.changeLog,
								"onUpdate:modelValue": _cache[18] || (_cache[18] = ($event) => branchForm.value.changeLog = $event),
								type: "textarea",
								rows: 2
							}, null, 8, ["modelValue"])]),
							_: 1
						})]),
						_: 1
					})]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: tagDialogVisible.value,
					"onUpdate:modelValue": _cache[23] || (_cache[23] = ($event) => tagDialogVisible.value = $event),
					title: "添加标签",
					width: "500px"
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[22] || (_cache[22] = ($event) => tagDialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[49] || (_cache[49] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						onClick: submitTag
					}, {
						default: withCtx(() => [..._cache[50] || (_cache[50] = [createTextVNode("添加", -1)])]),
						_: 1
					})]),
					default: withCtx(() => [createVNode(_component_el_form, { "label-width": "120px" }, {
						default: withCtx(() => [createVNode(_component_el_form_item, { label: "标签" }, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: tagForm.value.tag,
								"onUpdate:modelValue": _cache[21] || (_cache[21] = ($event) => tagForm.value.tag = $event),
								placeholder: "如 v1.0-release、审核通过"
							}, null, 8, ["modelValue"])]),
							_: 1
						})]),
						_: 1
					})]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(ImportConflictResolver_default, {
					visible: conflictResolverVisible.value,
					"onUpdate:visible": _cache[24] || (_cache[24] = ($event) => conflictResolverVisible.value = $event),
					file: importFile.value,
					"target-environment": conflictResolverTargetEnv.value,
					onImported: onConflictResolved
				}, null, 8, [
					"visible",
					"file",
					"target-environment"
				]),
				createVNode(RollbackPreviewDialog_default, {
					visible: rollbackPreviewVisible.value,
					"onUpdate:visible": _cache[25] || (_cache[25] = ($event) => rollbackPreviewVisible.value = $event),
					"config-type": configType.value,
					"config-id": configId.value || 0,
					"target-version": rollbackTargetVersion.value,
					"target-snapshot": rollbackTargetSnapshot.value,
					"version-list": versionList.value,
					onRolled: onRollbackDone
				}, null, 8, [
					"visible",
					"config-type",
					"config-id",
					"target-version",
					"target-snapshot",
					"version-list"
				])
			])), [[_directive_loading, loading.value]]);
		};
	}
}), [["__scopeId", "data-v-0752b58c"]]);
//#endregion
export { version_history_default as default };

//# sourceMappingURL=version-history-BSzpDZ3s.js.map