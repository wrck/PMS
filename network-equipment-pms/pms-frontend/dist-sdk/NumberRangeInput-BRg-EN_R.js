import { createElementBlock, createElementVNode, createVNode, defineComponent, openBlock, ref, resolveComponent, watch } from "vue";
//#region src/components/LowCodeWidgets/NumberRangeInput.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = {
	class: "widget-placeholder number-range-input",
	style: {
		"display": "inline-flex",
		"gap": "8px",
		"align-items": "center"
	}
};
//#endregion
//#region src/components/LowCodeWidgets/NumberRangeInput.vue
var NumberRangeInput_default = /* @__PURE__ */ defineComponent({
	__name: "NumberRangeInput",
	props: {
		modelValue: {},
		min: {},
		max: {}
	},
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		const props = __props;
		const emit = __emit;
		ref(props.modelValue);
		const minVal = ref(void 0);
		const maxVal = ref(void 0);
		watch([minVal, maxVal], () => {
			emit("update:modelValue", {
				min: minVal.value,
				max: maxVal.value
			});
		});
		return (_ctx, _cache) => {
			const _component_el_input_number = resolveComponent("el-input-number");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_input_number, {
					modelValue: minVal.value,
					"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => minVal.value = $event),
					placeholder: "最小值"
				}, null, 8, ["modelValue"]),
				_cache[2] || (_cache[2] = createElementVNode("span", null, "-", -1)),
				createVNode(_component_el_input_number, {
					modelValue: maxVal.value,
					"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => maxVal.value = $event),
					placeholder: "最大值"
				}, null, 8, ["modelValue"])
			]);
		};
	}
});
//#endregion
export { NumberRangeInput_default as default };

//# sourceMappingURL=NumberRangeInput-BRg-EN_R.js.map