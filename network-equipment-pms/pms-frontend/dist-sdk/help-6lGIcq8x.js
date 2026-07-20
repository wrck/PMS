import { r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, normalizeClass, onMounted, openBlock, ref, renderList, resolveComponent, resolveDirective, toDisplayString, unref, withCtx, withDirectives } from "vue";
import { ArrowLeft, Search, View } from "@element-plus/icons-vue";
//#region src/api/help.ts
/** 公开获取启用的帮助内容列表（可按分类过滤） */
function listHelpContents(category) {
	return get("/api/system/help-content/list", category ? { category } : void 0);
}
/** 公开获取帮助内容详情 */
function getHelpContent(id) {
	return get(`/api/system/help-content/${id}`);
}
//#endregion
//#region src/views/help/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "help-page" };
var _hoisted_2 = { class: "help-sidebar" };
var _hoisted_3 = { class: "help-sidebar__menu" };
var _hoisted_4 = ["onClick"];
var _hoisted_5 = { class: "help-sidebar__item-label" };
var _hoisted_6 = { class: "help-sidebar__item-count" };
var _hoisted_7 = { class: "help-main" };
var _hoisted_8 = { class: "help-header" };
var _hoisted_9 = {
	key: 0,
	class: "help-list"
};
var _hoisted_10 = { class: "help-list__title" };
var _hoisted_11 = {
	key: 1,
	class: "help-list__items"
};
var _hoisted_12 = ["onClick"];
var _hoisted_13 = { class: "help-list__item-title" };
var _hoisted_14 = { class: "help-list__item-meta" };
var _hoisted_15 = {
	key: 0,
	class: "help-list__item-time"
};
var _hoisted_16 = {
	key: 1,
	class: "help-detail"
};
var _hoisted_17 = { class: "help-detail__title" };
var _hoisted_18 = { class: "help-detail__meta" };
var _hoisted_19 = { key: 0 };
var _hoisted_20 = ["innerHTML"];
//#endregion
//#region src/views/help/index.vue
var help_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const CATEGORIES = [
			{
				label: "快速开始",
				value: "QUICK_START",
				icon: "Rocket"
			},
			{
				label: "常见问题",
				value: "FAQ",
				icon: "QuestionFilled"
			},
			{
				label: "视频教程",
				value: "VIDEO",
				icon: "VideoCamera"
			},
			{
				label: "进阶技巧",
				value: "ADVANCED",
				icon: "MagicStick"
			}
		];
		const loading = ref(false);
		const allContents = ref([]);
		const selectedCategory = ref("QUICK_START");
		const selectedContent = ref(null);
		const keyword = ref("");
		/** 按分类 + 关键词过滤后的内容列表 */
		const filteredContents = computed(() => {
			let list = allContents.value.filter((c) => c.category === selectedCategory.value);
			if (keyword.value.trim()) {
				const kw = keyword.value.trim().toLowerCase();
				list = list.filter((c) => c.title.toLowerCase().includes(kw) || (c.content || "").toLowerCase().includes(kw));
			}
			return list;
		});
		/** 各分类下的内容数量（用于菜单 badge） */
		function countOf(category) {
			return allContents.value.filter((c) => c.category === category).length;
		}
		/** 当前选中的分类对象 */
		const currentCategoryMeta = computed(() => {
			var _CATEGORIES$find;
			return (_CATEGORIES$find = CATEGORIES.find((c) => c.value === selectedCategory.value)) !== null && _CATEGORIES$find !== void 0 ? _CATEGORIES$find : CATEGORIES[0];
		});
		async function loadContents() {
			loading.value = true;
			try {
				const list = await listHelpContents();
				allContents.value = list || [];
				if (filteredContents.value.length > 0) await selectContent(filteredContents.value[0]);
				else selectedContent.value = null;
			} catch (_unused) {
				ElMessage.error("加载帮助内容失败");
				allContents.value = [];
			} finally {
				loading.value = false;
			}
		}
		async function selectCategory(cat) {
			selectedCategory.value = cat;
			keyword.value = "";
			await nextTickSafe();
			if (filteredContents.value.length > 0) await selectContent(filteredContents.value[0]);
			else selectedContent.value = null;
		}
		async function selectContent(item) {
			if (item.id == null) {
				selectedContent.value = item;
				return;
			}
			try {
				const detail = await getHelpContent(item.id);
				selectedContent.value = detail;
			} catch (_unused2) {
				selectedContent.value = item;
			}
		}
		function backToList() {
			selectedContent.value = null;
		}
		/** 安全 nextTick（避免在 setup 顶层使用） */
		async function nextTickSafe() {
			await Promise.resolve();
		}
		/**
		* 极简 Markdown 渲染器（无外部依赖）。
		*
		* <p>支持：代码块、标题、表格、有序/无序列表、加粗、行内代码、
		* 段落、水平线。输出经过 HTML 转义的安全 HTML 字符串。</p>
		*
		* <p>本渲染器不追求完整 Markdown 规范，仅覆盖帮助文档实际使用的语法。</p>
		*/
		function escapeHtml(s) {
			return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#39;");
		}
		function renderInline(text) {
			let s = escapeHtml(text);
			s = s.replace(/`([^`]+)`/g, "<code class=\"md-inline-code\">$1</code>");
			s = s.replace(/\*\*([^*]+)\*\*/g, "<strong>$1</strong>");
			s = s.replace(/\[([^\]]+)\]\(([^)]+)\)/g, "<a href=\"$2\" target=\"_blank\" rel=\"noopener\">$1</a>");
			return s;
		}
		function renderMarkdown(md) {
			if (!md) return "";
			const lines = md.replace(/\r\n/g, "\n").split("\n");
			const html = [];
			let i = 0;
			let inUl = false;
			let inOl = false;
			let inTable = false;
			function closeLists() {
				if (inUl) {
					html.push("</ul>");
					inUl = false;
				}
				if (inOl) {
					html.push("</ol>");
					inOl = false;
				}
			}
			function closeTable() {
				if (inTable) {
					html.push("</tbody></table>");
					inTable = false;
				}
			}
			while (i < lines.length) {
				const line = lines[i];
				if (line.trim().startsWith("```")) {
					closeLists();
					closeTable();
					const lang = line.trim().slice(3);
					const buf = [];
					i++;
					while (i < lines.length && !lines[i].trim().startsWith("```")) {
						buf.push(lines[i]);
						i++;
					}
					i++;
					html.push(`<pre class="md-code-block${lang ? ` md-code-block--${lang}` : ""}"><code>${escapeHtml(buf.join("\n"))}</code></pre>`);
					continue;
				}
				if (/^---+$/.test(line.trim())) {
					closeLists();
					closeTable();
					html.push("<hr class=\"md-hr\" />");
					i++;
					continue;
				}
				const headerMatch = line.match(/^(#{1,6})\s+(.*)$/);
				if (headerMatch) {
					closeLists();
					closeTable();
					const level = headerMatch[1].length;
					html.push(`<h${level} class="md-h md-h-${level}">${renderInline(headerMatch[2])}</h${level}>`);
					i++;
					continue;
				}
				if (line.includes("|") && line.trim().startsWith("|")) {
					const next = lines[i + 1] || "";
					if (/^\s*\|[\s:|-]+\|\s*$/.test(next) && next.includes("-")) {
						closeLists();
						const headers = line.trim().slice(1, -1).split("|").map((c) => c.trim());
						if (!inTable) {
							html.push("<table class=\"md-table\"><thead><tr>");
							headers.forEach((h) => html.push(`<th>${renderInline(h)}</th>`));
							html.push("</tr></thead><tbody>");
							inTable = true;
						}
						i += 2;
						continue;
					}
					if (inTable) {
						const cells = line.trim().slice(1, -1).split("|").map((c) => c.trim());
						html.push("<tr>");
						cells.forEach((c, idx) => {
							html.push(`<td>${renderInline(c)}</td>`);
						});
						html.push("</tr>");
						i++;
						continue;
					}
				}
				if (inTable) closeTable();
				if (/^\s*[-*+]\s+/.test(line)) {
					if (inOl) {
						html.push("</ol>");
						inOl = false;
					}
					if (!inUl) {
						html.push("<ul class=\"md-ul\">");
						inUl = true;
					}
					const item = line.replace(/^\s*[-*+]\s+/, "");
					html.push(`<li>${renderInline(item)}</li>`);
					i++;
					continue;
				}
				if (/^\s*\d+\.\s+/.test(line)) {
					if (inUl) {
						html.push("</ul>");
						inUl = false;
					}
					if (!inOl) {
						html.push("<ol class=\"md-ol\">");
						inOl = true;
					}
					const item = line.replace(/^\s*\d+\.\s+/, "");
					html.push(`<li>${renderInline(item)}</li>`);
					i++;
					continue;
				}
				if (line.trim() === "") {
					closeLists();
					closeTable();
					i++;
					continue;
				}
				closeLists();
				closeTable();
				const para = [line];
				i++;
				while (i < lines.length && lines[i].trim() !== "" && !/^(#{1,6})\s+/.test(lines[i]) && !/^\s*[-*+]\s+/.test(lines[i]) && !/^\s*\d+\.\s+/.test(lines[i]) && !lines[i].trim().startsWith("```") && !/^---+$/.test(lines[i].trim()) && !(lines[i].includes("|") && lines[i].trim().startsWith("|"))) {
					para.push(lines[i]);
					i++;
				}
				html.push(`<p class="md-p">${renderInline(para.join("<br>"))}</p>`);
			}
			closeLists();
			closeTable();
			return html.join("\n");
		}
		const renderedContent = computed(() => selectedContent.value ? renderMarkdown(selectedContent.value.content || "") : "");
		function formatTime(time) {
			if (!time) return "";
			return time.length >= 10 ? time.slice(0, 10) : time;
		}
		onMounted(() => {
			loadContents();
		});
		return (_ctx, _cache) => {
			var _selectedContent$valu;
			const _component_el_input = resolveComponent("el-input");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_icon = resolveComponent("el-icon");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [createElementVNode("aside", _hoisted_2, [_cache[1] || (_cache[1] = createElementVNode("h3", { class: "help-sidebar__title" }, "帮助中心", -1)), createElementVNode("ul", _hoisted_3, [(openBlock(), createElementBlock(Fragment, null, renderList(CATEGORIES, (cat) => {
				return createElementVNode("li", {
					key: cat.value,
					class: normalizeClass(["help-sidebar__item", { "is-active": selectedCategory.value === cat.value }]),
					onClick: ($event) => selectCategory(cat.value)
				}, [createElementVNode("span", _hoisted_5, toDisplayString(cat.label), 1), createElementVNode("span", _hoisted_6, toDisplayString(countOf(cat.value)), 1)], 10, _hoisted_4);
			}), 64))])]), createElementVNode("section", _hoisted_7, [createElementVNode("header", _hoisted_8, [createVNode(_component_el_input, {
				modelValue: keyword.value,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => keyword.value = $event),
				placeholder: "在当前分类下搜索标题或内容",
				"prefix-icon": unref(Search),
				clearable: "",
				class: "help-header__search"
			}, null, 8, ["modelValue", "prefix-icon"]), selectedContent.value ? (openBlock(), createBlock(_component_el_button, {
				key: 0,
				icon: unref(ArrowLeft),
				onClick: backToList
			}, {
				default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode(" 返回列表 ", -1)])]),
				_: 1
			}, 8, ["icon"])) : createCommentVNode("", true)]), !selectedContent.value ? withDirectives((openBlock(), createElementBlock("div", _hoisted_9, [createElementVNode("h2", _hoisted_10, toDisplayString(currentCategoryMeta.value.label), 1), filteredContents.value.length === 0 && !loading.value ? (openBlock(), createBlock(_component_el_empty, {
				key: 0,
				description: "暂无帮助内容"
			})) : (openBlock(), createElementBlock("ul", _hoisted_11, [(openBlock(true), createElementBlock(Fragment, null, renderList(filteredContents.value, (item) => {
				var _item$viewCount;
				return openBlock(), createElementBlock("li", {
					key: item.id,
					class: "help-list__item",
					onClick: ($event) => selectContent(item)
				}, [createElementVNode("div", _hoisted_13, toDisplayString(item.title), 1), createElementVNode("div", _hoisted_14, [
					createVNode(_component_el_icon, null, {
						default: withCtx(() => [createVNode(unref(View))]),
						_: 1
					}),
					createElementVNode("span", null, toDisplayString((_item$viewCount = item.viewCount) !== null && _item$viewCount !== void 0 ? _item$viewCount : 0) + " 次浏览", 1),
					item.updateTime ? (openBlock(), createElementBlock("span", _hoisted_15, " · 更新于 " + toDisplayString(formatTime(item.updateTime)), 1)) : createCommentVNode("", true)
				])], 8, _hoisted_12);
			}), 128))]))])), [[_directive_loading, loading.value]]) : (openBlock(), createElementBlock("article", _hoisted_16, [
				createElementVNode("h1", _hoisted_17, toDisplayString(selectedContent.value.title), 1),
				createElementVNode("div", _hoisted_18, [
					createElementVNode("span", null, "分类：" + toDisplayString(currentCategoryMeta.value.label), 1),
					createElementVNode("span", null, "· " + toDisplayString((_selectedContent$valu = selectedContent.value.viewCount) !== null && _selectedContent$valu !== void 0 ? _selectedContent$valu : 0) + " 次浏览", 1),
					selectedContent.value.updateTime ? (openBlock(), createElementBlock("span", _hoisted_19, " · 更新于 " + toDisplayString(formatTime(selectedContent.value.updateTime)), 1)) : createCommentVNode("", true)
				]),
				createElementVNode("div", {
					class: "md-body",
					innerHTML: renderedContent.value
				}, null, 8, _hoisted_20)
			]))])]);
		};
	}
}), [["__scopeId", "data-v-cdb31b2d"]]);
//#endregion
export { help_default as default };

//# sourceMappingURL=help-6lGIcq8x.js.map