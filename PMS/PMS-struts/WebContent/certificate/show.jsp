<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="dp" uri="/dp"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script type="text/javascript" src="${pageContext.request.contextPath}/certificate/js/html2canvas.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/certificate/js/canvas2image.js"></script>
    <script>
        $(function() {
        	<%--// 图片过大是会出现加载不完全就渲染的问题
            //convert2canvas("pic", 4);
            --%>
            // 图片加载完成后进行转化
            imgLoad($("#bxk")[0], function() {
                convert2canvas("pic", 4);
            });
            function imgLoad(img, callback) {
                var timer = setInterval(function() {
                    if (img.complete) {
                        clearInterval(timer)
                        callback(img)
                    }
                }, 50)
            }
            /* html2canvas($("#pic")[0], {
                    height: $("#pic").outerHeight(),
                }).then(function (canvas) {
                    var url = canvas.toDataURL();
                    //以下代码为下载此图片功能
                    var triggerDownload = $("<a>").attr("href", url).attr("download", "合格证保存.png").appendTo("body");
                    triggerDownload[0].click();
                    triggerDownload.remove();
                }); */
        });
        
        function convert2canvas(elmId, scale) {
            var cntElem = $('#' + elmId)[0];
            var shareContent = cntElem;//需要截图的包裹的（原生的）DOM 对象
            var width = shareContent.offsetWidth; //获取dom 宽度
            var height = shareContent.offsetHeight; //获取dom 高度
            var canvas = document.createElement("canvas"); //创建一个canvas节点
            //var scale = 4; //定义任意放大倍数 支持小数
            canvas.id=new Date().getTime();
            canvas.width = width * scale; //定义canvas 宽度 * 缩放
            canvas.height = height * scale; //定义canvas高度 *缩放
            canvas.getContext("2d").scale(scale, scale); //获取context,设置scale
            var opts = {
                scale: scale, // 添加的scale 参数
                canvas: canvas, //自定义 canvas
                // logging: true, //日志开关，便于查看html2canvas的内部执行流程
                width: width, //dom 原始宽度
                height: height,
                useCORS: true // 【重要】开启跨域配置
            };
            try {
            	html2canvas(shareContent, opts).then(function (canvas) {
                    var context = canvas.getContext('2d');
                    // 【重要】关闭抗锯齿
                    context.mozImageSmoothingEnabled = false;
                    context.webkitImageSmoothingEnabled = false;
                    context.msImageSmoothingEnabled = false;
                    context.imageSmoothingEnabled = false;

                    // 【重要】默认转化的格式为png,也可设置为其他格式
                    var img = Canvas2Image.convertToPNG(canvas, canvas.width, canvas.height);
                    img.id = 'printImg';
                    //document.body.appendChild(img);

                    $(img).css({
                        "width": canvas.width / scale + "px",
                        "height": canvas.height / scale + "px",
                    }).addClass('f-full');
                    //$("#" + elmId).hide();
                    $("#" + elmId).html(img);
                });
            } catch(e) {
            	$("#" + elmId).text("该浏览器版本不支持合格证打印，建议使用谷歌浏览器！").css("color", "red");
            	//$("#" + elmId).remove();
            	//alert("该浏览器版本不支持合格证打印，建议使用谷歌浏览器！");
            }
        }

    </script>
    <style>
        html {
            font-size: 12px;
            font-family: "SimSun" !important;
        }
        html,body {
            margin: 0;
            padding: 0;
        }
        #pic {
            opacity: 1;
        }
        #yz:after{
            content: attr(data-qc);
            position: absolute;
            bottom: 0.07em;
            left: calc(50% - 0.55em);
            font-size: 5.4mm;
            transform: rotateZ(1.5deg);
            color: #27457f;
            font-weight: bolder;
            z-index: 99;
            background: transparent;
            line-height: normal;
            font-family: "SimSun" !important;
        }
        #hgz {
            height: 78mm;
        }
        #yz {
            position: absolute;
            width: 17mm;
            height: 17mm;
            left: 25mm;
            z-index: 999;
            top: 28mm;
           /*  background-image: url(../images/certificate/yz_1.png); */
            background-size: contain;
            background-repeat: no-repeat;
            font-family: "SimSun" !important;
        }
        
        #yz img{
            width:100%;
            height:100%;
        }
        
        #prdDate {
            /* position: absolute;
            left: 26mm;
            top: 45mm; */
            position: absolute;
            left: 27mm;
            top: 46mm;
            font-size: 0.8em;
            font-family: "SimSun" !important;
        }
        
        #bxk {
            /* height: 210mm; */
            width: 297mm;
        }
    </style>
</head>
<body>
    <s:if test='errmsg == "" && barcode != null'>
        <div id="pic" style="display: inline-block;position:relative;page-break-after:always">
            <%-- <img id="hgz" src="${pageContext.request.contextPath}/certificate/image/hgz.png">
            <div id="yz" data-qc='${results.oqcNo}'>
                <img src="${pageContext.request.contextPath}/certificate/image/yz_1.png">
            </div>
            <span id="prdDate">${results.productionDate}</span> --%>
            
            <img id="bxk" src="${pageContext.request.contextPath}/certificate/image/bxk_300.png">
        </div>
    </s:if>
    <s:else>
        ${errmsg}
    </s:else>
</body>
</html>