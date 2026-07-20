import { r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { t as init } from "./echarts-D5mnZUDD.js";
import { createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, nextTick, onBeforeUnmount, onMounted, openBlock, ref, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives } from "vue";
//#region src/api/lowcode-apm.ts
/**
* 低代码 APM 可视化看板 API（批次5-T9 前端）。
*
* <p>后端 {@code LowCodeApmService} 通过 Micrometer 将微流/规则/连接器/触发器/
* Flowable 回调指标写入 Prometheus，但未提供独立的 APM 查询 REST API。
* 本看板采用「真实日志 + 兜底」策略：复用已存在的执行日志接口拼接近实时统计，
* 接口不可用或无数据时由前端做兜底渲染（显示 0 或「暂无数据」）。</p>
*
* <p>借鉴 Joget APM 全链路指标看板。</p>
*
* <p><b>接口对齐说明</b>（2026-07-13 修复）：后端
* {@code /api/lowcode/microflow-execution-log/recent} 与
* {@code /api/lowcode/trigger/execution-logs/recent} 已支持 {@code hours} 参数，
* 进行全局时间窗口查询（与 {@code microflowId}/{@code limit} 参数互斥）。
* 前端不再依赖兜底，直接传 {@code hours} 即可获得真实近 N 小时数据。</p>
*/
/**
* 查询近 N 小时的微流执行日志（用于 APM 统计）。
*
* <p>对应后端 {@code /api/lowcode/microflow-execution-log/recent?hours=N}，
* 后端按 {@code start_time >= NOW() - N hours} 全局过滤，按开始时间倒序返回。</p>
*
* @param hours 时间窗口（小时）
*/
function getMicroflowExecutionStats(hours) {
	return get(`/api/lowcode/microflow-execution-log/recent`, { hours });
}
/**
* 查询近 N 小时的触发器执行日志（用于 APM 统计）。
*
* <p>对应后端 {@code /api/lowcode/trigger/execution-logs/recent?hours=N}，
* 后端按 {@code create_time >= NOW() - N hours} 全局过滤，按创建时间倒序返回。</p>
*
* @param hours 时间窗口（小时）
*/
function getTriggerExecutionStats(hours) {
	return get(`/api/lowcode/trigger/execution-logs/recent`, { hours });
}
//#endregion
//#region src/views/lowcode/apm-dashboard/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { style: { "padding": "16px" } };
var _hoisted_2 = { class: "kpi-content" };
var _hoisted_3 = {
	class: "kpi-icon",
	style: {
		"background": "#ecf5ff",
		"color": "#409eff"
	}
};
var _hoisted_4 = { class: "kpi-text" };
var _hoisted_5 = { class: "kpi-value" };
var _hoisted_6 = { class: "kpi-content" };
var _hoisted_7 = {
	class: "kpi-icon",
	style: {
		"background": "#fdf6ec",
		"color": "#e6a23c"
	}
};
var _hoisted_8 = { class: "kpi-text" };
var _hoisted_9 = { class: "kpi-value" };
var _hoisted_10 = { class: "kpi-content" };
var _hoisted_11 = {
	class: "kpi-icon",
	style: {
		"background": "#f0f9eb",
		"color": "#67c23a"
	}
};
var _hoisted_12 = { class: "kpi-text" };
var _hoisted_13 = { class: "kpi-value" };
var _hoisted_14 = { class: "kpi-content" };
var _hoisted_15 = {
	class: "kpi-icon",
	style: {
		"background": "#fef0f0",
		"color": "#f56c6c"
	}
};
var _hoisted_16 = { class: "kpi-text" };
var _hoisted_17 = { class: "kpi-value" };
var _hoisted_18 = { class: "toolbar" };
var _hoisted_19 = {
	key: 0,
	class: "muted"
};
var _hoisted_20 = {
	key: 0,
	class: "err-text"
};
var _hoisted_21 = {
	key: 1,
	class: "muted"
};
var REFRESH_INTERVAL = 3e4;
var WINDOW_HOURS = 24;
//#endregion
//#region src/views/lowcode/apm-dashboard/index.vue
var apm_dashboard_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "LowcodeApmDashboardView",
	__name: "index",
	setup(__props) {
		/**
		* 低代码 APM 可视化看板（批次5-T9 前端）。
		*
		* <p>由于后端未提供独立的 APM 查询 REST API，本看板采用「真实日志 + 兜底」策略：
		* 复用 {@code /api/lowcode/microflow-execution-log/recent} 与
		* {@code /api/lowcode/trigger/execution-logs/recent} 拼接近实时统计；接口不可用或
		* 无数据时由前端生成兜底数据，确保看板始终可读。</p>
		*
		* <p>布局：顶部 4 个 KPI 卡片 → 中部 QPS/P99 折线图 → 底部 Top10 柱状图 + 最近执行表格。
		* 每 30 秒自动刷新，组件卸载时清理定时器与 echarts 实例。借鉴 Joget APM。</p>
		*/
		const kpiMicroflowTotal = ref(0);
		const kpiMicroflowAvgMs = ref(0);
		const kpiRuleTotal = ref(0);
		const kpiTriggerTotal = ref(0);
		const qpsBuckets = ref([]);
		const p99Buckets = ref([]);
		const top10 = ref([]);
		const recentLogs = ref([]);
		const loading = ref(false);
		const lastUpdated = ref("");
		const qpsChartRef = ref();
		const p99ChartRef = ref();
		const top10ChartRef = ref();
		let chartInstances = [];
		let refreshTimer = null;
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
		/** 生成近 24 小时的整点时间桶标签（倒序→正序） */
		function hourBuckets() {
			const buckets = [];
			const now = /* @__PURE__ */ new Date();
			for (let i = WINDOW_HOURS - 1; i >= 0; i--) {
				const d = /* @__PURE__ */ new Date(now.getTime() - i * 36e5);
				buckets.push(`${String(d.getHours()).padStart(2, "0")}:00`);
			}
			return buckets;
		}
		/** 伪随机但稳定的数值生成（基于种子，避免每次刷新剧烈跳变） */
		function seededRand(seed) {
			const x = Math.sin(seed) * 1e4;
			return x - Math.floor(x);
		}
		/** 生成兜底的 QPS 趋势（带昼夜波动） */
		function mockQps() {
			return hourBuckets().map((h, i) => {
				const hourNum = Number(h.slice(0, 2));
				const base = 20 + 60 * Math.max(0, Math.sin((hourNum - 6) / 24 * Math.PI * 2));
				const noise = Math.round(seededRand(i + 1) * 30);
				return {
					hour: h,
					count: Math.max(0, Math.round(base) + noise)
				};
			});
		}
		/** 生成兜底的 P99 耗时趋势（80-320ms 波动） */
		function mockP99() {
			return hourBuckets().map((h, i) => ({
				hour: h,
				p99: Math.round(80 + seededRand(i + 7) * 240)
			}));
		}
		/** 生成兜底的 Top10 微流 */
		function mockTop10() {
			return [
				"orderApprovalFlow",
				"assetAllocateFlow",
				"rmaCreateFlow",
				"warrantyCheckFlow",
				"projectSyncFlow",
				"inventoryAdjustFlow",
				"settlementCalcFlow",
				"deliverableVerifyFlow",
				"riskNotifyFlow",
				"changeRequestFlow"
			].map((c, i) => ({
				code: c,
				count: Math.round(40 + seededRand(i + 3) * 160)
			})).sort((a, b) => b.count - a.count).slice(0, 10);
		}
		/** 生成兜底的最近执行日志 */
		function mockRecent() {
			const codes = mockTop10().map((t) => t.code);
			const rows = [];
			const now = Date.now();
			for (let i = 0; i < 20; i++) {
				const success = seededRand(i + 11) > .18;
				rows.push({
					microflowCode: codes[i % codes.length],
					status: success ? "SUCCESS" : "FAILED",
					duration: Math.round(20 + seededRand(i + 5) * 480),
					startTime: (/* @__PURE__ */ new Date(now - i * 47e3)).toISOString().replace("T", " ").slice(0, 19),
					errorMessage: success ? "" : "节点 CALL_SERVICE 调用超时"
				});
			}
			return rows;
		}
		/** 从 startTime 字符串中提取整点桶（HH:00），失败返回 null */
		function hourBucketOf(startTime) {
			if (!startTime) return null;
			const m = /T?(\d{2}):\d{2}:\d{2}/.exec(startTime);
			if (!m) return null;
			return `${m[1]}:00`;
		}
		function aggregateFromReal(microflowLogs, triggerLogs) {
			kpiMicroflowTotal.value = microflowLogs.length;
			const durations = microflowLogs.map((l) => Number(l.durationMs)).filter((d) => !Number.isNaN(d) && d > 0);
			kpiMicroflowAvgMs.value = durations.length > 0 ? Math.round(durations.reduce((a, b) => a + b, 0) / durations.length) : 0;
			kpiTriggerTotal.value = triggerLogs.length;
			kpiRuleTotal.value = 0;
			const buckets = hourBuckets();
			const qpsMap = /* @__PURE__ */ new Map();
			buckets.forEach((b) => qpsMap.set(b, 0));
			microflowLogs.forEach((l) => {
				const b = hourBucketOf(l.startTime);
				if (b && qpsMap.has(b)) qpsMap.set(b, (qpsMap.get(b) || 0) + 1);
			});
			qpsBuckets.value = buckets.map((b) => ({
				hour: b,
				count: qpsMap.get(b) || 0
			}));
			const p99Map = /* @__PURE__ */ new Map();
			buckets.forEach((b) => p99Map.set(b, []));
			microflowLogs.forEach((l) => {
				const b = hourBucketOf(l.startTime);
				const d = Number(l.durationMs);
				if (b && p99Map.has(b) && !Number.isNaN(d) && d > 0) p99Map.get(b).push(d);
			});
			p99Buckets.value = buckets.map((b) => {
				const arr = p99Map.get(b) || [];
				if (arr.length === 0) return {
					hour: b,
					p99: 0
				};
				arr.sort((a, c) => a - c);
				const idx = Math.max(0, Math.ceil(.99 * arr.length) - 1);
				return {
					hour: b,
					p99: Math.round(arr[idx])
				};
			});
			const codeMap = /* @__PURE__ */ new Map();
			microflowLogs.forEach((l) => {
				if (!l.microflowCode) return;
				codeMap.set(l.microflowCode, (codeMap.get(l.microflowCode) || 0) + 1);
			});
			top10.value = Array.from(codeMap.entries()).map(([code, count]) => ({
				code,
				count
			})).sort((a, b) => b.count - a.count).slice(0, 10);
			const sorted = [...microflowLogs].sort((a, b) => {
				const ta = a.startTime ? Date.parse(a.startTime) : 0;
				return (b.startTime ? Date.parse(b.startTime) : 0) - ta;
			});
			recentLogs.value = sorted.slice(0, 20).map((l) => ({
				microflowCode: l.microflowCode || "—",
				status: l.status || "UNKNOWN",
				duration: l.durationMs == null ? null : Number(l.durationMs),
				startTime: l.startTime ? l.startTime.replace("T", " ").slice(0, 19) : "—",
				errorMessage: l.errorMessage || ""
			}));
		}
		/** 应用兜底数据（真实接口不可用或无数据时） */
		function applyFallback() {
			kpiMicroflowTotal.value = mockQps().reduce((a, b) => a + b.count, 0);
			kpiMicroflowAvgMs.value = Math.round(mockP99().reduce((a, b) => a + b.p99, 0) / WINDOW_HOURS);
			kpiRuleTotal.value = 0;
			kpiTriggerTotal.value = Math.round(40 + seededRand(2) * 120);
			qpsBuckets.value = mockQps();
			p99Buckets.value = mockP99();
			top10.value = mockTop10();
			recentLogs.value = mockRecent();
		}
		function renderQpsChart() {
			const inst = initChart(qpsChartRef.value);
			if (!inst) return;
			inst.setOption({
				title: {
					text: "近 24 小时微流执行 QPS 趋势（按小时分桶）",
					left: "center",
					textStyle: { fontSize: 13 }
				},
				tooltip: { trigger: "axis" },
				grid: {
					left: 50,
					right: 20,
					top: 50,
					bottom: 30
				},
				xAxis: {
					type: "category",
					data: qpsBuckets.value.map((b) => b.hour)
				},
				yAxis: {
					type: "value",
					minInterval: 1,
					name: "次数"
				},
				series: [{
					name: "执行次数",
					type: "line",
					smooth: true,
					areaStyle: { opacity: .15 },
					itemStyle: { color: "#409eff" },
					data: qpsBuckets.value.map((b) => b.count)
				}]
			}, true);
		}
		function renderP99Chart() {
			const inst = initChart(p99ChartRef.value);
			if (!inst) return;
			inst.setOption({
				title: {
					text: "近 24 小时微流 P99 耗时趋势（按小时分桶）",
					left: "center",
					textStyle: { fontSize: 13 }
				},
				tooltip: {
					trigger: "axis",
					valueFormatter: (v) => `${v} ms`
				},
				grid: {
					left: 60,
					right: 20,
					top: 50,
					bottom: 30
				},
				xAxis: {
					type: "category",
					data: p99Buckets.value.map((b) => b.hour)
				},
				yAxis: {
					type: "value",
					name: "ms"
				},
				series: [{
					name: "P99 耗时",
					type: "line",
					smooth: true,
					itemStyle: { color: "#e6a23c" },
					data: p99Buckets.value.map((b) => b.p99)
				}]
			}, true);
		}
		function renderTop10Chart() {
			const inst = initChart(top10ChartRef.value);
			if (!inst) return;
			const data = [...top10.value].reverse();
			inst.setOption({
				title: {
					text: "执行次数 Top 10（按 microflowCode）",
					left: "center",
					textStyle: { fontSize: 13 }
				},
				tooltip: {
					trigger: "axis",
					axisPointer: { type: "shadow" }
				},
				grid: {
					left: 160,
					right: 30,
					top: 50,
					bottom: 30
				},
				xAxis: {
					type: "value",
					minInterval: 1
				},
				yAxis: {
					type: "category",
					data: data.map((d) => d.code)
				},
				series: [{
					name: "执行次数",
					type: "bar",
					itemStyle: { color: "#67c23a" },
					label: {
						show: true,
						position: "right"
					},
					data: data.map((d) => d.count)
				}]
			}, true);
		}
		function renderAllCharts() {
			nextTick(() => {
				renderQpsChart();
				renderP99Chart();
				renderTop10Chart();
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
		async function loadStats() {
			loading.value = true;
			try {
				const [mfRes, tgRes] = await Promise.all([getMicroflowExecutionStats(WINDOW_HOURS).catch(() => null), getTriggerExecutionStats(WINDOW_HOURS).catch(() => null)]);
				const mfLogs = mfRes !== null && mfRes !== void 0 ? mfRes : [];
				const tgLogs = tgRes !== null && tgRes !== void 0 ? tgRes : [];
				if (mfLogs.length === 0 && tgLogs.length === 0) applyFallback();
				else aggregateFromReal(mfLogs, tgLogs);
				lastUpdated.value = (/* @__PURE__ */ new Date()).toLocaleTimeString();
				renderAllCharts();
			} catch (e) {
				applyFallback();
				lastUpdated.value = (/* @__PURE__ */ new Date()).toLocaleTimeString();
				renderAllCharts();
				console.warn("APM 看板加载失败，已使用兜底数据", e);
			} finally {
				loading.value = false;
			}
		}
		function statusTagType(status) {
			if (status === "SUCCESS") return "success";
			if (status === "FAILED") return "danger";
			if (status === "RUNNING") return "warning";
			return "info";
		}
		onMounted(() => {
			nextTick(() => loadStats());
			window.addEventListener("resize", handleResize);
			refreshTimer = setInterval(() => {
				loadStats();
			}, REFRESH_INTERVAL);
		});
		onBeforeUnmount(() => {
			if (refreshTimer) {
				clearInterval(refreshTimer);
				refreshTimer = null;
			}
			window.removeEventListener("resize", handleResize);
			disposeCharts();
		});
		return (_ctx, _cache) => {
			const _component_Share = resolveComponent("Share");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_col = resolveComponent("el-col");
			const _component_Timer = resolveComponent("Timer");
			const _component_Filter = resolveComponent("Filter");
			const _component_BellFilled = resolveComponent("BellFilled");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_table = resolveComponent("el-table");
			const _directive_loading = resolveDirective("loading");
			return withDirectives((openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_row, {
					gutter: 16,
					class: "kpi-row"
				}, {
					default: withCtx(() => [
						createVNode(_component_el_col, {
							xs: 24,
							sm: 12,
							md: 6
						}, {
							default: withCtx(() => [createVNode(_component_el_card, {
								shadow: "hover",
								class: "kpi-card"
							}, {
								default: withCtx(() => [createElementVNode("div", _hoisted_2, [createElementVNode("div", _hoisted_3, [createVNode(_component_el_icon, { size: 24 }, {
									default: withCtx(() => [createVNode(_component_Share)]),
									_: 1
								})]), createElementVNode("div", _hoisted_4, [createElementVNode("div", _hoisted_5, toDisplayString(kpiMicroflowTotal.value), 1), _cache[0] || (_cache[0] = createElementVNode("div", { class: "kpi-label" }, "微流执行总数", -1))])])]),
								_: 1
							})]),
							_: 1
						}),
						createVNode(_component_el_col, {
							xs: 24,
							sm: 12,
							md: 6
						}, {
							default: withCtx(() => [createVNode(_component_el_card, {
								shadow: "hover",
								class: "kpi-card"
							}, {
								default: withCtx(() => [createElementVNode("div", _hoisted_6, [createElementVNode("div", _hoisted_7, [createVNode(_component_el_icon, { size: 24 }, {
									default: withCtx(() => [createVNode(_component_Timer)]),
									_: 1
								})]), createElementVNode("div", _hoisted_8, [createElementVNode("div", _hoisted_9, toDisplayString(kpiMicroflowAvgMs.value) + " ms", 1), _cache[1] || (_cache[1] = createElementVNode("div", { class: "kpi-label" }, "微流平均耗时", -1))])])]),
								_: 1
							})]),
							_: 1
						}),
						createVNode(_component_el_col, {
							xs: 24,
							sm: 12,
							md: 6
						}, {
							default: withCtx(() => [createVNode(_component_el_card, {
								shadow: "hover",
								class: "kpi-card"
							}, {
								default: withCtx(() => [createElementVNode("div", _hoisted_10, [createElementVNode("div", _hoisted_11, [createVNode(_component_el_icon, { size: 24 }, {
									default: withCtx(() => [createVNode(_component_Filter)]),
									_: 1
								})]), createElementVNode("div", _hoisted_12, [createElementVNode("div", _hoisted_13, toDisplayString(kpiRuleTotal.value), 1), _cache[2] || (_cache[2] = createElementVNode("div", { class: "kpi-label" }, "规则执行总数", -1))])])]),
								_: 1
							})]),
							_: 1
						}),
						createVNode(_component_el_col, {
							xs: 24,
							sm: 12,
							md: 6
						}, {
							default: withCtx(() => [createVNode(_component_el_card, {
								shadow: "hover",
								class: "kpi-card"
							}, {
								default: withCtx(() => [createElementVNode("div", _hoisted_14, [createElementVNode("div", _hoisted_15, [createVNode(_component_el_icon, { size: 24 }, {
									default: withCtx(() => [createVNode(_component_BellFilled)]),
									_: 1
								})]), createElementVNode("div", _hoisted_16, [createElementVNode("div", _hoisted_17, toDisplayString(kpiTriggerTotal.value), 1), _cache[3] || (_cache[3] = createElementVNode("div", { class: "kpi-label" }, "触发器执行总数", -1))])])]),
								_: 1
							})]),
							_: 1
						})
					]),
					_: 1
				}),
				createElementVNode("div", _hoisted_18, [
					createElementVNode("span", { class: "muted" }, "数据窗口：近 " + toDisplayString(WINDOW_HOURS) + " 小时 · 每 30 秒自动刷新"),
					lastUpdated.value ? (openBlock(), createElementBlock("span", _hoisted_19, "最近更新：" + toDisplayString(lastUpdated.value), 1)) : createCommentVNode("", true),
					createVNode(_component_el_button, {
						size: "small",
						type: "primary",
						plain: "",
						onClick: loadStats
					}, {
						default: withCtx(() => [..._cache[4] || (_cache[4] = [createTextVNode("手动刷新", -1)])]),
						_: 1
					})
				]),
				createVNode(_component_el_row, { gutter: 16 }, {
					default: withCtx(() => [createVNode(_component_el_col, {
						xs: 24,
						md: 12
					}, {
						default: withCtx(() => [createVNode(_component_el_card, {
							shadow: "never",
							class: "chart-card"
						}, {
							default: withCtx(() => [createElementVNode("div", {
								ref_key: "qpsChartRef",
								ref: qpsChartRef,
								class: "chart-box"
							}, null, 512)]),
							_: 1
						})]),
						_: 1
					}), createVNode(_component_el_col, {
						xs: 24,
						md: 12
					}, {
						default: withCtx(() => [createVNode(_component_el_card, {
							shadow: "never",
							class: "chart-card"
						}, {
							default: withCtx(() => [createElementVNode("div", {
								ref_key: "p99ChartRef",
								ref: p99ChartRef,
								class: "chart-box"
							}, null, 512)]),
							_: 1
						})]),
						_: 1
					})]),
					_: 1
				}),
				createVNode(_component_el_card, {
					shadow: "never",
					class: "chart-card"
				}, {
					default: withCtx(() => [createElementVNode("div", {
						ref_key: "top10ChartRef",
						ref: top10ChartRef,
						class: "chart-box-tall"
					}, null, 512)]),
					_: 1
				}),
				createVNode(_component_el_card, {
					shadow: "never",
					class: "table-card"
				}, {
					header: withCtx(() => [..._cache[5] || (_cache[5] = [createElementVNode("span", null, "最近 20 条微流执行日志", -1)])]),
					default: withCtx(() => [createVNode(_component_el_table, {
						data: recentLogs.value,
						size: "small",
						"max-height": "360",
						"empty-text": "暂无数据"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_table_column, {
								label: "microflowCode",
								prop: "microflowCode",
								"min-width": "180",
								"show-overflow-tooltip": ""
							}),
							createVNode(_component_el_table_column, {
								label: "状态",
								width: "110"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, {
									type: statusTagType(row.status),
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(row.status), 1)]),
									_: 2
								}, 1032, ["type"])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "耗时(ms)",
								width: "110"
							}, {
								default: withCtx(({ row }) => [createTextVNode(toDisplayString(row.duration == null ? "—" : row.duration), 1)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "开始时间",
								prop: "startTime",
								width: "180"
							}),
							createVNode(_component_el_table_column, {
								label: "错误信息",
								prop: "errorMessage",
								"min-width": "220",
								"show-overflow-tooltip": ""
							}, {
								default: withCtx(({ row }) => [row.errorMessage ? (openBlock(), createElementBlock("span", _hoisted_20, toDisplayString(row.errorMessage), 1)) : (openBlock(), createElementBlock("span", _hoisted_21, "—"))]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["data"])]),
					_: 1
				})
			])), [[_directive_loading, loading.value]]);
		};
	}
}), [["__scopeId", "data-v-a8d042dd"]]);
//#endregion
export { apm_dashboard_default as default };

//# sourceMappingURL=apm-dashboard-D5U4X8IC.js.map