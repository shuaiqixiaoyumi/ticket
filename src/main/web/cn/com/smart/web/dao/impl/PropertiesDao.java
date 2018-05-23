package cn.com.smart.web.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.mixsmart.utils.StringUtils;

import cn.com.smart.dao.impl.BaseDaoImpl;
import cn.com.smart.exception.DaoException;
import cn.com.smart.res.SQLResUtil;
import cn.com.smart.res.sqlmap.SqlMapping;
import cn.com.smart.web.bean.entity.TNDoc;
import cn.com.smart.web.bean.entity.TPfproperties;
/**
 * 
 * @author zhanglb
 *
 */
@Repository("propertiesDao")
public class PropertiesDao extends BaseDaoImpl<TPfproperties>{

	private SqlMapping sqlMap;
	private Map<String, Object> params;
	
	public PropertiesDao() {
		sqlMap = SQLResUtil.getBaseSqlMap();
	}
	
	public TPfproperties getByCode(String code) {
		TPfproperties tPfproperties = null;
		if(null == code || StringUtils.isEmpty(code.toString())) {
			return tPfproperties;
		}
		String sql = sqlMap.getSQL("get_properties_by_code");
		if(StringUtils.isEmpty(sql)) {
			return tPfproperties;
		}
		params = new HashMap<String, Object>();
		params.put("code", code);
		try {
			SQLQuery sqlQuery = (SQLQuery) super.getQuery(sql, params, true);
			sqlQuery.addEntity(TPfproperties.class);
			tPfproperties = (TPfproperties)sqlQuery.uniqueResult();
		} catch (Exception e) {
			throw new DaoException(e);
		} finally {
			params = null;
		}
		return tPfproperties;
	}
}
