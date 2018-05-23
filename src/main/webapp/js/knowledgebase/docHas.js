var loadview = function(){
	autoIndexHeight();
	// 全局变量
	APP_PATH = document.getElementById('App_PATH').value;
	docuuid = document.getElementById('docuuid').value;
	// 搜索类型
	var testEditormdView2;
	getArt(docuuid);
	//viewByRoleNameAndUserId();
	viewByDocIdAndUserId();
   }

function copyurl(){
	var Url2=document.getElementById("hascopyurl");
	Url2.select(); // 选择对象
	//Url2.createTextRange();
	document.execCommand("Copy"); // 执行浏览器复制命令
	//document.execCommand("Copy");  
	alert("已复制好，可贴粘。");
} 

var viewByDocIdAndUserId = function(){
	$.ajax({
		url:APP_PATH+"/markdownController/viewByDocIdAndUserId",
		type:"POST",
		async : false,  
		//contentType:'application/json;charset=UTF-8',  
		data:{uuid:docuuid},
		success:function(result){
			var ops = result.map;
			document.getElementById("editdoc2").style.display = "none";
			document.getElementById("deldoc2").style.display = "none";
			document.getElementById("downdoc2").style.display = "none";
			document.getElementById("copyurldoc2").style.display = "none";
			for(var key in ops){
				if(key == "edit")
					document.getElementById("editdoc2").style.display = "block";
				else if(key == "del")
					document.getElementById("deldoc2").style.display = "block";
				else if(key == "download")
					document.getElementById("downdoc2").style.display = "block";
				else if(key == "share")
					document.getElementById("copyurldoc2").style.display = "block";
			}
		}
	}); 
};

function viewByRoleNameAndUserId(){
	   $.ajax({
			url:APP_PATH+"/markdownController/viewByRoleNameAndUserId",
			type:"POST",
			async : false,  
			//contentType:'application/json;charset=UTF-8',  
			data:{RoleName:"内部文档成员"},
			success:function(result){
				var msg = result.msg;
				if(msg == "true"){
					show();
				}else{
					hide();
				}
				
			}
		}); 
}

function hide(){
	document.getElementById("editdoc2").style.display = "none";
	document.getElementById("deldoc2").style.display = "none";
}
function show(){
	document.getElementById("editdoc2").style.display = "block";
	document.getElementById("deldoc2").style.display = "block";
}
 
function autoIndexHeight(){
	   var h = getMainHeight();
	   var panelBodyH = h-$(".tabs-header:eq(0)").outerHeight(true)-($(".panel-heading:eq(0)").outerHeight(true)*2)-26;
	   panelBodyH = panelBodyH;
	   $(".default-page-body").height(panelBodyH);
};
//获取id对应文章
function getArt(id){
	$.ajax({
		url:APP_PATH + "/markdownController/view",
		type:"GET",
		data:{ uuid:id },
		success:function(result){
			console.log(result);
			// alert("填入数据—————："+insertText);
			display(result);
		}
	}); 
}

//解析markdown
function display(result){
	var artical = result.map;
	// 标题、概况
	var title = artical.file_title;
	document.title = title;
	var time = formatDateTime(artical.create_time);
	var copyurl = artical.copyurl;
	var file_remark = artical.file_remark;
	var file_type = artical.file_type;
	document.getElementById("doctitleName").innerHTML = title;
	document.getElementById("docremark").innerHTML = file_remark;
	document.getElementById("hascopyurl").value = copyurl;
	// 获取文章内容
	var insertText = artical.contents;
	// 填充文本框
	document.getElementById("test-editormd-docview2").innerHTML = '<textarea id="to_load" style="display: none;">' + insertText + '</textarea>';
	// 解析
	testEditormdView2 = editormd.markdownToHTML("test-editormd-docview2", {
        htmlDecode      : "style,script,iframe",  // you can filter tags
													// decode
        tocm            : true,
        emoji           : true,
        taskList        : true,
        tex             : true,  // 默认不解析
        flowChart       : true,  // 默认不解析
        sequenceDiagram : true,  // 默认不解析
    });
	console.log(testEditormdView2);
}

//function download(){
//	var id = document.getElementById("uuid").value;
//	console.log(testEditormdView2);
//	console.log( testEditormdView2[0].innerHTML);
//	$.ajax({
//		url:APP_PATH + "/markdownController/markownDownload",
//		type:"GET",
//		data:{ uuid:id},
//		success:function(result){
//			console.log(result);
//			
//		}
//	}); 
//}

function download()   
{    
	var headhtml = window.document.head.innerHTML;
	console.log(headhtml);
	var printStr = "<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'>";
	printStr = printStr +"<link rel='stylesheet' type='text/css' href='"+APP_PATH+"/css/knowledge/css/style.css' media='print' >";
	printStr = printStr +"<link rel='stylesheet' type='text/css' href='"+APP_PATH+"/css/knowledge/css/demo.css' media='print'>";
	printStr = printStr +"<link rel='stylesheet' type='text/css' href='"+APP_PATH+"/css/knowledge/css/set.css' media='print'>";
	printStr = printStr +"<link rel='stylesheet' type='text/css' href='"+APP_PATH+"/css/knowledge/css/default.css' media='print'>";
	printStr = printStr +"<link rel='stylesheet' type='text/css' href='"+APP_PATH+"/css/knowledge/css/component.css' media='print'>";
	printStr = printStr +"<link rel='stylesheet' type='text/css' href='"+APP_PATH+"/markdown/css/editormd.css' media='print'>";
	
	printStr = printStr +"<link rel='stylesheet' type='text/css' href='"+APP_PATH+"/css/knowledge/css/style.css' >";
	printStr = printStr +"<link rel='stylesheet' type='text/css' href='"+APP_PATH+"/css/knowledge/css/demo.css' >";
	printStr = printStr +"<link rel='stylesheet' type='text/css' href='"+APP_PATH+"/css/knowledge/css/set.css' >";
	printStr = printStr +"<link rel='stylesheet' type='text/css' href='"+APP_PATH+"/css/knowledge/css/default.css'>";
	printStr = printStr +"<link rel='stylesheet' type='text/css' href='"+APP_PATH+"/css/knowledge/css/component.css' >";
	printStr = printStr +"<link rel='stylesheet' type='text/css' href='"+APP_PATH+"/markdown/css/editormd.css' >";
//	printStr =printStr+"<style type='text/css'>ul li {list-style-type:disc}</style>";
	
	printStr = printStr + "</head><body ><div id='test-editormd-docview2' class='markdown-body editormd-html-preview'>";
	var content = testEditormdView2[0].innerHTML;
    printStr = printStr+content+"</div></body></html>";                                              
    var pwin=window.open("Print.htm","print"); //如果是本地测试，需要先新建Print.htm，如果是在域中使用，则不需要
    pwin.document.write(printStr);
    pwin.document.close();                   //这句很重要，没有就无法实现  
    pwin.print();    
}

//function download(){
//	html2canvas($('#test-editormd-view2'), {
//    height:5000,
//    onrendered: function(canvas) {         
//        var imgData = canvas.toDataURL('img/notice/png');
//        var doc = new jsPDF('p', 'px','a3');
//        //第一列 左右边距  第二列上下边距  第三列是图片左右拉伸  第四列 图片上下拉伸
//        doc.addImage(imgData, 'PNG', -9, 0,650,1500);
//        doc.addPage();
//        doc.addImage(imgData, 'PNG', -9, -900,650,1500);
//        doc.save('test.pdf');
//    }
//});
//}
//function download(){
//	var pdf = new jsPDF('p','pt','a4');
//	pdf.internal.scaleFactor = 1;
//	var options = {
//	     pagesplit: true
//	};
//	
//	//$('.pdf-wrapper')
//	pdf.addHTML(testEditormdView2[0].innerHTML,options,function() {
//	    pdf.save('web1111.pdf');
//	});
//
//}


function deletedoc(){
	$.ajax({
		url:APP_PATH + "/markdownController/deletedoc",
		type:"GET",
		data:{ uuid:docuuid },
		success:function(result){
			console.log(result);
			// alert("填入数据—————："+insertText);
			display(result);
		}
	}); 
}




// 时间戳 to 20XX-XX-XX 00:00:00
function formatDateTime(inputTime) {    
    var date = new Date(inputTime);  
    var y = date.getFullYear();    
    var m = date.getMonth() + 1;    
    m = m < 10 ? ('0' + m) : m;    
    var d = date.getDate();    
    d = d < 10 ? ('0' + d) : d;    
    var h = date.getHours();  
    h = h < 10 ? ('0' + h) : h;  
    var minute = date.getMinutes();  
    var second = date.getSeconds();  
    minute = minute < 10 ? ('0' + minute) : minute;    
    second = second < 10 ? ('0' + second) : second;   
    return y + '-' + m + '-' + d+' '+h+':'+minute+':'+second;    
}