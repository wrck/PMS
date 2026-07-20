import { createElementBlock, createElementVNode, defineComponent, normalizeStyle, openBlock, ref, toDisplayString, unref } from "vue";
//#region src/components/LowCodeWidgets/QrcodeDisplay.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "widget-placeholder qrcode-display" };
//#endregion
//#region src/components/LowCodeWidgets/QrcodeDisplay.vue
var QrcodeDisplay_default = /* @__PURE__ */ defineComponent({
	__name: "QrcodeDisplay",
	props: {
		modelValue: {},
		size: {}
	},
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		var _props$size;
		const props = __props;
		const size = (_props$size = props.size) !== null && _props$size !== void 0 ? _props$size : 128;
		const model = ref(props.modelValue);
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", _hoisted_1, [createElementVNode("div", { style: normalizeStyle({
				width: unref(size) + "px",
				height: unref(size) + "px",
				border: "1px solid #dcdfe6",
				display: "flex",
				alignItems: "center",
				justifyContent: "center",
				color: "#909399"
			}) }, toDisplayString(model.value || "二维码占位"), 5)]);
		};
	}
});
//#endregion
export { QrcodeDisplay_default as default };

//# sourceMappingURL=QrcodeDisplay-CgV4JN8J.js.map