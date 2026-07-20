import { computed, isRef, ref } from "vue";
//#region src/composables/useUndoRedo.ts
/** 深拷贝工具：对象/数组走 JSON 序列化避免引用污染，原始类型直接返回 */
function deepClone(value) {
	if (value === null || typeof value !== "object") return value;
	try {
		return JSON.parse(JSON.stringify(value));
	} catch (_unused) {
		return value;
	}
}
function useUndoRedo(initial, options = {}) {
	var _options$maxHistory;
	const maxHistory = typeof options === "number" ? options : (_options$maxHistory = options.maxHistory) !== null && _options$maxHistory !== void 0 ? _options$maxHistory : 50;
	const past = ref([]);
	const present = ref(deepClone(initial));
	const future = ref([]);
	/** 设置新值：当前 present 压入 past（深拷贝保护），超限则丢弃最旧，清空 future */
	function set(newValue) {
		past.value.push(present.value);
		if (past.value.length > maxHistory) past.value.shift();
		present.value = deepClone(newValue);
		future.value = [];
	}
	/** 撤销：past 非空时把 present 压入 future，取 past 末尾为新 present */
	function undo() {
		if (past.value.length === 0) return;
		future.value.push(present.value);
		present.value = past.value.pop();
	}
	/** 重做：future 非空时把 present 压入 past，取 future 末尾为新 present */
	function redo() {
		if (future.value.length === 0) return;
		past.value.push(present.value);
		present.value = future.value.pop();
	}
	/** 重置：清空 past/future 并深拷贝设置新 present */
	function reset(value) {
		past.value = [];
		future.value = [];
		present.value = deepClone(value);
	}
	const canUndo = computed(() => past.value.length > 0);
	const canRedo = computed(() => future.value.length > 0);
	const historySize = computed(() => past.value.length + future.value.length);
	/**
	* 启用键盘快捷键（可选）：Ctrl/Cmd+Z 撤销、Ctrl+Y 或 Ctrl/Cmd+Shift+Z 重做。
	*
	* @param targetEl 目标元素（HTMLElement 或其 ref），缺省监听 window
	* @returns cleanup 函数，调用以移除 keydown 监听
	*/
	function enableKeyboard(targetEl) {
		const resolved = targetEl ? isRef(targetEl) ? targetEl.value : targetEl : null;
		const target = resolved !== null && resolved !== void 0 ? resolved : window;
		function handler(event) {
			if (!(typeof navigator !== "undefined" && navigator.platform.toUpperCase().indexOf("MAC") >= 0 ? event.metaKey : event.ctrlKey)) return;
			const key = event.key.toLowerCase();
			if (key === "z" && !event.shiftKey) {
				event.preventDefault();
				undo();
				return;
			}
			if (key === "z" && event.shiftKey || key === "y") {
				event.preventDefault();
				redo();
			}
		}
		target.addEventListener("keydown", handler);
		return () => target.removeEventListener("keydown", handler);
	}
	return {
		present,
		set,
		undo,
		redo,
		reset,
		canUndo,
		canRedo,
		historySize,
		enableKeyboard
	};
}
//#endregion
export { useUndoRedo as t };

//# sourceMappingURL=useUndoRedo-C9SCn4rB.js.map