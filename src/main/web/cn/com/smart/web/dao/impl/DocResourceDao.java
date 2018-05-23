package cn.com.smart.web.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mixsmart.utils.StringUtils;

import cn.com.smart.dao.impl.BaseDaoImpl;
import cn.com.smart.exception.DaoException;
import cn.com.smart.res.SQLResUtil;
import cn.com.smart.res.sqlmap.SqlMapping;
import cn.com.smart.web.bean.entity.TNDoc;
import cn.com.smart.web.bean.entity.TNRoleDocResource;
import cn.com.smart.web.bean.entity.TNRoleResource;
import cn.com.smart.web.dao.IOrgDao;

@Repository("docResourceDao")
public class DocResourceDao  extends BaseDaoImpl<TNRoleDocResource> implements IOrgDao {
	@Autowired
	private OPAuthDao opAuthDao;
	
	private SqlMapping sqlMap;
	private Map<String, Object> params;
	
	public DocResourceDao() {
		sqlMap = SQLResUtil.getBaseSqlMap();
	}
	
	public List<TNRoleDocResource>  getRoleResources(){
		String hql = "from "+TNRoleDocResource.class.getName();
		List<TNRoleDocResource> lists = queryHql(hql, new HashMap<String,Object>());
		return lists;
	}
	
	/**
	 * 保存角色文档资源数据
	 * @param roleId
	 * @param resources
	 * @return
	 */
	public boolean save(String roleId,List<TNDoc> resources) throws DaoException {
		List<Serializable> ids = null;
		if(StringUtils.isNotEmpty(roleId) && null != resources && resources.size()>0) {
			List<TNRoleDocResource> roleResList = new ArrayList<TNRoleDocResource>();
			TNRoleDocResource roleRes = null;
			for (TNDoc res : resources) {
				roleRes = new TNRoleDocResource();
				roleRes.setRoleId(roleId);
				roleRes.setResourceId(res.getId());
				roleRes.setOpAuths(res.authsToString());
				roleResList.add(roleRes);
			}
			roleRes = null;
			resources = null;
			ids = super.save(roleResList);
			roleResList = null;
		}
		return (null != ids && ids.size()>0)?true:false;
	}
	
	/**
	 * 按角色删除
	 * @param roleId
	 * @return
	 */
	public boolean deleteByRole(String roleId) throws DaoException {
		boolean is = false;
		if(StringUtils.isNotEmpty(roleId)) {
			params = new HashMap<String, Object>(1);
			params.put("roleId", roleId);
			String delSql = sqlMap.getSQL("del_rolerdoces_by_role");
			if(StringUtils.isNotEmpty(delSql)) {
				executeSql(delSql, params);
				is = true;
			}
			params = null;
		}
		return is;
	}
}
