import { c as useUserStore, d as useRouter, m as __exportAll, u as useRoute } from "./request-BQrAOfxW.js";
import { F as getRelatedPageByCode, N as getListByCode, f as TabsType, j as getFormByCode, u as TabPageType } from "./lowcode-F-suzo7c.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineAsyncComponent, defineComponent, openBlock, ref, renderList, resolveComponent, resolveDirective, resolveDynamicComponent, toDisplayString, unref, watch, withCtx, withDirectives } from "vue";
//#region src/components/LowCodeTabRenderer/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "low-code-tab-renderer" };
var _hoisted_2 = { class: "tab-label" };
var _hoisted_3 = {
	key: 0,
	class: "tab-content"
};
var _hoisted_4 = {
	key: 0,
	class: "tab-error"
};
var _hoisted_5 = {
	key: 1,
	class: "tab-content"
};
var _hoisted_6 = {
	key: 0,
	class: "tab-error"
};
var _hoisted_7 = {
	key: 2,
	class: "tab-content"
};
var _hoisted_8 = {
	key: 0,
	class: "tab-error"
};
var _hoisted_9 = {
	key: 3,
	class: "tab-content"
};
var _hoisted_10 = {
	key: 0,
	class: "custom-page"
};
var _hoisted_11 = ["src"];
var _hoisted_12 = {
	key: 1,
	class: "custom-router"
};
var _hoisted_13 = { class: "custom-router-tip" };
/** 异步加载子页面渲染器，避免循环依赖 */
var index_vue_vue_type_script_setup_true_lang_default = /*@__PURE__*/ defineComponent({
	__name: "index",
	props: {
		config: {},
		modelValue: { default: "" },
		contextData: { default: () => ({}) }
	},
	emits: [
		"update:modelValue",
		"tab-change",
		"navigate",
		"page-loaded"
	],
	setup(__props, { emit: __emit }) {
		/**
		* 低代码标签页渲染引擎。
		*
		* <p>根据传入的 {@link TabConfig} 动态渲染 Element Plus el-tabs，支持：</p>
		* <ul>
		*   <li>4 种页面类型：form（LowCodeFormRenderer）/ list（LowCodeListRenderer）/
		*       related-page（LowCodeRelatedPageRenderer）/ custom（iframe 或 router-view）</li>
		*   <li>v-model 绑定当前激活的 tab name</li>
		*   <li>lazy 懒加载、disabled 禁用、closable/addable/editable 顶层属性</li>
		*   <li>icon 图标显示在标签标题前</li>
		*   <li>visible 显示条件表达式（对 contextData 求值）</li>
		*   <li>props 模板变量解析（${route.params.id} / ${row.field} / ${user.userId} 等）</li>
		* </ul>
		*
		* <p>渲染 form/list/related-page 时通过异步加载对应的低代码配置（getFormByCode /
		* getListByCode / getRelatedPageByCode），并嵌入对应的渲染器组件。
		* custom 类型直接渲染 iframe（pageUrl）或 emit navigate 事件由业务层处理。</p>
		*
		* <p>对外暴露 tab-change / navigate / page-loaded 事件，方便业务层介入。</p>
		*/
		const LowCodeFormRenderer = defineAsyncComponent(() => import("./LowCodeFormRenderer-AJit0-ob.js").then((n) => n.n));
		const LowCodeListRenderer = defineAsyncComponent(() => import("./LowCodeListRenderer-CyLlB3D5.js").then((n) => n.n));
		const LowCodeRelatedPageRenderer = defineAsyncComponent(() => import("./LowCodeRelatedPageRenderer-DCPiySON.js").then((n) => n.n));
		/** Props 定义 */
		const props = __props;
		/** Emits 定义 */
		const emit = __emit;
		const route = useRoute();
		const router = useRouter();
		const userStore = useUserStore();
		/** 内部维护的激活 tab name */
		const activeName = ref(props.modelValue || "");
		watch(() => props.modelValue, (val) => {
			if (val && val !== activeName.value) activeName.value = val;
		});
		/** el-tabs type 值：plain 类型在 Element Plus 中对应空字符串 */
		const tabsType = computed(() => {
			const t = props.config.type || TabsType.BORDER_CARD;
			return t === TabsType.PLAIN ? "" : t;
		});
		/**
		* 可见标签列表：根据 visible 表达式过滤。
		*/
		const visibleTabs = computed(() => {
			return (props.config.tabs || []).filter((tab) => evalVisible(tab));
		});
		watch(visibleTabs, (tabs) => {
			if (tabs.length === 0) {
				activeName.value = "";
				return;
			}
			if (!tabs.some((t) => t.name === activeName.value)) {
				activeName.value = tabs[0].name;
				emit("update:modelValue", activeName.value);
			}
		}, { immediate: true });
		/**
		* 求值 visible 显示条件表达式。
		*
		* <p>使用 new Function 编译表达式，注入 row/context/route/user 上下文。
		* 留空或求值出错时返回 true（显示）。</p>
		*/
		function evalVisible(tab) {
			const expr = tab.visible;
			if (!expr || !expr.trim()) return true;
			try {
				const row = props.contextData.row || {};
				const context = props.contextData.context || {};
				return !!new Function("row", "context", "route", "user", `"use strict"; return (${expr});`)(row, context, route, userStore.userInfo || {});
			} catch (e) {
				console.warn(`[LowCodeTabRenderer] visible 表达式求值失败: ${expr}`, e);
				return true;
			}
		}
		/**
		* 解析 props 模板变量。
		*
		* <p>支持 ${route.params.id} / ${route.query.code} / ${row.field} /
		* ${context.field} / ${user.userId} 等模板。非字符串值原样返回。</p>
		*/
		function resolveProps(tab) {
			const result = {};
			const src = tab.props || {};
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
					console.warn(`[LowCodeTabRenderer] props 模板解析失败: ${expr}`, e);
					return "";
				}
			});
		}
		/**
		* 已加载的子页面配置缓存：key 为 tab.id，value 为解析后的配置对象。
		*
		* <p>使用 shallowRef 避免 reactive 深度代理（form/list 配置可能较大）。
		* lazy 模式下首次激活对应 tab 时才触发加载。</p>
		*/
		const pageConfigCache = ref({});
		/** 各 tab 加载状态 */
		const loadingMap = ref({});
		/** 各 tab 加载错误信息 */
		const errorMap = ref({});
		/**
		* 加载指定 tab 引用的子页面配置。
		*/
		async function loadPageConfig(tab) {
			if (!tab.pageCode) return;
			if (pageConfigCache.value[tab.id]) return;
			loadingMap.value[tab.id] = true;
			errorMap.value[tab.id] = "";
			try {
				let cfg = null;
				if (tab.pageType === TabPageType.FORM) {
					const res = await getFormByCode(tab.pageCode);
					cfg = JSON.parse(res.formConfig);
				} else if (tab.pageType === TabPageType.LIST) {
					const res = await getListByCode(tab.pageCode);
					cfg = JSON.parse(res.listConfig);
				} else if (tab.pageType === TabPageType.RELATED_PAGE) {
					const res = await getRelatedPageByCode(tab.pageCode);
					cfg = JSON.parse(res.relatedConfig);
				}
				if (cfg) {
					pageConfigCache.value[tab.id] = cfg;
					emit("page-loaded", tab, cfg);
				}
			} catch (e) {
				errorMap.value[tab.id] = e.message || "加载失败";
				console.warn(`[LowCodeTabRenderer] 加载子页面配置失败: ${tab.pageCode}`, e);
			} finally {
				loadingMap.value[tab.id] = false;
			}
		}
		/** 当前激活 tab 变化时触发懒加载 */
		watch(activeName, (name) => {
			const tab = visibleTabs.value.find((t) => t.name === name);
			if (tab && tab.lazy !== false) loadPageConfig(tab);
			if (tab) {
				emit("tab-change", tab);
				emit("update:modelValue", name);
			}
		});
		/** 非懒加载的 tab 在初始化时主动加载 */
		watch(visibleTabs, (tabs) => {
			for (const tab of tabs) if (tab.lazy === false) loadPageConfig(tab);
		}, { immediate: true });
		/** tab 点击事件 */
		function handleTabClick(tabName) {
			activeName.value = tabName;
		}
		/** custom 类型 tab 跳转 */
		function handleCustomNavigate(tab) {
			if (!tab.pageUrl) {
				ElMessage.warning("自定义页面未配置 pageUrl");
				return;
			}
			const resolved = resolveTemplate(tab.pageUrl, {
				row: props.contextData.row || {},
				context: props.contextData.context || {},
				route,
				user: userStore.userInfo || {}
			});
			emit("navigate", resolved, tab);
			if (resolved.startsWith("http")) window.open(resolved, "_blank");
			else router.push(resolved).catch(() => {
				window.open(resolved, "_blank");
			});
		}
		return (_ctx, _cache) => {
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_alert = resolveComponent("el-alert");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_tab_pane = resolveComponent("el-tab-pane");
			const _component_el_tabs = resolveComponent("el-tabs");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_tabs, {
				modelValue: activeName.value,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => activeName.value = $event),
				type: tabsType.value,
				"tab-position": __props.config.tabPosition || "top",
				closable: __props.config.closable,
				addable: __props.config.addable,
				editable: __props.config.editable,
				onTabClick: _cache[1] || (_cache[1] = (pane) => {
					var _pane$props$name;
					return handleTabClick(String((_pane$props$name = pane.props.name) !== null && _pane$props$name !== void 0 ? _pane$props$name : ""));
				})
			}, {
				default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(visibleTabs.value, (tab) => {
					return openBlock(), createBlock(_component_el_tab_pane, {
						key: tab.id,
						label: tab.title,
						name: tab.name,
						lazy: tab.lazy !== false,
						disabled: tab.disabled
					}, {
						label: withCtx(() => [createElementVNode("span", _hoisted_2, [tab.icon ? (openBlock(), createBlock(_component_el_icon, { key: 0 }, {
							default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(tab.icon)))]),
							_: 2
						}, 1024)) : createCommentVNode("", true), createElementVNode("span", null, toDisplayString(tab.title), 1)])]),
						default: withCtx(() => [tab.pageType === "form" ? withDirectives((openBlock(), createElementBlock("div", _hoisted_3, [errorMap.value[tab.id] ? (openBlock(), createElementBlock("div", _hoisted_4, [createVNode(_component_el_alert, {
							title: `加载表单失败：${errorMap.value[tab.id]}`,
							type: "error",
							closable: false
						}, null, 8, ["title"])])) : pageConfigCache.value[tab.id] ? (openBlock(), createBlock(unref(LowCodeFormRenderer), {
							key: 1,
							config: pageConfigCache.value[tab.id],
							"model-value": resolveProps(tab)
						}, null, 8, ["config", "model-value"])) : createCommentVNode("", true)])), [[_directive_loading, loadingMap.value[tab.id]]]) : tab.pageType === "list" ? withDirectives((openBlock(), createElementBlock("div", _hoisted_5, [errorMap.value[tab.id] ? (openBlock(), createElementBlock("div", _hoisted_6, [createVNode(_component_el_alert, {
							title: `加载列表失败：${errorMap.value[tab.id]}`,
							type: "error",
							closable: false
						}, null, 8, ["title"])])) : pageConfigCache.value[tab.id] ? (openBlock(), createBlock(unref(LowCodeListRenderer), {
							key: 1,
							config: pageConfigCache.value[tab.id],
							"auto-fetch": true
						}, null, 8, ["config"])) : createCommentVNode("", true)])), [[_directive_loading, loadingMap.value[tab.id]]]) : tab.pageType === "related-page" ? withDirectives((openBlock(), createElementBlock("div", _hoisted_7, [errorMap.value[tab.id] ? (openBlock(), createElementBlock("div", _hoisted_8, [createVNode(_component_el_alert, {
							title: `加载关联页失败：${errorMap.value[tab.id]}`,
							type: "error",
							closable: false
						}, null, 8, ["title"])])) : pageConfigCache.value[tab.id] ? (openBlock(), createBlock(unref(LowCodeRelatedPageRenderer), {
							key: 1,
							config: pageConfigCache.value[tab.id],
							"context-data": resolveProps(tab)
						}, null, 8, ["config", "context-data"])) : createCommentVNode("", true)])), [[_directive_loading, loadingMap.value[tab.id]]]) : tab.pageType === "custom" ? (openBlock(), createElementBlock("div", _hoisted_9, [tab.pageUrl ? (openBlock(), createElementBlock("div", _hoisted_10, [tab.pageUrl.startsWith("http") ? (openBlock(), createElementBlock("iframe", {
							key: 0,
							src: resolveTemplate(tab.pageUrl, {
								row: __props.contextData.row || {},
								context: __props.contextData.context || {},
								route: unref(route),
								user: unref(userStore).userInfo || {}
							}),
							frameborder: "0",
							class: "custom-iframe"
						}, null, 8, _hoisted_11)) : (openBlock(), createElementBlock("div", _hoisted_12, [createVNode(_component_el_button, {
							type: "primary",
							onClick: ($event) => handleCustomNavigate(tab)
						}, {
							default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("打开页面", -1)])]),
							_: 1
						}, 8, ["onClick"]), createElementVNode("span", _hoisted_13, toDisplayString(tab.pageUrl), 1)]))])) : (openBlock(), createBlock(_component_el_empty, {
							key: 1,
							description: "自定义页面未配置 URL",
							"image-size": 80
						}))])) : (openBlock(), createBlock(_component_el_empty, {
							key: 4,
							description: `未知的页面类型: ${tab.pageType}`,
							"image-size": 80
						}, null, 8, ["description"]))]),
						_: 2
					}, 1032, [
						"label",
						"name",
						"lazy",
						"disabled"
					]);
				}), 128))]),
				_: 1
			}, 8, [
				"modelValue",
				"type",
				"tab-position",
				"closable",
				"addable",
				"editable"
			])]);
		};
	}
});
//#endregion
//#region src/components/LowCodeTabRenderer/index.vue
var LowCodeTabRenderer_exports = /* @__PURE__ */ __exportAll({ default: () => LowCodeTabRenderer_default });
var LowCodeTabRenderer_default = /*#__PURE__*/ _plugin_vue_export_helper_default(index_vue_vue_type_script_setup_true_lang_default, [["__scopeId", "data-v-0db9e712"]]);
//#endregion
export { LowCodeTabRenderer_exports as n, LowCodeTabRenderer_default as t };

//# sourceMappingURL=LowCodeTabRenderer-Cgh2BbMb.js.map