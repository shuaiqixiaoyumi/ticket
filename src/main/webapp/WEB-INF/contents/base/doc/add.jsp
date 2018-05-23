<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
 <div class="wrap-content-dialog">
	<form class="form-horizontal" role="form" id="form-add" action="doc/add.json" target="#main-content">
		    <div class="form-group m-b-10">
			    <label for="input01" class="col-sm-2 control-label">文档名称</label>
			    <div class="col-sm-9 p-l-0">
			      <input type="text" class="form-control require" name="name" data-label-name="文档名称" id="input01" />
			    </div>
			</div>
			<div class="form-group m-b-10">
			    <label for="input02" class="col-sm-2 control-label">结构代码</label>
			    <div class="col-sm-9 p-l-0">
			      <input type="text" class="form-control require" name="code" data-label-name="结构代码" id="input02" />
			    </div>
			</div>
			<div class="form-group m-b-10">
			    <label for="input03" class="col-sm-2 control-label">父级机构</label>
			    <div class="col-sm-9 p-l-0">
			      <input type="text" class="form-control require cnoj-input-tree" data-uri="op/queryTree/select_doc_tree.json" data-is-show-none="yes" name="parentId" value="${id }" id="input03" />
			    </div>
			</div>
			<div class="form-group m-b-10">
			    <label for="input04" class="col-sm-2 control-label">机构类型</label>
			    <div class="col-sm-9 p-l-0">
			       <select class="form-control cnoj-select" data-uri="dict/item/DOC_TYPE.json" name="type" id="input04"></select>
			    </div>
			</div>
			<div class="form-group m-b-10">
			    <label for="input05" class="col-sm-2 control-label">创建人</label>
			    <div class="col-sm-9 p-l-0">
			      <input type="text" id="input05" class="form-control" name="contacts" />
			    </div>
			</div>
			<div class="form-group m-b-10">
			    <label for="input06" class="col-sm-2 control-label">序号</label>
			    <div class="col-sm-9 p-l-0">
			      <input type="text" id="input06" class="form-control require" data-label-name="序号" data-format="num" data-length="1,5" name="sortOrder" value="${sortOrder }" />
			    </div>
			</div>
			
			<div class="text-center">
			      <button type="button" class="btn btn-primary cnoj-data-submit" data-refresh-uri="doc/list">
			      <i class="glyphicon glyphicon-ok-sign"></i> 确定</button>
			</div>
	</form>
</div><!-- wrap-content-dialog -->