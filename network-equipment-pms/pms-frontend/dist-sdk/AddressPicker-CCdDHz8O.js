import { createElementBlock, createVNode, defineComponent, openBlock, ref, resolveComponent, watch } from "vue";
//#region src/components/LowCodeWidgets/AddressPicker.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = {
	class: "widget-placeholder address-picker",
	style: {
		"display": "inline-flex",
		"gap": "8px"
	}
};
//#endregion
//#region src/components/LowCodeWidgets/AddressPicker.vue
var AddressPicker_default = /* @__PURE__ */ defineComponent({
	__name: "AddressPicker",
	props: {
		modelValue: {},
		level: {}
	},
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		const emit = __emit;
		const province = ref("");
		const city = ref("");
		const district = ref("");
		watch([
			province,
			city,
			district
		], () => {
			emit("update:modelValue", {
				province: province.value,
				city: city.value,
				district: district.value
			});
		});
		return (_ctx, _cache) => {
			const _component_el_select = resolveComponent("el-select");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_select, {
					modelValue: province.value,
					"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => province.value = $event),
					placeholder: "省"
				}, null, 8, ["modelValue"]),
				createVNode(_component_el_select, {
					modelValue: city.value,
					"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => city.value = $event),
					placeholder: "市"
				}, null, 8, ["modelValue"]),
				createVNode(_component_el_select, {
					modelValue: district.value,
					"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => district.value = $event),
					placeholder: "区"
				}, null, 8, ["modelValue"])
			]);
		};
	}
});
//#endregion
export { AddressPicker_default as default };

//# sourceMappingURL=AddressPicker-CCdDHz8O.js.map