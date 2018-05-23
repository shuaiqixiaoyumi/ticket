package cn.com.smart.web.bean.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.com.smart.bean.BaseBeanImpl;
import cn.com.smart.validate.Validate;

/**
 * 角色与文档菜单关联（实体Bean）
 * @author zhanglb
 * @version 1.0 2018年4月30日
 * @since 1.0
 *
 */
@Entity
@Table(name = "t_n_role_doc")
public class TNRoleDoc extends BaseBeanImpl {
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String roleId;
	
	private String docId;
	
	@Id
	@Column(name = "id", length=50)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Validate(nullable=false)
	@Column(name = "role_id", length = 50,nullable=false)
	public String getRoleId() {
		return this.roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	@Validate(nullable=false)
	@Column(name = "doc_id", length=50,nullable=false)
	public String getDocId() {
		return this.docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	@Override
	@Transient
	public String getPrefix() {
		return "rd";
	}
	

}
