import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { t as ExpressionEditor_default } from "./ExpressionEditor-CFxBT6yN.js";
import { a as publishRuleWithVersion, i as getRuleVersions, n as executeRule, o as rollbackRule, r as getRuleList, s as saveRule, t as deleteRule } from "./lowcode-rule-BZ8USwM5.js";
import { t as JsonTreeDiff_default } from "./JsonTreeDiff-CvcFM07K.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, normalizeClass, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, vShow, watch, withCtx, withDirectives } from "vue";
//#region src/components/RuleDesigner/DecisionTableEditor.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$3 = { class: "decision-table-editor" };
var _hoisted_2$3 = { class: "dte-toolbar" };
var _hoisted_3$3 = { class: "dte-table-wrap" };
var _hoisted_4$3 = {
	key: 0,
	class: "dte-table"
};
var _hoisted_5$3 = ["colspan"];
var _hoisted_6$3 = {
	key: 1,
	class: "dte-divider",
	rowspan: "2"
};
var _hoisted_7$2 = ["colspan"];
var _hoisted_8$1 = { class: "dte-col-head-inner" };
var _hoisted_9$1 = { class: "dte-col-head-inner" };
var _hoisted_10$1 = { class: "dte-row-head" };
var _hoisted_11$1 = { class: "dte-row-idx" };
var _hoisted_12$1 = { class: "dte-row-ops" };
var _hoisted_13$1 = { key: 0 };
var _hoisted_14$1 = ["colspan"];
var _hoisted_15$1 = {
	key: 1,
	class: "dte-placeholder"
};
//#endregion
//#region src/components/RuleDesigner/DecisionTableEditor.vue
var DecisionTableEditor_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "DecisionTableEditor",
	__name: "DecisionTableEditor",
	props: { modelValue: {} },
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		/**
		* 决策表可视化编辑器（借鉴 Appian Decision Designer）。
		*
		* <p>以表格形式编辑决策表：左侧条件列（字段 + 操作符 + 值），右侧动作列（字段 + 值），
		* 中间分隔线。支持 Hit Policy 切换、增删条件/动作列、增删/上下移动规则行，
		* 单元格直接可编辑，Tab 键自然在单元格间切换。</p>
		*
		* <p>definition 采用新结构化格式：
		* <pre>
		* {hitPolicy, conditionColumns:[{field, operator}], actionColumns:[{field}],
		*  rows:[{conditions:[{value}], actions:[{value}]}]}
		* </pre>
		* 加载时若检测到旧格式（rows 内联 conditions/actions）会自动转换，保证向后兼容。</p>
		*/
		/** 有效的操作符集合，CSV 表头解析时校验 */
		const VALID_OPERATORS = /* @__PURE__ */ new Set([
			"EQ",
			"NE",
			"GT",
			"GE",
			"LT",
			"LE",
			"IN"
		]);
		/** CSV 文件输入引用 */
		const csvFileInput = ref(null);
		const props = __props;
		const emit = __emit;
		/** 操作符选项 */
		const operatorOptions = [
			{
				label: "EQ 等于",
				value: "EQ"
			},
			{
				label: "NE 不等于",
				value: "NE"
			},
			{
				label: "GT 大于",
				value: "GT"
			},
			{
				label: "GE 大于等于",
				value: "GE"
			},
			{
				label: "LT 小于",
				value: "LT"
			},
			{
				label: "LE 小于等于",
				value: "LE"
			},
			{
				label: "IN 包含于",
				value: "IN"
			}
		];
		/** 命中策略选项 */
		const hitPolicyOptions = [
			{
				label: "FIRST 匹配首行",
				value: "FIRST"
			},
			{
				label: "ALL 匹配全部",
				value: "ALL"
			},
			{
				label: "COLLECT 收集全部",
				value: "COLLECT"
			}
		];
		/** 内部可编辑结构 */
		const table = reactive({
			hitPolicy: "FIRST",
			conditionColumns: [],
			actionColumns: [],
			rows: []
		});
		/** 标记是否正在从外部同步，避免回写触发循环 */
		let syncing = false;
		/**
		* 将 definition 字符串解析为结构化对象。
		* 兼容新格式（含 conditionColumns/hitPolicy）与旧格式（rows 内联 conditions/actions）。
		*/
		function parseDefinition(raw) {
			const result = {
				hitPolicy: "FIRST",
				conditionColumns: [],
				actionColumns: [],
				rows: []
			};
			if (!raw || !raw.trim()) return result;
			let obj;
			try {
				obj = JSON.parse(raw);
			} catch (_unused) {
				return result;
			}
			if (obj.hitPolicy || obj.conditionColumns) {
				result.hitPolicy = obj.hitPolicy || "FIRST";
				result.conditionColumns = (obj.conditionColumns || []).map((c) => ({
					field: c.field || "",
					operator: c.operator || "EQ"
				}));
				result.actionColumns = (obj.actionColumns || []).map((a) => ({ field: a.field || "" }));
				result.rows = (obj.rows || []).map((r) => ({
					conditions: normalizeCells(r.conditions, result.conditionColumns.length),
					actions: normalizeCells(r.actions, result.actionColumns.length)
				}));
				return result;
			}
			const rows = obj.rows || [];
			if (rows.length === 0) return result;
			result.conditionColumns = (rows[0].conditions || []).map((c) => ({
				field: c.field || "",
				operator: c.operator || "EQ"
			}));
			const firstActions = rows[0].actions || {};
			result.actionColumns = Object.keys(firstActions).map((field) => ({ field }));
			result.rows = rows.map((r) => {
				const conds = r.conditions || [];
				const acts = r.actions || {};
				return {
					conditions: result.conditionColumns.map((_, i) => {
						var _conds$i$value, _conds$i;
						return { value: (_conds$i$value = (_conds$i = conds[i]) === null || _conds$i === void 0 ? void 0 : _conds$i.value) !== null && _conds$i$value !== void 0 ? _conds$i$value : "" };
					}),
					actions: result.actionColumns.map((c) => {
						var _acts$c$field;
						return { value: (_acts$c$field = acts[c.field]) !== null && _acts$c$field !== void 0 ? _acts$c$field : "" };
					})
				};
			});
			return result;
		}
		/** 规范化单元格数组，保证长度与列数一致 */
		function normalizeCells(cells, len) {
			const arr = (cells || []).slice(0, len);
			while (arr.length < len) arr.push({ value: "" });
			return arr;
		}
		/** 同步外部字符串到内部结构 */
		function syncFromModel(raw) {
			syncing = true;
			const parsed = parseDefinition(raw);
			table.hitPolicy = parsed.hitPolicy;
			table.conditionColumns.splice(0, table.conditionColumns.length, ...parsed.conditionColumns);
			table.actionColumns.splice(0, table.actionColumns.length, ...parsed.actionColumns);
			table.rows.splice(0, table.rows.length, ...parsed.rows);
			syncing = false;
		}
		watch(() => props.modelValue, (v) => {
			if (syncing) return;
			syncFromModel(v);
		}, { immediate: true });
		/** 单元格值 → 显示文本 */
		function toText(v) {
			if (v === null || v === void 0) return "";
			if (typeof v === "string") return v;
			return JSON.stringify(v);
		}
		/** 显示文本 → 存储值（尝试 JSON 解析以保留数字/布尔/数组类型） */
		function fromText(text) {
			const s = text.trim();
			if (s === "") return "";
			try {
				return JSON.parse(s);
			} catch (_unused2) {
				return text;
			}
		}
		/** 序列化内部结构为 definition JSON 字符串并回写 */
		function emitChange() {
			if (syncing) return;
			const payload = {
				hitPolicy: table.hitPolicy,
				conditionColumns: table.conditionColumns.map((c) => ({
					field: c.field,
					operator: c.operator
				})),
				actionColumns: table.actionColumns.map((a) => ({ field: a.field })),
				rows: table.rows.map((r) => ({
					conditions: r.conditions.map((c) => ({ value: c.value })),
					actions: r.actions.map((a) => ({ value: a.value }))
				}))
			};
			emit("update:modelValue", JSON.stringify(payload, null, 2));
		}
		/** 深度监听内部结构变化，自动回写 */
		watch(table, emitChange, { deep: true });
		function addConditionColumn() {
			table.conditionColumns.push({
				field: "",
				operator: "EQ"
			});
			table.rows.forEach((r) => r.conditions.push({ value: "" }));
			emitChange();
		}
		function addActionColumn() {
			table.actionColumns.push({ field: "" });
			table.rows.forEach((r) => r.actions.push({ value: "" }));
			emitChange();
		}
		function removeConditionColumn(idx) {
			table.conditionColumns.splice(idx, 1);
			table.rows.forEach((r) => r.conditions.splice(idx, 1));
			emitChange();
		}
		function removeActionColumn(idx) {
			table.actionColumns.splice(idx, 1);
			table.rows.forEach((r) => r.actions.splice(idx, 1));
			emitChange();
		}
		function addRow() {
			table.rows.push({
				conditions: table.conditionColumns.map(() => ({ value: "" })),
				actions: table.actionColumns.map(() => ({ value: "" }))
			});
			emitChange();
		}
		function removeRow(idx) {
			table.rows.splice(idx, 1);
			emitChange();
		}
		function moveRow(idx, delta) {
			const target = idx + delta;
			if (target < 0 || target >= table.rows.length) return;
			const tmp = table.rows[idx];
			table.rows[idx] = table.rows[target];
			table.rows[target] = tmp;
			emitChange();
		}
		/**
		* 单元格值序列化为 CSV 文本：
		* - 数字/布尔/字符串原样输出；
		* - 对象/数组用 JSON 字符串表示；
		* - 含逗号、双引号或换行的值用双引号包裹，内部双引号转义为两个双引号。
		*/
		function toCsvCell(v) {
			let text;
			if (v === null || v === void 0) text = "";
			else if (typeof v === "string") text = v;
			else try {
				text = JSON.stringify(v);
			} catch (_unused3) {
				text = String(v);
			}
			if (/[",\r\n]/.test(text)) return "\"" + text.replace(/"/g, "\"\"") + "\"";
			return text;
		}
		/** 将决策表结构序列化为 CSV 字符串（含表头行 + 数据行） */
		function buildCsv() {
			const headerCells = [];
			table.conditionColumns.forEach((c) => {
				headerCells.push(toCsvCell(`条件:${c.field || ""}(${c.operator || "EQ"})`));
			});
			table.actionColumns.forEach((a) => {
				headerCells.push(toCsvCell(`动作:${a.field || ""}`));
			});
			const lines = [headerCells.join(",")];
			table.rows.forEach((row) => {
				const cells = [];
				row.conditions.forEach((c) => cells.push(toCsvCell(c.value)));
				row.actions.forEach((a) => cells.push(toCsvCell(a.value)));
				lines.push(cells.join(","));
			});
			return lines.join("\r\n") + "\r\n";
		}
		/** 解析一行 CSV 文本为单元格数组，支持双引号转义 */
		function parseCsvLine(line) {
			const cells = [];
			let i = 0;
			const len = line.length;
			while (i <= len) {
				if (i === len) {
					cells.push("");
					break;
				}
				if (line[i] === "\"") {
					let buf = "";
					i++;
					while (i < len) if (line[i] === "\"") if (i + 1 < len && line[i + 1] === "\"") {
						buf += "\"";
						i += 2;
					} else {
						i++;
						break;
					}
					else {
						buf += line[i];
						i++;
					}
					cells.push(buf);
					while (i < len && line[i] !== ",") i++;
					if (i < len && line[i] === ",") i++;
					else if (i >= len) break;
				} else {
					let j = i;
					while (j < len && line[j] !== ",") j++;
					cells.push(line.slice(i, j));
					i = j;
					if (i < len && line[i] === ",") i++;
					else break;
				}
			}
			return cells;
		}
		/**
		* 将 CSV 文本拆分为行数组，处理引号内的换行（CRLF/LF）。
		* 仅在引号未闭合时跨行合并。
		*/
		function splitCsvRows(text) {
			const normalized = text.replace(/\r\n/g, "\n").replace(/\r/g, "\n");
			const rows = [];
			let buf = "";
			let inQuotes = false;
			for (let i = 0; i < normalized.length; i++) {
				const ch = normalized[i];
				if (ch === "\"") if (inQuotes && normalized[i + 1] === "\"") {
					buf += "\"\"";
					i++;
				} else {
					inQuotes = !inQuotes;
					buf += ch;
				}
				else if (ch === "\n" && !inQuotes) {
					rows.push(buf);
					buf = "";
				} else buf += ch;
			}
			if (buf.length > 0) rows.push(buf);
			while (rows.length > 0 && rows[rows.length - 1].trim() === "") rows.pop();
			return rows;
		}
		/** 尝试把字符串解析为强类型值（数字/布尔/JSON），失败则返回原字符串 */
		function parseCellText(text) {
			const s = text.trim();
			if (s === "") return "";
			try {
				return JSON.parse(s);
			} catch (_unused4) {
				return text;
			}
		}
		/**
		* 解析 CSV 文本为决策表结构。
		* 表头格式：`条件:字段名(操作符)` 或 `动作:字段名`，不区分的列视为动作列。
		* 解析失败抛出 Error，由调用方捕获并提示。
		*/
		function parseCsv(text) {
			const rows = splitCsvRows(text);
			if (rows.length === 0) throw new Error("CSV 内容为空");
			const headerCells = parseCsvLine(rows[0]).map((c) => c.trim());
			const conditionColumns = [];
			const actionColumns = [];
			const colTypes = [];
			headerCells.forEach((h) => {
				const condMatch = h.match(/^条件\s*:\s*(.*?)\s*\(\s*([A-Za-z]+)\s*\)\s*$/);
				if (condMatch) {
					const op = condMatch[2].toUpperCase();
					conditionColumns.push({
						field: condMatch[1] || "",
						operator: VALID_OPERATORS.has(op) ? op : "EQ"
					});
					colTypes.push("cond");
					return;
				}
				const actMatch = h.match(/^动作\s*:\s*(.*)$/);
				if (actMatch) actionColumns.push({ field: actMatch[1] || "" });
				else actionColumns.push({ field: h });
				colTypes.push("act");
			});
			const dataRows = rows.slice(1).map((line) => {
				const cells = parseCsvLine(line);
				const conditions = [];
				const actions = [];
				colTypes.forEach((t, idx) => {
					var _cells$idx;
					const value = parseCellText((_cells$idx = cells[idx]) !== null && _cells$idx !== void 0 ? _cells$idx : "");
					if (t === "cond") conditions.push({ value });
					else actions.push({ value });
				});
				while (conditions.length < conditionColumns.length) conditions.push({ value: "" });
				while (actions.length < actionColumns.length) actions.push({ value: "" });
				return {
					conditions,
					actions
				};
			});
			return {
				hitPolicy: table.hitPolicy,
				conditionColumns,
				actionColumns,
				rows: dataRows
			};
		}
		/** 触发 CSV 下载（保留当前 hitPolicy） */
		function exportCsv() {
			if (table.conditionColumns.length === 0 && table.actionColumns.length === 0) {
				ElMessage.warning("当前决策表无列，无法导出");
				return;
			}
			const csv = buildCsv();
			const blob = new Blob(["﻿" + csv], { type: "text/csv;charset=utf-8;" });
			const url = URL.createObjectURL(blob);
			const a = document.createElement("a");
			a.href = url;
			a.download = `decision-table-${Date.now()}.csv`;
			document.body.appendChild(a);
			a.click();
			document.body.removeChild(a);
			URL.revokeObjectURL(url);
		}
		/** 触发文件选择器 */
		function onImportCsvClick() {
			var _csvFileInput$value;
			(_csvFileInput$value = csvFileInput.value) === null || _csvFileInput$value === void 0 || _csvFileInput$value.click();
		}
		/** 读取 CSV 文件并解析回填到决策表 */
		async function onImportCsvFile(e) {
			var _target$files;
			const target = e.target;
			const file = (_target$files = target.files) === null || _target$files === void 0 ? void 0 : _target$files[0];
			if (!file) return;
			try {
				const parsed = parseCsv(await file.text());
				syncing = true;
				table.conditionColumns.splice(0, table.conditionColumns.length, ...parsed.conditionColumns);
				table.actionColumns.splice(0, table.actionColumns.length, ...parsed.actionColumns);
				table.rows.splice(0, table.rows.length, ...parsed.rows);
				syncing = false;
				emitChange();
				ElMessage.success(`已导入 ${parsed.rows.length} 行规则`);
			} catch (err) {
				ElMessage.error("CSV 解析失败：" + (err instanceof Error ? err.message : String(err)));
			} finally {
				target.value = "";
			}
		}
		return (_ctx, _cache) => {
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_input = resolveComponent("el-input");
			return openBlock(), createElementBlock("div", _hoisted_1$3, [
				createElementVNode("div", _hoisted_2$3, [
					_cache[6] || (_cache[6] = createElementVNode("span", { class: "dte-toolbar-label" }, "命中策略", -1)),
					createVNode(_component_el_select, {
						modelValue: table.hitPolicy,
						"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => table.hitPolicy = $event),
						size: "small",
						style: { "width": "180px" }
					}, {
						default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(hitPolicyOptions, (o) => {
							return createVNode(_component_el_option, {
								key: o.value,
								label: o.label,
								value: o.value
							}, null, 8, ["label", "value"]);
						}), 64))]),
						_: 1
					}, 8, ["modelValue"]),
					createVNode(_component_el_button, {
						size: "small",
						onClick: addConditionColumn
					}, {
						default: withCtx(() => [..._cache[1] || (_cache[1] = [createTextVNode("+ 条件列", -1)])]),
						_: 1
					}),
					createVNode(_component_el_button, {
						size: "small",
						onClick: addActionColumn
					}, {
						default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("+ 动作列", -1)])]),
						_: 1
					}),
					createVNode(_component_el_button, {
						size: "small",
						type: "primary",
						onClick: addRow
					}, {
						default: withCtx(() => [..._cache[3] || (_cache[3] = [createTextVNode("+ 规则行", -1)])]),
						_: 1
					}),
					createVNode(_component_el_button, {
						size: "small",
						onClick: onImportCsvClick
					}, {
						default: withCtx(() => [..._cache[4] || (_cache[4] = [createTextVNode("导入 CSV", -1)])]),
						_: 1
					}),
					createVNode(_component_el_button, {
						size: "small",
						onClick: exportCsv
					}, {
						default: withCtx(() => [..._cache[5] || (_cache[5] = [createTextVNode("导出 CSV", -1)])]),
						_: 1
					}),
					createElementVNode("input", {
						ref_key: "csvFileInput",
						ref: csvFileInput,
						type: "file",
						accept: ".csv,text/csv",
						style: { "display": "none" },
						onChange: onImportCsvFile
					}, null, 544)
				]),
				createElementVNode("div", _hoisted_3$3, [table.conditionColumns.length || table.actionColumns.length ? (openBlock(), createElementBlock("table", _hoisted_4$3, [createElementVNode("thead", null, [createElementVNode("tr", null, [
					_cache[7] || (_cache[7] = createElementVNode("th", {
						rowspan: "2",
						class: "dte-row-head"
					}, "#", -1)),
					table.conditionColumns.length ? (openBlock(), createElementBlock("th", {
						key: 0,
						colspan: table.conditionColumns.length,
						class: "dte-group dte-group-cond"
					}, " 条件 (Conditions) ", 8, _hoisted_5$3)) : createCommentVNode("", true),
					table.conditionColumns.length ? (openBlock(), createElementBlock("th", _hoisted_6$3)) : createCommentVNode("", true),
					table.actionColumns.length ? (openBlock(), createElementBlock("th", {
						key: 2,
						colspan: table.actionColumns.length,
						class: "dte-group dte-group-act"
					}, " 动作 (Actions) ", 8, _hoisted_7$2)) : createCommentVNode("", true)
				]), createElementVNode("tr", null, [(openBlock(true), createElementBlock(Fragment, null, renderList(table.conditionColumns, (c, ci) => {
					return openBlock(), createElementBlock("th", {
						key: "c" + ci,
						class: "dte-col-head dte-col-cond"
					}, [createElementVNode("div", _hoisted_8$1, [
						createVNode(_component_el_input, {
							modelValue: c.field,
							"onUpdate:modelValue": ($event) => c.field = $event,
							size: "small",
							placeholder: "字段名"
						}, null, 8, ["modelValue", "onUpdate:modelValue"]),
						createVNode(_component_el_select, {
							modelValue: c.operator,
							"onUpdate:modelValue": ($event) => c.operator = $event,
							size: "small",
							class: "dte-op-select"
						}, {
							default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(operatorOptions, (o) => {
								return createVNode(_component_el_option, {
									key: o.value,
									label: o.value,
									value: o.value
								}, null, 8, ["label", "value"]);
							}), 64))]),
							_: 1
						}, 8, ["modelValue", "onUpdate:modelValue"]),
						createVNode(_component_el_button, {
							size: "small",
							link: "",
							class: "dte-col-del",
							title: "删除列",
							onClick: ($event) => removeConditionColumn(ci)
						}, {
							default: withCtx(() => [..._cache[8] || (_cache[8] = [createTextVNode("✕", -1)])]),
							_: 1
						}, 8, ["onClick"])
					])]);
				}), 128)), (openBlock(true), createElementBlock(Fragment, null, renderList(table.actionColumns, (a, ai) => {
					return openBlock(), createElementBlock("th", {
						key: "a" + ai,
						class: "dte-col-head dte-col-act"
					}, [createElementVNode("div", _hoisted_9$1, [createVNode(_component_el_input, {
						modelValue: a.field,
						"onUpdate:modelValue": ($event) => a.field = $event,
						size: "small",
						placeholder: "字段名"
					}, null, 8, ["modelValue", "onUpdate:modelValue"]), createVNode(_component_el_button, {
						size: "small",
						link: "",
						class: "dte-col-del",
						title: "删除列",
						onClick: ($event) => removeActionColumn(ai)
					}, {
						default: withCtx(() => [..._cache[9] || (_cache[9] = [createTextVNode("✕", -1)])]),
						_: 1
					}, 8, ["onClick"])])]);
				}), 128))])]), createElementVNode("tbody", null, [(openBlock(true), createElementBlock(Fragment, null, renderList(table.rows, (row, ri) => {
					return openBlock(), createElementBlock("tr", { key: "r" + ri }, [
						createElementVNode("td", _hoisted_10$1, [createElementVNode("span", _hoisted_11$1, toDisplayString(ri + 1), 1), createElementVNode("div", _hoisted_12$1, [
							createVNode(_component_el_button, {
								size: "small",
								link: "",
								disabled: ri === 0,
								title: "上移",
								onClick: ($event) => moveRow(ri, -1)
							}, {
								default: withCtx(() => [..._cache[10] || (_cache[10] = [createTextVNode("▲", -1)])]),
								_: 1
							}, 8, ["disabled", "onClick"]),
							createVNode(_component_el_button, {
								size: "small",
								link: "",
								disabled: ri === table.rows.length - 1,
								title: "下移",
								onClick: ($event) => moveRow(ri, 1)
							}, {
								default: withCtx(() => [..._cache[11] || (_cache[11] = [createTextVNode("▼", -1)])]),
								_: 1
							}, 8, ["disabled", "onClick"]),
							createVNode(_component_el_button, {
								size: "small",
								link: "",
								class: "dte-row-del",
								title: "删除行",
								onClick: ($event) => removeRow(ri)
							}, {
								default: withCtx(() => [..._cache[12] || (_cache[12] = [createTextVNode("✕", -1)])]),
								_: 1
							}, 8, ["onClick"])
						])]),
						(openBlock(true), createElementBlock(Fragment, null, renderList(table.conditionColumns, (_, ci) => {
							var _row$conditions$ci;
							return openBlock(), createElementBlock("td", {
								key: "rc" + ci,
								class: "dte-cell"
							}, [createVNode(_component_el_input, {
								"model-value": toText((_row$conditions$ci = row.conditions[ci]) === null || _row$conditions$ci === void 0 ? void 0 : _row$conditions$ci.value),
								size: "small",
								placeholder: "值",
								"onUpdate:modelValue": (v) => row.conditions[ci].value = fromText(v)
							}, null, 8, ["model-value", "onUpdate:modelValue"])]);
						}), 128)),
						_cache[13] || (_cache[13] = createElementVNode("td", { class: "dte-divider-cell" }, null, -1)),
						(openBlock(true), createElementBlock(Fragment, null, renderList(table.actionColumns, (_, ai) => {
							var _row$actions$ai;
							return openBlock(), createElementBlock("td", {
								key: "ra" + ai,
								class: "dte-cell"
							}, [createVNode(_component_el_input, {
								"model-value": toText((_row$actions$ai = row.actions[ai]) === null || _row$actions$ai === void 0 ? void 0 : _row$actions$ai.value),
								size: "small",
								placeholder: "值",
								"onUpdate:modelValue": (v) => row.actions[ai].value = fromText(v)
							}, null, 8, ["model-value", "onUpdate:modelValue"])]);
						}), 128))
					]);
				}), 128)), table.rows.length === 0 ? (openBlock(), createElementBlock("tr", _hoisted_13$1, [createElementVNode("td", {
					colspan: table.conditionColumns.length + table.actionColumns.length + 2,
					class: "dte-empty"
				}, " 暂无规则行，点击「+ 规则行」添加 ", 8, _hoisted_14$1)])) : createCommentVNode("", true)])])) : (openBlock(), createElementBlock("div", _hoisted_15$1, [_cache[16] || (_cache[16] = createElementVNode("p", null, "暂无列，请先添加条件列或动作列", -1)), createElementVNode("div", null, [createVNode(_component_el_button, {
					size: "small",
					onClick: addConditionColumn
				}, {
					default: withCtx(() => [..._cache[14] || (_cache[14] = [createTextVNode("+ 条件列", -1)])]),
					_: 1
				}), createVNode(_component_el_button, {
					size: "small",
					onClick: addActionColumn
				}, {
					default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode("+ 动作列", -1)])]),
					_: 1
				})])]))]),
				_cache[17] || (_cache[17] = createElementVNode("div", { class: "dte-tips" }, [
					createElementVNode("span", null, "提示："),
					createElementVNode("span", null, "条件值支持数字(18)、字符串(CN)、布尔(true)、数组(IN 操作符用 [1,2,3])；"),
					createElementVNode("span", null, "Tab 键可在单元格间切换。")
				], -1))
			]);
		};
	}
}), [["__scopeId", "data-v-18db9eb7"]]);
//#endregion
//#region src/components/RuleDesigner/ExpressionRuleEditor.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$2 = { class: "expression-rule-editor" };
var _hoisted_2$2 = { class: "ere-main" };
var _hoisted_3$2 = { class: "ere-editor" };
var _hoisted_4$2 = { class: "ere-schema" };
var _hoisted_5$2 = { class: "ere-schema-header" };
var _hoisted_6$2 = { class: "ere-schema-list" };
var _hoisted_7$1 = {
	key: 0,
	class: "ere-schema-empty"
};
//#endregion
//#region src/components/RuleDesigner/ExpressionRuleEditor.vue
var ExpressionRuleEditor_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "ExpressionRuleEditor",
	__name: "ExpressionRuleEditor",
	props: {
		modelValue: {},
		ext: {}
	},
	emits: ["update:modelValue", "update:ext"],
	setup(__props, { emit: __emit }) {
		/**
		* 表达式规则编辑器（包装 ExpressionEditor + facts schema 维护侧栏）。
		*
		* <p>主体复用批次1的 ExpressionEditor（language=aviator，带语法高亮 + 变量/函数补全），
		* 右侧额外提供 inputsSchema 维护面板：用户手动维护输入字段（name/type），
		* 这些字段会作为变量传入 ExpressionEditor 的侧栏供点击插入，并持久化到 rule 的 ext 字段。</p>
		*
		* <p>双向绑定：</p>
		* <ul>
		*   <li>modelValue：Aviator 表达式字符串（与 definition 一致，后端直接求值）</li>
		*   <li>ext：ExpressionExt JSON 字符串（含 inputsSchema）</li>
		* </ul>
		*/
		const props = __props;
		const emit = __emit;
		/** 表达式文本（v-model 中转） */
		const expression = ref(props.modelValue || "");
		watch(() => props.modelValue, (v) => {
			if (v !== expression.value) expression.value = v !== null && v !== void 0 ? v : "";
		});
		function onExprInput(v) {
			expression.value = v;
			emit("update:modelValue", v);
		}
		/** 解析 ext 为 ExpressionExt，容错处理 */
		function parseExt(raw) {
			if (!raw || !raw.trim()) return { inputsSchema: [] };
			try {
				const obj = JSON.parse(raw);
				return { inputsSchema: Array.isArray(obj.inputsSchema) ? obj.inputsSchema : [] };
			} catch (_unused) {
				return { inputsSchema: [] };
			}
		}
		const inputsSchema = ref(parseExt(props.ext).inputsSchema);
		watch(() => props.ext, (v) => {
			const parsed = parseExt(v);
			if (JSON.stringify(parsed.inputsSchema) !== JSON.stringify(inputsSchema.value)) inputsSchema.value = parsed.inputsSchema;
		});
		/** 同步 inputsSchema 到 ext 字符串 */
		function syncExt() {
			const payload = { inputsSchema: inputsSchema.value.map((s) => ({ ...s })) };
			emit("update:ext", JSON.stringify(payload, null, 2));
		}
		function addField() {
			inputsSchema.value.push({
				name: "",
				type: "string"
			});
			syncExt();
		}
		function removeField(idx) {
			inputsSchema.value.splice(idx, 1);
			syncExt();
		}
		function onFieldChange() {
			syncExt();
		}
		/** 传给 ExpressionEditor 的变量列表（{name, type} 形式，支持类型显示） */
		const variables = computed(() => inputsSchema.value.filter((s) => s.name).map((s) => ({
			name: s.name,
			type: s.type
		})));
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			return openBlock(), createElementBlock("div", _hoisted_1$2, [createElementVNode("div", _hoisted_2$2, [createElementVNode("div", _hoisted_3$2, [createVNode(ExpressionEditor_default, {
				"model-value": expression.value,
				language: "aviator",
				variables: variables.value,
				height: 320,
				"onUpdate:modelValue": onExprInput
			}, null, 8, ["model-value", "variables"])]), createElementVNode("div", _hoisted_4$2, [
				createElementVNode("div", _hoisted_5$2, [_cache[1] || (_cache[1] = createElementVNode("span", { class: "ere-schema-title" }, "输入变量 (Facts Schema)", -1)), createVNode(_component_el_button, {
					size: "small",
					type: "primary",
					link: "",
					onClick: addField
				}, {
					default: withCtx(() => [..._cache[0] || (_cache[0] = [createTextVNode("+ 添加", -1)])]),
					_: 1
				})]),
				_cache[3] || (_cache[3] = createElementVNode("div", { class: "ere-schema-tip" }, " 手动维护可用变量，点击编辑器变量区可插入到表达式（Aviator 用 ${name}）。 ", -1)),
				createElementVNode("div", _hoisted_6$2, [(openBlock(true), createElementBlock(Fragment, null, renderList(inputsSchema.value, (f, idx) => {
					return openBlock(), createElementBlock("div", {
						key: idx,
						class: "ere-schema-row"
					}, [
						createVNode(_component_el_input, {
							modelValue: f.name,
							"onUpdate:modelValue": ($event) => f.name = $event,
							size: "small",
							placeholder: "变量名",
							onInput: onFieldChange
						}, null, 8, ["modelValue", "onUpdate:modelValue"]),
						createVNode(_component_el_select, {
							modelValue: f.type,
							"onUpdate:modelValue": ($event) => f.type = $event,
							size: "small",
							class: "ere-type-select",
							onChange: onFieldChange
						}, {
							default: withCtx(() => [
								createVNode(_component_el_option, {
									label: "string",
									value: "string"
								}),
								createVNode(_component_el_option, {
									label: "number",
									value: "number"
								}),
								createVNode(_component_el_option, {
									label: "boolean",
									value: "boolean"
								}),
								createVNode(_component_el_option, {
									label: "date",
									value: "date"
								}),
								createVNode(_component_el_option, {
									label: "object",
									value: "object"
								})
							]),
							_: 1
						}, 8, ["modelValue", "onUpdate:modelValue"]),
						createVNode(_component_el_button, {
							size: "small",
							link: "",
							class: "ere-row-del",
							title: "删除",
							onClick: ($event) => removeField(idx)
						}, {
							default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("✕", -1)])]),
							_: 1
						}, 8, ["onClick"])
					]);
				}), 128)), inputsSchema.value.length === 0 ? (openBlock(), createElementBlock("div", _hoisted_7$1, " 暂无变量，点击「+ 添加」 ")) : createCommentVNode("", true)])
			])])]);
		};
	}
}), [["__scopeId", "data-v-d2a17c9d"]]);
//#endregion
//#region src/components/RuleDesigner/RuleTestPanel.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$1 = { class: "rtp-title" };
var _hoisted_2$1 = { class: "rtp-body" };
var _hoisted_3$1 = { class: "rtp-input-row" };
var _hoisted_4$1 = { class: "rtp-actions" };
var _hoisted_5$1 = {
	key: 0,
	class: "rtp-hint"
};
var _hoisted_6$1 = {
	key: 0,
	class: "rtp-result rtp-error"
};
var _hoisted_7 = {
	key: 1,
	class: "rtp-result"
};
var _hoisted_8 = { class: "rtp-result-label" };
var _hoisted_9 = { key: 0 };
var _hoisted_10 = { key: 1 };
var _hoisted_11 = {
	key: 2,
	class: "rtp-history"
};
var _hoisted_12 = { class: "rtp-history-title" };
var _hoisted_13 = ["onClick"];
var _hoisted_14 = { class: "rtp-history-time" };
var _hoisted_15 = { class: "rtp-history-facts" };
//#endregion
//#region src/components/RuleDesigner/RuleTestPanel.vue
var RuleTestPanel_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "RuleTestPanel",
	__name: "RuleTestPanel",
	props: {
		ruleCode: {},
		ruleType: {},
		inputsSchema: {},
		defaultExpanded: { type: Boolean }
	},
	setup(__props) {
		/**
		* 规则测试面板（可折叠）。
		*
		* <p>提供事实输入（JSON 文本框）、执行按钮、结果展示与最近 5 次执行历史（可重放）。
		* 决策表返回 hitActions 列表（result.actions），表达式返回求值结果（result.result）。</p>
		*/
		const props = __props;
		const collapsed = ref(!props.defaultExpanded);
		function toggle() {
			collapsed.value = !collapsed.value;
		}
		const factsText = ref("");
		/** 依据 inputsSchema 生成 facts 模板，便于快速填入测试输入 */
		function fillTemplate() {
			const schema = props.inputsSchema || [];
			if (schema.length === 0) {
				factsText.value = "{\n  \n}";
				return;
			}
			const tpl = {};
			schema.forEach((s) => {
				if (!s.name) return;
				switch (s.type) {
					case "number":
						tpl[s.name] = "0";
						break;
					case "boolean":
						tpl[s.name] = "false";
						break;
					default: tpl[s.name] = "";
				}
			});
			factsText.value = JSON.stringify(tpl, null, 2);
		}
		const history = ref([]);
		const loading = ref(false);
		const currentResult = ref(null);
		const currentError = ref("");
		/** 决策表命中动作列表（结果展示用） */
		const hitActions = computed(() => {
			if (props.ruleType !== "DECISION_TABLE") return [];
			const r = currentResult.value;
			return (r === null || r === void 0 ? void 0 : r.actions) || [];
		});
		/** 表达式求值结果（结果展示用） */
		const exprResult = computed(() => {
			if (props.ruleType === "DECISION_TABLE") return null;
			const r = currentResult.value;
			return r === null || r === void 0 ? void 0 : r.result;
		});
		function pretty(v) {
			if (v === null || v === void 0) return "";
			if (typeof v === "string") return v;
			try {
				return JSON.stringify(v, null, 2);
			} catch (_unused) {
				return String(v);
			}
		}
		async function execute() {
			if (!props.ruleCode) {
				ElMessage.warning("请先填写规则编码");
				return;
			}
			let facts = {};
			if (factsText.value.trim()) try {
				facts = JSON.parse(factsText.value);
			} catch (_unused2) {
				ElMessage.error("输入事实 JSON 解析失败");
				return;
			}
			loading.value = true;
			currentError.value = "";
			const entry = {
				time: (/* @__PURE__ */ new Date()).toLocaleTimeString("zh-CN", { hour12: false }),
				facts: factsText.value,
				result: null
			};
			try {
				const result = await executeRule(props.ruleCode, facts);
				currentResult.value = result;
				entry.result = result;
			} catch (e) {
				const msg = e instanceof Error ? e.message : String(e);
				currentError.value = msg;
				entry.error = msg;
			} finally {
				loading.value = false;
				history.value.unshift(entry);
				if (history.value.length > 5) history.value.length = 5;
			}
		}
		/** 重放历史：将该次 facts 填回输入框 */
		function replay(entry) {
			factsText.value = entry.facts;
		}
		return (_ctx, _cache) => {
			const _component_Cpu = resolveComponent("Cpu");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_ArrowDown = resolveComponent("ArrowDown");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_tag = resolveComponent("el-tag");
			return openBlock(), createElementBlock("div", { class: normalizeClass(["rule-test-panel", { collapsed: collapsed.value }]) }, [createElementVNode("div", {
				class: "rtp-header",
				onClick: toggle
			}, [createElementVNode("span", _hoisted_1$1, [createVNode(_component_el_icon, null, {
				default: withCtx(() => [createVNode(_component_Cpu)]),
				_: 1
			}), _cache[1] || (_cache[1] = createTextVNode(" 测试面板 ", -1))]), createVNode(_component_el_icon, { class: normalizeClass(["rtp-collapse-icon", { rotated: collapsed.value }]) }, {
				default: withCtx(() => [createVNode(_component_ArrowDown)]),
				_: 1
			}, 8, ["class"])]), withDirectives(createElementVNode("div", _hoisted_2$1, [
				createElementVNode("div", _hoisted_3$1, [_cache[3] || (_cache[3] = createElementVNode("span", { class: "rtp-label" }, "输入事实 (Facts JSON)", -1)), createVNode(_component_el_button, {
					size: "small",
					link: "",
					onClick: fillTemplate
				}, {
					default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("生成模板", -1)])]),
					_: 1
				})]),
				createVNode(_component_el_input, {
					modelValue: factsText.value,
					"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => factsText.value = $event),
					type: "textarea",
					rows: 4,
					placeholder: "{\"field\":\"value\"}",
					class: "rtp-facts"
				}, null, 8, ["modelValue"]),
				createElementVNode("div", _hoisted_4$1, [createVNode(_component_el_button, {
					size: "small",
					type: "primary",
					loading: loading.value,
					disabled: !__props.ruleCode,
					onClick: execute
				}, {
					default: withCtx(() => [..._cache[4] || (_cache[4] = [createTextVNode(" 执行 ", -1)])]),
					_: 1
				}, 8, ["loading", "disabled"]), !__props.ruleCode ? (openBlock(), createElementBlock("span", _hoisted_5$1, "请先填写规则编码")) : createCommentVNode("", true)]),
				currentError.value ? (openBlock(), createElementBlock("div", _hoisted_6$1, [_cache[5] || (_cache[5] = createElementVNode("div", { class: "rtp-result-label" }, "执行错误", -1)), createElementVNode("pre", null, toDisplayString(currentError.value), 1)])) : currentResult.value !== null ? (openBlock(), createElementBlock("div", _hoisted_7, [createElementVNode("div", _hoisted_8, toDisplayString(__props.ruleType === "DECISION_TABLE" ? `命中动作 (${hitActions.value.length})` : "求值结果"), 1), __props.ruleType === "DECISION_TABLE" ? (openBlock(), createElementBlock("pre", _hoisted_9, toDisplayString(pretty(hitActions.value)), 1)) : (openBlock(), createElementBlock("pre", _hoisted_10, toDisplayString(pretty(exprResult.value)), 1))])) : createCommentVNode("", true),
				history.value.length ? (openBlock(), createElementBlock("div", _hoisted_11, [createElementVNode("div", _hoisted_12, "最近 " + toDisplayString(history.value.length) + " 次执行（点击重放）", 1), (openBlock(true), createElementBlock(Fragment, null, renderList(history.value, (h, i) => {
					return openBlock(), createElementBlock("div", {
						key: i,
						class: normalizeClass(["rtp-history-item", { "has-error": !!h.error }]),
						onClick: ($event) => replay(h)
					}, [
						createElementVNode("span", _hoisted_14, toDisplayString(h.time), 1),
						createVNode(_component_el_tag, {
							size: "small",
							type: h.error ? "danger" : "success"
						}, {
							default: withCtx(() => [createTextVNode(toDisplayString(h.error ? "失败" : "成功"), 1)]),
							_: 2
						}, 1032, ["type"]),
						createElementVNode("span", _hoisted_15, toDisplayString(h.facts || "(空)"), 1)
					], 10, _hoisted_13);
				}), 128))])) : createCommentVNode("", true)
			], 512), [[vShow, !collapsed.value]])], 2);
		};
	}
}), [["__scopeId", "data-v-0be92d83"]]);
//#endregion
//#region src/views/lowcode/rule-designer/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { style: { "padding": "16px" } };
var _hoisted_2 = { style: {
	"display": "flex",
	"justify-content": "space-between",
	"align-items": "center"
} };
var _hoisted_3 = {
	key: 0,
	class: "rule-editor"
};
var _hoisted_4 = { class: "rule-meta-row" };
var _hoisted_5 = { class: "rule-editor-section" };
var _hoisted_6 = {
	key: 2,
	class: "rule-liteflow-placeholder"
};
//#endregion
//#region src/views/lowcode/rule-designer/index.vue
var rule_designer_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "RuleDesignerView",
	__name: "index",
	setup(__props) {
		/**
		* 低代码规则设计器（可视化）。
		*
		* <p>列表页保留 el-table（code/name/type/status/操作）。新建/编辑打开可视化对话框：
		* 顶部规则元信息（type 选中后不可更改），中部按类型分发可视化编辑器——
		* 决策表编辑器 / 表达式编辑器 / LiteFlow 文本编辑，底部可折叠测试面板。</p>
		*/
		const list = ref([]);
		const current = ref(null);
		const dialogVisible = ref(false);
		/** 列表「执行」入口打开时自动展开测试面板 */
		const testExpanded = ref(false);
		/** 版本历史对话框 */
		const versionDialogVisible = ref(false);
		/** 版本历史加载中 */
		const versionLoading = ref(false);
		/** 版本历史列表 */
		const versionList = ref([]);
		/** 版本历史对应的规则（用于 Diff 取当前定义、回滚后刷新） */
		const versionRule = ref(null);
		/** Diff 对话框 */
		const diffDialogVisible = ref(false);
		/** Diff 旧数据（历史版本快照） */
		const diffOldData = ref(null);
		/** Diff 新数据（当前规则定义） */
		const diffNewData = ref(null);
		/** Diff 标题（标注对比版本） */
		const diffTitle = ref("");
		const typeOptions = [
			{
				label: "决策表",
				value: "DECISION_TABLE"
			},
			{
				label: "表达式",
				value: "EXPRESSION"
			},
			{
				label: "LiteFlow",
				value: "LITEFLOW"
			}
		];
		const statusOptions = [{
			label: "草稿",
			value: "DRAFT"
		}, {
			label: "已发布",
			value: "PUBLISHED"
		}];
		async function load() {
			list.value = await getRuleList();
		}
		function openNew() {
			current.value = {
				code: "",
				name: "",
				description: "",
				type: "EXPRESSION",
				definition: "",
				ext: "",
				status: "DRAFT"
			};
			testExpanded.value = false;
			dialogVisible.value = true;
		}
		function openEdit(row) {
			current.value = { ...row };
			testExpanded.value = false;
			dialogVisible.value = true;
		}
		/** 列表「执行」入口：打开编辑对话框并自动展开测试面板 */
		function openExec(row) {
			current.value = { ...row };
			testExpanded.value = true;
			dialogVisible.value = true;
		}
		/**
		* 类型切换（仅新建时可切换）。切换时清空 definition 与 ext，避免不同类型格式串扰。
		* 已有 id 的规则（编辑态）类型锁定，不会触发本方法。
		*/
		function onTypeChange(t) {
			if (!current.value) return;
			current.value.type = t;
			current.value.definition = "";
			if (t !== "EXPRESSION") current.value.ext = "";
		}
		/** 当前规则是否处于编辑态（已有 id，类型锁定） */
		const isEdit = computed(() => {
			var _current$value;
			return !!((_current$value = current.value) === null || _current$value === void 0 ? void 0 : _current$value.id);
		});
		/** 表达式规则的 inputsSchema（从 ext 解析，传给测试面板生成模板） */
		const testInputsSchema = computed(() => {
			if (!current.value || current.value.type !== "EXPRESSION" || !current.value.ext) return void 0;
			try {
				const obj = JSON.parse(current.value.ext);
				return Array.isArray(obj.inputsSchema) ? obj.inputsSchema : void 0;
			} catch (_unused) {
				return;
			}
		});
		async function save() {
			if (!current.value) return;
			if (!current.value.code.trim()) {
				ElMessage.warning("请填写规则编码");
				return;
			}
			if (!current.value.name.trim()) {
				ElMessage.warning("请填写规则名称");
				return;
			}
			await saveRule(current.value);
			ElMessage.success("保存成功");
			dialogVisible.value = false;
			await load();
		}
		async function remove(row) {
			if (!row.id) return;
			try {
				await ElMessageBox.confirm(`确认删除规则「${row.name}」？`, "确认", { type: "warning" });
				await deleteRule(row.id);
				ElMessage.success("删除成功");
				await load();
			} catch (_unused2) {}
		}
		/** 发布规则并生成版本快照 */
		async function publish(row) {
			if (!row.id) return;
			try {
				await ElMessageBox.confirm(`确认发布规则「${row.name}」？发布后将生成不可变版本快照。`, "规则发布", { type: "warning" });
				await publishRuleWithVersion(row.id);
				ElMessage.success("发布成功，已生成版本快照");
				await load();
			} catch (_unused3) {}
		}
		/** 打开版本历史对话框 */
		async function openVersionHistory(row) {
			if (!row.id) return;
			versionRule.value = { ...row };
			versionDialogVisible.value = true;
			versionLoading.value = true;
			try {
				versionList.value = await getRuleVersions(row.id);
			} catch (_unused4) {
				ElMessage.error("加载版本历史失败");
				versionList.value = [];
			} finally {
				versionLoading.value = false;
			}
		}
		/** 解析快照/定义为可 Diff 数据：JSON 优先，否则返回原始字符串 */
		function parseForDiff(raw) {
			if (!raw) return null;
			try {
				return JSON.parse(raw);
			} catch (_unused5) {
				return raw;
			}
		}
		/** 查看指定历史版本与当前定义的 Diff */
		function showVersionDiff(version) {
			var _versionRule$value;
			diffOldData.value = parseForDiff(version.snapshot);
			diffNewData.value = parseForDiff((_versionRule$value = versionRule.value) === null || _versionRule$value === void 0 ? void 0 : _versionRule$value.definition);
			diffTitle.value = `版本 v${version.version} 与当前定义对比`;
			diffDialogVisible.value = true;
		}
		/** 回滚到指定历史版本 */
		async function onRollbackVersion(version) {
			var _versionRule$value2;
			if (!((_versionRule$value2 = versionRule.value) === null || _versionRule$value2 === void 0 ? void 0 : _versionRule$value2.id)) return;
			try {
				await ElMessageBox.confirm(`确认回滚到版本 v${version.version}？当前规则定义将被历史快照覆盖，并生成新的回滚版本。`, "版本回滚", { type: "warning" });
				await rollbackRule(versionRule.value.id, version.version);
				ElMessage.success("回滚成功");
				await openVersionHistory(await refreshRule(versionRule.value.id));
			} catch (_unused6) {}
		}
		/** 重新拉取单条规则，回滚后用于刷新 versionRule 与列表 */
		async function refreshRule(id) {
			await load();
			const fresh = list.value.find((r) => r.id === id);
			return fresh !== null && fresh !== void 0 ? fresh : versionRule.value;
		}
		onMounted(load);
		return (_ctx, _cache) => {
			var _versionRule$value$na, _versionRule$value3;
			const _component_el_button = resolveComponent("el-button");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_alert = resolveComponent("el-alert");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _component_el_empty = resolveComponent("el-empty");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, { shadow: "never" }, {
					header: withCtx(() => [createElementVNode("div", _hoisted_2, [_cache[16] || (_cache[16] = createElementVNode("span", null, "规则设计器", -1)), createVNode(_component_el_button, {
						type: "primary",
						onClick: openNew
					}, {
						default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode("新建规则", -1)])]),
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
								width: "140"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, null, {
									default: withCtx(() => [createTextVNode(toDisplayString(row.type), 1)]),
									_: 2
								}, 1024)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "状态",
								prop: "status",
								width: "100"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, { type: row.status === "PUBLISHED" ? "success" : "info" }, {
									default: withCtx(() => [createTextVNode(toDisplayString(row.status), 1)]),
									_: 2
								}, 1032, ["type"])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "操作",
								width: "380"
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
										onClick: ($event) => openExec(row)
									}, {
										default: withCtx(() => [..._cache[18] || (_cache[18] = [createTextVNode("执行", -1)])]),
										_: 1
									}, 8, ["onClick"]),
									createVNode(_component_el_button, {
										size: "small",
										type: "primary",
										onClick: ($event) => publish(row)
									}, {
										default: withCtx(() => [..._cache[19] || (_cache[19] = [createTextVNode("发布", -1)])]),
										_: 1
									}, 8, ["onClick"]),
									createVNode(_component_el_button, {
										size: "small",
										onClick: ($event) => openVersionHistory(row)
									}, {
										default: withCtx(() => [..._cache[20] || (_cache[20] = [createTextVNode("版本历史", -1)])]),
										_: 1
									}, 8, ["onClick"]),
									createVNode(_component_el_button, {
										size: "small",
										type: "danger",
										onClick: ($event) => remove(row)
									}, {
										default: withCtx(() => [..._cache[21] || (_cache[21] = [createTextVNode("删除", -1)])]),
										_: 1
									}, 8, ["onClick"])
								]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["data"])]),
					_: 1
				}),
				createVNode(_component_el_dialog, {
					modelValue: dialogVisible.value,
					"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => dialogVisible.value = $event),
					title: "规则设计器",
					width: "1100px",
					top: "5vh",
					"close-on-click-modal": false
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[9] || (_cache[9] = ($event) => dialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[22] || (_cache[22] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						onClick: save
					}, {
						default: withCtx(() => [..._cache[23] || (_cache[23] = [createTextVNode("保存", -1)])]),
						_: 1
					})]),
					default: withCtx(() => [current.value ? (openBlock(), createElementBlock("div", _hoisted_3, [
						createVNode(_component_el_form, {
							"label-width": "80px",
							class: "rule-meta-form"
						}, {
							default: withCtx(() => [createElementVNode("div", _hoisted_4, [
								createVNode(_component_el_form_item, {
									label: "编码",
									class: "rule-meta-item"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: current.value.code,
										"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => current.value.code = $event),
										disabled: isEdit.value,
										placeholder: "唯一编码"
									}, null, 8, ["modelValue", "disabled"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, {
									label: "名称",
									class: "rule-meta-item"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: current.value.name,
										"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => current.value.name = $event),
										placeholder: "规则名称"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, {
									label: "类型",
									class: "rule-meta-item rule-meta-type"
								}, {
									default: withCtx(() => [createVNode(_component_el_select, {
										"model-value": current.value.type,
										disabled: isEdit.value,
										placeholder: "选择类型",
										"onUpdate:modelValue": _cache[2] || (_cache[2] = (v) => onTypeChange(v))
									}, {
										default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(typeOptions, (o) => {
											return createVNode(_component_el_option, {
												key: o.value,
												label: o.label,
												value: o.value
											}, null, 8, ["label", "value"]);
										}), 64))]),
										_: 1
									}, 8, ["model-value", "disabled"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, {
									label: "状态",
									class: "rule-meta-item rule-meta-status"
								}, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: current.value.status,
										"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => current.value.status = $event)
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
							]), createVNode(_component_el_form_item, { label: "描述" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: current.value.description,
									"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => current.value.description = $event),
									placeholder: "规则描述（可选）"
								}, null, 8, ["modelValue"])]),
								_: 1
							})]),
							_: 1
						}),
						createElementVNode("div", _hoisted_5, [current.value.type === "DECISION_TABLE" ? (openBlock(), createBlock(DecisionTableEditor_default, {
							key: 0,
							modelValue: current.value.definition,
							"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => current.value.definition = $event)
						}, null, 8, ["modelValue"])) : current.value.type === "EXPRESSION" ? (openBlock(), createBlock(ExpressionRuleEditor_default, {
							key: 1,
							modelValue: current.value.definition,
							"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => current.value.definition = $event),
							ext: current.value.ext,
							"onUpdate:ext": _cache[7] || (_cache[7] = ($event) => current.value.ext = $event)
						}, null, 8, ["modelValue", "ext"])) : (openBlock(), createElementBlock("div", _hoisted_6, [createVNode(_component_el_alert, {
							title: "LiteFlow EL 编辑",
							type: "info",
							closable: false,
							"show-icon": "",
							description: "后端通过 LiteFlow 2.15.0 执行 EL 表达式，组件内可通过 DefaultContext 读写上下文，执行结果取自 result 键。"
						}), createVNode(_component_el_input, {
							modelValue: current.value.definition,
							"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => current.value.definition = $event),
							type: "textarea",
							rows: 10,
							placeholder: "THEN(a, b, c)",
							class: "rule-liteflow-text"
						}, null, 8, ["modelValue"])]))]),
						createVNode(RuleTestPanel_default, {
							"rule-code": current.value.code,
							"rule-type": current.value.type,
							"inputs-schema": testInputsSchema.value,
							"default-expanded": testExpanded.value
						}, null, 8, [
							"rule-code",
							"rule-type",
							"inputs-schema",
							"default-expanded"
						])
					])) : createCommentVNode("", true)]),
					_: 1
				}, 8, ["modelValue"]),
				createVNode(_component_el_dialog, {
					modelValue: versionDialogVisible.value,
					"onUpdate:modelValue": _cache[12] || (_cache[12] = ($event) => versionDialogVisible.value = $event),
					title: `版本历史 — ${(_versionRule$value$na = (_versionRule$value3 = versionRule.value) === null || _versionRule$value3 === void 0 ? void 0 : _versionRule$value3.name) !== null && _versionRule$value$na !== void 0 ? _versionRule$value$na : ""}`,
					width: "900px",
					top: "8vh"
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[11] || (_cache[11] = ($event) => versionDialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[26] || (_cache[26] = [createTextVNode("关闭", -1)])]),
						_: 1
					})]),
					default: withCtx(() => [withDirectives((openBlock(), createBlock(_component_el_table, {
						data: versionList.value,
						"row-key": "version",
						size: "small"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_table_column, {
								label: "版本",
								prop: "version",
								width: "70"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, { size: "small" }, {
									default: withCtx(() => [createTextVNode("v" + toDisplayString(row.version), 1)]),
									_: 2
								}, 1024)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "变更说明",
								prop: "changeLog",
								"show-overflow-tooltip": ""
							}),
							createVNode(_component_el_table_column, {
								label: "操作人",
								prop: "createBy",
								width: "100"
							}),
							createVNode(_component_el_table_column, {
								label: "时间",
								prop: "createTime",
								width: "160"
							}, {
								default: withCtx(({ row }) => {
									var _row$createTime;
									return [createTextVNode(toDisplayString((_row$createTime = row.createTime) === null || _row$createTime === void 0 ? void 0 : _row$createTime.replace("T", " ").slice(0, 16)), 1)];
								}),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "操作",
								width: "160"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_button, {
									size: "small",
									link: "",
									type: "primary",
									onClick: ($event) => showVersionDiff(row)
								}, {
									default: withCtx(() => [..._cache[24] || (_cache[24] = [createTextVNode("查看 Diff", -1)])]),
									_: 1
								}, 8, ["onClick"]), createVNode(_component_el_button, {
									size: "small",
									link: "",
									type: "warning",
									onClick: ($event) => onRollbackVersion(row)
								}, {
									default: withCtx(() => [..._cache[25] || (_cache[25] = [createTextVNode("回滚", -1)])]),
									_: 1
								}, 8, ["onClick"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["data"])), [[_directive_loading, versionLoading.value]]), !versionLoading.value && versionList.value.length === 0 ? (openBlock(), createBlock(_component_el_empty, {
						key: 0,
						description: "暂无版本历史，发布规则后将生成版本快照",
						"image-size": 60
					})) : createCommentVNode("", true)]),
					_: 1
				}, 8, ["modelValue", "title"]),
				createVNode(_component_el_dialog, {
					modelValue: diffDialogVisible.value,
					"onUpdate:modelValue": _cache[14] || (_cache[14] = ($event) => diffDialogVisible.value = $event),
					title: diffTitle.value,
					width: "900px",
					top: "8vh"
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[13] || (_cache[13] = ($event) => diffDialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[27] || (_cache[27] = [createTextVNode("关闭", -1)])]),
						_: 1
					})]),
					default: withCtx(() => [createVNode(JsonTreeDiff_default, {
						"old-data": diffOldData.value,
						"new-data": diffNewData.value
					}, null, 8, ["old-data", "new-data"])]),
					_: 1
				}, 8, ["modelValue", "title"])
			]);
		};
	}
}), [["__scopeId", "data-v-fcfe54cd"]]);
//#endregion
export { rule_designer_default as default };

//# sourceMappingURL=rule-designer-C8Ey-kbZ.js.map