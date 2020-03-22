/**
 * COMMON DHTML FUNCTIONS
 * These are handy functions I use all the time.
 *
 * By Seth Banks (webmaster at subimage dot com)
 * http://www.subimage.com/
 *
 * Up to date code can be found at http://www.subimage.com/dhtml/
 *
 * This code is free for you to use anywhere, just keep this comment block.
 */

/**
 * X-browser event handler attachment and detachment
 * TH: Switched first true to false per http://www.onlinetools.org/articles/unobtrusivejavascript/chapter4.html
 *
 * @argument obj - the object to attach event to
 * @argument evType - name of the event - DONT ADD "on", pass only "mouseover", etc
 * @argument fn - function to call
 */
function addEvent(obj, evType, fn){
 if (obj.addEventListener){
    obj.addEventListener(evType, fn, false);
    return true;
 } else if (obj.attachEvent){
    var r = obj.attachEvent("on"+evType, fn);
    return r;
 } else {
    return false;
 }
}
function removeEvent(obj, evType, fn, useCapture){
  if (obj.removeEventListener){
    obj.removeEventListener(evType, fn, useCapture);
    return true;
  } else if (obj.detachEvent){
    var r = obj.detachEvent("on"+evType, fn);
    return r;
  } else {
    alert("Handler could not be removed");
  }
}

/**
 * Code below taken from - http://www.evolt.org/article/document_body_doctype_switching_and_more/17/30655/
 *
 * Modified 4/22/04 to work with Opera/Moz (by webmaster at subimage dot com)
 *
 * Gets the full width/height because it's different for most browsers.
 */
function getViewportHeight() {
	if (window.innerHeight!=window.undefined) return window.innerHeight;
	if (document.compatMode=='CSS1Compat') return document.documentElement.clientHeight;
	if (document.body) return document.body.clientHeight; 

	return window.undefined; 
}
function getViewportWidth() {
	var offset = 17;
	var width = null;
	if (window.innerWidth!=window.undefined) return window.innerWidth; 
	if (document.compatMode=='CSS1Compat') return document.documentElement.clientWidth; 
	if (document.body) return document.body.clientWidth; 
}

/**
 * Gets the real scroll top
 */
function getScrollTop() {
	if (self.pageYOffset) // all except Explorer
	{
		return self.pageYOffset;
	}
	else if (document.documentElement && document.documentElement.scrollTop)
		// Explorer 6 Strict
	{
		return document.documentElement.scrollTop;
	}
	else if (document.body) // all other Explorers
	{
		return document.body.scrollTop;
	}
}
function getScrollLeft() {
	if (self.pageXOffset) // all except Explorer
	{
		return self.pageXOffset;
	}
	else if (document.documentElement && document.documentElement.scrollLeft)
		// Explorer 6 Strict
	{
		return document.documentElement.scrollLeft;
	}
	else if (document.body) // all other Explorers
	{
		return document.body.scrollLeft;
	}
}

/**
 * 文本框根据输入内容自适应高度 两种方案：  
 * 
 * 方案一：加上以下四个属性，并在页面加载时给指定元素获取焦点，以初始化textarea
 * 	 style="overflow-y:hidden;" 
	 onpropertychange="this.style.height=this.scrollHeight+'px';" 
	 oninput="this.style.height=this.scrollHeight+'px';"
	 onfocus="this.style.height=this.scrollHeight+'px';"
	
  方案二：有些表单不起作用，可能受系统采用bootstrap框架影响
	 
 * @param                {HTMLElement}        输入框元素
 * @param                {Number}                设置光标与输入框保持的距离(默认0)
 * @param                {Number}                设置最大高度(可选)
 */
var autoTextarea = function (elem, extra, maxHeight) {
        extra = extra || 0;
        var isFirefox = !!document.getBoxObjectFor || 'mozInnerScreenX' in window,
        isOpera = !!window.opera && !!window.opera.toString().indexOf('Opera'),
                addEvent = function (type, callback) {
                        elem.addEventListener ?
                                elem.addEventListener(type, callback, false) :
                                elem.attachEvent('on' + type, callback);
                },
                getStyle = elem.currentStyle ? function (name) {
                        var val = elem.currentStyle[name];
 
                        if (name === 'height' && val.search(/px/i) !== 1) {
                                var rect = elem.getBoundingClientRect();
                                return rect.bottom - rect.top -
                                        parseFloat(getStyle('paddingTop')) -
                                        parseFloat(getStyle('paddingBottom')) + 'px';        
                        };
 
                        return val;
                } : function (name) {
                                return getComputedStyle(elem, null)[name];
                },
                minHeight = parseFloat(getStyle('height'));
 
        elem.style.resize = 'none';
 
        var change = function () {
                var scrollTop, height,
                        padding = 0,
                        style = elem.style;
 
                if (elem._length === elem.value.length) return;
                elem._length = elem.value.length;
 
                if (!isFirefox && !isOpera) {
                        padding = parseInt(getStyle('paddingTop')) + parseInt(getStyle('paddingBottom'));
                };
                scrollTop = document.body.scrollTop || document.documentElement.scrollTop;
 
                elem.style.height = minHeight + 'px';
                if (elem.scrollHeight > minHeight) {
                        if (maxHeight && elem.scrollHeight > maxHeight) {
                                height = maxHeight - padding;
                                style.overflowY = 'auto';
                        } else {
                                height = elem.scrollHeight - padding;
                                style.overflowY = 'hidden';
                        };
                        style.height = height + extra + 'px';
                        scrollTop += parseInt(style.height) - elem.currHeight;
                        document.body.scrollTop = scrollTop;
                        document.documentElement.scrollTop = scrollTop;
                        elem.currHeight = parseInt(style.height);
                };
        };
 
        addEvent('propertychange', change);
        addEvent('input', change);
        addEvent('focus', change);
        change();
}



//Select all the checkbox
function checkAll() 
{
    for(var i=0;i<document.getElementsByName("selected").length;i++)
    {
       document.getElementsByName("selected")[i].checked=document.getElementsByName("checkall")[0].checked;
    }
}

//
function checkValue()
{
    var value=new Array();
    var index=0;
    var boxObj=document.getElementsByName("selected");
    for(i=0;i<boxObj.length;i++)
    {
    	if(boxObj[i].checked)
    	 {
    	 	value[index++]=boxObj[i].value;
    	 }
    }
    if(value.length==0)
    {
    	alert("Please select the item!")
    }
    else
    {
    	if(confirm("Comfirm to delete?"))
    	{
    		return value;
    	}
    }
}

/**
 * 根据项目编码查询用户名
 * @param projectCode  项目编码
 * @param callback  回调函数
 * @author linyaoyan
 */
function selectusernamebyprojectcode(projectCode, callback){
	$.ajax({
		url:'selectusernamebyprojectcode.action',
		type:'post',
		dataType:'json',
		data:{projectCode: $("#projectCode").val()},
		async:false,
		success:callback
	});
}

//function solutionopen(){
//	var width = 420, height = 320;
//	var availwidth = screen.availWidth, availheight = screen.availHeight;
//	var url = 'http://' + location.host + "/" + (location.pathname+"").split("\/")[1] + '/solutionlistopen.jsp',
//		name = 'newwindow',
//		param = 'height='+height+',width='+width+',top='+(availheight-height)/2+
//		',left='+(availwidth-width)/2+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no';
//	window.open(url,name,param);
//}
   
//外购服务费用折扣等计算
function calculateoutsourcing(){
	var pd = $("input[name='purchaseDiscount']");
	for(var i = 0;i < pd.length; i++){
		//出货折扣没填就不计算
		if($(pd).eq(i).val() == ""){
			return;
		}
	}
	var outsourcing = $("input.outsourcing");
	var outsourcingsum = parseFloat(0);
	for(var i = 0;i < $(outsourcing).length; i++){
		var osval = $(outsourcing).eq(i).val();
		if(osval == "" || isNaN(osval)){
			osval = 0
		}
		outsourcingsum = outsourcingsum + parseFloat(osval);
	}
	var ppsum = parseFloat($("input[name='purchasePriceSum']").val());
	if(ppsum < parseFloat(outsourcingsum) && ppsum > 0){
		alert("外包费用不能高于公司出货价！");
		for(var i = 0;i < $(outsourcing).length; i++){
			$(outsourcing).eq(i).val(0);
		}
		return;
	}
	
	var costallocation = $("input.costallocation");
	for(var i = 0;i < costallocation.length; i++){
		var pp = parseFloat($("input.purchaseprice").eq(i).val());
		if(ppsum == 0){
			$(costallocation).eq(i).val(0);
		}else{
			$(costallocation).eq(i).val((pp/ppsum*outsourcingsum).toFixed(2));
		}
		var cc = $("input.costallocation").eq(i).val();
		$("input.afterpurchasePrice").eq(i).val((pp - cc).toFixed(2));
		
		var tp = $("input[name='totalPrice']").eq(i).val();
		$("input.afterpurchaseDiscount").eq(i).val(((pp - cc)/tp*100).toFixed(2));
	}
	var ccsum = parseFloat(outsourcingsum).toFixed(2);
	$("input[name='costallocationsum']").val(ccsum);
	$("input[name='afterpurchasePricesum']").val((ppsum - ccsum).toFixed(2));
}