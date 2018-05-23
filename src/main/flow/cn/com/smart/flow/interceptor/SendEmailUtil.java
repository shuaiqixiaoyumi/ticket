package cn.com.smart.flow.interceptor;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;

import com.mixsmart.utils.CollectionUtils;

import cn.com.smart.flow.bean.entity.TFlowForm;
import cn.com.smart.flow.service.FlowFormService;
import cn.com.smart.init.config.InitSysConfig;
import cn.com.smart.web.bean.entity.TNUser;
import cn.com.smart.web.bean.entity.TPfproperties;
import cn.com.smart.web.service.PropertiesService;
import cn.com.smart.web.service.SubPropertiesService;
import cn.com.smart.web.service.UserService;

public class SendEmailUtil {
	
	@Autowired
	private UserService userServ;
	@Autowired
	private FlowFormService flowFormServ;
	@Autowired
    private PropertiesService propertiesService;
    
    @Autowired
    private SubPropertiesService subPropertiesService;

	private static final String START_NODE = "start";
	private static String host = null;
	private static int port ;
	private static String userName = null;
	private static String password = null;
	private static String to = null;
	
	public SendEmailUtil() {
		TPfproperties toPfproperties = propertiesService.getByCode("sendemail");
		String formDataId = toPfproperties.getForm_data_id();
		host = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "email.host").getKeyvalue();
		port = Integer.parseInt(subPropertiesService.getByFormDataIdAndKeyname(formDataId, "email.port").getKeyvalue()) ;
		userName = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "email.userName").getKeyvalue();
		password = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "email.password").getKeyvalue();
		to = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "email.to").getKeyvalue();
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
	public String getUserEmailByUserId(String userId) {
		TNUser user = userServ.getDao().find(userId);
		String email = user.getEmail();
		return email;
	} 
	
	/**
	 * 获取用户ID通过职位ID
	 * @param positionIds
	 * @return
	 */
	public Set<String> getUserIdByPosition(Set<String> positionIds) {
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
	public Set<String> getUserIdByGroupIds(Set<String> groupIds) {
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
public String getTitle(String orderId) {
	TFlowForm flowForm = flowFormServ.getFlowFormByOrderId(orderId);
	if(null != flowForm) {
		return flowForm.getTitle();
	}
	return null;
}
}
