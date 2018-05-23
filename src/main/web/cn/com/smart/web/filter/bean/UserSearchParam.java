package cn.com.smart.web.filter.bean;

import org.springframework.stereotype.Component;

import cn.com.smart.filter.bean.FilterParam;

/**
 * 用户列表搜索类
 * @author lmq
 *
 */
@Component
public class UserSearchParam extends FilterParam {

	private String orgId;
	
	private String info;
	
	private String[] userIds;
	
	public UserSearchParam() {
		super();
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String[] getUserIds() {
		return userIds;
	}

	public void setUserIds(String[] userIds) {
		this.userIds = userIds;
	}

	
	
}
