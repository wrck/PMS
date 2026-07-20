import { n as getConnectorList } from "./lowcode-connector-Cjm1QnL-.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { n as Graph, t as register } from "./es-0oWOj6Bi.js";
import { t as ExpressionEditor_default } from "./ExpressionEditor-CFxBT6yN.js";
import { a as getRecentExecutionLogs, c as stepOverMicroflowDebug, i as getMicroflowList, l as terminateMicroflowDebug, n as executeMicroflow, o as saveMicroflow, r as getExecutionLogs, s as startMicroflowDebug, t as continueMicroflowDebug } from "./lowcode-microflow-CXsjmWyP.js";
import { r as getRuleList } from "./lowcode-rule-BZ8USwM5.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, nextTick, normalizeClass, normalizeStyle, onBeforeUnmount, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, shallowRef, toDisplayString, unref, vShow, watch, withCtx, withDirectives, withModifiers } from "vue";
//#region src/components/MicroflowDesigner/NodePalette.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$6 = { class: "node-palette" };
var _hoisted_2$6 = { class: "palette-list" };
var _hoisted_3$5 = { class: "group-title" };
var _hoisted_4$4 = [
	"title",
	"onDragstart",
	"onClick"
];
var _hoisted_5$4 = { class: "palette-text" };
var _hoisted_6$3 = { class: "palette-label" };
var _hoisted_7$3 = { class: "palette-desc" };
//#endregion
//#region src/components/MicroflowDesigner/NodePalette.vue
var NodePalette_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "MicroflowNodePalette",
	__name: "NodePalette",
	emits: ["add-node"],
	setup(__props, { emit: __emit }) {
		/**
		* 微流节点面板（左侧，借鉴 Mendix Microflows 工具栏）。
		*
		* <p>按分组列出 11 种节点类型，每种带图标 + 文字 + 主题色，支持 HTML5 draggable。
		* 拖到中间画布时，父组件监听 drop 事件并调用 X6 addNode 创建节点。</p>
		*
		* <p>分组：流程控制（START/END/RETURN/THROW_EXCEPTION）、逻辑（ASSIGN/CONDITION/LOOP）、
		* 调用（CALL_SERVICE/CALL_MICROFLOW/CALL_RULE/CALL_CONNECTOR）。
		* 同时支持点击节点项将其添加到画布默认位置（无 drag 时的降级路径）。</p>
		*/
		const emit = __emit;
		const PALETTE = [
			{
				type: "START",
				label: "开始",
				icon: "▶",
				color: "#67c23a",
				shape: "circle",
				description: "流程起点"
			},
			{
				type: "END",
				label: "结束",
				icon: "■",
				color: "#f56c6c",
				shape: "circle",
				description: "流程终点"
			},
			{
				type: "ASSIGN",
				label: "赋值",
				icon: "=",
				color: "#409eff",
				shape: "rect",
				description: "变量赋值"
			},
			{
				type: "CONDITION",
				label: "条件",
				icon: "?",
				color: "#e6a23c",
				shape: "diamond",
				description: "条件分支"
			},
			{
				type: "LOOP",
				label: "循环",
				icon: "↻",
				color: "#9c27b0",
				shape: "rect",
				description: "迭代循环"
			},
			{
				type: "CALL_SERVICE",
				label: "调用服务",
				icon: "⚙",
				color: "#00bcd4",
				shape: "rect",
				description: "调用 Spring Bean 方法"
			},
			{
				type: "CALL_MICROFLOW",
				label: "调用子微流",
				icon: "✦",
				color: "#00bcd4",
				shape: "rect",
				description: "调用其他微流"
			},
			{
				type: "CALL_RULE",
				label: "调用规则",
				icon: "§",
				color: "#00bcd4",
				shape: "rect",
				description: "调用规则引擎"
			},
			{
				type: "CALL_CONNECTOR",
				label: "调用连接器",
				icon: "⇄",
				color: "#00bcd4",
				shape: "rect",
				description: "调用 REST/DB 连接器"
			},
			{
				type: "THROW_EXCEPTION",
				label: "抛异常",
				icon: "!",
				color: "#ff9800",
				shape: "rect",
				description: "抛出业务异常"
			},
			{
				type: "RETURN",
				label: "返回",
				icon: "←",
				color: "#67c23a",
				shape: "rect",
				description: "返回结果"
			}
		];
		/** 节点分组定义（流程控制 / 逻辑 / 调用） */
		const GROUPS = [
			{
				title: "流程控制",
				types: [
					"START",
					"END",
					"RETURN",
					"THROW_EXCEPTION"
				]
			},
			{
				title: "逻辑",
				types: [
					"ASSIGN",
					"CONDITION",
					"LOOP"
				]
			},
			{
				title: "调用",
				types: [
					"CALL_SERVICE",
					"CALL_MICROFLOW",
					"CALL_RULE",
					"CALL_CONNECTOR"
				]
			}
		];
		/** type → PaletteItem 查找表 */
		const PALETTE_MAP = PALETTE.reduce((acc, item) => {
			acc[item.type] = item;
			return acc;
		}, {});
		function onDragStart(e, item) {
			if (!e.dataTransfer) return;
			e.dataTransfer.setData("microflow-node-type", item.type);
			e.dataTransfer.effectAllowed = "copy";
		}
		function onClick(item) {
			emit("add-node", item.type);
		}
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", _hoisted_1$6, [
				_cache[0] || (_cache[0] = createElementVNode("div", { class: "palette-header" }, "节点面板", -1)),
				createElementVNode("div", _hoisted_2$6, [(openBlock(), createElementBlock(Fragment, null, renderList(GROUPS, (group) => {
					return createElementVNode("div", {
						key: group.title,
						class: "palette-group"
					}, [createElementVNode("div", _hoisted_3$5, toDisplayString(group.title), 1), (openBlock(true), createElementBlock(Fragment, null, renderList(group.types, (t) => {
						return openBlock(), createElementBlock("div", {
							key: t,
							class: normalizeClass(["palette-item", `shape-${unref(PALETTE_MAP)[t].shape}`]),
							draggable: "true",
							title: unref(PALETTE_MAP)[t].description,
							onDragstart: ($event) => onDragStart($event, unref(PALETTE_MAP)[t]),
							onClick: ($event) => onClick(unref(PALETTE_MAP)[t])
						}, [createElementVNode("span", {
							class: "palette-icon",
							style: normalizeStyle({ background: unref(PALETTE_MAP)[t].color })
						}, toDisplayString(unref(PALETTE_MAP)[t].icon), 5), createElementVNode("span", _hoisted_5$4, [createElementVNode("span", _hoisted_6$3, toDisplayString(unref(PALETTE_MAP)[t].label), 1), createElementVNode("span", _hoisted_7$3, toDisplayString(unref(PALETTE_MAP)[t].description), 1)])], 42, _hoisted_4$4);
					}), 128))]);
				}), 64))]),
				_cache[1] || (_cache[1] = createElementVNode("div", { class: "palette-tip" }, "提示：拖拽或点击节点添加到画布", -1))
			]);
		};
	}
}), [["__scopeId", "data-v-256cf372"]]);
//#endregion
//#region src/components/MicroflowDesigner/NodeParamPanel.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$5 = { class: "node-param-panel" };
var _hoisted_2$5 = {
	key: 0,
	class: "empty-state"
};
var _hoisted_3$4 = { class: "panel-header" };
var _hoisted_4$3 = { class: "header-title" };
var _hoisted_5$3 = {
	key: 0,
	class: "form-tip"
};
//#endregion
//#region src/components/MicroflowDesigner/NodeParamPanel.vue
var NodeParamPanel_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "MicroflowNodeParamPanel",
	__name: "NodeParamPanel",
	props: {
		node: {},
		nodes: {},
		microflowOptions: {},
		ruleOptions: {},
		connectorOptions: {},
		variables: {}
	},
	emits: ["update:node"],
	setup(__props, { emit: __emit }) {
		/**
		* 微流节点参数面板（右侧，借鉴 Mendix Microflows 属性面板）。
		*
		* <p>根据选中节点的 type 渲染对应的参数配置表单（config 字段名与后端 MicroflowNodeExecutor 对齐）：</p>
		* <ul>
		*   <li>ASSIGN：target（目标变量）+ expression（Groovy 表达式）</li>
		*   <li>CONDITION：expression（Groovy 布尔表达式）+ trueBranch/falseBranch 目标节点</li>
		*   <li>LOOP：iterableExpression（Groovy 表达式）+ bodyNodeId（循环体起始节点）</li>
		*   <li>CALL_SERVICE：beanName + methodName + target（结果变量，可选）+ args（Groovy 表达式）</li>
		*   <li>CALL_MICROFLOW：microflowCode（下拉）+ inputsExpression（Groovy 表达式）</li>
		*   <li>CALL_RULE：ruleCode（下拉）+ inputsExpression（Groovy 表达式）</li>
		*   <li>CALL_CONNECTOR：connectorCode（下拉）+ inputsExpression（Groovy 表达式）</li>
		*   <li>THROW_EXCEPTION：errorMessage + errorCode</li>
		*   <li>RETURN：expression（Groovy 返回值表达式）</li>
		*   <li>START/END：无参数</li>
		* </ul>
		*
		* <p>表达式字段统一使用 ExpressionEditor（language=groovy，对齐后端 GroovySandboxExecutor），
		* 可绑定变量/字段补全。</p>
		*/
		const props = __props;
		const emit = __emit;
		/** 当前编辑的节点 config 副本（深度响应） */
		const config = reactive({});
		/**
		* 仅在节点 ID 变化（切换选中节点）时重置 config，
		* 避免编辑过程中父组件回写 config 导致的来回重置（丢焦点/丢光标）。
		* 撤销/重做场景由父组件通过 :key 重建本组件来强制刷新。
		*/
		watch(() => {
			var _props$node;
			return (_props$node = props.node) === null || _props$node === void 0 ? void 0 : _props$node.id;
		}, () => {
			for (const k of Object.keys(config)) delete config[k];
			if (props.node) Object.assign(config, JSON.parse(JSON.stringify(props.node.config || {})));
		}, { immediate: true });
		/** 同步 config 到父组件 */
		function syncConfig() {
			if (!props.node) return;
			emit("update:node", {
				...props.node,
				config: JSON.parse(JSON.stringify(config))
			});
		}
		/** 节点标题双向绑定 */
		const label = computed({
			get: () => {
				var _props$node2;
				return ((_props$node2 = props.node) === null || _props$node2 === void 0 ? void 0 : _props$node2.label) || "";
			},
			set: (v) => {
				if (!props.node) return;
				emit("update:node", {
					...props.node,
					label: v
				});
			}
		});
		/** ExpressionEditor 用的 binding items（输入 + 局部变量） */
		const bindingItems = computed(() => (props.variables || []).map((v) => ({
			name: v.name,
			type: v.type
		})));
		/** 用于分支/循环目标节点选择（排除当前节点） */
		const targetOptions = computed(() => (props.nodes || []).filter((n) => {
			var _props$node3;
			return n.id !== ((_props$node3 = props.node) === null || _props$node3 === void 0 ? void 0 : _props$node3.id);
		}).map((n) => ({
			label: `${n.label} (${n.type})`,
			value: n.id
		})));
		const TYPE_LABELS = {
			START: "开始节点",
			END: "结束节点",
			ASSIGN: "赋值节点",
			CONDITION: "条件分支节点",
			LOOP: "循环节点",
			CALL_SERVICE: "调用服务节点",
			CALL_MICROFLOW: "调用子微流节点",
			CALL_RULE: "调用规则节点",
			CALL_CONNECTOR: "调用连接器节点",
			THROW_EXCEPTION: "抛出异常节点",
			RETURN: "返回节点"
		};
		function getTypeLabel(t) {
			return TYPE_LABELS[t] || t;
		}
		return (_ctx, _cache) => {
			const _component_InfoFilled = resolveComponent("InfoFilled");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_form = resolveComponent("el-form");
			return openBlock(), createElementBlock("div", _hoisted_1$5, [!__props.node ? (openBlock(), createElementBlock("div", _hoisted_2$5, [createVNode(_component_el_icon, null, {
				default: withCtx(() => [createVNode(_component_InfoFilled)]),
				_: 1
			}), _cache[21] || (_cache[21] = createElementVNode("div", null, "请选中节点查看/编辑参数", -1))])) : (openBlock(), createElementBlock(Fragment, { key: 1 }, [createElementVNode("div", _hoisted_3$4, [createVNode(_component_el_tag, {
				size: "small",
				type: "info"
			}, {
				default: withCtx(() => [createTextVNode(toDisplayString(__props.node.type), 1)]),
				_: 1
			}), createElementVNode("span", _hoisted_4$3, toDisplayString(getTypeLabel(__props.node.type)), 1)]), createVNode(_component_el_form, {
				"label-width": "90px",
				size: "small",
				class: "param-form"
			}, {
				default: withCtx(() => [createVNode(_component_el_form_item, { label: "节点标题" }, {
					default: withCtx(() => [createVNode(_component_el_input, {
						modelValue: label.value,
						"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => label.value = $event),
						placeholder: "节点显示名称",
						onChange: syncConfig
					}, null, 8, ["modelValue"])]),
					_: 1
				}), __props.node.type === "START" || __props.node.type === "END" ? (openBlock(), createElementBlock("div", _hoisted_5$3, "该节点类型无额外参数配置。")) : __props.node.type === "ASSIGN" ? (openBlock(), createElementBlock(Fragment, { key: 1 }, [createVNode(_component_el_form_item, { label: "目标变量" }, {
					default: withCtx(() => [createVNode(_component_el_input, {
						modelValue: config.target,
						"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => config.target = $event),
						placeholder: "如 result",
						onChange: syncConfig
					}, null, 8, ["modelValue"])]),
					_: 1
				}), createVNode(_component_el_form_item, { label: "表达式" }, {
					default: withCtx(() => [createVNode(ExpressionEditor_default, {
						modelValue: config.expression,
						"onUpdate:modelValue": [_cache[2] || (_cache[2] = ($event) => config.expression = $event), syncConfig],
						language: "groovy",
						variables: bindingItems.value,
						height: 160
					}, null, 8, ["modelValue", "variables"])]),
					_: 1
				})], 64)) : __props.node.type === "CONDITION" ? (openBlock(), createElementBlock(Fragment, { key: 2 }, [
					createVNode(_component_el_form_item, { label: "条件表达式" }, {
						default: withCtx(() => [createVNode(ExpressionEditor_default, {
							modelValue: config.expression,
							"onUpdate:modelValue": [_cache[3] || (_cache[3] = ($event) => config.expression = $event), syncConfig],
							language: "groovy",
							variables: bindingItems.value,
							height: 160
						}, null, 8, ["modelValue", "variables"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "true 分支" }, {
						default: withCtx(() => [createVNode(_component_el_select, {
							modelValue: config.trueBranch,
							"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => config.trueBranch = $event),
							placeholder: "选择 true 跳转节点",
							clearable: "",
							onChange: syncConfig
						}, {
							default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(targetOptions.value, (o) => {
								return openBlock(), createBlock(_component_el_option, {
									key: o.value,
									label: o.label,
									value: o.value
								}, null, 8, ["label", "value"]);
							}), 128))]),
							_: 1
						}, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "false 分支" }, {
						default: withCtx(() => [createVNode(_component_el_select, {
							modelValue: config.falseBranch,
							"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => config.falseBranch = $event),
							placeholder: "选择 false 跳转节点",
							clearable: "",
							onChange: syncConfig
						}, {
							default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(targetOptions.value, (o) => {
								return openBlock(), createBlock(_component_el_option, {
									key: o.value,
									label: o.label,
									value: o.value
								}, null, 8, ["label", "value"]);
							}), 128))]),
							_: 1
						}, 8, ["modelValue"])]),
						_: 1
					})
				], 64)) : __props.node.type === "LOOP" ? (openBlock(), createElementBlock(Fragment, { key: 3 }, [createVNode(_component_el_form_item, { label: "迭代表达式" }, {
					default: withCtx(() => [createVNode(ExpressionEditor_default, {
						modelValue: config.iterableExpression,
						"onUpdate:modelValue": [_cache[6] || (_cache[6] = ($event) => config.iterableExpression = $event), syncConfig],
						language: "groovy",
						variables: bindingItems.value,
						height: 120
					}, null, 8, ["modelValue", "variables"])]),
					_: 1
				}), createVNode(_component_el_form_item, { label: "循环体起点" }, {
					default: withCtx(() => [createVNode(_component_el_select, {
						modelValue: config.bodyNodeId,
						"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => config.bodyNodeId = $event),
						placeholder: "选择循环体起始节点",
						clearable: "",
						onChange: syncConfig
					}, {
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(targetOptions.value, (o) => {
							return openBlock(), createBlock(_component_el_option, {
								key: o.value,
								label: o.label,
								value: o.value
							}, null, 8, ["label", "value"]);
						}), 128))]),
						_: 1
					}, 8, ["modelValue"])]),
					_: 1
				})], 64)) : __props.node.type === "CALL_SERVICE" ? (openBlock(), createElementBlock(Fragment, { key: 4 }, [
					createVNode(_component_el_form_item, { label: "Bean 名称" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: config.beanName,
							"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => config.beanName = $event),
							placeholder: "如 userService",
							onChange: syncConfig
						}, null, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "方法名" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: config.methodName,
							"onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => config.methodName = $event),
							placeholder: "如 getById",
							onChange: syncConfig
						}, null, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "结果变量" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: config.target,
							"onUpdate:modelValue": _cache[10] || (_cache[10] = ($event) => config.target = $event),
							placeholder: "可选，结果写入该变量",
							onChange: syncConfig
						}, null, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "参数表达式" }, {
						default: withCtx(() => [createVNode(ExpressionEditor_default, {
							modelValue: config.args,
							"onUpdate:modelValue": [_cache[11] || (_cache[11] = ($event) => config.args = $event), syncConfig],
							language: "groovy",
							variables: bindingItems.value,
							height: 120
						}, null, 8, ["modelValue", "variables"])]),
						_: 1
					})
				], 64)) : __props.node.type === "CALL_MICROFLOW" ? (openBlock(), createElementBlock(Fragment, { key: 5 }, [createVNode(_component_el_form_item, { label: "目标微流" }, {
					default: withCtx(() => [createVNode(_component_el_select, {
						modelValue: config.microflowCode,
						"onUpdate:modelValue": _cache[12] || (_cache[12] = ($event) => config.microflowCode = $event),
						placeholder: "选择子微流",
						filterable: "",
						onChange: syncConfig
					}, {
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(__props.microflowOptions, (m) => {
							return openBlock(), createBlock(_component_el_option, {
								key: m.code,
								label: `${m.name} (${m.code})`,
								value: m.code
							}, null, 8, ["label", "value"]);
						}), 128))]),
						_: 1
					}, 8, ["modelValue"])]),
					_: 1
				}), createVNode(_component_el_form_item, { label: "输入表达式" }, {
					default: withCtx(() => [createVNode(ExpressionEditor_default, {
						modelValue: config.inputsExpression,
						"onUpdate:modelValue": [_cache[13] || (_cache[13] = ($event) => config.inputsExpression = $event), syncConfig],
						language: "groovy",
						variables: bindingItems.value,
						height: 120
					}, null, 8, ["modelValue", "variables"])]),
					_: 1
				})], 64)) : __props.node.type === "CALL_RULE" ? (openBlock(), createElementBlock(Fragment, { key: 6 }, [createVNode(_component_el_form_item, { label: "目标规则" }, {
					default: withCtx(() => [createVNode(_component_el_select, {
						modelValue: config.ruleCode,
						"onUpdate:modelValue": _cache[14] || (_cache[14] = ($event) => config.ruleCode = $event),
						placeholder: "选择规则",
						filterable: "",
						onChange: syncConfig
					}, {
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(__props.ruleOptions, (r) => {
							return openBlock(), createBlock(_component_el_option, {
								key: r.code,
								label: `${r.name} (${r.code})`,
								value: r.code
							}, null, 8, ["label", "value"]);
						}), 128))]),
						_: 1
					}, 8, ["modelValue"])]),
					_: 1
				}), createVNode(_component_el_form_item, { label: "输入表达式" }, {
					default: withCtx(() => [createVNode(ExpressionEditor_default, {
						modelValue: config.inputsExpression,
						"onUpdate:modelValue": [_cache[15] || (_cache[15] = ($event) => config.inputsExpression = $event), syncConfig],
						language: "groovy",
						variables: bindingItems.value,
						height: 120
					}, null, 8, ["modelValue", "variables"])]),
					_: 1
				})], 64)) : __props.node.type === "CALL_CONNECTOR" ? (openBlock(), createElementBlock(Fragment, { key: 7 }, [createVNode(_component_el_form_item, { label: "目标连接器" }, {
					default: withCtx(() => [createVNode(_component_el_select, {
						modelValue: config.connectorCode,
						"onUpdate:modelValue": _cache[16] || (_cache[16] = ($event) => config.connectorCode = $event),
						placeholder: "选择连接器",
						filterable: "",
						onChange: syncConfig
					}, {
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(__props.connectorOptions, (c) => {
							return openBlock(), createBlock(_component_el_option, {
								key: c.code,
								label: `${c.name} (${c.code})`,
								value: c.code
							}, null, 8, ["label", "value"]);
						}), 128))]),
						_: 1
					}, 8, ["modelValue"])]),
					_: 1
				}), createVNode(_component_el_form_item, { label: "输入表达式" }, {
					default: withCtx(() => [createVNode(ExpressionEditor_default, {
						modelValue: config.inputsExpression,
						"onUpdate:modelValue": [_cache[17] || (_cache[17] = ($event) => config.inputsExpression = $event), syncConfig],
						language: "groovy",
						variables: bindingItems.value,
						height: 120
					}, null, 8, ["modelValue", "variables"])]),
					_: 1
				})], 64)) : __props.node.type === "THROW_EXCEPTION" ? (openBlock(), createElementBlock(Fragment, { key: 8 }, [createVNode(_component_el_form_item, { label: "错误消息" }, {
					default: withCtx(() => [createVNode(_component_el_input, {
						modelValue: config.errorMessage,
						"onUpdate:modelValue": _cache[18] || (_cache[18] = ($event) => config.errorMessage = $event),
						placeholder: "如 订单不存在",
						onChange: syncConfig
					}, null, 8, ["modelValue"])]),
					_: 1
				}), createVNode(_component_el_form_item, { label: "错误码" }, {
					default: withCtx(() => [createVNode(_component_el_input, {
						modelValue: config.errorCode,
						"onUpdate:modelValue": _cache[19] || (_cache[19] = ($event) => config.errorCode = $event),
						placeholder: "如 ORDER_NOT_FOUND",
						onChange: syncConfig
					}, null, 8, ["modelValue"])]),
					_: 1
				})], 64)) : __props.node.type === "RETURN" ? (openBlock(), createBlock(_component_el_form_item, {
					key: 9,
					label: "返回值表达式"
				}, {
					default: withCtx(() => [createVNode(ExpressionEditor_default, {
						modelValue: config.expression,
						"onUpdate:modelValue": [_cache[20] || (_cache[20] = ($event) => config.expression = $event), syncConfig],
						language: "groovy",
						variables: bindingItems.value,
						height: 120
					}, null, 8, ["modelValue", "variables"])]),
					_: 1
				})) : createCommentVNode("", true)]),
				_: 1
			})], 64))]);
		};
	}
}), [["__scopeId", "data-v-271b6ad2"]]);
//#endregion
//#region src/components/MicroflowDesigner/MicroflowMetaPanel.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$4 = { class: "microflow-meta-panel" };
var _hoisted_2$4 = { class: "panel-header" };
var _hoisted_3$3 = {
	key: 1,
	class: "empty-state"
};
//#endregion
//#region src/components/MicroflowDesigner/MicroflowMetaPanel.vue
var MicroflowMetaPanel_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "MicroflowMetaPanel",
	__name: "MicroflowMetaPanel",
	props: { microflow: {} },
	emits: ["update:microflow"],
	setup(__props, { emit: __emit }) {
		/**
		* 微流元信息面板（右栏，未选中节点时显示，借鉴 Mendix Microflows 属性）。
		*
		* <p>展示并编辑微流基础信息：编码 / 名称 / 描述 / 状态。
		* 通过 v-model:microflow 与父组件双向绑定；下方由父组件挂载变量面板
		* （VariablePanel）管理输入参数与局部变量。</p>
		*/
		const props = __props;
		const emit = __emit;
		/** 各字段双向绑定代理：读取 props，写入时整体 emit 回父组件 */
		const code = computed({
			get: () => {
				var _props$microflow;
				return ((_props$microflow = props.microflow) === null || _props$microflow === void 0 ? void 0 : _props$microflow.code) || "";
			},
			set: (v) => props.microflow && emit("update:microflow", {
				...props.microflow,
				code: v
			})
		});
		const name = computed({
			get: () => {
				var _props$microflow2;
				return ((_props$microflow2 = props.microflow) === null || _props$microflow2 === void 0 ? void 0 : _props$microflow2.name) || "";
			},
			set: (v) => props.microflow && emit("update:microflow", {
				...props.microflow,
				name: v
			})
		});
		const description = computed({
			get: () => {
				var _props$microflow3;
				return ((_props$microflow3 = props.microflow) === null || _props$microflow3 === void 0 ? void 0 : _props$microflow3.description) || "";
			},
			set: (v) => props.microflow && emit("update:microflow", {
				...props.microflow,
				description: v
			})
		});
		const status = computed(() => {
			var _props$microflow4;
			return ((_props$microflow4 = props.microflow) === null || _props$microflow4 === void 0 ? void 0 : _props$microflow4.status) || "DRAFT";
		});
		return (_ctx, _cache) => {
			const _component_Document = resolveComponent("Document");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_form = resolveComponent("el-form");
			const _component_InfoFilled = resolveComponent("InfoFilled");
			return openBlock(), createElementBlock("div", _hoisted_1$4, [createElementVNode("div", _hoisted_2$4, [createVNode(_component_el_icon, null, {
				default: withCtx(() => [createVNode(_component_Document)]),
				_: 1
			}), _cache[3] || (_cache[3] = createElementVNode("span", null, "微流信息", -1))]), __props.microflow ? (openBlock(), createBlock(_component_el_form, {
				key: 0,
				"label-width": "72px",
				size: "small",
				class: "meta-form"
			}, {
				default: withCtx(() => [
					createVNode(_component_el_form_item, { label: "编码" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: code.value,
							"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => code.value = $event),
							placeholder: "如 order_approval"
						}, null, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "名称" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: name.value,
							"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => name.value = $event),
							placeholder: "微流显示名称"
						}, null, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "描述" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: description.value,
							"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => description.value = $event),
							type: "textarea",
							rows: 2,
							placeholder: "微流用途说明"
						}, null, 8, ["modelValue"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, { label: "状态" }, {
						default: withCtx(() => [createVNode(_component_el_tag, { type: status.value === "PUBLISHED" ? "success" : "info" }, {
							default: withCtx(() => [createTextVNode(toDisplayString(status.value), 1)]),
							_: 1
						}, 8, ["type"])]),
						_: 1
					})
				]),
				_: 1
			})) : (openBlock(), createElementBlock("div", _hoisted_3$3, [createVNode(_component_el_icon, null, {
				default: withCtx(() => [createVNode(_component_InfoFilled)]),
				_: 1
			}), _cache[4] || (_cache[4] = createElementVNode("div", null, "请选择或新建微流", -1))]))]);
		};
	}
}), [["__scopeId", "data-v-90b435b2"]]);
//#endregion
//#region src/components/MicroflowDesigner/VariablePanel.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$3 = { class: "variable-panel" };
var _hoisted_2$3 = { class: "section" };
var _hoisted_3$2 = { class: "section-header" };
var _hoisted_4$2 = {
	key: 0,
	class: "empty-tip"
};
var _hoisted_5$2 = { class: "section" };
var _hoisted_6$2 = { class: "section-header" };
var _hoisted_7$2 = {
	key: 0,
	class: "empty-tip"
};
var _hoisted_8$2 = { class: "section" };
//#endregion
//#region src/components/MicroflowDesigner/VariablePanel.vue
var VariablePanel_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "MicroflowVariablePanel",
	__name: "VariablePanel",
	props: { variables: {} },
	emits: ["update:variables"],
	setup(__props, { emit: __emit }) {
		/**
		* 微流变量面板（右侧 Tab，借鉴 Mendix Microflows Parameters）。
		*
		* <p>管理三类变量：</p>
		* <ul>
		*   <li>输入参数（inputs）：调用方传入</li>
		*   <li>局部变量（locals）：微流内部声明</li>
		*   <li>返回值类型（returnType）：微流出口类型</li>
		* </ul>
		*
		* <p>通过 v-model:variables 双向绑定；变量类型可选 STRING/INTEGER/LONG/DECIMAL/
		* BOOLEAN/DATE/DATETIME/OBJECT/LIST/Map 等。</p>
		*/
		const props = __props;
		const emit = __emit;
		const VARIABLE_TYPES = [
			"String",
			"Integer",
			"Long",
			"Double",
			"Boolean",
			"Date",
			"DateTime",
			"Object",
			"List",
			"Map",
			"BigDecimal"
		];
		const localInputs = computed({
			get: () => props.variables.inputs || [],
			set: (val) => emit("update:variables", {
				...props.variables,
				inputs: val
			})
		});
		const localLocals = computed({
			get: () => props.variables.locals || [],
			set: (val) => emit("update:variables", {
				...props.variables,
				locals: val
			})
		});
		const localReturnType = computed({
			get: () => props.variables.returnType || "Object",
			set: (val) => emit("update:variables", {
				...props.variables,
				returnType: val
			})
		});
		function addInput() {
			localInputs.value = [...localInputs.value, {
				name: `input${localInputs.value.length + 1}`,
				type: "String"
			}];
		}
		function removeInput(idx) {
			const arr = [...localInputs.value];
			arr.splice(idx, 1);
			localInputs.value = arr;
		}
		function addLocal() {
			localLocals.value = [...localLocals.value, {
				name: `var${localLocals.value.length + 1}`,
				type: "Object"
			}];
		}
		function removeLocal(idx) {
			const arr = [...localLocals.value];
			arr.splice(idx, 1);
			localLocals.value = arr;
		}
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			return openBlock(), createElementBlock("div", _hoisted_1$3, [
				createElementVNode("div", _hoisted_2$3, [
					createElementVNode("div", _hoisted_3$2, [_cache[2] || (_cache[2] = createElementVNode("span", null, "输入参数", -1)), createVNode(_component_el_button, {
						size: "small",
						type: "primary",
						link: "",
						onClick: addInput
					}, {
						default: withCtx(() => [..._cache[1] || (_cache[1] = [createTextVNode("+ 添加", -1)])]),
						_: 1
					})]),
					(openBlock(true), createElementBlock(Fragment, null, renderList(localInputs.value, (item, idx) => {
						return openBlock(), createElementBlock("div", {
							key: `in-${idx}`,
							class: "var-row"
						}, [
							createVNode(_component_el_input, {
								modelValue: item.name,
								"onUpdate:modelValue": ($event) => item.name = $event,
								size: "small",
								placeholder: "参数名"
							}, null, 8, ["modelValue", "onUpdate:modelValue"]),
							createVNode(_component_el_select, {
								modelValue: item.type,
								"onUpdate:modelValue": ($event) => item.type = $event,
								size: "small",
								placeholder: "类型"
							}, {
								default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(VARIABLE_TYPES, (t) => {
									return createVNode(_component_el_option, {
										key: t,
										label: t,
										value: t
									}, null, 8, ["label", "value"]);
								}), 64))]),
								_: 1
							}, 8, ["modelValue", "onUpdate:modelValue"]),
							createVNode(_component_el_button, {
								size: "small",
								type: "danger",
								link: "",
								onClick: ($event) => removeInput(idx)
							}, {
								default: withCtx(() => [..._cache[3] || (_cache[3] = [createTextVNode("删除", -1)])]),
								_: 1
							}, 8, ["onClick"])
						]);
					}), 128)),
					localInputs.value.length === 0 ? (openBlock(), createElementBlock("div", _hoisted_4$2, "暂无输入参数")) : createCommentVNode("", true)
				]),
				createElementVNode("div", _hoisted_5$2, [
					createElementVNode("div", _hoisted_6$2, [_cache[5] || (_cache[5] = createElementVNode("span", null, "局部变量", -1)), createVNode(_component_el_button, {
						size: "small",
						type: "primary",
						link: "",
						onClick: addLocal
					}, {
						default: withCtx(() => [..._cache[4] || (_cache[4] = [createTextVNode("+ 添加", -1)])]),
						_: 1
					})]),
					(openBlock(true), createElementBlock(Fragment, null, renderList(localLocals.value, (item, idx) => {
						return openBlock(), createElementBlock("div", {
							key: `loc-${idx}`,
							class: "var-row"
						}, [
							createVNode(_component_el_input, {
								modelValue: item.name,
								"onUpdate:modelValue": ($event) => item.name = $event,
								size: "small",
								placeholder: "变量名"
							}, null, 8, ["modelValue", "onUpdate:modelValue"]),
							createVNode(_component_el_select, {
								modelValue: item.type,
								"onUpdate:modelValue": ($event) => item.type = $event,
								size: "small",
								placeholder: "类型"
							}, {
								default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(VARIABLE_TYPES, (t) => {
									return createVNode(_component_el_option, {
										key: t,
										label: t,
										value: t
									}, null, 8, ["label", "value"]);
								}), 64))]),
								_: 1
							}, 8, ["modelValue", "onUpdate:modelValue"]),
							createVNode(_component_el_button, {
								size: "small",
								type: "danger",
								link: "",
								onClick: ($event) => removeLocal(idx)
							}, {
								default: withCtx(() => [..._cache[6] || (_cache[6] = [createTextVNode("删除", -1)])]),
								_: 1
							}, 8, ["onClick"])
						]);
					}), 128)),
					localLocals.value.length === 0 ? (openBlock(), createElementBlock("div", _hoisted_7$2, "暂无局部变量")) : createCommentVNode("", true)
				]),
				createElementVNode("div", _hoisted_8$2, [_cache[7] || (_cache[7] = createElementVNode("div", { class: "section-header" }, [createElementVNode("span", null, "返回值类型")], -1)), createVNode(_component_el_select, {
					modelValue: localReturnType.value,
					"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => localReturnType.value = $event),
					size: "small",
					placeholder: "返回类型",
					style: { "width": "100%" }
				}, {
					default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(VARIABLE_TYPES, (t) => {
						return createVNode(_component_el_option, {
							key: t,
							label: t,
							value: t
						}, null, 8, ["label", "value"]);
					}), 64))]),
					_: 1
				}, 8, ["modelValue"])])
			]);
		};
	}
}), [["__scopeId", "data-v-4763c36c"]]);
//#endregion
//#region src/components/MicroflowDesigner/ExecutionLogPanel.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$2 = { class: "log-title" };
var _hoisted_2$2 = {
	key: 0,
	class: "log-count"
};
var _hoisted_3$1 = { class: "log-body" };
var _hoisted_4$1 = { class: "log-toolbar" };
var _hoisted_5$1 = {
	key: 0,
	class: "empty-tip"
};
var _hoisted_6$1 = ["onClick"];
var _hoisted_7$1 = { class: "log-item-header" };
var _hoisted_8$1 = { class: "node-name" };
var _hoisted_9$1 = {
	key: 0,
	class: "duration"
};
var _hoisted_10$1 = {
	key: 0,
	class: "log-detail"
};
var _hoisted_11$1 = {
	key: 0,
	class: "detail-block error-block"
};
var _hoisted_12$1 = {
	key: 1,
	class: "detail-block"
};
var _hoisted_13$1 = {
	key: 2,
	class: "detail-block"
};
var _hoisted_14 = {
	key: 3,
	class: "detail-block"
};
//#endregion
//#region src/components/MicroflowDesigner/ExecutionLogPanel.vue
var ExecutionLogPanel_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "MicroflowExecutionLogPanel",
	__name: "ExecutionLogPanel",
	props: {
		microflowId: {},
		executionId: {},
		collapsed: { type: Boolean }
	},
	emits: [
		"update:collapsed",
		"highlight-node",
		"logs-loaded"
	],
	setup(__props, { emit: __emit }) {
		/**
		* 微流执行日志可视化面板（借鉴 Joget APM）。
		*
		* <p>展示某次执行的节点级轨迹时间轴：</p>
		* <ul>
		*   <li>顶部下拉：选择历史 executionId（多次执行历史）</li>
		*   <li>时间轴：节点名 + 类型图标 + 开始时间 + 耗时(ms) + 状态徽章</li>
		*   <li>点击节点条目展开详情：inputs / outputs / variablesSnapshot / errorMessage</li>
		* </ul>
		*
		* <p>状态颜色：SUCCESS=绿、FAILED=红、RUNNING=蓝。面板可折叠/展开。</p>
		*/
		const props = __props;
		const emit = __emit;
		const innerCollapsed = ref(props.collapsed || false);
		watch(() => props.collapsed, (v) => {
			innerCollapsed.value = v;
		});
		function toggleCollapse() {
			innerCollapsed.value = !innerCollapsed.value;
			emit("update:collapsed", innerCollapsed.value);
		}
		const recentExecutions = ref([]);
		const currentExecutionId = ref(props.executionId || "");
		const logs = ref([]);
		const loading = ref(false);
		/** 展开详情的 nodeId 集合 */
		const expandedNodeIds = ref(/* @__PURE__ */ new Set());
		watch(() => props.executionId, (v) => {
			if (v && v !== currentExecutionId.value) {
				currentExecutionId.value = v;
				loadLogs(v);
			}
		});
		watch(() => props.microflowId, async (id) => {
			if (id) await loadRecentExecutions(id);
		}, { immediate: true });
		async function loadRecentExecutions(microflowId) {
			try {
				const list = await getRecentExecutionLogs(microflowId, 20);
				const seen = /* @__PURE__ */ new Set();
				recentExecutions.value = (list || []).filter((l) => {
					if (seen.has(l.executionId)) return false;
					seen.add(l.executionId);
					return true;
				});
			} catch (_unused) {
				recentExecutions.value = [];
			}
		}
		async function loadLogs(executionId) {
			if (!executionId) {
				logs.value = [];
				emit("logs-loaded", logs.value);
				return;
			}
			loading.value = true;
			try {
				logs.value = await getExecutionLogs(executionId) || [];
			} catch (e) {
				ElMessage.error("加载执行日志失败");
				logs.value = [];
			} finally {
				loading.value = false;
				emit("logs-loaded", logs.value);
			}
		}
		async function onExecutionChange(v) {
			currentExecutionId.value = v;
			await loadLogs(v);
		}
		function toggleExpand(nodeId) {
			const s = new Set(expandedNodeIds.value);
			if (s.has(nodeId)) s.delete(nodeId);
			else s.add(nodeId);
			expandedNodeIds.value = s;
		}
		/** 点击日志条目：展开/收起详情，并通知父组件在画布上高亮对应节点 */
		function onLogItemClick(log) {
			toggleExpand(log.nodeId);
			emit("highlight-node", log.nodeId);
		}
		function statusColor(status) {
			if (status === "SUCCESS") return "#67c23a";
			if (status === "FAILED") return "#f56c6c";
			return "#409eff";
		}
		function statusTagType(status) {
			if (status === "SUCCESS") return "success";
			if (status === "FAILED") return "danger";
			if (status === "RUNNING") return "primary";
			return "info";
		}
		function prettyJson(s) {
			if (!s) return "";
			try {
				return JSON.stringify(JSON.parse(s), null, 2);
			} catch (_unused2) {
				return s;
			}
		}
		function formatTime(s) {
			if (!s) return "";
			try {
				return new Date(s).toLocaleString("zh-CN", { hour12: false });
			} catch (_unused3) {
				return s;
			}
		}
		async function refresh() {
			if (props.microflowId) await loadRecentExecutions(props.microflowId);
			if (currentExecutionId.value) await loadLogs(currentExecutionId.value);
		}
		return (_ctx, _cache) => {
			const _component_DataLine = resolveComponent("DataLine");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_ArrowDown = resolveComponent("ArrowDown");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_Refresh = resolveComponent("Refresh");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_ArrowRight = resolveComponent("ArrowRight");
			const _component_el_timeline_item = resolveComponent("el-timeline-item");
			const _component_el_timeline = resolveComponent("el-timeline");
			const _component_el_scrollbar = resolveComponent("el-scrollbar");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", { class: normalizeClass(["execution-log-panel", { collapsed: innerCollapsed.value }]) }, [createElementVNode("div", {
				class: "log-header",
				onClick: toggleCollapse
			}, [
				createElementVNode("span", _hoisted_1$2, [createVNode(_component_el_icon, null, {
					default: withCtx(() => [createVNode(_component_DataLine)]),
					_: 1
				}), _cache[1] || (_cache[1] = createTextVNode(" 执行日志 ", -1))]),
				!innerCollapsed.value && currentExecutionId.value ? (openBlock(), createElementBlock("span", _hoisted_2$2, toDisplayString(logs.value.length) + " 节点 ", 1)) : createCommentVNode("", true),
				createVNode(_component_el_icon, { class: normalizeClass(["collapse-icon", { rotated: innerCollapsed.value }]) }, {
					default: withCtx(() => [createVNode(_component_ArrowDown)]),
					_: 1
				}, 8, ["class"])
			]), withDirectives((openBlock(), createElementBlock("div", _hoisted_3$1, [createElementVNode("div", _hoisted_4$1, [createVNode(_component_el_select, {
				modelValue: currentExecutionId.value,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => currentExecutionId.value = $event),
				size: "small",
				placeholder: "选择执行 ID",
				filterable: "",
				style: { "flex": "1" },
				onChange: onExecutionChange
			}, {
				default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(recentExecutions.value, (e) => {
					return openBlock(), createBlock(_component_el_option, {
						key: e.executionId,
						label: `${e.executionId.substring(0, 8)} (${formatTime(e.startTime)})`,
						value: e.executionId
					}, null, 8, ["label", "value"]);
				}), 128))]),
				_: 1
			}, 8, ["modelValue"]), createVNode(_component_el_button, {
				size: "small",
				link: "",
				onClick: withModifiers(refresh, ["stop"])
			}, {
				default: withCtx(() => [createVNode(_component_el_icon, null, {
					default: withCtx(() => [createVNode(_component_Refresh)]),
					_: 1
				})]),
				_: 1
			})]), logs.value.length === 0 ? (openBlock(), createElementBlock("div", _hoisted_5$1, "暂无执行日志")) : (openBlock(), createBlock(_component_el_scrollbar, {
				key: 1,
				class: "log-scroll"
			}, {
				default: withCtx(() => [createVNode(_component_el_timeline, null, {
					default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(logs.value, (log) => {
						return openBlock(), createBlock(_component_el_timeline_item, {
							key: log.nodeId,
							color: statusColor(log.status),
							timestamp: formatTime(log.startTime),
							placement: "top"
						}, {
							default: withCtx(() => [createElementVNode("div", {
								class: normalizeClass(["log-item", `status-${log.status.toLowerCase()}`]),
								onClick: ($event) => onLogItemClick(log)
							}, [createElementVNode("div", _hoisted_7$1, [
								createElementVNode("span", _hoisted_8$1, toDisplayString(log.nodeId), 1),
								createVNode(_component_el_tag, {
									size: "small",
									type: statusTagType(log.status)
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(log.nodeType), 1)]),
									_: 2
								}, 1032, ["type"]),
								createVNode(_component_el_tag, {
									size: "small",
									type: statusTagType(log.status),
									effect: "dark"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(log.status), 1)]),
									_: 2
								}, 1032, ["type"]),
								log.durationMs !== void 0 && log.durationMs !== null ? (openBlock(), createElementBlock("span", _hoisted_9$1, toDisplayString(log.durationMs) + " ms ", 1)) : createCommentVNode("", true),
								createVNode(_component_el_icon, { class: normalizeClass(["expand-icon", { rotated: expandedNodeIds.value.has(log.nodeId) }]) }, {
									default: withCtx(() => [createVNode(_component_ArrowRight)]),
									_: 1
								}, 8, ["class"])
							]), expandedNodeIds.value.has(log.nodeId) ? (openBlock(), createElementBlock("div", _hoisted_10$1, [
								log.errorMessage ? (openBlock(), createElementBlock("div", _hoisted_11$1, [_cache[2] || (_cache[2] = createElementVNode("div", { class: "detail-label" }, "错误信息", -1)), createElementVNode("pre", null, toDisplayString(log.errorMessage), 1)])) : createCommentVNode("", true),
								log.inputs ? (openBlock(), createElementBlock("div", _hoisted_12$1, [_cache[3] || (_cache[3] = createElementVNode("div", { class: "detail-label" }, "输入", -1)), createElementVNode("pre", null, toDisplayString(prettyJson(log.inputs)), 1)])) : createCommentVNode("", true),
								log.outputs ? (openBlock(), createElementBlock("div", _hoisted_13$1, [_cache[4] || (_cache[4] = createElementVNode("div", { class: "detail-label" }, "输出", -1)), createElementVNode("pre", null, toDisplayString(prettyJson(log.outputs)), 1)])) : createCommentVNode("", true),
								log.variablesSnapshot ? (openBlock(), createElementBlock("div", _hoisted_14, [_cache[5] || (_cache[5] = createElementVNode("div", { class: "detail-label" }, "变量快照", -1)), createElementVNode("pre", null, toDisplayString(prettyJson(log.variablesSnapshot)), 1)])) : createCommentVNode("", true)
							])) : createCommentVNode("", true)], 10, _hoisted_6$1)]),
							_: 2
						}, 1032, ["color", "timestamp"]);
					}), 128))]),
					_: 1
				})]),
				_: 1
			}))])), [[vShow, !innerCollapsed.value], [_directive_loading, loading.value]])], 2);
		};
	}
}), [["__scopeId", "data-v-f88cef5a"]]);
//#endregion
//#region src/components/MicroflowDesigner/MicroflowNode.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$1 = {
	key: 0,
	class: "mn-breakpoint",
	title: "断点"
};
var _hoisted_2$1 = { class: "mn-label" };
//#endregion
//#region src/components/MicroflowDesigner/MicroflowNode.vue
var MicroflowNode_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "MicroflowNode",
	props: {
		node: {},
		graph: {}
	},
	setup(__props) {
		/**
		* 微流节点（X6 自定义 Vue 节点，通过 @antv/x6-vue-shape 注册）。
		*
		* <p>由 x6-vue-shape 以 props.node（X6 Node 实例）注入；节点数据从 node.getData() 读取，
		* 监听 change:data 以响应选中态、执行状态（RUNNING/SUCCESS/FAILED）与日志高亮变更。</p>
		*
		* <p>渲染：类型图标 + 节点名称 + 状态指示点。边框颜色随执行状态变化，
		* 便于执行后在画布上直观看到每个节点的执行结果。</p>
		*/
		const props = __props;
		/** 响应式读取节点数据（监听 change:data 以响应 setData 触发的状态/高亮变更） */
		const nodeData = ref(props.node.getData() || {});
		const onDataChange = () => {
			nodeData.value = props.node.getData() || {};
		};
		props.node.on("change:data", onDataChange);
		onBeforeUnmount(() => {
			props.node.off("change:data", onDataChange);
		});
		/** 节点类型元信息（图标 + 主题色） */
		const META = {
			START: {
				icon: "▶",
				color: "#67c23a"
			},
			END: {
				icon: "■",
				color: "#f56c6c"
			},
			ASSIGN: {
				icon: "=",
				color: "#409eff"
			},
			CONDITION: {
				icon: "?",
				color: "#e6a23c"
			},
			LOOP: {
				icon: "↻",
				color: "#9c27b0"
			},
			CALL_SERVICE: {
				icon: "⚙",
				color: "#00bcd4"
			},
			CALL_MICROFLOW: {
				icon: "✦",
				color: "#00bcd4"
			},
			CALL_RULE: {
				icon: "§",
				color: "#00bcd4"
			},
			CALL_CONNECTOR: {
				icon: "⇄",
				color: "#00bcd4"
			},
			THROW_EXCEPTION: {
				icon: "!",
				color: "#ff9800"
			},
			RETURN: {
				icon: "←",
				color: "#67c23a"
			}
		};
		const meta = computed(() => META[nodeData.value.type] || {
			icon: "•",
			color: "#909399"
		});
		/** 边框色：调试当前节点优先，其次执行状态，未执行节点使用默认灰色边框 */
		const borderColor = computed(() => {
			if (nodeData.value.debugCurrent) return "#9c27b0";
			const s = nodeData.value.status;
			if (s === "SUCCESS") return "#67c23a";
			if (s === "FAILED") return "#f56c6c";
			if (s === "RUNNING") return "#409eff";
			return "#c0c4cc";
		});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", {
				class: normalizeClass(["microflow-node", {
					highlighted: nodeData.value.highlighted,
					"is-running": nodeData.value.status === "RUNNING",
					"is-debug-current": nodeData.value.debugCurrent
				}]),
				style: normalizeStyle({ borderColor: borderColor.value })
			}, [
				nodeData.value.breakpoint ? (openBlock(), createElementBlock("span", _hoisted_1$1)) : createCommentVNode("", true),
				createElementVNode("span", {
					class: "mn-icon",
					style: normalizeStyle({ background: meta.value.color })
				}, toDisplayString(meta.value.icon), 5),
				createElementVNode("span", _hoisted_2$1, toDisplayString(nodeData.value.label || nodeData.value.type), 1),
				nodeData.value.status ? (openBlock(), createElementBlock("span", {
					key: 1,
					class: normalizeClass(["mn-status", `st-${nodeData.value.status.toLowerCase()}`])
				}, null, 2)) : createCommentVNode("", true)
			], 6);
		};
	}
}), [["__scopeId", "data-v-596e5eee"]]);
//#endregion
//#region src/views/lowcode/microflow-designer/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "microflow-designer" };
var _hoisted_2 = { class: "top-bar" };
var _hoisted_3 = { class: "main" };
var _hoisted_4 = { class: "canvas-panel" };
var _hoisted_5 = { class: "right-panel" };
var _hoisted_6 = { class: "right-section" };
var _hoisted_7 = { class: "debug-panel" };
var _hoisted_8 = { class: "debug-section" };
var _hoisted_9 = {
	key: 0,
	class: "debug-section"
};
var _hoisted_10 = { class: "debug-value" };
var _hoisted_11 = { class: "debug-section" };
var _hoisted_12 = {
	key: 1,
	class: "debug-section"
};
var _hoisted_13 = { class: "debug-result" };
var MICROFLOW_NODE_SHAPE = "microflow-node";
/** 节点尺寸 */
var NODE_WIDTH = 168;
var NODE_HEIGHT = 48;
//#endregion
//#region src/views/lowcode/microflow-designer/index.vue
var microflow_designer_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "MicroflowDesignerView",
	__name: "index",
	setup(__props) {
		/**
		* 低代码微流设计器（三栏 DAG 可视化画布，借鉴 Mendix Microflows）。
		*
		* <p>布局：</p>
		* <ul>
		*   <li>顶栏：微流选择 / 新建 / 保存 / 执行 / 清空 / 自动布局 / 缩放适配 / 删除选中</li>
		*   <li>左栏 NodePalette：11 种节点类型按分组拖拽到画布</li>
		*   <li>中栏 X6 画布：节点 + 连线，点击节点配置参数，点击空白编辑微流元信息</li>
		*   <li>右栏：选中节点 → NodeParamPanel；未选中 → MicroflowMetaPanel + VariablePanel</li>
		*   <li>底部 ExecutionLogPanel：执行轨迹时间轴，点击日志项高亮画布节点</li>
		* </ul>
		*
		* <p>definition JSON 结构与后端 MicroflowEngine 对齐：{nodes:[{id,type,config,x,y}],edges:[{source,target}]}。
		* 节点 config 字段名与各 MicroflowNodeExecutor 对齐（见 NodeParamPanel）。
		* 执行结果不含 executionId，故执行后通过 getRecentExecutionLogs 取最新轨迹。</p>
		*/
		/** 自定义节点 shape 名（通过 x6-vue-shape 注册） */
		const microflowList = ref([]);
		const selectedMicroflowId = ref(void 0);
		const currentMicroflow = ref(null);
		const definition = ref(emptyDefinition());
		const selectedNodeId = ref(null);
		/** 当前选中的画布元素 ID（节点或边，用于 Delete 删除） */
		const selectedCellId = ref(null);
		const latestExecutionId = ref(void 0);
		const logCollapsed = ref(false);
		/** 下拉数据（CALL_MICROFLOW / CALL_RULE / CALL_CONNECTOR 用） */
		const ruleOptions = ref([]);
		const connectorOptions = ref([]);
		/** 执行输入对话框 */
		const execDialogVisible = ref(false);
		const execInputs = ref("");
		/** 对话框模式：execute（执行）/ debug（调试） */
		const dialogMode = ref("execute");
		/** 断点节点 ID 集合 */
		const breakpoints = ref(/* @__PURE__ */ new Set());
		/** 是否处于调试会话中 */
		const isDebugging = ref(false);
		/** 调试会话 ID */
		const debugSessionId = ref(null);
		/** 当前变量快照 */
		const debugVariables = ref({});
		/** 当前步骤状态：PAUSED/COMPLETED/FAILED */
		const debugStatus = ref(null);
		/** 当前暂停节点 ID（下一个待执行节点） */
		const debugCurrentNodeId = ref(null);
		/** 调试最终结果 */
		const debugResult = ref(void 0);
		/** 变量监视抽屉可见性 */
		const debugVariablesVisible = ref(false);
		/** 调试操作进行中（禁用按钮防止重复点击） */
		const debugLoading = ref(false);
		const graphRef = shallowRef(null);
		const canvasContainer = ref();
		function emptyDefinition() {
			return {
				nodes: [],
				edges: [],
				variables: {
					inputs: [],
					locals: [],
					returnType: "Object"
				}
			};
		}
		/** 节点类型默认显示名 */
		const DEFAULT_LABELS = {
			START: "开始",
			END: "结束",
			ASSIGN: "赋值",
			CONDITION: "条件",
			LOOP: "循环",
			CALL_SERVICE: "调用服务",
			CALL_MICROFLOW: "调用子微流",
			CALL_RULE: "调用规则",
			CALL_CONNECTOR: "调用连接器",
			THROW_EXCEPTION: "抛出异常",
			RETURN: "返回"
		};
		/** 解析 definition JSON，容错处理缺失字段 */
		function parseDefinition(json) {
			if (!json) return emptyDefinition();
			try {
				const p = JSON.parse(json);
				return {
					nodes: (p.nodes || []).map((n) => {
						var _n$x, _n$y;
						return {
							id: n.id,
							type: n.type,
							label: n.label || DEFAULT_LABELS[n.type] || n.type,
							x: (_n$x = n.x) !== null && _n$x !== void 0 ? _n$x : 0,
							y: (_n$y = n.y) !== null && _n$y !== void 0 ? _n$y : 0,
							config: n.config || {}
						};
					}),
					edges: (p.edges || []).map((e) => ({
						id: e.id || `e_${Date.now()}_${Math.random().toString(36).slice(2, 8)}`,
						source: e.source,
						target: e.target,
						sourcePort: e.sourcePort || "",
						targetPort: e.targetPort || ""
					})),
					variables: p.variables || {
						inputs: [],
						locals: [],
						returnType: "Object"
					}
				};
			} catch (_unused) {
				return emptyDefinition();
			}
		}
		/** 输入端口组（左侧，蓝色圆点） */
		function buildInPortGroup() {
			return {
				position: "left",
				attrs: { circle: {
					r: 4,
					magnet: true,
					stroke: "#409eff",
					fill: "#fff",
					strokeWidth: 1
				} }
			};
		}
		/** 普通输出端口组（右侧，绿色圆点） */
		function buildOutPortGroup() {
			return {
				position: "right",
				attrs: { circle: {
					r: 4,
					magnet: true,
					stroke: "#67c23a",
					fill: "#fff",
					strokeWidth: 1
				} }
			};
		}
		/**
		* 带文字标签的输出端口组（CONDITION 真/假、LOOP 循环/退出）。
		* 通过 markup 同时渲染圆点与文字，使分支语义在画布上可视化。
		*/
		function buildLabeledOutPortGroup(label, color) {
			return {
				position: "right",
				markup: [{
					tagName: "circle",
					selector: "circle"
				}, {
					tagName: "text",
					selector: "text"
				}],
				attrs: {
					circle: {
						r: 5,
						magnet: true,
						stroke: color,
						fill: color,
						strokeWidth: 1
					},
					text: {
						text: label,
						fill: color,
						fontSize: 11,
						refX: 8,
						textAnchor: "start",
						textVerticalAnchor: "middle"
					}
				}
			};
		}
		/**
		* 构造 X6 节点 addNode 配置（含输入/输出端口）。
		*
		* <p>端口语义：</p>
		* <ul>
		*   <li>CONDITION：true（绿）/ false（红）双输出端口，对应 config.trueBranch/falseBranch</li>
		*   <li>LOOP：body 循环体（蓝）/ exit 退出（灰）双输出端口，对应 config.bodyNodeId</li>
		*   <li>其他节点：单 in/out 端口</li>
		* </ul>
		* <p>端口 ID 规范：`${nodeId}-in` / `-true` / `-false` / `-body` / `-exit` / `-out`，
		* 便于 renderGraph 按 sourcePort 推断连线语义。CONDITION/LOOP 的 config 跳转字段仍保留（后端用）。</p>
		*/
		function nodeAddConfig(node) {
			const base = {
				shape: MICROFLOW_NODE_SHAPE,
				id: node.id,
				x: node.x,
				y: node.y,
				width: NODE_WIDTH,
				height: NODE_HEIGHT,
				data: {
					nodeId: node.id,
					type: node.type,
					label: node.label
				}
			};
			if (node.type === "CONDITION") return {
				...base,
				ports: {
					groups: {
						in: buildInPortGroup(),
						outTrue: buildLabeledOutPortGroup("真", "#67c23a"),
						outFalse: buildLabeledOutPortGroup("假", "#f56c6c")
					},
					items: [
						{
							id: `${node.id}-in`,
							group: "in"
						},
						{
							id: `${node.id}-true`,
							group: "outTrue"
						},
						{
							id: `${node.id}-false`,
							group: "outFalse"
						}
					]
				}
			};
			if (node.type === "LOOP") return {
				...base,
				ports: {
					groups: {
						in: buildInPortGroup(),
						outBody: buildLabeledOutPortGroup("循环", "#409eff"),
						outExit: buildLabeledOutPortGroup("退出", "#909399")
					},
					items: [
						{
							id: `${node.id}-in`,
							group: "in"
						},
						{
							id: `${node.id}-body`,
							group: "outBody"
						},
						{
							id: `${node.id}-exit`,
							group: "outExit"
						}
					]
				}
			};
			return {
				...base,
				ports: {
					groups: {
						in: buildInPortGroup(),
						out: buildOutPortGroup()
					},
					items: [{
						id: `${node.id}-in`,
						group: "in"
					}, {
						id: `${node.id}-out`,
						group: "out"
					}]
				}
			};
		}
		const selectedNode = computed(() => definition.value.nodes.find((n) => n.id === selectedNodeId.value) || null);
		/** 传给 NodeParamPanel / ExpressionEditor 的变量列表（输入 + 局部） */
		const allVariables = computed(() => [...definition.value.variables.inputs || [], ...definition.value.variables.locals || []]);
		/** ExecutionLogPanel 的 key，切换微流时强制重置其内部状态 */
		const logPanelKey = computed(() => {
			var _currentMicroflow$val, _currentMicroflow$val2;
			return (_currentMicroflow$val = (_currentMicroflow$val2 = currentMicroflow.value) === null || _currentMicroflow$val2 === void 0 ? void 0 : _currentMicroflow$val2.id) !== null && _currentMicroflow$val !== void 0 ? _currentMicroflow$val : "new";
		});
		async function loadMicroflowList() {
			try {
				microflowList.value = await getMicroflowList();
			} catch (_unused2) {
				ElMessage.error("加载微流列表失败");
			}
		}
		async function loadOptions() {
			try {
				const [rules, connectors] = await Promise.all([getRuleList(), getConnectorList()]);
				ruleOptions.value = rules || [];
				connectorOptions.value = connectors || [];
			} catch (_unused3) {}
		}
		function selectMicroflow(item) {
			currentMicroflow.value = { ...item };
			definition.value = parseDefinition(item.definition);
			selectedNodeId.value = null;
			selectedCellId.value = null;
			latestExecutionId.value = void 0;
			nextTick(() => renderGraph());
		}
		function onMicroflowChange(id) {
			const item = microflowList.value.find((m) => m.id === id);
			if (item) selectMicroflow(item);
		}
		function newMicroflow() {
			var _graphRef$value;
			currentMicroflow.value = {
				code: "",
				name: "",
				description: "",
				definition: "",
				status: "DRAFT"
			};
			definition.value = emptyDefinition();
			selectedNodeId.value = null;
			selectedCellId.value = null;
			selectedMicroflowId.value = void 0;
			latestExecutionId.value = void 0;
			(_graphRef$value = graphRef.value) === null || _graphRef$value === void 0 || _graphRef$value.clearCells();
		}
		/**
		* 根据源端口 ID 推断连线语义（标签 + 颜色）。
		*
		* <p>端口 ID 规范见 nodeAddConfig：`xxx-true` / `xxx-false` / `xxx-body` / `xxx-exit`。
		* CONDITION 真→绿、假→红；LOOP 循环→蓝、退出→灰；其他→默认蓝、无标签。</p>
		*/
		function edgeSemantic(sourcePort) {
			if (sourcePort.endsWith("-true")) return {
				label: "真",
				color: "#67c23a"
			};
			if (sourcePort.endsWith("-false")) return {
				label: "假",
				color: "#f56c6c"
			};
			if (sourcePort.endsWith("-body")) return {
				label: "循环",
				color: "#409eff"
			};
			if (sourcePort.endsWith("-exit")) return {
				label: "退出",
				color: "#909399"
			};
			return {
				label: "",
				color: "#409eff"
			};
		}
		function renderGraph() {
			const g = graphRef.value;
			if (!g) return;
			g.clearCells();
			const needsLayout = definition.value.nodes.some((n) => !n.x && !n.y);
			for (const n of definition.value.nodes) g.addNode(nodeAddConfig(n));
			for (const e of definition.value.edges) {
				const source = { cell: e.source };
				const target = { cell: e.target };
				if (e.sourcePort) source.port = e.sourcePort;
				if (e.targetPort) target.port = e.targetPort;
				const { label: edgeLabel, color: edgeColor } = edgeSemantic(e.sourcePort || "");
				g.addEdge({
					id: e.id,
					source,
					target,
					attrs: { line: {
						stroke: edgeColor,
						strokeWidth: 2,
						targetMarker: {
							name: "classic",
							size: 6
						}
					} },
					labels: edgeLabel ? [{ attrs: { label: {
						text: edgeLabel,
						fill: "#606266",
						fontSize: 11
					} } }] : []
				});
			}
			if (needsLayout && definition.value.nodes.length > 0) autoLayout();
			else if (definition.value.nodes.length > 0) g.zoomToFit({
				padding: 20,
				maxScale: 1
			});
		}
		function addNodeToCanvas(type, x, y) {
			const id = `node_${Date.now()}_${Math.floor(Math.random() * 1e3)}`;
			const count = definition.value.nodes.length;
			const defaultX = 120 + count % 6 * 40;
			const defaultY = 120 + Math.floor(count / 6) * 100 + count % 3 * 20;
			const node = {
				id,
				type,
				label: DEFAULT_LABELS[type],
				x: x !== null && x !== void 0 ? x : defaultX,
				y: y !== null && y !== void 0 ? y : defaultY,
				config: {}
			};
			definition.value.nodes.push(node);
			const g = graphRef.value;
			if (g) try {
				g.addNode(nodeAddConfig(node));
			} catch (e) {
				console.error("[Microflow] Failed to add node to canvas:", e);
			}
			else console.warn("[Microflow] Graph not initialized, node added to data only");
			selectedNodeId.value = id;
			selectedCellId.value = id;
			setHighlighted(id);
		}
		function onPaletteAdd(type) {
			addNodeToCanvas(type);
		}
		function onCanvasDrop(e) {
			var _e$dataTransfer, _canvasContainer$valu;
			e.preventDefault();
			const type = (_e$dataTransfer = e.dataTransfer) === null || _e$dataTransfer === void 0 ? void 0 : _e$dataTransfer.getData("microflow-node-type");
			if (!type) return;
			const rect = (_canvasContainer$valu = canvasContainer.value) === null || _canvasContainer$valu === void 0 ? void 0 : _canvasContainer$valu.getBoundingClientRect();
			const x = e.clientX - ((rect === null || rect === void 0 ? void 0 : rect.left) || 0) - NODE_WIDTH / 2;
			const y = e.clientY - ((rect === null || rect === void 0 ? void 0 : rect.top) || 0) - NODE_HEIGHT / 2;
			addNodeToCanvas(type, Math.max(0, x), Math.max(0, y));
		}
		/** NodeParamPanel 编辑回写：更新 definition 节点并同步画布节点 label */
		function onUpdateNode(updated) {
			const idx = definition.value.nodes.findIndex((n) => n.id === updated.id);
			if (idx >= 0) definition.value.nodes[idx] = updated;
			const g = graphRef.value;
			if (g) {
				const cell = g.getCellById(updated.id);
				if (cell && cell.isNode()) {
					const data = cell.getData() || {};
					cell.setData({
						...data,
						label: updated.label
					});
				}
			}
		}
		/** 高亮指定节点（画布点击 / 日志点击共用） */
		function setHighlighted(nodeId) {
			const g = graphRef.value;
			if (!g) return;
			g.getNodes().forEach((n) => {
				const data = n.getData() || {};
				n.setData({
					...data,
					highlighted: !!nodeId && data.nodeId === nodeId
				});
			});
		}
		function deleteSelected() {
			const g = graphRef.value;
			if (!g || !selectedCellId.value) return;
			const cell = g.getCellById(selectedCellId.value);
			if (!cell) {
				selectedCellId.value = null;
				return;
			}
			if (cell.isNode()) {
				const id = cell.id;
				definition.value.nodes = definition.value.nodes.filter((n) => n.id !== id);
				if (selectedNodeId.value === id) selectedNodeId.value = null;
			}
			g.removeCell(cell);
			selectedCellId.value = null;
		}
		function autoLayout() {
			var _nodes$find;
			const g = graphRef.value;
			if (!g || definition.value.nodes.length === 0) return;
			const nodes = definition.value.nodes;
			const adj = /* @__PURE__ */ new Map();
			nodes.forEach((n) => adj.set(n.id, []));
			g.getEdges().forEach((e) => {
				const s = e.getSourceCellId();
				const t = e.getTargetCellId();
				if (s && t && adj.has(s) && !adj.get(s).includes(t)) adj.get(s).push(t);
			});
			for (const n of nodes) {
				const cfg = n.config || {};
				for (const k of [
					"trueBranch",
					"falseBranch",
					"bodyNodeId"
				]) {
					const v = cfg[k];
					if (typeof v === "string" && adj.has(n.id) && adj.has(v) && !adj.get(n.id).includes(v)) adj.get(n.id).push(v);
				}
			}
			const layer = /* @__PURE__ */ new Map();
			const start = ((_nodes$find = nodes.find((n) => n.type === "START")) === null || _nodes$find === void 0 ? void 0 : _nodes$find.id) || nodes[0].id;
			layer.set(start, 0);
			const queue = [start];
			while (queue.length) {
				const cur = queue.shift();
				const curLayer = layer.get(cur) || 0;
				for (const nb of adj.get(cur) || []) {
					var _layer$get;
					const nl = curLayer + 1;
					if (((_layer$get = layer.get(nb)) !== null && _layer$get !== void 0 ? _layer$get : -1) < nl) {
						layer.set(nb, nl);
						queue.push(nb);
					}
				}
			}
			let maxLayer = 0;
			layer.forEach((l) => maxLayer = Math.max(maxLayer, l));
			nodes.forEach((n) => {
				if (!layer.has(n.id)) {
					maxLayer += 1;
					layer.set(n.id, maxLayer);
				}
			});
			const byLayer = /* @__PURE__ */ new Map();
			layer.forEach((l, id) => {
				if (!byLayer.has(l)) byLayer.set(l, []);
				byLayer.get(l).push(id);
			});
			const gapX = 230;
			const gapY = 90;
			const startX = 40;
			const startY = 40;
			byLayer.forEach((ids, l) => {
				ids.forEach((id, i) => {
					const cell = g.getCellById(id);
					if (cell && cell.isNode()) cell.position(startX + l * gapX, startY + i * gapY);
				});
			});
			g.zoomToFit({
				padding: 20,
				maxScale: 1
			});
		}
		function zoomToFit() {
			var _graphRef$value2;
			(_graphRef$value2 = graphRef.value) === null || _graphRef$value2 === void 0 || _graphRef$value2.zoomToFit({
				padding: 20,
				maxScale: 1
			});
		}
		async function clearCanvas() {
			var _graphRef$value3;
			try {
				await ElMessageBox.confirm("确认清空画布上所有节点与连线？", "确认", { type: "warning" });
			} catch (_unused4) {
				return;
			}
			(_graphRef$value3 = graphRef.value) === null || _graphRef$value3 === void 0 || _graphRef$value3.clearCells();
			definition.value = emptyDefinition();
			selectedNodeId.value = null;
			selectedCellId.value = null;
		}
		/**
		* 校验所有节点必填字段（config 字段名与 NodeParamPanel / 后端 MicroflowNodeExecutor 对齐）。
		*
		* <p>校验项：</p>
		* <ul>
		*   <li>ASSIGN：target（目标变量）+ expression（赋值表达式）</li>
		*   <li>CONDITION：expression（条件表达式）</li>
		*   <li>LOOP：iterableExpression（可迭代对象表达式）+ bodyNodeId（循环体起点）</li>
		*   <li>CALL_SERVICE：beanName + methodName</li>
		*   <li>CALL_MICROFLOW：microflowCode</li>
		*   <li>CALL_RULE：ruleCode</li>
		*   <li>CALL_CONNECTOR：connectorCode</li>
		*   <li>THROW_EXCEPTION：errorMessage</li>
		*   <li>RETURN：expression（返回值表达式）</li>
		*   <li>结构校验：必须含 START，且含 END 或 RETURN</li>
		* </ul>
		*/
		function validateNodes() {
			const errors = [];
			for (const node of definition.value.nodes) {
				const cfg = node.config || {};
				switch (node.type) {
					case "ASSIGN":
						if (!cfg.target) errors.push(`节点 ${node.label}：缺少目标变量名`);
						if (!cfg.expression) errors.push(`节点 ${node.label}：缺少赋值表达式`);
						break;
					case "CONDITION":
						if (!cfg.expression) errors.push(`节点 ${node.label}：缺少条件表达式`);
						break;
					case "LOOP":
						if (!cfg.iterableExpression) errors.push(`节点 ${node.label}：缺少可迭代对象表达式`);
						if (!cfg.bodyNodeId) errors.push(`节点 ${node.label}：缺少循环体起点节点`);
						break;
					case "CALL_SERVICE":
						if (!cfg.beanName) errors.push(`节点 ${node.label}：缺少 Bean 名称`);
						if (!cfg.methodName) errors.push(`节点 ${node.label}：缺少方法名`);
						break;
					case "CALL_MICROFLOW":
						if (!cfg.microflowCode) errors.push(`节点 ${node.label}：缺少微流编码`);
						break;
					case "CALL_RULE":
						if (!cfg.ruleCode) errors.push(`节点 ${node.label}：缺少规则编码`);
						break;
					case "CALL_CONNECTOR":
						if (!cfg.connectorCode) errors.push(`节点 ${node.label}：缺少连接器编码`);
						break;
					case "THROW_EXCEPTION":
						if (!cfg.errorMessage) errors.push(`节点 ${node.label}：缺少错误消息`);
						break;
					case "RETURN":
						if (!cfg.expression) errors.push(`节点 ${node.label}：缺少返回值表达式`);
						break;
				}
			}
			if (!definition.value.nodes.some((n) => n.type === "START")) errors.push("微流必须包含一个开始节点");
			if (!definition.value.nodes.some((n) => n.type === "END" || n.type === "RETURN")) errors.push("微流必须包含结束或返回节点");
			return errors;
		}
		async function save() {
			if (!currentMicroflow.value) {
				ElMessage.warning("请先选择或新建微流");
				return;
			}
			if (!currentMicroflow.value.code || !currentMicroflow.value.name) {
				ElMessage.warning("请填写微流编码和名称");
				return;
			}
			const errors = validateNodes();
			if (errors.length > 0) {
				ElMessage.warning(`节点配置不完整：\n${errors.join("\n")}`);
				return;
			}
			const g = graphRef.value;
			if (g) {
				for (const n of definition.value.nodes) {
					const cell = g.getCellById(n.id);
					if (cell && cell.isNode()) {
						const pos = cell.position();
						n.x = pos.x;
						n.y = pos.y;
					}
				}
				definition.value.edges = g.getEdges().map((e) => {
					var _e$getSourceCellId, _e$getTargetCellId, _e$getSourcePortId, _e$getTargetPortId;
					return {
						id: e.id,
						source: (_e$getSourceCellId = e.getSourceCellId()) !== null && _e$getSourceCellId !== void 0 ? _e$getSourceCellId : "",
						target: (_e$getTargetCellId = e.getTargetCellId()) !== null && _e$getTargetCellId !== void 0 ? _e$getTargetCellId : "",
						sourcePort: (_e$getSourcePortId = e.getSourcePortId()) !== null && _e$getSourcePortId !== void 0 ? _e$getSourcePortId : "",
						targetPort: (_e$getTargetPortId = e.getTargetPortId()) !== null && _e$getTargetPortId !== void 0 ? _e$getTargetPortId : ""
					};
				});
			}
			currentMicroflow.value.definition = JSON.stringify(definition.value);
			try {
				const saved = await saveMicroflow(currentMicroflow.value);
				currentMicroflow.value = saved;
				selectedMicroflowId.value = saved.id;
				ElMessage.success("保存成功");
				await loadMicroflowList();
			} catch (_unused5) {
				ElMessage.error("保存失败");
			}
		}
		function openExec() {
			var _currentMicroflow$val3;
			if (!((_currentMicroflow$val3 = currentMicroflow.value) === null || _currentMicroflow$val3 === void 0 ? void 0 : _currentMicroflow$val3.id)) {
				ElMessage.warning("请先保存微流");
				return;
			}
			dialogMode.value = "execute";
			execInputs.value = "";
			execDialogVisible.value = true;
		}
		/** 对话框确认：按 dialogMode 分发到执行或调试 */
		async function doExecute() {
			var _currentMicroflow$val4;
			if (!((_currentMicroflow$val4 = currentMicroflow.value) === null || _currentMicroflow$val4 === void 0 ? void 0 : _currentMicroflow$val4.id)) return;
			let inputs = {};
			try {
				inputs = execInputs.value ? JSON.parse(execInputs.value) : {};
			} catch (_unused6) {
				ElMessage.error("输入参数 JSON 解析失败");
				return;
			}
			execDialogVisible.value = false;
			if (dialogMode.value === "debug") {
				await doStartDebug(inputs);
				return;
			}
			logCollapsed.value = false;
			try {
				await executeMicroflow(currentMicroflow.value.code, inputs);
				ElMessage.success("执行完成");
				await fetchLatestExecutionLogs(currentMicroflow.value.id);
			} catch (e) {
				ElMessage.error("执行失败：" + e.message);
				await fetchLatestExecutionLogs(currentMicroflow.value.id);
			}
		}
		/** 切换选中节点的断点（无选中则提示） */
		function toggleBreakpoint() {
			const nodeId = selectedNodeId.value;
			if (!nodeId) {
				ElMessage.warning("请先选中一个节点");
				return;
			}
			toggleBreakpointAt(nodeId);
		}
		/** 切换指定节点的断点 */
		function toggleBreakpointAt(nodeId) {
			const set = new Set(breakpoints.value);
			if (set.has(nodeId)) set.delete(nodeId);
			else set.add(nodeId);
			breakpoints.value = set;
			refreshDebugMarkers();
		}
		/** 打开调试输入对话框 */
		function openDebug() {
			var _currentMicroflow$val5;
			if (!((_currentMicroflow$val5 = currentMicroflow.value) === null || _currentMicroflow$val5 === void 0 ? void 0 : _currentMicroflow$val5.id)) {
				ElMessage.warning("请先保存微流");
				return;
			}
			if (isDebugging.value) {
				ElMessage.warning("当前已在调试中，请先终止");
				return;
			}
			dialogMode.value = "debug";
			execInputs.value = "";
			execDialogVisible.value = true;
		}
		/** 启动调试会话 */
		async function doStartDebug(inputs) {
			if (!currentMicroflow.value) return;
			debugLoading.value = true;
			try {
				const session = await startMicroflowDebug(currentMicroflow.value.code, {
					inputs,
					breakpointNodeIds: Array.from(breakpoints.value)
				});
				debugSessionId.value = session.sessionId;
				isDebugging.value = true;
				debugVariables.value = session.variables || {};
				debugStatus.value = "PAUSED";
				debugCurrentNodeId.value = session.currentNodeId;
				debugResult.value = void 0;
				debugVariablesVisible.value = true;
				clearAllNodeStatuses();
				refreshDebugMarkers();
				centerOnNode(session.currentNodeId);
				ElMessage.success("调试会话已启动");
			} catch (e) {
				ElMessage.error("启动调试失败：" + e.message);
			} finally {
				debugLoading.value = false;
			}
		}
		/** 单步执行 */
		async function doStepOver() {
			if (!debugSessionId.value || debugLoading.value) return;
			debugLoading.value = true;
			try {
				applyStepResult(await stepOverMicroflowDebug(debugSessionId.value));
			} catch (e) {
				ElMessage.error("单步执行失败：" + e.message);
			} finally {
				debugLoading.value = false;
			}
		}
		/** 继续执行到下一断点 */
		async function doContinueDebug() {
			if (!debugSessionId.value || debugLoading.value) return;
			debugLoading.value = true;
			try {
				applyStepResult(await continueMicroflowDebug(debugSessionId.value));
			} catch (e) {
				ElMessage.error("继续执行失败：" + e.message);
			} finally {
				debugLoading.value = false;
			}
		}
		/** 终止调试会话 */
		async function doTerminateDebug() {
			if (!debugSessionId.value) {
				resetDebugState();
				return;
			}
			try {
				await terminateMicroflowDebug(debugSessionId.value);
			} catch (_unused7) {}
			resetDebugState();
			ElMessage.info("调试会话已终止");
		}
		/** 重置调试本地状态 */
		function resetDebugState() {
			isDebugging.value = false;
			debugSessionId.value = null;
			debugVariables.value = {};
			debugStatus.value = null;
			debugCurrentNodeId.value = null;
			debugResult.value = void 0;
			debugLoading.value = false;
			refreshDebugMarkers();
		}
		/** 应用单步/继续结果到调试状态与画布 */
		function applyStepResult(result) {
			debugVariables.value = result.variables || {};
			debugStatus.value = result.status;
			debugResult.value = result.result;
			setNodeStatus(result.nodeId, result.status === "FAILED" ? "FAILED" : "SUCCESS");
			if (result.status === "COMPLETED" || result.status === "FAILED") {
				debugCurrentNodeId.value = null;
				if (result.status === "COMPLETED") ElMessage.success("微流执行完成");
				else ElMessage.error("节点执行失败：" + (result.errorMessage || "未知错误"));
			} else debugCurrentNodeId.value = result.nextNodeId;
			refreshDebugMarkers();
			centerOnNode(debugCurrentNodeId.value);
		}
		/** 将调试标记（断点 / 当前节点）同步到画布所有节点 */
		function refreshDebugMarkers() {
			const g = graphRef.value;
			if (!g) return;
			g.getNodes().forEach((node) => {
				const data = node.getData() || {};
				node.setData({
					...data,
					breakpoint: breakpoints.value.has(data.nodeId),
					debugCurrent: data.nodeId === debugCurrentNodeId.value
				});
			});
		}
		/** 设置某节点的执行状态 */
		function setNodeStatus(nodeId, status) {
			const g = graphRef.value;
			if (!g || !nodeId) return;
			const cell = g.getCellById(nodeId);
			if (cell && cell.isNode()) {
				const data = cell.getData() || {};
				cell.setData({
					...data,
					status: status || void 0
				});
			}
		}
		/** 清空所有节点执行状态（调试开始时重置画布） */
		function clearAllNodeStatuses() {
			const g = graphRef.value;
			if (!g) return;
			g.getNodes().forEach((node) => {
				const data = node.getData() || {};
				node.setData({
					...data,
					status: void 0
				});
			});
		}
		/** 居中到指定节点 */
		function centerOnNode(nodeId) {
			if (!nodeId) return;
			const g = graphRef.value;
			if (!g) return;
			const cell = g.getCellById(nodeId);
			if (cell && cell.isNode()) g.centerCell(cell);
		}
		/** 调试状态标签类型 */
		const debugStatusTagType = computed(() => {
			if (debugStatus.value === "COMPLETED") return "success";
			if (debugStatus.value === "FAILED") return "danger";
			if (debugStatus.value === "PAUSED") return "warning";
			return "info";
		});
		/** 调试状态文案 */
		const debugStatusText = computed(() => {
			if (!isDebugging.value) return "";
			if (debugStatus.value === "COMPLETED") return "已完成";
			if (debugStatus.value === "FAILED") return "失败";
			if (debugStatus.value === "PAUSED") return "已暂停";
			return "调试中";
		});
		/** 变量监视面板：将变量转为可展示的键值对列表 */
		const debugVariableEntries = computed(() => {
			return Object.entries(debugVariables.value).map(([k, v]) => ({
				key: k,
				value: formatDebugValue(v)
			}));
		});
		/** 格式化调试变量值（对象/数组转 JSON，其余转字符串） */
		function formatDebugValue(v) {
			if (v === null) return "null";
			if (v === void 0) return "undefined";
			if (typeof v === "object") try {
				return JSON.stringify(v);
			} catch (_unused8) {
				return String(v);
			}
			return String(v);
		}
		/** 执行后取最新一次执行的 executionId，交给 ExecutionLogPanel 加载并高亮画布 */
		async function fetchLatestExecutionLogs(microflowId) {
			try {
				const recent = await getRecentExecutionLogs(microflowId, 50);
				if (recent && recent.length > 0) latestExecutionId.value = recent[0].executionId;
			} catch (_unused9) {}
		}
		/** ExecutionLogPanel 加载完日志后，按 status 同步画布节点状态 */
		function onLogsLoaded(logs) {
			const g = graphRef.value;
			if (!g) return;
			const statusMap = /* @__PURE__ */ new Map();
			for (const l of logs) statusMap.set(l.nodeId, l.status);
			g.getNodes().forEach((node) => {
				const data = node.getData() || {};
				const status = statusMap.get(data.nodeId);
				node.setData({
					...data,
					status
				});
			});
		}
		/** 点击日志条目 → 高亮并居中对应画布节点 */
		function onHighlightNode(nodeId) {
			setHighlighted(nodeId);
			const g = graphRef.value;
			if (!g) return;
			const cell = g.getCellById(nodeId);
			if (cell && cell.isNode()) g.centerCell(cell);
		}
		function onKeyDown(e) {
			if (e.key !== "Delete" && e.key !== "Backspace") return;
			const el = e.target;
			const tag = el === null || el === void 0 ? void 0 : el.tagName;
			if (tag === "INPUT" || tag === "TEXTAREA" || (el === null || el === void 0 ? void 0 : el.isContentEditable)) return;
			if (selectedCellId.value) {
				e.preventDefault();
				deleteSelected();
			} else if (e.key === "Backspace") e.preventDefault();
		}
		function initGraph() {
			if (!canvasContainer.value) return;
			const g = new Graph({
				container: canvasContainer.value,
				background: { color: "#f7f8fa" },
				grid: {
					visible: true,
					size: 10,
					type: "dot"
				},
				interacting: {
					nodeMovable: true,
					edgeMovable: true
				},
				panning: true,
				mousewheel: {
					enabled: true,
					modifiers: ["ctrl"]
				},
				connecting: {
					allowBlank: false,
					allowLoop: false,
					allowMulti: true,
					router: "orth",
					connector: "rounded",
					createEdge() {
						return this.createEdge({
							shape: "edge",
							attrs: { line: {
								stroke: "#409eff",
								strokeWidth: 2,
								targetMarker: {
									name: "classic",
									size: 6
								}
							} }
						});
					}
				}
			});
			g.on("node:click", ({ node }) => {
				var _data$nodeId, _data$nodeId2;
				const data = node.getData() || {};
				selectedNodeId.value = (_data$nodeId = data.nodeId) !== null && _data$nodeId !== void 0 ? _data$nodeId : null;
				selectedCellId.value = node.id;
				setHighlighted((_data$nodeId2 = data.nodeId) !== null && _data$nodeId2 !== void 0 ? _data$nodeId2 : null);
			});
			g.on("edge:click", ({ edge }) => {
				selectedNodeId.value = null;
				selectedCellId.value = edge.id;
				setHighlighted(null);
			});
			g.on("blank:click", () => {
				selectedNodeId.value = null;
				selectedCellId.value = null;
				setHighlighted(null);
			});
			graphRef.value = g;
		}
		onMounted(async () => {
			try {
				register({
					shape: MICROFLOW_NODE_SHAPE,
					width: NODE_WIDTH,
					height: NODE_HEIGHT,
					component: MicroflowNode_default
				});
			} catch (e) {
				console.warn("[Microflow] Node shape registration skipped (may already be registered):", e);
			}
			try {
				initGraph();
			} catch (e) {
				console.error("[Microflow] Failed to initialize graph:", e);
			}
			await Promise.all([loadMicroflowList(), loadOptions()]);
			window.addEventListener("keydown", onKeyDown);
		});
		onBeforeUnmount(() => {
			var _graphRef$value4;
			window.removeEventListener("keydown", onKeyDown);
			(_graphRef$value4 = graphRef.value) === null || _graphRef$value4 === void 0 || _graphRef$value4.dispose();
			graphRef.value = null;
		});
		return (_ctx, _cache) => {
			var _currentMicroflow$val6;
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_divider = resolveComponent("el-divider");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_dialog = resolveComponent("el-dialog");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_drawer = resolveComponent("el-drawer");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createElementVNode("div", _hoisted_2, [
					createVNode(_component_el_select, {
						"model-value": selectedMicroflowId.value,
						placeholder: "选择微流",
						filterable: "",
						clearable: "",
						class: "microflow-select",
						onChange: onMicroflowChange
					}, {
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(microflowList.value, (m) => {
							return openBlock(), createBlock(_component_el_option, {
								key: m.id,
								label: `${m.name} (${m.code})`,
								value: m.id
							}, null, 8, ["label", "value"]);
						}), 128))]),
						_: 1
					}, 8, ["model-value"]),
					createVNode(_component_el_button, {
						size: "small",
						onClick: newMicroflow
					}, {
						default: withCtx(() => [..._cache[9] || (_cache[9] = [createTextVNode("新建", -1)])]),
						_: 1
					}),
					createVNode(_component_el_divider, { direction: "vertical" }),
					createVNode(_component_el_button, {
						size: "small",
						type: "primary",
						onClick: save
					}, {
						default: withCtx(() => [..._cache[10] || (_cache[10] = [createTextVNode("保存", -1)])]),
						_: 1
					}),
					createVNode(_component_el_button, {
						size: "small",
						type: "success",
						onClick: openExec
					}, {
						default: withCtx(() => [..._cache[11] || (_cache[11] = [createTextVNode("执行", -1)])]),
						_: 1
					}),
					createVNode(_component_el_button, {
						size: "small",
						onClick: clearCanvas
					}, {
						default: withCtx(() => [..._cache[12] || (_cache[12] = [createTextVNode("清空", -1)])]),
						_: 1
					}),
					createVNode(_component_el_divider, { direction: "vertical" }),
					createVNode(_component_el_button, {
						size: "small",
						onClick: autoLayout
					}, {
						default: withCtx(() => [..._cache[13] || (_cache[13] = [createTextVNode("自动布局", -1)])]),
						_: 1
					}),
					createVNode(_component_el_button, {
						size: "small",
						onClick: zoomToFit
					}, {
						default: withCtx(() => [..._cache[14] || (_cache[14] = [createTextVNode("缩放适配", -1)])]),
						_: 1
					}),
					createVNode(_component_el_button, {
						size: "small",
						type: "warning",
						onClick: deleteSelected
					}, {
						default: withCtx(() => [..._cache[15] || (_cache[15] = [createTextVNode("删除选中", -1)])]),
						_: 1
					}),
					createVNode(_component_el_divider, { direction: "vertical" }),
					createVNode(_component_el_button, {
						size: "small",
						disabled: !selectedNodeId.value,
						onClick: toggleBreakpoint
					}, {
						default: withCtx(() => [..._cache[16] || (_cache[16] = [createTextVNode("切换断点", -1)])]),
						_: 1
					}, 8, ["disabled"]),
					!isDebugging.value ? (openBlock(), createBlock(_component_el_button, {
						key: 0,
						size: "small",
						type: "primary",
						onClick: openDebug
					}, {
						default: withCtx(() => [..._cache[17] || (_cache[17] = [createTextVNode("开始调试", -1)])]),
						_: 1
					})) : (openBlock(), createElementBlock(Fragment, { key: 1 }, [
						createVNode(_component_el_tag, {
							type: debugStatusTagType.value,
							size: "small"
						}, {
							default: withCtx(() => [createTextVNode(toDisplayString(debugStatusText.value), 1)]),
							_: 1
						}, 8, ["type"]),
						createVNode(_component_el_button, {
							size: "small",
							type: "primary",
							loading: debugLoading.value,
							disabled: debugStatus.value !== "PAUSED",
							onClick: doStepOver
						}, {
							default: withCtx(() => [..._cache[18] || (_cache[18] = [createTextVNode("单步", -1)])]),
							_: 1
						}, 8, ["loading", "disabled"]),
						createVNode(_component_el_button, {
							size: "small",
							type: "success",
							loading: debugLoading.value,
							disabled: debugStatus.value !== "PAUSED",
							onClick: doContinueDebug
						}, {
							default: withCtx(() => [..._cache[19] || (_cache[19] = [createTextVNode("继续", -1)])]),
							_: 1
						}, 8, ["loading", "disabled"]),
						createVNode(_component_el_button, {
							size: "small",
							type: "danger",
							loading: debugLoading.value,
							onClick: doTerminateDebug
						}, {
							default: withCtx(() => [..._cache[20] || (_cache[20] = [createTextVNode("终止", -1)])]),
							_: 1
						}, 8, ["loading"]),
						createVNode(_component_el_button, {
							size: "small",
							onClick: _cache[0] || (_cache[0] = ($event) => debugVariablesVisible.value = true)
						}, {
							default: withCtx(() => [..._cache[21] || (_cache[21] = [createTextVNode("变量", -1)])]),
							_: 1
						})
					], 64))
				]),
				createElementVNode("div", _hoisted_3, [
					createVNode(NodePalette_default, { onAddNode: onPaletteAdd }),
					createElementVNode("div", _hoisted_4, [createElementVNode("div", {
						ref_key: "canvasContainer",
						ref: canvasContainer,
						class: "canvas-container",
						onDrop: onCanvasDrop,
						onDragover: _cache[1] || (_cache[1] = withModifiers(() => {}, ["prevent"]))
					}, null, 544)]),
					createElementVNode("div", _hoisted_5, [selectedNode.value ? (openBlock(), createBlock(NodeParamPanel_default, {
						key: selectedNode.value.id,
						node: selectedNode.value,
						nodes: definition.value.nodes,
						"microflow-options": microflowList.value,
						"rule-options": ruleOptions.value,
						"connector-options": connectorOptions.value,
						variables: allVariables.value,
						"onUpdate:node": onUpdateNode
					}, null, 8, [
						"node",
						"nodes",
						"microflow-options",
						"rule-options",
						"connector-options",
						"variables"
					])) : (openBlock(), createElementBlock(Fragment, { key: 1 }, [createVNode(MicroflowMetaPanel_default, {
						microflow: currentMicroflow.value,
						"onUpdate:microflow": _cache[2] || (_cache[2] = ($event) => currentMicroflow.value = $event)
					}, null, 8, ["microflow"]), createElementVNode("div", _hoisted_6, [_cache[22] || (_cache[22] = createElementVNode("div", { class: "right-section-title" }, "变量", -1)), createVNode(VariablePanel_default, {
						variables: definition.value.variables,
						"onUpdate:variables": _cache[3] || (_cache[3] = ($event) => definition.value.variables = $event)
					}, null, 8, ["variables"])])], 64))])
				]),
				(openBlock(), createBlock(ExecutionLogPanel_default, {
					key: logPanelKey.value,
					"microflow-id": (_currentMicroflow$val6 = currentMicroflow.value) === null || _currentMicroflow$val6 === void 0 ? void 0 : _currentMicroflow$val6.id,
					"execution-id": latestExecutionId.value,
					collapsed: logCollapsed.value,
					"onUpdate:collapsed": _cache[4] || (_cache[4] = ($event) => logCollapsed.value = $event),
					onHighlightNode,
					onLogsLoaded
				}, null, 8, [
					"microflow-id",
					"execution-id",
					"collapsed"
				])),
				createVNode(_component_el_dialog, {
					modelValue: execDialogVisible.value,
					"onUpdate:modelValue": _cache[7] || (_cache[7] = ($event) => execDialogVisible.value = $event),
					title: dialogMode.value === "debug" ? "调试输入" : "执行输入",
					width: "600px"
				}, {
					footer: withCtx(() => [createVNode(_component_el_button, { onClick: _cache[6] || (_cache[6] = ($event) => execDialogVisible.value = false) }, {
						default: withCtx(() => [..._cache[23] || (_cache[23] = [createTextVNode("取消", -1)])]),
						_: 1
					}), createVNode(_component_el_button, {
						type: "primary",
						onClick: doExecute
					}, {
						default: withCtx(() => [createTextVNode(toDisplayString(dialogMode.value === "debug" ? "开始调试" : "执行"), 1)]),
						_: 1
					})]),
					default: withCtx(() => [createVNode(_component_el_input, {
						modelValue: execInputs.value,
						"onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => execInputs.value = $event),
						type: "textarea",
						rows: 6,
						placeholder: "{\"key\":\"value\"}"
					}, null, 8, ["modelValue"])]),
					_: 1
				}, 8, ["modelValue", "title"]),
				createVNode(_component_el_drawer, {
					modelValue: debugVariablesVisible.value,
					"onUpdate:modelValue": _cache[8] || (_cache[8] = ($event) => debugVariablesVisible.value = $event),
					title: "变量监视",
					direction: "rtl",
					size: "440px"
				}, {
					default: withCtx(() => [createElementVNode("div", _hoisted_7, [
						createElementVNode("div", _hoisted_8, [_cache[24] || (_cache[24] = createElementVNode("span", { class: "debug-label" }, "状态：", -1)), createVNode(_component_el_tag, {
							type: debugStatusTagType.value,
							size: "small"
						}, {
							default: withCtx(() => [createTextVNode(toDisplayString(debugStatusText.value), 1)]),
							_: 1
						}, 8, ["type"])]),
						debugCurrentNodeId.value ? (openBlock(), createElementBlock("div", _hoisted_9, [_cache[25] || (_cache[25] = createElementVNode("span", { class: "debug-label" }, "当前节点：", -1)), createElementVNode("span", _hoisted_10, toDisplayString(debugCurrentNodeId.value), 1)])) : createCommentVNode("", true),
						createElementVNode("div", _hoisted_11, [_cache[26] || (_cache[26] = createElementVNode("div", { class: "debug-label" }, "变量：", -1)), createVNode(_component_el_table, {
							data: debugVariableEntries.value,
							border: "",
							size: "small",
							"empty-text": "暂无变量"
						}, {
							default: withCtx(() => [createVNode(_component_el_table_column, {
								prop: "key",
								label: "名称",
								width: "140"
							}), createVNode(_component_el_table_column, {
								prop: "value",
								label: "值",
								"show-overflow-tooltip": ""
							})]),
							_: 1
						}, 8, ["data"])]),
						debugResult.value !== void 0 ? (openBlock(), createElementBlock("div", _hoisted_12, [_cache[27] || (_cache[27] = createElementVNode("div", { class: "debug-label" }, "结果：", -1)), createElementVNode("pre", _hoisted_13, toDisplayString(formatDebugValue(debugResult.value)), 1)])) : createCommentVNode("", true)
					])]),
					_: 1
				}, 8, ["modelValue"])
			]);
		};
	}
}), [["__scopeId", "data-v-28cced0e"]]);
//#endregion
export { microflow_designer_default as default };

//# sourceMappingURL=microflow-designer-ChR-Jtun.js.map