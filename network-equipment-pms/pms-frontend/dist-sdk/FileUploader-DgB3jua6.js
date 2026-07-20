import { f as axios } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, normalizeClass, openBlock, ref, renderList, resolveComponent, toDisplayString, unref, withCtx, withModifiers } from "vue";
import { Close, Document, UploadFilled } from "@element-plus/icons-vue";
//#region src/components/FileUploader/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "file-uploader" };
var _hoisted_2 = { class: "drop-tip" };
var _hoisted_3 = ["accept", "multiple"];
var _hoisted_4 = {
	key: 1,
	class: "file-list"
};
var _hoisted_5 = { class: "file-thumb" };
var _hoisted_6 = ["src", "alt"];
var _hoisted_7 = { class: "file-info" };
var _hoisted_8 = ["title"];
var _hoisted_9 = { class: "file-meta" };
var _hoisted_10 = {
	key: 0,
	class: "file-time"
};
//#endregion
//#region src/components/FileUploader/index.vue
var FileUploader_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	props: {
		bizType: {},
		bizId: {},
		accept: { default: "" },
		multiple: {
			type: Boolean,
			default: false
		},
		maxSize: { default: 20 }
	},
	emits: [
		"success",
		"remove",
		"change"
	],
	setup(__props, { emit: __emit }) {
		const props = __props;
		const emit = __emit;
		/** 已上传文件列表 */
		const fileList = ref([]);
		/** 是否正在上传 */
		const uploading = ref(false);
		/** 上传进度（0-100） */
		const progress = ref(0);
		/** 拖拽悬浮状态 */
		const dragging = ref(false);
		/** 文件输入框引用 */
		const fileInput = ref(null);
		/** token */
		const token = computed(() => localStorage.getItem("pms_token") || "");
		/** 文件大小限制（字节） */
		const maxBytes = computed(() => props.maxSize * 1024 * 1024);
		/** 判断是否图片 */
		function isImage(mimeType) {
			return mimeType.startsWith("image/");
		}
		/** 缩略图 URL */
		function thumbnailUrl(file) {
			return `/api/file/${file.id}/thumbnail`;
		}
		/** 格式化文件大小 */
		function formatSize(bytes) {
			if (bytes < 1024) return bytes + " B";
			if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + " KB";
			return (bytes / 1024 / 1024).toFixed(2) + " MB";
		}
		/** 校验文件 */
		function validate(file) {
			if (file.size > maxBytes.value) {
				ElMessage.error(`文件 ${file.name} 超过最大限制 ${props.maxSize}MB`);
				return false;
			}
			return true;
		}
		/** 执行单个文件上传 */
		async function uploadOne(file) {
			var _payload$data;
			const formData = new FormData();
			formData.append("file", file);
			formData.append("bizType", props.bizType);
			if (props.bizId !== void 0) formData.append("bizId", String(props.bizId));
			const payload = (await axios.post("/api/file/upload", formData, {
				headers: { Authorization: `Bearer ${token.value}` },
				onUploadProgress: (e) => {
					if (e.total) progress.value = Math.round(e.loaded * 100 / e.total);
				}
			})).data;
			return (_payload$data = payload === null || payload === void 0 ? void 0 : payload.data) !== null && _payload$data !== void 0 ? _payload$data : payload;
		}
		/** 处理文件选择/拖拽 */
		async function handleFiles(files) {
			const list = Array.isArray(files) ? files : [files];
			if (!props.multiple && list.length > 1) ElMessage.warning("当前不支持多文件，仅上传第一个");
			uploading.value = true;
			progress.value = 0;
			try {
				for (const f of list) {
					if (!validate(f)) continue;
					const att = await uploadOne(f);
					fileList.value.push(att);
					emit("success", att);
				}
				emit("change", fileList.value);
			} catch (e) {
				var _e$response;
				const msg = e === null || e === void 0 || (_e$response = e.response) === null || _e$response === void 0 || (_e$response = _e$response.data) === null || _e$response === void 0 ? void 0 : _e$response.message;
				ElMessage.error(msg || "上传失败");
			} finally {
				uploading.value = false;
				progress.value = 0;
			}
		}
		/** input change 事件 */
		function onInputChange(e) {
			const input = e.target;
			if (input.files && input.files.length) {
				handleFiles(Array.from(input.files));
				input.value = "";
			}
		}
		/** 触发文件选择 */
		function triggerSelect() {
			var _fileInput$value;
			(_fileInput$value = fileInput.value) === null || _fileInput$value === void 0 || _fileInput$value.click();
		}
		/** 拖拽事件 */
		function onDrop(e) {
			var _e$dataTransfer;
			dragging.value = false;
			if ((_e$dataTransfer = e.dataTransfer) === null || _e$dataTransfer === void 0 || (_e$dataTransfer = _e$dataTransfer.files) === null || _e$dataTransfer === void 0 ? void 0 : _e$dataTransfer.length) handleFiles(Array.from(e.dataTransfer.files));
		}
		function onDragOver() {
			dragging.value = true;
		}
		function onDragLeave() {
			dragging.value = false;
		}
		/** 删除已上传文件 */
		async function handleRemove(file, event) {
			event.stopPropagation();
			try {
				await axios.delete(`/api/file/${file.id}`, { headers: { Authorization: `Bearer ${token.value}` } });
				const idx = fileList.value.findIndex((f) => f.id === file.id);
				if (idx !== -1) fileList.value.splice(idx, 1);
				emit("remove", file.id);
				emit("change", fileList.value);
			} catch (e) {
				var _e$response2;
				const msg = e === null || e === void 0 || (_e$response2 = e.response) === null || _e$response2 === void 0 || (_e$response2 = _e$response2.data) === null || _e$response2 === void 0 ? void 0 : _e$response2.message;
				ElMessage.error(msg || "删除失败");
			}
		}
		return (_ctx, _cache) => {
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_progress = resolveComponent("el-progress");
			const _component_el_tag = resolveComponent("el-tag");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createElementVNode("div", {
					class: normalizeClass(["drop-zone", { dragging: dragging.value }]),
					onDragover: withModifiers(onDragOver, ["prevent"]),
					onDragleave: withModifiers(onDragLeave, ["prevent"]),
					onDrop: withModifiers(onDrop, ["prevent"]),
					onClick: triggerSelect
				}, [
					createVNode(_component_el_icon, { class: "drop-icon" }, {
						default: withCtx(() => [createVNode(unref(UploadFilled))]),
						_: 1
					}),
					_cache[0] || (_cache[0] = createElementVNode("div", { class: "drop-text" }, [createTextVNode("将文件拖到此处，或"), createElementVNode("em", null, "点击上传")], -1)),
					createElementVNode("div", _hoisted_2, " 支持 " + toDisplayString(__props.accept || "所有") + " 类型，单个文件不超过 " + toDisplayString(__props.maxSize) + "MB ", 1),
					createElementVNode("input", {
						ref_key: "fileInput",
						ref: fileInput,
						type: "file",
						class: "file-input",
						accept: __props.accept,
						multiple: __props.multiple,
						onChange: onInputChange
					}, null, 40, _hoisted_3)
				], 34),
				uploading.value ? (openBlock(), createBlock(_component_el_progress, {
					key: 0,
					percentage: progress.value,
					"stroke-width": 6,
					class: "upload-progress"
				}, null, 8, ["percentage"])) : createCommentVNode("", true),
				fileList.value.length ? (openBlock(), createElementBlock("ul", _hoisted_4, [(openBlock(true), createElementBlock(Fragment, null, renderList(fileList.value, (file) => {
					return openBlock(), createElementBlock("li", {
						key: file.id,
						class: "file-item"
					}, [
						createElementVNode("div", _hoisted_5, [isImage(file.mimeType) ? (openBlock(), createElementBlock("img", {
							key: 0,
							src: thumbnailUrl(file),
							alt: file.fileName
						}, null, 8, _hoisted_6)) : (openBlock(), createBlock(_component_el_icon, { key: 1 }, {
							default: withCtx(() => [createVNode(unref(Document))]),
							_: 1
						}))]),
						createElementVNode("div", _hoisted_7, [createElementVNode("div", {
							class: "file-name",
							title: file.fileName
						}, toDisplayString(file.fileName), 9, _hoisted_8), createElementVNode("div", _hoisted_9, [
							createElementVNode("span", null, toDisplayString(formatSize(file.fileSize)), 1),
							file.uploadTime ? (openBlock(), createElementBlock("span", _hoisted_10, toDisplayString(file.uploadTime), 1)) : createCommentVNode("", true),
							file.geoFenceStatus ? (openBlock(), createBlock(_component_el_tag, {
								key: 1,
								size: "small",
								type: "warning",
								effect: "plain"
							}, {
								default: withCtx(() => [createTextVNode(toDisplayString(file.geoFenceStatus), 1)]),
								_: 2
							}, 1024)) : createCommentVNode("", true)
						])]),
						createVNode(_component_el_icon, {
							class: "remove-icon",
							onClick: ($event) => handleRemove(file, $event)
						}, {
							default: withCtx(() => [createVNode(unref(Close))]),
							_: 1
						}, 8, ["onClick"])
					]);
				}), 128))])) : createCommentVNode("", true)
			]);
		};
	}
}), [["__scopeId", "data-v-2c759b81"]]);
//#endregion
export { FileUploader_default as t };

//# sourceMappingURL=FileUploader-DgB3jua6.js.map