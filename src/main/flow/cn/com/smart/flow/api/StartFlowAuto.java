package cn.com.smart.flow.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.snaker.engine.entity.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.com.smart.flow.ProcessContext;
import cn.com.smart.flow.SnakerEngineFacets;
import cn.com.smart.flow.bean.SubmitFormData;
import cn.com.smart.flow.bean.entity.TFlowProcess;
import cn.com.smart.flow.dao.FlowProcessDao;
import cn.com.smart.form.bean.entity.TFormField;
import cn.com.smart.form.dao.FormFieldDao;
import cn.com.smart.form.service.FormService;
import cn.com.smart.utils.DbBase;
import cn.com.smart.web.bean.entity.TNUser;
import cn.com.smart.web.service.UserService;
@Component
public class StartFlowAuto {

	@Autowired
	private SnakerEngineFacets facets;
	
	@Autowired
	private FlowProcessDao flowProcessDao;
	
	@Autowired
	private FormFieldDao formFieldDao;
	
	@Autowired
	private FormService formService;
	
	@Autowired
	private ProcessContext processContext;
	@Autowired
	private UserService userService;
	
	DbBase db = new DbBase();
	
	public boolean start(String processName, String str, String nextLineName) {
		boolean result = false;
		try {
			SubmitFormData submitFormData = new  SubmitFormData();
			Process process = facets.getEngine().process().getProcessByName(processName);
			String processId= process.getId();
			submitFormData.setProcessId(processId);
			TFlowProcess tFlowProcess = flowProcessDao.findByProcessId(processId);
			submitFormData.setFormId(tFlowProcess.getFormId());
			List<Object> list =formService.findFormInfoByFormId(tFlowProcess.getFormId());
			List<TFormField> tFormFieldlist = new ArrayList<>();
			if(list.size() >0) {
				for(Object obj :list) {
					tFormFieldlist.add((TFormField)obj);
				}
				
			}
			Map<String, Object> maps = JSON.parseObject(str);
			maps.put("formId", tFlowProcess.getFormId());
			maps.put("taskKey", "");
			maps.put("orderId", "");
			maps.put("processId",processId );
			maps.put("processName", processName);
			maps.put("formDataId", "");
			maps.put("taskId", "");
			maps.put("taskId", "");
			maps.put("nextLineName", nextLineName);
			submitFormData.setParams(maps);
			 List<Map<String, Object>> userinfolist= userService.getVirtualUserId();
			 TNUser userInfo = userService.getDao().queryUser("register");
			 if(userinfolist.size()>0) {
				 processContext.execute(submitFormData,userInfo.getId(),userInfo.getOrgId()); 
			 }
			
			result =true;
//			JSONObject json = new JSONObject(str);
//			if(!"".equals(json) && json != null ) {
//				Map<String, Object> map = new HashMap<>();
//				if(tFormFieldlist.size()>0) {
//					for(TFormField tformField : tFormFieldlist) {
//						if(str.contains(tformField.getId())) {
//							
//						}
//					}
//				}
//			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 获取流程表单对应的字段id
	 * @param name
	 * @return
	 */
	public List<Map<String, Object>> getFormInfo(String name) {
		String sql = "select * from t_form_field where form_id = (select id from t_form where name = ?)";
		List<Map<String, Object>> list = db.queryForList(sql, new Object[] {name});
		return list;
		
	}
	
}
