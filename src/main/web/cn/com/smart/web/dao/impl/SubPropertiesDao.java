package cn.com.smart.web.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.mixsmart.utils.StringUtils;

import cn.com.smart.dao.impl.BaseDaoImpl;
import cn.com.smart.exception.DaoException;
import cn.com.smart.res.SQLResUtil;
import cn.com.smart.res.sqlmap.SqlMapping;
import cn.com.smart.web.bean.entity.TPfSubproperties;
import cn.com.smart.web.bean.entity.TPfproperties;
/**
 * 
 * @author zhanglb
 *
 */
@Repository("subPropertiesDao")
public class SubPropertiesDao  extends BaseDaoImpl<TPfSubproperties>{
	private SqlMapping sqlMap;
	private Map<String, Object> params;
	
	public SubPropertiesDao() {
		sqlMap = SQLResUtil.getBaseSqlMap();
	}
	
	@SuppressWarnings("unchecked")
	public List<TPfSubproperties>  getByFormDataId(String formDataId) {
		List<TPfSubproperties>  list = null;
		if(null == formDataId || StringUtils.isEmpty(formDataId.toString())) {
			return list;
		}
		String sql = sqlMap.getSQL("get_subproperties_by_formdataid");
		if(StringUtils.isEmpty(sql)) {
			return list;
		}
		params = new HashMap<String, Object>();
		params.put("formDataId", formDataId);
		try {
			SQLQuery sqlQuery = (SQLQuery) super.getQuery(sql, params, true);
			sqlQuery.addEntity(TPfSubproperties.class);
			list = (List<TPfSubproperties>)sqlQuery.list();
		} catch (Exception e) {
			throw new DaoException(e);
		} finally {
			params = null;
		}
		return list;
	}
	
	public TPfSubproperties getByFormDataIdAndKeyname(String formDataId, String keyName) {
		TPfSubproperties  tPfSubproperties = null;
		if(null == formDataId || StringUtils.isEmpty(formDataId.toString()) ||null == keyName || StringUtils.isEmpty(keyName.toString())) {
			return tPfSubproperties;
		}
		String sql = sqlMap.getSQL("get_subproperties_by_formdataidandkeyname");
		if(StringUtils.isEmpty(sql)) {
			return tPfSubproperties;
		}
		params = new HashMap<String, Object>();
		params.put("formDataId", formDataId);
		params.put("keyName", keyName);
		try {
			SQLQuery sqlQuery = (SQLQuery) super.getQuery(sql, params, true);
			sqlQuery.addEntity(TPfSubproperties.class);
			tPfSubproperties = (TPfSubproperties)sqlQuery.uniqueResult();
		} catch (Exception e) {
			throw new DaoException(e);
		} finally {
			params = null;
		}
		return tPfSubproperties;
	}
	
}
