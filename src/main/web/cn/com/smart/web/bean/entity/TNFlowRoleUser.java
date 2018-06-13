package cn.com.smart.web.bean.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.com.smart.bean.BaseBeanImpl;
/**
 * 流程角色和用户关联表
 * @author zhanglb
 *
 */
@Entity
@Table(name = "t_n_flowrole_user")
public class TNFlowRoleUser extends BaseBeanImpl {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String roleId;
	private String userId;
	private String sortOrder;

	@Id
	@Column(name = "ID", length=50)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "ROLE_ID", length = 50)
	public String getRoleId() {
		return this.roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	@Column(name = "USER_ID", length = 50)
	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@Column(name = "sort_order", length = 11)
	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

}
