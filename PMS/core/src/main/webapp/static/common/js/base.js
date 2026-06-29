/**
 * Created by HANZO on 2016/6/17.
 */

function ajaxPost(url , params, callbacks , async, contentType, complete){
	return ajax(url ,'post', params, callbacks , async, contentType, complete)
}

function ajaxGet(url , params, callbacks , async, contentType, complete){
	return ajax(url ,'get', params, callbacks , async, contentType, complete)
}


function ajax(url ,method, params, callbacks , async, contentType, complete) {
	var result = null;
	var headers = {};
	if(method != 'get'){
		try{
			headers['__RequestVerificationToken'] = __RequestVerificationToken;
		}catch(e){}
	}
    if (contentType) {
    	headers['Content-Type'] = contentType;
    }
    var async = async == false ? async : true;
    var index2;
	$.ajax({
		type : method,
		async : async,
		url : url,
		data : params,
		dataType : 'json',
		headers: headers,
		beforeSend:function(){
//			index2 = layer.load(1);
		},
		success : function(data, status) {
			result = data;
			if(data&&data.code&&data.code=='101'){
				modals.error("操作失败，请刷新重试，具体错误："+data.message);
				return false;
			}
			if (callbacks) { 
				if (typeof callbacks == "function") {
					callbacks.call(this, data, status);
				} else if (typeof callbacks == "object") {
					for (var i in callbacks) {
						var callback = callbacks[i];
						callback.call(this, data, status);
					}
				}
/*				try {
					callbacks.call(this, data, status);
				} catch (e) {
					for (var i in callbacks) {
						var callback = callbacks[i];
						callback.call(this, data, status);
					}
				}
*/			}
		},
		error : function(err, err1, err2) {
			if(err.responseText.indexOf(" | Log in</title>") > -1){
				window.location.href= basePath + "/login.html";
				return false;
			}
			if(err.responseText.indexOf(" | Unauthorized</title>") > -1){
				window.location.href= basePath + "/unauthorized.html";
				return false;
			}
			var text = JSON.stringify(err).replace(/(\\n)/g,"&#10").replace(/(\\t)/g,"&#09") + '<br/>textStatus:' + JSON.stringify(err1) + '<br/>errorThrown:' + JSON.stringify(err2);
			if(err.responseText.indexOf("errorLogId") > -1) {
				try {
					var errorObj = JSON.parse(err.responseText);
					text = "<h4 class='text-danger'><i class='fa fa-warning'></i>出错啦！</h4><p>很抱歉，服务器偷了会懒，发生了错误。<br>错误信息："+errorObj.error+"<br>如有疑问请携带ID：<span class='text-red'>"+errorObj.errorLogId+"</span>联系管理员。</p>";
					modals.error({
						text : text,
					});
					return false;
				} catch(e) {
				}
			}
			modals.error({
				text : text,
				large : true
			});
		}, 
		complete : function (data, status) {
			if (complete) { 
				complete.call(this, data, status);
			}
//			layer.close(index2);  
		}
	});

	return result;
}

function getServerTime(base_path, format) {
	var result = null;
	var sdate = new Date(ajaxPost(base_path+'/base/getServerTime', null, null, false));
	if (sdate != 'Invalid Date') {
		result = formatDate(sdate, format||'yyyy-MM-dd');
	}

	return result;
}

/**
 * 格式化日期
 */
function formatDate(date, format) {
    if (!date)
        return date;
    var newDate = !isNaN(date) ? new Date(Number(date)) : new Date(date);
    date = !isNaN(newDate.getTime()) ? newDate : date;
    try {
        return date.Format(format);
    } catch (e) {
        return date;
    }
}

Date.prototype.Format = function(fmt) {
	var o = {
		"M+" : this.getMonth() + 1, // 月份
		"d+" : this.getDate(), // 日
		"H+" : this.getHours(), // 小时
		"m+" : this.getMinutes(), // 分
		"s+" : this.getSeconds(), // 秒
		"q+" : Math.floor((this.getMonth() + 3) / 3), // 季度
		"S" : this.getMilliseconds()
	// 毫秒
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}

/**
 * 将map类型[name,value]的数据转化为对象类型
 */
function getObjectFromMap(aData) {
	var map = {};
	for (var i = 0; i < aData.length; i++) {
		var item = aData[i];
		if (!map[item.name]) {
			map[item.name] = item.value;
		}
	}
	return map;
}

/**
 * 获取render,并转化为对象数组
 */
function getRenderObject(render) {
	var arr = render.split(",");
	var obj = new Object();
	for (var i = 0; i < arr.length; i++) {
		var strA = arr[i].split("=");
		obj[strA[0]] = strA[1];
	}
	if (!obj.type)
		obj.type = "eq";
	return obj;
}

/**
 * 获取下一个编码 000001，000001000006，6
 * 得到结果 000001000007
 */
function getNextCode(prefix,maxCode,length){
	if(maxCode==null){
		var str="";
		for(var i=0;i<length-1;i++){
			str+="0";
		}
		return prefix+str+1;
	}else{
		var str="";
		var sno = parseInt(maxCode.substring(prefix.length))+1;
		for(var i=0;i<length-sno.toString().length;i++){
			str+="0";
		}
		return prefix+str+sno;
	}
	
}

//获取布尔值
/*String.prototype.BoolValue=function(){
	if(this==undefined)
		return false;
	if(this=="false"||this=="0")
		return false;
	return true;
}*/

/**
 * 数据 生效 失效状态统一处理函数
 */
function initStateName (data, type, row){
	if(data == 1){
		return "生效";
	}else{
		return "失效";
	}
}
const fileTypes = {
	image: ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp', 'svg'],
	video: ['mp4', 'webm', 'ogg', 'mov', 'avi'],
	audio: ['mp3', 'wav', 'ogg', 'aac'],
	pdf: ['pdf'],
	text: ['txt', 'csv', 'json'],
	office: ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx']
};
function filePreviewRender(data, type, row, options) {
	try {
		data = data || row.name || row.path || '';
		var {  filePath, fileName, fileExt } = getFileExt(row.path || data);
		if (fileExt == 'unknow' || !filePath) {
			return data;
		}
		fileName = data || fileName;
		var config = { filePath, fileName: data || fileName, fileExt };
		var html = `<i class="mr-05 fa fw fa-eye file-previwer" onclick="previewFile(this)" data-file-ext="${fileExt}" data-file-name="${fileName}" data-file-path="${filePath}"></i>` + data;
		return html;
	} catch (e) {
	}
	return data;
}
/**
 * 从 URL 提取文件名和扩展名
 * @param url
 * @returns { filename, ext }
 */
function getFileExt(url) {
	url = url || "";
	url = url.replaceAll("\\", "/");
	var pathname = url || "";
	try {
		pathname = new URL(pathname).pathname; // 获取路径部分
	} catch (e) {
	}
	
	const fileName = pathname.split('/').pop(); // 获取最后一部分
	const fileExt = fileName.split('.').pop().toLowerCase();
	return { filePath:pathname, fileName, fileExt };
}

//判断文件类型
function getFileCategory(filename) {
	const fileExt = getFileExt(filename);
	for (const [type, exts] of Object.entries(fileTypes)) {
	  if (exts.includes(fileExt)) return type;
	}
	return 'unknown';
}

/**
 * 根据类型生成预览 HTML
 * @param url
 * @returns
 */
/**
 * 根据类型生成预览 HTML
 * @param url
 * @returns
 */
function previewFile(_this, config) {
    config = config || $(_this).data();
    if (typeof config == 'string') {
        config = JSON.parse(config) || $(_this).data();
    }
    var {filePath, fileName, fileExt} = getFileExt(config.filePath);
    fileName = config.fileName || fileName;
    const preview = {};

    const url = (!filePath.startsWith(basePath) ? basePath : "") + "/" + filePath;

    // 清空预览区
    preview.innerHTML = '<div class="text-center file-previwer-conatiner">';

    // 判断类型并预览
    if (fileTypes.image.includes(fileExt)) {
        preview.innerHTML += `<img src="${url}" alt="${fileName}" style="max-width:100%; height:100%"></div>`;
    } else if (fileTypes.video.includes(fileExt)) {
        preview.innerHTML += `
	      <video controls style="max-width:100%; height:100%">
	        <source src="${url}" type="video/${ext}">
	        您的浏览器不支持该视频格式。
	      </video>`;
    } else if (fileTypes.audio.includes(fileExt)) {
        preview.innerHTML += `
	      <audio controls>
	        <source src="${url}" type="audio/${ext}">
	        您的浏览器不支持该音频格式。
	      </audio>
	      <p>🎵 ${fileName}</p>`;
    } else if (fileTypes.pdf.includes(fileExt)) {
        preview.innerHTML += `<iframe src="${url}" width="100%" height="100%"></iframe>`;
    } else if (fileTypes.text.includes(fileExt)) {
        // 动态加载文本内容
        fetch(url).then(res=>res.text()).then(text=>{
            preview.innerHTML += `<pre>${text.slice(0, 1000)}${text.length > 1000 ? '...' : ''}</pre>`;
        }).catch(()=>{
            preview.innerHTML += `<p>📄 ${fileName}（无法加载内容）</p>`;
        });
    } else if (fileTypes.office.includes(fileExt)) {
        preview.innerHTML += `
	      <p>📎 ${fileName}</p>
	      <p>Office 文件无法直接在浏览器中预览，建议下载或使用在线服务（如 Google Docs、Office Online）。</p>
	      <a href="${url}" target="_blank">点击下载</a>`;
    } else {
        preview.innerHTML += `
	      <p>📦 未知文件类型: ${fileName}</p>
	      <a href="${url}" target="_blank">点击下载</a>`;
    }
    preview.innerHTML += "</div>";
    var $innerHtml = $(preview.innerHTML);
    if($innerHtml.find("iframe").length > 0) {
    	$innerHtml.css("height", "100%");
    }
    modals.popup({
    	winId: 'previewFileWin',
    	title: fileName,
        text: $innerHtml[0].outerHTML,
        width: "75vw",
        height: "90vh"
    });
}

function userNameRender(data, type, row, options) {
	try {
		var colIdx = options.col;
		var rowIdx = options.row;
		var settings = options.settings;
		var column = settings.aoColumns[colIdx];
		var columnBy = column.data;
		var columnName = columnBy.replace("By", "Name");
		var columnNameValue = row[columnName] || (row.customInfo || {})[columnName] || "";
		if (columnNameValue) {
			return [data, columnNameValue].join("-");
		}
	} catch (e) {
		console.error(e);
	}
	return data;
}

/**
 * 表格信息过长处理
 */
function handleLongText(data,length){
	if(data.length > length){
		var span = "<span title='"+data+"'>"+data.substr(0, length)+"...</span>";
		return span;
	}else{
		return data;
	}
}

String.prototype.charLength = function() {
	var intLength = 0
	for (var i = 0; i < this.length; i++) {
		if ((this.charCodeAt(i) < 0) || (this.charCodeAt(i) > 255)) {
			intLength = intLength + 2
		} else {
			intLength = intLength + 1
		}
	}
	return intLength;
}

if (typeof String.prototype.startsWith != 'function') {
	String.prototype.startsWith = function(prefix) {
		if (typeof prefix === "undefined" || prefix === null) {
			return false;
		}
		return this.slice(0, prefix.length) === prefix;
	};
}



function simpleAjaxUploadFile(JqFile,callback){
	ajaxUploadFile(JqFile[0] ,JqFile.attr('uploadUrl'),JqFile.attr('allowType'),callback)
}


//AJAX文件上传 
function ajaxUploadFile(eleFile ,uploadUrl,allowType ,callback){
	// 利用formdata进行文件的ajax上传
	var myform = new FormData();
	var files = eleFile.files;
	if(files.length == 0){
		modals.info('请选择要上传的文件')
		return ;
	}
	for(var k=0,l=files.length;k<l;k++){
		var file = files[k];
		
		if(file == undefined){//文件未定义
			return;
		}
		if(!checkFileType(file.name ,allowType)){
			return;
		}
		myform.append('myFile',file);
	}	
	ajaxupload(myform,uploadUrl,callback);
}

//检查发票图片类型
function checkFileType(fileName ,allowType){
	if(allowType == null){//不做限制
		return true;
	}
	var suffix = fileName.substr(fileName.lastIndexOf(".")+1);
	suffix = suffix.toLowerCase(); 
	if(allowType.indexOf(suffix) == -1){
		modals.info('不允许上传类型为【.'+suffix+'】的文件，请上传类型为【'+allowType+'】的文件');
		return false;
	}
	return true;
}

//执行上传
function ajaxupload(myform ,uploadUrl, callback){
	var headers = {};
    headers['__RequestVerificationToken'] = __RequestVerificationToken;
    var loading;
	$.ajax({
		url: uploadUrl,// 上传URL
		type: "POST",
		data: myform,
		mimeType: "multipart/form-data",
		headers:headers,
		contentType: false,
		cache: false,
		processData: false,
		beforeSend:function(){
			loading = layer.load(2);
		},
		success:callback,
		complete:function(){
			layer.close(loading); 
		},
		error:function(){
			layer.close(loading); 
		}
	});
}

/**
 * 可解决iframe页面a标签无法下载的问题。
 * @param url
 * @param saveName
 * @returns
 */
function getDownload(url, saveName)
{
	if(typeof url == 'object' && url instanceof Blob)
	{
		url = URL.createObjectURL(url); // 创建blob地址
	}
	var aLink = top.window.document.createElement('a');
	aLink.href = url;
	aLink.download = saveName || ''; // HTML5新增的属性，指定保存文件名，可以不要后缀，注意，file:///模式下不会生效
	var event;
	if(window.MouseEvent) event = new MouseEvent('click');
	else
	{
		event = top.window.document.createEvent('MouseEvents');
		event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
	}
	aLink.dispatchEvent(event);
}

/**
 * post方式下载文件
 * @param url
 * @param saveName
 * @returns
 */
function postDownload(url, formData) {
	var $target = $((window.event || {}).target);
	$target.button("loading");
	if(typeof url == 'object' && url instanceof Blob) {
		url = URL.createObjectURL(url); // 创建blob地址
	}
	var timestamep = new Date().getTime();
	var iframeName = "downloadFrame" + timestamep;
	var fromName = "downloadForm" + timestamep;
	var $iframe = $('<iframe name="' + iframeName + '" style="display: none;" frameborder="0" />'); 
	var $form = $('<form name="' + fromName + '" style="display: none;" method="post"/>');
	// 添加iframe加载完成事件
	var iframe = $iframe[0];
	if (iframe.attachEvent){
		iframe.attachEvent("onload", function(){
			setTimeout(function() {
				$form.remove();
				$target.button("reset");
			}, 60000);
		});
	} else {
		iframe.onload = function(){
			setTimeout(function() {
				$form.remove();
				$target.button("reset");
			}, 60000);
		};
	}
	$form.append($iframe);  //addxxformfx：form的id
	$form.attr("action", url);//要提交到的action
	$form.attr("target", iframeName);//downloadFrame，指向上面iframe的名字
	
	// 提交的数据
	formData = formData || {};
	// 添加Token
	formData['__RequestVerificationToken'] = __RequestVerificationToken;
	for (var name in formData) {
		var value = formData[name];
		if (value === undefined) {
			continue;
		}
		$form.append("<input type='hidden' name='" + name + "' value='" + value + "' />");
	}
	$("body").append($form);
	$form.submit();
}