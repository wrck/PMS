import { createElementBlock, createVNode, defineComponent, openBlock, ref, resolveComponent, watch } from "vue";
//#region src/components/LowCodeWidgets/CodeEditor.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "widget-placeholder code-editor" };
var displayName = "代码编辑器";
//#endregion
//#region src/components/LowCodeWidgets/CodeEditor.vue
var CodeEditor_default = /* @__PURE__ */ defineComponent({
	__name: "CodeEditor",
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
				rows: 10,
				placeholder: displayName,
				style: { "font-family": "monospace" }
			}, null, 8, ["modelValue"])]);
		};
	}
});
//#endregion
export { CodeEditor_default as default };

//# sourceMappingURL=CodeEditor-Be9gLJp9.js.map