import { d as useRouter, u as useRoute } from "./request-BQrAOfxW.js";
import { F as getRelatedPageByCode, L as getTabByCode, N as getListByCode, _ as checkLowCodePermission, j as getFormByCode } from "./lowcode-F-suzo7c.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage } from "element-plus";
import { computed, createBlock, createCommentVNode, createElementBlock, createTextVNode, createVNode, defineAsyncComponent, defineComponent, onMounted, openBlock, ref, resolveComponent, resolveDynamicComponent, watch, withCtx } from "vue";
//#region src/views/lowcode/render/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "lowcode-render-page" };
//#endregion
//#region src/views/lowcode/render/index.vue
var render_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		/**
		* 低代码页面通用渲染入口。
		*
		* <p>路由 /lowcode/:pageType/:pageCode 进入此组件。组件职责：</p>
		* <ul>
		*   <li>根据 pageType 调用对应 API 拉取已发布配置（getFormByCode 等）</li>
		*   <li>调用权限校验接口 checkLowCodePermission，无权限时显示 403 提示</li>
		*   <li>解析配置 JSON 字符串后，动态加载对应渲染器渲染</li>
		*   <li>加载中显示骨架屏；配置不存在显示 404；无权限显示 403</li>
		*   <li>将路由 query 参数透传给渲染器作为上下文数据</li>
		* </ul>
		*
		* <p>4 种渲染器对应关系：</p>
		* <ul>
		*   <li>form → LowCodeFormRenderer（props: config: FormConfig）</li>
		*   <li>list → LowCodeListRenderer（props: config: ListConfig）</li>
		*   <li>tab → LowCodeTabRenderer（props: config: TabConfig, contextData）</li>
		*   <li>related-page → LowCodeRelatedPageRenderer（props: config: RelatedPageConfig, contextData）</li>
		* </ul>
		*/
		const route = useRoute();
		const router = useRouter();
		/** 加载状态：loading / done / not-found / forbidden / error */
		const state = ref("loading");
		/** 解析后的配置对象（FormConfig / ListConfig / TabConfig / RelatedPageConfig 之一） */
		const config = ref(null);
		const pageType = computed(() => route.params.pageType);
		const pageCode = computed(() => route.params.pageCode);
		/** 合法的页面类型集合 */
		const VALID_PAGE_TYPES = /* @__PURE__ */ new Set([
			"form",
			"list",
			"tab",
			"related-page"
		]);
		/** 根据 pageType 解析渲染器组件（异步加载，避免首屏加载全部渲染器） */
		const renderer = computed(() => {
			switch (pageType.value) {
				case "form": return defineAsyncComponent(() => import("./LowCodeFormRenderer-AJit0-ob.js").then((n) => n.n));
				case "list": return defineAsyncComponent(() => import("./LowCodeListRenderer-CyLlB3D5.js").then((n) => n.n));
				case "tab": return defineAsyncComponent(() => import("./LowCodeTabRenderer-Cgh2BbMb.js").then((n) => n.n));
				case "related-page": return defineAsyncComponent(() => import("./LowCodeRelatedPageRenderer-DCPiySON.js").then((n) => n.n));
				default: return null;
			}
		});
		/** 透传给渲染器的上下文数据（route / params / query） */
		const contextData = computed(() => ({
			route,
			params: route.params,
			query: route.query
		}));
		/**
		* 拉取已发布配置并解析 JSON。
		* 返回 { name, config } 或 null（配置不存在 / 状态非 PUBLISHED）。
		*/
		async function fetchConfig(type, code) {
			let meta = null;
			let configStr;
			switch (type) {
				case "form":
					meta = await getFormByCode(code);
					configStr = meta === null || meta === void 0 ? void 0 : meta.formConfig;
					break;
				case "list":
					meta = await getListByCode(code);
					configStr = meta === null || meta === void 0 ? void 0 : meta.listConfig;
					break;
				case "tab":
					meta = await getTabByCode(code);
					configStr = meta === null || meta === void 0 ? void 0 : meta.tabConfig;
					break;
				case "related-page":
					meta = await getRelatedPageByCode(code);
					configStr = meta === null || meta === void 0 ? void 0 : meta.relatedConfig;
					break;
				default: return null;
			}
			if (!meta) return null;
			if (!configStr) return null;
			try {
				const parsed = typeof configStr === "string" ? JSON.parse(configStr) : configStr;
				return {
					name: meta.name || "",
					config: parsed
				};
			} catch (e) {
				console.error("低代码配置 JSON 解析失败", e);
				return null;
			}
		}
		/** 加载并校验低代码页面 */
		async function load() {
			state.value = "loading";
			config.value = null;
			if (!VALID_PAGE_TYPES.has(pageType.value)) {
				state.value = "not-found";
				return;
			}
			if (!pageCode.value) {
				state.value = "not-found";
				return;
			}
			try {
				let allowed = true;
				try {
					allowed = await checkLowCodePermission(pageType.value, pageCode.value);
				} catch (e) {
					console.warn("低代码权限校验接口不可用，降级放行", e);
					allowed = true;
				}
				if (!allowed) {
					state.value = "forbidden";
					ElMessage.error("您没有访问该低代码页面的权限");
					return;
				}
				const result = await fetchConfig(pageType.value, pageCode.value);
				if (!result) {
					state.value = "not-found";
					return;
				}
				config.value = result.config;
				if (result.name) document.title = `${result.name} - 网络设备工程项目管理系统`;
				state.value = "done";
			} catch (e) {
				console.error("加载低代码页面失败", e);
				state.value = "error";
			}
		}
		onMounted(load);
		watch([pageType, pageCode], () => {
			load();
		});
		function goBack() {
			router.back();
		}
		return (_ctx, _cache) => {
			const _component_el_skeleton = resolveComponent("el-skeleton");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_result = resolveComponent("el-result");
			return openBlock(), createElementBlock("div", _hoisted_1, [state.value === "loading" ? (openBlock(), createBlock(_component_el_skeleton, {
				key: 0,
				rows: 10,
				animated: ""
			})) : state.value === "not-found" ? (openBlock(), createBlock(_component_el_result, {
				key: 1,
				icon: "warning",
				title: "页面不存在",
				"sub-title": "请检查页面编码或联系管理员"
			}, {
				extra: withCtx(() => [createVNode(_component_el_button, {
					type: "primary",
					onClick: goBack
				}, {
					default: withCtx(() => [..._cache[0] || (_cache[0] = [createTextVNode("返回", -1)])]),
					_: 1
				})]),
				_: 1
			})) : state.value === "forbidden" ? (openBlock(), createBlock(_component_el_result, {
				key: 2,
				icon: "error",
				title: "无访问权限",
				"sub-title": "您没有访问该低代码页面的权限，请联系管理员授权"
			}, {
				extra: withCtx(() => [createVNode(_component_el_button, {
					type: "primary",
					onClick: goBack
				}, {
					default: withCtx(() => [..._cache[1] || (_cache[1] = [createTextVNode("返回", -1)])]),
					_: 1
				})]),
				_: 1
			})) : state.value === "error" ? (openBlock(), createBlock(_component_el_result, {
				key: 3,
				icon: "error",
				title: "加载失败",
				"sub-title": "低代码页面加载异常，请稍后重试"
			}, {
				extra: withCtx(() => [createVNode(_component_el_button, {
					type: "primary",
					onClick: load
				}, {
					default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("重试", -1)])]),
					_: 1
				}), createVNode(_component_el_button, { onClick: goBack }, {
					default: withCtx(() => [..._cache[3] || (_cache[3] = [createTextVNode("返回", -1)])]),
					_: 1
				})]),
				_: 1
			})) : state.value === "done" && renderer.value && config.value ? (openBlock(), createBlock(resolveDynamicComponent(renderer.value), {
				key: 4,
				config: config.value,
				"context-data": contextData.value
			}, null, 8, ["config", "context-data"])) : createCommentVNode("", true)]);
		};
	}
}), [["__scopeId", "data-v-29956c42"]]);
//#endregion
export { render_default as default };

//# sourceMappingURL=render-CdAd4IFr.js.map