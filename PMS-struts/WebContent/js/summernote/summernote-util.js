 'use strict';
 function saveImageUpload(files, _this) {  
 	var filename = false;  
 	var file = files[0];
     try{  
     	filename = file['name'];  
     } catch(e){  
         filename = false;  
     }  
     if(!filename){  
         $(".note-alarm").remove();  
     }  
     //以上防止在图片在编辑器内拖拽引发第二次上传导致的提示错误  
     var data = new FormData();  
     data.append("upload", file);  
     data.append("uploadFileName",filename); //唯一性参数  
   
     $.ajax({  
	        data: data,  
	        type: "POST",  
	        url: "uploadImage.action",  
	        cache: false,  
	        contentType: false,  
	        processData: false,  
	        success: function(data) {  
	            if(data){  
	                var files = data.message.split(";");
	                for(var i in files){
	                	$(_this).summernote('insertImage',files[i],'img');
	                }
	                alert("上传成功！");  
	            }else{
	            	alert("上传失败！");  
	            	return;
	            }
	            //alert(url);  
	        //setTimeout(function(){$(".note-alarm").remove();},3000);  
	        },  
	        error:function(){  
	            alert("上传失败！");  
	            return;  
	            //setTimeout(function(){$(".note-alarm").remove();},3000);  
         }  
   	});  
}