<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html lang="zh-CN">
<head>
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<meta name="function" content="<s:text name='prob.manage' />">
<script type="text/javascript">
    $(function(){
        date_picker("effectiveTo");
        date_picker("effectiveFrom");
        
        $("#create").click(function(){
            $("#mainForm").attr("action","module/probProduct_save.action");
            //对要必须的数据进行选择
            if(checkPost()){
                $("#mainForm").submit();
                return true;
            }
            return false;
        });
        $("#update").click(function(){
            $("#mainForm").attr("action","module/probProduct_save.action");
            //对要必须的数据进行选择
            if(checkPost()){
                $("#mainForm").submit();
                return true;
            }
            return false;
        });
    });
    
    /*
     * 检查要提交的参数
     */
     function checkPost(){
         fields = new Array('name','state');
         for(i = 0 ;i < fields.length ; i++){
             if(!checkField(fields[i])){
                 return false;
             }
         }
         return true;
     }
     
     function checkField(fieldId){
         $field = $("#"+fieldId);
         if($field.val() == ''){
             $("#"+fieldId+"Msg").text("此字段为必须输入项").addClass("redMark");
             $field.focus();
             return false;
         }else{
             $("#"+fieldId+"Msg").text("").removeClass("redMark");
             return true;
         }
         return true;
     }
     
</script>
</head>
<body>
    <div class="container-flux">
    
        <div class="row">
            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                <div class="listView divHeader">
                    <img src="images/right_zhishi.gif" border="0">
                    <s:if test="probProduct.id != 0 || probProduct.id == null">
                        <s:text name="probProduct.manage.edit"></s:text>
                    </s:if>
                    <s:else>
                        <s:text name="probProduct.manage.create"></s:text>
                    </s:else>
                </div>
                <s:form method="post" action="module/probProduct_save.action" id="mainForm"
                    cssClass="form-horizontal" name="mainForm" enctype="multipart/form-data">
                    <s:hidden name="probProduct.id" id="probProductId"></s:hidden>
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <div class="form-group">
                                <label for="num" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><span class="redmark">*</span><s:text name="probProduct.info.type"></s:text></label>
                                <div class="col-xs-4">
                                    <s:textfield id="type" name="probProduct.type" cssClass="form-control"></s:textfield>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="num" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><span class="redmark">*</span><s:text name="probProduct.info.name"></s:text></label>
                                <div class="col-xs-4">
                                    <s:textfield id="name" name="probProduct.name" cssClass="form-control"></s:textfield>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="num" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><span class="redmark">*</span><s:text name="probProduct.info.name"></s:text></label>
                                <div class="col-xs-4">
                                    <s:textfield id="version" name="probProduct.version" cssClass="form-control"></s:textfield>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="startdate" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"><s:text name="probProduct.info.status"></s:text></label>
                                <div class="col-xs-4">
                                    <s:radio list="#{true:'有效',false:'失效'}" name="probProduct.state"></s:radio>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="" class="col-xs-2 col-sm-2 col-md-2 col-lg-2 control-label"></label>
                                <div class="col-xs-4">
                                    <!-- 保存 -->
                                    <s:if test="user.isHasAnyRole(1,22)">
                                        <s:if test="probProduct.id == 0 || probProduct.id == null">
                                            <button type="button" id="create" style="width: 80px;" class="btn btn-default btn-sm"><s:text name='sys.btn.add' /></button>
                                        </s:if>
                                        <s:else>
                                            <button type="button" id="update" style="width: 80px;" class="btn btn-default btn-sm"><s:text name='sys.btn.update' /></button>
                                        </s:else>
                                    </s:if>
                                    <span style="width:30px;display:inline-block;"></span>
                                    <a href="module/probProduct_list.action" style="width: 80px;" class="btn btn-default btn-sm" ><s:text name='sys.back' /></a>
                                </div>
                            </div>
                        </div>
                    </div>
                </s:form>
            </div>
        </div>
    </div>
</body>
</html>