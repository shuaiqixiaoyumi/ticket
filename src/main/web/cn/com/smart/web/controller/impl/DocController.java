package cn.com.smart.web.controller.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mixsmart.utils.StringUtils;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.web.bean.UserInfo;
import cn.com.smart.web.bean.entity.TNDoc;
import cn.com.smart.web.constant.enums.SelectedEventType;
import cn.com.smart.web.controller.base.BaseController;
import cn.com.smart.web.plugins.DocZTreeData;
import cn.com.smart.web.plugins.ZTreeData;
import cn.com.smart.web.service.DocService;
import cn.com.smart.web.service.OPService;
import cn.com.smart.web.service.RoleDocService;
import cn.com.smart.web.tag.bean.ALink;
import cn.com.smart.web.tag.bean.DelBtn;
import cn.com.smart.web.tag.bean.EditBtn;
import cn.com.smart.web.tag.bean.RefreshBtn;
import cn.com.smart.web.tag.bean.SelectedEventProp;
/**
 * 文档结构
 * @author zhanglb
 *
 */
@Controller
@RequestMapping("doc")
public class DocController extends BaseController {
	
	private static final String VIEW_DIR = WEB_BASE_VIEW_DIR+"/doc";
		
	@Autowired
	private OPService opServ;
	
	@Autowired
	private DocService docServ;
	
	@Autowired
	private RoleDocService roleDocServ;

	@RequestMapping("list")
	public ModelAndView list(ModelAndView modelView) throws Exception {
		SmartResponse<Object> smartResp = opServ.getTreeDatas("doc_mgr_tree_list");
		
		String uri = "doc/list"; 
		addBtn = new EditBtn("add","showPage/base_doc_add", "doc", "添加文档结构", "600");
		editBtn = new EditBtn("edit","showPage/base_doc_edit", "doc", "修改文档结构", "600");
		delBtn = new DelBtn("doc/delete.json", "确定要删除选中的文档吗？（注：如果该文档结构下有文档的话，会一起删除的哦~）",uri,null, null);
		refreshBtn = new RefreshBtn(uri, "org",null);
		
		ModelMap modelMap = modelView.getModelMap();
		modelMap.put("smartResp", smartResp);
		modelMap.put("addBtn", addBtn);
		modelMap.put("editBtn", editBtn);
		modelMap.put("delBtn", delBtn);
		modelMap.put("refreshBtn", refreshBtn);
		modelView.setViewName(VIEW_DIR+"/list");
		return modelView;
	}
	
	/**
	 * 添加文档架构
	 * @param session
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/add",method=RequestMethod.POST)
	public @ResponseBody SmartResponse<String> add(HttpSession session, TNDoc doc) throws Exception {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		if(null != doc) {
			smartResp = docServ.save(doc);
			if(OP_SUCCESS.equals(smartResp.getResult())) {
				
			}
		}
		return smartResp;
	}
	
	@RequestMapping(value="/edit",method=RequestMethod.POST)
	public @ResponseBody SmartResponse<String> edit(TNDoc doc) throws Exception {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		if(null != doc) {
			smartResp = docServ.update(doc);
		}
		return smartResp;
	}
	
	
	/**
	 * 删除
	 * @param session
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	@ResponseBody
	public SmartResponse<String> delete(HttpSession session, String id) {
		SmartResponse<String> smartResp = docServ.delete(id);
		if(OP_SUCCESS.equals(smartResp.getResult())) {
			
		}
		return smartResp;
	}
	
	/**
	 * 验证时否文档节点
	 * @param session
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/checkDocNode",method=RequestMethod.POST)
	@ResponseBody
	public SmartResponse<Object> checkDocNode(HttpSession session, String id) {
		SmartResponse<Object> smartResp = opServ.find(TNDoc.class, id);
		if(smartResp.getResult().equals(OP_SUCCESS)) {
			TNDoc doc = (TNDoc) smartResp.getData();
			String doctype= doc.getType();
			if(!doctype.equals("doc")) {
				smartResp.setResult(OP_FAIL);
			}
		}
		return smartResp;
	}
	
	
	
	@RequestMapping("simplist")
	public ModelAndView simplist(HttpSession session,ModelAndView modelView) throws Exception {
		UserInfo user = getUserInfoFromSession(session);
		Map<String, Object> map = new HashMap<>();
		map.put("userId", user.getId());
		SmartResponse<Object> smartResp = opServ.getTreeDatas("doc_mgr_tree_list_byuser",map);
		
		String uri = "doc/simplist"; 
		alinks = new ArrayList<ALink>();
		ALink link = new ALink();
		link.setUri("flow/show");
		link.setDialogTitle("查看流程图");
		link.setDialogWidth("");
		ALink link2 = new ALink();
		link2.setUri("flow/show");
		link2.setDialogTitle("查看流程图");
		link2.setDialogWidth("");
		link2.setLinkPostion("1");
		alinks.add(link);
		alinks.add(link2);
		link = null;link2 = null;
		selectedEventProp = new SelectedEventProp(SelectedEventType.OPEN_TO_TARGET.getValue(),"docauth/docHas","#has-docauth-list","id");	

		ModelMap modelMap = modelView.getModelMap();
		modelMap.put("smartResp", smartResp);
		modelMap.put("alinks", alinks);
		modelMap.put("selectedEventProp", selectedEventProp);
		modelView.setViewName(VIEW_DIR+"/simplist");
		return modelView;
	}
	
	@RequestMapping("/tree")
	public @ResponseBody SmartResponse<DocZTreeData> tree(HttpSession session,String treeType) throws Exception {
		return docServ.getZTree();
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/docTreeSelect")
	public @ResponseBody SmartResponse<ZTreeData> docTreeSelect(String id) throws Exception {
		SmartResponse<ZTreeData> smartResp = null;
		if(StringUtils.isNotEmpty(id)) {
			smartResp = roleDocServ.docTree(id);
		} else {
			smartResp = roleDocServ.docTree();
		}
		return smartResp;
	}
	
	@RequestMapping(value="/selectDocResAuth")
	public @ResponseBody SmartResponse<ZTreeData>  selectDocResAuth(String id) throws Exception {
		return docServ.selectDocResAuth(id);
	}
	
}
