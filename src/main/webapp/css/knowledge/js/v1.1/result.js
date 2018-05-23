$(document).ready(function(){
	//全局变量
	APP_PATH = document.getElementById('App_PATH').value;
	//搜索类型
	SEARCH_TYPE = document.getElementById('Search_TYPE').value;
	U_ID = document.getElementById('UserID').value;
	T_ID = document.getElementById('type_id').value;
	Key_Word = document.getElementById('KeyWord').value;
	//根据controller层返回的map获取标签tid,查询并页面填充
	//alert(U_ID + T_ID);
	SearchBlog(1);
});

//根据user返回文章
function SearchBlog(pn) {
	//Todo,判断当前查询类型(标签or关键字)
	if(SEARCH_TYPE == 'T'){
		//标签查询
		document.getElementById("hint").style.display="none";
		$.ajax({
			type : "POST",
			url : APP_PATH + "/getArtByType",
			data : {"pn":pn,"tid":T_ID},
			success : function(result) {
				display(result);
				console.log('标签Search'+result);
			}
		});
	}else{
		//关键字查询
		$.ajax({
			type : "POST",
			url : APP_PATH + "/getArtByKeyWord",
			data : {"pn":pn,"keyword":Key_Word},
			success : function(result) {
				display(result);
				console.log('关键字Search'+result);
			}
		});
	}
}

//插入结果(分页条,文章列表)
function display(result) {
	
	var state = result.code;
	if (state == 100) {
		//构建分页条
		build_page_info(result.extend.TagInfo);

		//此页文章数量
		var art_num = result.extend.TagInfo.size;
		//alert(art_num);
		//获取文章数组
		var art_content1 = result.extend.TagInfo.list;
		build_bloglist(art_content1);
		
		/* for (var i = 0; i < art_content1.length; i++) {					
		//alert(art_content1[i].articals[0].title);
		} */
		//alert("文章数量"+art_num+"第0篇"+art_content1);
		//alert(JSON.parse(JSON.stringify(text)));
		
		//页面滚动到文章合适位置
		//document.body.scrollTop = document.documentElement.scrollTop = 200;
		scrollTo(0,130);
		
	} else {
		alert("登录超时( $ _ $ ),返回主页刷新...");
		window.location.href = APP_PATH+'/front/index.jsp';
	}
}

//时间戳格式化
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
	return y + '-' + m + '-' + d + ' ' + h + ':' + minute + ':' + second;
}

// 构建分页信息
function build_page_info(TagInfo){
	//alert("分页信息：");
	$("#page_info_area").empty();
	$("#page_info_nav").empty();
	//分页信息
	document.getElementById("page_info_area").innerHTML="当前第" + TagInfo.pageNum 
		+ "页,总"+ TagInfo.pages + "页,共" + TagInfo.total + "条记录";
	//分页条
	/* <ul class="pagination">
					<li><a href="${APP_PATH }/emps?pn=1">首页</a></li>
					<li><a href="${APP_PATH }/emps?pn=1">1</a></li>
					<li><a href="${APP_PATH }/emps?pn=1">2</a></li>
					<li><a href="${APP_PATH }/emps?pn=1">3</a></li>
					<li><a href="${APP_PATH }/emps?pn=${TagInfo.pages}">末页</a></li>
				</ul> */
	//document.getElementById("page_info_nav");
	//var PageNavValue = "<li><a href=\"${APP_PATH }/SearchBlog?pn="+ 1 + ">首页</a></li>";
	var ul = $("<ul></ul>").addClass("pagination");
	var firstPageLi = $("<li></li>").append($("<a></a>").append("首页"));
	var prePageLi = $("<li></li>").append($("<a></a>").append("&laquo;"));
	if(TagInfo.hasPreviousPage == false){
		firstPageLi.addClass("disabled");
		prePageLi.addClass("disabled");
	}else{
		//翻页点击事件
		firstPageLi.click(function(){
			SearchBlog(1);
		});
		prePageLi.click(function(){
			SearchBlog(TagInfo.pageNum -1);
		});
	}
	var nextPageLi = $("<li></li>").append($("<a></a>").append("&raquo;"));
	var lastPageLi = $("<li></li>").append($("<a></a>").append("末页"));
	if(TagInfo.hasNextPage == false){
		nextPageLi.addClass("disabled");
		lastPageLi.addClass("disabled");
	}else{
		nextPageLi.click(function(){
			SearchBlog(TagInfo.pageNum + 1);
		});
		lastPageLi.click(function(){
			SearchBlog(TagInfo.pages);
		});
	}
	
	
	ul.append(firstPageLi).append(prePageLi);
	//页码提示
	$.each(TagInfo.navigatepageNums,function(index,item){
		//alert("下标："+index+"页码"+item);
		var numLi = $("<li></li>").append($("<a></a>").append(item));
		//当前页
		if(TagInfo.pageNum==item){
			numLi.addClass("active");
		}
		numLi.click(function(){
			SearchBlog(item);
		});
		ul.append(numLi);
		//alert(ul.get(0).outerHTML);
	});
	//添加下页、末页提示
	ul.append(nextPageLi).append(lastPageLi);
	//alert("添加下页、末页"+ul.get(0).outerHTML);
	//$("page_info_nav").append(ul);
	var navEle = $("<nav></nav>").append(ul);
	navEle.appendTo("#page_info_nav");
}

//构建文章列表
function build_bloglist(art_content1){
	$('#bloglist').html('');
	//判断所查文章列表是否为空
	if(art_content1.length == 0) {
		//构建暂无结果
		var bl_li = $("<li>抱歉,暂无相关文章!</li>");
		bl_li.appendTo("#bloglist");
		//隐藏分页条
		document.getElementById("pagetool").style.display="none";
	} else {
		//id = bloglist;
		for (var i = 0; i < art_content1.length; i++) {	
			var TData = eval(art_content1); 
			//alert(TData[i].title);
			var bl_li = $("<li></li>");
			var blog_a = $("<a></a>").attr('href',APP_PATH+'/blog?blog_id='+TData[i].id)
				.attr('target','_self');
			//alert(blog_a.get(0).outerHTML);
			var bl_div = $("<div></div>").addClass("art_");
				var bl_head = $("<div></div>").addClass("art_head");
					var bl_h3 = $("<h3></h3>").append(TData[i].title);
				var bl_ms_div = $("<div></div>").addClass("art_mess");
					var bl_time = $("<div></div>").addClass("art_time")
						.append(formatDateTime(TData[i].createtime));;
					
			bl_head.append(bl_h3);
			bl_ms_div.append(bl_time);
			bl_div.append(bl_head).append(bl_ms_div);
			blog_a.append(bl_div);
			bl_li.append(blog_a);
			
			bl_li.appendTo("#bloglist");
		}
	}
}
