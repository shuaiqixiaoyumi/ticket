package cn.com.smart.web.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mixsmart.utils.StringUtils;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.web.bean.entity.TNFlowrole;
import cn.com.smart.web.bean.entity.TNRole;
import cn.com.smart.web.bean.entity.TNUser;
import cn.com.smart.web.controller.base.BaseController;
import cn.com.smart.web.service.FlowroleService;
import cn.com.smart.web.service.OPService;

@Controller
@RequestMapping("/flowauth")
public class FlowauthController extends BaseController {

	private static final String VIEW_DIR = WEB_BASE_VIEW_DIR+"/flowauth";
	
	@Autowired
	private FlowroleService flowroleServ;
	
	@Autowired
	private OPService opServ;
	
	@RequestMapping("/index")
	public ModelAndView index(ModelAndView modelView) throws Exception {
		modelView.setViewName(VIEW_DIR+"/index");
		return modelView;
	}
	
	@RequestMapping("/userHas")
	public ModelAndView userHas(ModelAndView modelView,String id) throws Exception {
		if(StringUtils.isNotEmpty(id)) {
			SmartResponse<Object> smartResp = opServ.find(TNUser.class, id);
			ModelMap modelMap = modelView.getModelMap();
			modelMap.put("id", id);
			if(smartResp.getResult().equals(OP_SUCCESS)) {
				TNUser user = (TNUser) smartResp.getData();
				String name = (null != user)?user.getFullName():null;
				modelMap.put("name", name);
				user = null;
			}
			smartResp = null;
		}
		modelView.setViewName(VIEW_DIR+"/userHas");
		return modelView;
	}
	
	@RequestMapping("/roleHas")
	public ModelAndView roleHas(ModelAndView modelView,String id) throws Exception {
		if(StringUtils.isNotEmpty(id)) {
			SmartResponse<Object> smartResp = flowroleServ.find(TNFlowrole.class, id);
			
			ModelMap modelMap = modelView.getModelMap();
			modelMap.put("id", id);
			if(smartResp.getResult().equals(OP_SUCCESS)) {
				TNFlowrole role = (TNFlowrole) smartResp.getData();
				String name = (null != role)?role.getName():null;
				modelMap.put("name", name);
				role = null;
			}
			smartResp = null;
		}
		modelView.setViewName(VIEW_DIR+"/roleHas");
		return modelView;
	}

}
