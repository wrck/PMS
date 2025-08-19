// 数据源
const cascadeData = {
  key: "platformType",
  label: "软件平台",
  data: "$ref:platformType",
  definitions: {
    platformType: [
      {
        value: "conplat",
        label: "Conplat平台",
        children: "$ref:conplatReleaseType"
      },
      {
        value: "smart",
        label: "Smart平台",
        children: "$ref:smartReleaseType"
      },
      {
        value: "other",
        label: "其它平台"
      }
    ],
    conplatReleaseType: {
      key: "releaseType",
      label: "发布类型",
      data: [
        { value: "S", label: "S", children: "$ref:nonHArchitectureType" },
        { value: "B", label: "B", children: "$ref:nonHArchitectureType" },
        { value: "A", label: "A", children: "$ref:nonHArchitectureType" },
        { value: "H", label: "H", children: "$ref:HArchitectureType" }
      ]
    },
    smartReleaseType: {
      key: "releaseType",
      label: "发布类型",
      data: [
        { value: "S", label: "S", children: "$ref:nonHArchitectureTypeSmart" },
        { value: "H", label: "H", children: "$ref:HArchitectureTypeSmart" }
      ]
    },
    HArchitectureType: {
      key: "architectureType",
      label: "架构类型",
      data: Array.from({ length: 11 }, (_, i) => ({
        value: `${i + 1}`,
        label: `${i + 1}`,
        children: "$ref:HBranchType"
      }))
    },
    HArchitectureTypeSmart: {
      key: "architectureType",
      label: "架构类型",
      data: Array.from({ length: 11 }, (_, i) => ({
        value: `${i + 1}`,
        label: `${i + 1}`,
        children: "$ref:HBranchTypeSmart"
      }))
    },
    nonHArchitectureType: {
      key: "architectureType",
      label: "架构类型",
      data: [
        { value: "111", label: "111", children: "$ref:nonHBranchType" },
        { value: "211", label: "211", children: "$ref:nonHBranchType" },
        { value: "311", label: "311", children: "$ref:nonHBranchType" },
        { value: "511", label: "511", children: "$ref:nonHBranchType" }
      ]
    },
    nonHBranchType: {
      key: "branchType",
      label: "版本类型",
      data: [
        { value: "C004", label: "C004" },
        { value: "C008", label: "C008" },
        { value: "C011", label: "C011" },
        { value: "C012", label: "C012" },
        { value: "C013", label: "C013" },
        { value: "CM005", label: "CM005" },
        { value: "CM006", label: "CM006" }
      ]
    },
    nonHArchitectureTypeSmart: {
      key: "architectureType",
      label: "架构类型",
	  data: [
        { value: "221", label: "221", children: "$ref:nonHBranchTypeSmart" }
      ]
	},
    nonHBranchTypeSmart: {
      key: "branchType",
      label: "版本类型",
      data: [
        { value: "S005", label: "S005" },
		{ value: "S006", label: "S006" }
      ]
    },
    HBranchType: {
      key: "branchType",
      label: "版本类型",
      defaultValue: 'C',
      data: [{ value: "C", label: "C" }]
    },
    HBranchTypeSmart: {
      key: "branchType",
      label: "版本类型",
      defaultValue: 'S',
      data: [{ value: "S", label: "S" }]
    }
  }
};

// 工具函数：解析 $ref 路径
//工具函数：解析 $ref 路径
function resolveRefs(data, definitions) {
	if (typeof data === 'string' && data.startsWith("$ref:")) {
		const refKey = data.slice(5);
	    return JSON.parse(JSON.stringify(definitions[refKey])); ;
	} else if (Array.isArray(data)) {
		return data.map(item => resolveRefs(item, definitions));
	} else if (typeof data === 'object' && data !== null) {
		var newObj = {};
      	for (let key in data) {
	        newObj[key] = resolveRefs(data[key], definitions);
	        newObj[key] = resolveRefs(newObj[key], definitions);
      	}
	    return newObj;
	}
	return data;
}

// 渲染整个级联结构
function renderSoftVersionTypesCascade(containerId, initialKey) {
  let $container = $(containerId);
  $container = $container.length > 0 ? $container  : $(`#${containerId}`);

  // 创建下拉框
  function createSelect(key, config, onChangeCallback) {
	options = config.data || config;
	var lable = config.label || '';
    const $select = $('<select class="form-control">')
    	.attr("id", key)
    	.attr("data-key", key)
    	.attr("data-label", config.label)
    	.attr("title", config.label);

    // 默认选项
    $select.append($("<option>").val("").text(`请选择${lable}`));

    // 添加选项
    options.forEach(item => {
      $select.append(
        $("<option>")
          .val(item.value)
          .text(item.label || item.value)
      );
    });

    // 绑定 change 事件
    if (onChangeCallback) {
      $select.on("change", function () {
        onChangeCallback($(this).val());
      });
    }

    $container.append($select);
    return $select;
  }

  // 递归加载下一级
  function loadNextLevel(currentKey, selectedValue, parentRef) {
    const definitions = cascadeData.definitions;
    let currentDef;

    if (!parentRef) {
      currentDef = resolveRefs(cascadeData, definitions);
    } else {
      currentDef = parentRef;
    }

    if (!currentDef || !currentDef.data) return;
    
    const nextKey = currentDef.key;
    selectedValue = selectedValue || currentDef.defaultValue;
    

    // 创建当前层级的下拉框
    const $nextSelect = createSelect(nextKey, currentDef, function (value) {
      // 删除后续所有下拉框
      const filteredData = Array.isArray(currentDef.data) ? currentDef.data : [currentDef.data];
      const allSelects = $container.find("select");
      const currentIndex = allSelects.index($nextSelect);
      allSelects.slice(currentIndex + 1).remove();

      const selectedItem = filteredData.find(d => d.value === value);
      if (selectedItem && selectedItem.children) {
        loadNextLevel(selectedItem.value, null, selectedItem.children);
      }
    });
    if (!(selectedValue == null || selectedValue == undefined || selectedValue == '' || selectedValue ==  "undefined")) {
    	$nextSelect.val(selectedValue).trigger("change");
    }
  }

  // 初始化第一个层级
  loadNextLevel(initialKey, null, null);
}

function generateValueLabelMaps(cascadeData) {
  const result = {};
  const definitions = cascadeData || cascadeData.definitions;

  for (const defKey in definitions) {
    const definition = definitions[defKey];

    // 如果 definition 是数组（如 platformType）
    if (Array.isArray(definition)) {
      const key = definition.key || definitions.key || defKey;
      const mapName = `${key}Map`;
      result[mapName] = result[mapName] || {};

      definition.forEach(item => {
        result[mapName][item.value] = item.label || item.value;
      });

      continue;
    }

    // 如果 definition 是对象，并且包含 data 数组
    if (definition && typeof definition === 'object' && Array.isArray(definition.data)) {
      const key = definition.key || definitions.key ||  defKey;
      const mapName = `${key}Map`;
      result[mapName] = result[mapName] || {};

      definition.data.forEach(item => {
        result[mapName][item.value] = item.label || item.value;
        
        if (item.children) {
        	const childResult = generateOrderedValueLabelMaps(item.children);
        	for ( var mapName in childResult) {
        		result[mapName] = childResult[mapName];
			}
        }
      });
    }
  }

  return result;
}

function generateOrderedValueLabelMaps(cascadeData) {
  const result = {};
  const definitions = cascadeData || cascadeData.definitions;

  for (const defKey in definitions) {
    const definition = definitions[defKey];

    let dataArray = [];

    // 如果是数组类型（如 platformType）
    if (Array.isArray(definition)) {
      dataArray = definition;
    }
    // 如果是对象且包含 data 数组
    else if (definition && typeof definition === 'object' && Array.isArray(definition.data)) {
      dataArray = definition.data;
    } else {
      continue; // 忽略无效数据
    }

    const key = definition.key || definitions.key || defKey;
    const mapName = `${key}Map`;
    const map = result[mapName] || new Map();

    dataArray.forEach(item => {
      if (item && item.value !== undefined) {
        map.set(item.value, item.label || item.value);
        
        if (item.children) {
        	generateOrderedValueLabelMaps(item.children);
        }
      }
    });

    result[mapName] = map;
  }

  return result;
}

function extractMapFromResolvedCascade(cascade) {
  const result = {};

  function traverse(node, visited = new Set()) {
    // 防止循环引用
    if (visited.has(node)) return;
    visited.add(node);

    // 当前层级的 key 和数据
    const key = node.key;
    const data = node.data || [];

    // 创建 Map
    const mapName = `${key}`;
    if (!result[mapName]) {
      result[mapName] = {};
    }

    // 添加当前层级的数据到 Map（保持顺序）
    for (const item of data) {
      if (item.value !== undefined) {
        result[mapName][item.value] = item.label || item.value;
      }

      // 如果有 children，继续递归
      if (item.children) {
        traverse(item.children, visited);
      }
    }
  }

  // 开始递归
  traverse(cascade);

  return result;
}

function extractMapsFromResolvedCascade(cascade) {
  const result = {};

  function traverse(node, visited = new Set()) {
    // 防止循环引用
    if (visited.has(node)) return;
    visited.add(node);

    // 当前层级的 key 和数据
    const key = node.key;
    const data = node.data || [];

    // 创建 Map
    const mapName = `${key}`;
    if (!result[mapName]) {
      result[mapName] = new Map();
    }

    // 添加当前层级的数据到 Map（保持顺序）
    for (const item of data) {
      if (item.value !== undefined) {
        result[mapName].set(item.value, item.label || item.value);
      }

      // 如果有 children，继续递归
      if (item.children) {
        traverse(item.children, visited);
      }
    }
  }

  // 开始递归
  traverse(cascade);

  return result;
}

function mapToObject(map) {
  const obj = {};
  const values = [];
  for (const [k, v] of map.entries()) {
    obj[k] = v;
    values.push(k);
  }
  return { map: obj, values };
}

function objDeepOmit(obj, keysToOmit) {
  if (Array.isArray(obj)) {
    return obj.map(v => objDeepOmit(v, keysToOmit));
  } else if (obj && typeof obj === 'object') {
    return Object.keys(obj).reduce((acc, key) => {
      if (keysToOmit.includes(key)) return acc;
      acc[key] = objDeepOmit(obj[key], keysToOmit);
      return acc;
    }, {});
  }
  return obj;
}