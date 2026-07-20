import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { Fragment, computed, createCommentVNode, createElementBlock, createElementVNode, defineComponent, nextTick, normalizeClass, normalizeStyle, onBeforeUnmount, openBlock, ref, renderList, toDisplayString, watch } from "vue";
//#region src/components/ExpressionEditor/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "expression-editor" };
var _hoisted_2 = { class: "ee-editor" };
var _hoisted_3 = ["value"];
var _hoisted_4 = { class: "ee-sidebar" };
var _hoisted_5 = { class: "ee-section" };
var _hoisted_6 = { class: "ee-chips" };
var _hoisted_7 = ["title", "onClick"];
var _hoisted_8 = {
	key: 0,
	class: "ee-empty"
};
var _hoisted_9 = { class: "ee-section" };
var _hoisted_10 = { class: "ee-chips" };
var _hoisted_11 = ["title", "onClick"];
var _hoisted_12 = {
	key: 0,
	class: "ee-empty"
};
var _hoisted_13 = { class: "ee-functions" };
var _hoisted_14 = { class: "ee-fn-group-label" };
var _hoisted_15 = ["title", "onClick"];
var _hoisted_16 = {
	key: 0,
	class: "ee-status-text"
};
var _hoisted_17 = {
	key: 1,
	class: "ee-status-text"
};
//#endregion
//#region src/components/ExpressionEditor/index.vue
var ExpressionEditor_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "ExpressionEditor",
	__name: "index",
	props: {
		modelValue: {},
		language: { default: "aviator" },
		variables: { default: () => [] },
		fields: { default: () => [] },
		height: { default: 200 }
	},
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		/**
		* 表达式编辑器组件（借鉴 Budibase Bindings Drawer）。
		*
		* <p>基于 &lt;textarea&gt; + 叠加 &lt;pre&gt; 实现简易语法高亮，不引入 monaco/codemirror
		* 等重量级编辑器，避免打包体积过大。布局：</p>
		* <ul>
		*   <li>左侧：表达式文本框（透明文字 + 下方 pre 着色，光标可编辑）</li>
		*   <li>右侧：变量/字段侧栏（点击插入到光标位置）</li>
		*   <li>底部：函数库提示（math / string / date，点击插入）</li>
		*   <li>最底部：语法校验状态栏（实时检查括号 / 引号 / 运算符，debounce 300ms）</li>
		* </ul>
		*
		* <p>插入变量格式：Aviator 用 <code>${变量名}</code>，Groovy/JavaScript 用 <code>变量名</code>。
		* 通过 v-model 双向绑定 modelValue。</p>
		*/
		/** 表达式语言类型 */
		const props = __props;
		const emit = __emit;
		const text = ref(props.modelValue || "");
		watch(() => props.modelValue, (v) => {
			if (v !== text.value) text.value = v !== null && v !== void 0 ? v : "";
		});
		const textareaRef = ref(null);
		const preRef = ref(null);
		function onInput(e) {
			const ta = e.target;
			text.value = ta.value;
			emit("update:modelValue", ta.value);
		}
		/** 同步 pre 高亮层滚动位置，保证与 textarea 对齐 */
		function onScroll(e) {
			const ta = e.target;
			if (preRef.value) {
				preRef.value.scrollTop = ta.scrollTop;
				preRef.value.scrollLeft = ta.scrollLeft;
			}
		}
		/** 变量插入文本：aviator 用 ${name}，其余用裸名 */
		function insertTextFor(name) {
			return props.language === "aviator" ? `\${${name}}` : name;
		}
		/**
		* 在 textarea 光标处插入文本。textarea 失焦后 selectionStart/End 仍保留上次位置，
		* 故点击侧栏 chip 时可直接读取，无需额外缓存。插入后焦点回到 textarea 并定位光标。
		*/
		function insertAtCursor(insertText) {
			var _ta$selectionStart, _ta$selectionEnd;
			const ta = textareaRef.value;
			if (!ta) {
				const next = text.value + insertText;
				text.value = next;
				emit("update:modelValue", next);
				return;
			}
			const start = (_ta$selectionStart = ta.selectionStart) !== null && _ta$selectionStart !== void 0 ? _ta$selectionStart : 0;
			const end = (_ta$selectionEnd = ta.selectionEnd) !== null && _ta$selectionEnd !== void 0 ? _ta$selectionEnd : 0;
			const before = text.value.slice(0, start);
			const after = text.value.slice(end);
			const next = before + insertText + after;
			text.value = next;
			emit("update:modelValue", next);
			nextTick(() => {
				ta.focus();
				const pos = start + insertText.length;
				ta.setSelectionRange(pos, pos);
			});
		}
		/** 变量名提取：字符串取本身，对象取 name */
		function varName(v) {
			return typeof v === "string" ? v : v.name;
		}
		/** 变量标签：字符串显示本身，对象显示 name (type) */
		function varLabel(v) {
			if (typeof v === "string") return v;
			return v.type ? `${v.name} (${v.type})` : v.name;
		}
		function onVarClick(v) {
			insertAtCursor(insertTextFor(varName(v)));
		}
		function onFieldClick(f) {
			insertAtCursor(insertTextFor(f.name));
		}
		const FUNCTION_LIBRARY = [
			{
				key: "math",
				label: "math",
				items: [
					{
						label: "abs(x)",
						insertText: "abs(x)",
						description: "绝对值"
					},
					{
						label: "ceil(x)",
						insertText: "ceil(x)",
						description: "向上取整"
					},
					{
						label: "floor(x)",
						insertText: "floor(x)",
						description: "向下取整"
					}
				]
			},
			{
				key: "string",
				label: "string",
				items: [{
					label: "length(s)",
					insertText: "length(s)",
					description: "字符串长度"
				}, {
					label: "substring(s, start, end)",
					insertText: "substring(s, start, end)",
					description: "截取子串"
				}]
			},
			{
				key: "date",
				label: "date",
				items: [{
					label: "now()",
					insertText: "now()",
					description: "当前时间戳"
				}, {
					label: "format(date, pattern)",
					insertText: "format(date, \"yyyy-MM-dd\")",
					description: "按指定格式格式化日期"
				}]
			}
		];
		function onFuncClick(item) {
			insertAtCursor(item.insertText);
		}
		function isIdentStart(ch) {
			return ch >= "a" && ch <= "z" || ch >= "A" && ch <= "Z" || ch === "_";
		}
		function isIdentPart(ch) {
			return isIdentStart(ch) || ch >= "0" && ch <= "9";
		}
		function isDigit(ch) {
			return ch >= "0" && ch <= "9";
		}
		function isSpace(ch) {
			return ch === " " || ch === "	" || ch === "\n" || ch === "\r";
		}
		/**
		* 简易词法分析：单趟扫描生成 token 列表，避免正则叠加导致的嵌套匹配问题。
		* 识别：aviator ${var}、字符串、数字、函数调用（标识符后跟 ( ）、普通标识符、其他字符。
		*/
		function tokenize(code, lang) {
			const tokens = [];
			const n = code.length;
			let i = 0;
			while (i < n) {
				const ch = code[i];
				if (lang === "aviator" && ch === "$" && code[i + 1] === "{") {
					const end = code.indexOf("}", i + 2);
					const stop = end === -1 ? n : end + 1;
					tokens.push({
						text: code.slice(i, stop),
						cls: "ee-tok-var"
					});
					i = stop;
					continue;
				}
				if (ch === "\"" || ch === "'") {
					let j = i + 1;
					while (j < n && code[j] !== ch) {
						if (code[j] === "\\") j++;
						j++;
					}
					j = Math.min(j + 1, n);
					tokens.push({
						text: code.slice(i, j),
						cls: "ee-tok-str"
					});
					i = j;
					continue;
				}
				if (isDigit(ch)) {
					let j = i;
					while (j < n && (isDigit(code[j]) || code[j] === ".")) j++;
					tokens.push({
						text: code.slice(i, j),
						cls: "ee-tok-num"
					});
					i = j;
					continue;
				}
				if (isIdentStart(ch)) {
					let j = i;
					while (j < n && isIdentPart(code[j])) j++;
					const word = code.slice(i, j);
					let k = j;
					while (k < n && isSpace(code[k])) k++;
					const isFn = code[k] === "(";
					tokens.push({
						text: word,
						cls: isFn ? "ee-tok-fn" : "ee-tok-id"
					});
					i = j;
					continue;
				}
				tokens.push({
					text: ch,
					cls: "ee-tok-plain"
				});
				i++;
			}
			return tokens;
		}
		const tokens = computed(() => tokenize(text.value, props.language));
		/** Aviator 合法运算符集合（含多字符运算符） */
		const AVIATOR_OPERATORS = /* @__PURE__ */ new Set([
			"+",
			"-",
			"*",
			"/",
			"%",
			">",
			"<",
			"==",
			"!=",
			">=",
			"<=",
			"&&",
			"||",
			"!",
			"?",
			":"
		]);
		/**
		* 移除字符串字面量后的表达式（用于结构校验，避免字符串内字符被误判）。
		*
		* <p>对 `"..."` / `'...'` 进行扫描，跳过转义字符；未闭合的字符串保留起始引号
		* 占位以便后续校验识别为「未闭合」。</p>
		*/
		function stripStrings(expr) {
			let out = "";
			let i = 0;
			const n = expr.length;
			while (i < n) {
				const ch = expr[i];
				if (ch === "\"" || ch === "'") {
					let j = i + 1;
					while (j < n && expr[j] !== ch) {
						if (expr[j] === "\\") j++;
						j++;
					}
					if (j >= n) {
						out += ch;
						return {
							stripped: out,
							unclosedQuote: ch
						};
					}
					out += " ";
					i = j + 1;
					continue;
				}
				out += ch;
				i++;
			}
			return {
				stripped: out,
				unclosedQuote: null
			};
		}
		/** 校验括号匹配：返回首个不匹配的括号信息（null 表示匹配） */
		function checkBrackets(s) {
			const stack = [];
			const pairs = {
				")": "(",
				"]": "[",
				"}": "{"
			};
			const opens = /* @__PURE__ */ new Set([
				"(",
				"[",
				"{"
			]);
			for (let i = 0; i < s.length; i++) {
				const ch = s[i];
				if (opens.has(ch)) stack.push({
					ch,
					pos: i
				});
				else if (ch in pairs) {
					const top = stack.pop();
					if (!top || top.ch !== pairs[ch]) return {
						char: ch,
						pos: i,
						kind: "mismatch"
					};
				}
			}
			if (stack.length > 0) return {
				char: stack[stack.length - 1].ch,
				pos: stack[stack.length - 1].pos,
				kind: "unclosed"
			};
			return null;
		}
		/** Aviator 校验：括号、${} 闭合、运算符合法、字符串引号闭合 */
		function validateAviator(expr) {
			const { stripped, unclosedQuote } = stripStrings(expr);
			if (unclosedQuote) return {
				valid: false,
				error: `字符串引号 ${unclosedQuote} 未闭合`
			};
			let i = 0;
			while (i < stripped.length) {
				if (stripped[i] === "$") {
					if (stripped[i + 1] !== "{") return {
						valid: false,
						error: `位置 ${i + 1}：'$' 后必须跟 '{'`
					};
					const end = stripped.indexOf("}", i + 2);
					if (end === -1) return {
						valid: false,
						error: `位置 ${i + 1}：'\${' 未闭合 '}'`
					};
					i = end + 1;
					continue;
				}
				if (stripped[i] === "{" || stripped[i] === "}") return {
					valid: false,
					error: `位置 ${i + 1}：Aviator 中 '{}' 仅用于 '\${变量名}'`
				};
				i++;
			}
			const bracketErr = checkBrackets(stripped.replace(/[{}]/g, " "));
			if (bracketErr) {
				if (bracketErr.kind === "unclosed") return {
					valid: false,
					error: `括号 '${bracketErr.char}' 未闭合`
				};
				return {
					valid: false,
					error: `位置 ${bracketErr.pos + 1}：括号 '${bracketErr.char}' 不匹配`
				};
			}
			const opChars = /* @__PURE__ */ new Set([
				"+",
				"-",
				"*",
				"/",
				"%",
				">",
				"<",
				"=",
				"!",
				"&",
				"|",
				"?",
				":"
			]);
			let j = 0;
			while (j < stripped.length) {
				const ch = stripped[j];
				if (!opChars.has(ch)) {
					j++;
					continue;
				}
				const two = stripped.slice(j, j + 2);
				if (AVIATOR_OPERATORS.has(two)) {
					j += 2;
					continue;
				}
				if (AVIATOR_OPERATORS.has(ch)) {
					j += 1;
					continue;
				}
				return {
					valid: false,
					error: `位置 ${j + 1}：非法运算符 '${ch}'（Aviator 支持 + - * / % > < == != >= <= && || ! ? :）`
				};
			}
			return { valid: true };
		}
		/** Groovy 校验：括号、字符串引号闭合、连续运算符（** 除外） */
		function validateGroovy(expr) {
			const { stripped, unclosedQuote } = stripStrings(expr);
			if (unclosedQuote) return {
				valid: false,
				error: `字符串引号 ${unclosedQuote} 未闭合`
			};
			const bracketErr = checkBrackets(stripped);
			if (bracketErr) {
				if (bracketErr.kind === "unclosed") return {
					valid: false,
					error: `括号 '${bracketErr.char}' 未闭合`
				};
				return {
					valid: false,
					error: `位置 ${bracketErr.pos + 1}：括号 '${bracketErr.char}' 不匹配`
				};
			}
			const opChars = /* @__PURE__ */ new Set([
				"+",
				"-",
				"*",
				"/",
				"%",
				">",
				"<",
				"=",
				"!",
				"&",
				"|",
				"?",
				":",
				"@",
				"~",
				"^"
			]);
			let k = 0;
			while (k < stripped.length) {
				const ch = stripped[k];
				if (!opChars.has(ch)) {
					k++;
					continue;
				}
				let run = 1;
				while (k + run < stripped.length && stripped[k + run] === ch) run++;
				if (run >= 3) return {
					valid: false,
					error: `位置 ${k + 1}：连续运算符 '${ch.repeat(run)}' 不合法`
				};
				k += run;
			}
			return { valid: true };
		}
		/** JavaScript 校验：用 new Function(expr) try-catch（仅语法解析，不执行） */
		function validateJavaScript(expr) {
			try {
				new Function(`return (${expr});`);
				return { valid: true };
			} catch (e) {
				return {
					valid: false,
					error: `JavaScript 语法错误：${e instanceof Error ? e.message : String(e)}`
				};
			}
		}
		/**
		* 表达式语法校验入口。
		*
		* <p>按语言分发：aviator/groovy 走简化本地校验（括号/引号/运算符），
		* javascript 用 `new Function()` try-catch（仅解析不执行）。
		* 空表达式视为合法（无需校验）。</p>
		*
		* @param expr     表达式字符串
		* @param language 语言：aviator / groovy / javascript
		* @returns 校验结果
		*/
		function validateExpression(expr, language) {
			if (!expr || !expr.trim()) return { valid: true };
			if (language === "aviator") return validateAviator(expr);
			if (language === "groovy") return validateGroovy(expr);
			return validateJavaScript(expr);
		}
		const isValid = ref(true);
		const errorMessage = ref("");
		let validateTimer = null;
		function runValidation() {
			var _result$error;
			const result = validateExpression(text.value, props.language);
			isValid.value = result.valid;
			errorMessage.value = (_result$error = result.error) !== null && _result$error !== void 0 ? _result$error : "";
		}
		watch(() => text.value, () => {
			if (validateTimer) clearTimeout(validateTimer);
			validateTimer = setTimeout(runValidation, 300);
		});
		watch(() => props.language, () => {
			if (validateTimer) clearTimeout(validateTimer);
			runValidation();
		});
		onBeforeUnmount(() => {
			if (validateTimer) clearTimeout(validateTimer);
		});
		runValidation();
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createElementVNode("div", {
					class: "ee-main",
					style: normalizeStyle({ height: `${__props.height}px` })
				}, [createElementVNode("div", _hoisted_2, [createElementVNode("pre", {
					ref_key: "preRef",
					ref: preRef,
					class: "ee-highlight",
					"aria-hidden": "true"
				}, [(openBlock(true), createElementBlock(Fragment, null, renderList(tokens.value, (t, i) => {
					return openBlock(), createElementBlock("span", {
						key: i,
						class: normalizeClass(t.cls)
					}, toDisplayString(t.text), 3);
				}), 128))], 512), createElementVNode("textarea", {
					ref_key: "textareaRef",
					ref: textareaRef,
					class: "ee-textarea",
					value: text.value,
					placeholder: "输入表达式，点击右侧变量/字段或底部函数插入",
					spellcheck: "false",
					onInput,
					onScroll
				}, null, 40, _hoisted_3)]), createElementVNode("div", _hoisted_4, [createElementVNode("div", _hoisted_5, [_cache[0] || (_cache[0] = createElementVNode("div", { class: "ee-section-title" }, "变量", -1)), createElementVNode("div", _hoisted_6, [(openBlock(true), createElementBlock(Fragment, null, renderList(__props.variables, (v, i) => {
					return openBlock(), createElementBlock("span", {
						key: "v" + i,
						class: "ee-chip",
						title: `插入 ${varName(v)}`,
						onClick: ($event) => onVarClick(v)
					}, toDisplayString(varLabel(v)), 9, _hoisted_7);
				}), 128)), __props.variables.length === 0 ? (openBlock(), createElementBlock("span", _hoisted_8, "无可用变量")) : createCommentVNode("", true)])]), createElementVNode("div", _hoisted_9, [_cache[1] || (_cache[1] = createElementVNode("div", { class: "ee-section-title" }, "字段", -1)), createElementVNode("div", _hoisted_10, [(openBlock(true), createElementBlock(Fragment, null, renderList(__props.fields, (f, i) => {
					return openBlock(), createElementBlock("span", {
						key: "f" + i,
						class: "ee-chip",
						title: `插入 ${f.name}`,
						onClick: ($event) => onFieldClick(f)
					}, toDisplayString(f.type ? `${f.name} (${f.type})` : f.name), 9, _hoisted_11);
				}), 128)), __props.fields.length === 0 ? (openBlock(), createElementBlock("span", _hoisted_12, "无可用字段")) : createCommentVNode("", true)])])])], 4),
				createElementVNode("div", _hoisted_13, [(openBlock(), createElementBlock(Fragment, null, renderList(FUNCTION_LIBRARY, (g) => {
					return createElementVNode("div", {
						key: g.key,
						class: "ee-fn-group"
					}, [createElementVNode("span", _hoisted_14, toDisplayString(g.label) + ":", 1), (openBlock(true), createElementBlock(Fragment, null, renderList(g.items, (item) => {
						return openBlock(), createElementBlock("span", {
							key: item.label,
							class: "ee-fn-chip",
							title: item.description,
							onClick: ($event) => onFuncClick(item)
						}, toDisplayString(item.label), 9, _hoisted_15);
					}), 128))]);
				}), 64))]),
				createElementVNode("div", {
					class: normalizeClass(["ee-status", {
						"ee-status-valid": isValid.value,
						"ee-status-invalid": !isValid.value
					}]),
					role: "status",
					"aria-live": "polite"
				}, [isValid.value ? (openBlock(), createElementBlock("span", _hoisted_16, "✓ 语法正确")) : (openBlock(), createElementBlock("span", _hoisted_17, "✗ " + toDisplayString(errorMessage.value), 1))], 2)
			]);
		};
	}
}), [["__scopeId", "data-v-ceecc3cb"]]);
//#endregion
export { ExpressionEditor_default as t };

//# sourceMappingURL=ExpressionEditor-CFxBT6yN.js.map