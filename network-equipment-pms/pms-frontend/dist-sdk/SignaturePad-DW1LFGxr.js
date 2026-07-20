import { createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, openBlock, ref, resolveComponent, unref, withCtx } from "vue";
//#region src/components/LowCodeWidgets/SignaturePad.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "widget-placeholder signature-pad" };
var _hoisted_2 = ["width", "height"];
var _hoisted_3 = { style: { "margin-top": "8px" } };
//#endregion
//#region src/components/LowCodeWidgets/SignaturePad.vue
var SignaturePad_default = /* @__PURE__ */ defineComponent({
	__name: "SignaturePad",
	props: {
		modelValue: {},
		width: {},
		height: {}
	},
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		var _props$width, _props$height;
		const props = __props;
		const emit = __emit;
		const width = (_props$width = props.width) !== null && _props$width !== void 0 ? _props$width : 400;
		const height = (_props$height = props.height) !== null && _props$height !== void 0 ? _props$height : 200;
		const canvasRef = ref(null);
		function clear() {
			const canvas = canvasRef.value;
			if (!canvas) return;
			const ctx = canvas.getContext("2d");
			if (ctx) ctx.clearRect(0, 0, canvas.width, canvas.height);
			emit("update:modelValue", "");
		}
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			return openBlock(), createElementBlock("div", _hoisted_1, [createElementVNode("canvas", {
				ref_key: "canvasRef",
				ref: canvasRef,
				width: unref(width),
				height: unref(height),
				style: {
					"border": "1px solid #dcdfe6",
					"background": "#fafafa"
				}
			}, null, 8, _hoisted_2), createElementVNode("div", _hoisted_3, [createVNode(_component_el_button, {
				size: "small",
				onClick: clear
			}, {
				default: withCtx(() => [..._cache[0] || (_cache[0] = [createTextVNode("清空", -1)])]),
				_: 1
			})])]);
		};
	}
});
//#endregion
export { SignaturePad_default as default };

//# sourceMappingURL=SignaturePad-DW1LFGxr.js.map