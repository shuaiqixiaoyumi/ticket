<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="cnoj" uri="/cnoj-tags" %>
<div class="wrap-content">
	<div class="panel panel-default no-border">
	    <cnoj:table smartResp="${smartResp }" headers="模板名称,英文标识,启用状态" 
	    headerWidths="50%,42%,8%"  isCheckbox="1" isRowSelected="1" currentUri="${currentUri }" 
	    alinks="${alinks }" page="${pageParam }"
	     addBtn="${addBtn }" editBtn="${editBtn }" delBtn="${delBtn }" refreshBtn="${refreshBtn }"  
	    />
	</div>
</div>