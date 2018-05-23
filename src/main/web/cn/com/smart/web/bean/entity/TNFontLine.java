package cn.com.smart.web.bean.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import cn.com.smart.bean.BaseBeanImpl;
import cn.com.smart.bean.DateBean;

@Entity
@Table(name = "t_n_fontline")
public class TNFontLine extends BaseBeanImpl implements DateBean {

private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String groupId;
	
	private String userId;
	
	private Integer sortOrder;
	
	private Integer state;
	
	private Date createTime;
	@Id
	@Column(name="id",length=50)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	@Column(name = "groupId", length = 50)
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	@Column(name = "userId", length = 50)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Column(name = "sort_order", length = 2)
	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	@Column(name = "state", length = 2)
	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
	
	@Override
	@Transient
	public String getPrefix() {
		return "f";
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time",updatable=false)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
