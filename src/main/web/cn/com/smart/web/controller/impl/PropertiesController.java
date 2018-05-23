package cn.com.smart.web.controller.impl;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.filter.bean.FilterParam;
import cn.com.smart.web.bean.RequestPage;
import cn.com.smart.web.bean.entity.TPfproperties;
import cn.com.smart.web.controller.base.BaseController;
import cn.com.smart.web.service.OPService;
import cn.com.smart.web.service.PropertiesService;
import cn.com.smart.web.service.SubPropertiesService;
import cn.com.smart.web.tag.bean.ALink;
import cn.com.smart.web.tag.bean.DelBtn;
import cn.com.smart.web.tag.bean.EditBtn;
import cn.com.smart.web.tag.bean.PageParam;
import cn.com.smart.web.tag.bean.RefreshBtn;
@Controller
@RequestMapping("/properties")
public class PropertiesController extends BaseController {

	private static final String VIEW_PRO = WEB_BASE_VIEW_DIR+"/properties/";
	@Autowired
    private OPService opServ;
	
	@Autowired
    private PropertiesService propertiesService;
	
	
	
	
	 /**
     * 表单实例列表
     * @param request
     * @param searchFilter
     * @param page
     * @return
     */
    @RequestMapping("/list")
    public ModelAndView list(HttpServletRequest request, FilterParam searchFilter, RequestPage page) {
        ModelAndView modelView = new ModelAndView();
        SmartResponse<Object> smartResp = opServ.getDatas("get_form_properties_list", searchFilter, page.getStartNum(), page.getPageSize());
//        String uri = HttpRequestHelper.getCurrentUri(request);
        String uri ="properties/list";
        refreshBtn = new RefreshBtn(uri, "properties", null);
//        addBtn = new EditBtn("add", "form/instance/create?formId=U152628756522343831", null, "新增数据", null);
        editBtn = new EditBtn("edit", "form/instance/edit", null, "修改表单数据", null);
        delBtn = new DelBtn("form/instance/delete", "您确定要删除选中的表单数据吗？", uri, null, null);
        ALink alink = new ALink("form/instance/view", null, "查看表单信息");
        alink.setParamIndex("4,5");
        alink.setParamName("formId,formDataId");
        alinks = new ArrayList<ALink>(1);
        alinks.add(alink);
        pageParam = new PageParam(uri, null, page.getPageSize());
        
        ModelMap modelMap = modelView.getModelMap();
        modelMap.put("smartResp", smartResp);
        modelMap.put("refreshBtn", refreshBtn);
//        modelMap.put("addBtn", addBtn);
        modelMap.put("delBtn", delBtn);
        modelMap.put("editBtn", editBtn);
        modelMap.put("alinks", alinks);
        modelMap.put("pageParam", pageParam);
        modelView.setViewName(VIEW_PRO+"list");
        return modelView;
    }
    
    @RequestMapping("/isValidByCode")
    public boolean isValidByCode(String code) {
    	boolean result = true;
    	TPfproperties tPfproperties = propertiesService.getByCode(code);
    	if(tPfproperties.getStatus() ==1) {
    		result =false;
    	}
    	return result;
    }
    
    
}
