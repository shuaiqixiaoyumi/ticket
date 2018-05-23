<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<div class="wrap-content">
    <div class="p-l-15 p-t-5">
        <ul id="role-config-docres-tree" class="ztree"></ul>
   </div>
</div>
<script type="text/javascript">
   setTimeout("loadJs2()", 200);
   var IDMark_Switch = "_switch",
	IDMark_Icon = "_ico",
	IDMark_Span = "_span",
	IDMark_Input = "_input",
	IDMark_Check = "_check",
	IDMark_Edit = "_edit",
	IDMark_Remove = "_remove",
	IDMark_Ul = "_ul",
	IDMark_A = "_a";
   function loadJs2() {
	   $.get("doc/selectDocResAuth.json?id=${id}",function(data){
		   var jsonData = data;
		   if(jsonData.result=='1') {
			  var setting = {check: {enable: true},data:{simpleData:{enable: true}},view: {addDiyDom: addDiyDom2},callback:{beforeClick:function(treeId, treeNode) {
					var zTree = $.fn.zTree.getZTreeObj(treeId);
					zTree.checkNode(treeNode, !treeNode.checked, true, true);
					return false;
				}}};
			  $.fn.zTree.init($("#role-config-docres-tree"), setting, jsonData.datas);
		   }
	   });
   }
   function addDiyDom2(treeId, treeNode) {
	   if (treeNode.flag=="auth") {
		   var aObj = $("#"+treeNode.getParentNode().tId+IDMark_Ul);
		   var count=0;
		   aObj.children().each(function(){
			   count++;
			   var idName = $(this).attr("id");
			    if(!utils.isEmpty(idName)) {
			    	if(count>1) {
			    		$(this).find("#"+idName+IDMark_Switch).addClass("bg-img-none");
					}
			   }
		   });
		  $("#"+treeNode.tId+IDMark_Icon).remove();
		  $("#"+treeNode.tId).addClass("li-inline");
		  var pnId = treeNode.getParentNode().tId;
		  $("#"+pnId).next().addClass("clear");
	   } else {
		   var aObj = $("#"+treeNode.tId).addClass("clear");
	   }
   }
</script>