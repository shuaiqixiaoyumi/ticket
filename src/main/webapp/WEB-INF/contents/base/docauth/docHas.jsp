<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- APP_PATH相当于webapp目录 -->
<body>
<%
	pageContext.setAttribute("APP_PATH", request.getContextPath());
%>
<link rel="stylesheet" href="${APP_PATH}/css/knowledge/css/style.css" />
<!-- 文本框 -->
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/css/normalize.css" />
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/fonts/font-awesome-4.2.0/css/font-awesome.min.css" />
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/css/demo.css" />

<!--必要样式-->
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/css/set.css" />
<!-- 动态提示框引入css和部分js -->
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/css/default.css" />
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/css/component.css" />


<!-- 动态提示框引入库  classie.js by @desandro: https://github.com/desandro/classie -->
<script src="${APP_PATH}/css/knowledge/js/classie.js"></script>
<script src="${APP_PATH}/css/knowledge/js/modalEffects.js"></script>
<script src="${APP_PATH}/css/knowledge/js/cssParser.js"></script>

<script src="${APP_PATH}/markdown/lib/marked.min.js"></script>
<script src="${APP_PATH}/markdown/lib/prettify.min.js"></script>

<script src="${APP_PATH}/markdown/lib/raphael.min.js"></script>
<script src="${APP_PATH}/markdown/lib/underscore.min.js"></script>
<script src="${APP_PATH}/markdown/lib/sequence-diagram.min.js"></script>
<script src="${APP_PATH}/markdown/lib/flowchart.min.js"></script>
<script src="${APP_PATH}/markdown/lib/jquery.flowchart.min.js"></script>

<!-- markdown -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/markdown/css/editormd.css" />
<script src="${pageContext.request.contextPath}/markdown/editormd.min.js"></script>
   

<script type="text/javascript" src="${APP_PATH}/js/knowledgebase/docHas.js"></script>
<script type="text/javascript" src="${APP_PATH}/js/knowledgebase/html2canvas.js"></script>
<script type="text/javascript" src="${APP_PATH}/js/knowledgebase/jsPdf.debug.js"></script>

<style type="text/css">
		@import url("${APP_PATH}/css/knowledge/css/style.css") print;
		@import url("${APP_PATH}/css/knowledge/css/demo.css") print;
		@import url("${APP_PATH}/css/knowledge/css/set.css") print;
		@import url("${APP_PATH}/css/knowledge/css/default.css") print;
		@import url("${APP_PATH}/css/knowledge/css/component.css") print;
		@import url("${APP_PATH}/markdown/css/editormd.css") print;
  </style>

<link rel="stylesheet" href="${APP_PATH}/css/knowledge/css/style.css" media="print"  />
<!-- 文本框 -->
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/css/normalize.css" media="print" />
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/fonts/font-awesome-4.2.0/css/font-awesome.min.css" media="print"  />
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/css/demo.css" media="print"  />

<!--必要样式-->
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/css/set.css" media="print"  />
<!-- 动态提示框引入css和部分js -->
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/css/default.css" media="print"  />
<link rel="stylesheet" type="text/css" href="${APP_PATH}/css/knowledge/css/component.css"  media="print" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/markdown/css/editormd.css"media="print"  />
<div id="hastop-title" style="position: relative">
        <textarea  id="hascopyurl" style="float:left;z-index: 99;position: absolute;width:5px;height:2px"  readonly value="" />
</div>
<div class="default-page-body">
<div id="wrap-content">
	<div >
		<button id="editdoc2" style="float:right"  data-title="修改文档" type="button" class="btn btn-primary cnoj-open-tabs" href="index/editpage?uuid=${uuid}"><i class="fa fa-pencil-square-o"></i>&nbsp;修改</button>
		<button id="downdoc2" style="float:right" onclick="download()" type="button" class="btn btn-primary" >下载pdf</button>
		<button id="copyurldoc2" style="float:right" onclick="copyurl()" type="button" class="btn btn-primary" >复制分享链接</button>
		<button id="deldoc2" style="float:right" onclick="deletedoc()" type="button" class="btn btn-primary" >删除</button>
		
	</div>
	<input id="App_PATH" type="hidden" value="${APP_PATH}" />
	<input id="docuuid" type="hidden" value="${uuid}" />
	<header>
		<center>
		<p id="doctitleName" class="art-title">加载中。。。</p>
		<p id="docremark">文章概况:</p>		
		</center>
	</header>
	<div id="test-editormd-docview2">
		
	</div>
</div>

</div>



<script type="text/javascript">
setTimeout("loadview()", 200);


</script>
</body>