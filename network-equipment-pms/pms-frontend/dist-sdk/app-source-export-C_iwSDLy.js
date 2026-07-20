import { f as axios, r as get } from "./request-BQrAOfxW.js";
import { n as triggerBlobDownload } from "./excel-BtLU3Vmp.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives } from "vue";
//#region src/api/lowcode-app-source.ts
/** 查询可导出的应用列表（按 bizType 去重） */
function listApps() {
	return get("/api/lowcode/app-source/apps");
}
/**
* 预览导出清单（不生成 ZIP）。
*
* @param bizType 业务类型（应用分组键），为空时预览全部
*/
function previewManifest(bizType) {
	return get("/api/lowcode/app-source/manifest", { bizType });
}
/**
* 导出应用源码（ZIP）并以 Blob 形式返回。
*
* <p>该接口返回二进制流（application/zip，非统一 envelope），故绕过统一的
* axios 响应拦截器，直接使用原始 axios 注入 token 并以 blob 形式接收。</p>
*
* @param bizType 业务类型（应用分组键），为空时导出全部
*/
async function exportAsZip(bizType) {
	const token = localStorage.getItem("pms_token") || "";
	return (await axios.get("/api/lowcode/app-source/export", {
		params: { bizType },
		responseType: "blob",
		headers: { Authorization: `Bearer ${token}` }
	})).data;
}
/**
* 导出应用源码（ZIP）并直接触发浏览器下载。
*
* @param bizType 业务类型（应用分组键），为空时导出全部
*/
async function exportAndDownload(bizType) {
	triggerBlobDownload(await exportAsZip(bizType), `lowcode-app${bizType ? `-${bizType}` : "-all"}.zip`);
}
//#endregion
//#region src/views/lowcode/app-source-export/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { style: { "padding": "16px" } };
var _hoisted_2 = { class: "action-bar" };
var _hoisted_3 = { class: "header-extra" };
var _hoisted_4 = { class: "header-extra" };
var _hoisted_5 = {
	key: 0,
	class: "tag-cloud"
};
var _hoisted_6 = { class: "guide-pre" };
//#endregion
//#region src/views/lowcode/app-source-export/index.vue
var app_source_export_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "LowcodeAppSourceExportView",
	__name: "index",
	setup(__props) {
		/**
		* 应用源码导出 UI（批次5-T10 前端）。
		*
		* <p>借鉴网易轻舟源码导出 — 无黑盒引擎。前端提供应用选择 + 清单预览 + ZIP 下载：
		* 顶部应用选择器（含「全部应用」选项）+ 预览/导出按钮；中部以 el-descriptions
		* 展示清单元数据、el-table 展示配置数量、el-tag 云展示实体表；底部以 `<pre>`
		* 展示部署指南。导出 ZIP 时取 Blob → ObjectURL → 触发浏览器下载。</p>
		*/
		const apps = ref([]);
		/** 选中的应用 bizType；空串表示「全部应用」 */
		const selectedBizType = ref("");
		const manifest = ref(null);
		const loading = ref(false);
		const exporting = ref(false);
		/** 配置数量表格行（由 configCounts Map 转换） */
		const configCountRows = computed(() => {
			var _manifest$value;
			const map = (_manifest$value = manifest.value) === null || _manifest$value === void 0 ? void 0 : _manifest$value.configCounts;
			if (!map) return [];
			return Object.entries(map).map(([type, count]) => ({
				type,
				count
			}));
		});
		const entityTables = computed(() => {
			var _manifest$value$entit, _manifest$value2;
			return (_manifest$value$entit = (_manifest$value2 = manifest.value) === null || _manifest$value2 === void 0 ? void 0 : _manifest$value2.entityTables) !== null && _manifest$value$entit !== void 0 ? _manifest$value$entit : [];
		});
		const totalConfigCount = computed(() => configCountRows.value.reduce((a, r) => a + r.count, 0));
		async function loadApps() {
			try {
				apps.value = await listApps();
			} catch (e) {
				ElMessage.error("加载应用列表失败：" + (e instanceof Error ? e.message : String(e)));
				apps.value = [];
			}
		}
		async function preview() {
			loading.value = true;
			try {
				manifest.value = await previewManifest(selectedBizType.value || void 0);
				ElMessage.success("清单预览已加载");
			} catch (e) {
				ElMessage.error("预览清单失败：" + (e instanceof Error ? e.message : String(e)));
				manifest.value = null;
			} finally {
				loading.value = false;
			}
		}
		async function doExport() {
			exporting.value = true;
			try {
				await exportAndDownload(selectedBizType.value || void 0);
				ElMessage.success("导出成功，已开始下载 ZIP");
			} catch (e) {
				ElMessage.error("导出失败：" + (e instanceof Error ? e.message : String(e)));
			} finally {
				exporting.value = false;
			}
		}
		function formatTime(t) {
			if (!t) return "—";
			return t.replace("T", " ").slice(0, 19);
		}
		onMounted(() => {
			loadApps();
		});
		return (_ctx, _cache) => {
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_descriptions_item = resolveComponent("el-descriptions-item");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_descriptions = resolveComponent("el-descriptions");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_row = resolveComponent("el-row");
			const _directive_loading = resolveDirective("loading");
			return withDirectives((openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, {
					shadow: "never",
					style: { "margin-bottom": "16px" }
				}, {
					default: withCtx(() => [createElementVNode("div", _hoisted_2, [
						_cache[3] || (_cache[3] = createElementVNode("span", { class: "label" }, "选择应用：", -1)),
						createVNode(_component_el_select, {
							modelValue: selectedBizType.value,
							"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => selectedBizType.value = $event),
							placeholder: "选择要导出的应用",
							clearable: "",
							style: { "width": "280px" }
						}, {
							default: withCtx(() => [createVNode(_component_el_option, {
								label: "全部应用（bizType=null）",
								value: ""
							}), (openBlock(true), createElementBlock(Fragment, null, renderList(apps.value, (app) => {
								return openBlock(), createBlock(_component_el_option, {
									key: app,
									label: app,
									value: app
								}, null, 8, ["label", "value"]);
							}), 128))]),
							_: 1
						}, 8, ["modelValue"]),
						createVNode(_component_el_button, {
							type: "primary",
							plain: "",
							loading: loading.value,
							onClick: preview
						}, {
							default: withCtx(() => [..._cache[1] || (_cache[1] = [createTextVNode(" 预览清单 ", -1)])]),
							_: 1
						}, 8, ["loading"]),
						createVNode(_component_el_button, {
							type: "success",
							loading: exporting.value,
							onClick: doExport
						}, {
							default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode(" 导出 ZIP ", -1)])]),
							_: 1
						}, 8, ["loading"]),
						_cache[4] || (_cache[4] = createElementVNode("span", { class: "hint" }, "导出包含 JSON + DDL + POM + README，可独立部署（无黑盒引擎）", -1))
					])]),
					_: 1
				}),
				createVNode(_component_el_card, {
					shadow: "never",
					style: { "margin-bottom": "16px" }
				}, {
					header: withCtx(() => [..._cache[5] || (_cache[5] = [createElementVNode("span", null, "导出清单", -1)])]),
					default: withCtx(() => [!manifest.value ? (openBlock(), createBlock(_component_el_empty, {
						key: 0,
						description: "尚未预览清单，请选择应用后点击「预览清单」"
					})) : (openBlock(), createBlock(_component_el_descriptions, {
						key: 1,
						column: 2,
						border: "",
						size: "small"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_descriptions_item, { label: "清单版本" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(manifest.value.manifestVersion || "—"), 1)]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "应用编码" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(manifest.value.appCode || "—"), 1)]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "应用名称" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(manifest.value.appName || "—"), 1)]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "源系统" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(manifest.value.sourceSystem || "—"), 1)]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "导出人" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(manifest.value.exportBy || "—"), 1)]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "导出时间" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(formatTime(manifest.value.exportTime)), 1)]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "平台版本" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(manifest.value.platformVersion || "—"), 1)]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "凭据脱敏" }, {
								default: withCtx(() => [createVNode(_component_el_tag, {
									type: manifest.value.credentialsRedacted ? "success" : "warning",
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(manifest.value.credentialsRedacted ? "已脱敏" : "未脱敏"), 1)]),
									_: 1
								}, 8, ["type"])]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, {
								label: "描述",
								span: 2
							}, {
								default: withCtx(() => [createTextVNode(toDisplayString(manifest.value.description || "—"), 1)]),
								_: 1
							})
						]),
						_: 1
					}))]),
					_: 1
				}),
				manifest.value ? (openBlock(), createBlock(_component_el_row, {
					key: 0,
					gutter: 16
				}, {
					default: withCtx(() => [createVNode(_component_el_col, {
						xs: 24,
						md: 12
					}, {
						default: withCtx(() => [createVNode(_component_el_card, {
							shadow: "never",
							class: "section-card"
						}, {
							header: withCtx(() => [_cache[6] || (_cache[6] = createElementVNode("span", null, "配置数量统计", -1)), createElementVNode("span", _hoisted_3, "共 " + toDisplayString(totalConfigCount.value) + " 项", 1)]),
							default: withCtx(() => [createVNode(_component_el_table, {
								data: configCountRows.value,
								size: "small",
								"max-height": "320",
								"empty-text": "暂无配置"
							}, {
								default: withCtx(() => [createVNode(_component_el_table_column, {
									label: "配置类型",
									prop: "type",
									"min-width": "180"
								}), createVNode(_component_el_table_column, {
									label: "数量",
									prop: "count",
									width: "120",
									align: "right"
								})]),
								_: 1
							}, 8, ["data"])]),
							_: 1
						})]),
						_: 1
					}), createVNode(_component_el_col, {
						xs: 24,
						md: 12
					}, {
						default: withCtx(() => [createVNode(_component_el_card, {
							shadow: "never",
							class: "section-card"
						}, {
							header: withCtx(() => [_cache[7] || (_cache[7] = createElementVNode("span", null, "实体表列表", -1)), createElementVNode("span", _hoisted_4, "共 " + toDisplayString(entityTables.value.length) + " 张", 1)]),
							default: withCtx(() => [entityTables.value.length > 0 ? (openBlock(), createElementBlock("div", _hoisted_5, [(openBlock(true), createElementBlock(Fragment, null, renderList(entityTables.value, (t) => {
								return openBlock(), createBlock(_component_el_tag, {
									key: t,
									size: "small",
									effect: "plain",
									class: "entity-tag"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(t), 1)]),
									_: 2
								}, 1024);
							}), 128))])) : (openBlock(), createBlock(_component_el_empty, {
								key: 1,
								description: "暂无实体表",
								"image-size": 60
							}))]),
							_: 1
						})]),
						_: 1
					})]),
					_: 1
				})) : createCommentVNode("", true),
				manifest.value && manifest.value.deploymentGuide ? (openBlock(), createBlock(_component_el_card, {
					key: 1,
					shadow: "never",
					class: "section-card"
				}, {
					header: withCtx(() => [..._cache[8] || (_cache[8] = [createElementVNode("span", null, "部署指南", -1)])]),
					default: withCtx(() => [createElementVNode("pre", _hoisted_6, toDisplayString(manifest.value.deploymentGuide), 1)]),
					_: 1
				})) : createCommentVNode("", true)
			])), [[_directive_loading, loading.value]]);
		};
	}
}), [["__scopeId", "data-v-40cd1ff4"]]);
//#endregion
export { app_source_export_default as default };

//# sourceMappingURL=app-source-export-C_iwSDLy.js.map