package cn.com.smart.web.bean.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.com.smart.bean.BaseBeanImpl;
import cn.com.smart.validate.Validate;

/**
 * 配置信息表
 * @author zhanglb
 *
 */
@Entity
@Table(name = "t_pf_properties")
public class TPfproperties extends BaseBeanImpl{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String modulename;
	
	private String code;
	
	private String form_data_id;
	
	private int status;
	
	private List<TPfSubproperties> list;
	
	
	
	@Id
	@Column(name = "id", length = 50)
	@Validate(nullable=false,length="1,50")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "modulename",length = 255)
	@Validate(length="1,255")
	public String getModulename() {
		return modulename;
	}

	public void setModulename(String modulename) {
		this.modulename = modulename;
	}

	@Column(name = "code",length = 255)
	@Validate(length="1,255")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "status", length=2)
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "form_data_id",length = 255)
	@Validate(length="1,255")
	public String getForm_data_id() {
		return form_data_id;
	}

	public void setForm_data_id(String form_data_id) {
		this.form_data_id = form_data_id;
	}

	@Transient
	public List<TPfSubproperties> getList() {
		return list;
	}

	public void setList(List<TPfSubproperties> list) {
		this.list = list;
	}
	
	

}
