//#region src/styles/breakpoints.ts
/** 断点顺序数组（自小到大，用于遍历与继承查找） */
var BREAKPOINT_ORDER = [
	"xs",
	"sm",
	"md",
	"lg",
	"xl"
];
/** 各断点的预览宽度（px，用于设计器画布预览模拟，取断点的典型设备宽度） */
var BREAKPOINT_PREVIEW_WIDTH = {
	xs: 375,
	sm: 768,
	md: 992,
	lg: 1200,
	xl: 1920
};
/** 各断点的显示文案（含屏幕宽度范围） */
var BREAKPOINT_LABEL = {
	xs: "xs (<768px) 手机",
	sm: "sm (≥768px) 平板竖屏",
	md: "md (≥992px) 平板横屏",
	lg: "lg (≥1200px) 桌面",
	xl: "xl (≥1920px) 大桌面"
};
//#endregion
export { BREAKPOINT_ORDER as n, BREAKPOINT_PREVIEW_WIDTH as r, BREAKPOINT_LABEL as t };

//# sourceMappingURL=breakpoints-CjTxpeuh.js.map