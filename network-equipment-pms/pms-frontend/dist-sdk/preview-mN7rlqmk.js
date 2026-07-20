import { u as useRoute } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, normalizeStyle, openBlock, ref, resolveComponent, toDisplayString, withCtx } from "vue";
//#region src/views/lowcode/preview/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "preview-container" };
var _hoisted_2 = { class: "preview-toolbar" };
var _hoisted_3 = { style: {
	"margin-left": "12px",
	"color": "#909399",
	"font-size": "12px"
} };
var _hoisted_4 = ["src"];
//#endregion
//#region src/views/lowcode/preview/index.vue
var preview_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "LowCodePreviewView",
	__name: "index",
	setup(__props) {
		const route = useRoute();
		const configType = ref(route.query.configType || "FORM");
		const configCode = ref(route.query.configCode || "");
		const device = ref("pc");
		const orientation = ref("portrait");
		const deviceSize = computed(() => {
			const s = {
				pc: {
					width: 1920,
					height: 1080
				},
				tablet: {
					width: 768,
					height: 1024
				},
				mobile: {
					width: 375,
					height: 812
				}
			}[device.value];
			return orientation.value === "landscape" && device.value !== "pc" ? {
				width: s.height,
				height: s.width
			} : s;
		});
		const pageTypeMap = {
			FORM: "form",
			LIST: "list",
			TAB: "tab",
			RELATED_PAGE: "related-page"
		};
		const previewUrl = computed(() => {
			return `/lowcode/${pageTypeMap[configType.value] || configType.value.toLowerCase()}/${configCode.value}?preview=true`;
		});
		return (_ctx, _cache) => {
			const _component_el_radio_button = resolveComponent("el-radio-button");
			const _component_el_radio_group = resolveComponent("el-radio-group");
			return openBlock(), createElementBlock("div", _hoisted_1, [createElementVNode("div", _hoisted_2, [
				createVNode(_component_el_radio_group, {
					modelValue: device.value,
					"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => device.value = $event),
					size: "small"
				}, {
					default: withCtx(() => [
						createVNode(_component_el_radio_button, { value: "pc" }, {
							default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("PC", -1)])]),
							_: 1
						}),
						createVNode(_component_el_radio_button, { value: "tablet" }, {
							default: withCtx(() => [..._cache[3] || (_cache[3] = [createTextVNode("平板", -1)])]),
							_: 1
						}),
						createVNode(_component_el_radio_button, { value: "mobile" }, {
							default: withCtx(() => [..._cache[4] || (_cache[4] = [createTextVNode("手机", -1)])]),
							_: 1
						})
					]),
					_: 1
				}, 8, ["modelValue"]),
				device.value !== "pc" ? (openBlock(), createBlock(_component_el_radio_group, {
					key: 0,
					modelValue: orientation.value,
					"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => orientation.value = $event),
					size: "small",
					style: { "margin-left": "12px" }
				}, {
					default: withCtx(() => [createVNode(_component_el_radio_button, { value: "portrait" }, {
						default: withCtx(() => [..._cache[5] || (_cache[5] = [createTextVNode("竖屏", -1)])]),
						_: 1
					}), createVNode(_component_el_radio_button, { value: "landscape" }, {
						default: withCtx(() => [..._cache[6] || (_cache[6] = [createTextVNode("横屏", -1)])]),
						_: 1
					})]),
					_: 1
				}, 8, ["modelValue"])) : createCommentVNode("", true),
				createElementVNode("span", _hoisted_3, toDisplayString(deviceSize.value.width) + " × " + toDisplayString(deviceSize.value.height), 1)
			]), createElementVNode("div", {
				class: "preview-stage",
				style: normalizeStyle({
					width: deviceSize.value.width + "px",
					height: deviceSize.value.height + "px",
					maxWidth: "100%",
					maxHeight: "calc(100vh - 140px)"
				})
			}, [createElementVNode("iframe", {
				src: previewUrl.value,
				style: normalizeStyle({
					width: deviceSize.value.width + "px",
					height: deviceSize.value.height + "px",
					border: "1px solid #dcdfe6",
					transformOrigin: "top left"
				})
			}, null, 12, _hoisted_4)], 4)]);
		};
	}
}), [["__scopeId", "data-v-c4f54149"]]);
//#endregion
export { preview_default as default };

//# sourceMappingURL=preview-mN7rlqmk.js.map