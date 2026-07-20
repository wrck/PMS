import { r as get } from "./request-BQrAOfxW.js";
import { Fragment, createBlock, createElementBlock, defineComponent, openBlock, ref, renderList, resolveComponent, watch, withCtx } from "vue";
//#endregion
//#region src/components/LowCodeWidgets/DeptSelector.vue
var DeptSelector_default = /* @__PURE__ */ defineComponent({
	__name: "DeptSelector",
	props: {
		modelValue: {},
		multiple: { type: Boolean }
	},
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const depts = ref([]);
		const model = ref(props.modelValue);
		watch(model, (v) => emit("update:modelValue", v));
		async function search(query) {
			if (!query) return;
			try {
				depts.value = await get("/api/system/dept/search", { keyword: query });
			} catch (_unused) {}
		}
		return (_ctx, _cache) => {
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			return openBlock(), createBlock(_component_el_select, {
				modelValue: model.value,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => model.value = $event),
				multiple: __props.multiple,
				placeholder: "选择部门",
				filterable: "",
				remote: "",
				"remote-method": search
			}, {
				default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(depts.value, (d) => {
					return openBlock(), createBlock(_component_el_option, {
						key: d.id,
						label: d.name,
						value: d.id
					}, null, 8, ["label", "value"]);
				}), 128))]),
				_: 1
			}, 8, ["modelValue", "multiple"]);
		};
	}
});
//#endregion
export { DeptSelector_default as default };

//# sourceMappingURL=DeptSelector-D3zAHcBu.js.map