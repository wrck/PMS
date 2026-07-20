import { d as useRouter } from "./request-BQrAOfxW.js";
import { J as publishRelatedPage, O as exportRelatedPage, W as listRelatedPages, h as archiveRelatedPage, w as deleteRelatedPage } from "./lowcode-F-suzo7c.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withKeys } from "vue";
//#region src/views/lowcode/related-page-list/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "related-page-list-page" };
var _hoisted_2 = { style: {
	"display": "flex",
	"justify-content": "space-between",
	"align-items": "center"
} };
//#endregion
//#region src/views/lowcode/related-page-list/index.vue
var related_page_list_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		/**
		* 低代码关联页配置列表页。
		*
		* <p>提供关联页配置的分页查询、新建（跳转到设计器）、编辑、删除、发布、归档、导入、导出。</p>
		*/
		const router = useRouter();
		const loading = ref(false);
		const tableData = ref([]);
		const total = ref(0);
		const query = reactive({
			page: 1,
			size: 10,
			code: "",
			name: "",
			status: "",
			bizType: ""
		});
		/** 状态选项 */
		const statusOptions = [
			{
				label: "草稿",
				value: "DRAFT"
			},
			{
				label: "已发布",
				value: "PUBLISHED"
			},
			{
				label: "已归档",
				value: "ARCHIVED"
			}
		];
		/** 状态标签颜色 */
		function statusTagType(status) {
			if (status === "PUBLISHED") return "success";
			if (status === "ARCHIVED") return "info";
			return "warning";
		}
		/** 加载列表 */
		async function loadData() {
			loading.value = true;
			try {
				const res = await listRelatedPages(query);
				tableData.value = res.records || [];
				total.value = res.total || 0;
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		function handleSearch() {
			query.page = 1;
			loadData();
		}
		function handleReset() {
			query.code = "";
			query.name = "";
			query.status = "";
			query.bizType = "";
			query.page = 1;
			loadData();
		}
		function handleAdd() {
			router.push("/lowcode/related-page-designer");
		}
		function handleEdit(row) {
			router.push({
				path: "/lowcode/related-page-designer",
				query: { id: String(row.id) }
			});
		}
		async function handleDelete(row) {
			if (!row.id) return;
			try {
				await ElMessageBox.confirm(`确认删除关联页「${row.name}」？`, "确认", { type: "warning" });
				await deleteRelatedPage(row.id);
				ElMessage.success("删除成功");
				loadData();
			} catch (_unused2) {}
		}
		async function handlePublish(row) {
			if (!row.id) return;
			try {
				await publishRelatedPage(row.id);
				ElMessage.success("发布成功");
				loadData();
			} catch (_unused3) {}
		}
		async function handleArchive(row) {
			if (!row.id) return;
			try {
				await ElMessageBox.confirm(`确认归档关联页「${row.name}」？`, "确认", { type: "warning" });
				await archiveRelatedPage(row.id);
				ElMessage.success("归档成功");
				loadData();
			} catch (_unused4) {}
		}
		async function handleExport(row) {
			if (!row.code) return;
			try {
				await exportRelatedPage(row.code);
				ElMessage.success("导出成功");
			} catch (_unused5) {}
		}
		onMounted(() => {
			loadData();
		});
		return (_ctx, _cache) => {
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_pagination = resolveComponent("el-pagination");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_card, {
				shadow: "never",
				"body-style": { padding: "12px 16px" }
			}, {
				default: withCtx(() => [createVNode(_component_el_form, {
					inline: "",
					model: query
				}, {
					default: withCtx(() => [
						createVNode(_component_el_form_item, { label: "编码" }, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: query.code,
								"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => query.code = $event),
								placeholder: "关联页编码",
								clearable: "",
								style: { "width": "180px" },
								onKeyup: withKeys(handleSearch, ["enter"])
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, { label: "名称" }, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: query.name,
								"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => query.name = $event),
								placeholder: "关联页名称",
								clearable: "",
								style: { "width": "180px" },
								onKeyup: withKeys(handleSearch, ["enter"])
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, { label: "状态" }, {
							default: withCtx(() => [createVNode(_component_el_select, {
								modelValue: query.status,
								"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => query.status = $event),
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
						createVNode(_component_el_form_item, { label: "业务类型" }, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: query.bizType,
								"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => query.bizType = $event),
								placeholder: "业务类型",
								clearable: "",
								style: { "width": "140px" },
								onKeyup: withKeys(handleSearch, ["enter"])
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, null, {
							default: withCtx(() => [createVNode(_component_el_button, {
								type: "primary",
								icon: "Search",
								onClick: handleSearch
							}, {
								default: withCtx(() => [..._cache[6] || (_cache[6] = [createTextVNode("查询", -1)])]),
								_: 1
							}), createVNode(_component_el_button, {
								icon: "Refresh",
								onClick: handleReset
							}, {
								default: withCtx(() => [..._cache[7] || (_cache[7] = [createTextVNode("重置", -1)])]),
								_: 1
							})]),
							_: 1
						})
					]),
					_: 1
				}, 8, ["model"])]),
				_: 1
			}), createVNode(_component_el_card, {
				shadow: "never",
				style: { "margin-top": "12px" }
			}, {
				header: withCtx(() => [createElementVNode("div", _hoisted_2, [_cache[9] || (_cache[9] = createElementVNode("span", null, "关联页配置列表", -1)), createVNode(_component_el_button, {
					type: "primary",
					icon: "Plus",
					onClick: handleAdd
				}, {
					default: withCtx(() => [..._cache[8] || (_cache[8] = [createTextVNode("新建关联页", -1)])]),
					_: 1
				})])]),
				default: withCtx(() => [withDirectives((openBlock(), createBlock(_component_el_table, {
					data: tableData.value,
					border: "",
					stripe: ""
				}, {
					default: withCtx(() => [
						createVNode(_component_el_table_column, {
							type: "index",
							label: "#",
							width: "50"
						}),
						createVNode(_component_el_table_column, {
							prop: "code",
							label: "编码",
							"min-width": "180",
							"show-overflow-tooltip": ""
						}),
						createVNode(_component_el_table_column, {
							prop: "name",
							label: "名称",
							"min-width": "160",
							"show-overflow-tooltip": ""
						}),
						createVNode(_component_el_table_column, {
							prop: "bizType",
							label: "业务类型",
							width: "120"
						}),
						createVNode(_component_el_table_column, {
							prop: "status",
							label: "状态",
							width: "100",
							align: "center"
						}, {
							default: withCtx(({ row }) => [createVNode(_component_el_tag, { type: statusTagType(row.status) }, {
								default: withCtx(() => [createTextVNode(toDisplayString(row.status), 1)]),
								_: 2
							}, 1032, ["type"])]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							prop: "version",
							label: "版本",
							width: "80",
							align: "center"
						}),
						createVNode(_component_el_table_column, {
							prop: "updateTime",
							label: "更新时间",
							width: "170"
						}),
						createVNode(_component_el_table_column, {
							label: "操作",
							width: "280",
							fixed: "right"
						}, {
							default: withCtx(({ row }) => [
								createVNode(_component_el_button, {
									size: "small",
									link: "",
									type: "primary",
									onClick: ($event) => handleEdit(row)
								}, {
									default: withCtx(() => [..._cache[10] || (_cache[10] = [createTextVNode("编辑", -1)])]),
									_: 1
								}, 8, ["onClick"]),
								row.status === "DRAFT" ? (openBlock(), createBlock(_component_el_button, {
									key: 0,
									size: "small",
									link: "",
									type: "success",
									onClick: ($event) => handlePublish(row)
								}, {
									default: withCtx(() => [..._cache[11] || (_cache[11] = [createTextVNode("发布", -1)])]),
									_: 1
								}, 8, ["onClick"])) : createCommentVNode("", true),
								row.status === "PUBLISHED" ? (openBlock(), createBlock(_component_el_button, {
									key: 1,
									size: "small",
									link: "",
									type: "warning",
									onClick: ($event) => handleArchive(row)
								}, {
									default: withCtx(() => [..._cache[12] || (_cache[12] = [createTextVNode("归档", -1)])]),
									_: 1
								}, 8, ["onClick"])) : createCommentVNode("", true),
								createVNode(_component_el_button, {
									size: "small",
									link: "",
									onClick: ($event) => handleExport(row)
								}, {
									default: withCtx(() => [..._cache[13] || (_cache[13] = [createTextVNode("导出", -1)])]),
									_: 1
								}, 8, ["onClick"]),
								createVNode(_component_el_button, {
									size: "small",
									link: "",
									type: "danger",
									onClick: ($event) => handleDelete(row)
								}, {
									default: withCtx(() => [..._cache[14] || (_cache[14] = [createTextVNode("删除", -1)])]),
									_: 1
								}, 8, ["onClick"])
							]),
							_: 1
						})
					]),
					_: 1
				}, 8, ["data"])), [[_directive_loading, loading.value]]), createVNode(_component_el_pagination, {
					"current-page": query.page,
					"onUpdate:currentPage": _cache[4] || (_cache[4] = ($event) => query.page = $event),
					"page-size": query.size,
					"onUpdate:pageSize": _cache[5] || (_cache[5] = ($event) => query.size = $event),
					total: total.value,
					"page-sizes": [
						10,
						20,
						50,
						100
					],
					layout: "total, sizes, prev, pager, next, jumper",
					style: {
						"margin-top": "12px",
						"justify-content": "flex-end"
					},
					onCurrentChange: loadData,
					onSizeChange: loadData
				}, null, 8, [
					"current-page",
					"page-size",
					"total"
				])]),
				_: 1
			})]);
		};
	}
}), [["__scopeId", "data-v-668d4260"]]);
//#endregion
export { related_page_list_default as default };

//# sourceMappingURL=related-page-list-Dd4hZhTv.js.map