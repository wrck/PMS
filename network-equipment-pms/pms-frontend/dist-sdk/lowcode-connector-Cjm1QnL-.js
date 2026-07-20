import { i as post, m as __exportAll, n as del, r as get } from "./request-BQrAOfxW.js";
//#region src/api/lowcode-connector.ts
var lowcode_connector_exports = /* @__PURE__ */ __exportAll({
	deleteConnector: () => deleteConnector,
	getConnectorList: () => getConnectorList,
	saveConnector: () => saveConnector,
	testConnector: () => testConnector,
	testOperation: () => testOperation
});
function getConnectorList() {
	return get("/api/lowcode/connector");
}
function saveConnector(data) {
	return post("/api/lowcode/connector", data);
}
function deleteConnector(id) {
	return del(`/api/lowcode/connector/${id}`);
}
function testConnector(code) {
	return post(`/api/lowcode/connector/${code}/test`);
}
/**
* 测试单个操作。
*
* <p>调用后端 {@code /api/lowcode/connector/{code}/test-operation} 接口，
* 传入操作名与参数，返回响应详情（状态码、Headers、Body、耗时）。</p>
*/
function testOperation(code, payload) {
	return post(`/api/lowcode/connector/${code}/test-operation`, payload);
}
//#endregion
export { testOperation as a, saveConnector as i, getConnectorList as n, lowcode_connector_exports as r, deleteConnector as t };

//# sourceMappingURL=lowcode-connector-Cjm1QnL-.js.map