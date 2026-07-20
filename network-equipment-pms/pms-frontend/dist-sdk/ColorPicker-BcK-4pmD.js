import { createElementBlock, createVNode, defineComponent, openBlock, ref, resolveComponent, unref, watch } from "vue";
//#region src/components/LowCodeWidgets/ColorPicker.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "widget-placeholder color-picker" };
//#endregion
//#region src/components/LowCodeWidgets/ColorPicker.vue
var ColorPicker_default = /* @__PURE__ */ defineComponent({
	__name: "ColorPicker",
	props: {
		modelValue: {},
		showAlpha: { type: Boolean }
	},
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		var _props$showAlpha;
		const props = __props;
		const emit = __emit;
		const model = ref(props.modelValue);
		const showAlpha = (_props$showAlpha = props.showAlpha) !== null && _props$showAlpha !== void 0 ? _props$showAlpha : true;
		watch(model, (v) => emit("update:modelValue", v));
		return (_ctx, _cache) => {
			const _component_el_color_picker = resolveComponent("el-color-picker");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_color_picker, {
				modelValue: model.value,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => model.value = $event),
				"show-alpha": unref(showAlpha)
			}, null, 8, ["modelValue", "show-alpha"])]);
		};
	}
});
//#endregion
export { ColorPicker_default as default };

//# sourceMappingURL=ColorPicker-BcK-4pmD.js.map