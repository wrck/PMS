<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="system.title" /></title>
<cssTag>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/datepicker/datepicker3.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/common/css/base.css">
	<!-- treeview -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-treeview/bootstrap-treeview.min.css">
</cssTag>
</head>
<body>
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>菜单/功能管理</h1>
		<ol class="breadcrumb">
			<li><a href="#"><i class="fa fa-dashboard"></i> 首页</a></li>
			<li><a href="#">系统管理</a></li>
			<li class="active">菜单/功能管理</li>
		</ol>
	</section>
	<!-- Main content -->
	<section class="content">

		<div class="row">
			<div class="col-md-3">

				<!-- Profile Image -->
				<div class="box box-primary">
					<div class="box-body box-profile">
						<div id="tree"></div>
					</div>
					<!-- /.box-body -->
				</div>
				<!-- /.box -->
			</div>
			<!-- /.col -->
			<div class="col-md-9">
				<div class="box box-primary">
					<div class="box-header with-border">
						<div class="btn-group">
							<button type="button" class="btn btn-default"
								data-btn-type="addRoot">
								<li class="fa fa-plus">&nbsp;新增根菜单</li>
							</button>
							<button type="button" class="btn btn-default" data-btn-type="add">
								<li class="fa fa-plus">&nbsp;新增下级菜单</li>
							</button>
							<button type="button" class="btn btn-default"
								data-btn-type="edit">
								<li class="fa fa-edit">&nbsp;编辑当前菜单</li>
							</button>
							<button type="button" class="btn btn-default"
								data-btn-type="delete">
								<li class="fa fa-remove">&nbsp;删除当前菜单</li>
							</button>
						</div>
						<!-- /.box-tools -->
					</div>
					<!-- /.box-header -->
					<div class="box-body">
						<form class="form-horizontal" id="function-form">
							<input type="hidden" name="_method" value="POST" /> <input
								type="hidden" name="pid" /> <input type="hidden" id="id"
								name="id" />
							<div class="form-group">
								<label for="parentName" class="col-sm-2 control-label">上级</label>
								<div class="col-sm-9">
									<input type="text" class="form-control" disabled="disabled"
										id="parentName" name="parentName" placeholder="上级">
								</div>
							</div>

							<div class="form-group">
								<label for="name" class="col-sm-2 control-label">名称</label>
								<div class="col-sm-9">
									<input type="text" class="form-control" id="name" name="name"
										data-flag="icheck" placeholder="名称">
								</div>
							</div>
							<!-- <div class="form-group">
								<label for="id" class="col-sm-2 control-label">编码</label>
								<div class="col-sm-9">
									<input type="text" class="form-control" id="id" name="id" placeholder="编码">
								</div>
							</div> -->
							<div class="form-group">
								<label for="url" class="col-sm-2 control-label">URL</label>
								<div class="col-sm-9">
									<input type="text" class="form-control" id="url"
										data-flag="icheck" name="url" placeholder="URL">
								</div>
							</div>
							<!-- 	 <div class="form-group">
								<label class="col-sm-2 control-label">菜单类型</label>
								<div class="col-sm-9">
									<label class="control-label"> <input type="radio" name="functype" class="flat-red" checked="checked"
										value="0"> 目录
									</label> &nbsp;&nbsp;&nbsp; <label class="control-label"> <input type="radio" name="functype" class="flat-red"
										value="1"> 菜单
									</label> &nbsp;&nbsp;&nbsp; <label class="control-label"> <input type="radio" name="functype" class="flat-red"
										value="2"> 按钮
									</label>
								</div>
							</div> -->
							<div class="form-group">
								<label for="icon" class="col-sm-2 control-label">图标</label>
								<div class="col-sm-7">
									<i data-bv-icon-for="icon" id="icon_i"
										class="form-control-feedback fa fa-circle-o"
										style="right: 15px"></i> <input type="text"
										class="form-control" id="icon" name="icon" placeholder="图标">
								</div>
								<div class="col-sm-2">
									<button type="button" id="selectIcon"
										class="btn btn-primary disabled" data-btn-type="selectIcon">
										<i class="fa fa-hand-pointer-o">&nbsp;选择图标</i>
									</button>
								</div>
							</div>
							<div class="form-group">
								<label for="sort" class="col-sm-2 control-label">排序</label>
								<div class="col-sm-9">
									<input type="text" class="form-control" id="sort" name="sort"
										placeholder="排序">
								</div>
							</div>
							<div class="form-group">
								<label for="status" class="col-sm-2 control-label">是否可用</label>
								<div class="col-sm-9">
									<label class="control-label"> <input type="radio" data-flag="icheck"
										name="status" class="flat-green" value="1"> 启用
									</label> &nbsp; <label class="control-label"> <input
										type="radio" name="status" class="flat-red" value="0"  data-flag="icheck">
										禁用
									</label>
								</div>
							</div>
							<div class="form-group">
								<label for="remark" class="col-sm-2 control-label">说明</label>
								<div class="col-sm-9">
									<textarea class="form-control" id="remark" name="remark"
										placeholder="说明"></textarea>
								</div>
							</div>
							<div class="box-footer" style="display: none">
								<div class="text-center">
									<button type="button" class="btn btn-default"
										data-btn-type="cancel">
										<i class="fa fa-reply">&nbsp;取消</i>
									</button>
									<button type="submit" class="btn btn-primary">
										<i class="fa fa-save">&nbsp;保存</i>
									</button>
								</div>
							</div>
						</form>
					</div>
					<!-- /.box-body -->
				</div>
				<!-- /. box -->
			</div>
		</div>
		<!-- /.row -->

	</section>
</body>
<jsTag>
	<!-- 表单验证 --><script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/datepicker/bootstrap-datepicker.js"></script>
	<!-- treeview -->
	<script src="${pageContext.request.contextPath}/static/plugins/bootstrap-treeview/bootstrap-treeview.min.js"></script>

	<%-- <script src="${pageContext.request.contextPath}/static/common/js/base.js"></script> --%>
	<script src="${pageContext.request.contextPath}/static/common/js/base-form.js"></script>
	<script src="${pageContext.request.contextPath}/static/common/js/base-modal.js"></script>
	<script>
        //初始化form表单
		var form = null;
		var winId='iconWin';
		var basePath = "${pageContext.request.contextPath}";
		$(function() {
			form=$('#function-form').form();
			console.log(form);
			initTree(0);
			//初始化校验
			$('#function-form').bootstrapValidator({
				message : '请输入有效值',
				feedbackIcons : {
					valid : 'glyphicon glyphicon-ok',
					invalid : 'glyphicon glyphicon-remove',
					validating : 'glyphicon glyphicon-refresh'
				},
				submitHandler : function(validator, functionform, submitButton) {
					modals.confirm('确认保存？', function() {
						//Save Data，对应'submit-提交'
						var path = "/sys/menu/";
						if(btntype == "edit"){
							$("input[name='_method']").val("PUT");
							path += $("#id").val()+".json";
						}else{
							$("input[name='_method']").val("POST");
							path += "detail.json";
						}
						var params = form.getFormSimpleData();
						ajaxPost(basePath + path, params, function(data, status) {
							if (status == 'success') {
								//var id=$("input[name='id']").val();
								var selectedArr=$("#tree").data("treeview").getSelected();
								var selectedNodeId=selectedArr.length>0?selectedArr[0].nodeId:0;
							    initTree(selectedNodeId);
							}
						});
					});
				},
				fields : {
					name : {
						validators : {
							notEmpty : {
								message : '请输入名称'
							}
						}
					},
					url : {
						validators : {
							notEmpty : {
								message : '请输入URL'
							}
						}
					},
					status : {
						validators : {
							notEmpty : {
								message : '请选择是否可用'
							}
						}
					}
				}
			});

			//按钮事件
			var btntype=null;
			$('button[data-btn-type]').click(function() {
				var action = $(this).attr('data-btn-type');
				var selectedArr=$("#tree").data("treeview").getSelected();
				var selectedNode=selectedArr.length>0?selectedArr[0]:null;
				switch (action) {
				case 'addRoot':
					formWritable(action);
					form.clearForm();
					$("#icon_i").removeClass();
					//填充上级菜单和层级编码
					fillParentAndLevelCode(null);
					btntype='add';
					break;
				case 'add':
					if(!selectedNode){
						modals.info('请先选择上级菜单');
						return false;
					}
					formWritable(action);
					form.clearForm();
					$("#icon_i").removeClass();
					//填充上级菜单和层级编码
					fillParentAndLevelCode(selectedNode);
					btntype='add';
					break;
				case 'edit':
					if(!selectedNode){
						modals.info('请先选择要编辑的节点');
						return false;
					}
					if(btntype=='add'){
						fillDictForm(selectedNode);
					}
					formWritable(action);
					btntype='edit';
					break;
				case 'delete':
					if(!selectedNode){
						modals.info('请先选择要删除的节点');
						return false;
					}
					if(btntype=='add')
						fillDictForm(selectedNode);
					formReadonly();
					$(".box-header button[data-btn-type='delete']").removeClass("btn-default").addClass("btn-primary");
				    if(selectedNode.nodes){
				    	modals.info('该节点含有子节点，请先删除子节点');
				    	return false;
				    }
				    modals.confirm('是否删除该节点',function(){
				    	ajaxPost(basePath+"/sys/menu/"+selectedNode.id +".json?_method=DELETE",null,function(data, status){
				    		if (status == 'success') {
				    		   modals.correct('删除成功');
				    		}else{
				    			modals.info(data.message);
				    		}
				    		//定位
				    		var brothers=$("#tree").data("treeview").getSiblings(selectedNode);
				    		if(brothers.length>0)
				    		   initTree(brothers[brothers.length-1].nodeId);
				    		else{
				    		   var parent=$("#tree").data("treeview").getParent(selectedNode);
				    		   initTree(parent?parent.nodeId:0);
				    		}
				    	});
				    });
					break;
				case 'cancel':
					if(btntype=='add')
						fillDictForm(selectedNode);
					formReadonly();
					break;
				case 'selectIcon':
					var disabled=$(this).hasClass("disabled");
			        if(disabled){
			         	break;
			        }
					var iconName;
					if($("#icon").val()){
					   iconName = encodeURIComponent($("#icon").val());
					}
					modals.openWin({
                       	winId:winId,
                       	title:'图标选择器（双击选择）',
                       	width:'1000px',
                       	url:basePath+"/sys/modals/icon_selector?iconName="+iconName
                       });
					break;
				}
			});
		})

		function initTree(selectNodeId){
			var treeData = '';
			ajaxPost(basePath + "/sys/menu/getTreeData.json", null, function(data) {
				treeData = data;
				//console.log(JSON.stringify(treeData));
				$("#tree").treeview({
					data : treeData,
					showBorder : true,
					expandIcon : "glyphicon glyphicon-chevron-right",
					collapseIcon : "glyphicon glyphicon-chevron-down",
					levels : 1,
					onNodeSelected : function(event, data) {
						/*   alert("i am selected");
						  alert(data.nodeId); */
						fillDictForm(data);
						formReadonly();
						//console.log(JSON.stringify(data));
					}
				});
				if(treeData.length==0)
					return;

				/*
				//默认选中第一个节点
				selectNodeId=selectNodeId||0;
				$("#tree").data('treeview').selectNode(selectNodeId);
				$("#tree").data('treeview').expandNode(selectNodeId);
				$("#tree").data('treeview').revealNode(selectNodeId); */
				// 展开所有节点
				$("#tree").data('treeview').selectNode(selectNodeId);
				$("#tree").data('treeview').expandAll();
			});
		}

		//新增时，带入父级菜单名称id,自动生成levelcode
		function fillParentAndLevelCode(selectedNode){
			$("input[name='parentName']").val(selectedNode?selectedNode.text:'');
		    $("input[name='status'][value='1']").prop("checked","checked");
		    if(selectedNode){
		    	$("input[name='pid']").val(selectedNode.id);
				var nodes=selectedNode.nodes;
				var sort=nodes?nodes[nodes.length-1].sort:0;
				$("input[name='sort']").val(sort+1);
		    }else{
		    	var brothers=null;
		    	brothers=$("#tree").data("treeview").getSiblings(0);
		    	var sort = 0;
		    	if(brothers&&brothers.length>0)
		    	   sort = brothers[brothers.length-1].sort;
		    	$("input[name='sort']").val(sort+1);
		    }
		}

		//填充form
		function fillDictForm(node){
			form.clearForm();
			ajaxPost(basePath+"/sys/menu/"+node.id+".json",null,function(data){
				data = data.menu;
				form.initFormData(data);
				fillBackIconName(data.icon);
			})
		}

		//设置form为只读
		function formReadonly(){
			//所有文本框只读
			$("input[name],textarea[name]").attr("readonly","readonly");
			//隐藏取消、保存按钮
			$("#function-form .box-footer").hide();
			//还原新增、编辑、删除按钮样式
			$(".box-header button").removeClass("btn-primary").addClass("btn-default");
			//选择图标按钮、单选项为只读
			$("#selectIcon").addClass("disabled");
			$("input[name][type='radio']").attr("disabled","disabled");
			//还原校验框
			if($("#function-form").data('bootstrapValidator'))
				$("#function-form").data('bootstrapValidator').resetForm();
		}

		function formWritable(action){
			$("input[name],textarea[name]").removeAttr("readonly");
			$("input[name][type='radio']").removeAttr("disabled");
			$("#function-form .box-footer").show();
			$(".box-header button").removeClass("btn-primary").addClass("btn-default");
			$("#selectIcon").removeClass("disabled");
			if(action)
				$(".box-header button[data-btn-type='"+action+"']").removeClass("btn-default").addClass("btn-primary");
		}

		//回填图标
		function fillBackIconName(icon_name){
			$("#icon").val(icon_name);
			$("#icon_i").removeClass().addClass("form-control-feedback").addClass(icon_name);
		}
	</script>
</jsTag>
</html>