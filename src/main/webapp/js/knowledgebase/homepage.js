

var insertDiv = function(result,fileType,divId,busi_name){
	
	var html='<div class="wcp-widecolumn-titlebox"><div class="wcp-inner-titlebox"><i class=" glyphicon glyphicon-unchecked"></i>'+busi_name+' </div><a class="cnoj-open-tabs right" data-title="搜索文档结果" href="index/searchresultview?search='+encodeURI(fileType)+'&pagenumber=1">更多</a><hr></div>';
//	html = '<div class="right"><a class="cnoj-open-self" data-title="我的待办" href="process/todo">更多</a></div>';
	html = html+'<div class="docTypeBox-innerBox" style="height: 230px;"><ul class="box-list knowListUl" id="hots">';
	var msg = result.msg;
	var i=0;
	for(var o in msg){
		if(i>4){
			break ;
		}
		html = html +'<li style="float: none; border-right: none 0px #fff;"><div class="li-out"><span class="last"> </span> <span class="name" style="width: 355px;">';
		html = html+'<span style="font-size: 10px;">'+formatDateTime(msg[o].create_time)+'</span><span class="glyphicon glyphicon-book"></span>';
		html = html+'<a class="cnoj-open-tabs" href="index/viewpage?uuid='+msg[o].id+'" data-title="查看文档" >'+msg[o].file_name+'</a></span>'
		html = html+'</div></li>';
		i++;
	}
	document.getElementById(divId).innerHTML = html;
};

function queryArt(){
	var searchInput = document.getElementById("searchInput").value;
	document.getElementById("view_mds").href = "index/searchresultview?search="+searchInput+"&pagenumber=1"
	document.getElementById("view_mds").click();
}

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
