<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<div class="wrap-content">
  <div class="container-fluid">
	   <div class="row">
		   <div class="col-sm-4 p-l-0 p-r-0">
		      <div class="panel panel-default m-b-0">
				    <div class="panel-tabs-wrap" id="config-flowauth-tabs">
				         <div class="panel-heading p-0 p-t-3">
							<div class="panel-tabs-tab">
								<ul class="nav nav-tabs" role="tablist">
									<li class="active"><a href="#flowrole-tab" role="presentation" data-toggle="tab">角色</a></li>
									<li><a href="#flowuser-tab" role="presentation" data-toggle="tab">用户</a></li>
								</ul>
							</div>
						</div>
						<div class="panel-body p-0">
							<div class="tab-content panel-tab-content bg-color-white cnoj-auto-limit-height" style="padding: 0">
							  	<div role="tabpanel" class="tab-pane active" id="flowrole-tab">
							  	   <div class="cnoj-load-url" data-uri="flowrole/simplist" ></div>
								</div>
								<div role="tabpanel" class="tab-pane" id="flowuser-tab">
								   <div class="cnoj-load-url" data-uri="user/flowsimplist" ></div>
								</div>
							</div>
						</div>
					</div>
				</div><!-- panel -->
			</div><!-- col -->
			<div class="col-sm-8 p-l-5 p-r-0">
			    <div id="has-flowauth-list">
			       <div class="panel panel-default m-b-0">
			        <div class="panel-heading ui-widget-header">已拥有的权限</div>
			        <div class="panel-body p-b-0">
			           <div class="cnoj-auto-limit-height"></div>
			        </div>
			      </div>
			  </div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
	  $(function(){
		  var isResize = false;
		  $("#config-flowauth-tabs").find('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
			  var id = $(e.target).attr("href");
			  var $tag = $(id);
			  tableWrapListener($tag, isResize);
			  limitHeightListener($tag, isResize);
		  });
		  $(window).resize(function(){
			  isResize = true; 
	      });
	  });
	</script>
</div>
