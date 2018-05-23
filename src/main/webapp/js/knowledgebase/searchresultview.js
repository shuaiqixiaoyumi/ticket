function loadsearchresultview(){
	GetBlogs(1);
}

//根据user返回文章
function GetBlogs(pn){
	var searchValue = document.getElementById("searchValue").value;
	$.ajax({
		type:"GET",
       url : "markdownController/getSearchResultView",
       contentType:'application/json;charset=UTF-8',  
       data:{pn:pn, searchInput:searchValue},  
       success: function(result){
    	   getPageInfo(result);
       }
		});
}

//插入结果
function getPageInfo(result){
	var state = result.result;
    if(state==1){
    	//构建分页条
    	build_page_info(result);
    	
    	//此页文章数量
 	   	var art_num = result.msgsize;
    	//获取文章数组
 	   	var art_content1 = result.msg;
 	   	build_bloglist(art_content1);
 	   	
    }else{
 	   	alert("未查到相关文档");
    }
}



//分页信息
function build_page_info(pageInfo){
	var searchValue = document.getElementById("searchValue").value;
	//alert("分页信息：");
	$("#page_info_area").empty();
	$("#page_info_nav").empty();
	//分页信息
	document.getElementById("page_info_area").innerHTML="当前第" + pageInfo.pageNum 
		+ "页,总"+ pageInfo.totalpages + "页,共" + pageInfo.totalsize + "条记录";
	var ul = $("<ul></ul>").addClass("pagination");
	var firstPageLi = $("<li></li>").append($("<a></a>").append("首页").attr("class","cnoj-change-page").attr("data-uri","index/searchresultview?search="+encodeURI(searchValue)+"&pagenumber=1").attr("href","#"));
	var prePageLi = $("<li></li>").append($("<a></a>").append("&laquo;").attr("class","cnoj-change-page").attr("data-uri","index/searchresultview?search="+encodeURI(searchValue)+"&pagenumber="+((pageInfo.pageNum -1)==0?1:(pageInfo.pageNum -1))).attr("href","#"));
	if(pageInfo.hasPreviousPage == false){
		firstPageLi.addClass("disabled");
		prePageLi.addClass("disabled");
	}else{
		//翻页点击事件
//		firstPageLi.click(function(){
//			GetBlogs(1);
//		});
//		prePageLi.click(function(){
//			GetBlogs(pageInfo.pageNum -1);
//		});
	}
	var nextPageLi = $("<li></li>").append($("<a></a>").append("&raquo;").attr("class","cnoj-change-page").attr("data-uri","index/searchresultview?search="+encodeURI(searchValue)+"&pagenumber="+((pageInfo.pageNum +1)>pageInfo.totalpages?pageInfo.totalpages:(pageInfo.pageNum +1))).attr("href","#"));
	var lastPageLi = $("<li></li>").append($("<a></a>").append("末页").attr("class","cnoj-change-page").attr("data-uri","index/searchresultview?search="+encodeURI(searchValue)+"&pagenumber="+pageInfo.totalpages).attr("href","#"));
	if(pageInfo.hasNextPage == false){
		nextPageLi.addClass("disabled");
		lastPageLi.addClass("disabled");
	}else{
//		nextPageLi.click(function(){
//			GetBlogs(pageInfo.pageNum + 1);
//		});
//		lastPageLi.click(function(){
//			GetBlogs(pageInfo.totalpages);
//		});
	}
	
	
	ul.append(firstPageLi).append(prePageLi);
	//页码提示
	$.each(eval(pageInfo.navigatepageNums),function(index,item){
		//alert("下标："+index+"页码"+item);
		var numLi = $("<li></li>").append($("<a></a>").append(item).attr("class","cnoj-change-page").attr("data-uri","index/searchresultview?search="+encodeURI(searchValue)+"&pagenumber="+item).attr("href","#"));
		//var numLi = $("<li></li>").append($("<a></a>").append(item));
		//当前页
		if(pageInfo.pageNum==item){
			numLi.addClass("active");
		}
//		numLi.click(function(){
//			GetBlogs(item);
//		});
		ul.append(numLi);
		//alert(ul.get(0).outerHTML);
	});
	//添加下页、末页提示
	ul.append(nextPageLi).append(lastPageLi);
	//alert("添加下页、末页"+ul.get(0).outerHTML);
	$("page_info_nav").append(ul);
	var navEle = $("<nav></nav>").append(ul);
	navEle.appendTo("#page_info_nav");
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
    return y + '-' + m + '-' + d+' '+h+':'+minute+':'+second;    
};  
