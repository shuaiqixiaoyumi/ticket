package cn.com.smart.flow.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snaker.engine.helper.StreamHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.flow.SnakerHelper;
import cn.com.smart.flow.enums.FlowDeployType;
import cn.com.smart.flow.ext.ExtModelParser;
import cn.com.smart.flow.ext.ExtProcessModel;
import cn.com.smart.flow.filter.FlowSearchParam;
import cn.com.smart.flow.service.FlowService;
import cn.com.smart.utils.DbBase;
import cn.com.smart.web.bean.RequestPage;
import cn.com.smart.web.bean.UserInfo;
import cn.com.smart.web.constant.enums.BtnPropType;
import cn.com.smart.web.service.OPService;
import cn.com.smart.web.tag.bean.ALink;
import cn.com.smart.web.tag.bean.CustomBtn;
import cn.com.smart.web.tag.bean.DelBtn;
import cn.com.smart.web.tag.bean.PageParam;
import cn.com.smart.web.tag.bean.RefreshBtn;

import com.mixsmart.utils.StringUtils;


/**
 * 流程设计 <br />
 * 修改于：2016年10月15日 <br />
 * 去掉了方法异常抛出，去掉方法中的ModelAndView类型的参数
 * @author lmq
 * @version 1.1
 * @since 1.1
 */
@Controller
@RequestMapping("/flow")
public class FlowController extends BaseFlowControler {
	private static final Logger logger = LoggerFactory.getLogger(FlowController.class);
	private static final String VIEW_DIR = "flow";
	@Autowired
	private FlowService flowServ;
	@Autowired
	private OPService opServ;
	
	DbBase db = new DbBase();
	
	/**
	 * 流程设计器
	 * @param id
	 * @return
	 */
	@RequestMapping("/designer")
	public ModelAndView designer(String id) {
		ModelAndView modelView  = new ModelAndView();
		if(StringUtils.isNotEmpty(id)) {
			modelView.getModelMap().put("process", flowServ.getProcessJson(id));
		}
		modelView.setViewName(VIEW_DIR+"/designer");
		return modelView;
	}
	
	/**
	 * 流程列表
	 * @param session
	 * @param searchParam
	 * @param page
	 * @return
	 */
	@RequestMapping("/list")
	public ModelAndView list(HttpSession session,FlowSearchParam searchParam,RequestPage page) {
		ModelAndView modelView  = new ModelAndView();
		String uri = "flow/list";
		if("0".equals(searchParam.getOrgId())) {
			searchParam.setOrgId(null);
		}
		searchParam.setOrgIds(StringUtils.list2Array(getUserInfoFromSession(session).getOrgIds()));
		pageParam = new PageParam(uri, null, page.getPage(), page.getPageSize());
		uri += (null != searchParam)?("?"+searchParam.getParamToString()):"";
		
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("orgIds", StringUtils.list2Array(getUserInfoFromSession(session).getOrgIds()));
		SmartResponse<Object> smartResp = opServ.getDatas("flow_process_list",searchParam, page.getStartNum(), page.getPageSize());
		params = null;
		
		CustomBtn customBtn = new CustomBtn("edit_designer", "流程设计器", "修改流程设计", "flow/designer");
		customBtn.setSelectedType(BtnPropType.SelectType.ONE.getValue());
		customBtn.setBtnIcon("glyphicon-pencil");
		customBtn.setOpenStyle(BtnPropType.OpenStyle.OPEN_SELF);
		customBtns = new ArrayList<CustomBtn>(1);
		customBtns.add(customBtn);
		
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
		
		delBtn = new DelBtn("flow/delete", "确定要删除选中的部署流程信息吗，删除后数据将无法恢复？",uri,null, null);
		refreshBtn = new RefreshBtn(uri, null,null);
		ModelMap modelMap = modelView.getModelMap();
		modelMap.put("smartResp", smartResp);
		modelMap.put("customBtns", customBtns);
		modelMap.put("delBtn", delBtn);
		modelMap.put("refreshBtn", refreshBtn);
		modelMap.put("pageParam", pageParam);
		modelMap.put("alinks", alinks);
		customBtn = null;delBtn = null;
		refreshBtn = null;pageParam = null;
		alinks = null;
		
		modelView.setViewName(VIEW_DIR+"/list");
		return modelView;
	}
	
	/**
	 * 部署流程
	 * @param session HttpSession
	 * @param model 流程模板（XML）
	 * @param id 流程ID
	 * @param deployType 部署类型
	 * @return
	 */
	@RequestMapping("/deploy")
	public @ResponseBody SmartResponse<String> deploy(HttpSession session,String model,String id, String deployType) {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		smartResp.setMsg("流程部署失败");
		UserInfo userInfo = super.getUserInfoFromSession(session);
		if(StringUtils.isEmpty(model)) {
			return smartResp;
		}
		if(StringUtils.isNotEmpty(model) && StringUtils.isNotEmpty(deployType)) {
			smartResp = flowServ.deployProcess(model, id, userInfo.getId(), FlowDeployType.getObj(deployType));
		}
		return smartResp;
	}
	
	/**
	 * 删除流程部署信息
	 * @param id
	 * @return
	 */
	@RequestMapping("/delete")
	public @ResponseBody SmartResponse<String> delete(String id){
		SmartResponse<String> smartResp = new SmartResponse<String>();
		if(StringUtils.isNotEmpty(id)) {
			String[] ids = id.split(",");
			smartResp = flowServ.deleteProcess(ids);
		}
		return smartResp;
	}
	
	/**
	 * 查看流程图
	 * @param id
	 * @return
	 */
	@RequestMapping("/show")
	public ModelAndView show(String id){
		ModelAndView modelView = new ModelAndView();
		if(StringUtils.isNotEmpty(id)) {
			modelView.getModelMap().put("process", flowServ.getProcessJson(id));
		}
		modelView.setViewName(VIEW_DIR+"/show");
		return modelView;
	}
	
	/**
	 * 导出流程
	 * @param model 导出的流程数据
	 * @return
	 */
	@RequestMapping("/export")
	public void export(String model,  HttpServletResponse response) {
		HttpHeaders headers = new HttpHeaders();
		if(StringUtils.isNotEmpty(model)){
			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + SnakerHelper.convertXml(model);
			InputStream input = StreamHelper.getStreamFromString(xml);
			
			
			
			try {
				byte[] bytes = StreamHelper.readBytes(input);
				ExtProcessModel processModel = ExtModelParser.parse(bytes);
				response.setContentType("application/octet-stream ");
				response.setHeader("Connection", "close"); // 表示不能用浏览器直接打开
				response.setHeader("Accept-Ranges", "bytes");// 告诉客户端允许断点续传多线程连接下载
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename=\""+processModel.getName()+".zip\"");   
				ServletOutputStream outputStream = response.getOutputStream();
				ZipOutputStream zip = new ZipOutputStream(outputStream);
				zip.putNextEntry(new ZipEntry(processModel.getName()+".xml"));
				zip.write(bytes);
		        zip.flush();
		        String content = exportFlowForm(processModel.getName());
		        zip.putNextEntry(new ZipEntry("insert.sql"));
		        zip.write(content.getBytes());
		        zip.flush();
		        zip.closeEntry();
		        zip.close();
		        
		       
//				byte[] bytes = StreamHelper.readBytes(input);
//				ExtProcessModel processModel = ExtModelParser.parse(bytes);
//				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//				headers.setContentDispositionFormData("attachment", new String(processModel.getName().getBytes("UTF-8"), "ISO8859-1")+".xml");
//				return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
	}
	
	public String exportFlowForm(String processName) {
		boolean result = false;
		String insetsqlcontent = "";
		try {
			String TFormsql = "select * from  t_form_field where form_id = (select form_id from t_flow_process where process_id = (select id from t_wf_process where name = ?  order by version limit 1))";
			List<Map<String , Object>> tFormlist = db.queryForList(TFormsql, new Object[] {processName});
			
			if(tFormlist.size() >0) {
				insetsqlcontent = insetsqlcontent + getSqlContent(tFormlist, "t_form_field");
			}
			String tablesql = "select * from t_create_table where id in (select table_id from  t_form_field where form_id = (select form_id from t_flow_process where process_id = (select id from t_wf_process where name = ?  order by version limit 1)) group by table_id)";
			List<Map<String , Object>> tablelist = db.queryForList(tablesql, new Object[] {processName});
			if(tablelist.size() >0) {
				insetsqlcontent = insetsqlcontent + getSqlContent(tablelist, "t_create_table");
			}
			String tablefieldsql = "select * from t_create_table_field where id in (select table_field_id from  t_form_field where form_id = (select form_id from t_flow_process where process_id = (select id from t_wf_process where name = ?  order by version limit 1)) )"; 
			List<Map<String , Object>> tablefieldlist = db.queryForList(tablefieldsql, new Object[] {processName});
			if(tablefieldlist.size() >0) {
				insetsqlcontent = insetsqlcontent + getSqlContent(tablefieldlist, "t_create_table_field");
			}
			String formsql = "select * from  t_form where id = (select form_id from t_flow_process where process_id = (select id from t_wf_process where name = ?  order by version limit 1))";
			List<Map<String , Object>> formsqllist = db.queryForList(formsql, new Object[] {processName});
			if(formsqllist.size() >0) {
				insetsqlcontent = insetsqlcontent + getSqlContent(formsqllist, "t_form");
			}
		
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return insetsqlcontent;
		
	}
	
	public String getSqlContent(List<Map<String , Object>> list, String tableName) {
		String insetsqlcontent = "";
		if(list.size() >0) {
			for(Map<String, Object> map : list) {
				String insert = "insert into "+tableName+" ( ";
				String values = "values ( ";
				int i=0;
				for(String key: map.keySet()) {
					if(i == 0) {
						insert  = insert +key;
						values = values+"'"+map.get(key)+"'";
					}else {
						insert  = insert+ "," +key;
						values = values+", '"+map.get(key)+"'";
					}
					i++;
				}
				insetsqlcontent = insetsqlcontent+insert+") "+values +" ); \n";
				
			}
		}
		return insetsqlcontent;
	}
	
	/**
	 * 导入流程
	 * @param file 导入的流程文件
	 * @return
	 */
	@RequestMapping("/import")
	@ResponseBody
	public SmartResponse<String> importModel(@RequestParam MultipartFile file) {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		if(logger.isInfoEnabled()) {
			logger.info("正在导入流程...");
		}
		String msg = "流程导入失败";
		if(null != file) {
			String ext = StringUtils.getFileSuffix(file.getOriginalFilename());
			if(StringUtils.isNotEmpty(ext) && "xml".equalsIgnoreCase(ext)) {
				try {
					byte[] bytes = StreamHelper.readBytes(file.getInputStream());
					ExtProcessModel processModel = ExtModelParser.parse(bytes);
					if(null != processModel) {
						String json = SnakerHelper.getModelJson(processModel);
						if(StringUtils.isNotEmpty(json)) {
							smartResp.setData(json);
							msg="流程导入成功";
							if(logger.isInfoEnabled()) {
								logger.info("流程导入[成功]...");
							}
						} else {
							if(logger.isErrorEnabled()) {
								logger.error("流程模板转换成JSON格式[失败]");
							}
						}
					} else {
						if(logger.isErrorEnabled()) {
							logger.error("流程文件解析[失败]...");
						}
					}
				} catch (IOException e) {
					if(logger.isErrorEnabled()) {
						logger.error("导入文件读取[失败]...");
					}
					e.printStackTrace();
				}
			} else {
				if(logger.isErrorEnabled()) {
					logger.error("流程导入失败[文件类型("+ext+")不支持导入]...");
				}
			}
		}
		smartResp.setMsg(msg);
		return smartResp;
	}
	
}
