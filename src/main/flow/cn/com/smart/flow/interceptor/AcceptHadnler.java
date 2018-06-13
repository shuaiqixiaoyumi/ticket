package cn.com.smart.flow.interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.snaker.engine.core.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.flow.MyAssignmentHandler;
import cn.com.smart.flow.service.FlowFormService;
import cn.com.smart.service.SmartContextService;
import cn.com.smart.web.bean.entity.TNFontLine;
import cn.com.smart.web.bean.entity.TNOrg;
import cn.com.smart.web.bean.entity.TNUser;
import cn.com.smart.web.dao.impl.UserDao;
import cn.com.smart.web.service.FlowroleService;
import cn.com.smart.web.service.FontLineService;
import cn.com.smart.web.service.OrgService;
import cn.com.smart.web.service.UserService;

/**
 * 参与者处理类
 * @author zhanglb
 *
 */
@Component
public class AcceptHadnler implements MyAssignmentHandler{
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrgService orgServ;
	
	@Autowired
	private FlowroleService flowroleServ;
	
	@Autowired
	private FontLineService fontlineServ ;
	
	public AcceptHadnler() {
		userService  = SmartContextService.find(UserService.class);
		orgServ =SmartContextService.find(OrgService.class);
		flowroleServ = SmartContextService.find(FlowroleService.class);
		fontlineServ = SmartContextService.find(FontLineService.class);
	}

	@Override
	public Object assign(Execution execution) {
		execution.getTask().getOrderId();
		String userId = execution.getOrder().getLastUpdator();
		System.out.println("435252235"+userId);
		String actorIds = "";
		SmartResponse<TNUser> userInfo = userService.find(userId);
		TNUser tnUser = userInfo.getData();
		String orgId = tnUser.getOrgId();
		TNOrg org = orgServ.getDao().find(orgId);
		String bussinessGroupId =orgServ.getBusinessGroupByOrgId(orgId);
		System.out.println("bussinessGroupId"+bussinessGroupId);
		try {
			if(bussinessGroupId != null && !"".equals(bussinessGroupId)) {
				Map<String, Object> map = new HashMap<>();
				map.put("groupId", bussinessGroupId);
				List<TNFontLine> list =  fontlineServ.findByGroupId(bussinessGroupId) ;
				if(list!=null && list.size() >0 ) {
					for(TNFontLine tnf : list) {
						if(actorIds == null || "".equals(actorIds)) {
							 actorIds = "u_"+tnf.getUserId();
						 }else {
							 actorIds =actorIds + ",u_"+tnf.getUserId();
						 }
					}
					if(actorIds != null && !"".equals(actorIds)){
						System.out.println(111111);
						System.out.println(actorIds);
						return actorIds;
					}
					
				}else {
					return null;
				}
			}else {
				return null;
			}
		}catch(Exception e) {
			return null;
		}
		
		
		
		return null;
	}

	@Override
	public Object assignByParentUser(Execution execution, String userId) {
		String actorIds = "";
		SmartResponse<TNUser> userInfo = userService.find(userId);
		TNUser tnUser = userInfo.getData();
		String orgId = tnUser.getOrgId();
		
		return actorIds;
	}
	
	public Object assignByParentUser( String userId) {
		String actorIds = "";
		SmartResponse<TNUser> userInfo = userService.find(userId);
		TNUser tnUser = userInfo.getData();
		String orgId = tnUser.getOrgId();
		TNOrg org = orgServ.getDao().find(orgId);
		String bussinessGroupId =orgServ.getBusinessGroupByOrgId(orgId);
		System.out.println("bussinessGroupId"+bussinessGroupId);
		try {
			if(bussinessGroupId != null && !"".equals(bussinessGroupId)) {
				Map<String, Object> map = new HashMap<>();
				map.put("groupId", bussinessGroupId);
				List<TNFontLine> list =  fontlineServ.findByGroupId(bussinessGroupId) ;
				if(list!=null && list.size() >0 ) {
					for(TNFontLine tnf : list) {
						if(actorIds == null || "".equals(actorIds)) {
							 actorIds = tnf.getUserId();
						 }else {
							 actorIds =actorIds + ","+tnf.getUserId();
						 }
					}
					if(actorIds != null && !"".equals(actorIds)){
						return actorIds;
					}
					
				}else {
					return null;
				}
			}else {
				return null;
			}
		}catch(Exception e) {
			return null;
		}
		return null;
	}

//	@Override
//	public Object assignByParentUser(Execution execution, String userId, String assigns) {
//		String actorIds = "";
//		SmartResponse<TNUser> userInfo = userService.find(userId);
//		TNUser tnUser = userInfo.getData();
//		String orgId = tnUser.getOrgId();
//		TNOrg org = orgServ.getDao().find(orgId);
//		String bussinessGroupId =orgServ.getBusinessGroupByOrgId(orgId);
//		try {
//			List<TNUser> list =flowroleServ.getUserListByFlowRoleName("frontline");
//			if(list.size() >0 ) {
//				if(bussinessGroupId != null && !"".equals(bussinessGroupId)) {
//					System.out.println(bussinessGroupId);
//					for(TNUser user : list) {
//						orgId = user.getOrgId();
//						if(orgId != null && !"".equals(orgId)) {
//							 String bussinessGroupId2 = orgServ.getBusinessGroupByOrgId(orgId);
//							 if(bussinessGroupId2 != null && !"".equals(bussinessGroupId2)) {
//								 if(bussinessGroupId2 == bussinessGroupId || bussinessGroupId2.equals(bussinessGroupId)) {
//									 if(actorIds == null || "".equals(actorIds)) {
//										 actorIds = "u_"+user.getId();
//									 }
//									 actorIds =actorIds + ",u_"+user.getId();
//								 }
//							 }
//						}
//						
//					}
//					System.out.println("actorIds====="+actorIds);
//					if(actorIds != null && !"".equals(actorIds)){
//						return actorIds;
//					}
//				}else {
//					return assigns;
//				}
//			}else {
//				return assigns;
//			}
//		}catch(Exception e) {
//			return assigns;
//		}
//		
//		
//		
//		return assigns;
//	}
	
	@Override
	public Object assignByParentUser(Execution execution, String userId, String assigns) {
		String actorIds = "";
		SmartResponse<TNUser> userInfo = userService.find(userId);
		TNUser tnUser = userInfo.getData();
		String orgId = tnUser.getOrgId();
		TNOrg org = orgServ.getDao().find(orgId);
		String bussinessGroupId =orgServ.getBusinessGroupByOrgId(orgId);
		System.out.println("bussinessGroupId"+bussinessGroupId);
		try {
			if(bussinessGroupId != null && !"".equals(bussinessGroupId)) {
				Map<String, Object> map = new HashMap<>();
				map.put("groupId", bussinessGroupId);
				List<TNFontLine> list =  fontlineServ.findByGroupId(bussinessGroupId) ;
				if(list!=null && list.size() >0 ) {
					for(TNFontLine tnf : list) {
						if(actorIds == null || "".equals(actorIds)) {
							 actorIds = "u_"+tnf.getUserId();
						 }else {
							 actorIds =actorIds + ",u_"+tnf.getUserId();
						 }
						
					}
					if(actorIds != null && !"".equals(actorIds)){
						return actorIds;
					}
					
				}else {
					return assigns;
				}
			}else {
				return assigns;
			}
		}catch(Exception e) {
			return assigns;
		}
		
		
		
		return assigns;
	}

}
