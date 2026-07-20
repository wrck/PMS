import { createElementBlock, createVNode, defineComponent, openBlock, ref, resolveComponent, unref, watch } from "vue";
//#region src/components/LowCodeWidgets/TreeSelect.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "widget-placeholder tree-select" };
//#endregion
//#region src/components/LowCodeWidgets/TreeSelect.vue
var TreeSelect_default = /* @__PURE__ */ defineComponent({
	__name: "TreeSelect",
	props: {
		modelValue: {},
		data: {}
	},
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		var _props$data;
		const props = __props;
		const emit = __emit;
		const model = ref(props.modelValue);
		const data = (_props$data = props.data) !== null && _props$data !== void 0 ? _props$data : [];
		watch(model, (v) => emit("update:modelValue", v));
		return (_ctx, _cache) => {
			const _component_el_tree_select = resolveComponent("el-tree-select");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_tree_select, {
				modelValue: model.value,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => model.value = $event),
				data: unref(data),
				placeholder: "请选择"
			}, null, 8, ["modelValue", "data"])]);
		};
	}
});
//#endregion
export { TreeSelect_default as default };

//# sourceMappingURL=TreeSelect-wZnmqs1T.js.map