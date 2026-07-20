import { r as get } from "./request-BQrAOfxW.js";
import { Fragment, createBlock, createElementBlock, defineComponent, onMounted, openBlock, ref, renderList, resolveComponent, watch, withCtx } from "vue";
//#endregion
//#region src/components/LowCodeWidgets/DictSelect.vue
var DictSelect_default = /* @__PURE__ */ defineComponent({
	__name: "DictSelect",
	props: {
		modelValue: {},
		dictCode: {}
	},
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const dictItems = ref([]);
		const model = ref(props.modelValue);
		watch(model, (v) => emit("update:modelValue", v));
		async function loadDict() {
			if (!props.dictCode) return;
			try {
				dictItems.value = await get("/api/system/dict/items", { code: props.dictCode });
			} catch (_unused) {}
		}
		onMounted(loadDict);
		watch(() => props.dictCode, loadDict);
		return (_ctx, _cache) => {
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			return openBlock(), createBlock(_component_el_select, {
				modelValue: model.value,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => model.value = $event),
				placeholder: "请选择"
			}, {
				default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(dictItems.value, (d) => {
					return openBlock(), createBlock(_component_el_option, {
						key: d.value,
						label: d.label,
						value: d.value
					}, null, 8, ["label", "value"]);
				}), 128))]),
				_: 1
			}, 8, ["modelValue"]);
		};
	}
});
//#endregion
export { DictSelect_default as default };

//# sourceMappingURL=DictSelect-CdM3Lroh.js.map