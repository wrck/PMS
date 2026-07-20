import { c as useUserStore, d as useRouter, m as __exportAll, u as useRoute } from "./request-BQrAOfxW.js";
import { L as getTabByCode, N as getListByCode, c as RelatedPageLayout, j as getFormByCode, l as SectionType } from "./lowcode-F-suzo7c.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineAsyncComponent, defineComponent, mergeProps, openBlock, ref, renderList, resolveComponent, resolveDirective, toDisplayString, unref, watch, withCtx, withDirectives } from "vue";
//#region src/components/LowCodeRelatedPageRenderer/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "low-code-related-page-renderer" };
var _hoisted_2 = { class: "section-title" };
var _hoisted_3 = { class: "section-content" };
var _hoisted_4 = {
	key: 0,
	class: "section-error"
};
var _hoisted_5 = {
	key: 4,
	class: "custom-section"
};
var _hoisted_6 = ["src"];
var _hoisted_7 = {
	key: 1,
	class: "custom-router"
};
var _hoisted_8 = { class: "custom-router-tip" };
var _hoisted_9 = { class: "section-content" };
var _hoisted_10 = {
	key: 0,
	class: "section-error"
};
var _hoisted_11 = {
	key: 4,
	class: "custom-section"
};
var _hoisted_12 = ["src"];
var _hoisted_13 = {
	key: 1,
	class: "custom-router"
};
var _hoisted_14 = { class: "custom-router-tip" };
var _hoisted_15 = { class: "section-content" };
var _hoisted_16 = {
	key: 0,
	class: "section-error"
};
var _hoisted_17 = {
	key: 4,
	class: "custom-section"
};
var _hoisted_18 = ["src"];
var _hoisted_19 = {
	key: 1,
	class: "custom-router"
};
var _hoisted_20 = { class: "custom-router-tip" };
/** 异步加载子页面渲染器，避免循环依赖 */
var index_vue_vue_type_script_setup_true_lang_default = /*@__PURE__*/ defineComponent({
	__name: "index",
	props: {
		config: {},
		contextData: { default: () => ({}) }
	},
	emits: [
		"section-change",
		"navigate",
		"page-loaded"
	],
	setup(__props, { emit: __emit }) {
		/**
		* 低代码关联页渲染引擎。
		*
		* <p>根据传入的 {@link RelatedPageConfig} 动态渲染关联区块，支持：</p>
		* <ul>
		*   <li>3 种布局：grid（el-row + el-col）/ tabs（el-tabs）/ collapse（el-collapse）</li>
		*   <li>4 种区块类型：form（LowCodeFormRenderer）/ list（LowCodeListRenderer）/
		*       tab（LowCodeTabRenderer）/ custom（iframe 或 router-view）</li>
		*   <li>section.span 栅格宽度（grid 模式生效）</li>
		*   <li>section.order 排序（升序排列区块）</li>
		*   <li>section.visible 显示条件表达式（对 contextData 求值）</li>
		*   <li>section.props 模板变量解析（${route.params.id} / ${row.field} 等）</li>
		* </ul>
		*
		* <p>渲染 form/list/tab 时通过异步加载对应的低代码配置（getFormByCode /
		* getListByCode / getTabByCode），并嵌入对应的渲染器组件。
		* custom 类型直接渲染 iframe（pageUrl）或 emit navigate 事件由业务层处理。</p>
		*
		* <p>对外暴露 section-change / navigate / page-loaded 事件，方便业务层介入。</p>
		*/
		const LowCodeFormRenderer = defineAsyncComponent(() => import("./LowCodeFormRenderer-AJit0-ob.js").then((n) => n.n));
		const LowCodeListRenderer = defineAsyncComponent(() => import("./LowCodeListRenderer-CyLlB3D5.js").then((n) => n.n));
		const LowCodeTabRenderer = defineAsyncComponent(() => import("./LowCodeTabRenderer-Cgh2BbMb.js").then((n) => n.n));
		/** Props 定义 */
		const props = __props;
		/** Emits 定义 */
		const emit = __emit;
		const route = useRoute();
		const router = useRouter();
		const userStore = useUserStore();
		/**
		* 排序+过滤后的区块列表：按 order 升序，相同 order 按数组顺序；过滤 visible=false 的区块。
		*/
		const visibleSections = computed(() => {
			const list = (props.config.sections || []).filter((s) => evalVisible(s)).slice();
			list.sort((a, b) => {
				var _a$order, _b$order;
				return ((_a$order = a.order) !== null && _a$order !== void 0 ? _a$order : 100) - ((_b$order = b.order) !== null && _b$order !== void 0 ? _b$order : 100);
			});
			return list;
		});
		/** 布局类型（默认 grid） */
		const layout = computed(() => props.config.layout || RelatedPageLayout.GRID);
		/** 栅格间距（默认 16） */
		const gutter = computed(() => {
			var _props$config$gutter;
			return (_props$config$gutter = props.config.gutter) !== null && _props$config$gutter !== void 0 ? _props$config$gutter : 16;
		});
		/**
		* 解析 section.span 为 el-col 绑定属性。
		*
		* <p>向后兼容：span 为数字或缺省时按 :span= 渲染（缺省 24）；
		* span 为响应式断点对象时按 :xs= :sm= :md= :lg= :xl= 渲染。</p>
		*/
		function colProps(span) {
			if (span === void 0 || typeof span === "number") return { span: span !== null && span !== void 0 ? span : 24 };
			const result = {};
			if (span.xs !== void 0) result.xs = span.xs;
			if (span.sm !== void 0) result.sm = span.sm;
			if (span.md !== void 0) result.md = span.md;
			if (span.lg !== void 0) result.lg = span.lg;
			if (span.xl !== void 0) result.xl = span.xl;
			return result;
		}
		/** tabs/collapse 模式下当前激活项 */
		const activeTab = ref("");
		const activeCollapse = ref([]);
		watch(visibleSections, (sections) => {
			if (sections.length === 0) return;
			if (!activeTab.value && sections[0]) activeTab.value = sections[0].id;
			activeCollapse.value = sections.map((s) => s.id);
		}, { immediate: true });
		/**
		* 求值 visible 显示条件表达式。
		*/
		function evalVisible(section) {
			const expr = section.visible;
			if (!expr || !expr.trim()) return true;
			try {
				const row = props.contextData.row || {};
				const context = props.contextData.context || {};
				return !!new Function("row", "context", "route", "user", `"use strict"; return (${expr});`)(row, context, route, userStore.userInfo || {});
			} catch (e) {
				console.warn(`[LowCodeRelatedPageRenderer] visible 表达式求值失败: ${expr}`, e);
				return true;
			}
		}
		/**
		* 解析 props 模板变量。
		*/
		function resolveProps(section) {
			const result = {};
			const src = section.props || {};
			const row = props.contextData.row || {};
			const context = props.contextData.context || {};
			const user = userStore.userInfo || {};
			for (const key of Object.keys(src)) {
				const val = src[key];
				if (typeof val === "string") result[key] = resolveTemplate(val, {
					row,
					context,
					route,
					user
				});
				else result[key] = val;
			}
			return result;
		}
		/** 模板变量正则：匹配 ${...} */
		const TEMPLATE_RE = /\$\{\s*([^}]+?)\s*\}/g;
		/** 解析单个字符串中的模板变量 */
		function resolveTemplate(tpl, ctx) {
			return tpl.replace(TEMPLATE_RE, (_, expr) => {
				try {
					const val = new Function(...Object.keys(ctx), `"use strict"; return (${expr});`)(...Object.values(ctx));
					return val == null ? "" : String(val);
				} catch (e) {
					console.warn(`[LowCodeRelatedPageRenderer] props 模板解析失败: ${expr}`, e);
					return "";
				}
			});
		}
		/** 已加载的子页面配置缓存：key 为 section.id */
		const pageConfigCache = ref({});
		/** 各 section 加载状态 */
		const loadingMap = ref({});
		/** 各 section 加载错误信息 */
		const errorMap = ref({});
		/**
		* 加载指定 section 引用的子页面配置。
		*/
		async function loadPageConfig(section) {
			if (!section.pageCode) return;
			if (pageConfigCache.value[section.id]) return;
			loadingMap.value[section.id] = true;
			errorMap.value[section.id] = "";
			try {
				let cfg = null;
				if (section.type === SectionType.FORM) {
					const res = await getFormByCode(section.pageCode);
					cfg = JSON.parse(res.formConfig);
				} else if (section.type === SectionType.LIST) {
					const res = await getListByCode(section.pageCode);
					cfg = JSON.parse(res.listConfig);
				} else if (section.type === SectionType.TAB) {
					const res = await getTabByCode(section.pageCode);
					cfg = JSON.parse(res.tabConfig);
				}
				if (cfg) {
					pageConfigCache.value[section.id] = cfg;
					emit("page-loaded", section, cfg);
				}
			} catch (e) {
				errorMap.value[section.id] = e.message || "加载失败";
				console.warn(`[LowCodeRelatedPageRenderer] 加载子页面配置失败: ${section.pageCode}`, e);
			} finally {
				loadingMap.value[section.id] = false;
			}
		}
		/** 可见区块变化时主动加载所有区块配置 */
		watch(visibleSections, (sections) => {
			for (const section of sections) loadPageConfig(section);
		}, { immediate: true });
		/** tabs 模式切换 */
		function handleTabChange(tabId) {
			const section = visibleSections.value.find((s) => s.id === tabId);
			if (section) emit("section-change", section);
		}
		/** collapse 模式切换 */
		function handleCollapseChange(activeNames) {
			const arr = Array.isArray(activeNames) ? activeNames : [activeNames];
			if (arr.length > 0) {
				const section = visibleSections.value.find((s) => s.id === arr[arr.length - 1]);
				if (section) emit("section-change", section);
			}
		}
		/** custom 类型 section 跳转 */
		function handleCustomNavigate(section) {
			if (!section.pageUrl) {
				ElMessage.warning("自定义页面未配置 pageUrl");
				return;
			}
			const resolved = resolveTemplate(section.pageUrl, {
				row: props.contextData.row || {},
				context: props.contextData.context || {},
				route,
				user: userStore.userInfo || {}
			});
			emit("navigate", resolved, section);
			if (resolved.startsWith("http")) window.open(resolved, "_blank");
			else router.push(resolved).catch(() => {
				window.open(resolved, "_blank");
			});
		}
		return (_ctx, _cache) => {
			const _component_el_alert = resolveComponent("el-alert");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_tab_pane = resolveComponent("el-tab-pane");
			const _component_el_tabs = resolveComponent("el-tabs");
			const _component_el_collapse_item = resolveComponent("el-collapse-item");
			const _component_el_collapse = resolveComponent("el-collapse");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [layout.value === "grid" ? (openBlock(), createBlock(_component_el_row, {
				key: 0,
				gutter: gutter.value
			}, {
				default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(visibleSections.value, (section) => {
					return openBlock(), createBlock(_component_el_col, mergeProps({ key: section.id }, { ref_for: true }, colProps(section.span)), {
						default: withCtx(() => [createVNode(_component_el_card, {
							shadow: "never",
							class: "section-card"
						}, {
							header: withCtx(() => [createElementVNode("span", _hoisted_2, toDisplayString(section.title), 1)]),
							default: withCtx(() => [withDirectives((openBlock(), createElementBlock("div", _hoisted_3, [errorMap.value[section.id] ? (openBlock(), createElementBlock("div", _hoisted_4, [createVNode(_component_el_alert, {
								title: `加载失败：${errorMap.value[section.id]}`,
								type: "error",
								closable: false
							}, null, 8, ["title"])])) : section.type === "form" && pageConfigCache.value[section.id] ? (openBlock(), createBlock(unref(LowCodeFormRenderer), {
								key: 1,
								config: pageConfigCache.value[section.id],
								"model-value": resolveProps(section)
							}, null, 8, ["config", "model-value"])) : section.type === "list" && pageConfigCache.value[section.id] ? (openBlock(), createBlock(unref(LowCodeListRenderer), {
								key: 2,
								config: pageConfigCache.value[section.id],
								"auto-fetch": true
							}, null, 8, ["config"])) : section.type === "tab" && pageConfigCache.value[section.id] ? (openBlock(), createBlock(unref(LowCodeTabRenderer), {
								key: 3,
								config: pageConfigCache.value[section.id],
								"context-data": resolveProps(section)
							}, null, 8, ["config", "context-data"])) : section.type === "custom" ? (openBlock(), createElementBlock("div", _hoisted_5, [section.pageUrl && section.pageUrl.startsWith("http") ? (openBlock(), createElementBlock("iframe", {
								key: 0,
								src: resolveTemplate(section.pageUrl, {
									row: __props.contextData.row || {},
									context: __props.contextData.context || {},
									route: unref(route),
									user: unref(userStore).userInfo || {}
								}),
								frameborder: "0",
								class: "custom-iframe"
							}, null, 8, _hoisted_6)) : section.pageUrl ? (openBlock(), createElementBlock("div", _hoisted_7, [createVNode(_component_el_button, {
								type: "primary",
								onClick: ($event) => handleCustomNavigate(section)
							}, {
								default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("打开页面", -1)])]),
								_: 1
							}, 8, ["onClick"]), createElementVNode("span", _hoisted_8, toDisplayString(section.pageUrl), 1)])) : (openBlock(), createBlock(_component_el_empty, {
								key: 2,
								description: "自定义页面未配置 URL",
								"image-size": 80
							}))])) : section.type !== "custom" ? (openBlock(), createBlock(_component_el_empty, {
								key: 5,
								description: "配置加载中...",
								"image-size": 80
							})) : createCommentVNode("", true)])), [[_directive_loading, loadingMap.value[section.id]]])]),
							_: 2
						}, 1024)]),
						_: 2
					}, 1040);
				}), 128))]),
				_: 1
			}, 8, ["gutter"])) : layout.value === "tabs" ? (openBlock(), createBlock(_component_el_tabs, {
				key: 1,
				modelValue: activeTab.value,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => activeTab.value = $event),
				type: "border-card",
				onTabChange: handleTabChange
			}, {
				default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(visibleSections.value, (section) => {
					return openBlock(), createBlock(_component_el_tab_pane, {
						key: section.id,
						label: section.title,
						name: section.id
					}, {
						default: withCtx(() => [withDirectives((openBlock(), createElementBlock("div", _hoisted_9, [errorMap.value[section.id] ? (openBlock(), createElementBlock("div", _hoisted_10, [createVNode(_component_el_alert, {
							title: `加载失败：${errorMap.value[section.id]}`,
							type: "error",
							closable: false
						}, null, 8, ["title"])])) : section.type === "form" && pageConfigCache.value[section.id] ? (openBlock(), createBlock(unref(LowCodeFormRenderer), {
							key: 1,
							config: pageConfigCache.value[section.id],
							"model-value": resolveProps(section)
						}, null, 8, ["config", "model-value"])) : section.type === "list" && pageConfigCache.value[section.id] ? (openBlock(), createBlock(unref(LowCodeListRenderer), {
							key: 2,
							config: pageConfigCache.value[section.id],
							"auto-fetch": true
						}, null, 8, ["config"])) : section.type === "tab" && pageConfigCache.value[section.id] ? (openBlock(), createBlock(unref(LowCodeTabRenderer), {
							key: 3,
							config: pageConfigCache.value[section.id],
							"context-data": resolveProps(section)
						}, null, 8, ["config", "context-data"])) : section.type === "custom" ? (openBlock(), createElementBlock("div", _hoisted_11, [section.pageUrl && section.pageUrl.startsWith("http") ? (openBlock(), createElementBlock("iframe", {
							key: 0,
							src: resolveTemplate(section.pageUrl, {
								row: __props.contextData.row || {},
								context: __props.contextData.context || {},
								route: unref(route),
								user: unref(userStore).userInfo || {}
							}),
							frameborder: "0",
							class: "custom-iframe"
						}, null, 8, _hoisted_12)) : section.pageUrl ? (openBlock(), createElementBlock("div", _hoisted_13, [createVNode(_component_el_button, {
							type: "primary",
							onClick: ($event) => handleCustomNavigate(section)
						}, {
							default: withCtx(() => [..._cache[3] || (_cache[3] = [createTextVNode("打开页面", -1)])]),
							_: 1
						}, 8, ["onClick"]), createElementVNode("span", _hoisted_14, toDisplayString(section.pageUrl), 1)])) : (openBlock(), createBlock(_component_el_empty, {
							key: 2,
							description: "自定义页面未配置 URL",
							"image-size": 80
						}))])) : (openBlock(), createBlock(_component_el_empty, {
							key: 5,
							description: "配置加载中...",
							"image-size": 80
						}))])), [[_directive_loading, loadingMap.value[section.id]]])]),
						_: 2
					}, 1032, ["label", "name"]);
				}), 128))]),
				_: 1
			}, 8, ["modelValue"])) : layout.value === "collapse" ? (openBlock(), createBlock(_component_el_collapse, {
				key: 2,
				modelValue: activeCollapse.value,
				"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => activeCollapse.value = $event),
				onChange: handleCollapseChange
			}, {
				default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(visibleSections.value, (section) => {
					return openBlock(), createBlock(_component_el_collapse_item, {
						key: section.id,
						title: section.title,
						name: section.id
					}, {
						default: withCtx(() => [withDirectives((openBlock(), createElementBlock("div", _hoisted_15, [errorMap.value[section.id] ? (openBlock(), createElementBlock("div", _hoisted_16, [createVNode(_component_el_alert, {
							title: `加载失败：${errorMap.value[section.id]}`,
							type: "error",
							closable: false
						}, null, 8, ["title"])])) : section.type === "form" && pageConfigCache.value[section.id] ? (openBlock(), createBlock(unref(LowCodeFormRenderer), {
							key: 1,
							config: pageConfigCache.value[section.id],
							"model-value": resolveProps(section)
						}, null, 8, ["config", "model-value"])) : section.type === "list" && pageConfigCache.value[section.id] ? (openBlock(), createBlock(unref(LowCodeListRenderer), {
							key: 2,
							config: pageConfigCache.value[section.id],
							"auto-fetch": true
						}, null, 8, ["config"])) : section.type === "tab" && pageConfigCache.value[section.id] ? (openBlock(), createBlock(unref(LowCodeTabRenderer), {
							key: 3,
							config: pageConfigCache.value[section.id],
							"context-data": resolveProps(section)
						}, null, 8, ["config", "context-data"])) : section.type === "custom" ? (openBlock(), createElementBlock("div", _hoisted_17, [section.pageUrl && section.pageUrl.startsWith("http") ? (openBlock(), createElementBlock("iframe", {
							key: 0,
							src: resolveTemplate(section.pageUrl, {
								row: __props.contextData.row || {},
								context: __props.contextData.context || {},
								route: unref(route),
								user: unref(userStore).userInfo || {}
							}),
							frameborder: "0",
							class: "custom-iframe"
						}, null, 8, _hoisted_18)) : section.pageUrl ? (openBlock(), createElementBlock("div", _hoisted_19, [createVNode(_component_el_button, {
							type: "primary",
							onClick: ($event) => handleCustomNavigate(section)
						}, {
							default: withCtx(() => [..._cache[4] || (_cache[4] = [createTextVNode("打开页面", -1)])]),
							_: 1
						}, 8, ["onClick"]), createElementVNode("span", _hoisted_20, toDisplayString(section.pageUrl), 1)])) : (openBlock(), createBlock(_component_el_empty, {
							key: 2,
							description: "自定义页面未配置 URL",
							"image-size": 80
						}))])) : (openBlock(), createBlock(_component_el_empty, {
							key: 5,
							description: "配置加载中...",
							"image-size": 80
						}))])), [[_directive_loading, loadingMap.value[section.id]]])]),
						_: 2
					}, 1032, ["title", "name"]);
				}), 128))]),
				_: 1
			}, 8, ["modelValue"])) : (openBlock(), createBlock(_component_el_empty, {
				key: 3,
				description: `未知的布局类型: ${layout.value}`,
				"image-size": 80
			}, null, 8, ["description"]))]);
		};
	}
});
//#endregion
//#region src/components/LowCodeRelatedPageRenderer/index.vue
var LowCodeRelatedPageRenderer_exports = /* @__PURE__ */ __exportAll({ default: () => LowCodeRelatedPageRenderer_default });
var LowCodeRelatedPageRenderer_default = /*#__PURE__*/ _plugin_vue_export_helper_default(index_vue_vue_type_script_setup_true_lang_default, [["__scopeId", "data-v-64312eb1"]]);
//#endregion
export { LowCodeRelatedPageRenderer_exports as n, LowCodeRelatedPageRenderer_default as t };

//# sourceMappingURL=LowCodeRelatedPageRenderer-DCPiySON.js.map