import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElButton, ElEmpty, ElIcon, ElTag } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, defineComponent, openBlock, renderList, resolveDynamicComponent, toDisplayString, unref, withCtx } from "vue";
//#region src/components/MobileListCard/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "mobile-list-card" };
var _hoisted_2 = { class: "mlc-header" };
var _hoisted_3 = { class: "mlc-title-wrap" };
var _hoisted_4 = { class: "mlc-title" };
var _hoisted_5 = { class: "mlc-index" };
var _hoisted_6 = {
	key: 0,
	class: "mlc-subtitle"
};
var _hoisted_7 = {
	key: 1,
	class: "mlc-body"
};
var _hoisted_8 = { class: "mlc-label" };
var _hoisted_9 = { class: "mlc-value" };
var _hoisted_10 = {
	key: 2,
	class: "mlc-actions"
};
//#endregion
//#region src/components/MobileListCard/index.vue
var MobileListCard_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	props: {
		data: {},
		columns: {},
		operations: { default: () => [] },
		titleProp: { default: "" },
		titleIcon: { default: "" },
		emptyText: { default: "暂无数据" }
	},
	setup(__props) {
		const props = __props;
		const resolvedTitleProp = computed(() => {
			var _props$columns$;
			return props.titleProp || ((_props$columns$ = props.columns[0]) === null || _props$columns$ === void 0 ? void 0 : _props$columns$.prop) || "";
		});
		const subtitleColumn = computed(() => props.columns.find((c) => c.subtitle));
		const bodyColumns = computed(() => props.columns.filter((c) => c.prop !== resolvedTitleProp.value && !c.subtitle));
		function asRow(row) {
			return row !== null && row !== void 0 ? row : {};
		}
		function cellText(row, col) {
			const raw = asRow(row)[col.prop];
			if (col.formatter) return col.formatter(asRow(row), raw);
			if (raw === void 0 || raw === null || raw === "") return "-";
			return String(raw);
		}
		function titleText(row) {
			const raw = asRow(row)[resolvedTitleProp.value];
			if (raw === void 0 || raw === null || raw === "") return "未命名";
			return String(raw);
		}
		function subtitleText(row) {
			if (!subtitleColumn.value) return "";
			return cellText(row, subtitleColumn.value);
		}
		function resolveTagType(row, col) {
			return col.tagType ? col.tagType(asRow(row)) : "info";
		}
		function visibleOperations(row) {
			return props.operations.filter((op) => !op.show || op.show(asRow(row)));
		}
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", _hoisted_1, [!__props.data || __props.data.length === 0 ? (openBlock(), createBlock(unref(ElEmpty), {
				key: 0,
				description: __props.emptyText
			}, null, 8, ["description"])) : (openBlock(true), createElementBlock(Fragment, { key: 1 }, renderList(__props.data, (row, idx) => {
				var _asRow$id;
				return openBlock(), createElementBlock("div", {
					key: (_asRow$id = asRow(row).id) !== null && _asRow$id !== void 0 ? _asRow$id : idx,
					class: "mlc-item"
				}, [
					createElementVNode("div", _hoisted_2, [createElementVNode("div", _hoisted_3, [__props.titleIcon ? (openBlock(), createBlock(unref(ElIcon), {
						key: 0,
						class: "mlc-title-icon"
					}, {
						default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(__props.titleIcon)))]),
						_: 1
					})) : createCommentVNode("", true), createElementVNode("span", _hoisted_4, toDisplayString(titleText(row)), 1)]), createElementVNode("span", _hoisted_5, "#" + toDisplayString(idx + 1), 1)]),
					subtitleColumn.value ? (openBlock(), createElementBlock("div", _hoisted_6, toDisplayString(subtitleText(row)), 1)) : createCommentVNode("", true),
					bodyColumns.value.length > 0 ? (openBlock(), createElementBlock("div", _hoisted_7, [(openBlock(true), createElementBlock(Fragment, null, renderList(bodyColumns.value, (col) => {
						return openBlock(), createElementBlock("div", {
							key: col.prop,
							class: "mlc-field"
						}, [createElementVNode("span", _hoisted_8, toDisplayString(col.label), 1), createElementVNode("span", _hoisted_9, [col.render === "tag" ? (openBlock(), createBlock(unref(ElTag), {
							key: 0,
							type: resolveTagType(row, col),
							size: "small",
							effect: "light"
						}, {
							default: withCtx(() => [createTextVNode(toDisplayString(cellText(row, col)), 1)]),
							_: 2
						}, 1032, ["type"])) : (openBlock(), createElementBlock(Fragment, { key: 1 }, [createTextVNode(toDisplayString(cellText(row, col)), 1)], 64))])]);
					}), 128))])) : createCommentVNode("", true),
					visibleOperations(row).length > 0 ? (openBlock(), createElementBlock("div", _hoisted_10, [(openBlock(true), createElementBlock(Fragment, null, renderList(visibleOperations(row), (op, i) => {
						return openBlock(), createBlock(unref(ElButton), {
							key: i,
							link: "",
							type: op.type || "primary",
							size: "small",
							onClick: ($event) => op.onClick(asRow(row))
						}, {
							default: withCtx(() => [createTextVNode(toDisplayString(op.label), 1)]),
							_: 2
						}, 1032, ["type", "onClick"]);
					}), 128))])) : createCommentVNode("", true)
				]);
			}), 128))]);
		};
	}
}), [["__scopeId", "data-v-e8a45d78"]]);
//#endregion
export { MobileListCard_default as t };

//# sourceMappingURL=MobileListCard-DJUDXCEp.js.map