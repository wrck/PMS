/**
 * 菜单展开、导航栏以及滚动条
 */
//$("body .wrapper .content-wrapper").css("minHeight",$(window).height() - $("header.main-header").height() - $("footer.main-footer").outerHeight());
// 默认展开至二级菜单
function menuInit2() {
	$(".treeview").addClass("active");
	$(".treeview>.treeview-menu").addClass("active");
	var href = window.location.href;
	$(".treeview-menu a").each(function(){
		var url = $(this).attr("href");
		url = url.replace(".html","");
		if(url && (href.indexOf(url+".")>-1 || href.indexOf(url+"/")>-1)){
			// 展开选中的菜单
			$(this).parents("li").addClass("active");
			// 导航栏标题
	        var title = $(this).text().trim();
			// 绘制导航栏
			var breadcrumb = $(".breadcrumb");
			$(".breadcrumb").prev().text(title);
			if(breadcrumb){
				var html = "<li><a href='"+ basePath +"/success.html'><i class='fa fa-dashboard'></i>首页</a></li>";
				//var crumbs= $(".sidebar-menu .active>a");
				var crumbs= $(".active>a, a:first",$(this).parents(".treeview"));
				for(var i=0; i<crumbs.length; i++){
					crumb = crumbs[i];
					var li = "<li>";
					if(i == crumbs.length-1){
						li = "<li  class='active'>";
					}
					html+=li+"<a href='"+$(crumb).attr("href")+"'>"+$(crumb).find(".menu-name").text().trim()+"</a></li>"
				}
				breadcrumb.html(html);
			}
		}
	})
}
function menuInit(ignore) {
	$(".treeview").addClass("active");
	$(".treeview>.treeview-menu").addClass("active");
	var href = window.location.href;
	var menuName = $(".content-header>h1").text();
	if (ignore) {
		href = href.split("?")[0];
	}
	var isFind = false;
	$(".treeview-menu a").each(function(){
		var url = $(this).attr("href");
		var aName = $(this).text().trim();
		if (ignore) {
			url = url.split("?")[0];
			url = url.replace(".html","");
		}
		if(url && (href.indexOf(url)>-1 || href.indexOf(url+"/")>-1) || menuName.indexOf(aName)>-1){
			isFind = true;
			// 展开选中的菜单
			$(this).parents("li").addClass("active");
			// 导航栏标题
	        var title = $(this).text().trim();
			// 绘制导航栏
			var breadcrumb = $(".breadcrumb");
			$(".breadcrumb").prev().text(title);
			if(breadcrumb){
				var html = "<li><a href='"+ basePath +"/sys/success.html'><i class='fa fa-dashboard'></i>首页</a></li>";
				//var crumbs= $(".sidebar-menu .active>a");
				var crumbs= $(".active>a, a:first",$(this).parents(".treeview"));
				for(var i=0; i<crumbs.length; i++){
					crumb = crumbs[i];
					var li = "<li>";
					if(i == crumbs.length-1){
						li = "<li  class='active'>";
					}
					html+=li+"<a href='"+$(crumb).attr("href")+"'>"+$(crumb).find(".menu-name").text().trim()+"</a></li>"
				}
				breadcrumb.html(html);
			}
		}
	})
	if (!isFind) {
		menuInit(true);
	}
}
menuInit();

$(function() {
	fitSideBarScroll();
	fitWrapperScroll();
	fitMenuScroll();
	/*var t;
	$(window).resize(function(){
		if (t) {
			clearTimeout(t);
		}
		t = setTimeout(function() {
			fitWrapperScroll();
			fitMenuScroll();
			t = undefined;
		}, 500);
	});*/
	$(document).on('click','a[data-toggle="control-sidebar"]',function(){
		if($(".control-sidebar.control-sidebar-open").length > 0){
			fitSideBarScroll();
		}
	});
	
	$("#changeCompanyBtn").click(function(e){
		e.stopPropagation();
		$(this).parent().addClass("open");
	});
	
	$(".changeCompanyLink").click(function() {
		var compId = $(this).children().data("value");
		ajaxPost(basePath + "/sys/changeCompany.json", {compId: compId}, function(data) {
	    	if(data.status) {
	    		if (typeof modals != 'undefined') {
	    			modals.correct(data.message);
	    		}
				window.location.href = basePath + "/sys/success.html";
//	    		window.location.reload(true);
			} else {
				if (typeof modals != 'undefined') {
	    			modals.error(data.message);
	    		}
			}
		})
	})
})

function fitMenuScroll() {
	if($.fn.slimScroll){
		$("body aside.main-sidebar .sidebar").slimScroll({destroy:true});
		$("body aside.main-sidebar .sidebar").height("");
		var menuHeight = $("body aside.main-sidebar .sidebar").height();
		var windowHeight = $(window).height() - 50;
		var contentWrapperHeight = $(".content-wrapper").height() + 48;
		if (menuHeight <= windowHeight || menuHeight <= contentWrapperHeight) {
			return;
		}
		var height = windowHeight >= contentWrapperHeight ? "calc(100vh - 50px)" : contentWrapperHeight + "px";
		var color = $(".main-header .navbar").css("backgroundColor");
	    //$("body aside.main-sidebar .sidebar").slimScroll({destroy:true});
	    $("body aside.main-sidebar .sidebar").slimScroll({
            //height: "calc(100vh - 50px)",
	    	height: height,
            size: 8,
            disableFadeOut: true,
            allowPageScroll: true,
            color: color,
        }).css("paddingBottom",0).next().css("display","none");
	}
}
function fitWrapperScroll(){
	if($.fn.slimScroll){
		//var windowHeight = $(window).height();
		var windowHeight = $(window).height();
		var headerHeight = $("header.main-header").height();
		var footerHeight = $("footer.main-footer").outerHeight();
		$(".content-wrapper").css("minHeight","calc(100vh - " + headerHeight + "px - " + footerHeight + "px)").height();
		$("body .wrapper").slimScroll({destroy:true});
		$("body .wrapper").slimScroll({
			height: "100vh",
			size: 8,
			disableFadeOut: true,
			allowPageScroll: true,
			//railVisible: true
		}).next().css("display","none");
	}
}

function fitSideBarScroll(){
	if($.fn.slimScroll){
		var windowHeight = $(window).height();
		var headerHeight = $("header.main-header").height();
		var footerHeight = $("footer.main-footer").outerHeight();
		var contentWrapperHeight = $(".content-wrapper").css("minHeight","calc(100vh - " + headerHeight + "px - " + footerHeight + "px)").height();
		var contentWrapperHeight = $(".content-wrapper").height();
		
		var controlSidebarHeight = $(".control-sidebar").height();
		var controlSidebarPaddingTop = $(".control-sidebar").css("paddingTop").replace("px",'');
		var controlSidebarTabsHeight = $(".control-sidebar-tabs").height();

		var viewHeight = windowHeight - headerHeight < contentWrapperHeight?windowHeight - headerHeight - controlSidebarTabsHeight :contentWrapperHeight - controlSidebarTabsHeight;
		//if( controlSidebarHeight > viewHeight){
			$(".control-sidebar .tab-content").slimScroll({destroy:true});
			$(".control-sidebar .tab-content").slimScroll({
				height: viewHeight - 20,
				size: 8,
				disableFadeOut: true,
				allowPageScroll: true,
				//railVisible: true
			}).parent().css("padding","10px 15px").css("height",viewHeight);
		//}
	}
}

// 监听页面容器的宽度变化，调整容器大小和菜单栏	
var EleResize = {
    _handleResize: function (e) {
        var ele = e.target || e.srcElement;
        var trigger = ele.__resizeTrigger__;
        if (trigger) {
            var handlers = trigger.__z_resizeListeners;
            if (handlers) {
                var size = handlers.length;
                for (var i = 0; i < size; i++) {
                    var h = handlers[i];
                    var handler = h.handler;
                    var context = h.context;
                    handler.apply(context, [e]);
                }
            }
        }
    },
    _removeHandler: function (ele, handler, context) {
        var handlers = ele.__z_resizeListeners;
        if (handlers) {
            var size = handlers.length;
            for (var i = 0; i < size; i++) {
                var h = handlers[i];
                if (h.handler === handler && h.context === context) {
                    handlers.splice(i, 1);
                    return;
                }
            }
        }
    },
    _createResizeTrigger: function (ele) {
        var obj = document.createElement('object');
        obj.setAttribute('style',
            'display: block; position: absolute; top: 0; left: 0; height: 100%; width: 100%; overflow: hidden;opacity: 0; pointer-events: none; z-index: -1;');
        obj.onload = EleResize._handleObjectLoad;
        obj.type = 'text/html';
        ele.appendChild(obj);
        obj.data = 'about:blank';
        return obj;
    },
    _handleObjectLoad: function (evt) {
        this.contentDocument.defaultView.__resizeTrigger__ = this.__resizeElement__;
        this.contentDocument.defaultView.addEventListener('resize', EleResize._handleResize);
    }
};
if (document.attachEvent) {//ie9-10
    EleResize.on = function (ele, handler, context) {
        var handlers = ele.__z_resizeListeners;
        if (!handlers) {
            handlers = [];
            ele.__z_resizeListeners = handlers;
            ele.__resizeTrigger__ = ele;
            ele.attachEvent('onresize', EleResize._handleResize);
        }
        handlers.push({
            handler: handler,
            context: context
        });
    };
    EleResize.off = function (ele, handler, context) {
        var handlers = ele.__z_resizeListeners;
        if (handlers) {
            EleResize._removeHandler(ele, handler, context);
            if (handlers.length === 0) {
                ele.detachEvent('onresize', EleResize._handleResize);
                delete  ele.__z_resizeListeners;
            }
        }
    }
} else {
    EleResize.on = function (ele, handler, context) {
        var handlers = ele.__z_resizeListeners;
        if (!handlers) {
            handlers = [];
            ele.__z_resizeListeners = handlers;

            if (getComputedStyle(ele, null).position === 'static') {
                ele.style.position = 'relative';
            }
            var obj = EleResize._createResizeTrigger(ele);
            ele.__resizeTrigger__ = obj;
            obj.__resizeElement__ = ele;
        }
        handlers.push({
            handler: handler,
            context: context
        });
    };
    EleResize.off = function (ele, handler, context) {
        var handlers = ele.__z_resizeListeners;
        if (handlers) {
            EleResize._removeHandler(ele, handler, context);
            if (handlers.length === 0) {
                var trigger = ele.__resizeTrigger__;
                if (trigger) {
                    trigger.contentDocument.defaultView.removeEventListener('resize', EleResize._handleResize);
                    ele.removeChild(trigger);
                    delete ele.__resizeTrigger__;
                }
                delete  ele.__z_resizeListeners;
            }
        }
    }
}

EleResize.on($(".content-wrapper")[0], function() {
	//fitWrapperScroll();
	fitMenuScroll();
});