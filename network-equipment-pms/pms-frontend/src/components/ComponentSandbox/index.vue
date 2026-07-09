<!--
  组件<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、B<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     -<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     -<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handle<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T =<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 Sandbox<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据）<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RES<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /**<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e:<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e:<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result',<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid:<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev &&<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP_ALLOWLIST__）
  const allowlist =
    props.allowOrigins.length > 0
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP_ALLOWLIST__）
  const allowlist =
    props.allowOrigins.length > 0
      ? props.allowOrigins
      : ((window as any).__LOWCODE_CSP_ALLOWLIST__<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP_ALLOWLIST__）
  const allowlist =
    props.allowOrigins.length > 0
      ? props.allowOrigins
      : ((window as any).__LOWCODE_CSP_ALLOWLIST__ as string[] | undefined) || []
  if (allowlist.length > 0) {
    try {
      const host = new URL(src).hostname
      const matched = allowlist.some((pattern) => {
        if (pattern.startsWith('*<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP_ALLOWLIST__）
  const allowlist =
    props.allowOrigins.length > 0
      ? props.allowOrigins
      : ((window as any).__LOWCODE_CSP_ALLOWLIST__ as string[] | undefined) || []
  if (allowlist.length > 0) {
    try {
      const host = new URL(src).hostname
      const matched = allowlist.some((pattern) => {
        if (pattern.startsWith('*.')) return host.endsWith(pattern.slice(1))
        return host === pattern
      })
      if (!matched) {
        return { valid: false, reason: `域名 ${host} 不在<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP_ALLOWLIST__）
  const allowlist =
    props.allowOrigins.length > 0
      ? props.allowOrigins
      : ((window as any).__LOWCODE_CSP_ALLOWLIST__ as string[] | undefined) || []
  if (allowlist.length > 0) {
    try {
      const host = new URL(src).hostname
      const matched = allowlist.some((pattern) => {
        if (pattern.startsWith('*.')) return host.endsWith(pattern.slice(1))
        return host === pattern
      })
      if (!matched) {
        return { valid: false, reason: `域名 ${host} 不在 CSP 白名单内` }
      }
    } catch {
      return { valid: false, reason<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP_ALLOWLIST__）
  const allowlist =
    props.allowOrigins.length > 0
      ? props.allowOrigins
      : ((window as any).__LOWCODE_CSP_ALLOWLIST__ as string[] | undefined) || []
  if (allowlist.length > 0) {
    try {
      const host = new URL(src).hostname
      const matched = allowlist.some((pattern) => {
        if (pattern.startsWith('*.')) return host.endsWith(pattern.slice(1))
        return host === pattern
      })
      if (!matched) {
        return { valid: false, reason: `域名 ${host} 不在 CSP 白名单内` }
      }
    } catch {
      return { valid: false, reason: `URL 解析失败: ${src}` }
    }
  }
  return { valid:<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP_ALLOWLIST__）
  const allowlist =
    props.allowOrigins.length > 0
      ? props.allowOrigins
      : ((window as any).__LOWCODE_CSP_ALLOWLIST__ as string[] | undefined) || []
  if (allowlist.length > 0) {
    try {
      const host = new URL(src).hostname
      const matched = allowlist.some((pattern) => {
        if (pattern.startsWith('*.')) return host.endsWith(pattern.slice(1))
        return host === pattern
      })
      if (!matched) {
        return { valid: false, reason: `域名 ${host} 不在 CSP 白名单内` }
      }
    } catch {
      return { valid: false, reason: `URL 解析失败: ${src}` }
    }
  }
  return { valid: true }
}

/** 安全校验后的 src（<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP_ALLOWLIST__）
  const allowlist =
    props.allowOrigins.length > 0
      ? props.allowOrigins
      : ((window as any).__LOWCODE_CSP_ALLOWLIST__ as string[] | undefined) || []
  if (allowlist.length > 0) {
    try {
      const host = new URL(src).hostname
      const matched = allowlist.some((pattern) => {
        if (pattern.startsWith('*.')) return host.endsWith(pattern.slice(1))
        return host === pattern
      })
      if (!matched) {
        return { valid: false, reason: `域名 ${host} 不在 CSP 白名单内` }
      }
    } catch {
      return { valid: false, reason: `URL 解析失败: ${src}` }
    }
  }
  return { valid: true }
}

/** 安全校验后的 src（不通过则置空触发错误提示） */
<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP_ALLOWLIST__）
  const allowlist =
    props.allowOrigins.length > 0
      ? props.allowOrigins
      : ((window as any).__LOWCODE_CSP_ALLOWLIST__ as string[] | undefined) || []
  if (allowlist.length > 0) {
    try {
      const host = new URL(src).hostname
      const matched = allowlist.some((pattern) => {
        if (pattern.startsWith('*.')) return host.endsWith(pattern.slice(1))
        return host === pattern
      })
      if (!matched) {
        return { valid: false, reason: `域名 ${host} 不在 CSP 白名单内` }
      }
    } catch {
      return { valid: false, reason: `URL 解析失败: ${src}` }
    }
  }
  return { valid: true }
}

/** 安全校验后的 src（不通过则置空触发错误提示） */
const safeSrc = computed(() => {
  const r = validateSrc(props.src)
  if (!<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP_ALLOWLIST__）
  const allowlist =
    props.allowOrigins.length > 0
      ? props.allowOrigins
      : ((window as any).__LOWCODE_CSP_ALLOWLIST__ as string[] | undefined) || []
  if (allowlist.length > 0) {
    try {
      const host = new URL(src).hostname
      const matched = allowlist.some((pattern) => {
        if (pattern.startsWith('*.')) return host.endsWith(pattern.slice(1))
        return host === pattern
      })
      if (!matched) {
        return { valid: false, reason: `域名 ${host} 不在 CSP 白名单内` }
      }
    } catch {
      return { valid: false, reason: `URL 解析失败: ${src}` }
    }
  }
  return { valid: true }
}

/** 安全校验后的 src（不通过则置空触发错误提示） */
const safeSrc = computed(() => {
  const r = validateSrc(props.src)
  if (!r.valid) {
    errorMessage.value = r.reason<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP_ALLOWLIST__）
  const allowlist =
    props.allowOrigins.length > 0
      ? props.allowOrigins
      : ((window as any).__LOWCODE_CSP_ALLOWLIST__ as string[] | undefined) || []
  if (allowlist.length > 0) {
    try {
      const host = new URL(src).hostname
      const matched = allowlist.some((pattern) => {
        if (pattern.startsWith('*.')) return host.endsWith(pattern.slice(1))
        return host === pattern
      })
      if (!matched) {
        return { valid: false, reason: `域名 ${host} 不在 CSP 白名单内` }
      }
    } catch {
      return { valid: false, reason: `URL 解析失败: ${src}` }
    }
  }
  return { valid: true }
}

/** 安全校验后的 src（不通过则置空触发错误提示） */
const safeSrc = computed(() => {
  const r = validateSrc(props.src)
  if (!r.valid) {
    errorMessage.value = r.reason || 'src 校验失败'
    return ''
  }
  errorMessage.value = ''
  return props.src
})

/** sandbox 属性字符串 */
const<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP_ALLOWLIST__）
  const allowlist =
    props.allowOrigins.length > 0
      ? props.allowOrigins
      : ((window as any).__LOWCODE_CSP_ALLOWLIST__ as string[] | undefined) || []
  if (allowlist.length > 0) {
    try {
      const host = new URL(src).hostname
      const matched = allowlist.some((pattern) => {
        if (pattern.startsWith('*.')) return host.endsWith(pattern.slice(1))
        return host === pattern
      })
      if (!matched) {
        return { valid: false, reason: `域名 ${host} 不在 CSP 白名单内` }
      }
    } catch {
      return { valid: false, reason: `URL 解析失败: ${src}` }
    }
  }
  return { valid: true }
}

/** 安全校验后的 src（不通过则置空触发错误提示） */
const safeSrc = computed(() => {
  const r = validateSrc(props.src)
  if (!r.valid) {
    errorMessage.value = r.reason || 'src 校验失败'
    return ''
  }
  errorMessage.value = ''
  return props.src
})

/** sandbox 属性字符串 */
const sandboxAttr = computed(() => {
  const attrs = [...props.sandbox]
  if (props.allowSameOrigin && !attrs.includes('allow-s<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP_ALLOWLIST__）
  const allowlist =
    props.allowOrigins.length > 0
      ? props.allowOrigins
      : ((window as any).__LOWCODE_CSP_ALLOWLIST__ as string[] | undefined) || []
  if (allowlist.length > 0) {
    try {
      const host = new URL(src).hostname
      const matched = allowlist.some((pattern) => {
        if (pattern.startsWith('*.')) return host.endsWith(pattern.slice(1))
        return host === pattern
      })
      if (!matched) {
        return { valid: false, reason: `域名 ${host} 不在 CSP 白名单内` }
      }
    } catch {
      return { valid: false, reason: `URL 解析失败: ${src}` }
    }
  }
  return { valid: true }
}

/** 安全校验后的 src（不通过则置空触发错误提示） */
const safeSrc = computed(() => {
  const r = validateSrc(props.src)
  if (!r.valid) {
    errorMessage.value = r.reason || 'src 校验失败'
    return ''
  }
  errorMessage.value = ''
  return props.src
})

/** sandbox 属性字符串 */
const sandboxAttr = computed(() => {
  const attrs = [...props.sandbox]
  if (props.allowSameOrigin && !attrs.includes('allow-same-origin')) {
    attrs.push('allow-same-origin')
  }
  return attrs.join(' ')
})

/** iframe 样式 */
const iframeStyle = computed(() => ({
  width: typeof<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP_ALLOWLIST__）
  const allowlist =
    props.allowOrigins.length > 0
      ? props.allowOrigins
      : ((window as any).__LOWCODE_CSP_ALLOWLIST__ as string[] | undefined) || []
  if (allowlist.length > 0) {
    try {
      const host = new URL(src).hostname
      const matched = allowlist.some((pattern) => {
        if (pattern.startsWith('*.')) return host.endsWith(pattern.slice(1))
        return host === pattern
      })
      if (!matched) {
        return { valid: false, reason: `域名 ${host} 不在 CSP 白名单内` }
      }
    } catch {
      return { valid: false, reason: `URL 解析失败: ${src}` }
    }
  }
  return { valid: true }
}

/** 安全校验后的 src（不通过则置空触发错误提示） */
const safeSrc = computed(() => {
  const r = validateSrc(props.src)
  if (!r.valid) {
    errorMessage.value = r.reason || 'src 校验失败'
    return ''
  }
  errorMessage.value = ''
  return props.src
})

/** sandbox 属性字符串 */
const sandboxAttr = computed(() => {
  const attrs = [...props.sandbox]
  if (props.allowSameOrigin && !attrs.includes('allow-same-origin')) {
    attrs.push('allow-same-origin')
  }
  return attrs.join(' ')
})

/** iframe 样式 */
const iframeStyle = computed(() => ({
  width: typeof props.width === 'number' ? `${props.width}px` : props.width,
  height: typeof<!--
  组件 iframe 沙箱（批次4-T7）。

  用途：为低代码平台的"自定义页面"和"远程市场组件"提供安全的 iframe 隔离容器，
  封装 sandbox 属性、postMessage 双向通信协议、CSP 白名单校验、配置实时同步。

  借鉴：ToolJet Custom Component（iframe + postMessage）、
  Power Apps PCF iframe 隔离、Budibase 数据绑定实时同步。

  设计要点：
  1. sandbox 属性：默认 allow-scripts allow-forms allow-popups allow-same-origin（远程组件），
     可通过 props 收紧（如禁止 allow-same-origin 实现完全隔离）。
  2. postMessage 协议：约定消息格式 { source: 'pms-lowcode', type, payload, requestId }，
     双方校验 source 字段防止第三方消息干扰。
  3. CSP 白名单：父窗口只接受 origin 在 allowlist 内的子窗口消息；子窗口通过
     <meta http-equiv="Content-Security-Policy"> 或 HTTP 响应头配置（由宿主负责）。
  4. 配置实时同步：父窗口 watch config 变化，通过 postMessage 推送 SET_CONFIG 消息，
     子窗口监听并重新渲染（借鉴 Budibase reactive bindings）。
  5. 安全防御：
     - 协议白名单：仅 https（生产）/http+localhost（开发），禁止 file/data/javascript
     - 域名白名单：window.__LOWCODE_CSP_ALLOWLIST__ 或 props.allowOrigins
     - 消息 origin 校验：仅接受 allowlist 内 origin 的消息
     - 消息格式校验：缺少 source 字段或 type 未知均忽略
-->
<template>
  <div class="component-sandbox" :class="{ 'sandbox-error': errorMessage }">
    <!-- 正常 iframe 模式 -->
    <iframe
      v-if="!errorMessage"
      ref="iframeRef"
      :src="safeSrc"
      :sandbox="sandboxAttr"
      :style="iframeStyle"
      frameborder="0"
      class="sandbox-iframe"
      :title="title"
      @load="handleIframeLoad"
    />
    <!-- 错误提示模式 -->
    <div v-else class="sandbox-error-tip">
      <el-alert
        :title="`沙箱加载失败：${errorMessage}`"
        type="error"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, shallowRef } from 'vue'

/**
 * postMessage 消息格式契约。
 *
 * <p>双方（父窗口 / 子 iframe）均按此格式收发消息，通过 source 字段过滤第三方干扰。</p>
 */
export interface SandboxMessage<T = unknown> {
  /** 固定为 'pms-lowcode'，用于过滤非本平台消息 */
  source: 'pms-lowcode'
  /** 消息类型：见 SandboxMessageType */
  type: string
  /** 消息负载 */
  payload?: T
  /** 请求 ID（用于请求-响应配对） */
  requestId?: string
}

/** 父窗口 → 子窗口 消息类型 */
export type ParentToChildMessageType =
  | 'SET_CONFIG' /** 推送配置（实时同步） */
  | 'SET_PROPS' /** 推送 props 值 */
  | 'SET_CONTEXT' /** 推送运行时上下文（用户/表单数据） */
  | 'INVOKE_METHOD' /** 调用子组件方法 */
  | 'RESIZE' /** 通知子窗口尺寸变化 */

/** 子窗口 → 父窗口 消息类型 */
export type ChildToParentMessageType =
  | 'READY' /** 子窗口就绪 */
  | 'PROPS_CHANGE' /** 子组件 props 变更（双向绑定） */
  | 'EVENT' /** 子组件触发事件 */
  | 'METHOD_RESULT' /** 方法调用结果 */
  | 'RESIZE_REQUEST' /** 子窗口请求父窗口调整 iframe 尺寸 */
  | 'ERROR' /** 子窗口报错 */

const props = withDefaults(
  defineProps<{
    /** iframe 加载的 URL（必须 http/https，禁止 file/data/javascript） */
    src: string
    /** iframe 标题（无障碍） */
    title?: string
    /** 允许的 origin 白名单（用于 postMessage origin 校验）；缺省时仅校验协议 */
    allowOrigins?: string[]
    /** 推送给子窗口的配置对象（实时同步，watch 变化推送 SET_CONFIG） */
    config?: Record<string, unknown>
    /** 推送给子窗口的 props 值（实时同步，watch 变化推送 SET_PROPS） */
    componentProps?: Record<string, unknown>
    /** 推送给子窗口的运行时上下文（用户/表单数据） */
    context?: Record<string, unknown>
    /** sandbox 属性，默认允许脚本+表单+弹窗+同源 */
    sandbox?: string[]
    /** iframe 高度（px 或 CSS 字符串） */
    height?: string | number
    /** iframe 宽度（px 或 CSS 字符串） */
    width?: string | number
    /** 是否允许同源（远程组件通常需要 false 实现完全隔离） */
    allowSameOrigin?: boolean
  }>(),
  {
    title: '低代码组件沙箱',
    allowOrigins: () => [],
    config: () => ({}),
    componentProps: () => ({}),
    context: () => ({}),
    sandbox: () => ['allow-scripts', 'allow-forms', 'allow-popups'],
    height: '100%',
    width: '100%',
    allowSameOrigin: true
  }
)

const emit = defineEmits<{
  /** 子窗口就绪 */
  (e: 'ready'): void
  /** 子组件 props 变更（双向绑定回传） */
  (e: 'props-change', payload: Record<string, unknown>): void
  /** 子组件触发事件 */
  (e: 'event', payload: { event: string; data?: unknown }): void
  /** 子窗口请求调整尺寸 */
  (e: 'resize-request', payload: { width?: number; height?: number }): void
  /** 子窗口报错 */
  (e: 'error', payload: { message: string; stack?: string }): void
  /** 方法调用结果 */
  (e: 'method-result', payload: { requestId: string; result?: unknown; error?: string }): void
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const errorMessage = ref<string>('')
const childReady = ref(false)
/** 已就绪但未推送的配置（子窗口 READY 前缓存的 SET_CONFIG/SET_PROPS/SET_CONTEXT） */
const pendingMessages = shallowRef<SandboxMessage[]>([])

/**
 * 校验 src 协议与域名白名单（CSP 运行时防御）。
 */
function validateSrc(src: string): { valid: boolean; reason?: string } {
  if (!src || typeof src !== 'string') {
    return { valid: false, reason: 'src 为空' }
  }
  const lower = src.toLowerCase()
  if (lower.startsWith('file:') || lower.startsWith('data:') || lower.startsWith('javascript:')) {
    return { valid: false, reason: `禁止的协议: ${lower.split(':')[0]}` }
  }
  const isDev = import.meta.env?.DEV === true
  if (isDev && (lower.startsWith('http://localhost') || lower.startsWith('https://localhost'))) {
    return { valid: true }
  }
  if (!isDev && !lower.startsWith('https://')) {
    return { valid: false, reason: '生产环境必须 https' }
  }
  // 域名白名单校验（props.allowOrigins 优先，其次 window.__LOWCODE_CSP_ALLOWLIST__）
  const allowlist =
    props.allowOrigins.length > 0
      ? props.allowOrigins
      : ((window as any).__LOWCODE_CSP_ALLOWLIST__ as string[] | undefined) || []
  if (allowlist.length > 0) {
    try {
      const host = new URL(src).hostname
      const matched = allowlist.some((pattern) => {
        if (pattern.startsWith('*.')) return host.endsWith(pattern.slice(1))
        return host === pattern
      })
      if (!matched) {
        return { valid: false, reason: `域名 ${host} 不在 CSP 白名单内` }
      }
    } catch {
      return { valid: false, reason: `URL 解析失败: ${src}` }
    }
  }
  return { valid: true }
}

/** 安全校验后的 src（不通过则置空触发错误提示） */
const safeSrc = computed(() => {
  const r = validateSrc(props.src)
  if (!r.valid) {
    errorMessage.value = r.reason || 'src 校验失败'
    return ''
  }
  errorMessage.value = ''
  return props.src
})

/** sandbox 属性字符串 */
const sandboxAttr = computed(() => {
  const attrs = [...props.sandbox]
  if (props.allowSameOrigin && !attrs.includes('allow-same-origin')) {
    attrs.push('allow-same-origin')
  }
  return attrs.join(' ')
})

/** iframe 样式 */
const iframeStyle = computed(() => ({
  width: typeof props.width === 'number' ? `${props.width}px` : props.width,
  height: typeof props.height === 'number' ? `${props.height}px` : props.height,
  border: 'none',
  display: 'block'
}))

/**
