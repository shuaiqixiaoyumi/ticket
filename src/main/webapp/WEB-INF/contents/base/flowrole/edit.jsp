<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<div class="wrap-content-dialog">
    <form class="form-horizontal" role="form" id="form-edit" action="flowrole/edit.json" target="#main-content">
		 <input type="hidden" name="id" value="${objBean.id}" />
		    <div class="form-group m-b-10">
			    <label for="input01" class="col-sm-2 control-label">角色中文名称</label>
			    <div class="col-sm-9 p-l-0">
			      <input type="text" class="form-control require" name="name" data-label-name="角色中文名称" value="${objBean.name }" id="input01" />
			    </div>
			</div>
			<div class="form-group m-b-10">
			    <label for="input02" class="col-sm-2 control-label">角色英文名称</label>
			    <div class="col-sm-9 p-l-0">
			      <input type="text" class="form-control require" name="code" data-label-name="角色英文名称" value="${objBean.code }" id="input02" />
			    </div>
			</div>
			<div class="form-group m-b-10">
			    <label for="input03" class="col-sm-2 control-label">状态</label>
			    <div class="col-sm-9 p-l-0">
			       <select class="form-control" name="state" id="input03" >
			            <option value="1" ${objBean.state==1?'selected':''}>有效</option>
					    <option value="0" ${objBean.state==0?'selected':''}>无效</option>
			       </select>
			    </div>
			</div>
			<div class="form-group m-b-10">
			    <label for="input04" class="col-sm-2 control-label">描述</label>
			    <div class="col-sm-9 p-l-0">
			      <textarea class="form-control" name="descr" rows="3" id="input04">${objBean.descr }</textarea>
			    </div>
			</div>
			
			<div class="text-right p-t-3 p-r-10">
			      <button type="button" class="btn btn-primary" id="flowrole-submit" data-refresh-uri="flowrole/list" ><i class="glyphicon glyphicon-ok-sign"></i> 确定</button>
			</div>
	</form>
</div>
<script type="text/javascript">
   setTimeout("loadRoleJs()", 200);
   function loadRoleJs() {
	   $("#flowrole-submit").click(function(event){
		   var $form = $(this).parents("form");
		   if($form.validateForm({placement:"bottom"})) {
			   //提交数据
			   var params = $form.serialize();
			   var uri = $form.attr("action");
			   cnoj.submitDialogData(uri,params,null,$(this),$form);
		   }
		   event.stopPropagation();
		   return false;
	   });
   }
</script>