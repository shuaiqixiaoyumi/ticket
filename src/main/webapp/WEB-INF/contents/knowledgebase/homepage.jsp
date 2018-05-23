<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	pageContext.setAttribute("APP_PATH", request.getContextPath());
%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/knowledgebase/homepage.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/web.css" />
<br/>
<div class="default-page-body">
<div class="wcp-zebra-1">
	<div class="container">
		<div class="row wcp-margin-top8">
			<div class="col-lg-1"></div>
			<div class="col-lg-10">
				<div class="widget-box shadow-box hidden-xs" style="background-color: #fff;">
					<div class="title">
						<div style="margin: 16px;  padding-left: 10%; padding-right: 10%;">
							<div class="input-group">
								<input name="word" class="form-control" id="searchInput" type="text" placeholder="请输入检索关键字..." value="" autocomplete="off"> <span class="input-group-btn">
								<button class="btn btn-info" onkeydown="queryArt()"  onclick="queryArt()">
								<span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
								<a id="view_mds" data-title="搜索文档结果" class="cnoj-open-tabs"  href="#" ></a>
								</span>
							</div>
						</div>
					</div>
				</div>
				
				<div id= "dotab" class=" hidden-xs" style="margin: 4px; margin-bottom: 8px; padding-left: 10%; padding-right: 10%;">
					<a id="index-tab"  data-title="编写文档" class="cnoj-open-tabs"  href="index/knowledge" ><span class="label label-danger" style="cursor: pointer;"></span><i class="fa fa-edit"></i>写一篇！</a>
				</div>
			</div>
			<div class="col-lg-1"></div>
		</div>
	</div>
</div>
 
<div class="wcp-zebra-1 ">
	<div class="container  hidden-xs">
		<div class="row ">
			<div class="col-lg-12 ">
				<div>
					<div class="stream-list p-stream">
						<div class="row">
							<div class="col-md-6 docTypeBox" id = "viewModle1">
								
							</div>
							<div class="col-md-6 docTypeBox" id="viewModle2">
								
							</div>
						</div>
						<div style="margin-top: 20px;"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="wcp-zebra-1">
	<div class="container  hidden-xs">
		<div class="row ">
			<div class="col-lg-12">
				<div>
					<div class="stream-list p-stream">
						<div class="row">
							<div class="col-md-6 docTypeBox" id="viewModle3">
								
							</div>
							<div class="col-md-6 docTypeBox" id="viewModle4">
								
							</div>
						</div>
						<div style="margin-top: 20px;"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

</div>

<script type="text/javascript">
   $(function(){
	   $(function () {
		   $('input:text:first').focus(); //把焦点放在第一个文本框 
		   var $inp = $('input'); //所有的input元素 
		   $inp.keypress(function (e) { //这里给function一个事件参数命名为e，叫event也行，随意的，e就是IE窗口发生的事件。 
		   var key = e.which; //e.which是按键的值 
		   if (key == 13) { 
			   queryArt();
		   } 
		   }); 
		   }); 
	   autoIndexHeight();
	   $(window).resize(function(){
		   setTimeout(function() {
			   autoIndexHeight();
			   
		   }, 200);
		   
	   });
	   function autoIndexHeight(){
		   var h = getMainHeight();
		  
		   var panelBodyH = h-$(".tabs-header:eq(0)").outerHeight(true)-($(".panel-heading:eq(0)").outerHeight(true)*2)-26;
		   panelBodyH = panelBodyH;
		   $(".default-page-body").height(panelBodyH);
		   initAllViewModels();
		   viewByRoleNameAndUserId();
	   };
	   function initAllViewModels(){
		   //initViewModel("sopi_doc","viewModle1");
		   //initViewModel("indept_doc","viewModle2");
		   //initViewModel("outdept_doc","viewModle3");
		   //initViewModel("other_doc","viewModle4");
		   $.ajax({
				url:"${APP_PATH}/dict/findByParentNameAndRole",
				type:"POST",
				async : false,  
				//contentType:'application/json;charset=UTF-8',  
				data:{busiValue:"doctype"},
				success:function(result){
					console.log(result);
					var msg = result.msg;
					var i=0;
					for(var o in msg){
						i++;
						initViewModel(msg[o].busi_name, msg[o].busi_value,"viewModle"+i);
					}
					
				}
			}); 
	   }
	   function initViewModel(busi_name,fileType, divId){
			$.ajax({
				url:"${APP_PATH}/markdownController/getViewModle",
				type:"GET",
				contentType:'application/json;charset=UTF-8',  
				data:{ fileType: encodeURI(fileType)},
				success:function(result){
					// alert("填入数据—————："+insertText);
					insertDiv(result,fileType,divId,busi_name);
				}
			}); 
		} ;
		
		function viewByRoleNameAndUserId(){
			   $.ajax({
					url:"${APP_PATH}/markdownController/viewByRoleNameAndUserId",
					type:"POST",
					async : false,  
					//contentType:'application/json;charset=UTF-8',  
					data:{RoleName:"内部文档成员"},
					success:function(result){
						console.log(result);
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
			document.getElementById("dotab").style.display = "none";
		}
		function show(){
			document.getElementById("dotab").style.display = "block";
	    }
		
   });
</script>
