import { r as get } from "./request-BQrAOfxW.js";
import { Fragment, createBlock, createElementBlock, defineComponent, openBlock, ref, renderList, resolveComponent, watch, withCtx } from "vue";
//#endregion
//#region src/components/LowCodeWidgets/UserSelector.vue
var UserSelector_default = /* @__PURE__ */ defineComponent({
	__name: "UserSelector",
	props: {
		modelValue: {},
		multiple: { type: Boolean }
	},
	emits: ["update:modelValue"],
	setup(__props, { expose: __expose, emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const users = ref([]);
		const model = ref(props.modelValue);
		function sync(v) {
			model.value = v;
			emit("update:modelValue", v);
		}
		__expose({ sync });
		watch(model, (v) => emit("update:modelValue", v));
		async function search(query) {
			if (!query) return;
			try {
				users.value = await get("/api/system/user/search", { keyword: query });
			} catch (_unused) {}
		}
		return (_ctx, _cache) => {
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			return openBlock(), createBlock(_component_el_select, {
				modelValue: model.value,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => model.value = $event),
				multiple: __props.multiple,
				placeholder: "选择用户",
				filterable: "",
				remote: "",
				"remote-method": search
			}, {
				default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(users.value, (u) => {
					return openBlock(), createBlock(_component_el_option, {
						key: u.id,
						label: u.name,
						value: u.id
					}, null, 8, ["label", "value"]);
				}), 128))]),
				_: 1
			}, 8, ["modelValue", "multiple"]);
		};
	}
});
//#endregion
export { UserSelector_default as default };

//# sourceMappingURL=UserSelector-DnXkhH1e.js.map