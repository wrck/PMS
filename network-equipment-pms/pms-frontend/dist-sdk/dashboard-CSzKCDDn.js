import { c as useUserStore, d as useRouter } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { t as init } from "./echarts-D5mnZUDD.js";
import { a as getProjectTrend, n as getDashboardStats, o as getRecentActivities, s as getTodoList, t as getAssetStats } from "./report-D_OreQK4.js";
import { Fragment, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, nextTick, normalizeClass, normalizeStyle, onBeforeUnmount, onMounted, openBlock, ref, renderList, resolveComponent, resolveDirective, resolveDynamicComponent, toDisplayString, unref, withCtx, withDirectives } from "vue";
//#region src/views/dashboard/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "dashboard" };
var _hoisted_2 = { class: "welcome-title" };
var _hoisted_3 = { class: "stat-content" };
var _hoisted_4 = { class: "stat-info" };
var _hoisted_5 = { class: "stat-value" };
var _hoisted_6 = { class: "stat-title" };
var _hoisted_7 = { class: "stat-sub" };
var _hoisted_8 = ["onClick"];
var _hoisted_9 = { class: "shortcut-text" };
var _hoisted_10 = { class: "card-header-flex" };
var _hoisted_11 = {
	key: 1,
	class: "todo-list"
};
var _hoisted_12 = ["onClick"];
var _hoisted_13 = { class: "todo-main" };
var _hoisted_14 = { class: "todo-title" };
var _hoisted_15 = { class: "todo-meta" };
var _hoisted_16 = {
	key: 0,
	class: "todo-project"
};
var _hoisted_17 = {
	key: 1,
	class: "todo-deadline"
};
var _hoisted_18 = {
	key: 1,
	class: "activity-list"
};
var _hoisted_19 = { class: "activity-body" };
var _hoisted_20 = { class: "activity-head" };
var _hoisted_21 = { class: "activity-desc" };
var _hoisted_22 = { class: "activity-foot" };
var _hoisted_23 = {
	key: 0,
	class: "activity-operator"
};
var _hoisted_24 = { class: "activity-time" };
//#endregion
//#region src/views/dashboard/index.vue
var dashboard_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "DashboardView",
	__name: "index",
	setup(__props) {
		var _userStore$userInfo, _userStore$userInfo2;
		const userStore = useUserStore();
		const router = useRouter();
		const statCards = ref([
			{
				title: "项目总数",
				value: 0,
				icon: "Folder",
				color: "#409eff",
				sub: "进行中 0"
			},
			{
				title: "在库设备",
				value: 0,
				icon: "Box",
				color: "#67c23a",
				sub: "本月新增 0"
			},
			{
				title: "待办任务",
				value: 0,
				icon: "Bell",
				color: "#e6a23c",
				sub: "告警 0"
			},
			{
				title: "本月交付",
				value: 0,
				icon: "TrendCharts",
				color: "#f56c6c",
				sub: "本月立项 0"
			}
		]);
		const dashboardStats = ref({
			projectTotal: 0,
			assetInStock: 0,
			todoCount: 0,
			monthDelivery: 0,
			projectInProgress: 0,
			monthNewProject: 0,
			monthNewAsset: 0,
			alertCount: 0
		});
		const projectTrend = ref([]);
		const todoList = ref([]);
		const recentActivities = ref([]);
		const assetStats = ref({
			byStatus: {},
			byCategory: {},
			totalValue: 0,
			total: 0,
			inStock: 0,
			allocated: 0,
			inTransfer: 0,
			scrapped: 0
		});
		const loading = ref(false);
		const welcomeName = ((_userStore$userInfo = userStore.userInfo) === null || _userStore$userInfo === void 0 ? void 0 : _userStore$userInfo.nickname) || ((_userStore$userInfo2 = userStore.userInfo) === null || _userStore$userInfo2 === void 0 ? void 0 : _userStore$userInfo2.username) || "管理员";
		const trendChartRef = ref();
		const statusPieRef = ref();
		const assetChartRef = ref();
		let chartInstances = [];
		function initChart(dom) {
			if (!dom) return null;
			const existing = chartInstances.find((c) => c.getDom() === dom);
			if (existing) return existing;
			try {
				const inst = init(dom);
				chartInstances.push(inst);
				return inst;
			} catch (e) {
				console.warn("echarts init failed", e);
				return null;
			}
		}
		const STATUS_LABELS = {
			PENDING: "待立项",
			APPROVED: "已立项",
			IN_PROGRESS: "进行中",
			INITIAL_ACCEPTANCE: "初验",
			FINAL_ACCEPTANCE: "终验",
			COMPLETED: "已完成",
			CLOSED: "已关闭",
			REJECTED: "已拒绝"
		};
		const STATUS_COLORS = {
			PENDING: "#909399",
			APPROVED: "#a0cfff",
			IN_PROGRESS: "#409eff",
			INITIAL_ACCEPTANCE: "#e6a23c",
			FINAL_ACCEPTANCE: "#f56c6c",
			COMPLETED: "#67c23a",
			CLOSED: "#b3b3b3",
			REJECTED: "#9c27b0"
		};
		function renderTrendChart() {
			const inst = initChart(trendChartRef.value);
			if (!inst) return;
			const months = [...new Set(projectTrend.value.map((t) => t.month))];
			const series = [...new Set(projectTrend.value.map((t) => t.status))].map((status) => ({
				name: STATUS_LABELS[status] || status,
				type: "bar",
				stack: "total",
				emphasis: { focus: "series" },
				data: months.map((m) => {
					var _projectTrend$value$f;
					return ((_projectTrend$value$f = projectTrend.value.find((t) => t.month === m && t.status === status)) === null || _projectTrend$value$f === void 0 ? void 0 : _projectTrend$value$f.count) || 0;
				}),
				itemStyle: { color: STATUS_COLORS[status] || "#409eff" }
			}));
			inst.setOption({
				title: {
					text: "项目趋势（最近 6 月状态分布）",
					left: "center",
					textStyle: { fontSize: 14 }
				},
				tooltip: {
					trigger: "axis",
					axisPointer: { type: "shadow" }
				},
				legend: {
					bottom: 0,
					type: "scroll"
				},
				grid: {
					left: 40,
					right: 20,
					top: 50,
					bottom: 50
				},
				xAxis: {
					type: "category",
					data: months
				},
				yAxis: {
					type: "value",
					minInterval: 1
				},
				series
			}, true);
		}
		function renderStatusPie() {
			const inst = initChart(statusPieRef.value);
			if (!inst) return;
			const statusMap = {};
			projectTrend.value.forEach((t) => {
				statusMap[t.status] = (statusMap[t.status] || 0) + t.count;
			});
			const data = Object.entries(statusMap).map(([k, v]) => ({
				name: STATUS_LABELS[k] || k,
				value: v,
				itemStyle: { color: STATUS_COLORS[k] || "#409eff" }
			}));
			inst.setOption({
				title: {
					text: "项目状态分布",
					left: "center",
					textStyle: { fontSize: 14 }
				},
				tooltip: {
					trigger: "item",
					formatter: "{b}: {c} ({d}%)"
				},
				legend: {
					bottom: 0,
					type: "scroll"
				},
				series: [{
					type: "pie",
					radius: ["40%", "70%"],
					center: ["50%", "50%"],
					avoidLabelOverlap: true,
					label: {
						show: true,
						formatter: "{b}\n{d}%"
					},
					data
				}]
			}, true);
		}
		function renderAssetChart() {
			const inst = initChart(assetChartRef.value);
			if (!inst) return;
			const a = assetStats.value;
			inst.setOption({
				title: {
					text: "设备状态分布",
					left: "center",
					textStyle: { fontSize: 14 }
				},
				tooltip: {
					trigger: "item",
					formatter: "{b}: {c} ({d}%)"
				},
				legend: {
					bottom: 0,
					type: "scroll"
				},
				series: [{
					type: "pie",
					radius: ["40%", "70%"],
					center: ["50%", "50%"],
					label: {
						show: true,
						formatter: "{b}\n{d}%"
					},
					data: [
						{
							value: a.inStock,
							name: "在库",
							itemStyle: { color: "#409EFF" }
						},
						{
							value: a.allocated,
							name: "已分配",
							itemStyle: { color: "#67C23A" }
						},
						{
							value: a.inTransfer,
							name: "调拨中",
							itemStyle: { color: "#E6A23C" }
						},
						{
							value: a.scrapped,
							name: "报废",
							itemStyle: { color: "#909399" }
						}
					]
				}]
			}, true);
		}
		function renderAllCharts() {
			nextTick(() => {
				renderTrendChart();
				renderStatusPie();
				renderAssetChart();
			});
		}
		function handleResize() {
			chartInstances.forEach((c) => c.resize());
		}
		function disposeCharts() {
			chartInstances.forEach((c) => {
				try {
					c.dispose();
				} catch (e) {
					console.warn("echarts dispose failed", e);
				}
			});
			chartInstances = [];
		}
		function applyStats(s) {
			statCards.value[0].value = s.projectTotal;
			statCards.value[0].sub = `进行中 ${s.projectInProgress}`;
			statCards.value[1].value = s.assetInStock;
			statCards.value[1].sub = `本月新增 ${s.monthNewAsset}`;
			statCards.value[2].value = s.todoCount;
			statCards.value[2].sub = `告警 ${s.alertCount}`;
			statCards.value[3].value = s.monthDelivery;
			statCards.value[3].sub = `本月立项 ${s.monthNewProject}`;
		}
		async function loadAll() {
			loading.value = true;
			try {
				const [statsRes, trendRes, todoRes, activityRes, assetRes] = await Promise.all([
					getDashboardStats().catch(() => null),
					getProjectTrend().catch(() => []),
					getTodoList(5).catch(() => []),
					getRecentActivities(10).catch(() => []),
					getAssetStats().catch(() => null)
				]);
				if (statsRes) {
					dashboardStats.value = statsRes;
					applyStats(statsRes);
				}
				projectTrend.value = trendRes || [];
				todoList.value = todoRes || [];
				recentActivities.value = activityRes || [];
				if (assetRes) assetStats.value = assetRes;
				renderAllCharts();
			} catch (e) {
				console.warn("Failed to load dashboard data", e);
			} finally {
				loading.value = false;
			}
		}
		onMounted(() => {
			nextTick(() => loadAll());
			window.addEventListener("resize", handleResize);
		});
		onBeforeUnmount(() => {
			window.removeEventListener("resize", handleResize);
			disposeCharts();
		});
		const shortcuts = [
			{
				title: "新建项目",
				icon: "FolderAdd",
				color: "#409eff",
				route: "/project/list"
			},
			{
				title: "资产入库",
				icon: "Box",
				color: "#67c23a",
				route: "/asset/list"
			},
			{
				title: "创建任务",
				icon: "Tickets",
				color: "#e6a23c",
				route: "/implementation/task"
			},
			{
				title: "待办中心",
				icon: "Bell",
				color: "#f56c6c",
				route: "/workflow/todo"
			}
		];
		function goTo(route) {
			router.push(route);
		}
		function priorityTagType(p) {
			if (p === "HIGH") return "danger";
			if (p === "NORMAL") return "warning";
			return "info";
		}
		function priorityLabel(p) {
			if (p === "HIGH") return "高";
			if (p === "NORMAL") return "中";
			return "低";
		}
		function activityTagType(t) {
			if (t === "LOGIN") return "success";
			if (t === "OPER") return "primary";
			return "info";
		}
		function activityTypeLabel(t) {
			if (t === "LOGIN") return "登录";
			if (t === "OPER") return "操作";
			if (t === "SCHEDULE") return "定时";
			if (t === "INTEGRATION") return "集成";
			return t;
		}
		function formatTime(t) {
			if (!t) return "";
			return t.replace("T", " ").slice(0, 16);
		}
		function todoLink(t) {
			return `/implementation/task`;
		}
		return (_ctx, _cache) => {
			const _component_el_card = resolveComponent("el-card");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_tag = resolveComponent("el-tag");
			const _directive_loading = resolveDirective("loading");
			return withDirectives((openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, {
					shadow: "never",
					class: "welcome-card"
				}, {
					default: withCtx(() => [createElementVNode("h2", _hoisted_2, "欢迎回来，" + toDisplayString(unref(welcomeName)) + " 👋", 1), _cache[1] || (_cache[1] = createElementVNode("p", { class: "welcome-desc" }, "这里是网络设备工程项目管理系统工作台，下面是系统概览信息。", -1))]),
					_: 1
				}),
				createVNode(_component_el_row, {
					gutter: 16,
					class: "stat-row"
				}, {
					default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(statCards.value, (item) => {
						return openBlock(), createBlock(_component_el_col, {
							key: item.title,
							xs: 24,
							sm: 12,
							md: 8,
							lg: 6
						}, {
							default: withCtx(() => [createVNode(_component_el_card, {
								shadow: "hover",
								class: "stat-card"
							}, {
								default: withCtx(() => [createElementVNode("div", _hoisted_3, [createElementVNode("div", {
									class: "stat-icon",
									style: normalizeStyle({ backgroundColor: item.color })
								}, [createVNode(_component_el_icon, {
									size: 28,
									color: "#fff"
								}, {
									default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(item.icon)))]),
									_: 2
								}, 1024)], 4), createElementVNode("div", _hoisted_4, [
									createElementVNode("div", _hoisted_5, toDisplayString(item.value), 1),
									createElementVNode("div", _hoisted_6, toDisplayString(item.title), 1),
									createElementVNode("div", _hoisted_7, toDisplayString(item.sub), 1)
								])])]),
								_: 2
							}, 1024)]),
							_: 2
						}, 1024);
					}), 128))]),
					_: 1
				}),
				createVNode(_component_el_card, {
					shadow: "never",
					class: "shortcut-card"
				}, {
					header: withCtx(() => [..._cache[2] || (_cache[2] = [createElementVNode("span", { class: "section-title" }, "快捷入口", -1)])]),
					default: withCtx(() => [createVNode(_component_el_row, { gutter: 16 }, {
						default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(shortcuts, (item) => {
							return createVNode(_component_el_col, {
								key: item.title,
								xs: 12,
								sm: 6,
								md: 6,
								lg: 6
							}, {
								default: withCtx(() => [createElementVNode("div", {
									class: "shortcut-item",
									onClick: ($event) => goTo(item.route)
								}, [createElementVNode("div", {
									class: "shortcut-icon",
									style: normalizeStyle({ backgroundColor: item.color })
								}, [createVNode(_component_el_icon, {
									size: 22,
									color: "#fff"
								}, {
									default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(item.icon)))]),
									_: 2
								}, 1024)], 4), createElementVNode("div", _hoisted_9, toDisplayString(item.title), 1)], 8, _hoisted_8)]),
								_: 2
							}, 1024);
						}), 64))]),
						_: 1
					})]),
					_: 1
				}),
				createVNode(_component_el_row, {
					gutter: 16,
					class: "chart-row"
				}, {
					default: withCtx(() => [createVNode(_component_el_col, {
						xs: 24,
						lg: 12
					}, {
						default: withCtx(() => [createVNode(_component_el_card, {
							shadow: "never",
							class: "chart-card"
						}, {
							header: withCtx(() => [..._cache[3] || (_cache[3] = [createElementVNode("span", { class: "section-title" }, "项目趋势", -1)])]),
							default: withCtx(() => [createElementVNode("div", {
								ref_key: "trendChartRef",
								ref: trendChartRef,
								class: "chart-container"
							}, null, 512)]),
							_: 1
						})]),
						_: 1
					}), createVNode(_component_el_col, {
						xs: 24,
						lg: 12
					}, {
						default: withCtx(() => [createVNode(_component_el_card, {
							shadow: "never",
							class: "chart-card"
						}, {
							header: withCtx(() => [..._cache[4] || (_cache[4] = [createElementVNode("span", { class: "section-title" }, "项目状态分布", -1)])]),
							default: withCtx(() => [createElementVNode("div", {
								ref_key: "statusPieRef",
								ref: statusPieRef,
								class: "chart-container"
							}, null, 512)]),
							_: 1
						})]),
						_: 1
					})]),
					_: 1
				}),
				createVNode(_component_el_row, {
					gutter: 16,
					class: "chart-row"
				}, {
					default: withCtx(() => [createVNode(_component_el_col, {
						xs: 24,
						lg: 12
					}, {
						default: withCtx(() => [createVNode(_component_el_card, {
							shadow: "never",
							class: "chart-card"
						}, {
							header: withCtx(() => [..._cache[5] || (_cache[5] = [createElementVNode("span", { class: "section-title" }, "设备状态分布", -1)])]),
							default: withCtx(() => [createElementVNode("div", {
								ref_key: "assetChartRef",
								ref: assetChartRef,
								class: "chart-container"
							}, null, 512)]),
							_: 1
						})]),
						_: 1
					}), createVNode(_component_el_col, {
						xs: 24,
						lg: 12
					}, {
						default: withCtx(() => [createVNode(_component_el_card, {
							shadow: "never",
							class: "todo-card"
						}, {
							header: withCtx(() => [createElementVNode("div", _hoisted_10, [_cache[7] || (_cache[7] = createElementVNode("span", { class: "section-title" }, "待办事项", -1)), createVNode(_component_el_button, {
								link: "",
								type: "primary",
								onClick: _cache[0] || (_cache[0] = ($event) => goTo("/workflow/todo"))
							}, {
								default: withCtx(() => [..._cache[6] || (_cache[6] = [createTextVNode("更多", -1)])]),
								_: 1
							})])]),
							default: withCtx(() => [todoList.value.length === 0 ? (openBlock(), createBlock(_component_el_empty, {
								key: 0,
								description: "暂无待办",
								"image-size": 80
							})) : (openBlock(), createElementBlock("ul", _hoisted_11, [(openBlock(true), createElementBlock(Fragment, null, renderList(todoList.value, (t) => {
								return openBlock(), createElementBlock("li", {
									key: t.id,
									class: "todo-item",
									onClick: ($event) => goTo(todoLink(t))
								}, [createElementVNode("div", _hoisted_13, [createVNode(_component_el_tag, {
									type: priorityTagType(t.priority),
									size: "small",
									effect: "plain",
									class: "todo-priority"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(priorityLabel(t.priority)), 1)]),
									_: 2
								}, 1032, ["type"]), createElementVNode("span", _hoisted_14, toDisplayString(t.title), 1)]), createElementVNode("div", _hoisted_15, [t.projectName ? (openBlock(), createElementBlock("span", _hoisted_16, toDisplayString(t.projectName), 1)) : createCommentVNode("", true), t.deadline ? (openBlock(), createElementBlock("span", _hoisted_17, "截止 " + toDisplayString(t.deadline), 1)) : createCommentVNode("", true)])], 8, _hoisted_12);
							}), 128))]))]),
							_: 1
						})]),
						_: 1
					})]),
					_: 1
				}),
				createVNode(_component_el_card, {
					shadow: "never",
					class: "activity-card"
				}, {
					header: withCtx(() => [..._cache[8] || (_cache[8] = [createElementVNode("span", { class: "section-title" }, "近期动态", -1)])]),
					default: withCtx(() => [recentActivities.value.length === 0 ? (openBlock(), createBlock(_component_el_empty, {
						key: 0,
						description: "暂无动态",
						"image-size": 80
					})) : (openBlock(), createElementBlock("ul", _hoisted_18, [(openBlock(true), createElementBlock(Fragment, null, renderList(recentActivities.value, (a) => {
						return openBlock(), createElementBlock("li", {
							key: `${a.type}-${a.id}`,
							class: "activity-item"
						}, [createElementVNode("div", { class: normalizeClass(["activity-dot", `dot-${a.type.toLowerCase()}`]) }, null, 2), createElementVNode("div", _hoisted_19, [createElementVNode("div", _hoisted_20, [createVNode(_component_el_tag, {
							type: activityTagType(a.type),
							size: "small",
							effect: "plain"
						}, {
							default: withCtx(() => [createTextVNode(toDisplayString(activityTypeLabel(a.type)), 1)]),
							_: 2
						}, 1032, ["type"]), createElementVNode("span", _hoisted_21, toDisplayString(a.description), 1)]), createElementVNode("div", _hoisted_22, [a.operatorName ? (openBlock(), createElementBlock("span", _hoisted_23, toDisplayString(a.operatorName), 1)) : createCommentVNode("", true), createElementVNode("span", _hoisted_24, toDisplayString(formatTime(a.createdAt)), 1)])])]);
					}), 128))]))]),
					_: 1
				})
			])), [[_directive_loading, loading.value]]);
		};
	}
}), [["__scopeId", "data-v-1e9a0bfe"]]);
//#endregion
export { dashboard_default as default };

//# sourceMappingURL=dashboard-CSzKCDDn.js.map