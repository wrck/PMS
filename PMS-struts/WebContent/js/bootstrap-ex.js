/**
 * 本文件主要是对于bootstrap中的js进行扩展
 */

/**
 * 选项卡控制，作用标签<nav class="navbar navbar-default" role="navigation"/>
 * div的class属性为navDiv
 * @param e
 * @param navClass 选项卡对应的div的唯一class
 */
function clickNavLi(e, navClass){
	var navli = "li[name=navli]", navdiv = ".navDiv";
	var index = $(e).index();
//	for(var i = 0;i < $(navli).length;i++){
//		$(navli).eq(i).removeClass("active");
//		$(navdiv).eq(i).hide();
//	}
	$(navli).removeClass("active");
	$(navdiv).hide();
	$(".nav"+e).addClass("active");
	var tabName = $(".nav" + e).data("name");
	if ($("."+tabName).length > 0) {
		$("."+tabName).show();
	} else if ($("."+navClass).length > 0) {
		$("."+navClass).show();
	} else {
		$("#"+navClass).show();
	}
	
}
/**
 * 选项卡切换
 */
function clickSwitchTab(e , navClass , href){
	clickNavLi(e , navClass);
	window.location.href=href;
}

/**
 * JS时间格式化
 * @param format
 * @returns
 */
Date.prototype.format = function(format){ 
	var o = { 
	"M+" : this.getMonth()+1, //month 
	"d+" : this.getDate(), //day 
	"h+" : this.getHours(), //hour 
	"m+" : this.getMinutes(), //minute 
	"s+" : this.getSeconds(), //second 
	"q+" : Math.floor((this.getMonth()+3)/3), //quarter 
	"S" : this.getMilliseconds() //millisecond 
	};

	if(/(y+)/.test(format)) { 
	format = format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
	}

	for(var k in o) { 
		if(new RegExp("("+ k +")").test(format)) { 
		format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length)); 
		} 
	} 
	return format; 
}
//得到当前时间字符串，格式为：YYYY-MM-DD HH:MM:SS  
function CurentTime()  
{   
    var now = new Date();  
         
    var year = now.getFullYear();       //年  
    var month = now.getMonth() + 1;     //月  
    var day = now.getDate();            //日  
         
    var hh = now.getHours();            //时  
    var mm = now.getMinutes();          //分  
    var ss=now.getSeconds();            //秒  
         
    var clock = year + "-";  
         
    if(month < 10) clock += "0";         
    clock += month + "-";  
         
    if(day < 10) clock += "0";   
    clock += day + " ";  
         
    if(hh < 10) clock += "0";  
    clock += hh + ":";  
  
    if (mm < 10) clock += '0';   
    clock += mm+ ":";  
          
    if (ss < 10) clock += '0';   
    clock += ss;  
  
    return(clock);   
}
//得到当前时间字符串，格式为：YYYY-MM-DD  
function CurentDate()  
{   
    var now = new Date();  
         
    var year = now.getFullYear();       //年  
    var month = now.getMonth() + 1;     //月  
    var day = now.getDate();            //日  
         
    var hh = now.getHours();            //时  
    var mm = now.getMinutes();          //分  
    var ss=now.getSeconds();            //秒  
         
    var clock = year + "-";  
         
    if(month < 10) clock += "0";         
    clock += month + "-";  
         
    if(day < 10) clock += "0";   
    clock += day + " ";  
  
    return(clock);   
}

/*
 * 时间控件
 * */

function date_picker(inputId){
 	$("#"+inputId).datepicker({
	      changeMonth: true,
	      changeYear: true,
	    });
	$("#"+inputId).datepicker('option',{dateFormat:"yy-mm-dd"});
	$("#ui-datepicker-div").hide();
}

function date_picker2(inputId){
	
	input = document.getElementById(inputId);
	var v = input.value;
 	$(input).datepicker({
	      changeMonth: true,
	      changeYear: true,
	    });
	$(input).datepicker('option',{dateFormat:"y-mm-dd"});
	$(input).css({"width":"85px"});
	$("#ui-datepicker-div").hide();
	$(input).val(v);
}

function date_picker3(inputId){
	
	input = document.getElementById(inputId);
	var v = input.value;
	$(input).datepicker({
	      changeMonth: true,
	      changeYear: true,
	    });
	$(input).datepicker('option',{dateFormat:"yy-mm-dd"});
	$("#ui-datepicker-div").hide();
	//$(input).val(v);
}


function time_picker(inputId){
	 $("#"+inputId).datepicker({
	      changeMonth: true,
	      changeYear: true
	    });
	$("#"+inputId).datepicker('option',{dateFormat:"yy-mm-dd 23:59:59"});
	$("#ui-datepicker-div").hide();
}

/**
 * 下拉选择多选
 */

function multiselect(selectID, inputID) {
	$("#" + selectID).multiselect({
		header : true,
		height : 200,
		minWidth : 385,
		selectedList : 50,//预设值最多显示10被选中项
		hide : [ "explode", 500 ],
		checkAllText : "全选",
		uncheckAllText : '取消',
		noneSelectedText : '==请选择==',
		close : function() {
			var values = $("#" + selectID).val();
			$("#" + inputID).val(values);
		}
	});
}

function multiselect(selectID, inputID ,width, height) {
	$("#" + selectID).multiselect({
		header : true,
		height : height,
		minWidth : width,
		selectedList : 50,//预设值最多显示10被选中项
		hide : [ "explode", 500 ],
		checkAllText : "全选",
		uncheckAllText : '取消',
		noneSelectedText : '==请选择==',
		close : function() {
			var values = $("#" + selectID).val();
			$("#" + inputID).val(values);
		}
	});
}
/**
 * 下拉默认多选
 */
function installselect(selectID , inputID ,prefix){
	var input_value = $("#"+inputID).val();
	 
	 $("#"+selectID + "  option").each(function(){
		 if(input_value.toString().indexOf(prefix+$(this).val().toString()+prefix)> -1){
			 $(this).attr("selected", "selected"); 
		 }	
	 });
}


function setselect(selectID ,inputID ,prefix){
	
	if(prefix == undefined){
		installselect(selectID , inputID ,"");
	}
	installselect(selectID , inputID ,prefix);
}

/**
 * 模糊搜索
 * @param idjson
 * @param id
 * @returns {Boolean}
 */
function commonfill(idjson, id , usernameArr , realnameArr){
	var obj=document.getElementById(idjson);
	if(obj.value==""){
		document.getElementById(id).value="";
	}
	if(obj.value!=""){
		var i=0;
		for(;i<realnameArr.length;i++){
			if(realnameArr[i]==obj.value){
				break;
			}
		}
		if(i==realnameArr.length){
			return false;
		} else{
			document.getElementById(id).value=usernameArr[i];
		}
	}
}