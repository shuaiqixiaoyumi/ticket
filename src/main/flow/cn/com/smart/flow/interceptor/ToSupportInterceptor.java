package cn.com.smart.flow.interceptor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snaker.engine.SnakerInterceptor;
import org.snaker.engine.core.Execution;
import org.snaker.engine.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;

import com.mixsmart.utils.CollectionUtils;
import com.mixsmart.utils.LoggerUtils;
import com.mixsmart.utils.StringUtils;

import cn.com.smart.flow.service.FlowFormService;
import cn.com.smart.service.SmartContextService;
import cn.com.smart.web.bean.entity.TNOrg;
import cn.com.smart.web.bean.entity.TNPosition;
import cn.com.smart.web.push.MessageType;
import cn.com.smart.web.push.impl.PushMessageContext;
import cn.com.smart.web.service.PropertiesService;
import cn.com.smart.web.service.UserService;

public class ToSupportInterceptor implements SnakerInterceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(ToSupportInterceptor.class);
	
	private static final String MSG_TMPL = "您有一个新的待办，需要处理“${title}”中的“${displayName}”环节,系统地址http://ticket.prod.ops.com";
	
	private static final String START_NODE = "start";
	
	@Autowired
	private PushMessageContext pushMsgContext;
	@Autowired
	private UserService userServ;
	
	private FlowFormService flowFormServ;
	
	private SendEmailUtil sendEmailUtil;
	
    private PropertiesService propertiesService;
    
	
	
	public ToSupportInterceptor() {
		flowFormServ  = SmartContextService.find(FlowFormService.class);
		sendEmailUtil = SmartContextService.find(SendEmailUtil.class);
		propertiesService = SmartContextService.find(PropertiesService.class);
	}

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
		String title = sendEmailUtil.getTitle(execution.getOrder().getId());
		System.out.println("title="+title);
		if(StringUtils.isEmpty(title)) {
			title = StringUtils.handNull(execution.getOrder().getVariableMap().get("title"));
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
				Set<String> userIdTmps = sendEmailUtil.getUserIdByPosition(positionIds);
				if(CollectionUtils.isNotEmpty(userIdTmps)) {
					userIds.addAll(userIdTmps);
				}
			}
			//task.getModel()
			if(CollectionUtils.isNotEmpty(groupIds)) {
				Set<String> userIdTmps = sendEmailUtil.getUserIdByGroupIds(groupIds);
				if(CollectionUtils.isNotEmpty(userIdTmps)) {
					userIds.addAll(userIdTmps);
				}
			}
			String content = sendContent.replace("${displayName}", task.getDisplayName());
			for(String userid : userIds) {
				String email = sendEmailUtil.getUserEmailByUserId(userid);
				try {
					if(propertiesService.isValidByCode("sendemail")) {
						sendEmailUtil.sendHtmlMail(content, "工单流程提醒", email);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			pushMsgContext.sendMsg(MessageType.TODO, userIds, groupIds, content, null);
		}//for
	}
	

}
