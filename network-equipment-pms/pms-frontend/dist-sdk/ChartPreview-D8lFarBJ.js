import { createElementBlock, createElementVNode, defineComponent, normalizeStyle, openBlock, ref, toDisplayString, unref } from "vue";
//#region src/components/LowCodeWidgets/ChartPreview.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { style: {
	"padding": "16px",
	"color": "#909399",
	"text-align": "center"
} };
var displayName = "图表预览";
//#endregion
//#region src/components/LowCodeWidgets/ChartPreview.vue
var ChartPreview_default = /* @__PURE__ */ defineComponent({
	__name: "ChartPreview",
	props: {
		modelValue: {},
		chartType: {}
	},
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		var _props$chartType;
		const props = __props;
		const chartType = (_props$chartType = props.chartType) !== null && _props$chartType !== void 0 ? _props$chartType : "bar";
		ref(props.modelValue);
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", {
				class: "widget-placeholder chart-preview",
				style: normalizeStyle({
					width: "100%",
					height: "300px"
				})
			}, [createElementVNode("div", _hoisted_1, toDisplayString(displayName) + "（chartType=" + toDisplayString(unref(chartType)) + "）— 占位", 1)], 4);
		};
	}
});
//#endregion
export { ChartPreview_default as default };

//# sourceMappingURL=ChartPreview-D8lFarBJ.js.map