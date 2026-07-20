import { a as put, d as useRouter, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives } from "vue";
//#region src/api/notification.ts
function listNotifications(params) {
	return get("/api/notification/page", params);
}
function getUnreadCount() {
	return get("/api/notification/unread/count");
}
function markAsRead(id) {
	return put(`/api/notification/${id}/read`);
}
function markAllAsRead() {
	return put("/api/notification/read/all");
}
//#endregion
//#region src/views/notification/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "card-header" };
//#endregion
//#region src/views/notification/index.vue
var notification_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const router = useRouter();
		const loading = ref(false);
		const tableData = ref([]);
		const total = ref(0);
		const unreadCount = ref(0);
		const activeMenu = ref("all");
		const query = reactive({
			page: 1,
			size: 10,
			category: void 0,
			readStatus: void 0
		});
		const menuItems = [
			{
				index: "all",
				label: "全部"
			},
			{
				index: "unread",
				label: "未读",
				readStatus: "UNREAD"
			},
			{
				index: "MILESTONE",
				label: "里程碑",
				category: "MILESTONE"
			},
			{
				index: "TASK",
				label: "任务",
				category: "TASK"
			},
			{
				index: "APPROVAL",
				label: "审批",
				category: "APPROVAL"
			},
			{
				index: "PUNCH_LIST",
				label: "Punch List",
				category: "PUNCH_LIST"
			},
			{
				index: "WARRANTY",
				label: "质保",
				category: "WARRANTY"
			},
			{
				index: "RMA",
				label: "RMA",
				category: "RMA"
			},
			{
				index: "SETTLEMENT",
				label: "结算",
				category: "SETTLEMENT"
			}
		];
		const categoryMeta = {
			MILESTONE: {
				label: "里程碑",
				tagType: "primary"
			},
			TASK: {
				label: "任务",
				tagType: "warning"
			},
			APPROVAL: {
				label: "审批",
				tagType: "danger"
			},
			PUNCH_LIST: {
				label: "Punch List",
				tagType: "info"
			},
			WARRANTY: {
				label: "质保",
				tagType: "success"
			},
			RMA: {
				label: "RMA",
				tagType: "danger"
			},
			SETTLEMENT: {
				label: "结算",
				tagType: "info"
			}
		};
		function getCategoryMeta(category) {
			var _categoryMeta;
			return (_categoryMeta = categoryMeta[category !== null && category !== void 0 ? category : ""]) !== null && _categoryMeta !== void 0 ? _categoryMeta : {
				label: category !== null && category !== void 0 ? category : "-",
				tagType: "info"
			};
		}
		function formatDateTime(val) {
			var _val$replace$slice;
			return (_val$replace$slice = val === null || val === void 0 ? void 0 : val.replace("T", " ").slice(0, 19)) !== null && _val$replace$slice !== void 0 ? _val$replace$slice : "-";
		}
		async function loadData() {
			loading.value = true;
			try {
				var _res$records, _res$total;
				const params = {
					page: query.page,
					size: query.size
				};
				if (query.category) params.category = query.category;
				if (query.readStatus) params.readStatus = query.readStatus;
				const res = await listNotifications(params);
				tableData.value = (_res$records = res.records) !== null && _res$records !== void 0 ? _res$records : [];
				total.value = (_res$total = res.total) !== null && _res$total !== void 0 ? _res$total : 0;
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		async function loadUnreadCount() {
			try {
				unreadCount.value = await getUnreadCount();
			} catch (_unused2) {}
		}
		function handleMenuSelect(index) {
			activeMenu.value = index;
			const item = menuItems.find((m) => m.index === index);
			query.category = item === null || item === void 0 ? void 0 : item.category;
			query.readStatus = item === null || item === void 0 ? void 0 : item.readStatus;
			query.page = 1;
			loadData();
		}
		function handlePageChange(p) {
			query.page = p;
			loadData();
		}
		function handleSizeChange(s) {
			query.size = s;
			query.page = 1;
			loadData();
		}
		function handleMarkAllRead() {
			ElMessageBox.confirm("确认将所有未读通知标记为已读吗？", "全部已读", { type: "warning" }).then(async () => {
				await markAllAsRead();
				ElMessage.success("已全部标记为已读");
				loadData();
				loadUnreadCount();
			}).catch(() => {});
		}
		async function handleView(row) {
			if (row.readStatus === "UNREAD") try {
				await markAsRead(row.id);
				row.readStatus = "READ";
				unreadCount.value = Math.max(0, unreadCount.value - 1);
			} catch (_unused3) {}
			jumpToBiz(row);
		}
		function jumpToBiz(notif) {
			if (!notif.bizType || !notif.bizId) return;
			const path = {
				PROJECT: `/project/detail/${notif.bizId}`,
				PUNCH_LIST: `/punch-list`,
				RMA: `/rma`,
				WARRANTY: `/warranty`,
				DELIVERABLE: `/deliverable`,
				MILESTONE: `/project/detail/${notif.bizId}`
			}[notif.bizType];
			if (path) router.push(path);
		}
		onMounted(() => {
			loadUnreadCount();
			loadData();
		});
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_badge = resolveComponent("el-badge");
			const _component_el_menu_item = resolveComponent("el-menu-item");
			const _component_el_menu = resolveComponent("el-menu");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_pagination = resolveComponent("el-pagination");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_card = resolveComponent("el-card");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_card, { shadow: "never" }, {
				header: withCtx(() => [createElementVNode("div", _hoisted_2, [_cache[1] || (_cache[1] = createElementVNode("span", { class: "page-title" }, "消息中心", -1)), createVNode(_component_el_button, {
					type: "primary",
					icon: "Check",
					onClick: handleMarkAllRead
				}, {
					default: withCtx(() => [..._cache[0] || (_cache[0] = [createTextVNode("全部已读", -1)])]),
					_: 1
				})])]),
				default: withCtx(() => [createVNode(_component_el_row, { gutter: 16 }, {
					default: withCtx(() => [createVNode(_component_el_col, { span: 4 }, {
						default: withCtx(() => [createVNode(_component_el_menu, {
							"default-active": activeMenu.value,
							onSelect: handleMenuSelect
						}, {
							default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(menuItems, (item) => {
								return createVNode(_component_el_menu_item, {
									key: item.index,
									index: item.index
								}, {
									default: withCtx(() => [createElementVNode("span", null, toDisplayString(item.label), 1), item.index === "unread" && unreadCount.value > 0 ? (openBlock(), createBlock(_component_el_badge, {
										key: 0,
										value: unreadCount.value,
										class: "menu-badge"
									}, null, 8, ["value"])) : createCommentVNode("", true)]),
									_: 2
								}, 1032, ["index"]);
							}), 64))]),
							_: 1
						}, 8, ["default-active"])]),
						_: 1
					}), createVNode(_component_el_col, { span: 20 }, {
						default: withCtx(() => [withDirectives((openBlock(), createBlock(_component_el_table, {
							data: tableData.value,
							border: "",
							stripe: ""
						}, {
							empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无通知" })]),
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									prop: "title",
									label: "标题",
									"min-width": "180",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "content",
									label: "内容预览",
									"min-width": "240",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									label: "分类",
									width: "120",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, {
										type: getCategoryMeta(row.category).tagType,
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(getCategoryMeta(row.category).label), 1)]),
										_: 2
									}, 1032, ["type"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "已读状态",
									width: "100",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, {
										type: row.readStatus === "UNREAD" ? "danger" : "info",
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(row.readStatus === "UNREAD" ? "未读" : "已读"), 1)]),
										_: 2
									}, 1032, ["type"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "时间",
									width: "160",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDateTime(row.createdAt)), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "操作",
									width: "100",
									fixed: "right"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_button, {
										link: "",
										type: "primary",
										onClick: ($event) => handleView(row)
									}, {
										default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("查看", -1)])]),
										_: 1
									}, 8, ["onClick"])]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["data"])), [[_directive_loading, loading.value]]), createVNode(_component_el_pagination, {
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
						])]),
						_: 1
					})]),
					_: 1
				})]),
				_: 1
			})]);
		};
	}
}), [["__scopeId", "data-v-43480837"]]);
//#endregion
export { notification_default as default };

//# sourceMappingURL=notification-Cv9_9ivL.js.map