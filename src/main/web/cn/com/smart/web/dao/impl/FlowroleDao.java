package cn.com.smart.web.dao.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.com.smart.dao.impl.BaseDaoImpl;
import cn.com.smart.res.SQLResUtil;
import cn.com.smart.res.sqlmap.SqlMapping;
import cn.com.smart.web.bean.entity.TNFlowrole;
import cn.com.smart.web.bean.entity.TNFontLine;

@Repository("flowroleDao")
public class FlowroleDao extends BaseDaoImpl<TNFlowrole>{
	private SqlMapping sqlMap;
	private Map<String,Object> params;
	
	public FlowroleDao() {
		sqlMap = SQLResUtil.getBaseSqlMap();
	}
}
