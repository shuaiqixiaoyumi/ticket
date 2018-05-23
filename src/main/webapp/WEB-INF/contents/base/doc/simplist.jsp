<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="cnoj" uri="/cnoj-tags" %>
<%
	pageContext.setAttribute("APP_PATH", request.getContextPath());
%>
<div class="wrap-content">
	<div class="panel no-border">
        <div class="panel-search borer-bottom">
              <form class="form-inline cnoj-entry-submit" id="search-form-doc" method="post" role="form" action="doc/simplist" target="#doc-tab">
                  <!--  <div class="form-group p-r-10">
				    <label for="search-input02">关键字：</label>
				    <input type="text" class="form-control input-form-sm-control" id="search-input02" name="name" placeholder="请输入文档名称" value="${searchParam.name }"/>
				  </div>
				  <div class="form-group p-l-10">
					  <span class="btn btn-info btn-sm cnoj-search-submit">
						<i class="glyphicon glyphicon-search"></i>
						<span>搜索</span>
					  </span>
				  </div>-->
				  <a id="view_mds" target="#has-docauth-list"   href="#has-docauth-list" ></a>
              </form>
          </div>
          <input id="APP_PATH" type="hidden" value="${APP_PATH}" />
		<cnoj:tableTree smartResp="${smartResp }" headers="文档名称"  isExpand="1" isClick="1" clickfun="viewdoc" currentUri="${currentUri }" 
		   alinks="${alinks }" headerWidths="100%"
		 />
	</div>
</div>
<script type="text/javascript">
function viewdoc(id){
	 checkDocNode(id);
	
	//document.getElementById("has-docauth-list").href = "docauth/docHas"
	//document.getElementById("has-docauth-list").click();
}
function checkDocNode(id){
	var PATH = document.getElementById('APP_PATH').value;
	$.ajax({
		url:PATH+"/doc/checkDocNode",
		type:"POST",
		async : false,  
		//contentType:'application/json;charset=UTF-8',  
		data:{id:id},
		success:function(result){
			console.log(result);
			if(result.result =="1"){
				loadUri("#has-docauth-list","docauth/docHas?id="+id);
			}
		}
	}); 
}
</script>