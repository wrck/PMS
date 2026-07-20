import { d as useRouter, u as useRoute } from "./request-BQrAOfxW.js";
import { B as importRelatedPage, J as publishRelatedPage, O as exportRelatedPage, P as getRelatedPage, Q as updateRelatedPage, b as createRelatedPage, c as RelatedPageLayout, h as archiveRelatedPage, l as SectionType } from "./lowcode-F-suzo7c.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { t as LowCodeRelatedPageRenderer_default } from "./LowCodeRelatedPageRenderer-DCPiySON.js";
import { t as useUndoRedo } from "./useUndoRedo-C9SCn4rB.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, nextTick, normalizeClass, onBeforeUnmount, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDynamicComponent, toDisplayString, unref, watch, withCtx, withModifiers } from "vue";
//#region src/views/lowcode/related-page-designer/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "related-page-designer" };
var _hoisted_2 = { class: "toolbar" };
var _hoisted_3 = { class: "toolbar-left" };
var _hoisted_4 = { class: "toolbar-right" };
var _hoisted_5 = { class: "designer-body" };
var _hoisted_6 = { class: "comp-group-title" };
var _hoisted_7 = { class: "comp-items" };
var _hoisted_8 = ["onDragstart", "onClick"];
var _hoisted_9 = { class: "canvas-header" };
var _hoisted_10 = {
	key: 0,
	class: "empty-tip"
};
var _hoisted_11 = {
	key: 1,
	class: "item-list"
};
var _hoisted_12 = [
	"onDragstart",
	"onDrop",
	"onClick"
];
var _hoisted_13 = { class: "item-card-header" };
var _hoisted_14 = { class: "item-label" };
var _hoisted_15 = {
	key: 0,
	class: "item-prop"
};
var _hoisted_16 = { class: "item-prop" };
var _hoisted_17 = { class: "item-prop" };
var _hoisted_18 = { class: "item-actions" };
var _hoisted_19 = { class: "item-card-meta" };
var _hoisted_20 = {
	key: 0,
	class: "empty-prop"
};
var _hoisted_21 = { class: "props-list" };
/** 响应式断点折叠面板激活项（默认展开） */
var HISTORY_DEBOUNCE_MS = 400;
//#endregion
//#region src/views/lowcode/related-page-designer/index.vue
var related_page_designer_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		/**
		* 低代码关联页设计器。
		*
		* <p>三栏布局：</p>
		* <ul>
		*   <li>左侧：组件库面板（Section 组件，按 type 分组：form/list/tab/custom）</li>
		*   <li>中间：画布（关联页顶层配置 + Section 列表，可拖拽排序，
		*       每个卡片显示 title/type/pageCode/span/order）</li>
		*   <li>右侧：属性面板（选中 Section 的 title/type/pageCode/pageUrl/span/order/visible/props）</li>
		* </ul>
		*
		* <p>顶部操作栏：保存草稿 / 发布 / 归档 / 导入 / 导出 / 预览 / 重置，以及元信息编辑。</p>
		*
		* <p>使用原生 HTML5 拖拽 API（draggable + dragstart/dragover/drop）实现，
		* 支持组件库 → 画布拖拽、画布内 Section 排序拖拽两种交互。Section ID 自动生成
		* （section_N），默认 span=24、order=100，点击"预览"打开 LowCodeRelatedPageRenderer 弹窗。</p>
		*/
		const route = useRoute();
		const router = useRouter();
		const componentGroups = ref([{
			title: "区块组件（按引用页面类型）",
			items: [
				{
					type: SectionType.FORM,
					label: "表单区块",
					icon: "Document",
					defaultProps: {
						type: SectionType.FORM,
						span: 24,
						order: 100
					}
				},
				{
					type: SectionType.LIST,
					label: "列表区块",
					icon: "List",
					defaultProps: {
						type: SectionType.LIST,
						span: 24,
						order: 100
					}
				},
				{
					type: SectionType.TAB,
					label: "标签页区块",
					icon: "Files",
					defaultProps: {
						type: SectionType.TAB,
						span: 24,
						order: 100
					}
				},
				{
					type: SectionType.CUSTOM,
					label: "自定义区块",
					icon: "Setting",
					defaultProps: {
						type: SectionType.CUSTOM,
						span: 24,
						order: 100,
						pageUrl: ""
					}
				}
			]
		}]);
		/** 关联页元信息（对应 LowCodeRelatedPageConfig 的非 relatedConfig 字段） */
		const metaForm = reactive({
			code: "",
			name: "",
			description: "",
			relatedConfig: "",
			status: "DRAFT",
			bizType: "",
			version: 1
		});
		/** 设计器内部维护的 RelatedPageConfig 对象 */
		const relatedConfig = reactive({
			title: "",
			description: "",
			mainEntity: "",
			sections: [],
			layout: RelatedPageLayout.GRID,
			gutter: 16
		});
		/** 当前选中项 id（section 的 id） */
		const selectedId = ref("");
		/** Section ID 计数器 */
		let sectionSeq = 0;
		/** 元信息表单 ref */
		const metaFormRef = ref();
		const metaRules = {
			code: [{
				required: true,
				message: "请输入关联页编码",
				trigger: "blur"
			}],
			name: [{
				required: true,
				message: "请输入关联页名称",
				trigger: "blur"
			}]
		};
		const loading = ref(false);
		const previewVisible = ref(false);
		/** 当前选中的区块 */
		const selectedSection = computed(() => relatedConfig.sections.find((s) => s.id === selectedId.value) || null);
		const sectionResponsiveCollapse = ref(["resp"]);
		/** 当前选中区块是否启用响应式断点（span 为对象） */
		const isSectionResponsive = computed({
			get: () => !!selectedSection.value && typeof selectedSection.value.span === "object",
			set: (val) => {
				const section = selectedSection.value;
				if (!section) return;
				if (val) {
					const cur = typeof section.span === "number" ? section.span : 24;
					section.span = {
						xs: cur,
						sm: cur,
						md: cur,
						lg: cur,
						xl: cur
					};
				} else {
					var _obj$md;
					const obj = section.span;
					section.span = typeof obj === "object" && obj ? (_obj$md = obj.md) !== null && _obj$md !== void 0 ? _obj$md : 24 : 24;
				}
			}
		});
		/** 非响应式模式下的栅格宽度（数字） */
		const sectionSpan = computed({
			get: () => {
				var _selectedSection$valu;
				return typeof ((_selectedSection$valu = selectedSection.value) === null || _selectedSection$valu === void 0 ? void 0 : _selectedSection$valu.span) === "number" ? selectedSection.value.span : 24;
			},
			set: (v) => {
				if (selectedSection.value) selectedSection.value.span = v;
			}
		});
		/** 读取指定断点值（缺省回退 24） */
		function getSectionBreakpoint(k) {
			var _selectedSection$valu2;
			const s = (_selectedSection$valu2 = selectedSection.value) === null || _selectedSection$valu2 === void 0 ? void 0 : _selectedSection$valu2.span;
			return typeof s === "object" && s && s[k] !== void 0 ? s[k] : 24;
		}
		/** 设置指定断点值（自动转为响应式对象） */
		function setSectionBreakpoint(k, v) {
			const section = selectedSection.value;
			if (!section) return;
			const s = section.span;
			const obj = typeof s === "object" && s ? { ...s } : {};
			obj[k] = v;
			section.span = obj;
		}
		/** 格式化 span 用于卡片展示：数字直接返回，对象拼接断点键值 */
		function formatSpan(span) {
			if (span === void 0) return "24";
			if (typeof span === "number") return String(span);
			const parts = [];
			if (span.xs !== void 0) parts.push(`xs:${span.xs}`);
			if (span.sm !== void 0) parts.push(`sm:${span.sm}`);
			if (span.md !== void 0) parts.push(`md:${span.md}`);
			if (span.lg !== void 0) parts.push(`lg:${span.lg}`);
			if (span.xl !== void 0) parts.push(`xl:${span.xl}`);
			return parts.length ? parts.join(" ") : "24";
		}
		/** 生成区块对象 */
		function createSection(comp) {
			sectionSeq++;
			const idx = sectionSeq;
			return {
				id: `section_${idx}`,
				title: `区块${idx}`,
				type: comp.type,
				span: 24,
				order: 100,
				...comp.defaultProps || {}
			};
		}
		/** 添加项（点击组件库或拖拽放置） */
		function addComponent(comp) {
			const section = createSection(comp);
			relatedConfig.sections.push(section);
			selectedId.value = section.id;
		}
		/** 删除项 */
		function removeItem(id) {
			relatedConfig.sections = relatedConfig.sections.filter((s) => s.id !== id);
			if (selectedId.value === id) selectedId.value = "";
		}
		/** 复制项 */
		function duplicateItem(id) {
			const section = relatedConfig.sections.find((s) => s.id === id);
			if (!section) return;
			sectionSeq++;
			const copy = JSON.parse(JSON.stringify(section));
			copy.id = `section_${sectionSeq}`;
			copy.title = `${section.title}_副本`;
			const idx = relatedConfig.sections.findIndex((s) => s.id === id);
			relatedConfig.sections.splice(idx + 1, 0, copy);
			selectedId.value = copy.id;
		}
		/** 上下移动 */
		function moveItem(id, direction) {
			const arr = relatedConfig.sections;
			const idx = arr.findIndex((s) => s.id === id);
			if (idx < 0) return;
			const newIdx = idx + direction;
			if (newIdx < 0 || newIdx >= arr.length) return;
			const tmp = arr[idx];
			arr[idx] = arr[newIdx];
			arr[newIdx] = tmp;
		}
		/** 选中项 */
		function selectItem(id) {
			selectedId.value = id;
		}
		let dragType = "";
		let dragItemId = "";
		/** 组件库 dragstart */
		function onCompDragStart(event, comp) {
			dragType = comp.type;
			dragItemId = "";
			if (event.dataTransfer) {
				event.dataTransfer.effectAllowed = "copy";
				event.dataTransfer.setData("text/plain", `comp:${comp.type}`);
			}
		}
		/** 画布项 dragstart（用于排序） */
		function onItemDragStart(event, id) {
			dragItemId = id;
			dragType = "";
			if (event.dataTransfer) {
				event.dataTransfer.effectAllowed = "move";
				event.dataTransfer.setData("text/plain", `item:${id}`);
			}
		}
		/** 画布 dragover */
		function onCanvasDragOver(event) {
			if (event.dataTransfer) event.dataTransfer.dropEffect = dragType ? "copy" : "move";
			event.preventDefault();
		}
		/** 画布 drop：组件库 → 添加 */
		function onCanvasDrop(event) {
			var _event$dataTransfer;
			event.preventDefault();
			const raw = ((_event$dataTransfer = event.dataTransfer) === null || _event$dataTransfer === void 0 ? void 0 : _event$dataTransfer.getData("text/plain")) || "";
			if (raw.startsWith("comp:")) {
				const type = raw.slice(5);
				for (const g of componentGroups.value) {
					const comp = g.items.find((c) => c.type === type);
					if (comp) {
						addComponent(comp);
						return;
					}
				}
			}
		}
		/** 项 drop：将 dragItemId 移动到 targetId 之前 */
		function onItemDrop(event, targetId) {
			event.preventDefault();
			if (!dragItemId || dragItemId === targetId) return;
			const arr = relatedConfig.sections;
			const fromIdx = arr.findIndex((s) => s.id === dragItemId);
			const toIdx = arr.findIndex((s) => s.id === targetId);
			if (fromIdx < 0 || toIdx < 0) return;
			const [moved] = arr.splice(fromIdx, 1);
			arr.splice(toIdx, 0, moved);
			dragItemId = "";
		}
		function addSectionProp(section) {
			if (!section.props) section.props = {};
			const key = `prop_${Object.keys(section.props).length + 1}`;
			section.props[key] = "";
		}
		function removeSectionProp(section, key) {
			if (section.props) delete section.props[key];
		}
		function syncRelatedConfigToStr() {
			metaForm.relatedConfig = JSON.stringify(relatedConfig, null, 2);
		}
		function parseRelatedConfigFromStr() {
			try {
				var _parsed$title, _parsed$description, _parsed$mainEntity, _parsed$layout, _parsed$gutter;
				if (!metaForm.relatedConfig) {
					relatedConfig.sections = [];
					return;
				}
				const parsed = JSON.parse(metaForm.relatedConfig);
				relatedConfig.title = (_parsed$title = parsed.title) !== null && _parsed$title !== void 0 ? _parsed$title : "";
				relatedConfig.description = (_parsed$description = parsed.description) !== null && _parsed$description !== void 0 ? _parsed$description : "";
				relatedConfig.mainEntity = (_parsed$mainEntity = parsed.mainEntity) !== null && _parsed$mainEntity !== void 0 ? _parsed$mainEntity : "";
				relatedConfig.sections = parsed.sections || [];
				relatedConfig.layout = (_parsed$layout = parsed.layout) !== null && _parsed$layout !== void 0 ? _parsed$layout : RelatedPageLayout.GRID;
				relatedConfig.gutter = (_parsed$gutter = parsed.gutter) !== null && _parsed$gutter !== void 0 ? _parsed$gutter : 16;
				sectionSeq = 0;
				relatedConfig.sections.forEach((s) => {
					const m = /section_(\d+)/.exec(s.id);
					if (m) {
						const n = parseInt(m[1], 10);
						if (n > sectionSeq) sectionSeq = n;
					}
				});
			} catch (e) {
				ElMessage.error("关联页配置 JSON 解析失败：" + e.message);
			}
		}
		/**
		* 撤销/重做历史栈：对整个 relatedConfig 做 JSON 快照。
		*
		* <p>采用 watch 深度监听 relatedConfig 自动推历史（400ms 防抖合并连续输入，
		* 避免 50 步栈被逐字符吞掉）；undo/redo 时反向同步快照回 reactive，
		* 保持 relatedConfig 引用不变以兼容现有 UI 双向绑定。</p>
		*/
		const history = useUndoRedo(JSON.parse(JSON.stringify(relatedConfig)));
		const { present: historyPresent, canUndo, canRedo } = history;
		/** 抑制标志：undo/redo 同步快照回 relatedConfig 时关闭 watch 推历史，避免循环 */
		let suppressHistory = false;
		/** 防抖计时器：连续输入合并为一次历史入栈 */
		let historyDebounce = null;
		function commitPendingHistory() {
			if (historyDebounce) {
				clearTimeout(historyDebounce);
				historyDebounce = null;
				history.set(JSON.parse(JSON.stringify(relatedConfig)));
			}
		}
		watch(relatedConfig, () => {
			if (suppressHistory) return;
			if (historyDebounce) clearTimeout(historyDebounce);
			historyDebounce = setTimeout(() => {
				historyDebounce = null;
				history.set(JSON.parse(JSON.stringify(relatedConfig)));
			}, HISTORY_DEBOUNCE_MS);
		}, {
			deep: true,
			flush: "sync"
		});
		/** 将历史当前快照同步回 reactive relatedConfig（保持引用不变，UI 自动更新） */
		function applyHistoryToRelatedConfig() {
			const snap = historyPresent.value;
			suppressHistory = true;
			try {
				const target = relatedConfig;
				const src = snap;
				for (const key of Object.keys(target)) if (!(key in src)) delete target[key];
				for (const key of Object.keys(src)) target[key] = JSON.parse(JSON.stringify(src[key]));
				sectionSeq = 0;
				for (const s of relatedConfig.sections) {
					const m = /section_(\d+)/.exec(s.id);
					if (m) {
						const n = parseInt(m[1], 10);
						if (n > sectionSeq) sectionSeq = n;
					}
				}
			} finally {
				nextTick(() => {
					suppressHistory = false;
				});
			}
		}
		/** 撤销 */
		function undo() {
			commitPendingHistory();
			if (!canUndo.value) return;
			history.undo();
			applyHistoryToRelatedConfig();
		}
		/** 重做 */
		function redo() {
			commitPendingHistory();
			if (!canRedo.value) return;
			history.redo();
			applyHistoryToRelatedConfig();
		}
		/** 键盘快捷键：Ctrl/Cmd+Z 撤销，Ctrl+Y 或 Ctrl/Cmd+Shift+Z 重做 */
		function onUndoRedoKeydown(event) {
			if (!(typeof navigator !== "undefined" && navigator.platform.toUpperCase().indexOf("MAC") >= 0 ? event.metaKey : event.ctrlKey)) return;
			const key = event.key.toLowerCase();
			if (key === "z" && !event.shiftKey) {
				event.preventDefault();
				undo();
			} else if (key === "z" && event.shiftKey || key === "y") {
				event.preventDefault();
				redo();
			}
		}
		async function loadRelatedPage(id) {
			loading.value = true;
			try {
				const data = await getRelatedPage(id);
				Object.assign(metaForm, data);
				parseRelatedConfigFromStr();
				if (historyDebounce) {
					clearTimeout(historyDebounce);
					historyDebounce = null;
				}
				history.reset(JSON.parse(JSON.stringify(relatedConfig)));
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		async function handleSave() {
			if (!metaFormRef.value) return;
			await metaFormRef.value.validate(async (valid) => {
				if (!valid) return;
				if (relatedConfig.sections.length === 0) {
					ElMessage.warning("请至少添加一个区块");
					return;
				}
				syncRelatedConfigToStr();
				loading.value = true;
				try {
					if (metaForm.id) {
						await updateRelatedPage(metaForm.id, metaForm);
						ElMessage.success("保存成功");
					} else {
						const created = await createRelatedPage(metaForm);
						metaForm.id = created.id;
						metaForm.status = created.status;
						ElMessage.success("创建成功");
					}
				} catch (_unused2) {} finally {
					loading.value = false;
				}
			});
		}
		async function handlePublish() {
			if (!metaForm.id) {
				ElMessage.warning("请先保存草稿");
				return;
			}
			syncRelatedConfigToStr();
			loading.value = true;
			try {
				await updateRelatedPage(metaForm.id, metaForm);
				await publishRelatedPage(metaForm.id);
				metaForm.status = "PUBLISHED";
				ElMessage.success("发布成功");
			} catch (_unused3) {} finally {
				loading.value = false;
			}
		}
		async function handleArchive() {
			if (!metaForm.id) return;
			try {
				await ElMessageBox.confirm("确认归档此关联页？归档后不可再使用", "确认", { type: "warning" });
				await archiveRelatedPage(metaForm.id);
				metaForm.status = "ARCHIVED";
				ElMessage.success("归档成功");
			} catch (_unused4) {}
		}
		async function handleExport() {
			if (!metaForm.code) {
				ElMessage.warning("请先填写关联页编码");
				return;
			}
			syncRelatedConfigToStr();
			if (metaForm.id) try {
				await exportRelatedPage(metaForm.code);
				ElMessage.success("导出成功");
			} catch (_unused5) {}
			else {
				const blob = new Blob([JSON.stringify(metaForm, null, 2)], { type: "application/json" });
				const url = URL.createObjectURL(blob);
				const link = document.createElement("a");
				link.href = url;
				link.download = `related-page-${metaForm.code}.json`;
				document.body.appendChild(link);
				link.click();
				document.body.removeChild(link);
				setTimeout(() => URL.revokeObjectURL(url), 0);
				ElMessage.success("本地导出成功");
			}
		}
		async function handleImport() {
			const input = document.createElement("input");
			input.type = "file";
			input.accept = ".json,application/json";
			input.onchange = async () => {
				var _input$files;
				const file = (_input$files = input.files) === null || _input$files === void 0 ? void 0 : _input$files[0];
				if (!file) return;
				const text = await file.text();
				try {
					const imported = await importRelatedPage(text);
					ElMessage.success(`导入成功，编码：${imported.code}`);
					Object.assign(metaForm, imported);
					parseRelatedConfigFromStr();
				} catch (_unused6) {
					try {
						const parsed = JSON.parse(text);
						if (parsed.relatedConfig && typeof parsed.relatedConfig === "string") {
							Object.assign(metaForm, parsed);
							parseRelatedConfigFromStr();
							ElMessage.success("已加载到画布（本地解析，未提交后端）");
						} else if (Array.isArray(parsed.sections)) {
							metaForm.relatedConfig = text;
							parseRelatedConfigFromStr();
							ElMessage.success("已加载到画布");
						} else ElMessage.error("无法识别的 JSON 结构");
					} catch (e) {
						ElMessage.error("JSON 解析失败：" + e.message);
					}
				}
			};
			input.click();
		}
		function handlePreview() {
			syncRelatedConfigToStr();
			previewVisible.value = true;
		}
		function handleReset() {
			ElMessageBox.confirm("确认清空画布所有配置？此操作不可恢复", "确认", { type: "warning" }).then(() => {
				relatedConfig.sections = [];
				selectedId.value = "";
				sectionSeq = 0;
				ElMessage.success("已重置画布");
			}).catch(() => {});
		}
		function goToList() {
			router.push("/lowcode/related-page-list");
		}
		const editId = route.query.id ? Number(route.query.id) : 0;
		if (editId > 0) loadRelatedPage(editId);
		else {
			relatedConfig.title = "未命名关联页";
			if (historyDebounce) {
				clearTimeout(historyDebounce);
				historyDebounce = null;
			}
			history.reset(JSON.parse(JSON.stringify(relatedConfig)));
		}
		onMounted(() => {
			window.addEventListener("keydown", onUndoRedoKeydown);
		});
		onBeforeUnmount(() => {
			window.removeEventListener("keydown", onUndoRedoKeydown);
			if (historyDebounce) clearTimeout(historyDebounce);
		});
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_Plus = resolveComponent("Plus");
			const _component_el_button_group = resolveComponent("el-button-group");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_divider = resolveComponent("el-divider");
			const _component_el_switch = resolveComponent("el-switch");
			const _component_el_collapse_item = resolveComponent("el-collapse-item");
			const _component_el_collapse = resolveComponent("el-collapse");
			const _component_el_dialog = resolveComponent("el-dialog");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, {
					shadow: "never",
					class: "toolbar-card",
					"body-style": { padding: "12px 16px" }
				}, {
					default: withCtx(() => [createElementVNode("div", _hoisted_2, [createElementVNode("div", _hoisted_3, [
						createVNode(_component_el_button, {
							type: "primary",
							icon: "Document",
							loading: loading.value,
							onClick: handleSave
						}, {
							default: withCtx(() => [..._cache[24] || (_cache[24] = [createTextVNode("保存草稿", -1)])]),
							_: 1
						}, 8, ["loading"]),
						createVNode(_component_el_button, {
							type: "success",
							icon: "Promotion",
							onClick: handlePublish
						}, {
							default: withCtx(() => [..._cache[25] || (_cache[25] = [createTextVNode("发布", -1)])]),
							_: 1
						}),
						createVNode(_component_el_button, {
							icon: "Download",
							onClick: handleExport
						}, {
							default: withCtx(() => [..._cache[26] || (_cache[26] = [createTextVNode("导出", -1)])]),
							_: 1
						}),
						createVNode(_component_el_button, {
							icon: "Upload",
							onClick: handleImport
						}, {
							default: withCtx(() => [..._cache[27] || (_cache[27] = [createTextVNode("导入", -1)])]),
							_: 1
						}),
						createVNode(_component_el_button, {
							icon: "View",
							onClick: handlePreview
						}, {
							default: withCtx(() => [..._cache[28] || (_cache[28] = [createTextVNode("预览", -1)])]),
							_: 1
						}),
						createVNode(_component_el_button, {
							icon: "RefreshLeft",
							disabled: !unref(canUndo),
							onClick: undo
						}, {
							default: withCtx(() => [..._cache[29] || (_cache[29] = [createTextVNode("撤销", -1)])]),
							_: 1
						}, 8, ["disabled"]),
						createVNode(_component_el_button, {
							icon: "RefreshRight",
							disabled: !unref(canRedo),
							onClick: redo
						}, {
							default: withCtx(() => [..._cache[30] || (_cache[30] = [createTextVNode("重做", -1)])]),
							_: 1
						}, 8, ["disabled"]),
						createVNode(_component_el_button, {
							icon: "Refresh",
							onClick: handleReset
						}, {
							default: withCtx(() => [..._cache[31] || (_cache[31] = [createTextVNode("重置", -1)])]),
							_: 1
						}),
						metaForm.status === "PUBLISHED" ? (openBlock(), createBlock(_component_el_button, {
							key: 0,
							icon: "FolderOpened",
							onClick: handleArchive
						}, {
							default: withCtx(() => [..._cache[32] || (_cache[32] = [createTextVNode("归档", -1)])]),
							_: 1
						})) : createCommentVNode("", true)
					]), createElementVNode("div", _hoisted_4, [createVNode(_component_el_tag, { type: metaForm.status === "PUBLISHED" ? "success" : metaForm.status === "ARCHIVED" ? "info" : "warning" }, {
						default: withCtx(() => [createTextVNode(toDisplayString(metaForm.status || "DRAFT"), 1)]),
						_: 1
					}, 8, ["type"]), createVNode(_component_el_button, {
						link: "",
						type: "primary",
						onClick: goToList
					}, {
						default: withCtx(() => [..._cache[33] || (_cache[33] = [createTextVNode("返回列表", -1)])]),
						_: 1
					})])])]),
					_: 1
				}),
				createVNode(_component_el_card, {
					shadow: "never",
					class: "meta-card",
					"body-style": { padding: "12px 16px" }
				}, {
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "metaFormRef",
						ref: metaFormRef,
						model: metaForm,
						rules: metaRules,
						inline: "",
						"label-width": "90px"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, {
								label: "关联页编码",
								prop: "code"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: metaForm.code,
									"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => metaForm.code = $event),
									placeholder: "如：tpl_project_overview_related",
									disabled: !!metaForm.id,
									style: { "width": "240px" }
								}, null, 8, ["modelValue", "disabled"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "关联页名称",
								prop: "name"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: metaForm.name,
									"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => metaForm.name = $event),
									placeholder: "请输入关联页名称",
									style: { "width": "220px" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "业务类型" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: metaForm.bizType,
									"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => metaForm.bizType = $event),
									placeholder: "如：PROJECT",
									style: { "width": "160px" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "描述" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: metaForm.description,
									"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => metaForm.description = $event),
									placeholder: "关联页描述",
									style: { "width": "320px" }
								}, null, 8, ["modelValue"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}),
				createElementVNode("div", _hoisted_5, [
					createVNode(_component_el_card, {
						shadow: "never",
						class: "panel panel-left",
						"body-style": { padding: "8px" }
					}, {
						header: withCtx(() => [..._cache[34] || (_cache[34] = [createElementVNode("span", { class: "panel-title" }, "组件库", -1)])]),
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(componentGroups.value, (group) => {
							return openBlock(), createElementBlock("div", {
								key: group.title,
								class: "comp-group"
							}, [createElementVNode("div", _hoisted_6, toDisplayString(group.title), 1), createElementVNode("div", _hoisted_7, [(openBlock(true), createElementBlock(Fragment, null, renderList(group.items, (comp) => {
								return openBlock(), createElementBlock("div", {
									key: comp.type,
									class: "comp-item",
									draggable: "true",
									onDragstart: ($event) => onCompDragStart($event, comp),
									onClick: ($event) => addComponent(comp)
								}, [createVNode(_component_el_icon, null, {
									default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(comp.icon)))]),
									_: 2
								}, 1024), createElementVNode("span", null, toDisplayString(comp.label), 1)], 40, _hoisted_8);
							}), 128))])]);
						}), 128))]),
						_: 1
					}),
					createVNode(_component_el_card, {
						shadow: "never",
						class: "panel panel-center",
						"body-style": { padding: "12px" }
					}, {
						header: withCtx(() => [createElementVNode("div", _hoisted_9, [_cache[35] || (_cache[35] = createElementVNode("span", { class: "panel-title" }, "画布", -1)), createVNode(_component_el_form, {
							inline: "",
							size: "small",
							class: "canvas-config"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_form_item, { label: "主实体" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: relatedConfig.mainEntity,
										"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => relatedConfig.mainEntity = $event),
										placeholder: "如 project/asset",
										style: { "width": "140px" }
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "布局" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: relatedConfig.layout,
										"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => relatedConfig.layout = $event),
										style: { "width": "110px" }
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "栅格 grid",
												value: unref(RelatedPageLayout).GRID
											}, null, 8, ["value"]),
											createVNode(_component_el_option, {
												label: "标签页 tabs",
												value: unref(RelatedPageLayout).TABS
											}, null, 8, ["value"]),
											createVNode(_component_el_option, {
												label: "折叠面板 collapse",
												value: unref(RelatedPageLayout).COLLAPSE
											}, null, 8, ["value"])
										]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								relatedConfig.layout === unref(RelatedPageLayout).GRID ? (openBlock(), createBlock(_component_el_form_item, {
									key: 0,
									label: "栅格间距"
								}, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: relatedConfig.gutter,
										"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => relatedConfig.gutter = $event),
										min: 0,
										max: 60,
										step: 4,
										style: { "width": "110px" }
									}, null, 8, ["modelValue"])]),
									_: 1
								})) : createCommentVNode("", true)
							]),
							_: 1
						})])]),
						default: withCtx(() => [createElementVNode("div", {
							class: normalizeClass(["canvas-dropzone", { empty: relatedConfig.sections.length === 0 }]),
							onDragover: onCanvasDragOver,
							onDrop: onCanvasDrop
						}, [relatedConfig.sections.length === 0 ? (openBlock(), createElementBlock("div", _hoisted_10, [createVNode(_component_el_icon, { size: 40 }, {
							default: withCtx(() => [createVNode(_component_Plus)]),
							_: 1
						}), _cache[36] || (_cache[36] = createElementVNode("p", null, "从左侧拖拽区块组件到此处", -1))])) : (openBlock(), createElementBlock("div", _hoisted_11, [(openBlock(true), createElementBlock(Fragment, null, renderList(relatedConfig.sections, (section, idx) => {
							var _section$order;
							return openBlock(), createElementBlock("div", {
								key: section.id,
								class: normalizeClass(["item-card", { active: selectedId.value === section.id }]),
								draggable: "true",
								onDragstart: ($event) => onItemDragStart($event, section.id),
								onDragover: onCanvasDragOver,
								onDrop: ($event) => onItemDrop($event, section.id),
								onClick: ($event) => selectItem(section.id)
							}, [createElementVNode("div", _hoisted_13, [
								createVNode(_component_el_tag, {
									size: "small",
									type: "info"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(section.type), 1)]),
									_: 2
								}, 1024),
								createElementVNode("span", _hoisted_14, toDisplayString(section.title), 1),
								section.pageCode ? (openBlock(), createElementBlock("span", _hoisted_15, "pageCode: " + toDisplayString(section.pageCode), 1)) : createCommentVNode("", true),
								createElementVNode("span", _hoisted_16, "span: " + toDisplayString(formatSpan(section.span)), 1),
								createElementVNode("span", _hoisted_17, "order: " + toDisplayString((_section$order = section.order) !== null && _section$order !== void 0 ? _section$order : 100), 1),
								createElementVNode("div", _hoisted_18, [createVNode(_component_el_button_group, { size: "small" }, {
									default: withCtx(() => [
										createVNode(_component_el_button, {
											icon: "Top",
											disabled: idx === 0,
											onClick: withModifiers(($event) => moveItem(section.id, -1), ["stop"])
										}, null, 8, ["disabled", "onClick"]),
										createVNode(_component_el_button, {
											icon: "Bottom",
											disabled: idx === relatedConfig.sections.length - 1,
											onClick: withModifiers(($event) => moveItem(section.id, 1), ["stop"])
										}, null, 8, ["disabled", "onClick"]),
										createVNode(_component_el_button, {
											icon: "CopyDocument",
											onClick: withModifiers(($event) => duplicateItem(section.id), ["stop"])
										}, null, 8, ["onClick"]),
										createVNode(_component_el_button, {
											icon: "Delete",
											type: "danger",
											onClick: withModifiers(($event) => removeItem(section.id), ["stop"])
										}, null, 8, ["onClick"])
									]),
									_: 2
								}, 1024)])
							]), createElementVNode("div", _hoisted_19, [section.pageUrl ? (openBlock(), createBlock(_component_el_tag, {
								key: 0,
								size: "small",
								type: "success"
							}, {
								default: withCtx(() => [createTextVNode("url: " + toDisplayString(section.pageUrl), 1)]),
								_: 2
							}, 1024)) : createCommentVNode("", true), section.visible ? (openBlock(), createBlock(_component_el_tag, {
								key: 1,
								size: "small",
								type: "info"
							}, {
								default: withCtx(() => [createTextVNode("visible: " + toDisplayString(section.visible), 1)]),
								_: 2
							}, 1024)) : createCommentVNode("", true)])], 42, _hoisted_12);
						}), 128))]))], 34)]),
						_: 1
					}),
					createVNode(_component_el_card, {
						shadow: "never",
						class: "panel panel-right",
						"body-style": { padding: "12px" }
					}, {
						header: withCtx(() => [..._cache[37] || (_cache[37] = [createElementVNode("span", { class: "panel-title" }, "属性面板", -1)])]),
						default: withCtx(() => [!selectedSection.value ? (openBlock(), createElementBlock("div", _hoisted_20, [createVNode(_component_el_empty, {
							description: "请选择一个区块",
							"image-size": 80
						})])) : (openBlock(), createBlock(_component_el_form, {
							key: 1,
							model: selectedSection.value,
							"label-width": "100px",
							size: "small"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[38] || (_cache[38] = [createTextVNode("基础属性", -1)])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "区块标题" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedSection.value.title,
										"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => selectedSection.value.title = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "区块类型" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: selectedSection.value.type,
										"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => selectedSection.value.type = $event),
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "表单 form",
												value: unref(SectionType).FORM
											}, null, 8, ["value"]),
											createVNode(_component_el_option, {
												label: "列表 list",
												value: unref(SectionType).LIST
											}, null, 8, ["value"]),
											createVNode(_component_el_option, {
												label: "标签页 tab",
												value: unref(SectionType).TAB
											}, null, 8, ["value"]),
											createVNode(_component_el_option, {
												label: "自定义 custom",
												value: unref(SectionType).CUSTOM
											}, null, 8, ["value"])
										]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[39] || (_cache[39] = [createTextVNode("页面引用", -1)])]),
									_: 1
								}),
								selectedSection.value.type !== "custom" ? (openBlock(), createBlock(_component_el_form_item, {
									key: 0,
									label: "页面编码"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedSection.value.pageCode,
										"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => selectedSection.value.pageCode = $event),
										placeholder: "引用的低代码页面编码"
									}, null, 8, ["modelValue"])]),
									_: 1
								})) : createCommentVNode("", true),
								selectedSection.value.type === "custom" ? (openBlock(), createBlock(_component_el_form_item, {
									key: 1,
									label: "页面 URL"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedSection.value.pageUrl,
										"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => selectedSection.value.pageUrl = $event),
										placeholder: "/project/{id}/basic 或 http(s)://..."
									}, null, 8, ["modelValue"])]),
									_: 1
								})) : createCommentVNode("", true),
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[40] || (_cache[40] = [createTextVNode("布局与排序", -1)])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "响应式栅格" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: isSectionResponsive.value,
										"onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => isSectionResponsive.value = $event)
									}, null, 8, ["modelValue"]), _cache[41] || (_cache[41] = createElementVNode("span", { class: "form-tip" }, "开启后按 xs/sm/md/lg/xl 五档断点配置", -1))]),
									_: 1
								}),
								!isSectionResponsive.value ? (openBlock(), createBlock(_component_el_form_item, {
									key: 2,
									label: "栅格宽度"
								}, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: sectionSpan.value,
										"onUpdate:modelValue": _cache[12] || (_cache[12] = ($event) => sectionSpan.value = $event),
										min: 1,
										max: 24,
										step: 1,
										style: { "width": "100%" }
									}, null, 8, ["modelValue"]), _cache[42] || (_cache[42] = createElementVNode("span", { class: "form-tip" }, "grid 布局下生效，1-24", -1))]),
									_: 1
								})) : (openBlock(), createBlock(_component_el_collapse, {
									key: 3,
									modelValue: sectionResponsiveCollapse.value,
									"onUpdate:modelValue": _cache[18] || (_cache[18] = ($event) => sectionResponsiveCollapse.value = $event),
									class: "resp-collapse"
								}, {
									default: withCtx(() => [createVNode(_component_el_collapse_item, {
										title: "响应式断点（1-24）",
										name: "resp"
									}, {
										default: withCtx(() => [
											createVNode(_component_el_form_item, { label: "xs" }, {
												default: withCtx(() => [createVNode(_component_el_input_number, {
													"model-value": getSectionBreakpoint("xs"),
													min: 1,
													max: 24,
													step: 1,
													"controls-position": "right",
													style: { "width": "100%" },
													"onUpdate:modelValue": _cache[13] || (_cache[13] = (v) => setSectionBreakpoint("xs", v))
												}, null, 8, ["model-value"])]),
												_: 1
											}),
											createVNode(_component_el_form_item, { label: "sm" }, {
												default: withCtx(() => [createVNode(_component_el_input_number, {
													"model-value": getSectionBreakpoint("sm"),
													min: 1,
													max: 24,
													step: 1,
													"controls-position": "right",
													style: { "width": "100%" },
													"onUpdate:modelValue": _cache[14] || (_cache[14] = (v) => setSectionBreakpoint("sm", v))
												}, null, 8, ["model-value"])]),
												_: 1
											}),
											createVNode(_component_el_form_item, { label: "md" }, {
												default: withCtx(() => [createVNode(_component_el_input_number, {
													"model-value": getSectionBreakpoint("md"),
													min: 1,
													max: 24,
													step: 1,
													"controls-position": "right",
													style: { "width": "100%" },
													"onUpdate:modelValue": _cache[15] || (_cache[15] = (v) => setSectionBreakpoint("md", v))
												}, null, 8, ["model-value"])]),
												_: 1
											}),
											createVNode(_component_el_form_item, { label: "lg" }, {
												default: withCtx(() => [createVNode(_component_el_input_number, {
													"model-value": getSectionBreakpoint("lg"),
													min: 1,
													max: 24,
													step: 1,
													"controls-position": "right",
													style: { "width": "100%" },
													"onUpdate:modelValue": _cache[16] || (_cache[16] = (v) => setSectionBreakpoint("lg", v))
												}, null, 8, ["model-value"])]),
												_: 1
											}),
											createVNode(_component_el_form_item, { label: "xl" }, {
												default: withCtx(() => [createVNode(_component_el_input_number, {
													"model-value": getSectionBreakpoint("xl"),
													min: 1,
													max: 24,
													step: 1,
													"controls-position": "right",
													style: { "width": "100%" },
													"onUpdate:modelValue": _cache[17] || (_cache[17] = (v) => setSectionBreakpoint("xl", v))
												}, null, 8, ["model-value"])]),
												_: 1
											})
										]),
										_: 1
									})]),
									_: 1
								}, 8, ["modelValue"])),
								createVNode(_component_el_form_item, { label: "排序号" }, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: selectedSection.value.order,
										"onUpdate:modelValue": _cache[19] || (_cache[19] = ($event) => selectedSection.value.order = $event),
										min: 0,
										max: 9999,
										step: 10,
										style: { "width": "100%" }
									}, null, 8, ["modelValue"]), _cache[43] || (_cache[43] = createElementVNode("span", { class: "form-tip" }, "升序排列，相同 order 按数组顺序", -1))]),
									_: 1
								}),
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[44] || (_cache[44] = [createTextVNode("显示条件", -1)])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "visible 表达式" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedSection.value.visible,
										"onUpdate:modelValue": _cache[20] || (_cache[20] = ($event) => selectedSection.value.visible = $event),
										type: "textarea",
										rows: 2,
										placeholder: "如：row.status === 'IN_PROGRESS'，留空表示始终显示"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[45] || (_cache[45] = [createTextVNode("props 传参", -1)])]),
									_: 1
								}),
								createElementVNode("div", _hoisted_21, [(openBlock(true), createElementBlock(Fragment, null, renderList(Object.keys(selectedSection.value.props || {}), (key) => {
									return openBlock(), createElementBlock("div", {
										key,
										class: "prop-row"
									}, [
										createVNode(_component_el_input, {
											modelValue: selectedSection.value.props[key],
											"onUpdate:modelValue": ($event) => selectedSection.value.props[key] = $event,
											placeholder: key,
											size: "small"
										}, null, 8, [
											"modelValue",
											"onUpdate:modelValue",
											"placeholder"
										]),
										createVNode(_component_el_input, {
											"model-value": key,
											disabled: "",
											size: "small",
											style: { "width": "30%" }
										}, null, 8, ["model-value"]),
										createVNode(_component_el_button, {
											icon: "Delete",
											type: "danger",
											size: "small",
											onClick: ($event) => removeSectionProp(selectedSection.value, key)
										}, null, 8, ["onClick"])
									]);
								}), 128)), createVNode(_component_el_button, {
									icon: "Plus",
									size: "small",
									onClick: _cache[21] || (_cache[21] = ($event) => addSectionProp(selectedSection.value))
								}, {
									default: withCtx(() => [..._cache[46] || (_cache[46] = [createTextVNode("添加 props", -1)])]),
									_: 1
								})])
							]),
							_: 1
						}, 8, ["model"]))]),
						_: 1
					})
				]),
				createVNode(_component_el_dialog, {
					modelValue: previewVisible.value,
					"onUpdate:modelValue": _cache[23] || (_cache[23] = ($event) => previewVisible.value = $event),
					title: "关联页预览",
					width: "90%",
					top: "3vh",
					"close-on-click-modal": false,
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[22] || (_cache[22] = ($event) => previewVisible.value = false) }, {
						default: withCtx(() => [..._cache[47] || (_cache[47] = [createTextVNode("关闭", -1)])]),
						_: 1
					})]),
					default: withCtx(() => [createVNode(LowCodeRelatedPageRenderer_default, {
						config: relatedConfig,
						"context-data": {
							row: {
								id: 1,
								status: "IN_PROGRESS",
								projectId: "P2025001",
								warrantyStatus: "ACTIVE"
							},
							context: {}
						}
					}, null, 8, ["config"])]),
					_: 1
				}, 8, ["modelValue"])
			]);
		};
	}
}), [["__scopeId", "data-v-6194891c"]]);
//#endregion
export { related_page_designer_default as default };

//# sourceMappingURL=related-page-designer-BTULUNRe.js.map