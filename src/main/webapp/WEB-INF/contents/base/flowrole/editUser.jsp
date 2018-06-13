<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<div class="wrap-content-dialog">
    <form class="form-horizontal" role="form" id="form-edit" action="flowrole/updateFlowUser.json">
	    <input type="hidden" name="id" value="${id}" />
			<div class="form-group m-b-10">
			    <label for="input5" class="col-sm-2 control-label">序号</label>
			    <div class="col-sm-9 p-l-0">
			      <input type="text" id="input5" class="form-control require" data-label-name="序号" data-format="num" data-length="1,5" name="sortOrder" value="${sort_order }" />
			    </div>
			</div>
			<div class="text-center">
			      <button type="button" class="btn btn-primary cnoj-data-submit"  ><i class="glyphicon glyphicon-ok-sign"></i> 确定</button>
			</div>
	</form>
</div>