<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
        <li class="fa fa-remove"></li>
    </button>
    <!--<h5 class="modal-title"></h5>-->
    <ul class="nav nav-tabs">
        <li class="active"><a href="#tab-content-xml" data-toggle="tab" id="nav-tab-list" aria-expanded="true"><i class="fa fa-file-code-o"></i>&nbsp;xml</a></li>
        <li class=""><a href="#tab-content-edit" data-toggle="tab" id="nav-tab-edit" aria-expanded="false"><i class="fa fa-image"></i>&nbsp;png</a></li>
    </ul>
</div>
<div class="modal-body nav-tabs-custom" style="height: 600px;padding: 0px;">
    <div class="tab-content" style="padding: 0px;">
        <div class="tab-pane ace_editor ace-xcode active" id="tab-content-xml" style="min-height: 600px; border: 1px solid rgb(224, 224, 224); font-size: 14px;">
        </div>
        <div class="tab-pane" id="tab-content-edit">
            <div id="imageContainer" class="text-center" align="center">
                <img src="" style="max-width:100%;">
            </div>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/static/external/lib/ace/noconflict/ace.js"></script>
<script src="${pageContext.request.contextPath}/static/external/lib/ace/noconflict/ext-language_tools.js"></script>
<script>
    var modelId = "0";
    var xmlPath = "definition/xml/${processDefinitionId}.html";
    var imagePath = "definition/png/${processDefinitionId}.html";
    var editor_xml;
    ace.require("ace/ext/language_tools");
    //init ace editor
    editor_xml = ace.edit("tab-content-xml");
    editor_xml.setOptions({
        enableBasicAutocompletion: true,
        enableSnippets: true,
        enableLiveAutocompletion: true
    });

    editor_xml.setTheme("ace/theme/xcode");
    editor_xml.session.setMode("ace/mode/xml");
    editor_xml.setAutoScrollEditorIntoView(true);
    editor_xml.setHighlightActiveLine(false);
    editor_xml.setReadOnly(true);

    editor_xml.renderer.setShowPrintMargin(false);
    editor_xml.setFontSize(14);
    if (xmlPath != 0) {
        $.ajax({
            type: "get",
            url: xmlPath,
            async: false,
            dataType: 'text',
            success: function (xmlContent) {
                //填充xml代码
                editor_xml.setValue(xmlContent);
                editor_xml.clearSelection();
            }
        });
    }
    if (imagePath != 0) {
        $("#imageContainer img").attr("src", imagePath);
    }
</script>