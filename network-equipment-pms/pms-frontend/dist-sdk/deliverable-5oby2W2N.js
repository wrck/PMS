import { a as put, i as post, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { t as FileUploader_default } from "./FileUploader-DgB3jua6.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, openBlock, ref, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withModifiers } from "vue";
//#region src/api/deliverable.ts
function listDeliverables(projectId) {
	return get(`/api/project/deliverable-checklist/project/${projectId}`);
}
function initChecklist(projectId) {
	return post(`/api/project/deliverable-checklist/project/${projectId}/init`);
}
function markUploaded(id, attachmentId) {
	return put("/api/project/deliverable-checklist", {
		id,
		attachmentId,
		uploaded: true
	});
}
/** 取消已上传的附件标记 */
function cancelUploaded(id) {
	return put("/api/project/deliverable-checklist", {
		id,
		attachmentId: null,
		uploaded: false
	});
}
/** 附件下载地址 */
function downloadAttachment(id) {
	return `/api/file/${id}/download`;
}
//#endregion
//#region src/views/deliverable/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = {
	key: 0,
	class: "validate-bar"
};
var _hoisted_3 = {
	key: 0,
	class: "upload-tip"
};
//#endregion
//#region src/views/deliverable/index.vue
var deliverable_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const loading = ref(false);
		const tableData = ref([]);
		const projectId = ref(void 0);
		const typeLabels = {
			AS_BUILT: "竣工图",
			TEST_REPORT: "测试报告",
			ACCEPTANCE_CERT: "验收证书",
			TRAINING_RECORD: "培训记录",
			OPERATION_MANUAL: "操作手册",
			ASSET_REGISTER: "资产清单",
			WARRANTY_CERT: "质保证书",
			SPARE_PARTS_LIST: "备件清单"
		};
		function typeLabel(type) {
			var _ref, _typeLabels;
			return (_ref = (_typeLabels = typeLabels[type !== null && type !== void 0 ? type : ""]) !== null && _typeLabels !== void 0 ? _typeLabels : type) !== null && _ref !== void 0 ? _ref : "-";
		}
		function formatDateTime(val) {
			var _val$replace$slice;
			return (_val$replace$slice = val === null || val === void 0 ? void 0 : val.replace("T", " ").slice(0, 19)) !== null && _val$replace$slice !== void 0 ? _val$replace$slice : "-";
		}
		function uploadedMeta(row) {
			if (row.uploaded) return {
				tagType: "success",
				label: "已上传"
			};
			if (row.required) return {
				tagType: "danger",
				label: "未上传"
			};
			return {
				tagType: "info",
				label: "无需上传"
			};
		}
		function requiredMeta(row) {
			return row.required ? {
				tagType: "warning",
				label: "必需"
			} : {
				tagType: "info",
				label: "可选"
			};
		}
		const pendingCount = computed(() => {
			return tableData.value.filter((r) => r.required && !r.uploaded).length;
		});
		const uploadVisible = ref(false);
		const currentRow = ref(null);
		async function loadData() {
			if (!projectId.value) {
				ElMessage.warning("请输入项目 ID");
				return;
			}
			loading.value = true;
			try {
				const res = await listDeliverables(projectId.value);
				tableData.value = res !== null && res !== void 0 ? res : [];
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		function handleSearch() {
			loadData();
		}
		function handleInit() {
			if (!projectId.value) {
				ElMessage.warning("请输入项目 ID");
				return;
			}
			ElMessageBox.confirm(`确认为项目「${projectId.value}」初始化终验交付物清单吗？`, "初始化清单", { type: "warning" }).then(async () => {
				await initChecklist(projectId.value);
				ElMessage.success("清单已初始化");
				loadData();
			}).catch(() => {});
		}
		function handleUpload(row) {
			currentRow.value = row;
			uploadVisible.value = true;
		}
		async function handleUploaded(payload) {
			const row = currentRow.value;
			if (!(row === null || row === void 0 ? void 0 : row.id)) return;
			const attachmentId = typeof payload === "number" ? payload : payload === null || payload === void 0 ? void 0 : payload.id;
			if (typeof attachmentId !== "number") {
				ElMessage.warning("未获取到附件 ID");
				return;
			}
			try {
				await markUploaded(row.id, attachmentId);
				ElMessage.success("已标记上传");
				uploadVisible.value = false;
				loadData();
			} catch (_unused2) {}
		}
		function handleDownload(row) {
			if (!row.attachmentId) return;
			window.open(downloadAttachment(row.attachmentId), "_blank");
		}
		function handleCancelUpload(row) {
			if (!row.id) return;
			ElMessageBox.confirm(`确认取消「${typeLabel(row.deliverableType)}」的上传标记吗？`, "取消上传", { type: "warning" }).then(async () => {
				await cancelUploaded(row.id);
				ElMessage.success("已取消上传");
				loadData();
			}).catch(() => {});
		}
		return (_ctx, _cache) => {
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_card, { shadow: "never" }, {
				header: withCtx(() => [..._cache[4] || (_cache[4] = [createElementVNode("span", { class: "page-title" }, "终验交付物清单", -1)])]),
				default: withCtx(() => [
					createVNode(_component_el_form, {
						inline: true,
						onSubmit: _cache[1] || (_cache[1] = withModifiers(() => {}, ["prevent"]))
					}, {
						default: withCtx(() => [createVNode(_component_el_form_item, { label: "项目 ID" }, {
							default: withCtx(() => [createVNode(_component_el_input_number, {
								modelValue: projectId.value,
								"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => projectId.value = $event),
								min: 1,
								controls: false,
								placeholder: "请输入项目 ID",
								style: { "width": "180px" }
							}, null, 8, ["modelValue"])]),
							_: 1
						}), createVNode(_component_el_form_item, null, {
							default: withCtx(() => [createVNode(_component_el_button, {
								type: "primary",
								icon: "Search",
								onClick: handleSearch
							}, {
								default: withCtx(() => [..._cache[5] || (_cache[5] = [createTextVNode("查询", -1)])]),
								_: 1
							}), createVNode(_component_el_button, {
								type: "success",
								icon: "Files",
								onClick: handleInit
							}, {
								default: withCtx(() => [..._cache[6] || (_cache[6] = [createTextVNode("初始化清单", -1)])]),
								_: 1
							})]),
							_: 1
						})]),
						_: 1
					}),
					withDirectives((openBlock(), createBlock(_component_el_table, {
						data: tableData.value,
						border: "",
						stripe: ""
					}, {
						empty: withCtx(() => [createVNode(_component_el_empty, { description: "请输入项目 ID 并查询，或点击「初始化清单」" })]),
						default: withCtx(() => [
							createVNode(_component_el_table_column, {
								prop: "id",
								label: "ID",
								width: "70"
							}),
							createVNode(_component_el_table_column, {
								label: "交付物类型",
								"min-width": "140"
							}, {
								default: withCtx(({ row }) => [createTextVNode(toDisplayString(typeLabel(row.deliverableType)), 1)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "是否必需",
								width: "100",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, {
									type: requiredMeta(row).tagType,
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(requiredMeta(row).label), 1)]),
									_: 2
								}, 1032, ["type"])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "上传状态",
								width: "110",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, {
									type: uploadedMeta(row).tagType,
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(uploadedMeta(row).label), 1)]),
									_: 2
								}, 1032, ["type"])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								prop: "attachmentId",
								label: "附件 ID",
								width: "100",
								align: "center"
							}, {
								default: withCtx(({ row }) => {
									var _row$attachmentId;
									return [createTextVNode(toDisplayString((_row$attachmentId = row.attachmentId) !== null && _row$attachmentId !== void 0 ? _row$attachmentId : "-"), 1)];
								}),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "确认时间",
								width: "160",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDateTime(row.checkedAt)), 1)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "操作",
								width: "220",
								fixed: "right"
							}, {
								default: withCtx(({ row }) => [
									createVNode(_component_el_button, {
										link: "",
										type: "primary",
										onClick: ($event) => handleUpload(row)
									}, {
										default: withCtx(() => [..._cache[7] || (_cache[7] = [createTextVNode("上传附件", -1)])]),
										_: 1
									}, 8, ["onClick"]),
									row.attachmentId ? (openBlock(), createBlock(_component_el_button, {
										key: 0,
										link: "",
										type: "primary",
										onClick: ($event) => handleDownload(row)
									}, {
										default: withCtx(() => [..._cache[8] || (_cache[8] = [createTextVNode(" 下载 ", -1)])]),
										_: 1
									}, 8, ["onClick"])) : createCommentVNode("", true),
									row.uploaded ? (openBlock(), createBlock(_component_el_button, {
										key: 1,
										link: "",
										type: "danger",
										onClick: ($event) => handleCancelUpload(row)
									}, {
										default: withCtx(() => [..._cache[9] || (_cache[9] = [createTextVNode(" 取消上传 ", -1)])]),
										_: 1
									}, 8, ["onClick"])) : createCommentVNode("", true)
								]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["data"])), [[_directive_loading, loading.value]]),
					tableData.value.length > 0 ? (openBlock(), createElementBlock("div", _hoisted_2, [createVNode(_component_el_tag, {
						type: pendingCount.value === 0 ? "success" : "warning",
						size: "large"
					}, {
						default: withCtx(() => [createTextVNode(toDisplayString(pendingCount.value === 0 ? "✓ 全部必需项已上传，可以提交终验" : `还有 ${pendingCount.value} 项未上传`), 1)]),
						_: 1
					}, 8, ["type"])])) : createCommentVNode("", true)
				]),
				_: 1
			}), createVNode(_component_el_dialog, {
				modelValue: uploadVisible.value,
				"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => uploadVisible.value = $event),
				title: "上传交付物",
				width: "520px",
				"destroy-on-close": ""
			}, {
				footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[2] || (_cache[2] = ($event) => uploadVisible.value = false) }, {
					default: withCtx(() => [..._cache[11] || (_cache[11] = [createTextVNode("关闭", -1)])]),
					_: 1
				})]),
				default: withCtx(() => [currentRow.value ? (openBlock(), createElementBlock("div", _hoisted_3, [_cache[10] || (_cache[10] = createTextVNode(" 当前交付物：", -1)), createElementVNode("strong", null, toDisplayString(typeLabel(currentRow.value.deliverableType)), 1)])) : createCommentVNode("", true), createVNode(FileUploader_default, {
					"biz-type": "DELIVERABLE",
					onUploaded: handleUploaded
				})]),
				_: 1
			}, 8, ["modelValue"])]);
		};
	}
}), [["__scopeId", "data-v-9bbdb056"]]);
//#endregion
export { deliverable_default as default };

//# sourceMappingURL=deliverable-5oby2W2N.js.map