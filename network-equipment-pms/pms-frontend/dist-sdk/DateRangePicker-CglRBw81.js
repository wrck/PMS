import { createElementBlock, createVNode, defineComponent, openBlock, ref, resolveComponent, unref, watch } from "vue";
//#region src/components/LowCodeWidgets/DateRangePicker.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "widget-placeholder date-range-picker" };
//#endregion
//#region src/components/LowCodeWidgets/DateRangePicker.vue
var DateRangePicker_default = /* @__PURE__ */ defineComponent({
	__name: "DateRangePicker",
	props: {
		modelValue: {},
		format: {}
	},
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		var _props$format;
		const props = __props;
		const emit = __emit;
		const model = ref(props.modelValue);
		const format = (_props$format = props.format) !== null && _props$format !== void 0 ? _props$format : "YYYY-MM-DD";
		watch(model, (v) => emit("update:modelValue", v));
		return (_ctx, _cache) => {
			const _component_el_date_picker = resolveComponent("el-date-picker");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_date_picker, {
				modelValue: model.value,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => model.value = $event),
				type: "daterange",
				format: unref(format),
				"range-separator": "至",
				"start-placeholder": "开始",
				"end-placeholder": "结束"
			}, null, 8, ["modelValue", "format"])]);
		};
	}
});
//#endregion
export { DateRangePicker_default as default };

//# sourceMappingURL=DateRangePicker-CglRBw81.js.map