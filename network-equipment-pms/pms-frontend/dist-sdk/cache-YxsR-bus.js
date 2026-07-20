import { i as post, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, createBlock, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives } from "vue";
//#region src/api/system-cache.ts
/**
* 缓存管理 API。
* 对应后端 CacheManagementController（/api/system/cache）。
*/
/** 获取全部缓存名称列表 */
function getCacheNames() {
	return get("/api/system/cache/names");
}
/** 清除指定名称的缓存 */
function clearCache(name) {
	return post(`/api/system/cache/clear/${encodeURIComponent(name)}`);
}
/** 清除全部缓存 */
function clearAllCache() {
	return post("/api/system/cache/clearAll");
}
//#endregion
//#region src/views/system/cache/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "page-header" };
var _hoisted_3 = {
	key: 0,
	class: "empty-wrap"
};
var _hoisted_4 = { class: "cache-card-body" };
var _hoisted_5 = ["title"];
//#endregion
//#region src/views/system/cache/index.vue
var cache_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "SystemCache",
	__name: "index",
	setup(__props) {
		const loading = ref(false);
		const clearing = ref(false);
		const clearingName = ref("");
		const cacheNames = ref([]);
		async function loadNames() {
			loading.value = true;
			try {
				const res = await getCacheNames();
				cacheNames.value = Array.isArray(res) ? res : [];
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		async function handleClear(name) {
			try {
				await ElMessageBox.confirm(`确定清除缓存「${name}」吗？`, "提示", { type: "warning" });
			} catch (_unused2) {
				return;
			}
			clearingName.value = name;
			try {
				await clearCache(name);
				ElMessage.success(`缓存「${name}」已清除`);
				loadNames();
			} catch (_unused3) {} finally {
				clearingName.value = "";
			}
		}
		async function handleClearAll() {
			try {
				await ElMessageBox.confirm("确定清除全部缓存吗？该操作可能影响系统性能。", "危险操作", {
					type: "error",
					confirmButtonText: "确定清除",
					cancelButtonText: "取消"
				});
			} catch (_unused4) {
				return;
			}
			clearing.value = true;
			try {
				await clearAllCache();
				ElMessage.success("全部缓存已清除");
				loadNames();
			} catch (_unused5) {} finally {
				clearing.value = false;
			}
		}
		onMounted(() => {
			loadNames();
		});
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_Coin = resolveComponent("Coin");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_row = resolveComponent("el-row");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [createElementVNode("div", _hoisted_2, [_cache[1] || (_cache[1] = createElementVNode("span", { class: "page-title" }, "缓存管理", -1)), createVNode(_component_el_button, {
				type: "danger",
				icon: "Delete",
				loading: clearing.value,
				disabled: cacheNames.value.length === 0,
				onClick: handleClearAll
			}, {
				default: withCtx(() => [..._cache[0] || (_cache[0] = [createTextVNode(" 清除全部缓存 ", -1)])]),
				_: 1
			}, 8, ["loading", "disabled"])]), withDirectives((openBlock(), createBlock(_component_el_card, { shadow: "never" }, {
				default: withCtx(() => [cacheNames.value.length === 0 && !loading.value ? (openBlock(), createElementBlock("div", _hoisted_3, [createVNode(_component_el_empty, { description: "暂无缓存数据" })])) : (openBlock(), createBlock(_component_el_row, {
					key: 1,
					gutter: 16
				}, {
					default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(cacheNames.value, (name) => {
						return openBlock(), createBlock(_component_el_col, {
							key: name,
							xs: 24,
							sm: 12,
							md: 8,
							lg: 6
						}, {
							default: withCtx(() => [createVNode(_component_el_card, {
								shadow: "hover",
								class: "cache-card"
							}, {
								default: withCtx(() => [createElementVNode("div", _hoisted_4, [createElementVNode("div", {
									class: "cache-name",
									title: name
								}, [createVNode(_component_el_icon, { class: "cache-icon" }, {
									default: withCtx(() => [createVNode(_component_Coin)]),
									_: 1
								}), createElementVNode("span", null, toDisplayString(name), 1)], 8, _hoisted_5), createVNode(_component_el_button, {
									type: "primary",
									plain: "",
									size: "small",
									loading: clearingName.value === name,
									onClick: ($event) => handleClear(name)
								}, {
									default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode(" 清除 ", -1)])]),
									_: 1
								}, 8, ["loading", "onClick"])])]),
								_: 2
							}, 1024)]),
							_: 2
						}, 1024);
					}), 128))]),
					_: 1
				}))]),
				_: 1
			})), [[_directive_loading, loading.value]])]);
		};
	}
}), [["__scopeId", "data-v-e1abfd5b"]]);
//#endregion
export { cache_default as default };

//# sourceMappingURL=cache-YxsR-bus.js.map