<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@page import="com.dp.plat.util.StringEscUtil"%>
<html>
<dp:base />
<head>
</head>
<body>
    <style>
        .layui-table-view .layui-table tbody tr[data-level="0"] td {
            border-top: solid 1px !important;
        }
        .layui-table-view .layui-table tbody tr.even {
            background-color: #f8f8f8;
        }
    </style>
    <script type="text/html" id="projectProductConfigLevelInfoListTableToolbar">
  <div class="layui-btn-container">
    <button class="layui-btn layui-btn-sm" id="expendAll" lay-event="expendAll">折叠全部</i>
    </button>
  </div>
</script>
	<display:table
		name="commonList" pagesize="${commonList.size()}" id="projectProductConfigLevelInfoListTable" 
		size="${commonList.size()}" sort="external" export="true"
		decorator="com.dp.plat.decorators.Wrapper" class="displayTable table hidden"
		requestURI="module/sub/projectSub_projectProductConfigLevelInfo.action" 
		partialList="true">
        <display:column property="parentGroupPaths" class="parentGroupPaths" media="html"></display:column>
        <display:column property="parentBomPaths" class="parentBomPaths" media="html"></display:column>
        
		<display:column property="projectCode" titleKey="pm.project.projectCode" class="projectCode" headerClass="projectCode"></display:column>
        <display:column property="groupPaths" title="配置路径" class="groupPaths" headerClass="groupPaths" media="html"></display:column>
        <display:column property="itemGroup" title="分组" class="itemGroup"></display:column>
        <display:column property="level" title="层级" class="level"></display:column>
        <display:column property="bomPaths" title="配置路径" class="bomPaths" headerClass="bomPaths" media="excel"></display:column>
        <%-- <display:column property="groupLevel" title="分组层级" class="groupLevel" headerClass="groupLevel" media="html"></display:column> --%>
        <display:column property="parentCode" title="父编码" class="parentCode" media="html"></display:column>
        <display:column property="itemCode" titleKey="pm.orderdata.itemCode" class="itemCode" headerClass="itemCode" media="html"></display:column>
        <display:column titleKey="pm.orderdata.itemCode" class="itemCode" headerClass="itemCode" media="excel">
            <c:forEach begin="2" end="${projectProductConfigLevelInfoListTable.level}">—</c:forEach>${projectProductConfigLevelInfoListTable.itemCode}
        </display:column>
		<display:column property="itemModel" titleKey="pm.orderdata.model" class="itemModel" headerClass="itemModel"></display:column>
		<display:column property="itemDesc" titleKey="pm.orderdata.itemName" class="itemDesc" headerClass="itemDesc"></display:column>
		<display:column property="quantity" title="数量" class="quantity" headerClass="quantity"></display:column>
		<display:setProperty name="export.excel.filename" value='配置关系清单.xls'/>
	</display:table>
    
    <link href="statics/plugins/layui/css/layui.css" rel="stylesheet">
    <script type="text/javascript" src="statics/plugins/layui/layui.js"></script>
    <script type="text/javascript">
    
    // 构建树形结构
    function buildTree(tableId) {
      const map = {};
      const rootNodes = [];
      
      var $trs = $("#" + tableId).find("tbody tr");
      // 先将所有节点存入 map，key 为 groupPaths
      var data = [];
      var cols = [];
      var $titleTr = $("#" + tableId).find("thead tr");
      $("td,th", $titleTr).each(function() {
          var field = $.trim($(this).attr("class"));
          var title = $.trim($(this).text());
          if (field) {
              cols.push({
                  field: field,
                  title: title
              })
          }
      })
      $trs.each((index, $tr) => {
    	  var item = {};
    	  $("td", $tr).each(function() {
    		  var field = $.trim($(this).attr("class"));
    		  var value = $.trim($(this).text());
    		  item[field] = value;
    	  })
    	  item.groupLevel = item.groupLevel || [item.itemGroup, item.level].join("-");
    	  data.push(item);
          map[item.groupPaths] = { ...item };
      });

      // 遍历每个节点，根据 parentGroupPaths 找到父节点
      data.forEach(item => {
        const node = map[item.groupPaths];
        if (!node) return;

        const parentBomPaths = item.parentBomPaths;
        const parentPath = item.parentGroupPaths;
        if (parentPath === '' || parentPath === null || parentBomPaths === '' || parentBomPaths === null) {
          // 根节点
          node.isParent = true;
          rootNodes.push(node);
        } else {
          const parentNode = map[parentPath];
          if (parentNode) {
            if (!parentNode.children) parentNode.children = [];
            parentNode.isParent = true;
            parentNode.children.push(node);
          }
        }
      });

      var expandAllDefault = true;
      return {
    	  cols:[cols],
    	  tree: { // 启用树形表格模式
    		  customName: { 
    			  name: 'itemCode' , // 指定哪个字段用于显示树形结构（通常是标题或名称）
    			  pid: "parentGroupPaths",
    			  id: "groupPaths",
    		  },
    		  view: {
    			  indent: 20,
    	    	  //iconOpen: 'layui-icon layui-icon-triangle-d', // 展开图标
    	    	  //iconClose: 'layui-icon layui-icon-triangle-r', // 折叠图标
    	    	  //flexIconOpen:  'layui-icon layui-icon-triangle-d', // 展开图标    
	    		  //flexIconClose:  'layui-icon layui-icon-triangle-r', // 折叠图标     
    	    	  iconLeaf: 'layui-icon layui-icon-subtraction',
    	    	  expandAllDefault : expandAllDefault // 是否默认全部展开
    	      },
    	      data: {
    	    	  rootPid: 'root',
    	      }
    	  },
    	  data: rootNodes,
    	  expandAllStatus: expandAllDefault,
      }
    }
    
    var layfilter = 'projectProductConfigLevelInfoListTable';
    const treeData = buildTree(layfilter);
    /* $("#" + layfilter).attr("lay-filter", layfilter) */
    let currentGroup = null;
    let useClassA = true; 
    layui.use(function(){
	  var treeTable = layui.treeTable;
	  var util = layui.util;
      /* // 转化静态表格
      table.init(layfilter, {
        // height: ''
      }); */
      var inst = treeTable.render($.extend({
          elem:"#" + layfilter,
          toolbar:'#projectProductConfigLevelInfoListTableToolbar',
          defaultToolbar: [],// ['print'],
          //cellExpandedMode: 'tips',
          //even: true,
          // --- 关键：使用 row 回调 ---
          done: function(obj){
        	  $('tr[data-level="0"]').each(function() {
    		    var dataIndex = Number($(this).data("index"));
    		    var evenClass = dataIndex % 2 ? 'even' : 'oven';
    		    $(this).addClass(evenClass);
    		    $('tr[data-index^=' + dataIndex + '-]').addClass(evenClass);
    		  })
          },
      }, treeData));
      
      // 表头工具栏工具事件
      treeTable.on(`toolbar(\${layfilter})`, function (obj) {
        var config = obj.config;
        var tableId = config.id;
        var status = treeTable.checkStatus(tableId);
        // 获取选中行
        console.log(obj.event, obj);
        if (obj.event === "expendAll") {
        	var $expendAllBtn = $('[lay-event="expendAll"]', `[lay-table-id="\${layfilter}"]`);
        	var expandAllStatus = $expendAllBtn.data("expandAllStatus");
        	expandAllStatus = expandAllStatus == undefined ? config.expandAllStatus : expandAllStatus;
        	expandAllStatus = !expandAllStatus;
        	treeTable.expandAll(`\${layfilter}`, expandAllStatus);
        	$expendAllBtn.data("expandAllStatus", expandAllStatus).text(expandAllStatus ? "折叠全部" : "展开全部");
        }
      });
	});
    </script>
</body>
</html>