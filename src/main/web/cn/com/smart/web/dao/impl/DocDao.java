package cn.com.smart.web.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mixsmart.utils.StringUtils;

import cn.com.smart.dao.impl.BaseDaoImpl;
import cn.com.smart.exception.DaoException;
import cn.com.smart.filter.HandleFilterParam;
import cn.com.smart.filter.bean.FilterParam;
import cn.com.smart.res.SQLResUtil;
import cn.com.smart.res.sqlmap.SqlMapping;
import cn.com.smart.web.bean.entity.TNDoc;
import cn.com.smart.web.bean.entity.TNOPAuth;
import cn.com.smart.web.bean.entity.TNRoleDocResource;
import cn.com.smart.web.dao.IOrgDao;

/**
 * 文档机构DAO
 * @author zhanglb
 *
 */
@Repository("docDao")
public class DocDao extends BaseDaoImpl<TNDoc> implements IOrgDao {
	
	@Autowired
	private OPAuthDao opAuthDao;
	
	private SqlMapping sqlMap;
	private Map<String, Object> params;
	
	public DocDao() {
		sqlMap = SQLResUtil.getBaseSqlMap();
	}
	
	public TNDoc getDocByCode(String code) {
		TNDoc doc = null;
		if(null == code || StringUtils.isEmpty(code.toString())) {
			return doc;
		}
		String sql = sqlMap.getSQL("get_doc_by_code");
		if(StringUtils.isEmpty(sql)) {
			return doc;
		}
		params = new HashMap<String, Object>();
		params.put("code", code);
		try {
			SQLQuery sqlQuery = (SQLQuery) super.getQuery(sql, params, true);
			sqlQuery.addEntity(TNDoc.class);
			doc = (TNDoc)sqlQuery.uniqueResult();
		} catch (Exception e) {
			throw new DaoException(e);
		} finally {
			params = null;
		}
		return doc;
	}
	

	public List<TNDoc> queryContainAuths(FilterParam searchParam) throws DaoException  {
//		params = new HandleFilterParam(searchParam).getParams();
//		String hql = "from "+TNDoc.class.getName()+" where t.type='doc' [and t.name like '%:name%'] order by createTime asc";
		String hql = "from "+TNDoc.class.getName()+" where type='doc'  order by createTime asc";
		List<TNDoc> lists = queryHql(hql, null);
		System.out.println("list.seize"+lists.size());
		if(null != lists && lists.size()>0) {
			String[] authValueArray = null;
			List<TNOPAuth> auths = null;
			for (TNDoc res : lists) {
				String str = "edit,del,download,share";
				authValueArray = str.split(",");
				auths = opAuthDao.queryAuths(authValueArray);
				if(null != auths && auths.size()>0) {
					res.setAuths(auths);
				}
			}//for
		}
		return lists;
	}
	
	
	
}
