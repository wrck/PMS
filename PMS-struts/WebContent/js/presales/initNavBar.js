try {
	var scripts = document.getElementsByTagName("script");
	eval(scripts[ scripts.length - 1 ].innerHTML);
	var showNavBarCode = (window.location.hash.match(/navBarCode=([^&]*)/) || [])[1];
	showNavBarCode = showNavBarCode || window.location.hash.replace("#", "");
	showNavBarCode = showNavBarCode || (window.location.search.match(/navBarCode=([^&]*)/) || [])[1];
} catch(e) {
	console.log(e);
}
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
/**
* 需要异步获取的信息
*/
var ajaxNavBar = window.ajaxNavBar || {
    lend2SaleInfo: "借转销信息",
    lend2RmaInfo: "核销信息",
    /*shipmentInfo: {
        name : "发货信息",
        data: {
        	"containRma": true
        },
    },*/
    shipmentInfo: "发货信息",
    maintenanceInfo: {
        name : "维护记录",
        url: "module/sub/maintenance_projectMaintenance.action",
        data: {
        	"projectMaintenance.projectId": window.presalesId,
        	"projectMaintenance.projectType": 20,
            "projectMaintenance.officeCode": window.officeCode,
            "projectMaintenance.hideQuesnaire": true,
            "redirect": encodeURIComponent(window.location.pathname + window.location.search + "#maintenanceInfo")
        },
    },
    tempAuthInfo: {
    	name: "临时授权",
    	data: {
    		"presales.presalesId": window.presalesId,
    		"presales.lendInfoId": window.lendInfoId,
    	},
    	enable: function() {
    		var dataSource = window.presalesSource || "";
    		if (dataSource == 'OA') {
    			return true;
    		}
    		return false;
    	}
    }
};
/**
* 向前合并的navDiv, 指定navName 或者 navCode, 便于快速取值使用对象，判断是否为true
*/
var mergeIntoPrevOption = window.mergeIntoPrevOption || {
    "流程办理": true, // navName
    "lend2RmaInfo": true, // navCode
}
var prevNarBarSelector = window.prevNarBarSelector === undefined ? "#product" : window.prevNarBarSelector;
var enabelNavBar = window.enabelNavBar == false ? false : true;
if (enabelNavBar) {
    $(function(){
    	$('a[href*="history.go(-1)"],a[href*="history.back()"]').each(function() {
    		if(window.history.length == 1) {
    			$(this).attr("href", "javascript:window.close()");
	    	} else {
	    		window.preHisLength = history.length;
    			$(this).attr("href", "javascript:history.go(preHisLength - history.length - 1)");
	    	}
    	})
    	initNavBar(prevNarBarSelector);
    });
    $(document).on('show', ".navDiv", function() {
    	var navCode = $.trim($(this).attr("class").replace(/navDiv|hideDiv/g, ""));
    	var type = navCode.replace("ListDiv", "");
    	//window.location.hash = "#" + type;
    	window.location.replace(getFilterUrl(type));
        if($("." + navCode + " .loading").length != 0) {
        	var callback = null;
        	try {
        		callback = eval(type+"Callback");
        	} catch(e){};
            ajaxLoadInfo(type, callback, ajaxNavBar[type]);
        }
    });
    function ajaxLoadInfo(type, callback, extraData) {
        if (!type) {
            return false;
        }
        var selector = "#" + type + ",#" + type + "ListDiv" + "," + "." + type + "ListDiv";
        if ($(selector).length == 0) {
            $(prevNarBarSelector).after("<div id='wrapper-" + type + "'></div>");
        }
        var presalesCode = $("input[type='hidden'][name='presales.presalesCode']").val();
        var url = (extraData || {}).url || "module/presales_" + type + ".action";
        var formdata = (extraData || {}).data || {"presalesCode":presalesCode};
        formdata.presalesCode = presalesCode;
        $.ajax({
            url: url,
            type: "post",
            dataTpe: "html",
            data: formdata,
            success: function(html) {
                var data = html.substring(html.indexOf("<fieldset>"), html.indexOf("</fieldset>"));
                if (data) {
                    data = data.replace("<fieldset>", "<fieldset class='navDiv hideDiv " + type + "ListDiv'>");
                } else {
                    data = html.substring(html.indexOf("<body>") + 6, html.indexOf("</body>"));
                    data = "<fieldset class='navDiv hideDiv " + type + "ListDiv'>" + data + "</fieldset>";
                }
                
                if ($(selector).length > 0) {
                    //$(selector).replaceWith(data);
                    $(selector).html($(data).html()).find("legend:first").hide();
                    $(selector).find("tr:first:not(.warning)").addClass("warning");
                } else if ($("#wrapper-" + type).length > 0) {
                    $("#wrapper-" + type).replaceWith(data);
                } else {
                    $(prevNarBarSelector).after(data);
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
        while($("#lend2RmaTable tbody tr").not(".even, .odd").length > 1) {
            var prevClass = "." + $("#lend2RmaTable tbody tr").not(".even, .odd").children("td[class^='pc']:first").prop("class");
            $(prevClass).parent().addClass(nth++ % 2 ? "even" : "odd");
        }
    }
    function initNavBar(prevSelector) {
        ajaxNavBar = ajaxNavBar || {};
        for (var type in ajaxNavBar) {
        	var navBar = ajaxNavBar[type] || {};
        	var enable = navBar.enable || true;
        	if (typeof enable == 'function') {
        		enable = enable.call(navBar);
        	}
        	if (!enable) {
        		continue;
        	}
            var navCode = type + "ListDiv";
            var navName = navBar;
            navName = navName.name || navName;
            var selector = "#" + type + ",#" + navCode +",." + navCode;
            if ($(selector).length == 0) {
                var html = '<fieldset class="navDiv hideDiv ' + navCode + '">' +
                                "<legend><b>" + navName + "</b></legend>" + loadingImg + 
                           "</fieldset>";
                var prevType = prevSelector ? prevSelector : ".navDiv:last";
                $(prevType).after(html);
            }
        }
        if($("nav .navbar-nav").length == 0) {
            $("fieldset:first").after(
                '<nav class="navbar navbar-default" role="navigation" style="margin-top: 20px;">' +
                '    <div>' +
                '        <ul class="nav navbar-nav">' +
                '        </ul>' +
                '    </div>' +
                '</nav>');
        } else {
            $("nav .navbar-nav").html("");
        }
        $("fieldset:not(:first):not(.navDiv)").addClass("navDiv hideDiv").each(function(index, item) {
        	$(this).addClass("customNav" + index + "ListDiv");
        });
        var prevNavCode = "";
        var prevNavIndex = 0;
        var showNavIndex = 0;
        showNavBarCode = window.showNavBarCode || "";
        $(".navDiv").each(function(index, item) {
            var navCode = $.trim($(this).attr("class").replace(/navDiv|hideDiv/g, ""));
            var navName = $(this).find("legend:first").text();
            var isShow = showNavBarCode && (showNavBarCode + "ListDiv" == navCode);
            // 判断是否需要合并到前面的navbar控制中
            if (prevNavCode && (mergeIntoPrevOption[navCode] || mergeIntoPrevOption[navName])) {
                $(this).removeClass(navCode).addClass(prevNavCode);
                if (isShow) {
                	showNavIndex = prevNavIndex;
                }
                return true;
            } else if (isShow) {
            	showNavIndex = index;
            }
            prevNavCode = navCode;
            prevNavIndex = index;
            $(this).find("legend:not(:first)").css("marginTop", "20px");// 多个信息在一个Tab中显示时，增加间距
            var navBar = '<li name="navli" class="nav' + index + '"' + ' onclick="clickNavLi(' + index + ", '" + navCode + "')" + '"><a href="javascript:void(0)">' + navName + '</a></li>';
            $("nav .navbar-nav").append(navBar);
            $(this).find("legend:first").hide();
        })
    	$(".nav" + showNavIndex).click();
    }
    function getFilterUrl(hash) {
        return document.location.protocol + '//' + document.location.host + document.location.pathname + document.location.search + '#' + hash;
	}
}