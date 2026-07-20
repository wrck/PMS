import { c as useUserStore, d as useRouter, u as useRoute } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage } from "element-plus";
import { createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, openBlock, reactive, ref, resolveComponent, unref, withCtx, withKeys } from "vue";
import { Cpu, Lock, User } from "@element-plus/icons-vue";
//#region src/views/login/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "login-container" };
var _hoisted_2 = { class: "login-card" };
var _hoisted_3 = { class: "login-header" };
//#endregion
//#region src/views/login/index.vue
var login_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const router = useRouter();
		const route = useRoute();
		const userStore = useUserStore();
		const loginFormRef = ref();
		const loading = ref(false);
		const loginForm = reactive({
			username: "",
			password: ""
		});
		const rules = {
			username: [{
				required: true,
				message: "请输入用户名",
				trigger: "blur"
			}],
			password: [{
				required: true,
				message: "请输入密码",
				trigger: "blur"
			}, {
				min: 6,
				message: "密码长度不能少于 6 位",
				trigger: "blur"
			}]
		};
		async function handleLogin() {
			if (!loginFormRef.value) return;
			await loginFormRef.value.validate(async (valid) => {
				if (!valid) return;
				loading.value = true;
				try {
					await userStore.login({ ...loginForm });
					ElMessage.success("登录成功");
					const redirect = route.query.redirect || "/dashboard";
					router.push(redirect);
				} catch (_unused) {} finally {
					loading.value = false;
				}
			});
		}
		return (_ctx, _cache) => {
			const _component_el_icon = resolveComponent("el-icon");
			const _component_el_input = resolveComponent("el-input");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_form = resolveComponent("el-form");
			return openBlock(), createElementBlock("div", _hoisted_1, [createElementVNode("div", _hoisted_2, [createElementVNode("div", _hoisted_3, [
				createVNode(_component_el_icon, {
					size: 44,
					color: "#409eff"
				}, {
					default: withCtx(() => [createVNode(unref(Cpu))]),
					_: 1
				}),
				_cache[2] || (_cache[2] = createElementVNode("h2", { class: "login-title" }, "网络设备工程项目管理系统", -1)),
				_cache[3] || (_cache[3] = createElementVNode("p", { class: "login-subtitle" }, "Network Equipment Project Management System", -1))
			]), createVNode(_component_el_form, {
				ref_key: "loginFormRef",
				ref: loginFormRef,
				model: loginForm,
				rules,
				size: "large",
				onKeyup: withKeys(handleLogin, ["enter"])
			}, {
				default: withCtx(() => [
					createVNode(_component_el_form_item, { prop: "username" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: loginForm.username,
							"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => loginForm.username = $event),
							placeholder: "请输入用户名",
							"prefix-icon": unref(User),
							clearable: ""
						}, null, 8, ["modelValue", "prefix-icon"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, { prop: "password" }, {
						default: withCtx(() => [createVNode(_component_el_input, {
							modelValue: loginForm.password,
							"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => loginForm.password = $event),
							type: "password",
							"show-password": "",
							placeholder: "请输入密码",
							"prefix-icon": unref(Lock)
						}, null, 8, ["modelValue", "prefix-icon"])]),
						_: 1
					}),
					createVNode(_component_el_form_item, null, {
						default: withCtx(() => [createVNode(_component_el_button, {
							type: "primary",
							loading: loading.value,
							class: "login-btn",
							onClick: handleLogin
						}, {
							default: withCtx(() => [..._cache[4] || (_cache[4] = [createTextVNode(" 登 录 ", -1)])]),
							_: 1
						}, 8, ["loading"])]),
						_: 1
					})
				]),
				_: 1
			}, 8, ["model"])])]);
		};
	}
}), [["__scopeId", "data-v-69d515fe"]]);
//#endregion
export { login_default as default };

//# sourceMappingURL=login-DvkjZFZF.js.map