import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, openBlock, ref, renderList, resolveComponent, toDisplayString, vShow, withCtx, withDirectives } from "vue";
//#region src/views/changelog/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "changelog-page" };
var _hoisted_2 = { class: "page-header" };
var _hoisted_3 = { class: "page-actions" };
var _hoisted_4 = { class: "changelog-list" };
var _hoisted_5 = ["onClick"];
var _hoisted_6 = { class: "version-header__left" };
var _hoisted_7 = { class: "version-header__number" };
var _hoisted_8 = {
	key: 0,
	class: "version-header__desc"
};
var _hoisted_9 = { class: "version-header__count" };
var _hoisted_10 = { class: "version-body" };
var _hoisted_11 = { class: "change-group__title" };
var _hoisted_12 = { class: "change-group__list" };
//#endregion
//#region src/views/changelog/index.vue
var changelog_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const VERSIONS = [
			{
				version: "Unreleased",
				date: "未发布",
				description: "开发中，尚未正式发布",
				changes: [
					{
						type: "added",
						description: "用户引导系统：首次登录自动展示 5 步功能引导"
					},
					{
						type: "added",
						description: "帮助中心页面：分类浏览、搜索、Markdown 渲染"
					},
					{
						type: "added",
						description: "浮动反馈按钮：右下角悬浮，提交 BUG / 建议 / 咨询"
					},
					{
						type: "added",
						description: "系统状态页：后端健康、磁盘、反馈统计、近期动态"
					},
					{
						type: "added",
						description: "版本日志页：按版本展示变更记录"
					},
					{
						type: "added",
						description: "后端 sys_help_content / sys_feedback 表与对应 API"
					}
				]
			},
			{
				version: "v1.1.0",
				date: "2026-06-15",
				description: "低代码能力 + 集成治理增强",
				changes: [
					{
						type: "added",
						description: "低代码引擎：表单 / 列表 / 标签页 / 关联页 4 类可视化配置"
					},
					{
						type: "added",
						description: "D365 / FP / OA 集成推送日志与重试机制"
					},
					{
						type: "added",
						description: "集成健康检查面板：实时监控外部系统连通性"
					},
					{
						type: "added",
						description: "消息中心与 WebSocket 实时通知"
					},
					{
						type: "added",
						description: "Punch List、RMA 返修、质保期管理模块"
					},
					{
						type: "added",
						description: "风险登记册、变更管理、问题日志（项目治理三件套）"
					},
					{
						type: "changed",
						description: "审计日志增强：操作日志、登录日志、异常日志、调度日志分类存储"
					},
					{
						type: "changed",
						description: "定时任务支持 Cron 表达式 + 失败重试"
					},
					{
						type: "fixed",
						description: "修复高并发下乐观锁冲突导致的更新失败"
					},
					{
						type: "fixed",
						description: "修复 Excel 导入大数据量内存溢出问题"
					},
					{
						type: "security",
						description: "升级 Spring Boot 至 3.2.5，修复已知 CVE"
					}
				]
			},
			{
				version: "v1.0.0",
				date: "2026-03-01",
				description: "初始正式版本，覆盖项目交付全生命周期",
				changes: [
					{
						type: "added",
						description: "项目管理：项目列表、详情、交付看板"
					},
					{
						type: "added",
						description: "资产管理：设备分类、型号、资产清单、状态流转"
					},
					{
						type: "added",
						description: "实施管理：施工任务、服务商、结算管理"
					},
					{
						type: "added",
						description: "工作流引擎：Activiti 集成、待办中心"
					},
					{
						type: "added",
						description: "报表统计：交付、资产、实施效能统计"
					},
					{
						type: "added",
						description: "系统管理：用户、角色、菜单、字典管理"
					},
					{
						type: "added",
						description: "安全：JWT 认证、RBAC 权限、字段加密、CSRF / XSS 防护"
					},
					{
						type: "added",
						description: "审计日志：操作日志、登录日志"
					},
					{
						type: "added",
						description: "定时任务：Quartz 调度"
					},
					{
						type: "added",
						description: "缓存管理：Redis 缓存查询与清理"
					}
				]
			}
		];
		const expandedVersions = ref(new Set(VERSIONS.map((v) => v.version)));
		function toggle(version) {
			if (expandedVersions.value.has(version)) expandedVersions.value.delete(version);
			else expandedVersions.value.add(version);
		}
		function isExpanded(version) {
			return expandedVersions.value.has(version);
		}
		function expandAll() {
			expandedVersions.value = new Set(VERSIONS.map((v) => v.version));
		}
		function collapseAll() {
			expandedVersions.value = /* @__PURE__ */ new Set();
		}
		const changeTypeMeta = {
			added: {
				label: "新增",
				tagType: "success"
			},
			changed: {
				label: "优化",
				tagType: "info"
			},
			deprecated: {
				label: "废弃",
				tagType: "warning"
			},
			removed: {
				label: "移除",
				tagType: "danger"
			},
			fixed: {
				label: "修复",
				tagType: "warning"
			},
			security: {
				label: "安全",
				tagType: "danger"
			}
		};
		function groupByVersion(changes) {
			const groups = {
				added: [],
				changed: [],
				deprecated: [],
				removed: [],
				fixed: [],
				security: []
			};
			changes.forEach((c) => {
				groups[c.type].push(c);
			});
			return groups;
		}
		const groupedVersions = computed(() => VERSIONS.map((v) => ({
			...v,
			grouped: groupByVersion(v.changes)
		})));
		const changeTypeOrder = [
			"added",
			"changed",
			"fixed",
			"deprecated",
			"removed",
			"security"
		];
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_ArrowDown = resolveComponent("ArrowDown");
			const _component_ArrowRight = resolveComponent("ArrowRight");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_card = resolveComponent("el-card");
			return openBlock(), createElementBlock("div", _hoisted_1, [createElementVNode("header", _hoisted_2, [_cache[2] || (_cache[2] = createElementVNode("h2", { class: "page-title" }, "版本日志", -1)), createElementVNode("div", _hoisted_3, [createVNode(_component_el_button, {
				size: "small",
				onClick: expandAll
			}, {
				default: withCtx(() => [..._cache[0] || (_cache[0] = [createTextVNode("全部展开", -1)])]),
				_: 1
			}), createVNode(_component_el_button, {
				size: "small",
				onClick: collapseAll
			}, {
				default: withCtx(() => [..._cache[1] || (_cache[1] = [createTextVNode("全部折叠", -1)])]),
				_: 1
			})])]), createElementVNode("div", _hoisted_4, [(openBlock(true), createElementBlock(Fragment, null, renderList(groupedVersions.value, (ver) => {
				return openBlock(), createBlock(_component_el_card, {
					key: ver.version,
					class: "version-card"
				}, {
					header: withCtx(() => [createElementVNode("div", {
						class: "version-header",
						onClick: ($event) => toggle(ver.version)
					}, [createElementVNode("div", _hoisted_6, [
						createVNode(_component_el_icon, { class: "version-header__toggle" }, {
							default: withCtx(() => [isExpanded(ver.version) ? (openBlock(), createBlock(_component_ArrowDown, { key: 0 })) : (openBlock(), createBlock(_component_ArrowRight, { key: 1 }))]),
							_: 2
						}, 1024),
						createElementVNode("span", _hoisted_7, toDisplayString(ver.version), 1),
						createVNode(_component_el_tag, {
							size: "small",
							type: "info"
						}, {
							default: withCtx(() => [createTextVNode(toDisplayString(ver.date), 1)]),
							_: 2
						}, 1024),
						ver.description ? (openBlock(), createElementBlock("span", _hoisted_8, toDisplayString(ver.description), 1)) : createCommentVNode("", true)
					]), createElementVNode("span", _hoisted_9, toDisplayString(ver.changes.length) + " 项变更", 1)], 8, _hoisted_5)]),
					default: withCtx(() => [withDirectives(createElementVNode("div", _hoisted_10, [(openBlock(), createElementBlock(Fragment, null, renderList(changeTypeOrder, (type) => {
						return createElementVNode("div", {
							key: type,
							class: "change-group"
						}, [ver.grouped[type].length > 0 ? (openBlock(), createElementBlock(Fragment, { key: 0 }, [createElementVNode("div", _hoisted_11, [createVNode(_component_el_tag, {
							type: changeTypeMeta[type].tagType,
							size: "small"
						}, {
							default: withCtx(() => [createTextVNode(toDisplayString(changeTypeMeta[type].label), 1)]),
							_: 2
						}, 1032, ["type"])]), createElementVNode("ul", _hoisted_12, [(openBlock(true), createElementBlock(Fragment, null, renderList(ver.grouped[type], (item, idx) => {
							return openBlock(), createElementBlock("li", {
								key: idx,
								class: "change-item"
							}, toDisplayString(item.description), 1);
						}), 128))])], 64)) : createCommentVNode("", true)]);
					}), 64))], 512), [[vShow, isExpanded(ver.version)]])]),
					_: 2
				}, 1024);
			}), 128))])]);
		};
	}
}), [["__scopeId", "data-v-d8d44cb8"]]);
//#endregion
export { changelog_default as default };

//# sourceMappingURL=changelog-CD96TvbK.js.map