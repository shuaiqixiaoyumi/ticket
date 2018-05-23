package cn.com.smart.flow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snaker.engine.SnakerEngine;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.entity.Order;
import org.snaker.engine.entity.Process;
import org.snaker.engine.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.web.service.OPService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations={"classpath:applicationContext.xml", "classpath:applicationContext-snaker.xml"})
////@ContextConfiguration(locations={"classpath:applicationContext.xml", "classpath:applicationContext-snaker.xml","classpath:spring-web-config.xml"})
//@WebAppConfiguration
public class ProcessFlowTest {

//    @Autowired
//    private ApplicationContext applicationContext;
//    
//	/**
//	 * 查询数据库
//	 */
//	@Autowired
//	private OPService opServ;
//	/**
//	 * 流程引擎读取流程信息
//	 */
//
//
//	@Autowired
//	private WebApplicationContext context;
//	
//	private MockMvc mockMvc;
//    
//	@Before
//	public void setUp() {
//		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
//		//mockMvc(mockMvc);
//		//RestAssuredMockMvc.mockMvc(mockMvc);
//		//RestAssuredMockMvc.webAppContextSetup(context);
//		//RestAssuredMockMvc.webAppContextSetup(context);
//		//RestAssuredMockMvc.standaloneSetup(new LoginController());
//	}
//    @After 
//    public void  rest_assured_is_reset_after_each_test() {
//    	RestAssuredMockMvc.reset();
//    }
//	//@Test
//	public void testGetProcessList() {
//		
//		SnakerEngine engine = applicationContext.getBean(SnakerEngine.class);
//		QueryFilter queryFilter = new QueryFilter();
//		List<org.snaker.engine.entity.Process> processList =engine.process().getProcesss(queryFilter);
//        for(org.snaker.engine.entity.Process process:processList)
//        {
//           System.out.println("process id =" + process.getId() + "  name = "+ process.getName() + "\tdisplayname = "+ process.getDisplayName()
//           + "\tversion = "+ process.getVersion());
//        }
//		//engine.process().deploy(StreamHelper.getStreamFromClasspath("contactform.snaker"));
//		// Map<String, Object> args = new HashMap<String, Object>();
//		// args.put("receipt.operator", new String[]{"1"});
//		// Order order = engine.startInstanceByName("contactFormFlow", 0, "2", args);
//		// System.out.println("order=" + order);
//		// List<Task> tasks = engine.query().getActiveTasks(new
//		// QueryFilter().setOrderId(order.getId()));
//		// for(Task task : tasks) {
//		// engine.executeTask(task.getId(), "1", args);
//		// }
//	}
//	/**
//	 * 测试读取流程名称(特定名称)
//	 */
//	//@Test
//	public void testGetProcessByname() {
//		SnakerEngine engine = applicationContext.getBean(SnakerEngine.class);
//		Process process =engine.process().getProcessByName("openNewAccount");
// 
//        System.out.println("process id =" + process.getId() + "  name = "+ process.getName() + "\tdisplayname = "+ process.getDisplayName()
//           + "\tversion = "+ process.getVersion());
//
//	}
//	/**
//	 * 读取流程实例
//	 * 根据流程定义ID查询流程实例列表 
//     * List<Order> getActiveOrders(String... processIds); 
//	 */
//	//@Test
//	public void testGetInstnace() {
//		// 第一种查询方法
//		Map<String,Object> params = new HashMap<String, Object>();
//		SmartResponse<Object> chRes = opServ.getDatas("process_order_mgr_list");
//		List<Object> objlist = chRes.getDatas();
//		for (Object obj : objlist) {
//			Object[] objArray = (Object[])obj;
//			System.out.println(objArray.toString());
//			for(int i=0;i < objArray.length;i++) {
//				System.out.println(objArray[i]);
//			}
//			params.clear();
//			params.put("processId", objArray[0]);
//			params.put("orderId", objArray[1]);
//		}
//		// 第二种查询方法
//		SnakerEngine engine = applicationContext.getBean(SnakerEngine.class);
//		List<Order> orderlist = engine.query().getActiveOrders(new QueryFilter());
//		for(Order order:orderlist)
//		{
//			System.out.println("Creator = "+order.getCreator()+ "\tlastupdatetime = "
//					+ order.getLastUpdateTime() +"\tprocessId=" +order.getProcessId() 
//					+ "\torderId=" + order.getId());
//		}
//	}
//	/**
//	 * 测试获取活动任务(某个流程实例的活动任务)
//	 */
//	//@Test
//	public void testGetTask() {
//		SnakerEngine engine = applicationContext.getBean(SnakerEngine.class);
//		List<Task> orderlist = engine.query().getActiveTasks(new QueryFilter());
//		for(Task task:orderlist)
//		{   // 只打印 工单账号申请流程 实例的活动任务(当前只有一个流程实例)
//			if (task.getOrderId().equals("fed8a4fb2c4e4e22b84a3aa28cd8543b")) {
//			System.out.println("TaskDisplayName = "+task.getDisplayName()+ "\ttaskname = "
//					+ task.getTaskName() +"\tactorids=" +task.getActorIds()[0] 
//					+ "\ttaskId=" + task.getId());
//			}
//		}
//	}
//	
//	/**
//	 * 测试任务执行(需要使用 httpsession) 无法测试
//	 */
//	//@Test
//	public void testExecTask() {
//		SnakerEngine engine = applicationContext.getBean(SnakerEngine.class);
//		//List<Task> orderlist = engine.executeTask("a6f6673fd67e464bac7eef21488acfe4");
//		List<Task> orderlist = engine.executeTask("a6f6673fd67e464bac7eef21488acfe4", "admin");
//		for(Task task:orderlist)
//		{   // 只打印 工单账号申请流程 实例的活动任务(当前只有一个流程实例)
//			if (task.getOrderId().equals("fed8a4fb2c4e4e22b84a3aa28cd8543b")) {
//			System.out.println("TaskDisplayName = "+task.getDisplayName()+ "\ttaskname = "
//					+ task.getTaskName() +"\tactorids=" +task.getActorIds() 
//					+ "\ttaskId=" + task.getId());
//			}
//		}
//	}
//	/**
//	 * 无法测试通过 测试登录  这个是依赖注入问题
//	 * @throws Exception 
//	 */
//	//@Test
//	public void testLogin() throws Exception {
///*		given().
//	        param("userName", "admin").
//	        param("password","123456").
//	        param("resolution","1536x864").
//	        param("screenWidth","1536").
//	        param("screenHeight","864").
//	     when().
//	         post("/login").
//	     then().
//	         statusCode(SC_OK);*/
//		
//		this.mockMvc
//        .perform(post("/login")
//                 .accept(MediaType.APPLICATION_JSON)
//           .param("userName", "admin")
//           .param("password", "123456"))
//           .andDo(print())
//           .andExpect(status().isOk());
//	}
	
	
}
