<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<!-- APP_PATH相当于webapp目录 -->
<%
	pageContext.setAttribute("APP_PATH", request.getContextPath());
%>
<link rel="stylesheet" href="${APP_PATH}/css/knowledge/css/style.css" />
<!-- 文本框 -->
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/css/normalize.css" />
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/fonts/font-awesome-4.2.0/css/font-awesome.min.css" />
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/css/demo.css" />

<!--必要样式-->
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/css/set.css" />
<!-- 动态提示框引入css和部分js -->
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/css/default.css" />
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/css/component.css" />

<!-- markdown -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/markdown/css/editormd.css" />
<script src="${pageContext.request.contextPath}/markdown/editormd.js"></script>
   
<div class="default-page-body">
<div id="layout">
	<header>
		<center>
		<div class="art_title demofonts">
	    	<div>
				<select style= "width:50%" class= "form-control col-sm-6"  id="editsystemType"  >
					
				</select>
				<select style= "width:50%" class= "form-control col-sm-6"  id="editpower"  >
					<option value='0'>private</option>
					<option value='1'>protect</option>
					<option value='2'>public</option>
				</select>
			</div>
	    	<input id="edituuid" type="hidden" value="${edituuid}" />
	    	<input id="Edit_PATH" type="hidden" value="${APP_PATH}" />
            <span class="input input--nao">
				<input class="input__field input__field--nao" type="text" id="edit-head" />
				<label class="input__label input__label--nao" for="edit-head">
					<span class="input__label-content input__label-content--nao">标题</span>
				</label>
				<svg class="graphic graphic--nao" width="300%" height="100%"
					 viewBox="0 0 1200 60" preserveAspectRatio="none">
					<path d="M0,56.5c0,0,298.666,0,399.333,0C448.336,56.5,513.994,46,597,46c77.327,0,135,10.5,200.999,10.5c95.996,0,402.001,0,402.001,0"/>
				</svg>
			</span>
		</div>
		<div class="art_sum">
			<span class="input input--yoshiko">
				<input class="input__field input__field--yoshiko" type="text" id="edit-sum" />
				<label class="input__label input__label--yoshiko" for="edit-sum">
					<span class="input__label-content input__label-content--yoshiko"
						 data-content="文章概况">描述</span>
				</label>
			</span>
		</div>		
		</center>		
	</header>
   	<div class="btn-group">
		<p>
			<button onclick="art_edit()"
				type="button" class="btn btn-lg btn-primary" background="#285090">发布</button>
			<!-- 隐藏触发弹出显示--> 
			<button id="Show_editsuccess" style="display:none" type="button"  data-modal="modal-10"
				class="md-trigger btn btn-success">Test_ShowWindows</button>
		</p>
	</div>
	<div class="edit-tool" id="editormd" >
		<textarea id="edit-markdown-doc" name="edit-markdown-doc" style="display:none;"></textarea>
		<!-- 注意：name属性的值-->
		<textarea id="edit-html-code" name="edit-html-code" style="display:none;"></textarea>
	</div>
</div>
</div>
<!-- 发布提示框 -->
<div class="md-modal md-effect-10" id="modal-10">
	<div class="md-content">
		<h3>dd-blog</h3>
		<div>
			<p>发表成功，前往<a id="edithref" class="cnoj-open-tabs" data-title="查看文档" href="#">查看</a></p>
			<button class="md-close">关闭</button>
		</div>
	</div>
</div>

<!-- 动态提示框引入库  classie.js by @desandro: https://github.com/desandro/classie -->
<script src="${APP_PATH}/css/knowledge/js/classie.js"></script>
<script src="${APP_PATH}/css/knowledge/js/modalEffects.js"></script>

<!-- for the blur effect -->
<!-- by @derSchepp https://github.com/Schepp/CSS-Filters-Polyfill 
<script>
	// this is important for IEs
	var polyfilter_scriptpath = '/js/';
</script>
<script src="${APP_PATH}/css/knowledge/js/cssParser.js"></script>
<script src="${APP_PATH}/css/knowledge/js/css-filters-polyfill.js"></script>-->


<script type="text/javascript" src="${APP_PATH}/css/knowledge/js/classie.js"></script>
<script type="text/javascript">
	(function() {
		// trim polyfill : https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/Trim
		if (!String.prototype.trim) {
			(function() {
				// Make sure we trim BOM and NBSP
				var rtrim = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g;
				String.prototype.trim = function() {
					return this.replace(rtrim, '');
				};
			})();
		}
	
		[].slice.call( document.querySelectorAll( 'input.input__field' ) ).forEach( function( inputEl ) {
			// in case the input is already filled..
			if( inputEl.value.trim() !== '' ) {
				classie.add( inputEl.parentNode, 'input--filled' );
			}
	
			// events:
			inputEl.addEventListener( 'focus', onInputFocus );
			inputEl.addEventListener( 'blur', onInputBlur );
		} );
	
		function onInputFocus( ev ) {
			classie.add( ev.target.parentNode, 'input--filled' );
		}
	
		function onInputBlur( ev ) {
			if( ev.target.value.trim() === '' ) {
				classie.remove( ev.target.parentNode, 'input--filled' );
			}
		}
	})();
</script>

<script type="text/javascript">
var edittimecount = 0;
setTimeout("editloadJs()", 200);
   var editloadJs = function(){
	   editormd("editormd", {//注意1：这里的就是上面的DIV的id属性值
	          width   : "90%",
	          height  : 640,
	          syncScrolling : "single",
	          path    : "${pageContext.request.contextPath}/markdown/lib/",//注意2：你的路径
	          saveHTMLToTextarea : true,//注意3：这个配置，方便post提交表单
	          tex : true,
	          Toc:true,
	          flowChart : true,
	          /**上传图片相关配置如下*/
	          imageUpload : true,
	          imageFormats : ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
	          imageUploadURL : "${APP_PATH}/markdownController/uploadimg/?uuid="+document.getElementById("edituuid").value,//注意你后端的上传图片服务地址
	            
	          /**上传附件相关配置如下*/
	          attachUpload : true,
	          attachFormats : ["rar", "zip", "text", "kdb", "word", "pdf", "html", "ppt", "pptx"],
	          attachUploadURL : "${APP_PATH}/markdownController/uploadattach/?uuid="+document.getElementById("edituuid").value,//注意你后端的上传图片服务地址
	      		  
	        
	        onchange : function() {
	        	  edittimecount++;
	        	  if(edittimecount >=50){
	        		  edittimecount =0;
	        		  heartbeat();
	        	  }
	          }   
	   });
	   initeditorpower();
	   initEdotFileType();
	   initArt();
	   autoIndexHeight();
   }
   function autoIndexHeight(){
	   var h = getMainHeight();
	   var panelBodyH = h-$(".tabs-header:eq(0)").outerHeight(true)-($(".panel-heading:eq(0)").outerHeight(true)*2)-26;
	   panelBodyH = panelBodyH;
	   $(".default-page-body").height(panelBodyH);
   };
   
   function heartbeat(){
	   $.ajax({
			url:"${APP_PATH}/markdownController/heartbeat",
			type:"POST",
			async : false,  
			//contentType:'application/json;charset=UTF-8',  
			data:{},
			success:function(result){
				console.log(result);
				//弹窗发布成功
				var rn = result.msg;
				if(rn != 200){
					alert(rn);
				}
			}
		}); 
   };
   
   function initArt(){
	   var edituuid = document.getElementById("edituuid").value;
	   Edit_PATH = document.getElementById('Edit_PATH').value;
	   insertArt(edituuid);
   }
   
 //获取文章信息
	function insertArt(id){
		$.ajax({
			url:"${APP_PATH}/markdownController/view",
			type:"GET",
			data:{ uuid:id },
			success:function(result){
				// alert("填入数据—————："+insertText);
				display(result);
			}
		}); 
	}
	//填入待修改数据
	function display(result){
		var artical = result.map;
		// 标题、概况
		var title = artical.file_title;
		document.getElementById("edit-head").value = title;
		document.getElementById("edit-sum").value = artical.file_remark;
		document.getElementById("edit-markdown-doc").value = artical.contents;
		document.getElementById("editsystemType").value = artical.file_type;
		document.getElementById("editpower").value = artical.power;
	}
	
	function initEdotFileType(){
		   var filetype =document.getElementById("editsystemType");
		   $.ajax({
				url:"${APP_PATH}/dict/findByParentName",
				type:"POST",
				async : false,  
				//contentType:'application/json;charset=UTF-8',  
				data:{busiValue:"doctype"},
				success:function(result){
					var msg = result.msg;
					var i=0;
					for(var o in msg){
						filetype.options[i] =new Option(msg[o].busi_name, msg[o].busi_value);
						i++;
					}
					
				}
			}); 
	   };
	   
	   function initeditorpower(){
		   var power =document.getElementById("editpower");
		   console.log(power);
		   $.ajax({
				url:"${APP_PATH}/dict/findByParentName",
				type:"POST",
				async : false,  
				//contentType:'application/json;charset=UTF-8',  
				data:{busiValue:"doc_power"},
				success:function(result){
					var msg = result.msg;
					var i=0;
					console.log(msg);
					for(var o in msg){
						power.options[i] =new Option(msg[o].busi_name, msg[o].busi_value);
						i++;
					}
					
				}
			}); 
	   }
   
 //博文提交	
	function art_edit(){
		var art_title =	document.getElementById("edit-head").value; 
		var art_sum = document.getElementById("edit-sum").value;
		var art_md = document.getElementById("edit-markdown-doc").value;
		var uuid = document.getElementById("edituuid").value;
		var fileType = document.getElementById("editsystemType").value;
		var power = document.getElementById("editpower").value ;
		if(art_title !=null && art_title !="null"){
			$.ajax({
				url:"${APP_PATH}/markdownController/update",
				type:"POST",
				async : false,  
				data:{  title:art_title, summary:art_sum, contents:art_md,fileType : fileType ,uuid :uuid,power:power },
				success:function(result){
					console.log(result);
					console.log(result.msg);
					//弹窗发布成功
					var uuid = result.msg;
					
					//生成新文章链接
					document.getElementById("edithref").href = "index/viewpage?uuid="+uuid; 
					document.getElementById("Show_editsuccess").click();
				}
			}); 
								
		}else{
			alert("标题不可为空");
			//document.getElementById("Show_success").click();
		}	
	}
</script>
</html>