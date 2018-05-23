package cn.com.smart.flow.interceptor;

import org.snaker.engine.SnakerInterceptor;
import org.snaker.engine.core.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;

import cn.com.smart.flow.service.FlowFormService;
import cn.com.smart.service.SmartContextService;
import cn.com.smart.web.service.UserService;

@Component
public class TestRemindInterceptor implements SnakerInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(TestRemindInterceptor.class);  
	
	
	private FlowFormService flowFormServ;
	
	@Autowired
	private UserService userSer ;
	
	public TestRemindInterceptor() {
		flowFormServ  = SmartContextService.find(FlowFormService.class);
	}
	
	@Override
	public void intercept(Execution execution) {
//		// TODO Auto-generated method stub
//		Order order = execution.getOrder();
//		System.out.println("order.getId()==="+order.getId());
////		TFlowForm flowForm = flowFormServ.getFlowFormByOrderId(order.getId());
//		SmartResponse<QueryFormData> formDdata = flowFormServ.getFormData(order.getId());
////		System.out.println("----"+formDdata.getData().getValue());
//		System.out.println("----ceshijieshu");
	}
}
