package cn.com.smart.flow.interceptor;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snaker.engine.SnakerInterceptor;
import org.snaker.engine.core.Execution;
import org.snaker.engine.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mixsmart.utils.CollectionUtils;
import com.mixsmart.utils.LoggerUtils;
import com.mixsmart.utils.StringUtils;

import cn.com.smart.flow.bean.entity.TFlowForm;
import cn.com.smart.flow.service.FlowFormService;
import cn.com.smart.init.config.InitSysConfig;
import cn.com.smart.service.SmartContextService;
import cn.com.smart.web.bean.entity.TNOrg;
import cn.com.smart.web.bean.entity.TNPosition;
import cn.com.smart.web.bean.entity.TNUser;
import cn.com.smart.web.bean.entity.TPfproperties;
import cn.com.smart.web.push.MessageType;
import cn.com.smart.web.push.impl.PushMessageContext;
import cn.com.smart.web.service.PropertiesService;
import cn.com.smart.web.service.SubPropertiesService;
import cn.com.smart.web.service.UserService;

/**
 * 待办提醒
 * @author lmq
 * @version 1.0
 * @since 1.0
 */
@Component
public class TodoRemindInterceptor implements SnakerInterceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(TodoRemindInterceptor.class);
	
//	private static final String MSG_TMPL = InitSysConfig.getInstance().getValue("MSG_TMPL");
	private static final String START_NODE = "start";
	private static String host = null;
	private static int port ;
	private static String userName = null;
	private static String password =null;
	private static String to = null;
	
	private static final String MSG_TMPL = "您有一个新的待办，需要处理“${title}”中的“${displayName}”环节,系统地址http://help.xinguangnet.com:8090";
//	private static final String START_NODE = "start";
//	private static String host = "smtp.exmail.qq.com";
//	private static int port = 25;
//	private static String userName = "ops@xinguangnet.com";
//	private static String password = "Aa123465";
//	private static String to = "xinxizhongxin@xinguangnet.com";
	
	
	@Autowired
	private PushMessageContext pushMsgContext;
	@Autowired
	private UserService userServ;
	@Autowired
    private PropertiesService propertiesService;
    
    @Autowired
    private SubPropertiesService subPropertiesService;
	
	private FlowFormService flowFormServ;
	
	public TodoRemindInterceptor() {
		flowFormServ  = SmartContextService.find(FlowFormService.class);
		propertiesService  = SmartContextService.find(PropertiesService.class);
		subPropertiesService  = SmartContextService.find(SubPropertiesService.class);
		
		TPfproperties toPfproperties = propertiesService.getByCode("sendemail");
		String formDataId = toPfproperties.getForm_data_id();
		host = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "email.host").getKeyvalue();
		port = Integer.parseInt(subPropertiesService.getByFormDataIdAndKeyname(formDataId, "email.port").getKeyvalue()==""?"25":subPropertiesService.getByFormDataIdAndKeyname(formDataId, "email.port").getKeyvalue()) ;
		userName = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "email.userName").getKeyvalue();
		password = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "email.password").getKeyvalue();
		to = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "email.to").getKeyvalue();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void intercept(Execution execution) {
		LoggerUtils.debug(logger, "处理待办提醒");
		if(null == execution || CollectionUtils.isEmpty(execution.getTasks())) {
			LoggerUtils.error(logger, "流程任务为空");
			return;
		}
		List<Task> tasks = execution.getTasks();
		//如果上一节点是开始节点；则不提醒
		if(START_NODE.equals(tasks.get(0).getParentTaskId())) {
			return;
		}
		Set<String> userIds = null;
		Set<String> groupIds = null;
		Set<String> positionIds = null;
		
		String title = getTitle(execution.getOrder().getId());
		System.out.println("title="+title);
		if(StringUtils.isEmpty(title)) {
			title = StringUtils.handleNull(execution.getOrder().getVariableMap().get("title"));
		}
		String sendContent = MSG_TMPL.replace("${title}", title);
		for (Task task : tasks) {
			userIds = new HashSet<String>();
			groupIds = new HashSet<String>();
			positionIds = new HashSet<String>();
			String[] actorIds = task.getActorIds();
			System.out.println(task.getOperator());
			for (int i = 0; i < actorIds.length; i++) {
				if(actorIds[i].startsWith(TNOrg.PREFIX+"_")) {
					groupIds.add(actorIds[i]);
				} else if(actorIds[i].startsWith(TNPosition.PREFIX+"_")) {
					positionIds.add(actorIds[i]);
				} else {
					userIds.add(actorIds[i]);
				}
			}//for
			if(CollectionUtils.isNotEmpty(positionIds)) {
				Set<String> userIdTmps = getUserIdByPosition(positionIds);
				if(CollectionUtils.isNotEmpty(userIdTmps)) {
					userIds.addAll(userIdTmps);
				}
			}
			//task.getModel()
			if(CollectionUtils.isNotEmpty(groupIds)) {
				Set<String> userIdTmps = getUserIdByGroupIds(groupIds);
				if(CollectionUtils.isNotEmpty(userIdTmps)) {
					userIds.addAll(userIdTmps);
				}
			}
			
			//sendContent = sendContent.replace("${url}", task.getActionUrl());
			String content = sendContent.replace("${displayName}", task.getDisplayName());
			for(String userid : userIds) {
				String email = getUserEmailByUserId(userid);
				try {
					if(propertiesService.isValidByCode("sendemail")) {
						sendHtmlMail(content, "工单变更流程提醒", email);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			pushMsgContext.sendMsg(MessageType.TODO, userIds, groupIds, content, null);
		}//for
		
	}
	
	public void sendHtmlMail(String content, String subject, String acceptemail) throws Exception
	   {
	      HtmlEmail mail = new HtmlEmail();
	      // 设置邮箱服务器信息
	      mail.setSmtpPort(port);
	      mail.setHostName(host);
	      // 设置密码验证器
	      mail.setAuthentication(userName, password);
	      // 设置邮件发送者
	      mail.setFrom(userName);
	      // 设置邮件接收者
	      mail.addTo(acceptemail);
	      // 设置邮件编码
	      mail.setCharset("UTF-8");
	      // 设置邮件主题
	      mail.setSubject(subject);
	      // 设置邮件内容
	      mail.setHtmlMsg(content);
	      // 设置邮件发送时间
	      mail.setSentDate(new Date());
	      // 发送邮件
	      mail.send();
	   }
	
	
	 /**
	 * 获取用户ID通过职位ID
	 * @param positionIds
	 * @return
	 */
	private String getUserEmailByUserId(String userId) {
		TNUser user = userServ.getDao().find(userId);
		String email = user.getEmail();
		return email;
	} 
	
	/**
	 * 获取用户ID通过职位ID
	 * @param positionIds
	 * @return
	 */
	private Set<String> getUserIdByPosition(Set<String> positionIds) {
		Set<String> userIds = null;
		String[] array = new String[positionIds.size()];
		positionIds.toArray(array);
		List<TNUser> users = userServ.getDao().findByPositionId(array);
		if(CollectionUtils.isNotEmpty(users)) {
			userIds = new HashSet<String>(users.size());
			for (TNUser user : users) {
				userIds.add(user.getId());
			}
		}
		return userIds;
	} 
	
	
	/**
	 * 获取用户ID通过职位ID
	 * @param positionIds
	 * @return
	 */
	private Set<String> getUserIdByGroupIds(Set<String> groupIds) {
		Set<String> userIds = null;
		String[] array = new String[groupIds.size()];
		groupIds.toArray(array);
		List<TNUser> users = userServ.getDao().queryByOrgIds(array);
		if(CollectionUtils.isNotEmpty(users)) {
			userIds = new HashSet<String>(users.size());
			for (TNUser user : users) {
				userIds.add(user.getId());
			}
		}
		return userIds;
	} 
	
	/**
	 * 获取流程实例标题
	 * @param orderId
	 * @return
	 */
	private String getTitle(String orderId) {
		TFlowForm flowForm = flowFormServ.getFlowFormByOrderId(orderId);
		if(null != flowForm) {
			return flowForm.getTitle();
		}
		return null;
	}
}
