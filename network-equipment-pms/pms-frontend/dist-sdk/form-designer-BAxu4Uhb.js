import { c as useUserStore, d as useRouter, i as post, r as get, u as useRoute } from "./request-BQrAOfxW.js";
import { n as initBuiltinComponents, t as LowCodeComponentRegistry_default } from "./LowCodeComponentRegistry-BhIrM3BV.js";
import { A as getForm, E as exportForm, K as publishForm, R as importForm, X as updateForm, i as FieldType, o as LayoutType, p as archiveForm, v as createForm } from "./lowcode-F-suzo7c.js";
import { t as LowCodeFormRenderer_default } from "./LowCodeFormRenderer-AJit0-ob.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { t as ExpressionEditor_default } from "./ExpressionEditor-CFxBT6yN.js";
import { t as useUndoRedo } from "./useUndoRedo-C9SCn4rB.js";
import { n as BREAKPOINT_ORDER, r as BREAKPOINT_PREVIEW_WIDTH, t as BREAKPOINT_LABEL } from "./breakpoints-CjTxpeuh.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, isRef, nextTick, normalizeClass, normalizeStyle, onBeforeUnmount, onMounted, onUnmounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDynamicComponent, toDisplayString, unref, watch, withCtx, withModifiers } from "vue";
//#region src/components/LowCodePropertyPanel/PropField.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$3 = { class: "prop-label" };
var _hoisted_2$3 = {
	key: 0,
	class: "prop-required"
};
var _hoisted_3$2 = {
	key: 6,
	class: "prop-expression"
};
var _hoisted_4$1 = {
	key: 7,
	class: "prop-object"
};
var _hoisted_5$1 = {
	key: 8,
	class: "prop-array"
};
//#endregion
//#region src/components/LowCodePropertyPanel/PropField.vue
var PropField_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "PropField",
	__name: "PropField",
	props: {
		propDef: {},
		modelValue: {}
	},
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		/**
		* 单个属性字段的递归渲染组件（schema 驱动）。
		*
		* <p>根据 {@link ComponentPropDef.type} 渲染对应的 Element Plus 控件；
		* type=object 时按 properties 递归渲染子属性，type=array 时按 itemProp
		* 递归渲染数组项并支持新增/删除。组件通过自身文件名（PropField）实现递归引用。</p>
		*
		* <p>type=code / type=expression 复用批次1的 ExpressionEditor（textarea + 变量/字段
		* 侧栏 + 函数库 + 简易高亮）；expression 额外提供 language 切换下拉。</p>
		*
		* <p>所有变更通过 emit('update:modelValue') 上抛不可变新值，
		* 由父级（LowCodePropertyPanel）写回响应式 modelValue，保证单向数据流。</p>
		*/
		const props = __props;
		const emit = __emit;
		/** expression 类型的语言切换状态（缺省取 schema.language，回退 aviator） */
		const expressionLang = ref(props.propDef.language || "aviator");
		/** 显示标签：优先 label，回退 key */
		function label() {
			return props.propDef.label || props.propDef.key;
		}
		/** 容器值的安全读取：object 缺省返回 {}，array 缺省返回 [] */
		function containerValue() {
			if (props.propDef.type === "object") return props.modelValue && typeof props.modelValue === "object" ? props.modelValue : {};
			if (props.propDef.type === "array") return Array.isArray(props.modelValue) ? props.modelValue : [];
			return props.modelValue;
		}
		/** 数组项 schema：优先 itemProp，缺省按 string 渲染 */
		function itemSchema() {
			return props.propDef.itemProp || {
				key: "item",
				type: "string"
			};
		}
		/** 根据类型生成默认值（用于数组新增项初始化） */
		function defaultForType(type) {
			switch (type) {
				case "boolean": return false;
				case "number": return 0;
				case "object": return {};
				case "array": return [];
				default: return "";
			}
		}
		/** 数组：新增一项 */
		function addArrayItem() {
			const arr = containerValue();
			const itemDef = itemSchema();
			const newItem = itemDef.default !== void 0 ? itemDef.default : defaultForType(itemDef.type);
			emit("update:modelValue", [...arr, newItem]);
		}
		/** 数组：删除指定索引项 */
		function removeArrayItem(idx) {
			const arr = containerValue();
			emit("update:modelValue", arr.filter((_, i) => i !== idx));
		}
		/** object 子属性变更：emit 新对象 */
		function updateObjectField(key, v) {
			const obj = containerValue();
			emit("update:modelValue", {
				...obj,
				[key]: v
			});
		}
		/** 数组项变更：emit 新数组 */
		function updateArrayItem(idx, v) {
			const arr = containerValue();
			emit("update:modelValue", arr.map((it, i) => i === idx ? v : it));
		}
		return (_ctx, _cache) => {
			const _component_QuestionFilled = resolveComponent("QuestionFilled");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_tooltip = resolveComponent("el-tooltip");
			const _component_el_switch = resolveComponent("el-switch");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_color_picker = resolveComponent("el-color-picker");
			const _component_el_date_picker = resolveComponent("el-date-picker");
			const _component_PropField = resolveComponent("PropField", true);
			const _component_el_collapse_item = resolveComponent("el-collapse-item");
			const _component_el_collapse = resolveComponent("el-collapse");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			return openBlock(), createBlock(_component_el_form_item, { class: "prop-field" }, {
				label: withCtx(() => [createElementVNode("span", _hoisted_1$3, [
					__props.propDef.required ? (openBlock(), createElementBlock("span", _hoisted_2$3, "*")) : createCommentVNode("", true),
					createTextVNode(" " + toDisplayString(label()) + " ", 1),
					__props.propDef.description ? (openBlock(), createBlock(_component_el_tooltip, {
						key: 1,
						content: __props.propDef.description,
						placement: "top"
					}, {
						default: withCtx(() => [createVNode(_component_el_icon, { class: "prop-tip-icon" }, {
							default: withCtx(() => [createVNode(_component_QuestionFilled)]),
							_: 1
						})]),
						_: 1
					}, 8, ["content"])) : createCommentVNode("", true)
				])]),
				default: withCtx(() => {
					var _props$propDef$step;
					return [__props.propDef.type === "boolean" ? (openBlock(), createBlock(_component_el_switch, {
						key: 0,
						"model-value": !!__props.modelValue,
						"onUpdate:modelValue": _cache[0] || (_cache[0] = (v) => emit("update:modelValue", v))
					}, null, 8, ["model-value"])) : __props.propDef.type === "number" ? (openBlock(), createBlock(_component_el_input_number, {
						key: 1,
						"model-value": __props.modelValue,
						min: __props.propDef.min,
						max: __props.propDef.max,
						step: (_props$propDef$step = __props.propDef.step) !== null && _props$propDef$step !== void 0 ? _props$propDef$step : 1,
						"controls-position": "right",
						style: { "width": "100%" },
						"onUpdate:modelValue": _cache[1] || (_cache[1] = (v) => emit("update:modelValue", v))
					}, null, 8, [
						"model-value",
						"min",
						"max",
						"step"
					])) : __props.propDef.type === "select" ? (openBlock(), createBlock(_component_el_select, {
						key: 2,
						"model-value": __props.modelValue,
						placeholder: __props.propDef.placeholder,
						style: { "width": "100%" },
						"onUpdate:modelValue": _cache[2] || (_cache[2] = (v) => emit("update:modelValue", v))
					}, {
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(__props.propDef.options || [], (opt) => {
							return openBlock(), createBlock(_component_el_option, {
								key: String(opt.value),
								label: opt.label,
								value: opt.value
							}, null, 8, ["label", "value"]);
						}), 128))]),
						_: 1
					}, 8, ["model-value", "placeholder"])) : __props.propDef.type === "color" ? (openBlock(), createBlock(_component_el_color_picker, {
						key: 3,
						"model-value": __props.modelValue,
						"onUpdate:modelValue": _cache[3] || (_cache[3] = (v) => emit("update:modelValue", v))
					}, null, 8, ["model-value"])) : __props.propDef.type === "date" ? (openBlock(), createBlock(_component_el_date_picker, {
						key: 4,
						"model-value": __props.modelValue,
						type: __props.propDef.dateType || "date",
						placeholder: __props.propDef.placeholder,
						style: { "width": "100%" },
						"onUpdate:modelValue": _cache[4] || (_cache[4] = (v) => emit("update:modelValue", v))
					}, null, 8, [
						"model-value",
						"type",
						"placeholder"
					])) : __props.propDef.type === "code" ? (openBlock(), createBlock(ExpressionEditor_default, {
						key: 5,
						"model-value": __props.modelValue || "",
						language: __props.propDef.language || "aviator",
						height: __props.propDef.rows ? __props.propDef.rows * 22 : 160,
						"onUpdate:modelValue": _cache[5] || (_cache[5] = (v) => emit("update:modelValue", v))
					}, null, 8, [
						"model-value",
						"language",
						"height"
					])) : __props.propDef.type === "expression" ? (openBlock(), createElementBlock("div", _hoisted_3$2, [createVNode(_component_el_select, {
						modelValue: expressionLang.value,
						"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => expressionLang.value = $event),
						size: "small",
						class: "prop-expression-lang"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_option, {
								label: "Aviator",
								value: "aviator"
							}),
							createVNode(_component_el_option, {
								label: "Groovy",
								value: "groovy"
							}),
							createVNode(_component_el_option, {
								label: "JavaScript",
								value: "javascript"
							})
						]),
						_: 1
					}, 8, ["modelValue"]), createVNode(ExpressionEditor_default, {
						"model-value": __props.modelValue || "",
						language: expressionLang.value,
						height: __props.propDef.rows ? __props.propDef.rows * 22 : 140,
						"onUpdate:modelValue": _cache[7] || (_cache[7] = (v) => emit("update:modelValue", v))
					}, null, 8, [
						"model-value",
						"language",
						"height"
					])])) : __props.propDef.type === "object" ? (openBlock(), createElementBlock("div", _hoisted_4$1, [createVNode(_component_el_collapse, null, {
						default: withCtx(() => [createVNode(_component_el_collapse_item, {
							title: "展开配置",
							name: "props"
						}, {
							default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(__props.propDef.properties || [], (sub) => {
								return openBlock(), createBlock(_component_PropField, {
									key: sub.key,
									"prop-def": sub,
									"model-value": containerValue()[sub.key],
									"onUpdate:modelValue": (v) => updateObjectField(sub.key, v)
								}, null, 8, [
									"prop-def",
									"model-value",
									"onUpdate:modelValue"
								]);
							}), 128))]),
							_: 1
						})]),
						_: 1
					})])) : __props.propDef.type === "array" ? (openBlock(), createElementBlock("div", _hoisted_5$1, [(openBlock(true), createElementBlock(Fragment, null, renderList(containerValue(), (item, idx) => {
						return openBlock(), createElementBlock("div", {
							key: idx,
							class: "prop-array-item"
						}, [createVNode(_component_PropField, {
							"prop-def": itemSchema(),
							"model-value": item,
							"onUpdate:modelValue": (v) => updateArrayItem(idx, v)
						}, null, 8, [
							"prop-def",
							"model-value",
							"onUpdate:modelValue"
						]), createVNode(_component_el_button, {
							icon: "Delete",
							type: "danger",
							size: "small",
							class: "prop-array-remove",
							onClick: ($event) => removeArrayItem(idx)
						}, null, 8, ["onClick"])]);
					}), 128)), createVNode(_component_el_button, {
						icon: "Plus",
						size: "small",
						onClick: addArrayItem
					}, {
						default: withCtx(() => [..._cache[9] || (_cache[9] = [createTextVNode("新增一项", -1)])]),
						_: 1
					})])) : (openBlock(), createBlock(_component_el_input, {
						key: 9,
						"model-value": __props.modelValue,
						placeholder: __props.propDef.placeholder || (__props.propDef.default !== void 0 ? `默认: ${__props.propDef.default}` : ""),
						"onUpdate:modelValue": _cache[8] || (_cache[8] = (v) => emit("update:modelValue", v))
					}, null, 8, ["model-value", "placeholder"]))];
				}),
				_: 1
			});
		};
	}
}), [["__scopeId", "data-v-735556d1"]]);
//#endregion
//#region src/components/LowCodePropertyPanel/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$2 = { class: "lowcode-property-panel" };
var _hoisted_2$2 = { class: "panel-header" };
//#endregion
//#region src/components/LowCodePropertyPanel/index.vue
var LowCodePropertyPanel_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	props: {
		meta: {},
		modelValue: {}
	},
	emits: ["update:modelValue"],
	setup(__props, { emit: __emit }) {
		/**
		* 低代码属性面板（schema 驱动，借鉴 NocoBase JSONSchema）。
		*
		* <p>根据传入的 {@link ComponentMeta.propsSchema} 动态渲染任意属性表单，
		* 每个属性委托给递归组件 {@link PropField} 渲染对应控件。
		* 支持 boolean/number/string/select/color/date/array/object/code/expression 类型，
		* 是后续 4 个设计器打通组件注册中心的基础。</p>
		*
		* <p>通过 v-model 双向绑定到 props.modelValue；内部维护 local 响应式副本，
		* 深度监听后回传父组件。挂载时按 propDef.default 初始化缺失字段。</p>
		*/
		const props = __props;
		const emit = __emit;
		const local = reactive({ ...props.modelValue });
		function initDefaults() {
			var _props$meta;
			const schema = ((_props$meta = props.meta) === null || _props$meta === void 0 ? void 0 : _props$meta.propsSchema) || [];
			for (const def of schema) if (local[def.key] === void 0 && def.default !== void 0) local[def.key] = def.default;
		}
		initDefaults();
		watch(local, () => emit("update:modelValue", { ...local }), { deep: true });
		watch(() => props.modelValue, (val) => {
			if (!val) return;
			for (const key of Object.keys(val)) if (local[key] !== val[key]) local[key] = val[key];
		}, { deep: true });
		return (_ctx, _cache) => {
			var _props$meta2;
			const _component_el_form = resolveComponent("el-form");
			return openBlock(), createElementBlock("div", _hoisted_1$2, [createElementVNode("div", _hoisted_2$2, toDisplayString(((_props$meta2 = __props.meta) === null || _props$meta2 === void 0 ? void 0 : _props$meta2.displayName) || "属性配置"), 1), createVNode(_component_el_form, {
				"label-width": "110px",
				size: "small"
			}, {
				default: withCtx(() => {
					var _props$meta3;
					return [(openBlock(true), createElementBlock(Fragment, null, renderList(((_props$meta3 = __props.meta) === null || _props$meta3 === void 0 ? void 0 : _props$meta3.propsSchema) || [], (prop) => {
						return openBlock(), createBlock(PropField_default, {
							key: prop.key,
							"prop-def": prop,
							"model-value": local[prop.key],
							"onUpdate:modelValue": (v) => local[prop.key] = v
						}, null, 8, [
							"prop-def",
							"model-value",
							"onUpdate:modelValue"
						]);
					}), 128))];
				}),
				_: 1
			})]);
		};
	}
}), [["__scopeId", "data-v-ad2e4985"]]);
//#endregion
//#region src/api/lowcode-collaboration.ts
var SILENT = { silent: true };
function joinCollaboration(configType, configId, user) {
	return post("/api/lowcode/collaboration/join", {
		configType,
		configId,
		user
	}, SILENT);
}
function leaveCollaboration(configType, configId, userId) {
	return post("/api/lowcode/collaboration/leave", {
		configType,
		configId,
		userId
	}, SILENT);
}
function heartbeatCollaboration(configType, configId, userId) {
	return post("/api/lowcode/collaboration/heartbeat", {
		configType,
		configId,
		userId
	}, SILENT);
}
function getOnlineUsers(configType, configId) {
	return get("/api/lowcode/collaboration/online", {
		configType,
		configId
	}, SILENT);
}
function broadcastChange(configType, configId, change) {
	return post("/api/lowcode/collaboration/change", {
		configType,
		configId,
		change
	}, SILENT);
}
function getChanges(configType, configId, sinceSeq = 0) {
	return get("/api/lowcode/collaboration/changes", {
		configType,
		configId,
		sinceSeq
	}, SILENT);
}
//#endregion
//#region src/composables/useCollaboration.ts
/**
* 协同编辑 composable（批次5-T6，借鉴 Mendix 协同编辑）。
*
* <p>基于 HTTP 轮询的简化协同方案：
* <ul>
*   <li>join: 进入页面时调用，注册在线状态</li>
*   <li>heartbeat: 每 10s 心跳保活</li>
*   <li>getOnlineUsers: 每 5s 轮询在线用户列表</li>
*   <li>getChanges: 每 3s 轮询增量变更</li>
*   <li>broadcastChange: 用户编辑时主动上报变更</li>
*   <li>leave: 离开页面时调用（onUnmounted 自动触发）</li>
* </ul></p>
*
* <p>升级路径：将 HTTP 轮询替换为 WebSocket（y-websocket），接口语义保持不变。</p>
*/
function useCollaboration(opts) {
	const onlineUsers = ref([]);
	const recentChanges = ref([]);
	const lastSeq = ref(0);
	const joined = ref(false);
	let cancelled = false;
	let heartbeatTimer = null;
	let onlineTimer = null;
	let changesTimer = null;
	async function join() {
		try {
			await joinCollaboration(opts.configType, opts.configId, {
				userId: opts.userId,
				userName: opts.userName,
				avatar: opts.avatar
			});
			if (cancelled) {
				try {
					await leaveCollaboration(opts.configType, opts.configId, opts.userId);
				} catch (_unused) {}
				return;
			}
			joined.value = true;
			await refreshOnline();
			if (!cancelled) startTimers();
		} catch (e) {
			console.warn("[useCollaboration] join failed", e);
		}
	}
	async function leave() {
		stopTimers();
		if (!joined.value) return;
		try {
			await leaveCollaboration(opts.configType, opts.configId, opts.userId);
			joined.value = false;
		} catch (e) {
			console.warn("[useCollaboration] leave failed", e);
		}
	}
	async function heartbeat() {
		if (!joined.value) return;
		try {
			await heartbeatCollaboration(opts.configType, opts.configId, opts.userId);
		} catch (e) {
			console.warn("[useCollaboration] heartbeat failed", e);
		}
	}
	async function refreshOnline() {
		try {
			onlineUsers.value = await getOnlineUsers(opts.configType, opts.configId);
		} catch (e) {
			console.warn("[useCollaboration] refresh online failed", e);
		}
	}
	async function refreshChanges() {
		if (!joined.value) return;
		try {
			const changes = await getChanges(opts.configType, opts.configId, lastSeq.value);
			if (changes.length > 0) {
				recentChanges.value = changes;
				const maxSeq = changes.reduce((max, c) => Math.max(max, c.seq || 0), lastSeq.value);
				lastSeq.value = maxSeq;
			}
		} catch (e) {
			console.warn("[useCollaboration] refresh changes failed", e);
		}
	}
	async function emitChange(change) {
		if (!joined.value) return;
		try {
			await broadcastChange(opts.configType, opts.configId, {
				...change,
				userId: opts.userId,
				userName: opts.userName
			});
		} catch (e) {
			console.warn("[useCollaboration] emit change failed", e);
		}
	}
	function startTimers() {
		var _opts$heartbeatInterv, _opts$onlinePollInter, _opts$changesPollInte;
		stopTimers();
		const hbInterval = (_opts$heartbeatInterv = opts.heartbeatInterval) !== null && _opts$heartbeatInterv !== void 0 ? _opts$heartbeatInterv : 1e4;
		const onlineInterval = (_opts$onlinePollInter = opts.onlinePollInterval) !== null && _opts$onlinePollInter !== void 0 ? _opts$onlinePollInter : 5e3;
		const changesInterval = (_opts$changesPollInte = opts.changesPollInterval) !== null && _opts$changesPollInte !== void 0 ? _opts$changesPollInte : 3e3;
		heartbeatTimer = setInterval(heartbeat, hbInterval);
		onlineTimer = setInterval(refreshOnline, onlineInterval);
		changesTimer = setInterval(refreshChanges, changesInterval);
	}
	function stopTimers() {
		if (heartbeatTimer) {
			clearInterval(heartbeatTimer);
			heartbeatTimer = null;
		}
		if (onlineTimer) {
			clearInterval(onlineTimer);
			onlineTimer = null;
		}
		if (changesTimer) {
			clearInterval(changesTimer);
			changesTimer = null;
		}
	}
	onUnmounted(() => {
		cancelled = true;
		leave();
	});
	return {
		onlineUsers,
		recentChanges,
		joined,
		join,
		leave,
		emitChange,
		refreshOnline,
		refreshChanges
	};
}
//#endregion
//#region src/components/OnlineUsersIndicator/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$1 = { class: "online-users-indicator" };
var _hoisted_2$1 = { class: "user-avatar extra" };
var _hoisted_3$1 = {
	key: 1,
	class: "no-users"
};
//#endregion
//#region src/components/OnlineUsersIndicator/index.vue
var OnlineUsersIndicator_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	props: {
		users: {},
		currentUserId: { default: 0 }
	},
	setup(__props) {
		/**
		* 在线用户指示器（批次5-T6）。
		*
		* <p>显示当前配置的在线协同用户头像列表，鼠标悬停显示用户名。
		* 当前用户高亮显示。</p>
		*/
		const props = __props;
		const displayUsers = computed(() => props.users.slice(0, 8));
		const extraCount = computed(() => Math.max(0, props.users.length - 8));
		function initials(name) {
			if (!name) return "?";
			return name.slice(0, 2).toUpperCase();
		}
		function avatarColor(userId) {
			if (!userId) return "#909399";
			const colors = [
				"#409eff",
				"#67c23a",
				"#e6a23c",
				"#f56c6c",
				"#909399",
				"#9c27b0",
				"#00bcd4",
				"#ff9800"
			];
			return colors[userId % colors.length];
		}
		return (_ctx, _cache) => {
			const _component_el_tooltip = resolveComponent("el-tooltip");
			return openBlock(), createElementBlock("div", _hoisted_1$1, [
				(openBlock(true), createElementBlock(Fragment, null, renderList(displayUsers.value, (u) => {
					return openBlock(), createBlock(_component_el_tooltip, {
						key: u.userId,
						content: `${u.userName}${u.userId === __props.currentUserId ? " (我)" : ""}`,
						placement: "bottom"
					}, {
						default: withCtx(() => [createElementVNode("div", {
							class: normalizeClass(["user-avatar", { "is-current": u.userId === __props.currentUserId }]),
							style: normalizeStyle({ backgroundColor: avatarColor(u.userId) })
						}, toDisplayString(initials(u.userName)), 7)]),
						_: 2
					}, 1032, ["content"]);
				}), 128)),
				extraCount.value > 0 ? (openBlock(), createBlock(_component_el_tooltip, {
					key: 0,
					content: `还有 ${extraCount.value} 位用户`,
					placement: "bottom"
				}, {
					default: withCtx(() => [createElementVNode("div", _hoisted_2$1, "+" + toDisplayString(extraCount.value), 1)]),
					_: 1
				}, 8, ["content"])) : createCommentVNode("", true),
				__props.users.length === 0 ? (openBlock(), createElementBlock("span", _hoisted_3$1, "无人在线")) : createCommentVNode("", true)
			]);
		};
	}
}), [["__scopeId", "data-v-0cd1a5f2"]]);
//#endregion
//#region src/views/lowcode/form-designer/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "form-designer" };
var _hoisted_2 = { class: "toolbar" };
var _hoisted_3 = { class: "toolbar-left" };
var _hoisted_4 = { class: "toolbar-right" };
var _hoisted_5 = {
	key: 0,
	class: "designer-body"
};
var _hoisted_6 = { class: "comp-group-title" };
var _hoisted_7 = { class: "comp-items" };
var _hoisted_8 = ["onDragstart", "onClick"];
var _hoisted_9 = { class: "canvas-header" };
var _hoisted_10 = { class: "panel-title" };
var _hoisted_11 = { class: "resp-preview-bar" };
var _hoisted_12 = {
	key: 0,
	class: "resp-preview-bar__tip"
};
var _hoisted_13 = ["onClick"];
var _hoisted_14 = { class: "resp-preview-cell__label" };
var _hoisted_15 = { class: "resp-preview-cell__span" };
var _hoisted_16 = {
	key: 0,
	class: "empty-tip"
};
var _hoisted_17 = {
	key: 1,
	class: "field-list"
};
var _hoisted_18 = ["onClick"];
var _hoisted_19 = { class: "field-card-header" };
var _hoisted_20 = { class: "field-label" };
var _hoisted_21 = { class: "field-prop" };
var _hoisted_22 = {
	key: 0,
	class: "field-required"
};
var _hoisted_23 = { class: "field-actions" };
var _hoisted_24 = { class: "field-preview" };
var _hoisted_25 = { key: 9 };
var _hoisted_26 = { key: 10 };
var _hoisted_27 = {
	key: 0,
	class: "empty-prop"
};
var _hoisted_28 = { class: "bp-row" };
var _hoisted_29 = { class: "preview-header" };
var _hoisted_30 = { class: "panel-title" };
/** 断点枚举数组（自小到大，用于遍历与继承查找）— 引用统一常量 */
var HISTORY_DEBOUNCE_MS = 400;
//#endregion
//#region src/views/lowcode/form-designer/index.vue
var form_designer_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		var _userStore$userInfo, _userStore$userInfo2, _userStore$userInfo3;
		/**
		* 低代码表单设计器。
		*
		* <p>三栏布局：</p>
		* <ul>
		*   <li>左侧：组件库面板（按分类列出可拖拽组件）</li>
		*   <li>中间：画布（拖拽放置区 + 字段列表 + 实时预览）</li>
		*   <li>右侧：属性面板（选中字段的属性配置）</li>
		* </ul>
		*
		* <p>顶部操作栏：保存草稿 / 发布 / 导入 / 导出 / 预览 / 重置，以及表单元信息编辑。</p>
		*
		* <p>使用原生 HTML5 拖拽 API（draggable + dragstart/dragover/drop）实现，
		* 避免引入额外依赖；字段排序通过上移/下移按钮 + 拖拽两种方式。</p>
		*/
		const route = useRoute();
		const router = useRouter();
		const userStore = useUserStore();
		const baseComponentGroups = [
			{
				title: "基础组件",
				items: [
					{
						type: FieldType.INPUT,
						label: "单行文本",
						icon: "EditPen",
						defaultProps: { maxlength: 100 }
					},
					{
						type: FieldType.TEXTAREA,
						label: "多行文本",
						icon: "Document",
						defaultProps: {
							rows: 3,
							maxlength: 500
						}
					},
					{
						type: FieldType.NUMBER,
						label: "数字",
						icon: "Histogram",
						defaultProps: {
							min: 0,
							step: 1
						}
					},
					{
						type: FieldType.PASSWORD,
						label: "密码",
						icon: "Lock",
						defaultProps: { showPassword: true }
					}
				]
			},
			{
				title: "选择组件",
				items: [
					{
						type: FieldType.SELECT,
						label: "下拉选择",
						icon: "ArrowDown",
						defaultProps: {
							options: [],
							multiple: false
						}
					},
					{
						type: FieldType.RADIO,
						label: "单选",
						icon: "CircleCheck",
						defaultProps: { options: [] }
					},
					{
						type: FieldType.CHECKBOX,
						label: "多选",
						icon: "Select",
						defaultProps: { options: [] }
					},
					{
						type: FieldType.SWITCH,
						label: "开关",
						icon: "Open",
						defaultProps: {
							activeText: "",
							inactiveText: ""
						}
					},
					{
						type: FieldType.SLIDER,
						label: "滑块",
						icon: "Minus",
						defaultProps: {
							min: 0,
							max: 100
						}
					},
					{
						type: FieldType.RATE,
						label: "评分",
						icon: "Star",
						defaultProps: {
							max: 5,
							allowHalf: false
						}
					},
					{
						type: FieldType.CASCADER,
						label: "级联选择",
						icon: "Share",
						defaultProps: { options: [] }
					}
				]
			},
			{
				title: "日期组件",
				items: [
					{
						type: FieldType.DATE,
						label: "日期",
						icon: "Calendar",
						defaultProps: {
							format: "YYYY-MM-DD",
							valueFormat: "YYYY-MM-DD"
						}
					},
					{
						type: FieldType.DATETIME,
						label: "日期时间",
						icon: "Clock",
						defaultProps: {
							format: "YYYY-MM-DD HH:mm:ss",
							valueFormat: "YYYY-MM-DD HH:mm:ss"
						}
					},
					{
						type: FieldType.DATERANGE,
						label: "日期范围",
						icon: "Calendar",
						defaultProps: {
							format: "YYYY-MM-DD",
							valueFormat: "YYYY-MM-DD"
						}
					}
				]
			},
			{
				title: "上传组件",
				items: [{
					type: FieldType.UPLOAD,
					label: "文件上传",
					icon: "Upload",
					defaultProps: {
						action: "/api/file/upload",
						limit: 5,
						accept: "",
						multiple: true
					}
				}]
			},
			{
				title: "布局组件",
				items: [{
					type: FieldType.DIVIDER,
					label: "分隔线",
					icon: "Minus",
					defaultProps: { contentPosition: "center" }
				}, {
					type: FieldType.TITLE,
					label: "标题",
					icon: "Document",
					defaultProps: {}
				}]
			}
		];
		/** 注册中心加载的业务组件 meta 列表（按 category 分组后合并到组件库） */
		const registryComponents = ref([]);
		/**
		* 组件库分组（computed）：基础组件 + 注册中心业务组件。
		*
		* <p>基础 5 组保留不动；registry 组件按 category 分组，在基础组之后追加。
		* registry 组件统一标记 isRegistry=true、type='custom'，拖入画布时写入 componentName。</p>
		*/
		const componentGroups = computed(() => {
			const groups = baseComponentGroups.map((g) => ({
				...g,
				items: [...g.items]
			}));
			if (registryComponents.value.length === 0) return groups;
			const byCategory = /* @__PURE__ */ new Map();
			for (const meta of registryComponents.value) {
				const cat = meta.category || "其他";
				if (!byCategory.has(cat)) byCategory.set(cat, []);
				byCategory.get(cat).push(meta);
			}
			for (const [cat, metas] of byCategory) groups.push({
				title: `业务组件·${cat}`,
				items: metas.map((meta) => ({
					type: FieldType.CUSTOM,
					label: meta.displayName,
					icon: "Box",
					isRegistry: true,
					componentName: meta.name
				}))
			});
			return groups;
		});
		/** 表单元信息（对应 LowCodeFormConfig 的非 formConfig 字段） */
		const metaForm = reactive({
			code: "",
			name: "",
			description: "",
			formConfig: "",
			status: "DRAFT",
			bizType: "",
			version: 1
		});
		/** 设计器内部维护的 FormConfig 对象（解析后） */
		const formConfig = reactive({
			title: "",
			description: "",
			labelWidth: 100,
			labelPosition: "right",
			size: "default",
			fields: [],
			layout: {
				type: LayoutType.GRID,
				gutter: 16
			}
		});
		/** 当前选中的字段 id */
		const selectedFieldId = ref("");
		/** 字段计数器，用于生成 field_N */
		let fieldSeq = 0;
		/** 元信息表单 ref */
		const metaFormRef = ref();
		/** 元信息校验规则 */
		const metaRules = {
			code: [{
				required: true,
				message: "请输入表单编码",
				trigger: "blur"
			}],
			name: [{
				required: true,
				message: "请输入表单名称",
				trigger: "blur"
			}]
		};
		/** 加载状态 */
		const loading = ref(false);
		/** 预览模式 */
		const previewMode = ref(false);
		/** 预览表单数据 */
		let previewData = reactive({});
		/** 选中字段对象 */
		const selectedField = computed(() => formConfig.fields.find((f) => f.id === selectedFieldId.value) || null);
		/** 选中字段是否为 registry 业务组件（type=custom 且携带 componentName） */
		const isSelectedRegistry = computed(() => !!selectedField.value && selectedField.value.type === FieldType.CUSTOM && !!selectedField.value.componentName);
		/** 选中 registry 组件的 meta（含 propsSchema，供 LowCodePropertyPanel 渲染） */
		const selectedComponentMeta = computed(() => {
			var _LowCodeComponentRegi;
			const field = selectedField.value;
			if (!field || !field.componentName) return null;
			return ((_LowCodeComponentRegi = LowCodeComponentRegistry_default.get(field.componentName)) === null || _LowCodeComponentRegi === void 0 ? void 0 : _LowCodeComponentRegi.meta) || null;
		});
		/** 响应式断点折叠面板激活项（默认展开） */
		const responsiveCollapse = ref(["resp"]);
		const breakpointOrder = BREAKPOINT_ORDER;
		/** 各断点对应的最小屏幕宽度（px），用于画布响应式预览模拟 — 引用统一常量 */
		const breakpointWidth = BREAKPOINT_PREVIEW_WIDTH;
		/** 断点显示文案（含屏幕宽度范围）— 引用统一常量 */
		const breakpointLabel = BREAKPOINT_LABEL;
		/**
		* 当前选中字段是否启用响应式断点（span 为对象）。
		*
		* <p>开启时仅初始化 xs 与 md 两个断点为当前 span，sm/lg/xl 留空，
		* 由 el-col 按断点继承规则回退到更小断点，体现“留空即继承”。</p>
		*/
		const isResponsive = computed({
			get: () => !!selectedField.value && typeof selectedField.value.span === "object",
			set: (val) => {
				const field = selectedField.value;
				if (!field) return;
				if (val) {
					const cur = typeof field.span === "number" ? field.span : 24;
					field.span = {
						xs: cur,
						md: cur
					};
				} else {
					var _ref, _ref2, _obj$md;
					const obj = field.span;
					field.span = typeof obj === "object" && obj ? (_ref = (_ref2 = (_obj$md = obj.md) !== null && _obj$md !== void 0 ? _obj$md : obj.sm) !== null && _ref2 !== void 0 ? _ref2 : obj.xs) !== null && _ref !== void 0 ? _ref : 24 : 24;
				}
			}
		});
		/** 非响应式模式下的栅格宽度（数字） */
		const fieldSpan = computed({
			get: () => {
				var _selectedField$value;
				return typeof ((_selectedField$value = selectedField.value) === null || _selectedField$value === void 0 ? void 0 : _selectedField$value.span) === "number" ? selectedField.value.span : 24;
			},
			set: (v) => {
				if (selectedField.value) selectedField.value.span = v;
			}
		});
		/**
		* 读取指定断点值。
		*
		* <p>未配置时返回 undefined，表示该断点留空、由 el-col 继承更小断点
		* （对应属性面板 el-input-number 显示占位“留空”）。</p>
		*/
		function getBreakpoint(k) {
			var _selectedField$value2;
			const s = (_selectedField$value2 = selectedField.value) === null || _selectedField$value2 === void 0 ? void 0 : _selectedField$value2.span;
			return typeof s === "object" && s ? s[k] : void 0;
		}
		/**
		* 设置指定断点值。
		*
		* <p>传入 undefined/null/NaN 视为“留空”——从对象中删除该断点 key，
		* el-col 渲染时即不输出对应 prop，自动继承更小断点。</p>
		*/
		function setBreakpoint(k, v) {
			const field = selectedField.value;
			if (!field) return;
			const s = field.span;
			const obj = typeof s === "object" && s ? { ...s } : {};
			if (v === void 0 || v === null || Number.isNaN(v)) delete obj[k];
			else obj[k] = v;
			field.span = obj;
		}
		/**
		* 计算字段在指定断点下的有效 span（模拟 el-col 断点继承）。
		*
		* <p>从当前断点向更小断点逐级查找，取第一个已配置的值；均未配置回退 24。
		* 用于画布响应式预览——因 el-col 的 :xs/:sm 等基于视口媒体查询，
		* 无法在受限容器内触发，故手动计算有效 span 以 :span= 形式渲染。</p>
		*/
		function effectiveSpan(field, bp) {
			const s = field.span;
			if (s === void 0 || typeof s === "number") return s !== null && s !== void 0 ? s : 24;
			const idx = breakpointOrder.indexOf(bp);
			for (let i = idx; i >= 0; i--) {
				const v = s[breakpointOrder[i]];
				if (v !== void 0) return v;
			}
			return 24;
		}
		/** 预览宽度档位：auto=跟随画布宽度，其余模拟对应断点 */
		const previewWidth = ref("auto");
		/** 当前预览选中的断点（auto 时为 null） */
		const activePreviewBp = computed(() => previewWidth.value === "auto" ? null : previewWidth.value);
		/** 预览模拟的屏幕宽度（px） */
		const previewWidthPx = computed(() => activePreviewBp.value ? breakpointWidth[activePreviewBp.value] : 0);
		/** 预览断点显示文案 */
		const previewLabel = computed(() => activePreviewBp.value ? breakpointLabel[activePreviewBp.value] : "");
		/**
		* 响应式预览行：每个字段在当前模拟断点下的有效 span。
		*
		* <p>实时反映 formConfig.fields 与各字段 span 配置，切换断点即时重算。</p>
		*/
		const previewRows = computed(() => {
			const bp = activePreviewBp.value;
			if (!bp) return [];
			return formConfig.fields.map((f) => ({
				field: f,
				span: effectiveSpan(f, bp)
			}));
		});
		/**
		* 创建一个新字段对象。
		*/
		function createField(type, label, extraProps = {}) {
			fieldSeq++;
			return {
				id: `field_${fieldSeq}`,
				type,
				label,
				prop: `field${fieldSeq}`,
				placeholder: `请输入${label}`,
				required: false,
				disabled: false,
				readonly: false,
				hidden: false,
				clearable: true,
				span: 24,
				props: { ...extraProps },
				events: {}
			};
		}
		/** 添加字段到画布（点击组件库或拖拽放置） */
		function addField(comp) {
			const field = createField(comp.type, comp.label, comp.defaultProps || {});
			if (comp.isRegistry && comp.componentName) field.componentName = comp.componentName;
			formConfig.fields.push(field);
			selectedFieldId.value = field.id;
		}
		/** 删除字段 */
		function removeField(id) {
			const idx = formConfig.fields.findIndex((f) => f.id === id);
			if (idx >= 0) {
				formConfig.fields.splice(idx, 1);
				if (selectedFieldId.value === id) selectedFieldId.value = "";
			}
		}
		/** 复制字段 */
		function duplicateField(id) {
			const src = formConfig.fields.find((f) => f.id === id);
			if (!src) return;
			fieldSeq++;
			const copy = JSON.parse(JSON.stringify(src));
			copy.id = `field_${fieldSeq}`;
			copy.prop = `${src.prop}_copy`;
			copy.label = `${src.label}_副本`;
			const idx = formConfig.fields.findIndex((f) => f.id === id);
			formConfig.fields.splice(idx + 1, 0, copy);
			selectedFieldId.value = copy.id;
		}
		/** 上移字段 */
		function moveUp(id) {
			const idx = formConfig.fields.findIndex((f) => f.id === id);
			if (idx > 0) {
				const tmp = formConfig.fields[idx];
				formConfig.fields[idx] = formConfig.fields[idx - 1];
				formConfig.fields[idx - 1] = tmp;
			}
		}
		/** 下移字段 */
		function moveDown(id) {
			const idx = formConfig.fields.findIndex((f) => f.id === id);
			if (idx >= 0 && idx < formConfig.fields.length - 1) {
				const tmp = formConfig.fields[idx];
				formConfig.fields[idx] = formConfig.fields[idx + 1];
				formConfig.fields[idx + 1] = tmp;
			}
		}
		/** 选中字段 */
		function selectField(id) {
			selectedFieldId.value = id;
		}
		/** 当前拖拽的组件类型（来自组件库） */
		let dragType = "";
		/** 当前拖拽的组件定义（registry 组件 type 同为 'custom'，需用完整对象区分） */
		let dragComp = null;
		function onDragStart(event, comp) {
			dragType = comp.type;
			dragComp = comp;
			if (event.dataTransfer) {
				event.dataTransfer.effectAllowed = "copy";
				event.dataTransfer.setData("text/plain", comp.isRegistry && comp.componentName ? `custom::${comp.componentName}` : comp.type);
			}
		}
		function onCanvasDragOver(event) {
			if (event.dataTransfer) event.dataTransfer.dropEffect = "copy";
			event.preventDefault();
		}
		function onCanvasDrop(event) {
			var _event$dataTransfer;
			event.preventDefault();
			const raw = ((_event$dataTransfer = event.dataTransfer) === null || _event$dataTransfer === void 0 ? void 0 : _event$dataTransfer.getData("text/plain")) || dragType;
			if (!raw) return;
			if (dragComp) {
				addField(dragComp);
				dragComp = null;
				dragType = "";
				return;
			}
			if (raw.startsWith("custom::")) {
				const cn = raw.slice(8);
				for (const group of componentGroups.value) {
					const comp = group.items.find((c) => c.isRegistry && c.componentName === cn);
					if (comp) {
						addField(comp);
						return;
					}
				}
				return;
			}
			for (const group of componentGroups.value) {
				const comp = group.items.find((c) => c.type === raw && !c.isRegistry);
				if (comp) {
					addField(comp);
					return;
				}
			}
		}
		/** 类型选项 */
		const typeOptions = computed(() => {
			const list = [];
			for (const g of componentGroups.value) for (const c of g.items) list.push({
				value: c.type,
				label: c.label,
				group: g.title
			});
			return list;
		});
		/** 切换字段类型：保留公共属性，重置类型特定属性 */
		function changeFieldType(field, newType) {
			if (!typeOptions.value.find((t) => t.value === newType)) return;
			let defaultProps = {};
			for (const g of componentGroups.value) {
				const c = g.items.find((c) => c.type === newType);
				if (c) {
					defaultProps = c.defaultProps || {};
					break;
				}
			}
			field.type = newType;
			field.props = { ...defaultProps };
		}
		/** 添加选项 */
		function addOption(field) {
			if (!field.props) field.props = {};
			if (!Array.isArray(field.props.options)) field.props.options = [];
			field.props.options.push({
				label: "新选项",
				value: `option_${Date.now()}`
			});
		}
		/** 删除选项 */
		function removeOption(field, idx) {
			var _field$props;
			if (((_field$props = field.props) === null || _field$props === void 0 ? void 0 : _field$props.options) && Array.isArray(field.props.options)) field.props.options.splice(idx, 1);
		}
		/** 将 formConfig 对象序列化为 JSON 字符串（赋值到 metaForm.formConfig） */
		function syncFormConfigToStr() {
			metaForm.formConfig = JSON.stringify(formConfig, null, 2);
		}
		/** 从 JSON 字符串解析到 formConfig 对象 */
		function parseFormConfigFromStr() {
			try {
				var _parsed$title, _parsed$description, _parsed$labelWidth, _parsed$labelPosition, _parsed$size;
				if (!metaForm.formConfig) {
					formConfig.fields = [];
					formConfig.layout = {
						type: LayoutType.GRID,
						gutter: 16
					};
					return;
				}
				const parsed = JSON.parse(metaForm.formConfig);
				formConfig.title = (_parsed$title = parsed.title) !== null && _parsed$title !== void 0 ? _parsed$title : "";
				formConfig.description = (_parsed$description = parsed.description) !== null && _parsed$description !== void 0 ? _parsed$description : "";
				formConfig.labelWidth = (_parsed$labelWidth = parsed.labelWidth) !== null && _parsed$labelWidth !== void 0 ? _parsed$labelWidth : 100;
				formConfig.labelPosition = (_parsed$labelPosition = parsed.labelPosition) !== null && _parsed$labelPosition !== void 0 ? _parsed$labelPosition : "right";
				formConfig.size = (_parsed$size = parsed.size) !== null && _parsed$size !== void 0 ? _parsed$size : "default";
				formConfig.fields = parsed.fields || [];
				formConfig.layout = parsed.layout || {
					type: LayoutType.GRID,
					gutter: 16
				};
				fieldSeq = 0;
				for (const f of formConfig.fields) {
					const m = /field_(\d+)/.exec(f.id);
					if (m) {
						const n = parseInt(m[1], 10);
						if (n > fieldSeq) fieldSeq = n;
					}
				}
			} catch (e) {
				ElMessage.error("表单配置 JSON 解析失败：" + e.message);
			}
		}
		/**
		* 撤销/重做历史栈：对整个 formConfig 做 JSON 快照。
		*
		* <p>采用 watch 深度监听 formConfig 自动推历史（400ms 防抖合并连续输入，
		* 避免 50 步栈被逐字符吞掉）；undo/redo 时反向同步快照回 reactive，
		* 保持 formConfig 引用不变以兼容现有 UI 双向绑定。</p>
		*/
		const history = useUndoRedo(JSON.parse(JSON.stringify(formConfig)));
		const { present: historyPresent, canUndo, canRedo } = history;
		/** 抑制标志：undo/redo 同步快照回 formConfig 时关闭 watch 推历史，避免循环 */
		let suppressHistory = false;
		/** 防抖计时器：连续输入合并为一次历史入栈 */
		let historyDebounce = null;
		function commitPendingHistory() {
			if (historyDebounce) {
				clearTimeout(historyDebounce);
				historyDebounce = null;
				history.set(JSON.parse(JSON.stringify(formConfig)));
			}
		}
		watch(formConfig, () => {
			if (suppressHistory) return;
			if (historyDebounce) clearTimeout(historyDebounce);
			historyDebounce = setTimeout(() => {
				historyDebounce = null;
				history.set(JSON.parse(JSON.stringify(formConfig)));
			}, HISTORY_DEBOUNCE_MS);
		}, {
			deep: true,
			flush: "sync"
		});
		/** 将历史当前快照同步回 reactive formConfig（保持引用不变，UI 自动更新） */
		function applyHistoryToFormConfig() {
			const snap = historyPresent.value;
			suppressHistory = true;
			try {
				const target = formConfig;
				const src = snap;
				for (const key of Object.keys(target)) if (!(key in src)) delete target[key];
				for (const key of Object.keys(src)) target[key] = JSON.parse(JSON.stringify(src[key]));
				fieldSeq = 0;
				for (const f of formConfig.fields) {
					const m = /field_(\d+)/.exec(f.id);
					if (m) {
						const n = parseInt(m[1], 10);
						if (n > fieldSeq) fieldSeq = n;
					}
				}
			} finally {
				nextTick(() => {
					suppressHistory = false;
				});
			}
		}
		/** 撤销 */
		function undo() {
			commitPendingHistory();
			if (!canUndo.value) return;
			history.undo();
			applyHistoryToFormConfig();
		}
		/** 重做 */
		function redo() {
			commitPendingHistory();
			if (!canRedo.value) return;
			history.redo();
			applyHistoryToFormConfig();
		}
		/** 键盘快捷键：Ctrl/Cmd+Z 撤销，Ctrl+Y 或 Ctrl/Cmd+Shift+Z 重做 */
		function onUndoRedoKeydown(event) {
			if (!(typeof navigator !== "undefined" && navigator.platform.toUpperCase().indexOf("MAC") >= 0 ? event.metaKey : event.ctrlKey)) return;
			const key = event.key.toLowerCase();
			if (key === "z" && !event.shiftKey) {
				event.preventDefault();
				undo();
			} else if (key === "z" && event.shiftKey || key === "y") {
				event.preventDefault();
				redo();
			}
		}
		async function loadForm(id) {
			loading.value = true;
			try {
				const data = await getForm(id);
				Object.assign(metaForm, data);
				parseFormConfigFromStr();
				if (historyDebounce) {
					clearTimeout(historyDebounce);
					historyDebounce = null;
				}
				history.reset(JSON.parse(JSON.stringify(formConfig)));
			} catch (_unused) {} finally {
				loading.value = false;
			}
		}
		/** 保存草稿（创建或更新） */
		async function handleSave() {
			if (!metaFormRef.value) return;
			await metaFormRef.value.validate(async (valid) => {
				if (!valid) return;
				if (formConfig.fields.length === 0) {
					ElMessage.warning("请至少添加一个字段");
					return;
				}
				syncFormConfigToStr();
				loading.value = true;
				try {
					if (metaForm.id) {
						await updateForm(metaForm.id, metaForm);
						ElMessage.success("保存成功");
					} else {
						const created = await createForm(metaForm);
						metaForm.id = created.id;
						metaForm.status = created.status;
						ElMessage.success("创建成功");
					}
				} catch (_unused2) {} finally {
					loading.value = false;
				}
			});
		}
		/** 发布 */
		async function handlePublish() {
			if (!metaForm.id) {
				ElMessage.warning("请先保存草稿");
				return;
			}
			syncFormConfigToStr();
			loading.value = true;
			try {
				await updateForm(metaForm.id, metaForm);
				await publishForm(metaForm.id);
				metaForm.status = "PUBLISHED";
				ElMessage.success("发布成功");
			} catch (_unused3) {} finally {
				loading.value = false;
			}
		}
		/** 归档 */
		async function handleArchive() {
			if (!metaForm.id) return;
			try {
				await ElMessageBox.confirm("确认归档此表单？归档后不可再使用", "确认", { type: "warning" });
				await archiveForm(metaForm.id);
				metaForm.status = "ARCHIVED";
				ElMessage.success("归档成功");
			} catch (_unused4) {}
		}
		/** 导出当前表单配置为 JSON 文件 */
		async function handleExport() {
			if (!metaForm.code) {
				ElMessage.warning("请先填写表单编码");
				return;
			}
			syncFormConfigToStr();
			if (metaForm.id) try {
				await exportForm(metaForm.code);
				ElMessage.success("导出成功");
			} catch (_unused5) {}
			else {
				const blob = new Blob([JSON.stringify(metaForm, null, 2)], { type: "application/json" });
				const url = URL.createObjectURL(blob);
				const link = document.createElement("a");
				link.href = url;
				link.download = `form-${metaForm.code}.json`;
				document.body.appendChild(link);
				link.click();
				document.body.removeChild(link);
				setTimeout(() => URL.revokeObjectURL(url), 0);
				ElMessage.success("本地导出成功");
			}
		}
		/** 导入 JSON 文件 */
		async function handleImport() {
			const input = document.createElement("input");
			input.type = "file";
			input.accept = ".json,application/json";
			input.onchange = async () => {
				var _input$files;
				const file = (_input$files = input.files) === null || _input$files === void 0 ? void 0 : _input$files[0];
				if (!file) return;
				const text = await file.text();
				try {
					const imported = await importForm(text);
					ElMessage.success(`导入成功，编码：${imported.code}`);
					Object.assign(metaForm, imported);
					parseFormConfigFromStr();
				} catch (_unused6) {
					try {
						const parsed = JSON.parse(text);
						if (parsed.formConfig && typeof parsed.formConfig === "string") {
							Object.assign(metaForm, parsed);
							parseFormConfigFromStr();
							ElMessage.success("已加载到画布（本地解析，未提交后端）");
						} else if (Array.isArray(parsed.fields)) {
							metaForm.formConfig = text;
							parseFormConfigFromStr();
							ElMessage.success("已加载到画布");
						} else ElMessage.error("无法识别的 JSON 结构");
					} catch (e) {
						ElMessage.error("JSON 解析失败：" + e.message);
					}
				}
			};
			input.click();
		}
		/** 进入预览模式 */
		function handlePreview() {
			syncFormConfigToStr();
			for (const k of Object.keys(previewData)) delete previewData[k];
			for (const f of formConfig.fields) {
				var _f$defaultValue;
				previewData[f.prop] = (_f$defaultValue = f.defaultValue) !== null && _f$defaultValue !== void 0 ? _f$defaultValue : "";
			}
			previewMode.value = true;
		}
		/** 退出预览 */
		function exitPreview() {
			previewMode.value = false;
		}
		/** 预览提交 */
		function handlePreviewSubmit(val) {
			ElMessageBox.alert(`<pre style="max-height:400px;overflow:auto;">${JSON.stringify(val, null, 2)}</pre>`, "提交数据预览", {
				dangerouslyUseHTMLString: true,
				confirmButtonText: "关闭"
			});
		}
		/** 重置画布 */
		function handleReset() {
			ElMessageBox.confirm("确认清空画布所有字段？此操作不可恢复", "确认", { type: "warning" }).then(() => {
				formConfig.fields = [];
				selectedFieldId.value = "";
				fieldSeq = 0;
				ElMessage.success("已重置画布");
			}).catch(() => {});
		}
		function goToList() {
			router.push("/lowcode/form-list");
		}
		const editId = route.query.id ? Number(route.query.id) : 0;
		if (editId > 0) loadForm(editId);
		else {
			formConfig.title = "未命名表单";
			if (historyDebounce) {
				clearTimeout(historyDebounce);
				historyDebounce = null;
			}
			history.reset(JSON.parse(JSON.stringify(formConfig)));
		}
		const collaboration = useCollaboration({
			configType: "FORM",
			configId: editId,
			userId: ((_userStore$userInfo = userStore.userInfo) === null || _userStore$userInfo === void 0 ? void 0 : _userStore$userInfo.id) || 0,
			userName: ((_userStore$userInfo2 = userStore.userInfo) === null || _userStore$userInfo2 === void 0 ? void 0 : _userStore$userInfo2.nickname) || ((_userStore$userInfo3 = userStore.userInfo) === null || _userStore$userInfo3 === void 0 ? void 0 : _userStore$userInfo3.username) || ""
		});
		onMounted(async () => {
			window.addEventListener("keydown", onUndoRedoKeydown);
			try {
				await initBuiltinComponents();
				registryComponents.value = LowCodeComponentRegistry_default.list();
			} catch (e) {
				console.error("[FormDesigner] 组件加载失败，业务组件面板可能不可用:", e);
			}
			if (editId > 0) collaboration.join();
		});
		onBeforeUnmount(() => {
			window.removeEventListener("keydown", onUndoRedoKeydown);
			if (historyDebounce) clearTimeout(historyDebounce);
		});
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_input_number = resolveComponent("el-input-number");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_radio_button = resolveComponent("el-radio-button");
			const _component_el_radio_group = resolveComponent("el-radio-group");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_row = resolveComponent("el-row");
			const _component_Plus = resolveComponent("Plus");
			const _component_el_button_group = resolveComponent("el-button-group");
			const _component_el_date_picker = resolveComponent("el-date-picker");
			const _component_el_switch = resolveComponent("el-switch");
			const _component_el_rate = resolveComponent("el-rate");
			const _component_el_slider = resolveComponent("el-slider");
			const _component_el_divider = resolveComponent("el-divider");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_option_group = resolveComponent("el-option-group");
			const _component_el_collapse_item = resolveComponent("el-collapse-item");
			const _component_el_collapse = resolveComponent("el-collapse");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_card, {
					shadow: "never",
					class: "toolbar-card",
					"body-style": { padding: "12px 16px" }
				}, {
					default: withCtx(() => {
						var _unref$userInfo;
						return [createElementVNode("div", _hoisted_2, [createElementVNode("div", _hoisted_3, [
							createVNode(_component_el_button, {
								type: "primary",
								icon: "Document",
								loading: loading.value,
								onClick: handleSave
							}, {
								default: withCtx(() => [..._cache[28] || (_cache[28] = [createTextVNode(" 保存草稿 ", -1)])]),
								_: 1
							}, 8, ["loading"]),
							createVNode(_component_el_button, {
								type: "success",
								icon: "Promotion",
								onClick: handlePublish
							}, {
								default: withCtx(() => [..._cache[29] || (_cache[29] = [createTextVNode("发布", -1)])]),
								_: 1
							}),
							createVNode(_component_el_button, {
								icon: "Download",
								onClick: handleExport
							}, {
								default: withCtx(() => [..._cache[30] || (_cache[30] = [createTextVNode("导出", -1)])]),
								_: 1
							}),
							createVNode(_component_el_button, {
								icon: "Upload",
								onClick: handleImport
							}, {
								default: withCtx(() => [..._cache[31] || (_cache[31] = [createTextVNode("导入", -1)])]),
								_: 1
							}),
							createVNode(_component_el_button, {
								icon: "View",
								onClick: handlePreview
							}, {
								default: withCtx(() => [..._cache[32] || (_cache[32] = [createTextVNode("预览", -1)])]),
								_: 1
							}),
							createVNode(_component_el_button, {
								icon: "RefreshLeft",
								disabled: !unref(canUndo),
								onClick: undo
							}, {
								default: withCtx(() => [..._cache[33] || (_cache[33] = [createTextVNode("撤销", -1)])]),
								_: 1
							}, 8, ["disabled"]),
							createVNode(_component_el_button, {
								icon: "RefreshRight",
								disabled: !unref(canRedo),
								onClick: redo
							}, {
								default: withCtx(() => [..._cache[34] || (_cache[34] = [createTextVNode("重做", -1)])]),
								_: 1
							}, 8, ["disabled"]),
							createVNode(_component_el_button, {
								icon: "Refresh",
								onClick: handleReset
							}, {
								default: withCtx(() => [..._cache[35] || (_cache[35] = [createTextVNode("重置", -1)])]),
								_: 1
							}),
							metaForm.status === "PUBLISHED" ? (openBlock(), createBlock(_component_el_button, {
								key: 0,
								icon: "FolderOpened",
								onClick: handleArchive
							}, {
								default: withCtx(() => [..._cache[36] || (_cache[36] = [createTextVNode(" 归档 ", -1)])]),
								_: 1
							})) : createCommentVNode("", true)
						]), createElementVNode("div", _hoisted_4, [
							unref(editId) > 0 ? (openBlock(), createBlock(OnlineUsersIndicator_default, {
								key: 0,
								users: unref(collaboration).onlineUsers.value,
								"current-user-id": ((_unref$userInfo = unref(userStore).userInfo) === null || _unref$userInfo === void 0 ? void 0 : _unref$userInfo.id) || 0
							}, null, 8, ["users", "current-user-id"])) : createCommentVNode("", true),
							createVNode(_component_el_tag, { type: metaForm.status === "PUBLISHED" ? "success" : metaForm.status === "ARCHIVED" ? "info" : "warning" }, {
								default: withCtx(() => [createTextVNode(toDisplayString(metaForm.status || "DRAFT"), 1)]),
								_: 1
							}, 8, ["type"]),
							createVNode(_component_el_button, {
								link: "",
								type: "primary",
								onClick: goToList
							}, {
								default: withCtx(() => [..._cache[37] || (_cache[37] = [createTextVNode("返回列表", -1)])]),
								_: 1
							})
						])])];
					}),
					_: 1
				}),
				createVNode(_component_el_card, {
					shadow: "never",
					class: "meta-card",
					"body-style": { padding: "12px 16px" }
				}, {
					default: withCtx(() => [createVNode(_component_el_form, {
						ref_key: "metaFormRef",
						ref: metaFormRef,
						model: metaForm,
						rules: metaRules,
						inline: "",
						"label-width": "90px"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, {
								label: "表单编码",
								prop: "code"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: metaForm.code,
									"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => metaForm.code = $event),
									placeholder: "如：tpl_project_create",
									disabled: !!metaForm.id,
									style: { "width": "220px" }
								}, null, 8, ["modelValue", "disabled"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "表单名称",
								prop: "name"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: metaForm.name,
									"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => metaForm.name = $event),
									placeholder: "请输入表单名称",
									style: { "width": "220px" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "业务类型" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: metaForm.bizType,
									"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => metaForm.bizType = $event),
									placeholder: "如：PROJECT",
									style: { "width": "160px" }
								}, null, 8, ["modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "描述" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: metaForm.description,
									"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => metaForm.description = $event),
									placeholder: "表单描述",
									style: { "width": "320px" }
								}, null, 8, ["modelValue"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["model"])]),
					_: 1
				}),
				!previewMode.value ? (openBlock(), createElementBlock("div", _hoisted_5, [
					createVNode(_component_el_card, {
						shadow: "never",
						class: "panel panel-left",
						"body-style": { padding: "8px" }
					}, {
						header: withCtx(() => [..._cache[38] || (_cache[38] = [createElementVNode("span", { class: "panel-title" }, "组件库", -1)])]),
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(componentGroups.value, (group) => {
							return openBlock(), createElementBlock("div", {
								key: group.title,
								class: "comp-group"
							}, [createElementVNode("div", _hoisted_6, toDisplayString(group.title), 1), createElementVNode("div", _hoisted_7, [(openBlock(true), createElementBlock(Fragment, null, renderList(group.items, (comp) => {
								return openBlock(), createElementBlock("div", {
									key: comp.type,
									class: "comp-item",
									draggable: "true",
									onDragstart: ($event) => onDragStart($event, comp),
									onClick: ($event) => addField(comp)
								}, [createVNode(_component_el_icon, null, {
									default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(comp.icon)))]),
									_: 2
								}, 1024), createElementVNode("span", null, toDisplayString(comp.label), 1)], 40, _hoisted_8);
							}), 128))])]);
						}), 128))]),
						_: 1
					}),
					createVNode(_component_el_card, {
						shadow: "never",
						class: "panel panel-center",
						"body-style": { padding: "12px" }
					}, {
						header: withCtx(() => [createElementVNode("div", _hoisted_9, [createElementVNode("span", _hoisted_10, "画布（" + toDisplayString(formConfig.fields.length) + " 个字段）", 1), createVNode(_component_el_form, {
							inline: "",
							size: "small",
							class: "canvas-config"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_form_item, { label: "标签宽度" }, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: formConfig.labelWidth,
										"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => formConfig.labelWidth = $event),
										min: 60,
										max: 200,
										step: 10,
										"controls-position": "right",
										style: { "width": "110px" }
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "标签位置" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: formConfig.labelPosition,
										"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => formConfig.labelPosition = $event),
										style: { "width": "100px" }
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "左",
												value: "left"
											}),
											createVNode(_component_el_option, {
												label: "右",
												value: "right"
											}),
											createVNode(_component_el_option, {
												label: "上",
												value: "top"
											})
										]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "尺寸" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: formConfig.size,
										"onUpdate:modelValue": _cache[6] || (_cache[6] = ($event) => formConfig.size = $event),
										style: { "width": "100px" }
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "大",
												value: "large"
											}),
											createVNode(_component_el_option, {
												label: "默认",
												value: "default"
											}),
											createVNode(_component_el_option, {
												label: "小",
												value: "small"
											})
										]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "布局" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: formConfig.layout.type,
										"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => formConfig.layout.type = $event),
										style: { "width": "110px" }
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "栅格",
												value: "grid"
											}),
											createVNode(_component_el_option, {
												label: "标签页",
												value: "tabs"
											}),
											createVNode(_component_el_option, {
												label: "折叠面板",
												value: "collapse"
											})
										]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								formConfig.layout.type === "grid" ? (openBlock(), createBlock(_component_el_form_item, {
									key: 0,
									label: "间距"
								}, {
									default: withCtx(() => [createVNode(_component_el_input_number, {
										modelValue: formConfig.layout.gutter,
										"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => formConfig.layout.gutter = $event),
										min: 0,
										max: 40,
										style: { "width": "100px" }
									}, null, 8, ["modelValue"])]),
									_: 1
								})) : createCommentVNode("", true)
							]),
							_: 1
						})])]),
						default: withCtx(() => {
							var _formConfig$layout$gu, _formConfig$layout;
							return [
								createElementVNode("div", _hoisted_11, [
									_cache[40] || (_cache[40] = createElementVNode("span", { class: "resp-preview-bar__title" }, "响应式预览", -1)),
									createVNode(_component_el_radio_group, {
										modelValue: previewWidth.value,
										"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => previewWidth.value = $event),
										size: "small"
									}, {
										default: withCtx(() => [createVNode(_component_el_radio_button, { value: "auto" }, {
											default: withCtx(() => [..._cache[39] || (_cache[39] = [createTextVNode("自适应", -1)])]),
											_: 1
										}), (openBlock(true), createElementBlock(Fragment, null, renderList(unref(breakpointOrder), (bp) => {
											return openBlock(), createBlock(_component_el_radio_button, {
												key: bp,
												value: bp
											}, {
												default: withCtx(() => [createTextVNode(toDisplayString(bp), 1)]),
												_: 2
											}, 1032, ["value"]);
										}), 128))]),
										_: 1
									}, 8, ["modelValue"]),
									activePreviewBp.value ? (openBlock(), createElementBlock("span", _hoisted_12, " 模拟 " + toDisplayString(previewLabel.value) + "（" + toDisplayString(previewWidthPx.value) + "px）— 展示该断点下各字段栅格占比 ", 1)) : createCommentVNode("", true)
								]),
								activePreviewBp.value && formConfig.fields.length > 0 ? (openBlock(), createElementBlock("div", {
									key: 0,
									class: "resp-preview-grid",
									style: normalizeStyle({ maxWidth: previewWidthPx.value + "px" })
								}, [createVNode(_component_el_row, { gutter: (_formConfig$layout$gu = (_formConfig$layout = formConfig.layout) === null || _formConfig$layout === void 0 ? void 0 : _formConfig$layout.gutter) !== null && _formConfig$layout$gu !== void 0 ? _formConfig$layout$gu : 16 }, {
									default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(previewRows.value, (row) => {
										return openBlock(), createBlock(_component_el_col, {
											key: row.field.id,
											span: row.span
										}, {
											default: withCtx(() => [createElementVNode("div", {
												class: normalizeClass(["resp-preview-cell", { active: selectedFieldId.value === row.field.id }]),
												onClick: ($event) => selectField(row.field.id)
											}, [createElementVNode("span", _hoisted_14, toDisplayString(row.field.label), 1), createElementVNode("span", _hoisted_15, toDisplayString(row.span) + "/24", 1)], 10, _hoisted_13)]),
											_: 2
										}, 1032, ["span"]);
									}), 128))]),
									_: 1
								}, 8, ["gutter"])], 4)) : createCommentVNode("", true),
								createElementVNode("div", {
									class: normalizeClass(["canvas-dropzone", { empty: formConfig.fields.length === 0 }]),
									onDragover: onCanvasDragOver,
									onDrop: onCanvasDrop
								}, [formConfig.fields.length === 0 ? (openBlock(), createElementBlock("div", _hoisted_16, [createVNode(_component_el_icon, { size: 40 }, {
									default: withCtx(() => [createVNode(_component_Plus)]),
									_: 1
								}), _cache[41] || (_cache[41] = createElementVNode("p", null, "从左侧拖拽组件到此处，或点击组件添加", -1))])) : (openBlock(), createElementBlock("div", _hoisted_17, [(openBlock(true), createElementBlock(Fragment, null, renderList(formConfig.fields, (field, idx) => {
									return openBlock(), createElementBlock("div", {
										key: field.id,
										class: normalizeClass(["field-card", { active: selectedFieldId.value === field.id }]),
										onClick: ($event) => selectField(field.id)
									}, [createElementVNode("div", _hoisted_19, [
										createVNode(_component_el_tag, {
											size: "small",
											type: field.type === unref(FieldType).CUSTOM && field.componentName ? "warning" : "info"
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(field.type === unref(FieldType).CUSTOM && field.componentName ? field.componentName : field.type), 1)]),
											_: 2
										}, 1032, ["type"]),
										createElementVNode("span", _hoisted_20, toDisplayString(field.label), 1),
										createElementVNode("span", _hoisted_21, toDisplayString(field.prop), 1),
										field.required ? (openBlock(), createElementBlock("span", _hoisted_22, "*")) : createCommentVNode("", true),
										createElementVNode("div", _hoisted_23, [createVNode(_component_el_button_group, { size: "small" }, {
											default: withCtx(() => [
												createVNode(_component_el_button, {
													icon: "Top",
													disabled: idx === 0,
													onClick: withModifiers(($event) => moveUp(field.id), ["stop"])
												}, null, 8, ["disabled", "onClick"]),
												createVNode(_component_el_button, {
													icon: "Bottom",
													disabled: idx === formConfig.fields.length - 1,
													onClick: withModifiers(($event) => moveDown(field.id), ["stop"])
												}, null, 8, ["disabled", "onClick"]),
												createVNode(_component_el_button, {
													icon: "CopyDocument",
													onClick: withModifiers(($event) => duplicateField(field.id), ["stop"])
												}, null, 8, ["onClick"]),
												createVNode(_component_el_button, {
													icon: "Delete",
													type: "danger",
													onClick: withModifiers(($event) => removeField(field.id), ["stop"])
												}, null, 8, ["onClick"])
											]),
											_: 2
										}, 1024)])
									]), createElementVNode("div", _hoisted_24, [field.type === unref(FieldType).INPUT ? (openBlock(), createBlock(_component_el_input, {
										key: 0,
										placeholder: field.placeholder,
										disabled: "",
										size: "small"
									}, null, 8, ["placeholder"])) : field.type === unref(FieldType).TEXTAREA ? (openBlock(), createBlock(_component_el_input, {
										key: 1,
										type: "textarea",
										rows: 2,
										placeholder: field.placeholder,
										disabled: "",
										size: "small"
									}, null, 8, ["placeholder"])) : field.type === unref(FieldType).NUMBER ? (openBlock(), createBlock(_component_el_input_number, {
										key: 2,
										placeholder: field.placeholder,
										disabled: "",
										size: "small",
										style: { "width": "100%" }
									}, null, 8, ["placeholder"])) : field.type === unref(FieldType).SELECT ? (openBlock(), createBlock(_component_el_select, {
										key: 3,
										placeholder: field.placeholder,
										disabled: "",
										size: "small",
										style: { "width": "100%" }
									}, null, 8, ["placeholder"])) : field.type === unref(FieldType).DATE || field.type === unref(FieldType).DATETIME || field.type === unref(FieldType).DATERANGE ? (openBlock(), createBlock(_component_el_date_picker, {
										key: 4,
										placeholder: field.placeholder,
										disabled: "",
										size: "small",
										style: { "width": "100%" }
									}, null, 8, ["placeholder"])) : field.type === unref(FieldType).SWITCH ? (openBlock(), createBlock(_component_el_switch, {
										key: 5,
										disabled: "",
										size: "small"
									})) : field.type === unref(FieldType).RATE ? (openBlock(), createBlock(_component_el_rate, {
										key: 6,
										disabled: "",
										size: "small"
									})) : field.type === unref(FieldType).SLIDER ? (openBlock(), createBlock(_component_el_slider, {
										key: 7,
										disabled: "",
										size: "small"
									})) : field.type === unref(FieldType).DIVIDER ? (openBlock(), createBlock(_component_el_divider, { key: 8 }, {
										default: withCtx(() => [createTextVNode(toDisplayString(field.label), 1)]),
										_: 2
									}, 1024)) : field.type === unref(FieldType).TITLE ? (openBlock(), createElementBlock("h4", _hoisted_25, toDisplayString(field.label), 1)) : (openBlock(), createElementBlock("span", _hoisted_26, toDisplayString(field.label) + "（" + toDisplayString(field.type) + "）", 1))])], 10, _hoisted_18);
								}), 128))]))], 34)
							];
						}),
						_: 1
					}),
					createVNode(_component_el_card, {
						shadow: "never",
						class: "panel panel-right",
						"body-style": { padding: "12px" }
					}, {
						header: withCtx(() => [..._cache[42] || (_cache[42] = [createElementVNode("span", { class: "panel-title" }, "属性面板", -1)])]),
						default: withCtx(() => [!selectedField.value ? (openBlock(), createElementBlock("div", _hoisted_27, [createVNode(_component_el_empty, {
							description: "请选择一个字段",
							"image-size": 80
						})])) : (openBlock(), createBlock(_component_el_form, {
							key: 1,
							model: selectedField.value,
							"label-width": "90px",
							size: "small"
						}, {
							default: withCtx(() => [
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[43] || (_cache[43] = [createTextVNode("基础属性", -1)])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "字段类型" }, {
									default: withCtx(() => [isSelectedRegistry.value ? (openBlock(), createBlock(_component_el_tag, {
										key: 0,
										type: "warning",
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode(" 业务组件：" + toDisplayString(selectedField.value.componentName), 1)]),
										_: 1
									})) : (openBlock(), createBlock(_component_el_select, {
										key: 1,
										"model-value": selectedField.value.type,
										onChange: _cache[10] || (_cache[10] = (v) => changeFieldType(selectedField.value, v)),
										style: { "width": "100%" }
									}, {
										default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(componentGroups.value, (g) => {
											return openBlock(), createBlock(_component_el_option_group, {
												key: g.title,
												label: g.title
											}, {
												default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(g.items, (c) => {
													return openBlock(), createBlock(_component_el_option, {
														key: c.type,
														label: c.label,
														value: c.type
													}, null, 8, ["label", "value"]);
												}), 128))]),
												_: 2
											}, 1032, ["label"]);
										}), 128))]),
										_: 1
									}, 8, ["model-value"]))]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "标签" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedField.value.label,
										"onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => selectedField.value.label = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "字段名" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedField.value.prop,
										"onUpdate:modelValue": _cache[12] || (_cache[12] = ($event) => selectedField.value.prop = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "占位符" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedField.value.placeholder,
										"onUpdate:modelValue": _cache[13] || (_cache[13] = ($event) => selectedField.value.placeholder = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "默认值" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedField.value.defaultValue,
										"onUpdate:modelValue": _cache[14] || (_cache[14] = ($event) => selectedField.value.defaultValue = $event),
										placeholder: "留空表示无默认值"
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								isSelectedRegistry.value && selectedComponentMeta.value ? (openBlock(), createElementBlock(Fragment, { key: 0 }, [createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[44] || (_cache[44] = [createTextVNode("组件属性", -1)])]),
									_: 1
								}), createVNode(LowCodePropertyPanel_default, {
									meta: selectedComponentMeta.value,
									"model-value": selectedField.value.props || {},
									"onUpdate:modelValue": _cache[15] || (_cache[15] = (v) => {
										selectedField.value.props = v;
									})
								}, null, 8, ["meta", "model-value"])], 64)) : createCommentVNode("", true),
								!isSelectedRegistry.value && selectedField.value.props && "options" in selectedField.value.props ? (openBlock(), createElementBlock(Fragment, { key: 1 }, [
									createVNode(_component_el_divider, { "content-position": "left" }, {
										default: withCtx(() => [..._cache[45] || (_cache[45] = [createTextVNode("选项配置", -1)])]),
										_: 1
									}),
									(openBlock(true), createElementBlock(Fragment, null, renderList(selectedField.value.props.options, (opt, idx) => {
										return openBlock(), createElementBlock("div", {
											key: idx,
											class: "option-row"
										}, [
											createVNode(_component_el_input, {
												modelValue: opt.label,
												"onUpdate:modelValue": ($event) => opt.label = $event,
												placeholder: "标签",
												size: "small",
												style: { "width": "40%" }
											}, null, 8, ["modelValue", "onUpdate:modelValue"]),
											createVNode(_component_el_input, {
												modelValue: opt.value,
												"onUpdate:modelValue": ($event) => opt.value = $event,
												placeholder: "值",
												size: "small",
												style: {
													"width": "40%",
													"margin-left": "4px"
												}
											}, null, 8, ["modelValue", "onUpdate:modelValue"]),
											createVNode(_component_el_button, {
												icon: "Delete",
												type: "danger",
												size: "small",
												onClick: ($event) => removeOption(selectedField.value, idx),
												style: { "margin-left": "4px" }
											}, null, 8, ["onClick"])
										]);
									}), 128)),
									createVNode(_component_el_button, {
										icon: "Plus",
										size: "small",
										onClick: _cache[16] || (_cache[16] = ($event) => addOption(selectedField.value))
									}, {
										default: withCtx(() => [..._cache[46] || (_cache[46] = [createTextVNode("添加选项", -1)])]),
										_: 1
									})
								], 64)) : createCommentVNode("", true),
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[47] || (_cache[47] = [createTextVNode("校验属性", -1)])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "必填" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: selectedField.value.required,
										"onUpdate:modelValue": _cache[17] || (_cache[17] = ($event) => selectedField.value.required = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								selectedField.value.type === unref(FieldType).INPUT || selectedField.value.type === unref(FieldType).TEXTAREA ? (openBlock(), createBlock(_component_el_form_item, {
									key: 2,
									label: "最大长度"
								}, {
									default: withCtx(() => {
										var _selectedField$value$;
										return [createVNode(_component_el_input_number, {
											"model-value": (_selectedField$value$ = selectedField.value.props) === null || _selectedField$value$ === void 0 ? void 0 : _selectedField$value$.maxlength,
											onChange: _cache[18] || (_cache[18] = (v) => {
												selectedField.value.props = selectedField.value.props || {};
												selectedField.value.props.maxlength = v;
											}),
											min: 1,
											max: 9999,
											style: { "width": "100%" }
										}, null, 8, ["model-value"])];
									}),
									_: 1
								})) : createCommentVNode("", true),
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[48] || (_cache[48] = [createTextVNode("样式属性", -1)])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "响应式栅格" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: isResponsive.value,
										"onUpdate:modelValue": _cache[19] || (_cache[19] = ($event) => isResponsive.value = $event)
									}, null, 8, ["modelValue"]), _cache[49] || (_cache[49] = createElementVNode("span", { class: "form-tip" }, "开启后按 xs/sm/md/lg/xl 五档断点配置", -1))]),
									_: 1
								}),
								!isResponsive.value ? (openBlock(), createBlock(_component_el_form_item, {
									key: 3,
									label: "栅格宽度"
								}, {
									default: withCtx(() => [createVNode(_component_el_slider, {
										modelValue: fieldSpan.value,
										"onUpdate:modelValue": _cache[20] || (_cache[20] = ($event) => fieldSpan.value = $event),
										min: 1,
										max: 24,
										"show-input": "",
										style: { "width": "100%" }
									}, null, 8, ["modelValue"])]),
									_: 1
								})) : (openBlock(), createBlock(_component_el_collapse, {
									key: 4,
									modelValue: responsiveCollapse.value,
									"onUpdate:modelValue": _cache[21] || (_cache[21] = ($event) => responsiveCollapse.value = $event),
									class: "resp-collapse"
								}, {
									default: withCtx(() => [createVNode(_component_el_collapse_item, { name: "resp" }, {
										title: withCtx(() => [..._cache[50] || (_cache[50] = [createElementVNode("span", null, "响应式断点（1-24，留空继承更小断点）", -1)])]),
										default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(unref(breakpointOrder), (bp) => {
											return openBlock(), createBlock(_component_el_form_item, {
												key: bp,
												label: bp
											}, {
												default: withCtx(() => [createElementVNode("div", _hoisted_28, [createVNode(_component_el_input_number, {
													"model-value": getBreakpoint(bp),
													min: 1,
													max: 24,
													"controls-position": "right",
													placeholder: "留空",
													style: { "flex": "1" },
													"onUpdate:modelValue": (v) => setBreakpoint(bp, v !== null && v !== void 0 ? v : void 0)
												}, null, 8, ["model-value", "onUpdate:modelValue"]), getBreakpoint(bp) !== void 0 ? (openBlock(), createBlock(_component_el_button, {
													key: 0,
													link: "",
													type: "primary",
													icon: "Close",
													title: "清除（留空，继承更小断点）",
													onClick: ($event) => setBreakpoint(bp, void 0)
												}, null, 8, ["onClick"])) : createCommentVNode("", true)])]),
												_: 2
											}, 1032, ["label"]);
										}), 128))]),
										_: 1
									})]),
									_: 1
								}, 8, ["modelValue"])),
								createVNode(_component_el_form_item, { label: "可清空" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: selectedField.value.clearable,
										"onUpdate:modelValue": _cache[22] || (_cache[22] = ($event) => selectedField.value.clearable = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[51] || (_cache[51] = [createTextVNode("状态属性", -1)])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "禁用" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: selectedField.value.disabled,
										"onUpdate:modelValue": _cache[23] || (_cache[23] = ($event) => selectedField.value.disabled = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "只读" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: selectedField.value.readonly,
										"onUpdate:modelValue": _cache[24] || (_cache[24] = ($event) => selectedField.value.readonly = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "隐藏" }, {
									default: withCtx(() => [createVNode(_component_el_switch, {
										modelValue: selectedField.value.hidden,
										"onUpdate:modelValue": _cache[25] || (_cache[25] = ($event) => selectedField.value.hidden = $event)
									}, null, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_divider, { "content-position": "left" }, {
									default: withCtx(() => [..._cache[52] || (_cache[52] = [createTextVNode("高级属性", -1)])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "change回调" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: selectedField.value.events.change,
										"onUpdate:modelValue": _cache[26] || (_cache[26] = ($event) => selectedField.value.events.change = $event),
										placeholder: "如：onFieldChange"
									}, null, 8, ["modelValue"])]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["model"]))]),
						_: 1
					})
				])) : (openBlock(), createBlock(_component_el_card, {
					key: 1,
					shadow: "never",
					class: "preview-card"
				}, {
					header: withCtx(() => [createElementVNode("div", _hoisted_29, [createElementVNode("span", _hoisted_30, "表单预览：" + toDisplayString(formConfig.title || metaForm.name), 1), createVNode(_component_el_button, {
						icon: "Back",
						onClick: exitPreview
					}, {
						default: withCtx(() => [..._cache[53] || (_cache[53] = [createTextVNode("退出预览", -1)])]),
						_: 1
					})])]),
					default: withCtx(() => [createVNode(LowCodeFormRenderer_default, {
						config: formConfig,
						modelValue: unref(previewData),
						"onUpdate:modelValue": _cache[27] || (_cache[27] = ($event) => isRef(previewData) ? previewData.value = $event : previewData = $event),
						onSubmit: handlePreviewSubmit
					}, null, 8, ["config", "modelValue"])]),
					_: 1
				}))
			]);
		};
	}
}), [["__scopeId", "data-v-e222b582"]]);
//#endregion
export { form_designer_default as default };

//# sourceMappingURL=form-designer-BAxu4Uhb.js.map