package cn.com.smart.web.controller.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mixsmart.utils.StringUtils;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.utils.DbBase;
import cn.com.smart.web.bean.UserInfo;
import cn.com.smart.web.bean.entity.TNDict;
import cn.com.smart.web.constant.IActionConstant;
import cn.com.smart.web.controller.base.BaseController;
import cn.com.smart.web.service.DictService;
import cn.com.smart.web.service.OPService;
import cn.com.smart.web.tag.bean.DelBtn;
import cn.com.smart.web.tag.bean.EditBtn;
import cn.com.smart.web.tag.bean.RefreshBtn;

/**
 * 数据字典
 * @author lmq
 *
 */
@Controller
@RequestMapping("/dict")
public class DictController extends BaseController {

	private static final String VIEW_DIR = WEB_BASE_VIEW_DIR+"/dict";
	
	DbBase db = new  DbBase();
	
	@Autowired
	private DictService dictServ;
	
	@Autowired
	private OPService opServ;
	
	@RequestMapping("list")
	public ModelAndView list(ModelAndView modelView) throws Exception {
		SmartResponse<Object> smartResp = dictServ.findObjAll();
		
		String uri = "dict/list"; 
		addBtn = new EditBtn("add","showPage/base_dict_add", "dict", "添加数据字典", "600");
		editBtn = new EditBtn("edit","showPage/base_dict_edit", "dict", "修改数据字典", "600");
		delBtn = new DelBtn("dict/delete.json", "确定要删除选中的数据字典吗？",uri,null, null);
		refreshBtn = new RefreshBtn(uri, "dict",null);
		
		ModelMap modelMap = modelView.getModelMap();
		modelMap.put("smartResp", smartResp);
		modelMap.put("addBtn", addBtn);
		modelMap.put("editBtn", editBtn);
		modelMap.put("delBtn", delBtn);
		modelMap.put("refreshBtn", refreshBtn);
		addBtn = null;editBtn = null;delBtn = null;
		refreshBtn = null;
		modelView.setViewName(VIEW_DIR+"/list");
		return modelView;
	}
	
	@RequestMapping(value="/add",method=RequestMethod.POST)
	public @ResponseBody SmartResponse<String> add(TNDict dict) throws Exception {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		if(null != dict) {
			smartResp = dictServ.save(dict);
		}
		return smartResp;
	}
	
	@RequestMapping(value="/edit",method=RequestMethod.POST)
	public @ResponseBody SmartResponse<String> edit(TNDict dict) throws Exception {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		if(null != dict) {
			smartResp = dictServ.update(dict);
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
		return dictServ.delete(id);
	}
	
	/**
	 * 获取数据字典内容
	 * @param busiValue
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/item/{busiValue}")
	public @ResponseBody SmartResponse<Object> item(@PathVariable String busiValue,String name) throws Exception {
		SmartResponse<Object> smartResp = new SmartResponse<Object>();
		if(StringUtils.isNotEmpty(busiValue)) {
			smartResp = dictServ.getItem(busiValue, name);
		}
		return smartResp;
	}
	
	
	/**
	 * 获取数据字典内容
	 * @param busiValue
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/itemById/{id}")
	public @ResponseBody SmartResponse<Object> itemById(@PathVariable String id,String name) throws Exception {
		SmartResponse<Object> smartResp = new SmartResponse<Object>();
		if(StringUtils.isNotEmpty(id)) {
			smartResp = dictServ.getItemById(id, name);
		}
		return smartResp;
	}
	
	/**
	 * 根据父节点的值获取数据字典子节点内容
	 * @param busiValue
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/findByParentName")
	public @ResponseBody Map<String,Object> findByParentName(@RequestParam Map<String, Object> map, HttpServletRequest request) throws Exception {
		String busiValue = map.get("busiValue")+"";
		Map<String, Object> remap = new HashMap<>();
		UserInfo userInfo = (UserInfo) request.getSession().getAttribute(IActionConstant.SESSION_USER_KEY);
		if(StringUtils.isNotEmpty(busiValue)) {
//			StringBuffer sqlbuffer = new StringBuffer("select n.* from ( ");
//			sqlbuffer.append(" select count(file_type) as filecount , file_type from t_n_markdown  ");
//			sqlbuffer.append("  group by file_type order by filecount desc ) t ");
//			sqlbuffer.append(" left join t_n_dict n on t.file_type = n.busi_value where n.parent_id in (select id from t_n_dict where busi_value = ?) ");
			String sql = "select * from t_n_dict where parent_id = (select id from t_n_dict where busi_value = ?)";
//			System.out.println(sqlbuffer.toString());
			List<Map<String, Object>> list = db.queryForList(sql, new Object[] {busiValue});
			if(list.size() >0) {
	    		remap.put("result", "1");
	    		remap.put("msg", list);
	    	}
		}
		return remap;
	}
	
	public boolean checkByUserIdAndRoleName (String roleName, String userId) {
		String sql = "select * from t_n_role r, t_n_role_user u where  u.role_id = r.id and (r.name = ? or r.flag= 'super_admin')  and u.USER_ID = ?";
		List<Map<String, Object>> list = db.queryForList(sql, new Object[] {roleName, userId});
		if(list.size() >0) {
			return true;
		}else {
			return false;
		}
	}
	
	
	/**
	 * 根据父节点的值获取数据字典子节点内容
	 * @param busiValue
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/findByParentNameAndRole")
	public @ResponseBody Map<String,Object> findByParentNameAndRole(@RequestParam Map<String, Object> map, HttpServletRequest request) throws Exception {
		String busiValue = map.get("busiValue")+"";
		Map<String, Object> remap = new HashMap<>();
		UserInfo userInfo = (UserInfo) request.getSession().getAttribute(IActionConstant.SESSION_USER_KEY);
		List<String> roleIds = userInfo.getRoleIds();
		System.out.println("userInfo.getRoleIds()="+userInfo.getRoleIds());
		
		boolean isPublic = true;
		if(!checkByUserIdAndRoleName("内部文档成员",userInfo.getId()) && !"一线值班人".equals(userInfo.getDeptName()) && !userInfo.getUsername().equals("admin")) {
			isPublic = false; 
		}
		if(StringUtils.isNotEmpty(busiValue)) {
			StringBuffer sqlbuffer = new StringBuffer("select n.* from ( ");
			sqlbuffer.append(" select count(file_type) as filecount , file_type from t_n_markdown  ");
			if(!isPublic ) {
				sqlbuffer.append("  where power = 1 ");
			}
			sqlbuffer.append("  group by file_type order by filecount desc ) t ");
			sqlbuffer.append(" left join t_n_dict n on t.file_type = n.busi_value where n.parent_id in (select id from t_n_dict where busi_value = ?) ");
//			String sql = "select * from t_n_dict where ";
			List<Map<String, Object>> list = db.queryForList(sqlbuffer.toString(), new Object[] {busiValue});
			if(list.size() >0) {
	    		remap.put("result", "1");
	    		remap.put("msg", list);
	    	}
		}
		return remap;
	}
	
	/**
	 * 查询数据
	 * @param session
	 * @param resId
	 * @param paramName
	 * @param paramValue
	 * @return 返回JSON格式数据
	 * @throws Exception
	 */
	@RequestMapping("/querySelectDic/{resId}")
	public @ResponseBody SmartResponse<Object> querySelectDic(HttpSession session,@PathVariable String resId) throws Exception {
		SmartResponse<Object> smartResp = new SmartResponse<Object>();
		if(StringUtils.isNotEmpty(resId)) {
			Map<String, Object> params = new HashMap<>();
			System.out.println(resId);
			params.put("busiValue", resId);
			smartResp = opServ.getDatas("input_select_dic", params);
			params = null;
//			String sql = "select busi_value, busi_name from t_n_dic where parent_id = (select id from t_n_dic where busi_value = ?) ";
//			List<Map<String, Object>> list = db.queryForList(sql, new Object[] {resId});
		}
		return smartResp;
	}
	
}
