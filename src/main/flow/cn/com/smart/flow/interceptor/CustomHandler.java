package cn.com.smart.flow.interceptor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snaker.engine.core.Execution;
import org.snaker.engine.entity.Order;
import org.snaker.engine.handlers.IHandler;

import com.mixsmart.security.SecurityUtils;
import com.mixsmart.utils.LoggerUtils;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.flow.service.FlowFormService;
import cn.com.smart.form.bean.QueryFormData;
import cn.com.smart.service.SmartContextService;
import cn.com.smart.utils.DbBase;
import cn.com.smart.web.bean.entity.TNUser;
import cn.com.smart.web.dao.impl.UserDao;
import cn.com.smart.web.service.OrgService;
import cn.com.smart.web.service.PropertiesService;
import cn.com.smart.web.service.UserService;


/**
 * 自定义任务执行类(实现handle接口)
 * @author hunterfu2009
 * @since 1.0
 */
public class CustomHandler implements IHandler {
	
	DbBase db = new DbBase();
	
	private static final Logger logger = LoggerFactory.getLogger(CustomHandler.class);
	private static final String START_NODE = "start";
	
	private FlowFormService flowFormServ;
	
	private UserService userService;
	private UserDao userDao;
	private OrgService orgService;
	private PropertiesService propertiesService;
	
	/* (non-Javadoc)
	 * @see org.snaker.engine.handlers.IHandler#handle(org.snaker.engine.core.Execution)
	 */
	public CustomHandler() {
		flowFormServ  = SmartContextService.find(FlowFormService.class);
		userService =SmartContextService.find(UserService.class);
		userDao = SmartContextService.find(UserDao.class);
		orgService = SmartContextService.find(OrgService.class);
		propertiesService= SmartContextService.find(PropertiesService.class);
	}
	
	public void handle(Execution execution) {
		try {
			System.out.println("========= custom handler============");
			logger.info("自动化任务执行脚本 ========= custom handler============");
			LoggerUtils.debug(logger, "自动化任务处理.....");
			
//			CustomHandlerService customHandlerService = (CustomHandlerService)SpringContextHelper.getBean("CustomHandlerService");
//			
//			customHandlerService.handle(execution);
			
			// 获取自动执行任务的参数
			Map<String, Object> args = execution.getArgs();
			Iterator<Entry<String, Object>> entries = args.entrySet().iterator();  
			  
			while (entries.hasNext()) {  
			  
			    Entry<String, Object> entry = entries.next();  
			  
			    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  
			  
			} 
			// 设置上下文环境变量 下一个流程判断节点表达式会用到此 变量
			// 判断表达式     #result?'success':'fail'
			args.put("result", true);

			// 获取流程实例
			Order order = execution.getOrder();
	        // 获取表单数据 (autowired 不成功)
			SmartResponse<QueryFormData> formDdata = flowFormServ.getFormData(order.getId());
			if(formDdata.getResult() == "1") {
				String fordataid = formDdata.getData().getValue()+"";
				List<Map<String, Object>> list = getFormInfo(fordataid, "t_pf_open_account");
				String username = list.get(0).get("username")+"";
				String email = list.get(0).get("email")+"";
				String mobile = list.get(0).get("mobile")+"";
				String accountName = email.split("@")[0];
				String password = (int)((Math.random()*9+1)*100000)+"";  
				String pwd = password;
				System.out.println(accountName +"---"+ password+"----"+mobile);
				boolean isAduser = userService.registerUser(accountName, password, mobile);
				String content ="账号注册失败，请询问管理员后重新发起流程";
				String deptId  = orgService.getDeptIdByPhoneFromYZj(mobile,accountName);
				if(isAduser) {
					content = "你的工单账号已经注册,账号密码为你的域账号和域密码,账号为"+accountName+",要是不记得域密码了，可以暂时用临时密码登入,临时密码为"+pwd+".进入系统后请修改密码!";
				}else {
					TNUser userEntity =  userDao.queryUser(accountName);
				    if(userEntity != null) {
				    	userEntity.setFullName(username);
				    	userEntity.setEmail(email);
				    	userEntity.setMobileNo(mobile);
				    	userEntity.setOrgId(deptId);
				    	userService.update(userEntity);
				    	password = SecurityUtils.md5(password);
				    	userDao.changePwd(userEntity.getId(), password);
				    }else {
				    	userEntity = new TNUser();
				    	userEntity.setUsername(accountName);
				    	userEntity.setFullName(username);
				    	userEntity.setMobileNo(mobile);
				    	userEntity.setOrgId(deptId);
				    	userEntity.setEmail(email);
				    	userEntity.setPassword(password);
				    	userEntity.setState("1");
				    	userService.save(userEntity);
				    }
				    content = "你的工单账号已经注册,账号为"+accountName+"密码为"+pwd+",请登入后及时修改密码";
				    userService.saveRoleToNewUser(userEntity.getId(), "普通用户");
				}
				
				TodoRemindInterceptor  todoRemindInterceptor = new TodoRemindInterceptor();
				if(propertiesService.isValidByCode("sendemail")) {
					todoRemindInterceptor.sendHtmlMail(content, "工单账号注册", email);
				}
				
			}
			System.out.println("zhixingjiesu");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
	public List<Map<String, Object>> getFormInfo(String fordataid, String tableName){
		List<Map<String, Object>> list = new ArrayList<>();
		String sql = "select * from "+ tableName +" where form_data_id = ?";
		list = db.queryForList(sql, new Object[] {fordataid});
		return list;
	}
	
	/**
	 * 执行任务脚本方法(不执行方法)
	 */
	public void execScript() {
		System.out.println("========= execScript ......============");
	}
}