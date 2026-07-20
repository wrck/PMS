import { createElementBlock, createVNode, defineComponent, openBlock, ref, resolveComponent, watch } from "vue";
//#region src/components/LowCodeWidgets/RichTextEditor.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "widget-placeholder rich-text-editor" };
var displayName = "富文本编辑器";
//#endregion
//#region src/components/LowCodeWidgets/RichTextEditor.vue
var RichTextEditor_default = /* @__PURE__ */ defineComponent({
	__name: "RichTextEditor",
	props: { modelValue: {} },
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const model = ref(props.modelValue);
		watch(model, (v) => emit("update:modelValue", v));
		return (_ctx, _cache) => {
			const _component_el_input = resolveComponent("el-input");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_input, {
				modelValue: model.value,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => model.value = $event),
				type: "textarea",
				rows: 8,
				placeholder: displayName
			}, null, 8, ["modelValue"])]);
		};
	}
});
//#endregion
export { RichTextEditor_default as default };

//# sourceMappingURL=RichTextEditor-DN8onT2b.js.map