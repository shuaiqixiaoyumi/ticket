<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cnoj" uri="/cnoj-tags" %>
<div class="wrap-content-dialog">
   <div class="version-view" >
       <!-- table -->
		<cnoj:table smartResp="${smartResp }" headers="用户名,姓名,单位,手机号码,QQ,邮箱,职位,状态,创建时间" 
		headerWidths="10%,12%,15%,10%,10%,16%,10%,5%,12%" 
		  isCheckbox="1" isRowSelected="1" currentUri="${currentUri }" 
		/>
   		
   </div>
</div><!-- wrap-content-dialog -->