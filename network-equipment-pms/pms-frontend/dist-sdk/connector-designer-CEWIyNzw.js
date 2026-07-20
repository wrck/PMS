import { a as testOperation, i as saveConnector, n as getConnectorList, t as deleteConnector } from "./lowcode-connector-Cjm1QnL-.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, normalizeClass, onMounted, openBlock, reactive, ref, renderList, resolveComponent, toDisplayString, vShow, watch, withCtx, withDirectives, withModifiers } from "vue";
//#region src/components/ConnectorDesigner/config.ts
/** 默认重试配置 */
var DEFAULT_RETRY = {
	maxAttempts: 3,
	waitMillis: 1e3,
	timeoutMillis: 3e4,
	retryOnStatusCodes: [
		500,
		502,
		503
	]
};
/** 默认分页配置 */
var DEFAULT_PAGINATION = { type: "NONE" };
/** 创建空 REST 操作 */
function createEmptyRestOperation() {
	return {
		name: "",
		method: "GET",
		path: "",
		headers: [],
		params: [],
		body: null
	};
}
/** 创建空 SQL 模板 */
function createEmptySqlTemplate() {
	return {
		name: "",
		sqlType: "QUERY",
		sqlTemplate: ""
	};
}
/** 创建默认配置（按类型） */
function createDefaultConfig(type) {
	if (type === "DB") return {
		type: "DB",
		driverClassName: "com.mysql.cj.jdbc.Driver",
		dbUrl: "",
		dbUsername: "",
		dbPassword: "",
		maxPoolSize: 10,
		sqlTemplates: [],
		responseMapping: [],
		retry: { ...DEFAULT_RETRY }
	};
	return {
		type: "REST",
		baseUrl: "",
		authType: "NONE",
		operations: [],
		responseMapping: [],
		pagination: { ...DEFAULT_PAGINATION },
		retry: { ...DEFAULT_RETRY }
	};
}
/** 安全解析 JSON 字符串 */
function safeParse(json) {
	if (!json) return null;
	try {
		return JSON.parse(json);
	} catch (_unused) {
		return null;
	}
}
/**
* 将旧版简单 JSON 配置转换为结构化 {@link ConnectorConfig}。
*
* <p>兼容场景：
* <ul>
*   <li>{@code {"url":"","method":"GET"}} — 旧版 REST 单操作</li>
*   <li>{@code {"url":"jdbc:...","driverClassName":"..."}} — 旧版 DB</li>
*   <li>已结构化的配置 — 直接返回（补全缺失字段）</li>
* </ul></p>
*/
function parseConnectorConfig(configStr, type) {
	var _parsed$maxPoolSize;
	const parsed = safeParse(configStr || "");
	if (!parsed || typeof parsed !== "object") return createDefaultConfig(type);
	if (parsed.type === "REST" || parsed.type === "DB") return normalizeConfig(parsed);
	if (type === "REST") {
		var _parsed$body;
		const method = parsed.method || "GET";
		return {
			type: "REST",
			baseUrl: parsed.baseUrl || parsed.url || "",
			authType: parsed.authType || "NONE",
			username: parsed.username,
			password: parsed.password,
			token: parsed.token,
			headerName: parsed.headerName,
			apiKey: parsed.apiKey,
			operations: Array.isArray(parsed.operations) ? parsed.operations.map(normalizeRestOperation) : [{
				name: "default",
				method,
				path: parsed.path || "",
				headers: toKeyValueList(parsed.headers),
				params: toKeyValueList(parsed.params),
				body: (_parsed$body = parsed.body) !== null && _parsed$body !== void 0 ? _parsed$body : null
			}],
			responseMapping: Array.isArray(parsed.responseMapping) ? parsed.responseMapping.map(normalizeResponseMapping) : [],
			pagination: parsed.pagination ? normalizePagination(parsed.pagination) : { ...DEFAULT_PAGINATION },
			retry: parsed.retry ? normalizeRetry(parsed.retry) : { ...DEFAULT_RETRY }
		};
	}
	return {
		type: "DB",
		driverClassName: parsed.driverClassName || "com.mysql.cj.jdbc.Driver",
		dbUrl: parsed.dbUrl || parsed.url || "",
		dbUsername: parsed.dbUsername || parsed.username || "",
		dbPassword: parsed.dbPassword || parsed.password || "",
		maxPoolSize: (_parsed$maxPoolSize = parsed.maxPoolSize) !== null && _parsed$maxPoolSize !== void 0 ? _parsed$maxPoolSize : 10,
		sqlTemplates: Array.isArray(parsed.sqlTemplates) ? parsed.sqlTemplates.map(normalizeSqlTemplate) : Array.isArray(parsed.operations) ? parsed.operations.map((op) => ({
			name: op.name || "",
			sqlType: op.sqlType || "QUERY",
			sqlTemplate: op.sqlTemplate || op.sql || ""
		})) : [],
		responseMapping: Array.isArray(parsed.responseMapping) ? parsed.responseMapping.map(normalizeResponseMapping) : [],
		retry: parsed.retry ? normalizeRetry(parsed.retry) : { ...DEFAULT_RETRY }
	};
}
/** 标准化结构化配置，补全缺失字段 */
function normalizeConfig(cfg) {
	var _cfg$driverClassName, _cfg$dbUrl, _cfg$dbUsername, _cfg$dbPassword, _cfg$maxPoolSize;
	if (cfg.type === "REST") {
		var _cfg$baseUrl, _cfg$authType;
		return {
			type: "REST",
			baseUrl: (_cfg$baseUrl = cfg.baseUrl) !== null && _cfg$baseUrl !== void 0 ? _cfg$baseUrl : "",
			authType: (_cfg$authType = cfg.authType) !== null && _cfg$authType !== void 0 ? _cfg$authType : "NONE",
			username: cfg.username,
			password: cfg.password,
			token: cfg.token,
			headerName: cfg.headerName,
			apiKey: cfg.apiKey,
			operations: (cfg.operations || []).map(normalizeRestOperation),
			responseMapping: (cfg.responseMapping || []).map(normalizeResponseMapping),
			pagination: cfg.pagination ? normalizePagination(cfg.pagination) : { ...DEFAULT_PAGINATION },
			retry: cfg.retry ? normalizeRetry(cfg.retry) : { ...DEFAULT_RETRY }
		};
	}
	return {
		type: "DB",
		driverClassName: (_cfg$driverClassName = cfg.driverClassName) !== null && _cfg$driverClassName !== void 0 ? _cfg$driverClassName : "com.mysql.cj.jdbc.Driver",
		dbUrl: (_cfg$dbUrl = cfg.dbUrl) !== null && _cfg$dbUrl !== void 0 ? _cfg$dbUrl : "",
		dbUsername: (_cfg$dbUsername = cfg.dbUsername) !== null && _cfg$dbUsername !== void 0 ? _cfg$dbUsername : "",
		dbPassword: (_cfg$dbPassword = cfg.dbPassword) !== null && _cfg$dbPassword !== void 0 ? _cfg$dbPassword : "",
		maxPoolSize: (_cfg$maxPoolSize = cfg.maxPoolSize) !== null && _cfg$maxPoolSize !== void 0 ? _cfg$maxPoolSize : 10,
		sqlTemplates: (cfg.sqlTemplates || []).map(normalizeSqlTemplate),
		responseMapping: (cfg.responseMapping || []).map(normalizeResponseMapping),
		retry: cfg.retry ? normalizeRetry(cfg.retry) : { ...DEFAULT_RETRY }
	};
}
function normalizeRestOperation(op) {
	var _op$body;
	return {
		name: op.name || "",
		method: op.method || "GET",
		path: op.path || "",
		headers: toKeyValueList(op.headers),
		params: toKeyValueList(op.params),
		body: (_op$body = op.body) !== null && _op$body !== void 0 ? _op$body : null
	};
}
function normalizeSqlTemplate(t) {
	return {
		name: t.name || "",
		sqlType: t.sqlType || "QUERY",
		sqlTemplate: t.sqlTemplate || ""
	};
}
function normalizeResponseMapping(m) {
	var _m$transform;
	return {
		sourcePath: m.sourcePath || "",
		targetField: m.targetField || "",
		transform: (_m$transform = m.transform) !== null && _m$transform !== void 0 ? _m$transform : null
	};
}
function normalizePagination(p) {
	return {
		type: p.type || "NONE",
		offsetParam: p.offsetParam,
		limitParam: p.limitParam,
		totalCountPath: p.totalCountPath,
		pageParam: p.pageParam,
		pageSizeParam: p.pageSizeParam,
		totalPagesPath: p.totalPagesPath,
		nextLinkPath: p.nextLinkPath
	};
}
function normalizeRetry(r) {
	var _r$maxAttempts, _r$waitMillis, _r$timeoutMillis;
	return {
		maxAttempts: (_r$maxAttempts = r.maxAttempts) !== null && _r$maxAttempts !== void 0 ? _r$maxAttempts : 3,
		waitMillis: (_r$waitMillis = r.waitMillis) !== null && _r$waitMillis !== void 0 ? _r$waitMillis : 1e3,
		timeoutMillis: (_r$timeoutMillis = r.timeoutMillis) !== null && _r$timeoutMillis !== void 0 ? _r$timeoutMillis : 3e4,
		retryOnStatusCodes: Array.isArray(r.retryOnStatusCodes) ? r.retryOnStatusCodes : [
			500,
			502,
			503
		]
	};
}
/** 将 headers/params 多种形态（对象 / KV 数组）统一为 KV 数组 */
function toKeyValueList(raw) {
	if (!raw) return [];
	if (Array.isArray(raw)) return raw.map((kv) => {
		var _kv$key, _kv$value;
		return {
			key: (_kv$key = kv.key) !== null && _kv$key !== void 0 ? _kv$key : "",
			value: (_kv$value = kv.value) !== null && _kv$value !== void 0 ? _kv$value : ""
		};
	});
	if (typeof raw === "object") return Object.entries(raw).map(([key, value]) => ({
		key,
		value: String(value !== null && value !== void 0 ? value : "")
	}));
	return [];
}
/** 序列化结构化配置为 JSON 字符串 */
function serializeConnectorConfig(cfg) {
	return JSON.stringify(cfg, null, 2);
}
//#endregion
//#region src/components/ConnectorDesigner/StepBasicInfo.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$9 = { class: "type-hint" };
var _hoisted_2$6 = { key: 0 };
var _hoisted_3$5 = { key: 1 };
//#endregion
//#region src/components/ConnectorDesigner/StepBasicInfo.vue
var StepBasicInfo_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "StepBasicInfo",
	props: {
		code: {},
		name: {},
		description: {},
		bizType: {},
		type: {}
	},
	emits: [
		"update:code",
		"update:name",
		"update:description",
		"update:bizType",
		"update:type"
	],
	setup(__props, { emit: __emit }) {
		/**
		* 连接器分步表单 — Step 1 基本信息。
		*
		* <p>借鉴 Power Apps Custom Connectors 的第一步：编码 / 名称 / 描述 / 类型。</p>
		*/
		const props = __props;
		const emit = __emit;
		const form = reactive({
			code: props.code,
			name: props.name,
			description: props.description,
			bizType: props.bizType,
			type: props.type
		});
		watch(() => [
			props.code,
			props.name,
			props.description,
			props.bizType,
			props.type
		], ([code, name, description, bizType, type]) => {
			form.code = code;
			form.name = name;
			form.description = description;
			form.bizType = bizType;
			form.type = type;
		});
		watch(form, (val) => {
			emit("update:code", val.code);
			emit("update:name", val.name);
			emit("update:description", val.description);
			emit("update:bizType", val.bizType);
			emit("update:type", val.type);
		});
		return (_ctx, _cache) => {
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_radio_button = resolveComponent("el-radio-button");
			const _component_el_radio_group = resolveComponent("el-radio-group");
			const _component_el_form = resolveComponent("el-form");
			return openBlock(), createBlock(_component_el_form, {
				model: form,
				"label-width": "100px",
				class: "step-basic-info"
			}, {
				default: withCtx(() => [
					createVNode(_component_el_form_item, {
						label: "连接器编码",
						required: ""
					}, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: form.code,
							"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => form.code = $event),
							placeholder: "如 githubConnector"
						}, null, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, {
						label: "连接器名称",
						required: ""
					}, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: form.name,
							"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => form.name = $event),
							placeholder: "如 GitHub 连接器"
						}, null, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "描述" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: form.description,
							"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => form.description = $event),
							type: "textarea",
							rows: 3,
							placeholder: "连接器用途说明"
						}, null, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "业务类型" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: form.bizType,
							"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => form.bizType = $event),
							placeholder: "如 integration / external-api"
						}, null, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, {
						label: "连接器类型",
						required: ""
					}, {
						default: withCtx(() => [createVNode(_component_el_radio_group, {
							modelValue: form.type,
							"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => form.type = $event)
						}, {
							default: withCtx(() => [createVNode(_component_el_radio_button, { value: "REST" }, {
								default: withCtx(() => [..._cache[5] || (_cache[5] = [createTextVNode("REST API", -1)])]),
								_: 1
							}), createVNode(_component_el_radio_button, { value: "DB" }, {
								default: withCtx(() => [..._cache[6] || (_cache[6] = [createTextVNode("数据库 (DB)", -1)])]),
								_: 1
							})]),
							_: 1
						}, 8, ["modelValue"]), createElementVNode("div", _hoisted_1$9, [form.type === "REST" ? (openBlock(), createElementBlock("span", _hoisted_2$6, "REST 类型：通过 HTTP 调用外部 API，支持认证、操作、分页")) : (openBlock(), createElementBlock("span", _hoisted_3$5, "DB 类型：直连数据库执行 SQL，支持数据源池化与 SQL 模板"))])]),
						_: 1
					})
				]),
				_: 1
			}, 8, ["model"]);
		};
	}
}), [["__scopeId", "data-v-a9305dac"]]);
//#endregion
//#region src/components/ConnectorDesigner/StepAuth.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$8 = { class: "step-auth" };
//#endregion
//#region src/components/ConnectorDesigner/StepAuth.vue
var StepAuth_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "StepAuth",
	props: {
		type: {},
		authType: {},
		username: {},
		password: {},
		token: {},
		headerName: {},
		apiKey: {},
		baseUrl: {},
		driverClassName: {},
		dbUrl: {},
		dbUsername: {},
		dbPassword: {},
		maxPoolSize: {}
	},
	emits: [
		"update:authType",
		"update:username",
		"update:password",
		"update:token",
		"update:headerName",
		"update:apiKey",
		"update:baseUrl",
		"update:driverClassName",
		"update:dbUrl",
		"update:dbUsername",
		"update:dbPassword",
		"update:maxPoolSize"
	],
	setup(__props, { emit: __emit }) {
		/**
		* 连接器分步表单 — Step 2 认证配置（REST）/ 数据源配置（DB）。
		*
		* <p>REST：authType 下拉（NONE/BASIC/BEARER/API_KEY），按类型展示对应字段 + baseUrl。
		* DB：driverClassName / url / username / password / maxPoolSize。</p>
		*/
		const props = __props;
		const emit = __emit;
		const form = reactive({
			authType: props.authType,
			username: props.username,
			password: props.password,
			token: props.token,
			headerName: props.headerName,
			apiKey: props.apiKey,
			baseUrl: props.baseUrl,
			driverClassName: props.driverClassName,
			dbUrl: props.dbUrl,
			dbUsername: props.dbUsername,
			dbPassword: props.dbPassword,
			maxPoolSize: props.maxPoolSize
		});
		watch(() => [
			props.authType,
			props.username,
			props.password,
			props.token,
			props.headerName,
			props.apiKey,
			props.baseUrl,
			props.driverClassName,
			props.dbUrl,
			props.dbUsername,
			props.dbPassword,
			props.maxPoolSize
		], ([authType, username, password, token, headerName, apiKey, baseUrl, driverClassName, dbUrl, dbUsername, dbPassword, maxPoolSize]) => {
			form.authType = authType;
			form.username = username;
			form.password = password;
			form.token = token;
			form.headerName = headerName;
			form.apiKey = apiKey;
			form.baseUrl = baseUrl;
			form.driverClassName = driverClassName;
			form.dbUrl = dbUrl;
			form.dbUsername = dbUsername;
			form.dbPassword = dbPassword;
			form.maxPoolSize = maxPoolSize;
		});
		watch(form, (val) => {
			emit("update:authType", val.authType);
			emit("update:username", val.username);
			emit("update:password", val.password);
			emit("update:token", val.token);
			emit("update:headerName", val.headerName);
			emit("update:apiKey", val.apiKey);
			emit("update:baseUrl", val.baseUrl);
			emit("update:driverClassName", val.driverClassName);
			emit("update:dbUrl", val.dbUrl);
			emit("update:dbUsername", val.dbUsername);
			emit("update:dbPassword", val.dbPassword);
			emit("update:maxPoolSize", val.maxPoolSize);
		});
		const DRIVER_PRESETS = [
			{
				label: "MySQL 8",
				value: "com.mysql.cj.jdbc.Driver"
			},
			{
				label: "MySQL 5.x",
				value: "com.mysql.jdbc.Driver"
			},
			{
				label: "PostgreSQL",
				value: "org.postgresql.Driver"
			},
			{
				label: "SQL Server",
				value: "com.microsoft.sqlserver.jdbc.SQLServerDriver"
			},
			{
				label: "Oracle",
				value: "oracle.jdbc.OracleDriver"
			}
		];
		return (_ctx, _cache) => {
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_divider = resolveComponent("el-divider");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_input_number = resolveComponent("el-input-number");
			return openBlock(), createElementBlock("div", _hoisted_1$8, [props.type === "REST" ? (openBlock(), createBlock(_component_el_form, {
				key: 0,
				model: form,
				"label-width": "120px"
			}, {
				default: withCtx(() => [
					createVNode(_component_el_form_item, { label: "认证类型" }, {
						default: withCtx(() => [createVNode(_component_el_select, {
							modelValue: form.authType,
							"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => form.authType = $event),
							style: { "width": "240px" }
						}, {
							default: withCtx(() => [
								createVNode(_component_el_option, {
									label: "无认证 (NONE)",
									value: "NONE"
								}),
								createVNode(_component_el_option, {
									label: "Basic 认证",
									value: "BASIC"
								}),
								createVNode(_component_el_option, {
									label: "Bearer Token",
									value: "BEARER"
								}),
								createVNode(_component_el_option, {
									label: "API Key",
									value: "API_KEY"
								})
							]),
							_: 1
						}, 8, ["modelValue"])]),
						_: 1
					}),
					form.authType === "BASIC" ? (openBlock(), createElementBlock(Fragment, { key: 0 }, [createVNode(_component_el_form_item, { label: "用户名" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: form.username,
							"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => form.username = $event),
							placeholder: "basic auth 用户名"
						}, null, 8, ["modelValue"])]),
						_: 1
					}), createVNode(_component_el_form_item, { label: "密码" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: form.password,
							"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => form.password = $event),
							type: "password",
							"show-password": "",
							placeholder: "basic auth 密码"
						}, null, 8, ["modelValue"])]),
						_: 1
					})], 64)) : form.authType === "BEARER" ? (openBlock(), createBlock(_component_el_form_item, {
						key: 1,
						label: "Token"
					}, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: form.token,
							"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => form.token = $event),
							type: "password",
							"show-password": "",
							placeholder: "Bearer token 值"
						}, null, 8, ["modelValue"])]),
						_: 1
					})) : form.authType === "API_KEY" ? (openBlock(), createElementBlock(Fragment, { key: 2 }, [createVNode(_component_el_form_item, { label: "Header 名称" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: form.headerName,
							"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => form.headerName = $event),
							placeholder: "如 X-API-Key"
						}, null, 8, ["modelValue"])]),
						_: 1
					}), createVNode(_component_el_form_item, { label: "API Key" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: form.apiKey,
							"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => form.apiKey = $event),
							type: "password",
							"show-password": "",
							placeholder: "API Key 值"
						}, null, 8, ["modelValue"])]),
						_: 1
					})], 64)) : createCommentVNode("", true),
					createVNode(_component_el_divider, { "content-position": "left" }, {
						default: withCtx(() => [..._cache[12] || (_cache[12] = [createTextVNode("服务地址", -1)])]),
						_: 1
					}),
					createVNode(_component_el_form_item, {
						label: "Base URL",
						required: ""
					}, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: form.baseUrl,
							"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => form.baseUrl = $event),
							placeholder: "https://api.example.com"
						}, null, 8, ["modelValue"])]),
						_: 1
					})
				]),
				_: 1
			}, 8, ["model"])) : (openBlock(), createBlock(_component_el_form, {
				key: 1,
				model: form,
				"label-width": "120px"
			}, {
				default: withCtx(() => [
					createVNode(_component_el_form_item, {
						label: "驱动类名",
						required: ""
					}, {
						default: withCtx(() => [createVNode(_component_el_select, {
							modelValue: form.driverClassName,
							"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => form.driverClassName = $event),
							filterable: "",
							"allow-create": "",
							"default-first-option": "",
							style: { "width": "360px" }
						}, {
							default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(DRIVER_PRESETS, (d) => {
								return createVNode(_component_el_option, {
									key: d.value,
									label: `${d.label} — ${d.value}`,
									value: d.value
								}, null, 8, ["label", "value"]);
							}), 64))]),
							_: 1
						}, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, {
						label: "JDBC URL",
						required: ""
					}, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: form.dbUrl,
							"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => form.dbUrl = $event),
							placeholder: "jdbc:mysql://localhost:3306/dppms_d365?useSSL=false"
						}, null, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, {
						label: "用户名",
						required: ""
					}, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: form.dbUsername,
							"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => form.dbUsername = $event)
						}, null, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, {
						label: "密码",
						required: ""
					}, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: form.dbPassword,
							"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => form.dbPassword = $event),
							type: "password",
							"show-password": ""
						}, null, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "连接池大小" }, {
						default: withCtx(() => [createVNode(_component_el_input_number, {
							modelValue: form.maxPoolSize,
							"onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => form.maxPoolSize = $event),
							min: 1,
							max: 100
						}, null, 8, ["modelValue"])]),
						_: 1
					})
				]),
				_: 1
			}, 8, ["model"]))]);
		};
	}
}), [["__scopeId", "data-v-2c17704b"]]);
//#endregion
//#region node_modules/js-yaml/dist/js-yaml.mjs
function getDefaultExportFromCjs(x) {
	return x && x.__esModule && Object.prototype.hasOwnProperty.call(x, "default") ? x["default"] : x;
}
var jsYaml = {};
var loader = {};
var common = {};
var hasRequiredCommon;
function requireCommon() {
	if (hasRequiredCommon) return common;
	hasRequiredCommon = 1;
	function isNothing(subject) {
		return typeof subject === "undefined" || subject === null;
	}
	function isObject(subject) {
		return typeof subject === "object" && subject !== null;
	}
	function toArray(sequence) {
		if (Array.isArray(sequence)) return sequence;
		else if (isNothing(sequence)) return [];
		return [sequence];
	}
	function extend(target, source) {
		if (source) {
			const sourceKeys = Object.keys(source);
			for (let index = 0, length = sourceKeys.length; index < length; index += 1) {
				const key = sourceKeys[index];
				target[key] = source[key];
			}
		}
		return target;
	}
	function repeat(string, count) {
		let result = "";
		for (let cycle = 0; cycle < count; cycle += 1) result += string;
		return result;
	}
	function isNegativeZero(number) {
		return number === 0 && Number.NEGATIVE_INFINITY === 1 / number;
	}
	common.isNothing = isNothing;
	common.isObject = isObject;
	common.toArray = toArray;
	common.repeat = repeat;
	common.isNegativeZero = isNegativeZero;
	common.extend = extend;
	return common;
}
var exception;
var hasRequiredException;
function requireException() {
	if (hasRequiredException) return exception;
	hasRequiredException = 1;
	function formatError(exception2, compact) {
		let where = "";
		const message = exception2.reason || "(unknown reason)";
		if (!exception2.mark) return message;
		if (exception2.mark.name) where += "in \"" + exception2.mark.name + "\" ";
		where += "(" + (exception2.mark.line + 1) + ":" + (exception2.mark.column + 1) + ")";
		if (!compact && exception2.mark.snippet) where += "\n\n" + exception2.mark.snippet;
		return message + " " + where;
	}
	function YAMLException2(reason, mark) {
		Error.call(this);
		this.name = "YAMLException";
		this.reason = reason;
		this.mark = mark;
		this.message = formatError(this, false);
		if (Error.captureStackTrace) Error.captureStackTrace(this, this.constructor);
		else this.stack = (/* @__PURE__ */ new Error()).stack || "";
	}
	YAMLException2.prototype = Object.create(Error.prototype);
	YAMLException2.prototype.constructor = YAMLException2;
	YAMLException2.prototype.toString = function toString(compact) {
		return this.name + ": " + formatError(this, compact);
	};
	exception = YAMLException2;
	return exception;
}
var snippet;
var hasRequiredSnippet;
function requireSnippet() {
	if (hasRequiredSnippet) return snippet;
	hasRequiredSnippet = 1;
	const common2 = requireCommon();
	function getLine(buffer, lineStart, lineEnd, position, maxLineLength) {
		let head = "";
		let tail = "";
		const maxHalfLength = Math.floor(maxLineLength / 2) - 1;
		if (position - lineStart > maxHalfLength) {
			head = " ... ";
			lineStart = position - maxHalfLength + head.length;
		}
		if (lineEnd - position > maxHalfLength) {
			tail = " ...";
			lineEnd = position + maxHalfLength - tail.length;
		}
		return {
			str: head + buffer.slice(lineStart, lineEnd).replace(/\t/g, "→") + tail,
			pos: position - lineStart + head.length
		};
	}
	function padStart(string, max) {
		return common2.repeat(" ", max - string.length) + string;
	}
	function makeSnippet(mark, options) {
		options = Object.create(options || null);
		if (!mark.buffer) return null;
		if (!options.maxLength) options.maxLength = 79;
		if (typeof options.indent !== "number") options.indent = 1;
		if (typeof options.linesBefore !== "number") options.linesBefore = 3;
		if (typeof options.linesAfter !== "number") options.linesAfter = 2;
		const re = /\r?\n|\r|\0/g;
		const lineStarts = [0];
		const lineEnds = [];
		let match;
		let foundLineNo = -1;
		while (match = re.exec(mark.buffer)) {
			lineEnds.push(match.index);
			lineStarts.push(match.index + match[0].length);
			if (mark.position <= match.index && foundLineNo < 0) foundLineNo = lineStarts.length - 2;
		}
		if (foundLineNo < 0) foundLineNo = lineStarts.length - 1;
		let result = "";
		const lineNoLength = Math.min(mark.line + options.linesAfter, lineEnds.length).toString().length;
		const maxLineLength = options.maxLength - (options.indent + lineNoLength + 3);
		for (let i = 1; i <= options.linesBefore; i++) {
			if (foundLineNo - i < 0) break;
			const line2 = getLine(mark.buffer, lineStarts[foundLineNo - i], lineEnds[foundLineNo - i], mark.position - (lineStarts[foundLineNo] - lineStarts[foundLineNo - i]), maxLineLength);
			result = common2.repeat(" ", options.indent) + padStart((mark.line - i + 1).toString(), lineNoLength) + " | " + line2.str + "\n" + result;
		}
		const line = getLine(mark.buffer, lineStarts[foundLineNo], lineEnds[foundLineNo], mark.position, maxLineLength);
		result += common2.repeat(" ", options.indent) + padStart((mark.line + 1).toString(), lineNoLength) + " | " + line.str + "\n";
		result += common2.repeat("-", options.indent + lineNoLength + 3 + line.pos) + "^\n";
		for (let i = 1; i <= options.linesAfter; i++) {
			if (foundLineNo + i >= lineEnds.length) break;
			const line2 = getLine(mark.buffer, lineStarts[foundLineNo + i], lineEnds[foundLineNo + i], mark.position - (lineStarts[foundLineNo] - lineStarts[foundLineNo + i]), maxLineLength);
			result += common2.repeat(" ", options.indent) + padStart((mark.line + i + 1).toString(), lineNoLength) + " | " + line2.str + "\n";
		}
		return result.replace(/\n$/, "");
	}
	snippet = makeSnippet;
	return snippet;
}
var type;
var hasRequiredType;
function requireType() {
	if (hasRequiredType) return type;
	hasRequiredType = 1;
	const YAMLException2 = requireException();
	const TYPE_CONSTRUCTOR_OPTIONS = [
		"kind",
		"multi",
		"resolve",
		"construct",
		"instanceOf",
		"predicate",
		"represent",
		"representName",
		"defaultStyle",
		"styleAliases"
	];
	const YAML_NODE_KINDS = [
		"scalar",
		"sequence",
		"mapping"
	];
	function compileStyleAliases(map2) {
		const result = {};
		if (map2 !== null) Object.keys(map2).forEach(function(style) {
			map2[style].forEach(function(alias) {
				result[String(alias)] = style;
			});
		});
		return result;
	}
	function Type2(tag, options) {
		options = options || {};
		Object.keys(options).forEach(function(name) {
			if (TYPE_CONSTRUCTOR_OPTIONS.indexOf(name) === -1) throw new YAMLException2("Unknown option \"" + name + "\" is met in definition of \"" + tag + "\" YAML type.");
		});
		this.options = options;
		this.tag = tag;
		this.kind = options["kind"] || null;
		this.resolve = options["resolve"] || function() {
			return true;
		};
		this.construct = options["construct"] || function(data) {
			return data;
		};
		this.instanceOf = options["instanceOf"] || null;
		this.predicate = options["predicate"] || null;
		this.represent = options["represent"] || null;
		this.representName = options["representName"] || null;
		this.defaultStyle = options["defaultStyle"] || null;
		this.multi = options["multi"] || false;
		this.styleAliases = compileStyleAliases(options["styleAliases"] || null);
		if (YAML_NODE_KINDS.indexOf(this.kind) === -1) throw new YAMLException2("Unknown kind \"" + this.kind + "\" is specified for \"" + tag + "\" YAML type.");
	}
	type = Type2;
	return type;
}
var schema;
var hasRequiredSchema;
function requireSchema() {
	if (hasRequiredSchema) return schema;
	hasRequiredSchema = 1;
	const YAMLException2 = requireException();
	const Type2 = requireType();
	function compileList(schema2, name) {
		const result = [];
		schema2[name].forEach(function(currentType) {
			let newIndex = result.length;
			result.forEach(function(previousType, previousIndex) {
				if (previousType.tag === currentType.tag && previousType.kind === currentType.kind && previousType.multi === currentType.multi) newIndex = previousIndex;
			});
			result[newIndex] = currentType;
		});
		return result;
	}
	function compileMap() {
		const result = {
			scalar: {},
			sequence: {},
			mapping: {},
			fallback: {},
			multi: {
				scalar: [],
				sequence: [],
				mapping: [],
				fallback: []
			}
		};
		function collectType(type2) {
			if (type2.multi) {
				result.multi[type2.kind].push(type2);
				result.multi["fallback"].push(type2);
			} else result[type2.kind][type2.tag] = result["fallback"][type2.tag] = type2;
		}
		for (let index = 0, length = arguments.length; index < length; index += 1) arguments[index].forEach(collectType);
		return result;
	}
	function Schema2(definition) {
		return this.extend(definition);
	}
	Schema2.prototype.extend = function extend(definition) {
		let implicit = [];
		let explicit = [];
		if (definition instanceof Type2) explicit.push(definition);
		else if (Array.isArray(definition)) explicit = explicit.concat(definition);
		else if (definition && (Array.isArray(definition.implicit) || Array.isArray(definition.explicit))) {
			if (definition.implicit) implicit = implicit.concat(definition.implicit);
			if (definition.explicit) explicit = explicit.concat(definition.explicit);
		} else throw new YAMLException2("Schema.extend argument should be a Type, [ Type ], or a schema definition ({ implicit: [...], explicit: [...] })");
		implicit.forEach(function(type2) {
			if (!(type2 instanceof Type2)) throw new YAMLException2("Specified list of YAML types (or a single Type object) contains a non-Type object.");
			if (type2.loadKind && type2.loadKind !== "scalar") throw new YAMLException2("There is a non-scalar type in the implicit list of a schema. Implicit resolving of such types is not supported.");
			if (type2.multi) throw new YAMLException2("There is a multi type in the implicit list of a schema. Multi tags can only be listed as explicit.");
		});
		explicit.forEach(function(type2) {
			if (!(type2 instanceof Type2)) throw new YAMLException2("Specified list of YAML types (or a single Type object) contains a non-Type object.");
		});
		const result = Object.create(Schema2.prototype);
		result.implicit = (this.implicit || []).concat(implicit);
		result.explicit = (this.explicit || []).concat(explicit);
		result.compiledImplicit = compileList(result, "implicit");
		result.compiledExplicit = compileList(result, "explicit");
		result.compiledTypeMap = compileMap(result.compiledImplicit, result.compiledExplicit);
		return result;
	};
	schema = Schema2;
	return schema;
}
var str;
var hasRequiredStr;
function requireStr() {
	if (hasRequiredStr) return str;
	hasRequiredStr = 1;
	str = new (requireType())("tag:yaml.org,2002:str", {
		kind: "scalar",
		construct: function(data) {
			return data !== null ? data : "";
		}
	});
	return str;
}
var seq;
var hasRequiredSeq;
function requireSeq() {
	if (hasRequiredSeq) return seq;
	hasRequiredSeq = 1;
	seq = new (requireType())("tag:yaml.org,2002:seq", {
		kind: "sequence",
		construct: function(data) {
			return data !== null ? data : [];
		}
	});
	return seq;
}
var map;
var hasRequiredMap;
function requireMap() {
	if (hasRequiredMap) return map;
	hasRequiredMap = 1;
	map = new (requireType())("tag:yaml.org,2002:map", {
		kind: "mapping",
		construct: function(data) {
			return data !== null ? data : {};
		}
	});
	return map;
}
var failsafe;
var hasRequiredFailsafe;
function requireFailsafe() {
	if (hasRequiredFailsafe) return failsafe;
	hasRequiredFailsafe = 1;
	failsafe = new (requireSchema())({ explicit: [
		requireStr(),
		requireSeq(),
		requireMap()
	] });
	return failsafe;
}
var _null;
var hasRequired_null;
function require_null() {
	if (hasRequired_null) return _null;
	hasRequired_null = 1;
	const Type2 = requireType();
	function resolveYamlNull(data) {
		if (data === null) return true;
		const max = data.length;
		return max === 1 && data === "~" || max === 4 && (data === "null" || data === "Null" || data === "NULL");
	}
	function constructYamlNull() {
		return null;
	}
	function isNull(object) {
		return object === null;
	}
	_null = new Type2("tag:yaml.org,2002:null", {
		kind: "scalar",
		resolve: resolveYamlNull,
		construct: constructYamlNull,
		predicate: isNull,
		represent: {
			canonical: function() {
				return "~";
			},
			lowercase: function() {
				return "null";
			},
			uppercase: function() {
				return "NULL";
			},
			camelcase: function() {
				return "Null";
			},
			empty: function() {
				return "";
			}
		},
		defaultStyle: "lowercase"
	});
	return _null;
}
var bool;
var hasRequiredBool;
function requireBool() {
	if (hasRequiredBool) return bool;
	hasRequiredBool = 1;
	const Type2 = requireType();
	function resolveYamlBoolean(data) {
		if (data === null) return false;
		const max = data.length;
		return max === 4 && (data === "true" || data === "True" || data === "TRUE") || max === 5 && (data === "false" || data === "False" || data === "FALSE");
	}
	function constructYamlBoolean(data) {
		return data === "true" || data === "True" || data === "TRUE";
	}
	function isBoolean(object) {
		return Object.prototype.toString.call(object) === "[object Boolean]";
	}
	bool = new Type2("tag:yaml.org,2002:bool", {
		kind: "scalar",
		resolve: resolveYamlBoolean,
		construct: constructYamlBoolean,
		predicate: isBoolean,
		represent: {
			lowercase: function(object) {
				return object ? "true" : "false";
			},
			uppercase: function(object) {
				return object ? "TRUE" : "FALSE";
			},
			camelcase: function(object) {
				return object ? "True" : "False";
			}
		},
		defaultStyle: "lowercase"
	});
	return bool;
}
var int;
var hasRequiredInt;
function requireInt() {
	if (hasRequiredInt) return int;
	hasRequiredInt = 1;
	const common2 = requireCommon();
	const Type2 = requireType();
	function isHexCode(c) {
		return c >= 48 && c <= 57 || c >= 65 && c <= 70 || c >= 97 && c <= 102;
	}
	function isOctCode(c) {
		return c >= 48 && c <= 55;
	}
	function isDecCode(c) {
		return c >= 48 && c <= 57;
	}
	function resolveYamlInteger(data) {
		if (data === null) return false;
		const max = data.length;
		let index = 0;
		let hasDigits = false;
		if (!max) return false;
		let ch = data[index];
		if (ch === "-" || ch === "+") ch = data[++index];
		if (ch === "0") {
			if (index + 1 === max) return true;
			ch = data[++index];
			if (ch === "b") {
				index++;
				for (; index < max; index++) {
					ch = data[index];
					if (ch !== "0" && ch !== "1") return false;
					hasDigits = true;
				}
				return hasDigits && isFinite(parseYamlInteger(data));
			}
			if (ch === "x") {
				index++;
				for (; index < max; index++) {
					if (!isHexCode(data.charCodeAt(index))) return false;
					hasDigits = true;
				}
				return hasDigits && isFinite(parseYamlInteger(data));
			}
			if (ch === "o") {
				index++;
				for (; index < max; index++) {
					if (!isOctCode(data.charCodeAt(index))) return false;
					hasDigits = true;
				}
				return hasDigits && isFinite(parseYamlInteger(data));
			}
		}
		for (; index < max; index++) {
			if (!isDecCode(data.charCodeAt(index))) return false;
			hasDigits = true;
		}
		if (!hasDigits) return false;
		return isFinite(parseYamlInteger(data));
	}
	function parseYamlInteger(data) {
		let value = data;
		let sign = 1;
		let ch = value[0];
		if (ch === "-" || ch === "+") {
			if (ch === "-") sign = -1;
			value = value.slice(1);
			ch = value[0];
		}
		if (value === "0") return 0;
		if (ch === "0") {
			if (value[1] === "b") return sign * parseInt(value.slice(2), 2);
			if (value[1] === "x") return sign * parseInt(value.slice(2), 16);
			if (value[1] === "o") return sign * parseInt(value.slice(2), 8);
		}
		return sign * parseInt(value, 10);
	}
	function constructYamlInteger(data) {
		return parseYamlInteger(data);
	}
	function isInteger(object) {
		return Object.prototype.toString.call(object) === "[object Number]" && object % 1 === 0 && !common2.isNegativeZero(object);
	}
	int = new Type2("tag:yaml.org,2002:int", {
		kind: "scalar",
		resolve: resolveYamlInteger,
		construct: constructYamlInteger,
		predicate: isInteger,
		represent: {
			binary: function(obj) {
				return obj >= 0 ? "0b" + obj.toString(2) : "-0b" + obj.toString(2).slice(1);
			},
			octal: function(obj) {
				return obj >= 0 ? "0o" + obj.toString(8) : "-0o" + obj.toString(8).slice(1);
			},
			decimal: function(obj) {
				return obj.toString(10);
			},
			hexadecimal: function(obj) {
				return obj >= 0 ? "0x" + obj.toString(16).toUpperCase() : "-0x" + obj.toString(16).toUpperCase().slice(1);
			}
		},
		defaultStyle: "decimal",
		styleAliases: {
			binary: [2, "bin"],
			octal: [8, "oct"],
			decimal: [10, "dec"],
			hexadecimal: [16, "hex"]
		}
	});
	return int;
}
var float;
var hasRequiredFloat;
function requireFloat() {
	if (hasRequiredFloat) return float;
	hasRequiredFloat = 1;
	const common2 = requireCommon();
	const Type2 = requireType();
	const YAML_FLOAT_PATTERN = /* @__PURE__ */ new RegExp("^(?:[-+]?(?:[0-9]+)(?:\\.[0-9]*)?(?:[eE][-+]?[0-9]+)?|\\.[0-9]+(?:[eE][-+]?[0-9]+)?|[-+]?\\.(?:inf|Inf|INF)|\\.(?:nan|NaN|NAN))$");
	const YAML_FLOAT_SPECIAL_PATTERN = /* @__PURE__ */ new RegExp("^(?:[-+]?\\.(?:inf|Inf|INF)|\\.(?:nan|NaN|NAN))$");
	function resolveYamlFloat(data) {
		if (data === null) return false;
		if (!YAML_FLOAT_PATTERN.test(data)) return false;
		if (isFinite(parseFloat(data, 10))) return true;
		return YAML_FLOAT_SPECIAL_PATTERN.test(data);
	}
	function constructYamlFloat(data) {
		let value = data.toLowerCase();
		const sign = value[0] === "-" ? -1 : 1;
		if ("+-".indexOf(value[0]) >= 0) value = value.slice(1);
		if (value === ".inf") return sign === 1 ? Number.POSITIVE_INFINITY : Number.NEGATIVE_INFINITY;
		else if (value === ".nan") return NaN;
		return sign * parseFloat(value, 10);
	}
	const SCIENTIFIC_WITHOUT_DOT = /^[-+]?[0-9]+e/;
	function representYamlFloat(object, style) {
		if (isNaN(object)) switch (style) {
			case "lowercase": return ".nan";
			case "uppercase": return ".NAN";
			case "camelcase": return ".NaN";
		}
		else if (Number.POSITIVE_INFINITY === object) switch (style) {
			case "lowercase": return ".inf";
			case "uppercase": return ".INF";
			case "camelcase": return ".Inf";
		}
		else if (Number.NEGATIVE_INFINITY === object) switch (style) {
			case "lowercase": return "-.inf";
			case "uppercase": return "-.INF";
			case "camelcase": return "-.Inf";
		}
		else if (common2.isNegativeZero(object)) return "-0.0";
		const res = object.toString(10);
		return SCIENTIFIC_WITHOUT_DOT.test(res) ? res.replace("e", ".e") : res;
	}
	function isFloat(object) {
		return Object.prototype.toString.call(object) === "[object Number]" && (object % 1 !== 0 || common2.isNegativeZero(object));
	}
	float = new Type2("tag:yaml.org,2002:float", {
		kind: "scalar",
		resolve: resolveYamlFloat,
		construct: constructYamlFloat,
		predicate: isFloat,
		represent: representYamlFloat,
		defaultStyle: "lowercase"
	});
	return float;
}
var json;
var hasRequiredJson;
function requireJson() {
	if (hasRequiredJson) return json;
	hasRequiredJson = 1;
	json = requireFailsafe().extend({ implicit: [
		require_null(),
		requireBool(),
		requireInt(),
		requireFloat()
	] });
	return json;
}
var core;
var hasRequiredCore;
function requireCore() {
	if (hasRequiredCore) return core;
	hasRequiredCore = 1;
	core = requireJson();
	return core;
}
var timestamp;
var hasRequiredTimestamp;
function requireTimestamp() {
	if (hasRequiredTimestamp) return timestamp;
	hasRequiredTimestamp = 1;
	const Type2 = requireType();
	const YAML_DATE_REGEXP = /* @__PURE__ */ new RegExp("^([0-9][0-9][0-9][0-9])-([0-9][0-9])-([0-9][0-9])$");
	const YAML_TIMESTAMP_REGEXP = /* @__PURE__ */ new RegExp("^([0-9][0-9][0-9][0-9])-([0-9][0-9]?)-([0-9][0-9]?)(?:[Tt]|[ \\t]+)([0-9][0-9]?):([0-9][0-9]):([0-9][0-9])(?:\\.([0-9]*))?(?:[ \\t]*(Z|([-+])([0-9][0-9]?)(?::([0-9][0-9]))?))?$");
	function resolveYamlTimestamp(data) {
		if (data === null) return false;
		if (YAML_DATE_REGEXP.exec(data) !== null) return true;
		if (YAML_TIMESTAMP_REGEXP.exec(data) !== null) return true;
		return false;
	}
	function constructYamlTimestamp(data) {
		let fraction = 0;
		let delta = null;
		let match = YAML_DATE_REGEXP.exec(data);
		if (match === null) match = YAML_TIMESTAMP_REGEXP.exec(data);
		if (match === null) throw new Error("Date resolve error");
		const year = +match[1];
		const month = +match[2] - 1;
		const day = +match[3];
		if (!match[4]) return new Date(Date.UTC(year, month, day));
		const hour = +match[4];
		const minute = +match[5];
		const second = +match[6];
		if (match[7]) {
			fraction = match[7].slice(0, 3);
			while (fraction.length < 3) fraction += "0";
			fraction = +fraction;
		}
		if (match[9]) {
			const tzHour = +match[10];
			const tzMinute = +(match[11] || 0);
			delta = (tzHour * 60 + tzMinute) * 6e4;
			if (match[9] === "-") delta = -delta;
		}
		const date = new Date(Date.UTC(year, month, day, hour, minute, second, fraction));
		if (delta) date.setTime(date.getTime() - delta);
		return date;
	}
	function representYamlTimestamp(object) {
		return object.toISOString();
	}
	timestamp = new Type2("tag:yaml.org,2002:timestamp", {
		kind: "scalar",
		resolve: resolveYamlTimestamp,
		construct: constructYamlTimestamp,
		instanceOf: Date,
		represent: representYamlTimestamp
	});
	return timestamp;
}
var merge;
var hasRequiredMerge;
function requireMerge() {
	if (hasRequiredMerge) return merge;
	hasRequiredMerge = 1;
	const Type2 = requireType();
	function resolveYamlMerge(data) {
		return data === "<<" || data === null;
	}
	merge = new Type2("tag:yaml.org,2002:merge", {
		kind: "scalar",
		resolve: resolveYamlMerge
	});
	return merge;
}
var binary;
var hasRequiredBinary;
function requireBinary() {
	if (hasRequiredBinary) return binary;
	hasRequiredBinary = 1;
	const Type2 = requireType();
	const BASE64_MAP = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=\n\r";
	function resolveYamlBinary(data) {
		if (data === null) return false;
		let bitlen = 0;
		const max = data.length;
		const map2 = BASE64_MAP;
		for (let idx = 0; idx < max; idx++) {
			const code = map2.indexOf(data.charAt(idx));
			if (code > 64) continue;
			if (code < 0) return false;
			bitlen += 6;
		}
		return bitlen % 8 === 0;
	}
	function constructYamlBinary(data) {
		const input = data.replace(/[\r\n=]/g, "");
		const max = input.length;
		const map2 = BASE64_MAP;
		let bits = 0;
		const result = [];
		for (let idx = 0; idx < max; idx++) {
			if (idx % 4 === 0 && idx) {
				result.push(bits >> 16 & 255);
				result.push(bits >> 8 & 255);
				result.push(bits & 255);
			}
			bits = bits << 6 | map2.indexOf(input.charAt(idx));
		}
		const tailbits = max % 4 * 6;
		if (tailbits === 0) {
			result.push(bits >> 16 & 255);
			result.push(bits >> 8 & 255);
			result.push(bits & 255);
		} else if (tailbits === 18) {
			result.push(bits >> 10 & 255);
			result.push(bits >> 2 & 255);
		} else if (tailbits === 12) result.push(bits >> 4 & 255);
		return new Uint8Array(result);
	}
	function representYamlBinary(object) {
		let result = "";
		let bits = 0;
		const max = object.length;
		const map2 = BASE64_MAP;
		for (let idx = 0; idx < max; idx++) {
			if (idx % 3 === 0 && idx) {
				result += map2[bits >> 18 & 63];
				result += map2[bits >> 12 & 63];
				result += map2[bits >> 6 & 63];
				result += map2[bits & 63];
			}
			bits = (bits << 8) + object[idx];
		}
		const tail = max % 3;
		if (tail === 0) {
			result += map2[bits >> 18 & 63];
			result += map2[bits >> 12 & 63];
			result += map2[bits >> 6 & 63];
			result += map2[bits & 63];
		} else if (tail === 2) {
			result += map2[bits >> 10 & 63];
			result += map2[bits >> 4 & 63];
			result += map2[bits << 2 & 63];
			result += map2[64];
		} else if (tail === 1) {
			result += map2[bits >> 2 & 63];
			result += map2[bits << 4 & 63];
			result += map2[64];
			result += map2[64];
		}
		return result;
	}
	function isBinary(obj) {
		return Object.prototype.toString.call(obj) === "[object Uint8Array]";
	}
	binary = new Type2("tag:yaml.org,2002:binary", {
		kind: "scalar",
		resolve: resolveYamlBinary,
		construct: constructYamlBinary,
		predicate: isBinary,
		represent: representYamlBinary
	});
	return binary;
}
var omap;
var hasRequiredOmap;
function requireOmap() {
	if (hasRequiredOmap) return omap;
	hasRequiredOmap = 1;
	const Type2 = requireType();
	const _hasOwnProperty = Object.prototype.hasOwnProperty;
	const _toString = Object.prototype.toString;
	function resolveYamlOmap(data) {
		if (data === null) return true;
		const objectKeys = [];
		const object = data;
		for (let index = 0, length = object.length; index < length; index += 1) {
			const pair = object[index];
			let pairHasKey = false;
			if (_toString.call(pair) !== "[object Object]") return false;
			let pairKey;
			for (pairKey in pair) if (_hasOwnProperty.call(pair, pairKey)) if (!pairHasKey) pairHasKey = true;
			else return false;
			if (!pairHasKey) return false;
			if (objectKeys.indexOf(pairKey) === -1) objectKeys.push(pairKey);
			else return false;
		}
		return true;
	}
	function constructYamlOmap(data) {
		return data !== null ? data : [];
	}
	omap = new Type2("tag:yaml.org,2002:omap", {
		kind: "sequence",
		resolve: resolveYamlOmap,
		construct: constructYamlOmap
	});
	return omap;
}
var pairs;
var hasRequiredPairs;
function requirePairs() {
	if (hasRequiredPairs) return pairs;
	hasRequiredPairs = 1;
	const Type2 = requireType();
	const _toString = Object.prototype.toString;
	function resolveYamlPairs(data) {
		if (data === null) return true;
		const object = data;
		const result = new Array(object.length);
		for (let index = 0, length = object.length; index < length; index += 1) {
			const pair = object[index];
			if (_toString.call(pair) !== "[object Object]") return false;
			const keys = Object.keys(pair);
			if (keys.length !== 1) return false;
			result[index] = [keys[0], pair[keys[0]]];
		}
		return true;
	}
	function constructYamlPairs(data) {
		if (data === null) return [];
		const object = data;
		const result = new Array(object.length);
		for (let index = 0, length = object.length; index < length; index += 1) {
			const pair = object[index];
			const keys = Object.keys(pair);
			result[index] = [keys[0], pair[keys[0]]];
		}
		return result;
	}
	pairs = new Type2("tag:yaml.org,2002:pairs", {
		kind: "sequence",
		resolve: resolveYamlPairs,
		construct: constructYamlPairs
	});
	return pairs;
}
var set;
var hasRequiredSet;
function requireSet() {
	if (hasRequiredSet) return set;
	hasRequiredSet = 1;
	const Type2 = requireType();
	const _hasOwnProperty = Object.prototype.hasOwnProperty;
	function resolveYamlSet(data) {
		if (data === null) return true;
		const object = data;
		for (const key in object) if (_hasOwnProperty.call(object, key)) {
			if (object[key] !== null) return false;
		}
		return true;
	}
	function constructYamlSet(data) {
		return data !== null ? data : {};
	}
	set = new Type2("tag:yaml.org,2002:set", {
		kind: "mapping",
		resolve: resolveYamlSet,
		construct: constructYamlSet
	});
	return set;
}
var _default;
var hasRequired_default;
function require_default() {
	if (hasRequired_default) return _default;
	hasRequired_default = 1;
	_default = requireCore().extend({
		implicit: [requireTimestamp(), requireMerge()],
		explicit: [
			requireBinary(),
			requireOmap(),
			requirePairs(),
			requireSet()
		]
	});
	return _default;
}
var hasRequiredLoader;
function requireLoader() {
	if (hasRequiredLoader) return loader;
	hasRequiredLoader = 1;
	const common2 = requireCommon();
	const YAMLException2 = requireException();
	const makeSnippet = requireSnippet();
	const DEFAULT_SCHEMA2 = require_default();
	const _hasOwnProperty = Object.prototype.hasOwnProperty;
	const CONTEXT_FLOW_IN = 1;
	const CONTEXT_FLOW_OUT = 2;
	const CONTEXT_BLOCK_IN = 3;
	const CONTEXT_BLOCK_OUT = 4;
	const CHOMPING_CLIP = 1;
	const CHOMPING_STRIP = 2;
	const CHOMPING_KEEP = 3;
	const PATTERN_NON_PRINTABLE = /[\x00-\x08\x0B\x0C\x0E-\x1F\x7F-\x84\x86-\x9F\uFFFE\uFFFF]|[\uD800-\uDBFF](?![\uDC00-\uDFFF])|(?:[^\uD800-\uDBFF]|^)[\uDC00-\uDFFF]/;
	const PATTERN_NON_ASCII_LINE_BREAKS = /[\x85\u2028\u2029]/;
	const PATTERN_FLOW_INDICATORS = /[,\[\]{}]/;
	const PATTERN_TAG_HANDLE = /^(?:!|!!|![0-9A-Za-z-]+!)$/;
	const PATTERN_TAG_URI = /^(?:!|[^,\[\]{}])(?:%[0-9a-f]{2}|[0-9a-z\-#;/?:@&=+$,_.!~*'()\[\]])*$/i;
	function _class(obj) {
		return Object.prototype.toString.call(obj);
	}
	function isEol(c) {
		return c === 10 || c === 13;
	}
	function isWhiteSpace(c) {
		return c === 9 || c === 32;
	}
	function isWsOrEol(c) {
		return c === 9 || c === 32 || c === 10 || c === 13;
	}
	function isFlowIndicator(c) {
		return c === 44 || c === 91 || c === 93 || c === 123 || c === 125;
	}
	function fromHexCode(c) {
		if (c >= 48 && c <= 57) return c - 48;
		const lc = c | 32;
		if (lc >= 97 && lc <= 102) return lc - 97 + 10;
		return -1;
	}
	function escapedHexLen(c) {
		if (c === 120) return 2;
		if (c === 117) return 4;
		if (c === 85) return 8;
		return 0;
	}
	function fromDecimalCode(c) {
		if (c >= 48 && c <= 57) return c - 48;
		return -1;
	}
	function simpleEscapeSequence(c) {
		switch (c) {
			case 48: return "\0";
			case 97: return "\x07";
			case 98: return "\b";
			case 116: return "	";
			case 9: return "	";
			case 110: return "\n";
			case 118: return "\v";
			case 102: return "\f";
			case 114: return "\r";
			case 101: return "\x1B";
			case 32: return " ";
			case 34: return "\"";
			case 47: return "/";
			case 92: return "\\";
			case 78: return "";
			case 95: return "\xA0";
			case 76: return "\u2028";
			case 80: return "\u2029";
			default: return "";
		}
	}
	function charFromCodepoint(c) {
		if (c <= 65535) return String.fromCharCode(c);
		return String.fromCharCode((c - 65536 >> 10) + 55296, (c - 65536 & 1023) + 56320);
	}
	function setProperty(object, key, value) {
		if (key === "__proto__") Object.defineProperty(object, key, {
			configurable: true,
			enumerable: true,
			writable: true,
			value
		});
		else object[key] = value;
	}
	const simpleEscapeCheck = new Array(256);
	const simpleEscapeMap = new Array(256);
	for (let i = 0; i < 256; i++) {
		simpleEscapeCheck[i] = simpleEscapeSequence(i) ? 1 : 0;
		simpleEscapeMap[i] = simpleEscapeSequence(i);
	}
	function State(input, options) {
		this.input = input;
		this.filename = options["filename"] || null;
		this.schema = options["schema"] || DEFAULT_SCHEMA2;
		this.onWarning = options["onWarning"] || null;
		this.legacy = options["legacy"] || false;
		this.json = options["json"] || false;
		this.listener = options["listener"] || null;
		this.maxDepth = typeof options["maxDepth"] === "number" ? options["maxDepth"] : 100;
		this.maxTotalMergeKeys = typeof options["maxTotalMergeKeys"] === "number" ? options["maxTotalMergeKeys"] : 1e4;
		this.implicitTypes = this.schema.compiledImplicit;
		this.typeMap = this.schema.compiledTypeMap;
		this.length = input.length;
		this.position = 0;
		this.line = 0;
		this.lineStart = 0;
		this.lineIndent = 0;
		this.depth = 0;
		this.totalMergeKeys = 0;
		this.firstTabInLine = -1;
		this.documents = [];
		this.anchorMapTransactions = [];
	}
	function generateError(state, message) {
		const mark = {
			name: state.filename,
			buffer: state.input.slice(0, -1),
			position: state.position,
			line: state.line,
			column: state.position - state.lineStart
		};
		mark.snippet = makeSnippet(mark);
		return new YAMLException2(message, mark);
	}
	function throwError(state, message) {
		throw generateError(state, message);
	}
	function throwWarning(state, message) {
		if (state.onWarning) state.onWarning.call(null, generateError(state, message));
	}
	function storeAnchor(state, name, value) {
		const transactions = state.anchorMapTransactions;
		if (transactions.length !== 0) {
			const transaction = transactions[transactions.length - 1];
			if (!_hasOwnProperty.call(transaction, name)) transaction[name] = {
				existed: _hasOwnProperty.call(state.anchorMap, name),
				value: state.anchorMap[name]
			};
		}
		state.anchorMap[name] = value;
	}
	function beginAnchorTransaction(state) {
		state.anchorMapTransactions.push(/* @__PURE__ */ Object.create(null));
	}
	function commitAnchorTransaction(state) {
		const transaction = state.anchorMapTransactions.pop();
		const transactions = state.anchorMapTransactions;
		if (transactions.length === 0) return;
		const parent = transactions[transactions.length - 1];
		const names = Object.keys(transaction);
		for (let index = 0, length = names.length; index < length; index += 1) {
			const name = names[index];
			if (!_hasOwnProperty.call(parent, name)) parent[name] = transaction[name];
		}
	}
	function rollbackAnchorTransaction(state) {
		const transaction = state.anchorMapTransactions.pop();
		const names = Object.keys(transaction);
		for (let index = names.length - 1; index >= 0; index -= 1) {
			const entry = transaction[names[index]];
			if (entry.existed) state.anchorMap[names[index]] = entry.value;
			else delete state.anchorMap[names[index]];
		}
	}
	function snapshotState(state) {
		return {
			position: state.position,
			line: state.line,
			lineStart: state.lineStart,
			lineIndent: state.lineIndent,
			firstTabInLine: state.firstTabInLine,
			tag: state.tag,
			anchor: state.anchor,
			kind: state.kind,
			result: state.result
		};
	}
	function restoreState(state, snapshot) {
		state.position = snapshot.position;
		state.line = snapshot.line;
		state.lineStart = snapshot.lineStart;
		state.lineIndent = snapshot.lineIndent;
		state.firstTabInLine = snapshot.firstTabInLine;
		state.tag = snapshot.tag;
		state.anchor = snapshot.anchor;
		state.kind = snapshot.kind;
		state.result = snapshot.result;
	}
	const directiveHandlers = {
		YAML: function handleYamlDirective(state, name, args) {
			if (state.version !== null) throwError(state, "duplication of %YAML directive");
			if (args.length !== 1) throwError(state, "YAML directive accepts exactly one argument");
			const match = /^([0-9]+)\.([0-9]+)$/.exec(args[0]);
			if (match === null) throwError(state, "ill-formed argument of the YAML directive");
			const major = parseInt(match[1], 10);
			const minor = parseInt(match[2], 10);
			if (major !== 1) throwError(state, "unacceptable YAML version of the document");
			state.version = args[0];
			state.checkLineBreaks = minor < 2;
			if (minor !== 1 && minor !== 2) throwWarning(state, "unsupported YAML version of the document");
		},
		TAG: function handleTagDirective(state, name, args) {
			let prefix;
			if (args.length !== 2) throwError(state, "TAG directive accepts exactly two arguments");
			const handle = args[0];
			prefix = args[1];
			if (!PATTERN_TAG_HANDLE.test(handle)) throwError(state, "ill-formed tag handle (first argument) of the TAG directive");
			if (_hasOwnProperty.call(state.tagMap, handle)) throwError(state, "there is a previously declared suffix for \"" + handle + "\" tag handle");
			if (!PATTERN_TAG_URI.test(prefix)) throwError(state, "ill-formed tag prefix (second argument) of the TAG directive");
			try {
				prefix = decodeURIComponent(prefix);
			} catch (err) {
				throwError(state, "tag prefix is malformed: " + prefix);
			}
			state.tagMap[handle] = prefix;
		}
	};
	function captureSegment(state, start, end, checkJson) {
		if (start < end) {
			const _result = state.input.slice(start, end);
			if (checkJson) for (let _position = 0, _length = _result.length; _position < _length; _position += 1) {
				const _character = _result.charCodeAt(_position);
				if (!(_character === 9 || _character >= 32 && _character <= 1114111)) throwError(state, "expected valid JSON character");
			}
			else if (PATTERN_NON_PRINTABLE.test(_result)) throwError(state, "the stream contains non-printable characters");
			state.result += _result;
		}
	}
	function mergeMappings(state, destination, source, overridableKeys) {
		if (!common2.isObject(source)) throwError(state, "cannot merge mappings; the provided source object is unacceptable");
		const sourceKeys = Object.keys(source);
		for (let index = 0, quantity = sourceKeys.length; index < quantity; index += 1) {
			const key = sourceKeys[index];
			if (state.maxTotalMergeKeys !== -1 && ++state.totalMergeKeys > state.maxTotalMergeKeys) throwError(state, "merge keys exceeded maxTotalMergeKeys (" + state.maxTotalMergeKeys + ")");
			if (!_hasOwnProperty.call(destination, key)) {
				setProperty(destination, key, source[key]);
				overridableKeys[key] = true;
			}
		}
	}
	function storeMappingPair(state, _result, overridableKeys, keyTag, keyNode, valueNode, startLine, startLineStart, startPos) {
		if (Array.isArray(keyNode)) {
			keyNode = Array.prototype.slice.call(keyNode);
			for (let index = 0, quantity = keyNode.length; index < quantity; index += 1) {
				if (Array.isArray(keyNode[index])) throwError(state, "nested arrays are not supported inside keys");
				if (typeof keyNode === "object" && _class(keyNode[index]) === "[object Object]") keyNode[index] = "[object Object]";
			}
		}
		if (typeof keyNode === "object" && _class(keyNode) === "[object Object]") keyNode = "[object Object]";
		keyNode = String(keyNode);
		if (_result === null) _result = {};
		if (keyTag === "tag:yaml.org,2002:merge") if (Array.isArray(valueNode)) for (let index = 0, quantity = valueNode.length; index < quantity; index += 1) mergeMappings(state, _result, valueNode[index], overridableKeys);
		else mergeMappings(state, _result, valueNode, overridableKeys);
		else {
			if (!state.json && !_hasOwnProperty.call(overridableKeys, keyNode) && _hasOwnProperty.call(_result, keyNode)) {
				state.line = startLine || state.line;
				state.lineStart = startLineStart || state.lineStart;
				state.position = startPos || state.position;
				throwError(state, "duplicated mapping key");
			}
			setProperty(_result, keyNode, valueNode);
			delete overridableKeys[keyNode];
		}
		return _result;
	}
	function readLineBreak(state) {
		const ch = state.input.charCodeAt(state.position);
		if (ch === 10) state.position++;
		else if (ch === 13) {
			state.position++;
			if (state.input.charCodeAt(state.position) === 10) state.position++;
		} else throwError(state, "a line break is expected");
		state.line += 1;
		state.lineStart = state.position;
		state.firstTabInLine = -1;
	}
	function skipSeparationSpace(state, allowComments, checkIndent) {
		let lineBreaks = 0;
		let ch = state.input.charCodeAt(state.position);
		while (ch !== 0) {
			while (isWhiteSpace(ch)) {
				if (ch === 9 && state.firstTabInLine === -1) state.firstTabInLine = state.position;
				ch = state.input.charCodeAt(++state.position);
			}
			if (allowComments && ch === 35) do
				ch = state.input.charCodeAt(++state.position);
			while (ch !== 10 && ch !== 13 && ch !== 0);
			if (isEol(ch)) {
				readLineBreak(state);
				ch = state.input.charCodeAt(state.position);
				lineBreaks++;
				state.lineIndent = 0;
				while (ch === 32) {
					state.lineIndent++;
					ch = state.input.charCodeAt(++state.position);
				}
			} else break;
		}
		if (checkIndent !== -1 && lineBreaks !== 0 && state.lineIndent < checkIndent) throwWarning(state, "deficient indentation");
		return lineBreaks;
	}
	function testDocumentSeparator(state) {
		let _position = state.position;
		let ch = state.input.charCodeAt(_position);
		if ((ch === 45 || ch === 46) && ch === state.input.charCodeAt(_position + 1) && ch === state.input.charCodeAt(_position + 2)) {
			_position += 3;
			ch = state.input.charCodeAt(_position);
			if (ch === 0 || isWsOrEol(ch)) return true;
		}
		return false;
	}
	function writeFoldedLines(state, count) {
		if (count === 1) state.result += " ";
		else if (count > 1) state.result += common2.repeat("\n", count - 1);
	}
	function readPlainScalar(state, nodeIndent, withinFlowCollection) {
		let captureStart;
		let captureEnd;
		let hasPendingContent;
		let _line;
		let _lineStart;
		let _lineIndent;
		const _kind = state.kind;
		const _result = state.result;
		let ch = state.input.charCodeAt(state.position);
		if (isWsOrEol(ch) || isFlowIndicator(ch) || ch === 35 || ch === 38 || ch === 42 || ch === 33 || ch === 124 || ch === 62 || ch === 39 || ch === 34 || ch === 37 || ch === 64 || ch === 96) return false;
		if (ch === 63 || ch === 45) {
			const following = state.input.charCodeAt(state.position + 1);
			if (isWsOrEol(following) || withinFlowCollection && isFlowIndicator(following)) return false;
		}
		state.kind = "scalar";
		state.result = "";
		captureStart = captureEnd = state.position;
		hasPendingContent = false;
		while (ch !== 0) {
			if (ch === 58) {
				const following = state.input.charCodeAt(state.position + 1);
				if (isWsOrEol(following) || withinFlowCollection && isFlowIndicator(following)) break;
			} else if (ch === 35) {
				if (isWsOrEol(state.input.charCodeAt(state.position - 1))) break;
			} else if (state.position === state.lineStart && testDocumentSeparator(state) || withinFlowCollection && isFlowIndicator(ch)) break;
			else if (isEol(ch)) {
				_line = state.line;
				_lineStart = state.lineStart;
				_lineIndent = state.lineIndent;
				skipSeparationSpace(state, false, -1);
				if (state.lineIndent >= nodeIndent) {
					hasPendingContent = true;
					ch = state.input.charCodeAt(state.position);
					continue;
				} else {
					state.position = captureEnd;
					state.line = _line;
					state.lineStart = _lineStart;
					state.lineIndent = _lineIndent;
					break;
				}
			}
			if (hasPendingContent) {
				captureSegment(state, captureStart, captureEnd, false);
				writeFoldedLines(state, state.line - _line);
				captureStart = captureEnd = state.position;
				hasPendingContent = false;
			}
			if (!isWhiteSpace(ch)) captureEnd = state.position + 1;
			ch = state.input.charCodeAt(++state.position);
		}
		captureSegment(state, captureStart, captureEnd, false);
		if (state.result) return true;
		state.kind = _kind;
		state.result = _result;
		return false;
	}
	function readSingleQuotedScalar(state, nodeIndent) {
		let captureStart;
		let captureEnd;
		let ch = state.input.charCodeAt(state.position);
		if (ch !== 39) return false;
		state.kind = "scalar";
		state.result = "";
		state.position++;
		captureStart = captureEnd = state.position;
		while ((ch = state.input.charCodeAt(state.position)) !== 0) if (ch === 39) {
			captureSegment(state, captureStart, state.position, true);
			ch = state.input.charCodeAt(++state.position);
			if (ch === 39) {
				captureStart = state.position;
				state.position++;
				captureEnd = state.position;
			} else return true;
		} else if (isEol(ch)) {
			captureSegment(state, captureStart, captureEnd, true);
			writeFoldedLines(state, skipSeparationSpace(state, false, nodeIndent));
			captureStart = captureEnd = state.position;
		} else if (state.position === state.lineStart && testDocumentSeparator(state)) throwError(state, "unexpected end of the document within a single quoted scalar");
		else {
			state.position++;
			if (!isWhiteSpace(ch)) captureEnd = state.position;
		}
		throwError(state, "unexpected end of the stream within a single quoted scalar");
	}
	function readDoubleQuotedScalar(state, nodeIndent) {
		let captureStart;
		let captureEnd;
		let tmp;
		let ch = state.input.charCodeAt(state.position);
		if (ch !== 34) return false;
		state.kind = "scalar";
		state.result = "";
		state.position++;
		captureStart = captureEnd = state.position;
		while ((ch = state.input.charCodeAt(state.position)) !== 0) if (ch === 34) {
			captureSegment(state, captureStart, state.position, true);
			state.position++;
			return true;
		} else if (ch === 92) {
			captureSegment(state, captureStart, state.position, true);
			ch = state.input.charCodeAt(++state.position);
			if (isEol(ch)) skipSeparationSpace(state, false, nodeIndent);
			else if (ch < 256 && simpleEscapeCheck[ch]) {
				state.result += simpleEscapeMap[ch];
				state.position++;
			} else if ((tmp = escapedHexLen(ch)) > 0) {
				let hexLength = tmp;
				let hexResult = 0;
				for (; hexLength > 0; hexLength--) {
					ch = state.input.charCodeAt(++state.position);
					if ((tmp = fromHexCode(ch)) >= 0) hexResult = (hexResult << 4) + tmp;
					else throwError(state, "expected hexadecimal character");
				}
				state.result += charFromCodepoint(hexResult);
				state.position++;
			} else throwError(state, "unknown escape sequence");
			captureStart = captureEnd = state.position;
		} else if (isEol(ch)) {
			captureSegment(state, captureStart, captureEnd, true);
			writeFoldedLines(state, skipSeparationSpace(state, false, nodeIndent));
			captureStart = captureEnd = state.position;
		} else if (state.position === state.lineStart && testDocumentSeparator(state)) throwError(state, "unexpected end of the document within a double quoted scalar");
		else {
			state.position++;
			if (!isWhiteSpace(ch)) captureEnd = state.position;
		}
		throwError(state, "unexpected end of the stream within a double quoted scalar");
	}
	function readFlowCollection(state, nodeIndent) {
		let readNext = true;
		let _line;
		let _lineStart;
		let _pos;
		const _tag = state.tag;
		let _result;
		const _anchor = state.anchor;
		let terminator;
		let isPair;
		let isExplicitPair;
		let isMapping;
		const overridableKeys = /* @__PURE__ */ Object.create(null);
		let keyNode;
		let keyTag;
		let valueNode;
		let ch = state.input.charCodeAt(state.position);
		if (ch === 91) {
			terminator = 93;
			isMapping = false;
			_result = [];
		} else if (ch === 123) {
			terminator = 125;
			isMapping = true;
			_result = {};
		} else return false;
		if (state.anchor !== null) storeAnchor(state, state.anchor, _result);
		ch = state.input.charCodeAt(++state.position);
		while (ch !== 0) {
			skipSeparationSpace(state, true, nodeIndent);
			ch = state.input.charCodeAt(state.position);
			if (ch === terminator) {
				state.position++;
				state.tag = _tag;
				state.anchor = _anchor;
				state.kind = isMapping ? "mapping" : "sequence";
				state.result = _result;
				return true;
			} else if (!readNext) throwError(state, "missed comma between flow collection entries");
			else if (ch === 44) throwError(state, "expected the node content, but found ','");
			keyTag = keyNode = valueNode = null;
			isPair = isExplicitPair = false;
			if (ch === 63) {
				if (isWsOrEol(state.input.charCodeAt(state.position + 1))) {
					isPair = isExplicitPair = true;
					state.position++;
					skipSeparationSpace(state, true, nodeIndent);
				}
			}
			_line = state.line;
			_lineStart = state.lineStart;
			_pos = state.position;
			composeNode(state, nodeIndent, CONTEXT_FLOW_IN, false, true);
			keyTag = state.tag;
			keyNode = state.result;
			skipSeparationSpace(state, true, nodeIndent);
			ch = state.input.charCodeAt(state.position);
			if ((isExplicitPair || state.line === _line) && ch === 58) {
				isPair = true;
				ch = state.input.charCodeAt(++state.position);
				skipSeparationSpace(state, true, nodeIndent);
				composeNode(state, nodeIndent, CONTEXT_FLOW_IN, false, true);
				valueNode = state.result;
			}
			if (isMapping) storeMappingPair(state, _result, overridableKeys, keyTag, keyNode, valueNode, _line, _lineStart, _pos);
			else if (isPair) _result.push(storeMappingPair(state, null, overridableKeys, keyTag, keyNode, valueNode, _line, _lineStart, _pos));
			else _result.push(keyNode);
			skipSeparationSpace(state, true, nodeIndent);
			ch = state.input.charCodeAt(state.position);
			if (ch === 44) {
				readNext = true;
				ch = state.input.charCodeAt(++state.position);
			} else readNext = false;
		}
		throwError(state, "unexpected end of the stream within a flow collection");
	}
	function readBlockScalar(state, nodeIndent) {
		let folding;
		let chomping = CHOMPING_CLIP;
		let didReadContent = false;
		let detectedIndent = false;
		let textIndent = nodeIndent;
		let emptyLines = 0;
		let atMoreIndented = false;
		let tmp;
		let ch = state.input.charCodeAt(state.position);
		if (ch === 124) folding = false;
		else if (ch === 62) folding = true;
		else return false;
		state.kind = "scalar";
		state.result = "";
		while (ch !== 0) {
			ch = state.input.charCodeAt(++state.position);
			if (ch === 43 || ch === 45) if (CHOMPING_CLIP === chomping) chomping = ch === 43 ? CHOMPING_KEEP : CHOMPING_STRIP;
			else throwError(state, "repeat of a chomping mode identifier");
			else if ((tmp = fromDecimalCode(ch)) >= 0) if (tmp === 0) throwError(state, "bad explicit indentation width of a block scalar; it cannot be less than one");
			else if (!detectedIndent) {
				textIndent = nodeIndent + tmp - 1;
				detectedIndent = true;
			} else throwError(state, "repeat of an indentation width identifier");
			else break;
		}
		if (isWhiteSpace(ch)) {
			do
				ch = state.input.charCodeAt(++state.position);
			while (isWhiteSpace(ch));
			if (ch === 35) do
				ch = state.input.charCodeAt(++state.position);
			while (!isEol(ch) && ch !== 0);
		}
		while (ch !== 0) {
			readLineBreak(state);
			state.lineIndent = 0;
			ch = state.input.charCodeAt(state.position);
			while ((!detectedIndent || state.lineIndent < textIndent) && ch === 32) {
				state.lineIndent++;
				ch = state.input.charCodeAt(++state.position);
			}
			if (!detectedIndent && state.lineIndent > textIndent) textIndent = state.lineIndent;
			if (isEol(ch)) {
				emptyLines++;
				continue;
			}
			if (!detectedIndent && textIndent === 0) throwError(state, "missing indentation for block scalar");
			if (state.lineIndent < textIndent) {
				if (chomping === CHOMPING_KEEP) state.result += common2.repeat("\n", didReadContent ? 1 + emptyLines : emptyLines);
				else if (chomping === CHOMPING_CLIP) {
					if (didReadContent) state.result += "\n";
				}
				break;
			}
			if (folding) if (isWhiteSpace(ch)) {
				atMoreIndented = true;
				state.result += common2.repeat("\n", didReadContent ? 1 + emptyLines : emptyLines);
			} else if (atMoreIndented) {
				atMoreIndented = false;
				state.result += common2.repeat("\n", emptyLines + 1);
			} else if (emptyLines === 0) {
				if (didReadContent) state.result += " ";
			} else state.result += common2.repeat("\n", emptyLines);
			else state.result += common2.repeat("\n", didReadContent ? 1 + emptyLines : emptyLines);
			didReadContent = true;
			detectedIndent = true;
			emptyLines = 0;
			const captureStart = state.position;
			while (!isEol(ch) && ch !== 0) ch = state.input.charCodeAt(++state.position);
			captureSegment(state, captureStart, state.position, false);
		}
		return true;
	}
	function readBlockSequence(state, nodeIndent) {
		const _tag = state.tag;
		const _anchor = state.anchor;
		const _result = [];
		let detected = false;
		if (state.firstTabInLine !== -1) return false;
		if (state.anchor !== null) storeAnchor(state, state.anchor, _result);
		let ch = state.input.charCodeAt(state.position);
		while (ch !== 0) {
			if (state.firstTabInLine !== -1) {
				state.position = state.firstTabInLine;
				throwError(state, "tab characters must not be used in indentation");
			}
			if (ch !== 45) break;
			if (!isWsOrEol(state.input.charCodeAt(state.position + 1))) break;
			detected = true;
			state.position++;
			if (skipSeparationSpace(state, true, -1)) {
				if (state.lineIndent <= nodeIndent) {
					_result.push(null);
					ch = state.input.charCodeAt(state.position);
					continue;
				}
			}
			const _line = state.line;
			composeNode(state, nodeIndent, CONTEXT_BLOCK_IN, false, true);
			_result.push(state.result);
			skipSeparationSpace(state, true, -1);
			ch = state.input.charCodeAt(state.position);
			if ((state.line === _line || state.lineIndent > nodeIndent) && ch !== 0) throwError(state, "bad indentation of a sequence entry");
			else if (state.lineIndent < nodeIndent) break;
		}
		if (detected) {
			state.tag = _tag;
			state.anchor = _anchor;
			state.kind = "sequence";
			state.result = _result;
			return true;
		}
		return false;
	}
	function readBlockMapping(state, nodeIndent, flowIndent) {
		let allowCompact;
		let _keyLine;
		let _keyLineStart;
		let _keyPos;
		const _tag = state.tag;
		const _anchor = state.anchor;
		const _result = {};
		const overridableKeys = /* @__PURE__ */ Object.create(null);
		let keyTag = null;
		let keyNode = null;
		let valueNode = null;
		let atExplicitKey = false;
		let detected = false;
		if (state.firstTabInLine !== -1) return false;
		if (state.anchor !== null) storeAnchor(state, state.anchor, _result);
		let ch = state.input.charCodeAt(state.position);
		while (ch !== 0) {
			if (!atExplicitKey && state.firstTabInLine !== -1) {
				state.position = state.firstTabInLine;
				throwError(state, "tab characters must not be used in indentation");
			}
			const following = state.input.charCodeAt(state.position + 1);
			const _line = state.line;
			if ((ch === 63 || ch === 58) && isWsOrEol(following)) {
				if (ch === 63) {
					if (atExplicitKey) {
						storeMappingPair(state, _result, overridableKeys, keyTag, keyNode, null, _keyLine, _keyLineStart, _keyPos);
						keyTag = keyNode = valueNode = null;
					}
					detected = true;
					atExplicitKey = true;
					allowCompact = true;
				} else if (atExplicitKey) {
					atExplicitKey = false;
					allowCompact = true;
				} else throwError(state, "incomplete explicit mapping pair; a key node is missed; or followed by a non-tabulated empty line");
				state.position += 1;
				ch = following;
			} else {
				_keyLine = state.line;
				_keyLineStart = state.lineStart;
				_keyPos = state.position;
				if (!composeNode(state, flowIndent, CONTEXT_FLOW_OUT, false, true)) break;
				if (state.line === _line) {
					ch = state.input.charCodeAt(state.position);
					while (isWhiteSpace(ch)) ch = state.input.charCodeAt(++state.position);
					if (ch === 58) {
						ch = state.input.charCodeAt(++state.position);
						if (!isWsOrEol(ch)) throwError(state, "a whitespace character is expected after the key-value separator within a block mapping");
						if (atExplicitKey) {
							storeMappingPair(state, _result, overridableKeys, keyTag, keyNode, null, _keyLine, _keyLineStart, _keyPos);
							keyTag = keyNode = valueNode = null;
						}
						detected = true;
						atExplicitKey = false;
						allowCompact = false;
						keyTag = state.tag;
						keyNode = state.result;
					} else if (detected) throwError(state, "can not read an implicit mapping pair; a colon is missed");
					else {
						state.tag = _tag;
						state.anchor = _anchor;
						return true;
					}
				} else if (detected) throwError(state, "can not read a block mapping entry; a multiline key may not be an implicit key");
				else {
					state.tag = _tag;
					state.anchor = _anchor;
					return true;
				}
			}
			if (state.line === _line || state.lineIndent > nodeIndent) {
				if (atExplicitKey) {
					_keyLine = state.line;
					_keyLineStart = state.lineStart;
					_keyPos = state.position;
				}
				if (composeNode(state, nodeIndent, CONTEXT_BLOCK_OUT, true, allowCompact)) if (atExplicitKey) keyNode = state.result;
				else valueNode = state.result;
				if (!atExplicitKey) {
					storeMappingPair(state, _result, overridableKeys, keyTag, keyNode, valueNode, _keyLine, _keyLineStart, _keyPos);
					keyTag = keyNode = valueNode = null;
				}
				skipSeparationSpace(state, true, -1);
				ch = state.input.charCodeAt(state.position);
			}
			if ((state.line === _line || state.lineIndent > nodeIndent) && ch !== 0) throwError(state, "bad indentation of a mapping entry");
			else if (state.lineIndent < nodeIndent) break;
		}
		if (atExplicitKey) storeMappingPair(state, _result, overridableKeys, keyTag, keyNode, null, _keyLine, _keyLineStart, _keyPos);
		if (detected) {
			state.tag = _tag;
			state.anchor = _anchor;
			state.kind = "mapping";
			state.result = _result;
		}
		return detected;
	}
	function readTagProperty(state) {
		let isVerbatim = false;
		let isNamed = false;
		let tagHandle;
		let tagName;
		let ch = state.input.charCodeAt(state.position);
		if (ch !== 33) return false;
		if (state.tag !== null) throwError(state, "duplication of a tag property");
		ch = state.input.charCodeAt(++state.position);
		if (ch === 60) {
			isVerbatim = true;
			ch = state.input.charCodeAt(++state.position);
		} else if (ch === 33) {
			isNamed = true;
			tagHandle = "!!";
			ch = state.input.charCodeAt(++state.position);
		} else tagHandle = "!";
		let _position = state.position;
		if (isVerbatim) {
			do
				ch = state.input.charCodeAt(++state.position);
			while (ch !== 0 && ch !== 62);
			if (state.position < state.length) {
				tagName = state.input.slice(_position, state.position);
				ch = state.input.charCodeAt(++state.position);
			} else throwError(state, "unexpected end of the stream within a verbatim tag");
		} else {
			while (ch !== 0 && !isWsOrEol(ch)) {
				if (ch === 33) if (!isNamed) {
					tagHandle = state.input.slice(_position - 1, state.position + 1);
					if (!PATTERN_TAG_HANDLE.test(tagHandle)) throwError(state, "named tag handle cannot contain such characters");
					isNamed = true;
					_position = state.position + 1;
				} else throwError(state, "tag suffix cannot contain exclamation marks");
				ch = state.input.charCodeAt(++state.position);
			}
			tagName = state.input.slice(_position, state.position);
			if (PATTERN_FLOW_INDICATORS.test(tagName)) throwError(state, "tag suffix cannot contain flow indicator characters");
		}
		if (tagName && !PATTERN_TAG_URI.test(tagName)) throwError(state, "tag name cannot contain such characters: " + tagName);
		try {
			tagName = decodeURIComponent(tagName);
		} catch (err) {
			throwError(state, "tag name is malformed: " + tagName);
		}
		if (isVerbatim) state.tag = tagName;
		else if (_hasOwnProperty.call(state.tagMap, tagHandle)) state.tag = state.tagMap[tagHandle] + tagName;
		else if (tagHandle === "!") state.tag = "!" + tagName;
		else if (tagHandle === "!!") state.tag = "tag:yaml.org,2002:" + tagName;
		else throwError(state, "undeclared tag handle \"" + tagHandle + "\"");
		return true;
	}
	function readAnchorProperty(state) {
		let ch = state.input.charCodeAt(state.position);
		if (ch !== 38) return false;
		if (state.anchor !== null) throwError(state, "duplication of an anchor property");
		ch = state.input.charCodeAt(++state.position);
		const _position = state.position;
		while (ch !== 0 && !isWsOrEol(ch) && !isFlowIndicator(ch)) ch = state.input.charCodeAt(++state.position);
		if (state.position === _position) throwError(state, "name of an anchor node must contain at least one character");
		state.anchor = state.input.slice(_position, state.position);
		return true;
	}
	function readAlias(state) {
		let ch = state.input.charCodeAt(state.position);
		if (ch !== 42) return false;
		ch = state.input.charCodeAt(++state.position);
		const _position = state.position;
		while (ch !== 0 && !isWsOrEol(ch) && !isFlowIndicator(ch)) ch = state.input.charCodeAt(++state.position);
		if (state.position === _position) throwError(state, "name of an alias node must contain at least one character");
		const alias = state.input.slice(_position, state.position);
		if (!_hasOwnProperty.call(state.anchorMap, alias)) throwError(state, "unidentified alias \"" + alias + "\"");
		state.result = state.anchorMap[alias];
		skipSeparationSpace(state, true, -1);
		return true;
	}
	function tryReadBlockMappingFromProperty(state, propertyStart, nodeIndent, flowIndent) {
		const fallbackState = snapshotState(state);
		beginAnchorTransaction(state);
		restoreState(state, propertyStart);
		state.tag = null;
		state.anchor = null;
		state.kind = null;
		state.result = null;
		if (readBlockMapping(state, nodeIndent, flowIndent) && state.kind === "mapping") {
			commitAnchorTransaction(state);
			return true;
		}
		rollbackAnchorTransaction(state);
		restoreState(state, fallbackState);
		return false;
	}
	function composeNode(state, parentIndent, nodeContext, allowToSeek, allowCompact) {
		let allowBlockScalars;
		let allowBlockCollections;
		let indentStatus = 1;
		let atNewLine = false;
		let hasContent = false;
		let propertyStart = null;
		let type2;
		let flowIndent;
		let blockIndent;
		if (state.depth >= state.maxDepth) throwError(state, "nesting exceeded maxDepth (" + state.maxDepth + ")");
		state.depth += 1;
		if (state.listener !== null) state.listener("open", state);
		state.tag = null;
		state.anchor = null;
		state.kind = null;
		state.result = null;
		const allowBlockStyles = allowBlockScalars = allowBlockCollections = CONTEXT_BLOCK_OUT === nodeContext || CONTEXT_BLOCK_IN === nodeContext;
		if (allowToSeek) {
			if (skipSeparationSpace(state, true, -1)) {
				atNewLine = true;
				if (state.lineIndent > parentIndent) indentStatus = 1;
				else if (state.lineIndent === parentIndent) indentStatus = 0;
				else if (state.lineIndent < parentIndent) indentStatus = -1;
			}
		}
		if (indentStatus === 1) while (true) {
			const ch = state.input.charCodeAt(state.position);
			const propertyState = snapshotState(state);
			if (atNewLine && (ch === 33 && state.tag !== null || ch === 38 && state.anchor !== null)) break;
			if (!readTagProperty(state) && !readAnchorProperty(state)) break;
			if (propertyStart === null) propertyStart = propertyState;
			if (skipSeparationSpace(state, true, -1)) {
				atNewLine = true;
				allowBlockCollections = allowBlockStyles;
				if (state.lineIndent > parentIndent) indentStatus = 1;
				else if (state.lineIndent === parentIndent) indentStatus = 0;
				else if (state.lineIndent < parentIndent) indentStatus = -1;
			} else allowBlockCollections = false;
		}
		if (allowBlockCollections) allowBlockCollections = atNewLine || allowCompact;
		if (indentStatus === 1 || CONTEXT_BLOCK_OUT === nodeContext) {
			if (CONTEXT_FLOW_IN === nodeContext || CONTEXT_FLOW_OUT === nodeContext) flowIndent = parentIndent;
			else flowIndent = parentIndent + 1;
			blockIndent = state.position - state.lineStart;
			if (indentStatus === 1) if (allowBlockCollections && (readBlockSequence(state, blockIndent) || readBlockMapping(state, blockIndent, flowIndent)) || readFlowCollection(state, flowIndent)) hasContent = true;
			else {
				const ch = state.input.charCodeAt(state.position);
				if (propertyStart !== null && allowBlockStyles && !allowBlockCollections && ch !== 124 && ch !== 62 && tryReadBlockMappingFromProperty(state, propertyStart, propertyStart.position - propertyStart.lineStart, flowIndent)) hasContent = true;
				else if (allowBlockScalars && readBlockScalar(state, flowIndent) || readSingleQuotedScalar(state, flowIndent) || readDoubleQuotedScalar(state, flowIndent)) hasContent = true;
				else if (readAlias(state)) {
					hasContent = true;
					if (state.tag !== null || state.anchor !== null) throwError(state, "alias node should not have any properties");
				} else if (readPlainScalar(state, flowIndent, CONTEXT_FLOW_IN === nodeContext)) {
					hasContent = true;
					if (state.tag === null) state.tag = "?";
				}
				if (state.anchor !== null) storeAnchor(state, state.anchor, state.result);
			}
			else if (indentStatus === 0) hasContent = allowBlockCollections && readBlockSequence(state, blockIndent);
		}
		if (state.tag === null) {
			if (state.anchor !== null) storeAnchor(state, state.anchor, state.result);
		} else if (state.tag === "?") {
			if (state.result !== null && state.kind !== "scalar") throwError(state, "unacceptable node kind for !<?> tag; it should be \"scalar\", not \"" + state.kind + "\"");
			for (let typeIndex = 0, typeQuantity = state.implicitTypes.length; typeIndex < typeQuantity; typeIndex += 1) {
				type2 = state.implicitTypes[typeIndex];
				if (type2.resolve(state.result)) {
					state.result = type2.construct(state.result);
					state.tag = type2.tag;
					if (state.anchor !== null) storeAnchor(state, state.anchor, state.result);
					break;
				}
			}
		} else if (state.tag !== "!") {
			if (_hasOwnProperty.call(state.typeMap[state.kind || "fallback"], state.tag)) type2 = state.typeMap[state.kind || "fallback"][state.tag];
			else {
				type2 = null;
				const typeList = state.typeMap.multi[state.kind || "fallback"];
				for (let typeIndex = 0, typeQuantity = typeList.length; typeIndex < typeQuantity; typeIndex += 1) if (state.tag.slice(0, typeList[typeIndex].tag.length) === typeList[typeIndex].tag) {
					type2 = typeList[typeIndex];
					break;
				}
			}
			if (!type2) throwError(state, "unknown tag !<" + state.tag + ">");
			if (state.result !== null && type2.kind !== state.kind) throwError(state, "unacceptable node kind for !<" + state.tag + "> tag; it should be \"" + type2.kind + "\", not \"" + state.kind + "\"");
			if (!type2.resolve(state.result, state.tag)) throwError(state, "cannot resolve a node with !<" + state.tag + "> explicit tag");
			else {
				state.result = type2.construct(state.result, state.tag);
				if (state.anchor !== null) storeAnchor(state, state.anchor, state.result);
			}
		}
		if (state.listener !== null) state.listener("close", state);
		state.depth -= 1;
		return state.tag !== null || state.anchor !== null || hasContent;
	}
	function readDocument(state) {
		const documentStart = state.position;
		let hasDirectives = false;
		let ch;
		state.version = null;
		state.checkLineBreaks = state.legacy;
		state.tagMap = /* @__PURE__ */ Object.create(null);
		state.anchorMap = /* @__PURE__ */ Object.create(null);
		while ((ch = state.input.charCodeAt(state.position)) !== 0) {
			skipSeparationSpace(state, true, -1);
			ch = state.input.charCodeAt(state.position);
			if (state.lineIndent > 0 || ch !== 37) break;
			hasDirectives = true;
			ch = state.input.charCodeAt(++state.position);
			let _position = state.position;
			while (ch !== 0 && !isWsOrEol(ch)) ch = state.input.charCodeAt(++state.position);
			const directiveName = state.input.slice(_position, state.position);
			const directiveArgs = [];
			if (directiveName.length < 1) throwError(state, "directive name must not be less than one character in length");
			while (ch !== 0) {
				while (isWhiteSpace(ch)) ch = state.input.charCodeAt(++state.position);
				if (ch === 35) {
					do
						ch = state.input.charCodeAt(++state.position);
					while (ch !== 0 && !isEol(ch));
					break;
				}
				if (isEol(ch)) break;
				_position = state.position;
				while (ch !== 0 && !isWsOrEol(ch)) ch = state.input.charCodeAt(++state.position);
				directiveArgs.push(state.input.slice(_position, state.position));
			}
			if (ch !== 0) readLineBreak(state);
			if (_hasOwnProperty.call(directiveHandlers, directiveName)) directiveHandlers[directiveName](state, directiveName, directiveArgs);
			else throwWarning(state, "unknown document directive \"" + directiveName + "\"");
		}
		skipSeparationSpace(state, true, -1);
		if (state.lineIndent === 0 && state.input.charCodeAt(state.position) === 45 && state.input.charCodeAt(state.position + 1) === 45 && state.input.charCodeAt(state.position + 2) === 45) {
			state.position += 3;
			skipSeparationSpace(state, true, -1);
		} else if (hasDirectives) throwError(state, "directives end mark is expected");
		composeNode(state, state.lineIndent - 1, CONTEXT_BLOCK_OUT, false, true);
		skipSeparationSpace(state, true, -1);
		if (state.checkLineBreaks && PATTERN_NON_ASCII_LINE_BREAKS.test(state.input.slice(documentStart, state.position))) throwWarning(state, "non-ASCII line breaks are interpreted as content");
		state.documents.push(state.result);
		if (state.position === state.lineStart && testDocumentSeparator(state)) {
			if (state.input.charCodeAt(state.position) === 46) {
				state.position += 3;
				skipSeparationSpace(state, true, -1);
			}
			return;
		}
		if (state.position < state.length - 1) throwError(state, "end of the stream or a document separator is expected");
	}
	function loadDocuments(input, options) {
		input = String(input);
		options = options || {};
		if (input.length !== 0) {
			if (input.charCodeAt(input.length - 1) !== 10 && input.charCodeAt(input.length - 1) !== 13) input += "\n";
			if (input.charCodeAt(0) === 65279) input = input.slice(1);
		}
		const state = new State(input, options);
		const nullpos = input.indexOf("\0");
		if (nullpos !== -1) {
			state.position = nullpos;
			throwError(state, "null byte is not allowed in input");
		}
		state.input += "\0";
		while (state.input.charCodeAt(state.position) === 32) {
			state.lineIndent += 1;
			state.position += 1;
		}
		while (state.position < state.length - 1) readDocument(state);
		return state.documents;
	}
	function loadAll2(input, iterator, options) {
		if (iterator !== null && typeof iterator === "object" && typeof options === "undefined") {
			options = iterator;
			iterator = null;
		}
		const documents = loadDocuments(input, options);
		if (typeof iterator !== "function") return documents;
		for (let index = 0, length = documents.length; index < length; index += 1) iterator(documents[index]);
	}
	function load2(input, options) {
		const documents = loadDocuments(input, options);
		if (documents.length === 0) return;
		else if (documents.length === 1) return documents[0];
		throw new YAMLException2("expected a single document in the stream, but found more");
	}
	loader.loadAll = loadAll2;
	loader.load = load2;
	return loader;
}
var dumper = {};
var hasRequiredDumper;
function requireDumper() {
	if (hasRequiredDumper) return dumper;
	hasRequiredDumper = 1;
	const common2 = requireCommon();
	const YAMLException2 = requireException();
	const DEFAULT_SCHEMA2 = require_default();
	const _toString = Object.prototype.toString;
	const _hasOwnProperty = Object.prototype.hasOwnProperty;
	const CHAR_BOM = 65279;
	const CHAR_TAB = 9;
	const CHAR_LINE_FEED = 10;
	const CHAR_CARRIAGE_RETURN = 13;
	const CHAR_SPACE = 32;
	const CHAR_EXCLAMATION = 33;
	const CHAR_DOUBLE_QUOTE = 34;
	const CHAR_SHARP = 35;
	const CHAR_PERCENT = 37;
	const CHAR_AMPERSAND = 38;
	const CHAR_SINGLE_QUOTE = 39;
	const CHAR_ASTERISK = 42;
	const CHAR_COMMA = 44;
	const CHAR_MINUS = 45;
	const CHAR_COLON = 58;
	const CHAR_EQUALS = 61;
	const CHAR_GREATER_THAN = 62;
	const CHAR_QUESTION = 63;
	const CHAR_COMMERCIAL_AT = 64;
	const CHAR_LEFT_SQUARE_BRACKET = 91;
	const CHAR_RIGHT_SQUARE_BRACKET = 93;
	const CHAR_GRAVE_ACCENT = 96;
	const CHAR_LEFT_CURLY_BRACKET = 123;
	const CHAR_VERTICAL_LINE = 124;
	const CHAR_RIGHT_CURLY_BRACKET = 125;
	const ESCAPE_SEQUENCES = {};
	ESCAPE_SEQUENCES[0] = "\\0";
	ESCAPE_SEQUENCES[7] = "\\a";
	ESCAPE_SEQUENCES[8] = "\\b";
	ESCAPE_SEQUENCES[9] = "\\t";
	ESCAPE_SEQUENCES[10] = "\\n";
	ESCAPE_SEQUENCES[11] = "\\v";
	ESCAPE_SEQUENCES[12] = "\\f";
	ESCAPE_SEQUENCES[13] = "\\r";
	ESCAPE_SEQUENCES[27] = "\\e";
	ESCAPE_SEQUENCES[34] = "\\\"";
	ESCAPE_SEQUENCES[92] = "\\\\";
	ESCAPE_SEQUENCES[133] = "\\N";
	ESCAPE_SEQUENCES[160] = "\\_";
	ESCAPE_SEQUENCES[8232] = "\\L";
	ESCAPE_SEQUENCES[8233] = "\\P";
	const DEPRECATED_BOOLEANS_SYNTAX = [
		"y",
		"Y",
		"yes",
		"Yes",
		"YES",
		"on",
		"On",
		"ON",
		"n",
		"N",
		"no",
		"No",
		"NO",
		"off",
		"Off",
		"OFF"
	];
	const DEPRECATED_BASE60_SYNTAX = /^[-+]?[0-9_]+(?::[0-9_]+)+(?:\.[0-9_]*)?$/;
	function compileStyleMap(schema2, map2) {
		if (map2 === null) return {};
		const result = {};
		const keys = Object.keys(map2);
		for (let index = 0, length = keys.length; index < length; index += 1) {
			let tag = keys[index];
			let style = String(map2[tag]);
			if (tag.slice(0, 2) === "!!") tag = "tag:yaml.org,2002:" + tag.slice(2);
			const type2 = schema2.compiledTypeMap["fallback"][tag];
			if (type2 && _hasOwnProperty.call(type2.styleAliases, style)) style = type2.styleAliases[style];
			result[tag] = style;
		}
		return result;
	}
	function encodeHex(character) {
		let handle;
		let length;
		const string = character.toString(16).toUpperCase();
		if (character <= 255) {
			handle = "x";
			length = 2;
		} else if (character <= 65535) {
			handle = "u";
			length = 4;
		} else if (character <= 4294967295) {
			handle = "U";
			length = 8;
		} else throw new YAMLException2("code point within a string may not be greater than 0xFFFFFFFF");
		return "\\" + handle + common2.repeat("0", length - string.length) + string;
	}
	const QUOTING_TYPE_SINGLE = 1;
	const QUOTING_TYPE_DOUBLE = 2;
	function State(options) {
		this.schema = options["schema"] || DEFAULT_SCHEMA2;
		this.indent = Math.max(1, options["indent"] || 2);
		this.noArrayIndent = options["noArrayIndent"] || false;
		this.skipInvalid = options["skipInvalid"] || false;
		this.flowLevel = common2.isNothing(options["flowLevel"]) ? -1 : options["flowLevel"];
		this.styleMap = compileStyleMap(this.schema, options["styles"] || null);
		this.sortKeys = options["sortKeys"] || false;
		this.lineWidth = options["lineWidth"] || 80;
		this.noRefs = options["noRefs"] || false;
		this.noCompatMode = options["noCompatMode"] || false;
		this.condenseFlow = options["condenseFlow"] || false;
		this.quotingType = options["quotingType"] === "\"" ? QUOTING_TYPE_DOUBLE : QUOTING_TYPE_SINGLE;
		this.forceQuotes = options["forceQuotes"] || false;
		this.replacer = typeof options["replacer"] === "function" ? options["replacer"] : null;
		this.implicitTypes = this.schema.compiledImplicit;
		this.explicitTypes = this.schema.compiledExplicit;
		this.tag = null;
		this.result = "";
		this.duplicates = [];
		this.usedDuplicates = null;
	}
	function indentString(string, spaces) {
		const ind = common2.repeat(" ", spaces);
		let position = 0;
		let result = "";
		const length = string.length;
		while (position < length) {
			let line;
			const next = string.indexOf("\n", position);
			if (next === -1) {
				line = string.slice(position);
				position = length;
			} else {
				line = string.slice(position, next + 1);
				position = next + 1;
			}
			if (line.length && line !== "\n") result += ind;
			result += line;
		}
		return result;
	}
	function generateNextLine(state, level) {
		return "\n" + common2.repeat(" ", state.indent * level);
	}
	function testImplicitResolving(state, str2) {
		for (let index = 0, length = state.implicitTypes.length; index < length; index += 1) if (state.implicitTypes[index].resolve(str2)) return true;
		return false;
	}
	function isWhitespace(c) {
		return c === CHAR_SPACE || c === CHAR_TAB;
	}
	function isPrintable(c) {
		return c >= 32 && c <= 126 || c >= 161 && c <= 55295 && c !== 8232 && c !== 8233 || c >= 57344 && c <= 65533 && c !== CHAR_BOM || c >= 65536 && c <= 1114111;
	}
	function isNsCharOrWhitespace(c) {
		return isPrintable(c) && c !== CHAR_BOM && c !== CHAR_CARRIAGE_RETURN && c !== CHAR_LINE_FEED;
	}
	function isPlainSafe(c, prev, inblock) {
		const cIsNsCharOrWhitespace = isNsCharOrWhitespace(c);
		const cIsNsChar = cIsNsCharOrWhitespace && !isWhitespace(c);
		return (inblock ? cIsNsCharOrWhitespace : cIsNsCharOrWhitespace && c !== CHAR_COMMA && c !== CHAR_LEFT_SQUARE_BRACKET && c !== CHAR_RIGHT_SQUARE_BRACKET && c !== CHAR_LEFT_CURLY_BRACKET && c !== CHAR_RIGHT_CURLY_BRACKET) && c !== CHAR_SHARP && !(prev === CHAR_COLON && !cIsNsChar) || isNsCharOrWhitespace(prev) && !isWhitespace(prev) && c === CHAR_SHARP || prev === CHAR_COLON && cIsNsChar;
	}
	function isPlainSafeFirst(c) {
		return isPrintable(c) && c !== CHAR_BOM && !isWhitespace(c) && c !== CHAR_MINUS && c !== CHAR_QUESTION && c !== CHAR_COLON && c !== CHAR_COMMA && c !== CHAR_LEFT_SQUARE_BRACKET && c !== CHAR_RIGHT_SQUARE_BRACKET && c !== CHAR_LEFT_CURLY_BRACKET && c !== CHAR_RIGHT_CURLY_BRACKET && c !== CHAR_SHARP && c !== CHAR_AMPERSAND && c !== CHAR_ASTERISK && c !== CHAR_EXCLAMATION && c !== CHAR_VERTICAL_LINE && c !== CHAR_EQUALS && c !== CHAR_GREATER_THAN && c !== CHAR_SINGLE_QUOTE && c !== CHAR_DOUBLE_QUOTE && c !== CHAR_PERCENT && c !== CHAR_COMMERCIAL_AT && c !== CHAR_GRAVE_ACCENT;
	}
	function isPlainSafeLast(c) {
		return !isWhitespace(c) && c !== CHAR_COLON;
	}
	function codePointAt(string, pos) {
		const first = string.charCodeAt(pos);
		let second;
		if (first >= 55296 && first <= 56319 && pos + 1 < string.length) {
			second = string.charCodeAt(pos + 1);
			if (second >= 56320 && second <= 57343) return (first - 55296) * 1024 + second - 56320 + 65536;
		}
		return first;
	}
	function needIndentIndicator(string) {
		return /^\n* /.test(string);
	}
	const STYLE_PLAIN = 1;
	const STYLE_SINGLE = 2;
	const STYLE_LITERAL = 3;
	const STYLE_FOLDED = 4;
	const STYLE_DOUBLE = 5;
	function chooseScalarStyle(string, singleLineOnly, indentPerLevel, lineWidth, testAmbiguousType, quotingType, forceQuotes, inblock) {
		let i;
		let char = 0;
		let prevChar = null;
		let hasLineBreak = false;
		let hasFoldableLine = false;
		const shouldTrackWidth = lineWidth !== -1;
		let previousLineBreak = -1;
		let plain = isPlainSafeFirst(codePointAt(string, 0)) && isPlainSafeLast(codePointAt(string, string.length - 1));
		if (singleLineOnly || forceQuotes) for (i = 0; i < string.length; char >= 65536 ? i += 2 : i++) {
			char = codePointAt(string, i);
			if (!isPrintable(char)) return STYLE_DOUBLE;
			plain = plain && isPlainSafe(char, prevChar, inblock);
			prevChar = char;
		}
		else {
			for (i = 0; i < string.length; char >= 65536 ? i += 2 : i++) {
				char = codePointAt(string, i);
				if (char === CHAR_LINE_FEED) {
					hasLineBreak = true;
					if (shouldTrackWidth) {
						hasFoldableLine = hasFoldableLine || i - previousLineBreak - 1 > lineWidth && string[previousLineBreak + 1] !== " ";
						previousLineBreak = i;
					}
				} else if (!isPrintable(char)) return STYLE_DOUBLE;
				plain = plain && isPlainSafe(char, prevChar, inblock);
				prevChar = char;
			}
			hasFoldableLine = hasFoldableLine || shouldTrackWidth && i - previousLineBreak - 1 > lineWidth && string[previousLineBreak + 1] !== " ";
		}
		if (!hasLineBreak && !hasFoldableLine) {
			if (plain && !forceQuotes && !testAmbiguousType(string)) return STYLE_PLAIN;
			return quotingType === QUOTING_TYPE_DOUBLE ? STYLE_DOUBLE : STYLE_SINGLE;
		}
		if (indentPerLevel > 9 && needIndentIndicator(string)) return STYLE_DOUBLE;
		if (!forceQuotes) return hasFoldableLine ? STYLE_FOLDED : STYLE_LITERAL;
		return quotingType === QUOTING_TYPE_DOUBLE ? STYLE_DOUBLE : STYLE_SINGLE;
	}
	function writeScalar(state, string, level, iskey, inblock) {
		state.dump = (function() {
			if (string.length === 0) return state.quotingType === QUOTING_TYPE_DOUBLE ? "\"\"" : "''";
			if (!state.noCompatMode) {
				if (DEPRECATED_BOOLEANS_SYNTAX.indexOf(string) !== -1 || DEPRECATED_BASE60_SYNTAX.test(string)) return state.quotingType === QUOTING_TYPE_DOUBLE ? "\"" + string + "\"" : "'" + string + "'";
			}
			const indent = state.indent * Math.max(1, level);
			const lineWidth = state.lineWidth === -1 ? -1 : Math.max(Math.min(state.lineWidth, 40), state.lineWidth - indent);
			const singleLineOnly = iskey || state.flowLevel > -1 && level >= state.flowLevel;
			function testAmbiguity(string2) {
				return testImplicitResolving(state, string2);
			}
			switch (chooseScalarStyle(string, singleLineOnly, state.indent, lineWidth, testAmbiguity, state.quotingType, state.forceQuotes && !iskey, inblock)) {
				case STYLE_PLAIN: return string;
				case STYLE_SINGLE: return "'" + string.replace(/'/g, "''") + "'";
				case STYLE_LITERAL: return "|" + blockHeader(string, state.indent) + dropEndingNewline(indentString(string, indent));
				case STYLE_FOLDED: return ">" + blockHeader(string, state.indent) + dropEndingNewline(indentString(foldString(string, lineWidth), indent));
				case STYLE_DOUBLE: return "\"" + escapeString(string) + "\"";
				default: throw new YAMLException2("impossible error: invalid scalar style");
			}
		})();
	}
	function blockHeader(string, indentPerLevel) {
		const indentIndicator = needIndentIndicator(string) ? String(indentPerLevel) : "";
		const clip = string[string.length - 1] === "\n";
		return indentIndicator + (clip && (string[string.length - 2] === "\n" || string === "\n") ? "+" : clip ? "" : "-") + "\n";
	}
	function dropEndingNewline(string) {
		return string[string.length - 1] === "\n" ? string.slice(0, -1) : string;
	}
	function foldString(string, width) {
		const lineRe = /(\n+)([^\n]*)/g;
		let result = (function() {
			let nextLF = string.indexOf("\n");
			nextLF = nextLF !== -1 ? nextLF : string.length;
			lineRe.lastIndex = nextLF;
			return foldLine(string.slice(0, nextLF), width);
		})();
		let prevMoreIndented = string[0] === "\n" || string[0] === " ";
		let moreIndented;
		let match;
		while (match = lineRe.exec(string)) {
			const prefix = match[1];
			const line = match[2];
			moreIndented = line[0] === " ";
			result += prefix + (!prevMoreIndented && !moreIndented && line !== "" ? "\n" : "") + foldLine(line, width);
			prevMoreIndented = moreIndented;
		}
		return result;
	}
	function foldLine(line, width) {
		if (line === "" || line[0] === " ") return line;
		const breakRe = / [^ ]/g;
		let match;
		let start = 0;
		let end;
		let curr = 0;
		let next = 0;
		let result = "";
		while (match = breakRe.exec(line)) {
			next = match.index;
			if (next - start > width) {
				end = curr > start ? curr : next;
				result += "\n" + line.slice(start, end);
				start = end + 1;
			}
			curr = next;
		}
		result += "\n";
		if (line.length - start > width && curr > start) result += line.slice(start, curr) + "\n" + line.slice(curr + 1);
		else result += line.slice(start);
		return result.slice(1);
	}
	function escapeString(string) {
		let result = "";
		let char = 0;
		for (let i = 0; i < string.length; char >= 65536 ? i += 2 : i++) {
			char = codePointAt(string, i);
			const escapeSeq = ESCAPE_SEQUENCES[char];
			if (!escapeSeq && isPrintable(char)) {
				result += string[i];
				if (char >= 65536) result += string[i + 1];
			} else result += escapeSeq || encodeHex(char);
		}
		return result;
	}
	function writeFlowSequence(state, level, object) {
		let _result = "";
		const _tag = state.tag;
		for (let index = 0, length = object.length; index < length; index += 1) {
			let value = object[index];
			if (state.replacer) value = state.replacer.call(object, String(index), value);
			if (writeNode(state, level, value, false, false) || typeof value === "undefined" && writeNode(state, level, null, false, false)) {
				if (_result !== "") _result += "," + (!state.condenseFlow ? " " : "");
				_result += state.dump;
			}
		}
		state.tag = _tag;
		state.dump = "[" + _result + "]";
	}
	function writeBlockSequence(state, level, object, compact) {
		let _result = "";
		const _tag = state.tag;
		for (let index = 0, length = object.length; index < length; index += 1) {
			let value = object[index];
			if (state.replacer) value = state.replacer.call(object, String(index), value);
			if (writeNode(state, level + 1, value, true, true, false, true) || typeof value === "undefined" && writeNode(state, level + 1, null, true, true, false, true)) {
				if (!compact || _result !== "") _result += generateNextLine(state, level);
				if (state.dump && CHAR_LINE_FEED === state.dump.charCodeAt(0)) _result += "-";
				else _result += "- ";
				_result += state.dump;
			}
		}
		state.tag = _tag;
		state.dump = _result || "[]";
	}
	function writeFlowMapping(state, level, object) {
		let _result = "";
		const _tag = state.tag;
		const objectKeyList = Object.keys(object);
		for (let index = 0, length = objectKeyList.length; index < length; index += 1) {
			let pairBuffer = "";
			if (_result !== "") pairBuffer += ", ";
			if (state.condenseFlow) pairBuffer += "\"";
			const objectKey = objectKeyList[index];
			let objectValue = object[objectKey];
			if (state.replacer) objectValue = state.replacer.call(object, objectKey, objectValue);
			if (!writeNode(state, level, objectKey, false, false)) continue;
			if (state.dump.length > 1024) pairBuffer += "? ";
			pairBuffer += state.dump + (state.condenseFlow ? "\"" : "") + ":" + (state.condenseFlow ? "" : " ");
			if (!writeNode(state, level, objectValue, false, false)) continue;
			pairBuffer += state.dump;
			_result += pairBuffer;
		}
		state.tag = _tag;
		state.dump = "{" + _result + "}";
	}
	function writeBlockMapping(state, level, object, compact) {
		let _result = "";
		const _tag = state.tag;
		const objectKeyList = Object.keys(object);
		if (state.sortKeys === true) objectKeyList.sort();
		else if (typeof state.sortKeys === "function") objectKeyList.sort(state.sortKeys);
		else if (state.sortKeys) throw new YAMLException2("sortKeys must be a boolean or a function");
		for (let index = 0, length = objectKeyList.length; index < length; index += 1) {
			let pairBuffer = "";
			if (!compact || _result !== "") pairBuffer += generateNextLine(state, level);
			const objectKey = objectKeyList[index];
			let objectValue = object[objectKey];
			if (state.replacer) objectValue = state.replacer.call(object, objectKey, objectValue);
			if (!writeNode(state, level + 1, objectKey, true, true, true)) continue;
			const explicitPair = state.tag !== null && state.tag !== "?" || state.dump && state.dump.length > 1024;
			if (explicitPair) if (state.dump && CHAR_LINE_FEED === state.dump.charCodeAt(0)) pairBuffer += "?";
			else pairBuffer += "? ";
			pairBuffer += state.dump;
			if (explicitPair) pairBuffer += generateNextLine(state, level);
			if (!writeNode(state, level + 1, objectValue, true, explicitPair)) continue;
			if (state.dump && CHAR_LINE_FEED === state.dump.charCodeAt(0)) pairBuffer += ":";
			else pairBuffer += ": ";
			pairBuffer += state.dump;
			_result += pairBuffer;
		}
		state.tag = _tag;
		state.dump = _result || "{}";
	}
	function detectType(state, object, explicit) {
		const typeList = explicit ? state.explicitTypes : state.implicitTypes;
		for (let index = 0, length = typeList.length; index < length; index += 1) {
			const type2 = typeList[index];
			if ((type2.instanceOf || type2.predicate) && (!type2.instanceOf || typeof object === "object" && object instanceof type2.instanceOf) && (!type2.predicate || type2.predicate(object))) {
				if (explicit) if (type2.multi && type2.representName) state.tag = type2.representName(object);
				else state.tag = type2.tag;
				else state.tag = "?";
				if (type2.represent) {
					const style = state.styleMap[type2.tag] || type2.defaultStyle;
					let _result;
					if (_toString.call(type2.represent) === "[object Function]") _result = type2.represent(object, style);
					else if (_hasOwnProperty.call(type2.represent, style)) _result = type2.represent[style](object, style);
					else throw new YAMLException2("!<" + type2.tag + "> tag resolver accepts not \"" + style + "\" style");
					state.dump = _result;
				}
				return true;
			}
		}
		return false;
	}
	function writeNode(state, level, object, block, compact, iskey, isblockseq) {
		state.tag = null;
		state.dump = object;
		if (!detectType(state, object, false)) detectType(state, object, true);
		const type2 = _toString.call(state.dump);
		const inblock = block;
		if (block) block = state.flowLevel < 0 || state.flowLevel > level;
		const objectOrArray = type2 === "[object Object]" || type2 === "[object Array]";
		let duplicateIndex;
		let duplicate;
		if (objectOrArray) {
			duplicateIndex = state.duplicates.indexOf(object);
			duplicate = duplicateIndex !== -1;
		}
		if (state.tag !== null && state.tag !== "?" || duplicate || state.indent !== 2 && level > 0) compact = false;
		if (duplicate && state.usedDuplicates[duplicateIndex]) state.dump = "*ref_" + duplicateIndex;
		else {
			if (objectOrArray && duplicate && !state.usedDuplicates[duplicateIndex]) state.usedDuplicates[duplicateIndex] = true;
			if (type2 === "[object Object]") if (block && Object.keys(state.dump).length !== 0) {
				writeBlockMapping(state, level, state.dump, compact);
				if (duplicate) state.dump = "&ref_" + duplicateIndex + state.dump;
			} else {
				writeFlowMapping(state, level, state.dump);
				if (duplicate) state.dump = "&ref_" + duplicateIndex + " " + state.dump;
			}
			else if (type2 === "[object Array]") if (block && state.dump.length !== 0) {
				if (state.noArrayIndent && !isblockseq && level > 0) writeBlockSequence(state, level - 1, state.dump, compact);
				else writeBlockSequence(state, level, state.dump, compact);
				if (duplicate) state.dump = "&ref_" + duplicateIndex + state.dump;
			} else {
				writeFlowSequence(state, level, state.dump);
				if (duplicate) state.dump = "&ref_" + duplicateIndex + " " + state.dump;
			}
			else if (type2 === "[object String]") {
				if (state.tag !== "?") writeScalar(state, state.dump, level, iskey, inblock);
			} else if (type2 === "[object Undefined]") return false;
			else {
				if (state.skipInvalid) return false;
				throw new YAMLException2("unacceptable kind of an object to dump " + type2);
			}
			if (state.tag !== null && state.tag !== "?") {
				let tagStr = encodeURI(state.tag[0] === "!" ? state.tag.slice(1) : state.tag).replace(/!/g, "%21");
				if (state.tag[0] === "!") tagStr = "!" + tagStr;
				else if (tagStr.slice(0, 18) === "tag:yaml.org,2002:") tagStr = "!!" + tagStr.slice(18);
				else tagStr = "!<" + tagStr + ">";
				state.dump = tagStr + " " + state.dump;
			}
		}
		return true;
	}
	function getDuplicateReferences(object, state) {
		const objects = [];
		const duplicatesIndexes = [];
		inspectNode(object, objects, duplicatesIndexes);
		const length = duplicatesIndexes.length;
		for (let index = 0; index < length; index += 1) state.duplicates.push(objects[duplicatesIndexes[index]]);
		state.usedDuplicates = new Array(length);
	}
	function inspectNode(object, objects, duplicatesIndexes) {
		if (object !== null && typeof object === "object") {
			const index = objects.indexOf(object);
			if (index !== -1) {
				if (duplicatesIndexes.indexOf(index) === -1) duplicatesIndexes.push(index);
			} else {
				objects.push(object);
				if (Array.isArray(object)) for (let i = 0, length = object.length; i < length; i += 1) inspectNode(object[i], objects, duplicatesIndexes);
				else {
					const objectKeyList = Object.keys(object);
					for (let i = 0, length = objectKeyList.length; i < length; i += 1) inspectNode(object[objectKeyList[i]], objects, duplicatesIndexes);
				}
			}
		}
	}
	function dump2(input, options) {
		options = options || {};
		const state = new State(options);
		if (!state.noRefs) getDuplicateReferences(input, state);
		let value = input;
		if (state.replacer) value = state.replacer.call({ "": value }, "", value);
		if (writeNode(state, 0, value, true, true)) return state.dump + "\n";
		return "";
	}
	dumper.dump = dump2;
	return dumper;
}
var hasRequiredJsYaml;
function requireJsYaml() {
	if (hasRequiredJsYaml) return jsYaml;
	hasRequiredJsYaml = 1;
	const loader2 = requireLoader();
	const dumper2 = requireDumper();
	function renamed(from, to) {
		return function() {
			throw new Error("Function yaml." + from + " is removed in js-yaml 4. Use yaml." + to + " instead, which is now safe by default.");
		};
	}
	jsYaml.Type = requireType();
	jsYaml.Schema = requireSchema();
	jsYaml.FAILSAFE_SCHEMA = requireFailsafe();
	jsYaml.JSON_SCHEMA = requireJson();
	jsYaml.CORE_SCHEMA = requireCore();
	jsYaml.DEFAULT_SCHEMA = require_default();
	jsYaml.load = loader2.load;
	jsYaml.loadAll = loader2.loadAll;
	jsYaml.dump = dumper2.dump;
	jsYaml.YAMLException = requireException();
	jsYaml.types = {
		binary: requireBinary(),
		float: requireFloat(),
		map: requireMap(),
		null: require_null(),
		pairs: requirePairs(),
		set: requireSet(),
		timestamp: requireTimestamp(),
		bool: requireBool(),
		int: requireInt(),
		merge: requireMerge(),
		omap: requireOmap(),
		seq: requireSeq(),
		str: requireStr()
	};
	jsYaml.safeLoad = renamed("safeLoad", "load");
	jsYaml.safeLoadAll = renamed("safeLoadAll", "loadAll");
	jsYaml.safeDump = renamed("safeDump", "dump");
	return jsYaml;
}
var yaml = /* @__PURE__ */ getDefaultExportFromCjs(requireJsYaml());
var { Type, Schema, FAILSAFE_SCHEMA, JSON_SCHEMA, CORE_SCHEMA, DEFAULT_SCHEMA, load, loadAll, dump, YAMLException, types, safeLoad, safeLoadAll, safeDump } = yaml;
//#endregion
//#region src/components/ConnectorDesigner/OpenApiImporter.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$7 = {
	key: 0,
	style: { "margin-bottom": "12px" }
};
var _hoisted_2$5 = {
	key: 1,
	style: { "margin-bottom": "12px" }
};
var _hoisted_3$4 = { style: { "margin-bottom": "12px" } };
var _hoisted_4$4 = {
	key: 3,
	class: "empty-tip"
};
//#endregion
//#region src/components/ConnectorDesigner/OpenApiImporter.vue
var OpenApiImporter_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "OpenApiImporter",
	props: { baseUrl: {} },
	emits: ["import"],
	setup(__props, { expose: __expose, emit: __emit }) {
		const emit = __emit;
		const visible = ref(false);
		const inputMode = ref("content");
		const urlInput = ref("");
		const contentInput = ref("");
		const parsing = ref(false);
		const parsedOperations = ref([]);
		function open() {
			visible.value = true;
			urlInput.value = "";
			contentInput.value = "";
			parsedOperations.value = [];
			inputMode.value = "content";
		}
		__expose({ open });
		const hasParsed = computed(() => parsedOperations.value.length > 0);
		const selectedCount = computed(() => parsedOperations.value.filter((r) => r.selected).length);
		const allSelected = computed(() => parsedOperations.value.length > 0 && selectedCount.value === parsedOperations.value.length);
		function toggleAll(val) {
			parsedOperations.value.forEach((row) => {
				row.selected = val;
			});
		}
		function handleSelectAllChange(val) {
			toggleAll(val);
		}
		/**
		* 解析 OpenAPI 文档。
		*
		* <p>支持 OpenAPI 3.0 与 Swagger 2.0 的 paths 结构。parameters 转 params/headers：
		* <ul>
		*   <li>in=query / in=path → params</li>
		*   <li>in=header → headers</li>
		* </ul></p>
		*/
		async function parse() {
			if (!(inputMode.value === "url" ? urlInput.value.trim() : contentInput.value.trim())) {
				ElMessage.warning(inputMode.value === "url" ? "请输入 OpenAPI URL" : "请粘贴 OpenAPI 内容");
				return;
			}
			parsing.value = true;
			try {
				let raw;
				if (inputMode.value === "url") {
					const resp = await fetch(urlInput.value, { mode: "cors" });
					if (!resp.ok) throw new Error(`HTTP ${resp.status} ${resp.statusText}`);
					raw = tryParse(await resp.text());
				} else raw = tryParse(contentInput.value);
				if (!raw || typeof raw !== "object") throw new Error("无法解析为 JSON / YAML 对象");
				const ops = extractOperations(raw);
				if (ops.length === 0) {
					ElMessage.warning("未在 OpenAPI 文档中找到任何 paths/operations");
					parsedOperations.value = [];
					return;
				}
				parsedOperations.value = ops;
				ElMessage.success(`成功解析 ${ops.length} 个操作，请勾选要导入的项`);
			} catch (e) {
				ElMessage.error("OpenAPI 解析失败：" + ((e === null || e === void 0 ? void 0 : e.message) || String(e)));
				parsedOperations.value = [];
			} finally {
				parsing.value = false;
			}
		}
		/** 自动判断 JSON / YAML 并解析 */
		function tryParse(text) {
			const trimmed = text.trim();
			if (trimmed.startsWith("{") || trimmed.startsWith("[")) try {
				return JSON.parse(trimmed);
			} catch (_unused) {}
			try {
				return yaml.load(trimmed);
			} catch (e) {
				try {
					return JSON.parse(trimmed);
				} catch (_unused2) {
					throw new Error("既不是合法 JSON 也不是合法 YAML：" + e.message);
				}
			}
		}
		/** 从 OpenAPI 文档提取所有 operations */
		function extractOperations(doc) {
			const paths = doc.paths;
			if (!paths || typeof paths !== "object") return [];
			const rows = [];
			const VALID_METHODS = [
				"get",
				"post",
				"put",
				"delete",
				"patch"
			];
			for (const [path, pathItem] of Object.entries(paths)) {
				if (!pathItem || typeof pathItem !== "object") continue;
				for (const method of VALID_METHODS) {
					const opDef = pathItem[method];
					if (!opDef || typeof opDef !== "object") continue;
					const upperMethod = method.toUpperCase();
					const name = opDef.operationId || opDef.summary || `${upperMethod.toLowerCase()} ${path}`;
					const summary = opDef.summary || opDef.description || "";
					const { headers, params } = extractParameters(opDef.parameters, pathItem);
					rows.push({
						selected: true,
						name: String(name).replace(/\s+/g, "_"),
						method: upperMethod,
						path,
						summary,
						headers,
						params
					});
				}
			}
			return rows;
		}
		/** 提取 parameters 为 headers / params */
		function extractParameters(opParams, pathItem) {
			const headers = [];
			const params = [];
			const allParams = [...Array.isArray(pathItem === null || pathItem === void 0 ? void 0 : pathItem.parameters) ? pathItem.parameters : [], ...Array.isArray(opParams) ? opParams : []];
			for (const p of allParams) {
				if (!p || !p.name) continue;
				const item = {
					key: String(p.name),
					value: ""
				};
				if (p.in === "header") headers.push(item);
				else params.push(item);
			}
			return {
				headers,
				params
			};
		}
		function confirmImport() {
			const selected = parsedOperations.value.filter((r) => r.selected);
			if (selected.length === 0) {
				ElMessage.warning("请至少勾选一个操作");
				return;
			}
			const ops = selected.map((r) => ({
				name: r.name,
				method: r.method,
				path: r.path,
				headers: r.headers.map((h) => ({ ...h })),
				params: r.params.map((p) => ({ ...p })),
				body: null
			}));
			emit("import", ops);
			ElMessage.success(`已导入 ${ops.length} 个操作`);
			visible.value = false;
		}
		/** HTTP 方法的 tag 类型（用于表格颜色区分） */
		function methodTagType(method) {
			switch (method) {
				case "GET": return "success";
				case "POST": return "warning";
				case "PUT": return "";
				case "DELETE": return "danger";
				default: return "info";
			}
		}
		return (_ctx, _cache) => {
			const _component_el_radio_button = resolveComponent("el-radio-button");
			const _component_el_radio_group = resolveComponent("el-radio-group");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_checkbox = resolveComponent("el-checkbox");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_dialog = resolveComponent("el-dialog");
			return openBlock(), createBlock(_component_el_dialog, {
				modelValue: visible.value,
				"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => visible.value = $event),
				title: "导入 OpenAPI / Swagger",
				width: "880px",
				"close-on-click-modal": false,
				"destroy-on-close": ""
			}, {
				footer: withCtx(() => [
					createVNode(_component_el_button, { onClick: _cache[3] || (_cache[3] = ($event) => visible.value = false) }, {
						default: withCtx(() => [..._cache[13] || (_cache[13] = [createTextVNode("取消", -1)])]),
						_: 1
					}),
					hasParsed.value ? (openBlock(), createBlock(_component_el_button, {
						key: 0,
						onClick: _cache[4] || (_cache[4] = ($event) => toggleAll(true))
					}, {
						default: withCtx(() => [..._cache[14] || (_cache[14] = [createTextVNode("全选", -1)])]),
						_: 1
					})) : createCommentVNode("", true),
					hasParsed.value ? (openBlock(), createBlock(_component_el_button, {
						key: 1,
						onClick: _cache[5] || (_cache[5] = ($event) => toggleAll(false))
					}, {
						default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode("全不选", -1)])]),
						_: 1
					})) : createCommentVNode("", true),
					createVNode(_component_el_button, {
						type: "primary",
						disabled: !hasParsed.value || selectedCount.value === 0,
						onClick: confirmImport
					}, {
						default: withCtx(() => [createTextVNode(" 导入选中操作 (" + toDisplayString(selectedCount.value) + ") ", 1)]),
						_: 1
					}, 8, ["disabled"])
				]),
				default: withCtx(() => [
					createVNode(_component_el_radio_group, {
						modelValue: inputMode.value,
						"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => inputMode.value = $event),
						style: { "margin-bottom": "12px" }
					}, {
						default: withCtx(() => [createVNode(_component_el_radio_button, { value: "content" }, {
							default: withCtx(() => [..._cache[7] || (_cache[7] = [createTextVNode("粘贴内容", -1)])]),
							_: 1
						}), createVNode(_component_el_radio_button, { value: "url" }, {
							default: withCtx(() => [..._cache[8] || (_cache[8] = [createTextVNode("URL 拉取", -1)])]),
							_: 1
						})]),
						_: 1
					}, 8, ["modelValue"]),
					inputMode.value === "url" ? (openBlock(), createElementBlock("div", _hoisted_1$7, [createVNode(_component_el_input, {
						modelValue: urlInput.value,
						"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => urlInput.value = $event),
						placeholder: "https://api.example.com/openapi.yaml"
					}, {
						prepend: withCtx(() => [..._cache[9] || (_cache[9] = [createTextVNode("URL", -1)])]),
						_: 1
					}, 8, ["modelValue"]), _cache[10] || (_cache[10] = createElementVNode("div", { class: "hint" }, "支持 CORS 跨域的可访问 URL，返回 JSON 或 YAML", -1))])) : (openBlock(), createElementBlock("div", _hoisted_2$5, [createVNode(_component_el_input, {
						modelValue: contentInput.value,
						"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => contentInput.value = $event),
						type: "textarea",
						rows: 8,
						placeholder: "在此粘贴 OpenAPI 3.0 / Swagger 2.0 的 JSON 或 YAML 内容"
					}, null, 8, ["modelValue"])])),
					createElementVNode("div", _hoisted_3$4, [createVNode(_component_el_button, {
						type: "primary",
						loading: parsing.value,
						onClick: parse
					}, {
						default: withCtx(() => [..._cache[11] || (_cache[11] = [createTextVNode("解析", -1)])]),
						_: 1
					}, 8, ["loading"]), createVNode(_component_el_button, { onClick: open }, {
						default: withCtx(() => [..._cache[12] || (_cache[12] = [createTextVNode("重置", -1)])]),
						_: 1
					})]),
					hasParsed.value ? (openBlock(), createBlock(_component_el_table, {
						key: 2,
						data: parsedOperations.value,
						border: "",
						"max-height": "320"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_table_column, {
								width: "55",
								align: "center"
							}, {
								header: withCtx(() => [createVNode(_component_el_checkbox, {
									"model-value": allSelected.value,
									onChange: handleSelectAllChange
								}, null, 8, ["model-value"])]),
								default: withCtx(({ row }) => [createVNode(_component_el_checkbox, {
									modelValue: row.selected,
									"onUpdate:modelValue": ($event) => row.selected = $event
								}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "操作名",
								prop: "name",
								"min-width": "160",
								"show-overflow-tooltip": ""
							}),
							createVNode(_component_el_table_column, {
								label: "方法",
								prop: "method",
								width: "80"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, {
									type: methodTagType(row.method),
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(row.method), 1)]),
									_: 2
								}, 1032, ["type"])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "路径",
								prop: "path",
								"min-width": "180",
								"show-overflow-tooltip": ""
							}),
							createVNode(_component_el_table_column, {
								label: "说明",
								prop: "summary",
								"min-width": "160",
								"show-overflow-tooltip": ""
							}),
							createVNode(_component_el_table_column, {
								label: "参数数",
								width: "80",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createTextVNode(toDisplayString(row.params.length + row.headers.length), 1)]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["data"])) : !parsing.value ? (openBlock(), createElementBlock("div", _hoisted_4$4, " 解析后此处展示操作列表，可勾选要导入的项 ")) : createCommentVNode("", true)
				]),
				_: 1
			}, 8, ["modelValue"]);
		};
	}
}), [["__scopeId", "data-v-7a3457b8"]]);
//#endregion
//#region src/components/ConnectorDesigner/StepOperations.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$6 = { class: "step-operations" };
var _hoisted_2$4 = { class: "step-toolbar" };
var _hoisted_3$3 = {
	key: 1,
	class: "dash"
};
var _hoisted_4$3 = { class: "step-toolbar" };
var _hoisted_5$1 = { class: "kv-list" };
var _hoisted_6$1 = { class: "kv-list" };
//#endregion
//#region src/components/ConnectorDesigner/StepOperations.vue
var StepOperations_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "StepOperations",
	props: {
		type: {},
		operations: {},
		sqlTemplates: {},
		baseUrl: {}
	},
	emits: ["update:operations", "update:sqlTemplates"],
	setup(__props, { emit: __emit }) {
		/**
		* 连接器分步表单 — Step 3 操作列表（REST）/ SQL 模板（DB）。
		*
		* <p>REST 操作表格：operationName、method、path、headers（KV 表）、body（JSON textarea）、
		* params（KV 表）。支持新增 / 编辑 / 删除。提供 "导入 OpenAPI" 按钮（Task 9）批量导入。</p>
		*
		* <p>DB SQL 模板列表：operationName、sqlType（QUERY/UPDATE）、sqlTemplate。</p>
		*/
		const props = __props;
		const emit = __emit;
		const METHODS = [
			"GET",
			"POST",
			"PUT",
			"DELETE"
		];
		const SQL_TYPES = ["QUERY", "UPDATE"];
		const restDialogVisible = ref(false);
		const editingRestIndex = ref(-1);
		const editingRest = ref(createEmptyRestOperation());
		const sqlDialogVisible = ref(false);
		const editingSqlIndex = ref(-1);
		const editingSql = ref(createEmptySqlTemplate());
		const openApiImporterRef = ref(null);
		function addRestOperation() {
			editingRestIndex.value = -1;
			editingRest.value = createEmptyRestOperation();
			restDialogVisible.value = true;
		}
		function editRestOperation(index) {
			editingRestIndex.value = index;
			const op = props.operations[index];
			editingRest.value = {
				name: op.name,
				method: op.method,
				path: op.path,
				headers: op.headers.map((h) => ({ ...h })),
				params: op.params.map((p) => ({ ...p })),
				body: op.body
			};
			restDialogVisible.value = true;
		}
		function saveRestOperation() {
			var _editingRest$value$bo;
			if (!editingRest.value.name.trim()) {
				ElMessage.warning("请输入操作名");
				return;
			}
			const op = {
				name: editingRest.value.name,
				method: editingRest.value.method,
				path: editingRest.value.path,
				headers: editingRest.value.headers.filter((h) => h.key.trim()),
				params: editingRest.value.params.filter((p) => p.key.trim()),
				body: ((_editingRest$value$bo = editingRest.value.body) === null || _editingRest$value$bo === void 0 ? void 0 : _editingRest$value$bo.trim()) ? editingRest.value.body.trim() : null
			};
			if (editingRestIndex.value === -1) emit("update:operations", [...props.operations, op]);
			else {
				const updated = [...props.operations];
				updated[editingRestIndex.value] = op;
				emit("update:operations", updated);
			}
			restDialogVisible.value = false;
		}
		function removeRestOperation(index) {
			const updated = [...props.operations];
			updated.splice(index, 1);
			emit("update:operations", updated);
		}
		function addKv(list) {
			list.push({
				key: "",
				value: ""
			});
		}
		function removeKv(list, index) {
			list.splice(index, 1);
		}
		function openImporter() {
			var _openApiImporterRef$v;
			(_openApiImporterRef$v = openApiImporterRef.value) === null || _openApiImporterRef$v === void 0 || _openApiImporterRef$v.open();
		}
		function handleImport(ops) {
			const existingNames = new Set(props.operations.map((o) => o.name));
			const toAdd = ops.filter((o) => !existingNames.has(o.name));
			const dupCount = ops.length - toAdd.length;
			if (toAdd.length === 0) {
				ElMessage.warning(`全部 ${ops.length} 个操作已存在（按名称去重），未导入新操作`);
				return;
			}
			emit("update:operations", [...props.operations, ...toAdd]);
			if (dupCount > 0) ElMessage.info(`已新增 ${toAdd.length} 个操作，跳过 ${dupCount} 个重名操作`);
		}
		function addSqlTemplate() {
			editingSqlIndex.value = -1;
			editingSql.value = createEmptySqlTemplate();
			sqlDialogVisible.value = true;
		}
		function editSqlTemplate(index) {
			editingSqlIndex.value = index;
			const t = props.sqlTemplates[index];
			editingSql.value = {
				name: t.name,
				sqlType: t.sqlType,
				sqlTemplate: t.sqlTemplate
			};
			sqlDialogVisible.value = true;
		}
		function saveSqlTemplate() {
			if (!editingSql.value.name.trim()) {
				ElMessage.warning("请输入操作名");
				return;
			}
			if (!editingSql.value.sqlTemplate.trim()) {
				ElMessage.warning("请输入 SQL 模板");
				return;
			}
			const t = {
				name: editingSql.value.name,
				sqlType: editingSql.value.sqlType,
				sqlTemplate: editingSql.value.sqlTemplate
			};
			if (editingSqlIndex.value === -1) emit("update:sqlTemplates", [...props.sqlTemplates, t]);
			else {
				const updated = [...props.sqlTemplates];
				updated[editingSqlIndex.value] = t;
				emit("update:sqlTemplates", updated);
			}
			sqlDialogVisible.value = false;
		}
		function removeSqlTemplate(index) {
			const updated = [...props.sqlTemplates];
			updated.splice(index, 1);
			emit("update:sqlTemplates", updated);
		}
		function methodTagType(method) {
			switch (method) {
				case "GET": return "success";
				case "POST": return "warning";
				case "PUT": return "";
				case "DELETE": return "danger";
				default: return "info";
			}
		}
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_dialog = resolveComponent("el-dialog");
			return openBlock(), createElementBlock("div", _hoisted_1$6, [
				props.type === "REST" ? (openBlock(), createElementBlock(Fragment, { key: 0 }, [
					createElementVNode("div", _hoisted_2$4, [createVNode(_component_el_button, {
						type: "primary",
						size: "small",
						onClick: addRestOperation
					}, {
						default: withCtx(() => [..._cache[13] || (_cache[13] = [createTextVNode("新增操作", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						size: "small",
						onClick: openImporter
					}, {
						default: withCtx(() => [..._cache[14] || (_cache[14] = [createTextVNode("导入 OpenAPI", -1)])]),
						_: 1
					})]),
					createVNode(_component_el_table, {
						data: props.operations,
						border: "",
						size: "small"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_table_column, {
								label: "操作名",
								prop: "name",
								"min-width": "140",
								"show-overflow-tooltip": ""
							}),
							createVNode(_component_el_table_column, {
								label: "方法",
								prop: "method",
								width: "80"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, {
									type: methodTagType(row.method),
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(row.method), 1)]),
									_: 2
								}, 1032, ["type"])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "路径",
								prop: "path",
								"min-width": "180",
								"show-overflow-tooltip": ""
							}),
							createVNode(_component_el_table_column, {
								label: "Headers",
								width: "90",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createTextVNode(toDisplayString(row.headers.length), 1)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "Params",
								width: "80",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createTextVNode(toDisplayString(row.params.length), 1)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "Body",
								width: "80",
								align: "center"
							}, {
								default: withCtx(({ row }) => [row.body ? (openBlock(), createBlock(_component_el_tag, {
									key: 0,
									size: "small",
									type: "info"
								}, {
									default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode("有", -1)])]),
									_: 1
								})) : (openBlock(), createElementBlock("span", _hoisted_3$3, "—"))]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "操作",
								width: "120",
								align: "center"
							}, {
								default: withCtx(({ $index }) => [createVNode(_component_el_button, {
									size: "small",
									link: "",
									onClick: ($event) => editRestOperation($index)
								}, {
									default: withCtx(() => [..._cache[16] || (_cache[16] = [createTextVNode("编辑", -1)])]),
									_: 1
								}, 8, ["onClick"]), createVNode(_component_el_button, {
									size: "small",
									link: "",
									type: "danger",
									onClick: ($event) => removeRestOperation($index)
								}, {
									default: withCtx(() => [..._cache[17] || (_cache[17] = [createTextVNode(" 删除 ", -1)])]),
									_: 1
								}, 8, ["onClick"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["data"]),
					props.operations.length === 0 ? (openBlock(), createBlock(_component_el_empty, {
						key: 0,
						description: "暂无操作，点击「新增操作」或「导入 OpenAPI」",
						"image-size": 60
					})) : createCommentVNode("", true)
				], 64)) : (openBlock(), createElementBlock(Fragment, { key: 1 }, [
					createElementVNode("div", _hoisted_4$3, [createVNode(_component_el_button, {
						type: "primary",
						size: "small",
						onClick: addSqlTemplate
					}, {
						default: withCtx(() => [..._cache[18] || (_cache[18] = [createTextVNode("新增 SQL 模板", -1)])]),
						_: 1
					})]),
					createVNode(_component_el_table, {
						data: props.sqlTemplates,
						border: "",
						size: "small"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_table_column, {
								label: "操作名",
								prop: "name",
								"min-width": "140",
								"show-overflow-tooltip": ""
							}),
							createVNode(_component_el_table_column, {
								label: "类型",
								prop: "sqlType",
								width: "100"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, {
									type: row.sqlType === "QUERY" ? "success" : "warning",
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(row.sqlType), 1)]),
									_: 2
								}, 1032, ["type"])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "SQL 模板",
								prop: "sqlTemplate",
								"min-width": "280",
								"show-overflow-tooltip": ""
							}),
							createVNode(_component_el_table_column, {
								label: "操作",
								width: "120",
								align: "center"
							}, {
								default: withCtx(({ $index }) => [createVNode(_component_el_button, {
									size: "small",
									link: "",
									onClick: ($event) => editSqlTemplate($index)
								}, {
									default: withCtx(() => [..._cache[19] || (_cache[19] = [createTextVNode("编辑", -1)])]),
									_: 1
								}, 8, ["onClick"]), createVNode(_component_el_button, {
									size: "small",
									link: "",
									type: "danger",
									onClick: ($event) => removeSqlTemplate($index)
								}, {
									default: withCtx(() => [..._cache[20] || (_cache[20] = [createTextVNode(" 删除 ", -1)])]),
									_: 1
								}, 8, ["onClick"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["data"]),
					props.sqlTemplates.length === 0 ? (openBlock(), createBlock(_component_el_empty, {
						key: 0,
						description: "暂无 SQL 模板，点击「新增 SQL 模板」",
						"image-size": 60
					})) : createCommentVNode("", true)
				], 64)),
				createVNode(_component_el_dialog, {
					modelValue: restDialogVisible.value,
					"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => restDialogVisible.value = $event),
					title: editingRestIndex.value === -1 ? "新增操作" : "编辑操作",
					width: "720px",
					"close-on-click-modal": false,
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[6] || (_cache[6] = ($event) => restDialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[26] || (_cache[26] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						onClick: saveRestOperation
					}, {
						default: withCtx(() => [..._cache[27] || (_cache[27] = [createTextVNode("保存", -1)])]),
						_: 1
					})]),
					default: withCtx(() => [createVNode(_component_el_form, {
						model: editingRest.value,
						"label-width": "90px",
						size: "small"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, {
								label: "操作名",
								required: ""
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: editingRest.value.name,
									"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => editingRest.value.name = $event),
									placeholder: "如 getUser"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "HTTP 方法",
								required: ""
							}, {
								default: withCtx(() => [createVNode(_component_el_select, {
									modelValue: editingRest.value.method,
									"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => editingRest.value.method = $event),
									style: { "width": "160px" }
								}, {
									default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(METHODS, (m) => {
										return createVNode(_component_el_option, {
											key: m,
											label: m,
											value: m
										}, null, 8, ["label", "value"]);
									}), 64))]),
									_: 1
								}, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "路径",
								required: ""
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: editingRest.value.path,
									"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => editingRest.value.path = $event),
									placeholder: "/users/{id}"
								}, null, 8, ["modelValue"]), _cache[21] || (_cache[21] = createElementVNode("div", { class: "field-hint" }, "支持路径参数，如 /users/{id}", -1))]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "Headers" }, {
								default: withCtx(() => [createElementVNode("div", _hoisted_5$1, [(openBlock(true), createElementBlock(Fragment, null, renderList(editingRest.value.headers, (kv, idx) => {
									return openBlock(), createElementBlock("div", {
										key: idx,
										class: "kv-row"
									}, [
										createVNode(_component_el_input, {
											modelValue: kv.key,
											"onUpdate:modelValue": ($event) => kv.key = $event,
											placeholder: "key",
											style: { "width": "180px" }
										}, null, 8, ["modelValue", "onUpdate:modelValue"]),
										createVNode(_component_el_input, {
											modelValue: kv.value,
											"onUpdate:modelValue": ($event) => kv.value = $event,
											placeholder: "value",
											style: { "flex": "1" }
										}, null, 8, ["modelValue", "onUpdate:modelValue"]),
										createVNode(_component_el_button, {
											link: "",
											type: "danger",
											onClick: ($event) => removeKv(editingRest.value.headers, idx)
										}, {
											default: withCtx(() => [..._cache[22] || (_cache[22] = [createTextVNode(" 删除 ", -1)])]),
											_: 1
										}, 8, ["onClick"])
									]);
								}), 128)), createVNode(_component_el_button, {
									size: "small",
									onClick: _cache[3] || (_cache[3] = ($event) => addKv(editingRest.value.headers))
								}, {
									default: withCtx(() => [..._cache[23] || (_cache[23] = [createTextVNode("+ 添加 Header", -1)])]),
									_: 1
								})])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "Params" }, {
								default: withCtx(() => [createElementVNode("div", _hoisted_6$1, [(openBlock(true), createElementBlock(Fragment, null, renderList(editingRest.value.params, (kv, idx) => {
									return openBlock(), createElementBlock("div", {
										key: idx,
										class: "kv-row"
									}, [
										createVNode(_component_el_input, {
											modelValue: kv.key,
											"onUpdate:modelValue": ($event) => kv.key = $event,
											placeholder: "key",
											style: { "width": "180px" }
										}, null, 8, ["modelValue", "onUpdate:modelValue"]),
										createVNode(_component_el_input, {
											modelValue: kv.value,
											"onUpdate:modelValue": ($event) => kv.value = $event,
											placeholder: "value",
											style: { "flex": "1" }
										}, null, 8, ["modelValue", "onUpdate:modelValue"]),
										createVNode(_component_el_button, {
											link: "",
											type: "danger",
											onClick: ($event) => removeKv(editingRest.value.params, idx)
										}, {
											default: withCtx(() => [..._cache[24] || (_cache[24] = [createTextVNode(" 删除 ", -1)])]),
											_: 1
										}, 8, ["onClick"])
									]);
								}), 128)), createVNode(_component_el_button, {
									size: "small",
									onClick: _cache[4] || (_cache[4] = ($event) => addKv(editingRest.value.params))
								}, {
									default: withCtx(() => [..._cache[25] || (_cache[25] = [createTextVNode("+ 添加 Param", -1)])]),
									_: 1
								})])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "Body" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: editingRest.value.body,
									"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => editingRest.value.body = $event),
									type: "textarea",
									rows: 5,
									placeholder: "JSON 请求体，如 {\"name\":\"test\"}（GET/DELETE 可留空）"
								}, null, 8, ["modelValue"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue", "title"]),
				createVNode(_component_el_dialog, {
					modelValue: sqlDialogVisible.value,
					"onUpdate:modelValue": _cache[12] || (_cache[12] = ($event) => sqlDialogVisible.value = $event),
					title: editingSqlIndex.value === -1 ? "新增 SQL 模板" : "编辑 SQL 模板",
					width: "680px",
					"close-on-click-modal": false,
					"destroy-on-close": ""
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[11] || (_cache[11] = ($event) => sqlDialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[30] || (_cache[30] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						onClick: saveSqlTemplate
					}, {
						default: withCtx(() => [..._cache[31] || (_cache[31] = [createTextVNode("保存", -1)])]),
						_: 1
					})]),
					default: withCtx(() => [createVNode(_component_el_form, {
						model: editingSql.value,
						"label-width": "90px"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, {
								label: "操作名",
								required: ""
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: editingSql.value.name,
									"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => editingSql.value.name = $event),
									placeholder: "如 findUserById"
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "SQL 类型",
								required: ""
							}, {
								default: withCtx(() => [createVNode(_component_el_select, {
									modelValue: editingSql.value.sqlType,
									"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => editingSql.value.sqlType = $event),
									style: { "width": "200px" }
								}, {
									default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(SQL_TYPES, (t) => {
										return createVNode(_component_el_option, {
											key: t,
											label: t,
											value: t
										}, null, 8, ["label", "value"]);
									}), 64))]),
									_: 1
								}, 8, ["modelValue"]), _cache[28] || (_cache[28] = createElementVNode("span", {
									class: "field-hint",
									style: { "margin-left": "12px" }
								}, " QUERY 返回结果集，UPDATE 返回影响行数 ", -1))]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "SQL 模板",
								required: ""
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: editingSql.value.sqlTemplate,
									"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => editingSql.value.sqlTemplate = $event),
									type: "textarea",
									rows: 8,
									placeholder: "SELECT * FROM users WHERE id = :id"
								}, null, 8, ["modelValue"]), _cache[29] || (_cache[29] = createElementVNode("div", { class: "field-hint" }, "支持命名参数，如 :id、:name", -1))]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}, 8, ["modelValue", "title"]),
				createVNode(OpenApiImporter_default, {
					ref_key: "openApiImporterRef",
					ref: openApiImporterRef,
					"base-url": props.baseUrl,
					onImport: handleImport
				}, null, 8, ["base-url"])
			]);
		};
	}
}), [["__scopeId", "data-v-6bc036c5"]]);
//#endregion
//#region src/components/ConnectorDesigner/StepResponseMapping.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$5 = { class: "step-response-mapping" };
var _hoisted_2$3 = { class: "step-toolbar" };
//#endregion
//#region src/components/ConnectorDesigner/StepResponseMapping.vue
var StepResponseMapping_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "StepResponseMapping",
	props: { responseMapping: {} },
	emits: ["update:responseMapping"],
	setup(__props, { emit: __emit }) {
		/**
		* 连接器分步表单 — Step 4 响应映射。
		*
		* <p>响应字段 → 实体字段映射表格：sourcePath（JSONPath）+ targetField + transform。
		* 用于将连接器返回的原始响应转换为业务实体字段结构。</p>
		*/
		const props = __props;
		const emit = __emit;
		function addRow() {
			emit("update:responseMapping", [...props.responseMapping, {
				sourcePath: "",
				targetField: "",
				transform: null
			}]);
		}
		function removeRow(index) {
			const updated = [...props.responseMapping];
			updated.splice(index, 1);
			emit("update:responseMapping", updated);
		}
		function updateRow(index, patch) {
			const updated = [...props.responseMapping];
			updated[index] = {
				...updated[index],
				...patch
			};
			emit("update:responseMapping", updated);
		}
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_empty = resolveComponent("el-empty");
			return openBlock(), createElementBlock("div", _hoisted_1$5, [
				createElementVNode("div", _hoisted_2$3, [createVNode(_component_el_button, {
					type: "primary",
					size: "small",
					onClick: addRow
				}, {
					default: withCtx(() => [..._cache[0] || (_cache[0] = [createTextVNode("新增映射", -1)])]),
					_: 1
				}), _cache[1] || (_cache[1] = createElementVNode("span", { class: "hint" }, "将响应字段（JSONPath）映射到目标实体字段", -1))]),
				createVNode(_component_el_table, {
					data: props.responseMapping,
					border: "",
					size: "small"
				}, {
					default: withCtx(() => [
						createVNode(_component_el_table_column, {
							label: "序号",
							width: "60",
							align: "center",
							type: "index"
						}),
						createVNode(_component_el_table_column, {
							label: "响应字段 (JSONPath)",
							"min-width": "220"
						}, {
							default: withCtx(({ row, $index }) => [createVNode(_component_el_input, {
								"model-value": row.sourcePath,
								placeholder: "$.data.id",
								"onUpdate:modelValue": (v) => updateRow($index, { sourcePath: v })
							}, null, 8, ["model-value", "onUpdate:modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							label: "目标字段",
							"min-width": "160"
						}, {
							default: withCtx(({ row, $index }) => [createVNode(_component_el_input, {
								"model-value": row.targetField,
								placeholder: "id",
								"onUpdate:modelValue": (v) => updateRow($index, { targetField: v })
							}, null, 8, ["model-value", "onUpdate:modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							label: "转换表达式",
							"min-width": "200"
						}, {
							default: withCtx(({ row, $index }) => [createVNode(_component_el_input, {
								"model-value": row.transform || "",
								placeholder: "可选，如 toString(#value)",
								"onUpdate:modelValue": (v) => updateRow($index, { transform: v || null })
							}, null, 8, ["model-value", "onUpdate:modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							label: "操作",
							width: "80",
							align: "center"
						}, {
							default: withCtx(({ $index }) => [createVNode(_component_el_button, {
								size: "small",
								link: "",
								type: "danger",
								onClick: ($event) => removeRow($index)
							}, {
								default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("删除", -1)])]),
								_: 1
							}, 8, ["onClick"])]),
							_: 1
						})
					]),
					_: 1
				}, 8, ["data"]),
				props.responseMapping.length === 0 ? (openBlock(), createBlock(_component_el_empty, {
					key: 0,
					description: "暂无映射规则，可点击「新增映射」",
					"image-size": 60
				})) : createCommentVNode("", true)
			]);
		};
	}
}), [["__scopeId", "data-v-75f39c1c"]]);
//#endregion
//#region src/components/ConnectorDesigner/StepPagination.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$4 = { class: "step-pagination" };
//#endregion
//#region src/components/ConnectorDesigner/StepPagination.vue
var StepPagination_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "StepPagination",
	props: { pagination: {} },
	emits: ["update:pagination"],
	setup(__props, { emit: __emit }) {
		/**
		* 连接器分步表单 — Step 5 分页配置（仅 REST）。
		*
		* <p>支持四种分页类型：
		* <ul>
		*   <li>NONE — 不分页</li>
		*   <li>OFFSET — offset/limit 偏移分页（totalCountPath 返回总数）</li>
		*   <li>PAGE — page/pageSize 页码分页（totalPagesPath 返回总页数）</li>
		*   <li>NEXT_LINK — 响应中包含下一页链接（nextLinkPath）</li>
		* </ul></p>
		*/
		const props = __props;
		const emit = __emit;
		const form = reactive({ ...props.pagination });
		watch(() => props.pagination, (val) => {
			Object.assign(form, val);
		}, { deep: true });
		watch(form, (val) => {
			emit("update:pagination", { ...val });
		}, { deep: true });
		const TYPES = [
			{
				label: "不分页",
				value: "NONE",
				desc: "单次请求，不自动翻页"
			},
			{
				label: "OFFSET 偏移",
				value: "OFFSET",
				desc: "通过 offset/limit 参数分页，需返回总数"
			},
			{
				label: "PAGE 页码",
				value: "PAGE",
				desc: "通过 page/pageSize 参数分页，需返回总页数"
			},
			{
				label: "NEXT_LINK 链接",
				value: "NEXT_LINK",
				desc: "响应体含下一页链接字段"
			}
		];
		return (_ctx, _cache) => {
			const _component_el_radio_button = resolveComponent("el-radio-button");
			const _component_el_radio_group = resolveComponent("el-radio-group");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_alert = resolveComponent("el-alert");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form = resolveComponent("el-form");
			return openBlock(), createElementBlock("div", _hoisted_1$4, [createVNode(_component_el_form, {
				model: form,
				"label-width": "140px",
				class: "pagination-form"
			}, {
				default: withCtx(() => {
					var _TYPES$find;
					return [
						createVNode(_component_el_form_item, { label: "分页类型" }, {
							default: withCtx(() => [createVNode(_component_el_radio_group, {
								modelValue: form.type,
								"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => form.type = $event)
							}, {
								default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(TYPES, (t) => {
									return createVNode(_component_el_radio_button, {
										key: t.value,
										value: t.value
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(t.label), 1)]),
										_: 2
									}, 1032, ["value"]);
								}), 64))]),
								_: 1
							}, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_alert, {
							title: ((_TYPES$find = TYPES.find((t) => t.value === form.type)) === null || _TYPES$find === void 0 ? void 0 : _TYPES$find.desc) || "",
							type: "info",
							closable: false,
							"show-icon": "",
							style: { "margin-bottom": "16px" }
						}, null, 8, ["title"]),
						form.type === "OFFSET" ? (openBlock(), createElementBlock(Fragment, { key: 0 }, [
							createVNode(_component_el_form_item, { label: "offset 参数名" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.offsetParam,
									"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => form.offsetParam = $event),
									placeholder: "offset",
									style: { "width": "220px" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "limit 参数名" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.limitParam,
									"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => form.limitParam = $event),
									placeholder: "limit",
									style: { "width": "220px" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "总数 JSONPath" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.totalCountPath,
									"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => form.totalCountPath = $event),
									placeholder: "$.total",
									style: { "width": "320px" }
								}, null, 8, ["modelValue"]), _cache[8] || (_cache[8] = createElementVNode("div", { class: "field-hint" }, "响应中总数的 JSONPath 路径", -1))]),
								_: 1
							})
						], 64)) : form.type === "PAGE" ? (openBlock(), createElementBlock(Fragment, { key: 1 }, [
							createVNode(_component_el_form_item, { label: "page 参数名" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.pageParam,
									"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => form.pageParam = $event),
									placeholder: "page",
									style: { "width": "220px" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "pageSize 参数名" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.pageSizeParam,
									"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => form.pageSizeParam = $event),
									placeholder: "pageSize",
									style: { "width": "220px" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "总页数 JSONPath" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.totalPagesPath,
									"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => form.totalPagesPath = $event),
									placeholder: "$.totalPages",
									style: { "width": "320px" }
								}, null, 8, ["modelValue"]), _cache[9] || (_cache[9] = createElementVNode("div", { class: "field-hint" }, "响应中总页数的 JSONPath 路径", -1))]),
								_: 1
							})
						], 64)) : form.type === "NEXT_LINK" ? (openBlock(), createBlock(_component_el_form_item, {
							key: 2,
							label: "下一页链接 JSONPath"
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: form.nextLinkPath,
								"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => form.nextLinkPath = $event),
								placeholder: "$.nextLink",
								style: { "width": "320px" }
							}, null, 8, ["modelValue"]), _cache[10] || (_cache[10] = createElementVNode("div", { class: "field-hint" }, "响应中下一页 URL 的 JSONPath 路径，存在则继续翻页", -1))]),
							_: 1
						})) : (openBlock(), createBlock(_component_el_form_item, { key: 3 }, {
							default: withCtx(() => [..._cache[11] || (_cache[11] = [createElementVNode("span", { class: "muted" }, "该类型无需额外配置", -1)])]),
							_: 1
						}))
					];
				}),
				_: 1
			}, 8, ["model"])]);
		};
	}
}), [["__scopeId", "data-v-23203ec6"]]);
//#endregion
//#region src/components/ConnectorDesigner/StepRetry.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$3 = { class: "step-retry" };
//#endregion
//#region src/components/ConnectorDesigner/StepRetry.vue
var StepRetry_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "StepRetry",
	props: { retry: {} },
	emits: ["update:retry"],
	setup(__props, { emit: __emit }) {
		/**
		* 连接器分步表单 — Step 6 重试与超时。
		*
		* <p>配置：maxAttempts（最大重试次数）、waitMillis（重试间隔）、
		* timeoutMillis（请求超时）、retryOnStatusCodes（触发重试的 HTTP 状态码）。</p>
		*/
		const props = __props;
		const emit = __emit;
		const form = reactive({
			maxAttempts: props.retry.maxAttempts,
			waitMillis: props.retry.waitMillis,
			timeoutMillis: props.retry.timeoutMillis,
			retryOnStatusCodes: [...props.retry.retryOnStatusCodes]
		});
		watch(() => props.retry, (val) => {
			form.maxAttempts = val.maxAttempts;
			form.waitMillis = val.waitMillis;
			form.timeoutMillis = val.timeoutMillis;
			form.retryOnStatusCodes = [...val.retryOnStatusCodes];
		}, { deep: true });
		watch(form, (val) => {
			emit("update:retry", {
				maxAttempts: val.maxAttempts,
				waitMillis: val.waitMillis,
				timeoutMillis: val.timeoutMillis,
				retryOnStatusCodes: [...val.retryOnStatusCodes]
			});
		}, { deep: true });
		const codesText = ref(form.retryOnStatusCodes.join(","));
		watch(codesText, (val) => {
			form.retryOnStatusCodes = val.split(",").map((s) => s.trim()).filter((s) => s.length > 0).map((s) => Number(s)).filter((n) => !Number.isNaN(n));
		});
		watch(() => props.retry.retryOnStatusCodes, (val) => {
			codesText.value = val.join(",");
		});
		return (_ctx, _cache) => {
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form = resolveComponent("el-form");
			return openBlock(), createElementBlock("div", _hoisted_1$3, [createVNode(_component_el_form, {
				model: form,
				"label-width": "160px",
				class: "retry-form"
			}, {
				default: withCtx(() => [
					createVNode(_component_el_form_item, { label: "最大重试次数" }, {
						default: withCtx(() => [createVNode(_component_el_input_number, {
							modelValue: form.maxAttempts,
							"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => form.maxAttempts = $event),
							min: 0,
							max: 10
						}, null, 8, ["modelValue"]), _cache[4] || (_cache[4] = createElementVNode("div", { class: "field-hint" }, "0 表示不重试，建议 3", -1))]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "重试间隔 (毫秒)" }, {
						default: withCtx(() => [createVNode(_component_el_input_number, {
							modelValue: form.waitMillis,
							"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => form.waitMillis = $event),
							min: 0,
							max: 6e4,
							step: 500
						}, null, 8, ["modelValue"]), _cache[5] || (_cache[5] = createElementVNode("div", { class: "field-hint" }, "每次重试之间的等待时间", -1))]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "请求超时 (毫秒)" }, {
						default: withCtx(() => [createVNode(_component_el_input_number, {
							modelValue: form.timeoutMillis,
							"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => form.timeoutMillis = $event),
							min: 1e3,
							max: 3e5,
							step: 1e3
						}, null, 8, ["modelValue"]), _cache[6] || (_cache[6] = createElementVNode("div", { class: "field-hint" }, "单次请求超时时间，建议 30000", -1))]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "触发重试状态码" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: codesText.value,
							"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => codesText.value = $event),
							placeholder: "500,502,503",
							style: { "width": "240px" }
						}, null, 8, ["modelValue"]), _cache[7] || (_cache[7] = createElementVNode("div", { class: "field-hint" }, "以逗号分隔的 HTTP 状态码，命中时触发重试", -1))]),
						_: 1
					})
				]),
				_: 1
			}, 8, ["model"])]);
		};
	}
}), [["__scopeId", "data-v-4f051060"]]);
//#endregion
//#region src/components/ConnectorDesigner/JsonTree.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$2 = {
	key: 1,
	class: "jt-arrow-placeholder"
};
var _hoisted_2$2 = {
	key: 0,
	class: "jt-empty"
};
var _hoisted_3$2 = {
	key: 0,
	class: "jt-collapsed-preview"
};
var _hoisted_4$2 = {
	key: 0,
	class: "jt-children"
};
//#endregion
//#region src/components/ConnectorDesigner/JsonTree.vue
var JsonTree_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "JsonTree",
	props: {
		data: {},
		level: { default: 0 },
		name: {},
		defaultExpanded: {
			type: Boolean,
			default: true
		}
	},
	setup(__props) {
		/**
		* 通用 JSON 树形展示组件（可折叠）。
		*
		* <p>用于测试控制台响应 Body 的可视化展示。递归渲染对象/数组/基本类型，
		* 支持点击 key 或对象头部折叠/展开。</p>
		*/
		const props = __props;
		const expanded = ref(props.defaultExpanded);
		function toggle() {
			expanded.value = !expanded.value;
		}
		const isObject = computed(() => props.data !== null && typeof props.data === "object");
		const isArray = computed(() => Array.isArray(props.data));
		const isPlainObject = computed(() => isObject.value && !isArray.value);
		const entries = computed(() => {
			if (!isObject.value) return [];
			if (isArray.value) return props.data.map((v, i) => [String(i), v]);
			return Object.entries(props.data);
		});
		const size = computed(() => entries.value.length);
		const valuePreview = computed(() => {
			if (isObject.value) return isArray.value ? `Array(${size.value})` : `{${size.value} keys}`;
			if (typeof props.data === "string") return `"${props.data}"`;
			if (props.data === null) return "null";
			if (props.data === void 0) return "undefined";
			return String(props.data);
		});
		const valueType = computed(() => {
			if (isArray.value) return "array";
			if (isPlainObject.value) return "object";
			if (props.data === null) return "null";
			return typeof props.data;
		});
		const valueClass = computed(() => {
			switch (valueType.value) {
				case "string": return "jt-string";
				case "number":
				case "bigint": return "jt-number";
				case "boolean": return "jt-boolean";
				case "null": return "jt-null";
				default: return "jt-other";
			}
		});
		return (_ctx, _cache) => {
			const _component_JsonTree = resolveComponent("JsonTree", true);
			return openBlock(), createElementBlock("div", { class: normalizeClass(["json-tree", { "is-root": __props.level === 0 }]) }, [__props.name !== void 0 ? (openBlock(), createElementBlock("span", {
				key: 0,
				class: "jt-key",
				onClick: _cache[0] || (_cache[0] = ($event) => isObject.value && toggle())
			}, [isObject.value ? (openBlock(), createElementBlock("span", {
				key: 0,
				class: normalizeClass(["jt-arrow", { collapsed: !expanded.value }])
			}, "▶", 2)) : (openBlock(), createElementBlock("span", _hoisted_1$2)), createTextVNode(" \"" + toDisplayString(__props.name) + "\": ", 1)])) : createCommentVNode("", true), !isObject.value ? (openBlock(), createElementBlock("span", {
				key: 1,
				class: normalizeClass(valueClass.value)
			}, toDisplayString(valuePreview.value), 3)) : (openBlock(), createElementBlock(Fragment, { key: 2 }, [size.value === 0 ? (openBlock(), createElementBlock("span", _hoisted_2$2, toDisplayString(isArray.value ? "[]" : "{}"), 1)) : (openBlock(), createElementBlock(Fragment, { key: 1 }, [createElementVNode("span", {
				class: "jt-toggle",
				onClick: toggle
			}, [createElementVNode("span", { class: normalizeClass(["jt-arrow", { collapsed: !expanded.value }]) }, "▶", 2), !expanded.value ? (openBlock(), createElementBlock("span", _hoisted_3$2, toDisplayString(valuePreview.value), 1)) : createCommentVNode("", true)]), expanded.value ? (openBlock(), createElementBlock("div", _hoisted_4$2, [(openBlock(true), createElementBlock(Fragment, null, renderList(entries.value, ([k, v]) => {
				return openBlock(), createElementBlock("div", {
					key: k,
					class: "jt-child"
				}, [createVNode(_component_JsonTree, {
					data: v,
					name: k,
					level: __props.level + 1,
					"default-expanded": true
				}, null, 8, [
					"data",
					"name",
					"level"
				])]);
			}), 128))])) : createCommentVNode("", true)], 64))], 64))], 2);
		};
	}
}), [["__scopeId", "data-v-56af7729"]]);
//#endregion
//#region src/components/ConnectorDesigner/TestConsole.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$1 = { class: "test-console" };
var _hoisted_2$1 = { class: "op-path" };
var _hoisted_3$1 = {
	key: 1,
	class: "muted-tip"
};
var _hoisted_4$1 = {
	key: 0,
	class: "muted-tip"
};
var _hoisted_5 = {
	key: 1,
	class: "response-area"
};
var _hoisted_6 = { class: "response-meta" };
var _hoisted_7 = { class: "meta-item" };
var _hoisted_8 = { class: "meta-item" };
var _hoisted_9 = { class: "meta-value" };
var _hoisted_10 = {
	key: 0,
	class: "meta-item"
};
var _hoisted_11 = { class: "meta-value error-text" };
var _hoisted_12 = {
	key: 0,
	class: "response-section"
};
var _hoisted_13 = { class: "header-name" };
var _hoisted_14 = { class: "response-section" };
var _hoisted_15 = {
	key: 0,
	class: "muted-tip"
};
var _hoisted_16 = {
	key: 1,
	class: "body-tree"
};
var _hoisted_17 = { class: "history-header" };
var _hoisted_18 = { class: "panel-title" };
var _hoisted_19 = {
	key: 0,
	class: "muted-tip",
	style: { "padding": "12px" }
};
var _hoisted_20 = {
	key: 1,
	class: "status-badge status-5xx"
};
var _hoisted_21 = {
	key: 2,
	class: "muted-tip"
};
//#endregion
//#region src/components/ConnectorDesigner/TestConsole.vue
var TestConsole_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "TestConsole",
	props: {
		connectorCode: {},
		type: {},
		operations: {},
		sqlTemplates: {}
	},
	setup(__props) {
		/**
		* 连接器测试控制台（Task 10）。
		*
		* <p>借鉴 Power Apps Custom Connectors 的 Test 标签页：
		* <ol>
		*   <li>选择操作（下拉）</li>
		*   <li>根据操作的 params / body 动态生成输入表单</li>
		*   <li>点击「发送」调用 testOperation API 测试单个操作</li>
		*   <li>展示响应：状态码（带颜色）、Headers 表格、Body（JSON 树形）、耗时</li>
		*   <li>请求历史持久化到 localStorage（key: lowcode:connector-test-history:${connectorCode}），
		*       最多保留 20 条，支持点击「重放」回填表单与响应，支持「清空历史」</li>
		* </ol></p>
		*/
		const props = __props;
		/**
		* 测试历史记录项。
		*
		* <p>持久化到 localStorage（key: lowcode:connector-test-history:${connectorCode}），
		* 最多保留 20 条。包含请求/响应全部信息，支持点击重放（回填表单）。</p>
		*/
		const HISTORY_LIMIT = 20;
		/** 历史记录折叠面板展开状态（el-collapse v-model 为展开项 name 数组） */
		const historyCollapsed = ref([]);
		const selectedOpName = ref("");
		const paramValues = ref({});
		const bodyText = ref("");
		const sending = ref(false);
		const currentResult = ref(null);
		const history = ref([]);
		/** 拼接 localStorage key */
		function historyStorageKey() {
			return `lowcode:connector-test-history:${props.connectorCode}`;
		}
		/**
		* 从 localStorage 读取历史记录。
		*
		* <p>容错处理：connectorCode 为空、localStorage 不可用、JSON 解析失败、
		* 数据结构异常时均返回空数组，避免影响主流程。</p>
		*/
		function loadHistoryFromStorage() {
			if (!props.connectorCode) return [];
			try {
				const raw = localStorage.getItem(historyStorageKey());
				if (!raw) return [];
				const parsed = JSON.parse(raw);
				if (!Array.isArray(parsed)) return [];
				return parsed.filter((item) => item !== null && typeof item === "object" && typeof item.timestamp === "number" && typeof item.operation === "string").slice(0, HISTORY_LIMIT);
			} catch (_unused) {
				return [];
			}
		}
		/** 将当前 history 持久化到 localStorage（失败时静默降级） */
		function saveHistoryToStorage() {
			if (!props.connectorCode) return;
			try {
				localStorage.setItem(historyStorageKey(), JSON.stringify(history.value));
			} catch (_unused2) {}
		}
		const availableOps = computed(() => {
			if (props.type === "REST") return props.operations;
			return props.sqlTemplates.map((t) => ({
				name: t.name,
				method: "GET",
				path: "",
				headers: [],
				params: [],
				body: null
			}));
		});
		const currentOp = computed(() => {
			if (!selectedOpName.value) return null;
			if (props.type === "REST") return props.operations.find((o) => o.name === selectedOpName.value) || null;
			const t = props.sqlTemplates.find((s) => s.name === selectedOpName.value);
			if (!t) return null;
			return {
				name: t.name,
				method: "GET",
				path: "",
				headers: [],
				params: [],
				body: null
			};
		});
		const showBody = computed(() => props.type === "REST" && currentOp.value !== null && (currentOp.value.method === "POST" || currentOp.value.method === "PUT"));
		watch(selectedOpName, () => {
			resetForm();
		});
		function resetForm() {
			paramValues.value = {};
			if (currentOp.value) currentOp.value.params.forEach((p) => {
				paramValues.value[p.key] = "";
			});
			bodyText.value = "";
			currentResult.value = null;
		}
		function statusClass(code) {
			if (code === void 0) return "status-unknown";
			if (code >= 200 && code < 300) return "status-2xx";
			if (code >= 300 && code < 400) return "status-3xx";
			if (code >= 400 && code < 500) return "status-4xx";
			if (code >= 500) return "status-5xx";
			return "status-unknown";
		}
		function statusText(code) {
			if (code === void 0) return "—";
			return String(code);
		}
		const headerRows = computed(() => {
			var _currentResult$value;
			if (!((_currentResult$value = currentResult.value) === null || _currentResult$value === void 0 ? void 0 : _currentResult$value.headers)) return [];
			return Object.entries(currentResult.value.headers);
		});
		const hasResult = computed(() => currentResult.value !== null);
		const hasHistory = computed(() => history.value.length > 0);
		async function send() {
			if (!selectedOpName.value) {
				ElMessage.warning("请先选择操作");
				return;
			}
			if (!props.connectorCode) {
				ElMessage.warning("请先保存连接器后再测试");
				return;
			}
			let body = void 0;
			if (showBody.value && bodyText.value.trim()) try {
				body = JSON.parse(bodyText.value);
			} catch (_unused3) {
				ElMessage.error("Body 不是合法 JSON");
				return;
			}
			const params = {};
			Object.entries(paramValues.value).forEach(([k, v]) => {
				if (v !== "" && v !== null && v !== void 0) params[k] = v;
			});
			const op = currentOp.value;
			const requestHeaders = {};
			if (op) op.headers.forEach((h) => {
				if (h.key) requestHeaders[h.key] = h.value;
			});
			sending.value = true;
			const historyItem = {
				timestamp: Date.now(),
				operation: selectedOpName.value,
				method: (op === null || op === void 0 ? void 0 : op.method) || "",
				url: (op === null || op === void 0 ? void 0 : op.path) || "",
				requestHeaders,
				requestBody: body,
				status: 0,
				responseHeaders: {},
				responseBody: void 0,
				duration: 0
			};
			try {
				const result = await testOperation(props.connectorCode, {
					operationName: selectedOpName.value,
					params,
					body
				});
				currentResult.value = result;
				historyItem.status = result.statusCode || 0;
				historyItem.responseHeaders = result.headers || {};
				historyItem.responseBody = result.body;
				historyItem.duration = result.durationMillis || 0;
				if (result.error) {
					historyItem.error = result.error;
					ElMessage.warning("测试返回错误：" + result.error);
				} else if (result.statusCode && result.statusCode >= 400) ElMessage.warning(`响应状态码 ${result.statusCode}`);
				else ElMessage.success("测试完成");
			} catch (e) {
				const errorMsg = (e === null || e === void 0 ? void 0 : e.message) || String(e);
				historyItem.error = errorMsg;
				currentResult.value = { error: errorMsg };
				ElMessage.error("测试请求失败：" + errorMsg);
			} finally {
				sending.value = false;
				history.value.unshift(historyItem);
				if (history.value.length > HISTORY_LIMIT) history.value = history.value.slice(0, HISTORY_LIMIT);
				saveHistoryToStorage();
			}
		}
		/**
		* 重放历史记录：填充操作选择 + Body 文本，并展示历史响应。
		*
		* <p>选择操作后会触发 selectedOpName 的 watch，自动 resetForm 重置参数表单，
		* 故通过 setTimeout 在 watch 触发后再回填 Body 与响应。</p>
		*/
		function loadHistory(item) {
			selectedOpName.value = item.operation;
			setTimeout(() => {
				paramValues.value = {};
				if (currentOp.value) currentOp.value.params.forEach((p) => {
					paramValues.value[p.key] = "";
				});
				bodyText.value = item.requestBody !== void 0 && item.requestBody !== null ? safeStringify(item.requestBody) : "";
				currentResult.value = {
					statusCode: item.status || void 0,
					headers: item.responseHeaders,
					body: item.responseBody,
					durationMillis: item.duration || void 0,
					error: item.error
				};
			}, 0);
		}
		/** 安全序列化（处理循环引用等异常） */
		function safeStringify(v) {
			try {
				return JSON.stringify(v, null, 2);
			} catch (_unused4) {
				return String(v);
			}
		}
		/** 清空历史记录（含 localStorage） */
		async function clearHistory() {
			try {
				await ElMessageBox.confirm("确认清空所有历史记录？此操作不可恢复。", "清空确认", {
					type: "warning",
					confirmButtonText: "清空",
					cancelButtonText: "取消"
				});
			} catch (_unused5) {
				return;
			}
			history.value = [];
			currentResult.value = null;
			if (props.connectorCode) try {
				localStorage.removeItem(historyStorageKey());
			} catch (_unused6) {}
		}
		function formatTime(ts) {
			const d = new Date(ts);
			return `${String(d.getHours()).padStart(2, "0")}:${String(d.getMinutes()).padStart(2, "0")}:${String(d.getSeconds()).padStart(2, "0")}`;
		}
		/** 完整日期时间格式（用于历史记录展开行展示） */
		function formatDateTime(ts) {
			const d = new Date(ts);
			return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")} ${formatTime(ts)}`;
		}
		onMounted(() => {
			history.value = loadHistoryFromStorage();
		});
		watch(() => props.connectorCode, () => {
			history.value = loadHistoryFromStorage();
		});
		return (_ctx, _cache) => {
			const _component_el_alert = resolveComponent("el-alert");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_divider = resolveComponent("el-divider");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_collapse_item = resolveComponent("el-collapse-item");
			const _component_el_collapse = resolveComponent("el-collapse");
			return openBlock(), createElementBlock("div", _hoisted_1$1, [
				!props.connectorCode ? (openBlock(), createBlock(_component_el_alert, {
					key: 0,
					title: "请先保存连接器后再进行测试",
					type: "warning",
					closable: false,
					"show-icon": "",
					style: { "margin-bottom": "12px" }
				})) : createCommentVNode("", true),
				createVNode(_component_el_row, { gutter: 16 }, {
					default: withCtx(() => [createVNode(_component_el_col, { span: 12 }, {
						default: withCtx(() => [createVNode(_component_el_card, {
							shadow: "never",
							class: "panel"
						}, {
							header: withCtx(() => [..._cache[3] || (_cache[3] = [createElementVNode("span", { class: "panel-title" }, "请求构建", -1)])]),
							default: withCtx(() => [createVNode(_component_el_form, {
								"label-width": "100px",
								size: "small"
							}, {
								default: withCtx(() => [
									createVNode(_component_el_form_item, { label: "选择操作" }, {
										default: withCtx(() => [createVNode(_component_el_select, {
											modelValue: selectedOpName.value,
											"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => selectedOpName.value = $event),
											placeholder: "请选择操作",
											style: { "width": "100%" },
											filterable: ""
										}, {
											default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(availableOps.value, (op) => {
												return openBlock(), createBlock(_component_el_option, {
													key: op.name,
													label: op.name,
													value: op.name
												}, null, 8, ["label", "value"]);
											}), 128))]),
											_: 1
										}, 8, ["modelValue"])]),
										_: 1
									}),
									currentOp.value ? (openBlock(), createElementBlock(Fragment, { key: 0 }, [
										props.type === "REST" ? (openBlock(), createBlock(_component_el_form_item, {
											key: 0,
											label: "HTTP"
										}, {
											default: withCtx(() => [createVNode(_component_el_tag, { size: "small" }, {
												default: withCtx(() => [createTextVNode(toDisplayString(currentOp.value.method), 1)]),
												_: 1
											}), createElementVNode("span", _hoisted_2$1, toDisplayString(currentOp.value.path || "—"), 1)]),
											_: 1
										})) : createCommentVNode("", true),
										createVNode(_component_el_divider, { "content-position": "left" }, {
											default: withCtx(() => [..._cache[4] || (_cache[4] = [createTextVNode("参数", -1)])]),
											_: 1
										}),
										currentOp.value.params.length === 0 ? (openBlock(), createElementBlock("div", _hoisted_3$1, "该操作无参数")) : createCommentVNode("", true),
										(openBlock(true), createElementBlock(Fragment, null, renderList(currentOp.value.params, (p) => {
											return openBlock(), createBlock(_component_el_form_item, {
												key: p.key,
												label: p.key
											}, {
												default: withCtx(() => [createVNode(_component_el_input, {
													modelValue: paramValues.value[p.key],
													"onUpdate:modelValue": ($event) => paramValues.value[p.key] = $event,
													placeholder: p.value || "请输入值"
												}, null, 8, [
													"modelValue",
													"onUpdate:modelValue",
													"placeholder"
												])]),
												_: 2
											}, 1032, ["label"]);
										}), 128)),
										showBody.value ? (openBlock(), createElementBlock(Fragment, { key: 2 }, [createVNode(_component_el_divider, { "content-position": "left" }, {
											default: withCtx(() => [..._cache[5] || (_cache[5] = [createTextVNode("请求 Body", -1)])]),
											_: 1
										}), createVNode(_component_el_form_item, { label: "Body" }, {
											default: withCtx(() => [createVNode(_component_el_input, {
												modelValue: bodyText.value,
												"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => bodyText.value = $event),
												type: "textarea",
												rows: 6,
												placeholder: "{\"key\":\"value\"}"
											}, null, 8, ["modelValue"])]),
											_: 1
										})], 64)) : createCommentVNode("", true)
									], 64)) : createCommentVNode("", true),
									createVNode(_component_el_form_item, null, {
										default: withCtx(() => [createVNode(_component_el_button, {
											type: "primary",
											loading: sending.value,
											disabled: !selectedOpName.value || !props.connectorCode,
											onClick: send
										}, {
											default: withCtx(() => [..._cache[6] || (_cache[6] = [createTextVNode(" 发送请求 ", -1)])]),
											_: 1
										}, 8, ["loading", "disabled"]), createVNode(_component_el_button, {
											disabled: !selectedOpName.value,
											onClick: resetForm
										}, {
											default: withCtx(() => [..._cache[7] || (_cache[7] = [createTextVNode("重置", -1)])]),
											_: 1
										}, 8, ["disabled"])]),
										_: 1
									})
								]),
								_: 1
							})]),
							_: 1
						})]),
						_: 1
					}), createVNode(_component_el_col, { span: 12 }, {
						default: withCtx(() => [createVNode(_component_el_card, {
							shadow: "never",
							class: "panel"
						}, {
							header: withCtx(() => [..._cache[8] || (_cache[8] = [createElementVNode("span", { class: "panel-title" }, "响应结果", -1)])]),
							default: withCtx(() => {
								var _currentResult$value2, _currentResult$value3, _currentResult$value4, _currentResult$value5, _currentResult$value6, _currentResult$value7;
								return [!hasResult.value ? (openBlock(), createElementBlock("div", _hoisted_4$1, "点击「发送请求」后在此查看响应")) : (openBlock(), createElementBlock("div", _hoisted_5, [
									createElementVNode("div", _hoisted_6, [
										createElementVNode("div", _hoisted_7, [_cache[9] || (_cache[9] = createElementVNode("span", { class: "meta-label" }, "状态码", -1)), createElementVNode("span", { class: normalizeClass(["status-badge", statusClass((_currentResult$value2 = currentResult.value) === null || _currentResult$value2 === void 0 ? void 0 : _currentResult$value2.statusCode)]) }, toDisplayString(statusText((_currentResult$value3 = currentResult.value) === null || _currentResult$value3 === void 0 ? void 0 : _currentResult$value3.statusCode)), 3)]),
										createElementVNode("div", _hoisted_8, [_cache[10] || (_cache[10] = createElementVNode("span", { class: "meta-label" }, "耗时", -1)), createElementVNode("span", _hoisted_9, toDisplayString(((_currentResult$value4 = currentResult.value) === null || _currentResult$value4 === void 0 ? void 0 : _currentResult$value4.durationMillis) != null ? currentResult.value.durationMillis + " ms" : "—"), 1)]),
										((_currentResult$value5 = currentResult.value) === null || _currentResult$value5 === void 0 ? void 0 : _currentResult$value5.error) ? (openBlock(), createElementBlock("div", _hoisted_10, [_cache[11] || (_cache[11] = createElementVNode("span", { class: "meta-label" }, "错误", -1)), createElementVNode("span", _hoisted_11, toDisplayString(currentResult.value.error), 1)])) : createCommentVNode("", true)
									]),
									headerRows.value.length > 0 ? (openBlock(), createElementBlock("div", _hoisted_12, [_cache[12] || (_cache[12] = createElementVNode("div", { class: "section-title" }, "响应 Headers", -1)), createVNode(_component_el_table, {
										data: headerRows.value,
										size: "small",
										border: "",
										"max-height": "160"
									}, {
										default: withCtx(() => [createVNode(_component_el_table_column, {
											label: "名称",
											width: "180",
											"show-overflow-tooltip": ""
										}, {
											default: withCtx(({ row }) => [createElementVNode("span", _hoisted_13, toDisplayString(row[0]), 1)]),
											_: 1
										}), createVNode(_component_el_table_column, {
											label: "值",
											"show-overflow-tooltip": ""
										}, {
											default: withCtx(({ row }) => [createTextVNode(toDisplayString(row[1]), 1)]),
											_: 1
										})]),
										_: 1
									}, 8, ["data"])])) : createCommentVNode("", true),
									createElementVNode("div", _hoisted_14, [_cache[13] || (_cache[13] = createElementVNode("div", { class: "section-title" }, "响应 Body", -1)), ((_currentResult$value6 = currentResult.value) === null || _currentResult$value6 === void 0 ? void 0 : _currentResult$value6.body) === void 0 || ((_currentResult$value7 = currentResult.value) === null || _currentResult$value7 === void 0 ? void 0 : _currentResult$value7.body) === null ? (openBlock(), createElementBlock("div", _hoisted_15, " 无响应体 ")) : (openBlock(), createElementBlock("div", _hoisted_16, [createVNode(JsonTree_default, {
										data: currentResult.value.body,
										"default-expanded": true
									}, null, 8, ["data"])]))])
								]))];
							}),
							_: 1
						})]),
						_: 1
					})]),
					_: 1
				}),
				createVNode(_component_el_collapse, {
					modelValue: historyCollapsed.value,
					"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => historyCollapsed.value = $event),
					class: "history-collapse"
				}, {
					default: withCtx(() => [createVNode(_component_el_collapse_item, { name: "history" }, {
						title: withCtx(() => [createElementVNode("div", _hoisted_17, [createElementVNode("span", _hoisted_18, " 历史记录（" + toDisplayString(history.value.length) + "/" + toDisplayString(HISTORY_LIMIT) + "，持久化） ", 1), hasHistory.value ? (openBlock(), createBlock(_component_el_button, {
							key: 0,
							size: "small",
							link: "",
							type: "danger",
							onClick: withModifiers(clearHistory, ["stop"])
						}, {
							default: withCtx(() => [..._cache[14] || (_cache[14] = [createTextVNode("清空历史", -1)])]),
							_: 1
						})) : createCommentVNode("", true)])]),
						default: withCtx(() => [!hasHistory.value ? (openBlock(), createElementBlock("div", _hoisted_19, " 暂无历史记录。执行测试后将自动保存到浏览器本地，下次打开仍可查看。 ")) : (openBlock(), createBlock(_component_el_table, {
							key: 1,
							data: history.value,
							size: "small",
							border: ""
						}, {
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									label: "时间",
									width: "160"
								}, {
									default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDateTime(row.timestamp)), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "操作",
									prop: "operation",
									"min-width": "140",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									label: "方法",
									prop: "method",
									width: "80",
									align: "center"
								}),
								createVNode(_component_el_table_column, {
									label: "状态",
									width: "80",
									align: "center"
								}, {
									default: withCtx(({ row }) => [row.status ? (openBlock(), createElementBlock("span", {
										key: 0,
										class: normalizeClass(["status-badge", statusClass(row.status)])
									}, toDisplayString(row.status), 3)) : row.error ? (openBlock(), createElementBlock("span", _hoisted_20, "ERR")) : (openBlock(), createElementBlock("span", _hoisted_21, "—"))]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "耗时",
									width: "90",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createTextVNode(toDisplayString(row.duration ? row.duration + " ms" : "—"), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "请求 Body",
									"min-width": "200",
									"show-overflow-tooltip": ""
								}, {
									default: withCtx(({ row }) => [createTextVNode(toDisplayString(row.requestBody === void 0 || row.requestBody === null ? "—" : safeStringify(row.requestBody)), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "操作",
									width: "120",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_button, {
										size: "small",
										link: "",
										type: "primary",
										onClick: ($event) => loadHistory(row)
									}, {
										default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode("重放", -1)])]),
										_: 1
									}, 8, ["onClick"])]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["data"]))]),
						_: 1
					})]),
					_: 1
				}, 8, ["modelValue"])
			]);
		};
	}
}), [["__scopeId", "data-v-ea88b741"]]);
//#endregion
//#region src/views/lowcode/connector-designer/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { style: { "padding": "16px" } };
var _hoisted_2 = { style: {
	"display": "flex",
	"justify-content": "space-between",
	"align-items": "center"
} };
var _hoisted_3 = { class: "step-body" };
var _hoisted_4 = { class: "dialog-footer" };
//#endregion
//#region src/views/lowcode/connector-designer/index.vue
var connector_designer_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "ConnectorDesignerView",
	__name: "index",
	setup(__props) {
		/**
		* 连接器设计器主页面（批次 2 — T8/T9/T10）。
		*
		* <p>借鉴 Power Apps Custom Connectors 的分步表单流程，将原先的「表格 + JSON 文本框」
		* 重构为 el-steps 分步表单：
		* <ol>
		*   <li>Step 1 基本信息（code/name/description/type）</li>
		*   <li>Step 2 认证配置（REST）/ 数据源配置（DB）</li>
		*   <li>Step 3 操作列表（REST）/ SQL 模板（DB），REST 集成 OpenAPI 导入（T9）</li>
		*   <li>Step 4 响应映射</li>
		*   <li>Step 5 分页配置（仅 REST）</li>
		*   <li>Step 6 重试与超时</li>
		*   <li>Step 7 测试控制台（T10）</li>
		* </ol></p>
		*
		* <p>config 在保存时序列化为 JSON 字符串存入 connector.config；
		* 加载时通过 parseConnectorConfig 反向解析，并兼容旧版简单 JSON。</p>
		*/
		const list = ref([]);
		const dialogVisible = ref(false);
		const activeStep = ref(0);
		const saving = ref(false);
		/** 列表加载 */
		async function load() {
			list.value = await getConnectorList();
		}
		/** 顶层字段（直接对应 LowCodeConnector 的非 config 字段） */
		const basic = reactive({
			code: "",
			name: "",
			description: "",
			bizType: "",
			type: "REST"
		});
		/** 结构化 config（分步表单各 Step 共享） */
		const config = reactive(createDefaultConfig("REST"));
		/** 当前编辑的连接器 id（保存后用于测试） */
		const editingId = ref(void 0);
		const steps = computed(() => {
			if (basic.type === "REST") return [
				{
					title: "基本信息",
					description: "编码 / 名称 / 类型"
				},
				{
					title: "认证配置",
					description: "Base URL + Auth"
				},
				{
					title: "操作列表",
					description: "REST 操作 + OpenAPI 导入"
				},
				{
					title: "响应映射",
					description: "JSONPath → 实体字段"
				},
				{
					title: "分页配置",
					description: "OFFSET / PAGE / NEXT_LINK"
				},
				{
					title: "重试与超时",
					description: "maxAttempts / timeout"
				},
				{
					title: "测试",
					description: "测试控制台"
				}
			];
			return [
				{
					title: "基本信息",
					description: "编码 / 名称 / 类型"
				},
				{
					title: "数据源配置",
					description: "JDBC + 连接池"
				},
				{
					title: "SQL 模板",
					description: "QUERY / UPDATE"
				},
				{
					title: "响应映射",
					description: "列 → 实体字段"
				},
				{
					title: "重试与超时",
					description: "maxAttempts / timeout"
				},
				{
					title: "测试",
					description: "测试控制台"
				}
			];
		});
		const isLastStep = computed(() => activeStep.value === steps.value.length - 1);
		function openNew() {
			editingId.value = void 0;
			activeStep.value = 0;
			Object.assign(basic, {
				code: "",
				name: "",
				description: "",
				bizType: "",
				type: "REST"
			});
			resetConfig("REST");
			dialogVisible.value = true;
		}
		function openEdit(row) {
			editingId.value = row.id;
			activeStep.value = 0;
			basic.code = row.code;
			basic.name = row.name;
			basic.description = row.description || "";
			basic.bizType = row.bizType || "";
			basic.type = row.type;
			const parsed = parseConnectorConfig(row.config, row.type);
			Object.assign(config, parsed);
			dialogVisible.value = true;
		}
		function resetConfig(type) {
			const fresh = createDefaultConfig(type);
			Object.assign(config, fresh);
		}
		/** 类型切换时重置 config（保留基本信息） */
		function handleTypeChange(type) {
			Object.assign(config, createDefaultConfig(type));
			activeStep.value = 0;
		}
		function next() {
			if (activeStep.value === 0) {
				if (!basic.code.trim() || !basic.name.trim()) {
					ElMessage.warning("请填写编码和名称");
					return;
				}
			}
			if (activeStep.value < steps.value.length - 1) activeStep.value++;
		}
		function prev() {
			if (activeStep.value > 0) activeStep.value--;
		}
		function goToStep(index) {
			activeStep.value = index;
		}
		function buildConfigObject() {
			return {
				type: basic.type,
				baseUrl: config.baseUrl,
				authType: config.authType,
				username: config.username,
				password: config.password,
				token: config.token,
				headerName: config.headerName,
				apiKey: config.apiKey,
				operations: config.operations,
				driverClassName: config.driverClassName,
				dbUrl: config.dbUrl,
				dbUsername: config.dbUsername,
				dbPassword: config.dbPassword,
				maxPoolSize: config.maxPoolSize,
				sqlTemplates: config.sqlTemplates,
				responseMapping: config.responseMapping,
				pagination: config.pagination,
				retry: config.retry
			};
		}
		function buildConnector() {
			return {
				id: editingId.value,
				code: basic.code,
				name: basic.name,
				description: basic.description,
				bizType: basic.bizType,
				type: basic.type,
				config: serializeConnectorConfig(buildConfigObject()),
				status: "ACTIVE"
			};
		}
		async function save() {
			if (!basic.code.trim() || !basic.name.trim()) {
				ElMessage.warning("请填写编码和名称");
				activeStep.value = 0;
				return;
			}
			saving.value = true;
			try {
				const saved = await saveConnector(buildConnector());
				editingId.value = saved.id;
				ElMessage.success("保存成功");
				await load();
			} catch (e) {
				ElMessage.error("保存失败：" + ((e === null || e === void 0 ? void 0 : e.message) || String(e)));
			} finally {
				saving.value = false;
			}
		}
		async function saveAndClose() {
			if (!basic.code.trim() || !basic.name.trim()) {
				ElMessage.warning("请填写编码和名称");
				activeStep.value = 0;
				return;
			}
			saving.value = true;
			try {
				await saveConnector(buildConnector());
				ElMessage.success("保存成功");
				dialogVisible.value = false;
				await load();
			} catch (e) {
				ElMessage.error("保存失败：" + ((e === null || e === void 0 ? void 0 : e.message) || String(e)));
			} finally {
				saving.value = false;
			}
		}
		async function remove(row) {
			if (!row.id) return;
			try {
				await ElMessageBox.confirm(`确认删除连接器「${row.name}」？`, "确认", { type: "warning" });
				await deleteConnector(row.id);
				ElMessage.success("删除成功");
				await load();
			} catch (_unused) {}
		}
		async function quickTest(row) {
			const { testConnector } = await import("./lowcode-connector-Cjm1QnL-.js").then((n) => n.r);
			try {
				const result = await testConnector(row.code);
				ElMessage.success("测试结果: " + JSON.stringify(result));
			} catch (e) {
				ElMessage.error("测试失败：" + ((e === null || e === void 0 ? void 0 : e.message) || String(e)));
			}
		}
		function handleBasicTypeChange(val) {
			if (val !== basic.type) {
				basic.type = val;
				handleTypeChange(val);
			}
		}
		const configProxy = {
			authType: computed({
				get: () => {
					var _config$authType;
					return (_config$authType = config.authType) !== null && _config$authType !== void 0 ? _config$authType : "NONE";
				},
				set: (v) => config.authType = v
			}),
			username: computed({
				get: () => config.username || "",
				set: (v) => config.username = v
			}),
			password: computed({
				get: () => config.password || "",
				set: (v) => config.password = v
			}),
			token: computed({
				get: () => config.token || "",
				set: (v) => config.token = v
			}),
			headerName: computed({
				get: () => config.headerName || "",
				set: (v) => config.headerName = v
			}),
			apiKey: computed({
				get: () => config.apiKey || "",
				set: (v) => config.apiKey = v
			}),
			baseUrl: computed({
				get: () => config.baseUrl || "",
				set: (v) => config.baseUrl = v
			}),
			driverClassName: computed({
				get: () => config.driverClassName || "com.mysql.cj.jdbc.Driver",
				set: (v) => config.driverClassName = v
			}),
			dbUrl: computed({
				get: () => config.dbUrl || "",
				set: (v) => config.dbUrl = v
			}),
			dbUsername: computed({
				get: () => config.dbUsername || "",
				set: (v) => config.dbUsername = v
			}),
			dbPassword: computed({
				get: () => config.dbPassword || "",
				set: (v) => config.dbPassword = v
			}),
			maxPoolSize: computed({
				get: () => {
					var _config$maxPoolSize;
					return (_config$maxPoolSize = config.maxPoolSize) !== null && _config$maxPoolSize !== void 0 ? _config$maxPoolSize : 10;
				},
				set: (v) => config.maxPoolSize = v
			}),
			operations: computed({
				get: () => config.operations || [],
				set: (v) => config.operations = v
			}),
			sqlTemplates: computed({
				get: () => config.sqlTemplates || [],
				set: (v) => config.sqlTemplates = v
			}),
			responseMapping: computed({
				get: () => config.responseMapping || [],
				set: (v) => config.responseMapping = v
			}),
			pagination: computed({
				get: () => config.pagination || { type: "NONE" },
				set: (v) => config.pagination = v
			}),
			retry: computed({
				get: () => config.retry || {
					maxAttempts: 3,
					waitMillis: 1e3,
					timeoutMillis: 3e4,
					retryOnStatusCodes: [
						500,
						502,
						503
					]
				},
				set: (v) => config.retry = v
			})
		};
		const connectorCodeForTest = computed(() => basic.code);
		onMounted(load);
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_step = resolveComponent("el-step");
			const _component_el_steps = resolveComponent("el-steps");
			const _component_el_dialog = resolveComponent("el-dialog");
			return openBlock(), createElementBlock("div", _hoisted_1, [createVNode(_component_el_card, { shadow: "never" }, {
				header: withCtx(() => [createElementVNode("div", _hoisted_2, [_cache[25] || (_cache[25] = createElementVNode("span", null, "连接器配置", -1)), createVNode(_component_el_button, {
					type: "primary",
					onClick: openNew
				}, {
					default: withCtx(() => [..._cache[24] || (_cache[24] = [createTextVNode("新建连接器", -1)])]),
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
							width: "80"
						}, {
							default: withCtx(({ row }) => [createVNode(_component_el_tag, { type: row.type === "REST" ? "" : "success" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(row.type), 1)]),
								_: 2
							}, 1032, ["type"])]),
							_: 1
						}),
						createVNode(_component_el_table_column, {
							label: "业务",
							prop: "bizType",
							width: "120",
							"show-overflow-tooltip": ""
						}),
						createVNode(_component_el_table_column, {
							label: "描述",
							prop: "description",
							"min-width": "200",
							"show-overflow-tooltip": ""
						}),
						createVNode(_component_el_table_column, {
							label: "操作",
							width: "200",
							align: "center"
						}, {
							default: withCtx(({ row }) => [
								createVNode(_component_el_button, {
									size: "small",
									onClick: ($event) => openEdit(row)
								}, {
									default: withCtx(() => [..._cache[26] || (_cache[26] = [createTextVNode("编辑", -1)])]),
									_: 1
								}, 8, ["onClick"]),
								createVNode(_component_el_button, {
									size: "small",
									type: "success",
									onClick: ($event) => quickTest(row)
								}, {
									default: withCtx(() => [..._cache[27] || (_cache[27] = [createTextVNode("快速测试", -1)])]),
									_: 1
								}, 8, ["onClick"]),
								createVNode(_component_el_button, {
									size: "small",
									type: "danger",
									onClick: ($event) => remove(row)
								}, {
									default: withCtx(() => [..._cache[28] || (_cache[28] = [createTextVNode("删除", -1)])]),
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
				"onUpdate:modelValue": _cache[23] || (_cache[23] = ($event) => dialogVisible.value = $event),
				title: editingId.value ? "编辑连接器" : "新建连接器",
				width: "960px",
				"close-on-click-modal": false,
				"destroy-on-close": "",
				class: "connector-designer-dialog"
			}, {
				footer: withCtx(() => [createElementVNode("div", _hoisted_4, [
					createVNode(_component_el_button, { onClick: _cache[22] || (_cache[22] = ($event) => dialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[29] || (_cache[29] = [createTextVNode("关闭", -1)])]),
						_: 1
					}),
					createVNode(_component_el_button, {
						disabled: activeStep.value === 0,
						onClick: prev
					}, {
						default: withCtx(() => [..._cache[30] || (_cache[30] = [createTextVNode("上一步", -1)])]),
						_: 1
					}, 8, ["disabled"]),
					!isLastStep.value ? (openBlock(), createBlock(_component_el_button, {
						key: 0,
						type: "primary",
						onClick: next
					}, {
						default: withCtx(() => [..._cache[31] || (_cache[31] = [createTextVNode("下一步", -1)])]),
						_: 1
					})) : createCommentVNode("", true),
					createVNode(_component_el_button, {
						type: "success",
						loading: saving.value,
						onClick: save
					}, {
						default: withCtx(() => [..._cache[32] || (_cache[32] = [createTextVNode("保存", -1)])]),
						_: 1
					}, 8, ["loading"]),
					createVNode(_component_el_button, {
						type: "primary",
						loading: saving.value,
						onClick: saveAndClose
					}, {
						default: withCtx(() => [..._cache[33] || (_cache[33] = [createTextVNode("保存并关闭", -1)])]),
						_: 1
					}, 8, ["loading"])
				])]),
				default: withCtx(() => [createVNode(_component_el_steps, {
					active: activeStep.value,
					"finish-status": "success",
					"align-center": "",
					class: "step-header"
				}, {
					default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(steps.value, (s, idx) => {
						return openBlock(), createBlock(_component_el_step, {
							key: s.title,
							title: s.title,
							description: s.description,
							onClick: ($event) => goToStep(idx)
						}, null, 8, [
							"title",
							"description",
							"onClick"
						]);
					}), 128))]),
					_: 1
				}, 8, ["active"]), createElementVNode("div", _hoisted_3, [
					withDirectives(createElementVNode("div", null, [createVNode(StepBasicInfo_default, {
						code: basic.code,
						"onUpdate:code": _cache[0] || (_cache[0] = ($event) => basic.code = $event),
						name: basic.name,
						"onUpdate:name": _cache[1] || (_cache[1] = ($event) => basic.name = $event),
						description: basic.description,
						"onUpdate:description": _cache[2] || (_cache[2] = ($event) => basic.description = $event),
						bizType: basic.bizType,
						"onUpdate:bizType": _cache[3] || (_cache[3] = ($event) => basic.bizType = $event),
						type: basic.type,
						"onUpdate:type": [_cache[4] || (_cache[4] = ($event) => basic.type = $event), handleBasicTypeChange]
					}, null, 8, [
						"code",
						"name",
						"description",
						"bizType",
						"type"
					])], 512), [[vShow, activeStep.value === 0]]),
					withDirectives(createElementVNode("div", null, [createVNode(StepAuth_default, {
						type: basic.type,
						"auth-type": configProxy.authType.value,
						"onUpdate:authType": _cache[5] || (_cache[5] = ($event) => configProxy.authType.value = $event),
						username: configProxy.username.value,
						"onUpdate:username": _cache[6] || (_cache[6] = ($event) => configProxy.username.value = $event),
						password: configProxy.password.value,
						"onUpdate:password": _cache[7] || (_cache[7] = ($event) => configProxy.password.value = $event),
						token: configProxy.token.value,
						"onUpdate:token": _cache[8] || (_cache[8] = ($event) => configProxy.token.value = $event),
						"header-name": configProxy.headerName.value,
						"onUpdate:headerName": _cache[9] || (_cache[9] = ($event) => configProxy.headerName.value = $event),
						"api-key": configProxy.apiKey.value,
						"onUpdate:apiKey": _cache[10] || (_cache[10] = ($event) => configProxy.apiKey.value = $event),
						"base-url": configProxy.baseUrl.value,
						"onUpdate:baseUrl": _cache[11] || (_cache[11] = ($event) => configProxy.baseUrl.value = $event),
						"driver-class-name": configProxy.driverClassName.value,
						"onUpdate:driverClassName": _cache[12] || (_cache[12] = ($event) => configProxy.driverClassName.value = $event),
						"db-url": configProxy.dbUrl.value,
						"onUpdate:dbUrl": _cache[13] || (_cache[13] = ($event) => configProxy.dbUrl.value = $event),
						"db-username": configProxy.dbUsername.value,
						"onUpdate:dbUsername": _cache[14] || (_cache[14] = ($event) => configProxy.dbUsername.value = $event),
						"db-password": configProxy.dbPassword.value,
						"onUpdate:dbPassword": _cache[15] || (_cache[15] = ($event) => configProxy.dbPassword.value = $event),
						"max-pool-size": configProxy.maxPoolSize.value,
						"onUpdate:maxPoolSize": _cache[16] || (_cache[16] = ($event) => configProxy.maxPoolSize.value = $event)
					}, null, 8, [
						"type",
						"auth-type",
						"username",
						"password",
						"token",
						"header-name",
						"api-key",
						"base-url",
						"driver-class-name",
						"db-url",
						"db-username",
						"db-password",
						"max-pool-size"
					])], 512), [[vShow, activeStep.value === 1]]),
					withDirectives(createElementVNode("div", null, [createVNode(StepOperations_default, {
						type: basic.type,
						operations: configProxy.operations.value,
						"sql-templates": configProxy.sqlTemplates.value,
						"base-url": configProxy.baseUrl.value,
						"onUpdate:operations": _cache[17] || (_cache[17] = ($event) => configProxy.operations.value = $event),
						"onUpdate:sqlTemplates": _cache[18] || (_cache[18] = ($event) => configProxy.sqlTemplates.value = $event)
					}, null, 8, [
						"type",
						"operations",
						"sql-templates",
						"base-url"
					])], 512), [[vShow, activeStep.value === 2]]),
					withDirectives(createElementVNode("div", null, [createVNode(StepResponseMapping_default, {
						"response-mapping": configProxy.responseMapping.value,
						"onUpdate:responseMapping": _cache[19] || (_cache[19] = ($event) => configProxy.responseMapping.value = $event)
					}, null, 8, ["response-mapping"])], 512), [[vShow, activeStep.value === 3]]),
					withDirectives(createElementVNode("div", null, [createVNode(StepPagination_default, {
						pagination: configProxy.pagination.value,
						"onUpdate:pagination": _cache[20] || (_cache[20] = ($event) => configProxy.pagination.value = $event)
					}, null, 8, ["pagination"])], 512), [[vShow, activeStep.value === 4 && basic.type === "REST"]]),
					withDirectives(createElementVNode("div", null, [createVNode(StepRetry_default, {
						retry: configProxy.retry.value,
						"onUpdate:retry": _cache[21] || (_cache[21] = ($event) => configProxy.retry.value = $event)
					}, null, 8, ["retry"])], 512), [[vShow, basic.type === "REST" && activeStep.value === 5 || basic.type === "DB" && activeStep.value === 4]]),
					withDirectives(createElementVNode("div", null, [createVNode(TestConsole_default, {
						"connector-code": connectorCodeForTest.value,
						type: basic.type,
						operations: configProxy.operations.value,
						"sql-templates": configProxy.sqlTemplates.value
					}, null, 8, [
						"connector-code",
						"type",
						"operations",
						"sql-templates"
					])], 512), [[vShow, basic.type === "REST" && activeStep.value === 6 || basic.type === "DB" && activeStep.value === 5]])
				])]),
				_: 1
			}, 8, ["modelValue", "title"])]);
		};
	}
}), [["__scopeId", "data-v-93459202"]]);
//#endregion
export { connector_designer_default as default };

//# sourceMappingURL=connector-designer-CEWIyNzw.js.map