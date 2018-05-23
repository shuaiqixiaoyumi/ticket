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
<link rel="stylesheet" href="${pageContext.request.contextPath}/markdown/css/editormd.min.css" />
<script src="${pageContext.request.contextPath}/markdown/editormd.js"></script>

<link rel="shortcut icon" href="https://pandao.github.io/editor.md/favicon.ico" type="image/x-icon" />
<link rel="stylesheet" href="${APP_PATH}/markdown/css/editormd.preview.css" />
   
<div class="default-page-body">
<div id="layout" class="qwe">
	
	<header >
		<center>
	    <div class="art_title demofonts">
	    	<input id="newuuid" type="hidden" value="" />
	    	<div  >
				<select style= "width:50%" class= "form-control col-sm-6"  id="systemType"  >
					<option value='sopi_doc'>sopi文档</option>
					<option value='indept_doc'>部门内文档</option>
					<option value='outdept_doc'>部门外文档</option>
					<option value='other_doc'>其他文档</option>
				</select>
				<select style= "width:50%" class= "form-control col-sm-6"  id="power"  >
					<option value='0'>private</option>
					<option value='1'>protect</option>
					<option value='2'>public</option>
				</select>
			</div>
            <span class="input input--nao ">
				<input class="input__field input__field--nao "  type="text" id="input-head" />
				<label class="input__label input__label--nao" for="input-head">
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
				<input class="input__field input__field--yoshiko" type="text" id="input-sum" />
				<label class="input__label input__label--yoshiko" for="input-sum">
					<span class="input__label-content input__label-content--yoshiko"
						 data-content="文章概况">描述</span>
				</label>
			</span>
		</div>		
		</center>		
	</header>
   	<div class="btn-group">
		<p>
			<button onclick="art_publish()"
				type="button" class="btn btn-lg btn-primary" background="#285090">发布</button>
			<!-- 隐藏触发弹出显示--> 
			<button id="Show_success" style="display:none" type="button"  data-modal="modal-10"
				class="md-trigger btn btn-success">Test_ShowWindows</button>
		</p>
	</div>
	<div class="edit-tool" id="my-editormd" >
		<textarea id="my-editormd-markdown-doc" name="my-editormd-markdown-doc" style="display:none;"></textarea>
		<!-- 注意：name属性的值-->
		<textarea id="my-editormd-html-code" name="my-editormd-html-code" style="display:none;"></textarea>
	</div>
</div>
</div>
<!-- 发布提示框 -->
<div class="md-modal md-effect-10" id="modal-10">
	<div class="md-content">
		<h3>dd-blog</h3>
		<div>
			<p>发表成功，前往<a id="newhref" class="cnoj-open-self" data-title="查看文档" href="#">查看</a></p>
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
var timecount =0;
setTimeout("loadJs()", 200);
   var loadJs = function(){
	   initNewuuid();
	   initFileType();
	   initpower();
	   
	   editormd("my-editormd", {//注意1：这里的就是上面的DIV的id属性值
	          width   : "90%",
	          height  : 640,
	          syncScrolling : "single",
	          path    : "${pageContext.request.contextPath}/markdown/lib/",//注意2：你的路径
	          saveHTMLToTextarea : true,//注意3：这个配置，方便post提交表单
	          tex : true,
	          Toc:true,
	          flowChart : true,
	          
	          htmlDecode : "style,script,iframe|on*", 
	          codeFold : true,
	          searchReplace : true,
	          emoji : true,
	          taskList : true,
	          tocm            : true,
	          sequenceDiagram : true,   
	          
	          /**上传图片相关配置如下*/
	          imageUpload : true,
	          imageFormats : ["jpg", "jpeg", "gif", "png", "bmp", "webp","rar","zip"],
	          imageUploadURL : "${APP_PATH}/markdownController/uploadimg/?uuid="+document.getElementById("newuuid").value,//注意你后端的上传图片服务地址
	        		  
	          /**上传图片相关配置如下*/
	          attachUpload : true,
	          attachFormats : ["rar", "zip", "text", "kdb", "word", "pdf", "html", "ppt", "pptx"],
	          attachUploadURL : "${APP_PATH}/markdownController/uploadattach/?uuid="+document.getElementById("newuuid").value,//注意你后端的上传图片服务地址
	      
	        		  
	          onchange : function() {
	        	  timecount++;
	        	  if(timecount >=50){
	        		  timecount =0;
	        		  heartbeat();
	        	  }
	          }   
	   });
	   autoIndexHeight(); 
	  
	   
   }
   function autoIndexHeight(){
	   var h = getMainHeight();
	   var panelBodyH = h-$(".tabs-header:eq(0)").outerHeight(true)-($(".panel-heading:eq(0)").outerHeight(true)*2)-26;
	   panelBodyH = panelBodyH;
	   $(".default-page-body").height(panelBodyH);
   };
   function initNewuuid(){
	   $.ajax({
			url:"${APP_PATH}/markdownController/initNewuuid",
			type:"POST",
			async : false,  
			//contentType:'application/json;charset=UTF-8',  
			data:{},
			success:function(result){
				console.log(result);
				//弹窗发布成功
				var uuid = result.msg;
				
				//生成新文章链接
				document.getElementById("newuuid").value = uuid; 
			}
		}); 
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
   
   function initFileType(){
	   var filetype =document.getElementById("systemType");
	   $.ajax({
			url:"${APP_PATH}/dict/findByParentName",
			type:"POST",
			async : false,  
			//contentType:'application/json;charset=UTF-8',  
			data:{busiValue:"doctype"},
			success:function(result){
				console.log(result);
				var msg = result.msg;
				var i=0;
				for(var o in msg){
					filetype.options[i] =new Option(msg[o].busi_name, msg[o].busi_value);
					i++;
				}
				
			}
		}); 
   };
   
   function initpower(){
	   var power =document.getElementById("power");
	   $.ajax({
			url:"${APP_PATH}/dict/findByParentName",
			type:"POST",
			async : false,  
			//contentType:'application/json;charset=UTF-8',  
			data:{busiValue:"doc_power"},
			success:function(result){
				var msg = result.msg;
				var i=0;
				for(var o in msg){
					power.options[i] =new Option(msg[o].busi_name, msg[o].busi_value);
					i++;
				}
				
			}
		}); 
   }
   
 //博文提交	
	function art_publish(){
		var art_title =	document.getElementById("input-head").value; 
		var art_sum = document.getElementById("input-sum").value;
		var art_md = document.getElementById("my-editormd-markdown-doc").value;
		var fileType = document.getElementById("systemType").value;
		var uuid = document.getElementById("newuuid").value;
		var power = document.getElementById("power").value ;
		if(art_title !=null && art_title !="null"){
			$.ajax({
				url:"${APP_PATH}/markdownController/save",
				type:"POST",
				async : false,  
				//contentType:'application/json;charset=UTF-8',  
				data:{  title:art_title, summary:art_sum, contents:art_md,fileType :fileType, uuid : uuid, power:power},
				success:function(result){
					console.log(result);
					//弹窗发布成功
					uuid = result.msg;
					
					//生成新文章链接
					document.getElementById("newhref").href = "index/viewpage?uuid="+uuid; 
					document.getElementById("Show_success").click();
				}
			}); 
								
		}else{
			alert("标题不可为空");
			//document.getElementById("Show_success").click();
		}	
	}
</script>
</html>