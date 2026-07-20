import { i as post, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, ref, renderList, resolveComponent, resolveDirective, toDisplayString, unref, watch, withCtx, withDirectives, withKeys } from "vue";
//#region src/api/lowcode-template.ts
/** 市场浏览（仅已发布，支持关键词/类型/分类过滤） */
function marketplace(keyword, configType, category) {
	return get("/api/lowcode/config-template/marketplace", {
		keyword,
		configType,
		category
	});
}
/** 模板详情（按 ID） */
function getById(id) {
	return get(`/api/lowcode/config-template/${id}`);
}
/** 上架模板 */
function publish(id) {
	return post(`/api/lowcode/config-template/${id}/publish`);
}
/** 下架模板 */
function unpublish(id) {
	return post(`/api/lowcode/config-template/${id}/unpublish`);
}
/** 归档模板 */
function archive(id) {
	return post(`/api/lowcode/config-template/${id}/archive`);
}
/**
* 下载模板（增加下载计数，应用参数化替换后返回配置）。
*
* @param id   模板 ID
* @param params 参数化替换键值（对应 parameters 定义），为空时直接返回原始配置
*/
function download(id, params) {
	return post(`/api/lowcode/config-template/${id}/download`, params !== null && params !== void 0 ? params : {});
}
/** 评分（更新平均评分与评分数，rating 范围 0-5） */
function rate(id, rating) {
	return post(`/api/lowcode/config-template/${id}/rate`, { rating });
}
/** 查询某 code 的所有版本（按 version desc） */
function listVersions(code) {
	return get(`/api/lowcode/config-template/versions/${code}`);
}
/** 安全解析 parameters JSON 字符串为参数数组（容错：空串/非法 JSON 返回空数组） */
function parseParameters(parametersJson) {
	if (!parametersJson) return [];
	try {
		const parsed = JSON.parse(parametersJson);
		if (!Array.isArray(parsed)) return [];
		return parsed;
	} catch (_unused) {
		return [];
	}
}
/** 安全解析 configJson 为格式化字符串（用于 `<pre>` 展示） */
function formatConfigJson(configJson) {
	if (!configJson) return "";
	try {
		return JSON.stringify(JSON.parse(configJson), null, 2);
	} catch (_unused2) {
		return configJson;
	}
}
/** 将 tags 字符串拆分为数组 */
function splitTags(tags) {
	if (!tags) return [];
	return tags.split(/[,，]/).map((t) => t.trim()).filter(Boolean);
}
//#endregion
//#region src/views/lowcode/template-market/DetailDialog.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$1 = { key: 0 };
var _hoisted_2$1 = {
	key: 0,
	class: "block"
};
var _hoisted_3$1 = { class: "block" };
var _hoisted_4$1 = { class: "block-title" };
var _hoisted_5$1 = {
	key: 0,
	class: "block-hint"
};
var _hoisted_6$1 = { class: "block" };
var _hoisted_7$1 = { class: "block" };
var _hoisted_8$1 = {
	key: 0,
	class: "json-pre"
};
var _hoisted_9$1 = { class: "block" };
//#endregion
//#region src/views/lowcode/template-market/DetailDialog.vue
var DetailDialog_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "DetailDialog",
	props: {
		modelValue: { type: Boolean },
		templateId: {}
	},
	emits: ["update:modelValue", "changed"],
	setup(__props, { emit: __emit }) {
		/**
		* 模板市场详情对话框。
		*
		* <p>展示完整模板信息 + configJson 预览（`<pre>`）+ 版本列表 + 参数化下载表单
		* （按 parameters JSON 动态生成输入框）。支持直接评分与参数化下载。</p>
		*/
		const props = __props;
		const emit = __emit;
		const visible = computed({
			get: () => props.modelValue,
			set: (v) => emit("update:modelValue", v)
		});
		const loading = ref(false);
		const template = ref(null);
		const versions = ref([]);
		const downloading = ref(false);
		const ratingValue = ref(0);
		const ratingSaving = ref(false);
		/** 参数化下载表单值（key → value） */
		const paramValues = ref({});
		const parameters = computed(() => {
			var _template$value;
			return parseParameters((_template$value = template.value) === null || _template$value === void 0 ? void 0 : _template$value.parameters);
		});
		const configJsonPretty = computed(() => {
			var _template$value2;
			return formatConfigJson((_template$value2 = template.value) === null || _template$value2 === void 0 ? void 0 : _template$value2.configJson);
		});
		const tags = computed(() => {
			var _template$value3;
			return splitTags((_template$value3 = template.value) === null || _template$value3 === void 0 ? void 0 : _template$value3.tags);
		});
		watch(() => props.templateId, async (id) => {
			if (!id) {
				template.value = null;
				versions.value = [];
				return;
			}
			loading.value = true;
			try {
				const detail = await getById(id);
				template.value = detail;
				ratingValue.value = detail.rating ? Number(detail.rating) : 0;
				const initVals = {};
				for (const p of parseParameters(detail.parameters)) {
					var _p$defaultValue;
					initVals[p.key] = (_p$defaultValue = p.defaultValue) !== null && _p$defaultValue !== void 0 ? _p$defaultValue : "";
				}
				paramValues.value = initVals;
				versions.value = detail.code ? await listVersions(detail.code) : [];
			} catch (e) {
				ElMessage.error("加载详情失败：" + (e instanceof Error ? e.message : String(e)));
				template.value = null;
				versions.value = [];
			} finally {
				loading.value = false;
			}
		});
		function validateParams() {
			for (const p of parameters.value) {
				var _paramValues$value$p$;
				if (p.required && !((_paramValues$value$p$ = paramValues.value[p.key]) === null || _paramValues$value$p$ === void 0 ? void 0 : _paramValues$value$p$.trim())) {
					ElMessage.warning(`请填写参数：${p.label || p.key}`);
					return false;
				}
			}
			return true;
		}
		async function doDownload() {
			var _template$value4;
			if (!((_template$value4 = template.value) === null || _template$value4 === void 0 ? void 0 : _template$value4.id)) return;
			if (!validateParams()) return;
			downloading.value = true;
			try {
				const params = {};
				for (const p of parameters.value) {
					const raw = paramValues.value[p.key];
					if (raw === void 0 || raw === "") continue;
					if (p.type === "number") {
						const n = Number(raw);
						params[p.key] = Number.isNaN(n) ? raw : n;
					} else if (p.type === "boolean") params[p.key] = raw === "true" || raw === "1";
					else params[p.key] = raw;
				}
				const result = await download(template.value.id, params);
				ElMessage.success("下载成功，已应用参数化替换");
				const text = formatConfigJson(result.configJson);
				if (text) try {
					await navigator.clipboard.writeText(text);
					ElMessage.info("配置 JSON 已复制到剪贴板");
				} catch (_unused) {}
				emit("changed");
			} catch (e) {
				ElMessage.error("下载失败：" + (e instanceof Error ? e.message : String(e)));
			} finally {
				downloading.value = false;
			}
		}
		async function doRate() {
			var _template$value5;
			if (!((_template$value5 = template.value) === null || _template$value5 === void 0 ? void 0 : _template$value5.id)) return;
			if (!ratingValue.value || ratingValue.value < 1) {
				ElMessage.warning("请选择评分（1-5 星）");
				return;
			}
			ratingSaving.value = true;
			try {
				const updated = await rate(template.value.id, ratingValue.value);
				template.value = updated;
				ElMessage.success("评分成功");
				emit("changed");
			} catch (e) {
				ElMessage.error("评分失败：" + (e instanceof Error ? e.message : String(e)));
			} finally {
				ratingSaving.value = false;
			}
		}
		function paramInputType(p) {
			if (p.type === "number") return "number";
			return "text";
		}
		return (_ctx, _cache) => {
			const _component_el_descriptions_item = resolveComponent("el-descriptions-item");
			const _component_el_rate = resolveComponent("el-rate");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_descriptions = resolveComponent("el-descriptions");
			const _component_el_image = resolveComponent("el-image");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_switch = resolveComponent("el-switch");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createBlock(_component_el_dialog, {
				modelValue: visible.value,
				"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => visible.value = $event),
				title: "模板详情",
				width: "820px",
				"close-on-click-modal": false,
				"destroy-on-close": ""
			}, {
				footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[1] || (_cache[1] = ($event) => visible.value = false) }, {
					default: withCtx(() => [..._cache[10] || (_cache[10] = [createTextVNode("关闭", -1)])]),
					_: 1
				})]),
				default: withCtx(() => [withDirectives((openBlock(), createElementBlock("div", null, [template.value ? (openBlock(), createElementBlock(Fragment, { key: 0 }, [
					createVNode(_component_el_descriptions, {
						column: 2,
						border: "",
						size: "small"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_descriptions_item, { label: "名称" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(template.value.name), 1)]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "编码" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(template.value.code), 1)]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "配置类型" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(template.value.configType), 1)]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "分类" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(template.value.category || "—"), 1)]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "作者" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(template.value.author || "—"), 1)]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "版本" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(template.value.version || "—"), 1)]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "下载量" }, {
								default: withCtx(() => {
									var _template$value$downl;
									return [createTextVNode(toDisplayString((_template$value$downl = template.value.downloadCount) !== null && _template$value$downl !== void 0 ? _template$value$downl : 0), 1)];
								}),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "评分" }, {
								default: withCtx(() => {
									var _template$value$ratin, _template$value$ratin2;
									return [createVNode(_component_el_rate, {
										"model-value": Number(template.value.rating) || 0,
										disabled: "",
										"show-score": "",
										"score-template": `${(_template$value$ratin = template.value.rating) !== null && _template$value$ratin !== void 0 ? _template$value$ratin : 0} (${(_template$value$ratin2 = template.value.ratingCount) !== null && _template$value$ratin2 !== void 0 ? _template$value$ratin2 : 0})`
									}, null, 8, ["model-value", "score-template"])];
								}),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "状态" }, {
								default: withCtx(() => [template.value.status ? (openBlock(), createBlock(_component_el_tag, {
									key: 0,
									type: template.value.status === "PUBLISHED" ? "success" : template.value.status === "ARCHIVED" ? "info" : "warning",
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(template.value.status), 1)]),
									_: 1
								}, 8, ["type"])) : createCommentVNode("", true)]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, { label: "标签" }, {
								default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(tags.value, (t) => {
									return openBlock(), createBlock(_component_el_tag, {
										key: t,
										size: "small",
										style: { "margin-right": "4px" }
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(t), 1)]),
										_: 2
									}, 1024);
								}), 128)), tags.value.length === 0 ? (openBlock(), createElementBlock("span", _hoisted_1$1, "—")) : createCommentVNode("", true)]),
								_: 1
							}),
							createVNode(_component_el_descriptions_item, {
								label: "描述",
								span: 2
							}, {
								default: withCtx(() => [createTextVNode(toDisplayString(template.value.description || "—"), 1)]),
								_: 1
							})
						]),
						_: 1
					}),
					template.value.thumbnail ? (openBlock(), createElementBlock("div", _hoisted_2$1, [_cache[3] || (_cache[3] = createElementVNode("div", { class: "block-title" }, "缩略图", -1)), createVNode(_component_el_image, {
						src: template.value.thumbnail,
						fit: "contain",
						style: {
							"max-height": "180px",
							"border-radius": "4px",
							"border": "1px solid var(--el-border-color-lighter)"
						}
					}, null, 8, ["src"])])) : createCommentVNode("", true),
					createElementVNode("div", _hoisted_3$1, [
						createElementVNode("div", _hoisted_4$1, [_cache[4] || (_cache[4] = createTextVNode(" 参数化下载 ", -1)), parameters.value.length === 0 ? (openBlock(), createElementBlock("span", _hoisted_5$1, "（该模板无可配置参数，直接下载即可）")) : createCommentVNode("", true)]),
						parameters.value.length > 0 ? (openBlock(), createBlock(_component_el_form, {
							key: 0,
							"label-width": "140px",
							size: "small"
						}, {
							default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(parameters.value, (p) => {
								return openBlock(), createBlock(_component_el_form_item, {
									key: p.key,
									label: p.label || p.key,
									required: !!p.required
								}, {
									default: withCtx(() => [p.type === "select" && p.options && p.options.length > 0 ? (openBlock(), createBlock(_component_el_select, {
										key: 0,
										modelValue: paramValues.value[p.key],
										"onUpdate:modelValue": ($event) => paramValues.value[p.key] = $event,
										placeholder: `请选择 ${p.label || p.key}`,
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(p.options, (o) => {
											return openBlock(), createBlock(_component_el_option, {
												key: o,
												label: o,
												value: o
											}, null, 8, ["label", "value"]);
										}), 128))]),
										_: 2
									}, 1032, [
										"modelValue",
										"onUpdate:modelValue",
										"placeholder"
									])) : p.type === "boolean" ? (openBlock(), createBlock(_component_el_switch, {
										key: 1,
										"model-value": paramValues.value[p.key] === "true",
										"onUpdate:modelValue": ($event) => paramValues.value[p.key] = $event ? "true" : "false"
									}, null, 8, ["model-value", "onUpdate:modelValue"])) : (openBlock(), createBlock(_component_el_input, {
										key: 2,
										modelValue: paramValues.value[p.key],
										"onUpdate:modelValue": ($event) => paramValues.value[p.key] = $event,
										type: paramInputType(p),
										placeholder: `请输入 ${p.label || p.key}`
									}, null, 8, [
										"modelValue",
										"onUpdate:modelValue",
										"type",
										"placeholder"
									]))]),
									_: 2
								}, 1032, ["label", "required"]);
							}), 128))]),
							_: 1
						})) : createCommentVNode("", true),
						createVNode(_component_el_button, {
							type: "primary",
							loading: downloading.value,
							onClick: doDownload
						}, {
							default: withCtx(() => [..._cache[5] || (_cache[5] = [createTextVNode("下载（应用参数替换）", -1)])]),
							_: 1
						}, 8, ["loading"])
					]),
					createElementVNode("div", _hoisted_6$1, [
						_cache[7] || (_cache[7] = createElementVNode("div", { class: "block-title" }, "为该模板评分", -1)),
						createVNode(_component_el_rate, {
							modelValue: ratingValue.value,
							"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => ratingValue.value = $event),
							max: 5,
							"allow-half": ""
						}, null, 8, ["modelValue"]),
						createVNode(_component_el_button, {
							type: "primary",
							plain: "",
							size: "small",
							style: { "margin-left": "12px" },
							loading: ratingSaving.value,
							onClick: doRate
						}, {
							default: withCtx(() => [..._cache[6] || (_cache[6] = [createTextVNode(" 提交评分 ", -1)])]),
							_: 1
						}, 8, ["loading"])
					]),
					createElementVNode("div", _hoisted_7$1, [_cache[8] || (_cache[8] = createElementVNode("div", { class: "block-title" }, "配置 JSON 预览", -1)), configJsonPretty.value ? (openBlock(), createElementBlock("pre", _hoisted_8$1, toDisplayString(configJsonPretty.value), 1)) : (openBlock(), createBlock(_component_el_empty, {
						key: 1,
						description: "暂无配置 JSON",
						"image-size": 60
					}))]),
					createElementVNode("div", _hoisted_9$1, [_cache[9] || (_cache[9] = createElementVNode("div", { class: "block-title" }, "版本历史", -1)), createVNode(_component_el_table, {
						data: versions.value,
						size: "small",
						"max-height": "240",
						"empty-text": "暂无版本记录"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_table_column, {
								label: "版本",
								prop: "version",
								width: "120"
							}),
							createVNode(_component_el_table_column, {
								label: "状态",
								width: "120"
							}, {
								default: withCtx(({ row }) => [row.status ? (openBlock(), createBlock(_component_el_tag, {
									key: 0,
									type: row.status === "PUBLISHED" ? "success" : row.status === "ARCHIVED" ? "info" : "warning",
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(row.status), 1)]),
									_: 2
								}, 1032, ["type"])) : createCommentVNode("", true)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "下载量",
								prop: "downloadCount",
								width: "100"
							}),
							createVNode(_component_el_table_column, {
								label: "更新时间",
								prop: "updateTime",
								"min-width": "160"
							}, {
								default: withCtx(({ row }) => [createTextVNode(toDisplayString(row.updateTime || "—"), 1)]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["data"])])
				], 64)) : !loading.value ? (openBlock(), createBlock(_component_el_empty, {
					key: 1,
					description: "未加载到模板信息"
				})) : createCommentVNode("", true)])), [[_directive_loading, loading.value]])]),
				_: 1
			}, 8, ["modelValue"]);
		};
	}
}), [["__scopeId", "data-v-433145f2"]]);
//#endregion
//#region src/views/lowcode/template-market/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { style: { "padding": "16px" } };
var _hoisted_2 = { class: "filter-bar" };
var _hoisted_3 = { class: "filter-summary" };
var _hoisted_4 = { class: "thumb-wrap" };
var _hoisted_5 = {
	key: 1,
	class: "thumb-placeholder"
};
var _hoisted_6 = { class: "title-line" };
var _hoisted_7 = ["title"];
var _hoisted_8 = { class: "author-line" };
var _hoisted_9 = { class: "tags-line" };
var _hoisted_10 = {
	key: 0,
	class: "muted"
};
var _hoisted_11 = { class: "stats-line" };
var _hoisted_12 = { class: "stat-item" };
var _hoisted_13 = { class: "stat-item version" };
var _hoisted_14 = { class: "actions" };
//#endregion
//#region src/views/lowcode/template-market/index.vue
var template_market_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "LowcodeTemplateMarketView",
	__name: "index",
	setup(__props) {
		/**
		* 低代码配置模板市场（批次5-T8 前端）。
		*
		* <p>顶部筛选区（关键字搜索 + configType 下拉 + category 下拉）+ 模板卡片网格
		* （每行 4 个 el-card）。卡片展示缩略图、名称、作者、tags、下载量、评分
		* （el-rate 只读）、版本、状态徽章，并提供详情/下载/评分/上架/下架操作。</p>
		*
		* <p>借鉴 Zoho 模板市场 / Appsmith 模板 / Mendix App Store。</p>
		*/
		/** 配置类型选项（与后端 configType 枚举对齐） */
		const configTypeOptions = [
			{
				label: "表单 FORM",
				value: "FORM"
			},
			{
				label: "列表 LIST",
				value: "LIST"
			},
			{
				label: "实体 ENTITY",
				value: "ENTITY"
			},
			{
				label: "微流 MICROFLOW",
				value: "MICROFLOW"
			},
			{
				label: "连接器 CONNECTOR",
				value: "CONNECTOR"
			},
			{
				label: "规则 RULE",
				value: "RULE"
			},
			{
				label: "标签页 TAB",
				value: "TAB"
			},
			{
				label: "关联页 RELATED_PAGE",
				value: "RELATED_PAGE"
			}
		];
		/** 分类候选（取自已加载模板的去重值） */
		const categoryOptions = computed(() => {
			const set = /* @__PURE__ */ new Set();
			list.value.forEach((t) => {
				if (t.category) set.add(t.category);
			});
			return Array.from(set).map((c) => ({
				label: c,
				value: c
			}));
		});
		const list = ref([]);
		const loading = ref(false);
		/** 筛选条件 */
		const keyword = ref("");
		const filterConfigType = ref("");
		const filterCategory = ref("");
		/** 详情对话框 */
		const detailVisible = ref(false);
		const detailId = ref(null);
		function statusTagType(status) {
			if (status === "PUBLISHED") return "success";
			if (status === "ARCHIVED") return "info";
			if (status === "DRAFT") return "warning";
			return "";
		}
		function ratingNumber(t) {
			const n = Number(t.rating);
			return Number.isNaN(n) ? 0 : n;
		}
		async function load() {
			loading.value = true;
			try {
				list.value = await marketplace(keyword.value || void 0, filterConfigType.value || void 0, filterCategory.value || void 0);
			} catch (e) {
				ElMessage.error("加载模板市场失败：" + (e instanceof Error ? e.message : String(e)));
				list.value = [];
			} finally {
				loading.value = false;
			}
		}
		function resetFilter() {
			keyword.value = "";
			filterConfigType.value = "";
			filterCategory.value = "";
			load();
		}
		function openDetail(t) {
			if (!t.id) return;
			detailId.value = t.id;
			detailVisible.value = true;
		}
		async function quickDownload(t) {
			if (!t.id) return;
			try {
				await ElMessageBox.confirm(`确认下载模板「${t.name}」？下载将增加下载计数，无参数模板直接应用原始配置。`, "确认下载", { type: "info" });
			} catch (_unused) {
				return;
			}
			try {
				await download(t.id);
				ElMessage.success("下载成功");
				await load();
			} catch (e) {
				ElMessage.error("下载失败：" + (e instanceof Error ? e.message : String(e)));
			}
		}
		/** 卡片「管理」下拉命令分发 */
		function handleManageCommand(cmd, t) {
			if (cmd === "toggle") togglePublish(t);
			else if (cmd === "archive") archiveTemplate(t);
		}
		async function togglePublish(t) {
			if (!t.id) return;
			const isPublished = t.status === "PUBLISHED";
			try {
				if (isPublished) {
					await ElMessageBox.confirm(`确认下架模板「${t.name}」？`, "确认下架", { type: "warning" });
					await unpublish(t.id);
					ElMessage.success("已下架");
				} else {
					await publish(t.id);
					ElMessage.success("已上架");
				}
				await load();
			} catch (e) {
				if (e === "cancel" || e === "close") return;
				ElMessage.error("操作失败：" + (e instanceof Error ? e.message : String(e)));
			}
		}
		async function archiveTemplate(t) {
			if (!t.id) return;
			try {
				await ElMessageBox.confirm(`确认归档模板「${t.name}」？归档后将从市场下架。`, "确认归档", { type: "warning" });
				await archive(t.id);
				ElMessage.success("已归档");
				await load();
			} catch (e) {
				if (e === "cancel" || e === "close") return;
				ElMessage.error("归档失败：" + (e instanceof Error ? e.message : String(e)));
			}
		}
		onMounted(() => {
			load();
		});
		return (_ctx, _cache) => {
			const _component_el_input = resolveComponent("el-input");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_image = resolveComponent("el-image");
			const _component_Goods = resolveComponent("Goods");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_Download = resolveComponent("Download");
			const _component_el_rate = resolveComponent("el-rate");
			const _component_ArrowDown = resolveComponent("ArrowDown");
			const _component_el_dropdown_item = resolveComponent("el-dropdown-item");
			const _component_el_dropdown_menu = resolveComponent("el-dropdown-menu");
			const _component_el_dropdown = resolveComponent("el-dropdown");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_empty = resolveComponent("el-empty");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, {
					shadow: "never",
					style: { "margin-bottom": "16px" }
				}, {
					default: withCtx(() => [createElementVNode("div", _hoisted_2, [
						createVNode(_component_el_input, {
							modelValue: keyword.value,
							"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => keyword.value = $event),
							placeholder: "搜索模板名称 / 描述 / 作者",
							clearable: "",
							style: { "width": "260px" },
							onKeyup: withKeys(load, ["enter"])
						}, null, 8, ["modelValue"]),
						createVNode(_component_el_select, {
							modelValue: filterConfigType.value,
							"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => filterConfigType.value = $event),
							placeholder: "配置类型",
							clearable: "",
							style: { "width": "180px" }
						}, {
							default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(configTypeOptions, (o) => {
								return createVNode(_component_el_option, {
									key: o.value,
									label: o.label,
									value: o.value
								}, null, 8, ["label", "value"]);
							}), 64))]),
							_: 1
						}, 8, ["modelValue"]),
						createVNode(_component_el_select, {
							modelValue: filterCategory.value,
							"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => filterCategory.value = $event),
							placeholder: "分类",
							clearable: "",
							style: { "width": "180px" }
						}, {
							default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(categoryOptions.value, (o) => {
								return openBlock(), createBlock(_component_el_option, {
									key: o.value,
									label: o.label,
									value: o.value
								}, null, 8, ["label", "value"]);
							}), 128))]),
							_: 1
						}, 8, ["modelValue"]),
						createVNode(_component_el_button, {
							type: "primary",
							onClick: load
						}, {
							default: withCtx(() => [..._cache[4] || (_cache[4] = [createTextVNode("搜索", -1)])]),
							_: 1
						}),
						createVNode(_component_el_button, { onClick: resetFilter }, {
							default: withCtx(() => [..._cache[5] || (_cache[5] = [createTextVNode("重置", -1)])]),
							_: 1
						}),
						createElementVNode("span", _hoisted_3, "共 " + toDisplayString(list.value.length) + " 个模板", 1)
					])]),
					_: 1
				}),
				withDirectives((openBlock(), createElementBlock("div", null, [list.value.length > 0 ? (openBlock(), createBlock(_component_el_row, {
					key: 0,
					gutter: 16
				}, {
					default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(list.value, (t) => {
						return openBlock(), createBlock(_component_el_col, {
							key: t.id,
							xs: 24,
							sm: 12,
							md: 8,
							lg: 6
						}, {
							default: withCtx(() => [createVNode(_component_el_card, {
								shadow: "hover",
								class: "tpl-card"
							}, {
								default: withCtx(() => {
									var _t$downloadCount;
									return [
										createElementVNode("div", _hoisted_4, [t.thumbnail ? (openBlock(), createBlock(_component_el_image, {
											key: 0,
											src: t.thumbnail,
											fit: "cover",
											class: "thumb"
										}, null, 8, ["src"])) : (openBlock(), createElementBlock("div", _hoisted_5, [createVNode(_component_el_icon, { size: 40 }, {
											default: withCtx(() => [createVNode(_component_Goods)]),
											_: 1
										}), createElementVNode("span", null, toDisplayString(t.configType), 1)])), t.status ? (openBlock(), createBlock(_component_el_tag, {
											key: 2,
											type: statusTagType(t.status),
											size: "small",
											class: "status-badge"
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(t.status), 1)]),
											_: 2
										}, 1032, ["type"])) : createCommentVNode("", true)]),
										createElementVNode("div", _hoisted_6, [createElementVNode("span", {
											class: "title",
											title: t.name
										}, toDisplayString(t.name), 9, _hoisted_7)]),
										createElementVNode("div", _hoisted_8, [createElementVNode("span", null, toDisplayString(t.author || "匿名"), 1), createVNode(_component_el_tag, {
											size: "small",
											type: "info"
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(t.configType), 1)]),
											_: 2
										}, 1024)]),
										createElementVNode("div", _hoisted_9, [(openBlock(true), createElementBlock(Fragment, null, renderList(unref(splitTags)(t.tags).slice(0, 3), (tag) => {
											return openBlock(), createBlock(_component_el_tag, {
												key: tag,
												size: "small",
												effect: "plain"
											}, {
												default: withCtx(() => [createTextVNode(toDisplayString(tag), 1)]),
												_: 2
											}, 1024);
										}), 128)), unref(splitTags)(t.tags).length === 0 ? (openBlock(), createElementBlock("span", _hoisted_10, "无标签")) : createCommentVNode("", true)]),
										createElementVNode("div", _hoisted_11, [
											createElementVNode("span", _hoisted_12, [createVNode(_component_el_icon, null, {
												default: withCtx(() => [createVNode(_component_Download)]),
												_: 1
											}), createTextVNode(" " + toDisplayString((_t$downloadCount = t.downloadCount) !== null && _t$downloadCount !== void 0 ? _t$downloadCount : 0), 1)]),
											createVNode(_component_el_rate, {
												"model-value": ratingNumber(t),
												disabled: "",
												size: "small",
												max: 5
											}, null, 8, ["model-value"]),
											createElementVNode("span", _hoisted_13, "v" + toDisplayString(t.version || "1.0.0"), 1)
										]),
										createElementVNode("div", _hoisted_14, [
											createVNode(_component_el_button, {
												size: "small",
												onClick: ($event) => openDetail(t)
											}, {
												default: withCtx(() => [..._cache[6] || (_cache[6] = [createTextVNode("详情", -1)])]),
												_: 1
											}, 8, ["onClick"]),
											createVNode(_component_el_button, {
												size: "small",
												type: "primary",
												plain: "",
												onClick: ($event) => quickDownload(t)
											}, {
												default: withCtx(() => [..._cache[7] || (_cache[7] = [createTextVNode("下载", -1)])]),
												_: 1
											}, 8, ["onClick"]),
											createVNode(_component_el_dropdown, {
												trigger: "click",
												onCommand: (cmd) => handleManageCommand(cmd, t)
											}, {
												dropdown: withCtx(() => [createVNode(_component_el_dropdown_menu, null, {
													default: withCtx(() => [createVNode(_component_el_dropdown_item, { command: "toggle" }, {
														default: withCtx(() => [createTextVNode(toDisplayString(t.status === "PUBLISHED" ? "下架" : "上架"), 1)]),
														_: 2
													}, 1024), createVNode(_component_el_dropdown_item, { command: "archive" }, {
														default: withCtx(() => [..._cache[9] || (_cache[9] = [createTextVNode("归档", -1)])]),
														_: 1
													})]),
													_: 2
												}, 1024)]),
												default: withCtx(() => [createVNode(_component_el_button, { size: "small" }, {
													default: withCtx(() => [_cache[8] || (_cache[8] = createTextVNode("管理", -1)), createVNode(_component_el_icon, { class: "el-icon--right" }, {
														default: withCtx(() => [createVNode(_component_ArrowDown)]),
														_: 1
													})]),
													_: 1
												})]),
												_: 2
											}, 1032, ["onCommand"])
										])
									];
								}),
								_: 2
							}, 1024)]),
							_: 2
						}, 1024);
					}), 128))]),
					_: 1
				})) : !loading.value ? (openBlock(), createBlock(_component_el_empty, {
					key: 1,
					description: "暂无模板，可调整筛选条件后重试"
				})) : createCommentVNode("", true)])), [[_directive_loading, loading.value]]),
				createVNode(DetailDialog_default, {
					modelValue: detailVisible.value,
					"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => detailVisible.value = $event),
					"template-id": detailId.value,
					onChanged: load
				}, null, 8, ["modelValue", "template-id"])
			]);
		};
	}
}), [["__scopeId", "data-v-9b12be5b"]]);
//#endregion
export { template_market_default as default };

//# sourceMappingURL=template-market-CEFo_OQN.js.map