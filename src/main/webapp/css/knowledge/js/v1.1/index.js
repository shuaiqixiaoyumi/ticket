$(document).ready(function(){
	//全局变量
	APP_PATH = document.getElementById('App_PATH').value;
	//alert(APP_PATH);
	/* alert("请先登录。");
	window.location.href='${APP_PATH}/front/login.jsp'; */
	//页面定位至顶部
	scrollTo(0,0);
	//自动登录
	$.ajax({
		type : "POST",
		url : APP_PATH + "/login",
		data : {
			username : null,
			password : null
		},
		success : function(result) {
			var state = result.code;
			if (state == 100) {
				//可将用户所需信息赋入隐藏控件,将USER_STATE置为 1
				//document.getElementById("USER_STATE").value="1";
				//alert("登录成功");
				//请求博文(传入页码1)
				GetBlogs(1);
				//获取标签(session提取用户id)
				GetTypes();
			} else {
				//document.getElementById("USER_STATE").value="0";
				//登录失败，添加失败样式
				alert("登录失败，请联系管理员");
			}
		}
	});
	
});

//根据user返回文章
function GetBlogs(pn) {
	$.ajax({
		type : "POST",
		url : APP_PATH + "/getblogs",
		data : "pn=" + pn,
		success : function(result) {
			//alert(result.code);
			display(result);
		}
	});
}

//根据user返回标签
function GetTypes() {
	$.ajax({
		type : "POST",
		url : APP_PATH + "/getTypes",
		data : "",
		success : function(result) {
			//alert(result.code);
			//构建标签
			var type_Data = eval(result.extend.Types);
			build_blogType(type_Data);
			
		}
	});
}

//插入结果(分页条,文章列表)
function display(result) {
	
	var state = result.code;
	if (state == 100) {
		//构建分页条
		build_page_info(result.extend.pageInfo);

		//此页文章数量
		var art_num = result.extend.pageInfo.size;
		//alert(art_num);
		//获取文章数组
		var art_content1 = result.extend.pageInfo.list;
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
		alert("请重新登录。");
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
function build_page_info(pageInfo){
	//alert("分页信息：");
	$("#page_info_area").empty();
	$("#page_info_nav").empty();
	//分页信息
	document.getElementById("page_info_area").innerHTML="当前第" + pageInfo.pageNum 
		+ "页,总"+ pageInfo.pages + "页,共" + pageInfo.total + "条记录";
	//分页条
	/* <ul class="pagination">
					<li><a href="${APP_PATH }/emps?pn=1">首页</a></li>
					<li><a href="${APP_PATH }/emps?pn=1">1</a></li>
					<li><a href="${APP_PATH }/emps?pn=1">2</a></li>
					<li><a href="${APP_PATH }/emps?pn=1">3</a></li>
					<li><a href="${APP_PATH }/emps?pn=${pageInfo.pages}">末页</a></li>
				</ul> */
	//document.getElementById("page_info_nav");
	//var PageNavValue = "<li><a href=\"${APP_PATH }/getblogs?pn="+ 1 + ">首页</a></li>";
	var ul = $("<ul></ul>").addClass("pagination");
	var firstPageLi = $("<li></li>").append($("<a></a>").append("首页"));
	var prePageLi = $("<li></li>").append($("<a></a>").append("&laquo;"));
	if(pageInfo.hasPreviousPage == false){
		firstPageLi.addClass("disabled");
		prePageLi.addClass("disabled");
	}else{
		//翻页点击事件
		firstPageLi.click(function(){
			GetBlogs(1);
		});
		prePageLi.click(function(){
			GetBlogs(pageInfo.pageNum -1);
		});
	}
	var nextPageLi = $("<li></li>").append($("<a></a>").append("&raquo;"));
	var lastPageLi = $("<li></li>").append($("<a></a>").append("末页"));
	if(pageInfo.hasNextPage == false){
		nextPageLi.addClass("disabled");
		lastPageLi.addClass("disabled");
	}else{
		nextPageLi.click(function(){
			GetBlogs(pageInfo.pageNum + 1);
		});
		lastPageLi.click(function(){
			GetBlogs(pageInfo.pages);
		});
	}
	
	
	ul.append(firstPageLi).append(prePageLi);
	//页码提示
	$.each(pageInfo.navigatepageNums,function(index,item){
		//alert("下标："+index+"页码"+item);
		var numLi = $("<li></li>").append($("<a></a>").append(item));
		//当前页
		if(pageInfo.pageNum==item){
			numLi.addClass("active");
		}
		numLi.click(function(){
			GetBlogs(item);
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

//构建标签列表
function build_blogType(type_Data){
	$('#type_li').html('');
	//只显示top4条 更多弹出搜索
	for (var i = 0; i < type_Data.length; i++) {
		//构建li填充至type_li
		var t_li = $("<li></li>");
		//标签id请求,controller控制跳转至search.jsp
		/*t_a.attr('target','_self')*/
		var t_a = $("<a></a>").attr('href',APP_PATH+'/TypeSearch?tid='+type_Data[i].id)
		.append(type_Data[i].typeName+'('+ type_Data[i].count + ')');
		t_li.append(t_a);
		t_li.appendTo("#type_li");
		//alert(type_Data[i].typeName);
		//只填充4行
		if (i == 3)break;		
	}
}
