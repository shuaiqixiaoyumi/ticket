package cn.com.smart.web.dao.impl;

import org.springframework.stereotype.Repository;

import cn.com.smart.dao.impl.BaseDaoImpl;
import cn.com.smart.res.SQLResUtil;
import cn.com.smart.res.sqlmap.SqlMapping;
import cn.com.smart.web.bean.entity.TNMarkdown;
/**
 * 
 * @author zhanglb
 *
 */
@Repository("markdownDao")
public class MarkdownDao  extends BaseDaoImpl<TNMarkdown>{
private SqlMapping sqlMap;
	
	public MarkdownDao() {
		sqlMap = SQLResUtil.getBaseSqlMap();
	}
}
