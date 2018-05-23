package cn.com.smart.flow.interceptor;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snaker.engine.core.Execution;
import org.snaker.engine.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mixsmart.utils.LoggerUtils;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.flow.service.FlowFormService;
import cn.com.smart.form.bean.QueryFormData;
import cn.com.smart.form.service.IFormDataService;

/**
 * 自定义任务执行类(实现handle接口)
 * @author hunterfu2009
 * @since 1.0
 */
@Component
public class CustomHandlerService {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomHandlerService.class);

	@Autowired
	private FlowFormService flowFormServ;
	
	@Autowired
	private IFormDataService formDataServ;

	/**
	 * @param execution 流程实例对象上下文
	 * 
	 */
	public void handle(Execution execution) {
		
		System.out.println("========= custom handler============");
		logger.info("自动化任务执行脚本 ========= custom handler============");
		LoggerUtils.debug(logger, "自动化任务处理.....");
		
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

		
	}
	
	/**
	 * 执行任务脚本方法(不执行方法)
	 */
	public void execScript() {
		System.out.println("========= execScript ......============");
		
	}
}