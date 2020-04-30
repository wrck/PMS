/**
 * 一些公共的变量
 */
$.namespace('sys.common');
sys.common = function(){
	tAdd = "<button class='btn btn-xs btn-success btn-add' type='button'><i class='fa fa-fw fa-plus'></i> 新建</button>";
	tEdit= function(rowid){
		return "<button data-rowid='"+rowid+"' class='btn btn-xs btn-success btn-edit' type='button'><i class='fa fa-fw fa-edit'></i> 编辑</button>";
	}
	tSave = function(rowid){
		return "<button data-rowid='"+rowid+"' class='btn btn-xs btn-success btn-save' type='button'><i class='fa fa-fw fa-save'></i> 保存</button>";
	} 
	tCancel = function(rowid){
		return "<button data-rowid='"+rowid+"' class='btn btn-xs btn-success btn-cancel' type='button'><i class='fa fa-fw fa-remove'></i> 取消</button>";
	} 
	tDetail = function(rowid){
		return "<button data-rowid='"+rowid+"' class='btn btn-xs btn-warning btn-deatil' type='button'><i class='fa fa-fw fa-table'></i> 明细</button>";
	} 
	
	feedbackIcons = {
        valid: 'glyphicon glyphicon-ok',
        invalid: 'glyphicon glyphicon-remove',
        validating: 'glyphicon glyphicon-refresh'
    }
	/**
	 * 表单是否禁用
	 */
	formDisabled = function(eleForm ,status){
		var $eleForm = $(eleForm);
		if(!status){
			$("input" ,$eleForm).attr('disabled','disabled');
			$("textarea" ,$eleForm).attr('disabled','disabled');
			$("select" ,$eleForm).attr('disabled','disabled');
			$("button[type='submit']" ,$eleForm).attr('disabled','disabled');
		}else{
			$("input",$eleForm).removeAttr('disabled');
			$("textarea",$eleForm).removeAttr('disabled');
			$("select",$eleForm).removeAttr('disabled');
			$("button[type='submit']",$eleForm).removeAttr('disabled');
		}
	}
	/**
	 * 失效数据项标红
	 */
	markRed = function(ele, status){
		if(!status){
			$(ele).css("color","red");
		}
	}
	
	return {tEdit:tEdit,
		tSave:tSave,
		tCancel:tCancel,
		tDetail:tDetail ,
		feedbackIcons:feedbackIcons,
		formDisabled:formDisabled,
		markRed:markRed};
}();
$(function() {
	$(document).off("click", '[data-btn-type="cancel"]').on("click", '[data-btn-type="cancel"]', cancelBtnClick);
	function cancelBtnClick() {
		if($(this).parents(".modal").length == 0) {
			if (window.history.length > 1) {
				history.back();
			} else {
				window.close();
			}
		}
	}
})
