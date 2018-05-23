package cn.com.smart.web.dao;

import java.util.List;

import cn.com.smart.web.bean.entity.TNRoleDoc;
/**
 * 
 * @author zhanglb
 *
 */
public interface IRoleDocDao {
	/**
	 * 获取角色文档关联数据
	 * @param roleId
	 * @return 返回角色文档实体对象集合
	 */
	public List<TNRoleDoc> queryByRole(String roleId);
	
	/**
	 * 判断角色文档是否已关联
	 * @param roleId
	 * @param menuId
	 * @return 返回true或false <br />
	 * 如已经关联则返回：true；否则返回：false
	 */
	public boolean isRoleMenuCombinExist(String roleId,String docId);
}
