<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- APP_PATH相当于webapp目录 -->
<%
	pageContext.setAttribute("APP_PATH", request.getContextPath());
%>

<link href="${APP_PATH}/css/knowledge/css/templatemo_style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${APP_PATH}/js/knowledgebase/searchresultview.js"></script>

<div class="container default-page-body">
	<input id="searchValue" type="hidden" value="${searchInput}" />
	<input id="pagenumber" type="hidden" value="${pagenumber}" />
	<div id="searchViewPages" ></div>

	<!-- 分页条 -->
	<div id="page_tool" >
		<div class="row">
			<!--分页文字信息  -->
			<div id="page_info_area" class="col-md-6"></div>
			<!-- 分页条信息 -->
			<div id="page_info_nav" class="col-md-6">
				<nav style="text-align: center" aria-label="Page navigation">
					
				</nav>
			</div>
		</div>
	</div>

</div>

<script type="text/javascript">
var pagenumber = document.getElementById("pagenumber").value;
   $(function(){
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
		   loadsearchresultview();
	   };
	   function loadsearchresultview(){
			GetBlogs(pagenumber);
		}
		
   });
</script>
<script type="text/javascript">
//文章列表
function build_bloglist(art_content1){
	//$("#bloglist").empty();
	$('#searchViewPages').html('');
	//id = bloglist;
	for (var i in art_content1) {	
		//alert(art_content1[i].articals[0].title);
		var blog_a = $("<a></a>").attr('href','index/viewpage?uuid='+art_content1[i].id)
			.attr('class','cnoj-open-tabs').attr('data-title','查看文档');
		//alert(blog_a.get(0).outerHTML);
		var bl_div = $("<div></div>").addClass("kosks");
		var bl_h = $("<blog-h></blog-h>").append(art_content1[i].file_name);
		var bl_ms_div = $("<div></div>").addClass("blog_message");
		//时间
		var blt_img = $("<img></img>").attr("src","css/knowledge/images/ico-calendar.png").attr("alt","发表时间");
		var bl_hst = $("<hs></hs>").append(formatDateTime(art_content1[i].create_time));
		var bl_bt = $("<blog-time></blog-time>").append(blt_img).append(bl_hst);
		//访问量
		var blv_img = $("<img></img>").attr("src","css/knowledge/images/ico-eye.png").attr("alt","访问量");
		var bl_hsc = $("<hs></hs>").append(art_content1[i].amount_ccess);
		var bl_bv = $("<blog-view></blog-view>").append(blv_img).append(bl_hsc);
		//填充/* div class="item" */
		bl_ms_div.append(bl_bt).append(bl_bv);
		bl_div.append(bl_h).append(bl_ms_div);
		blog_a.append(bl_div);
		
		blog_a.appendTo("#searchViewPages");
		
	}
}
</script>


