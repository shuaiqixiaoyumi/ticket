
window.onload = function(){
	//更改网页头背景
	//$("#header-wrapper").css("background","blue");
	var headbg = document.getElementById('header-wrapper');
	headbg.style.backgroundImage = 'url(' + 'images/v1.1/mt.png' + ')';
}

//判断响应端
if(/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
    //alert("Phone");
} else {
    //alert("PC");    
}


function Tocheck(){
	alert("a");
}

function Published(){
	alert("p");
}

function Tag(){
	alert("t");
}

// $(document).bind('click',function(ev){
	
// });


window._skel_config = {
	preset: 'standard',
	prefix: 'css/style',
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