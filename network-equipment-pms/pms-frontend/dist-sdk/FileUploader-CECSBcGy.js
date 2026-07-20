import { ElMessage } from "element-plus";
import { createBlock, createTextVNode, createVNode, defineComponent, openBlock, resolveComponent, withCtx } from "vue";
//#region src/components/LowCodeWidgets/FileUploader.vue?vue&type=script&setup=true&lang.ts
var action = "/api/file/upload";
//#endregion
//#region src/components/LowCodeWidgets/FileUploader.vue
var FileUploader_default = /* @__PURE__ */ defineComponent({
	__name: "FileUploader",
	props: {
		modelValue: {},
		accept: {},
		maxSize: {}
	},
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		const emit = __emit;
		function onSuccess(resp) {
			var _resp$data;
			if (resp === null || resp === void 0 || (_resp$data = resp.data) === null || _resp$data === void 0 ? void 0 : _resp$data.id) {
				emit("update:modelValue", resp.data.id);
				ElMessage.success("上传成功");
			}
		}
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_upload = resolveComponent("el-upload");
			return openBlock(), createBlock(_component_el_upload, {
				action,
				accept: __props.accept,
				limit: 1,
				"on-success": onSuccess
			}, {
				default: withCtx(() => [createVNode(_component_el_button, { type: "primary" }, {
					default: withCtx(() => [..._cache[0] || (_cache[0] = [createTextVNode("点击上传", -1)])]),
					_: 1
				})]),
				_: 1
			}, 8, ["accept"]);
		};
	}
});
//#endregion
export { FileUploader_default as default };

//# sourceMappingURL=FileUploader-CECSBcGy.js.map