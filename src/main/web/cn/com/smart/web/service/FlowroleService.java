package cn.com.smart.web.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mixsmart.utils.StringUtils;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.exception.DaoException;
import cn.com.smart.exception.ServiceException;
import cn.com.smart.service.impl.MgrServiceImpl;
import cn.com.smart.web.bean.entity.TNFlowRoleUser;
import cn.com.smart.web.bean.entity.TNFlowrole;
import cn.com.smart.web.bean.entity.TNRoleUser;
import cn.com.smart.web.bean.entity.TNUser;
import cn.com.smart.web.dao.impl.FlowRoleUserDao;
import cn.com.smart.web.dao.impl.FlowroleDao;

@Service("flowroleServ")
public class FlowroleService extends MgrServiceImpl<TNFlowrole>{

	@Autowired
	private FlowroleDao flowroleDao;
	
	@Autowired
	private FlowRoleUserDao flowroleUserDao;
	
	@Autowired
	private  UserService userServ; 
	/**
	 * 保存
	 * @param role
	 * @param menuIds
	 * @param resAuths
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<String> save(TNFlowrole role) throws ServiceException {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(null != role) {
				Serializable id = flowroleDao.save(role);
				smartResp.setResult(OP_SUCCESS);
				smartResp.setMsg(OP_SUCCESS_MSG);
			}
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(),e.getCause());
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return smartResp;
	}
	
	/**
     * 从角色中删除用户
     * @param roleId
     * @param userId
     * @return
     */
    public SmartResponse<String> deleteUser(String roleId, String userId) {
        SmartResponse<String> smartResp = new SmartResponse<String>();
        smartResp.setMsg("删除失败");
        if(StringUtils.isEmpty(roleId) || StringUtils.isEmpty(userId)) {
            return smartResp;
        }
        Map<String, Object> param = new HashMap<String, Object>(2);
        param.put("roleId", roleId);
        param.put("id", userId);
        if(flowroleUserDao.delete(param)) {
            smartResp.setResult(OP_SUCCESS);
            smartResp.setMsg("删除成功");
        }
        return smartResp;
    }
	
	/**
	 * 角色中添加用户
	 * @param roleId
	 * @param userIds
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<String> addUser2Role(String roleId,String[] userIds) throws ServiceException {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(StringUtils.isNotEmpty(roleId) && null != userIds && userIds.length>0) {
				if(!flowroleUserDao.isUserInRoleExist(roleId, userIds)) {
					List<TNFlowRoleUser> roleUsers = new ArrayList<TNFlowRoleUser>();
					TNFlowRoleUser roleUser = null;
					for (int i = 0; i < userIds.length; i++) {
						roleUser = new TNFlowRoleUser();
						roleUser.setRoleId(roleId);
						roleUser.setUserId(userIds[i]);
						roleUsers.add(roleUser);
					}
					List<Serializable> ids = flowroleUserDao.save(roleUsers);
					if(null != ids && ids.size()>0) {
						smartResp.setResult(OP_SUCCESS);
						smartResp.setMsg(OP_SUCCESS_MSG);
					}
					ids = null;
					roleUser = null;
					roleUsers = null;
				} else {
					smartResp.setMsg("用户已添加到流程角色里面，不能重复添加！");
				}
				userIds = null;
			}
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(),e.getCause());
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return smartResp;
	}
	
	
	public SmartResponse<String> updateFlowUser(String id,String sortOrder) throws ServiceException {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(StringUtils.isNotEmpty(id) && null != id ) {
				
					TNFlowRoleUser roleUser = flowroleUserDao.find(id);
					if(roleUser!=null) {
						roleUser.setSortOrder(sortOrder);
					}
					boolean result = flowroleUserDao.update(roleUser);
					if(result) {
						smartResp.setResult(OP_SUCCESS);
						smartResp.setMsg(OP_SUCCESS_MSG);
					}
					sortOrder = null;
					id = null;
			}
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(),e.getCause());
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return smartResp;
	}
	
	
	public List<TNUser> getUserListByFlowRoleName(String code){ 
		List<TNUser> list =  userServ.getDao().getUserListByFlowRoleCode(code);
		if(list !=null && list.size() >0) {
			return list;
		}
		return null;
	}
	
	public TNFlowRoleUser getByRoleIdandUserId(String roleId, String userId){ 
		TNFlowRoleUser tfru =  flowroleUserDao.getByRoleIdandUserId(roleId, userId);
		if(tfru !=null) {
			return tfru;
		}
		return null;
	}
}
