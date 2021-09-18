<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><li class="fa fa-remove"></li></button>
		<h5 class="modal-title">新增评论</h5>
	</div>

	<div class="modal-body">
		<form id="comment-form" name="comment-form" class="form-horizontal">
			<input type="hidden" name="commentId" id="id">
			<input type="hidden" name="projectCode" id="projectCode" value="${comment.projectCode}">
			<input type="hidden" name="executeId" id="executeId" value="${comment.executeId}">
			<input type="hidden" name="contractId" id="contractId" value="${comment.contractId}">
			<input type="hidden" name="projectName" id="projectName" value="${comment.projectName}">
			<input type="hidden" name="channelCode" id="channelCode" value="${comment.channelCode}">
			<input type="hidden" name="channelName" id="channelName" value="${comment.channelName}">
			<input type="hidden" name="soleAgentId" id="soleAgentId" value="${comment.soleAgentId}">
			<div class="box-body">
				<div class="col-md-12">
					<div class="form-group">
						<label for="soleAgentName" class="col-sm-3 control-label">总代名称</label>
						<div class="col-sm-8">
							<input type="text" class="form-control" id="soleAgentName" name="soleAgentName"  value="${comment.soleAgentName}" data-flag="icheck" readonly="readonly" placeholder="总代名称">
						</div>
					</div>
					<div class="form-group">
						<label for="comment" class="col-sm-3 control-label">评价</label>
						<div class="col-sm-8">
							<textarea class="form-control" rows="6" cols="1" id="comment" name="comment" data-flag="icheck" placeholder="评论内容" style="resize: vertical;"></textarea>
							<!-- <input type="text" class="form-control" id="comment" name="comment" data-flag="icheck" placeholder="评论内容"> -->
						</div>
					</div>
				</div>
			</div>
			<!-- /.box-body -->
			<div class="box-footer text-right">
				<button type="button" class="btn btn-default" data-btn-type="cancel" data-dismiss="modal">取消</button>
				<button type="submit" class="btn btn-primary" data-btn-type="save">提交</button>
			</div>
			<!-- /.box-footer -->
		</form>

	</div>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/css/bootstrap-validator.css"/>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/plugins/iCheck/all.css">
	<script src="${pageContext.request.contextPath}/static/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
	<script src="${pageContext.request.contextPath}/static/plugins/iCheck/icheck.min.js"></script>
	<script>
	//tableId,queryId,conditionContainer
	var form = null;
	var id = "${id!=0 && id!=null?id:0}";
	var uri = "${uri}";
	$(function() {
		//数据校验
		$("#comment-form").bootstrapValidator({
			message : '请输入有效值',
			feedbackIcons : {
				valid : 'glyphicon glyphicon-ok',
				invalid : 'glyphicon glyphicon-remove',
				validating : 'glyphicon glyphicon-refresh'
			},
			submitHandler : function(validator, commentform, submitButton) {
				modals.confirm('确认保存？', function() {
					//Save Data，对应'submit-提交'
					var params = form.getFormSimpleData();
					var url = '/comment/';
					if (uri) {
						url = "/" + uri;
					} else if(id !="0"){
						url += id+'.json';
					} else{
						url += 'detail.json';
					}

					ajaxPost(basePath + url, params, function(data, status) {
						if(status == 'success'){
							modals.closeWin("commentWinId");
							if(id!="0"){//更新
								modals.info("更新成功");
							}else{//新增
								modals.info("评论成功！");
								try {
									findComments(true);
								} catch (e) {
								}
							}
						} else {
							modals.info("评论失败！");
						}
					});
				});
			},
			fields : {
				comment : {
					validators : {
						notEmpty : {
							message : '请输入评论内容'
						}
					}
				},
			}
		});
		//初始化控件
		form=$("#comment-form").form();
		//回填id
		if(id!="0"){
			$("#comment-form").prepend('<input type="hidden" name="_method" value="PUT">');
			ajaxPost(basePath+"/comment/"+id+".json",null,function(data){
				form.initFormData(data.comment);
			})
		}

		$("#comment").change(function() {
			var comment = $(this).val();
			comment = comment.replace(/</g, "&lt;").replace(/>/g, "&gt;");
			$(this).val(comment);
		})
	});


	function resetForm(){
		form.clearForm();
		$("#comment-form").data('bootstrapValidator').resetForm();
	}
</script>