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
import cn.com.smart.web.bean.entity.TNFontLine;

@Repository("fontLineDao")
public class FontLineDao  extends BaseDaoImpl<TNFontLine>{
	private SqlMapping sqlMap;
	private Map<String,Object> params;
	
	public FontLineDao() {
		sqlMap = SQLResUtil.getBaseSqlMap();
	}
	
	/**
	 * 根据事业群id获取信息
	 * @param positionIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TNFontLine> findByGroupId(String GroupId) {
		List<TNFontLine> tnFontLine = null;
		String sql = sqlMap.getSQL("get_fontline_by_groupid");
		if(StringUtils.isEmpty(sql)) {
			return tnFontLine;
		}
		params = new HashMap<String, Object>();
		params.put("groupId", GroupId);
		try {
			SQLQuery sqlQuery = (SQLQuery) super.getQuery(sql, params, true);
			sqlQuery.addEntity(TNFontLine.class);
			tnFontLine = sqlQuery.list();
		} catch (Exception e) {
			throw new DaoException(e);
		} finally {
			params = null;
		}
		return tnFontLine;
	}

	/**
	 * 根据事业群id和一线值获取信息
	 * @param positionIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TNFontLine> getFontLine1ListByGroupId(String GroupId) {
		List<TNFontLine> tnFontLine = null;
		String sql = sqlMap.getSQL("get_fontline1_by_groupid");
		if(StringUtils.isEmpty(sql)) {
			return tnFontLine;
		}
		params = new HashMap<String, Object>();
		params.put("groupId", GroupId);
		try {
			SQLQuery sqlQuery = (SQLQuery) super.getQuery(sql, params, true);
			sqlQuery.addEntity(TNFontLine.class);
			tnFontLine = sqlQuery.list();
		} catch (Exception e) {
			throw new DaoException(e);
		} finally {
			params = null;
		}
		return tnFontLine;
	}
	
	/**
	 * 根据事业群id和一线值获取信息
	 * @param positionIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TNFontLine> getFontLine2ListByGroupId(String GroupId) {
		List<TNFontLine> tnFontLine = null;
		String sql = sqlMap.getSQL("get_fontline2_by_groupid");
		if(StringUtils.isEmpty(sql)) {
			return tnFontLine;
		}
		params = new HashMap<String, Object>();
		params.put("groupId", GroupId);
		try {
			SQLQuery sqlQuery = (SQLQuery) super.getQuery(sql, params, true);
			sqlQuery.addEntity(TNFontLine.class);
			tnFontLine = sqlQuery.list();
		} catch (Exception e) {
			throw new DaoException(e);
		} finally {
			params = null;
		}
		return tnFontLine;
	}
	
	public boolean deleteByGroupId(String groupId) {
		boolean is = false;
		if(null != groupId && !"".equals(groupId) ) {
			String delSql = null;
			delSql = sqlMap.getSQL("del_fontline_bygroupId");
			params = new HashMap<String, Object>();
			if(StringUtils.isNotEmpty(delSql)) {
				params.put("groupId", groupId);
				is = executeSql(delSql, params)>0?true:false;
			}
		}
		params = null;
		return is;
	}
}
