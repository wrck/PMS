import { createElementBlock, createTextVNode, createVNode, defineComponent, openBlock, ref, resolveComponent, watch, withCtx } from "vue";
//#region src/components/LowCodeWidgets/BarcodeInput.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = {
	class: "widget-placeholder barcode-input",
	style: {
		"display": "inline-flex",
		"gap": "8px"
	}
};
var displayName = "条码扫描";
//#endregion
//#region src/components/LowCodeWidgets/BarcodeInput.vue
var BarcodeInput_default = /* @__PURE__ */ defineComponent({
	__name: "BarcodeInput",
	props: { modelValue: {} },
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const model = ref(props.modelValue);
		watch(model, (v) => emit("update:modelValue", v));
		function scan() {}
		return (_ctx, _cache) => {
			const _component_el_input = resolveComponent("el-input");
			const _component_el_button = resolveComponent("el-button");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_input, {
				modelValue: model.value,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => model.value = $event),
				placeholder: displayName
			}, null, 8, ["modelValue"]), createVNode(_component_el_button, { onClick: scan }, {
				default: withCtx(() => [..._cache[1] || (_cache[1] = [createTextVNode("扫码", -1)])]),
				_: 1
			})]);
		};
	}
});
//#endregion
export { BarcodeInput_default as default };

//# sourceMappingURL=BarcodeInput-DcddCVLS.js.map