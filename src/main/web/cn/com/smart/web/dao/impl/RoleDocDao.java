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
import cn.com.smart.web.bean.entity.TNRoleDoc;
import cn.com.smart.web.dao.IRoleDocDao;

@Repository("roleDocDao")
public class RoleDocDao  extends BaseDaoImpl<TNRoleDoc> implements IRoleDocDao {

	
	private SqlMapping sqlMap;
	private Map<String,Object> params;
	
	@Autowired
	private DocDao docDao;
	
	public RoleDocDao() {
		sqlMap = SQLResUtil.getBaseSqlMap();
	}
	
	
	/**
	 * 保存角色菜单
	 * @param roleId
	 * @param menus
	 * @return
	 * @throws DaoException
	 */
	public boolean save(String roleId,List<TNDoc> menus) throws DaoException  {
		boolean is = false;
		List<Serializable> ids = null;
		if(StringUtils.isNotEmpty(roleId) && null != menus && menus.size()>0) {
			TNRoleDoc roleMenu = null;
			Map<String,TNRoleDoc> roleMenuMaps = new HashMap<String, TNRoleDoc>();
			for (TNDoc menu : menus) {
				roleMenu = roleMenuMaps.get(menu.getId());
				if(null == roleMenu) {
					roleMenu = new TNRoleDoc();
					roleMenu.setDocId(menu.getId());
					roleMenu.setRoleId(roleId);
					roleMenuMaps.put(menu.getId(), roleMenu);
					boolean isLoop = true;
					while(isLoop) {
						roleMenu = roleMenuMaps.get(menu.getParentId());
						if(null == roleMenu) {
							if(!isRoleMenuCombinExist(roleId,menu.getParentId())) {
								menu = docDao.find(menu.getParentId());
								if(null != menu) {
									roleMenu = new TNRoleDoc();
									roleMenu.setDocId(menu.getId());
									roleMenu.setRoleId(roleId);
									roleMenuMaps.put(menu.getId(), roleMenu);
								} else {
									isLoop = false;
								}
							} else {
								isLoop = false;
							}
						} else {
							isLoop = false;
						}
					}
				}//if
			}//for
			roleMenu = null;
			List<TNRoleDoc> lists = new ArrayList<TNRoleDoc>(roleMenuMaps.size());
			for (String key : roleMenuMaps.keySet() ) {
				lists.add(roleMenuMaps.get(key));
			}
			roleMenuMaps = null;
			if(lists.size()>0) {
				//保存角色文档菜单信息
				ids = save(lists);
				if(null != ids && ids.size()>0) { //保存成功，保存角色文档菜单操作权限信息
					is = true;
				}
			}
			lists = null;
		}
		return is;
	}
	
	
	@Override
	public List<TNRoleDoc> queryByRole(String roleId) throws DaoException  {
		List<TNRoleDoc> lists = null;
		if(StringUtils.isNotEmpty(roleId)) {
			String hql = "from "+TNRoleDoc.class.getName()+" t where t.roleId=:roleId";
			Map<String,Object> param = new HashMap<String,Object>(1);
			param.put("roleId", roleId);
			lists = queryHql(hql, param);
			param = null;
		}
		return lists;
	}
	
	
	/**
	 * 按角色删除
	 * @param roleId
	 * @return
	 * @throws DaoException
	 */
	public boolean deleteByRole(String roleId) throws DaoException {
		boolean is = false;
		if(StringUtils.isNotEmpty(roleId)) {
			params = new HashMap<String, Object>(1);
			params.put("roleId", roleId);
			String delSql = sqlMap.getSQL("del_role_doc_by_role_id");
			if(StringUtils.isNotEmpty(delSql)) {
				String[] delSqls = delSql.split(";");
				for (String sql : delSqls) {
					executeSql(sql, params);
				}
				is = true;
				delSqls = null;
			}
			params = null;
		}
		return is;
	}
	
	/**
	 * 级联删除
	 * @param roleId
	 * @param menuId
	 * @return
	 * @throws DaoException
	 */
	public boolean deleteByRoleMenu(String roleId,String docId) throws DaoException {
		boolean is = false;
		if(StringUtils.isNotEmpty(roleId) && StringUtils.isNotEmpty(docId)) {
			params = new HashMap<String, Object>(1);
			params.put("roleId", roleId);
			params.put("docId", docId);
			String delSql = sqlMap.getSQL("del_role_doc_by_id");
			if(StringUtils.isNotEmpty(delSql)) {
				String[] delSqls = delSql.split(";");
				for (String sql : delSqls) {
					executeSql(sql, params);
				}
				is = true;
				delSqls = null;
			}
			params = null;
		}
		return is;
	}
	
	
	@Override
	public boolean isRoleMenuCombinExist(String roleId,String docId) throws DaoException {
		boolean is = false;
		if(StringUtils.isNotEmpty(roleId) && StringUtils.isNotEmpty(docId)) {
			String sql = sqlMap.getSQL("check_role_doc_combin_exist");
			if(StringUtils.isNotEmpty(sql)) {
				params = new HashMap<String, Object>(2);
				params.put("roleId", roleId);
				params.put("docId", docId);
				long count = exeCountSql(sql, params);
				is = count>0?true:false;
				params = null;
			}
		}
		return is;
	}
}
