//#region src/components/LowCodeComponentRegistry/index.ts
var registry = /* @__PURE__ */ new Map();
function register(name, component, meta) {
	registry.set(name, {
		component,
		meta
	});
}
function get(name) {
	return registry.get(name);
}
function list() {
	return Array.from(registry.values()).map((v) => v.meta);
}
function has(name) {
	return registry.has(name);
}
/**
* Registry default export (aggregated API).
* Usage: `import LowCodeComponentRegistry from '@/components/LowCodeComponentRegistry'`
*/
var LowCodeComponentRegistry_default = {
	register,
	get,
	list,
	has,
	initBuiltinComponents
};
var BUILTIN_METAS = {
	UserSelector: {
		name: "UserSelector",
		displayName: "用户选择器",
		category: "SELECTOR",
		propsSchema: [{
			key: "multiple",
			type: "boolean",
			default: false
		}]
	},
	DeptSelector: {
		name: "DeptSelector",
		displayName: "部门选择器",
		category: "SELECTOR",
		propsSchema: [{
			key: "multiple",
			type: "boolean",
			default: false
		}]
	},
	DictSelect: {
		name: "DictSelect",
		displayName: "数据字典下拉",
		category: "SELECTOR",
		propsSchema: [{
			key: "dictCode",
			type: "string",
			required: true
		}]
	},
	FileUploader: {
		name: "FileUploader",
		displayName: "文件上传",
		category: "INPUT",
		propsSchema: [{
			key: "accept",
			type: "string"
		}, {
			key: "maxSize",
			type: "number",
			default: 10
		}]
	},
	RichTextEditor: {
		name: "RichTextEditor",
		displayName: "富文本编辑器",
		category: "INPUT",
		propsSchema: [{
			key: "height",
			type: "number",
			default: 300
		}]
	},
	CodeEditor: {
		name: "CodeEditor",
		displayName: "代码编辑器",
		category: "INPUT",
		propsSchema: [{
			key: "language",
			type: "string",
			default: "javascript"
		}]
	},
	ColorPicker: {
		name: "ColorPicker",
		displayName: "颜色选择器",
		category: "INPUT",
		propsSchema: [{
			key: "showAlpha",
			type: "boolean",
			default: true
		}]
	},
	TreeSelect: {
		name: "TreeSelect",
		displayName: "树形选择",
		category: "SELECTOR",
		propsSchema: [{
			key: "data",
			type: "array"
		}]
	},
	DateRangePicker: {
		name: "DateRangePicker",
		displayName: "日期范围",
		category: "INPUT",
		propsSchema: [{
			key: "format",
			type: "string",
			default: "YYYY-MM-DD"
		}]
	},
	NumberRangeInput: {
		name: "NumberRangeInput",
		displayName: "数字范围",
		category: "INPUT",
		propsSchema: [{
			key: "min",
			type: "number"
		}, {
			key: "max",
			type: "number"
		}]
	},
	AddressPicker: {
		name: "AddressPicker",
		displayName: "地址选择",
		category: "SELECTOR",
		propsSchema: [{
			key: "level",
			type: "number",
			default: 3
		}]
	},
	BarcodeInput: {
		name: "BarcodeInput",
		displayName: "条码扫描",
		category: "INPUT",
		propsSchema: [{
			key: "types",
			type: "array",
			default: ["CODE_128", "EAN_13"]
		}]
	},
	SignaturePad: {
		name: "SignaturePad",
		displayName: "电子签名",
		category: "INPUT",
		propsSchema: [{
			key: "width",
			type: "number",
			default: 400
		}, {
			key: "height",
			type: "number",
			default: 200
		}]
	},
	ChartPreview: {
		name: "ChartPreview",
		displayName: "图表预览",
		category: "DISPLAY",
		propsSchema: [{
			key: "chartType",
			type: "string",
			default: "bar"
		}]
	},
	QrcodeDisplay: {
		name: "QrcodeDisplay",
		displayName: "二维码展示",
		category: "DISPLAY",
		propsSchema: [{
			key: "size",
			type: "number",
			default: 128
		}]
	}
};
/**
* Parse backend propsSchema (JSON string) into ComponentPropDef[].
*
* Backend format: '{"props":[{"key":"multiple","type":"boolean","default":false}]}'
* or directly '[{"key":"multiple",...}]'
* Frontend expects: ComponentPropDef[] (array)
*/
function parsePropsSchema(raw) {
	if (!raw) return [];
	try {
		const parsed = JSON.parse(raw);
		if (Array.isArray(parsed)) return parsed;
		if (parsed && Array.isArray(parsed.props)) return parsed.props;
		return [];
	} catch (_unused) {
		console.warn("[LowCode] Failed to parse propsSchema:", raw);
		return [];
	}
}
/**
* Initialize builtin components:
* 1. Load local .vue widgets with hardcoded metas (reliable baseline)
* 2. Fetch backend component metas and merge (adds custom/marketplace components)
*
* Errors are logged but do not block the designer — base components remain usable.
* Widgets are loaded concurrently to avoid blocking the designer UI.
*/
async function initBuiltinComponents() {
	const widgets = /* #__PURE__ */ Object.assign({
		"../LowCodeWidgets/AddressPicker.vue": () => import("./AddressPicker-CCdDHz8O.js"),
		"../LowCodeWidgets/BarcodeInput.vue": () => import("./BarcodeInput-DcddCVLS.js"),
		"../LowCodeWidgets/ChartPreview.vue": () => import("./ChartPreview-D8lFarBJ.js"),
		"../LowCodeWidgets/CodeEditor.vue": () => import("./CodeEditor-Be9gLJp9.js"),
		"../LowCodeWidgets/ColorPicker.vue": () => import("./ColorPicker-BcK-4pmD.js"),
		"../LowCodeWidgets/DateRangePicker.vue": () => import("./DateRangePicker-CglRBw81.js"),
		"../LowCodeWidgets/DeptSelector.vue": () => import("./DeptSelector-D3zAHcBu.js"),
		"../LowCodeWidgets/DictSelect.vue": () => import("./DictSelect-CdM3Lroh.js"),
		"../LowCodeWidgets/FileUploader.vue": () => import("./FileUploader-CECSBcGy.js"),
		"../LowCodeWidgets/NumberRangeInput.vue": () => import("./NumberRangeInput-BRg-EN_R.js"),
		"../LowCodeWidgets/QrcodeDisplay.vue": () => import("./QrcodeDisplay-CgV4JN8J.js"),
		"../LowCodeWidgets/RichTextEditor.vue": () => import("./RichTextEditor-DN8onT2b.js"),
		"../LowCodeWidgets/SignaturePad.vue": () => import("./SignaturePad-DW1LFGxr.js"),
		"../LowCodeWidgets/TreeSelect.vue": () => import("./TreeSelect-wZnmqs1T.js"),
		"../LowCodeWidgets/UserSelector.vue": () => import("./UserSelector-DnXkhH1e.js")
	});
	const loadPromises = [];
	for (const [path, loader] of Object.entries(widgets)) {
		const name = path.split("/").pop().replace(".vue", "");
		const meta = BUILTIN_METAS[name];
		if (!meta) continue;
		loadPromises.push(loader().then((module) => {
			const component = module.default;
			register(name, component, meta);
		}).catch((e) => {
			console.error(`[LowCode] Failed to load widget "${name}":`, e);
		}));
	}
	await Promise.all(loadPromises);
	console.info(`[LowCode] Loaded ${registry.size} builtin widgets`);
	try {
		const { listComponentMetas } = await import("./pms-lowcode-sdk.es.js").then((n) => n.t);
		const timeoutPromise = new Promise((_, reject) => setTimeout(() => reject(/* @__PURE__ */ new Error("timeout")), 3e3));
		const remoteMetas = await Promise.race([listComponentMetas(), timeoutPromise]);
		let remoteCount = 0;
		for (const rm of remoteMetas) {
			if (registry.has(rm.name)) continue;
			if (rm.sourceType === "MARKETPLACE" && rm.entryUrl) try {
				const module = await import(
					/* @vite-ignore */
					rm.entryUrl
);
				const component = module.default || module;
				const meta = {
					name: rm.name,
					displayName: rm.displayName,
					category: rm.category,
					propsSchema: parsePropsSchema(rm.propsSchema)
				};
				register(rm.name, component, meta);
				remoteCount++;
			} catch (e) {
				console.error(`[LowCode] Failed to load remote component "${rm.name}" from ${rm.entryUrl}:`, e);
			}
		}
		if (remoteCount > 0) console.info(`[LowCode] Loaded ${remoteCount} remote components from backend`);
	} catch (e) {
		console.warn("[LowCode] Could not fetch backend component metas (builtin widgets still available):", e);
	}
}
//#endregion
export { initBuiltinComponents as n, LowCodeComponentRegistry_default as t };

//# sourceMappingURL=LowCodeComponentRegistry-BhIrM3BV.js.map