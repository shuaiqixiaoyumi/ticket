package cn.com.smart.web.controller.impl;

import javax.servlet.http.HttpServletRequest;

import org.snaker.engine.helper.JsonHelper;
import org.snaker.engine.model.TaskModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mixsmart.utils.CollectionUtils;
import com.mixsmart.utils.StringUtils;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.flow.ProcessContext;
import cn.com.smart.flow.SnakerEngineFacets;
import cn.com.smart.flow.bean.OutputClassify;
import cn.com.smart.flow.bean.TaskInfo;
import cn.com.smart.flow.ext.ExtProcess;
import cn.com.smart.flow.helper.ProcessHelper;
import cn.com.smart.flow.service.FlowFormService;
import cn.com.smart.flow.service.ProcessFacade;
import cn.com.smart.form.bean.QueryFormData;
import cn.com.smart.form.bean.entity.TForm;
import cn.com.smart.form.service.IFormDataService;
import cn.com.smart.web.bean.UserInfo;
import cn.com.smart.web.controller.base.BaseController;

/**
 * 注册
 * @author zhanglb
 *
 */
@Controller
@RequestMapping("/register")
public class RegisterController  extends BaseController{

	private String VIEW_DIR = "/";
	
	@Autowired
	private SnakerEngineFacets facets;
	@Autowired
	private ProcessFacade processFacade;
	@Autowired
	private FlowFormService flowFormServ;
	
	@Autowired
	private ProcessContext processContext;
	@Autowired
	private IFormDataService formDataServ;
	
	public ModelAndView register(HttpServletRequest request,ModelAndView modelView,TaskInfo taskInfo) {
		modelView.setViewName(VIEW_DIR+"register");
		ModelMap modelMap = modelView.getModelMap();
		modelMap.put("refreshUrl", taskInfo.getRefreshUrl());
		if(null == taskInfo || (StringUtils.isEmpty(taskInfo.getProcessId())) 
				&& StringUtils.isEmpty(taskInfo.getProcessName())) {
			return modelView;
		}
		UserInfo userInfo = getUserInfoFromSession(request);
		ProcessHelper.initProcessNameOrId(facets, taskInfo);
	    //获取流程表单：如表单生成的HTML源码
	    SmartResponse<TForm> smartResp = processFacade.getForm(taskInfo.getProcessId());
	    modelMap.put("smartResp", smartResp);
	    ExtProcess process = facets.getProcess(taskInfo.getProcessId());
	    if(null != process) {
	    	modelMap.put("isAtt", process.getAttachment());
	    }
	    //流程出口线分类,通过该属性可以判断出，当前节点是否有驳回按钮或有几条驳回线(正常出口线)等
	    TaskModel model = facets.getTaskModel(taskInfo.getProcessId(), taskInfo.getTaskKey());
	    modelMap.put("taskModel", model);
	    OutputClassify outputClassify = processFacade.outputClassify(model.getOutputs());
	    modelMap.put("outputClassify", outputClassify);
	    if(null != outputClassify && CollectionUtils.isNotEmpty(outputClassify.getBackLines())) {
	    	if(outputClassify.getBackLines().size()==1) {
	    		modelMap.put("backName", outputClassify.getBackLines().get(0).getName());
	    	}
	    }
	    int firstNode = 0;
	    if(!OP_SUCCESS.equals(smartResp.getResult())) {
	    	return modelView;
	    }
	    SmartResponse<QueryFormData> chRes = processFacade.getFormData(smartResp.getData().getId(), taskInfo.getOrderId(), userInfo.getId());
	    smartResp = null;
	    //流程实例ID(orderId)为空时，表示当前流程实例还未启动，流程在第一个节点
	    if(StringUtils.isEmpty(taskInfo.getOrderId())) {
	        firstNode = 1;
	    } else {
	        //获取起草人(拟稿人)和实例标题
	        String[] array = processFacade.getNameAndTitle(taskInfo.getOrderId());
	        if(null != array && array.length>1) {
	        	modelMap.put("title", array[0]);
	        	modelMap.put("creator", array[1]);
	        }
	    }
	    if(OP_SUCCESS.equals(chRes.getResult())) {
	    	//获取表单数据ID
			if(null != chRes.getData())
				modelMap.put("formDataId", chRes.getData().getValue());
	    }//if
	    String output = JsonHelper.toJson(chRes);
    	output = StringUtils.repaceSpecialChar(output);
    	output = StringUtils.repaceSlash(output);
    	chRes = null;
    	modelMap.put("output", output);
    	modelMap.put("taskInfo", taskInfo);
	    modelMap.put("firstNode", firstNode);
	    chRes = null;
		return modelView;
	}
}
