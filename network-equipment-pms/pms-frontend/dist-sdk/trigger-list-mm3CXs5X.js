import { i as post, n as del, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { a as getEntityList } from "./lowcode-entity-CBgvn79e.js";
import { t as ExpressionEditor_default } from "./ExpressionEditor-CFxBT6yN.js";
import { i as getMicroflowList } from "./lowcode-microflow-CXsjmWyP.js";
import { n as getProcessBindings } from "./lowcode-process-Cl-hxrRH.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, vShow, watch, withCtx, withDirectives, withModifiers } from "vue";
//#region src/api/lowcode-trigger.ts
/** 安全解析 JSON 字符串 */
function safeParse(json) {
	if (!json) return null;
	try {
		return JSON.parse(json);
	} catch (_unused) {
		return null;
	}
}
/** 深拷贝纯数据对象（配置均为可 JSON 序列化的数据） */
function deepClone(v) {
	return JSON.parse(JSON.stringify(v));
}
/** 创建默认配置（按触发器类型） */
function createDefaultTriggerConfig(type) {
	if (type === "QUARTZ") return { cronExpression: "0 0 * * * ?" };
	if (type === "EVENT") return {
		eventName: "",
		payloadSchema: []
	};
	return {
		entityCode: "",
		operations: [],
		timing: [],
		condition: ""
	};
}
/**
* 将 config JSON 字符串解析为结构化配置对象，并兼容历史写法。
*
* <p>兼容场景：
* <ul>
*   <li>QUARTZ：{@code {cronExpression:"..."}} 或 {@code {cron:"..."}}</li>
*   <li>CRUD：旧版单数 operation/timing 字段自动转为数组</li>
*   <li>解析失败或为空时返回该类型的默认配置</li>
* </ul></p>
*/
function parseTriggerConfig(configStr, type) {
	const parsed = safeParse(configStr || "");
	if (!parsed || typeof parsed !== "object") return createDefaultTriggerConfig(type);
	const obj = parsed;
	if (type === "QUARTZ") return { cronExpression: typeof obj.cronExpression === "string" && obj.cronExpression || typeof obj.cron === "string" && obj.cron || "0 0 * * * ?" };
	if (type === "EVENT") return {
		eventName: typeof obj.eventName === "string" ? obj.eventName : "",
		payloadSchema: Array.isArray(obj.payloadSchema) ? obj.payloadSchema.map((f) => ({
			name: typeof (f === null || f === void 0 ? void 0 : f.name) === "string" ? f.name : "",
			type: typeof (f === null || f === void 0 ? void 0 : f.type) === "string" ? f.type : "STRING"
		})) : []
	};
	return {
		entityCode: typeof obj.entityCode === "string" ? obj.entityCode : "",
		operations: Array.isArray(obj.operations) ? obj.operations.filter((o) => [
			"CREATE",
			"UPDATE",
			"DELETE"
		].includes(o)) : typeof obj.operation === "string" ? [obj.operation] : [],
		timing: Array.isArray(obj.timing) ? obj.timing.filter((t) => ["BEFORE", "AFTER"].includes(t)) : typeof obj.timing === "string" ? [obj.timing] : [],
		condition: typeof obj.condition === "string" ? obj.condition : ""
	};
}
/** 序列化结构化配置为 JSON 字符串（存入 LowCodeTrigger.config） */
function serializeTriggerConfig(cfg) {
	return JSON.stringify(deepClone(cfg), null, 2);
}
function getTriggerList() {
	return get("/api/lowcode/trigger");
}
function saveTrigger(data) {
	return post("/api/lowcode/trigger", data);
}
function deleteTrigger(id) {
	return del(`/api/lowcode/trigger/${id}`);
}
function executeTrigger(code, data) {
	return post(`/api/lowcode/trigger/${code}/execute`, data);
}
/** 查询指定触发器的执行历史（按时间倒序） */
function getTriggerExecutionLogs(id, limit = 50) {
	return get(`/api/lowcode/trigger/${id}/execution-logs`, { limit });
}
//#endregion
//#region src/components/TriggerDesigner/CrudTriggerConfig.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$4 = { class: "condition-wrap" };
//#endregion
//#region src/components/TriggerDesigner/CrudTriggerConfig.vue
var CrudTriggerConfig_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "CrudTriggerConfigView",
	__name: "CrudTriggerConfig",
	props: { modelValue: {} },
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		var _props$modelValue$con;
		/**
		* CRUD 触发配置（借鉴 ServiceNow Flow Designer 的 Record Trigger）。
		*
		* <p>配置项：</p>
		* <ul>
		*   <li>实体选择：从实体列表加载（getEntityList API）</li>
		*   <li>操作多选：CREATE / UPDATE / DELETE</li>
		*   <li>时机多选：BEFORE / AFTER</li>
		*   <li>条件表达式（可选）：Groovy 表达式，作为触发前置过滤条件</li>
		* </ul>
		*
		* <p>注：后端 CrudTriggerExecutor.matches 目前不支持 condition，本轮前端先存入
		* config.condition 字段，后端可选实现。</p>
		*/
		const props = __props;
		const emit = __emit;
		const operationOptions = [
			{
				label: "新增 (CREATE)",
				value: "CREATE"
			},
			{
				label: "更新 (UPDATE)",
				value: "UPDATE"
			},
			{
				label: "删除 (DELETE)",
				value: "DELETE"
			}
		];
		const timingOptions = [{
			label: "执行前 (BEFORE)",
			value: "BEFORE"
		}, {
			label: "执行后 (AFTER)",
			value: "AFTER"
		}];
		/** 条件表达式可用变量提示（Groovy 语言，裸名引用） */
		const conditionVariables = [
			"entity",
			"operation",
			"record",
			"oldRecord",
			"user"
		];
		const entities = ref([]);
		const loadingEntities = ref(false);
		const form = reactive({
			entityCode: props.modelValue.entityCode || "",
			operations: [...props.modelValue.operations || []],
			timing: [...props.modelValue.timing || []],
			condition: (_props$modelValue$con = props.modelValue.condition) !== null && _props$modelValue$con !== void 0 ? _props$modelValue$con : ""
		});
		watch(() => props.modelValue, (v) => {
			var _v$condition;
			form.entityCode = v.entityCode || "";
			form.operations = [...v.operations || []];
			form.timing = [...v.timing || []];
			form.condition = (_v$condition = v.condition) !== null && _v$condition !== void 0 ? _v$condition : "";
		});
		watch(form, () => {
			emit("update:modelValue", {
				entityCode: form.entityCode,
				operations: [...form.operations],
				timing: [...form.timing],
				condition: form.condition
			});
		}, { deep: true });
		async function loadEntities() {
			loadingEntities.value = true;
			try {
				entities.value = await getEntityList();
			} catch (e) {
				entities.value = [];
				const msg = e instanceof Error ? e.message : String(e);
				ElMessage.warning("实体列表加载失败：" + msg);
			} finally {
				loadingEntities.value = false;
			}
		}
		onMounted(loadEntities);
		return (_ctx, _cache) => {
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_checkbox = resolveComponent("el-checkbox");
			const _component_el_checkbox_group = resolveComponent("el-checkbox-group");
			const _component_el_form = resolveComponent("el-form");
			return openBlock(), createBlock(_component_el_form, {
				model: form,
				"label-width": "100px",
				class: "crud-trigger-config"
			}, {
				default: withCtx(() => [
					createVNode(_component_el_form_item, {
						label: "触发实体",
						required: ""
					}, {
						default: withCtx(() => [createVNode(_component_el_select, {
							modelValue: form.entityCode,
							"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => form.entityCode = $event),
							filterable: "",
							"allow-create": "",
							"default-first-option": "",
							loading: loadingEntities.value,
							placeholder: "选择或输入实体编码",
							style: { "width": "100%" }
						}, {
							default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(entities.value, (e) => {
								return openBlock(), createBlock(_component_el_option, {
									key: e.code,
									label: `${e.name} (${e.code})`,
									value: e.code
								}, null, 8, ["label", "value"]);
							}), 128))]),
							_: 1
						}, 8, ["modelValue", "loading"]), _cache[4] || (_cache[4] = createElementVNode("div", { class: "field-hint" }, "从实体列表加载，如列表为空可手动输入实体编码", -1))]),
						_: 1
					}),
					createVNode(_component_el_form_item, {
						label: "触发操作",
						required: ""
					}, {
						default: withCtx(() => [createVNode(_component_el_checkbox_group, {
							modelValue: form.operations,
							"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => form.operations = $event)
						}, {
							default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(operationOptions, (o) => {
								return createVNode(_component_el_checkbox, {
									key: o.value,
									value: o.value,
									label: o.label
								}, null, 8, ["value", "label"]);
							}), 64))]),
							_: 1
						}, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, {
						label: "触发时机",
						required: ""
					}, {
						default: withCtx(() => [createVNode(_component_el_checkbox_group, {
							modelValue: form.timing,
							"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => form.timing = $event)
						}, {
							default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(timingOptions, (t) => {
								return createVNode(_component_el_checkbox, {
									key: t.value,
									value: t.value,
									label: t.label
								}, null, 8, ["value", "label"]);
							}), 64))]),
							_: 1
						}, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "条件表达式" }, {
						default: withCtx(() => [createElementVNode("div", _hoisted_1$4, [createVNode(ExpressionEditor_default, {
							modelValue: form.condition,
							"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => form.condition = $event),
							language: "groovy",
							variables: conditionVariables,
							height: 160
						}, null, 8, ["modelValue"]), _cache[5] || (_cache[5] = createElementVNode("div", { class: "field-hint" }, " 可选。Groovy 表达式，返回 true 才触发。变量：entity / operation / record / oldRecord / user （后端 CrudTriggerExecutor.matches 当前未支持 condition，前端先存入，后端可选实现） ", -1))])]),
						_: 1
					})
				]),
				_: 1
			}, 8, ["model"]);
		};
	}
}), [["__scopeId", "data-v-d76403a6"]]);
//#endregion
//#region src/components/CronEditor/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$3 = { class: "cron-editor" };
var _hoisted_2$1 = { class: "ce-fields" };
var _hoisted_3$1 = { class: "ce-field-label" };
var _hoisted_4$1 = { class: "ce-field-control" };
var _hoisted_5$1 = {
	key: 0,
	class: "ce-hint"
};
var _hoisted_6$1 = { class: "ce-inline-text" };
var _hoisted_7$1 = { class: "ce-inline-text" };
var _hoisted_8$1 = { class: "ce-preview" };
var _hoisted_9$1 = { class: "ce-desc" };
var _hoisted_10$1 = {
	key: 0,
	class: "ce-next-list"
};
var _hoisted_11$1 = {
	key: 1,
	class: "ce-empty"
};
//#endregion
//#region src/components/CronEditor/index.vue
var CronEditor_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "CronEditor",
	__name: "index",
	props: { modelValue: {} },
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		/**
		* Cron 可视化编辑器（借鉴 Budibase Automation 的定时触发器配置）。
		*
		* <p>编辑 Quartz 6 字段 cron 表达式中的 5 个可配字段（分 / 时 / 日 / 月 / 周），
		* 秒字段固定为 0。每个字段支持 4 种模式：</p>
		* <ul>
		*   <li>EVERY 每（*）</li>
		*   <li>SPECIFIC 指定值（多选，逗号分隔）</li>
		*   <li>INTERVAL 间隔（起始值 + 间隔，如 0/15）</li>
		*   <li>RANGE 范围（起始值 + 结束值，如 9-17）</li>
		* </ul>
		*
		* <p>实时预览生成的 cron 表达式、人类可读描述，以及最近 5 次执行时间
		*（前端自行实现简单解析，不引入 cron-parser 等外部库）。
		* 通过 v-model 与 cron 字符串双向绑定。</p>
		*/
		const props = __props;
		const emit = __emit;
		const FIELDS = [
			{
				key: "minute",
				label: "分",
				min: 0,
				max: 59
			},
			{
				key: "hour",
				label: "时",
				min: 0,
				max: 23
			},
			{
				key: "day",
				label: "日",
				min: 1,
				max: 31
			},
			{
				key: "month",
				label: "月",
				min: 1,
				max: 12
			},
			{
				key: "week",
				label: "周",
				min: 0,
				max: 6
			}
		];
		const WEEK_LABELS = [
			"周日",
			"周一",
			"周二",
			"周三",
			"周四",
			"周五",
			"周六"
		];
		/** 月份中文标签 */
		const MONTH_LABELS = [
			"1 月",
			"2 月",
			"3 月",
			"4 月",
			"5 月",
			"6 月",
			"7 月",
			"8 月",
			"9 月",
			"10 月",
			"11 月",
			"12 月"
		];
		function createEvery(min, max) {
			return {
				mode: "EVERY",
				values: [],
				start: min,
				interval: 1,
				end: max
			};
		}
		/** 解析单个数字，失败返回 null */
		function parseNum(s, fallback) {
			const n = Number.parseInt(s, 10);
			if (Number.isNaN(n)) return fallback != null ? fallback : null;
			return n;
		}
		/** 将单个 cron 片段解析为 FieldConfig，无法识别时降级为 EVERY */
		function parseField(token, meta) {
			const t = (token || "").trim();
			if (t === "*" || t === "?") return createEvery(meta.min, meta.max);
			if (t.includes("/")) {
				var _parseNum, _parseNum2;
				const [s, i] = t.split("/");
				const start = (_parseNum = parseNum(s, meta.min)) !== null && _parseNum !== void 0 ? _parseNum : meta.min;
				const interval = (_parseNum2 = parseNum(i, 1)) !== null && _parseNum2 !== void 0 ? _parseNum2 : 1;
				return {
					mode: "INTERVAL",
					values: [],
					start,
					interval: Math.max(1, interval),
					end: meta.max
				};
			}
			if (t.includes("-")) {
				var _parseNum3, _parseNum4;
				const [s, e] = t.split("-");
				return {
					mode: "RANGE",
					values: [],
					start: (_parseNum3 = parseNum(s, meta.min)) !== null && _parseNum3 !== void 0 ? _parseNum3 : meta.min,
					interval: 1,
					end: (_parseNum4 = parseNum(e, meta.max)) !== null && _parseNum4 !== void 0 ? _parseNum4 : meta.max
				};
			}
			if (t.includes(",")) return {
				mode: "SPECIFIC",
				values: t.split(",").map((x) => parseNum(x, meta.min)).filter((x) => x != null),
				start: meta.min,
				interval: 1,
				end: meta.max
			};
			const n = parseNum(t, meta.min);
			if (n != null) return {
				mode: "SPECIFIC",
				values: [n],
				start: meta.min,
				interval: 1,
				end: meta.max
			};
			return createEvery(meta.min, meta.max);
		}
		/** FieldConfig -> cron 片段 */
		function fieldToCron(fc) {
			switch (fc.mode) {
				case "EVERY": return "*";
				case "SPECIFIC": return fc.values.length ? [...fc.values].sort((a, b) => a - b).join(",") : "*";
				case "INTERVAL": return `${fc.start}/${fc.interval}`;
				case "RANGE": return `${fc.start}-${fc.end}`;
				default: return "*";
			}
		}
		/** 字段值是否匹配（用于下次执行时间计算） */
		function matchField(fc, value) {
			switch (fc.mode) {
				case "EVERY": return true;
				case "SPECIFIC": return fc.values.includes(value);
				case "INTERVAL": return value >= fc.start && (value - fc.start) % fc.interval === 0;
				case "RANGE": return value >= fc.start && value <= fc.end;
				default: return true;
			}
		}
		/** 拆分 cron，统一为 6 字段（5 字段 unix 自动补秒位 0） */
		function splitCron(cron) {
			const parts = cron.trim().split(/\s+/).filter(Boolean);
			if (parts.length === 6) return parts;
			if (parts.length === 5) return ["0", ...parts];
			throw new Error("cron 表达式必须为 5 或 6 字段");
		}
		/**
		* 规整 日 / 周 两个字段，使其符合 Quartz 规则（二者之一必须为 ?）。
		*
		* <p>? 表示「不指定」，对应编辑器中的 EVERY。规则：</p>
		* <ul>
		*   <li>都为每（*）→ 日=*，周=?</li>
		*   <li>日为每、周指定 → 日=?，周=指定</li>
		*   <li>日指定、周为每 → 日=指定，周=?</li>
		*   <li>都指定（Quartz 不允许）→ 保留日，周=?</li>
		* </ul>
		*/
		function normalizeDayWeek(dayStr, weekStr) {
			const dayEvery = dayStr === "*";
			const weekEvery = weekStr === "*";
			if (dayEvery && weekEvery) return {
				day: "*",
				week: "?"
			};
			if (dayEvery && !weekEvery) return {
				day: "?",
				week: weekStr
			};
			if (!dayEvery && weekEvery) return {
				day: dayStr,
				week: "?"
			};
			return {
				day: dayStr,
				week: "?"
			};
		}
		/** 由 5 个 FieldConfig 组装完整 Quartz 6 字段 cron */
		function compose(f) {
			const { day, week } = normalizeDayWeek(fieldToCron(f.day), fieldToCron(f.week));
			return `0 ${fieldToCron(f.minute)} ${fieldToCron(f.hour)} ${day} ${fieldToCron(f.month)} ${week}`;
		}
		function createDefaultFields() {
			return {
				minute: createEvery(0, 59),
				hour: createEvery(0, 23),
				day: createEvery(1, 31),
				month: createEvery(1, 12),
				week: createEvery(0, 6)
			};
		}
		const fields = reactive(createDefaultFields());
		/** 将 cron 字符串应用到字段状态 */
		function applyCron(cronStr) {
			let parts;
			try {
				parts = splitCron(cronStr);
			} catch (_unused) {
				return;
			}
			fields.minute = parseField(parts[1], FIELDS[0]);
			fields.hour = parseField(parts[2], FIELDS[1]);
			fields.day = parseField(parts[3], FIELDS[2]);
			fields.month = parseField(parts[4], FIELDS[3]);
			fields.week = parseField(parts[5], FIELDS[4]);
		}
		applyCron(props.modelValue);
		watch(() => props.modelValue, (v) => {
			if (v !== cron.value) applyCron(v);
		});
		const cron = computed(() => compose(fields));
		watch(cron, (v) => emit("update:modelValue", v));
		/** 模式切换时重置该字段的默认值，避免遗留非法值 */
		function onModeChange(meta, mode) {
			const fc = fields[meta.key];
			fc.mode = mode;
			if (mode === "SPECIFIC" && fc.values.length === 0) fc.values = [meta.min];
			if (mode === "INTERVAL") {
				fc.start = meta.min;
				fc.interval = 1;
			}
			if (mode === "RANGE") {
				fc.start = meta.min;
				fc.end = meta.max;
			}
		}
		/** 手动输入 cron 字符串 */
		function onCronInput(val) {
			applyCron(val);
		}
		function optionsFor(meta) {
			const list = [];
			for (let v = meta.min; v <= meta.max; v++) {
				var _WEEK_LABELS$v, _MONTH_LABELS;
				let label = String(v);
				if (meta.key === "week") label = (_WEEK_LABELS$v = WEEK_LABELS[v]) !== null && _WEEK_LABELS$v !== void 0 ? _WEEK_LABELS$v : String(v);
				else if (meta.key === "month") label = (_MONTH_LABELS = MONTH_LABELS[v - 1]) !== null && _MONTH_LABELS !== void 0 ? _MONTH_LABELS : String(v);
				list.push({
					label,
					value: v
				});
			}
			return list;
		}
		function pad(n) {
			return String(n).padStart(2, "0");
		}
		function monthDesc(fc) {
			switch (fc.mode) {
				case "EVERY": return "每月";
				case "SPECIFIC": return fc.values.map((v) => {
					var _MONTH_LABELS2;
					return (_MONTH_LABELS2 = MONTH_LABELS[v - 1]) !== null && _MONTH_LABELS2 !== void 0 ? _MONTH_LABELS2 : `${v} 月`;
				}).join("、");
				case "RANGE": return `${fc.start}-${fc.end} 月`;
				case "INTERVAL": return `从 ${fc.start} 月起每 ${fc.interval} 月`;
				default: return "每月";
			}
		}
		/** 生成人类可读描述（覆盖常见组合，非穷尽） */
		function describe(cronStr) {
			let parts;
			try {
				parts = splitCron(cronStr);
			} catch (_unused2) {
				return "无法解析的 cron 表达式";
			}
			const m = parseField(parts[1], FIELDS[0]);
			const h = parseField(parts[2], FIELDS[1]);
			const d = parseField(parts[3], FIELDS[2]);
			const mo = parseField(parts[4], FIELDS[3]);
			const w = parseField(parts[5], FIELDS[4]);
			let timeDesc = "";
			if (m.mode === "EVERY" && h.mode === "EVERY") timeDesc = "每分钟";
			else if (m.mode === "INTERVAL") timeDesc = `每 ${m.interval} 分钟`;
			else if (h.mode === "INTERVAL") {
				timeDesc = `每 ${h.interval} 小时`;
				if (m.mode === "SPECIFIC") timeDesc += `的 ${m.values.map(pad).join("、")} 分`;
			} else if (h.mode === "SPECIFIC" && m.mode === "SPECIFIC") timeDesc = h.values.map((hv) => {
				var _m$values$;
				return `${pad(hv)}:${pad((_m$values$ = m.values[0]) !== null && _m$values$ !== void 0 ? _m$values$ : 0)}`;
			}).join("、");
			else if (h.mode === "EVERY" && m.mode === "SPECIFIC") timeDesc = `每小时的 ${m.values.map(pad).join("、")} 分`;
			else if (h.mode === "SPECIFIC" && m.mode === "EVERY") timeDesc = `${h.values.map(pad).join("、")} 点每分钟`;
			else timeDesc = "按设定时间";
			let dateDesc = "";
			const dayEvery = d.mode === "EVERY";
			const weekEvery = w.mode === "EVERY";
			const monthSuffix = mo.mode === "EVERY" ? "" : `（${monthDesc(mo)}）`;
			if (dayEvery && weekEvery) dateDesc = `每天${monthSuffix}`;
			else if (!dayEvery && weekEvery) if (d.mode === "SPECIFIC") dateDesc = `每月 ${d.values.join("、")} 日${monthSuffix}`;
			else if (d.mode === "RANGE") dateDesc = `每月 ${d.start}-${d.end} 日${monthSuffix}`;
			else if (d.mode === "INTERVAL") dateDesc = `每月从 ${d.start} 日起每 ${d.interval} 日${monthSuffix}`;
			else dateDesc = `每月指定日${monthSuffix}`;
			else if (dayEvery && !weekEvery) {
				var _WEEK_LABELS$w$start, _WEEK_LABELS$w$end, _WEEK_LABELS$w$start2;
				if (w.mode === "SPECIFIC") dateDesc = `每周 ${w.values.map((v) => {
					var _WEEK_LABELS$v2;
					return (_WEEK_LABELS$v2 = WEEK_LABELS[v]) !== null && _WEEK_LABELS$v2 !== void 0 ? _WEEK_LABELS$v2 : v;
				}).join("、")}${monthSuffix}`;
				else if (w.mode === "RANGE") dateDesc = `每周 ${(_WEEK_LABELS$w$start = WEEK_LABELS[w.start]) !== null && _WEEK_LABELS$w$start !== void 0 ? _WEEK_LABELS$w$start : w.start} 至 ${(_WEEK_LABELS$w$end = WEEK_LABELS[w.end]) !== null && _WEEK_LABELS$w$end !== void 0 ? _WEEK_LABELS$w$end : w.end}${monthSuffix}`;
				else if (w.mode === "INTERVAL") dateDesc = `每周从 ${(_WEEK_LABELS$w$start2 = WEEK_LABELS[w.start]) !== null && _WEEK_LABELS$w$start2 !== void 0 ? _WEEK_LABELS$w$start2 : w.start} 起每 ${w.interval} 天${monthSuffix}`;
				else dateDesc = `每周指定日${monthSuffix}`;
			} else dateDesc = `按设定日期${monthSuffix}`;
			return `${dateDesc} ${timeDesc} 执行`.replace(/\s+/g, " ").trim();
		}
		const description = computed(() => describe(cron.value));
		function formatDateTime(d) {
			const p = (n) => String(n).padStart(2, "0");
			return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`;
		}
		/**
		* 计算最近 count 次执行时间。
		*
		* <p>从「下一分钟」开始按分钟向前扫描，逐个时间点匹配 5 个字段。
		* 扫描上限为一年内的分钟数，足以覆盖月/周级别的低频任务。</p>
		*/
		function getNextExecutionTimes(cronStr, count = 5, from = /* @__PURE__ */ new Date()) {
			let parts;
			try {
				parts = splitCron(cronStr);
			} catch (_unused3) {
				return [];
			}
			const m = parseField(parts[1], FIELDS[0]);
			const h = parseField(parts[2], FIELDS[1]);
			const d = parseField(parts[3], FIELDS[2]);
			const mo = parseField(parts[4], FIELDS[3]);
			const w = parseField(parts[5], FIELDS[4]);
			const result = [];
			const start = new Date(from.getTime());
			start.setSeconds(0, 0);
			start.setMinutes(start.getMinutes() + 1);
			const maxIter = 366 * 24 * 60;
			for (let i = 0; i < maxIter && result.length < count; i++) {
				const ts = new Date(start.getTime() + i * 60 * 1e3);
				if (matchField(mo, ts.getMonth() + 1) && matchField(d, ts.getDate()) && matchField(w, ts.getDay()) && matchField(h, ts.getHours()) && matchField(m, ts.getMinutes())) result.push(formatDateTime(ts));
			}
			return result;
		}
		const nextTimes = computed(() => getNextExecutionTimes(cron.value, 5));
		return (_ctx, _cache) => {
			const _component_el_radio_button = resolveComponent("el-radio-button");
			const _component_el_radio_group = resolveComponent("el-radio-group");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_form = resolveComponent("el-form");
			return openBlock(), createElementBlock("div", _hoisted_1$3, [createElementVNode("div", _hoisted_2$1, [(openBlock(), createElementBlock(Fragment, null, renderList(FIELDS, (meta) => {
				return createElementVNode("div", {
					key: meta.key,
					class: "ce-field-row"
				}, [
					createElementVNode("div", _hoisted_3$1, toDisplayString(meta.label), 1),
					createVNode(_component_el_radio_group, {
						"model-value": fields[meta.key].mode,
						size: "small",
						"onUpdate:modelValue": ($event) => onModeChange(meta, $event)
					}, {
						default: withCtx(() => [
							createVNode(_component_el_radio_button, { value: "EVERY" }, {
								default: withCtx(() => [..._cache[0] || (_cache[0] = [createTextVNode("每", -1)])]),
								_: 1
							}),
							createVNode(_component_el_radio_button, { value: "SPECIFIC" }, {
								default: withCtx(() => [..._cache[1] || (_cache[1] = [createTextVNode("指定", -1)])]),
								_: 1
							}),
							createVNode(_component_el_radio_button, { value: "INTERVAL" }, {
								default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("间隔", -1)])]),
								_: 1
							}),
							createVNode(_component_el_radio_button, { value: "RANGE" }, {
								default: withCtx(() => [..._cache[3] || (_cache[3] = [createTextVNode("范围", -1)])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["model-value", "onUpdate:modelValue"]),
					createElementVNode("div", _hoisted_4$1, [fields[meta.key].mode === "EVERY" ? (openBlock(), createElementBlock("span", _hoisted_5$1, "每" + toDisplayString(meta.label), 1)) : fields[meta.key].mode === "SPECIFIC" ? (openBlock(), createBlock(_component_el_select, {
						key: 1,
						modelValue: fields[meta.key].values,
						"onUpdate:modelValue": ($event) => fields[meta.key].values = $event,
						multiple: "",
						filterable: "",
						"collapse-tags": "",
						"collapse-tags-tooltip": "",
						placeholder: "选择值",
						style: { "min-width": "200px" }
					}, {
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(optionsFor(meta), (o) => {
							return openBlock(), createBlock(_component_el_option, {
								key: o.value,
								label: o.label,
								value: o.value
							}, null, 8, ["label", "value"]);
						}), 128))]),
						_: 2
					}, 1032, ["modelValue", "onUpdate:modelValue"])) : fields[meta.key].mode === "INTERVAL" ? (openBlock(), createElementBlock(Fragment, { key: 2 }, [
						createVNode(_component_el_input_number, {
							modelValue: fields[meta.key].start,
							"onUpdate:modelValue": ($event) => fields[meta.key].start = $event,
							min: meta.min,
							max: meta.max,
							size: "small"
						}, null, 8, [
							"modelValue",
							"onUpdate:modelValue",
							"min",
							"max"
						]),
						_cache[4] || (_cache[4] = createElementVNode("span", { class: "ce-inline-text" }, "起，每", -1)),
						createVNode(_component_el_input_number, {
							modelValue: fields[meta.key].interval,
							"onUpdate:modelValue": ($event) => fields[meta.key].interval = $event,
							min: 1,
							max: meta.max,
							size: "small"
						}, null, 8, [
							"modelValue",
							"onUpdate:modelValue",
							"max"
						]),
						createElementVNode("span", _hoisted_6$1, toDisplayString(meta.label), 1)
					], 64)) : fields[meta.key].mode === "RANGE" ? (openBlock(), createElementBlock(Fragment, { key: 3 }, [
						createVNode(_component_el_input_number, {
							modelValue: fields[meta.key].start,
							"onUpdate:modelValue": ($event) => fields[meta.key].start = $event,
							min: meta.min,
							max: meta.max,
							size: "small"
						}, null, 8, [
							"modelValue",
							"onUpdate:modelValue",
							"min",
							"max"
						]),
						_cache[5] || (_cache[5] = createElementVNode("span", { class: "ce-inline-text" }, "至", -1)),
						createVNode(_component_el_input_number, {
							modelValue: fields[meta.key].end,
							"onUpdate:modelValue": ($event) => fields[meta.key].end = $event,
							min: meta.min,
							max: meta.max,
							size: "small"
						}, null, 8, [
							"modelValue",
							"onUpdate:modelValue",
							"min",
							"max"
						]),
						createElementVNode("span", _hoisted_7$1, toDisplayString(meta.label), 1)
					], 64)) : createCommentVNode("", true)])
				]);
			}), 64))]), createElementVNode("div", _hoisted_8$1, [createVNode(_component_el_form, {
				"label-width": "80px",
				"label-position": "left"
			}, {
				default: withCtx(() => [
					createVNode(_component_el_form_item, { label: "表达式" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							"model-value": cron.value,
							style: { "font-family": "monospace" },
							"onUpdate:modelValue": onCronInput
						}, {
							append: withCtx(() => [..._cache[6] || (_cache[6] = [createElementVNode("span", { style: { "color": "var(--el-text-color-secondary)" } }, "秒固定 0", -1)])]),
							_: 1
						}, 8, ["model-value"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "描述" }, {
						default: withCtx(() => [createElementVNode("span", _hoisted_9$1, toDisplayString(description.value), 1)]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "下次执行" }, {
						default: withCtx(() => [nextTimes.value.length ? (openBlock(), createElementBlock("div", _hoisted_10$1, [(openBlock(true), createElementBlock(Fragment, null, renderList(nextTimes.value, (t, i) => {
							return openBlock(), createElementBlock("span", {
								key: i,
								class: "ce-next-item"
							}, toDisplayString(t), 1);
						}), 128))])) : (openBlock(), createElementBlock("span", _hoisted_11$1, "一年内无可执行时间，请检查表达式"))]),
						_: 1
					})
				]),
				_: 1
			})])]);
		};
	}
}), [["__scopeId", "data-v-7fdb833c"]]);
//#endregion
//#region src/components/TriggerDesigner/QuartzTriggerConfig.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$2 = { class: "quartz-trigger-config" };
//#endregion
//#region src/components/TriggerDesigner/QuartzTriggerConfig.vue
var QuartzTriggerConfig_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "QuartzTriggerConfigView",
	__name: "QuartzTriggerConfig",
	props: { modelValue: {} },
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		/**
		* Quartz 触发配置（包装 CronEditor 可视化编辑器）。
		*
		* <p>结构化配置仅含 cronExpression 一个字段，通过 CronEditor 进行可视化编辑。
		* CronEditor 内部将 cron 字符串与 5 字段编辑状态双向同步，并附带人类可读
		* 描述与下次执行时间预览。</p>
		*/
		const props = __props;
		const emit = __emit;
		const form = reactive({ cronExpression: props.modelValue.cronExpression || "0 0 * * * ?" });
		watch(() => props.modelValue, (v) => {
			if (v.cronExpression !== form.cronExpression) form.cronExpression = v.cronExpression || "0 0 * * * ?";
		});
		watch(() => form.cronExpression, (v) => {
			emit("update:modelValue", { cronExpression: v });
		});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", _hoisted_1$2, [_cache[1] || (_cache[1] = createElementVNode("div", { class: "section-hint" }, " 配置定时触发器的 Quartz cron 表达式（6 字段：秒 分 时 日 月 周）。 ", -1)), createVNode(CronEditor_default, {
				modelValue: form.cronExpression,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => form.cronExpression = $event)
			}, null, 8, ["modelValue"])]);
		};
	}
}), [["__scopeId", "data-v-64286586"]]);
//#endregion
//#region src/components/TriggerDesigner/EventTriggerConfig.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$1 = { class: "schema-wrap" };
//#endregion
//#region src/components/TriggerDesigner/EventTriggerConfig.vue
var EventTriggerConfig_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "EventTriggerConfigView",
	__name: "EventTriggerConfig",
	props: { modelValue: {} },
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		/**
		* 事件触发配置（EventBus 事件名）。
		*
		* <p>配置项：</p>
		* <ul>
		*   <li>事件名：可从预置事件下拉选择或手动输入（如 entity.created / form.submitted）</li>
		*   <li>事件 payload schema（可选）：字段名 + 类型的表格编辑器</li>
		* </ul>
		*/
		const props = __props;
		const emit = __emit;
		/** 预置事件名（可扩展） */
		const presetEvents = [
			"entity.created",
			"entity.updated",
			"entity.deleted",
			"form.submitted",
			"form.approved",
			"workflow.started",
			"workflow.completed",
			"connector.called"
		];
		/** payload 字段类型选项 */
		const typeOptions = [
			"STRING",
			"INTEGER",
			"LONG",
			"DECIMAL",
			"BOOLEAN",
			"DATE",
			"DATETIME",
			"OBJECT",
			"ARRAY"
		];
		const form = reactive({
			eventName: props.modelValue.eventName || "",
			payloadSchema: (props.modelValue.payloadSchema || []).map((f) => ({ ...f }))
		});
		watch(() => props.modelValue, (v) => {
			form.eventName = v.eventName || "";
			form.payloadSchema = (v.payloadSchema || []).map((f) => ({ ...f }));
		});
		watch(form, () => {
			emit("update:modelValue", {
				eventName: form.eventName,
				payloadSchema: (form.payloadSchema || []).map((f) => ({ ...f }))
			});
		}, { deep: true });
		function addField() {
			if (!form.payloadSchema) form.payloadSchema = [];
			form.payloadSchema.push({
				name: "",
				type: "STRING"
			});
		}
		function removeField(index) {
			if (!form.payloadSchema) return;
			form.payloadSchema.splice(index, 1);
		}
		return (_ctx, _cache) => {
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_form = resolveComponent("el-form");
			return openBlock(), createBlock(_component_el_form, {
				model: form,
				"label-width": "100px",
				class: "event-trigger-config"
			}, {
				default: withCtx(() => [createVNode(_component_el_form_item, {
					label: "事件名",
					required: ""
				}, {
					default: withCtx(() => [createVNode(_component_el_select, {
						modelValue: form.eventName,
						"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => form.eventName = $event),
						filterable: "",
						"allow-create": "",
						"default-first-option": "",
						placeholder: "选择或输入事件名（如 entity.created）",
						style: { "width": "100%" }
					}, {
						default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(presetEvents, (e) => {
							return createVNode(_component_el_option, {
								key: e,
								label: e,
								value: e
							}, null, 8, ["label", "value"]);
						}), 64))]),
						_: 1
					}, 8, ["modelValue"]), _cache[1] || (_cache[1] = createElementVNode("div", { class: "field-hint" }, "EventBus 事件名，发布方与订阅方需保持一致", -1))]),
					_: 1
				}), createVNode(_component_el_form_item, { label: "Payload Schema" }, {
					default: withCtx(() => [createElementVNode("div", _hoisted_1$1, [
						createVNode(_component_el_table, {
							data: form.payloadSchema,
							border: "",
							size: "small",
							"empty-text": "暂无字段"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									label: "字段名",
									"min-width": "160"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_input, {
										modelValue: row.name,
										"onUpdate:modelValue": ($event) => row.name = $event,
										placeholder: "如 userId",
										size: "small"
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "类型",
									width: "160"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_select, {
										modelValue: row.type,
										"onUpdate:modelValue": ($event) => row.type = $event,
										size: "small"
									}, {
										default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(typeOptions, (t) => {
											return createVNode(_component_el_option, {
												key: t,
												label: t,
												value: t
											}, null, 8, ["label", "value"]);
										}), 64))]),
										_: 1
									}, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "操作",
									width: "80",
									align: "center"
								}, {
									default: withCtx(({ $index }) => [createVNode(_component_el_button, {
										size: "small",
										type: "danger",
										link: "",
										onClick: ($event) => removeField($index)
									}, {
										default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("删除", -1)])]),
										_: 1
									}, 8, ["onClick"])]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["data"]),
						createVNode(_component_el_button, {
							size: "small",
							style: { "margin-top": "8px" },
							onClick: addField
						}, {
							default: withCtx(() => [..._cache[3] || (_cache[3] = [createTextVNode("+ 新增字段", -1)])]),
							_: 1
						}),
						_cache[4] || (_cache[4] = createElementVNode("div", { class: "field-hint" }, "可选。定义事件 payload 的字段结构，便于目标微流/流程接收时校验", -1))
					])]),
					_: 1
				})]),
				_: 1
			}, 8, ["model"]);
		};
	}
}), [["__scopeId", "data-v-443a761c"]]);
//#endregion
//#region src/views/lowcode/trigger-list/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { style: { "padding": "16px" } };
var _hoisted_2 = { style: {
	"display": "flex",
	"justify-content": "space-between",
	"align-items": "center"
} };
var _hoisted_3 = { style: { "margin-left": "6px" } };
var _hoisted_4 = {
	key: 0,
	class: "step-body"
};
var _hoisted_5 = { class: "field-hint" };
var _hoisted_6 = { class: "config-preview" };
var _hoisted_7 = {
	key: 0,
	class: "field-hint",
	style: { "margin-left": "12px" }
};
var _hoisted_8 = { class: "config-preview" };
var _hoisted_9 = { class: "history-title" };
var _hoisted_10 = { class: "log-expand" };
var _hoisted_11 = {
	key: 0,
	class: "log-section"
};
var _hoisted_12 = { class: "config-preview" };
var _hoisted_13 = {
	key: 1,
	class: "log-section"
};
var _hoisted_14 = { class: "config-preview" };
var _hoisted_15 = {
	key: 2,
	class: "log-section"
};
var _hoisted_16 = { class: "config-preview error-preview" };
var _hoisted_17 = { class: "dialog-footer" };
//#endregion
//#region src/views/lowcode/trigger-list/index.vue
var trigger_list_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "TriggerListView",
	__name: "index",
	setup(__props) {
		/**
		* 低代码触发器列表 + 可视化触发器构建器。
		*
		* <p>将原先的「表格 + JSON 文本框」升级为分步表单构建器（借鉴 ServiceNow Flow
		* Designer / Budibase Automation）：</p>
		* <ol>
		*   <li>Step 1 触发器元信息：code / name / type（编辑时不可更改）/ targetType /
		*       targetCode（下拉从微流/流程列表加载）/ status</li>
		*   <li>Step 2 触发配置：按 type 分发 — CRUD / QUARTZ（Cron 可视化编辑器）/ EVENT</li>
		*   <li>Step 3 手动测试：输入数据 JSON → 调用 executeTrigger → 展示结果</li>
		* </ol>
		*
		* <p>config 在编辑器内部以结构化对象操作，保存时序列化为 JSON 字符串存入
		* LowCodeTrigger.config；加载时通过 parseTriggerConfig 反向解析并兼容历史写法。</p>
		*/
		const list = ref([]);
		const dialogVisible = ref(false);
		const activeStep = ref(0);
		const saving = ref(false);
		const typeOptions = [
			{
				label: "CRUD（数据增删改）",
				value: "CRUD"
			},
			{
				label: "Quartz（定时）",
				value: "QUARTZ"
			},
			{
				label: "Event（事件）",
				value: "EVENT"
			}
		];
		const targetTypeOptions = [{
			label: "微流",
			value: "MICROFLOW"
		}, {
			label: "流程",
			value: "PROCESS"
		}];
		const statusOptions = [{
			label: "启用",
			value: "ACTIVE"
		}, {
			label: "停用",
			value: "INACTIVE"
		}];
		const steps = [
			{
				title: "元信息",
				description: "编码 / 名称 / 类型 / 目标"
			},
			{
				title: "触发配置",
				description: "按类型分发的可视化配置"
			},
			{
				title: "手动测试",
				description: "输入数据 → 执行 → 查看结果"
			}
		];
		const isLastStep = computed(() => activeStep.value === steps.length - 1);
		/** 当前编辑的触发器顶层字段 */
		const current = ref(null);
		/** 结构化 config（按 current.type 解析） */
		const configObj = ref(createDefaultTriggerConfig("CRUD"));
		/** 微流 / 流程列表，用于目标编码下拉 */
		const microflows = ref([]);
		const processes = ref([]);
		/** 是否为编辑已有触发器（决定 type 是否可更改） */
		const isEdit = computed(() => {
			var _current$value;
			return !!((_current$value = current.value) === null || _current$value === void 0 ? void 0 : _current$value.id);
		});
		/** 目标编码下拉选项（按 targetType 切换数据源） */
		const targetOptions = computed(() => {
			var _current$value2;
			if (((_current$value2 = current.value) === null || _current$value2 === void 0 ? void 0 : _current$value2.targetType) === "PROCESS") return processes.value.map((p) => ({
				label: `${p.processDefinitionName || p.processDefinitionKey} (${p.processDefinitionKey})`,
				value: p.processDefinitionKey
			}));
			return microflows.value.map((m) => ({
				label: `${m.name} (${m.code})`,
				value: m.code
			}));
		});
		const crudConfig = computed({
			get: () => configObj.value,
			set: (v) => {
				configObj.value = v;
			}
		});
		const quartzConfig = computed({
			get: () => configObj.value,
			set: (v) => {
				configObj.value = v;
			}
		});
		const eventConfig = computed({
			get: () => configObj.value,
			set: (v) => {
				configObj.value = v;
			}
		});
		async function load() {
			list.value = await getTriggerList();
		}
		async function loadTargets() {
			const [mfs, procs] = await Promise.all([getMicroflowList().catch(() => []), getProcessBindings().catch(() => [])]);
			microflows.value = mfs;
			processes.value = procs;
		}
		function openNew() {
			current.value = {
				code: "",
				name: "",
				type: "CRUD",
				config: "{}",
				targetType: "MICROFLOW",
				targetCode: "",
				status: "ACTIVE"
			};
			configObj.value = createDefaultTriggerConfig("CRUD");
			activeStep.value = 0;
			testData.value = "{\n  \n}";
			testResult.value = "";
			executionLogs.value = [];
			historyCollapseActive.value = [];
			expandedRows.value = [];
			dialogVisible.value = true;
			loadTargets();
		}
		function openEdit(row, startStep = 0) {
			current.value = { ...row };
			configObj.value = parseTriggerConfig(row.config, row.type);
			activeStep.value = startStep;
			testData.value = "{\n  \n}";
			testResult.value = "";
			executionLogs.value = [];
			historyCollapseActive.value = [];
			expandedRows.value = [];
			dialogVisible.value = true;
			loadTargets();
		}
		/** 新建模式下切换类型：重置触发配置为该类型默认值 */
		function onTypeChange(val) {
			if (!current.value) return;
			if (isEdit.value) return;
			current.value.type = val;
			configObj.value = createDefaultTriggerConfig(val);
			activeStep.value = 0;
		}
		function next() {
			if (activeStep.value === 0 && !validateBasic()) return;
			if (activeStep.value < steps.length - 1) activeStep.value++;
		}
		function prev() {
			if (activeStep.value > 0) activeStep.value--;
		}
		function validateBasic() {
			const c = current.value;
			if (!c) return false;
			if (!c.code.trim() || !c.name.trim()) {
				ElMessage.warning("请填写编码和名称");
				return false;
			}
			if (!c.targetCode.trim()) {
				ElMessage.warning("请选择目标编码");
				return false;
			}
			return true;
		}
		function validateConfig() {
			const c = current.value;
			if (!c) return false;
			if (c.type === "CRUD") {
				const cfg = configObj.value;
				if (!cfg.entityCode) {
					ElMessage.warning("请选择触发实体");
					return false;
				}
				if (!cfg.operations.length) {
					ElMessage.warning("请至少选择一个触发操作");
					return false;
				}
				if (!cfg.timing.length) {
					ElMessage.warning("请至少选择一个触发时机");
					return false;
				}
			} else if (c.type === "EVENT") {
				if (!configObj.value.eventName.trim()) {
					ElMessage.warning("请填写事件名");
					return false;
				}
			}
			return true;
		}
		function buildTrigger() {
			const c = current.value;
			if (!c) return null;
			return {
				id: c.id,
				code: c.code,
				name: c.name,
				type: c.type,
				config: serializeTriggerConfig(configObj.value),
				targetType: c.targetType,
				targetCode: c.targetCode,
				status: c.status
			};
		}
		async function save(closeAfter = false) {
			if (!validateBasic()) {
				activeStep.value = 0;
				return;
			}
			if (!validateConfig()) {
				activeStep.value = 1;
				return;
			}
			const payload = buildTrigger();
			if (!payload) return;
			saving.value = true;
			try {
				const saved = await saveTrigger(payload);
				if (current.value) current.value.id = saved.id;
				ElMessage.success("保存成功");
				await load();
				if (closeAfter) dialogVisible.value = false;
			} catch (e) {
				ElMessage.error("保存失败：" + (e instanceof Error ? e.message : String(e)));
			} finally {
				saving.value = false;
			}
		}
		async function remove(row) {
			if (!row.id) return;
			try {
				await ElMessageBox.confirm(`确认删除触发器「${row.name}」？`, "确认", { type: "warning" });
				await deleteTrigger(row.id);
				ElMessage.success("删除成功");
				await load();
			} catch (_unused) {}
		}
		const testData = ref("{\n  \n}");
		const testResult = ref("");
		const testing = ref(false);
		async function runTest() {
			const c = current.value;
			if (!c) return;
			if (!c.id) {
				ElMessage.warning("请先保存触发器再测试");
				return;
			}
			let data = {};
			try {
				data = testData.value.trim() ? JSON.parse(testData.value) : {};
			} catch (_unused2) {
				ElMessage.error("输入数据 JSON 解析失败");
				return;
			}
			testing.value = true;
			testResult.value = "";
			try {
				const result = await executeTrigger(c.code, data);
				testResult.value = JSON.stringify(result, null, 2);
				ElMessage.success("执行完成");
				await loadExecutionLogs();
			} catch (e) {
				testResult.value = "执行失败：" + (e instanceof Error ? e.message : String(e));
				await loadExecutionLogs();
			} finally {
				testing.value = false;
			}
		}
		/** 当前编辑触发器的执行历史列表 */
		const executionLogs = ref([]);
		/** 折叠面板激活项（含 'history' 即展开） */
		const historyCollapseActive = ref([]);
		/** 历史加载中 */
		const historyLoading = ref(false);
		/** 展开行（点击行展开 inputs/outputs JSON）的 row key 集合 */
		const expandedRows = ref([]);
		/** 加载当前触发器的执行历史 */
		async function loadExecutionLogs() {
			const c = current.value;
			if (!(c === null || c === void 0 ? void 0 : c.id)) {
				executionLogs.value = [];
				return;
			}
			historyLoading.value = true;
			try {
				executionLogs.value = await getTriggerExecutionLogs(c.id, 50);
			} catch (e) {
				console.warn("加载执行历史失败：", e);
				executionLogs.value = [];
			} finally {
				historyLoading.value = false;
			}
		}
		/** 折叠面板展开时按需加载历史 */
		async function onHistoryCollapseChange(activeNames) {
			if ((Array.isArray(activeNames) ? activeNames : [activeNames]).includes("history") && executionLogs.value.length === 0) await loadExecutionLogs();
		}
		/** 手动刷新历史 */
		async function refreshHistory() {
			await loadExecutionLogs();
		}
		/** 行展开变化时同步 expandedRows（受控展开） */
		function onRowExpandChange(_row, expandedList) {
			expandedRows.value = expandedList.map((r) => r.id).filter((id) => typeof id === "number");
		}
		/** 美化 JSON 字符串展示（无效 JSON 原样返回） */
		function prettyJson(s) {
			if (!s) return "";
			try {
				return JSON.stringify(JSON.parse(s), null, 2);
			} catch (_unused3) {
				return s;
			}
		}
		/** 格式化耗时展示 */
		function formatDuration(ms) {
			if (ms == null) return "-";
			if (ms < 1e3) return `${ms} ms`;
			return `${(ms / 1e3).toFixed(2)} s`;
		}
		const configPreview = computed(() => serializeTriggerConfig(configObj.value));
		onMounted(load);
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_step = resolveComponent("el-step");
			const _component_el_steps = resolveComponent("el-steps");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_radio_button = resolveComponent("el-radio-button");
			const _component_el_radio_group = resolveComponent("el-radio-group");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_divider = resolveComponent("el-divider");
			const _component_el_collapse_item = resolveComponent("el-collapse-item");
			const _component_el_collapse = resolveComponent("el-collapse");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_card, { shadow: "never" }, {
				header: withCtx(() => [createElementVNode("div", _hoisted_2, [_cache[16] || (_cache[16] = createElementVNode("span", null, "触发器", -1)), createVNode(_component_el_button, {
					type: "primary",
					onClick: openNew
				}, {
					default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode("新建触发器", -1)])]),
					_: 1
				})])]),
				default: withCtx(() => [createVNode(_component_el_table, { data: list.value }, {
					default: withCtx(() => [
						createVNode(_component_el_table_column, {
							label: "编码",
							prop: "code",
							"min-width": "140",
							"show-overflow-tooltip": ""
						}),
						createVNode(_component_el_table_column, {
							label: "名称",
							prop: "name",
							"min-width": "140",
							"show-overflow-tooltip": ""
						}),
						createVNode(_component_el_table_column, {
							label: "类型",
							prop: "type",
							width: "100"
						}),
						createVNode(_component_el_table_column, {
							label: "目标",
							"min-width": "160"
						}, {
							default: withCtx(({ row }) => [createVNode(_component_el_tag, { size: "small" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(row.targetType), 1)]),
								_: 2
							}, 1024), createElementVNode("span", _hoisted_3, toDisplayString(row.targetCode), 1)]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							label: "状态",
							prop: "status",
							width: "100"
						}, {
							default: withCtx(({ row }) => [createVNode(_component_el_tag, { type: row.status === "ACTIVE" ? "success" : "info" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(row.status), 1)]),
								_: 2
							}, 1032, ["type"])]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							label: "操作",
							width: "280"
						}, {
							default: withCtx(({ row }) => [
								createVNode(_component_el_button, {
									size: "small",
									onClick: ($event) => openEdit(row)
								}, {
									default: withCtx(() => [..._cache[17] || (_cache[17] = [createTextVNode("编辑", -1)])]),
									_: 1
								}, 8, ["onClick"]),
								createVNode(_component_el_button, {
									size: "small",
									type: "success",
									onClick: ($event) => openEdit(row, 2)
								}, {
									default: withCtx(() => [..._cache[18] || (_cache[18] = [createTextVNode("执行", -1)])]),
									_: 1
								}, 8, ["onClick"]),
								createVNode(_component_el_button, {
									size: "small",
									type: "danger",
									onClick: ($event) => remove(row)
								}, {
									default: withCtx(() => [..._cache[19] || (_cache[19] = [createTextVNode("删除", -1)])]),
									_: 1
								}, 8, ["onClick"])
							]),
							_: 1
						})
					]),
					_: 1
				}, 8, ["data"])]),
				_: 1
			}), createVNode(_component_el_dialog, {
				modelValue: dialogVisible.value,
				"onUpdate:modelValue": _cache[14] || (_cache[14] = ($event) => dialogVisible.value = $event),
				title: isEdit.value ? "编辑触发器" : "新建触发器",
				width: "920px",
				"close-on-click-modal": false,
				"destroy-on-close": "",
				class: "trigger-designer-dialog"
			}, {
				footer: withCtx(() => [createElementVNode("div", _hoisted_17, [
					createVNode(_component_el_button, { onClick: _cache[11] || (_cache[11] = ($event) => dialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[27] || (_cache[27] = [createTextVNode("关闭", -1)])]),
						_: 1
					}),
					createVNode(_component_el_button, {
						disabled: activeStep.value === 0,
						onClick: prev
					}, {
						default: withCtx(() => [..._cache[28] || (_cache[28] = [createTextVNode("上一步", -1)])]),
						_: 1
					}, 8, ["disabled"]),
					!isLastStep.value ? (openBlock(), createBlock(_component_el_button, {
						key: 0,
						type: "primary",
						onClick: next
					}, {
						default: withCtx(() => [..._cache[29] || (_cache[29] = [createTextVNode("下一步", -1)])]),
						_: 1
					})) : createCommentVNode("", true),
					createVNode(_component_el_button, {
						type: "success",
						loading: saving.value,
						onClick: _cache[12] || (_cache[12] = ($event) => save(false))
					}, {
						default: withCtx(() => [..._cache[30] || (_cache[30] = [createTextVNode("保存", -1)])]),
						_: 1
					}, 8, ["loading"]),
					createVNode(_component_el_button, {
						type: "primary",
						loading: saving.value,
						onClick: _cache[13] || (_cache[13] = ($event) => save(true))
					}, {
						default: withCtx(() => [..._cache[31] || (_cache[31] = [createTextVNode("保存并关闭", -1)])]),
						_: 1
					}, 8, ["loading"])
				])]),
				default: withCtx(() => {
					var _current$value3;
					return [
						createVNode(_component_el_steps, {
							active: activeStep.value,
							"finish-status": "success",
							"align-center": "",
							class: "step-header"
						}, {
							default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(steps, (s, idx) => {
								return createVNode(_component_el_step, {
									key: s.title,
									title: s.title,
									description: s.description,
									onClick: ($event) => activeStep.value = idx
								}, null, 8, [
									"title",
									"description",
									"onClick"
								]);
							}), 64))]),
							_: 1
						}, 8, ["active"]),
						current.value ? (openBlock(), createElementBlock("div", _hoisted_4, [
							withDirectives(createElementVNode("div", null, [createVNode(_component_el_form, {
								model: current.value,
								"label-width": "100px",
								class: "meta-form"
							}, {
								default: withCtx(() => [
									createVNode(_component_el_form_item, {
										label: "编码",
										required: ""
									}, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: current.value.code,
											"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => current.value.code = $event),
											placeholder: "如 orderCreateTrigger",
											disabled: isEdit.value
										}, null, 8, ["modelValue", "disabled"])]),
										_: 1
									}),
									createVNode(_component_el_form_item, {
										label: "名称",
										required: ""
									}, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: current.value.name,
											"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => current.value.name = $event),
											placeholder: "如 订单创建触发器"
										}, null, 8, ["modelValue"])]),
										_: 1
									}),
									createVNode(_component_el_form_item, {
										label: "类型",
										required: ""
									}, {
										default: withCtx(() => [createVNode(_component_el_select, {
											"model-value": current.value.type,
											disabled: isEdit.value,
											style: { "width": "100%" },
											"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => onTypeChange($event))
										}, {
											default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(typeOptions, (o) => {
												return createVNode(_component_el_option, {
													key: o.value,
													label: o.label,
													value: o.value
												}, null, 8, ["label", "value"]);
											}), 64))]),
											_: 1
										}, 8, ["model-value", "disabled"]), createElementVNode("div", _hoisted_5, toDisplayString(isEdit.value ? "编辑模式下类型不可更改" : "选择后将进入对应类型的可视化配置；切换类型会重置触发配置"), 1)]),
										_: 1
									}),
									createVNode(_component_el_form_item, {
										label: "目标类型",
										required: ""
									}, {
										default: withCtx(() => [createVNode(_component_el_radio_group, {
											modelValue: current.value.targetType,
											"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => current.value.targetType = $event)
										}, {
											default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(targetTypeOptions, (o) => {
												return createVNode(_component_el_radio_button, {
													key: o.value,
													value: o.value
												}, {
													default: withCtx(() => [createTextVNode(toDisplayString(o.label), 1)]),
													_: 2
												}, 1032, ["value"]);
											}), 64))]),
											_: 1
										}, 8, ["modelValue"])]),
										_: 1
									}),
									createVNode(_component_el_form_item, {
										label: "目标编码",
										required: ""
									}, {
										default: withCtx(() => [createVNode(_component_el_select, {
											modelValue: current.value.targetCode,
											"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => current.value.targetCode = $event),
											filterable: "",
											"allow-create": "",
											"default-first-option": "",
											placeholder: "选择目标微流/流程，或手动输入编码",
											style: { "width": "100%" }
										}, {
											default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(targetOptions.value, (o) => {
												return openBlock(), createBlock(_component_el_option, {
													key: o.value,
													label: o.label,
													value: o.value
												}, null, 8, ["label", "value"]);
											}), 128))]),
											_: 1
										}, 8, ["modelValue"])]),
										_: 1
									}),
									createVNode(_component_el_form_item, { label: "状态" }, {
										default: withCtx(() => [createVNode(_component_el_select, {
											modelValue: current.value.status,
											"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => current.value.status = $event),
											style: { "width": "200px" }
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
									})
								]),
								_: 1
							}, 8, ["model"])], 512), [[vShow, activeStep.value === 0]]),
							withDirectives(createElementVNode("div", null, [
								current.value.type === "CRUD" ? (openBlock(), createBlock(CrudTriggerConfig_default, {
									key: 0,
									modelValue: crudConfig.value,
									"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => crudConfig.value = $event)
								}, null, 8, ["modelValue"])) : current.value.type === "QUARTZ" ? (openBlock(), createBlock(QuartzTriggerConfig_default, {
									key: 1,
									modelValue: quartzConfig.value,
									"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => quartzConfig.value = $event)
								}, null, 8, ["modelValue"])) : current.value.type === "EVENT" ? (openBlock(), createBlock(EventTriggerConfig_default, {
									key: 2,
									modelValue: eventConfig.value,
									"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => eventConfig.value = $event)
								}, null, 8, ["modelValue"])) : createCommentVNode("", true),
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[20] || (_cache[20] = [createTextVNode("配置预览（序列化 JSON）", -1)])]),
									_: 1
								}),
								createElementVNode("pre", _hoisted_6, toDisplayString(configPreview.value), 1)
							], 512), [[vShow, activeStep.value === 1]]),
							withDirectives(createElementVNode("div", null, [createVNode(_component_el_form, { "label-width": "100px" }, {
								default: withCtx(() => [
									createVNode(_component_el_form_item, { label: "输入数据" }, {
										default: withCtx(() => [createVNode(_component_el_input, {
											modelValue: testData.value,
											"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => testData.value = $event),
											type: "textarea",
											rows: 8,
											placeholder: "{\"key\":\"value\"}",
											style: { "font-family": "monospace" }
										}, null, 8, ["modelValue"])]),
										_: 1
									}),
									createVNode(_component_el_form_item, null, {
										default: withCtx(() => [createVNode(_component_el_button, {
											type: "primary",
											loading: testing.value,
											onClick: runTest
										}, {
											default: withCtx(() => [..._cache[21] || (_cache[21] = [createTextVNode("执行", -1)])]),
											_: 1
										}, 8, ["loading"]), !current.value.id ? (openBlock(), createElementBlock("span", _hoisted_7, " 触发器尚未保存，请先保存再测试 ")) : createCommentVNode("", true)]),
										_: 1
									}),
									testResult.value ? (openBlock(), createBlock(_component_el_form_item, {
										key: 0,
										label: "执行结果"
									}, {
										default: withCtx(() => [createElementVNode("pre", _hoisted_8, toDisplayString(testResult.value), 1)]),
										_: 1
									})) : createCommentVNode("", true)
								]),
								_: 1
							})], 512), [[vShow, activeStep.value === 2]])
						])) : createCommentVNode("", true),
						((_current$value3 = current.value) === null || _current$value3 === void 0 ? void 0 : _current$value3.id) ? (openBlock(), createBlock(_component_el_collapse, {
							key: 1,
							modelValue: historyCollapseActive.value,
							"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => historyCollapseActive.value = $event),
							class: "history-collapse",
							onChange: onHistoryCollapseChange
						}, {
							default: withCtx(() => [createVNode(_component_el_collapse_item, { name: "history" }, {
								title: withCtx(() => [createElementVNode("div", _hoisted_9, [
									_cache[23] || (_cache[23] = createElementVNode("span", null, "执行历史", -1)),
									createVNode(_component_el_tag, {
										size: "small",
										type: "info",
										style: { "margin-left": "8px" }
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(executionLogs.value.length), 1)]),
										_: 1
									}),
									createVNode(_component_el_button, {
										link: "",
										type: "primary",
										size: "small",
										style: { "margin-left": "12px" },
										loading: historyLoading.value,
										onClick: withModifiers(refreshHistory, ["stop"])
									}, {
										default: withCtx(() => [..._cache[22] || (_cache[22] = [createTextVNode("刷新", -1)])]),
										_: 1
									}, 8, ["loading"])
								])]),
								default: withCtx(() => [withDirectives((openBlock(), createBlock(_component_el_table, {
									data: executionLogs.value,
									"row-key": "id",
									size: "small",
									"expand-row-keys": expandedRows.value,
									onExpandChange: onRowExpandChange,
									"empty-text": "暂无执行历史",
									"max-height": "320"
								}, {
									default: withCtx(() => [
										createVNode(_component_el_table_column, { type: "expand" }, {
											default: withCtx(({ row }) => [createElementVNode("div", _hoisted_10, [
												row.inputs ? (openBlock(), createElementBlock("div", _hoisted_11, [_cache[24] || (_cache[24] = createElementVNode("div", { class: "log-section-title" }, "输入（inputs）", -1)), createElementVNode("pre", _hoisted_12, toDisplayString(prettyJson(row.inputs)), 1)])) : createCommentVNode("", true),
												row.outputs ? (openBlock(), createElementBlock("div", _hoisted_13, [_cache[25] || (_cache[25] = createElementVNode("div", { class: "log-section-title" }, "输出（outputs）", -1)), createElementVNode("pre", _hoisted_14, toDisplayString(prettyJson(row.outputs)), 1)])) : createCommentVNode("", true),
												row.errorMessage ? (openBlock(), createElementBlock("div", _hoisted_15, [_cache[26] || (_cache[26] = createElementVNode("div", { class: "log-section-title" }, "错误信息", -1)), createElementVNode("pre", _hoisted_16, toDisplayString(row.errorMessage), 1)])) : createCommentVNode("", true)
											])]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "执行时间",
											prop: "createTime",
											width: "170"
										}),
										createVNode(_component_el_table_column, {
											label: "状态",
											width: "100"
										}, {
											default: withCtx(({ row }) => [createVNode(_component_el_tag, {
												type: row.status === "SUCCESS" ? "success" : "danger",
												size: "small"
											}, {
												default: withCtx(() => [createTextVNode(toDisplayString(row.status), 1)]),
												_: 2
											}, 1032, ["type"])]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "耗时",
											width: "100"
										}, {
											default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDuration(row.durationMs)), 1)]),
											_: 1
										}),
										createVNode(_component_el_table_column, {
											label: "操作人",
											prop: "operator",
											width: "120",
											"show-overflow-tooltip": ""
										}),
										createVNode(_component_el_table_column, {
											label: "执行ID",
											prop: "executionId",
											"min-width": "200",
											"show-overflow-tooltip": ""
										}),
										createVNode(_component_el_table_column, {
											label: "错误信息",
											prop: "errorMessage",
											"min-width": "200",
											"show-overflow-tooltip": ""
										})
									]),
									_: 1
								}, 8, ["data", "expand-row-keys"])), [[_directive_loading, historyLoading.value]])]),
								_: 1
							})]),
							_: 1
						}, 8, ["modelValue"])) : createCommentVNode("", true)
					];
				}),
				_: 1
			}, 8, ["modelValue", "title"])]);
		};
	}
}), [["__scopeId", "data-v-0706949c"]]);
//#endregion
export { trigger_list_default as default };

//# sourceMappingURL=trigger-list-mm3CXs5X.js.map