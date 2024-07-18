(function ($) {
      $.each(['show', 'hide'], function (i, ev) {
        var el = $.fn[ev];
        $.fn[ev] = function () {
          this.trigger(ev);
          return el.apply(this, arguments);
        };
      });
})(jQuery);
var loadingImg = 
"<div class='loading' style='height:180px;display:-webkit-flex;display: flex;justify-content:center;align-items:center;'>" +
"    <img src='./images/loading-circle.gif'/>" +
"</div>";
var ajaxNavBar = {
    lend2SaleInfo: "借转销信息",
    lend2RmaInfo: "核销信息",
    shipmentInfo: "发货信息",
    tempAuthInfo: "临时授权"
};

$(function(){
	initNavBar("product");
});
$(document).on('show', ".navDiv", function() {
	var navCode = $.trim($(this).attr("class").replace(/navDiv|hideDiv/g, ""));
    if($("." + navCode + " .loading").length != 0) {
    	var type = navCode.replace("ListDiv", "");
    	var callback = null;
    	try {
    		callback = eval(type+"Callback");
    	} catch(e){};
        ajaxLoadInfo(type, callback);
    }
});
function ajaxLoadInfo(type, callback) {
    if (!type) {
        return false;
    }
    var selector = "#" + type + ",#" + type + "ListDiv" + "," + "." + type + "ListDiv";
    if ($(selector).length == 0) {
        $("#product").after("<div id='wrapper-" + type + "'></div>");
    }
    var presalesCode = $("input[type='hidden'][name='presales.presalesCode']").val();
    $.ajax({
        url:"module/presales_" + type + ".action",
        type:"post",
        dataTpe:"html",
        data:{"presalesCode":presalesCode},
        success: function(data) {
            data = data.substring(data.indexOf("<fieldset>"), data.indexOf("</fieldset>"));
            data = data.replace("<fieldset>", "<fieldset class='navDiv hideDiv " + type + "ListDiv'>");
            if ($(selector).length > 0) {
                //$(selector).replaceWith(data);
                $(selector).html($(data).html());
            } else if ($("#wrapper-" + type).length > 0) {
                $("#wrapper-" + type).replaceWith(data);
            } else {
                $("#product").after(data);
            }
            if (callback) {
                callback();
            }
        },
        //complete: initNavBar
    })
}
function lend2RmaInfoCallback() {
    var nth = 0;
    var trLen = $("#lend2RmaTable tbody tr").removeClass("even odd").not(".even, .odd").length;
    while($("#lend2RmaTable tbody tr").not(".even, .odd").length > 0) {
        var prevClass = "." + $("#lend2RmaTable tbody tr").not(".even, .odd").children("td[class^='pc']:first").prop("class");
        $(prevClass).parent().addClass(nth++ % 2 ? "even" : "odd");
    }
}
function initNavBar(afterTypeId) {
    ajaxNavBar = ajaxNavBar || {};
    for (var type in ajaxNavBar) {
        var navCode = type + "ListDiv";
        var navName = ajaxNavBar[type];
        var selector = "#" + type + ",#" + navCode +",." + navCode;
        if ($(selector).length == 0) {
            var html = '<fieldset class="navDiv hideDiv ' + navCode + '">' +
                            "<legend><b>" + navName + "</b></legend>" + loadingImg + 
                       "</fieldset>";
            var prevType = afterTypeId ? "#" + afterTypeId : ".navDiv:last";
            $(prevType).after(html);
        }
    }
    $("nav .navbar-nav").html("");
    $(".navDiv").each(function(index, item) {
        var navCode = $.trim($(this).attr("class").replace(/navDiv|hideDiv/g, ""));
        var navName = $(this).find("legend").text();
        var navBar = '<li name="navli" class="nav' + index + '"' + ' onclick="clickNavLi(' + index + ", '" + navCode + "')" + '"><a href="javascript:void(0)">' + navName + '</a></li>';
        console.log(navBar);
        $("nav .navbar-nav").append(navBar);
    })
    $(".nav0").click();
}