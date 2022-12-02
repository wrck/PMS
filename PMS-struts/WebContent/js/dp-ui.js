function showPopWin(url, width, height, returnFunc, showCloseBox, title) 
{
    var msgBox = " <div id='dialog' title='"+ title+"'></div>"; 
   /* msgBox += " <iframe id='loadUrl' src=" + url
     + " style=\"width:100%;height:100%;background-color:transparent;\" scrolling=\"auto\" frameborder=\"0\" allowtransparency=\"true\"  "
     + "> </iframe>"; 
    msgBox += "</div>"; */
    if($("#dialog").length) {
    	$('#dialog').dialog('destory');
    	$('#dialog').remove();
    }
    $("body").append(msgBox);
    $('#dialog').dialog(
        {
        	close: returnFunc,
			bgiframe: true,
			modal: true,
			width:width,
			height:height,
			hide: 'slide',
			resizable: false,
			autoOpen:true,
			open:function(event,ui){
				var contextPath = $("base").attr("href") || "";
				if (contextPath) {
					try {
						contextPath = new URL(contextPath).pathname;
					} catch(e) {
						var context = window.location;
						contextPath = contextPath.replace(context.protocol + "//" + context.host, "");
					}
				}
				var loadingImg = "/images/loading-circle.gif";
				contextPath = contextPath || ".";
				loadingImg = (contextPath + "/images/loading-circle.gif").replace("//", "/");
				$(this).html(" <iframe id='" + dialogFrm + "' name='" + dialogFrm + "' src='" + url
				 + "' style=\"width:100%;height:100%;background-color:transparent;\" scrolling=\"auto\" frameborder=\"0\" allowtransparency=\"true\"  "
				 + "> </iframe>"); 
				$("#"+dialogFrm).contents().find("body").append("<div style='height:100%;display:-webkit-flex;display: flex;justify-content:center;align-items:center;'><img src='" + loadingImg + "'/></div>");
			
				// 遮罩层
				//$(this).append("<div id='marklayer'></div>");
				$("#marklayer").dialog({
					bgiframe: true,
					width:'100%',
					height:'100%',
					modal:true,
//					closeOnEscape:false,
					dialogClass: "no-close",
					appendTo:"#dialog",
					//autoOpen: false
				});
//				$("#marklayer").
			},
			beforeClose:function(event,ui){
				// 移除ifrmae，防止关闭时在此加载页面
				$(this).find("iframe").remove();
			}
			/*,close: function (event, ui) {  
				//销毁容器元素，但是初始Div存在  
				$(this).dialog("destroy");  
				// 删除初始Div  
				$(this).remove();  
			}*/
		}
    );
}

function hidePopWin(callReturnFunc) {
	$('#dialog').dialog('close');
}

function popWindow(url, width, height, title, dialogid, modal) 
{
	var dialogEle = "#" + dialogid;
	$(dialogEle).remove();//每次打开新的dialog时先移除掉之前的
	var dialogFrm = "iframe_" + dialogid;
	var dialogFrmEle = "#iframe_" + dialogid;
    var msgBox = " <div id='" + dialogid + "' title='"+ title+"'></div>"; 
   /* msgBox += " <iframe id='" + dialogFrm + "' src='" + url
     + "' style=\"width:100%;height:100%;background-color:transparent;\" scrolling=\"auto\" frameborder=\"0\" allowtransparency=\"true\"  "
     + "> </iframe>"; 
    msgBox += "</div>"; */
    if(!$(dialogEle).length)
    {
        $("body").append(msgBox);
	    $(dialogEle).dialog(
	        {
				bgiframe: true,
				modal: modal,
				width:width,
				height:height,
				hide: 'slide',
				resizable: false,
				autoOpen:true,
				open:function(event,ui){
					var contextPath = $("base").attr("href") || "";
					if (contextPath) {
						try {
							contextPath = new URL(contextPath).pathname;
						} catch(e) {
							var context = window.location;
							contextPath = contextPath.replace(context.protocol + "//" + context.host, "");
						}
					}
					var loadingImg = "/images/loading-triple.gif";
					var loadingImg2 = "/images/loading-jumping.gif";
					contextPath = contextPath || ".";
					loadingImg = (contextPath + loadingImg).replace("//", "/");
					loadingImg2 = (contextPath + loadingImg2).replace("//", "/");
					
					$(this).html(" <iframe id='" + dialogFrm + "' name='" + dialogFrm + "' src='" + url
					 + "' style=\"width:100%;height:100%;background-color:transparent;\" scrolling=\"auto\" frameborder=\"0\" allowtransparency=\"true\"  "
					 + "> </iframe>"); 
//					$("#"+dialogFrm).contents().find("body").append("<div style='height:100%;display:-webkit-flex;display: flex;justify-content:center;align-items:center;'><img src='./images/loading-circle.gif'/></div>");
					
					// 遮罩层
					$(this).append("<div id='marklayer'><div style='display: flex;display:-webkit-flex;display:-ms-flexbox;display:-moz-box;flex-direction:column;height:100%;justify-content:center;align-items:center;'><img src='" + loadingImg + "' style='margin-left:-18px;'/><img src='" + loadingImg2 + "'/></div></div>");
					$("#marklayer").dialog({
						bgiframe: true,
						top:'0px',
						width:'100%',
						height:height,
						modal:true,
						appendTo:"#"+dialogid,
						resizable: false,
					});
					$("#marklayer").prev().remove();
					$("#marklayer").parent().css({'top':0,'left':0}).removeClass('ui-widget-content');
					t = setInterval(function(){
						if($("#"+dialogFrm).contents().find("body").children().length>0){
							$("#marklayer").dialog('close');
							clearInterval(t);
						}
					}, 200);
				},
				beforeClose:function(event,ui){
					// 移除ifrmae，防止关闭时在此加载页面
					$(this).find("iframe").remove();
				},
				close: function (event, ui) {  
					//销毁容器元素，但是初始Div存在  
					$(this).dialog("destroy");  
					// 删除初始Div  
					$(this).remove();  
				}

			}
    	);
    }
    else
    {
		$(dialogFrmEle).attr({src: url});
    	$(dialogEle).dialog('open');
    	$(dialogEle).dialog('moveToTop'); 
    }
}

function closeWindow(dialogid) 
{
	var dialogEle = "#" + dialogid;
    $(dialogEle).dialog('close');
}
