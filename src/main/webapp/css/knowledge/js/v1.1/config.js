
/*
	Dopetrope 2.0 by HTML5 UP
	html5up.net | @n33co
	Free for personal and commercial use under the CCA 3.0 license (html5up.net/license)
*/

	/*
		参考http://www.jianshu.com/p/0f05e628f87b,
		通过该方法来为每次弹出的模态框设置最新的zIndex值，从而使最新的modal显示在最前面
	*/
    // $(document).on('show.bs.modal', '.modal', function (event) {
    //     //var zIndex = -20;
    //     //alert("show");
    //     setTimeout(function() {
    //         $('.modal-backdrop').css({"z-index": "-20"});
    //         /*$("#myModal").css({"top":"10%"});*/
    //         //Esc退出模态框
    //         $("#myModal").modal({keyboard:true});
    //     }, 0);

    // });

    /*$(document).on('click',function(e){
	  if( e.target.css("z-index") < $('.modal-backdrop').css("z-index") ){
	    $('#myModal').modal('hide');
	  }
	  alert(e.target.css("z-index"));
	  alert($('.modal-backdrop').css("z-index"));
	});*/


//判断响应端
if(/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
    // alert("Phone");
    // document.getElementById('css').href = '../../css/v1.1/style-mobile.css';
} else {
    // alert("PC");    
    // document.getElementById('css').href = '../../css/v1.1/style-desktop.css';
}


function ShowSearch(){
	// 显示搜索框(手机端)
	$("#myModal").css({"z-index":"99999"});
	$('#myModal').removeClass('hide');
}

$(document).bind('click',function(ev){
	//(c_obj != "myModal") && (c_obj != "search_form")    	&& (c_obj != "search_box") && (c_obj != "s")
	var c_obj = ev.target;
    if (c_obj != $('#myModal')[0] && c_obj != $('#search_form')[0] && c_obj != $('#search_box')[0]
    	&& c_obj != $('#s')[0] ){
    	$('#myModal').addClass('hide');
    	//$('#myModal').removeClass('hide');    	
    }
});


/*搜索执行*/
function doSearch(){
	//获取搜索框keyword,跳转
	var kw = document.getElementById('s').value;
	//alert("phone-search:" + kw);
	window.open("http://localhost:8080/dd-blog/KeySearch?key=" + kw,"_self");
	//清空搜索框
	document.getElementById('s').value="";
}

// function stopPropagation(e) {
//     if (e.stopPropagation) 
//         e.stopPropagation();
//     else 
//         e.cancelBubble = true;
// }

// $(document).bind('click',function(){
//     $('#myModal').modal('hide');
// });

// $('#myModal').bind('click',function(e){
//     stopPropagation(e);
// });

/*	
	**=====prefix务必随css路径更改以加载jsp的noscript标签=====**
	https://stackoverflow.com/questions/19618294/loading-css-stylesheets-inside-noscript-tags-with-flask
*/
window._skel_config = {
	preset: 'standard',
	prefix: 'http://localhost:8080/dd-blog/front/css/v1.1/style',
	resetCSS: true,
	breakpoints: {
		'desktop': {
			grid: {
				gutters: 50
			}
		}
	}
};

window._skel_panels_config = {
	preset: 'standard'
};

jQuery(function() {
	$('#nav > ul').dropotron({ 
		offsetY: -17,
		offsetX: -1,
		mode: 'fade',
		noOpenerFade: true,
		detach: false
	});
});