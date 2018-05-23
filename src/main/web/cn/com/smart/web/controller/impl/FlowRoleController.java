package cn.com.smart.web.controller.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mixsmart.utils.StringUtils;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.filter.bean.FilterParam;
import cn.com.smart.web.bean.RequestPage;
import cn.com.smart.web.bean.SubmitDataBean;
import cn.com.smart.web.bean.UserInfo;
import cn.com.smart.web.bean.entity.TNFlowrole;
import cn.com.smart.web.bean.entity.TNUser;
import cn.com.smart.web.constant.enums.SelectedEventType;
import cn.com.smart.web.controller.base.BaseController;
import cn.com.smart.web.service.FlowroleService;
import cn.com.smart.web.service.OPService;
import cn.com.smart.web.service.UserService;
import cn.com.smart.web.tag.bean.DelBtn;
import cn.com.smart.web.tag.bean.EditBtn;
import cn.com.smart.web.tag.bean.PageParam;
import cn.com.smart.web.tag.bean.RefreshBtn;
import cn.com.smart.web.tag.bean.SelectedEventProp;

@Controller
@RequestMapping("/flowrole")
public class FlowRoleController extends BaseController{

	private static final String VIEW_DIR = WEB_BASE_VIEW_DIR+"/flowrole";
	
	@Autowired
	private OPService opServ;
	
	@Autowired
	private FlowroleService flowroleServ;
	
	@Autowired
	private UserService userServ;
	
	@RequestMapping("/list")
	public ModelAndView list(HttpSession session,ModelAndView modelView,RequestPage page) {
		Map<String,Object> params = null;
		UserInfo userInfo = getUserInfoFromSession(session);
		if(!isSuperAdmin(userInfo)) {
			params = new HashMap<String, Object>(1);
			params.put("roleIds", userInfo.getRoleIds().toArray());
		}
		SmartResponse<Object> smartResp = opServ.getDatas("flowrole_mgr_list", params, page.getStartNum(), page.getPageSize());
		
		String uri = "flowrole/list"; 
		addBtn = new EditBtn("add","showPage/base_flowrole_add", "添加角色", "600");
		editBtn = new EditBtn("edit","showPage/base_flowrole_edit", "flowrole", "修改角色", "600");
		delBtn = new DelBtn("flowrole/delete.json", "确定要删除选中的角色吗？",uri,null, null);
		refreshBtn = new RefreshBtn(uri, null,null);
		pageParam = new PageParam(uri, null, page.getPage(), page.getPageSize());
		
//		alinks = new ArrayList<ALink>();
//		ALink link = new ALink();
//		link.setUri("flowrole/show");
//		link.setDialogTitle("已配置的权限");
//		link.setDialogWidth("600");
//		alinks.add(link);
		
		ModelMap modelMap = modelView.getModelMap();
		modelMap.put("smartResp", smartResp);
		modelMap.put("addBtn", addBtn);
		modelMap.put("editBtn", editBtn);
		modelMap.put("delBtn", delBtn);
		modelMap.put("refreshBtn", refreshBtn);
		modelMap.put("pageParam", pageParam);
//		modelMap.put("alinks", alinks);

		modelView.setViewName(VIEW_DIR+"/list");
		return modelView;
	}
	
	
	@RequestMapping(value="/add",method=RequestMethod.POST)
	public @ResponseBody SmartResponse<String> add(HttpSession session,TNFlowrole role,
			String[] menuId,SubmitDataBean submitBean) throws Exception {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		if(null != role) {
			role.setUserId(getUserInfoFromSession(session).getId());
			smartResp = flowroleServ.save(role);
		}
		return smartResp;
	}
	
	@RequestMapping(value="/edit",method=RequestMethod.POST)
	public @ResponseBody SmartResponse<String> edit(TNFlowrole role) throws Exception {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		if(null != role) {
			smartResp = flowroleServ.update(role);
		}
		return smartResp;
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	@ResponseBody
	public SmartResponse<String> delete(String id) {
		return flowroleServ.delete(id);
	}
	
	/**
	 * 查看角色信息
	 * @param modelView
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/show")
	public ModelAndView show(ModelAndView modelView,String id) throws Exception {
		modelView.getModelMap().put("id", id);
		modelView.setViewName(VIEW_DIR+"/show");
		return modelView;
	}
	
	/**
	 * 简单角色列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/simplist")
	public ModelAndView simplist(HttpSession session,FilterParam searchParam,
			ModelAndView modelView,RequestPage page) throws Exception {
		String uri = "flowrole/simplist";
		UserInfo userInfo = getUserInfoFromSession(session);
		if(!isSuperAdmin(userInfo)) {
			if(null == searchParam) {
				searchParam = new FilterParam();
			}
			searchParam.setRoleIds(StringUtils.list2Array(userInfo.getRoleIds()));
		}
		SmartResponse<Object> smartResp = opServ.getDatas("flowrole_simp_list",searchParam, page.getStartNum(), page.getPageSize());
		pageParam = new PageParam(uri, "#flowrole-tab", page.getPage(), page.getPageSize());
		selectedEventProp = new SelectedEventProp(SelectedEventType.OPEN_TO_TARGET.getValue(),"flowauth/roleHas","#has-flowauth-list","id");	
		
		ModelMap modelMap = modelView.getModelMap();
		modelMap.put("smartResp", smartResp);
		modelMap.put("pageParam", pageParam);
		modelMap.put("selectedEventProp", selectedEventProp);
		modelMap.put("searchParam", searchParam);
		
		modelView.setViewName(VIEW_DIR+"/simplist");
		return modelView;
	}
	
	/**
	 * 拥有该角色的用户列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/userlist")
	public ModelAndView userlist(FilterParam searchParam,ModelAndView modelView,RequestPage page) throws Exception {
		String uri = "flowrole/userlist";
		SmartResponse<Object> smartResp = opServ.getDatas("flowrole_user_list",searchParam, page.getStartNum(), page.getPageSize());
		pageParam = new PageParam(uri, "#flowrole-user-tab", page.getPage(), page.getPageSize());
		uri = uri+"?id="+searchParam.getId();
		addBtn = new EditBtn("add","flowrole/addUser?id="+searchParam.getId(), null, "该角色中添加用户", "600");
		delBtn = new DelBtn("flowrole/deleteUser?roleId="+searchParam.getId(), "确定要从该角色中删除选中的用户吗？",uri,"#flowrole-user-tab", null);
		refreshBtn = new RefreshBtn(uri, "flowroleUser","#flowrole-user-tab");
		ModelMap modelMap = modelView.getModelMap();
		modelMap.put("smartResp", smartResp);
		modelMap.put("pageParam", pageParam);
		modelMap.put("addBtn", addBtn);
		modelMap.put("delBtn", delBtn);
		modelMap.put("refreshBtn", refreshBtn);
		modelMap.put("searchParam", searchParam);
		
		modelView.setViewName(VIEW_DIR+"/userlist");
		return modelView;
	}
	
	/**
	 * 从角色中删除用户
	 * @param roleId 角色ID
	 * @param id 用户ID
	 * @return
	 */
	@RequestMapping(value="/deleteUser", produces="application/json;charset=UTF-8")
	@ResponseBody
	public SmartResponse<String> deleteUser(String roleId, String id) {
	    return flowroleServ.deleteUser(roleId, id);
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/addUser")
	public ModelAndView addUser(FilterParam searchParam,ModelAndView modelView,RequestPage page) throws Exception {
		String uri = "flowrole/addUser";
		SmartResponse<Object> smartResp = opServ.getDatas("flowrole_adduser_list",searchParam, page.getStartNum(), page.getPageSize());
		pageParam = new PageParam(uri, ".bootstrap-dialog-message", page.getPage(), page.getPageSize());
		
		ModelMap modelMap = modelView.getModelMap();
		modelMap.put("smartResp", smartResp);
		modelMap.put("pageParam", pageParam);
		modelMap.put("searchParam", searchParam);
		
		modelView.setViewName(VIEW_DIR+"/addUser");
		return modelView;
	}
	
	@RequestMapping(value="/saveUser",method=RequestMethod.POST)
	public @ResponseBody SmartResponse<String> saveUser(String submitDatas,String id) throws Exception {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		if(StringUtils.isNotEmpty(submitDatas) && StringUtils.isNotEmpty(id)) {
			String[] values = submitDatas.split(",");
			smartResp = flowroleServ.addUser2Role(id, values);
		}
		return smartResp;
	}
	
	
}
