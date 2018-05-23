package cn.com.smart.web.bean.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.com.smart.bean.BaseBeanImpl;
import cn.com.smart.validate.Validate;

/**
 * 配置信息子表
 * @author zhanglb
 *
 */
@Entity
@Table(name = "t_pf_subproperties")
public class TPfSubproperties extends BaseBeanImpl{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String keyname;
	
	private String keyvalue;
	
	private String form_data_id;

	@Id
	@Column(name = "id", length = 50)
	@Validate(nullable=false,length="1,50")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "keyname",length = 255)
	@Validate(length="1,255")
	public String getKeyname() {
		return keyname;
	}

	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}

	@Column(name = "keyvalue",length = 255)
	@Validate(length="1,255")
	public String getKeyvalue() {
		return keyvalue;
	}
	
	
	public void setKeyvalue(String keyvalue) {
		this.keyvalue = keyvalue;
	}

	public String getForm_data_id() {
		return form_data_id;
	}
	
	@Column(name = "form_data_id",length = 255)
	@Validate(length="1,255")
	public void setForm_data_id(String form_data_id) {
		this.form_data_id = form_data_id;
	}
	
	
}
