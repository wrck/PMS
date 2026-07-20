import { d as useRouter } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { l as getDashboard } from "./project-Brd7mmQb.js";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, normalizeClass, normalizeStyle, onMounted, openBlock, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives } from "vue";
//#region src/views/project/kanban/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "card-header" };
var _hoisted_3 = { class: "page-summary" };
var _hoisted_4 = { class: "kanban-board" };
var _hoisted_5 = { class: "col-title" };
var _hoisted_6 = { class: "kanban-col-body" };
var _hoisted_7 = ["onClick"];
var _hoisted_8 = ["title"];
var _hoisted_9 = { class: "card-code" };
var _hoisted_10 = { class: "card-progress" };
var _hoisted_11 = { class: "card-row" };
var _hoisted_12 = { class: "card-row" };
var _hoisted_13 = { class: "card-row" };
//#endregion
//#region src/views/project/kanban/index.vue
var kanban_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const router = useRouter();
		const loading = ref(false);
		const dashboard = ref({});
		const columns = [
			{
				status: "PENDING",
				title: "待审批",
				color: "#909399"
			},
			{
				status: "APPROVED",
				title: "已立项",
				color: "#e6a23c"
			},
			{
				status: "IN_PROGRESS",
				title: "执行中",
				color: "#409eff"
			},
			{
				status: "INITIAL_ACCEPTANCE",
				title: "初验",
				color: "#e6a23c"
			},
			{
				status: "FINAL_ACCEPTANCE",
				title: "终验中",
				color: "#f56c6c"
			},
			{
				status: "COMPLETED",
				title: "已完成",
				color: "#67c23a"
			}
		];
		function getColumnProjects(status) {
			var _dashboard$value$stat;
			return (_dashboard$value$stat = dashboard.value[status]) !== null && _dashboard$value$stat !== void 0 ? _dashboard$value$stat : [];
		}
		function getColumnCount(status) {
			return getColumnProjects(status).length;
		}
		function formatDate(date) {
			if (!date) return "-";
			return date.length > 10 ? date.substring(0, 10) : date;
		}
		function isOverdue(project) {
			if (!project.planEndDate) return false;
			if (project.status === "COMPLETED" || project.status === "CLOSED") return false;
			const end = project.planEndDate.length > 10 ? project.planEndDate.substring(0, 10) : project.planEndDate;
			const today = /* @__PURE__ */ new Date();
			return end < today.getFullYear() + "-" + String(today.getMonth() + 1).padStart(2, "0") + "-" + String(today.getDate()).padStart(2, "0");
		}
		function viewDetail(project) {
			if (!project.id) return;
			router.push(`/project/detail/${project.id}`);
		}
		async function loadDashboard() {
			loading.value = true;
			try {
				var _await$getDashboard;
				dashboard.value = (_await$getDashboard = await getDashboard()) !== null && _await$getDashboard !== void 0 ? _await$getDashboard : {};
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		const totalCount = computed(() => columns.reduce((sum, col) => sum + getColumnCount(col.status), 0));
		onMounted(loadDashboard);
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_badge = resolveComponent("el-badge");
			const _component_el_progress = resolveComponent("el-progress");
			const _component_User = resolveComponent("User");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_Avatar = resolveComponent("Avatar");
			const _component_Calendar = resolveComponent("Calendar");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_card = resolveComponent("el-card");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_card, { shadow: "never" }, {
				header: withCtx(() => [createElementVNode("div", _hoisted_2, [
					_cache[1] || (_cache[1] = createElementVNode("span", { class: "page-title" }, "交付看板", -1)),
					createElementVNode("span", _hoisted_3, "项目总数：" + toDisplayString(totalCount.value), 1),
					createVNode(_component_el_button, {
						class: "refresh-btn",
						icon: "Refresh",
						link: "",
						onClick: loadDashboard
					}, {
						default: withCtx(() => [..._cache[0] || (_cache[0] = [createTextVNode("刷新", -1)])]),
						_: 1
					})
				])]),
				default: withCtx(() => [withDirectives((openBlock(), createElementBlock("div", _hoisted_4, [(openBlock(), createElementBlock(Fragment, null, renderList(columns, (col) => {
					return createElementVNode("div", {
						key: col.status,
						class: "kanban-col"
					}, [createElementVNode("div", {
						class: "kanban-col-header",
						style: normalizeStyle({ borderTopColor: col.color })
					}, [createElementVNode("span", _hoisted_5, [createElementVNode("span", {
						class: "col-dot",
						style: normalizeStyle({ backgroundColor: col.color })
					}, null, 4), createTextVNode(" " + toDisplayString(col.title), 1)]), createVNode(_component_el_badge, {
						value: getColumnCount(col.status),
						max: 999,
						class: "col-badge"
					}, null, 8, ["value"])], 4), createElementVNode("div", _hoisted_6, [(openBlock(true), createElementBlock(Fragment, null, renderList(getColumnProjects(col.status), (project) => {
						var _project$progress, _project$progress2;
						return openBlock(), createElementBlock("div", {
							key: project.id,
							class: normalizeClass(["kanban-card", { overdue: isOverdue(project) }]),
							onClick: ($event) => viewDetail(project)
						}, [
							createElementVNode("div", {
								class: "card-name",
								title: project.name
							}, toDisplayString(project.name), 9, _hoisted_8),
							createElementVNode("div", _hoisted_9, toDisplayString(project.code || "-"), 1),
							createElementVNode("div", _hoisted_10, [createVNode(_component_el_progress, {
								percentage: Number((_project$progress = project.progress) !== null && _project$progress !== void 0 ? _project$progress : 0),
								"stroke-width": 8,
								status: Number((_project$progress2 = project.progress) !== null && _project$progress2 !== void 0 ? _project$progress2 : 0) >= 100 ? "success" : ""
							}, null, 8, ["percentage", "status"])]),
							createElementVNode("div", _hoisted_11, [createVNode(_component_el_icon, null, {
								default: withCtx(() => [createVNode(_component_User)]),
								_: 1
							}), createElementVNode("span", null, toDisplayString(project.customerName || "-"), 1)]),
							createElementVNode("div", _hoisted_12, [createVNode(_component_el_icon, null, {
								default: withCtx(() => [createVNode(_component_Avatar)]),
								_: 1
							}), createElementVNode("span", null, toDisplayString(project.managerName || "-"), 1)]),
							createElementVNode("div", _hoisted_13, [
								createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(_component_Calendar)]),
									_: 1
								}),
								createElementVNode("span", { class: normalizeClass(["card-date", { "date-overdue": isOverdue(project) }]) }, toDisplayString(formatDate(project.planEndDate)), 3),
								isOverdue(project) ? (openBlock(), createBlock(_component_el_tag, {
									key: 0,
									size: "small",
									type: "danger",
									effect: "plain",
									class: "overdue-tag"
								}, {
									default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode(" 逾期 ", -1)])]),
									_: 1
								})) : createCommentVNode("", true)
							])
						], 10, _hoisted_7);
					}), 128)), getColumnCount(col.status) === 0 ? (openBlock(), createBlock(_component_el_empty, {
						key: 0,
						"image-size": 50,
						description: "暂无项目",
						class: "col-empty"
					})) : createCommentVNode("", true)])]);
				}), 64))])), [[_directive_loading, loading.value]])]),
				_: 1
			})]);
		};
	}
}), [["__scopeId", "data-v-5e424cbd"]]);
//#endregion
export { kanban_default as default };

//# sourceMappingURL=kanban-D6zJ9DHY.js.map