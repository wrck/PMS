import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { computed, createElementBlock, createElementVNode, defineComponent, openBlock } from "vue";
//#region node_modules/jsondiffpatch/lib/clone.js
function cloneRegExp(re) {
	var _a;
	const regexMatch = /^\/(.*)\/([gimyu]*)$/.exec(re.toString());
	if (!regexMatch) throw new Error("Invalid RegExp");
	return new RegExp((_a = regexMatch[1]) !== null && _a !== void 0 ? _a : "", regexMatch[2]);
}
function clone$1(arg) {
	if (typeof arg !== "object") return arg;
	if (arg === null) return null;
	if (Array.isArray(arg)) return arg.map(clone$1);
	if (arg instanceof Date) return new Date(arg.getTime());
	if (arg instanceof RegExp) return cloneRegExp(arg);
	const cloned = {};
	for (const name in arg) if (Object.prototype.hasOwnProperty.call(arg, name)) cloned[name] = clone$1(arg[name]);
	return cloned;
}
//#endregion
//#region node_modules/jsondiffpatch/lib/assertions/arrays.js
function assertNonEmptyArray(arr, message) {
	if (arr.length === 0) throw new Error(message || "Expected a non-empty array");
}
function assertArrayHasAtLeast2(arr, message) {
	if (arr.length < 2) throw new Error(message || "Expected an array with at least 2 items");
}
/**
* type-safe version of `arr[arr.length - 1]`
* @param arr a non empty array
* @returns the last element of the array
*/
var lastNonEmpty = (arr) => arr[arr.length - 1];
//#endregion
//#region node_modules/jsondiffpatch/lib/contexts/context.js
var Context = class {
	setResult(result) {
		this.result = result;
		this.hasResult = true;
		return this;
	}
	exit() {
		this.exiting = true;
		return this;
	}
	push(child, name) {
		child.parent = this;
		if (typeof name !== "undefined") child.childName = name;
		child.root = this.root || this;
		child.options = child.options || this.options;
		if (!this.children) {
			this.children = [child];
			this.nextAfterChildren = this.next || null;
			this.next = child;
		} else {
			assertNonEmptyArray(this.children);
			lastNonEmpty(this.children).next = child;
			this.children.push(child);
		}
		child.next = this;
		return this;
	}
};
//#endregion
//#region node_modules/jsondiffpatch/lib/contexts/diff.js
var DiffContext = class extends Context {
	constructor(left, right) {
		super();
		this.left = left;
		this.right = right;
		this.pipe = "diff";
	}
	prepareDeltaResult(result) {
		var _a, _b, _c, _d;
		if (typeof result === "object") {
			if (((_a = this.options) === null || _a === void 0 ? void 0 : _a.omitRemovedValues) && Array.isArray(result) && result.length > 1 && (result.length === 2 || result[2] === 0 || result[2] === 3)) result[0] = 0;
			if ((_b = this.options) === null || _b === void 0 ? void 0 : _b.cloneDiffValues) {
				const clone = typeof ((_c = this.options) === null || _c === void 0 ? void 0 : _c.cloneDiffValues) === "function" ? (_d = this.options) === null || _d === void 0 ? void 0 : _d.cloneDiffValues : clone$1;
				if (typeof result[0] === "object") result[0] = clone(result[0]);
				if (typeof result[1] === "object") result[1] = clone(result[1]);
			}
		}
		return result;
	}
	setResult(result) {
		this.prepareDeltaResult(result);
		return super.setResult(result);
	}
};
//#endregion
//#region node_modules/jsondiffpatch/lib/contexts/patch.js
var PatchContext = class extends Context {
	constructor(left, delta) {
		super();
		this.left = left;
		this.delta = delta;
		this.pipe = "patch";
	}
};
//#endregion
//#region node_modules/jsondiffpatch/lib/contexts/reverse.js
var ReverseContext = class extends Context {
	constructor(delta) {
		super();
		this.delta = delta;
		this.pipe = "reverse";
	}
};
//#endregion
//#region node_modules/jsondiffpatch/lib/pipe.js
var Pipe = class {
	constructor(name) {
		this.name = name;
		this.filters = [];
	}
	process(input) {
		if (!this.processor) throw new Error("add this pipe to a processor before using it");
		const debug = this.debug;
		const length = this.filters.length;
		const context = input;
		for (let index = 0; index < length; index++) {
			const filter = this.filters[index];
			if (!filter) continue;
			if (debug) this.log(`filter: ${filter.filterName}`);
			filter(context);
			if (typeof context === "object" && context.exiting) {
				context.exiting = false;
				break;
			}
		}
		if (!context.next && this.resultCheck) this.resultCheck(context);
	}
	log(msg) {
		console.log(`[jsondiffpatch] ${this.name} pipe, ${msg}`);
	}
	append(...args) {
		this.filters.push(...args);
		return this;
	}
	prepend(...args) {
		this.filters.unshift(...args);
		return this;
	}
	indexOf(filterName) {
		if (!filterName) throw new Error("a filter name is required");
		for (let index = 0; index < this.filters.length; index++) {
			const filter = this.filters[index];
			if ((filter === null || filter === void 0 ? void 0 : filter.filterName) === filterName) return index;
		}
		throw new Error(`filter not found: ${filterName}`);
	}
	list() {
		return this.filters.map((f) => f.filterName);
	}
	after(filterName, ...params) {
		const index = this.indexOf(filterName);
		this.filters.splice(index + 1, 0, ...params);
		return this;
	}
	before(filterName, ...params) {
		const index = this.indexOf(filterName);
		this.filters.splice(index, 0, ...params);
		return this;
	}
	replace(filterName, ...params) {
		const index = this.indexOf(filterName);
		this.filters.splice(index, 1, ...params);
		return this;
	}
	remove(filterName) {
		const index = this.indexOf(filterName);
		this.filters.splice(index, 1);
		return this;
	}
	clear() {
		this.filters.length = 0;
		return this;
	}
	shouldHaveResult(should) {
		if (should === false) {
			this.resultCheck = null;
			return this;
		}
		if (this.resultCheck) return this;
		this.resultCheck = (context) => {
			if (!context.hasResult) {
				console.log(context);
				const error = /* @__PURE__ */ new Error(`${this.name} failed`);
				error.noResult = true;
				throw error;
			}
		};
		return this;
	}
};
//#endregion
//#region node_modules/jsondiffpatch/lib/processor.js
var Processor = class {
	constructor(options) {
		this.selfOptions = options || {};
		this.pipes = {};
	}
	options(options) {
		if (options) this.selfOptions = options;
		return this.selfOptions;
	}
	pipe(name, pipeArg) {
		let pipe = pipeArg;
		if (typeof name === "string") {
			if (typeof pipe === "undefined") return this.pipes[name];
			this.pipes[name] = pipe;
		}
		if (name && name.name) {
			pipe = name;
			if (pipe.processor === this) return pipe;
			this.pipes[pipe.name] = pipe;
		}
		if (!pipe) throw new Error(`pipe is not defined: ${name}`);
		pipe.processor = this;
		return pipe;
	}
	process(input, pipe) {
		let context = input;
		context.options = this.options();
		let nextPipe = pipe || input.pipe || "default";
		let lastPipe = void 0;
		while (nextPipe) {
			if (typeof context.nextAfterChildren !== "undefined") {
				context.next = context.nextAfterChildren;
				context.nextAfterChildren = null;
			}
			if (typeof nextPipe === "string") nextPipe = this.pipe(nextPipe);
			nextPipe.process(context);
			lastPipe = nextPipe;
			nextPipe = null;
			if (context) {
				if (context.next) {
					context = context.next;
					nextPipe = context.pipe || lastPipe;
				}
			}
		}
		return context.hasResult ? context.result : void 0;
	}
};
//#endregion
//#region node_modules/jsondiffpatch/lib/filters/lcs.js
var defaultMatch = (array1, array2, index1, index2) => array1[index1] === array2[index2];
var lengthMatrix = (array1, array2, match, context) => {
	var _a, _b, _c;
	const len1 = array1.length;
	const len2 = array2.length;
	let x;
	let y;
	const matrix = new Array(len1 + 1);
	for (x = 0; x < len1 + 1; x++) {
		const matrixNewRow = new Array(len2 + 1);
		for (y = 0; y < len2 + 1; y++) matrixNewRow[y] = 0;
		matrix[x] = matrixNewRow;
	}
	matrix.match = match;
	for (x = 1; x < len1 + 1; x++) {
		const matrixRowX = matrix[x];
		if (matrixRowX === void 0) throw new Error("LCS matrix row is undefined");
		const matrixRowBeforeX = matrix[x - 1];
		if (matrixRowBeforeX === void 0) throw new Error("LCS matrix row is undefined");
		for (y = 1; y < len2 + 1; y++) if (match(array1, array2, x - 1, y - 1, context)) matrixRowX[y] = ((_a = matrixRowBeforeX[y - 1]) !== null && _a !== void 0 ? _a : 0) + 1;
		else matrixRowX[y] = Math.max((_b = matrixRowBeforeX[y]) !== null && _b !== void 0 ? _b : 0, (_c = matrixRowX[y - 1]) !== null && _c !== void 0 ? _c : 0);
	}
	return matrix;
};
var backtrack = (matrix, array1, array2, context) => {
	let index1 = array1.length;
	let index2 = array2.length;
	const subsequence = {
		sequence: [],
		indices1: [],
		indices2: []
	};
	while (index1 !== 0 && index2 !== 0) {
		if (matrix.match === void 0) throw new Error("LCS matrix match function is undefined");
		if (matrix.match(array1, array2, index1 - 1, index2 - 1, context)) {
			subsequence.sequence.unshift(array1[index1 - 1]);
			subsequence.indices1.unshift(index1 - 1);
			subsequence.indices2.unshift(index2 - 1);
			--index1;
			--index2;
		} else {
			const matrixRowIndex1 = matrix[index1];
			if (matrixRowIndex1 === void 0) throw new Error("LCS matrix row is undefined");
			const valueAtMatrixAbove = matrixRowIndex1[index2 - 1];
			if (valueAtMatrixAbove === void 0) throw new Error("LCS matrix value is undefined");
			const matrixRowBeforeIndex1 = matrix[index1 - 1];
			if (matrixRowBeforeIndex1 === void 0) throw new Error("LCS matrix row is undefined");
			const valueAtMatrixLeft = matrixRowBeforeIndex1[index2];
			if (valueAtMatrixLeft === void 0) throw new Error("LCS matrix value is undefined");
			if (valueAtMatrixAbove > valueAtMatrixLeft) --index2;
			else --index1;
		}
	}
	return subsequence;
};
var get = (array1, array2, match, context) => {
	const innerContext = context || {};
	return backtrack(lengthMatrix(array1, array2, match || defaultMatch, innerContext), array1, array2, innerContext);
};
var lcs_default = { get };
//#endregion
//#region node_modules/jsondiffpatch/lib/filters/arrays.js
var ARRAY_MOVE = 3;
function arraysHaveMatchByRef(array1, array2, len1, len2) {
	for (let index1 = 0; index1 < len1; index1++) {
		const val1 = array1[index1];
		for (let index2 = 0; index2 < len2; index2++) {
			const val2 = array2[index2];
			if (index1 !== index2 && val1 === val2) return true;
		}
	}
	return false;
}
function matchItems(array1, array2, index1, index2, context) {
	const value1 = array1[index1];
	const value2 = array2[index2];
	if (value1 === value2) return true;
	if (typeof value1 !== "object" || typeof value2 !== "object") return false;
	const objectHash = context.objectHash;
	if (!objectHash) return context.matchByPosition && index1 === index2;
	context.hashCache1 = context.hashCache1 || [];
	let hash1 = context.hashCache1[index1];
	if (typeof hash1 === "undefined") context.hashCache1[index1] = hash1 = objectHash(value1, index1);
	if (typeof hash1 === "undefined") return false;
	context.hashCache2 = context.hashCache2 || [];
	let hash2 = context.hashCache2[index2];
	if (typeof hash2 === "undefined") context.hashCache2[index2] = hash2 = objectHash(value2, index2);
	if (typeof hash2 === "undefined") return false;
	return hash1 === hash2;
}
var diffFilter$3 = function arraysDiffFilter(context) {
	var _a, _b, _c, _d, _e;
	if (!context.leftIsArray) return;
	const matchContext = {
		objectHash: (_a = context.options) === null || _a === void 0 ? void 0 : _a.objectHash,
		matchByPosition: (_b = context.options) === null || _b === void 0 ? void 0 : _b.matchByPosition
	};
	let commonHead = 0;
	let commonTail = 0;
	let index;
	let index1;
	let index2;
	const array1 = context.left;
	const array2 = context.right;
	const len1 = array1.length;
	const len2 = array2.length;
	let child;
	if (len1 > 0 && len2 > 0 && !matchContext.objectHash && typeof matchContext.matchByPosition !== "boolean") matchContext.matchByPosition = !arraysHaveMatchByRef(array1, array2, len1, len2);
	while (commonHead < len1 && commonHead < len2 && matchItems(array1, array2, commonHead, commonHead, matchContext)) {
		index = commonHead;
		child = new DiffContext(array1[index], array2[index]);
		context.push(child, index);
		commonHead++;
	}
	while (commonTail + commonHead < len1 && commonTail + commonHead < len2 && matchItems(array1, array2, len1 - 1 - commonTail, len2 - 1 - commonTail, matchContext)) {
		index1 = len1 - 1 - commonTail;
		index2 = len2 - 1 - commonTail;
		child = new DiffContext(array1[index1], array2[index2]);
		context.push(child, index2);
		commonTail++;
	}
	let result;
	if (commonHead + commonTail === len1) {
		if (len1 === len2) {
			context.setResult(void 0).exit();
			return;
		}
		result = result || { _t: "a" };
		for (index = commonHead; index < len2 - commonTail; index++) {
			result[index] = [array2[index]];
			context.prepareDeltaResult(result[index]);
		}
		context.setResult(result).exit();
		return;
	}
	if (commonHead + commonTail === len2) {
		result = result || { _t: "a" };
		for (index = commonHead; index < len1 - commonTail; index++) {
			const key = `_${index}`;
			result[key] = [
				array1[index],
				0,
				0
			];
			context.prepareDeltaResult(result[key]);
		}
		context.setResult(result).exit();
		return;
	}
	matchContext.hashCache1 = void 0;
	matchContext.hashCache2 = void 0;
	const trimmed1 = array1.slice(commonHead, len1 - commonTail);
	const trimmed2 = array2.slice(commonHead, len2 - commonTail);
	const seq = lcs_default.get(trimmed1, trimmed2, matchItems, matchContext);
	const removedItems = [];
	result = result || { _t: "a" };
	for (index = commonHead; index < len1 - commonTail; index++) if (seq.indices1.indexOf(index - commonHead) < 0) {
		const key = `_${index}`;
		result[key] = [
			array1[index],
			0,
			0
		];
		context.prepareDeltaResult(result[key]);
		removedItems.push(index);
	}
	let detectMove = true;
	if (((_c = context.options) === null || _c === void 0 ? void 0 : _c.arrays) && context.options.arrays.detectMove === false) detectMove = false;
	let includeValueOnMove = false;
	if ((_e = (_d = context.options) === null || _d === void 0 ? void 0 : _d.arrays) === null || _e === void 0 ? void 0 : _e.includeValueOnMove) includeValueOnMove = true;
	const removedItemsLength = removedItems.length;
	for (index = commonHead; index < len2 - commonTail; index++) {
		const indexOnArray2 = seq.indices2.indexOf(index - commonHead);
		if (indexOnArray2 < 0) {
			let isMove = false;
			if (detectMove && removedItemsLength > 0) for (let removeItemIndex1 = 0; removeItemIndex1 < removedItemsLength; removeItemIndex1++) {
				index1 = removedItems[removeItemIndex1];
				const resultItem = index1 === void 0 ? void 0 : result[`_${index1}`];
				if (index1 !== void 0 && resultItem && matchItems(trimmed1, trimmed2, index1 - commonHead, index - commonHead, matchContext)) {
					resultItem.splice(1, 2, index, ARRAY_MOVE);
					resultItem.splice(1, 2, index, ARRAY_MOVE);
					if (!includeValueOnMove) resultItem[0] = "";
					index2 = index;
					child = new DiffContext(array1[index1], array2[index2]);
					context.push(child, index2);
					removedItems.splice(removeItemIndex1, 1);
					isMove = true;
					break;
				}
			}
			if (!isMove) {
				result[index] = [array2[index]];
				context.prepareDeltaResult(result[index]);
			}
		} else {
			if (seq.indices1[indexOnArray2] === void 0) throw new Error(`Invalid indexOnArray2: ${indexOnArray2}, seq.indices1: ${seq.indices1}`);
			index1 = seq.indices1[indexOnArray2] + commonHead;
			if (seq.indices2[indexOnArray2] === void 0) throw new Error(`Invalid indexOnArray2: ${indexOnArray2}, seq.indices2: ${seq.indices2}`);
			index2 = seq.indices2[indexOnArray2] + commonHead;
			child = new DiffContext(array1[index1], array2[index2]);
			context.push(child, index2);
		}
	}
	context.setResult(result).exit();
};
diffFilter$3.filterName = "arrays";
var compare = {
	numerically(a, b) {
		return a - b;
	},
	numericallyBy(name) {
		return (a, b) => a[name] - b[name];
	}
};
var patchFilter$3 = function nestedPatchFilter(context) {
	var _a;
	if (!context.nested) return;
	const nestedDelta = context.delta;
	if (nestedDelta._t !== "a") return;
	let index;
	let index1;
	const delta = nestedDelta;
	const array = context.left;
	let toRemove = [];
	let toInsert = [];
	const toModify = [];
	for (index in delta) if (index !== "_t") if (index[0] === "_") {
		const removedOrMovedIndex = index;
		if (delta[removedOrMovedIndex] !== void 0 && (delta[removedOrMovedIndex][2] === 0 || delta[removedOrMovedIndex][2] === ARRAY_MOVE)) toRemove.push(Number.parseInt(index.slice(1), 10));
		else throw new Error(`only removal or move can be applied at original array indices, invalid diff type: ${(_a = delta[removedOrMovedIndex]) === null || _a === void 0 ? void 0 : _a[2]}`);
	} else {
		const numberIndex = index;
		if (delta[numberIndex].length === 1) toInsert.push({
			index: Number.parseInt(numberIndex, 10),
			value: delta[numberIndex][0]
		});
		else toModify.push({
			index: Number.parseInt(numberIndex, 10),
			delta: delta[numberIndex]
		});
	}
	toRemove = toRemove.sort(compare.numerically);
	for (index = toRemove.length - 1; index >= 0; index--) {
		index1 = toRemove[index];
		if (index1 === void 0) continue;
		const indexDiff = delta[`_${index1}`];
		const removedValue = array.splice(index1, 1)[0];
		if ((indexDiff === null || indexDiff === void 0 ? void 0 : indexDiff[2]) === ARRAY_MOVE) toInsert.push({
			index: indexDiff[1],
			value: removedValue
		});
	}
	toInsert = toInsert.sort(compare.numericallyBy("index"));
	const toInsertLength = toInsert.length;
	for (index = 0; index < toInsertLength; index++) {
		const insertion = toInsert[index];
		if (insertion === void 0) continue;
		array.splice(insertion.index, 0, insertion.value);
	}
	const toModifyLength = toModify.length;
	if (toModifyLength > 0) for (index = 0; index < toModifyLength; index++) {
		const modification = toModify[index];
		if (modification === void 0) continue;
		const child = new PatchContext(array[modification.index], modification.delta);
		context.push(child, modification.index);
	}
	if (!context.children) {
		context.setResult(array).exit();
		return;
	}
	context.exit();
};
patchFilter$3.filterName = "arrays";
var collectChildrenPatchFilter$1 = function collectChildrenPatchFilter(context) {
	if (!context || !context.children) return;
	if (context.delta._t !== "a") return;
	const array = context.left;
	const length = context.children.length;
	for (let index = 0; index < length; index++) {
		const child = context.children[index];
		if (child === void 0) continue;
		const arrayIndex = child.childName;
		array[arrayIndex] = child.result;
	}
	context.setResult(array).exit();
};
collectChildrenPatchFilter$1.filterName = "arraysCollectChildren";
var reverseFilter$3 = function arraysReverseFilter(context) {
	if (!context.nested) {
		const nonNestedDelta = context.delta;
		if (nonNestedDelta[2] === ARRAY_MOVE) {
			const arrayMoveDelta = nonNestedDelta;
			context.newName = `_${arrayMoveDelta[1]}`;
			context.setResult([
				arrayMoveDelta[0],
				Number.parseInt(context.childName.substring(1), 10),
				ARRAY_MOVE
			]).exit();
		}
		return;
	}
	const nestedDelta = context.delta;
	if (nestedDelta._t !== "a") return;
	const arrayDelta = nestedDelta;
	for (const name in arrayDelta) {
		if (name === "_t") continue;
		const child = new ReverseContext(arrayDelta[name]);
		context.push(child, name);
	}
	context.exit();
};
reverseFilter$3.filterName = "arrays";
var reverseArrayDeltaIndex = (delta, index, itemDelta) => {
	if (typeof index === "string" && index[0] === "_") return Number.parseInt(index.substring(1), 10);
	if (Array.isArray(itemDelta) && itemDelta[2] === 0) return `_${index}`;
	let reverseIndex = +index;
	for (const deltaIndex in delta) {
		const deltaItem = delta[deltaIndex];
		if (Array.isArray(deltaItem)) {
			if (deltaItem[2] === ARRAY_MOVE) {
				const moveFromIndex = Number.parseInt(deltaIndex.substring(1), 10);
				const moveToIndex = deltaItem[1];
				if (moveToIndex === +index) return moveFromIndex;
				if (moveFromIndex <= reverseIndex && moveToIndex > reverseIndex) reverseIndex++;
				else if (moveFromIndex >= reverseIndex && moveToIndex < reverseIndex) reverseIndex--;
			} else if (deltaItem[2] === 0) {
				if (Number.parseInt(deltaIndex.substring(1), 10) <= reverseIndex) reverseIndex++;
			} else if (deltaItem.length === 1 && Number.parseInt(deltaIndex, 10) <= reverseIndex) reverseIndex--;
		}
	}
	return reverseIndex;
};
var collectChildrenReverseFilter$1 = (context) => {
	if (!context || !context.children) return;
	const deltaWithChildren = context.delta;
	if (deltaWithChildren._t !== "a") return;
	const arrayDelta = deltaWithChildren;
	const length = context.children.length;
	const delta = { _t: "a" };
	for (let index = 0; index < length; index++) {
		const child = context.children[index];
		if (child === void 0) continue;
		let name = child.newName;
		if (typeof name === "undefined") {
			if (child.childName === void 0) throw new Error("child.childName is undefined");
			name = reverseArrayDeltaIndex(arrayDelta, child.childName, child.result);
		}
		if (delta[name] !== child.result) delta[name] = child.result;
	}
	context.setResult(delta).exit();
};
collectChildrenReverseFilter$1.filterName = "arraysCollectChildren";
//#endregion
//#region node_modules/jsondiffpatch/lib/filters/dates.js
var diffFilter$2 = function datesDiffFilter(context) {
	if (context.left instanceof Date) {
		if (context.right instanceof Date) if (context.left.getTime() !== context.right.getTime()) context.setResult([context.left, context.right]);
		else context.setResult(void 0);
		else context.setResult([context.left, context.right]);
		context.exit();
	} else if (context.right instanceof Date) context.setResult([context.left, context.right]).exit();
};
diffFilter$2.filterName = "dates";
//#endregion
//#region node_modules/jsondiffpatch/lib/filters/nested.js
var UNSAFE_KEYS = /* @__PURE__ */ new Set(["__proto__"]);
var collectChildrenDiffFilter = (context) => {
	if (!context || !context.children) return;
	const length = context.children.length;
	let result = context.result;
	for (let index = 0; index < length; index++) {
		const child = context.children[index];
		if (child === void 0) continue;
		if (typeof child.result === "undefined") continue;
		result = result || {};
		if (child.childName === void 0) throw new Error("diff child.childName is undefined");
		result[child.childName] = child.result;
	}
	if (result && context.leftIsArray) result._t = "a";
	context.setResult(result).exit();
};
collectChildrenDiffFilter.filterName = "collectChildren";
var objectsDiffFilter = (context) => {
	var _a;
	if (context.leftIsArray || context.leftType !== "object") return;
	const left = context.left;
	const right = context.right;
	const propertyFilter = (_a = context.options) === null || _a === void 0 ? void 0 : _a.propertyFilter;
	for (const name in left) {
		if (!Object.prototype.hasOwnProperty.call(left, name)) continue;
		if (propertyFilter && !propertyFilter(name, context)) continue;
		const child = new DiffContext(left[name], right[name]);
		context.push(child, name);
	}
	for (const name in right) {
		if (!Object.prototype.hasOwnProperty.call(right, name)) continue;
		if (propertyFilter && !propertyFilter(name, context)) continue;
		if (typeof left[name] === "undefined") {
			const child = new DiffContext(void 0, right[name]);
			context.push(child, name);
		}
	}
	if (!context.children || context.children.length === 0) {
		context.setResult(void 0).exit();
		return;
	}
	context.exit();
};
objectsDiffFilter.filterName = "objects";
var patchFilter$2 = function nestedPatchFilter(context) {
	if (!context.nested) return;
	const nestedDelta = context.delta;
	if (nestedDelta._t) return;
	const objectDelta = nestedDelta;
	let childrenPushed = false;
	for (const name in objectDelta) {
		if (UNSAFE_KEYS.has(name)) continue;
		if (!Object.prototype.hasOwnProperty.call(objectDelta, name)) continue;
		const left = context.left;
		const child = new PatchContext(left !== null && typeof left === "object" && Object.prototype.hasOwnProperty.call(left, name) ? left[name] : void 0, objectDelta[name]);
		context.push(child, name);
		childrenPushed = true;
	}
	if (!childrenPushed) {
		context.setResult(context.left).exit();
		return;
	}
	context.exit();
};
patchFilter$2.filterName = "objects";
var collectChildrenPatchFilter = function collectChildrenPatchFilter(context) {
	if (!context || !context.children) return;
	if (context.delta._t) return;
	if (context.left === null || typeof context.left !== "object") {
		context.setResult(context.left).exit();
		return;
	}
	const object = context.left;
	const length = context.children.length;
	for (let index = 0; index < length; index++) {
		const child = context.children[index];
		if (child === void 0) continue;
		const property = child.childName;
		if (UNSAFE_KEYS.has(property)) continue;
		if (Object.prototype.hasOwnProperty.call(context.left, property) && child.result === void 0) delete object[property];
		else if (object[property] !== child.result) object[property] = child.result;
	}
	context.setResult(object).exit();
};
collectChildrenPatchFilter.filterName = "collectChildren";
var reverseFilter$2 = function nestedReverseFilter(context) {
	if (!context.nested) return;
	if (context.delta._t) return;
	const objectDelta = context.delta;
	let childrenPushed = false;
	for (const name in objectDelta) {
		if (UNSAFE_KEYS.has(name)) continue;
		if (!Object.prototype.hasOwnProperty.call(objectDelta, name)) continue;
		const child = new ReverseContext(objectDelta[name]);
		context.push(child, name);
		childrenPushed = true;
	}
	if (!childrenPushed) {
		context.setResult({}).exit();
		return;
	}
	context.exit();
};
reverseFilter$2.filterName = "objects";
var collectChildrenReverseFilter = (context) => {
	if (!context || !context.children) return;
	if (context.delta._t) return;
	const length = context.children.length;
	const delta = {};
	for (let index = 0; index < length; index++) {
		const child = context.children[index];
		if (child === void 0) continue;
		const property = child.childName;
		if (UNSAFE_KEYS.has(property)) continue;
		if (delta[property] !== child.result) delta[property] = child.result;
	}
	context.setResult(delta).exit();
};
collectChildrenReverseFilter.filterName = "collectChildren";
//#endregion
//#region node_modules/jsondiffpatch/lib/filters/texts.js
var TEXT_DIFF = 2;
var DEFAULT_MIN_LENGTH = 60;
var cachedDiffPatch = null;
function getDiffMatchPatch(options, required) {
	var _a;
	if (!cachedDiffPatch) {
		let instance;
		if ((_a = options === null || options === void 0 ? void 0 : options.textDiff) === null || _a === void 0 ? void 0 : _a.diffMatchPatch) instance = new options.textDiff.diffMatchPatch();
		else {
			if (!required) return null;
			const error = /* @__PURE__ */ new Error("The diff-match-patch library was not provided. Pass the library in through the options or use the `jsondiffpatch/with-text-diffs` entry-point.");
			error.diff_match_patch_not_found = true;
			throw error;
		}
		cachedDiffPatch = {
			diff: (txt1, txt2) => instance.patch_toText(instance.patch_make(txt1, txt2)),
			patch: (txt1, patch) => {
				const results = instance.patch_apply(instance.patch_fromText(patch), txt1);
				for (const resultOk of results[1]) if (!resultOk) {
					const error = /* @__PURE__ */ new Error("text patch failed");
					error.textPatchFailed = true;
					throw error;
				}
				return results[0];
			}
		};
	}
	return cachedDiffPatch;
}
var diffFilter$1 = function textsDiffFilter(context) {
	var _a, _b;
	if (context.leftType !== "string") return;
	const left = context.left;
	const right = context.right;
	const minLength = ((_b = (_a = context.options) === null || _a === void 0 ? void 0 : _a.textDiff) === null || _b === void 0 ? void 0 : _b.minLength) || DEFAULT_MIN_LENGTH;
	if (left.length < minLength || right.length < minLength) {
		context.setResult([left, right]).exit();
		return;
	}
	const diffMatchPatch = getDiffMatchPatch(context.options);
	if (!diffMatchPatch) {
		context.setResult([left, right]).exit();
		return;
	}
	const diff = diffMatchPatch.diff;
	context.setResult([
		diff(left, right),
		0,
		TEXT_DIFF
	]).exit();
};
diffFilter$1.filterName = "texts";
var patchFilter$1 = function textsPatchFilter(context) {
	if (context.nested) return;
	const nonNestedDelta = context.delta;
	if (nonNestedDelta[2] !== TEXT_DIFF) return;
	const textDiffDelta = nonNestedDelta;
	const patch = getDiffMatchPatch(context.options, true).patch;
	context.setResult(patch(context.left, textDiffDelta[0])).exit();
};
patchFilter$1.filterName = "texts";
var textDeltaReverse = (delta) => {
	var _a, _b, _c;
	const headerRegex = /^@@ +-(\d+),(\d+) +\+(\d+),(\d+) +@@$/;
	const lines = delta.split("\n");
	for (let i = 0; i < lines.length; i++) {
		const line = lines[i];
		if (line === void 0) continue;
		const lineStart = line.slice(0, 1);
		if (lineStart === "@") {
			const header = headerRegex.exec(line);
			if (header !== null) {
				const lineHeader = i;
				lines[lineHeader] = `@@ -${header[3]},${header[4]} +${header[1]},${header[2]} @@`;
			}
		} else if (lineStart === "+") {
			lines[i] = `-${(_a = lines[i]) === null || _a === void 0 ? void 0 : _a.slice(1)}`;
			if (((_b = lines[i - 1]) === null || _b === void 0 ? void 0 : _b.slice(0, 1)) === "+") {
				const lineTmp = lines[i];
				lines[i] = lines[i - 1];
				lines[i - 1] = lineTmp;
			}
		} else if (lineStart === "-") lines[i] = `+${(_c = lines[i]) === null || _c === void 0 ? void 0 : _c.slice(1)}`;
	}
	return lines.join("\n");
};
var reverseFilter$1 = function textsReverseFilter(context) {
	if (context.nested) return;
	const nonNestedDelta = context.delta;
	if (nonNestedDelta[2] !== TEXT_DIFF) return;
	const textDiffDelta = nonNestedDelta;
	context.setResult([
		textDeltaReverse(textDiffDelta[0]),
		0,
		TEXT_DIFF
	]).exit();
};
reverseFilter$1.filterName = "texts";
//#endregion
//#region node_modules/jsondiffpatch/lib/filters/trivial.js
var diffFilter = function trivialMatchesDiffFilter(context) {
	if (context.left === context.right) {
		context.setResult(void 0).exit();
		return;
	}
	if (typeof context.left === "undefined") {
		if (typeof context.right === "function") throw new Error("functions are not supported");
		context.setResult([context.right]).exit();
		return;
	}
	if (typeof context.right === "undefined") {
		context.setResult([
			context.left,
			0,
			0
		]).exit();
		return;
	}
	if (typeof context.left === "function" || typeof context.right === "function") throw new Error("functions are not supported");
	context.leftType = context.left === null ? "null" : typeof context.left;
	context.rightType = context.right === null ? "null" : typeof context.right;
	if (context.leftType !== context.rightType) {
		context.setResult([context.left, context.right]).exit();
		return;
	}
	if (context.leftType === "boolean" || context.leftType === "number") {
		context.setResult([context.left, context.right]).exit();
		return;
	}
	if (context.leftType === "object") context.leftIsArray = Array.isArray(context.left);
	if (context.rightType === "object") context.rightIsArray = Array.isArray(context.right);
	if (context.leftIsArray !== context.rightIsArray) {
		context.setResult([context.left, context.right]).exit();
		return;
	}
	if (context.left instanceof RegExp) if (context.right instanceof RegExp) context.setResult([context.left.toString(), context.right.toString()]).exit();
	else context.setResult([context.left, context.right]).exit();
};
diffFilter.filterName = "trivial";
var patchFilter = function trivialMatchesPatchFilter(context) {
	if (typeof context.delta === "undefined") {
		context.setResult(context.left).exit();
		return;
	}
	context.nested = !Array.isArray(context.delta);
	if (context.nested) return;
	const nonNestedDelta = context.delta;
	if (nonNestedDelta.length === 1) {
		context.setResult(nonNestedDelta[0]).exit();
		return;
	}
	if (nonNestedDelta.length === 2) {
		if (context.left instanceof RegExp) {
			const regexArgs = /^\/(.*)\/([gimyu]+)$/.exec(nonNestedDelta[1]);
			if (regexArgs === null || regexArgs === void 0 ? void 0 : regexArgs[1]) {
				context.setResult(new RegExp(regexArgs[1], regexArgs[2])).exit();
				return;
			}
		}
		context.setResult(nonNestedDelta[1]).exit();
		return;
	}
	if (nonNestedDelta.length === 3 && nonNestedDelta[2] === 0) context.setResult(void 0).exit();
};
patchFilter.filterName = "trivial";
var reverseFilter = function trivialReferseFilter(context) {
	if (typeof context.delta === "undefined") {
		context.setResult(context.delta).exit();
		return;
	}
	context.nested = !Array.isArray(context.delta);
	if (context.nested) return;
	const nonNestedDelta = context.delta;
	if (nonNestedDelta.length === 1) {
		context.setResult([
			nonNestedDelta[0],
			0,
			0
		]).exit();
		return;
	}
	if (nonNestedDelta.length === 2) {
		context.setResult([nonNestedDelta[1], nonNestedDelta[0]]).exit();
		return;
	}
	if (nonNestedDelta.length === 3 && nonNestedDelta[2] === 0) context.setResult([nonNestedDelta[0]]).exit();
};
reverseFilter.filterName = "trivial";
//#endregion
//#region node_modules/jsondiffpatch/lib/diffpatcher.js
var DiffPatcher = class {
	constructor(options) {
		this.processor = new Processor(options);
		this.processor.pipe(new Pipe("diff").append(collectChildrenDiffFilter, diffFilter, diffFilter$2, diffFilter$1, objectsDiffFilter, diffFilter$3).shouldHaveResult());
		this.processor.pipe(new Pipe("patch").append(collectChildrenPatchFilter, collectChildrenPatchFilter$1, patchFilter, patchFilter$1, patchFilter$2, patchFilter$3).shouldHaveResult());
		this.processor.pipe(new Pipe("reverse").append(collectChildrenReverseFilter, collectChildrenReverseFilter$1, reverseFilter, reverseFilter$1, reverseFilter$2, reverseFilter$3).shouldHaveResult());
	}
	options(options) {
		return this.processor.options(options);
	}
	diff(left, right) {
		return this.processor.process(new DiffContext(left, right));
	}
	patch(left, delta) {
		return this.processor.process(new PatchContext(left, delta));
	}
	reverse(delta) {
		return this.processor.process(new ReverseContext(delta));
	}
	unpatch(right, delta) {
		return this.patch(right, this.reverse(delta));
	}
	clone(value) {
		return clone$1(value);
	}
};
//#endregion
//#region node_modules/jsondiffpatch/lib/index.js
function create$1(options) {
	return new DiffPatcher(options);
}
//#endregion
//#region node_modules/jsondiffpatch/lib/formatters/base.js
var BaseFormatter = class {
	format(delta, left) {
		const context = {};
		this.prepareContext(context);
		const preparedContext = context;
		this.recurse(preparedContext, delta, left);
		return this.finalize(preparedContext);
	}
	prepareContext(context) {
		context.buffer = [];
		context.out = function(...args) {
			if (!this.buffer) throw new Error("context buffer is not initialized");
			this.buffer.push(...args);
		};
	}
	typeFormattterNotFound(_context, deltaType) {
		throw new Error(`cannot format delta type: ${deltaType}`);
	}
	typeFormattterErrorFormatter(_context, _err, _delta, _leftValue, _key, _leftKey, _movedFrom) {}
	finalize({ buffer }) {
		if (Array.isArray(buffer)) return buffer.join("");
		return "";
	}
	recurse(context, delta, left, key, leftKey, movedFrom, isLast) {
		const leftValue = delta && movedFrom ? movedFrom.value : left;
		if (typeof delta === "undefined" && typeof key === "undefined") return;
		const type = this.getDeltaType(delta, movedFrom);
		const nodeType = type === "node" ? delta._t === "a" ? "array" : "object" : "";
		if (typeof key !== "undefined") this.nodeBegin(context, key, leftKey, type, nodeType, isLast !== null && isLast !== void 0 ? isLast : false);
		else this.rootBegin(context, type, nodeType);
		let typeFormattter;
		try {
			typeFormattter = type !== "unknown" ? this[`format_${type}`] : this.typeFormattterNotFound(context, type);
			typeFormattter.call(this, context, delta, leftValue, key, leftKey, movedFrom);
		} catch (err) {
			this.typeFormattterErrorFormatter(context, err, delta, leftValue, key, leftKey, movedFrom);
			if (typeof console !== "undefined" && console.error) console.error(err.stack);
		}
		if (typeof key !== "undefined") this.nodeEnd(context, key, leftKey, type, nodeType, isLast !== null && isLast !== void 0 ? isLast : false);
		else this.rootEnd(context, type, nodeType);
	}
	formatDeltaChildren(context, delta, left) {
		this.forEachDeltaKey(delta, left, (key, leftKey, movedFrom, isLast) => {
			this.recurse(context, delta[key], left ? left[leftKey] : void 0, key, leftKey, movedFrom, isLast);
		});
	}
	forEachDeltaKey(delta, left, fn) {
		const keys = [];
		if (!(delta._t === "a")) {
			const deltaKeys = Object.keys(delta);
			if (typeof left === "object" && left !== null) keys.push(...Object.keys(left));
			for (const key of deltaKeys) {
				if (keys.indexOf(key) >= 0) continue;
				keys.push(key);
			}
			for (let index = 0; index < keys.length; index++) {
				const key = keys[index];
				if (key === void 0) continue;
				fn(key, key, void 0, index === keys.length - 1);
			}
			return;
		}
		const movedFrom = {};
		for (const key in delta) if (Object.prototype.hasOwnProperty.call(delta, key)) {
			const value = delta[key];
			if (Array.isArray(value) && value[2] === 3) {
				const movedDelta = value;
				movedFrom[movedDelta[1]] = Number.parseInt(key.substring(1));
			}
		}
		const arrayDelta = delta;
		let leftIndex = 0;
		let rightIndex = 0;
		const leftArray = Array.isArray(left) ? left : void 0;
		const leftLength = leftArray ? leftArray.length : Object.keys(arrayDelta).reduce((max, key) => {
			if (key === "_t") return max;
			if (key.substring(0, 1) === "_") {
				const itemDelta = arrayDelta[key];
				const leftIndex = Number.parseInt(key.substring(1));
				const rightIndex = Array.isArray(itemDelta) && itemDelta.length >= 3 && itemDelta[2] === 3 ? itemDelta[1] : void 0;
				const maxIndex = Math.max(leftIndex, rightIndex !== null && rightIndex !== void 0 ? rightIndex : 0);
				return maxIndex > max ? maxIndex : max;
			}
			const rightIndex = Number.parseInt(key);
			const leftIndex = movedFrom[rightIndex];
			const maxIndex = Math.max(leftIndex !== null && leftIndex !== void 0 ? leftIndex : 0, rightIndex !== null && rightIndex !== void 0 ? rightIndex : 0);
			return maxIndex > max ? maxIndex : max;
		}, 0) + 1;
		let rightLength = leftLength;
		let previousFnArgs;
		const addKey = (...args) => {
			if (previousFnArgs) fn(...previousFnArgs);
			previousFnArgs = args;
		};
		const flushLastKey = () => {
			if (!previousFnArgs) return;
			fn(previousFnArgs[0], previousFnArgs[1], previousFnArgs[2], true);
		};
		while (leftIndex < leftLength || rightIndex < rightLength || `${rightIndex}` in arrayDelta) {
			let hasDelta = false;
			const leftIndexKey = `_${leftIndex}`;
			const rightIndexKey = `${rightIndex}`;
			const movedFromIndex = rightIndex in movedFrom ? movedFrom[rightIndex] : void 0;
			if (leftIndexKey in arrayDelta) {
				hasDelta = true;
				const itemDelta = arrayDelta[leftIndexKey];
				addKey(leftIndexKey, movedFromIndex !== null && movedFromIndex !== void 0 ? movedFromIndex : leftIndex, movedFromIndex ? {
					key: `_${movedFromIndex}`,
					value: leftArray ? leftArray[movedFromIndex] : void 0
				} : void 0, false);
				if (Array.isArray(itemDelta)) if (itemDelta[2] === 0) {
					rightLength--;
					leftIndex++;
				} else if (itemDelta[2] === 3) leftIndex++;
				else leftIndex++;
				else leftIndex++;
			}
			if (rightIndexKey in arrayDelta) {
				hasDelta = true;
				const itemDelta = arrayDelta[rightIndexKey];
				const isItemAdded = Array.isArray(itemDelta) && itemDelta.length === 1;
				addKey(rightIndexKey, movedFromIndex !== null && movedFromIndex !== void 0 ? movedFromIndex : leftIndex, movedFromIndex ? {
					key: `_${movedFromIndex}`,
					value: leftArray ? leftArray[movedFromIndex] : void 0
				} : void 0, false);
				if (isItemAdded) {
					rightLength++;
					rightIndex++;
				} else if (movedFromIndex === void 0) {
					leftIndex++;
					rightIndex++;
				} else rightIndex++;
			}
			if (!hasDelta) {
				if (leftArray && movedFromIndex === void 0 || this.includeMoveDestinations !== false) addKey(rightIndexKey, movedFromIndex !== null && movedFromIndex !== void 0 ? movedFromIndex : leftIndex, movedFromIndex ? {
					key: `_${movedFromIndex}`,
					value: leftArray ? leftArray[movedFromIndex] : void 0
				} : void 0, false);
				if (movedFromIndex !== void 0) rightIndex++;
				else {
					leftIndex++;
					rightIndex++;
				}
			}
		}
		flushLastKey();
	}
	getDeltaType(delta, movedFrom) {
		if (typeof delta === "undefined") {
			if (typeof movedFrom !== "undefined") return "movedestination";
			return "unchanged";
		}
		if (Array.isArray(delta)) {
			if (delta.length === 1) return "added";
			if (delta.length === 2) return "modified";
			if (delta.length === 3 && delta[2] === 0) return "deleted";
			if (delta.length === 3 && delta[2] === 2) return "textdiff";
			if (delta.length === 3 && delta[2] === 3) return "moved";
		} else if (typeof delta === "object") return "node";
		return "unknown";
	}
	parseTextDiff(value) {
		var _a;
		const output = [];
		const lines = value.split("\n@@ ");
		for (const line of lines) {
			const lineOutput = { pieces: [] };
			const location = (_a = /^(?:@@ )?[-+]?(\d+),(\d+)/.exec(line)) === null || _a === void 0 ? void 0 : _a.slice(1);
			if (!location) throw new Error("invalid text diff format");
			assertArrayHasAtLeast2(location);
			lineOutput.location = {
				line: location[0],
				chr: location[1]
			};
			const pieces = line.split("\n").slice(1);
			for (let pieceIndex = 0, piecesLength = pieces.length; pieceIndex < piecesLength; pieceIndex++) {
				const piece = pieces[pieceIndex];
				if (piece === void 0 || !piece.length) continue;
				const pieceOutput = { type: "context" };
				if (piece.substring(0, 1) === "+") pieceOutput.type = "added";
				else if (piece.substring(0, 1) === "-") pieceOutput.type = "deleted";
				pieceOutput.text = piece.slice(1);
				lineOutput.pieces.push(pieceOutput);
			}
			output.push(lineOutput);
		}
		return output;
	}
};
//#endregion
//#region node_modules/jsondiffpatch/lib/formatters/html.js
var HtmlFormatter = class extends BaseFormatter {
	typeFormattterErrorFormatter(context, err) {
		const message = typeof err === "object" && err !== null && "message" in err && typeof err.message === "string" ? err.message : String(err);
		context.out(`<pre class="jsondiffpatch-error">${htmlEscape(message)}</pre>`);
	}
	formatValue(context, value) {
		const valueAsHtml = typeof value === "undefined" ? "undefined" : htmlEscape(JSON.stringify(value, null, 2));
		context.out(`<pre>${valueAsHtml}</pre>`);
	}
	formatTextDiffString(context, value) {
		const lines = this.parseTextDiff(value);
		context.out("<ul class=\"jsondiffpatch-textdiff\">");
		for (let i = 0, l = lines.length; i < l; i++) {
			const line = lines[i];
			if (line === void 0) return;
			context.out(`<li><div class="jsondiffpatch-textdiff-location"><span class="jsondiffpatch-textdiff-line-number">${line.location.line}</span><span class="jsondiffpatch-textdiff-char">${line.location.chr}</span></div><div class="jsondiffpatch-textdiff-line">`);
			const pieces = line.pieces;
			for (let pieceIndex = 0, piecesLength = pieces.length; pieceIndex < piecesLength; pieceIndex++) {
				const piece = pieces[pieceIndex];
				if (piece === void 0) return;
				context.out(`<span class="jsondiffpatch-textdiff-${piece.type}">${htmlEscape(decodeURI(piece.text))}</span>`);
			}
			context.out("</div></li>");
		}
		context.out("</ul>");
	}
	rootBegin(context, type, nodeType) {
		const nodeClass = `jsondiffpatch-${type}${nodeType ? ` jsondiffpatch-child-node-type-${nodeType}` : ""}`;
		context.out(`<div class="jsondiffpatch-delta ${nodeClass}">`);
	}
	rootEnd(context) {
		context.out(`</div>${context.hasArrows ? `<script type="text/javascript">setTimeout(${adjustArrows.toString()},10);<\/script>` : ""}`);
	}
	nodeBegin(context, key, leftKey, type, nodeType) {
		const nodeClass = `jsondiffpatch-${type}${nodeType ? ` jsondiffpatch-child-node-type-${nodeType}` : ""}`;
		const label = typeof leftKey === "number" && key.substring(0, 1) === "_" ? key.substring(1) : key;
		context.out(`<li class="${nodeClass}" data-key="${htmlEscape(key)}"><div class="jsondiffpatch-property-name">${htmlEscape(label)}</div>`);
	}
	nodeEnd(context) {
		context.out("</li>");
	}
	format_unchanged(context, _delta, left) {
		if (typeof left === "undefined") return;
		context.out("<div class=\"jsondiffpatch-value\">");
		this.formatValue(context, left);
		context.out("</div>");
	}
	format_movedestination(context, _delta, left) {
		if (typeof left === "undefined") return;
		context.out("<div class=\"jsondiffpatch-value\">");
		this.formatValue(context, left);
		context.out("</div>");
	}
	format_node(context, delta, left) {
		const nodeType = delta._t === "a" ? "array" : "object";
		context.out(`<ul class="jsondiffpatch-node jsondiffpatch-node-type-${nodeType}">`);
		this.formatDeltaChildren(context, delta, left);
		context.out("</ul>");
	}
	format_added(context, delta) {
		context.out("<div class=\"jsondiffpatch-value\">");
		this.formatValue(context, delta[0]);
		context.out("</div>");
	}
	format_modified(context, delta) {
		context.out("<div class=\"jsondiffpatch-value jsondiffpatch-left-value\">");
		this.formatValue(context, delta[0]);
		context.out("</div><div class=\"jsondiffpatch-value jsondiffpatch-right-value\">");
		this.formatValue(context, delta[1]);
		context.out("</div>");
	}
	format_deleted(context, delta) {
		context.out("<div class=\"jsondiffpatch-value\">");
		this.formatValue(context, delta[0]);
		context.out("</div>");
	}
	format_moved(context, delta) {
		context.out("<div class=\"jsondiffpatch-value\">");
		this.formatValue(context, delta[0]);
		context.out(`</div><div class="jsondiffpatch-moved-destination">${delta[1]}</div>`);
		context.out("<div class=\"jsondiffpatch-arrow\" style=\"position: relative; left: -34px;\">\n          <svg width=\"30\" height=\"60\" style=\"position: absolute; display: none;\">\n          <defs>\n              <marker id=\"markerArrow\" markerWidth=\"8\" markerHeight=\"8\"\n                 refx=\"2\" refy=\"4\" stroke=\"#88f\"\n                     orient=\"auto\" markerUnits=\"userSpaceOnUse\">\n                  <path d=\"M1,1 L1,7 L7,4 L1,1\" style=\"fill: #339;\" />\n              </marker>\n          </defs>\n          <path d=\"M30,0 Q-10,25 26,50\"\n            style=\"stroke: #88f; stroke-width: 2px; fill: none; stroke-opacity: 0.5; marker-end: url(#markerArrow);\"\n          ></path>\n          </svg>\n      </div>");
		context.hasArrows = true;
	}
	format_textdiff(context, delta) {
		context.out("<div class=\"jsondiffpatch-value\">");
		this.formatTextDiffString(context, delta[0]);
		context.out("</div>");
	}
};
function htmlEscape(value) {
	if (typeof value === "number") return value;
	let html = String(value);
	for (const replacement of [
		[/&/g, "&amp;"],
		[/</g, "&lt;"],
		[/>/g, "&gt;"],
		[/'/g, "&apos;"],
		[/"/g, "&quot;"]
	]) html = html.replace(replacement[0], replacement[1]);
	return html;
}
var adjustArrows = function jsondiffpatchHtmlFormatterAdjustArrows(nodeArg) {
	const node = nodeArg || document;
	const getElementText = ({ textContent, innerText }) => textContent || innerText;
	const eachByQuery = (el, query, fn) => {
		const elems = el.querySelectorAll(query);
		for (let i = 0, l = elems.length; i < l; i++) fn(elems[i]);
	};
	const eachChildren = ({ children }, fn) => {
		for (let i = 0, l = children.length; i < l; i++) {
			const element = children[i];
			if (!element) continue;
			fn(element, i);
		}
	};
	eachByQuery(node, ".jsondiffpatch-arrow", ({ parentNode, children, style }) => {
		const arrowParent = parentNode;
		const svg = children[0];
		const path = svg.children[1];
		svg.style.display = "none";
		const moveDestinationElem = arrowParent.querySelector(".jsondiffpatch-moved-destination");
		if (!(moveDestinationElem instanceof HTMLElement)) return;
		const destination = getElementText(moveDestinationElem);
		const container = arrowParent.parentNode;
		if (!container) return;
		let destinationElem;
		eachChildren(container, (child) => {
			if (child.getAttribute("data-key") === destination) destinationElem = child;
		});
		if (!destinationElem) return;
		try {
			const distance = destinationElem.offsetTop - arrowParent.offsetTop;
			svg.setAttribute("height", `${Math.abs(distance) + 6}`);
			style.top = `${-8 + (distance > 0 ? 0 : distance)}px`;
			const curve = distance > 0 ? `M30,0 Q-10,${Math.round(distance / 2)} 26,${distance - 4}` : `M30,${-distance} Q-10,${Math.round(-distance / 2)} 26,4`;
			path.setAttribute("d", curve);
			svg.style.display = "";
		} catch (err) {
			console.debug(`[jsondiffpatch] error adjusting arrows: ${err}`);
		}
	});
};
var defaultInstance;
function format(delta, left) {
	if (!defaultInstance) defaultInstance = new HtmlFormatter();
	return defaultInstance.format(delta, left);
}
//#endregion
//#region node_modules/dompurify/dist/purify.es.mjs
/*! @license DOMPurify 3.4.11 | (c) Cure53 and other contributors | Released under the Apache license 2.0 and Mozilla Public License 2.0 | github.com/cure53/DOMPurify/blob/3.4.11/LICENSE */
function _arrayLikeToArray(r, a) {
	(null == a || a > r.length) && (a = r.length);
	for (var e = 0, n = Array(a); e < a; e++) n[e] = r[e];
	return n;
}
function _arrayWithHoles(r) {
	if (Array.isArray(r)) return r;
}
function _iterableToArrayLimit(r, l) {
	var t = null == r ? null : "undefined" != typeof Symbol && r[Symbol.iterator] || r["@@iterator"];
	if (null != t) {
		var e, n, i, u, a = [], f = true, o = false;
		try {
			if (i = (t = t.call(r)).next, 0 === l);
			else for (; !(f = (e = i.call(t)).done) && (a.push(e.value), a.length !== l); f = !0);
		} catch (r) {
			o = true, n = r;
		} finally {
			try {
				if (!f && null != t.return && (u = t.return(), Object(u) !== u)) return;
			} finally {
				if (o) throw n;
			}
		}
		return a;
	}
}
function _nonIterableRest() {
	throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.");
}
function _slicedToArray(r, e) {
	return _arrayWithHoles(r) || _iterableToArrayLimit(r, e) || _unsupportedIterableToArray(r, e) || _nonIterableRest();
}
function _unsupportedIterableToArray(r, a) {
	if (r) {
		if ("string" == typeof r) return _arrayLikeToArray(r, a);
		var t = {}.toString.call(r).slice(8, -1);
		return "Object" === t && r.constructor && (t = r.constructor.name), "Map" === t || "Set" === t ? Array.from(r) : "Arguments" === t || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(t) ? _arrayLikeToArray(r, a) : void 0;
	}
}
var entries = Object.entries, setPrototypeOf = Object.setPrototypeOf, isFrozen = Object.isFrozen, getPrototypeOf = Object.getPrototypeOf, getOwnPropertyDescriptor = Object.getOwnPropertyDescriptor;
var freeze = Object.freeze, seal = Object.seal, create = Object.create;
var _ref = typeof Reflect !== "undefined" && Reflect, apply = _ref.apply, construct = _ref.construct;
if (!freeze) freeze = function freeze(x) {
	return x;
};
if (!seal) seal = function seal(x) {
	return x;
};
if (!apply) apply = function apply(func, thisArg) {
	for (var _len = arguments.length, args = new Array(_len > 2 ? _len - 2 : 0), _key = 2; _key < _len; _key++) args[_key - 2] = arguments[_key];
	return func.apply(thisArg, args);
};
if (!construct) construct = function construct(Func) {
	for (var _len2 = arguments.length, args = new Array(_len2 > 1 ? _len2 - 1 : 0), _key2 = 1; _key2 < _len2; _key2++) args[_key2 - 1] = arguments[_key2];
	return new Func(...args);
};
var arrayForEach = unapply(Array.prototype.forEach);
var arrayLastIndexOf = unapply(Array.prototype.lastIndexOf);
var arrayPop = unapply(Array.prototype.pop);
var arrayPush = unapply(Array.prototype.push);
var arraySplice = unapply(Array.prototype.splice);
var arrayIsArray = Array.isArray;
var stringToLowerCase = unapply(String.prototype.toLowerCase);
var stringToString = unapply(String.prototype.toString);
var stringMatch = unapply(String.prototype.match);
var stringReplace = unapply(String.prototype.replace);
var stringIndexOf = unapply(String.prototype.indexOf);
var stringTrim = unapply(String.prototype.trim);
var numberToString = unapply(Number.prototype.toString);
var booleanToString = unapply(Boolean.prototype.toString);
var bigintToString = typeof BigInt === "undefined" ? null : unapply(BigInt.prototype.toString);
var symbolToString = typeof Symbol === "undefined" ? null : unapply(Symbol.prototype.toString);
var objectHasOwnProperty = unapply(Object.prototype.hasOwnProperty);
var objectToString = unapply(Object.prototype.toString);
var regExpTest = unapply(RegExp.prototype.test);
var typeErrorCreate = unconstruct(TypeError);
/**
* Creates a new function that calls the given function with a specified thisArg and arguments.
*
* @param func - The function to be wrapped and called.
* @returns A new function that calls the given function with a specified thisArg and arguments.
*/
function unapply(func) {
	return function(thisArg) {
		if (thisArg instanceof RegExp) thisArg.lastIndex = 0;
		for (var _len3 = arguments.length, args = new Array(_len3 > 1 ? _len3 - 1 : 0), _key3 = 1; _key3 < _len3; _key3++) args[_key3 - 1] = arguments[_key3];
		return apply(func, thisArg, args);
	};
}
/**
* Creates a new function that constructs an instance of the given constructor function with the provided arguments.
*
* @param func - The constructor function to be wrapped and called.
* @returns A new function that constructs an instance of the given constructor function with the provided arguments.
*/
function unconstruct(Func) {
	return function() {
		for (var _len4 = arguments.length, args = new Array(_len4), _key4 = 0; _key4 < _len4; _key4++) args[_key4] = arguments[_key4];
		return construct(Func, args);
	};
}
/**
* Add properties to a lookup table
*
* @param set - The set to which elements will be added.
* @param array - The array containing elements to be added to the set.
* @param transformCaseFunc - An optional function to transform the case of each element before adding to the set.
* @returns The modified set with added elements.
*/
function addToSet(set, array) {
	let transformCaseFunc = arguments.length > 2 && arguments[2] !== void 0 ? arguments[2] : stringToLowerCase;
	if (setPrototypeOf) setPrototypeOf(set, null);
	if (!arrayIsArray(array)) return set;
	let l = array.length;
	while (l--) {
		let element = array[l];
		if (typeof element === "string") {
			const lcElement = transformCaseFunc(element);
			if (lcElement !== element) {
				if (!isFrozen(array)) array[l] = lcElement;
				element = lcElement;
			}
		}
		set[element] = true;
	}
	return set;
}
/**
* Clean up an array to harden against CSPP
*
* @param array - The array to be cleaned.
* @returns The cleaned version of the array
*/
function cleanArray(array) {
	for (let index = 0; index < array.length; index++) if (!objectHasOwnProperty(array, index)) array[index] = null;
	return array;
}
/**
* Shallow clone an object
*
* @param object - The object to be cloned.
* @returns A new object that copies the original.
*/
function clone(object) {
	const newObject = create(null);
	for (const _ref2 of entries(object)) {
		var _ref3 = _slicedToArray(_ref2, 2);
		const property = _ref3[0];
		const value = _ref3[1];
		if (objectHasOwnProperty(object, property)) if (arrayIsArray(value)) newObject[property] = cleanArray(value);
		else if (value && typeof value === "object" && value.constructor === Object) newObject[property] = clone(value);
		else newObject[property] = value;
	}
	return newObject;
}
/**
* Convert non-node values into strings without depending on direct property access.
*
* @param value - The value to stringify.
* @returns A string representation of the provided value.
*/
function stringifyValue(value) {
	switch (typeof value) {
		case "string": return value;
		case "number": return numberToString(value);
		case "boolean": return booleanToString(value);
		case "bigint": return bigintToString ? bigintToString(value) : "0";
		case "symbol": return symbolToString ? symbolToString(value) : "Symbol()";
		case "undefined": return objectToString(value);
		case "function":
		case "object": {
			if (value === null) return objectToString(value);
			const valueAsRecord = value;
			const valueToString = lookupGetter(valueAsRecord, "toString");
			if (typeof valueToString === "function") {
				const stringified = valueToString(valueAsRecord);
				return typeof stringified === "string" ? stringified : objectToString(stringified);
			}
			return objectToString(value);
		}
		default: return objectToString(value);
	}
}
/**
* This method automatically checks if the prop is function or getter and behaves accordingly.
*
* @param object - The object to look up the getter function in its prototype chain.
* @param prop - The property name for which to find the getter function.
* @returns The getter function found in the prototype chain or a fallback function.
*/
function lookupGetter(object, prop) {
	while (object !== null) {
		const desc = getOwnPropertyDescriptor(object, prop);
		if (desc) {
			if (desc.get) return unapply(desc.get);
			if (typeof desc.value === "function") return unapply(desc.value);
		}
		object = getPrototypeOf(object);
	}
	function fallbackValue() {
		return null;
	}
	return fallbackValue;
}
function isRegex(value) {
	try {
		regExpTest(value, "");
		return true;
	} catch (_unused) {
		return false;
	}
}
var html$1 = freeze([
	"a",
	"abbr",
	"acronym",
	"address",
	"area",
	"article",
	"aside",
	"audio",
	"b",
	"bdi",
	"bdo",
	"big",
	"blink",
	"blockquote",
	"body",
	"br",
	"button",
	"canvas",
	"caption",
	"center",
	"cite",
	"code",
	"col",
	"colgroup",
	"content",
	"data",
	"datalist",
	"dd",
	"decorator",
	"del",
	"details",
	"dfn",
	"dialog",
	"dir",
	"div",
	"dl",
	"dt",
	"element",
	"em",
	"fieldset",
	"figcaption",
	"figure",
	"font",
	"footer",
	"form",
	"h1",
	"h2",
	"h3",
	"h4",
	"h5",
	"h6",
	"head",
	"header",
	"hgroup",
	"hr",
	"html",
	"i",
	"img",
	"input",
	"ins",
	"kbd",
	"label",
	"legend",
	"li",
	"main",
	"map",
	"mark",
	"marquee",
	"menu",
	"menuitem",
	"meter",
	"nav",
	"nobr",
	"ol",
	"optgroup",
	"option",
	"output",
	"p",
	"picture",
	"pre",
	"progress",
	"q",
	"rp",
	"rt",
	"ruby",
	"s",
	"samp",
	"search",
	"section",
	"select",
	"shadow",
	"slot",
	"small",
	"source",
	"spacer",
	"span",
	"strike",
	"strong",
	"style",
	"sub",
	"summary",
	"sup",
	"table",
	"tbody",
	"td",
	"template",
	"textarea",
	"tfoot",
	"th",
	"thead",
	"time",
	"tr",
	"track",
	"tt",
	"u",
	"ul",
	"var",
	"video",
	"wbr"
]);
var svg$1 = freeze([
	"svg",
	"a",
	"altglyph",
	"altglyphdef",
	"altglyphitem",
	"animatecolor",
	"animatemotion",
	"animatetransform",
	"circle",
	"clippath",
	"defs",
	"desc",
	"ellipse",
	"enterkeyhint",
	"exportparts",
	"filter",
	"font",
	"g",
	"glyph",
	"glyphref",
	"hkern",
	"image",
	"inputmode",
	"line",
	"lineargradient",
	"marker",
	"mask",
	"metadata",
	"mpath",
	"part",
	"path",
	"pattern",
	"polygon",
	"polyline",
	"radialgradient",
	"rect",
	"stop",
	"style",
	"switch",
	"symbol",
	"text",
	"textpath",
	"title",
	"tref",
	"tspan",
	"view",
	"vkern"
]);
var svgFilters = freeze([
	"feBlend",
	"feColorMatrix",
	"feComponentTransfer",
	"feComposite",
	"feConvolveMatrix",
	"feDiffuseLighting",
	"feDisplacementMap",
	"feDistantLight",
	"feDropShadow",
	"feFlood",
	"feFuncA",
	"feFuncB",
	"feFuncG",
	"feFuncR",
	"feGaussianBlur",
	"feImage",
	"feMerge",
	"feMergeNode",
	"feMorphology",
	"feOffset",
	"fePointLight",
	"feSpecularLighting",
	"feSpotLight",
	"feTile",
	"feTurbulence"
]);
var svgDisallowed = freeze([
	"animate",
	"color-profile",
	"cursor",
	"discard",
	"font-face",
	"font-face-format",
	"font-face-name",
	"font-face-src",
	"font-face-uri",
	"foreignobject",
	"hatch",
	"hatchpath",
	"mesh",
	"meshgradient",
	"meshpatch",
	"meshrow",
	"missing-glyph",
	"script",
	"set",
	"solidcolor",
	"unknown",
	"use"
]);
var mathMl$1 = freeze([
	"math",
	"menclose",
	"merror",
	"mfenced",
	"mfrac",
	"mglyph",
	"mi",
	"mlabeledtr",
	"mmultiscripts",
	"mn",
	"mo",
	"mover",
	"mpadded",
	"mphantom",
	"mroot",
	"mrow",
	"ms",
	"mspace",
	"msqrt",
	"mstyle",
	"msub",
	"msup",
	"msubsup",
	"mtable",
	"mtd",
	"mtext",
	"mtr",
	"munder",
	"munderover",
	"mprescripts"
]);
var mathMlDisallowed = freeze([
	"maction",
	"maligngroup",
	"malignmark",
	"mlongdiv",
	"mscarries",
	"mscarry",
	"msgroup",
	"mstack",
	"msline",
	"msrow",
	"semantics",
	"annotation",
	"annotation-xml",
	"mprescripts",
	"none"
]);
var text = freeze(["#text"]);
var html = freeze([
	"accept",
	"action",
	"align",
	"alt",
	"autocapitalize",
	"autocomplete",
	"autopictureinpicture",
	"autoplay",
	"background",
	"bgcolor",
	"border",
	"capture",
	"cellpadding",
	"cellspacing",
	"checked",
	"cite",
	"class",
	"clear",
	"color",
	"cols",
	"colspan",
	"command",
	"commandfor",
	"controls",
	"controlslist",
	"coords",
	"crossorigin",
	"datetime",
	"decoding",
	"default",
	"dir",
	"disabled",
	"disablepictureinpicture",
	"disableremoteplayback",
	"download",
	"draggable",
	"enctype",
	"enterkeyhint",
	"exportparts",
	"face",
	"for",
	"headers",
	"height",
	"hidden",
	"high",
	"href",
	"hreflang",
	"id",
	"inert",
	"inputmode",
	"integrity",
	"ismap",
	"kind",
	"label",
	"lang",
	"list",
	"loading",
	"loop",
	"low",
	"max",
	"maxlength",
	"media",
	"method",
	"min",
	"minlength",
	"multiple",
	"muted",
	"name",
	"nonce",
	"noshade",
	"novalidate",
	"nowrap",
	"open",
	"optimum",
	"part",
	"pattern",
	"placeholder",
	"playsinline",
	"popover",
	"popovertarget",
	"popovertargetaction",
	"poster",
	"preload",
	"pubdate",
	"radiogroup",
	"readonly",
	"rel",
	"required",
	"rev",
	"reversed",
	"role",
	"rows",
	"rowspan",
	"spellcheck",
	"scope",
	"selected",
	"shape",
	"size",
	"sizes",
	"slot",
	"span",
	"srclang",
	"start",
	"src",
	"srcset",
	"step",
	"style",
	"summary",
	"tabindex",
	"title",
	"translate",
	"type",
	"usemap",
	"valign",
	"value",
	"width",
	"wrap",
	"xmlns"
]);
var svg = freeze([
	"accent-height",
	"accumulate",
	"additive",
	"alignment-baseline",
	"amplitude",
	"ascent",
	"attributename",
	"attributetype",
	"azimuth",
	"basefrequency",
	"baseline-shift",
	"begin",
	"bias",
	"by",
	"class",
	"clip",
	"clippathunits",
	"clip-path",
	"clip-rule",
	"color",
	"color-interpolation",
	"color-interpolation-filters",
	"color-profile",
	"color-rendering",
	"cx",
	"cy",
	"d",
	"dx",
	"dy",
	"diffuseconstant",
	"direction",
	"display",
	"divisor",
	"dur",
	"edgemode",
	"elevation",
	"end",
	"exponent",
	"fill",
	"fill-opacity",
	"fill-rule",
	"filter",
	"filterunits",
	"flood-color",
	"flood-opacity",
	"font-family",
	"font-size",
	"font-size-adjust",
	"font-stretch",
	"font-style",
	"font-variant",
	"font-weight",
	"fx",
	"fy",
	"g1",
	"g2",
	"glyph-name",
	"glyphref",
	"gradientunits",
	"gradienttransform",
	"height",
	"href",
	"id",
	"image-rendering",
	"in",
	"in2",
	"intercept",
	"k",
	"k1",
	"k2",
	"k3",
	"k4",
	"kerning",
	"keypoints",
	"keysplines",
	"keytimes",
	"lang",
	"lengthadjust",
	"letter-spacing",
	"kernelmatrix",
	"kernelunitlength",
	"lighting-color",
	"local",
	"marker-end",
	"marker-mid",
	"marker-start",
	"markerheight",
	"markerunits",
	"markerwidth",
	"maskcontentunits",
	"maskunits",
	"max",
	"mask",
	"mask-type",
	"media",
	"method",
	"mode",
	"min",
	"name",
	"numoctaves",
	"offset",
	"operator",
	"opacity",
	"order",
	"orient",
	"orientation",
	"origin",
	"overflow",
	"paint-order",
	"path",
	"pathlength",
	"patterncontentunits",
	"patterntransform",
	"patternunits",
	"points",
	"preservealpha",
	"preserveaspectratio",
	"primitiveunits",
	"r",
	"rx",
	"ry",
	"radius",
	"refx",
	"refy",
	"repeatcount",
	"repeatdur",
	"restart",
	"result",
	"rotate",
	"scale",
	"seed",
	"shape-rendering",
	"slope",
	"specularconstant",
	"specularexponent",
	"spreadmethod",
	"startoffset",
	"stddeviation",
	"stitchtiles",
	"stop-color",
	"stop-opacity",
	"stroke-dasharray",
	"stroke-dashoffset",
	"stroke-linecap",
	"stroke-linejoin",
	"stroke-miterlimit",
	"stroke-opacity",
	"stroke",
	"stroke-width",
	"style",
	"surfacescale",
	"systemlanguage",
	"tabindex",
	"tablevalues",
	"targetx",
	"targety",
	"transform",
	"transform-origin",
	"text-anchor",
	"text-decoration",
	"text-rendering",
	"textlength",
	"type",
	"u1",
	"u2",
	"unicode",
	"values",
	"viewbox",
	"visibility",
	"version",
	"vert-adv-y",
	"vert-origin-x",
	"vert-origin-y",
	"width",
	"word-spacing",
	"wrap",
	"writing-mode",
	"xchannelselector",
	"ychannelselector",
	"x",
	"x1",
	"x2",
	"xmlns",
	"y",
	"y1",
	"y2",
	"z",
	"zoomandpan"
]);
var mathMl = freeze([
	"accent",
	"accentunder",
	"align",
	"bevelled",
	"close",
	"columnalign",
	"columnlines",
	"columnspacing",
	"columnspan",
	"denomalign",
	"depth",
	"dir",
	"display",
	"displaystyle",
	"encoding",
	"fence",
	"frame",
	"height",
	"href",
	"id",
	"largeop",
	"length",
	"linethickness",
	"lquote",
	"lspace",
	"mathbackground",
	"mathcolor",
	"mathsize",
	"mathvariant",
	"maxsize",
	"minsize",
	"movablelimits",
	"notation",
	"numalign",
	"open",
	"rowalign",
	"rowlines",
	"rowspacing",
	"rowspan",
	"rspace",
	"rquote",
	"scriptlevel",
	"scriptminsize",
	"scriptsizemultiplier",
	"selection",
	"separator",
	"separators",
	"stretchy",
	"subscriptshift",
	"supscriptshift",
	"symmetric",
	"voffset",
	"width",
	"xmlns"
]);
var xml = freeze([
	"xlink:href",
	"xml:id",
	"xlink:title",
	"xml:space",
	"xmlns:xlink"
]);
var MUSTACHE_EXPR = seal(/{{[\w\W]*|^[\w\W]*}}/g);
var ERB_EXPR = seal(/<%[\w\W]*|^[\w\W]*%>/g);
var TMPLIT_EXPR = seal(/\${[\w\W]*/g);
var DATA_ATTR = seal(/^data-[\-\w.\u00B7-\uFFFF]+$/);
var ARIA_ATTR = seal(/^aria-[\-\w]+$/);
var IS_ALLOWED_URI = seal(/^(?:(?:(?:f|ht)tps?|mailto|tel|callto|sms|cid|xmpp|matrix):|[^a-z]|[a-z+.\-]+(?:[^a-z+.\-:]|$))/i);
var IS_SCRIPT_OR_DATA = seal(/^(?:\w+script|data):/i);
var ATTR_WHITESPACE = seal(/[\u0000-\u0020\u00A0\u1680\u180E\u2000-\u2029\u205F\u3000]/g);
var DOCTYPE_NAME = seal(/^html$/i);
var CUSTOM_ELEMENT = seal(/^[a-z][.\w]*(-[.\w]+)+$/i);
var ELEMENT_MARKUP_PROBE = seal(/<[/\w!]/g);
var COMMENT_MARKUP_PROBE = seal(/<[/\w]/g);
var FALLBACK_TAG_CLOSE = seal(/<\/no(script|embed|frames)/i);
var SELF_CLOSING_TAG = seal(/\/>/i);
var NODE_TYPE = {
	element: 1,
	attribute: 2,
	text: 3,
	cdataSection: 4,
	entityReference: 5,
	entityNode: 6,
	processingInstruction: 7,
	comment: 8,
	document: 9,
	documentType: 10,
	documentFragment: 11,
	notation: 12
};
var getGlobal = function getGlobal() {
	return typeof window === "undefined" ? null : window;
};
/**
* Creates a no-op policy for internal use only.
* Don't export this function outside this module!
* @param trustedTypes The policy factory.
* @param purifyHostElement The Script element used to load DOMPurify (to determine policy name suffix).
* @return The policy created (or null, if Trusted Types
* are not supported or creating the policy failed).
*/
var _createTrustedTypesPolicy = function _createTrustedTypesPolicy(trustedTypes, purifyHostElement) {
	if (typeof trustedTypes !== "object" || typeof trustedTypes.createPolicy !== "function") return null;
	let suffix = null;
	const ATTR_NAME = "data-tt-policy-suffix";
	if (purifyHostElement && purifyHostElement.hasAttribute(ATTR_NAME)) suffix = purifyHostElement.getAttribute(ATTR_NAME);
	const policyName = "dompurify" + (suffix ? "#" + suffix : "");
	try {
		return trustedTypes.createPolicy(policyName, {
			createHTML(html) {
				return html;
			},
			createScriptURL(scriptUrl) {
				return scriptUrl;
			}
		});
	} catch (_) {
		console.warn("TrustedTypes policy " + policyName + " could not be created.");
		return null;
	}
};
var _createHooksMap = function _createHooksMap() {
	return {
		afterSanitizeAttributes: [],
		afterSanitizeElements: [],
		afterSanitizeShadowDOM: [],
		beforeSanitizeAttributes: [],
		beforeSanitizeElements: [],
		beforeSanitizeShadowDOM: [],
		uponSanitizeAttribute: [],
		uponSanitizeElement: [],
		uponSanitizeShadowNode: []
	};
};
/**
* Resolve a set-valued configuration option: a fresh set built from
* cfg[key] when it is an own array property (seeded with a clone of
* options.base when given, case-normalized via options.transform),
* the fallback set otherwise.
*
* @param cfg the cloned, prototype-free configuration object
* @param key the configuration property to read
* @param fallback the set to use when the option is absent or not an array
* @param options transform and optional base set to merge into
* @returns the resolved set
*/
var _resolveSetOption = function _resolveSetOption(cfg, key, fallback, options) {
	return objectHasOwnProperty(cfg, key) && arrayIsArray(cfg[key]) ? addToSet(options.base ? clone(options.base) : {}, cfg[key], options.transform) : fallback;
};
function createDOMPurify() {
	let window = arguments.length > 0 && arguments[0] !== void 0 ? arguments[0] : getGlobal();
	const DOMPurify = (root) => createDOMPurify(root);
	DOMPurify.version = "3.4.11";
	DOMPurify.removed = [];
	if (!window || !window.document || window.document.nodeType !== NODE_TYPE.document || !window.Element) {
		DOMPurify.isSupported = false;
		return DOMPurify;
	}
	let document = window.document;
	const originalDocument = document;
	const currentScript = originalDocument.currentScript;
	window.DocumentFragment;
	const HTMLTemplateElement = window.HTMLTemplateElement, Node = window.Node, Element = window.Element, NodeFilter = window.NodeFilter;
	window.NamedNodeMap === void 0 && (window.NamedNodeMap || window.MozNamedAttrMap);
	window.HTMLFormElement;
	const DOMParser = window.DOMParser, trustedTypes = window.trustedTypes;
	const ElementPrototype = Element.prototype;
	const cloneNode = lookupGetter(ElementPrototype, "cloneNode");
	const remove = lookupGetter(ElementPrototype, "remove");
	const getNextSibling = lookupGetter(ElementPrototype, "nextSibling");
	const getChildNodes = lookupGetter(ElementPrototype, "childNodes");
	const getParentNode = lookupGetter(ElementPrototype, "parentNode");
	const getShadowRoot = lookupGetter(ElementPrototype, "shadowRoot");
	const getAttributes = lookupGetter(ElementPrototype, "attributes");
	const getNodeType = Node && Node.prototype ? lookupGetter(Node.prototype, "nodeType") : null;
	const getNodeName = Node && Node.prototype ? lookupGetter(Node.prototype, "nodeName") : null;
	if (typeof HTMLTemplateElement === "function") {
		const template = document.createElement("template");
		if (template.content && template.content.ownerDocument) document = template.content.ownerDocument;
	}
	let trustedTypesPolicy;
	let emptyHTML = "";
	let defaultTrustedTypesPolicy;
	let defaultTrustedTypesPolicyResolved = false;
	let IN_TRUSTED_TYPES_POLICY = 0;
	const _assertNotInTrustedTypesPolicy = function _assertNotInTrustedTypesPolicy() {
		if (IN_TRUSTED_TYPES_POLICY > 0) throw typeErrorCreate("A configured TRUSTED_TYPES_POLICY callback (createHTML or createScriptURL) must not call DOMPurify.sanitize, as that causes infinite recursion. Do not pass a policy whose callbacks wrap DOMPurify as TRUSTED_TYPES_POLICY; see the \"DOMPurify and Trusted Types\" section of the README.");
	};
	const _createTrustedHTML = function _createTrustedHTML(html) {
		_assertNotInTrustedTypesPolicy();
		IN_TRUSTED_TYPES_POLICY++;
		try {
			return trustedTypesPolicy.createHTML(html);
		} finally {
			IN_TRUSTED_TYPES_POLICY--;
		}
	};
	const _createTrustedScriptURL = function _createTrustedScriptURL(scriptUrl) {
		_assertNotInTrustedTypesPolicy();
		IN_TRUSTED_TYPES_POLICY++;
		try {
			return trustedTypesPolicy.createScriptURL(scriptUrl);
		} finally {
			IN_TRUSTED_TYPES_POLICY--;
		}
	};
	const _getDefaultTrustedTypesPolicy = function _getDefaultTrustedTypesPolicy() {
		if (!defaultTrustedTypesPolicyResolved) {
			defaultTrustedTypesPolicy = _createTrustedTypesPolicy(trustedTypes, currentScript);
			defaultTrustedTypesPolicyResolved = true;
		}
		return defaultTrustedTypesPolicy;
	};
	const _document = document, implementation = _document.implementation, createNodeIterator = _document.createNodeIterator, createDocumentFragment = _document.createDocumentFragment, getElementsByTagName = _document.getElementsByTagName;
	const importNode = originalDocument.importNode;
	let hooks = _createHooksMap();
	/**
	* Expose whether this browser supports running the full DOMPurify.
	*/
	DOMPurify.isSupported = typeof entries === "function" && typeof getParentNode === "function" && implementation && implementation.createHTMLDocument !== void 0;
	const MUSTACHE_EXPR$1 = MUSTACHE_EXPR, ERB_EXPR$1 = ERB_EXPR, TMPLIT_EXPR$1 = TMPLIT_EXPR, DATA_ATTR$1 = DATA_ATTR, ARIA_ATTR$1 = ARIA_ATTR, IS_SCRIPT_OR_DATA$1 = IS_SCRIPT_OR_DATA, ATTR_WHITESPACE$1 = ATTR_WHITESPACE, CUSTOM_ELEMENT$1 = CUSTOM_ELEMENT;
	let IS_ALLOWED_URI$1 = IS_ALLOWED_URI;
	/**
	* We consider the elements and attributes below to be safe. Ideally
	* don't add any new ones but feel free to remove unwanted ones.
	*/
	let ALLOWED_TAGS = null;
	const DEFAULT_ALLOWED_TAGS = addToSet({}, [
		...html$1,
		...svg$1,
		...svgFilters,
		...mathMl$1,
		...text
	]);
	let ALLOWED_ATTR = null;
	const DEFAULT_ALLOWED_ATTR = addToSet({}, [
		...html,
		...svg,
		...mathMl,
		...xml
	]);
	let CUSTOM_ELEMENT_HANDLING = Object.seal(create(null, {
		tagNameCheck: {
			writable: true,
			configurable: false,
			enumerable: true,
			value: null
		},
		attributeNameCheck: {
			writable: true,
			configurable: false,
			enumerable: true,
			value: null
		},
		allowCustomizedBuiltInElements: {
			writable: true,
			configurable: false,
			enumerable: true,
			value: false
		}
	}));
	let FORBID_TAGS = null;
	let FORBID_ATTR = null;
	const EXTRA_ELEMENT_HANDLING = Object.seal(create(null, {
		tagCheck: {
			writable: true,
			configurable: false,
			enumerable: true,
			value: null
		},
		attributeCheck: {
			writable: true,
			configurable: false,
			enumerable: true,
			value: null
		}
	}));
	let ALLOW_ARIA_ATTR = true;
	let ALLOW_DATA_ATTR = true;
	let ALLOW_UNKNOWN_PROTOCOLS = false;
	let ALLOW_SELF_CLOSE_IN_ATTR = true;
	let SAFE_FOR_TEMPLATES = false;
	let SAFE_FOR_XML = true;
	let WHOLE_DOCUMENT = false;
	let SET_CONFIG = false;
	let SET_CONFIG_ALLOWED_TAGS = null;
	let SET_CONFIG_ALLOWED_ATTR = null;
	let FORCE_BODY = false;
	let RETURN_DOM = false;
	let RETURN_DOM_FRAGMENT = false;
	let RETURN_TRUSTED_TYPE = false;
	let SANITIZE_DOM = true;
	let SANITIZE_NAMED_PROPS = false;
	const SANITIZE_NAMED_PROPS_PREFIX = "user-content-";
	let KEEP_CONTENT = true;
	let IN_PLACE = false;
	let USE_PROFILES = {};
	let FORBID_CONTENTS = null;
	const DEFAULT_FORBID_CONTENTS = addToSet({}, [
		"annotation-xml",
		"audio",
		"colgroup",
		"desc",
		"foreignobject",
		"head",
		"iframe",
		"math",
		"mi",
		"mn",
		"mo",
		"ms",
		"mtext",
		"noembed",
		"noframes",
		"noscript",
		"plaintext",
		"script",
		"selectedcontent",
		"style",
		"svg",
		"template",
		"thead",
		"title",
		"video",
		"xmp"
	]);
	let DATA_URI_TAGS = null;
	const DEFAULT_DATA_URI_TAGS = addToSet({}, [
		"audio",
		"video",
		"img",
		"source",
		"image",
		"track"
	]);
	let URI_SAFE_ATTRIBUTES = null;
	const DEFAULT_URI_SAFE_ATTRIBUTES = addToSet({}, [
		"alt",
		"class",
		"for",
		"id",
		"label",
		"name",
		"pattern",
		"placeholder",
		"role",
		"summary",
		"title",
		"value",
		"style",
		"xmlns"
	]);
	const MATHML_NAMESPACE = "http://www.w3.org/1998/Math/MathML";
	const SVG_NAMESPACE = "http://www.w3.org/2000/svg";
	const HTML_NAMESPACE = "http://www.w3.org/1999/xhtml";
	let NAMESPACE = HTML_NAMESPACE;
	let IS_EMPTY_INPUT = false;
	let ALLOWED_NAMESPACES = null;
	const DEFAULT_ALLOWED_NAMESPACES = addToSet({}, [
		MATHML_NAMESPACE,
		SVG_NAMESPACE,
		HTML_NAMESPACE
	], stringToString);
	const DEFAULT_MATHML_TEXT_INTEGRATION_POINTS = freeze([
		"mi",
		"mo",
		"mn",
		"ms",
		"mtext"
	]);
	let MATHML_TEXT_INTEGRATION_POINTS = addToSet({}, DEFAULT_MATHML_TEXT_INTEGRATION_POINTS);
	const DEFAULT_HTML_INTEGRATION_POINTS = freeze(["annotation-xml"]);
	let HTML_INTEGRATION_POINTS = addToSet({}, DEFAULT_HTML_INTEGRATION_POINTS);
	const COMMON_SVG_AND_HTML_ELEMENTS = addToSet({}, [
		"title",
		"style",
		"font",
		"a",
		"script"
	]);
	let PARSER_MEDIA_TYPE = null;
	const SUPPORTED_PARSER_MEDIA_TYPES = ["application/xhtml+xml", "text/html"];
	const DEFAULT_PARSER_MEDIA_TYPE = "text/html";
	let transformCaseFunc = null;
	let CONFIG = null;
	const formElement = document.createElement("form");
	const isRegexOrFunction = function isRegexOrFunction(testValue) {
		return testValue instanceof RegExp || testValue instanceof Function;
	};
	/**
	* _parseConfig
	*
	* @param cfg optional config literal
	*/
	const _parseConfig = function _parseConfig() {
		let cfg = arguments.length > 0 && arguments[0] !== void 0 ? arguments[0] : {};
		if (CONFIG && CONFIG === cfg) return;
		if (!cfg || typeof cfg !== "object") cfg = {};
		cfg = clone(cfg);
		PARSER_MEDIA_TYPE = SUPPORTED_PARSER_MEDIA_TYPES.indexOf(cfg.PARSER_MEDIA_TYPE) === -1 ? DEFAULT_PARSER_MEDIA_TYPE : cfg.PARSER_MEDIA_TYPE;
		transformCaseFunc = PARSER_MEDIA_TYPE === "application/xhtml+xml" ? stringToString : stringToLowerCase;
		ALLOWED_TAGS = _resolveSetOption(cfg, "ALLOWED_TAGS", DEFAULT_ALLOWED_TAGS, { transform: transformCaseFunc });
		ALLOWED_ATTR = _resolveSetOption(cfg, "ALLOWED_ATTR", DEFAULT_ALLOWED_ATTR, { transform: transformCaseFunc });
		ALLOWED_NAMESPACES = _resolveSetOption(cfg, "ALLOWED_NAMESPACES", DEFAULT_ALLOWED_NAMESPACES, { transform: stringToString });
		URI_SAFE_ATTRIBUTES = _resolveSetOption(cfg, "ADD_URI_SAFE_ATTR", DEFAULT_URI_SAFE_ATTRIBUTES, {
			transform: transformCaseFunc,
			base: DEFAULT_URI_SAFE_ATTRIBUTES
		});
		DATA_URI_TAGS = _resolveSetOption(cfg, "ADD_DATA_URI_TAGS", DEFAULT_DATA_URI_TAGS, {
			transform: transformCaseFunc,
			base: DEFAULT_DATA_URI_TAGS
		});
		FORBID_CONTENTS = _resolveSetOption(cfg, "FORBID_CONTENTS", DEFAULT_FORBID_CONTENTS, { transform: transformCaseFunc });
		FORBID_TAGS = _resolveSetOption(cfg, "FORBID_TAGS", clone({}), { transform: transformCaseFunc });
		FORBID_ATTR = _resolveSetOption(cfg, "FORBID_ATTR", clone({}), { transform: transformCaseFunc });
		USE_PROFILES = objectHasOwnProperty(cfg, "USE_PROFILES") ? cfg.USE_PROFILES && typeof cfg.USE_PROFILES === "object" ? clone(cfg.USE_PROFILES) : cfg.USE_PROFILES : false;
		ALLOW_ARIA_ATTR = cfg.ALLOW_ARIA_ATTR !== false;
		ALLOW_DATA_ATTR = cfg.ALLOW_DATA_ATTR !== false;
		ALLOW_UNKNOWN_PROTOCOLS = cfg.ALLOW_UNKNOWN_PROTOCOLS || false;
		ALLOW_SELF_CLOSE_IN_ATTR = cfg.ALLOW_SELF_CLOSE_IN_ATTR !== false;
		SAFE_FOR_TEMPLATES = cfg.SAFE_FOR_TEMPLATES || false;
		SAFE_FOR_XML = cfg.SAFE_FOR_XML !== false;
		WHOLE_DOCUMENT = cfg.WHOLE_DOCUMENT || false;
		RETURN_DOM = cfg.RETURN_DOM || false;
		RETURN_DOM_FRAGMENT = cfg.RETURN_DOM_FRAGMENT || false;
		RETURN_TRUSTED_TYPE = cfg.RETURN_TRUSTED_TYPE || false;
		FORCE_BODY = cfg.FORCE_BODY || false;
		SANITIZE_DOM = cfg.SANITIZE_DOM !== false;
		SANITIZE_NAMED_PROPS = cfg.SANITIZE_NAMED_PROPS || false;
		KEEP_CONTENT = cfg.KEEP_CONTENT !== false;
		IN_PLACE = cfg.IN_PLACE || false;
		IS_ALLOWED_URI$1 = isRegex(cfg.ALLOWED_URI_REGEXP) ? cfg.ALLOWED_URI_REGEXP : IS_ALLOWED_URI;
		NAMESPACE = typeof cfg.NAMESPACE === "string" ? cfg.NAMESPACE : HTML_NAMESPACE;
		MATHML_TEXT_INTEGRATION_POINTS = objectHasOwnProperty(cfg, "MATHML_TEXT_INTEGRATION_POINTS") && cfg.MATHML_TEXT_INTEGRATION_POINTS && typeof cfg.MATHML_TEXT_INTEGRATION_POINTS === "object" ? clone(cfg.MATHML_TEXT_INTEGRATION_POINTS) : addToSet({}, DEFAULT_MATHML_TEXT_INTEGRATION_POINTS);
		HTML_INTEGRATION_POINTS = objectHasOwnProperty(cfg, "HTML_INTEGRATION_POINTS") && cfg.HTML_INTEGRATION_POINTS && typeof cfg.HTML_INTEGRATION_POINTS === "object" ? clone(cfg.HTML_INTEGRATION_POINTS) : addToSet({}, DEFAULT_HTML_INTEGRATION_POINTS);
		const customElementHandling = objectHasOwnProperty(cfg, "CUSTOM_ELEMENT_HANDLING") && cfg.CUSTOM_ELEMENT_HANDLING && typeof cfg.CUSTOM_ELEMENT_HANDLING === "object" ? clone(cfg.CUSTOM_ELEMENT_HANDLING) : create(null);
		CUSTOM_ELEMENT_HANDLING = create(null);
		if (objectHasOwnProperty(customElementHandling, "tagNameCheck") && isRegexOrFunction(customElementHandling.tagNameCheck)) CUSTOM_ELEMENT_HANDLING.tagNameCheck = customElementHandling.tagNameCheck;
		if (objectHasOwnProperty(customElementHandling, "attributeNameCheck") && isRegexOrFunction(customElementHandling.attributeNameCheck)) CUSTOM_ELEMENT_HANDLING.attributeNameCheck = customElementHandling.attributeNameCheck;
		if (objectHasOwnProperty(customElementHandling, "allowCustomizedBuiltInElements") && typeof customElementHandling.allowCustomizedBuiltInElements === "boolean") CUSTOM_ELEMENT_HANDLING.allowCustomizedBuiltInElements = customElementHandling.allowCustomizedBuiltInElements;
		seal(CUSTOM_ELEMENT_HANDLING);
		if (SAFE_FOR_TEMPLATES) ALLOW_DATA_ATTR = false;
		if (RETURN_DOM_FRAGMENT) RETURN_DOM = true;
		if (USE_PROFILES) {
			ALLOWED_TAGS = addToSet({}, text);
			ALLOWED_ATTR = create(null);
			if (USE_PROFILES.html === true) {
				addToSet(ALLOWED_TAGS, html$1);
				addToSet(ALLOWED_ATTR, html);
			}
			if (USE_PROFILES.svg === true) {
				addToSet(ALLOWED_TAGS, svg$1);
				addToSet(ALLOWED_ATTR, svg);
				addToSet(ALLOWED_ATTR, xml);
			}
			if (USE_PROFILES.svgFilters === true) {
				addToSet(ALLOWED_TAGS, svgFilters);
				addToSet(ALLOWED_ATTR, svg);
				addToSet(ALLOWED_ATTR, xml);
			}
			if (USE_PROFILES.mathMl === true) {
				addToSet(ALLOWED_TAGS, mathMl$1);
				addToSet(ALLOWED_ATTR, mathMl);
				addToSet(ALLOWED_ATTR, xml);
			}
		}
		EXTRA_ELEMENT_HANDLING.tagCheck = null;
		EXTRA_ELEMENT_HANDLING.attributeCheck = null;
		if (objectHasOwnProperty(cfg, "ADD_TAGS")) {
			if (typeof cfg.ADD_TAGS === "function") EXTRA_ELEMENT_HANDLING.tagCheck = cfg.ADD_TAGS;
			else if (arrayIsArray(cfg.ADD_TAGS)) {
				if (ALLOWED_TAGS === DEFAULT_ALLOWED_TAGS) ALLOWED_TAGS = clone(ALLOWED_TAGS);
				addToSet(ALLOWED_TAGS, cfg.ADD_TAGS, transformCaseFunc);
			}
		}
		if (objectHasOwnProperty(cfg, "ADD_ATTR")) {
			if (typeof cfg.ADD_ATTR === "function") EXTRA_ELEMENT_HANDLING.attributeCheck = cfg.ADD_ATTR;
			else if (arrayIsArray(cfg.ADD_ATTR)) {
				if (ALLOWED_ATTR === DEFAULT_ALLOWED_ATTR) ALLOWED_ATTR = clone(ALLOWED_ATTR);
				addToSet(ALLOWED_ATTR, cfg.ADD_ATTR, transformCaseFunc);
			}
		}
		if (objectHasOwnProperty(cfg, "ADD_URI_SAFE_ATTR") && arrayIsArray(cfg.ADD_URI_SAFE_ATTR)) addToSet(URI_SAFE_ATTRIBUTES, cfg.ADD_URI_SAFE_ATTR, transformCaseFunc);
		if (objectHasOwnProperty(cfg, "FORBID_CONTENTS") && arrayIsArray(cfg.FORBID_CONTENTS)) {
			if (FORBID_CONTENTS === DEFAULT_FORBID_CONTENTS) FORBID_CONTENTS = clone(FORBID_CONTENTS);
			addToSet(FORBID_CONTENTS, cfg.FORBID_CONTENTS, transformCaseFunc);
		}
		if (objectHasOwnProperty(cfg, "ADD_FORBID_CONTENTS") && arrayIsArray(cfg.ADD_FORBID_CONTENTS)) {
			if (FORBID_CONTENTS === DEFAULT_FORBID_CONTENTS) FORBID_CONTENTS = clone(FORBID_CONTENTS);
			addToSet(FORBID_CONTENTS, cfg.ADD_FORBID_CONTENTS, transformCaseFunc);
		}
		if (KEEP_CONTENT) ALLOWED_TAGS["#text"] = true;
		if (WHOLE_DOCUMENT) addToSet(ALLOWED_TAGS, [
			"html",
			"head",
			"body"
		]);
		if (ALLOWED_TAGS.table) {
			addToSet(ALLOWED_TAGS, ["tbody"]);
			delete FORBID_TAGS.tbody;
		}
		if (cfg.TRUSTED_TYPES_POLICY) {
			if (typeof cfg.TRUSTED_TYPES_POLICY.createHTML !== "function") throw typeErrorCreate("TRUSTED_TYPES_POLICY configuration option must provide a \"createHTML\" hook.");
			if (typeof cfg.TRUSTED_TYPES_POLICY.createScriptURL !== "function") throw typeErrorCreate("TRUSTED_TYPES_POLICY configuration option must provide a \"createScriptURL\" hook.");
			const previousTrustedTypesPolicy = trustedTypesPolicy;
			trustedTypesPolicy = cfg.TRUSTED_TYPES_POLICY;
			try {
				emptyHTML = _createTrustedHTML("");
			} catch (error) {
				trustedTypesPolicy = previousTrustedTypesPolicy;
				throw error;
			}
		} else if (cfg.TRUSTED_TYPES_POLICY === null) {
			trustedTypesPolicy = void 0;
			emptyHTML = "";
		} else {
			if (trustedTypesPolicy === void 0) trustedTypesPolicy = _getDefaultTrustedTypesPolicy();
			if (trustedTypesPolicy && typeof emptyHTML === "string") emptyHTML = _createTrustedHTML("");
		}
		if (freeze) freeze(cfg);
		CONFIG = cfg;
	};
	const ALL_SVG_TAGS = addToSet({}, [
		...svg$1,
		...svgFilters,
		...svgDisallowed
	]);
	const ALL_MATHML_TAGS = addToSet({}, [...mathMl$1, ...mathMlDisallowed]);
	/**
	* Namespace rules for an element in the SVG namespace.
	*
	* @param tagName the element's lowercase tag name
	* @param parent the (possibly simulated) parent node
	* @param parentTagName the parent's lowercase tag name
	* @returns true if a spec-compliant parser could produce this element
	*/
	const _checkSvgNamespace = function _checkSvgNamespace(tagName, parent, parentTagName) {
		if (parent.namespaceURI === HTML_NAMESPACE) return tagName === "svg";
		if (parent.namespaceURI === MATHML_NAMESPACE) return tagName === "svg" && (parentTagName === "annotation-xml" || MATHML_TEXT_INTEGRATION_POINTS[parentTagName]);
		return Boolean(ALL_SVG_TAGS[tagName]);
	};
	/**
	* Namespace rules for an element in the MathML namespace.
	*
	* @param tagName the element's lowercase tag name
	* @param parent the (possibly simulated) parent node
	* @param parentTagName the parent's lowercase tag name
	* @returns true if a spec-compliant parser could produce this element
	*/
	const _checkMathMlNamespace = function _checkMathMlNamespace(tagName, parent, parentTagName) {
		if (parent.namespaceURI === HTML_NAMESPACE) return tagName === "math";
		if (parent.namespaceURI === SVG_NAMESPACE) return tagName === "math" && HTML_INTEGRATION_POINTS[parentTagName];
		return Boolean(ALL_MATHML_TAGS[tagName]);
	};
	/**
	* Namespace rules for an element in the HTML namespace.
	*
	* @param tagName the element's lowercase tag name
	* @param parent the (possibly simulated) parent node
	* @param parentTagName the parent's lowercase tag name
	* @returns true if a spec-compliant parser could produce this element
	*/
	const _checkHtmlNamespace = function _checkHtmlNamespace(tagName, parent, parentTagName) {
		if (parent.namespaceURI === SVG_NAMESPACE && !HTML_INTEGRATION_POINTS[parentTagName]) return false;
		if (parent.namespaceURI === MATHML_NAMESPACE && !MATHML_TEXT_INTEGRATION_POINTS[parentTagName]) return false;
		return !ALL_MATHML_TAGS[tagName] && (COMMON_SVG_AND_HTML_ELEMENTS[tagName] || !ALL_SVG_TAGS[tagName]);
	};
	/**
	* @param element a DOM element whose namespace is being checked
	* @returns Return false if the element has a
	*  namespace that a spec-compliant parser would never
	*  return. Return true otherwise.
	*/
	const _checkValidNamespace = function _checkValidNamespace(element) {
		let parent = getParentNode(element);
		if (!parent || !parent.tagName) parent = {
			namespaceURI: NAMESPACE,
			tagName: "template"
		};
		const tagName = stringToLowerCase(element.tagName);
		const parentTagName = stringToLowerCase(parent.tagName);
		if (!ALLOWED_NAMESPACES[element.namespaceURI]) return false;
		if (element.namespaceURI === SVG_NAMESPACE) return _checkSvgNamespace(tagName, parent, parentTagName);
		if (element.namespaceURI === MATHML_NAMESPACE) return _checkMathMlNamespace(tagName, parent, parentTagName);
		if (element.namespaceURI === HTML_NAMESPACE) return _checkHtmlNamespace(tagName, parent, parentTagName);
		if (PARSER_MEDIA_TYPE === "application/xhtml+xml" && ALLOWED_NAMESPACES[element.namespaceURI]) return true;
		return false;
	};
	/**
	* _forceRemove
	*
	* @param node a DOM node
	*/
	const _forceRemove = function _forceRemove(node) {
		arrayPush(DOMPurify.removed, { element: node });
		try {
			getParentNode(node).removeChild(node);
		} catch (_) {
			remove(node);
			if (!getParentNode(node)) throw typeErrorCreate("a node selected for removal could not be detached from its tree and cannot be safely returned; refusing to sanitize in place");
		}
	};
	/**
	* _neutralizeRoot
	*
	* Fail-closed teardown of an in-place root after the sanitize walk aborts
	* (campaign-3 F2). An internal throw mid-walk — e.g. a page-registered
	* custom element's reaction detaches a node so `_forceRemove`'s deliberate
	* parentless guard throws, or any other re-entrant engine mutation — would
	* otherwise leave the caller's *live* tree half-sanitized, with everything
	* after the abort point still carrying its handlers. There is no safe way
	* to resume the walk (the tree mutated under us), so we strip the root bare:
	* remove every child and every attribute, then let the caller's catch see
	* the original error. Clobber-safe (cached `remove`/`childNodes`/`attributes`
	* getters; the root was already clobber-pre-flighted at the IN_PLACE entry).
	*
	* @param root the in-place root to empty
	*/
	const _neutralizeRoot = function _neutralizeRoot(root) {
		const childNodes = getChildNodes(root);
		if (childNodes) {
			const snapshot = [];
			arrayForEach(childNodes, (child) => {
				arrayPush(snapshot, child);
			});
			arrayForEach(snapshot, (child) => {
				try {
					remove(child);
				} catch (_) {}
			});
		}
		const attributes = getAttributes(root);
		if (attributes) for (let i = attributes.length - 1; i >= 0; --i) {
			const attribute = attributes[i];
			const name = attribute && attribute.name;
			if (typeof name === "string") try {
				root.removeAttribute(name);
			} catch (_) {}
		}
	};
	/**
	* _removeAttribute
	*
	* @param name an Attribute name
	* @param element a DOM node
	*/
	const _removeAttribute = function _removeAttribute(name, element) {
		try {
			arrayPush(DOMPurify.removed, {
				attribute: element.getAttributeNode(name),
				from: element
			});
		} catch (_) {
			arrayPush(DOMPurify.removed, {
				attribute: null,
				from: element
			});
		}
		element.removeAttribute(name);
		if (name === "is") if (RETURN_DOM || RETURN_DOM_FRAGMENT) try {
			_forceRemove(element);
		} catch (_) {}
		else try {
			element.setAttribute(name, "");
		} catch (_) {}
	};
	/**
	* _stripDisallowedAttributes
	*
	* Removes every attribute the active configuration does not allow from a
	* single element, using the same allowlist as the main attribute pass (so
	* `on*` handlers go, but no `/^on/` blocklist is introduced). Used only to
	* neutralise nodes that are being discarded from an in-place tree.
	*
	* @param element the element to strip
	*/
	const _stripDisallowedAttributes = function _stripDisallowedAttributes(element) {
		const attributes = getAttributes(element);
		if (!attributes) return;
		for (let i = attributes.length - 1; i >= 0; --i) {
			const attribute = attributes[i];
			const name = attribute && attribute.name;
			if (typeof name !== "string" || ALLOWED_ATTR[transformCaseFunc(name)]) continue;
			try {
				element.removeAttribute(name);
			} catch (_) {}
		}
	};
	/**
	* _neutralizeSubtree
	*
	* Completes the audit-5 F1 fix across every removal path. The KEEP_CONTENT
	* move-hoist neutralises only disallowed-tag removals; clobber, mXSS-canary,
	* namespace, comment, processing-instruction and KEEP_CONTENT:false removals
	* all drop their subtree wholesale via `_forceRemove`. On the IN_PLACE path
	* those dropped nodes are detached from the caller's LIVE tree but a
	* handler-bearing original among them (an `<img onerror>`/`<video>` that was
	* loading) keeps its queued resource event, which fires in page scope after
	* sanitize returns. This walks a removed subtree and strips every attribute
	* the active configuration does not allow — so `on*` handlers are cancelled
	* through the SAME allowlist that governs kept nodes, not a separate `/^on/`
	* blocklist. Run synchronously before sanitize returns, i.e. before any
	* queued event can fire. Hook-free by design: these nodes leave the output,
	* so firing attribute hooks for them would be surprising. Clobber-safe reads;
	* a doomed clobbered node may shadow `removeAttribute` (its own attributes are
	* irrelevant — it is discarded — while its non-clobbered descendants, e.g.
	* the `<img>`, are reached and scrubbed).
	*
	* @param root the root of a removed subtree to neutralise
	*/
	const _neutralizeSubtree = function _neutralizeSubtree(root) {
		const stack = [root];
		while (stack.length > 0) {
			const node = stack.pop();
			if ((getNodeType ? getNodeType(node) : node.nodeType) === NODE_TYPE.element) _stripDisallowedAttributes(node);
			const childNodes = getChildNodes(node);
			if (childNodes) for (let i = childNodes.length - 1; i >= 0; --i) stack.push(childNodes[i]);
		}
	};
	/**
	* _initDocument
	*
	* @param dirty - a string of dirty markup
	* @return a DOM, filled with the dirty markup
	*/
	const _initDocument = function _initDocument(dirty) {
		let doc = null;
		let leadingWhitespace = null;
		if (FORCE_BODY) dirty = "<remove></remove>" + dirty;
		else {
			const matches = stringMatch(dirty, /^[\r\n\t ]+/);
			leadingWhitespace = matches && matches[0];
		}
		if (PARSER_MEDIA_TYPE === "application/xhtml+xml" && NAMESPACE === HTML_NAMESPACE) dirty = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head></head><body>" + dirty + "</body></html>";
		const dirtyPayload = trustedTypesPolicy ? _createTrustedHTML(dirty) : dirty;
		if (NAMESPACE === HTML_NAMESPACE) try {
			doc = new DOMParser().parseFromString(dirtyPayload, PARSER_MEDIA_TYPE);
		} catch (_) {}
		if (!doc || !doc.documentElement) {
			doc = implementation.createDocument(NAMESPACE, "template", null);
			try {
				doc.documentElement.innerHTML = IS_EMPTY_INPUT ? emptyHTML : dirtyPayload;
			} catch (_) {}
		}
		const body = doc.body || doc.documentElement;
		if (dirty && leadingWhitespace) body.insertBefore(document.createTextNode(leadingWhitespace), body.childNodes[0] || null);
		if (NAMESPACE === HTML_NAMESPACE) return getElementsByTagName.call(doc, WHOLE_DOCUMENT ? "html" : "body")[0];
		return WHOLE_DOCUMENT ? doc.documentElement : body;
	};
	/**
	* Creates a NodeIterator object that you can use to traverse filtered lists of nodes or elements in a document.
	*
	* @param root The root element or node to start traversing on.
	* @return The created NodeIterator
	*/
	const _createNodeIterator = function _createNodeIterator(root) {
		return createNodeIterator.call(root.ownerDocument || root, root, NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_COMMENT | NodeFilter.SHOW_TEXT | NodeFilter.SHOW_PROCESSING_INSTRUCTION | NodeFilter.SHOW_CDATA_SECTION, null);
	};
	/**
	* Replace template expression syntax (mustache, ERB, template
	* literal) with a space; shared by all SAFE_FOR_TEMPLATES scrub
	* sites. Order matters: mustache, then ERB, then template literal.
	*
	* @param value the string to scrub
	* @returns the scrubbed string
	*/
	const _stripTemplateExpressions = function _stripTemplateExpressions(value) {
		value = stringReplace(value, MUSTACHE_EXPR$1, " ");
		value = stringReplace(value, ERB_EXPR$1, " ");
		value = stringReplace(value, TMPLIT_EXPR$1, " ");
		return value;
	};
	/**
	* Strip template-engine expressions ({{...}}, ${...}, <%...%>) from the
	* character data of an element subtree. Used as the final safety net for
	* SAFE_FOR_TEMPLATES on every DOM-returning code path so that expressions
	* which only form after text-node normalization (e.g. fragments split across
	* stripped elements) cannot survive into a template-evaluating framework.
	*
	* Walks text/comment/CDATA/processing-instruction nodes and mutates `.data`
	* in place rather than round-tripping through innerHTML. This preserves
	* descendant node references (important for IN_PLACE callers), avoids a
	* serialize/reparse cycle, and reads literal character data — which means
	* `<%...%>` in text content matches the ERB regex against its real bytes
	* instead of the HTML-entity-escaped form innerHTML would produce.
	*
	* Attribute values are not visited here; SAFE_FOR_TEMPLATES handling for
	* attributes is performed during the per-node `_sanitizeAttributes` pass.
	*
	* @param node The root element whose character data should be scrubbed.
	*/
	const _scrubTemplateExpressions2 = function _scrubTemplateExpressions(node) {
		var _node$querySelectorAl;
		node.normalize();
		const walker = createNodeIterator.call(node.ownerDocument || node, node, NodeFilter.SHOW_TEXT | NodeFilter.SHOW_COMMENT | NodeFilter.SHOW_CDATA_SECTION | NodeFilter.SHOW_PROCESSING_INSTRUCTION, null);
		let currentNode = walker.nextNode();
		while (currentNode) {
			currentNode.data = _stripTemplateExpressions(currentNode.data);
			currentNode = walker.nextNode();
		}
		const templates = (_node$querySelectorAl = node.querySelectorAll) === null || _node$querySelectorAl === void 0 ? void 0 : _node$querySelectorAl.call(node, "template");
		if (templates) arrayForEach(templates, (tmpl) => {
			if (_isDocumentFragment(tmpl.content)) _scrubTemplateExpressions2(tmpl.content);
		});
	};
	/**
	* _isClobbered
	*
	* Detect DOM-clobbering on HTMLFormElement nodes. Form is the only HTML
	* interface with [LegacyOverrideBuiltIns]; a descendant element with a
	* `name` attribute matching a prototype property shadows that property
	* on direct reads. We use this check at the IN_PLACE entry-point and
	* during attribute sanitization to refuse clobbered forms.
	*
	* @param element element to check for clobbering attacks
	* @return true if clobbered, false if safe
	*/
	const _isClobbered = function _isClobbered(element) {
		const realTagName = getNodeName ? getNodeName(element) : null;
		if (typeof realTagName !== "string") return false;
		if (transformCaseFunc(realTagName) !== "form") return false;
		return typeof element.nodeName !== "string" || typeof element.textContent !== "string" || typeof element.removeChild !== "function" || element.attributes !== getAttributes(element) || typeof element.removeAttribute !== "function" || typeof element.setAttribute !== "function" || typeof element.namespaceURI !== "string" || typeof element.insertBefore !== "function" || typeof element.hasChildNodes !== "function" || element.nodeType !== getNodeType(element) || element.childNodes !== getChildNodes(element);
	};
	/**
	* Checks whether the given value is a DocumentFragment from any realm.
	*
	* The realm-independent replacement reads `nodeType` through the cached
	* Node.prototype getter and compares to the DOCUMENT_FRAGMENT_NODE
	* constant (11). nodeType is a numeric value resolved from the node's
	* internal slot, identical across realms for the same kind of node.
	*
	* @param value object to check
	* @return true if value is a DocumentFragment-shaped node from any realm
	*/
	const _isDocumentFragment = function _isDocumentFragment(value) {
		if (!getNodeType || typeof value !== "object" || value === null) return false;
		try {
			return getNodeType(value) === NODE_TYPE.documentFragment;
		} catch (_) {
			return false;
		}
	};
	/**
	* Checks whether the given object is a DOM node, including nodes that
	* originate from a different window/realm (e.g. an iframe's
	* contentDocument). The previous `value instanceof Node` check was
	* realm-bound: nodes from a different window failed it, causing
	* sanitize() to silently stringify them and reset IN_PLACE to false,
	* returning the original node unsanitized. See GHSA-4w3q-35jp-p934.
	*
	* @param value object to check whether it's a DOM node
	* @return true if value is a DOM node from any realm
	*/
	const _isNode = function _isNode(value) {
		if (!getNodeType || typeof value !== "object" || value === null) return false;
		try {
			return typeof getNodeType(value) === "number";
		} catch (_) {
			return false;
		}
	};
	function _executeHooks(hooks, currentNode, data) {
		if (hooks.length === 0) return;
		arrayForEach(hooks, (hook) => {
			hook.call(DOMPurify, currentNode, data, CONFIG);
		});
	}
	/**
	* Structural-threat checks that condemn a node regardless of the
	* allowlists: mXSS via namespace confusion, risky CSS construction,
	* processing instructions, markup-bearing comments. Pure predicate;
	* the caller removes. Check order is load-bearing.
	*
	* @param currentNode the node to inspect
	* @param tagName the node's transformCaseFunc'd tag name
	* @return true if the node must be removed
	*/
	const _isUnsafeNode = function _isUnsafeNode(currentNode, tagName) {
		if (SAFE_FOR_XML && currentNode.hasChildNodes() && !_isNode(currentNode.firstElementChild) && regExpTest(ELEMENT_MARKUP_PROBE, currentNode.textContent) && regExpTest(ELEMENT_MARKUP_PROBE, currentNode.innerHTML)) return true;
		if (SAFE_FOR_XML && currentNode.namespaceURI === HTML_NAMESPACE && tagName === "style" && _isNode(currentNode.firstElementChild)) return true;
		if (currentNode.nodeType === NODE_TYPE.processingInstruction) return true;
		if (SAFE_FOR_XML && currentNode.nodeType === NODE_TYPE.comment && regExpTest(COMMENT_MARKUP_PROBE, currentNode.data)) return true;
		return false;
	};
	/**
	* Handle a node whose tag is forbidden or not allowlisted: keep
	* allowed custom elements (false return exits _sanitizeElements
	* early - namespace/fallback checks and the afterSanitizeElements
	* hook are intentionally skipped for kept custom elements), else
	* hoist content per KEEP_CONTENT and remove.
	*
	* @param currentNode the disallowed node
	* @param tagName the node's transformCaseFunc'd tag name
	* @return true if the node was removed, false if kept
	*/
	const _sanitizeDisallowedNode = function _sanitizeDisallowedNode(currentNode, tagName) {
		if (!FORBID_TAGS[tagName] && _isBasicCustomElement(tagName)) {
			if (CUSTOM_ELEMENT_HANDLING.tagNameCheck instanceof RegExp && regExpTest(CUSTOM_ELEMENT_HANDLING.tagNameCheck, tagName)) return false;
			if (CUSTOM_ELEMENT_HANDLING.tagNameCheck instanceof Function && CUSTOM_ELEMENT_HANDLING.tagNameCheck(tagName)) return false;
		}
		if (KEEP_CONTENT && !FORBID_CONTENTS[tagName]) {
			const parentNode = getParentNode(currentNode);
			const childNodes = getChildNodes(currentNode);
			if (childNodes && parentNode) {
				const childCount = childNodes.length;
				for (let i = childCount - 1; i >= 0; --i) {
					const hoisted = IN_PLACE ? childNodes[i] : cloneNode(childNodes[i], true);
					parentNode.insertBefore(hoisted, getNextSibling(currentNode));
				}
			}
		}
		_forceRemove(currentNode);
		return true;
	};
	/**
	* _sanitizeElements
	*
	* @protect nodeName
	* @protect textContent
	* @protect removeChild
	* @param currentNode to check for permission to exist
	* @return true if node was killed, false if left alive
	*/
	const _sanitizeElements = function _sanitizeElements(currentNode) {
		_executeHooks(hooks.beforeSanitizeElements, currentNode, null);
		if (_isClobbered(currentNode)) {
			_forceRemove(currentNode);
			return true;
		}
		const tagName = transformCaseFunc(getNodeName ? getNodeName(currentNode) : currentNode.nodeName);
		_executeHooks(hooks.uponSanitizeElement, currentNode, {
			tagName,
			allowedTags: ALLOWED_TAGS
		});
		if (_isUnsafeNode(currentNode, tagName)) {
			_forceRemove(currentNode);
			return true;
		}
		if (FORBID_TAGS[tagName] || !(EXTRA_ELEMENT_HANDLING.tagCheck instanceof Function && EXTRA_ELEMENT_HANDLING.tagCheck(tagName)) && !ALLOWED_TAGS[tagName]) return _sanitizeDisallowedNode(currentNode, tagName);
		if ((getNodeType ? getNodeType(currentNode) : currentNode.nodeType) === NODE_TYPE.element && !_checkValidNamespace(currentNode)) {
			_forceRemove(currentNode);
			return true;
		}
		if ((tagName === "noscript" || tagName === "noembed" || tagName === "noframes") && regExpTest(FALLBACK_TAG_CLOSE, currentNode.innerHTML)) {
			_forceRemove(currentNode);
			return true;
		}
		if (SAFE_FOR_TEMPLATES && currentNode.nodeType === NODE_TYPE.text) {
			const content = _stripTemplateExpressions(currentNode.textContent);
			if (currentNode.textContent !== content) {
				arrayPush(DOMPurify.removed, { element: currentNode.cloneNode() });
				currentNode.textContent = content;
			}
		}
		_executeHooks(hooks.afterSanitizeElements, currentNode, null);
		return false;
	};
	/**
	* _isValidAttribute
	*
	* @param lcTag Lowercase tag name of containing element.
	* @param lcName Lowercase attribute name.
	* @param value Attribute value.
	* @return Returns true if `value` is valid, otherwise false.
	*/
	const _isValidAttribute = function _isValidAttribute(lcTag, lcName, value) {
		if (FORBID_ATTR[lcName]) return false;
		if (SANITIZE_DOM && (lcName === "id" || lcName === "name") && (value in document || value in formElement)) return false;
		const nameIsPermitted = ALLOWED_ATTR[lcName] || EXTRA_ELEMENT_HANDLING.attributeCheck instanceof Function && EXTRA_ELEMENT_HANDLING.attributeCheck(lcName, lcTag);
		if (ALLOW_DATA_ATTR && regExpTest(DATA_ATTR$1, lcName));
		else if (ALLOW_ARIA_ATTR && regExpTest(ARIA_ATTR$1, lcName));
		else if (!nameIsPermitted) if (_isBasicCustomElement(lcTag) && (CUSTOM_ELEMENT_HANDLING.tagNameCheck instanceof RegExp && regExpTest(CUSTOM_ELEMENT_HANDLING.tagNameCheck, lcTag) || CUSTOM_ELEMENT_HANDLING.tagNameCheck instanceof Function && CUSTOM_ELEMENT_HANDLING.tagNameCheck(lcTag)) && (CUSTOM_ELEMENT_HANDLING.attributeNameCheck instanceof RegExp && regExpTest(CUSTOM_ELEMENT_HANDLING.attributeNameCheck, lcName) || CUSTOM_ELEMENT_HANDLING.attributeNameCheck instanceof Function && CUSTOM_ELEMENT_HANDLING.attributeNameCheck(lcName, lcTag)) || lcName === "is" && CUSTOM_ELEMENT_HANDLING.allowCustomizedBuiltInElements && (CUSTOM_ELEMENT_HANDLING.tagNameCheck instanceof RegExp && regExpTest(CUSTOM_ELEMENT_HANDLING.tagNameCheck, value) || CUSTOM_ELEMENT_HANDLING.tagNameCheck instanceof Function && CUSTOM_ELEMENT_HANDLING.tagNameCheck(value)));
		else return false;
		else if (URI_SAFE_ATTRIBUTES[lcName]);
		else if (regExpTest(IS_ALLOWED_URI$1, stringReplace(value, ATTR_WHITESPACE$1, "")));
		else if ((lcName === "src" || lcName === "xlink:href" || lcName === "href") && lcTag !== "script" && stringIndexOf(value, "data:") === 0 && DATA_URI_TAGS[lcTag]);
		else if (ALLOW_UNKNOWN_PROTOCOLS && !regExpTest(IS_SCRIPT_OR_DATA$1, stringReplace(value, ATTR_WHITESPACE$1, "")));
		else if (value) return false;
		return true;
	};
	const RESERVED_CUSTOM_ELEMENT_NAMES = addToSet({}, [
		"annotation-xml",
		"color-profile",
		"font-face",
		"font-face-format",
		"font-face-name",
		"font-face-src",
		"font-face-uri",
		"missing-glyph"
	]);
	/**
	* _isBasicCustomElement
	* checks if at least one dash is included in tagName, and it's not the first char
	* for more sophisticated checking see https://github.com/sindresorhus/validate-element-name
	*
	* @param tagName name of the tag of the node to sanitize
	* @returns Returns true if the tag name meets the basic criteria for a custom element, otherwise false.
	*/
	const _isBasicCustomElement = function _isBasicCustomElement(tagName) {
		return !RESERVED_CUSTOM_ELEMENT_NAMES[stringToLowerCase(tagName)] && regExpTest(CUSTOM_ELEMENT$1, tagName);
	};
	/**
	* Wrap an attribute value in the matching Trusted Types object when
	* the active policy requires it. Namespaced attributes pass through
	* unchanged (no TT support yet, see
	* https://bugs.chromium.org/p/chromium/issues/detail?id=1305293).
	*
	* @param lcTag lowercase tag name of the containing element
	* @param lcName lowercase attribute name
	* @param namespaceURI the attribute's namespace, if any
	* @param value the attribute value to wrap
	* @return the value, wrapped when Trusted Types demand it
	*/
	const _applyTrustedTypesToAttribute = function _applyTrustedTypesToAttribute(lcTag, lcName, namespaceURI, value) {
		if (trustedTypesPolicy && typeof trustedTypes === "object" && typeof trustedTypes.getAttributeType === "function" && !namespaceURI) switch (trustedTypes.getAttributeType(lcTag, lcName)) {
			case "TrustedHTML": return _createTrustedHTML(value);
			case "TrustedScriptURL": return _createTrustedScriptURL(value);
		}
		return value;
	};
	/**
	* Write a modified attribute value back onto the element. On
	* success, re-probe for clobbering introduced by the new value and
	* remove the element when found; otherwise pop the removal entry
	* recorded by the earlier _removeAttribute (long-standing pairing
	* with the SANITIZE_NAMED_PROPS path - do not "fix" casually). On
	* failure, remove the attribute instead.
	*
	* @param currentNode the element carrying the attribute
	* @param name the attribute name as present on the element
	* @param namespaceURI the attribute's namespace, if any
	* @param value the new attribute value
	*/
	const _setAttributeValue = function _setAttributeValue(currentNode, name, namespaceURI, value) {
		try {
			if (namespaceURI) currentNode.setAttributeNS(namespaceURI, name, value);
			else currentNode.setAttribute(name, value);
			if (_isClobbered(currentNode)) _forceRemove(currentNode);
			else arrayPop(DOMPurify.removed);
		} catch (_) {
			_removeAttribute(name, currentNode);
		}
	};
	/**
	* _sanitizeAttributes
	*
	* @protect attributes
	* @protect nodeName
	* @protect removeAttribute
	* @protect setAttribute
	*
	* @param currentNode to sanitize
	*/
	const _sanitizeAttributes = function _sanitizeAttributes(currentNode) {
		_executeHooks(hooks.beforeSanitizeAttributes, currentNode, null);
		const attributes = currentNode.attributes;
		if (!attributes || _isClobbered(currentNode)) return;
		const hookEvent = {
			attrName: "",
			attrValue: "",
			keepAttr: true,
			allowedAttributes: ALLOWED_ATTR,
			forceKeepAttr: void 0
		};
		let l = attributes.length;
		const lcTag = transformCaseFunc(currentNode.nodeName);
		while (l--) {
			const attr = attributes[l];
			const name = attr.name, namespaceURI = attr.namespaceURI, attrValue = attr.value;
			const lcName = transformCaseFunc(name);
			const initValue = attrValue;
			let value = name === "value" ? initValue : stringTrim(initValue);
			hookEvent.attrName = lcName;
			hookEvent.attrValue = value;
			hookEvent.keepAttr = true;
			hookEvent.forceKeepAttr = void 0;
			_executeHooks(hooks.uponSanitizeAttribute, currentNode, hookEvent);
			value = hookEvent.attrValue;
			if (SANITIZE_NAMED_PROPS && (lcName === "id" || lcName === "name") && stringIndexOf(value, SANITIZE_NAMED_PROPS_PREFIX) !== 0) {
				_removeAttribute(name, currentNode);
				value = SANITIZE_NAMED_PROPS_PREFIX + value;
			}
			if (SAFE_FOR_XML && regExpTest(/((--!?|])>)|<\/(style|script|title|xmp|textarea|noscript|iframe|noembed|noframes)/i, value)) {
				_removeAttribute(name, currentNode);
				continue;
			}
			if (lcName === "attributename" && stringMatch(value, "href")) {
				_removeAttribute(name, currentNode);
				continue;
			}
			if (hookEvent.forceKeepAttr) continue;
			if (!hookEvent.keepAttr) {
				_removeAttribute(name, currentNode);
				continue;
			}
			if (!ALLOW_SELF_CLOSE_IN_ATTR && regExpTest(SELF_CLOSING_TAG, value)) {
				_removeAttribute(name, currentNode);
				continue;
			}
			if (SAFE_FOR_TEMPLATES) value = _stripTemplateExpressions(value);
			if (!_isValidAttribute(lcTag, lcName, value)) {
				_removeAttribute(name, currentNode);
				continue;
			}
			value = _applyTrustedTypesToAttribute(lcTag, lcName, namespaceURI, value);
			if (value !== initValue) _setAttributeValue(currentNode, name, namespaceURI, value);
		}
		_executeHooks(hooks.afterSanitizeAttributes, currentNode, null);
	};
	/**
	* _sanitizeShadowDOM
	*
	* @param fragment to iterate over recursively
	*/
	const _sanitizeShadowDOM2 = function _sanitizeShadowDOM(fragment) {
		let shadowNode = null;
		const shadowIterator = _createNodeIterator(fragment);
		_executeHooks(hooks.beforeSanitizeShadowDOM, fragment, null);
		while (shadowNode = shadowIterator.nextNode()) {
			_executeHooks(hooks.uponSanitizeShadowNode, shadowNode, null);
			_sanitizeElements(shadowNode);
			_sanitizeAttributes(shadowNode);
			if (_isDocumentFragment(shadowNode.content)) _sanitizeShadowDOM2(shadowNode.content);
			if ((getNodeType ? getNodeType(shadowNode) : shadowNode.nodeType) === NODE_TYPE.element) {
				const innerSr = getShadowRoot(shadowNode);
				if (_isDocumentFragment(innerSr)) {
					_sanitizeAttachedShadowRoots(innerSr);
					_sanitizeShadowDOM2(innerSr);
				}
			}
		}
		_executeHooks(hooks.afterSanitizeShadowDOM, fragment, null);
	};
	/**
	* _sanitizeAttachedShadowRoots
	*
	* Walks `root` and feeds every attached shadow root we encounter into
	* the existing _sanitizeShadowDOM pipeline. The default node iterator
	* does not descend into shadow trees, so nodes inside an attached
	* shadow root would otherwise be skipped entirely.
	*
	* Two real input paths put attached shadow roots in front of us:
	*   1. IN_PLACE on a DOM node that already has shadow roots attached.
	*   2. DOM-node input where importNode(dirty, true) deep-clones the
	*      shadow root because it was created with `clonable: true`.
	*
	* This pass runs once, up front, so the main iteration loop (and the
	* existing _sanitizeShadowDOM template-content recursion) stay
	* untouched — string-input paths are not affected.
	*
	* @param root the subtree root to walk for attached shadow roots
	*/
	const _sanitizeAttachedShadowRoots = function _sanitizeAttachedShadowRoots(root) {
		const stack = [{
			node: root,
			shadow: null
		}];
		while (stack.length > 0) {
			const item = stack.pop();
			if (item.shadow) {
				_sanitizeShadowDOM2(item.shadow);
				continue;
			}
			const node = item.node;
			const isElement = (getNodeType ? getNodeType(node) : node.nodeType) === NODE_TYPE.element;
			const childNodes = getChildNodes(node);
			if (childNodes) for (let i = childNodes.length - 1; i >= 0; --i) stack.push({
				node: childNodes[i],
				shadow: null
			});
			if (isElement) {
				const rootName = getNodeName ? getNodeName(node) : null;
				if (typeof rootName === "string" && transformCaseFunc(rootName) === "template") {
					const content = node.content;
					if (_isDocumentFragment(content)) stack.push({
						node: content,
						shadow: null
					});
				}
			}
			if (isElement) {
				const sr = getShadowRoot(node);
				if (_isDocumentFragment(sr)) stack.push({
					node: null,
					shadow: sr
				}, {
					node: sr,
					shadow: null
				});
			}
		}
	};
	DOMPurify.sanitize = function(dirty) {
		let cfg = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {};
		let body = null;
		let importedNode = null;
		let currentNode = null;
		let returnNode = null;
		IS_EMPTY_INPUT = !dirty;
		if (IS_EMPTY_INPUT) dirty = "<!-->";
		if (typeof dirty !== "string" && !_isNode(dirty)) {
			dirty = stringifyValue(dirty);
			if (typeof dirty !== "string") throw typeErrorCreate("dirty is not a string, aborting");
		}
		if (!DOMPurify.isSupported) return dirty;
		if (SET_CONFIG) {
			ALLOWED_TAGS = SET_CONFIG_ALLOWED_TAGS;
			ALLOWED_ATTR = SET_CONFIG_ALLOWED_ATTR;
		} else _parseConfig(cfg);
		if (hooks.uponSanitizeElement.length > 0 || hooks.uponSanitizeAttribute.length > 0) ALLOWED_TAGS = clone(ALLOWED_TAGS);
		if (hooks.uponSanitizeAttribute.length > 0) ALLOWED_ATTR = clone(ALLOWED_ATTR);
		DOMPurify.removed = [];
		const inPlace = IN_PLACE && typeof dirty !== "string" && _isNode(dirty);
		if (inPlace) {
			const nn = getNodeName ? getNodeName(dirty) : dirty.nodeName;
			if (typeof nn === "string") {
				const tagName = transformCaseFunc(nn);
				if (!ALLOWED_TAGS[tagName] || FORBID_TAGS[tagName]) throw typeErrorCreate("root node is forbidden and cannot be sanitized in-place");
			}
			if (_isClobbered(dirty)) throw typeErrorCreate("root node is clobbered and cannot be sanitized in-place");
			try {
				_sanitizeAttachedShadowRoots(dirty);
			} catch (error) {
				_neutralizeRoot(dirty);
				throw error;
			}
		} else if (_isNode(dirty)) {
			body = _initDocument("<!---->");
			importedNode = body.ownerDocument.importNode(dirty, true);
			if (importedNode.nodeType === NODE_TYPE.element && importedNode.nodeName === "BODY") body = importedNode;
			else if (importedNode.nodeName === "HTML") body = importedNode;
			else body.appendChild(importedNode);
			_sanitizeAttachedShadowRoots(importedNode);
		} else {
			if (!RETURN_DOM && !SAFE_FOR_TEMPLATES && !WHOLE_DOCUMENT && dirty.indexOf("<") === -1) return trustedTypesPolicy && RETURN_TRUSTED_TYPE ? _createTrustedHTML(dirty) : dirty;
			body = _initDocument(dirty);
			if (!body) return RETURN_DOM ? null : RETURN_TRUSTED_TYPE ? emptyHTML : "";
		}
		if (body && FORCE_BODY) _forceRemove(body.firstChild);
		const nodeIterator = _createNodeIterator(inPlace ? dirty : body);
		try {
			while (currentNode = nodeIterator.nextNode()) {
				_sanitizeElements(currentNode);
				_sanitizeAttributes(currentNode);
				if (_isDocumentFragment(currentNode.content)) _sanitizeShadowDOM2(currentNode.content);
			}
		} catch (error) {
			if (inPlace) _neutralizeRoot(dirty);
			throw error;
		}
		if (inPlace) {
			arrayForEach(DOMPurify.removed, (entry) => {
				if (entry.element) _neutralizeSubtree(entry.element);
			});
			if (SAFE_FOR_TEMPLATES) _scrubTemplateExpressions2(dirty);
			return dirty;
		}
		if (RETURN_DOM) {
			if (SAFE_FOR_TEMPLATES) _scrubTemplateExpressions2(body);
			if (RETURN_DOM_FRAGMENT) {
				returnNode = createDocumentFragment.call(body.ownerDocument);
				while (body.firstChild) returnNode.appendChild(body.firstChild);
			} else returnNode = body;
			if (ALLOWED_ATTR.shadowroot || ALLOWED_ATTR.shadowrootmode) returnNode = importNode.call(originalDocument, returnNode, true);
			return returnNode;
		}
		let serializedHTML = WHOLE_DOCUMENT ? body.outerHTML : body.innerHTML;
		if (WHOLE_DOCUMENT && ALLOWED_TAGS["!doctype"] && body.ownerDocument && body.ownerDocument.doctype && body.ownerDocument.doctype.name && regExpTest(DOCTYPE_NAME, body.ownerDocument.doctype.name)) serializedHTML = "<!DOCTYPE " + body.ownerDocument.doctype.name + ">\n" + serializedHTML;
		if (SAFE_FOR_TEMPLATES) serializedHTML = _stripTemplateExpressions(serializedHTML);
		return trustedTypesPolicy && RETURN_TRUSTED_TYPE ? _createTrustedHTML(serializedHTML) : serializedHTML;
	};
	DOMPurify.setConfig = function() {
		let cfg = arguments.length > 0 && arguments[0] !== void 0 ? arguments[0] : {};
		_parseConfig(cfg);
		SET_CONFIG = true;
		SET_CONFIG_ALLOWED_TAGS = ALLOWED_TAGS;
		SET_CONFIG_ALLOWED_ATTR = ALLOWED_ATTR;
	};
	DOMPurify.clearConfig = function() {
		CONFIG = null;
		SET_CONFIG = false;
		SET_CONFIG_ALLOWED_TAGS = null;
		SET_CONFIG_ALLOWED_ATTR = null;
		trustedTypesPolicy = defaultTrustedTypesPolicy;
		emptyHTML = "";
	};
	DOMPurify.isValidAttribute = function(tag, attr, value) {
		if (!CONFIG) _parseConfig({});
		const lcTag = transformCaseFunc(tag);
		const lcName = transformCaseFunc(attr);
		return _isValidAttribute(lcTag, lcName, value);
	};
	DOMPurify.addHook = function(entryPoint, hookFunction) {
		if (typeof hookFunction !== "function") return;
		if (!objectHasOwnProperty(hooks, entryPoint)) return;
		arrayPush(hooks[entryPoint], hookFunction);
	};
	DOMPurify.removeHook = function(entryPoint, hookFunction) {
		if (!objectHasOwnProperty(hooks, entryPoint)) return;
		if (hookFunction !== void 0) {
			const index = arrayLastIndexOf(hooks[entryPoint], hookFunction);
			return index === -1 ? void 0 : arraySplice(hooks[entryPoint], index, 1)[0];
		}
		return arrayPop(hooks[entryPoint]);
	};
	DOMPurify.removeHooks = function(entryPoint) {
		if (!objectHasOwnProperty(hooks, entryPoint)) return;
		hooks[entryPoint] = [];
	};
	DOMPurify.removeAllHooks = function() {
		hooks = _createHooksMap();
	};
	return DOMPurify;
}
var purify = createDOMPurify();
//#endregion
//#region src/components/JsonTreeDiff/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "json-tree-diff" };
var _hoisted_2 = { class: "diff-container" };
var _hoisted_3 = { class: "diff-side diff-left" };
var _hoisted_4 = ["innerHTML"];
var _hoisted_5 = { class: "diff-side diff-right" };
var _hoisted_6 = ["innerHTML"];
//#endregion
//#region src/components/JsonTreeDiff/index.vue
var JsonTreeDiff_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	props: {
		oldData: {},
		newData: {}
	},
	setup(__props) {
		const props = __props;
		const jsondiffpatch = create$1();
		const delta = computed(() => jsondiffpatch.diff(props.oldData, props.newData));
		function sanitize(html) {
			return purify.sanitize(html, {
				ALLOWED_TAGS: [
					"div",
					"span",
					"ul",
					"li",
					"ins",
					"del",
					"i",
					"b",
					"small"
				],
				ALLOWED_ATTR: ["class", "style"]
			});
		}
		const leftHtml = computed(() => {
			return sanitize(format(delta.value, props.oldData) || "<i>无数据</i>");
		});
		const rightHtml = computed(() => {
			return sanitize(format(jsondiffpatch.diff(props.newData, props.oldData), props.newData) || "<i>无数据</i>");
		});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", _hoisted_1, [createElementVNode("div", _hoisted_2, [createElementVNode("div", _hoisted_3, [_cache[0] || (_cache[0] = createElementVNode("div", { class: "diff-header" }, "旧版本", -1)), createElementVNode("pre", {
				class: "diff-content",
				innerHTML: leftHtml.value
			}, null, 8, _hoisted_4)]), createElementVNode("div", _hoisted_5, [_cache[1] || (_cache[1] = createElementVNode("div", { class: "diff-header" }, "新版本", -1)), createElementVNode("pre", {
				class: "diff-content",
				innerHTML: rightHtml.value
			}, null, 8, _hoisted_6)])])]);
		};
	}
}), [["__scopeId", "data-v-228e6393"]]);
//#endregion
export { JsonTreeDiff_default as t };

//# sourceMappingURL=JsonTreeDiff-CvcFM07K.js.map