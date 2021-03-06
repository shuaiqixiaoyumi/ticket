<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="cnoj" uri="/cnoj-tags" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/auth-config.js"></script>
<div class="wrap-content-dialog m-n5">
  <div class="auth-config-dialog-list">
	   <div class="panel-search">
	      <form class="form-inline" id="search-form-flowrole" method="post" role="form" action="user/addFlowRole" target=".bootstrap-dialog-message">
              <input type="hidden" name="id" value="${searchParam.id }" />
              <div class="form-group p-r-10">
				  <label for="search-input01">名称：</label>
				  <input type="text" class="form-control input-form-sm-control" id="search-input01" name="name" placeholder="请输入名称" value="${searchParam.name }"/>
			  </div>
			  <div class="form-group p-l-10">
				 <span class="btn btn-info btn-sm cnoj-search-submit">
					 <i class="glyphicon glyphicon-search"></i>
					 <span>搜索</span>
				 </span>
			 </div>
			 <div class="right p-r-5">
			   <button type="button" class="btn btn-sm btn-success cnoj-auth-config-submit" data-uri="user/saveFlowRole.json" data-config-id="${searchParam.id}" data-alert-msg="请选择要添加的角色！" data-refresh-uri="user/flowrolelist?id=${searchParam.id}" data-refresh-target="#user-flowrole-tab" > &nbsp;<i class="glyphicon glyphicon-ok-sign"></i> 确定 &nbsp;</button>
		     </div>
          </form>
	   </div><!-- panel-search -->
	   <cnoj:table smartResp="${smartResp }" headers="名称,英文名称,描述" isCheckbox="1" isRowSelected="1" page="${pageParam }" />
   </div>
</div><!-- wrap-content-dialog -->