import { r as uploadExcel, t as downloadExcel } from "./excel-BtLU3Vmp.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { _ as listTransfers, a as approveTransfer, b as updateAsset, c as deleteAsset, d as getAsset, f as getAssetLifecycle, h as listAssets, i as applyTransfer, m as inboundAsset, n as TRANSFER_STATUS, r as allocateAsset, t as ASSET_STATUS, v as rejectTransfer, y as returnAsset } from "./asset-DozMltVh.js";
import { t as MobileListCard_default } from "./MobileListCard-DJUDXCEp.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, unref, withCtx, withDirectives, withKeys, withModifiers } from "vue";
import { Download, Upload, UploadFilled } from "@element-plus/icons-vue";
//#region src/components/ExcelImportExport/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$1 = { class: "excel-import-export" };
var _hoisted_2$1 = {
	key: 0,
	class: "result-summary"
};
//#endregion
//#region src/components/ExcelImportExport/index.vue
var ExcelImportExport_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	props: {
		templateUrl: { default: "" },
		importUrl: { default: "" },
		exportUrl: { default: "" },
		exportFileName: { default: "export.xlsx" },
		templateFileName: { default: "template.xlsx" },
		exportParams: {}
	},
	emits: ["refresh", "imported"],
	setup(__props, { emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const uploading = ref(false);
		const resultVisible = ref(false);
		const importResult = ref(null);
		/** el-upload 绑定的文件列表，用于在 :show-file-list 模式下控制显示与清理 */
		const fileList = ref([]);
		/** 下载模板 */
		async function handleDownloadTemplate() {
			if (!props.templateUrl) {
				ElMessage.warning("未配置模板下载地址");
				return;
			}
			try {
				await downloadExcel(props.templateUrl, void 0, props.templateFileName);
				ElMessage.success("模板下载成功");
			} catch (e) {
				var _e$response;
				const msg = e === null || e === void 0 || (_e$response = e.response) === null || _e$response === void 0 || (_e$response = _e$response.data) === null || _e$response === void 0 ? void 0 : _e$response.message;
				ElMessage.error(msg || "模板下载失败");
			}
		}
		/** 导出当前列表 */
		async function handleExport() {
			if (!props.exportUrl) {
				ElMessage.warning("未配置导出地址");
				return;
			}
			try {
				await downloadExcel(props.exportUrl, props.exportParams, props.exportFileName);
				ElMessage.success("导出成功");
			} catch (e) {
				var _e$response2;
				const msg = e === null || e === void 0 || (_e$response2 = e.response) === null || _e$response2 === void 0 || (_e$response2 = _e$response2.data) === null || _e$response2 === void 0 ? void 0 : _e$response2.message;
				ElMessage.error(msg || "导出失败");
			}
		}
		/**
		* el-upload 的 :on-change 钩子。因为 :auto-upload=false 不会自动上传，
		* 在文件被选入时手动用 axios FormData 上传。仅在文件状态为 ready 时触发，
		* 避免对上传完成/移除等状态重复触发。
		*/
		async function handleFileChange(uploadFile, uploadFiles) {
			if (uploadFile.status !== "ready") return;
			if (!props.importUrl) {
				ElMessage.warning("未配置导入地址");
				fileList.value = [];
				return;
			}
			const raw = uploadFile.raw;
			if (!raw) return;
			if (!validateFile(raw)) {
				fileList.value = [];
				return;
			}
			uploading.value = true;
			try {
				var _result$successList$l, _result$successList, _result$errors$length, _result$errors;
				const result = await uploadExcel(props.importUrl, raw);
				importResult.value = result;
				resultVisible.value = true;
				const successCount = (_result$successList$l = (_result$successList = result.successList) === null || _result$successList === void 0 ? void 0 : _result$successList.length) !== null && _result$successList$l !== void 0 ? _result$successList$l : 0;
				const errorCount = (_result$errors$length = (_result$errors = result.errors) === null || _result$errors === void 0 ? void 0 : _result$errors.length) !== null && _result$errors$length !== void 0 ? _result$errors$length : 0;
				if (successCount > 0) {
					ElMessage.success(`导入完成：成功 ${successCount} 条${errorCount > 0 ? `，失败 ${errorCount} 条` : ""}`);
					emit("refresh");
				} else if (errorCount > 0) ElMessage.warning(`导入失败：${errorCount} 条数据校验未通过`);
				else ElMessage.info("导入文件为空");
				emit("imported", result);
			} catch (e) {
				var _e$response3;
				const msg = e === null || e === void 0 || (_e$response3 = e.response) === null || _e$response3 === void 0 || (_e$response3 = _e$response3.data) === null || _e$response3 === void 0 ? void 0 : _e$response3.message;
				ElMessage.error(msg || "导入失败");
			} finally {
				uploading.value = false;
				fileList.value = [];
			}
		}
		/**
		* 校验待上传文件：仅允许 .xlsx，且大小不超过 20MB。
		*
		* @param file 待校验文件
		*/
		function validateFile(file) {
			if (!(file.type === "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" || file.name.toLowerCase().endsWith(".xlsx"))) {
				ElMessage.error("仅支持 .xlsx 格式文件");
				return false;
			}
			if (file.size > 20 * 1024 * 1024) {
				ElMessage.error("文件大小不能超过 20MB");
				return false;
			}
			return true;
		}
		/** 关闭结果对话框 */
		function handleCloseResult() {
			resultVisible.value = false;
		}
		/** 获取错误表格数据 */
		function errorRows() {
			var _importResult$value$e, _importResult$value;
			return (_importResult$value$e = (_importResult$value = importResult.value) === null || _importResult$value === void 0 ? void 0 : _importResult$value.errors) !== null && _importResult$value$e !== void 0 ? _importResult$value$e : [];
		}
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_upload = resolveComponent("el-upload");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_dialog = resolveComponent("el-dialog");
			return openBlock(), createElementBlock("div", _hoisted_1$1, [
				props.templateUrl ? (openBlock(), createBlock(_component_el_button, {
					key: 0,
					icon: unref(Download),
					onClick: handleDownloadTemplate
				}, {
					default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode(" 下载模板 ", -1)])]),
					_: 1
				}, 8, ["icon"])) : createCommentVNode("", true),
				props.importUrl ? (openBlock(), createBlock(_component_el_upload, {
					key: 1,
					"file-list": fileList.value,
					"onUpdate:fileList": _cache[0] || (_cache[0] = ($event) => fileList.value = $event),
					class: "upload-inline",
					"show-file-list": false,
					"auto-upload": false,
					"on-change": handleFileChange,
					accept: ".xlsx"
				}, {
					default: withCtx(() => [createVNode(_component_el_button, {
						icon: unref(Upload),
						loading: uploading.value
					}, {
						default: withCtx(() => [..._cache[3] || (_cache[3] = [createTextVNode("导入", -1)])]),
						_: 1
					}, 8, ["icon", "loading"])]),
					_: 1
				}, 8, ["file-list"])) : createCommentVNode("", true),
				props.exportUrl ? (openBlock(), createBlock(_component_el_button, {
					key: 2,
					icon: unref(UploadFilled),
					onClick: handleExport
				}, {
					default: withCtx(() => [..._cache[4] || (_cache[4] = [createTextVNode(" 导出 ", -1)])]),
					_: 1
				}, 8, ["icon"])) : createCommentVNode("", true),
				createVNode(_component_el_dialog, {
					modelValue: resultVisible.value,
					"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => resultVisible.value = $event),
					title: "导入结果",
					width: "720px",
					"destroy-on-close": "",
					onClose: handleCloseResult
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, {
						type: "primary",
						onClick: handleCloseResult
					}, {
						default: withCtx(() => [..._cache[5] || (_cache[5] = [createTextVNode("确定", -1)])]),
						_: 1
					})]),
					default: withCtx(() => {
						var _importResult$value$e2, _importResult$value$e3, _importResult$value$s3, _importResult$value$s4;
						return [importResult.value ? (openBlock(), createElementBlock("div", _hoisted_2$1, [createVNode(_component_el_tag, {
							type: "success",
							size: "large"
						}, {
							default: withCtx(() => {
								var _importResult$value$s, _importResult$value$s2;
								return [createTextVNode(" 成功 " + toDisplayString((_importResult$value$s = (_importResult$value$s2 = importResult.value.successList) === null || _importResult$value$s2 === void 0 ? void 0 : _importResult$value$s2.length) !== null && _importResult$value$s !== void 0 ? _importResult$value$s : 0) + " 条 ", 1)];
							}),
							_: 1
						}), createVNode(_component_el_tag, {
							type: ((_importResult$value$e2 = (_importResult$value$e3 = importResult.value.errors) === null || _importResult$value$e3 === void 0 ? void 0 : _importResult$value$e3.length) !== null && _importResult$value$e2 !== void 0 ? _importResult$value$e2 : 0) > 0 ? "danger" : "info",
							size: "large"
						}, {
							default: withCtx(() => {
								var _importResult$value$e4, _importResult$value$e5;
								return [createTextVNode(" 失败 " + toDisplayString((_importResult$value$e4 = (_importResult$value$e5 = importResult.value.errors) === null || _importResult$value$e5 === void 0 ? void 0 : _importResult$value$e5.length) !== null && _importResult$value$e4 !== void 0 ? _importResult$value$e4 : 0) + " 条 ", 1)];
							}),
							_: 1
						}, 8, ["type"])])) : createCommentVNode("", true), errorRows().length > 0 ? (openBlock(), createBlock(_component_el_table, {
							key: 1,
							data: errorRows(),
							border: "",
							stripe: "",
							"max-height": "360",
							class: "error-table"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									prop: "rowIndex",
									label: "行号",
									width: "80",
									align: "center"
								}),
								createVNode(_component_el_table_column, {
									prop: "rowData",
									label: "行数据",
									"min-width": "240",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "errorMessage",
									label: "错误信息",
									"min-width": "200",
									"show-overflow-tooltip": ""
								})
							]),
							_: 1
						}, 8, ["data"])) : importResult.value && ((_importResult$value$s3 = (_importResult$value$s4 = importResult.value.successList) === null || _importResult$value$s4 === void 0 ? void 0 : _importResult$value$s4.length) !== null && _importResult$value$s3 !== void 0 ? _importResult$value$s3 : 0) > 0 ? (openBlock(), createBlock(_component_el_empty, {
							key: 2,
							description: "全部数据导入成功"
						})) : createCommentVNode("", true)];
					}),
					_: 1
				}, 8, ["modelValue"])
			]);
		};
	}
}), [["__scopeId", "data-v-8aa3c110"]]);
//#endregion
//#region src/views/asset/list/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "toolbar" };
var _hoisted_3 = { class: "mobile-card-list" };
var _hoisted_4 = { class: "mobile-cards" };
var _hoisted_5 = { class: "mobile-card-list" };
var _hoisted_6 = {
	key: 1,
	class: "text-muted"
};
var _hoisted_7 = { class: "mobile-cards" };
var _hoisted_8 = { class: "lifecycle-row" };
var _hoisted_9 = { class: "lifecycle-operator" };
var _hoisted_10 = {
	key: 0,
	class: "lifecycle-project"
};
var _hoisted_11 = {
	key: 1,
	class: "lifecycle-remark"
};
//#endregion
//#region src/views/asset/list/index.vue
var list_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const loading = ref(false);
		const tableData = ref([]);
		const total = ref(0);
		const query = reactive({
			page: 1,
			size: 10,
			serialNo: "",
			status: "",
			projectId: void 0
		});
		const statusOptions = [
			{
				value: ASSET_STATUS.IN_STOCK,
				label: "在库",
				tag: "success"
			},
			{
				value: ASSET_STATUS.ALLOCATED,
				label: "已分配",
				tag: "warning"
			},
			{
				value: ASSET_STATUS.IN_TRANSIT,
				label: "调拨中",
				tag: "primary"
			},
			{
				value: ASSET_STATUS.SCRAPPED,
				label: "已报废",
				tag: "danger"
			}
		];
		function statusTagType(status) {
			var _statusOptions$find$t, _statusOptions$find;
			return (_statusOptions$find$t = (_statusOptions$find = statusOptions.find((o) => o.value === status)) === null || _statusOptions$find === void 0 ? void 0 : _statusOptions$find.tag) !== null && _statusOptions$find$t !== void 0 ? _statusOptions$find$t : "info";
		}
		function statusLabel(status) {
			var _ref, _statusOptions$find$l, _statusOptions$find2;
			return (_ref = (_statusOptions$find$l = (_statusOptions$find2 = statusOptions.find((o) => o.value === status)) === null || _statusOptions$find2 === void 0 ? void 0 : _statusOptions$find2.label) !== null && _statusOptions$find$l !== void 0 ? _statusOptions$find$l : status) !== null && _ref !== void 0 ? _ref : "-";
		}
		async function loadAssets() {
			loading.value = true;
			try {
				const res = await listAssets({
					page: query.page,
					size: query.size,
					serialNo: query.serialNo || void 0,
					status: query.status || void 0,
					projectId: query.projectId
				});
				tableData.value = res.records || [];
				total.value = res.total || 0;
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		function handleSearch() {
			query.page = 1;
			loadAssets();
		}
		function handleReset() {
			query.serialNo = "";
			query.status = "";
			query.projectId = void 0;
			query.page = 1;
			loadAssets();
		}
		function handlePageChange(p) {
			query.page = p;
			loadAssets();
		}
		function handleSizeChange(s) {
			query.size = s;
			query.page = 1;
			loadAssets();
		}
		function makeSerialNoValidator(getCurrentId) {
			return async (_rule, value, callback) => {
				if (!value) return callback();
				try {
					if (((await listAssets({
						page: 1,
						size: 50,
						serialNo: value
					})).records || []).some((a) => a.serialNo === value && a.id !== getCurrentId())) return callback(/* @__PURE__ */ new Error("该序列号已存在"));
					callback();
				} catch (_unused2) {
					callback();
				}
			};
		}
		const inboundVisible = ref(false);
		const inboundRef = ref();
		const inboundSubmitting = ref(false);
		const inboundForm = reactive(createInboundForm());
		const inboundRules = {
			serialNo: [{
				required: true,
				message: "请输入序列号",
				trigger: "blur"
			}, {
				validator: makeSerialNoValidator(() => void 0),
				trigger: "blur"
			}],
			name: [{
				required: true,
				message: "请输入设备名称",
				trigger: "blur"
			}],
			warehouse: [{
				required: true,
				message: "请输入仓库",
				trigger: "blur"
			}]
		};
		function createInboundForm() {
			return {
				serialNo: "",
				name: "",
				modelId: void 0,
				warehouse: "",
				location: "",
				remark: ""
			};
		}
		function openInbound() {
			Object.assign(inboundForm, createInboundForm());
			inboundVisible.value = true;
		}
		async function submitInbound() {
			if (!inboundRef.value) return;
			await inboundRef.value.validate(async (valid) => {
				if (!valid) return;
				inboundSubmitting.value = true;
				try {
					await inboundAsset({ ...inboundForm });
					ElMessage.success("入库成功");
					inboundVisible.value = false;
					loadAssets();
				} catch (_unused3) {} finally {
					inboundSubmitting.value = false;
				}
			});
		}
		const allocateVisible = ref(false);
		const allocateRef = ref();
		const allocateSubmitting = ref(false);
		const allocateForm = reactive({
			id: void 0,
			serialNo: "",
			projectId: void 0
		});
		const allocateRules = { projectId: [{
			required: true,
			message: "请输入项目ID",
			trigger: "blur"
		}] };
		function openAllocate(row) {
			allocateForm.id = row.id;
			allocateForm.serialNo = row.serialNo;
			allocateForm.projectId = row.projectId;
			allocateVisible.value = true;
		}
		async function submitAllocate() {
			if (!allocateRef.value || !allocateForm.id || !allocateForm.projectId) return;
			const assetId = allocateForm.id;
			const projectId = allocateForm.projectId;
			await allocateRef.value.validate(async (valid) => {
				if (!valid) return;
				allocateSubmitting.value = true;
				try {
					await allocateAsset(assetId, { projectId });
					ElMessage.success("分配成功");
					allocateVisible.value = false;
					loadAssets();
				} catch (_unused4) {} finally {
					allocateSubmitting.value = false;
				}
			});
		}
		function handleReturn(row) {
			if (!row.id) return;
			ElMessageBox.confirm(`确定回收设备「${row.serialNo}」吗？回收后设备将回到在库状态。`, "提示", { type: "warning" }).then(async () => {
				await returnAsset(row.id);
				ElMessage.success("回收成功");
				loadAssets();
			}).catch(() => {});
		}
		const editVisible = ref(false);
		const editRef = ref();
		const editSubmitting = ref(false);
		const editForm = reactive(createEditForm());
		const editRules = {
			serialNo: [{
				required: true,
				message: "请输入序列号",
				trigger: "blur"
			}, {
				validator: makeSerialNoValidator(() => editForm.id),
				trigger: "blur"
			}],
			name: [{
				required: true,
				message: "请输入设备名称",
				trigger: "blur"
			}]
		};
		function createEditForm() {
			return {
				id: void 0,
				serialNo: "",
				name: "",
				warehouse: "",
				location: "",
				remark: ""
			};
		}
		async function openEdit(row) {
			if (!row.id) return;
			try {
				const detail = await getAsset(row.id);
				Object.assign(editForm, createEditForm(), detail);
				editVisible.value = true;
			} catch (_unused5) {}
		}
		async function submitEdit() {
			if (!editRef.value || !editForm.id) return;
			await editRef.value.validate(async (valid) => {
				if (!valid) return;
				editSubmitting.value = true;
				try {
					await updateAsset({ ...editForm });
					ElMessage.success("更新成功");
					editVisible.value = false;
					loadAssets();
				} catch (_unused6) {} finally {
					editSubmitting.value = false;
				}
			});
		}
		function handleDelete(row) {
			if (!row.id) return;
			ElMessageBox.confirm(`确定删除资产「${row.serialNo}」吗？`, "提示", { type: "warning" }).then(async () => {
				await deleteAsset(row.id);
				ElMessage.success("删除成功");
				loadAssets();
			}).catch(() => {});
		}
		const lifecycleVisible = ref(false);
		const lifecycleLoading = ref(false);
		const lifecycleRecords = ref([]);
		const lifecycleAssetSn = ref("");
		const actionMeta = {
			INBOUND: {
				label: "入库",
				tag: "success"
			},
			ALLOCATE: {
				label: "分配",
				tag: "warning"
			},
			TRANSFER: {
				label: "调拨",
				tag: "primary"
			},
			RETURN: {
				label: "回收",
				tag: "info"
			},
			SCRAP: {
				label: "报废",
				tag: "danger"
			}
		};
		function actionLabel(action) {
			var _ref2, _actionMeta$label, _actionMeta;
			return (_ref2 = (_actionMeta$label = (_actionMeta = actionMeta[action !== null && action !== void 0 ? action : ""]) === null || _actionMeta === void 0 ? void 0 : _actionMeta.label) !== null && _actionMeta$label !== void 0 ? _actionMeta$label : action) !== null && _ref2 !== void 0 ? _ref2 : "-";
		}
		function actionTag(action) {
			var _actionMeta$tag, _actionMeta2;
			return (_actionMeta$tag = (_actionMeta2 = actionMeta[action !== null && action !== void 0 ? action : ""]) === null || _actionMeta2 === void 0 ? void 0 : _actionMeta2.tag) !== null && _actionMeta$tag !== void 0 ? _actionMeta$tag : "info";
		}
		async function openLifecycle(row) {
			if (!row.id) return;
			lifecycleAssetSn.value = row.serialNo;
			lifecycleVisible.value = true;
			lifecycleLoading.value = true;
			lifecycleRecords.value = [];
			try {
				lifecycleRecords.value = await getAssetLifecycle(row.id) || [];
			} catch (_unused7) {} finally {
				lifecycleLoading.value = false;
			}
		}
		function formatDateTime(val) {
			if (!val) return "-";
			return val.replace("T", " ").slice(0, 19);
		}
		const transferApplyVisible = ref(false);
		const transferApplyRef = ref();
		const transferApplySubmitting = ref(false);
		const transferApplyForm = reactive({
			assetId: void 0,
			assetSerialNo: "",
			fromProjectId: void 0,
			toProjectId: void 0,
			transferReason: ""
		});
		const transferApplyRules = {
			assetId: [{
				required: true,
				message: "请输入设备ID",
				trigger: "blur"
			}],
			fromProjectId: [{
				required: true,
				message: "请输入源项目ID",
				trigger: "blur"
			}],
			toProjectId: [{
				required: true,
				message: "请输入目标项目ID",
				trigger: "blur"
			}],
			transferReason: [{
				required: true,
				message: "请输入调拨原因",
				trigger: "blur"
			}]
		};
		function openTransferApply(row) {
			var _row$serialNo;
			transferApplyForm.assetId = row === null || row === void 0 ? void 0 : row.id;
			transferApplyForm.assetSerialNo = (_row$serialNo = row === null || row === void 0 ? void 0 : row.serialNo) !== null && _row$serialNo !== void 0 ? _row$serialNo : "";
			transferApplyForm.fromProjectId = row === null || row === void 0 ? void 0 : row.projectId;
			transferApplyForm.toProjectId = void 0;
			transferApplyForm.transferReason = "";
			transferApplyVisible.value = true;
		}
		async function submitTransferApply() {
			if (!transferApplyRef.value) return;
			await transferApplyRef.value.validate(async (valid) => {
				if (!valid) return;
				if (!transferApplyForm.assetId || !transferApplyForm.fromProjectId || !transferApplyForm.toProjectId) return;
				transferApplySubmitting.value = true;
				try {
					await applyTransfer({
						assetId: transferApplyForm.assetId,
						fromProjectId: transferApplyForm.fromProjectId,
						toProjectId: transferApplyForm.toProjectId,
						transferReason: transferApplyForm.transferReason
					});
					ElMessage.success("调拨申请已提交");
					transferApplyVisible.value = false;
					activeTab.value = "transfer";
					loadTransfers();
				} catch (_unused8) {} finally {
					transferApplySubmitting.value = false;
				}
			});
		}
		const activeTab = ref("asset");
		const transferLoading = ref(false);
		const transferTableData = ref([]);
		const transferTotal = ref(0);
		const transferQuery = reactive({
			page: 1,
			size: 10,
			status: ""
		});
		const transferStatusOptions = [
			{
				value: TRANSFER_STATUS.PENDING,
				label: "待审批",
				tag: "warning"
			},
			{
				value: TRANSFER_STATUS.APPROVED,
				label: "已通过",
				tag: "success"
			},
			{
				value: TRANSFER_STATUS.REJECTED,
				label: "已驳回",
				tag: "danger"
			}
		];
		function transferStatusTag(status) {
			var _transferStatusOption, _transferStatusOption2;
			return (_transferStatusOption = (_transferStatusOption2 = transferStatusOptions.find((o) => o.value === status)) === null || _transferStatusOption2 === void 0 ? void 0 : _transferStatusOption2.tag) !== null && _transferStatusOption !== void 0 ? _transferStatusOption : "info";
		}
		function transferStatusLabel(status) {
			var _ref3, _transferStatusOption3, _transferStatusOption4;
			return (_ref3 = (_transferStatusOption3 = (_transferStatusOption4 = transferStatusOptions.find((o) => o.value === status)) === null || _transferStatusOption4 === void 0 ? void 0 : _transferStatusOption4.label) !== null && _transferStatusOption3 !== void 0 ? _transferStatusOption3 : status) !== null && _ref3 !== void 0 ? _ref3 : "-";
		}
		async function loadTransfers() {
			transferLoading.value = true;
			try {
				const res = await listTransfers({
					page: transferQuery.page,
					size: transferQuery.size,
					status: transferQuery.status || void 0
				});
				transferTableData.value = res.records || [];
				transferTotal.value = res.total || 0;
			} catch (_unused9) {} finally {
				transferLoading.value = false;
			}
		}
		function handleTransferSearch() {
			transferQuery.page = 1;
			loadTransfers();
		}
		function handleTransferReset() {
			transferQuery.status = "";
			transferQuery.page = 1;
			loadTransfers();
		}
		function handleTransferPageChange(p) {
			transferQuery.page = p;
			loadTransfers();
		}
		function handleTransferSizeChange(s) {
			transferQuery.size = s;
			transferQuery.page = 1;
			loadTransfers();
		}
		const opinionVisible = ref(false);
		const opinionTitle = ref("");
		const opinionSubmitting = ref(false);
		const opinionForm = reactive({
			id: void 0,
			opinion: "",
			action: "approve"
		});
		const opinionRules = { opinion: [{
			required: true,
			message: "请输入审批意见",
			trigger: "blur"
		}] };
		function openApprove(row) {
			if (!row.id) return;
			opinionTitle.value = "审批通过";
			opinionForm.id = row.id;
			opinionForm.opinion = "";
			opinionForm.action = "approve";
			opinionVisible.value = true;
		}
		function openReject(row) {
			if (!row.id) return;
			opinionTitle.value = "驳回申请";
			opinionForm.id = row.id;
			opinionForm.opinion = "";
			opinionForm.action = "reject";
			opinionVisible.value = true;
		}
		const opinionFormRef = ref();
		async function handleSubmitOpinion() {
			if (!opinionFormRef.value || !opinionForm.id) return;
			await opinionFormRef.value.validate(async (valid) => {
				if (!valid) return;
				opinionSubmitting.value = true;
				try {
					if (opinionForm.action === "approve") {
						await approveTransfer(opinionForm.id, { opinion: opinionForm.opinion });
						ElMessage.success("审批通过");
					} else {
						await rejectTransfer(opinionForm.id, { opinion: opinionForm.opinion });
						ElMessage.success("已驳回");
					}
					opinionVisible.value = false;
					loadTransfers();
				} catch (_unused10) {} finally {
					opinionSubmitting.value = false;
				}
			});
		}
		const canApprove = (status) => status === TRANSFER_STATUS.PENDING;
		function handleTabChange(name) {
			if (name === "transfer" && transferTableData.value.length === 0) loadTransfers();
		}
		onMounted(() => {
			loadAssets();
		});
		const assetMobileColumns = [
			{
				prop: "name",
				label: "设备名称"
			},
			{
				prop: "status",
				label: "状态",
				render: "tag",
				tagType: (row) => statusTagType(row.status),
				formatter: (row) => statusLabel(row.status),
				subtitle: true
			},
			{
				prop: "serialNo",
				label: "序列号"
			},
			{
				prop: "modelName",
				label: "型号"
			},
			{
				prop: "categoryName",
				label: "分类"
			},
			{
				prop: "warehouse",
				label: "仓库"
			},
			{
				prop: "location",
				label: "库位"
			},
			{
				prop: "inboundDate",
				label: "入库时间",
				formatter: (_r, v) => formatDateTime(v)
			},
			{
				prop: "projectId",
				label: "关联项目",
				formatter: (row) => {
					var _row$projectName;
					return row.projectId ? `${(_row$projectName = row.projectName) !== null && _row$projectName !== void 0 ? _row$projectName : row.projectId}` : "-";
				}
			}
		];
		const assetMobileOperations = [
			{
				label: "生命周期",
				type: "primary",
				onClick: (row) => openLifecycle(row)
			},
			{
				label: "分配",
				type: "primary",
				show: (row) => row.status === ASSET_STATUS.IN_STOCK,
				onClick: (row) => openAllocate(row)
			},
			{
				label: "回收",
				type: "warning",
				show: (row) => row.status === ASSET_STATUS.ALLOCATED,
				onClick: (row) => handleReturn(row)
			},
			{
				label: "编辑",
				type: "primary",
				onClick: (row) => openEdit(row)
			},
			{
				label: "删除",
				type: "danger",
				onClick: (row) => handleDelete(row)
			}
		];
		const transferMobileColumns = [
			{
				prop: "assetSerialNo",
				label: "设备序列号"
			},
			{
				prop: "status",
				label: "状态",
				render: "tag",
				tagType: (row) => transferStatusTag(row.status),
				formatter: (row) => transferStatusLabel(row.status),
				subtitle: true
			},
			{
				prop: "fromProjectId",
				label: "源项目",
				formatter: (row) => {
					var _ref4, _row$fromProjectName;
					return String((_ref4 = (_row$fromProjectName = row.fromProjectName) !== null && _row$fromProjectName !== void 0 ? _row$fromProjectName : row.fromProjectId) !== null && _ref4 !== void 0 ? _ref4 : "-");
				}
			},
			{
				prop: "toProjectId",
				label: "目标项目",
				formatter: (row) => {
					var _ref5, _row$toProjectName;
					return String((_ref5 = (_row$toProjectName = row.toProjectName) !== null && _row$toProjectName !== void 0 ? _row$toProjectName : row.toProjectId) !== null && _ref5 !== void 0 ? _ref5 : "-");
				}
			},
			{
				prop: "transferReason",
				label: "调拨原因"
			},
			{
				prop: "applicantName",
				label: "申请人"
			},
			{
				prop: "applyTime",
				label: "申请时间",
				formatter: (_r, v) => formatDateTime(v)
			}
		];
		const transferMobileOperations = [{
			label: "审批通过",
			type: "success",
			show: (row) => canApprove(row.status),
			onClick: (row) => openApprove(row)
		}, {
			label: "驳回",
			type: "danger",
			show: (row) => canApprove(row.status),
			onClick: (row) => openReject(row)
		}];
		return (_ctx, _cache) => {
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_pagination = resolveComponent("el-pagination");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_tab_pane = resolveComponent("el-tab-pane");
			const _component_el_tabs = resolveComponent("el-tabs");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _component_el_timeline_item = resolveComponent("el-timeline-item");
			const _component_el_timeline = resolveComponent("el-timeline");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				_cache[63] || (_cache[63] = createElementVNode("div", { class: "page-title" }, "设备资产管理", -1)),
				createVNode(_component_el_tabs, {
					modelValue: activeTab.value,
					"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => activeTab.value = $event),
					onTabChange: handleTabChange
				}, {
					default: withCtx(() => [createVNode(_component_el_tab_pane, {
						label: "设备资产",
						name: "asset"
					}, {
						default: withCtx(() => [createVNode(_component_el_card, { shadow: "never" }, {
							default: withCtx(() => [
								createVNode(_component_el_form, {
									inline: true,
									onSubmit: _cache[3] || (_cache[3] = withModifiers(() => {}, ["prevent"]))
								}, {
									default: withCtx(() => [
										createVNode(_component_el_form_item, { label: "序列号" }, {
											default: withCtx(() => [createVNode(_component_el_input, {
												modelValue: query.serialNo,
												"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => query.serialNo = $event),
												placeholder: "设备序列号",
												clearable: "",
												style: { "width": "200px" },
												onKeyup: withKeys(handleSearch, ["enter"])
											}, null, 8, ["modelValue"])]),
											_: 1
										}),
										createVNode(_component_el_form_item, { label: "状态" }, {
											default: withCtx(() => [createVNode(_component_el_select, {
												modelValue: query.status,
												"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => query.status = $event),
												placeholder: "全部",
												clearable: "",
												style: { "width": "140px" }
											}, {
												default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(statusOptions, (o) => {
													return createVNode(_component_el_option, {
														key: o.value,
														label: o.label,
														value: o.value
													}, null, 8, ["label", "value"]);
												}), 64))]),
												_: 1
											}, 8, ["modelValue"])]),
											_: 1
										}),
										createVNode(_component_el_form_item, { label: "项目ID" }, {
											default: withCtx(() => [createVNode(_component_el_input_number, {
												modelValue: query.projectId,
												"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => query.projectId = $event),
												min: 1,
												"controls-position": "right",
												placeholder: "项目ID",
												style: { "width": "160px" }
											}, null, 8, ["modelValue"])]),
											_: 1
										}),
										createVNode(_component_el_form_item, null, {
											default: withCtx(() => [createVNode(_component_el_button, {
												type: "primary",
												icon: "Search",
												onClick: handleSearch
											}, {
												default: withCtx(() => [..._cache[37] || (_cache[37] = [createTextVNode("查询", -1)])]),
												_: 1
											}), createVNode(_component_el_button, {
												icon: "Refresh",
												onClick: handleReset
											}, {
												default: withCtx(() => [..._cache[38] || (_cache[38] = [createTextVNode("重置", -1)])]),
												_: 1
											})]),
											_: 1
										})
									]),
									_: 1
								}),
								createElementVNode("div", _hoisted_2, [
									createVNode(_component_el_button, {
										type: "primary",
										icon: "Download",
										onClick: openInbound
									}, {
										default: withCtx(() => [..._cache[39] || (_cache[39] = [createTextVNode("设备入库", -1)])]),
										_: 1
									}),
									createVNode(_component_el_button, {
										type: "warning",
										icon: "Switch",
										onClick: _cache[4] || (_cache[4] = ($event) => openTransferApply())
									}, {
										default: withCtx(() => [..._cache[40] || (_cache[40] = [createTextVNode(" 设备调拨 ", -1)])]),
										_: 1
									}),
									createVNode(ExcelImportExport_default, {
										"template-url": "/api/asset/template",
										"import-url": "/api/asset/import",
										"export-url": "/api/asset/export",
										"template-file-name": "asset-template.xlsx",
										"export-file-name": "asset-list.xlsx",
										"export-params": {
											serialNo: query.serialNo || void 0,
											status: query.status || void 0,
											projectId: query.projectId
										},
										onRefresh: loadAssets
									}, null, 8, ["export-params"])
								]),
								createElementVNode("div", _hoisted_3, [withDirectives((openBlock(), createBlock(_component_el_table, {
									data: tableData.value,
									border: "",
									stripe: ""
								}, {
									empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无数据" })]),
									default: withCtx(() => [
										createVNode(_component_el_table_column, {
											prop: "serialNo",
											label: "序列号",
											"min-width": "160",
											"show-overflow-tooltip": ""
										}),
										createVNode(_component_el_table_column, {
											prop: "name",
											label: "设备名称",
											"min-width": "140",
											"show-overflow-tooltip": ""
										}),
										createVNode(_component_el_table_column, {
											prop: "modelName",
											label: "型号",
											"min-width": "140",
											"show-overflow-tooltip": ""
										}),
										createVNode(_component_el_table_column, {
											prop: "categoryName",
											label: "分类",
											"min-width": "120",
											"show-overflow-tooltip": ""
										}),
										createVNode(_component_el_table_column, {
											label: "状态",
											width: "100",
											align: "center"
										}, {
											default: withCtx(({ row }) => [createVNode(_component_el_tag, { type: statusTagType(row.status) }, {
												default: withCtx(() => [createTextVNode(toDisplayString(statusLabel(row.status)), 1)]),
												_: 2
											}, 1032, ["type"])]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											prop: "warehouse",
											label: "仓库",
											"min-width": "120",
											"show-overflow-tooltip": ""
										}),
										createVNode(_component_el_table_column, {
											prop: "location",
											label: "库位",
											"min-width": "120",
											"show-overflow-tooltip": ""
										}),
										createVNode(_component_el_table_column, {
											label: "关联项目",
											"min-width": "120"
										}, {
											default: withCtx(({ row }) => {
												var _row$projectName2;
												return [createTextVNode(toDisplayString(row.projectId ? `${(_row$projectName2 = row.projectName) !== null && _row$projectName2 !== void 0 ? _row$projectName2 : row.projectId}` : "-"), 1)];
											}),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "入库时间",
											width: "160"
										}, {
											default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDateTime(row.inboundDate)), 1)]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "操作",
											width: "280",
											fixed: "right"
										}, {
											default: withCtx(({ row }) => [
												createVNode(_component_el_button, {
													link: "",
													type: "primary",
													onClick: ($event) => openLifecycle(row)
												}, {
													default: withCtx(() => [..._cache[41] || (_cache[41] = [createTextVNode(" 生命周期 ", -1)])]),
													_: 1
												}, 8, ["onClick"]),
												row.status === unref(ASSET_STATUS).IN_STOCK ? (openBlock(), createBlock(_component_el_button, {
													key: 0,
													link: "",
													type: "primary",
													onClick: ($event) => openAllocate(row)
												}, {
													default: withCtx(() => [..._cache[42] || (_cache[42] = [createTextVNode(" 分配 ", -1)])]),
													_: 1
												}, 8, ["onClick"])) : createCommentVNode("", true),
												row.status === unref(ASSET_STATUS).ALLOCATED ? (openBlock(), createBlock(_component_el_button, {
													key: 1,
													link: "",
													type: "warning",
													onClick: ($event) => handleReturn(row)
												}, {
													default: withCtx(() => [..._cache[43] || (_cache[43] = [createTextVNode(" 回收 ", -1)])]),
													_: 1
												}, 8, ["onClick"])) : createCommentVNode("", true),
												createVNode(_component_el_button, {
													link: "",
													type: "primary",
													onClick: ($event) => openEdit(row)
												}, {
													default: withCtx(() => [..._cache[44] || (_cache[44] = [createTextVNode("编辑", -1)])]),
													_: 1
												}, 8, ["onClick"]),
												createVNode(_component_el_button, {
													link: "",
													type: "danger",
													onClick: ($event) => handleDelete(row)
												}, {
													default: withCtx(() => [..._cache[45] || (_cache[45] = [createTextVNode("删除", -1)])]),
													_: 1
												}, 8, ["onClick"])
											]),
											_: 1
										})
									]),
									_: 1
								}, 8, ["data"])), [[_directive_loading, loading.value]]), createElementVNode("div", _hoisted_4, [withDirectives(createVNode(MobileListCard_default, {
									data: tableData.value,
									columns: assetMobileColumns,
									operations: assetMobileOperations,
									"title-prop": "name",
									"title-icon": "Box",
									"empty-text": "暂无数据"
								}, null, 8, ["data"]), [[_directive_loading, loading.value]])])]),
								createVNode(_component_el_pagination, {
									class: "pagination",
									background: "",
									"current-page": query.page,
									"page-size": query.size,
									total: total.value,
									"page-sizes": [
										10,
										20,
										50
									],
									layout: "total, sizes, prev, pager, next, jumper",
									onCurrentChange: handlePageChange,
									onSizeChange: handleSizeChange
								}, null, 8, [
									"current-page",
									"page-size",
									"total"
								])
							]),
							_: 1
						})]),
						_: 1
					}), createVNode(_component_el_tab_pane, {
						label: "调拨管理",
						name: "transfer"
					}, {
						default: withCtx(() => [createVNode(_component_el_card, { shadow: "never" }, {
							default: withCtx(() => [
								createVNode(_component_el_form, {
									inline: true,
									onSubmit: _cache[7] || (_cache[7] = withModifiers(() => {}, ["prevent"]))
								}, {
									default: withCtx(() => [createVNode(_component_el_form_item, { label: "状态" }, {
										default: withCtx(() => [createVNode(_component_el_select, {
											modelValue: transferQuery.status,
											"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => transferQuery.status = $event),
											placeholder: "全部",
											clearable: "",
											style: { "width": "160px" }
										}, {
											default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(transferStatusOptions, (o) => {
												return createVNode(_component_el_option, {
													key: o.value,
													label: o.label,
													value: o.value
												}, null, 8, ["label", "value"]);
											}), 64))]),
											_: 1
										}, 8, ["modelValue"])]),
										_: 1
									}), createVNode(_component_el_form_item, null, {
										default: withCtx(() => [
											createVNode(_component_el_button, {
												type: "primary",
												icon: "Search",
												onClick: handleTransferSearch
											}, {
												default: withCtx(() => [..._cache[46] || (_cache[46] = [createTextVNode(" 查询 ", -1)])]),
												_: 1
											}),
											createVNode(_component_el_button, {
												icon: "Refresh",
												onClick: handleTransferReset
											}, {
												default: withCtx(() => [..._cache[47] || (_cache[47] = [createTextVNode("重置", -1)])]),
												_: 1
											}),
											createVNode(_component_el_button, {
												type: "warning",
												icon: "Switch",
												onClick: _cache[6] || (_cache[6] = ($event) => openTransferApply())
											}, {
												default: withCtx(() => [..._cache[48] || (_cache[48] = [createTextVNode(" 新增调拨 ", -1)])]),
												_: 1
											})
										]),
										_: 1
									})]),
									_: 1
								}),
								createElementVNode("div", _hoisted_5, [withDirectives((openBlock(), createBlock(_component_el_table, {
									data: transferTableData.value,
									border: "",
									stripe: ""
								}, {
									empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无数据" })]),
									default: withCtx(() => [
										createVNode(_component_el_table_column, {
											prop: "assetSerialNo",
											label: "设备序列号",
											"min-width": "160",
											"show-overflow-tooltip": ""
										}),
										createVNode(_component_el_table_column, {
											label: "源项目",
											"min-width": "140"
										}, {
											default: withCtx(({ row }) => {
												var _ref6, _row$fromProjectName2;
												return [createTextVNode(toDisplayString((_ref6 = (_row$fromProjectName2 = row.fromProjectName) !== null && _row$fromProjectName2 !== void 0 ? _row$fromProjectName2 : row.fromProjectId) !== null && _ref6 !== void 0 ? _ref6 : "-"), 1)];
											}),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "目标项目",
											"min-width": "140"
										}, {
											default: withCtx(({ row }) => {
												var _ref7, _row$toProjectName2;
												return [createTextVNode(toDisplayString((_ref7 = (_row$toProjectName2 = row.toProjectName) !== null && _row$toProjectName2 !== void 0 ? _row$toProjectName2 : row.toProjectId) !== null && _ref7 !== void 0 ? _ref7 : "-"), 1)];
											}),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											prop: "transferReason",
											label: "调拨原因",
											"min-width": "180",
											"show-overflow-tooltip": ""
										}),
										createVNode(_component_el_table_column, {
											label: "状态",
											width: "100",
											align: "center"
										}, {
											default: withCtx(({ row }) => [createVNode(_component_el_tag, { type: transferStatusTag(row.status) }, {
												default: withCtx(() => [createTextVNode(toDisplayString(transferStatusLabel(row.status)), 1)]),
												_: 2
											}, 1032, ["type"])]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											prop: "applicantName",
											label: "申请人",
											"min-width": "100"
										}),
										createVNode(_component_el_table_column, {
											label: "申请时间",
											width: "160"
										}, {
											default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDateTime(row.applyTime)), 1)]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "操作",
											width: "160",
											fixed: "right"
										}, {
											default: withCtx(({ row }) => [canApprove(row.status) ? (openBlock(), createElementBlock(Fragment, { key: 0 }, [createVNode(_component_el_button, {
												link: "",
												type: "success",
												onClick: ($event) => openApprove(row)
											}, {
												default: withCtx(() => [..._cache[49] || (_cache[49] = [createTextVNode("审批通过", -1)])]),
												_: 1
											}, 8, ["onClick"]), createVNode(_component_el_button, {
												link: "",
												type: "danger",
												onClick: ($event) => openReject(row)
											}, {
												default: withCtx(() => [..._cache[50] || (_cache[50] = [createTextVNode("驳回", -1)])]),
												_: 1
											}, 8, ["onClick"])], 64)) : (openBlock(), createElementBlock("span", _hoisted_6, "-"))]),
											_: 1
										})
									]),
									_: 1
								}, 8, ["data"])), [[_directive_loading, transferLoading.value]]), createElementVNode("div", _hoisted_7, [withDirectives(createVNode(MobileListCard_default, {
									data: transferTableData.value,
									columns: transferMobileColumns,
									operations: transferMobileOperations,
									"title-prop": "assetSerialNo",
									"title-icon": "Switch",
									"empty-text": "暂无数据"
								}, null, 8, ["data"]), [[_directive_loading, transferLoading.value]])])]),
								createVNode(_component_el_pagination, {
									class: "pagination",
									background: "",
									"current-page": transferQuery.page,
									"page-size": transferQuery.size,
									total: transferTotal.value,
									"page-sizes": [
										10,
										20,
										50
									],
									layout: "total, sizes, prev, pager, next, jumper",
									onCurrentChange: handleTransferPageChange,
									onSizeChange: handleTransferSizeChange
								}, null, 8, [
									"current-page",
									"page-size",
									"total"
								])
							]),
							_: 1
						})]),
						_: 1
					})]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: inboundVisible.value,
					"onUpdate:modelValue": _cache[16] || (_cache[16] = ($event) => inboundVisible.value = $event),
					title: "设备入库",
					width: "680px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[15] || (_cache[15] = ($event) => inboundVisible.value = false) }, {
						default: withCtx(() => [..._cache[51] || (_cache[51] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: inboundSubmitting.value,
						onClick: submitInbound
					}, {
						default: withCtx(() => [..._cache[52] || (_cache[52] = [createTextVNode(" 确定 ", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "inboundRef",
						ref: inboundRef,
						model: inboundForm,
						rules: inboundRules,
						"label-width": "100px",
						class: "responsive-form"
					}, {
						default: withCtx(() => [createVNode(_component_el_row, {
							gutter: 16,
							class: "form-two-col-md"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_col, {
									xs: 24,
									sm: 12
								}, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "序列号",
										prop: "serialNo"
									}, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: inboundForm.serialNo,
											"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => inboundForm.serialNo = $event),
											placeholder: "请输入序列号",
											maxlength: "50"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, {
									xs: 24,
									sm: 12
								}, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "设备名称",
										prop: "name"
									}, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: inboundForm.name,
											"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => inboundForm.name = $event),
											placeholder: "请输入设备名称",
											maxlength: "100"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, {
									xs: 24,
									sm: 12
								}, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "型号ID",
										prop: "modelId"
									}, {
										default: withCtx(() => [createVNode(_component_el_input_number, {
											modelValue: inboundForm.modelId,
											"onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => inboundForm.modelId = $event),
											min: 1,
											"controls-position": "right",
											placeholder: "设备型号ID",
											style: { "width": "100%" }
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, {
									xs: 24,
									sm: 12
								}, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "仓库",
										prop: "warehouse"
									}, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: inboundForm.warehouse,
											"onUpdate:modelValue": _cache[12] || (_cache[12] = ($event) => inboundForm.warehouse = $event),
											placeholder: "请输入仓库名称",
											maxlength: "50"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, {
									xs: 24,
									sm: 12
								}, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "库位",
										prop: "location"
									}, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: inboundForm.location,
											"onUpdate:modelValue": _cache[13] || (_cache[13] = ($event) => inboundForm.location = $event),
											placeholder: "请输入库位",
											maxlength: "50"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 24 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "备注",
										prop: "remark"
									}, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: inboundForm.remark,
											"onUpdate:modelValue": _cache[14] || (_cache[14] = ($event) => inboundForm.remark = $event),
											type: "textarea",
											rows: 2,
											placeholder: "备注信息",
											maxlength: "200"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								})
							]),
							_: 1
						})]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: allocateVisible.value,
					"onUpdate:modelValue": _cache[19] || (_cache[19] = ($event) => allocateVisible.value = $event),
					title: "分配设备",
					width: "480px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[18] || (_cache[18] = ($event) => allocateVisible.value = false) }, {
						default: withCtx(() => [..._cache[53] || (_cache[53] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: allocateSubmitting.value,
						onClick: submitAllocate
					}, {
						default: withCtx(() => [..._cache[54] || (_cache[54] = [createTextVNode(" 确定 ", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "allocateRef",
						ref: allocateRef,
						model: allocateForm,
						rules: allocateRules,
						"label-width": "100px"
					}, {
						default: withCtx(() => [createVNode(_component_el_form_item, { label: "序列号" }, {
							default: withCtx(() => [createVNode(_component_el_input, {
								"model-value": allocateForm.serialNo,
								disabled: ""
							}, null, 8, ["model-value"])]),
							_: 1
						}), createVNode(_component_el_form_item, {
							label: "项目ID",
							prop: "projectId"
						}, {
							default: withCtx(() => [createVNode(_component_el_input_number, {
								modelValue: allocateForm.projectId,
								"onUpdate:modelValue": _cache[17] || (_cache[17] = ($event) => allocateForm.projectId = $event),
								min: 1,
								"controls-position": "right",
								placeholder: "请输入项目ID",
								style: { "width": "100%" }
							}, null, 8, ["modelValue"])]),
							_: 1
						})]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: editVisible.value,
					"onUpdate:modelValue": _cache[26] || (_cache[26] = ($event) => editVisible.value = $event),
					title: "编辑设备",
					width: "680px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[25] || (_cache[25] = ($event) => editVisible.value = false) }, {
						default: withCtx(() => [..._cache[55] || (_cache[55] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: editSubmitting.value,
						onClick: submitEdit
					}, {
						default: withCtx(() => [..._cache[56] || (_cache[56] = [createTextVNode("确定", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "editRef",
						ref: editRef,
						model: editForm,
						rules: editRules,
						"label-width": "100px",
						class: "responsive-form"
					}, {
						default: withCtx(() => [createVNode(_component_el_row, {
							gutter: 16,
							class: "form-two-col-md"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_col, {
									xs: 24,
									sm: 12
								}, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "序列号",
										prop: "serialNo"
									}, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: editForm.serialNo,
											"onUpdate:modelValue": _cache[20] || (_cache[20] = ($event) => editForm.serialNo = $event),
											placeholder: "请输入序列号",
											maxlength: "50"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, {
									xs: 24,
									sm: 12
								}, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "设备名称",
										prop: "name"
									}, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: editForm.name,
											"onUpdate:modelValue": _cache[21] || (_cache[21] = ($event) => editForm.name = $event),
											placeholder: "请输入设备名称",
											maxlength: "100"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, {
									xs: 24,
									sm: 12
								}, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "仓库",
										prop: "warehouse"
									}, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: editForm.warehouse,
											"onUpdate:modelValue": _cache[22] || (_cache[22] = ($event) => editForm.warehouse = $event),
											placeholder: "请输入仓库名称",
											maxlength: "50"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, {
									xs: 24,
									sm: 12
								}, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "库位",
										prop: "location"
									}, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: editForm.location,
											"onUpdate:modelValue": _cache[23] || (_cache[23] = ($event) => editForm.location = $event),
											placeholder: "请输入库位",
											maxlength: "50"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 24 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "备注",
										prop: "remark"
									}, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: editForm.remark,
											"onUpdate:modelValue": _cache[24] || (_cache[24] = ($event) => editForm.remark = $event),
											type: "textarea",
											rows: 2,
											placeholder: "备注信息",
											maxlength: "200"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								})
							]),
							_: 1
						})]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: lifecycleVisible.value,
					"onUpdate:modelValue": _cache[27] || (_cache[27] = ($event) => lifecycleVisible.value = $event),
					title: `设备生命周期 - ${lifecycleAssetSn.value}`,
					width: "640px",
					"destroy-on-close": ""
				}, {
					default: withCtx(() => [withDirectives((openBlock(), createElementBlock("div", null, [lifecycleRecords.value.length > 0 ? (openBlock(), createBlock(_component_el_timeline, { key: 0 }, {
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(lifecycleRecords.value, (item, idx) => {
							var _item$id;
							return openBlock(), createBlock(_component_el_timeline_item, {
								key: (_item$id = item.id) !== null && _item$id !== void 0 ? _item$id : idx,
								timestamp: formatDateTime(item.operateTime),
								placement: "top"
							}, {
								default: withCtx(() => [createVNode(_component_el_card, {
									shadow: "hover",
									class: "lifecycle-card"
								}, {
									default: withCtx(() => {
										var _ref8, _item$fromProjectName, _ref9, _item$toProjectName;
										return [
											createElementVNode("div", _hoisted_8, [createVNode(_component_el_tag, { type: actionTag(item.action) }, {
												default: withCtx(() => [createTextVNode(toDisplayString(actionLabel(item.action)), 1)]),
												_: 2
											}, 1032, ["type"]), createElementVNode("span", _hoisted_9, "操作人：" + toDisplayString(item.operator || "-"), 1)]),
											item.fromProjectId || item.toProjectId ? (openBlock(), createElementBlock("div", _hoisted_10, [
												_cache[57] || (_cache[57] = createElementVNode("span", null, "项目变更：", -1)),
												createElementVNode("span", null, toDisplayString((_ref8 = (_item$fromProjectName = item.fromProjectName) !== null && _item$fromProjectName !== void 0 ? _item$fromProjectName : item.fromProjectId) !== null && _ref8 !== void 0 ? _ref8 : "-"), 1),
												_cache[58] || (_cache[58] = createElementVNode("span", { class: "arrow" }, "→", -1)),
												createElementVNode("span", null, toDisplayString((_ref9 = (_item$toProjectName = item.toProjectName) !== null && _item$toProjectName !== void 0 ? _item$toProjectName : item.toProjectId) !== null && _ref9 !== void 0 ? _ref9 : "-"), 1)
											])) : createCommentVNode("", true),
											item.remark ? (openBlock(), createElementBlock("div", _hoisted_11, "备注：" + toDisplayString(item.remark), 1)) : createCommentVNode("", true)
										];
									}),
									_: 2
								}, 1024)]),
								_: 2
							}, 1032, ["timestamp"]);
						}), 128))]),
						_: 1
					})) : (openBlock(), createBlock(_component_el_empty, {
						key: 1,
						description: "暂无生命周期记录"
					}))])), [[_directive_loading, lifecycleLoading.value]])]),
					_: 1
				}, 8, ["modelValue", "title"]),
				createVNode(_component_el_dialog, {
					modelValue: transferApplyVisible.value,
					"onUpdate:modelValue": _cache[33] || (_cache[33] = ($event) => transferApplyVisible.value = $event),
					title: "设备调拨申请",
					width: "680px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[32] || (_cache[32] = ($event) => transferApplyVisible.value = false) }, {
						default: withCtx(() => [..._cache[59] || (_cache[59] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: transferApplySubmitting.value,
						onClick: submitTransferApply
					}, {
						default: withCtx(() => [..._cache[60] || (_cache[60] = [createTextVNode(" 提交申请 ", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "transferApplyRef",
						ref: transferApplyRef,
						model: transferApplyForm,
						rules: transferApplyRules,
						"label-width": "100px",
						class: "responsive-form"
					}, {
						default: withCtx(() => [createVNode(_component_el_row, {
							gutter: 16,
							class: "form-two-col-md"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_col, {
									xs: 24,
									sm: 12
								}, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "设备ID",
										prop: "assetId"
									}, {
										default: withCtx(() => [createVNode(_component_el_input_number, {
											modelValue: transferApplyForm.assetId,
											"onUpdate:modelValue": _cache[28] || (_cache[28] = ($event) => transferApplyForm.assetId = $event),
											min: 1,
											"controls-position": "right",
											placeholder: "请输入设备ID",
											style: { "width": "100%" }
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								transferApplyForm.assetSerialNo ? (openBlock(), createBlock(_component_el_col, {
									key: 0,
									xs: 24,
									sm: 12
								}, {
									default: withCtx(() => [createVNode(_component_el_form_item, { label: "序列号" }, {
										default: withCtx(() => [createVNode(_component_el_input, {
											"model-value": transferApplyForm.assetSerialNo,
											disabled: ""
										}, null, 8, ["model-value"])]),
										_: 1
									})]),
									_: 1
								})) : createCommentVNode("", true),
								createVNode(_component_el_col, {
									xs: 24,
									sm: 12
								}, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "源项目ID",
										prop: "fromProjectId"
									}, {
										default: withCtx(() => [createVNode(_component_el_input_number, {
											modelValue: transferApplyForm.fromProjectId,
											"onUpdate:modelValue": _cache[29] || (_cache[29] = ($event) => transferApplyForm.fromProjectId = $event),
											min: 1,
											"controls-position": "right",
											placeholder: "请输入源项目ID",
											style: { "width": "100%" }
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, {
									xs: 24,
									sm: 12
								}, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "目标项目ID",
										prop: "toProjectId"
									}, {
										default: withCtx(() => [createVNode(_component_el_input_number, {
											modelValue: transferApplyForm.toProjectId,
											"onUpdate:modelValue": _cache[30] || (_cache[30] = ($event) => transferApplyForm.toProjectId = $event),
											min: 1,
											"controls-position": "right",
											placeholder: "请输入目标项目ID",
											style: { "width": "100%" }
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								}),
								createVNode(_component_el_col, { span: 24 }, {
									default: withCtx(() => [createVNode(_component_el_form_item, {
										label: "调拨原因",
										prop: "transferReason"
									}, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: transferApplyForm.transferReason,
											"onUpdate:modelValue": _cache[31] || (_cache[31] = ($event) => transferApplyForm.transferReason = $event),
											type: "textarea",
											rows: 3,
											placeholder: "请输入调拨原因",
											maxlength: "200"
										}, null, 8, ["modelValue"])]),
										_: 1
									})]),
									_: 1
								})
							]),
							_: 1
						})]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: opinionVisible.value,
					"onUpdate:modelValue": _cache[36] || (_cache[36] = ($event) => opinionVisible.value = $event),
					title: opinionTitle.value,
					width: "480px",
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[35] || (_cache[35] = ($event) => opinionVisible.value = false) }, {
						default: withCtx(() => [..._cache[61] || (_cache[61] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						loading: opinionSubmitting.value,
						onClick: handleSubmitOpinion
					}, {
						default: withCtx(() => [..._cache[62] || (_cache[62] = [createTextVNode(" 确定 ", -1)])]),
						_: 1
					}, 8, ["loading"])]),
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "opinionFormRef",
						ref: opinionFormRef,
						model: opinionForm,
						rules: opinionRules,
						"label-width": "100px"
					}, {
						default: withCtx(() => [createVNode(_component_el_form_item, {
							label: "审批意见",
							prop: "opinion"
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: opinionForm.opinion,
								"onUpdate:modelValue": _cache[34] || (_cache[34] = ($event) => opinionForm.opinion = $event),
								type: "textarea",
								rows: 3,
								placeholder: "请输入审批意见",
								maxlength: "200"
							}, null, 8, ["modelValue"])]),
							_: 1
						})]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue", "title"])
			]);
		};
	}
}), [["__scopeId", "data-v-5d77432c"]]);
//#endregion
export { list_default as default };

//# sourceMappingURL=list-DLz1dyw_.js.map