package cn.com.smart.web.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mixsmart.utils.StringUtils;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.exception.DaoException;
import cn.com.smart.exception.ServiceException;
import cn.com.smart.helper.TreeHelper;
import cn.com.smart.service.impl.MgrServiceImpl;
import cn.com.smart.web.bean.entity.TNDoc;
import cn.com.smart.web.bean.entity.TNMenu;
import cn.com.smart.web.bean.entity.TNRoleDoc;
import cn.com.smart.web.bean.entity.TNRoleMenu;
import cn.com.smart.web.dao.impl.DocDao;
import cn.com.smart.web.dao.impl.RoleDocDao;
import cn.com.smart.web.plugins.ZTreeData;

/**
 * 
 * @author zhanglb
 *
 */
@Service
public class RoleDocService extends MgrServiceImpl<TNRoleDoc> {

	@Autowired
	private DocDao docDao;
	
	@Autowired
	private RoleDocDao roleDocDao;
	/**
	 * 菜单及操作权限组成的树
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<ZTreeData> docTree() throws ServiceException {
		SmartResponse<ZTreeData> smartResp = new SmartResponse<ZTreeData>();
		smartResp.setResult(OP_NOT_DATA_SUCCESS);
		smartResp.setMsg(OP_NOT_DATA_SUCCESS_MSG);
		try {
			List<TNDoc> menus = docDao.findAll();
			if(null != menus && menus.size()>0) {
				try {
					TreeHelper<TNDoc> menuTreeHelper = new TreeHelper<TNDoc>();
					List<TNDoc> menuTrees = menuTreeHelper.outPutTree(menus);
					menuTreeHelper = null;
					if(null != menuTrees && menuTrees.size()>0) {
						List<ZTreeData> zTreeDatas = new ArrayList<ZTreeData>();
						ZTreeData zTreeData = null;
						for (TNDoc menu : menuTrees) {
							zTreeData = new ZTreeData();
							zTreeData.setId(menu.getId());
							zTreeData.setName(menu.getName());
							zTreeData.setpId(menu.getParentId());
							zTreeData.setFlag("menu");
							zTreeDatas.add(zTreeData);
						}
						if(zTreeDatas.size()>0) {
							smartResp.setResult(OP_SUCCESS);
							smartResp.setMsg(OP_SUCCESS_MSG);
							smartResp.setDatas(zTreeDatas);
							smartResp.setTotalNum(zTreeDatas.size());
						}
						zTreeDatas = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			menus = null;
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(),e.getCause());
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return smartResp;
	}
	
	/**
	 * 菜单及操作权限组成的树
	 * @param roleId
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<ZTreeData> docTree(String roleId) throws ServiceException {
		SmartResponse<ZTreeData> smartResp = new SmartResponse<ZTreeData>();
		smartResp.setResult(OP_NOT_DATA_SUCCESS);
		smartResp.setMsg(OP_NOT_DATA_SUCCESS_MSG);
		try {
			if(StringUtils.isNotEmpty(roleId)) {
				List<TNDoc> menus = docDao.findAll();
				if(null != menus && menus.size()>0) {
					List<TNRoleDoc> roleMenus = roleDocDao.queryByRole(roleId);
					try {
						TreeHelper<TNDoc> menuTreeHelper = new TreeHelper<TNDoc>();
						List<TNDoc> menuTrees = menuTreeHelper.outPutTree(menus);
						menuTreeHelper = null;
						if(null != menuTrees && menuTrees.size()>0) {
							List<ZTreeData> zTreeDatas = new ArrayList<ZTreeData>();
							ZTreeData zTreeData = null;
							for (TNDoc menu : menuTrees) {
								zTreeData = new ZTreeData();
								zTreeData.setId(menu.getId());
								zTreeData.setName(menu.getName());
								zTreeData.setpId(menu.getParentId());
								zTreeData.setFlag("menu");
								zTreeDatas.add(zTreeData);
								//操作权限
							}
							zTreeData = null;
							if(zTreeDatas.size()>0) {
								if(null != roleMenus && roleMenus.size()>0) {
									for (TNRoleDoc roleMenu : roleMenus) {
										for (ZTreeData zTreeDataTmp : zTreeDatas) {
											if(roleMenu.getDocId().equals(zTreeDataTmp.getId()) && 
													"menu".equals(zTreeDataTmp.getFlag())) {
												zTreeDataTmp.setChecked(true);
											}
										}//for
									}//for
								}
								smartResp.setResult(OP_SUCCESS);
								smartResp.setMsg(OP_SUCCESS_MSG);
								smartResp.setDatas(zTreeDatas);
								smartResp.setTotalNum(zTreeDatas.size());
							}
							zTreeDatas = null;
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						roleMenus  = null;
					}
					
				}
				menus = null;
			}
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(),e.getCause());
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return smartResp;
	}
	
}
