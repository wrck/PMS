import { a as put, c as useUserStore, d as useRouter, l as defineStore, r as get, s as routeLoading, u as useRoute } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { t as createFeedback } from "./feedback-EQdFkxOX.js";
import { ElMessage, ElMessageBox, ElNotification } from "element-plus";
import { Fragment, Teleport, Transition, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, nextTick, normalizeClass, normalizeStyle, onBeforeUnmount, onMounted, openBlock, ref, renderList, resolveComponent, resolveDirective, resolveDynamicComponent, toDisplayString, unref, vShow, watch, withCtx, withDirectives } from "vue";
import { Bell, ChatLineRound, Close, RefreshRight } from "@element-plus/icons-vue";
//#region src/stores/app.ts
var useAppStore = defineStore("app", () => {
	const COLLAPSE_KEY = "pms_sidebar_collapsed";
	const sidebarCollapsed = ref(localStorage.getItem(COLLAPSE_KEY) === "true");
	function toggleSidebar() {
		sidebarCollapsed.value = !sidebarCollapsed.value;
		localStorage.setItem(COLLAPSE_KEY, String(sidebarCollapsed.value));
	}
	return {
		sidebarCollapsed,
		toggleSidebar
	};
});
//#endregion
//#region src/stores/websocket.ts
/**
* WebSocket 全局连接 store
* - 使用原生 WebSocket API 实现（不依赖 sockjs/stompjs）
* - 自动重连（5 秒间隔）
* - 收到通知时弹出 ElNotification 并累加未读数
*/
var useWebSocketStore = defineStore("websocket", () => {
	/** 是否已连接 */
	const connected = ref(false);
	/** WebSocket 实例 */
	const ws = ref(null);
	/** 重连定时器句柄 */
	const reconnectTimer = ref(null);
	/** 未读通知数 */
	const unreadCount = ref(0);
	/** 建立 WebSocket 连接 */
	function connect() {
		const token = localStorage.getItem("pms_token") || "";
		if (!token) return;
		const url = `${window.location.protocol === "https:" ? "wss:" : "ws:"}//${window.location.hostname}:8080/ws?token=${encodeURIComponent(token)}`;
		disconnect();
		const socket = new WebSocket(url);
		ws.value = socket;
		socket.onopen = () => {
			connected.value = true;
			console.log("[WS] 已连接");
		};
		socket.onmessage = (event) => {
			try {
				const data = JSON.parse(event.data);
				if (data.type === "notification" || data.title) {
					unreadCount.value++;
					ElNotification({
						title: data.title || "新通知",
						message: data.content || data.message || "",
						type: "info",
						duration: 5e3,
						onClick: () => {
							window.location.href = "/notification";
						}
					});
				}
			} catch (e) {
				console.warn("[WS] 消息解析失败", e);
			}
		};
		socket.onclose = () => {
			connected.value = false;
			console.log("[WS] 连接关闭，5秒后重连");
			scheduleReconnect();
		};
		socket.onerror = (error) => {
			console.error("[WS] 连接错误", error);
		};
	}
	/** 排定一次重连任务 */
	function scheduleReconnect() {
		if (reconnectTimer.value) clearTimeout(reconnectTimer.value);
		reconnectTimer.value = window.setTimeout(() => connect(), 5e3);
	}
	/** 主动断开连接（不会触发重连） */
	function disconnect() {
		if (reconnectTimer.value) {
			clearTimeout(reconnectTimer.value);
			reconnectTimer.value = null;
		}
		if (ws.value) {
			ws.value.onclose = null;
			ws.value.close();
			ws.value = null;
		}
		connected.value = false;
	}
	/** 重置未读数 */
	function resetUnread() {
		unreadCount.value = 0;
	}
	return {
		connected,
		unreadCount,
		connect,
		disconnect,
		resetUnread
	};
});
//#endregion
//#region src/stores/tags.ts
/**
* 标签页 store
* - 管理已访问过的路由标签列表
* - 标签持久化到 localStorage，刷新页面可恢复
*/
var useTagsStore = defineStore("tags", () => {
	const TAGS_KEY = "pms_tags_view";
	/** 已访问视图列表（从 localStorage 恢复） */
	const visitedViews = ref(safeParse(localStorage.getItem(TAGS_KEY) || "[]"));
	/** 安全解析 JSON，失败时返回空数组 */
	function safeParse(raw) {
		try {
			const arr = JSON.parse(raw);
			return Array.isArray(arr) ? arr : [];
		} catch (_unused) {
			return [];
		}
	}
	/** 持久化到 localStorage */
	function persist() {
		localStorage.setItem(TAGS_KEY, JSON.stringify(visitedViews.value));
	}
	/** 添加视图（已存在则跳过） */
	function addView(view) {
		if (visitedViews.value.some((v) => v.path === view.path)) {
			const target = visitedViews.value.find((v) => v.path === view.path);
			if (target) target.fullPath = view.fullPath || view.path;
			return;
		}
		visitedViews.value.push({
			...view,
			fullPath: view.fullPath || view.path
		});
		persist();
	}
	/** 删除指定路径的视图 */
	function delView(path) {
		const idx = visitedViews.value.findIndex((v) => v.path === path);
		if (idx !== -1) {
			visitedViews.value.splice(idx, 1);
			persist();
		}
	}
	/** 关闭其他视图，仅保留指定路径 */
	function delOthers(path) {
		visitedViews.value = visitedViews.value.filter((v) => v.path === path);
		persist();
	}
	/** 关闭全部视图 */
	function delAll() {
		visitedViews.value = [];
		persist();
	}
	return {
		visitedViews,
		addView,
		delView,
		delOthers,
		delAll
	};
});
//#endregion
//#region src/components/TagsView/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$4 = { class: "tags-view-container" };
var _hoisted_2$4 = { class: "tags-wrapper" };
var _hoisted_3$4 = ["onClick", "onContextmenu"];
var _hoisted_4$3 = {
	key: 0,
	class: "tag-dot"
};
var _hoisted_5$3 = { class: "tag-title" };
var _hoisted_6$3 = { class: "tag-actions" };
//#endregion
//#region src/components/TagsView/index.vue
var TagsView_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const route = useRoute();
		const router = useRouter();
		const tagsStore = useTagsStore();
		/** 标签栏横向滚动容器 */
		const scrollbarRef = ref(null);
		/** 右键菜单可见性 */
		const contextMenuVisible = ref(false);
		/** 右键菜单位置 */
		const contextMenuTop = ref(0);
		const contextMenuLeft = ref(0);
		/** 当前右键选中的标签路径 */
		const contextMenuPath = ref("");
		/** 当前激活的标签路径（基于 route.path） */
		const activePath = computed(() => route.path);
		/** 判断标签是否可关闭（只有一个标签时不允许关闭） */
		function isClosable(view) {
			return tagsStore.visitedViews.length > 1 || view.path !== "/dashboard";
		}
		/** 路由变化时自动添加标签 */
		function addCurrentView() {
			var _route$meta, _route$meta2;
			const title = ((_route$meta = route.meta) === null || _route$meta === void 0 ? void 0 : _route$meta.title) || "未命名";
			if (route.path === "/" || ((_route$meta2 = route.meta) === null || _route$meta2 === void 0 ? void 0 : _route$meta2.hidden)) return;
			tagsStore.addView({
				path: route.path,
				title,
				name: route.name,
				fullPath: route.fullPath
			});
			nextTick(() => scrollToActiveTag());
		}
		/** 点击标签跳转 */
		function handleClick(view) {
			if (view.path === activePath.value) return;
			router.push(view.fullPath || view.path);
		}
		/** 关闭标签 */
		function handleClose(view, event) {
			event === null || event === void 0 || event.stopPropagation();
			const wasActive = view.path === activePath.value;
			tagsStore.delView(view.path);
			if (wasActive) {
				const last = tagsStore.visitedViews[tagsStore.visitedViews.length - 1];
				router.push(last ? last.fullPath || last.path : "/dashboard");
			}
		}
		/** 刷新当前标签（先跳到一个临时路由再回来） */
		function refreshCurrent() {
			const path = contextMenuPath.value || activePath.value;
			router.replace("/redirect" + path).catch(() => {
				window.location.reload();
			});
		}
		/** 关闭其他 */
		function closeOthers() {
			const path = contextMenuPath.value || activePath.value;
			tagsStore.delOthers(path);
			const current = tagsStore.visitedViews.find((v) => v.path === path);
			if (current && path !== activePath.value) router.push(current.fullPath || current.path);
			hideContextMenu();
		}
		/** 关闭所有（保留首页） */
		function closeAll() {
			tagsStore.delAll();
			tagsStore.addView({
				path: "/dashboard",
				title: "首页",
				fullPath: "/dashboard"
			});
			router.push("/dashboard");
			hideContextMenu();
		}
		/** 右键打开上下文菜单 */
		function openContextMenu(event, view) {
			event.preventDefault();
			contextMenuPath.value = view.path;
			contextMenuTop.value = event.clientY;
			contextMenuLeft.value = event.clientX;
			contextMenuVisible.value = true;
		}
		/** 隐藏右键菜单 */
		function hideContextMenu() {
			contextMenuVisible.value = false;
		}
		/** 滚动到当前激活的标签 */
		function scrollToActiveTag() {
			var _scrollbarRef$value;
			const wrap = (_scrollbarRef$value = scrollbarRef.value) === null || _scrollbarRef$value === void 0 ? void 0 : _scrollbarRef$value.wrapRef;
			if (!wrap) return;
			wrap.scrollLeft = wrap.scrollWidth;
		}
		/** 监听路由变化 */
		watch(() => route.fullPath, () => addCurrentView(), { immediate: true });
		/** 点击页面其他位置隐藏右键菜单 */
		function onDocumentClick() {
			hideContextMenu();
		}
		onMounted(() => {
			document.addEventListener("click", onDocumentClick);
			if (!tagsStore.visitedViews.some((v) => v.path === "/dashboard")) tagsStore.addView({
				path: "/dashboard",
				title: "首页",
				fullPath: "/dashboard"
			});
		});
		onBeforeUnmount(() => {
			document.removeEventListener("click", onDocumentClick);
		});
		return (_ctx, _cache) => {
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_scrollbar = resolveComponent("el-scrollbar");
			const _component_el_tooltip = resolveComponent("el-tooltip");
			return openBlock(), createElementBlock("div", _hoisted_1$4, [
				createVNode(_component_el_scrollbar, {
					ref_key: "scrollbarRef",
					ref: scrollbarRef,
					class: "tags-scrollbar"
				}, {
					default: withCtx(() => [createElementVNode("div", _hoisted_2$4, [(openBlock(true), createElementBlock(Fragment, null, renderList(unref(tagsStore).visitedViews, (tag) => {
						return openBlock(), createElementBlock("div", {
							key: tag.path,
							class: normalizeClass(["tag-item", { active: tag.path === activePath.value }]),
							onClick: ($event) => handleClick(tag),
							onContextmenu: ($event) => openContextMenu($event, tag)
						}, [
							tag.path === activePath.value ? (openBlock(), createElementBlock("span", _hoisted_4$3)) : createCommentVNode("", true),
							createElementVNode("span", _hoisted_5$3, toDisplayString(tag.title), 1),
							isClosable(tag) ? (openBlock(), createBlock(_component_el_icon, {
								key: 1,
								class: "tag-close",
								onClick: ($event) => handleClose(tag, $event)
							}, {
								default: withCtx(() => [createVNode(unref(Close))]),
								_: 1
							}, 8, ["onClick"])) : createCommentVNode("", true)
						], 42, _hoisted_3$4);
					}), 128))])]),
					_: 1
				}, 512),
				createElementVNode("div", _hoisted_6$3, [createVNode(_component_el_tooltip, {
					content: "刷新当前",
					placement: "bottom"
				}, {
					default: withCtx(() => [createVNode(_component_el_icon, {
						class: "action-icon",
						onClick: refreshCurrent
					}, {
						default: withCtx(() => [createVNode(unref(RefreshRight))]),
						_: 1
					})]),
					_: 1
				})]),
				withDirectives(createElementVNode("ul", {
					class: "context-menu",
					style: normalizeStyle({
						top: contextMenuTop.value + "px",
						left: contextMenuLeft.value + "px"
					})
				}, [
					createElementVNode("li", { onClick: refreshCurrent }, "刷新当前"),
					createElementVNode("li", { onClick: closeOthers }, "关闭其他"),
					createElementVNode("li", { onClick: closeAll }, "关闭所有")
				], 4), [[vShow, contextMenuVisible.value]])
			]);
		};
	}
}), [["__scopeId", "data-v-c2a190da"]]);
//#endregion
//#region src/components/NotificationBell/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$3 = { class: "notification-panel" };
var _hoisted_2$3 = { class: "panel-header" };
var _hoisted_3$3 = {
	key: 0,
	class: "panel-count"
};
var _hoisted_4$2 = { class: "panel-body" };
var _hoisted_5$2 = {
	key: 0,
	class: "empty-tip"
};
var _hoisted_6$2 = ["onClick"];
var _hoisted_7$2 = { class: "notify-head" };
var _hoisted_8$2 = { class: "notify-time" };
var _hoisted_9$1 = { class: "notify-title" };
var _hoisted_10$1 = {
	key: 0,
	class: "notify-content"
};
//#endregion
//#region src/components/NotificationBell/index.vue
var NotificationBell_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const router = useRouter();
		/** 未读数 */
		const unreadCount = ref(0);
		/** 最近通知列表 */
		const recentList = ref([]);
		/** 加载中 */
		const loading = ref(false);
		/** 轮询定时器 */
		let timer = null;
		/** 拉取未读数 */
		async function fetchUnread() {
			try {
				const count = await get("/api/notification/unread/count");
				unreadCount.value = typeof count === "number" ? count : 0;
			} catch (_unused) {}
		}
		/** 拉取最近 5 条未读通知 */
		async function fetchRecent() {
			loading.value = true;
			try {
				const res = await get("/api/notification/page", {
					page: 1,
					size: 5,
					readStatus: "UNREAD"
				});
				recentList.value = (res === null || res === void 0 ? void 0 : res.records) || [];
			} catch (_unused2) {
				recentList.value = [];
			} finally {
				loading.value = false;
			}
		}
		/** 标记单条为已读并跳转 */
		async function markAsRead(item) {
			try {
				await put(`/api/notification/${item.id}/read`);
				const idx = recentList.value.findIndex((n) => n.id === item.id);
				if (idx !== -1) recentList.value.splice(idx, 1);
				if (unreadCount.value > 0) unreadCount.value--;
			} catch (_unused3) {}
			if (item.bizUrl) router.push(item.bizUrl);
		}
		/** 跳转消息中心 */
		function viewAll() {
			router.push("/notification");
		}
		/** 格式化时间 */
		function formatTime(time) {
			if (!time) return "";
			return time.length >= 16 ? time.slice(0, 16).replace("T", " ") : time;
		}
		/** 分类标签颜色 */
		function categoryType(category) {
			switch (category) {
				case "TASK":
				case "WORKFLOW": return "success";
				case "WARN":
				case "ALERT": return "warning";
				case "ERROR":
				case "URGENT": return "danger";
				default: return "info";
			}
		}
		onMounted(() => {
			fetchUnread();
			fetchRecent();
			timer = window.setInterval(fetchUnread, 3e4);
		});
		onBeforeUnmount(() => {
			if (timer) {
				clearInterval(timer);
				timer = null;
			}
		});
		return (_ctx, _cache) => {
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_badge = resolveComponent("el-badge");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_popover = resolveComponent("el-popover");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createBlock(_component_el_popover, {
				placement: "bottom-end",
				width: 360,
				trigger: "click",
				"popper-class": "notification-popover"
			}, {
				reference: withCtx(() => [createVNode(_component_el_badge, {
					value: unreadCount.value,
					hidden: unreadCount.value === 0,
					max: 99,
					class: "bell-badge"
				}, {
					default: withCtx(() => [createVNode(_component_el_icon, {
						class: "bell-icon",
						size: 20
					}, {
						default: withCtx(() => [createVNode(unref(Bell))]),
						_: 1
					})]),
					_: 1
				}, 8, ["value", "hidden"])]),
				default: withCtx(() => [createElementVNode("div", _hoisted_1$3, [
					createElementVNode("div", _hoisted_2$3, [_cache[0] || (_cache[0] = createElementVNode("span", { class: "panel-title" }, "通知", -1)), unreadCount.value > 0 ? (openBlock(), createElementBlock("span", _hoisted_3$3, toDisplayString(unreadCount.value) + " 条未读", 1)) : createCommentVNode("", true)]),
					withDirectives((openBlock(), createElementBlock("div", _hoisted_4$2, [!recentList.value.length && !loading.value ? (openBlock(), createElementBlock("div", _hoisted_5$2, " 暂无未读通知 ")) : createCommentVNode("", true), (openBlock(true), createElementBlock(Fragment, null, renderList(recentList.value, (item) => {
						return openBlock(), createElementBlock("div", {
							key: item.id,
							class: "notify-item",
							onClick: ($event) => markAsRead(item)
						}, [
							createElementVNode("div", _hoisted_7$2, [createVNode(_component_el_tag, {
								size: "small",
								type: categoryType(item.category),
								effect: "plain"
							}, {
								default: withCtx(() => [createTextVNode(toDisplayString(item.category), 1)]),
								_: 2
							}, 1032, ["type"]), createElementVNode("span", _hoisted_8$2, toDisplayString(formatTime(item.createdAt)), 1)]),
							createElementVNode("div", _hoisted_9$1, toDisplayString(item.title), 1),
							item.content ? (openBlock(), createElementBlock("div", _hoisted_10$1, toDisplayString(item.content), 1)) : createCommentVNode("", true)
						], 8, _hoisted_6$2);
					}), 128))])), [[_directive_loading, loading.value]]),
					createElementVNode("div", {
						class: "panel-footer",
						onClick: viewAll
					}, " 查看全部 ")
				])]),
				_: 1
			});
		};
	}
}), [["__scopeId", "data-v-a16b8db9"]]);
//#endregion
//#region src/composables/useFirstLogin.ts
/** localStorage key 前缀：按用户名区分首次登录标记 */
var FIRST_LOGIN_KEY_PREFIX = "pms_first_login_done_";
/**
* 首次登录检测 composable。
*
* <p>判定策略：基于 localStorage 中按用户名存储的「引导已读」标记。
* 用户切换或重新登录后，新用户名对应的标记不存在时即视为首次登录。
* 若用户名不可用（未登录），降级为匿名 key（仅在登录前调用时使用）。</p>
*
* <p>本实现为纯前端方案，避免引入额外的后端 user prefs 字段。
* 如需后端持久化，可在 markCompleted() 中追加调用 user prefs 接口。</p>
*
* 用法：
*   const { isFirstLogin, markCompleted } = useFirstLogin()
*   onMounted(() => { if (isFirstLogin.value) startGuide() })
*/
function useFirstLogin() {
	const userStore = useUserStore();
	/** 当前用户名（未登录时为空字符串） */
	const username = computed(() => {
		var _userStore$userInfo;
		return ((_userStore$userInfo = userStore.userInfo) === null || _userStore$userInfo === void 0 ? void 0 : _userStore$userInfo.username) || "";
	});
	/** localStorage key：按用户名区分 */
	const storageKey = computed(() => `${FIRST_LOGIN_KEY_PREFIX}${username.value || "anonymous"}`);
	/** 是否首次登录（响应式） */
	const isFirstLogin = ref(!localStorage.getItem(storageKey.value));
	/**
	* 标记当前用户的引导已完成。
	* 写入 localStorage，并将 isFirstLogin 置为 false。
	*/
	function markCompleted() {
		try {
			localStorage.setItem(storageKey.value, String(Date.now()));
		} catch (_unused) {}
		isFirstLogin.value = false;
	}
	/**
	* 重置当前用户的引导标记（用于调试或重新触发引导）。
	*/
	function reset() {
		try {
			localStorage.removeItem(storageKey.value);
		} catch (_unused2) {}
		isFirstLogin.value = true;
	}
	return {
		isFirstLogin,
		markCompleted,
		reset
	};
}
//#endregion
//#region src/components/UserGuide/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$2 = {
	key: 0,
	class: "user-guide"
};
var _hoisted_2$2 = {
	key: 1,
	class: "guide-popover guide-popover--center"
};
var _hoisted_3$2 = { class: "guide-popover__header" };
var _hoisted_4$1 = { class: "guide-popover__title" };
var _hoisted_5$1 = { class: "guide-popover__body" };
var _hoisted_6$1 = { class: "guide-popover__desc" };
var _hoisted_7$1 = { class: "guide-popover__footer" };
var _hoisted_8$1 = { class: "guide-popover__step" };
var _hoisted_9 = { class: "guide-popover__actions" };
var _hoisted_10 = { class: "guide-popover__header" };
var _hoisted_11 = { class: "guide-popover__title" };
var _hoisted_12 = { class: "guide-popover__body" };
var _hoisted_13 = { class: "guide-popover__desc" };
var _hoisted_14 = { class: "guide-popover__footer" };
var _hoisted_15 = { class: "guide-popover__step" };
var _hoisted_16 = { class: "guide-popover__actions" };
//#endregion
//#region src/components/UserGuide/index.vue
var UserGuide_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	props: { visible: { type: Boolean } },
	emits: ["finish", "skip"],
	setup(__props, { expose: __expose, emit: __emit }) {
		const STEPS = [
			{
				title: "欢迎使用网络设备工程项目管理系统",
				description: "本系统覆盖项目交付、资产管理、实施任务、质保治理等全生命周期。\n\n接下来用 30 秒带您熟悉核心模块，可随时点击「跳过」结束引导。",
				placement: "center"
			},
			{
				title: "项目管理",
				description: "在「项目管理」中创建并跟踪项目全生命周期：项目列表、交付看板、变更管理、风险登记册、问题日志等。",
				selector: "a[href=\"/project/list\"], .el-menu-item[index=\"/project/list\"]",
				placement: "right"
			},
			{
				title: "资产管理",
				description: "在「资产管理」中维护设备分类、型号、资产清单，跟踪在库 / 调拨 / 报废状态，并与项目、实施任务关联。",
				selector: "a[href=\"/asset/list\"], .el-menu-item[index=\"/asset/list\"]",
				placement: "right"
			},
			{
				title: "任务实施",
				description: "在「实施管理」中派发施工任务、管理服务商、结算费用，全流程在线协同与进度跟踪。",
				selector: "a[href=\"/implementation/task\"], .el-menu-item[index=\"/implementation/task\"]",
				placement: "right"
			},
			{
				title: "仪表盘",
				description: "回到「首页」仪表盘，实时查看项目交付、资产状态、待办任务、近期动态等关键指标。\n\n引导到此结束，祝您使用愉快！",
				selector: "a[href=\"/dashboard\"], .el-menu-item[index=\"/dashboard\"]",
				placement: "right"
			}
		];
		const props = __props;
		const emit = __emit;
		const { isFirstLogin, markCompleted } = useFirstLogin();
		/** 内部可见状态：首次登录 或 props.visible 为 true 时显示 */
		const internalVisible = ref(props.visible === true || isFirstLogin.value);
		/** 当前步骤索引 */
		const currentStep = ref(0);
		/** 当前步骤定义 */
		const current = computed(() => {
			var _STEPS$currentStep$va;
			return (_STEPS$currentStep$va = STEPS[currentStep.value]) !== null && _STEPS$currentStep$va !== void 0 ? _STEPS$currentStep$va : STEPS[0];
		});
		/** 是否最后一步 */
		const isLast = computed(() => currentStep.value === STEPS.length - 1);
		/** 目标元素的 bounding rect（找不到时为 null，回退到居中卡片） */
		const targetRect = ref(null);
		/** 气泡位置（屏幕坐标，px） */
		const popoverStyle = ref({});
		/** 滚动事件处理器引用（用于解绑） */
		let scrollHandler = null;
		/**
		* 计算目标元素位置并定位气泡。
		* 在 nextTick 后执行，确保 DOM 已更新。
		*/
		async function updatePosition() {
			if (!internalVisible.value) return;
			const step = current.value;
			if (step.placement === "center" || !step.selector) {
				targetRect.value = null;
				popoverStyle.value = {};
				return;
			}
			const candidates = document.querySelectorAll(step.selector);
			let el = null;
			for (const node of Array.from(candidates)) if (!el) {
				const rect = node.getBoundingClientRect();
				if (rect.width > 0 && rect.height > 0) el = node;
			}
			if (!el) {
				targetRect.value = null;
				popoverStyle.value = {};
				return;
			}
			const rect = el.getBoundingClientRect();
			targetRect.value = rect;
			const POPOVER_WIDTH = 360;
			const POPOVER_MAX_HEIGHT = 280;
			const GAP = 16;
			const vw = window.innerWidth;
			const vh = window.innerHeight;
			let top = 0;
			let left = 0;
			switch (step.placement) {
				case "right":
					top = rect.top + rect.height / 2 - POPOVER_MAX_HEIGHT / 2;
					left = rect.right + GAP;
					if (left + POPOVER_WIDTH > vw - 8) left = Math.max(8, rect.left - POPOVER_WIDTH - GAP);
					break;
				case "left":
					top = rect.top + rect.height / 2 - POPOVER_MAX_HEIGHT / 2;
					left = rect.left - POPOVER_WIDTH - GAP;
					if (left < 8) left = Math.min(vw - POPOVER_WIDTH - 8, rect.right + GAP);
					break;
				case "bottom":
					left = rect.left + rect.width / 2 - POPOVER_WIDTH / 2;
					top = rect.bottom + GAP;
					if (top + POPOVER_MAX_HEIGHT > vh - 8) top = Math.max(8, rect.top - POPOVER_MAX_HEIGHT - GAP);
					break;
				case "top":
					left = rect.left + rect.width / 2 - POPOVER_WIDTH / 2;
					top = rect.top - POPOVER_MAX_HEIGHT - GAP;
					if (top < 8) top = Math.min(vh - POPOVER_MAX_HEIGHT - 8, rect.bottom + GAP);
					break;
				default:
					top = (vh - POPOVER_MAX_HEIGHT) / 2;
					left = (vw - POPOVER_WIDTH) / 2;
			}
			top = Math.max(8, Math.min(top, vh - POPOVER_MAX_HEIGHT - 8));
			left = Math.max(8, Math.min(left, vw - POPOVER_WIDTH - 8));
			popoverStyle.value = {
				top: `${top}px`,
				left: `${left}px`,
				width: `${POPOVER_WIDTH}px`,
				maxHeight: `${POPOVER_MAX_HEIGHT}px`
			};
		}
		/**
		* spotlight overlay 的样式：使用 box-shadow 在屏幕上「挖洞」突出目标元素。
		* 当 targetRect 为 null（居中卡片）时，使用半透明遮罩。
		*/
		const overlayStyle = computed(() => {
			if (!targetRect.value) return { boxShadow: "none" };
			const r = targetRect.value;
			const padding = 6;
			return {
				position: "fixed",
				top: `${r.top - padding}px`,
				left: `${r.left - padding}px`,
				width: `${r.width + padding * 2}px`,
				height: `${r.height + padding * 2}px`,
				boxShadow: `0 0 0 9999px rgba(0, 0, 0, 0.55)`,
				borderRadius: "6px",
				transition: "all 0.25s ease",
				pointerEvents: "none"
			};
		});
		/** 跳过引导 */
		function skip() {
			internalVisible.value = false;
			markCompleted();
			emit("skip");
			cleanupListeners();
		}
		/** 下一步 / 完成 */
		async function next() {
			if (isLast.value) {
				internalVisible.value = false;
				markCompleted();
				emit("finish");
				cleanupListeners();
				return;
			}
			currentStep.value++;
			await nextTick();
			updatePosition();
		}
		/** 上一步 */
		async function prev() {
			if (currentStep.value === 0) return;
			currentStep.value--;
			await nextTick();
			updatePosition();
		}
		function bindScrollListener() {
			if (scrollHandler) return;
			scrollHandler = () => updatePosition();
			window.addEventListener("scroll", scrollHandler, true);
			window.addEventListener("resize", scrollHandler);
		}
		function cleanupListeners() {
			if (scrollHandler) {
				window.removeEventListener("scroll", scrollHandler, true);
				window.removeEventListener("resize", scrollHandler);
				scrollHandler = null;
			}
		}
		watch(() => props.visible, (v) => {
			if (v && !internalVisible.value) {
				internalVisible.value = true;
				currentStep.value = 0;
			}
		});
		watch(internalVisible, async (v) => {
			if (v) {
				bindScrollListener();
				await nextTick();
				updatePosition();
			} else cleanupListeners();
		}, { immediate: true });
		onBeforeUnmount(() => {
			cleanupListeners();
		});
		__expose({ 
		/** 外部手动启动引导 */
start() {
			currentStep.value = 0;
			internalVisible.value = true;
		} });
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			return openBlock(), createBlock(Teleport, { to: "body" }, [internalVisible.value ? (openBlock(), createElementBlock("div", _hoisted_1$2, [
				_cache[4] || (_cache[4] = createElementVNode("div", { class: "guide-mask" }, null, -1)),
				targetRect.value ? (openBlock(), createElementBlock("div", {
					key: 0,
					style: normalizeStyle(overlayStyle.value),
					class: "guide-spotlight"
				}, null, 4)) : createCommentVNode("", true),
				current.value.placement === "center" || !targetRect.value ? (openBlock(), createElementBlock("div", _hoisted_2$2, [
					createElementVNode("div", _hoisted_3$2, [createElementVNode("span", _hoisted_4$1, toDisplayString(current.value.title), 1), createElementVNode("button", {
						class: "guide-popover__close",
						type: "button",
						onClick: skip
					}, "×")]),
					createElementVNode("div", _hoisted_5$1, [createElementVNode("p", _hoisted_6$1, toDisplayString(current.value.description), 1)]),
					createElementVNode("div", _hoisted_7$1, [createElementVNode("span", _hoisted_8$1, toDisplayString(currentStep.value + 1) + " / " + toDisplayString(STEPS.length), 1), createElementVNode("div", _hoisted_9, [
						createVNode(_component_el_button, {
							size: "small",
							link: "",
							onClick: skip
						}, {
							default: withCtx(() => [..._cache[0] || (_cache[0] = [createTextVNode("跳过", -1)])]),
							_: 1
						}),
						currentStep.value > 0 ? (openBlock(), createBlock(_component_el_button, {
							key: 0,
							size: "small",
							onClick: prev
						}, {
							default: withCtx(() => [..._cache[1] || (_cache[1] = [createTextVNode("上一步", -1)])]),
							_: 1
						})) : createCommentVNode("", true),
						createVNode(_component_el_button, {
							size: "small",
							type: "primary",
							onClick: next
						}, {
							default: withCtx(() => [createTextVNode(toDisplayString(isLast.value ? "完成" : "下一步"), 1)]),
							_: 1
						})
					])])
				])) : (openBlock(), createElementBlock("div", {
					key: 2,
					class: "guide-popover guide-popover--anchored",
					style: normalizeStyle(popoverStyle.value)
				}, [
					createElementVNode("div", _hoisted_10, [createElementVNode("span", _hoisted_11, toDisplayString(current.value.title), 1), createElementVNode("button", {
						class: "guide-popover__close",
						type: "button",
						onClick: skip
					}, "×")]),
					createElementVNode("div", _hoisted_12, [createElementVNode("p", _hoisted_13, toDisplayString(current.value.description), 1)]),
					createElementVNode("div", _hoisted_14, [createElementVNode("span", _hoisted_15, toDisplayString(currentStep.value + 1) + " / " + toDisplayString(STEPS.length), 1), createElementVNode("div", _hoisted_16, [
						createVNode(_component_el_button, {
							size: "small",
							link: "",
							onClick: skip
						}, {
							default: withCtx(() => [..._cache[2] || (_cache[2] = [createTextVNode("跳过", -1)])]),
							_: 1
						}),
						currentStep.value > 0 ? (openBlock(), createBlock(_component_el_button, {
							key: 0,
							size: "small",
							onClick: prev
						}, {
							default: withCtx(() => [..._cache[3] || (_cache[3] = [createTextVNode("上一步", -1)])]),
							_: 1
						})) : createCommentVNode("", true),
						createVNode(_component_el_button, {
							size: "small",
							type: "primary",
							onClick: next
						}, {
							default: withCtx(() => [createTextVNode(toDisplayString(isLast.value ? "完成" : "下一步"), 1)]),
							_: 1
						})
					])])
				], 4))
			])) : createCommentVNode("", true)]);
		};
	}
}), [["__scopeId", "data-v-a1342ffc"]]);
//#endregion
//#region src/components/FeedbackButton/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1$1 = { class: "feedback-button" };
var _hoisted_2$1 = ["title"];
var _hoisted_3$1 = {
	key: 0,
	class: "feedback-fab__cooldown"
};
var RATE_LIMIT_SECONDS = 60;
//#endregion
//#region src/components/FeedbackButton/index.vue
var FeedbackButton_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const dialogVisible = ref(false);
		const submitting = ref(false);
		/** 剩余冷却秒数（提交成功后倒计时） */
		const cooldown = ref(0);
		let cooldownTimer = null;
		const form = ref({
			category: "BUG",
			title: "",
			content: "",
			contact: ""
		});
		const categoryOptions = [
			{
				label: "问题反馈 (BUG)",
				value: "BUG"
			},
			{
				label: "功能建议",
				value: "SUGGESTION"
			},
			{
				label: "使用咨询",
				value: "QUESTION"
			},
			{
				label: "其他",
				value: "OTHER"
			}
		];
		const canSubmit = computed(() => !submitting.value && cooldown.value === 0 && form.value.title.trim().length > 0 && form.value.content.trim().length > 0);
		function resetForm() {
			form.value = {
				category: "BUG",
				title: "",
				content: "",
				contact: ""
			};
		}
		function openDialog() {
			if (cooldown.value > 0) {
				ElMessage.warning(`提交过于频繁，请 ${cooldown.value} 秒后再试`);
				return;
			}
			resetForm();
			dialogVisible.value = true;
		}
		function startCooldown() {
			cooldown.value = RATE_LIMIT_SECONDS;
			if (cooldownTimer) clearInterval(cooldownTimer);
			cooldownTimer = setInterval(() => {
				cooldown.value--;
				if (cooldown.value <= 0) {
					if (cooldownTimer) {
						clearInterval(cooldownTimer);
						cooldownTimer = null;
					}
					cooldown.value = 0;
				}
			}, 1e3);
		}
		async function submit() {
			if (!canSubmit.value) return;
			submitting.value = true;
			try {
				await createFeedback({
					category: form.value.category,
					title: form.value.title.trim(),
					content: form.value.content.trim(),
					contact: form.value.contact.trim() || void 0
				});
				ElMessage.success("反馈已提交，我们将尽快处理，感谢您的支持！");
				dialogVisible.value = false;
				startCooldown();
			} catch (_unused) {} finally {
				submitting.value = false;
			}
		}
		function handleClose() {
			if (submitting.value) return;
			dialogVisible.value = false;
		}
		return (_ctx, _cache) => {
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_radio = resolveComponent("el-radio");
			const _component_el_radio_group = resolveComponent("el-radio-group");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_dialog = resolveComponent("el-dialog");
			return openBlock(), createElementBlock("div", _hoisted_1$1, [createElementVNode("button", {
				class: "feedback-fab",
				type: "button",
				title: cooldown.value > 0 ? `冷却中（${cooldown.value}s）` : "提交反馈",
				onClick: openDialog
			}, [
				createVNode(_component_el_icon, { size: 20 }, {
					default: withCtx(() => [createVNode(unref(ChatLineRound))]),
					_: 1
				}),
				_cache[5] || (_cache[5] = createElementVNode("span", { class: "feedback-fab__label" }, "反馈", -1)),
				cooldown.value > 0 ? (openBlock(), createElementBlock("span", _hoisted_3$1, toDisplayString(cooldown.value) + "s", 1)) : createCommentVNode("", true)
			], 8, _hoisted_2$1), createVNode(_component_el_dialog, {
				modelValue: dialogVisible.value,
				"onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => dialogVisible.value = $event),
				title: "提交反馈",
				width: "520",
				"close-on-click-modal": false,
				"before-close": handleClose
			}, {
				footer: withCtx(() => [createVNode(_component_el_button, {
					icon: unref(Close),
					onClick: handleClose
				}, {
					default: withCtx(() => [..._cache[6] || (_cache[6] = [createTextVNode("取消", -1)])]),
					_: 1
				}, 8, ["icon"]), createVNode(_component_el_button, {
					type: "primary",
					loading: submitting.value,
					disabled: !canSubmit.value,
					onClick: submit
				}, {
					default: withCtx(() => [..._cache[7] || (_cache[7] = [createTextVNode(" 提交反馈 ", -1)])]),
					_: 1
				}, 8, ["loading", "disabled"])]),
				default: withCtx(() => [createVNode(_component_el_form, {
					model: form.value,
					"label-width": "80px",
					"label-position": "right"
				}, {
					default: withCtx(() => [
						createVNode(_component_el_form_item, {
							label: "类型",
							required: ""
						}, {
							default: withCtx(() => [createVNode(_component_el_radio_group, {
								modelValue: form.value.category,
								"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => form.value.category = $event)
							}, {
								default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(categoryOptions, (opt) => {
									return createVNode(_component_el_radio, {
										key: opt.value,
										value: opt.value
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(opt.label), 1)]),
										_: 2
									}, 1032, ["value"]);
								}), 64))]),
								_: 1
							}, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, {
							label: "标题",
							required: ""
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: form.value.title,
								"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => form.value.title = $event),
								placeholder: "请简短描述您的问题或建议",
								maxlength: "200",
								"show-word-limit": ""
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, {
							label: "内容",
							required: ""
						}, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: form.value.content,
								"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => form.value.content = $event),
								type: "textarea",
								rows: 5,
								placeholder: "请详细描述问题现象、复现步骤或建议内容",
								maxlength: "4000",
								"show-word-limit": ""
							}, null, 8, ["modelValue"])]),
							_: 1
						}),
						createVNode(_component_el_form_item, { label: "联系方式" }, {
							default: withCtx(() => [createVNode(_component_el_input, {
								modelValue: form.value.contact,
								"onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => form.value.contact = $event),
								placeholder: "选填，电话或邮箱，便于我们回复",
								maxlength: "100"
							}, null, 8, ["modelValue"])]),
							_: 1
						})
					]),
					_: 1
				}, 8, ["model"])]),
				_: 1
			}, 8, ["modelValue"])]);
		};
	}
}), [["__scopeId", "data-v-7835649c"]]);
//#endregion
//#region src/layouts/DefaultLayout.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "logo" };
var _hoisted_2 = { class: "logo-text" };
var _hoisted_3 = { class: "logo" };
var _hoisted_4 = { class: "header-left" };
var _hoisted_5 = { class: "header-right" };
var _hoisted_6 = { class: "user-info" };
var _hoisted_7 = {
	key: 0,
	class: "username"
};
var _hoisted_8 = {
	key: 0,
	class: "route-loading-bar"
};
var MOBILE_BREAKPOINT = 768;
//#endregion
//#region src/layouts/DefaultLayout.vue
var DefaultLayout_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "DefaultLayout",
	setup(__props) {
		const appStore = useAppStore();
		const userStore = useUserStore();
		const websocketStore = useWebSocketStore();
		const route = useRoute();
		const router = useRouter();
		const isMobile = ref(false);
		/** 移动端抽屉可见性 */
		const drawerVisible = ref(false);
		function updateMobileFlag() {
			isMobile.value = typeof window !== "undefined" && (window.matchMedia ? window.matchMedia(`(max-width: ${MOBILE_BREAKPOINT}px)`).matches : window.innerWidth <= MOBILE_BREAKPOINT);
			if (!isMobile.value) drawerVisible.value = false;
		}
		if (typeof window !== "undefined") {
			updateMobileFlag();
			window.addEventListener("resize", updateMobileFlag);
		}
		const menuGroups = [
			{
				title: "首页",
				path: "/dashboard",
				icon: "HomeFilled"
			},
			{
				title: "系统管理",
				icon: "Setting",
				children: [
					{
						title: "用户管理",
						path: "/system/user",
						icon: "User"
					},
					{
						title: "角色管理",
						path: "/system/role",
						icon: "UserFilled"
					},
					{
						title: "菜单管理",
						path: "/system/menu",
						icon: "Menu"
					},
					{
						title: "字典管理",
						path: "/system/dict",
						icon: "Document"
					}
				]
			},
			{
				title: "项目管理",
				icon: "Folder",
				children: [{
					title: "项目列表",
					path: "/project/list",
					icon: "Folder"
				}, {
					title: "交付看板",
					path: "/project/kanban",
					icon: "Grid"
				}]
			},
			{
				title: "资产管理",
				icon: "Box",
				children: [
					{
						title: "设备分类",
						path: "/asset/category",
						icon: "Files"
					},
					{
						title: "设备型号",
						path: "/asset/model",
						icon: "Box"
					},
					{
						title: "资产清单",
						path: "/asset/list",
						icon: "List"
					}
				]
			},
			{
				title: "实施管理",
				icon: "Tools",
				children: [
					{
						title: "实施任务",
						path: "/implementation/task",
						icon: "Tickets"
					},
					{
						title: "服务商管理",
						path: "/implementation/agent",
						icon: "OfficeBuilding"
					},
					{
						title: "结算管理",
						path: "/implementation/settlement",
						icon: "Money"
					}
				]
			},
			{
				title: "工作流",
				icon: "Connection",
				children: [{
					title: "待办中心",
					path: "/workflow/todo",
					icon: "Bell"
				}]
			},
			{
				title: "交付治理",
				icon: "Operation",
				children: [
					{
						title: "Punch List",
						path: "/punch-list",
						icon: "WarningFilled"
					},
					{
						title: "RMA 返修",
						path: "/rma",
						icon: "RefreshRight"
					},
					{
						title: "质保期管理",
						path: "/warranty",
						icon: "Timer"
					},
					{
						title: "终验交付物",
						path: "/deliverable",
						icon: "Document"
					}
				]
			},
			{
				title: "项目治理",
				icon: "SetUp",
				children: [
					{
						title: "风险登记册",
						path: "/risk",
						icon: "Warning"
					},
					{
						title: "变更管理",
						path: "/change-request",
						icon: "EditPen"
					},
					{
						title: "问题日志",
						path: "/issue",
						icon: "ChatLineSquare"
					}
				]
			},
			{
				title: "系统监控",
				icon: "DataLine",
				children: [
					{
						title: "消息中心",
						path: "/notification",
						icon: "Bell"
					},
					{
						title: "集成健康",
						path: "/integration-health",
						icon: "Monitor"
					},
					{
						title: "系统状态",
						path: "/system-status",
						icon: "Monitor"
					},
					{
						title: "缓存管理",
						path: "/system/cache",
						icon: "Coin"
					},
					{
						title: "定时任务",
						path: "/system/schedule",
						icon: "Timer"
					},
					{
						title: "审计日志",
						path: "/system/audit",
						icon: "DocumentChecked"
					},
					{
						title: "版本日志",
						path: "/changelog",
						icon: "Notebook"
					}
				]
			},
			{
				title: "报表统计",
				path: "/report",
				icon: "TrendCharts"
			},
			{
				title: "低代码",
				icon: "MagicStick",
				children: [
					{
						title: "实体设计器",
						path: "/lowcode/entity-designer",
						icon: "Connection"
					},
					{
						title: "表单配置",
						path: "/lowcode/form-list",
						icon: "Document"
					},
					{
						title: "列表配置",
						path: "/lowcode/list-list",
						icon: "List"
					},
					{
						title: "标签页配置",
						path: "/lowcode/tab-list",
						icon: "Files"
					},
					{
						title: "关联页配置",
						path: "/lowcode/related-page-list",
						icon: "Share"
					},
					{
						title: "微流设计器",
						path: "/lowcode/microflow-designer",
						icon: "Share"
					},
					{
						title: "规则设计器",
						path: "/lowcode/rule-designer",
						icon: "Filter"
					},
					{
						title: "流程设计器",
						path: "/lowcode/process-designer",
						icon: "Connection"
					},
					{
						title: "触发器",
						path: "/lowcode/trigger-list",
						icon: "BellFilled"
					},
					{
						title: "连接器配置",
						path: "/lowcode/connector-designer",
						icon: "Connection"
					},
					{
						title: "发布中心",
						path: "/lowcode/publish-center",
						icon: "Promotion"
					},
					{
						title: "审批链配置",
						path: "/lowcode/approval-chain",
						icon: "SetUp"
					},
					{
						title: "版本历史",
						path: "/lowcode/version-history",
						icon: "Timer"
					},
					{
						title: "模板市场",
						path: "/lowcode/template-market",
						icon: "Goods"
					},
					{
						title: "APM 看板",
						path: "/lowcode/apm-dashboard",
						icon: "TrendCharts"
					},
					{
						title: "应用源码导出",
						path: "/lowcode/app-source-export",
						icon: "Download"
					}
				]
			},
			{
				title: "演示中心",
				icon: "Star",
				children: [{
					title: "员工列表",
					path: "/lowcode/list/list_demo_employee",
					icon: "User"
				}, {
					title: "员工档案",
					path: "/lowcode/form/form_demo_employee",
					icon: "Document"
				}]
			}
		];
		const activeMenu = computed(() => route.path);
		const breadcrumbs = computed(() => route.matched.filter((r) => {
			var _r$meta;
			return (_r$meta = r.meta) === null || _r$meta === void 0 ? void 0 : _r$meta.title;
		}).map((r) => ({
			title: r.meta.title,
			path: r.path
		})));
		const username = computed(() => {
			var _userStore$userInfo, _userStore$userInfo2;
			return ((_userStore$userInfo = userStore.userInfo) === null || _userStore$userInfo === void 0 ? void 0 : _userStore$userInfo.nickname) || ((_userStore$userInfo2 = userStore.userInfo) === null || _userStore$userInfo2 === void 0 ? void 0 : _userStore$userInfo2.username) || "用户";
		});
		/** 移动端点击菜单项后关闭抽屉 */
		function handleMenuSelect() {
			if (isMobile.value) drawerVisible.value = false;
		}
		/** 切换移动端抽屉 */
		function toggleMobileDrawer() {
			drawerVisible.value = !drawerVisible.value;
		}
		function handleUserCommand(command) {
			if (command === "logout") ElMessageBox.confirm("确定要退出登录吗？", "提示", { type: "warning" }).then(() => userStore.logout()).catch(() => {});
			else if (command === "dashboard") router.push("/dashboard");
		}
		/** 用户引导组件实例（用于手动触发引导） */
		const userGuideRef = ref(null);
		/** 跳转到帮助中心页 */
		function goHelp() {
			router.push("/help");
		}
		/** 手动触发用户引导（点击「引导」按钮时调用） */
		function startGuide() {
			var _userGuideRef$value;
			(_userGuideRef$value = userGuideRef.value) === null || _userGuideRef$value === void 0 || _userGuideRef$value.start();
		}
		onMounted(() => {
			if (typeof window !== "undefined") {
				window.removeEventListener("resize", updateMobileFlag);
				window.addEventListener("resize", updateMobileFlag);
			}
			if (userStore.token) websocketStore.connect();
		});
		onBeforeUnmount(() => {
			if (typeof window !== "undefined") window.removeEventListener("resize", updateMobileFlag);
			websocketStore.disconnect();
		});
		return (_ctx, _cache) => {
			const _component_Cpu = resolveComponent("Cpu");
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_menu_item = resolveComponent("el-menu-item");
			const _component_el_sub_menu = resolveComponent("el-sub-menu");
			const _component_el_menu = resolveComponent("el-menu");
			const _component_el_aside = resolveComponent("el-aside");
			const _component_el_drawer = resolveComponent("el-drawer");
			const _component_Fold = resolveComponent("Fold");
			const _component_Expand = resolveComponent("Expand");
			const _component_el_breadcrumb_item = resolveComponent("el-breadcrumb-item");
			const _component_el_breadcrumb = resolveComponent("el-breadcrumb");
			const _component_Guide = resolveComponent("Guide");
			const _component_el_tooltip = resolveComponent("el-tooltip");
			const _component_QuestionFilled = resolveComponent("QuestionFilled");
			const _component_UserFilled = resolveComponent("UserFilled");
			const _component_el_avatar = resolveComponent("el-avatar");
			const _component_ArrowDown = resolveComponent("ArrowDown");
			const _component_el_dropdown_item = resolveComponent("el-dropdown-item");
			const _component_el_dropdown_menu = resolveComponent("el-dropdown-menu");
			const _component_el_dropdown = resolveComponent("el-dropdown");
			const _component_el_header = resolveComponent("el-header");
			const _component_router_view = resolveComponent("router-view");
			const _component_el_main = resolveComponent("el-main");
			const _component_el_container = resolveComponent("el-container");
			return openBlock(), createBlock(_component_el_container, { class: "layout-root" }, {
				default: withCtx(() => [
					!isMobile.value ? (openBlock(), createBlock(_component_el_aside, {
						key: 0,
						width: unref(appStore).sidebarCollapsed ? "64px" : "220px",
						class: "layout-aside"
					}, {
						default: withCtx(() => [createElementVNode("div", _hoisted_1, [createVNode(_component_el_icon, {
							size: 24,
							color: "#fff"
						}, {
							default: withCtx(() => [createVNode(_component_Cpu)]),
							_: 1
						}), withDirectives(createElementVNode("span", _hoisted_2, "网络设备 PMS", 512), [[vShow, !unref(appStore).sidebarCollapsed]])]), createVNode(_component_el_menu, {
							"default-active": activeMenu.value,
							collapse: unref(appStore).sidebarCollapsed,
							router: "",
							"background-color": "#001529",
							"text-color": "#cfd5dc",
							"active-text-color": "#ffffff",
							class: "side-menu"
						}, {
							default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(menuGroups, (item, idx) => {
								return openBlock(), createElementBlock(Fragment, { key: idx }, ["children" in item ? (openBlock(), createBlock(_component_el_sub_menu, {
									key: 0,
									index: String(idx)
								}, {
									title: withCtx(() => [createVNode(_component_el_icon, null, {
										default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(item.icon)))]),
										_: 2
									}, 1024), createElementVNode("span", null, toDisplayString(item.title), 1)]),
									default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(item.children, (child) => {
										return openBlock(), createBlock(_component_el_menu_item, {
											key: child.path,
											index: child.path
										}, {
											title: withCtx(() => [createTextVNode(toDisplayString(child.title), 1)]),
											default: withCtx(() => [createVNode(_component_el_icon, null, {
												default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(child.icon)))]),
												_: 2
											}, 1024)]),
											_: 2
										}, 1032, ["index"]);
									}), 128))]),
									_: 2
								}, 1032, ["index"])) : (openBlock(), createBlock(_component_el_menu_item, {
									key: 1,
									index: item.path
								}, {
									title: withCtx(() => [createTextVNode(toDisplayString(item.title), 1)]),
									default: withCtx(() => [createVNode(_component_el_icon, null, {
										default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(item.icon)))]),
										_: 2
									}, 1024)]),
									_: 2
								}, 1032, ["index"]))], 64);
							}), 64))]),
							_: 1
						}, 8, ["default-active", "collapse"])]),
						_: 1
					}, 8, ["width"])) : createCommentVNode("", true),
					isMobile.value ? (openBlock(), createBlock(_component_el_drawer, {
						key: 1,
						modelValue: drawerVisible.value,
						"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => drawerVisible.value = $event),
						direction: "ltr",
						size: 220,
						"with-header": false,
						class: "mobile-drawer"
					}, {
						default: withCtx(() => [createElementVNode("div", _hoisted_3, [createVNode(_component_el_icon, {
							size: 24,
							color: "#fff"
						}, {
							default: withCtx(() => [createVNode(_component_Cpu)]),
							_: 1
						}), _cache[2] || (_cache[2] = createElementVNode("span", { class: "logo-text" }, "网络设备 PMS", -1))]), createVNode(_component_el_menu, {
							"default-active": activeMenu.value,
							router: "",
							"background-color": "#001529",
							"text-color": "#cfd5dc",
							"active-text-color": "#ffffff",
							class: "side-menu",
							onSelect: handleMenuSelect
						}, {
							default: withCtx(() => [(openBlock(), createElementBlock(Fragment, null, renderList(menuGroups, (item, idx) => {
								return openBlock(), createElementBlock(Fragment, { key: idx }, ["children" in item ? (openBlock(), createBlock(_component_el_sub_menu, {
									key: 0,
									index: String(idx)
								}, {
									title: withCtx(() => [createVNode(_component_el_icon, null, {
										default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(item.icon)))]),
										_: 2
									}, 1024), createElementVNode("span", null, toDisplayString(item.title), 1)]),
									default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(item.children, (child) => {
										return openBlock(), createBlock(_component_el_menu_item, {
											key: child.path,
											index: child.path
										}, {
											title: withCtx(() => [createTextVNode(toDisplayString(child.title), 1)]),
											default: withCtx(() => [createVNode(_component_el_icon, null, {
												default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(child.icon)))]),
												_: 2
											}, 1024)]),
											_: 2
										}, 1032, ["index"]);
									}), 128))]),
									_: 2
								}, 1032, ["index"])) : (openBlock(), createBlock(_component_el_menu_item, {
									key: 1,
									index: item.path
								}, {
									title: withCtx(() => [createTextVNode(toDisplayString(item.title), 1)]),
									default: withCtx(() => [createVNode(_component_el_icon, null, {
										default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(item.icon)))]),
										_: 2
									}, 1024)]),
									_: 2
								}, 1032, ["index"]))], 64);
							}), 64))]),
							_: 1
						}, 8, ["default-active"])]),
						_: 1
					}, 8, ["modelValue"])) : createCommentVNode("", true),
					createVNode(_component_el_container, null, {
						default: withCtx(() => [
							createVNode(_component_el_header, { class: "layout-header" }, {
								default: withCtx(() => [createElementVNode("div", _hoisted_4, [!isMobile.value ? (openBlock(), createBlock(_component_el_icon, {
									key: 0,
									class: "collapse-btn",
									size: 20,
									onClick: _cache[1] || (_cache[1] = ($event) => unref(appStore).toggleSidebar())
								}, {
									default: withCtx(() => [!unref(appStore).sidebarCollapsed ? (openBlock(), createBlock(_component_Fold, { key: 0 })) : (openBlock(), createBlock(_component_Expand, { key: 1 }))]),
									_: 1
								})) : (openBlock(), createBlock(_component_el_icon, {
									key: 1,
									class: "collapse-btn",
									size: 22,
									onClick: toggleMobileDrawer
								}, {
									default: withCtx(() => [createVNode(_component_Expand)]),
									_: 1
								})), !isMobile.value ? (openBlock(), createBlock(_component_el_breadcrumb, {
									key: 2,
									separator: "/"
								}, {
									default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(breadcrumbs.value, (b, i) => {
										return openBlock(), createBlock(_component_el_breadcrumb_item, {
											key: i,
											to: b.path
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(b.title), 1)]),
											_: 2
										}, 1032, ["to"]);
									}), 128))]),
									_: 1
								})) : createCommentVNode("", true)]), createElementVNode("div", _hoisted_5, [
									createVNode(_component_el_tooltip, {
										content: "功能引导",
										placement: "bottom"
									}, {
										default: withCtx(() => [createVNode(_component_el_icon, {
											class: "header-action-btn",
											size: 18,
											onClick: startGuide
										}, {
											default: withCtx(() => [createVNode(_component_Guide)]),
											_: 1
										})]),
										_: 1
									}),
									createVNode(_component_el_tooltip, {
										content: "帮助中心",
										placement: "bottom"
									}, {
										default: withCtx(() => [createVNode(_component_el_icon, {
											class: "header-action-btn",
											size: 18,
											onClick: goHelp
										}, {
											default: withCtx(() => [createVNode(_component_QuestionFilled)]),
											_: 1
										})]),
										_: 1
									}),
									createVNode(NotificationBell_default, { class: "header-notification" }),
									createVNode(_component_el_dropdown, { onCommand: handleUserCommand }, {
										dropdown: withCtx(() => [createVNode(_component_el_dropdown_menu, null, {
											default: withCtx(() => [createVNode(_component_el_dropdown_item, { command: "dashboard" }, {
												default: withCtx(() => [..._cache[3] || (_cache[3] = [createTextVNode("首页", -1)])]),
												_: 1
											}), createVNode(_component_el_dropdown_item, {
												command: "logout",
												divided: ""
											}, {
												default: withCtx(() => [..._cache[4] || (_cache[4] = [createTextVNode("退出登录", -1)])]),
												_: 1
											})]),
											_: 1
										})]),
										default: withCtx(() => [createElementVNode("span", _hoisted_6, [
											createVNode(_component_el_avatar, {
												size: 30,
												class: "user-avatar"
											}, {
												default: withCtx(() => [createVNode(_component_el_icon, null, {
													default: withCtx(() => [createVNode(_component_UserFilled)]),
													_: 1
												})]),
												_: 1
											}),
											!isMobile.value ? (openBlock(), createElementBlock("span", _hoisted_7, toDisplayString(username.value), 1)) : createCommentVNode("", true),
											createVNode(_component_el_icon, null, {
												default: withCtx(() => [createVNode(_component_ArrowDown)]),
												_: 1
											})
										])]),
										_: 1
									})
								])]),
								_: 1
							}),
							unref(routeLoading) ? (openBlock(), createElementBlock("div", _hoisted_8)) : createCommentVNode("", true),
							!isMobile.value ? (openBlock(), createBlock(TagsView_default, { key: 1 })) : createCommentVNode("", true),
							createVNode(_component_el_main, { class: "layout-main" }, {
								default: withCtx(() => [createVNode(_component_router_view, null, {
									default: withCtx(({ Component }) => [createVNode(Transition, {
										name: "fade-transform",
										mode: "out-in"
									}, {
										default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(Component)))]),
										_: 2
									}, 1024)]),
									_: 1
								})]),
								_: 1
							})
						]),
						_: 1
					}),
					createVNode(UserGuide_default, {
						ref_key: "userGuideRef",
						ref: userGuideRef
					}, null, 512),
					createVNode(FeedbackButton_default)
				]),
				_: 1
			});
		};
	}
}), [["__scopeId", "data-v-39e1fe19"]]);
//#endregion
export { DefaultLayout_default as default };

//# sourceMappingURL=DefaultLayout-7mj4ALxV.js.map