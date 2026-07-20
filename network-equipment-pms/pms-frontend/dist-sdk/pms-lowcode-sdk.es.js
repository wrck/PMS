import { m as __exportAll, r as get$1 } from "./request-BQrAOfxW.js";
import { t as LowCodeComponentRegistry_default } from "./LowCodeComponentRegistry-BhIrM3BV.js";
//#region src/api/lowcode-component-meta.ts
var lowcode_component_meta_exports = /* @__PURE__ */ __exportAll({ listComponentMetas: () => listComponentMetas });
/**
* 查询所有组件元数据（别名，与后端 /api/lowcode/component-meta GET 对齐）。
*
* <p>批次4-T6 新增此别名以匹配 SDK runtime 的命名规范。</p>
*/
function listComponentMetas() {
	return get$1("/api/lowcode/component-meta");
}
//#endregion
//#region src/sdk/csp-allowlist.ts
/**
* iframe/远程组件 URL 安全校验（批次4-T7）。
*
* <p>从 sdk/runtime.ts 抽出的独立工具函数，供 {@link ComponentSandbox} 与
* {@link initRemoteComponents} 共用，避免重复实现。</p>
*
* <p>规则：
* <ul>
*   <li>开发环境：允许 http://localhost:* 和 https://localhost:*</li>
*   <li>生产环境：仅允许 https:// 开头</li>
*   <li>禁止 file: / data: / javascript: / blob: 等危险协议</li>
*   <li>域名白名单：可在 window.__LOWCODE_CSP_ALLOWLIST__ 中配置（数组），
*       支持 *.example.com 通配符</li>
*   <li>未配置白名单时，开发环境允许所有 localhost，生产允许所有 https</li>
* </ul>
* </p>
*/
/** CSP 白名单配置的全局变量名 */
var CSP_ALLOWLIST_KEY = "__LOWCODE_CSP_ALLOWLIST__";
/**
* 读取 CSP 白名单配置。
*
* <p>白名单在应用启动时通过 `window.__LOWCODE_CSP_ALLOWLIST__ = ['cdn.example.com', '*.pms.com']`
* 设置，通常来自后端配置或环境变量。</p>
*/
function getCspAllowlist() {
	const list = window[CSP_ALLOWLIST_KEY];
	return Array.isArray(list) ? list : [];
}
/**
* 设置 CSP 白名单配置（供应用启动时调用）。
*/
function setCspAllowlist(patterns) {
	window[CSP_ALLOWLIST_KEY] = patterns;
}
/**
* 校验 URL 是否在允许范围内（CSP 白名单的运行时防御）。
*
* @param url 待校验的 URL
* @returns 是否允许加载
*/
function isAllowedUrl(url) {
	if (!url || typeof url !== "string") return false;
	const lower = url.toLowerCase();
	if (lower.startsWith("file:") || lower.startsWith("data:") || lower.startsWith("javascript:") || lower.startsWith("blob:") || lower.startsWith("vbscript:")) return false;
	if (!lower.startsWith("https://")) return false;
	const allowlist = getCspAllowlist();
	if (allowlist.length > 0) try {
		const host = new URL(url).hostname;
		return allowlist.some((pattern) => {
			if (pattern.startsWith("*.")) return host.endsWith(pattern.slice(1));
			return host === pattern;
		});
	} catch (_unused) {
		return false;
	}
	return true;
}
/**
* 生成 iframe sandbox 属性值（批次4-T7）。
*
* <p>借鉴 ToolJet/Payload CMS 的 iframe 隔离策略，按"最小权限原则"组合 sandbox token：
* <ul>
*   <li>allow-scripts：允许执行 JS（必需，否则远程组件无法运行）</li>
*   <li>allow-same-origin：允许同源访问（远程组件需 fetch 自己资源时必需，
*       但与 allow-scripts 组合会降低隔离强度，仅在 trusted 场景启用）</li>
*   <li>allow-forms：允许表单提交（表单类组件必需）</li>
*   <li>allow-popups：允许 window.open（弹窗类组件）</li>
*   <li>allow-modals：允许 alert/confirm（交互类组件）</li>
*   <li>禁用 allow-top-navigation：禁止 iframe 重定向父页面（安全）</li>
* </ul>
* </p>
*
* @param sameOrigin 是否允许同源访问（默认 false，最大化隔离）
* @param allowForms 是否允许表单提交
* @param allowPopups 是否允许弹窗
* @returns sandbox 属性值字符串（空格分隔的 token 列表）
*/
function buildSandboxAttribute(sameOrigin = false, allowForms = true, allowPopups = false) {
	const tokens = ["allow-scripts"];
	if (sameOrigin) tokens.push("allow-same-origin");
	if (allowForms) tokens.push("allow-forms");
	if (allowPopups) tokens.push("allow-popups");
	tokens.push("allow-modals");
	return tokens.join(" ");
}
/**
* 生成 iframe 的 CSP `Content-Security-Policy` 头（通过 meta 标签或 HTTP 头注入）。
*
* <p>由于 iframe 的 CSP 头无法由父页面直接设置（需目标页自身配置），
* 本函数返回的 policy 字符串主要用于：
* <ul>
*   <li>父页面注入 iframe 时，作为 data-csp 属性记录期望策略（调试用）</li>
*   <li>同源代理加载场景，由服务端按此 policy 注入 HTTP 头</li>
* </ul>
* 实际的 CSP 强制依赖目标页自身的 meta 标签或服务端 HTTP 头。</p>
*/
function buildFrameCsp(allowedOrigins) {
	return [
		`default-src 'none'`,
		`script-src 'self' 'unsafe-inline' 'unsafe-eval' ${allowedOrigins.join(" ")}`.trim(),
		`style-src 'self' 'unsafe-inline' ${allowedOrigins.join(" ")}`.trim(),
		`img-src 'self' data: blob: ${allowedOrigins.join(" ")}`.trim(),
		`font-src 'self' data: ${allowedOrigins.join(" ")}`.trim(),
		`connect-src 'self' ${allowedOrigins.join(" ")}`.trim(),
		`frame-ancestors 'self'`,
		`form-action 'self' ${allowedOrigins.join(" ")}`.trim(),
		`base-uri 'self'`
	].join("; ");
}
//#endregion
//#region src/sdk/runtime.ts
/** 透传 registry 基础 API */
var register = LowCodeComponentRegistry_default.register;
var get = LowCodeComponentRegistry_default.get;
var has = LowCodeComponentRegistry_default.has;
var list = LowCodeComponentRegistry_default.list;
var initBuiltinComponents = LowCodeComponentRegistry_default.initBuiltinComponents;
/**
* 定义并注册一个低代码自定义组件（批次4-T6 核心 API）。
*
* <p>使用示例见 {@link ./index.ts} 顶部 Javadoc。</p>
*
* @param def 组件定义
* @returns 已注册的组件记录
*/
function defineLowCodeComponent(def) {
	if (!def.name || typeof def.name !== "string") throw new Error("[LowCodeSDK] defineLowCodeComponent: name 必填且为字符串");
	if (!def.component) throw new Error(`[LowCodeSDK] defineLowCodeComponent: ${def.name} 缺少 component`);
	const meta = {
		name: def.name,
		displayName: def.meta.displayName || def.meta.name || def.name,
		category: def.meta.category || "CUSTOM",
		propsSchema: def.meta.propsSchema || []
	};
	register(def.name, def.component, meta);
	return {
		component: def.component,
		meta
	};
}
/**
* 从后端拉取市场组件元数据并动态注册（批次4-T6）。
*
* <p>拉取 sourceType=MARKETPLACE 的组件记录，按 entryUrl 动态 import 组件 JS，
* 注册到 registry。失败的单个组件仅记 warn 日志，不阻断整体加载。</p>
*
* <p>安全性：远程组件 entryUrl 必须满足以下条件才会加载：
* <ul>
*   <li>URL 以 https:// 开头（生产强制）或 http://localhost（开发环境）</li>
*   <li>URL 域名在 CSP 白名单内（由后端 component-meta.entryUrl 校验，前端再防御性校验）</li>
*   <li>动态 import 失败时仅 warn，不 throw</li>
* </ul>
* 实际的 iframe 沙箱隔离由 B4-T7 ComponentSandbox 负责，本函数仅负责组件 JS 注册。</p>
*
* <p>注意：本函数依赖原生 ES Module 动态 import，需目标服务器配置 CORS 允许跨域。
* 对于不支持 CORS 的源，建议改用 B4-T7 的 iframe 沙箱方案。</p>
*/
async function initRemoteComponents() {
	let metas;
	try {
		metas = await listComponentMetas();
	} catch (e) {
		console.warn("[LowCodeSDK] 拉取远程组件元数据失败，跳过远程组件加载", e);
		return;
	}
	for (const meta of metas) {
		if (meta.sourceType !== "MARKETPLACE" || !meta.entryUrl) continue;
		if (!isAllowedUrl(meta.entryUrl)) {
			console.warn(`[LowCodeSDK] 跳过远程组件 "${meta.name}"：entryUrl 不在允许范围 (CSP 白名单/协议校验失败): ${meta.entryUrl}`);
			continue;
		}
		try {
			const component = (await import(
				/* @vite-ignore */
				meta.entryUrl
)).default;
			if (!component) {
				console.warn(`[LowCodeSDK] 远程组件 "${meta.name}" 模块未导出 default，跳过`);
				continue;
			}
			let propsSchema = [];
			if (meta.propsSchema) try {
				propsSchema = JSON.parse(meta.propsSchema);
			} catch (_unused) {
				console.warn(`[LowCodeSDK] 远程组件 "${meta.name}" propsSchema JSON 解析失败，按空 schema 处理`);
			}
			register(meta.name, component, {
				name: meta.name,
				displayName: meta.displayName || meta.name,
				category: meta.category || "CUSTOM",
				propsSchema
			});
		} catch (e) {
			console.warn(`[LowCodeSDK] 加载远程组件 "${meta.name}" 失败: ${meta.entryUrl}`, e);
		}
	}
}
/** Host → Guest 消息类型 */
var HostToGuestMessage = {
	/** 注入初始 props + context（iframe READY 后父页面响应） */
	INIT: "LC_SANDBOX_INIT",
	/** props 变更同步（父页面 props watch 触发） */
	UPDATE_PROPS: "LC_SANDBOX_UPDATE_PROPS",
	/** 上下文变更（formData/mode 等 LowCodeContext 变化） */
	UPDATE_CONTEXT: "LC_SANDBOX_UPDATE_CONTEXT",
	/** 通知 iframe 容器尺寸变化（响应式布局） */
	RESIZE: "LC_SANDBOX_RESIZE",
	/** 请求 iframe 上报自身高度（自适应场景） */
	REQUEST_HEIGHT: "LC_SANDBOX_REQUEST_HEIGHT"
};
/** Guest → Host 消息类型 */
var GuestToHostMessage = {
	/** iframe 加载完成，请求初始 props */
	READY: "LC_SANDBOX_READY",
	/** 上报 v-model 值变更 */
	UPDATE_VALUE: "LC_SANDBOX_UPDATE_VALUE",
	/** 上报自定义事件（change/blur/focus 等） */
	EVENT: "LC_SANDBOX_EVENT",
	/** 上报自身内容高度（自适应） */
	REPORT_HEIGHT: "LC_SANDBOX_REPORT_HEIGHT",
	/** 上报运行时错误 */
	ERROR: "LC_SANDBOX_ERROR",
	/** 上报日志（调试用） */
	LOG: "LC_SANDBOX_LOG"
};
/**
* 判断 MessageEvent 是否为合法的 ComponentSandbox 消息。
*
* <p>校验：
* <ul>
*   <li>data 是对象且含 version + type 字段</li>
*   <li>version 与当前协议版本一致</li>
*   <li>type 在已知枚举范围内</li>
* </ul>
* </p>
*/
function isSandboxMessage(data) {
	if (!data || typeof data !== "object") return false;
	const msg = data;
	if (msg.version !== "1.0") return false;
	if (typeof msg.type !== "string") return false;
	return [...Object.values(HostToGuestMessage), ...Object.values(GuestToHostMessage)].includes(msg.type);
}
/**
* 创建协议消息（自动填充 version + timestamp）。
*/
function createMessage(type, payload, id) {
	return {
		version: "1.0",
		type,
		id,
		timestamp: Date.now(),
		payload
	};
}
//#endregion
//#region src/sdk/guest-runtime.ts
/**
* Guest 端 SDK 运行时（批次4-T7）。
*
* <p>远程组件（iframe 内运行的代码）使用本模块与父页面（host）通信。
* 提供与 host 端 {@link ComponentSandbox} 协议配套的 guest 端 API：
* <ul>
*   <li>{@link initGuest}：初始化 guest 运行时，监听 host 消息，上报 READY</li>
*   <li>{@link getProps}：获取 host 注入的 props</li>
*   <li>{@link getContext}：获取 host 注入的 LowCodeContext</li>
*   <li>{@link updateValue}：上报 v-model 值变更（触发 host update:modelValue）</li>
*   <li>{@link emitEvent}：上报自定义事件（change/blur/focus 等）</li>
*   <li>{@link reportHeight}：上报自身内容高度（自适应场景）</li>
*   <li>{@link reportError}：上报运行时错误</li>
*   <li>{@link onPropsChange}：订阅 props 变更（响应式推送）</li>
* </ul>
* </p>
*
* <p>远程组件典型用法：
* <pre>
* // entry.html 内联 JS 或独立 .js 文件
* import { initGuest } from '@/sdk/guest-runtime'
*
* const { getProps, getContext, updateValue, onPropsChange } = initGuest({
*   onInit: ({ props, context }) => {
*     renderMyComponent(document.body, props, context)
*   }
* })
*
* onPropsChange((newProps) => {
*   updateMyComponent(newProps)
* })
* </pre>
* </p>
*
* <p>注意：本模块需打包进远程组件的 JS bundle 中（或通过 CDN 引入），
* 不依赖 host 端的 vue/element-plus，是独立的轻量运行时。</p>
*/
/**
* 初始化 Guest 运行时。
*
* <p>调用后会：
* <ol>
*   <li>监听 window.message 事件（origin 校验 + 协议校验）</li>
*   <li>立即向父页面发送 READY 消息，触发 host 推送 INIT</li>
*   <li>收到 INIT 后调用 onInit 回调</li>
* </ol>
* </p>
*
* @param config 配置回调
* @returns Guest 运行时句柄
*/
function initGuest(config = {}) {
	let currentProps = {};
	let currentContext = {};
	const propsChangeHandlers = [];
	const contextChangeHandlers = [];
	if (config.onPropsChange) propsChangeHandlers.push(config.onPropsChange);
	if (config.onContextChange) contextChangeHandlers.push(config.onContextChange);
	/**
	* 向 host 发送消息。
	*
	* <p>targetOrigin 使用 document.referrer 的 origin（iframe 场景下 referrer 即父页面 URL），
	* 若 referrer 为空（如直接打开），回退到 '*'（仅开发场景，生产应配置明确 origin）。</p>
	*/
	function postToHost(type, payload) {
		let targetOrigin;
		try {
			targetOrigin = document.referrer ? new URL(document.referrer).origin : "*";
		} catch (_unused) {
			targetOrigin = "*";
		}
		const msg = createMessage(type, payload);
		window.parent.postMessage(msg, targetOrigin);
	}
	/**
	* 接收 host 消息（Host → Guest）。
	*/
	function onMessage(event) {
		if (window.parent !== event.source) return;
		if (!isSandboxMessage(event.data)) return;
		const msg = event.data;
		switch (msg.type) {
			case HostToGuestMessage.INIT: {
				var _config$onInit;
				const payload = msg.payload;
				currentProps = { ...payload.props };
				currentContext = { ...payload.context };
				(_config$onInit = config.onInit) === null || _config$onInit === void 0 || _config$onInit.call(config, payload);
				break;
			}
			case HostToGuestMessage.UPDATE_PROPS: {
				const payload = msg.payload;
				currentProps = { ...payload.props };
				propsChangeHandlers.forEach((h) => h(payload));
				break;
			}
			case HostToGuestMessage.UPDATE_CONTEXT: {
				const payload = msg.payload;
				currentContext = { ...payload.context };
				contextChangeHandlers.forEach((h) => h(payload));
				break;
			}
			case HostToGuestMessage.RESIZE: {
				var _config$onResize;
				const payload = msg.payload;
				(_config$onResize = config.onResize) === null || _config$onResize === void 0 || _config$onResize.call(config, payload);
				break;
			}
			case HostToGuestMessage.REQUEST_HEIGHT:
				var _config$onRequestHeig;
				(_config$onRequestHeig = config.onRequestHeight) === null || _config$onRequestHeig === void 0 || _config$onRequestHeig.call(config);
				break;
			default: break;
		}
	}
	window.addEventListener("message", onMessage);
	postToHost(GuestToHostMessage.READY, {
		componentName: window.__LC_COMPONENT_NAME__ || "unknown",
		url: window.location.href
	});
	return {
		getProps: () => ({ ...currentProps }),
		getContext: () => ({ ...currentContext }),
		updateValue: (value) => {
			postToHost(GuestToHostMessage.UPDATE_VALUE, { value });
		},
		emitEvent: (eventName, ...args) => {
			postToHost(GuestToHostMessage.EVENT, {
				eventName,
				args
			});
		},
		reportHeight: (height, width) => {
			if (height > 0) postToHost(GuestToHostMessage.REPORT_HEIGHT, {
				height,
				width
			});
		},
		reportError: (message, stack, source) => {
			const payload = {
				message,
				stack,
				source
			};
			postToHost(GuestToHostMessage.ERROR, payload);
		},
		log: (level, ...args) => {
			const payload = {
				level,
				args
			};
			postToHost(GuestToHostMessage.LOG, payload);
		},
		onPropsChange: (handler) => {
			propsChangeHandlers.push(handler);
		},
		onContextChange: (handler) => {
			contextChangeHandlers.push(handler);
		},
		destroy: () => {
			window.removeEventListener("message", onMessage);
			propsChangeHandlers.length = 0;
			contextChangeHandlers.length = 0;
		}
	};
}
/**
* 自动高度上报工具（批次4-T7）。
*
* <p>使用 ResizeObserver 监听 document.body 尺寸变化，自动通过 guest runtime 上报高度。
* 适用于内容动态变化的组件（如富文本编辑器、动态列表）。</p>
*
* @param runtime guest 运行时
* @returns 取消监听函数
*/
function autoReportHeight(runtime) {
	if (typeof ResizeObserver === "undefined") return () => {};
	const observer = new ResizeObserver((entries) => {
		for (const entry of entries) {
			const height = Math.ceil(entry.contentRect.height);
			if (height > 0) runtime.reportHeight(height, Math.ceil(entry.contentRect.width));
		}
	});
	observer.observe(document.body);
	return () => observer.disconnect();
}
//#endregion
//#region src/sdk/index.ts
/** SDK 版本（与 lowcode-platform-maturity-upgrade-design.md 批次4-T6/T7 对齐） */
var SDK_VERSION = "1.0.0";
//#endregion
export { SDK_VERSION, autoReportHeight, buildFrameCsp, buildSandboxAttribute, defineLowCodeComponent, get, getCspAllowlist, has, initBuiltinComponents, initGuest, initRemoteComponents, isAllowedUrl, list, register, setCspAllowlist, lowcode_component_meta_exports as t };

//# sourceMappingURL=pms-lowcode-sdk.es.js.map