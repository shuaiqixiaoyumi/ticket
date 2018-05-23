package cn.com.smart.web.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mixsmart.utils.StringUtils;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.web.bean.entity.TNDoc;
import cn.com.smart.web.bean.entity.TNOrg;
import cn.com.smart.web.controller.base.BaseController;
import cn.com.smart.web.service.OPService;
/**
 * 
 * @author zhanglb
 *
 */
@Controller
@RequestMapping("/docauth")
public class DocAuthController extends BaseController {
	private static final String VIEW_DIR = WEB_BASE_VIEW_DIR+"/docauth";
	@Autowired
	private OPService opServ;
	
	@RequestMapping("/index")
	public ModelAndView index(ModelAndView modelView) throws Exception {
		modelView.setViewName(VIEW_DIR+"/index");
		return modelView;
	}
	
	@RequestMapping("/docHas")
	public ModelAndView docHas(ModelAndView modelView,String id) throws Exception {
		if(StringUtils.isNotEmpty(id)) {
			SmartResponse<Object> smartResp = opServ.find(TNDoc.class, id);
			ModelMap modelMap = modelView.getModelMap();
			modelMap.put("uuid", id);
			if(smartResp.getResult().equals(OP_SUCCESS)) {
				TNDoc doc = (TNDoc) smartResp.getData();
				id= doc.getCode();
				String name = (null != doc)?doc.getName():null;
				modelMap.put("name", name);
				doc = null;
			}
			smartResp = null;
		}
		modelView.addObject("uuid", id);
//		modelView.setViewName("knowledgebase/viewpage");
		modelView.setViewName(VIEW_DIR+"/docHas");
		return modelView;
	}
}
